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

package kr.pe.codda.client.connection.sync;

import java.io.IOException;
import java.util.logging.Logger;

import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMiddleObjectForwarderIF;

/**
 * 동기 출력 메시지 수신자
 * @author Won Jonghoon
 *
 */
public class SyncOutputMessageReceiver implements ReceivedMiddleObjectForwarderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	// private ReadableMiddleObjectWrapper readableMiddleObjectWrapper = null;
	private int mailboxID;
	private int mailID;
	private String messageID;
	private Object readableMiddleObject = null;
	
	private MessageProtocolIF messageProtocol = null;
	private MessageCodecMangerIF messageCodecManger = null;
	private boolean isError = false;
	private String errorMessage = null;

	public SyncOutputMessageReceiver(MessageProtocolIF messageProtocol) {
		this.messageProtocol = messageProtocol;
	}

	@Override
	public void putReceivedMiddleObject(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {
		if (null != this.readableMiddleObject) {
			/** discard non-first message */
			isError = true;
			
			try {
				AbstractMessage discardedMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger,
						messageProtocol, mailboxID, mailID, messageID, readableMiddleObject);
				
				errorMessage = new StringBuilder().append("discard the received message[")
						.append(discardedMessage.toString())
						.append("] becase there are one more recevied messages").toString();
			} catch (Exception e) {
				
				errorMessage = new StringBuilder().append("discard the received message[mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
						.append("readableMiddleObject=")
						.append(readableMiddleObject.toString())
						.append("] becase there are one more recevied messages").toString();
			}
			
			log.warning(errorMessage);

			return;
		}
		
		if ((CommonStaticFinalVars.ASYN_MAILBOX_ID == mailboxID)) {
			/** discard asynchronous message */
			isError = true;
			
			try {
				AbstractMessage discardedMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger,
						messageProtocol, mailboxID, mailID, messageID, readableMiddleObject);
				
				errorMessage = new StringBuilder().append("discard the received message[")
						.append(discardedMessage.toString())
						.append("] becase the var mailboxID[")
						.append(mailboxID)
						.append("] is not a sync mailbox id").toString();
			} catch (Exception e) {
				errorMessage = new StringBuilder().append("discard the received message[mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
						.append("readableMiddleObject=")
						.append(readableMiddleObject.toString())
						.append("] becase there are one more recevied messages").toString();
			}			
			
			log.warning(errorMessage);

			return;
		}

		
		this.mailboxID = mailboxID;
		this.mailID = mailID;
		this.messageID = messageID;
		this.readableMiddleObject = readableMiddleObject;
	}

	/**
	 * 출력 메시지를 받을 준비 상태로 만든다.
	 * @param messageCodecManger 메시지 코덱 관리자
	 */
	public void ready(MessageCodecMangerIF messageCodecManger) {
		this.messageCodecManger = messageCodecManger;
		readableMiddleObject = null;
		isError = false;
	}

	/**
	 * @return 수신한 출력 메시지
	 * @throws IOException 서버로 부터 전달된 입출력 에러
	 * @throws BodyFormatException  서버로 부터 전달된 바디 포맷 에러 
	 * @throws ServerTaskException  서버로 부터 전달된 서버 비지니스 로직 에러 
	 * @throws NoMoreWrapBufferException 서버로 부터 전잘된 랩버퍼 부족 에러
	 * @throws DynamicClassCallException  서버로 부터 전다된 동적 호출 클래스 에러
	 */
	public AbstractMessage getReceiveMessage() throws DynamicClassCallException, NoMoreWrapBufferException, ServerTaskException, BodyFormatException, IOException {
		
		AbstractMessage receivedMessage = ClientMessageUtility.buildOutputMessage("recevied", messageCodecManger, messageProtocol, mailboxID,
				mailID, messageID, readableMiddleObject);
		
		return receivedMessage;
	}

	/**
	 * @return 출력 메시지 수신 여부
	 */
	public boolean isReceivedMessage() {
		return (null != readableMiddleObject);
	}

	/**
	 * @return 에러 여부
	 */
	public boolean isError() {
		return isError;
	}
	
	/**
	 * @return 에러 메시지, 단 에러 여부가 참인 경우에만 유효
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
}
