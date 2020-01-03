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

import java.util.logging.Logger;

import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageForwarderIF;

public class SyncOutputMessageReceiver implements ReceivedMessageForwarderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	// private ReadableMiddleObjectWrapper readableMiddleObjectWrapper = null;
	private MessageProtocolIF messageProtocol = null;
	private MessageCodecMangerIF messageCodecManger = null;
	private AbstractMessage receivedMessage = null;
	private boolean isError = false;
	private String errorMessage = null;

	public SyncOutputMessageReceiver(MessageProtocolIF messageProtocol) {
		this.messageProtocol = messageProtocol;
	}

	@Override
	public void putReceivedMessage(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {
		if (null != receivedMessage) {
			/** discard message */
			isError = true;

			AbstractMessage discardedMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger,
					messageProtocol, mailboxID, mailID, messageID, readableMiddleObject);

			errorMessage = new StringBuilder().append("discard the received message[")
					.append(discardedMessage.toString())
					.append("] becase there are one more recevied messages").toString();
			
			log.warning(errorMessage);

			return;
		}
		
		if ((CommonStaticFinalVars.CLIENT_ASYN_MAILBOX_ID == mailboxID) || (CommonStaticFinalVars.SERVER_ASYN_MAILBOX_ID == mailboxID)) {
			/** discard message */
			isError = true;

			AbstractMessage discardedMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger,
					messageProtocol, mailboxID, mailID, messageID, readableMiddleObject);

			errorMessage = new StringBuilder().append("discard the received message[")
					.append(discardedMessage.toString())
					.append("] becase the var mailboxID[")
					.append(mailboxID)
					.append("] is not a sync mailbox id").toString();
			
			log.warning(errorMessage);

			return;
		}

		receivedMessage = ClientMessageUtility.buildOutputMessage("recevied", messageCodecManger, messageProtocol, mailboxID,
				mailID, messageID, readableMiddleObject);
	}

	public void ready(MessageCodecMangerIF messageCodecManger) {
		this.messageCodecManger = messageCodecManger;
		receivedMessage = null;
		isError = false;
	}

	public AbstractMessage getReceiveMessage() {
		return receivedMessage;
	}

	public boolean isReceivedMessage() {
		return (null != receivedMessage);
	}

	public boolean isError() {
		return isError;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
}
