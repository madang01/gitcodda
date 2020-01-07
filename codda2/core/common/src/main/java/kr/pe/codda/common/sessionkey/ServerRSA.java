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

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.HexUtil;

/**
 * 서버 RSA
 * @author Won Jonghoon
 *
 */
public final class ServerRSA implements ServerRSAIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final KeyPair rsaKeypair;
	private final String rsaPrivateKeyModulesHexString;
	// private final BigInteger modulusOfRSAPrivateCrtKeySpec;
	
	/**
	 * 생성자
	 * @param rsaKeypair RSA 키 쌍
	 * @throws SymmetricException 암호화 관련 처리중 에러 발생시 던지는 예외
	 */
	public ServerRSA(KeyPair rsaKeypair) throws SymmetricException {
		if (null == rsaKeypair) {
			throw new IllegalArgumentException("the parameter rsaKeypair is null");
		}		

		this.rsaKeypair = rsaKeypair; 
		
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder()
					.append("fail to get the RSA KeyFactory, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}
		
		RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = null;
		try {
			rsaPrivateCrtKeySpec = rsaKeyFactory.getKeySpec(rsaKeypair.getPrivate(), RSAPrivateCrtKeySpec.class);			
		} catch (InvalidKeySpecException e) {
			String errorMessage = new StringBuilder()
					.append("fail to get the RSA private key spec(=RSAPrivateCrtKeySpec), errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}	
		
		BigInteger modulusOfRSAPrivateCrtKeySpec = rsaPrivateCrtKeySpec.getModulus();
		
		rsaPrivateKeyModulesHexString = HexUtil.getHexStringFromByteArray(modulusOfRSAPrivateCrtKeySpec.toByteArray());
	}

	@Override
	public byte[] getDupPublicKeyBytes() {
		byte[] publickKeyBytes = rsaKeypair.getPublic().getEncoded();
		return Arrays.copyOf(publickKeyBytes, publickKeyBytes.length);
	}

	@Override
	public byte[] decrypt(byte[] encryptedBytes) throws SymmetricException {
		if (null == encryptedBytes) {
			throw new IllegalArgumentException("the parameter encryptedBytes is null");
		}
		final byte[] decryptedBytes;		
		final Cipher rsaDecryptModeCipher;
		
		try {
			rsaDecryptModeCipher = Cipher.getInstance(CommonStaticFinalVars.RSA_TRANSFORMATION);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder()
					.append("NoSuchAlgorithmException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = 
					new StringBuilder()
					.append("NoSuchPaddingException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		try {
			rsaDecryptModeCipher.init(Cipher.DECRYPT_MODE, rsaKeypair.getPrivate());
		} catch (InvalidKeyException e) {
			String errorMessage = new StringBuilder()
					.append("InvalidKeyException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		
		try {
			decryptedBytes = rsaDecryptModeCipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = new StringBuilder()
					.append("IllegalBlockSizeException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = 
					new StringBuilder()
					.append("BadPaddingException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		// log.info("공개키로 암호화한 이진 데이터를 16진수로 표현한 문자열[%s]",
		// HexUtil.byteArrayAllToHex(encryptedBytesWithPublicKey));
		// log.info("비밀키로 복호화한 이진 데이터를 16진수로 표현한 문자열[%s]",
		// HexUtil.byteArrayAllToHex(decryptedBytesUsingPrivateKey));
		return decryptedBytes;
	}

	@Override
	public String getModulusHexStrForWeb() {
		return rsaPrivateKeyModulesHexString;
	}
}
