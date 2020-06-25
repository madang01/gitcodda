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

import kr.pe.codda.common.classloader.ClientClassLoader;
import kr.pe.codda.common.classloader.SystemClassDeterminer;
import kr.pe.codda.common.classloader.SystemClassVerifierIF;
import kr.pe.codda.common.exception.PartConfigurationException;

/**
 * 클라이언트 클래스 로더 팩토리
 * @author Won Jonghoon
 *
 */
public class ClientClassLoaderFactory {
	private String clientClassloaderClassPathString = null;
	private String clientClassloaderReousrcesPathString = null;
	private SystemClassVerifierIF excludedDynamicClassManager = new SystemClassDeterminer();
	
	/**
	 * 생성자
	 * @param clientClassloaderClassPathString 클래스 로더의 동적 클래스가 있는 경로
	 * @param clientClassloaderReousrcesPathString 클래스 로더의 리소스 경로
	 * @throws PartConfigurationException 설정 관련 처리중 에러 발생시 던지는 예외
	 */
	public ClientClassLoaderFactory(String clientClassloaderClassPathString,
			String clientClassloaderReousrcesPathString) throws PartConfigurationException {
		if (null == clientClassloaderClassPathString) {
			throw new IllegalArgumentException("the parameter clientClassloaderClassPathString is null");
		}

		File clientAPPINFClassPath = new File(clientClassloaderClassPathString);
		
		if (!clientAPPINFClassPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the client APP-INF class path[")
					.append(clientClassloaderClassPathString)
					.append("] doesn't exist").toString();
		 	throw new PartConfigurationException(errorMessage);
		}
		
		if (!clientAPPINFClassPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the client APP-INF class path[")
					.append(clientClassloaderClassPathString)
					.append("] isn't a directory").toString();
		 	throw new PartConfigurationException(errorMessage);
		}
		
		if (null == clientClassloaderReousrcesPathString) {
			throw new IllegalArgumentException("the parameter clientClassloaderReousrcesPathString is null");
		}
		
		File projectResourcesPath = new File(clientClassloaderReousrcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the project resources path[")
					.append(clientClassloaderReousrcesPathString)
					.append("] doesn't exist").toString();
		 	throw new PartConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the project resources path[")
					.append(clientClassloaderReousrcesPathString)
					.append("] isn't a directory").toString();
		 	throw new PartConfigurationException(errorMessage);
		}
		
		this.clientClassloaderClassPathString = clientClassloaderClassPathString;
		this.clientClassloaderReousrcesPathString = clientClassloaderReousrcesPathString;		
	}
	
	/**
	 * @return 신규 클라이언트 클래스 로더
	 */
	public ClientClassLoader createNewClientClassLoader() {
		return new ClientClassLoader(clientClassloaderClassPathString, clientClassloaderReousrcesPathString, excludedDynamicClassManager);
	}
}
