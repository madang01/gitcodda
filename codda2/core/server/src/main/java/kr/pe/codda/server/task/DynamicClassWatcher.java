/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package kr.pe.codda.server.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 * 동적 클래스 파일 변경 감시자
 * 
 * 
 * 변경 내용 : (1) system.out => logger, (2) 
 *  
 * 
 * 출처 : https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class DynamicClassWatcher extends Thread {
	private Logger log = Logger.getLogger(DynamicClassWatcher.class.getName());

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace = false;

	private final File dynamicClassDirectroy;
	private final AppInfClassFileModifyEventListener serverDynamicClassFileModifyEventListener;

	/**
	 * 생성자
	 * 
	 * @param serverAPPINFClassPath                     서버 동적 클래스 경로
	 * @param recursive                                 하위 디렉토리 포함 여부
	 * @param serverDynamicClassFileModifyEventListener 서버 동적 클래스 수정 이벤트 수신자
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 */
	public DynamicClassWatcher(File serverAPPINFClassPath, boolean recursive,
			AppInfClassFileModifyEventListener serverDynamicClassFileModifyEventListener) throws IOException {
		if (null == serverAPPINFClassPath) {
			throw new IllegalArgumentException("the parameter serverAPPINFClassPath is null");
		}

		if (!serverAPPINFClassPath.exists()) {
			throw new IllegalArgumentException("the parameter serverAPPINFClassPath does not exist");
		}

		if (!serverAPPINFClassPath.isDirectory()) {
			throw new IllegalArgumentException("the parameter serverAPPINFClassPath is not a directory");
		}

		if (null == serverDynamicClassFileModifyEventListener) {
			throw new IllegalArgumentException("the parameter serverDynamicClassFileModifyEventListener is null");
		}

		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.recursive = recursive;

		Path dir = serverAPPINFClassPath.toPath();

		if (recursive) {
			// System.out.format("Scanning %s ...\n",
			// dynamicClassDirectroy.getAbsolutePath());
			log.info("Scanning " + serverAPPINFClassPath.getAbsolutePath() + " ...");
			registerAll(dir);

			// System.out.println("Done.");
			log.info("Done.");
		} else {
			register(dir);
		}

		// enable trace after initial registration
		this.trace = true;
		this.dynamicClassDirectroy = serverAPPINFClassPath;
		this.serverDynamicClassFileModifyEventListener = serverDynamicClassFileModifyEventListener;
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				// System.out.format("register: %s\n", dir);
				String infoMessage = new StringBuilder().append("register: ").append(dir).toString();
				log.info(infoMessage);
			} else {
				if (!dir.equals(prev)) {

					// System.out.format("update: %s -> %s\n", prev, dir);
					String infoMessage = new StringBuilder().append("update: ").append(prev).append(" -> ").append(dir)
							.toString();

					log.info(infoMessage);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	@SuppressWarnings("rawtypes")
	void processEvents() throws Exception {
		for (;;) {

			// wait for key to be signalled
			WatchKey key = watcher.take();

			if (null == key) {
				log.warning("watcher key is null");
				break;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				log.severe("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				// System.out.format("%s: %s\n", event.kind().name(), child);
				String infoMessage = new StringBuilder().append(event.kind().name()).append(": ").append(child)
						.toString();
				log.info(infoMessage);

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_CREATE) {

					if (recursive) {
						try {
							if (Files.isDirectory(child, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
								registerAll(child);
							}
						} catch (IOException x) {
							// ignore to keep sample readbale
						}
					}
				} else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY) {

					File ModifiedDynamicClassFile = child.toFile();
					if (ModifiedDynamicClassFile.isFile()) {
						try {
							serverDynamicClassFileModifyEventListener.onAppInfClassFileModify(ModifiedDynamicClassFile);
						} catch (Exception e) {
							log.log(Level.WARNING, "fail to delevery to server dynmaic class file modifiey event", e);
						}
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}

			/**
			 * <pre>
			 * Prevent receiving two separate ENTRY_MODIFY events: file modified
			 * and timestamp updated. Instead, receive one ENTRY_MODIFY event
			 * with two counts.
			 * 
			 * 역자주 : 윈도10 에서 자체 테스트해 본 결과 3번 발생함.
			 * 
			 * 출처 : https://stackoverflow.com/questions/16777869/java-7-watchservice-ignoring-multiple-occurrences-of-the-same-event
			 * </pre>
			 */
			Thread.sleep(1);
		}
	}

	@Override
	public void run() {

		log.info("the DynamicClassWatcher proecess (re)start::" + dynamicClassDirectroy);

		while (true) {
			try {
				processEvents();
			} catch (InterruptedException e) {
				log.info("loop exit becase interruptedException occured");
				break;
			} catch (ClosedWatchServiceException e) {
				log.info("loop exit becase ClosedWatchServiceException occured");
				break;
			} catch (Exception e) {
				log.log(Level.SEVERE, "loop exit becase unknown error occured", e);
				break;
			}
		}

		log.info("the DynamicClassWatcher proecess exist::" + dynamicClassDirectroy);
	}

	public void close() {

		try {
			watcher.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "fail to close the DynamicClassWatcher proecess::"+dynamicClassDirectroy, e);
		}
	}
}
