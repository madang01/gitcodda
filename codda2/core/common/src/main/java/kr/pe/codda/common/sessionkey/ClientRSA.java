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

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;

/**
 * 클라이언트용 RSA
 * 
 * @author Won Jonghoon
 *
 */
public class ClientRSA implements ClientRSAIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);	
	
	private final byte[] publicKeyBytes;	
	
	/**
	 * 생성자
	 * @param publicKeyBytes 공개키 바이트 배열
	 * @throws SymmetricException 암호화 관련 처리중 에러 발생시 던지는 예외
	 */
	public ClientRSA(byte[] publicKeyBytes) throws SymmetricException {
		if (null == publicKeyBytes) {
			throw new IllegalArgumentException("the parameter publicKeyBytes is null");
		}
		
		this.publicKeyBytes = publicKeyBytes;		
	}

	@Override
	public byte[] getDupPublicKeyBytes() {		
		return Arrays.copyOf(publicKeyBytes, publicKeyBytes.length);
	}
	
	@Override
	public byte[] encrypt(byte plainTextBytes[]) throws SymmetricException {
		if (null == plainTextBytes) {
			throw new IllegalArgumentException("the parameter plainTextBytes is null");
		}
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder()
					.append("NoSuchAlgorithmException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				publicKeyBytes);
		
		PublicKey publicKey = null;
		try {
			publicKey = rsaKeyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String
					.format("RSA Public Key InvalidKeySpecException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		
		Cipher rsaCipher = null;
		try {
			rsaCipher = Cipher.getInstance(CommonStaticFinalVars.RSA_TRANSFORMATION);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("RSA Cipher.getInstance NoSuchAlgorithmException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = String
					.format("RSA Cipher NoSuchPaddingException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		try {
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		} catch (InvalidKeyException e) {
			String errorMessage = String
					.format("RSA Cipher InvalidKeyException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		
		byte encryptedBytes[] = null;
		try {
			encryptedBytes = rsaCipher
					.doFinal(plainTextBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = String
					.format("RSA Cipher IllegalBlockSizeException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = String
					.format("RSA Cipher BadPaddingException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		return encryptedBytes;
	}
	
}
