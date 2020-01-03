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

import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;

public class SessionKeyTestThread extends Thread {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	private int threadID = -1;
	
	private ServerSessionkeyIF mainProjectServerSessionkey = null;
	private ClientSessionKeyIF mainProjectClientSessionKey = null;
	
	
	private boolean isTerminated=false;
	private String errorMessage = null;
	
	public SessionKeyTestThread(int threadID, ServerSessionkeyIF mainProjectServerSessionkey, ClientSessionKeyIF mainProjectClientSessionKey) {
		this.threadID = threadID;
		
		this.mainProjectServerSessionkey = mainProjectServerSessionkey;
		this.mainProjectClientSessionKey = mainProjectClientSessionKey;
	}
	
	
	public void run() {
		Random random = new Random();
		random.setSeed(new Date().getTime());
		isTerminated=false;
		
		// log.info("threadID[{}] start", threadID);
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				// log.info("threadID[{}] running 111", threadID);
				
				String plainTextOfClient = new StringBuilder("hello한글").append(random.nextLong()).toString();
				
				byte [] plainTextBytesOfClient  = plainTextOfClient.getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);				
				
								
				byte sessionKeyBytes[] = mainProjectClientSessionKey.getDupSessionKeyBytes();
				byte ivBytes[] = mainProjectClientSessionKey.getDupIVBytes();				
				ClientSymmetricKeyIF  clientSymmetricKey = mainProjectClientSessionKey.getClientSymmetricKey();
				
				ServerSymmetricKeyIF serverSymmetricKey = mainProjectServerSessionkey
				.createNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);
				
				
				byte encryptedBytesOfClient[] = clientSymmetricKey.encrypt(plainTextBytesOfClient);
				byte decryptedBytesOfServer[] = serverSymmetricKey.decrypt(encryptedBytesOfClient);
				
				String plainTextOfServer = new StringBuilder("hello한글그림").append(random.nextLong()).toString();
				
				byte [] plainTextBytesOfServer  = plainTextOfServer.getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);				
				byte encryptedBytesOfServer[] = serverSymmetricKey.encrypt(plainTextBytesOfServer);
			
				byte decryptedBytesOfClient[] = clientSymmetricKey.decrypt(encryptedBytesOfServer);				
				
				String decryptedTextOfServer = new String(decryptedBytesOfServer, CommonStaticFinalVars.DEFUALT_CHARSET);
				String decryptedTextOfClient = new String(decryptedBytesOfClient, CommonStaticFinalVars.DEFUALT_CHARSET);
				
				if (! decryptedTextOfServer.equals(plainTextOfClient)) {
					errorMessage = new StringBuilder()
							.append("In the SessionKeyTestThread[")
							.append(threadID)
							.append("] the plain text[")
							.append(plainTextOfClient)
							.append("] of client is not same to the decrypted text[")
							.append(decryptedTextOfServer)
							.append("] of server").toString();
					log.warning(errorMessage);
					break;
				}
				
				if (! decryptedTextOfClient.equals(plainTextOfServer)) {
					errorMessage = new StringBuilder()
							.append("In the SessionKeyTestThread[")
							.append(threadID)
							.append("] the plain text[")
							.append(plainTextOfServer)
							.append("] of client is not same to the decrypted text[")
							.append(decryptedTextOfClient)
							.append("] of cleint").toString();
					log.warning(errorMessage);
					break;
				}
				
				// log.info("threadID[{}] running 222", threadID);
				
				Thread.sleep(random.nextInt(5)+5);
				
				// log.info("threadID[{}] running 333", threadID);
			}		
			
			log.info("the SessionKeyTestThread[" + threadID + "] loop exist");
		} catch (InterruptedException e) {
			log.info("the SessionKeyTestThread[" + threadID + "] was interrupted");
		} catch (Exception e) {
			errorMessage = new StringBuilder()
					.append("the SessionKeyTestThread[")
					.append("]'s errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
		} finally {
			isTerminated = true;
		}		
	}
	
	public boolean isTerminated() {
		return isTerminated;
	}
	
	public boolean isError() {
		return (errorMessage != null) ? true : false;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
