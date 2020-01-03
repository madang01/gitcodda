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

public abstract class ServerRSAKeypairGetter {


	public static KeyPair getRSAKeyPairFromKeyGenerator(int rsaKeySize) throws SymmetricException {
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

	public static KeyPair getRSAKeyPairFromFile(File rsaPrivateKeyFile, File rsaPublicKeyFile)
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

	public static void saveRSAKeyPairFile(int rsaKeySize, File rsaPrivateKeyFile, File rsaPublicKeyFile)
			throws SymmetricException {
		if (rsaKeySize <= 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter rsaKeySize[")
					.append(rsaKeySize)
					.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == rsaPrivateKeyFile) {
			throw new IllegalArgumentException("the parameter rsaPrivateKeyFile is null");
		}
		
		if (null == rsaPublicKeyFile) {
			throw new IllegalArgumentException("the parameter rsaPublicKeyFile is null");
		}

		KeyPairGenerator rsaKeyPairGenerator = null;
		try {
			rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		final RSAPrivateKey rsaPrivateKey;
		final RSAPublicKey rsaPublicKey;

		try {
			rsaKeyPairGenerator.initialize(rsaKeySize);
			KeyPair keyPair = rsaKeyPairGenerator.generateKeyPair();
			rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
			rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

			/*
			System.out.printf("rsaPublicKey[%s]\n", rsaPublicKey.toString());
			System.out.printf("rsaPrivateKey[%s]\n", rsaPrivateKey.toString());

			System.out.printf("******************* RSA staret ********************\n");
			String rsaAlgorithm = rsaPublicKey.getAlgorithm();
			System.out.printf("rsaAlgorithm[%s], rsaKeySize[%d]\n", rsaAlgorithm, rsaKeySize);
			System.out.printf("******************* RSA end ********************\n");

			System.out.printf("******************* Pulbic Key staret ********************\n");
			String rsaPublicKeyFormat = rsaPublicKey.getFormat();
			BigInteger rsaPublicKeyModulus = rsaPublicKey.getModulus();
			BigInteger rsaPublicKeyExponent = rsaPublicKey.getPublicExponent();
			System.out.printf("rsaPublicKeyFormat[%s], rsaPublicKeyExponent(hex)[%s], rsaPublicKeyModulus(hex)[%s]\n",
					rsaPublicKeyFormat, rsaPublicKeyExponent.toString(16), rsaPublicKeyModulus.toString(16));
			System.out.printf("******************* Pulbic Key end ********************\n");

			System.out.printf("******************* Private Key staret ********************\n");
			String rsaPrivateKeyFormat = rsaPrivateKey.getFormat();
			BigInteger rsaPrivateKeyModulus = rsaPrivateKey.getModulus();
			BigInteger rsaPrivateKeyExponent = rsaPrivateKey.getPrivateExponent();
			System.out.printf(
					"rsaPrivateKeyFormat[%s], rsaPrivateKeyExponent(hex)[%s], rsaPrivateKeyModulus(hex)[%s]\n",
					rsaPrivateKeyFormat, rsaPrivateKeyExponent.toString(16), rsaPrivateKeyModulus.toString(16));
			System.out.printf("******************* Private Key end ********************\n");
			*/

		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to create a RAS keypair having keysize[")
					.append(rsaKeySize).append("]").toString();

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
