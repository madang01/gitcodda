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
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;

/**
 * 동기 메시지용 메일 박스
 * @author Won Jonghoon
 *
 */
public final class SyncMessageMailbox {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	private final Object monitor = new Object();
	
	private final ConnectionIF conn;
	private final int mailboxID;
	private final long socketTimeOut;
	private final MessageProtocolIF messageProtocol;
	
	private transient int mailID = Integer.MIN_VALUE;
	private transient String receviedMessageID = null;
	private transient Object receviedReadableMiddleObject = null;

	// private MessageCodecMangerIF messageCodecManger = null;

	/**
	 * 생성자
	 * @param conn 연결
	 * @param mailboxID 메일 박스 식별자
	 * @param socketTimeOut 소켓 타음 아웃 시간
	 * @param messageProtocol 메시지 프로토콜
	 */
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

	/*
	public void setMessageCodecManger(MessageCodecMangerIF messageCodecManger) {
		this.messageCodecManger = messageCodecManger;
	}
	*/
	
	/*
	 * private int getMailboxID() { return mailboxID; }
	 */

	
	/**
	 * @return 메일 박스 식별자
	 */
	public int getMailboxID() {
		return mailboxID;
	}

	/**
	 * @return 메일 식별자
	 */
	public int getMailID() {
		return mailID;
	}
	

	/**
	 * 스트림에서 추출한 중간 객체를 저장하여 수신을 기다리는 측에 알려 받아 가도록 한다.
	 * 
	 * @param fromMailboxID 수신한 메시지의 메일 박스 식별자
	 * @param fromMailID 수신한 메시지의 메일 식별자
	 * @param messageID 메시지 식별자
	 * @param receivedMiddleObject 스트림에서 추출한 중간 객체
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	public void putSyncOutputMessage(int fromMailboxID, int fromMailID, String messageID, Object receivedMiddleObject)
			throws InterruptedException {
		if (null == receivedMiddleObject) {
			throw new IllegalArgumentException("the parameter receivedMiddleObject is null");
		}
		
		if (mailboxID != fromMailboxID) {
			messageProtocol.closeReadableMiddleObject(fromMailboxID, fromMailID, messageID, receivedMiddleObject);			
			
			String warnMessage = new StringBuilder()
					.append("drop the received letter[")
					.append(receivedMiddleObject.toString())
					.append("] because it's mailbox id is different form this mailbox id[")
					.append(mailboxID)
					.append("]").toString();
			log.warning(warnMessage);				
			return;
		}
		
		synchronized (monitor) {
			
			if (mailID != fromMailID) {
				messageProtocol.closeReadableMiddleObject(fromMailboxID, fromMailID, messageID, receivedMiddleObject);
				
				String warnMessage = new StringBuilder()
						.append("drop the received letter[")
						.append(receivedMiddleObject.toString())
						.append("] because it's mail id is different form this mailbox's mail id[")
						.append(mailID)
						.append("]").toString();
				
				log.warning(warnMessage);

				return;
			}			
			
			if (null != this.receviedReadableMiddleObject) {
				/** 서버단에서 메일 식별자가 같은 동기과 메시지를 연속하여 2번 보내지 않도록 안정 장치가 있지만 혹시 동일 메시지 식별자로 동기 메시지가 2번이상 도착했을 경우 이전에 받은 메시지를 버리고 이전 받은 메시를 버리고 마지막으로 받은 메시지를 취한다 */
				messageProtocol.closeReadableMiddleObject(fromMailboxID, fromMailID, messageID, receivedMiddleObject);
				
				String warnMessage = new StringBuilder()
						.append("drop the previous received letter[")
						.append(receivedMiddleObject.toString())
						.append("] because it's mail id is different form this mailbox's mail id[")
						.append(mailID)
						.append("]").toString();
				
				log.severe(warnMessage);

				return;
			}
			
			this.receviedMessageID = messageID;
			this.receviedReadableMiddleObject = receivedMiddleObject;
			monitor.notify();
		}
	}

	/**
	 * @param messageCodecManger 메시지 코덱 관리자 
	 * @return 스트림에서 추출한 중간 객체로 부터 변환된 메시지
	 * @throws IOException 소켓 타임 아웃 포함하여 입출력 에러 발생시 던지는 예외
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws BodyFormatException 
	 * @throws ServerTaskException 
	 * @throws NoMoreWrapBufferException 
	 * @throws DynamicClassCallException 
	 */
	public AbstractMessage getSyncOutputMessage(MessageCodecMangerIF messageCodecManger) throws IOException, InterruptedException, DynamicClassCallException, NoMoreWrapBufferException, ServerTaskException, BodyFormatException {
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
