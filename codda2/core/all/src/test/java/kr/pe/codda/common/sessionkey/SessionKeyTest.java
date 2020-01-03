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

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class SessionKeyTest {

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

	@Ignore
	public void testSessionKeyThreadSafe() {
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();

		ServerSessionkeyIF mainProjectServerSessionkey = null;
		ClientSessionKeyIF mainProjectClientSessionKey = null;
		try {
			mainProjectServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
			mainProjectClientSessionKey = clientSessionKeyManager
					.createNewClientSessionKey(mainProjectServerSessionkey.getDupPublicKeyBytes(), false);
		} catch (Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}

		int threadID = 0;
		SessionKeyTestThread sessionKeyTestThreadList[] = {
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey),
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey),
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey),
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey),
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey) };
		for (SessionKeyTestThread sessionKeyTestThread : sessionKeyTestThreadList) {
			sessionKeyTestThread.start();
		}

		try {
			// Thread.sleep(1000L*60*10);
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}

		for (SessionKeyTestThread sessionKeyTestThread : sessionKeyTestThreadList) {
			sessionKeyTestThread.interrupt();
		}

		while (!isAllTerminated(sessionKeyTestThreadList)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.log(Level.WARNING, e.getMessage(), e);
				fail(e.getMessage());
			}
		}
		for (SessionKeyTestThread sessionKeyTestThread : sessionKeyTestThreadList) {
			if (sessionKeyTestThread.isError()) {
				fail(sessionKeyTestThread.getErrorMessage());
			}
		}
	}

	private boolean isAllTerminated(SessionKeyTestThread sessionKeyTestThreadList[]) {
		for (SessionKeyTestThread sessionKeyTestThread : sessionKeyTestThreadList) {
			if (!sessionKeyTestThread.isTerminated()) {
				return false;
			}
		}
		return true;
	}
}