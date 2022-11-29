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

public class SymmetricKeyTestThread extends Thread {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private int threadID = -1;
	private SymmetricKeyInfo symmetricKeyInfo = null;
	
	private boolean isTerminated=false;
	private String errorMessage = null;
	
	public SymmetricKeyTestThread(int threadID, SymmetricKeyInfo symmetricKeyInfo) {
		this.threadID = threadID;
		this.symmetricKeyInfo = symmetricKeyInfo;
		
	}
	public void run() {
		Random random = new Random();
		random.setSeed(new Date().getTime());	
		
		SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
		
		try {
			while (!Thread.currentThread().isInterrupted()) {				
				String plainText = new StringBuilder("hello한글").append(random.nextLong()).toString();
				
				byte [] plainTextBytes  = plainText.getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
				
				String symmetricKeyAlgorithm = symmetricKeyInfo.getSymmetricKeyAlgorithm();
				
				byte symmetricKeyBytes[] = new byte[symmetricKeyInfo.getSymmetricKeySize()];
				random.nextBytes(symmetricKeyBytes);
						
				byte ivBytes[] = new byte[symmetricKeyInfo.getIvSize()];					
				random.nextBytes(ivBytes);
				
				
				byte encryptedBytes[] = symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
				byte decryptedBytes[] = symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
				
				String decryptedText = new String(decryptedBytes, CommonStaticFinalVars.DEFUALT_CHARSET);
				
				if (!decryptedText.equals(plainText)) {
					errorMessage = new StringBuilder()
							.append("In the SymmetricKeyTestThread[")
							.append(threadID)
							.append("] the plain text[")
							.append(plainText)
							.append("] is not same to the decrypted text[")
							.append(decryptedText)
							.append("]").toString();
					log.warning(errorMessage);
					break;
				}
				
				Thread.sleep(random.nextInt(5)+5);
			}
			
			log.info("the SymmetricKeyTestThread[" + threadID + "] loop exist");
			
		} catch (InterruptedException e) {
			log.info("the SymmetricKeyTestThread[" + threadID + "] was interrupted");
		} catch (Exception e) {
			errorMessage = new StringBuilder()
					.append("the SymmetricKeyTestThread[")
					.append(threadID)
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
