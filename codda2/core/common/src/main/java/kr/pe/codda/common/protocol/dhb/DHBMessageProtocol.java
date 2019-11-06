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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.FreeSizeInputStream;
import kr.pe.codda.common.io.FreeSizeOutputStream;
import kr.pe.codda.common.io.IngoingDataStream;
import kr.pe.codda.common.io.SocketWriteStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageForwarderIF;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoder;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoderMatcher;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoderMatcherIF;
import kr.pe.codda.common.protocol.thb.THBSingleItemEncoder;
import kr.pe.codda.common.protocol.thb.THBSingleItemEncoderMatcher;
import kr.pe.codda.common.protocol.thb.THBSingleItemEncoderMatcherIF;
import kr.pe.codda.common.util.HexUtil;

/**
 * DHB 메시지 교환 프로토콜
 * 
 * @author Won Jonghoon
 * 
 */
public class DHBMessageProtocol implements MessageProtocolIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private int dataPacketBufferMaxCntPerMessage;
	// private Charset streamCharset = null;
	private CharsetEncoder streamCharsetEncoder;
	@SuppressWarnings("unused")
	private CharsetDecoder streamCharsetDecoder;
	private WrapBufferPoolIF wrapBufferPool = null;

	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	private int headerBodySize;

	private THBSingleItemDecoder thbSingleItemDecoder = null;
	private THBSingleItemEncoder thbSingleItemEncoder = null;

	private final Charset headerCharset = Charset.forName("UTF-8");
	private CharsetEncoder headerCharsetEncoder = null;
	private CharsetDecoder headerCharsetDecoder = null;

	public DHBMessageProtocol(int dataPacketBufferMaxCntPerMessage,
			CharsetEncoder streamCharsetEncoder,
			CharsetDecoder streamCharsetDecoder,
			WrapBufferPoolIF wrapBufferPool) {

		if (dataPacketBufferMaxCntPerMessage <= 0) {
			String errorMessage = new StringBuilder()
			.append("the parameter dataPacketBufferMaxCntPerMessage[")
			.append(dataPacketBufferMaxCntPerMessage)
			.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException(
					"the parameter streamCharsetEncoder is null");
		}

		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException(
					"the parameter streamCharsetDecoder is null");
		}

		Charset streamCharsetOfEncoder = streamCharsetEncoder.charset();
		Charset streamCharsetOfDecoder = streamCharsetDecoder.charset();

		if (!streamCharsetOfEncoder.equals(streamCharsetOfDecoder)) {
			String errorMessage = new StringBuilder()
			.append("the parameter streamCharsetEncoder[")
			.append(streamCharsetOfEncoder.name())
			.append("] is not same to the parameter streamCharsetDecoder[")
			.append(streamCharsetOfDecoder.name())
			.append("]").toString();

			throw new IllegalArgumentException(errorMessage);
		}

		if (null == wrapBufferPool) {

			throw new IllegalArgumentException(
					"the parameter wrapBufferPoolManager is null");
		}

		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.wrapBufferPool = wrapBufferPool;

		this.headerBodySize = 8 + CommonStaticFinalVars.MD5_BYTESIZE;
		this.messageHeaderSize = headerBodySize
				+ CommonStaticFinalVars.MD5_BYTESIZE;

		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = new THBSingleItemDecoderMatcher(
				streamCharsetDecoder);
		this.thbSingleItemDecoder = new THBSingleItemDecoder(
				thbSingleItemDecoderMatcher);

		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = new THBSingleItemEncoderMatcher(
				streamCharsetEncoder);
		this.thbSingleItemEncoder = new THBSingleItemEncoder(
				thbSingleItemEncoderMatcher);

		this.headerCharsetEncoder = headerCharset.newEncoder();
		this.headerCharsetEncoder.onMalformedInput(streamCharsetEncoder
				.malformedInputAction());
		this.headerCharsetEncoder.onUnmappableCharacter(streamCharsetEncoder
				.unmappableCharacterAction());

		this.headerCharsetDecoder = headerCharset.newDecoder();
		this.headerCharsetDecoder.onMalformedInput(streamCharsetDecoder
				.malformedInputAction());
		this.headerCharsetEncoder.onUnmappableCharacter(streamCharsetDecoder
				.unmappableCharacterAction());
	}

	@Override
	public void M2S(AbstractMessage inputMessage, AbstractMessageEncoder messageEncoder, SocketWriteStream socketWriteStream)
			throws NoMoreDataPacketBufferException, BodyFormatException,
			HeaderFormatException {
		if (null == inputMessage) {
			throw new IllegalArgumentException(
					"the parameter inputMessage is null");
		}

		if (null == messageEncoder) {
			throw new IllegalArgumentException(
					"the parameter messageEncoder is null");
		}
		
		if (null == socketWriteStream) {
			throw new IllegalArgumentException(
					"the parameter socketWriteStream is null");
		}

		DHBMessageHeader dhbMessageHeader = new DHBMessageHeader();
		String messageID = inputMessage.getMessageID();
		int mailboxID = inputMessage.messageHeaderInfo.mailboxID;
		int mailID = inputMessage.messageHeaderInfo.mailID;

		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.SEVERE, "MD5 initialization failed");
			System.exit(1);
		}

		// log.info("1");

		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = new FreeSizeOutputStream(
				dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
				wrapBufferPool);
		try {
			bodyOutputStream.putUBPascalString(messageID, headerCharset);
			bodyOutputStream.putUnsignedShort(mailboxID);
			bodyOutputStream.putInt(mailID);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder(
					"fail to make the header in body of the input message[")
					.append(inputMessage.toString()).append("] becase of unknown error, errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}

		// log.info("2");

		try {
			messageEncoder.encode(inputMessage, thbSingleItemEncoder,
					bodyOutputStream);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
			.append("fail to encode the input message[")
			.append(inputMessage.toString())
			.append("] to the body output stream becase of unknown error, errmsg=")
			.append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}

		// log.info("3");

		dhbMessageHeader.bodySize = bodyOutputStream.size();

		ArrayDeque<WrapBuffer> readableWrapBufferListOfBodyOutputStream = bodyOutputStream
				.getReadableWrapBufferQueue();
		if (0 == dhbMessageHeader.bodySize) {
			dhbMessageHeader.bodyMD5Bytes = new byte[CommonStaticFinalVars.MD5_BYTESIZE];
			Arrays.fill(dhbMessageHeader.bodyMD5Bytes,
					CommonStaticFinalVars.ZERO_BYTE);
		} else {
			md5.reset();
			{
				for (WrapBuffer readableWrapBufferOfBodyOutputStream : readableWrapBufferListOfBodyOutputStream) {
					ByteBuffer readableByteBufferOfBodyOutputStream = readableWrapBufferOfBodyOutputStream
							.getByteBuffer();

					md5.update(readableByteBufferOfBodyOutputStream);

					readableByteBufferOfBodyOutputStream.flip();
				}

				dhbMessageHeader.bodyMD5Bytes = md5.digest();
			}
		}

		// log.info("2. bodyMD5Bytes=[{}]",
		// HexUtil.getHexStringFromByteArray(dhbMessageHeader.bodyMD5Bytes));

		// log.info("4");

		// log.info("3. readableWrapBufferListOfBodyOutputStream={}",
		// readableWrapBufferListOfBodyOutputStream.toString());

		FreeSizeOutputStream headerOutputStream = new FreeSizeOutputStream(
				dataPacketBufferMaxCntPerMessage, headerCharsetEncoder,
				wrapBufferPool);
		try {
			dhbMessageHeader.onlyHeaderBodyPartToOutputStream(
					headerOutputStream, headerCharset);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
			.append("fail to apply the input message[")
			.append(inputMessage.toString())
			.append("]'s header body part to the header output stream becase of unknow error, errmsg=")
			.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}

		// log.info("5");

		ArrayDeque<WrapBuffer> wrapBufferListOfHeaderOutputStream = headerOutputStream
				.getOutputStreamWrapBufferList();

		md5.reset();
		{
			/** header body md5 구하기 */
			for (WrapBuffer wrapBufferOfHeaderBody : wrapBufferListOfHeaderOutputStream) {
				ByteBuffer byteBufferOfHeaderBody = wrapBufferOfHeaderBody
						.getByteBuffer();
				/** 버퍼 상태를 변경하지 않기 위해서 복사 버퍼로 md5 작업함 */
				ByteBuffer duplicatedByteBufferOfHeaderBody = byteBufferOfHeaderBody
						.duplicate();
				duplicatedByteBufferOfHeaderBody.flip();
				md5.update(duplicatedByteBufferOfHeaderBody);
			}

			dhbMessageHeader.headerBodyMD5Bytes = md5.digest();
		}

		// log.info("6");

		// log.info("5. wrapBufferListOfHeaderBodyOutputStream={}",
		// wrapBufferListOfHeaderBodyOutputStream.toString());

		try {
			headerOutputStream.putBytes(dhbMessageHeader.headerBodyMD5Bytes);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
			.append("fail to write the header md5 of the input message[")
			.append(inputMessage.toString())
			.append("] to the header output stream becase of unknow error, errmsg=")
			.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}

		headerOutputStream.changeReadableWrapBufferList();


		wrapBufferListOfHeaderOutputStream
				.addAll(readableWrapBufferListOfBodyOutputStream);

		// log.info("8");

		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());

		return wrapBufferListOfHeaderOutputStream;
	}

	@Override
	public void S2MList(IngoingDataStream receivedDataOnlyStream,
			ReceivedMessageForwarderIF wrapMessageBlockingQueue)
			throws HeaderFormatException, NoMoreDataPacketBufferException,
			InterruptedException {
		if (null == receivedDataOnlyStream) {
			throw new IllegalArgumentException(
					"the parameter receivedDataOnlyStream is null");
		}

		DHBMessageHeader messageHeader = (DHBMessageHeader) receivedDataOnlyStream
				.getUserDefObject();

		boolean isMoreMessage = false;
		long numberOfSocketReadBytes = receivedDataOnlyStream.getReceviedBytes();
		try {
			do {
				if (null == messageHeader
						&& numberOfSocketReadBytes >= messageHeaderSize) {
					/** 헤더 읽기 */
					DHBMessageHeader dhbMessageHeader = new DHBMessageHeader();
					byte[] actualHeaderBodyMD5Bytes = null;

					try {
						/*
						receivedDataOnlyStream.mark();
						actualHeaderBodyMD5Bytes = receivedDataOnlyStream.getMD5(headerBodySize);
						receivedDataOnlyStream.reset();
						*/
						actualHeaderBodyMD5Bytes = receivedDataOnlyStream.getMD5WithoutChange(headerBodySize);
						
					} catch (Exception e) {
						String errorMessage = new StringBuilder(
								"fail to read the md5 checksum of a header's body from the output stream, , errmsg=")
								.append(e.getMessage()).toString();
						log.warn(errorMessage, e);
						throw new HeaderFormatException(errorMessage);
					}
					
					try {
						dhbMessageHeader.fromInputStream(receivedDataOnlyStream,
								headerCharsetDecoder);
					} catch (Exception e) {
						String errorMessage = new StringBuilder(
								"dhb header parsing error::").append(
								e.getMessage()).toString();
						log.warn(errorMessage, e);
						throw new HeaderFormatException(errorMessage);
					}
					
					boolean isValidHeaderBodyMD5 = java.util.Arrays.equals(
							dhbMessageHeader.headerBodyMD5Bytes,
							actualHeaderBodyMD5Bytes);

					if (! isValidHeaderBodyMD5) {
						String errorMessage = new StringBuilder()
						.append("the actual header-body MD5[")
						.append(HexUtil.getHexStringFromByteArray(actualHeaderBodyMD5Bytes))
						.append("] is different from the header-body MD5 of header[")
						.append(dhbMessageHeader.toString())
						.append("]").toString();

						throw new HeaderFormatException(errorMessage);
					}
					
					if (dhbMessageHeader.bodySize < 0) {
						String errorMessage = new StringBuilder()
						.append("the body size is less than zero, ")
						.append(dhbMessageHeader.toString()).toString();
						throw new HeaderFormatException(errorMessage);
					}

					numberOfSocketReadBytes = receivedDataOnlyStream.getReceviedBytes();
					messageHeader = dhbMessageHeader;
				}

				if (null != messageHeader) {
					if (numberOfSocketReadBytes >= (messageHeader.bodySize+messageHeaderSize)) {												
						byte[] acutalBodyMD5Bytes = null;
						try {		
							/*
							receivedDataOnlyStream.mark();
							acutalBodyMD5Bytes = receivedDataOnlyStream.getMD5(messageHeader.bodySize);
							receivedDataOnlyStream.reset();
							*/
							acutalBodyMD5Bytes = receivedDataOnlyStream.getMD5WithoutChange(messageHeader.bodySize);
						} catch (Exception e) {
							String errorMessage = new StringBuilder(
									"fail to read a body md5 from the output stream, , errmsg=")
									.append(e.getMessage()).toString();
							log.warn(errorMessage, e);

							throw new HeaderFormatException(errorMessage);
						}						

						boolean isValidBodyMD5 = java.util.Arrays.equals(
								messageHeader.bodyMD5Bytes, acutalBodyMD5Bytes);
						
						if (!isValidBodyMD5) {
							String errorMessage = String
									.format("different body MD5, header[%s], body md5[%s]",
											messageHeader.toString(),
											HexUtil.getHexStringFromByteArray(acutalBodyMD5Bytes));

							throw new HeaderFormatException(errorMessage);
						}
						
						FreeSizeInputStream messageInputStream = receivedDataOnlyStream
								.cutReceivedDataStream(messageHeader.bodySize+messageHeaderSize);
						
						String messageID = null;
						int mailboxID;
						int mailID;
						try {
							messageInputStream.skip(messageHeaderSize);
							messageID = messageInputStream
									.getUBPascalString(headerCharset);
							mailboxID = messageInputStream.getUnsignedShort();
							mailID = messageInputStream.getInt();
						} catch (Exception e) {
							String errorMessage = new StringBuilder(
									"fail to read a header in body from the output stream, , errmsg=")
									.append(e.getMessage()).toString();
							log.warn(errorMessage, e);

							throw new HeaderFormatException(errorMessage);
						}

						/*
						 * ReadableMiddleObjectWrapper readableMiddleObjectWrapper = new
						 * ReadableMiddleObjectWrapper( messageID, mailboxID, mailID,
						 * messageInputStream);
						 */
						try {
							// wrapMessageBlockingQueue.putReceivedMessage(readableMiddleObjectWrapper);
							wrapMessageBlockingQueue.putReceivedMessage(mailboxID, mailID, messageID, messageInputStream);
						} catch (InterruptedException e) {
							// readableMiddleObjectWrapper.closeReadableMiddleObject();
							messageInputStream.close();
							throw e;
						}

						messageHeader = null;
						numberOfSocketReadBytes = receivedDataOnlyStream
								.getReceviedBytes();
						if (numberOfSocketReadBytes > messageHeaderSize) {
							isMoreMessage = true;
						} else {
							isMoreMessage = false;
						}
					}
				}
			} while (isMoreMessage);
		} finally {
			receivedDataOnlyStream.setUserDefObject(messageHeader);
		}
	}

	@Override
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}

	@Override
	public SingleItemDecoderIF getSingleItemDecoder() {
		return thbSingleItemDecoder;
	}
}
