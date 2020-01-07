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
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.ServerClassLoader;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientDynamicTaskManger implements ClientTaskMangerIF {
	//private final Object monitor = new Object();
	
	private ClientClassLoaderFactory clientClassLoaderFactory = null;
	private ServerClassLoader currentWorkingClientClassLoader = null;

	private final HashMap<String, ClientTaskInfomation> messageID2ClientTaskInformationHash = new HashMap<String, ClientTaskInfomation>();
	
	public ClientDynamicTaskManger(ClientClassLoaderFactory clientClassLoaderFactory) {
		this.clientClassLoaderFactory = clientClassLoaderFactory;

		this.currentWorkingClientClassLoader = clientClassLoaderFactory.createClientClassLoader();
	}
	
	private ClientTaskInfomation getNewClientTaskFromWorkBaseClassload(String messageID)
			throws DynamicClassCallException {
		String clientTaskClassFullName = IOPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);
		
		Object retObject = CommonStaticUtil.createtNewInstance(currentWorkingClientClassLoader, clientTaskClassFullName);
		
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
	
	private ClientTaskInfomation getClientTaskInfomation(String messageID)
			throws DynamicClassCallException {
		//synchronized (monitor) {
			ClientTaskInfomation clientTaskInfomation = messageID2ClientTaskInformationHash.get(messageID);
			if (null == clientTaskInfomation) {
				// lock.lock();			
				clientTaskInfomation = getNewClientTaskFromWorkBaseClassload(messageID);

				messageID2ClientTaskInformationHash.put(messageID, clientTaskInfomation);
							
			} else if (clientTaskInfomation.isModifed()) {
				/** 새로운 서버 클래스 로더로 교체 */
				currentWorkingClientClassLoader = clientClassLoaderFactory.createClientClassLoader();
				clientTaskInfomation = getNewClientTaskFromWorkBaseClassload(messageID);
				messageID2ClientTaskInformationHash.put(messageID, clientTaskInfomation);
					
			}
			return clientTaskInfomation;
		//}		
	}

	@Override
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException {
		ClientTaskInfomation clientTaskInfomation = getClientTaskInfomation(messageID);
		return clientTaskInfomation.getClientTask();
	}

}
