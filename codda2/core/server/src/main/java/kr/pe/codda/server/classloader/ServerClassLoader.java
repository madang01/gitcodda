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

package kr.pe.codda.server.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.MessageEncoderManagerIF;
import kr.pe.codda.common.classloader.SystemClassVerifierIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.server.task.AbstractServerTask;

/**
 * 간략 클래스 로더
 * @author Won Jonghoon
 *
 */
public class ServerClassLoader extends ClassLoader implements MessageEncoderManagerIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	// private final Object monitor = new Object();

	private final ServerClassLoaderFactory serverClassLoaderFactory;
	private final String classloaderReousrcesPathString;
	private final SystemClassVerifierIF systemClassVerifier;
	
	private final static ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
	
	private final HashMap<String, MessageCodecIF> messageCodecHash = new  HashMap<String, MessageCodecIF>();
	
	private final HashMap<String, AbstractServerTask> severTaskHash = new  HashMap<String, AbstractServerTask>();
	
	
	/**
	 * 생성자
	 * @param classloaderClassPathString 동적 클래스 경로 문자열
	 * @param classloaderReousrcesPathString 동적 클래스 자원 경로 문자열
	 * @param systemClassVerifier 내장중인 시스템 클래스 검증기
	 */
	public ServerClassLoader(ServerClassLoaderFactory serverClassLoaderFactory, String classloaderReousrcesPathString,
			SystemClassVerifierIF systemClassVerifier) {
		super(systemClassLoader);
		
		this.serverClassLoaderFactory = serverClassLoaderFactory;
		this.classloaderReousrcesPathString = classloaderReousrcesPathString;
		this.systemClassVerifier = systemClassVerifier;		

		log.log(Level.INFO, "ServerClassLoader hashCode=[" + this.hashCode() + "] create");
	}
	
	public void createServerTask(String messageID) {
		
	}

	
	
	/**
	 * Warning! 효율을 위해서 이 메소드는 thread safe 를 지원하지 않는다. 하여 외부에서 이를 보장해야 한다.
	 */
	@Override
	public Class<?> loadClass(String classFullName) throws ClassNotFoundException {
		Class<?> retClass = null;
		// try {
		// synchronized (monitor) {
		retClass = findLoadedClass(classFullName);
		if (null == retClass) {			
			if (systemClassVerifier.isSystemClassName(classFullName)) {
				return systemClassLoader.loadClass(classFullName);
			}

			retClass = doLoadClass(classFullName);
		} else {
			log.log(Level.INFO, "the class[" + classFullName + "] is already loaded");
		}
		

		return retClass;
	}

	/**
	 * 지정한 클래스 전체 이름과 지정한 파일로 부터 적재된 클래스를 반환한다
	 * @param classFullName 클래스 전체 이름
	 * @return 지정한 클래스 전체 이름과 지정한 파일로 부터 적재된 클래스
	 * @throws ClassNotFoundException 처리중 에러 발생시 던지는 예외
	 */
	private Class<?> doLoadClass(String classFullName) throws ClassNotFoundException {
		String classFileName = serverClassLoaderFactory.getClassFilePathString(classFullName);

		// log.info("classFileName={}", classFileName);

		File classFile = new File(classFileName);
		if (!classFile.exists()) {
			String errorMessage = new StringBuilder()
					.append("the class[")
					.append(classFullName)
					.append("] file[")
					.append(classFileName)
					.append("] doesn't exist").toString();

			log.log(Level.WARNING, errorMessage);

			throw new ClassNotFoundException(errorMessage);
		}

		if (classFile.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the class[")
					.append(classFullName)
					.append("] file[")
					.append(classFileName)
					.append("] is a directory").toString();
			log.log(Level.WARNING, errorMessage);
			throw new ClassNotFoundException(errorMessage);
		}

		if (! classFile.canRead()) {
			String errorMessage = new StringBuilder()
					.append("it can't read the class[")
					.append(classFullName)
					.append("] file[")
					.append(classFileName)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage);
			throw new ClassNotFoundException(errorMessage);
		}
		
		Class<?> retClass = null;

		/** 서버 비지니스 로직 클래스 */
		try {
			long fileSize = classFile.length();

			if (fileSize > Integer.MAX_VALUE) {
				throw new ClassFormatError("over max size of file");
			}
			
			byte[] dynamicClassfileBytes = Files.readAllBytes(classFile.toPath());			

			retClass = defineClass(classFullName, dynamicClassfileBytes, 0, dynamicClassfileBytes.length);

		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("fail to read the class[")
					.append(classFullName)
					.append("][")
					.append(classFile.getAbsolutePath())
					.append("] in this classloader[")
					.append(this.hashCode())
					.append("]").toString();
			
			log.log(Level.WARNING, errorMessage, e);			

			throw new ClassNotFoundException(errorMessage);

		} catch (ClassFormatError e) {
			String errorMessage = new StringBuilder()
					.append("fail to define the class[")
					.append(classFullName)
					.append("][")
					.append(classFile.getAbsolutePath())
					.append("] in this classloader[")
					.append(this.hashCode())
					.append("]").toString();
			
			log.log(Level.WARNING, errorMessage, e);

			throw new ClassNotFoundException(errorMessage);
		}

		
		return retClass;
	}
	
	
	public InputStream getResourceAsStream(String name) {
		InputStream is = null;

		String realResourceFilePathString = CommonStaticUtil
				.buildFilePathStringFromResourcePathAndRelativePathOfFile(classloaderReousrcesPathString, name);
		
		// log.info("realResourceFilePathString=[{}]", realResourceFilePathString);
		
		
		File realResourceFile = new File(realResourceFilePathString);

		if (realResourceFile.exists()) {
			try {
				is = new FileInputStream(realResourceFile);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to get a input stream of the resource[").append(name).append("][")
						.append(realResourceFilePathString).append("]")
						.toString();
				log.log(Level.WARNING, errorMessage, e);
				return null;
			}
		} else {
			is = super.getResourceAsStream(name);
		}

		return is;
	}

	/*public URL getResource(String name) {
		URL url = null;

		String realResourceFilePathString = CommonStaticUtil
				.getFilePathStringFromResourcePathAndRelativePathOfFile(classloaderReousrcesPathString, name);
		File realResourceFile = new File(realResourceFilePathString);

		if (realResourceFile.exists()) {
			try {
				url = realResourceFile.toURI().toURL();
			} catch (Exception e) {
				log.warn(new StringBuilder("the resource[").append(name).append("] file[")
						.append(realResourceFilePathString).append("] fail to convert to url").toString(), e);
				return null;
			}
		} else {
			url = super.getResource(name);
		}

		return url;
	}*/
	
	public boolean isIncludedServerTask(String messageID) {
		return severTaskHash.containsKey(messageID);
	}
	
	public AbstractServerTask getServerTask(String messageID) {
		return severTaskHash.get(messageID);
	}

	public AbstractServerTask createNewServerTask(String messageID) throws DynamicClassCallException {
		String classFullName = IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
		
		Class<?> serverTaskClass = null;
		Object serverTaskInstance = null;
		try {
			serverTaskClass = loadClass(classFullName);
		} catch (ClassNotFoundException e) {
			String errorMessage = new StringBuilder("the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] is not found").toString();
			
			//log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("fail to load the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] class, errmsg=")
					.append(e.getMessage()).toString();
			
			//log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}
		
		try {
			serverTaskInstance = serverTaskClass.getDeclaredConstructor().newInstance();
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("fail to create a new instance of the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] class").toString();
			
			//log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}
		
		if (! (serverTaskInstance instanceof AbstractServerTask)) {
			String errorMessage = new StringBuilder("the new instance[")
					.append(classFullName)
					.append("] is not a server task class").toString();
			
			//log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}
		
		AbstractServerTask serverTask = (AbstractServerTask)serverTaskInstance;
		
		severTaskHash.put(messageID, serverTask);
		
		return serverTask;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 파라미터 'messageID' 에 1:1 대응하는 서버 메시지 코덱
	 * @throws DynamicClassCallException 동적 클래스 관련 처리중 에러 발생시 던지는 예외
	 */
	public MessageCodecIF getServerMessageCodec(String messageID) throws DynamicClassCallException {
		MessageCodecIF messageCodec = messageCodecHash.get(messageID);
		
		if (null == messageCodec) {
			synchronized (messageCodecHash) {
				messageCodec = messageCodecHash.get(messageID);
				
				if (null == messageCodec) {
					messageCodec = createNewServerMessageCodec(messageID);			
					messageCodecHash.put(messageID, messageCodec);
				}
			}
		}		
		
		return messageCodec;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 파라미터 'messageID' 에 1:1 대응하는 신규 서버 메시지 코덱
	 * @throws DynamicClassCallException 동적 클래스 관련 처리중 에러 발생시 던지는 예외
	 */
	private MessageCodecIF createNewServerMessageCodec(String messageID) throws DynamicClassCallException {
		String classFullName = IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);
		
		
		Class<?> messageCodecClass = null;
		Object messageCodecInstance = null;
		try {
			messageCodecClass = loadClass(classFullName);
		} catch (ClassNotFoundException e) {
			String errorMessage = new StringBuilder("the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] is not found").toString();
			
			//log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("fail to load the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] class, errmsg=")
					.append(e.getMessage()).toString();
			
			//log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}
		
		try {
			messageCodecInstance = messageCodecClass.getDeclaredConstructor().newInstance();
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("fail to create a new instance of the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] class").toString();
			
			//log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}
		
		if (! (messageCodecInstance instanceof MessageCodecIF)) {
			String errorMessage = new StringBuilder("the new instance[")
					.append(classFullName)
					.append("] is not a server message codec class").toString();
			
			//log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}
		
		MessageCodecIF messageCodec = (MessageCodecIF)messageCodecInstance;
		
		return messageCodec;
	}
	
	public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException {
		MessageCodecIF messageCodec = getServerMessageCodec(messageID);		
		return messageCodec.getMessageEncoder();
	}
	

	@Override
	protected void finalize() throws Throwable {
		// FIXME! 메모리 회수 확인용으로 삭제하지 마세요!
		log.log(Level.INFO, "ServerClassLoader[" + this.hashCode() + "] destroy");
	}
}
