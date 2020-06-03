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

package kr.pe.codda.impl.task.server;


import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.classloader.MessageEncoderManagerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.impl.message.Empty.EmptyServerCodec;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 클래스 풀 이름이 동적 클래스이지만 시스템 클래스 로더로 지정되어 내장된 'Empty' 메시지 서버 타스트 
 * @author Won Jonghoon
 *
 */
public class EmptyServerTask extends AbstractServerTask implements MessageEncoderManagerIF {
	
	
	private EmptyServerCodec emptyServerCodec = new EmptyServerCodec();
	private AbstractMessageDecoder inputMessageDecoder = null;
	private AbstractMessageEncoder outputMessageEncoder = null;
	
	public EmptyServerTask() {
		try {
			inputMessageDecoder = emptyServerCodec.getMessageDecoder();
			outputMessageEncoder = emptyServerCodec.getMessageEncoder();
		} catch (DynamicClassCallException e) {
			System.err.print("fail to create a instance of empty message decoder or encoder");
			e.printStackTrace();
			System.exit(1);
		}
	}
	

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		
		// FIXME!
		// log.info("call EmptyServerTask");
		
		toLetterCarrier.addBypassOutputMessage(inputMessage);
	}
	
	
	public void execute(String projectName,
			AcceptedConnection fromAcceptedConnection,			
			ProjectLoginManagerIF projectLoginManager,						
			int mailboxID, int mailID, String messageID, Object receviedMiddleObject,
			MessageProtocolIF messageProtocol,
			LoginManagerIF fromPersonalLoginManager) throws InterruptedException {
		// long startTime = System.nanoTime();
		
			
		AbstractMessage inputMessage = null;
		try {
			inputMessage = messageProtocol.O2M(inputMessageDecoder, mailboxID, mailID, messageID, receviedMiddleObject);			
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to get a input message from readable middle object[")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("]").toString();
			
			
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = new StringBuilder(errorMessage)
					.append(", errmsg=").append(e.getMessage()).toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorReason);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,					
					mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
			return;		
		} catch(Exception | Error e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a input message from readable middle object[")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("], errmsg=")
					.append(e.getMessage()).toString();			
			
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
			return;
		}
		
		
		ToLetterCarrier toLetterCarrier = new ToLetterCarrier(fromAcceptedConnection,
				inputMessage, 
				projectLoginManager,
				messageProtocol,
				this);				
		
		try {
			doTask(projectName, fromPersonalLoginManager, toLetterCarrier, inputMessage);
		} catch (InterruptedException e) {
			throw e;
		} catch (ServerTaskException e) {
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(ServerTaskException.class);
			
			String errorReason = e.getMessage();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
			return;
		} catch (Exception | Error e) {			
			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(ServerTaskException.class);
			
			
			String errorReason = new StringBuilder()
					.append("unknown error::fail to execuate the input message's task[")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
			return;
		} finally {			
			/*
			long endTime = System.nanoTime();
			String infoMessage = new StringBuilder().append("this server task[")
					.append(messageID)
					.append("] elapsed time[")
					.append(TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS))
					.append(" ms]").toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.info(infoMessage);
			*/
		}
	}


	@Override
	public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException {
		return outputMessageEncoder;
	}

}
