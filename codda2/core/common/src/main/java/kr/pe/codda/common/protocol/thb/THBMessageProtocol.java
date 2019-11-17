/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.common.protocol.thb;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.InputStreamResource;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageForwarderIF;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * THB 메시지 프로토콜<br/> 
 * DHB 의 축소형 프로토콜로 DHB 와 달리 쓰레드 세이프 검출및 데이터 검증에 취약하다.<br/> 
 * 따라서 쓰레드 세이프 검증이 필요 없고 데이터 신뢰성 높은 TCP/IP 환경에서 유효하다.<br/>
 * DHB 를 통해서 쓰레드 세이프 검증 완료한후 THB 프로토콜로 전환하는것을 추천함.<br/>
 * @author Won Jonghoon
 *
 */
public class THBMessageProtocol implements MessageProtocolIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	
	private final int dataPacketBufferMaxCntPerMessage;
	private final StreamCharsetFamily streamCharsetFamily;
	private final WrapBufferPoolIF wrapBufferPool;
	
	/** 메시지 헤더 크기, 단위 byte */
	private final int messageHeaderSize;
	private final THBSingleItemDecoder thbSingleItemDecoder;	
	private final THBSingleItemEncoder thbSingleItemEncoder;
	
	
	private final Charset headerCharset = Charset.forName("UTF-8");
	private final CharsetEncoder headerCharsetEncoder;
	private final CharsetDecoder headerCharsetDecoder;
	
	public THBMessageProtocol( 
			int dataPacketBufferMaxCntPerMessage,
			StreamCharsetFamily streamCharsetFamily,
			WrapBufferPoolIF wrapBufferPool) {
		if (dataPacketBufferMaxCntPerMessage <= 0) {
			String errorMessage = new StringBuilder()
			.append("the parameter dataPacketBufferMaxCntPerMessage[")
			.append(dataPacketBufferMaxCntPerMessage)
			.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == streamCharsetFamily) {
			throw new IllegalArgumentException("the parameter streamCharsetFamily is null");
		}

		if (null == wrapBufferPool) {

			throw new IllegalArgumentException("the parameter wrapBufferPoolManager is null");
		}
		
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;		
		this.streamCharsetFamily = streamCharsetFamily;
		this.wrapBufferPool = wrapBufferPool;
		
		
		messageHeaderSize = 8;
		
		CharsetEncoder streamCharsetEncoder = streamCharsetFamily.getCharsetEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharsetFamily.getCharsetDecoder();
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);
		
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = new THBSingleItemEncoderMatcher(streamCharsetEncoder);		
		thbSingleItemEncoder = new THBSingleItemEncoder(thbSingleItemEncoderMatcher);
		
		headerCharsetEncoder = headerCharset.newEncoder();
		headerCharsetEncoder.onMalformedInput(streamCharsetEncoder.malformedInputAction());
		headerCharsetEncoder.onUnmappableCharacter(streamCharsetEncoder.unmappableCharacterAction());
		
		
		headerCharsetDecoder = headerCharset.newDecoder();
		headerCharsetDecoder.onMalformedInput(streamCharsetDecoder.malformedInputAction());
		headerCharsetDecoder.onUnmappableCharacter(streamCharsetDecoder.unmappableCharacterAction());
	}
	
	
	/**
	 * 단위 테스트 특성상 이 메소드에 대한 기능 검증은 {@link #S2O(InputStreamResource, ReceivedMessageForwarderIF) } 와 쌍을 이루어 이루어진다.
	 * 하여 단위테스트를 원할 하게 하기 위해서 반환되는 리턴값의 타입을 InputStreamResource 으로 한다.
	 */
	@Override
	public StreamBuffer M2S(AbstractMessage inputMessage, AbstractMessageEncoder messageEncoder) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		String messageID = inputMessage.getMessageID();
		int mailboxID = inputMessage.messageHeaderInfo.mailboxID;
		int mailID = inputMessage.messageHeaderInfo.mailID;
		
		/**
		 * 참고) 단위 테스트 특성상  쉽게 하기 위해서 StreamBuffer 이 아닌 InputStreamResource 클래스로 인스턴스로 만듬.
		 */
		StreamBuffer messageOutputStream = new InputStreamResource(streamCharsetFamily, dataPacketBufferMaxCntPerMessage, wrapBufferPool);

		/**
		 * THB 프로토콜 구조 = 헤더  + 바디
		 * 헤더 =  8 byte 바디 크기 
		 * 바디 = 바디 헤더 + 메시지 바디 
		 * 바디 헤더 = 1 byte unsigned byte 메시지 식별자 크기 + 2 byte unsigned short 메일박스 식별자 + 4 byte int 메일 식별자
		 * 메시지 바디 = 메시지 내용 
		 */
		long bodyStartPosition = messageHeaderSize;
		
		/** 바디헤더 */
		try {
			messageOutputStream.setPosition(messageHeaderSize);
			
			messageOutputStream.putUBPascalString(messageID, headerCharset);
			messageOutputStream.putUnsignedShort(mailboxID);
			messageOutputStream.putInt(mailID);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("fail to make a body header of the the parameter inputMessage[")
					.append(inputMessage.toString())
					.append("], errmsg=").append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}
		
		/** 메시지 바디 */
		try {
			messageEncoder.encode(inputMessage, thbSingleItemEncoder, messageOutputStream);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
			.append("fail to encode the input message[")
			.append(inputMessage.toString())
			.append("] to the body output stream becase of unknown error, errmsg=")
			.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		}
		
		long bodyEndPostion = messageOutputStream.getPosition();

		/** 헤더 */		
		try {
			messageOutputStream.setPosition(0);
			messageOutputStream.putLong(bodyEndPostion  - bodyStartPosition);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
			.append("fail to apply the input message[")
			.append(inputMessage.toString())
			.append("]'s header to the header output stream becase of unknow error, errmsg=")
			.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);

			throw new HeaderFormatException(errorMessage);
		}	
		
		messageOutputStream.setPosition(0);
		messageOutputStream.setLimit(bodyEndPostion);
		
		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());
		
		
		return messageOutputStream;
	}
	
	
	@Override
	public void S2O(InputStreamResource inputStreamResource, ReceivedMessageForwarderIF receivedMessageForwarder) 
			throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException {		
		THBMessageHeader messageHeader = (THBMessageHeader)inputStreamResource.getUserDefObject();		
				
		
		boolean isMoreMessage = false;
		
		long numberOfSocketReadBytes = inputStreamResource.getPosition();
		
		try {
			do {
				if (null == messageHeader
						&& numberOfSocketReadBytes >= messageHeaderSize) {
					/** THB 프로토콜 구조 = 8 byte 바디 크기  + 바디 */
					THBMessageHeader thbMessageHeader = new THBMessageHeader();
					long oldPosition = inputStreamResource.getPosition();
					
					try {
						inputStreamResource.setPosition(0);
						thbMessageHeader.bodySize = inputStreamResource.getLong();
					} catch (Exception e) {
						String errorMessage = new StringBuilder(
								"fail to read the header from the output stream, , errmsg=")
								.append(e.getMessage()).toString();
						log.log(Level.WARNING, errorMessage, e);
						throw new HeaderFormatException(errorMessage);
					}
					
					inputStreamResource.setPosition(oldPosition);

					if (thbMessageHeader.bodySize < 0) {
						String errorMessage = new StringBuilder()
						.append("the body size is less than zero, ")
						.append(thbMessageHeader.toString()).toString();
						throw new HeaderFormatException(errorMessage);
					}

					
					// numberOfSocketReadBytes = inputStreamResource.getPosition();
					messageHeader = thbMessageHeader;
				}
				
				if (null != messageHeader) {
					/*log.info(String.format("3. inputStramSizeBeforeMessageWork[%d]", inputStramSizeBeforeMessageWork));*/
					
					long messageInputMessageSize = messageHeader.bodySize + messageHeaderSize;
					
					if (numberOfSocketReadBytes >= messageInputMessageSize) {
						/** 메시지 추출 */
						StreamBuffer messageInputStream = null;
						
						try {
							messageInputStream = inputStreamResource.cutMessageInputStream(messageInputMessageSize);
						} catch(Exception e) {
							String errorMessage = new StringBuilder("fail to cut one message input stream from input socket stream, errmsg=")
									.append(e.getMessage()).toString();
							log.log(Level.WARNING, errorMessage, e);
							
							throw new HeaderFormatException(errorMessage);
						}

						String messageID = null;
						int mailboxID;
						int mailID;
						try {
							messageInputStream.setPosition(messageHeaderSize);
							messageID = messageInputStream.getUBPascalString(headerCharset);
							mailboxID = messageInputStream.getUnsignedShort();
							mailID = messageInputStream.getInt();
						} catch (Exception e) {
							String errorMessage = new StringBuilder("fail to read body-header from input socket stream, errmsg=")
									.append(e.getMessage()).toString();
							log.log(Level.WARNING, errorMessage, e);
							
							throw new HeaderFormatException(errorMessage);
						}
						
						
						try {
							receivedMessageForwarder.putReceivedMessage(mailboxID, mailID, messageID, messageInputStream);
						} catch(InterruptedException e) {
							messageInputStream.close();							
							throw e;
						}

						numberOfSocketReadBytes = inputStreamResource.getPosition();
						if (numberOfSocketReadBytes > messageHeaderSize) {
							isMoreMessage = true;
						} else {
							isMoreMessage = false;
						}
						
						messageHeader = null;
					}
				}
			} while (isMoreMessage);			
		} finally {
			inputStreamResource.setUserDefObject(messageHeader);
		}
	}
	
	
	public AbstractMessage O2M(AbstractMessageDecoder messageDecoder, int mailboxID, int mailID, String messageID, Object readableMiddleObject) throws BodyFormatException {
		AbstractMessage receivedMessage = null;
		try {
			receivedMessage = CommonStaticUtil.O2M(messageDecoder, thbSingleItemDecoder, mailboxID, mailID, messageID, readableMiddleObject);
		} catch(IllegalArgumentException e) {
			if (null != readableMiddleObject && readableMiddleObject instanceof StreamBuffer) {
				StreamBuffer sb = (StreamBuffer)readableMiddleObject;
				try {
					sb.close();
				} catch(Exception e1) {
					String errorMessage = new StringBuilder()
							.append("fail to close the message body stream[messageID=")
							.append(messageID)
							.append(", mailboxID=")
							.append(mailboxID)
							.append(", mailID=")
							.append(mailID)
							.append("] body stream").toString();
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, errorMessage, e1);
				}
			}
			throw e;
		}
		return receivedMessage;
	}
	
	public void closeReadableMiddleObject(int mailboxID, int mailID, String messageID, Object readableMiddleObject) {
		if (null == readableMiddleObject) {
			throw new IllegalArgumentException("the parameter readableMiddleObject is null");
		}
		if (! (readableMiddleObject instanceof StreamBuffer)) {
			throw new IllegalArgumentException("the parameter readableMiddleObject is not a instance of StreamBuffer class");
		}
		
		StreamBuffer sb = (StreamBuffer)readableMiddleObject;
		sb.close();		
	}
	
	@Override
	public int getDataPacketBufferMaxCntPerMessage() {
		return dataPacketBufferMaxCntPerMessage;
	}
	
}