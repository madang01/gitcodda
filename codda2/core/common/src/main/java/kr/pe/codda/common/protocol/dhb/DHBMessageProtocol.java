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

package kr.pe.codda.common.protocol.dhb;

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
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageForwarderIF;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoder;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoderMatcher;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoderMatcherIF;
import kr.pe.codda.common.protocol.thb.THBSingleItemEncoder;
import kr.pe.codda.common.protocol.thb.THBSingleItemEncoderMatcher;
import kr.pe.codda.common.protocol.thb.THBSingleItemEncoderMatcherIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;

/**
 * DHB 메시지 교환 프로토콜
 * 
 * @author Won Jonghoon
 * 
 */
public class DHBMessageProtocol implements MessageProtocolIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final int dataPacketBufferMaxCntPerMessage;
	private final StreamCharsetFamily streamCharsetFamily;
	private final WrapBufferPoolIF wrapBufferPool;

	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	private int headerBodySize;

	private final THBSingleItemDecoder thbSingleItemDecoder;
	private final THBSingleItemEncoder thbSingleItemEncoder;

	private final Charset headerCharset = Charset.forName("UTF-8");
	private CharsetEncoder headerCharsetEncoder = null;
	private CharsetDecoder headerCharsetDecoder = null;

	public DHBMessageProtocol(int dataPacketBufferMaxCntPerMessage, StreamCharsetFamily streamCharsetFamily,
			WrapBufferPoolIF wrapBufferPool) {

		if (dataPacketBufferMaxCntPerMessage <= 0) {
			String errorMessage = new StringBuilder().append("the parameter dataPacketBufferMaxCntPerMessage[")
					.append(dataPacketBufferMaxCntPerMessage).append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == streamCharsetFamily) {
			throw new IllegalArgumentException("the parameter streamCharsetFamily is null");
		}

		if (null == wrapBufferPool) {

			throw new IllegalArgumentException("the parameter wrapBufferPool is null");
		}

		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamCharsetFamily = streamCharsetFamily;
		this.wrapBufferPool = wrapBufferPool;

		this.headerBodySize = 8 + CommonStaticFinalVars.MD5_BYTESIZE;
		this.messageHeaderSize = headerBodySize + CommonStaticFinalVars.MD5_BYTESIZE;


		CharsetEncoder streamCharsetEncoder = streamCharsetFamily.getCharsetEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharsetFamily.getCharsetDecoder();

		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = new THBSingleItemDecoderMatcher(
				streamCharsetDecoder);
		thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);

		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = new THBSingleItemEncoderMatcher(
				streamCharsetEncoder);
		thbSingleItemEncoder = new THBSingleItemEncoder(thbSingleItemEncoderMatcher);

		headerCharsetEncoder = headerCharset.newEncoder();
		headerCharsetEncoder.onMalformedInput(streamCharsetEncoder.malformedInputAction());
		headerCharsetEncoder.onUnmappableCharacter(streamCharsetEncoder.unmappableCharacterAction());

		headerCharsetDecoder = headerCharset.newDecoder();
		headerCharsetDecoder.onMalformedInput(streamCharsetDecoder.malformedInputAction());
		headerCharsetDecoder.onUnmappableCharacter(streamCharsetDecoder.unmappableCharacterAction());
	}
	
	public StreamBuffer createNewMessageStreamBuffer() {
		StreamBuffer outputMesssageStreamBuffer = new StreamBuffer(streamCharsetFamily, dataPacketBufferMaxCntPerMessage, 
				wrapBufferPool);
		
		return outputMesssageStreamBuffer;
	}

	/**
	 * 단위 테스트 특성상 이 메소드에 대한 기능 검증은 {@link #S2O(IncomingStream, ReceivedMessageForwarderIF) } 와 쌍을 이루어 이루어진다.
	 * 하여 단위테스트를 원할 하게 하기 위해서 반환되는 리턴값의 타입을 InputStreamResource 으로 한다.
	 */
	@Override
	public void M2S(AbstractMessage inputMessage, AbstractMessageEncoder messageEncoder, StreamBuffer targetStreamBuffer)
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		if (null == inputMessage) {
			throw new IllegalArgumentException("the parameter inputMessage is null");
		}

		if (null == messageEncoder) {
			throw new IllegalArgumentException("the parameter messageEncoder is null");
		}
		
		if (null == targetStreamBuffer) {
			throw new IllegalArgumentException("the parameter targetStreamBuffer is null");
		}

		
		DHBMessageHeader dhbMessageHeader = new DHBMessageHeader();
		String messageID = inputMessage.getMessageID();
		int mailboxID = inputMessage.getMailboxID();
		int mailID = inputMessage.getMailID();
		
		long messageStartPostion = targetStreamBuffer.getPosition();

		// log.info("1");

		/**
		 * DHB 프로토콜 구조 = 헤더 + 바디 헤더 = 헤더바디 + 128 bit 헤더바디 MD5 체크섬 헤더바디 = 8 byte long 바디
		 * 크기 + 128 bit 바디 MD5 체크섬 바디 = 바디 헤더 + 메시지 바디 * 바디 헤더 = 1 byte unsigned byte
		 * 메시지 식별자 크기 + 2 byte unsigned short 메일박스 식별자 + 4 byte int 메일 식별자 메시지 바디 = 메시지
		 * 내용
		 */

		/** 바디 헤더 */
		long bodyStartPosition = messageStartPostion + messageHeaderSize;

		try {
			targetStreamBuffer.setPosition(bodyStartPosition);
			targetStreamBuffer.putUBPascalString(messageID, headerCharset);
			targetStreamBuffer.putUnsignedShort(mailboxID);
			targetStreamBuffer.putInt(mailID);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("fail to make the header in body of the input message[")
					.append(inputMessage.toString()).append("] becase of unknown error, errmsg=").append(e.getMessage())
					.toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}

		// log.info("2");

		/** 메시지 바디 */
		try {
			messageEncoder.encode(inputMessage, thbSingleItemEncoder, targetStreamBuffer);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to encode the input message[")
					.append(inputMessage.toString())
					.append("] to the body output stream becase of unknown error, errmsg=").append(e.getMessage())
					.toString();

			log.log(Level.WARNING, errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}

		// log.info("3");
		long bodyEndPostion = targetStreamBuffer.getPosition();

		/** 헤더 */
		try {
			/** 바디 MD5 체크섬 */
			dhbMessageHeader.bodySize = bodyEndPostion - bodyStartPosition;
			targetStreamBuffer.setPosition(bodyStartPosition);
			dhbMessageHeader.bodyMD5Bytes = targetStreamBuffer.getMD5WithoutChange(dhbMessageHeader.bodySize);

			/** 헤더바디 & 헤더바디 MD5 체크섬 */
			targetStreamBuffer.setPosition(messageStartPostion);
			targetStreamBuffer.putLong(dhbMessageHeader.bodySize);
			targetStreamBuffer.putBytes(dhbMessageHeader.bodyMD5Bytes);
			targetStreamBuffer.setPosition(messageStartPostion);
			dhbMessageHeader.headerBodyMD5Bytes = targetStreamBuffer.getMD5WithoutChange(headerBodySize);

			targetStreamBuffer.setPosition(messageStartPostion + headerBodySize);
			targetStreamBuffer.putBytes(dhbMessageHeader.headerBodyMD5Bytes);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("fail to make the header of the input message[")
					.append(inputMessage.toString()).append("] becase of unknown error, errmsg=").append(e.getMessage())
					.toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}

		targetStreamBuffer.setPosition(bodyEndPostion);

		// log.info("8");

		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());
	}

	@Override
	public void S2O(IncomingStream inputStreamResource, ReceivedMessageForwarderIF receivedMessageForwarder)
			throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException {
		if (null == inputStreamResource) {
			throw new IllegalArgumentException("the parameter inputStreamResource is null");
		}

		if (null == receivedMessageForwarder) {
			throw new IllegalArgumentException("the parameter receivedMessageForwarder is null");
		}

		DHBMessageHeader messageHeader = (DHBMessageHeader) inputStreamResource.getUserDefObject();

		boolean isMoreMessage = false;
		long numberOfSocketReadBytes = inputStreamResource.getPosition();
		try {
			do {
				if (null == messageHeader && numberOfSocketReadBytes >= messageHeaderSize) {

					DHBMessageHeader dhbMessageHeader = new DHBMessageHeader();
					byte[] actualHeaderBodyMD5Bytes = null;

					long oldPosition = inputStreamResource.getPosition();

					/** 헤더 */
					try {

						/** 헤더 바디 MD5 체크섬 */
						inputStreamResource.setPosition(0);
						actualHeaderBodyMD5Bytes = inputStreamResource.getMD5WithoutChange(headerBodySize);

						//inputStreamResource.setPosition(0);
						/** 8 byte long 바디 크기 */
						dhbMessageHeader.bodySize = inputStreamResource.getLong();
						/** 128 bit 바디 MD5 체크섬 */
						dhbMessageHeader.bodyMD5Bytes = inputStreamResource
								.getBytes(CommonStaticFinalVars.MD5_BYTESIZE);
						/** 128 bit 헤더 바디 MD5 체크섬 */
						dhbMessageHeader.headerBodyMD5Bytes = inputStreamResource
								.getBytes(CommonStaticFinalVars.MD5_BYTESIZE);
					} catch (Exception e) {
						String errorMessage = new StringBuilder("dhb header parsing error::").append(e.getMessage())
								.toString();
						log.log(Level.WARNING, errorMessage, e);
						throw new HeaderFormatException(errorMessage);
					}
					
					inputStreamResource.setPosition(oldPosition);

					/** 헤더바디 MD5 체크섬과 헤더의 헤더바디 MD5 체크섬 비교 */
					boolean isValidHeaderBodyMD5 = java.util.Arrays.equals(dhbMessageHeader.headerBodyMD5Bytes,
							actualHeaderBodyMD5Bytes);

					if (!isValidHeaderBodyMD5) {
						String errorMessage = new StringBuilder().append("the actual header-body MD5[")
								.append(HexUtil.getHexStringFromByteArray(actualHeaderBodyMD5Bytes))
								.append("] is different from the header-body MD5 of header[")
								.append(dhbMessageHeader.toString()).append("]").toString();

						throw new HeaderFormatException(errorMessage);
					}

					if (dhbMessageHeader.bodySize < 0) {
						String errorMessage = new StringBuilder().append("the body size is less than zero, ")
								.append(dhbMessageHeader.toString()).toString();
						throw new HeaderFormatException(errorMessage);
					}
					
					//numberOfSocketReadBytes = inputStreamResource.getPosition();
					messageHeader = dhbMessageHeader;
				}

				if (null != messageHeader) {					
					long messageInputMessageSize = messageHeader.bodySize + messageHeaderSize;
					if (numberOfSocketReadBytes >= messageInputMessageSize) {
						// long oldPosition = inputStreamResource.getPosition();
						
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
						
						byte[] acutalBodyMD5Bytes = null;
						try {
							messageInputStream.setPosition(messageHeaderSize);
							acutalBodyMD5Bytes = messageInputStream.getMD5WithoutChange(messageHeader.bodySize);

							// inputStreamResource.setPosition(oldPosition);
						} catch (Exception e) {
							String errorMessage = new StringBuilder(
									"fail to read a body md5 from   stream, errmsg=").append(e.getMessage())
											.toString();
							log.log(Level.WARNING, errorMessage, e);

							throw new HeaderFormatException(errorMessage);
						}

						boolean isValidBodyMD5 = java.util.Arrays.equals(messageHeader.bodyMD5Bytes,
								acutalBodyMD5Bytes);

						if (!isValidBodyMD5) {
							String errorMessage = new StringBuilder()
									.append("different body MD5, header[")
									.append(messageHeader.toString())
									.append("], body md5[")
									.append(HexUtil.getHexStringFromByteArray(acutalBodyMD5Bytes))
									.append("]").toString();

							throw new HeaderFormatException(errorMessage);
						}

						String messageID = null;
						int mailboxID;
						int mailID;
						try {
							// messageInputStream.setPosition(messageHeaderSize);
							messageID = messageInputStream.getUBPascalString(headerCharset);
							mailboxID = messageInputStream.getUnsignedShort();
							mailID = messageInputStream.getInt();
						} catch (Exception e) {
							String errorMessage = new StringBuilder(
									"fail to read a header in body from the output stream, , errmsg=")
											.append(e.getMessage()).toString();
							log.log(Level.WARNING, errorMessage, e);

							throw new HeaderFormatException(errorMessage);
						}
						
						if (CommonStaticFinalVars.SERVER_ASYN_MAILBOX_ID == mailboxID) {
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
							receivedMessageForwarder.putReceivedMessage(mailboxID, mailID, messageID,
									messageInputStream);
						} catch (InterruptedException e) {
							messageInputStream.releaseAllWrapBuffers();
							throw e;
						}

						messageHeader = null;
						numberOfSocketReadBytes = inputStreamResource.getPosition();
						if (numberOfSocketReadBytes > messageHeaderSize) {
							isMoreMessage = true;
						} else {
							isMoreMessage = false;
						}
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

	/*
	@Override
	public int getDataPacketBufferMaxCntPerMessage() {
		return dataPacketBufferMaxCntPerMessage;
	}
	*/
}
