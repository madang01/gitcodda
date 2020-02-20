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

import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;
import kr.pe.codda.server.classloader.ServerClassLoader;

/**
 * <pre>
 * 로그인을 요구하지 않는 서버 비지니스 로직 부모 추상화 클래스. 
 * 메시지는 자신만의 서버 비지니스를 갖는다.
 * 서버 비지니스 로직 클래스 이름 형식은 
 * 접두어 '메시지 식별자' 와 접미어 'ServerTask' 로 구성된다. 
 * 개발자는 이 클래스를 상속 받은 메시지별 비지니스 로직을 개발하며, 
 * 이렇게 개발된 비지니스 로직 모듈은 동적으로 로딩된다. 
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractServerTask {
	// protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private ServerClassLoader serverClassLoader = null;
	private AbstractMessageDecoder inputMessageDecoder = null;
	

	/*
	public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException {
		if (null == messageID) {
			throw new IllegalArgumentException("the parameter messageID is null");
		}
		
		MessageCodecIF serverMessageCodec = messageID2ServerMessageCodecHash.get(messageID);
		if (null == serverMessageCodec) {
			String serverMessageCodecClassFullName = IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);
			Object retObject = CommonStaticUtil.getNewObjectFromClassloader(taskClassLoader, serverMessageCodecClassFullName);
			
			if (! (retObject instanceof MessageCodecIF)) {
				String errorMessage = new StringBuilder()
				.append("this instance of ")
				.append(serverMessageCodecClassFullName)
				.append(" class that was created by the classloader[")
				.append(taskClassLoader.hashCode())
				.append("] is not a instance of MessageCodecIF class").toString();

				throw new DynamicClassCallException(errorMessage);
			}
			
			serverMessageCodec = (MessageCodecIF)retObject;		
			
			messageID2ServerMessageCodecHash.put(messageID, serverMessageCodec);
		}
		
		return serverMessageCodec.getMessageEncoder();
	}	
	*/
	
	public ServerClassLoader getServerClassLoader() {
		return serverClassLoader;
	}
	
	/**
	 * 비지니스 로직을 수행한다. 내부적으로는 {@link #doTask(String, LoginManagerIF, ToLetterCarrier, AbstractMessage)} 를 호출한다.
	 * @param projectName 프로젝트 이름
	 * @param fromAcceptedConnection 입력 메시지를 보낸 연결
	 * @param projectLoginManager 프로젝트 로그인 관리자
	 * @param mailboxID 메일 박스 식별자
	 * @param mailID 메일 식별자
	 * @param messageID 메시지 식별자
	 * @param receviedMiddleObject 수신한 스트림으로 부터 추출된 중간 객체로 입력 메시지로 변환되는 매개체이다.
	 * @param messageProtocol 메시지 프로토콜
	 * @param fromPersonalLoginManager 개별적인 로그인 관리자
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	public void execute(String projectName,
			AcceptedConnection fromAcceptedConnection,			
			ProjectLoginManagerIF projectLoginManager,						
			int mailboxID, int mailID, String messageID, Object receviedMiddleObject,
			MessageProtocolIF messageProtocol,
			LoginManagerIF fromPersonalLoginManager) throws InterruptedException {
		// long startTime = System.nanoTime();
		
		if (null == serverClassLoader) {
			serverClassLoader = (ServerClassLoader)this.getClass().getClassLoader();
			
			try {
				MessageCodecIF messageCodec = serverClassLoader.getServerMessageCodec(messageID);
				
				inputMessageDecoder = messageCodec.getMessageDecoder();
			} catch (Exception e) {
				ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class);
				
				String errorReason = new StringBuilder()
						.append("fail to get a input message decoder, errmsg=")
						.append(e.getMessage()).toString();
				
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, errorReason, e);
				
				ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
						errorType,
						errorReason,
						mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
				return;
			}
		}
			
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
				serverClassLoader);				
		
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
	
	
	/**
	 * 개발자가 정의 해야할 타스크 작업 추상화 메소드
	 * 
	 * @param projectName 프로젝트 이름
	 * @param loginManager 로그인 관리자
	 * @param toLetterCarrier 클라이언트로 보낼 메시지 배달부
	 * @param inputMessage 입력 메시지
	 * @throws Exception 처리중 에러 발생시 던지는 예외
	 */
	abstract public void doTask(String projectName, LoginManagerIF loginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception;
	
	
	@Override
	public void finalize() {
		Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
		log.log(Level.INFO, this.getClass().getSimpleName() + " call finalize");
	}
}
