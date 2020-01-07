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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.security.KeyPair;
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
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class ClientSessionKeyTest {
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
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClientSessionKey_theParameterClientRSAIsNull() {
		ClientRSAIF clientRSA = null;
		String symmetricKeyAlgorithm = null;
		int symmetricKeySize = -1;
		int symmetricIVSize = -1;
		boolean isBase64 = false;
		
		try {
			new ClientSessionKey(clientRSA, symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isBase64);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter clientRSA is null";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientSessionKey class becase of unknown error");
		}
	}
	
	@Test
	public void testClientSessionKey_theParameterSymmetricKeyAlgorithmIsNull() {	
		final String symmetricKeyAlgorithm = "AES";
		final int symmetricKeySize = 2048;
		final int symmetricIVSize = 1024;
		
		final int rsaKeySize = 1024;
		KeyPair rsaKeypair = null;
		
		try {
			rsaKeypair = ServerRSAKeypairUtil.createRSAKeyPairFromKeyGenerator(rsaKeySize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}
		
		ServerSessionkey serverSessionkey = null;
		
		try {
			serverSessionkey = new ServerSessionkey(new ServerRSA(rsaKeypair), symmetricKeyAlgorithm,
					symmetricKeySize, symmetricIVSize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}		
		
		
		ClientRSAIF clientRSA = null;
		try {
			clientRSA = new ClientRSA(serverSessionkey.getDupPublicKeyBytes());		
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientRSAIF class becase of unknown error");
		}
		
		boolean isBase64 = false;
		
		try {
			new ClientSessionKey(clientRSA, null, symmetricKeySize, symmetricIVSize, isBase64);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter symmetricKeyAlgorithm is null";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientSessionKey class becase of unknown error");
		}
	}
	
	@Test
	public void testClientSessionKey_theParameterSymmetricKeySizeIsLessThanOrEqualToZero() {	
		final String symmetricKeyAlgorithm = "AES";
		final int symmetricKeySize = 2048;
		final int symmetricIVSize = 1024;
		
		final int rsaKeySize = 1024;
		KeyPair rsaKeypair = null;
		
		try {
			rsaKeypair = ServerRSAKeypairUtil.createRSAKeyPairFromKeyGenerator(rsaKeySize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}
		
		ServerSessionkey serverSessionkey = null;
		
		try {
			serverSessionkey = new ServerSessionkey(new ServerRSA(rsaKeypair), symmetricKeyAlgorithm,
					symmetricKeySize, symmetricIVSize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}		
		
		
		ClientRSAIF clientRSA = null;
		try {
			clientRSA = new ClientRSA(serverSessionkey.getDupPublicKeyBytes());		
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientRSAIF class becase of unknown error");
		}
		
		boolean isBase64 = false;
		
		try {
			new ClientSessionKey(clientRSA, symmetricKeyAlgorithm, 0, symmetricIVSize, isBase64);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter symmetricKeySize is less than or equal to zero";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientSessionKey class becase of unknown error");
		}
	}
	
	@Test
	public void testClientSessionKey_theParameterSymmetricIVSizeIsLessThanOrEqualToZero() {	
		final String symmetricKeyAlgorithm = "AES";
		final int symmetricKeySize = 24;
		final int symmetricIVSize = 16;
		
		final int rsaKeySize = 1024;
		KeyPair rsaKeypair = null;
		
		try {
			rsaKeypair = ServerRSAKeypairUtil.createRSAKeyPairFromKeyGenerator(rsaKeySize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}
		
		ServerSessionkey serverSessionkey = null;
		
		try {
			serverSessionkey = new ServerSessionkey(new ServerRSA(rsaKeypair), symmetricKeyAlgorithm,
					symmetricKeySize, symmetricIVSize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}		
		
		
		ClientRSAIF clientRSA = null;
		try {
			clientRSA = new ClientRSA(serverSessionkey.getDupPublicKeyBytes());		
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientRSAIF class becase of unknown error");
		}
		
		boolean isBase64 = false;
		
		try {
			new ClientSessionKey(clientRSA, symmetricKeyAlgorithm, symmetricKeySize, 0, isBase64);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter symmetricIVSize is less than or equal to zero";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientSessionKey class becase of unknown error");
		}
	}
	
	@Test
	public void testClientSessionKey_theParameterIsBase64IsTrue_OK() {	
		final String symmetricKeyAlgorithm = "AES";
		final int symmetricKeySize = 16;
		final int symmetricIVSize = 16;
		final boolean isBase64 = true;
		
		final int rsaKeySize = 2048;
		KeyPair rsaKeypair = null;
		
		try {
			rsaKeypair = ServerRSAKeypairUtil.createRSAKeyPairFromKeyGenerator(rsaKeySize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}
		
		ServerSessionkey serverSessionkey = null;
		
		try {
			serverSessionkey = new ServerSessionkey(new ServerRSA(rsaKeypair), symmetricKeyAlgorithm,
					symmetricKeySize, symmetricIVSize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}		
		
		
		ClientRSAIF clientRSA = null;
		try {
			clientRSA = new ClientRSA(serverSessionkey.getDupPublicKeyBytes());		
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientRSAIF class becase of unknown error");
		}
		
		
		ClientSessionKey clientSessionKey = null;
		
		try {
			clientSessionKey = new ClientSessionKey(clientRSA, symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isBase64);			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientSessionKey class becase of unknown error");
		}
		
		Random random = new Random();
		byte[] plainTextBytes = new byte[1024];
		random.nextBytes(plainTextBytes);
		
		ClientSymmetricKeyIF clientSymmetricKey = null;
		try {
			clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
						
			byte[] encryptedBytes = clientSymmetricKey.encrypt(plainTextBytes);			
			byte[] decryptedBytes = clientSymmetricKey.decrypt(encryptedBytes);
			
			assertArrayEquals(plainTextBytes, decryptedBytes);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}		
		
		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			serverSymmetricKey = serverSessionkey.createNewInstanceOfServerSymmetricKey(isBase64, clientSessionKey.getDupSessionKeyBytes(), clientSessionKey.getDupIVBytes());
			
			byte[] encryptedBytes = clientSymmetricKey.encrypt(plainTextBytes);
			
			byte[] decryptedBytes = serverSymmetricKey.decrypt(encryptedBytes);
			
			assertArrayEquals(plainTextBytes, decryptedBytes);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}		
	}
	
	@Test
	public void testClientSessionKey_theParameterIsBase64IsFalse_OK() {	
		final String symmetricKeyAlgorithm = "AES";
		final int symmetricKeySize = 16;
		final int symmetricIVSize = 16;
		final boolean isBase64 = false;
		
		final int rsaKeySize = 2048;
		KeyPair rsaKeypair = null;
		
		try {
			rsaKeypair = ServerRSAKeypairUtil.createRSAKeyPairFromKeyGenerator(rsaKeySize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}
		
		ServerSessionkey serverSessionkey = null;
		
		try {
			serverSessionkey = new ServerSessionkey(new ServerRSA(rsaKeypair), symmetricKeyAlgorithm,
					symmetricKeySize, symmetricIVSize);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to get a instance of RSA KeyPair becase of unknown error");
		}		
		
		
		ClientRSAIF clientRSA = null;
		try {
			clientRSA = new ClientRSA(serverSessionkey.getDupPublicKeyBytes());		
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientRSAIF class becase of unknown error");
		}
		
		
		ClientSessionKey clientSessionKey = null;
		
		try {
			clientSessionKey = new ClientSessionKey(clientRSA, symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isBase64);			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("fail to create a instance of ClientSessionKey class becase of unknown error");
		}
		
		Random random = new Random();
		byte[] plainTextBytes = new byte[1024];
		random.nextBytes(plainTextBytes);
		
		ClientSymmetricKeyIF clientSymmetricKey = null;
		try {
			clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
						
			byte[] encryptedBytes = clientSymmetricKey.encrypt(plainTextBytes);			
			byte[] decryptedBytes = clientSymmetricKey.decrypt(encryptedBytes);
			
			assertArrayEquals(plainTextBytes, decryptedBytes);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}		
		
		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			serverSymmetricKey = serverSessionkey.createNewInstanceOfServerSymmetricKey(isBase64, clientSessionKey.getDupSessionKeyBytes(), clientSessionKey.getDupIVBytes());
			
			byte[] encryptedBytes = clientSymmetricKey.encrypt(plainTextBytes);
			
			byte[] decryptedBytes = serverSymmetricKey.decrypt(encryptedBytes);
			
			assertArrayEquals(plainTextBytes, decryptedBytes);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}		
	}
}
