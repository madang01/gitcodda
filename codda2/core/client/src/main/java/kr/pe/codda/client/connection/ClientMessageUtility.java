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

package kr.pe.codda.client.connection;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.classloader.MessageDecoderManagerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.impl.message.ExceptionDeliveryRes.ExceptionDeliveryRes;

/**
 * 클라이언트 메시지 유틸 추상화 클래스
 * @author Won Jonghoon
 *
 */
public abstract class ClientMessageUtility {

	/**
	 * <pre>
	 * 지정한 '수신한 중간 객체'로 부터 변환된 
	 * 지정한 메일 박스 식별자와 메일 식별자 그리고 메시지 식별자를 갖는 출력 메시지를 반환한다.
	 * 이때 '수신한 중간 객체'의 자원 해제가 반듯이 이루어 지도록 보장한다.  
	 * 단 출력 메시지를 반환 과정에서 에러 발생시 
	 * 예를 들면 동적 호출 클래스 호출이 한다든지 하면 {@link ExceptionDeliveryRes} 을 반환한다.
	 * </pre>
	 * 
	 * @param title 만들고자 하는 메시지 제목
	 * @param messageCodecManger 메시지 코덱 관리자
	 * @param messageProtocol 메시지 프로토콜
	 * @param mailboxID 메일 박스 식별자
	 * @param mailID 메일 식별자
	 * @param messageID 메시지 식별자
	 * @param receviedMiddleObject 입력 스트림에서 메시지 변환용으로 추출된 '수신한 중간 객체'    
	 * @return 지정한 '수신한 중간 객체'로 부터 변환된 지정한 메일 박스 식별자와 메일 식별자 그리고 메시지 식별자를 갖는 출력 메시지
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 * @throws BodyFormatException 메시지 내용이 담기는 바디 부분에 잘못된 값이 있을때 던지는 예외
	 * @throws ServerTaskException  서버 비지니스 로직 에러 발생시 던지는 예외
	 * @throws NoMoreWrapBufferException  랩버퍼 부족시 던지는 예외
	 * @throws DynamicClassCallException  동적 클래스 호출 실패시 던지는 예외
	 */
	public static AbstractMessage buildOutputMessage(String title,
			MessageDecoderManagerIF messageCodecManger, 
			MessageProtocolIF messageProtocol,
			int mailboxID, int mailID, String messageID, Object receviedMiddleObject) throws DynamicClassCallException, NoMoreWrapBufferException, ServerTaskException, BodyFormatException, IOException {
		
		try {
			AbstractMessageDecoder messageDecoder = null;		
			try {
				messageDecoder = messageCodecManger.getMessageDecoder(messageID);
			} catch (DynamicClassCallException e) {
				String errorMessage = new StringBuilder("fail to get the client message decoder of the ")
						.append(title)
						.append(" output message")
						.append("mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
						.append("], errmsg=")
						.append(e.getMessage()).toString();

				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);
					
				
				ExceptionDeliveryRes exceptionDeliveryRes = new ExceptionDeliveryRes();
				exceptionDeliveryRes.setMailboxID(mailboxID);
				exceptionDeliveryRes.setMailID(mailID);
				exceptionDeliveryRes.setErrorPlace(ExceptionDelivery.ErrorPlace.CLIENT);
				exceptionDeliveryRes.setErrorType(ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class));
			
				exceptionDeliveryRes.setErrorMessageID(messageID);
				exceptionDeliveryRes.setErrorReason(errorMessage);
				
				return exceptionDeliveryRes;
			} catch (Exception e) {
				String errorMessage = new StringBuilder(
						"unknwon error::fail to get the client message decoder of the ")
						.append(title)
						.append(" output message[")
						.append("mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
								.append("], errmsg=").append(e.getMessage())
								.toString();
				
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, errorMessage, e);
				
				ExceptionDeliveryRes exceptionDeliveryRes = new ExceptionDeliveryRes();
				exceptionDeliveryRes.setMailboxID(mailboxID);
				exceptionDeliveryRes.setMailID(mailID);
				exceptionDeliveryRes.setErrorPlace(ExceptionDelivery.ErrorPlace.CLIENT);
				exceptionDeliveryRes.setErrorType(ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class));
			
				exceptionDeliveryRes.setErrorMessageID(messageID);
				exceptionDeliveryRes.setErrorReason(errorMessage);
				
				return exceptionDeliveryRes;
			}

			AbstractMessage outputMessage = null;
			try {
				outputMessage = messageProtocol.O2M(messageDecoder, mailboxID, mailID, messageID, receviedMiddleObject);
				outputMessage.setMailboxID(mailboxID);
				outputMessage.setMailID(mailID);
			} catch (BodyFormatException e) {
				String errorMessage = new StringBuilder("fail to decode the var 'readableMiddleObject' of the ")
						.append(title)
						.append(" output message")
						.append("mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
						.append("], errmsg=")
						.append("")
						.append(e.getMessage())
						.toString();

				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);		
				
				ExceptionDeliveryRes exceptionDeliveryRes = new ExceptionDeliveryRes();
				exceptionDeliveryRes.setMailboxID(mailboxID);
				exceptionDeliveryRes.setMailID(mailID);
				exceptionDeliveryRes.setErrorPlace(ExceptionDelivery.ErrorPlace.CLIENT);
				exceptionDeliveryRes.setErrorType(ExceptionDelivery.ErrorType.valueOf(BodyFormatException.class));
			
				exceptionDeliveryRes.setErrorMessageID(messageID);
				exceptionDeliveryRes.setErrorReason(errorMessage);
				
				return exceptionDeliveryRes;
			} catch (Exception | Error e) {
				String errorMessage = new StringBuilder("unknow error::fail to decode the var 'readableMiddleObject' of the ")
						.append(title)
						.append(" output message")
						.append("mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
						.append("], errmsg=")
						.append("")
						.append(e.getMessage())
						.toString();
				
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, errorMessage, e);
				
				ExceptionDeliveryRes exceptionDeliveryRes = new ExceptionDeliveryRes();
				exceptionDeliveryRes.setMailboxID(mailboxID);
				exceptionDeliveryRes.setMailID(mailID);
				exceptionDeliveryRes.setErrorPlace(ExceptionDelivery.ErrorPlace.CLIENT);
				exceptionDeliveryRes.setErrorType(ExceptionDelivery.ErrorType.valueOf(BodyFormatException.class));
			
				exceptionDeliveryRes.setErrorMessageID(messageID);
				exceptionDeliveryRes.setErrorReason(errorMessage);
				
				return exceptionDeliveryRes;
			}			
			
			if (outputMessage instanceof ExceptionDeliveryRes) {
				ExceptionDelivery.ErrorType.throwSelfExnException((ExceptionDeliveryRes)outputMessage);
			}

			return outputMessage;
		} finally {
			messageProtocol.closeReadableMiddleObject(mailboxID, mailID, messageID, receviedMiddleObject);
		}	
	}
	/*
	private static ArrayDeque<WrapBuffer> buildReadableWrapBufferList(
			MessageEncoderManagerIF messageEncoderManager, 
			MessageProtocolIF messageProtocol,
			AbstractMessage inputMessage)
			throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);
		
		AbstractMessageEncoder messageEncoder = null;

		try {
			messageEncoder = messageEncoderManager.getMessageEncoder(inputMessage.getMessageID());
		} catch (DynamicClassCallException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}

		ArrayDeque<WrapBuffer> wrapBufferList = null;
		try {
			wrapBufferList = messageProtocol.M2S(inputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (BodyFormatException e) {
			throw e;
		} catch (HeaderFormatException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		
		
		
		return wrapBufferList;
	}
*/
}
