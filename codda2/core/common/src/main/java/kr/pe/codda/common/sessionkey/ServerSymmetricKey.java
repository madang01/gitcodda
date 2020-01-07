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
 * 서버에서 클라이언트가 보낸 세션키로 부터 생성되는 대칭키
 * @author Won Jonghoon
 *
 */
public class ServerSymmetricKey implements ServerSymmetricKeyIF {
	private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
	
	private final byte[] symmetricKeyBytes;
	private final String symmetricKeyAlgorithm;
	// private final int symmetricKeySize;	
	private byte ivBytes[] = null;
	// private final int symmetricIVSize;
	
	/**
	 * 생성자
	 * @param symmetricKeyAlgorithm 대칭키 알고리즘
	 * @param symmetricKeyBytes 대칭키 값
	 * @param ivBytes iv 값
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public ServerSymmetricKey(String symmetricKeyAlgorithm, byte[] symmetricKeyBytes, byte[] ivBytes) throws SymmetricException {
		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}
		
		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("the parameter symmetricKeyBytes is null");
		}
		
		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
		}
		
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
