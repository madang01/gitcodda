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

package kr.pe.codda.server.task;

import java.nio.channels.SelectionKey;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.classloader.MessageEncoderManagerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.LoginUserNotFoundException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.impl.message.ExceptionDeliveryRes.ExceptionDeliveryRes;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.ProjectLoginManagerIF;

/**
 * 클라이언트로 보낼 메시지 배달부. 서버 비지니스 로직 호출할때 마다 할당 된다. 
 * @author Won Jonghoon
 *
 */
public class ToLetterCarrier {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private AcceptedConnection fromAcceptedConnection = null;
	private AbstractMessage inputMessage;
	private ProjectLoginManagerIF projectLoginManager = null;
	private MessageProtocolIF messageProtocol = null;
	private MessageEncoderManagerIF messageCodecManager = null;
	
	private boolean isAddedSyncOuputMessage = false;
	
	/**
	 * 생서자
	 * @param fromAcceptedConnection 입력 메시지를 보낸 연결 객체
	 * @param inputMessage 입력 메시지
	 * @param projectLoginManager 프로젝트별 로그인 관리자
	 * @param messageProtocol  메시지 프로토콜
	 * @param messageCodecManager  메시지 코덱 관리자
	 */
	public ToLetterCarrier(AcceptedConnection fromAcceptedConnection,
			AbstractMessage inputMessage,
			ProjectLoginManagerIF projectLoginManager,
			MessageProtocolIF messageProtocol,
			MessageEncoderManagerIF messageCodecManager) {
		this.fromAcceptedConnection = fromAcceptedConnection;
		this.inputMessage = inputMessage;
		this.projectLoginManager = projectLoginManager;
		this.messageProtocol = messageProtocol;
		this.messageCodecManager = messageCodecManager;
	}

	private static ExceptionDeliveryRes buildSelfExn(int mailboxIDOfSelfExn, 
			int mailIDOfSelfExn, 
			String errorMessageID,
			ExceptionDelivery.ErrorType errorType, 
			String errorReason) {
		
		ExceptionDeliveryRes selfExnRes = new ExceptionDeliveryRes();
		selfExnRes.setMailboxID(mailboxIDOfSelfExn);
		selfExnRes.setMailID(mailIDOfSelfExn);
		selfExnRes.setErrorPlace(ExceptionDelivery.ErrorPlace.SERVER);
		selfExnRes.setErrorType(errorType);
	
		selfExnRes.setErrorMessageID(errorMessageID);
		selfExnRes.setErrorReason(errorReason);
		
		return selfExnRes;
	}	

	private void doAddOutputMessage(AcceptedConnection toAcceptedConnection,
			AbstractMessage outputMessage, 
			MessageProtocolIF messageProtocol) throws InterruptedException {
		String messageIDOfOutputMessage = outputMessage.getMessageID();
	
				
		
		AbstractMessageEncoder messageEncoder = null;
		try {
			messageEncoder = messageCodecManager.getMessageEncoder(messageIDOfOutputMessage);
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get a output message encoder::").append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage);
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = e.getMessage();
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a output message encoder::").append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);			
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
	
	
		/*log.info("classLoader[{}], serverTask[{}], create new messageEncoder of messageIDToClient={}",
				classLoaderOfSererTask.hashCode(), inputMessageID, messageIDToClient);*/
		StreamBuffer outputMesssageStreamBuffer = messageProtocol.createNewMessageStreamBuffer();
		
		try {
			messageProtocol.M2S(outputMessage, messageEncoder, outputMesssageStreamBuffer);
			
			outputMesssageStreamBuffer.flip();
			
			toAcceptedConnection.addOutputMessage(outputMesssageStreamBuffer);
		} catch (NoMoreWrapBufferException e) {
			outputMesssageStreamBuffer.releaseAllWrapBuffers();
			
			String errorMessage = new StringBuilder("fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(NoMoreWrapBufferException.class);
			String errorReason = errorMessage;
			
			log.log(Level.WARNING, errorReason);
			
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (BodyFormatException e) {
			outputMesssageStreamBuffer.releaseAllWrapBuffers();
			
			String errorMessage = new StringBuilder("fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			log.log(Level.WARNING, errorReason);
			
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;			
		} catch (Exception | Error e) {
			outputMesssageStreamBuffer.releaseAllWrapBuffers();
			
			String errorMessage = new StringBuilder("unknown error::fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			
			log.log(Level.WARNING, errorReason, e);
			
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
		
		
		
	}

	private void doAddOutputErrorMessage(AcceptedConnection toAcceptedConnection,
			ExceptionDelivery.ErrorType errorType, 
			String errorReason,
			AbstractMessage outputMessage,			
			MessageProtocolIF messageProtocol) throws InterruptedException {
		ExceptionDeliveryRes selfExnRes = buildSelfExn( 
				outputMessage.getMailboxID(),
				outputMessage.getMailID(),
				outputMessage.getMessageID(),
				errorType,
				errorReason);
		
		StreamBuffer selfExnResStreamBuffer = messageProtocol.createNewMessageStreamBuffer();
		try {
			messageProtocol.M2S(selfExnRes, CommonStaticFinalVars.EXCEPTIONDELIVERY_ENCODER, selfExnResStreamBuffer);
			
			selfExnResStreamBuffer.flip();
			
			toAcceptedConnection.addOutputMessage(selfExnResStreamBuffer);
		} catch (Exception e) {
			selfExnResStreamBuffer.releaseAllWrapBuffers();
			
			String errorMessage = new StringBuilder()
					.append("fail to build a output stream of the output message ThrowExceptionRes[")
					.append(selfExnRes.toString())
					.append("] to send to to-client[")
					.append(toAcceptedConnection.toSimpleInfomation())
					.append("] because of unknown error, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			return;
		}
	}	
	
	public void addBypassOutputMessage(AbstractMessage bypassOutputMessage) throws InterruptedException {		
		if (inputMessage.getMailboxID() == CommonStaticFinalVars.CLIENT_ASYN_MAILBOX_ID) {
			addAsynOutputMessage(bypassOutputMessage);
		} else {
			addSyncOutputMessage(bypassOutputMessage);
		}
	}

	public void addSyncOutputMessage(AbstractMessage syncOutputMessage) throws InterruptedException {
		if (null == syncOutputMessage) {
			throw new IllegalArgumentException("the parameter syncOutputMessage is null");
		}
		if (CommonStaticFinalVars.CLIENT_ASYN_MAILBOX_ID == inputMessage.getMailboxID()) {			
			throw new IllegalArgumentException("the synchronous output message can't be added becase the inputMessage is a asynchronous message");
		}
		
		if (isAddedSyncOuputMessage) {
			throw new IllegalArgumentException("the synchronous output message can't be added becase another synchronous message is already registered in the toLetter list");	
		}
		
		isAddedSyncOuputMessage = true;
		
		syncOutputMessage.setMailboxID(inputMessage.getMailboxID());
		syncOutputMessage.setMailID(inputMessage.getMailID());
		
		doAddOutputMessage(fromAcceptedConnection, syncOutputMessage, messageProtocol);
	}
	
	public void addAsynOutputMessage(AbstractMessage asynOutputMessage) throws InterruptedException {
		if (null == asynOutputMessage) {
			throw new IllegalArgumentException("the parameter asynOutputMessage is null");
		}
		
		asynOutputMessage.setMailboxID(CommonStaticFinalVars.CLIENT_ASYN_MAILBOX_ID);
		asynOutputMessage.setMailID(fromAcceptedConnection.getServerMailID());	
		
		doAddOutputMessage(fromAcceptedConnection, asynOutputMessage, messageProtocol);
	}
	
	public void addAsynOutputMessage(String toLoginID, AbstractMessage asynOutputMessage) throws InterruptedException, LoginUserNotFoundException {
		if (null == toLoginID) {
			throw new IllegalArgumentException("the parameter toLoginID is null");
		}
		
		if (null == asynOutputMessage) {
			throw new IllegalArgumentException("the parameter asynOutputMessage is null");
		}
		
		SelectionKey loginIDSelectionKey = projectLoginManager.getSelectionKey(toLoginID);
		
		if (null == loginIDSelectionKey) {
			String errorMessage = new StringBuilder()
					.append("the user who has the parameter loginUserID[")
					.append(toLoginID)
					.append("] is not a member or doens't login").toString();
			throw new LoginUserNotFoundException(errorMessage);
		}
		
		Object attachedObject = loginIDSelectionKey.attachment();	
		
		if (null == attachedObject) {
			String errorMessage = new StringBuilder()
					.append("the user who has the parameter loginUserID[")
					.append(toLoginID)
					.append("] was disconnected").toString();			
			throw new LoginUserNotFoundException(errorMessage);
		}
		
		AcceptedConnection loignUserAcceptedConnection = (AcceptedConnection)attachedObject;
		
		asynOutputMessage.setMailboxID(CommonStaticFinalVars.SERVER_ASYN_MAILBOX_ID);
		asynOutputMessage.setMailID(loignUserAcceptedConnection.getServerMailID());		
		
		doAddOutputMessage(loignUserAcceptedConnection, asynOutputMessage, messageProtocol);
	}
	
	public static void putInputErrorMessageToOutputMessageQueue(ExceptionDelivery.ErrorType errorType,
			String errorReason,			
			int mailboxID, int mailID, String messageID,
			AcceptedConnection fromAcceptedConnection,
			MessageProtocolIF messageProtocol) throws InterruptedException {
		
		if (null == errorType) {
			throw new IllegalArgumentException("the parameter errorType is null");
		}		
		
		if (null == errorReason) {
			throw new IllegalArgumentException("the parameter errorReason is null");
		}
		
		
		if (null == fromAcceptedConnection) {
			throw new IllegalArgumentException("the parameter fromSocketResource is null");
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		
		ExceptionDeliveryRes selfExnRes = buildSelfExn(mailboxID, 
				mailID, 
				messageID, 
				errorType,
				errorReason);

		StreamBuffer selfExnResStreamBuffer = messageProtocol.createNewMessageStreamBuffer();
		try {
			messageProtocol.M2S(selfExnRes, CommonStaticFinalVars.EXCEPTIONDELIVERY_ENCODER, selfExnResStreamBuffer);
			
			selfExnResStreamBuffer.flip();
		
			fromAcceptedConnection.addOutputMessage(selfExnResStreamBuffer);
		} catch (Exception e) {
			selfExnResStreamBuffer.releaseAllWrapBuffers();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			String errorMessage = new StringBuilder()
					.append("fail to build a output stream of the output message ThrowExceptionRes[")
					.append(selfExnRes.toString())
					.append("] to send to from-client[")
					.append(fromAcceptedConnection.toSimpleInfomation())
					.append("] because of unknown error, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			return;
		}
	}	
}
