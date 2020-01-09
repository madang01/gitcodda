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

package kr.pe.codda.client.classloader;

import java.io.File;
import java.util.HashMap;

import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.classloader.ClientClassLoader;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * 클라이언트 타스크 관리자, WARNING! 현재 단일 쓰레드에서 돌아가기때문에 Thread safe 로직 제거하였음.
 * 
 * @author Won Jonghoon
 *
 */
public class ClientTaskManger implements ClientTaskMangerIF {
	//private final Object monitor = new Object();
	
	private ClientClassLoaderFactory clientClassLoaderFactory = null;
	private ClientClassLoader currentWorkingClientClassLoader = null;

	private final HashMap<String, ClientTaskInfomation> messageID2ClientTaskInformationHash = new HashMap<String, ClientTaskInfomation>();
	
	/**
	 * 생성자
	 * @param clientClassLoaderFactory 클라이언트 로더 팩토리
	 */
	public ClientTaskManger(ClientClassLoaderFactory clientClassLoaderFactory) {
		this.clientClassLoaderFactory = clientClassLoaderFactory;

		this.currentWorkingClientClassLoader = clientClassLoaderFactory.createNewClientClassLoader();
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 파라미터 'messageID'(=메시지 식별자) 에 1:1 대응하는 신규 클라이언트 타스크 정보 객체
	 * @throws DynamicClassCallException 동적 클래스 관련 처리중 에러 발생시 던지는 예외
	 */
	private ClientTaskInfomation createNewClientTaskInformation(String messageID)
			throws DynamicClassCallException {
		String clientTaskClassFullName = IOPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);
		
		Object retObject = CommonStaticUtil.createtNewObject(currentWorkingClientClassLoader, clientTaskClassFullName);
		
		if (! (retObject instanceof AbstractClientTask)) {
			String errorMessage = new StringBuilder()
					.append("this instance of ")
					.append(clientTaskClassFullName)
					.append(" class that was created by client dynamic classloader[")
					.append(currentWorkingClientClassLoader.hashCode())			
					.append("] class is not a instance of AbstractClientTask class").toString();
			throw new DynamicClassCallException(errorMessage);
		}
		
		AbstractClientTask clientTask = (AbstractClientTask) retObject;
		String clientTaskClassFilePathString = currentWorkingClientClassLoader.getClassFilePathString(clientTaskClassFullName);

		// serverTask.setServerSimpleClassloader(currentWorkingClassLoader);;
		// log.info("classFileName={}", classFileName);

		File clientTaskClassFile = new File(clientTaskClassFilePathString);

		return new ClientTaskInfomation(clientTaskClassFile, clientTask);
	}
	
	/**
	 * 유효한 클라이언트 타스크 정보를 반환한다.
	 * 
	 * 유효한 클라이언트 타스크 정보란 클라이언트 타스크 클래스 파일을 적재한 시간과 클라이언트 타스크 클래스 파일이 변경된 시간이 일치되는 클라이언트 타스크를 갖는 '클라이언트 타스크 정보' 를 뜻한다.
	 * 내부적으로는 해쉬를 이용하여 파라미터 'messageID'(=메시지 식별자) 에 1:1 대응하는 '클라이언트 타스크 정보' 를 넘겨주는데
	 * 만약 1:1 대응하는 '클라이언트 타스크 정보'가 없거나 적재한 시간과 클라이언트 타스크 파일 수정 시간이 틀린 경우에는 
	 * 신규 '클라이언트 타스크 정보' 를 만들어 해쉬에 등록 후 반환해 준다.
	 * 
	 * @param messageID 메시지 식별자
	 * @return 유효한 클라이언트 타스크 정보
	 * @throws DynamicClassCallException 동적 클래스 관련 처리중 에러 발생시 던지는 예외
	 */
	private ClientTaskInfomation getVadliClientTaskInfomation(String messageID)
			throws DynamicClassCallException {
		//synchronized (monitor) {
			ClientTaskInfomation clientTaskInfomation = messageID2ClientTaskInformationHash.get(messageID);
			if (null == clientTaskInfomation) {
				// lock.lock();			
				clientTaskInfomation = createNewClientTaskInformation(messageID);

				messageID2ClientTaskInformationHash.put(messageID, clientTaskInfomation);
							
			} else if (clientTaskInfomation.isModifed()) {
				/** 새로운 서버 클래스 로더로 교체 */
				currentWorkingClientClassLoader = clientClassLoaderFactory.createNewClientClassLoader();
				clientTaskInfomation = createNewClientTaskInformation(messageID);
				messageID2ClientTaskInformationHash.put(messageID, clientTaskInfomation);
					
			}
			return clientTaskInfomation;
		//}		
	}

	@Override
	public AbstractClientTask getValidClientTask(String messageID) throws DynamicClassCallException {
		ClientTaskInfomation clientTaskInfomation = getVadliClientTaskInfomation(messageID);
		return clientTaskInfomation.getClientTask();
	}

}
