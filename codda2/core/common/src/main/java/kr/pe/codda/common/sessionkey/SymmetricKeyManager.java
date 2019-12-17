/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.pe.codda.common.sessionkey;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.HexUtil;

/**
 * 대칭키 관리자. 신놀이가 지원하는 대칭키에 대한 관리를 위한 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public final class SymmetricKeyManager {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private Map<String, String> symmetricKeyTransformationHash = null;

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SymmetricKeyManagerHolder {
		static final SymmetricKeyManager singleton = new SymmetricKeyManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SymmetricKeyManager getInstance() {
		return SymmetricKeyManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private SymmetricKeyManager() {
		/**
		 * 자바스크립트 공개키 암호화 모듈 JSBN 와 자바 JCA 공통 지원 알고리즘만으로 구성된다. 웹의 특성상 대칭키 생성은 매
		 * 페이지 요청시 마다 하는것이 좋겠지만, hisotry back 기능이 제대로 동작 못하게 되는 문제가 있다. 따라서 이
		 * 문제를 해결하고자 CBC 로 고정한다. 각각의 페이지는 고유 iv를 갖게해서 history back에서도 안전하게 화면을
		 * 볼수있도록한다.
		 * 
		 * 자바 JCA 참고 url :
		 * http://docs.oracle.com/javase/6/docs/technotes/guides/
		 * security/StandardNames.html#alg 인용 : KeyGenerator Algorithms
		 * 
		 * The following algorithm names can be specified when requesting an
		 * instance of KeyGenerator.
		 * 
		 * Alg. Name Description AES Key generator for use with the AES
		 * algorithm. ARCFOUR Key generator for use with the ARCFOUR (RC4)
		 * algorithm. Blowfish Key generator for use with the Blowfish
		 * algorithm. DES Key generator for use with the DES algorithm. DESede
		 * Key generator for use with the DESede (triple-DES) algorithm. HmacMD5
		 * Key generator for use with the HmacMD5 algorithm. HmacSHA1 HmacSHA256
		 * HmacSHA384 HmacSHA512 Keys generator for use with the various flavors
		 * of the HmacSHA algorithms. RC2 Key generator for use with the RC2
		 * algorithm.
		 */

		symmetricKeyTransformationHash = new HashMap<String, String>();
		symmetricKeyTransformationHash.put("AES", "AES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DES", "DES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DESede", "DESede/CBC/PKCS5Padding");
	}	

	public byte[] decrypt(String symmetricKeyAlgorithm, byte[] symmetricKeyBytes, byte[] encryptedBytes, byte[] ivBytes)
			throws IllegalArgumentException, SymmetricException {

		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}

		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("the parameter symmetricKeyBytes is null");
		}

		if (null == encryptedBytes) {
			throw new IllegalArgumentException("the parameter encryptedBytes is null");
		}

		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
		}

		String transformation = symmetricKeyTransformationHash.get(symmetricKeyAlgorithm);

		if (null == transformation) {
			String errorMessage = new StringBuilder()
					.append("the parameter symmetricKeyAlgorithm[")
					.append(symmetricKeyAlgorithm)
					.append("] is not a element of symmetric key transfermation hash set")
					.append(symmetricKeyTransformationHash.keySet().toString()).toString();
			throw new IllegalArgumentException(errorMessage);
		}

		Cipher symmetricKeyCipher = null;
		try {
			symmetricKeyCipher = Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder()
					.append("NoSuchAlgorithmException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = new StringBuilder()
					.append("NoSuchPaddingException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		SecretKeySpec symmetricKey = new SecretKeySpec(symmetricKeyBytes, symmetricKeyAlgorithm);

		IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
		try {
			symmetricKeyCipher.init(Cipher.DECRYPT_MODE, symmetricKey, ivParameterSpec);
		} catch (InvalidKeyException e) {
			String errorMessage = new StringBuilder()
					.append("fail to initialize symmetricKey cipher, ")
					.append("symmetricKeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes))
					.append("], symmetricKeyAlgorithm=[")
					.append(symmetricKeyAlgorithm)
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			
			
			throw new SymmetricException("fail to initialize symmetricKey cipher becase of InvalidKeyException");
		} catch (InvalidAlgorithmParameterException e) {
			String errorMessage = new StringBuilder()
					.append("fail to initialize symmetricKey cipher, ")
					.append("symmetricKeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes))
					.append("], symmetricKeyAlgorithm=[")
					.append(symmetricKeyAlgorithm)
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException("fail to initialize symmetricKey cipher becase of InvalidAlgorithmParameterException");
		}

		// log.info("Cipher.init with IV");

		byte[] decryptedBytes;
		try {
			decryptedBytes = symmetricKeyCipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = new StringBuilder()
					.append("fail to decrypte the encryptedBytes, ")
					.append("symmetricKeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes))
					.append("], symmetricKeyAlgorithm=[")
					.append(symmetricKeyAlgorithm)
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("], encryptedBytes=[")
					.append(HexUtil.getHexStringFromByteArray(encryptedBytes))
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException("fail to decrypte the encryptedBytes becase of IllegalBlockSizeException");
		} catch (BadPaddingException e) {
			String errorMessage = new StringBuilder()
					.append("fail to decrypte the encryptedBytes, ")
					.append("symmetricKeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes))
					.append("], symmetricKeyAlgorithm=[")
					.append(symmetricKeyAlgorithm)
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("], encryptedBytes=[")
					.append(HexUtil.getHexStringFromByteArray(encryptedBytes))
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException("fail to decrypte the encryptedBytes becase of BadPaddingException");
		}

		// log.info("decryptedBytes[%s]",
		// HexUtil.byteArrayAllToHex(decryptedBytes));

		return decryptedBytes;
	}

	public byte[] encrypt(String symmetricKeyAlgorithm, byte[] symmetricKeyBytes, byte[] plainTextBytes, byte[] ivBytes)
			throws IllegalArgumentException, SymmetricException {

		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}

		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("the parameter symmetricKeyBytes is null");
		}

		if (null == plainTextBytes) {
			throw new IllegalArgumentException("the parameter plainTextBytes is null");
		}

		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
		}

		String transformation = symmetricKeyTransformationHash.get(symmetricKeyAlgorithm);

		if (null == transformation) {
			String errorMessage = 
					new StringBuilder()
					.append("the parameter symmetricKeyAlgorithm[")
					.append(symmetricKeyAlgorithm)
					.append("] is not a element of symmetric key transfermation hash set")
					.append(symmetricKeyTransformationHash.keySet().toString()).toString();			
			throw new IllegalArgumentException(errorMessage);
		}

		Cipher symmetricKeyCipher = null;
		try {
			symmetricKeyCipher = Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder()
					.append("NoSuchAlgorithmException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = new StringBuilder()
					.append("NoSuchPaddingException, errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}


		SecretKeySpec symmetricKey = new SecretKeySpec(symmetricKeyBytes, symmetricKeyAlgorithm);

		IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
		try {
			symmetricKeyCipher.init(Cipher.ENCRYPT_MODE, symmetricKey, ivParameterSpec);
		} catch (InvalidKeyException e) {
			String errorMessage =  new StringBuilder()
					.append("fail to initialize symmetricKey cipher, ")
					.append("symmetricKeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes))
					.append("], symmetricKeyAlgorithm=[")
					.append(symmetricKeyAlgorithm)
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException("fail to initialize symmetricKey cipher becase of InvalidKeyException");
		} catch (InvalidAlgorithmParameterException e) {
			String errorMessage = new StringBuilder()
					.append("fail to initialize symmetricKey cipher, ")
					.append("symmetricKeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes))
					.append("], symmetricKeyAlgorithm=[")
					.append(symmetricKeyAlgorithm)
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException("fail to initialize symmetricKey cipher becase of InvalidAlgorithmParameterException");
		}

		byte[] encryptedBytes;
		try {
			encryptedBytes = symmetricKeyCipher.doFinal(plainTextBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = new StringBuilder()
					.append("fail to encrypt the plainTextBytes, ")
					.append("symmetricKeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes))
					.append("], symmetricKeyAlgorithm=[")
					.append(symmetricKeyAlgorithm)
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("], plainTextBytes=[")
					.append(HexUtil.getHexStringFromByteArray(plainTextBytes))
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException("fail to encrypt the plainTextBytes becase of IllegalBlockSizeException");
		} catch (BadPaddingException e) {
			String errorMessage = new StringBuilder()
					.append("fail to encrypt the plainTextBytes, ")
					.append("symmetricKeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes))
					.append("], symmetricKeyAlgorithm=[")
					.append(symmetricKeyAlgorithm)
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("], plainTextBytes=[")
					.append(HexUtil.getHexStringFromByteArray(plainTextBytes))
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException("fail to encrypt the plainTextBytes becase of BadPaddingException");
		}


		return encryptedBytes;
	}

}
