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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;

/**
 * RSA 키 쌍 유티 추상화 클래스, 키쌍을 만드는 정적 메소드와 키쌍을 저장하는 정적 메소드 제공을 목적으로 한다.
 * 
 * @author Won Jonghoon
 *
 */
public abstract class ServerRSAKeypairUtil {


	/**
	 * @param rsaKeySize 원하는 RSA 키 크기
	 * @return 원하는 RSA 키 크기를 갖는 RSA 키 쌍
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public static KeyPair createRSAKeyPairFromKeyGenerator(int rsaKeySize) throws SymmetricException {
		if (rsaKeySize <= 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter rsaKeySize[")
					.append(rsaKeySize)
					.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		KeyPairGenerator rsaKeyPairGenerator = null;
		try {
			rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder().append("fail to get the RSA KeyPairGenerator, errmsg=")
					.append(e.getMessage()).toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}

		final KeyPair rsaKeypair;
		
		try {
			rsaKeyPairGenerator.initialize(rsaKeySize);
			
			rsaKeypair = rsaKeyPairGenerator.generateKeyPair();
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("fail to create a RSA key pair using key size[")
					.append(rsaKeySize)
					.append("], errmsg=").append(e.getMessage()).toString();
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.SEVERE, errorMessage, e);
			
			throw new SymmetricException(errorMessage);
		}
		

		return rsaKeypair;
	}

	/**
	 * @param rsaPrivateKeyFile RSA 개인키가 저장된 파일
	 * @param rsaPublicKeyFile RAS 공개키가 저장된 파일
	 * @return 파라미터 'rsaPrivateKeyFile'(=개인키가 저장된 파일) 와 파라미터 'rsaPublicKeyFile'(=RAS 공개키가 저장된 파일) 로 부터 생성된 공개키 쌍 
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public static KeyPair createRSAKeyPairFromFile(File rsaPrivateKeyFile, File rsaPublicKeyFile)
			throws SymmetricException {
		if (null == rsaPrivateKeyFile) {
			throw new IllegalArgumentException("the parameter rsaPrivateKeyFile is null");
		}
		
		if (null == rsaPublicKeyFile) {
			throw new IllegalArgumentException("the parameter rsaPublicKeyFile is null");
		}

		PrivateKey privateKey = null;
		PublicKey publicKey = null;
		
		byte privateKeyBytes[] = null;
		try {
			privateKeyBytes = CommonStaticUtil.readFileToByteArray(rsaPrivateKeyFile, 1024 * 1024 * 10);
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("the RSA private key File[")
					.append(rsaPrivateKeyFile.getAbsolutePath()).append("] IOException, errmsg=").append(e.getMessage())
					.toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			privateKey = rsaKeyFactory.generatePrivate(privateKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = new StringBuilder().append("fail to get the RSA private key(=PKCS8EncodedKeySpec)[")
					.append(HexUtil.getHexStringFromByteArray(privateKeyBytes)).append("]::errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}

		byte publicKeyBytes[] = null;
		try {
			publicKeyBytes = CommonStaticUtil.readFileToByteArray(rsaPublicKeyFile, 10 * 1024 * 1024);
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("the RSA public key file[")
					.append(rsaPublicKeyFile.getAbsolutePath()).append("] IOException, errmsg=").append(e.getMessage())
					.toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

		try {
			publicKey = rsaKeyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = new StringBuilder().append("fail to get the RSA public key(=X509EncodedKeySpec)[")
					.append(HexUtil.getHexStringFromByteArray(publicKeyBytes)).append("], errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}

		return new KeyPair(publicKey, privateKey);
	}

	/**
	 * 파라미터 'rsaKeypair'(=RSA 공개키 쌍) 을 파라미터 'rsaPrivateKeyFile'(=RSA 개인키 파일) 에는 RSA 개인키를 저장하고 
	 * 파라미터 'rsaPublicKeyFile'(=RSA 공개키 파일) 에는 RSA 공개키를 저장한다.
	 * 
	 * @param rsaKeypair RSA 공개키 쌍
	 * @param rsaPrivateKeyFile RSA 개인키 파일
	 * @param rsaPublicKeyFile RSA 공개키 파일
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public static void saveRSAKeyPairFile(KeyPair rsaKeypair, File rsaPrivateKeyFile, File rsaPublicKeyFile)
			throws SymmetricException {
		if (null == rsaKeypair) {
			throw new IllegalArgumentException("the parameter rsaKeypair is null");
		}
		
		if (null == rsaPrivateKeyFile) {
			throw new IllegalArgumentException("the parameter rsaPrivateKeyFile is null");
		}
		
		if (null == rsaPublicKeyFile) {
			throw new IllegalArgumentException("the parameter rsaPublicKeyFile is null");
		}

		
		final RSAPrivateKey rsaPrivateKey;
		final RSAPublicKey rsaPublicKey;

		try {
			rsaPublicKey = (RSAPublicKey) rsaKeypair.getPublic();
			rsaPrivateKey = (RSAPrivateKey) rsaKeypair.getPrivate();
		} catch (Exception e) {
			String errorMessage = "the parameter 'rsaKeypair' is not a RSA key pair";

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);

			throw new SymmetricException(errorMessage);
		}

		FileOutputStream publicKeyFOS = null;
		try {
			publicKeyFOS = new FileOutputStream(rsaPublicKeyFile);

			publicKeyFOS.write(rsaPublicKey.getEncoded());
			publicKeyFOS.flush();
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder().append("fail to create the parameter rsaPublicKeyFile[")
					.append(rsaPublicKeyFile.getAbsolutePath()).append("]").toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);

			throw new SymmetricException(errorMessage);

		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("fail to create the parameter rsaPublicKeyFile[")
					.append(rsaPublicKeyFile.getAbsolutePath()).append("]").toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);

			throw new SymmetricException(errorMessage);
		} finally {
			try {
				if (publicKeyFOS != null)
					publicKeyFOS.close();
			} catch (Exception e1) {
			}
		}

		FileOutputStream privateKeyFOS = null;
		try {
			privateKeyFOS = new FileOutputStream(rsaPrivateKeyFile);

			privateKeyFOS.write(rsaPrivateKey.getEncoded());
			privateKeyFOS.flush();
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder().append("fail to create the parameter rsaPrivateKeyFile[")
					.append(rsaPrivateKeyFile.getAbsolutePath()).append("]").toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);

			throw new SymmetricException(errorMessage);
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("fail to create the parameter rsaPrivateKeyFile[")
					.append(rsaPrivateKeyFile.getAbsolutePath()).append("]").toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);

			throw new SymmetricException(errorMessage);
		} finally {
			try {
				if (privateKeyFOS != null)
					privateKeyFOS.close();
			} catch (Exception e1) {
			}
		}
	}

}
