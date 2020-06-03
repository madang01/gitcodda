/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package kr.pe.codda.server.task;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.impl.task.server.EmptyServerTask;
import kr.pe.codda.server.classloader.ServerClassLoader;
import kr.pe.codda.server.classloader.ServerClassLoaderFactory;

/**
 * 서버 타스크 관리자
 * 
 * @author Won Jonghoon
 *
 */
public class ServerTaskManger implements ServerTaskMangerIF, AppInfClassFileModifyEventListener {
	private Logger log = Logger.getLogger(ServerTaskManger.class.getName());

	private final Object monitor = new Object();

	private final ServerClassLoaderFactory serverClassLoaderFactory;

	private transient ServerClassLoader currentServerClassLoader = null;
	

	private final ConcurrentHashMap<String, AbstractServerTask> messageID2ServerTaskHash = new ConcurrentHashMap<String, AbstractServerTask>();
	private final ConcurrentHashMap<String, Set<String>> classFullName2MessageIDSetHash = new ConcurrentHashMap<String, Set<String>>();

	/**
	 * 생성자
	 * 
	 * @param serverClassLoaderFactory 서버 동적 클래스 로더 팩토리
	 */
	public ServerTaskManger(ServerClassLoaderFactory serverClassLoaderFactory) {
		if (null == serverClassLoaderFactory) {
			throw new IllegalArgumentException("the parameter serverClassLoaderFactory is null");
		}
		

		this.serverClassLoaderFactory = serverClassLoaderFactory;

		currentServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
		
		/** 코다 시스템 내장되는 서버 비지니스 로직 */
		messageID2ServerTaskHash.put("Empty", new EmptyServerTask());
		
	}

	@Override
	public AbstractServerTask getValidServerTask(String messageID) throws DynamicClassCallException {
		

		AbstractServerTask serverTask = messageID2ServerTaskHash.get(messageID);

		if (null == serverTask) {

			synchronized (monitor) {
				serverTask = currentServerClassLoader.getServerTask(messageID);
				if (null == serverTask) {
					serverTask = currentServerClassLoader.createNewServerTask(messageID);

					log.info("create new a server task[" + messageID + "]");
				}
			}

			try {

				Set<String> serverTaskDepencySet = buildServerTaskDepencySet(messageID);

				messageID2ServerTaskHash.put(messageID, serverTask);

				updateDepencyTable(messageID, serverTaskDepencySet);
			} catch (DynamicClassCallException e) {
				throw e;
			}

		}

		return serverTask;
	}

	public Set<String> buildServerTaskDepencySet(String messageID) throws DynamicClassCallException {
		String serverTaskClassFullName = IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);

		String serverTaskClassFilePathString = serverClassLoaderFactory.getClassFilePathString(serverTaskClassFullName);

		// 주어진 메시지 식별자에 1:1 대응하는 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합을 만든다
		Set<String> setOfDynamicClassesThatServerTaskDependsOn = getDependentDynamicClassSet(
				serverTaskClassFilePathString);

		Set<String> serverTaskDepencySet = new LinkedHashSet<String>(setOfDynamicClassesThatServerTaskDependsOn);

		for (String dynamicClassFullNameThatServerTaskDependsOn : setOfDynamicClassesThatServerTaskDependsOn) {
			// 만약 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합의 원소가 메시지일 경우
			// '메시지용 서버 메시지 코덱' 클래스가 의존하는 동적 클래스 집합을 구하여
			// 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합에 추가한다.

			String innerMessageID = null;
			try {
				innerMessageID = checkMessageClass(dynamicClassFullNameThatServerTaskDependsOn);
			} catch (IllegalArgumentException e) {
				// 서버 비지니스 로직이 의존하는 클래스가 메시지가 아닌 경우 루프 계속
				// log.fine(e.getMessage());

				continue;
			}

			String serverMessageCodecClassFullName = IOPartDynamicClassNameUtil
					.getServerMessageCodecClassFullName(innerMessageID);

			String serverMessageCodecClassFilePathString = serverClassLoaderFactory
					.getClassFilePathString(serverMessageCodecClassFullName);

			Set<String> setOfDynamicClassesThatServerMessageCodecDependsOn = getDependentDynamicClassSet(
					serverMessageCodecClassFilePathString);

			serverTaskDepencySet.addAll(setOfDynamicClassesThatServerMessageCodecDependsOn);

		}

		String serverMessageCodecClassFullName = IOPartDynamicClassNameUtil
				.getServerMessageCodecClassFullName(messageID);

		// 만약 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합에 '입력 메시지용 서버 메시지 코덱' 이 없다면
		// '입력 메시지용 서버 메시지 코덱' 클래스가 의존하는 동적 클래스 집합을 구하여
		// 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합에 추가한다.
		if (!setOfDynamicClassesThatServerTaskDependsOn.contains(serverMessageCodecClassFullName)) {

			String serverMessageCodecClassFilePathString = serverClassLoaderFactory
					.getClassFilePathString(serverMessageCodecClassFullName);

			Set<String> setOfDynamicClassesThatServerMessageCodecDependsOn = getDependentDynamicClassSet(
					serverMessageCodecClassFilePathString);
			serverTaskDepencySet.addAll(setOfDynamicClassesThatServerMessageCodecDependsOn);
		}

		return serverTaskDepencySet;
	}

	public void updateDepencyTable(String messageID, Set<String> serverTaskDepencySet)
			throws DynamicClassCallException {

		// 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합의 내용을 바탕으로 '클래스 전체 이름별 메시지 식별자 집합 해쉬' 구성
		for (String dynamicClassFullNameThatServerTaskDependsOn : serverTaskDepencySet) {
			Set<String> messageIDSet = classFullName2MessageIDSetHash.get(dynamicClassFullNameThatServerTaskDependsOn);

			if (null == messageIDSet) {
				messageIDSet = new HashSet<String>();
				classFullName2MessageIDSetHash.put(dynamicClassFullNameThatServerTaskDependsOn, messageIDSet);
			}

			messageIDSet.add(messageID);
		}
	}

	/**
	 * 파라미터 '클래스 전체 이름' 의 메시지 클래스 여부를 검사하여 만약 메시지일 경우 메시지 식별자를 넘겨주고 아닌 경우 예외를 던진다.
	 * 
	 * @param classFullName 클래스 전체 이름
	 * @return 주어진 클래스 전체 이름이 메시지일 경우 메시지 식별자
	 * @throws IllegalArgumentException 파라마터 '클래스 전체 이름' 이 null 이거나 혹은 주어진 클래스 전체
	 *                                  이름이 메시지가 아닌 경우에 던지는 예외
	 */
	public String checkMessageClass(String classFullName) throws IllegalArgumentException {
		if (null == classFullName) {
			throw new IllegalArgumentException("the parameter classFullName is null");
		}

		int endIndex = -1;
		int beginIndex = -1;

		char[] charArrayOfClassFullName = classFullName.toCharArray();

		for (int i = charArrayOfClassFullName.length - 1; i >= 0; i--) {
			if ('.' == charArrayOfClassFullName[i]) {
				if (-1 == endIndex) {
					endIndex = i;
				} else if (-1 == beginIndex) {
					beginIndex = i;
					break;
				}
			}
		}

		if (-1 == endIndex) {
			throw new IllegalArgumentException("the var endIndex is -1");
		}

		if (-1 == beginIndex) {
			throw new IllegalArgumentException("the var beginIndex is -1");
		}

		int len = endIndex - beginIndex;

		if (1 == len) {
			throw new IllegalArgumentException(
					"the expected message id that is a substring from the var beginIndex to the var endIndex is a empty string");
		}

		String expectedMessageID = classFullName.substring(beginIndex + 1, endIndex);

		String expectedMessageClassFullName = IOPartDynamicClassNameUtil.getMessageClassFullName(expectedMessageID);

		if (!classFullName.equals(expectedMessageClassFullName)) {
			throw new IllegalArgumentException("the parameter classFullName is not a message class");
		}

		return expectedMessageID;
	}

	/*
	 * public String toMessageID(String messageClassFullName) { if (null ==
	 * messageClassFullName) { throw new
	 * IllegalArgumentException("the parameter messageClassFullName is null"); }
	 * 
	 * int endIndex = messageClassFullName.lastIndexOf('.');
	 * 
	 * int beginIndex = messageClassFullName.lastIndexOf('.', endIndex - 1) + 1;
	 * 
	 * String expectedMessageID = messageClassFullName.substring(beginIndex,
	 * endIndex);
	 * 
	 * return expectedMessageID; }
	 * 
	 * public boolean isMessageClass(String classFullName) { if (null ==
	 * classFullName) { throw new
	 * IllegalArgumentException("the parameter classFullName is null"); }
	 * 
	 * if (classFullName.length() <
	 * CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME.length()) { return false;
	 * }
	 * 
	 * String prefix = classFullName.substring(0,
	 * CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME.length()); boolean ret =
	 * prefix.equals(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME);
	 * 
	 * return ret; }
	 */

	public boolean isDynamicClass(String classFullName) {
		if (null == classFullName) {
			throw new IllegalArgumentException("the parameter classFullName is null");
		}

		if (classFullName.length() < CommonStaticFinalVars.BASE_DYNAMIC_CLASS_FULL_NAME.length()) {
			return false;
		}

		String prefix = classFullName.substring(0, CommonStaticFinalVars.BASE_DYNAMIC_CLASS_FULL_NAME.length());
		boolean ret = prefix.equals(CommonStaticFinalVars.BASE_DYNAMIC_CLASS_FULL_NAME);

		return ret;
	}

	public Set<String> getDependentDynamicClassSet(String classFilePathString) throws DynamicClassCallException {
		File classFile = new File(classFilePathString);

		if (! classFile.exists()) {
			String errorMessage = new StringBuilder().append("the dynamic class file[").append(classFilePathString)
					.append("] doesn't exist").toString();

			log.info(errorMessage);

			throw new DynamicClassCallException(errorMessage);
		}

		FileInputStream fis = null;

		BufferedInputStream bis = null;
		
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(classFile);
			bis = new BufferedInputStream(fis);			
			dis = new DataInputStream(bis);

			Set<String> dynamicClassSet = getDependentDynamicClassSet(dis);

			return dynamicClassSet;

		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get dependencies of the class[")
					.append(classFilePathString).append("]").toString();
			
			e.printStackTrace();

			log.log(Level.WARNING, errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		} finally {
			try {
				if (null != dis) {
					dis.close();
				}
			} catch (Exception e) {
			}
			
			try {
				if (null != bis) {
					bis.close();
				}
			} catch (Exception e) {
			}

			try {
				if (null != fis) {
					fis.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 참조 :
	 * https://stackoverflow.com/questions/50019075/get-bytecode-dependency-information-from-class-files-through-java
	 * ===> answered Apr 26 '18 at 10:11 Holger
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public Set<String> getDependentDynamicClassSet(DataInputStream dis) throws IOException {
		ClassFile cf = new ClassFile(dis);
		ConstPool constPool = cf.getConstPool();
		Set<String> set = new LinkedHashSet<>();

		for (int ix = 1, size = constPool.getSize(); ix < size; ix++) {

			int descriptorIndex;
			switch (constPool.getTag(ix)) {
			case ConstPool.CONST_Class: {
				String classFullName = constPool.getClassInfo(ix);
				if (isDynamicClass(classFullName)) {
					set.add(classFullName);
				}
			}
			default: {
				continue;
			}
			case ConstPool.CONST_NameAndType: {
				descriptorIndex = constPool.getNameAndTypeDescriptor(ix);
				break;
			}
			case ConstPool.CONST_MethodType: {
				descriptorIndex = constPool.getMethodTypeInfo(ix);
			}
			}

			String desc = constPool.getUtf8Info(descriptorIndex);

			for (int p = 0; p < desc.length(); p++) {
				if (desc.charAt(p) == 'L') {
					String classFullName = desc.substring(++p, p = desc.indexOf(';', p)).replace('/', '.');
					if (isDynamicClass(classFullName)) {
						set.add(classFullName);
					}
				}
			}
		}
		return set;
	}

	@Override
	public void onAppInfClassFileModify(File appInfClassFile) throws Exception {
		String classFulleName = serverClassLoaderFactory.toClassFullNameIfAppInfClassFile(appInfClassFile);

		Set<String> messageIDSet = classFullName2MessageIDSetHash.get(classFulleName);

		if (null != messageIDSet) {
			for (String messageID : messageIDSet) {
				// WARNING! 서버 비지니스 로직을 교체하기 위해서는 신규 서버 동적 클래스 로더로 교체한 상태에서 서버 비지니스 로직을 신규로 생성해야
				// 한다.
				// 따라서 먼저 서버 동적 클래스 로더를 신규로 변경 한후 메시지식별자별 서버 비지니스 로직 해쉬에서 수정한 파일에 의존성을 갖는 서버
				// 비지니스 로직을 제거하면
				// 해당 서버 비지니스 로직 요청시 신규 서버 동적 클래스 로더에서 서버 비지니스 로직을 신규로 생성하게 된다.
				synchronized (monitor) {
					if (currentServerClassLoader.isCreatedServerTask(messageID)) {
						currentServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
					}
				}

				messageID2ServerTaskHash.remove(messageID);
			}

		}

	}
}
