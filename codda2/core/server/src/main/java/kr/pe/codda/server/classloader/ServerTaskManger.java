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
import java.util.HashMap;

import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.ServerClassLoader;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.server.task.AbstractServerTask;

/**
 * 서버 타스크 관리자
 * @author Won Jonghoon
 *
 */
public class ServerTaskManger implements ServerTaskMangerIF {
	// private InternalLogger log = InternalLoggerFactory.getInstance(ServerDynamicObjectManger.class);

	// private final Object monitor = new Object();

	private ServerClassLoaderFactory serverClassLoaderFactory = null;
	private ServerClassLoader currentWorkingServerClassLoader = null;

	private final HashMap<String, ServerTaskInfomation> messageID2ServerTaskInformationHash = new HashMap<String, ServerTaskInfomation>();
	// private ReentrantLock lock = new ReentrantLock();

	public ServerTaskManger(ServerClassLoaderFactory serverClassLoaderFactory) {
		this.serverClassLoaderFactory = serverClassLoaderFactory;

		this.currentWorkingServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
	}

	/**
	 * @param messageID 메시지 식별자
	 * @return 파라미터 'messageID'(=메시지 식별자) 에 1:1 대응하는 신규 서버 타스크 정보 객체
	 * @throws DynamicClassCallException 동적 클래스 관련 처리중 에러 발생시 던지는 예외
	 */
	private ServerTaskInfomation createNewServerTaskInformation(String messageID)
			throws DynamicClassCallException {
		String serverTaskClassFullName = IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
		
		Object retObject = CommonStaticUtil.createtNewObject(currentWorkingServerClassLoader, serverTaskClassFullName);
		
		if (! (retObject instanceof AbstractServerTask)) {
			String errorMessage = new StringBuilder()
			.append("this instance of ")
			.append(serverTaskClassFullName)
			.append(" class that was created by server dynamic classloader[")
			.append(currentWorkingServerClassLoader.hashCode())			
			.append("] class is not a instance of AbstractServerTask class").toString();

			throw new DynamicClassCallException(errorMessage);
		}
		
		AbstractServerTask serverTask = (AbstractServerTask) retObject;
		String serverTaskClassFilePathString = currentWorkingServerClassLoader.getClassFilePathString(serverTaskClassFullName);

		// serverTask.setServerSimpleClassloader(currentWorkingClassLoader);;
		// log.info("classFileName={}", classFileName);

		File serverTaskClassFile = new File(serverTaskClassFilePathString);

		return new ServerTaskInfomation(serverTaskClassFile, serverTask);
	}
	
	
	/**
	 * 유효한 서버 타스크 정보를 반환한다.
	 * 
	 * 유효한 서버 타스크 정보란 서버 타스크 클래스 파일을 적재한 시간과 서버 타스크 클래스 파일이 변경된 시간이 일치되는 서버 타스크를 갖는 '서버 타스크 정보' 를 뜻한다.
	 * 내부적으로는 해쉬를 이용하여 파라미터 'messageID'(=메시지 식별자) 에 1:1 대응하는 '서버 타스크 정보' 를 넘겨주는데
	 * 만약 1:1 대응하는 '서버 타스크 정보'가 없거나 적재한 시간과 서버 타스크 파일 수정 시간이 틀린 경우에는 
	 * 신규 '서버 타스크 정보' 를 만들어 해쉬에 등록 후 반환해 준다.
	 * 
	 * @param messageID 메시지 식별자
	 * @return 유효한 서버 타스크 정보
	 * @throws DynamicClassCallException 동적 클래스 관련 처리중 에러 발생시 던지는 예외
	 */
	private ServerTaskInfomation getVadlinServerTaskInfomation(String messageID)
			throws DynamicClassCallException {
		ServerTaskInfomation serverTaskInfomation = messageID2ServerTaskInformationHash.get(messageID);
		if (null == serverTaskInfomation) {
			// lock.lock();			
			serverTaskInfomation = createNewServerTaskInformation(messageID);

			messageID2ServerTaskInformationHash.put(messageID, serverTaskInfomation);
						
		} else if (serverTaskInfomation.isModifed()) {
			/** 새로운 서버 클래스 로더로 교체 */
			currentWorkingServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
			serverTaskInfomation = createNewServerTaskInformation(messageID);
			messageID2ServerTaskInformationHash.put(messageID, serverTaskInfomation);
				
		}
		return serverTaskInfomation;
	}
	
	@Override
	public AbstractServerTask getValidServerTask(String messageID) throws DynamicClassCallException {
		ServerTaskInfomation serverTaskInfomation = getVadlinServerTaskInfomation(messageID);
	
		return serverTaskInfomation.getServerTask();
	}
}
