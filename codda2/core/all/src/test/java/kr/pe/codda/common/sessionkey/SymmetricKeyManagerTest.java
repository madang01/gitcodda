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

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class SymmetricKeyManagerTest {	
	
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		JDKLoggerCustomFormatter formatter = new JDKLoggerCustomFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testEncrypt_parameter_symmetricKeyAlgorithm_null() {
		
		try {
			SymmetricKeyManager.getInstance().encrypt(null, null, null, null);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			if (!e.getMessage().equals("the parameter symmetricKeyAlgorithm is null")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEncrypt_parameter_symmetricKeyBytes_null() {
		
		try {
			SymmetricKeyManager.getInstance().encrypt("AES", null, null, null);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			if (!e.getMessage().equals("the parameter symmetricKeyBytes is null")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEncrypt_parameter_plainTextBytes_null() {
		
		String symmetricKeyAlgorithm = "AES";
		byte symmetricKeyBytes[] = new byte[128]; 
		byte [] plainTextBytes  = null;
		byte ivBytes[] = null;
		
		Random random = new Random();
		random.setSeed(new Date().getTime());
		
		random.nextBytes(symmetricKeyBytes);
		
		try {
			SymmetricKeyManager.getInstance().encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			if (!e.getMessage().equals("the parameter plainTextBytes is null")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testEncrypt_parameter_ivBytes_null() {
		String symmetricKeyAlgorithm = "AES";
		byte symmetricKeyBytes[] = new byte[128]; 
		byte [] plainTextBytes  = "hello한글".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		byte ivBytes[] = null;
		
		Random random = new Random();
		
		random.nextBytes(symmetricKeyBytes);
		
		try {
			SymmetricKeyManager.getInstance().encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			if (!e.getMessage().equals("the parameter ivBytes is null")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEncrypt_parameter_symmetricKeyAlgorithm_unknownSymmetricKeyAlgorithm() {
		String symmetricKeyAlgorithm = "AAA";
		byte symmetricKeyBytes[] = new byte[128]; 
		byte [] plainTextBytes  = "hello한글".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		byte ivBytes[] = new byte[24];
		
		Random random = new Random();
		
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			SymmetricKeyManager.getInstance().encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			if (!e.getMessage().equals("the parameter symmetricKeyAlgorithm["+symmetricKeyAlgorithm+"] is not a element of symmetric key transfermation hash set[DES, DESede, AES]")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	/**
Java API : https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
Every implementation of the Java platform is required to support the following standard Cipher transformations with the keysizes in parentheses: 
AES/CBC/NoPadding (128)
AES/CBC/PKCS5Padding (128)
AES/ECB/NoPadding (128)
AES/ECB/PKCS5Padding (128)
DES/CBC/NoPadding (56)
DES/CBC/PKCS5Padding (56)
DES/ECB/NoPadding (56)
DES/ECB/PKCS5Padding (56)
DESede/CBC/NoPadding (168)
DESede/CBC/PKCS5Padding (168)
DESede/ECB/NoPadding (168)
DESede/ECB/PKCS5Padding (168)
RSA/ECB/PKCS1Padding (1024, 2048)
RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
	 */
	@Test
	public void testSymmetricKeyManagerBySymmetricKeyAlgorithm() {
		
		SymmetricKeyInfo[] SymmetricKeyInfoList = { 
				new SymmetricKeyInfo("AES", 16, 16), 
				new SymmetricKeyInfo("DES", 8, 8), 
				new SymmetricKeyInfo("DESede", 24, 8)}; 
		
		String plainText = "hello한글";
		byte [] plainTextBytes  = plainText.getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		
		
		Random random = new Random();
		random.setSeed(new Date().getTime());		
		
		SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
		
		for (SymmetricKeyInfo symmetricKeyInfo : SymmetricKeyInfoList) {
			String symmetricKeyAlgorithm = symmetricKeyInfo.getSymmetricKeyAlgorithm();
			
			byte symmetricKeyBytes[] = new byte[symmetricKeyInfo.getSymmetricKeySize()];
			random.nextBytes(symmetricKeyBytes);
					
			byte ivBytes[] = new byte[symmetricKeyInfo.getIvSize()];					
			random.nextBytes(ivBytes);
					
			try {
				byte encryptedBytes[] = symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
				byte decryptedBytes[] = symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
				
				String decryptedText = new String(decryptedBytes, CommonStaticFinalVars.DEFUALT_CHARSET);
				
				if (!decryptedText.equals(plainText)) {
					String errorMessage = new StringBuilder()
							.append("the plain text[")
							.append(plainText)
							.append("] is not same to the decrypted text[")
							.append(decryptedText)
							.append("]").toString();
					fail(errorMessage);
				}
			} catch (Exception e) {
				log.log(Level.WARNING, symmetricKeyInfo.toString(), e);
				fail(e.getMessage());
			}
		}		
	}
	
	@Test
	public void testSymmetricKeyManagerThreadSafe() {
		SymmetricKeyInfo aesSymmetricKeyInfo = new SymmetricKeyInfo("AES", 16, 16);
		SymmetricKeyInfo desSymmetricKeyInfo = new SymmetricKeyInfo("DES", 8, 8);
		SymmetricKeyInfo desedeSymmetricKeyInfo = new SymmetricKeyInfo("DESede", 24, 8);
		
		int threadID = 0;
		SymmetricKeyTestThread symmetricKeyTestThreadList[]  = {
				new SymmetricKeyTestThread(threadID++, aesSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, aesSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, aesSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, desedeSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, desedeSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, desSymmetricKeyInfo)
		};
		for (SymmetricKeyTestThread symmetricKeyTestThread : symmetricKeyTestThreadList) {
			symmetricKeyTestThread.start();
		}
		
		try {
			Thread.sleep(1000L*60*2);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		
		for (SymmetricKeyTestThread symmetricKeyTestThread : symmetricKeyTestThreadList) {
			symmetricKeyTestThread.interrupt();
		}
		
		while (! isAllTerminated(symmetricKeyTestThreadList)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.log(Level.WARNING, e.getMessage(), e);
				fail(e.getMessage());
			}
		}
		for (SymmetricKeyTestThread symmetricKeyTestThread : symmetricKeyTestThreadList) {
			if (symmetricKeyTestThread.isError()) {
				fail(symmetricKeyTestThread.getErrorMessage());
			}
		}		
	}
	
	private boolean isAllTerminated(SymmetricKeyTestThread symmetricKeyTestThreadList[]) {
		for (SymmetricKeyTestThread symmetricKeyTestThread : symmetricKeyTestThreadList) {
			if (!symmetricKeyTestThread.isTerminated()) {
				return false;
			}
		}
		return true;
	}
}
