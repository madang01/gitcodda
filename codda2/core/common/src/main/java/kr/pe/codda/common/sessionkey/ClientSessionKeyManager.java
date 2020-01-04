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

package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.exception.SymmetricException;

public final class ClientSessionKeyManager {
	private static String symmetricKeyAlgorithm;
	private static int symmetricKeySize;
	private static int symmetricIVSize;
	
	// private static ClientSessionKeyIF mainClientSessionKey = null;	
	// private static ConcurrentHashMap<String, ClientSessionKeyIF> subProjectNameToClientSessionKeyHash = new ConcurrentHashMap<String, ClientSessionKeyIF>();
	// private static AllSubProjectPartConfiguration allSubProjectPartConfiguration;
	
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ClientSessionKeyManagerHolder {
		static final ClientSessionKeyManager singleton = new ClientSessionKeyManager();
	}

	
	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드 
	 * @return ClientSessionKeyManager 객체
	 */
	public static ClientSessionKeyManager getInstance() {
		return ClientSessionKeyManagerHolder.singleton;
	}
	
	private ClientSessionKeyManager() {
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();
		// allSubProjectPartConfiguration = runningProjectConfiguration.getAllSubProjectPartConfiguration();		
	}

	/*
	public synchronized ClientSessionKeyIF getMainProjectClientSessionKey() throws SymmetricException, InterruptedException {
		if (null == mainClientSessionKey) {
			
			byte[] publicKeyBytes = clientRSAPublickeyGetter.getMainProjectPublickeyBytes();
			mainClientSessionKey = new ClientSessionKey(new ClientRSA(publicKeyBytes), symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isBase64);
		}
		return mainClientSessionKey;
	}
	
	public synchronized ClientSessionKeyIF getSubProjectClientSessionKey(String subProjectName, AbstractRSAPublickeyGetter clientRSAPublickeyGetter, boolean isBase64) throws IllegalArgumentException, SymmetricException {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}
				
		if (! allSubProjectPartConfiguration.isRegistedSubProjectName(subProjectName)) {				
			throw new IllegalArgumentException("the parameter subProjectName is not registered in configuration file");
		}
		
		ClientSessionKeyIF subClientSessionKey = subProjectNameToClientSessionKeyHash.get(subProjectName);
		
		if (null == subClientSessionKey) {
			byte[] publicKeyBytes = clientRSAPublickeyGetter.getSubProjectPublickeyBytes(subProjectName);
			subClientSessionKey = new ClientSessionKey(new ClientRSA(publicKeyBytes), symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isBase64);	
			subProjectNameToClientSessionKeyHash.put(subProjectName, subClientSessionKey);
		}
		 
		return subClientSessionKey;
	}
	
	*/
	
	public ClientSessionKeyIF createNewClientSessionKey(byte[] publicKeyBytes, boolean isBase64) throws SymmetricException {
		if (null == publicKeyBytes) {
			throw new IllegalArgumentException("the parameter publicKeyBytes is null");
		}
		
		return new ClientSessionKey(new ClientRSA(publicKeyBytes), symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isBase64);
	}
}
