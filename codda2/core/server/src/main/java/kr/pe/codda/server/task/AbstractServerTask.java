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

import kr.pe.codda.common.classloader.ServerClassLoader;
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
	
	private final ServerClassLoader taskClassLoader = (ServerClassLoader)this.getClass().getClassLoader();
	private final AbstractMessageDecoder inputMessageDecoder;
	// private HashMap<String, MessageCodecIF> messageID2ServerMessageCodecHash = new HashMap<String, MessageCodecIF>();
	
	public AbstractServerTask() throws DynamicClassCallException {
		
		String classFullName = this.getClass().getName();
		int startIndex = classFullName.lastIndexOf(".") + 1;		
		int endIndex = classFullName.lastIndexOf("ServerTask");
		
		String messageID = classFullName.substring(startIndex, endIndex);
		
		
		/** WARNING! junit 에서 inner class 로 mock 객체를 만들어 테스트시 필요하므로 지우지 말것 */ 
		int middleIndex = messageID.lastIndexOf("$");		
		if (middleIndex >= 0) {
			char[] classNames =  messageID.toCharArray();
			
			for (middleIndex++; middleIndex < classNames.length; middleIndex++) {
				if (classNames[middleIndex] < '0' || classNames[middleIndex] > '9') {
					break;
				}
			}
			
			startIndex = startIndex + middleIndex;
			
			messageID = classFullName.substring(startIndex, endIndex);
		}
		
		/*
		String messageCodecClassFullName = IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);
		
		Object retObject = CommonStaticUtil.getNewObjectFromClassloader(taskClassLoader, messageCodecClassFullName);		
		
		if (! (retObject instanceof MessageCodecIF)) {
			String errorMessage = new StringBuilder()
			.append("this instance(classLoader=")
			.append(taskClassLoader.hashCode())
			.append(") of ").append(classFullName)
			.append("] class is not a instance of MessageCodecIF class").toString();

			throw new DynamicClassCallException(errorMessage);
		}
		
		MessageCodecIF serverMessageCodec = (MessageCodecIF)retObject;
		
		inputMessageDecoder = serverMessageCodec.getMessageDecoder();		
		
		messageID2ServerMessageCodecHash.put(messageID, serverMessageCodec);
		*/
		
		MessageCodecIF messageCodec = taskClassLoader.getServerMessageCodec(messageID);
		
		inputMessageDecoder = messageCodec.getMessageDecoder();
	}

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
				taskClassLoader);				
		
		try {
			doTask(projectName, fromPersonalLoginManager, toLetterCarrier, inputMessage);
		} catch (InterruptedException e) {
			throw e;
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
	
	
	abstract public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception;
	
	
	@Override
	public void finalize() {
		Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
		log.log(Level.INFO, this.getClass().getSimpleName() + " call finalize");
	}
}
