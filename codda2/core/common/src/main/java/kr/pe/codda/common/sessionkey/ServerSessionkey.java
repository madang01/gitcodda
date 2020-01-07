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

import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * 서버 세션키
 * @author Won Jonghoon
 *
 */
public class ServerSessionkey implements ServerSessionkeyIF {
	
	private final ServerRSAIF serverRSA;
	private final String symmetricKeyAlgorithm;
	private final int symmetricKeySize;
	private final int symmetricIVSize;
	
	/**
	 * 생성자
	 * @param serverRSA 서버 RSA
	 * @param symmetricKeyAlgorithm 대칭키 알고리즘
	 * @param symmetricKeySize 대칭키 크기
	 * @param symmetricIVSize iv 크기
	 */
	public ServerSessionkey(ServerRSAIF serverRSA, String symmetricKeyAlgorithm, int symmetricKeySize, int symmetricIVSize) {
		if (null == serverRSA) {
			throw new IllegalArgumentException("the parameter serverRSA is null");
		}
		
		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}
		
		this.serverRSA = serverRSA;
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
		this.symmetricKeySize = symmetricKeySize;
		this.symmetricIVSize = symmetricIVSize; 
	}
	
	@Override
	public ServerSymmetricKeyIF createNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		return this.createNewInstanceOfServerSymmetricKey(false, sessionkeyBytes, ivBytes);
	}
	
	
	@Override
	public ServerSymmetricKeyIF createNewInstanceOfServerSymmetricKey(boolean whetherToApplyBase64ToSymmetricKeyForSssionKey, byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		// log.info("isBase64={}", isBase64);
		
		if (null == sessionkeyBytes) {
			throw new IllegalArgumentException("the parameter sessionkeyBytes is null");
		}
		
		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
		}
		
		if (symmetricIVSize != ivBytes.length) {
			String errorMessage = new StringBuilder()
					.append("the parameter ivBytes length[")
					.append(ivBytes.length)
					.append("] is differenct from symmetric iv size[")
					.append(symmetricIVSize)
					.append("] of configuration").toString();
			
			
			throw new SymmetricException(errorMessage);
		}		
		
		final byte[] symmetricKeyBytes;
		
		if (whetherToApplyBase64ToSymmetricKeyForSssionKey) {
			byte[] base64EncodedStringBytes = serverRSA.decrypt(sessionkeyBytes);
			try {
				symmetricKeyBytes = CommonStaticUtil.Base64Decoder.decode(base64EncodedStringBytes);
			} catch (Exception e) {
				String errorMessage = "fail to decode the parameter sessionkeyBytes using base64";
				
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, errorMessage, e);
				
				throw new SymmetricException(errorMessage);
			}
			
			
		} else {
			symmetricKeyBytes = serverRSA.decrypt(sessionkeyBytes);
		}
		
		if (symmetricKeySize != symmetricKeyBytes.length) {
			String errorMessage = new StringBuilder()
					.append("the parameter sessionkeyBytes's length[")
					.append(symmetricKeyBytes.length)
					.append("] is differenct from symmetric key size[")
					.append(symmetricKeySize)
					.append("] of configuration").toString();
			
			throw new SymmetricException(errorMessage);
		}
		
		return new ServerSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
	}	
	
	@Override
	public String getModulusHexStrForWeb() {
		return serverRSA.getModulusHexStrForWeb();
	}
	
	@Override
	public final byte[] getDupPublicKeyBytes() {
		return serverRSA.getDupPublicKeyBytes();
	}
	
	@Override
	public byte[] decryptUsingPrivateKey(byte[] encryptedBytesWithPublicKey) throws SymmetricException {		
		return serverRSA.decrypt(encryptedBytesWithPublicKey);
	}
}
