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

import kr.pe.codda.common.exception.SymmetricException;

/**
 * 클라이언트 대칭키
 * 
 * @author Won Jonghoon
 *
 */
public class ClientSymmetricKey implements ClientSymmetricKeyIF {
	private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
	
	private final String symmetricKeyAlgorithm;
	private final byte[] symmetricKeyBytes;
	private final byte[] ivBytes;
	
	/**
	 * 생성자
	 * @param symmetricKeyAlgorithm 대칭키 알고리즘 이름
	 * @param symmetricKeyBytes 대칭키 값
	 * @param ivBytes iv 값
	 * @throws SymmetricException 암호화 관련 처리중 에러 발생시 던지는 예외
	 */
	public ClientSymmetricKey(String symmetricKeyAlgorithm,  byte[] symmetricKeyBytes, byte[] ivBytes) throws SymmetricException {
		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}
		
		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("the parameter symmetricKeyBytes is null");
		}
		
		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
		}
		
		/*
				
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		*/
		
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
		this.symmetricKeyBytes = symmetricKeyBytes;
		this.ivBytes = ivBytes;
		
	}
	
	@Override
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException {
		return symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
	}
	
	@Override
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException {
		return symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
	}
	
}
