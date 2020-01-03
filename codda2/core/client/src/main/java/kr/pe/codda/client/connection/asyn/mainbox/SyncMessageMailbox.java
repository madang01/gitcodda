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

package kr.pe.codda.client.connection.asyn.mainbox;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;

public final class SyncMessageMailbox {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	private final Object monitor = new Object();
	
	private ConnectionIF conn;	

	private long socketTimeOut;

	private int mailboxID;
	private transient int mailID = Integer.MIN_VALUE;
	private MessageProtocolIF messageProtocol = null;
	
	
	private String receviedMessageID = null;
	private transient Object receviedReadableMiddleObject = null;

	private MessageCodecMangerIF messageCodecManger = null;

	public SyncMessageMailbox(ConnectionIF conn, int mailboxID, long socketTimeOut, MessageProtocolIF messageProtocol) {
		if (0 == mailboxID) {
			String errorMessage = new StringBuilder()
					.append("the parameter mailboxID[")
					.append(mailboxID)
					.append("] is not private mailbox id, it is a public mail box's id").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (mailboxID < 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter mailboxID[")
					.append(mailboxID)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (mailboxID > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			String errorMessage = new StringBuilder()
					.append("the parameter mailboxID[")
					.append(mailboxID)
					.append("] is greater than unsinged short max[")
					.append(CommonStaticFinalVars.UNSIGNED_SHORT_MAX)
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (socketTimeOut < 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter socketTimeOut[")
					.append(socketTimeOut)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.conn = conn;
		this.mailboxID = mailboxID;
		this.socketTimeOut = socketTimeOut;
		this.messageProtocol = messageProtocol;
	}

	public void setMessageCodecManger(MessageCodecMangerIF messageCodecManger) {
		this.messageCodecManger = messageCodecManger;
	}
	
	/*
	 * private int getMailboxID() { return mailboxID; }
	 */

	

	public int getMailboxID() {
		return mailboxID;
	}

	public int getMailID() {
		return mailID;
	}
	

	public void putSyncOutputMessage(int fromMailboxID, int fromMailID, String messageID, Object receviedMiddleObject)
			throws InterruptedException {
		if (null == receviedMiddleObject) {
			throw new IllegalArgumentException("the parameter receviedMiddleObject is null");
		}
		
		synchronized (monitor) {
			if (mailboxID != fromMailboxID) {
				AbstractMessage outputMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger, messageProtocol, fromMailboxID, fromMailID, messageID, receviedMiddleObject);
				
				String warnMessage = new StringBuilder()
						.append("drop the received letter[")
						.append(outputMessage.toString())
						.append("] because it's mailbox id is different form this mailbox id[")
						.append(mailboxID)
						.append("]").toString();
				log.warning(warnMessage);				
				return;
			}
			
			if (mailID != fromMailID) {
				AbstractMessage outputMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger, messageProtocol, fromMailboxID, fromMailID, messageID, receviedMiddleObject);
				
				String warnMessage = new StringBuilder()
						.append("drop the received letter[")
						.append(outputMessage.toString())
						.append("] because it's mail id is different form this mailbox's mail id[")
						.append(mailID)
						.append("]").toString();
				
				log.warning(warnMessage);

				return;
			}			
			
			if (null != this.receviedReadableMiddleObject) {
				/** 서버단에서 메일 식별자가 같은 동기과 메시지를 연속하여 2번 보내지 않도록 안정 장치가 있지만 혹시 동일 메시지 식별자로 동기 메시지가 2번이상 도착했을 경우 이전에 받은 메시지를 버리고 이전 받은 메시를 버리고 마지막으로 받은 메시지를 취한다 */
				AbstractMessage prevOutputMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger, messageProtocol, fromMailboxID, fromMailID, this.receviedMessageID, this.receviedReadableMiddleObject);
				
				String warnMessage = new StringBuilder()
						.append("drop the previous received letter[")
						.append(prevOutputMessage.toString())
						.append("] because it's mail id is different form this mailbox's mail id[")
						.append(mailID)
						.append("]").toString();
				
				log.severe(warnMessage);

				return;
			}
			
			this.receviedMessageID = messageID;
			this.receviedReadableMiddleObject = receviedMiddleObject;
			monitor.notify();
		}
	}

	public AbstractMessage getSyncOutputMessage() throws IOException, InterruptedException {
		AbstractMessage returnedObject = null;
		synchronized (monitor) {
			if (null == receviedReadableMiddleObject) {
				monitor.wait(socketTimeOut);				
				if (null == receviedReadableMiddleObject) {
					
					if (! conn.isConnected()) {
						String warnMessage = new StringBuilder()
								.append("this connection[")
								.append(conn.hashCode())
								.append("] disconnected so the input message's mail[mailboxID=")
								.append(mailboxID)
								.append(", mailID=")
								.append(mailID)
								.append("] lost and the mail identifier is incremented by one").toString();
						
						log.warning(warnMessage);
						
						
						if (Integer.MAX_VALUE == mailID) {
							mailID = Integer.MIN_VALUE;
						} else {
							mailID++;
						}
						throw new IOException("the connection has been disconnected");
					}
					
					String warnMessage = new StringBuilder()
							.append("this connection[")
							.append(conn.hashCode())
							.append("] timeout occurred so the input message's mail[mailboxID=")
							.append(mailboxID)
							.append(", mailID=")
							.append(mailID)
							.append("] lost and the mail identifier is incremented by one").toString();
					
					log.warning(warnMessage);
					
					
					if (Integer.MAX_VALUE == mailID) {
						mailID = Integer.MIN_VALUE;
					} else {
						mailID++;
					}					
					throw new SocketTimeoutException("socket timeout occurred");
				}				
			}
			returnedObject = ClientMessageUtility.buildOutputMessage("received", messageCodecManger, messageProtocol, mailboxID, mailID, receviedMessageID, receviedReadableMiddleObject);
			
			if (Integer.MAX_VALUE == mailID) {
				mailID = Integer.MIN_VALUE;
			} else {
				mailID++;
			}
			//receviedMessageID = null;
			receviedReadableMiddleObject = null;
		}
		
		return returnedObject;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SyncMailboxForAsynPrivate [connection=");
		builder.append(conn.hashCode());
		builder.append(", mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		builder.append(", socketTimeOut=");
		builder.append(socketTimeOut);
		builder.append("]");
		return builder.toString();
	}
}
