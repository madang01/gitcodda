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
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.server.classloader.ServerClassLoader;
import kr.pe.codda.server.classloader.ServerClassLoaderFactory;

/**
 * 서버 타스크 관리자
 * 
 * @author Won Jonghoon
 *
 */
public class ServerTaskManger implements ServerTaskMangerIF, ServerDynamicClassFileModifyEventListener {
	private Logger log = Logger.getLogger(ServerTaskManger.class.getName());

	private final Object monitor = new Object();
	private final ServerClassLoaderFactory serverClassLoaderFactory;
	
	private transient ServerClassLoader currentServerClassLoader = null;

	private final ConcurrentHashMap<String, AbstractServerTask> messageID2ServerTaskHash = new ConcurrentHashMap<String, AbstractServerTask>();
	private final ConcurrentHashMap<String, Set<String>> classFullName2MessageIDSetHash = new ConcurrentHashMap<String, Set<String>>();

	public ServerTaskManger(ServerClassLoaderFactory serverClassLoaderFactory) {
		this.serverClassLoaderFactory = serverClassLoaderFactory;

		currentServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
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

			messageID2ServerTaskHash.put(messageID, serverTask);
			
			updateDepencyTable(messageID);
			
		}

		return serverTask;
	}
	
	public void updateDepencyTable(String messageID) throws DynamicClassCallException {
		String serverTaskClassFullName = IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
		
		String serverTaskClassFilePathString = serverClassLoaderFactory.getClassFilePathString(serverTaskClassFullName);

		// 주어진 메시지 식별자에 1:1 대응하는 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합을 만든다
		Set<String> setOfDynamicClassesThatServerTaskDependsOn = toSetOfDynamicClasses(serverTaskClassFilePathString);

		for (String dynamicClassFullNameThatServerTaskDependsOn : setOfDynamicClassesThatServerTaskDependsOn) {
			// 만약 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합의 원소가 메시지일 경우
			// '메시지용 서버 메시지 코덱' 클래스가 의존하는 동적 클래스 집합을 구하여
			// 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합에 추가한다.
			if (isMessageClass(dynamicClassFullNameThatServerTaskDependsOn)) {

				String innerMessageID = toMessageID(dynamicClassFullNameThatServerTaskDependsOn);

				String serverMessageCodecClassFullName = IOPartDynamicClassNameUtil
						.getServerMessageCodecClassFullName(innerMessageID);
				
				
				String serverMessageCodecClassFilePathString = serverClassLoaderFactory.getClassFilePathString(serverMessageCodecClassFullName);
				
				Set<String> setOfDynamicClassesThatServerMessageCodecDependsOn = toSetOfDynamicClasses(serverMessageCodecClassFilePathString);

				setOfDynamicClassesThatServerTaskDependsOn
						.addAll(setOfDynamicClassesThatServerMessageCodecDependsOn);
			}
		}

		String serverMessageCodecClassFullName = IOPartDynamicClassNameUtil
				.getServerMessageCodecClassFullName(messageID);

		// 만약 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합에 '입력 메시지용 서버 메시지 코덱' 이 없다면
		// '입력 메시지용 서버 메시지 코덱' 클래스가 의존하는 동적 클래스 집합을 구하여
		// 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합에 추가한다.
		if (! setOfDynamicClassesThatServerTaskDependsOn.contains(serverMessageCodecClassFullName)) {
			
			String serverMessageCodecClassFilePathString = serverClassLoaderFactory.getClassFilePathString(serverMessageCodecClassFullName);
			
			Set<String> setOfDynamicClassesThatServerMessageCodecDependsOn = toSetOfDynamicClasses(serverMessageCodecClassFilePathString);
			setOfDynamicClassesThatServerTaskDependsOn.addAll(setOfDynamicClassesThatServerMessageCodecDependsOn);
		}

		// 서버 비지니스 로직 클래스가 의존하는 동적 클래스 집합의 내용을 바탕으로 '클래스 전체 이름별 메시지 식별자 집합 해쉬' 구성
		for (String dynamicClassFullNameThatServerTaskDependsOn : setOfDynamicClassesThatServerTaskDependsOn) {
			Set<String> messageIDSet = classFullName2MessageIDSetHash
					.get(dynamicClassFullNameThatServerTaskDependsOn);

			if (null == messageIDSet) {
				messageIDSet = new HashSet<String>();
				classFullName2MessageIDSetHash.put(dynamicClassFullNameThatServerTaskDependsOn, messageIDSet);
			}

			messageIDSet.add(messageID);
		}
	}

	public Set<String> toSetOfDynamicClasses(String classFilePathString) throws DynamicClassCallException {
		File classFile = new File(classFilePathString);		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(classFile);
			bis = new BufferedInputStream(fis);

			Set<String> dynamicClassSet = getDependentDynamicClassSet(bis);

			return dynamicClassSet;

		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get dependencies of the class[")
					.append(classFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		} finally {
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

	public String toMessageID(String messageClassFullName) {
		if (null == messageClassFullName) {
			throw new IllegalArgumentException("the parameter messageClassFullName is null");
		}

		int beginIndex = messageClassFullName.lastIndexOf('.');

		return messageClassFullName.substring(beginIndex + 1);
	}

	public boolean isMessageClass(String classFullName) {
		if (null == classFullName) {
			throw new IllegalArgumentException("the parameter classFullName is null");
		}

		if (classFullName.length() < CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME.length()) {
			return false;
		}

		String prefix = classFullName.substring(0, CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME.length());
		boolean ret = prefix.equals(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME);

		return ret;
	}

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

	/**
	 * 참조 :
	 * https://stackoverflow.com/questions/50019075/get-bytecode-dependency-information-from-class-files-through-java
	 * ===> answered Apr 26 '18 at 10:11 Holger
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public Set<String> getDependentDynamicClassSet(InputStream is) throws IOException {
		ClassFile cf = new ClassFile(new DataInputStream(is));
		ConstPool constPool = cf.getConstPool();
		HashSet<String> set = new HashSet<>();
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

	public String toClassFullName(File modifiedDynamicClassFile) {
		if (null == modifiedDynamicClassFile) {
			throw new IllegalArgumentException("the parameter modifiedDynamicClassFile is null");
		}

		String fileFullName = modifiedDynamicClassFile.getAbsolutePath();

		int beginIndex = fileFullName.indexOf(CommonStaticFinalVars.BASE_PACKAGE_NAME.replace('.', File.separatorChar));

		String partOfFileFullName = fileFullName.substring(beginIndex);

		String classFulleName = partOfFileFullName.replace(File.separatorChar, '.');

		return classFulleName;
	}

	@Override
	public void onServerDynamicClassFileModify(File modifiedDynamicClassFile) throws Exception {
		String classFulleName = toClassFullName(modifiedDynamicClassFile);

		Set<String> messageIDSet = classFullName2MessageIDSetHash.get(classFulleName);

		if (null != messageIDSet) {
			for (String messageID : messageIDSet) {
				// WARNING! 서버 비지니스 로직을 교체하기 위해서는 신규 서버 동적 클래스 로더로 교체한 상태에서 서버 비지니스 로직을 신규로 생성해야 한다.
				// 따라서 먼저 서버 동적 클래스 로더를 신규로 변경 한후 메시지식별자별 서버 비지니스 로직 해쉬에서 수정한 파일에 의존성을 갖는 서버 비지니스 로직을 제거하면
				// 해당 서버 비지니스 로직 요청시 신규 서버 동적 클래스 로더에서 서버 비지니스 로직을 신규로 생성하게 된다.
				synchronized (monitor) {
					if (currentServerClassLoader.isIncludedServerTask(messageID)) {
						currentServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
					}
				}

				messageID2ServerTaskHash.remove(messageID);
			}

		}

	}
}
