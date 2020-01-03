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

import static org.junit.Assert.*;

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

public class ServerRSATest {
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
	public void testServerRSA_theParameterRSAKeypairIsNull() {
		try {
			new ServerRSA(null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter rsaKeypair is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDecrypt_theParameterEncryptedBytesIsNull() {
		try {
			ServerRSA serverRSA = new ServerRSA(ServerRSAKeypairGetter.getRSAKeyPairFromKeyGenerator(1024));
			
			serverRSA.decrypt(null);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter encryptedBytes is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}

}
