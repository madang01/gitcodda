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
import kr.pe.codda.common.classloader.SystemClassDeterminer;
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
	
	private final ServerClassLoader taskClassLoader;
	private final AbstractMessageDecoder inputMessageDecoder;
	// private HashMap<String, MessageCodecIF> messageID2ServerMessageCodecHash = new HashMap<String, MessageCodecIF>();
	
	/**
	 * 생성자
	 * @throws DynamicClassCallException 동적 클래스 처리중 에러 발생시 던지는 예외
	 */
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
		
		ClassLoader thisClassLoader = this.getClass().getClassLoader();
		
		if ((thisClassLoader instanceof ServerClassLoader)) {
			taskClassLoader = (ServerClassLoader)thisClassLoader;
		} else {
			/**
			 * <pre>
			 * WARNING! 이 서버 타스크는 클래스 전체 이름이 동적 클래스를 뜻하지만 
			 * 시스템 클래스 로더에 올라가는 대상 클래스로 지정되어 있어 시스템 클래스 로더에 적재되어 사용된다.
			 * 하여 이 서버 타스크는 시스템 클래스로 동적 클래스와 연관이 없기때문에 
			 * 동적 클래스와 관련잇는 파라미터 'classloaderClassPathString' 와 파라미터 'lassloaderReousrcesPathString' 를  임의 지정하여
			 * 생성한 신규 ServerClassLoader 클래스 인스턴스 값을 멤버 변수 taskClassLoader 의 값으로 지정한다. 
			 * 
			 * 참고1) 시스템 클래스 로더에 올라가는 서버 타스크는 싱글턴 처럼 단 1번만 생성 된다. 반면에 동적 클래스는 동적 클래스 로더가 바뀔때 마다 생성된다.
			 * 
			 * 참고2) 서버 클래스 로더는 클래스 전체 이름이 동적 클래스를 뜻하더라도 시스템 클래스 로더에 올라가는 대상으로 지정된 클래스들은 시스템 클래스 로더에 위임한다.
			 * </pre> 
			 */
			SystemClassDeterminer systemClassDeterminer = new SystemClassDeterminer();
			taskClassLoader = new ServerClassLoader(".", ".", systemClassDeterminer);
		}
		
		
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
