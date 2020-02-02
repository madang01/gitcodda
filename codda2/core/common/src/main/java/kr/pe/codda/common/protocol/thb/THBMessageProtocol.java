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
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMiddleObjectForwarderIF;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * <pre>
 * THB 메시지 프로토콜.
 * 
 * DHB 의 축소형 프로토콜로 DHB 와 달리 쓰레드 세이프 검출및 데이터 검증을 하는데 도움을 주는 '헤더와 바디 부분을 교차하는 MD5 체크섬'이 없다.
 * 이때문에 DHB 프로토콜 보다 빠르다. 
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class THBMessageProtocol implements MessageProtocolIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	
	private final int maxNumberOfWrapBuffertPerMessage;
	private final StreamCharsetFamily streamCharsetFamily;
	private final WrapBufferPoolIF wrapBufferPool;
	
	/** 메시지 헤더 크기, 단위 byte */
	private final int messageHeaderSize;
	private final THBSingleItemDecoder thbSingleItemDecoder;	
	private final THBSingleItemEncoder thbSingleItemEncoder;
	
	
	private final Charset headerCharset = Charset.forName("UTF-8");
	private final CharsetEncoder headerCharsetEncoder;
	private final CharsetDecoder headerCharsetDecoder;
	
	/**
	 * 생성자
	 * 
	 * @param maxNumberOfWrapBuffertPerMessage 메시지 1개당 최대 랩 버퍼 갯수 
	 * @param streamCharsetFamily 문자셋, 문자셋 디코더, 문자셋 인코더 묶음
	 * @param wrapBufferPool 랩 버퍼 폴
	 */
	public THBMessageProtocol( 
			int maxNumberOfWrapBuffertPerMessage,
			StreamCharsetFamily streamCharsetFamily,
			WrapBufferPoolIF wrapBufferPool) {
		if (maxNumberOfWrapBuffertPerMessage <= 0) {
			String errorMessage = new StringBuilder()
			.append("the parameter maxNumberOfWrapBuffertPerMessage[")
			.append(maxNumberOfWrapBuffertPerMessage)
			.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == streamCharsetFamily) {
			throw new IllegalArgumentException("the parameter streamCharsetFamily is null");
		}

		if (null == wrapBufferPool) {

			throw new IllegalArgumentException("the parameter wrapBufferPool is null");
		}
		
		this.maxNumberOfWrapBuffertPerMessage = maxNumberOfWrapBuffertPerMessage;		
		this.streamCharsetFamily = streamCharsetFamily;
		this.wrapBufferPool = wrapBufferPool;
		
		
		messageHeaderSize = 8;
		
		CharsetEncoder streamCharsetEncoder = streamCharsetFamily.getCharsetEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharsetFamily.getCharsetDecoder();
		
		THBSingleItemTypeDecoderMatcherIF thbSingleItemDecoderMatcher = new THBSingleItemTypeDecoderMatcher(streamCharsetFamily);
		thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);
		
		
		THBSingleItemTypeEncoderMatcherIF thbSingleItemEncoderMatcher = new THBSingleItemTypeEncoderMatcher(streamCharsetFamily);		
		thbSingleItemEncoder = new THBSingleItemEncoder(thbSingleItemEncoderMatcher);
		
		headerCharsetEncoder = headerCharset.newEncoder();
		headerCharsetEncoder.onMalformedInput(streamCharsetEncoder.malformedInputAction());
		headerCharsetEncoder.onUnmappableCharacter(streamCharsetEncoder.unmappableCharacterAction());
		
		
		headerCharsetDecoder = headerCharset.newDecoder();
		headerCharsetDecoder.onMalformedInput(streamCharsetDecoder.malformedInputAction());
		headerCharsetDecoder.onUnmappableCharacter(streamCharsetDecoder.unmappableCharacterAction());
	}
	
	@Override
	public StreamBuffer createNewMessageStreamBuffer() {
		StreamBuffer newStreamBuffer = new StreamBuffer(streamCharsetFamily, maxNumberOfWrapBuffertPerMessage, 
				wrapBufferPool);
		
		return newStreamBuffer;
	}
	
	
	@Override
	public void M2S(AbstractMessage inputMessage, AbstractMessageEncoder messageEncoder, StreamBuffer targetStreamBuffer) 
			throws NoMoreWrapBufferException, BodyFormatException, HeaderFormatException {
		String messageID = inputMessage.getMessageID();
		int mailboxID = inputMessage.getMailboxID();
		int mailID = inputMessage.getMailID();
		
		long messageStartPostion = targetStreamBuffer.getPosition();
				
		/**
		 * 참고) 단위 테스트 특성상  쉽게 하기 위해서 StreamBuffer 이 아닌 InputStreamResource 클래스로 인스턴스로 만듬.
		 */
		// StreamBuffer messageOutputStream = new IncomingStream(streamCharsetFamily, dataPacketBufferMaxCntPerMessage, wrapBufferPool);
		

		/**
		 * THB 프로토콜 구조 = 헤더  + 바디
		 * 헤더 =  8 byte 바디 크기 
		 * 바디 = 바디 헤더 + 메시지 바디 
		 * 바디 헤더 = 1 byte unsigned byte 메시지 식별자 크기 + 2 byte unsigned short 메일박스 식별자 + 4 byte int 메일 식별자
		 * 메시지 바디 = 메시지 내용 
		 */		
		
		long bodyStartPosition = messageStartPostion + messageHeaderSize;
		
		/** 바디헤더 */
		try {
			targetStreamBuffer.setPosition(bodyStartPosition);
			
			targetStreamBuffer.putUBPascalString(messageID, headerCharset);
			targetStreamBuffer.putUnsignedShort(mailboxID);
			targetStreamBuffer.putInt(mailID);
		} catch (NoMoreWrapBufferException e) {
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
			messageEncoder.encode(inputMessage, thbSingleItemEncoder, targetStreamBuffer);
		} catch (NoMoreWrapBufferException e) {
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
		
		long bodyEndPostion = targetStreamBuffer.getPosition();

		/** 헤더 */		
		try {
			targetStreamBuffer.setPosition(messageStartPostion);
			targetStreamBuffer.putLong(bodyEndPostion  - bodyStartPosition);
		} catch (NoMoreWrapBufferException e) {
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
		
		targetStreamBuffer.setPosition(bodyEndPostion);
		
		
		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());
	}
	
	
	@Override
	public void S2O(IncomingStream inputStreamResource, ReceivedMiddleObjectForwarderIF receivedMessageForwarder) 
			throws HeaderFormatException, NoMoreWrapBufferException, InterruptedException {		
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
						
						if (CommonStaticFinalVars.NOCOUNT_ASYN_MAILBOX_ID == mailboxID) {
							String errorMessage = new StringBuilder()
									.append("the recevied message[messageID=")
									.append(messageID)
									.append(", mailboxID=")
									.append(mailboxID)
									.append(", mailID=")
									.append(mailID)
									.append("]'s mailbox id is bad, any client message can't have a server asyn mailbox id").toString();
							throw new HeaderFormatException(errorMessage);
						}						
						
						try {
							receivedMessageForwarder.putReceivedMiddleObject(mailboxID, mailID, messageID, messageInputStream);
						} catch(InterruptedException e) {
							messageInputStream.releaseAllWrapBuffers();							
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
	
	@Override
	public AbstractMessage O2M(AbstractMessageDecoder messageDecoder, int mailboxID, int mailID, String messageID, Object readableMiddleObject) throws BodyFormatException {
		AbstractMessage receivedMessage = CommonStaticUtil.O2M(this, messageDecoder, thbSingleItemDecoder, mailboxID, mailID, messageID, readableMiddleObject);
		
		return receivedMessage;
	}
	
	@Override
	public void closeReadableMiddleObject(int mailboxID, int mailID, String messageID, Object readableMiddleObject) {
		if (null == readableMiddleObject) {
			throw new IllegalArgumentException("the parameter readableMiddleObject is null");
		}
		if (! (readableMiddleObject instanceof StreamBuffer)) {
			throw new IllegalArgumentException("the parameter readableMiddleObject is not a instance of StreamBuffer class");
		}
		
		StreamBuffer sb = (StreamBuffer)readableMiddleObject;
		sb.releaseAllWrapBuffers();		
	}
}