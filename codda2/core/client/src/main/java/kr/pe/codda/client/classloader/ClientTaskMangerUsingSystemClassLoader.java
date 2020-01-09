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

import java.util.HashMap;

import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * 시스템 클래스 로더를 사용하는 클라이언트 타스크 관리자, WARNING! 아직 클라이언트 타스트에 동적 클래스 로더를 적용하지 못하여 임시 변통하여 만든 클래스.
 * 
 * @author Won Jonghoon
 *
 */
public class ClientTaskMangerUsingSystemClassLoader implements ClientTaskMangerIF {
	//private final Object monitor = new Object();
	private final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
	
	private final HashMap<String, AbstractClientTask> messageID2ClientTaskHash = new HashMap<String, AbstractClientTask>();	

	@Override
	public AbstractClientTask getValidClientTask(String messageID) throws DynamicClassCallException {
		//synchronized (monitor) {
			AbstractClientTask clientTask = messageID2ClientTaskHash.get(messageID);
			
			if (null == clientTask) {
				String clientTaskClassFullName = IOPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);
				
				Object retObject = CommonStaticUtil.createtNewObject(systemClassLoader, clientTaskClassFullName);
				
				if (! (retObject instanceof AbstractClientTask)) {
					String errorMessage = new StringBuilder()
					.append("this instance of ").append(clientTaskClassFullName)
					.append(" class that was created by system classloader is not a instance of AbstractClientTask class").toString();

					throw new DynamicClassCallException(errorMessage);
				}
				
				clientTask = (AbstractClientTask) retObject;
			}		
			
			return clientTask;
		//}
	}
}
