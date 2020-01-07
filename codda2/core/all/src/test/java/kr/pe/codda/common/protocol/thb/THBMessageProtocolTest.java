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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.WrapBufferPool;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMiddleObjectForwarderIF;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.impl.message.ExceptionDeliveryRes.ExceptionDeliveryRes;
import kr.pe.codda.impl.message.ExceptionDeliveryRes.ExceptionDeliveryResEncoder;

public class THBMessageProtocolTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		JDKLoggerCustomFormatter formatter = new JDKLoggerCustomFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testM2S_basic() {
		Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

		int dataPacketBufferMaxCntPerMessage = 10;
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("utf-8"));

		ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
		WrapBufferPoolIF wrapBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 4096;
		int wrapBufferPoolSize = 100;

		try {
			wrapBufferPool = new WrapBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, wrapBufferPoolSize);
		} catch (Exception e) {
			log.log(Level.WARNING, "" + e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}

		THBMessageProtocol thbMessageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetFamily,
				wrapBufferPool);

		ExceptionDeliveryResEncoder selfExnEncoder = new ExceptionDeliveryResEncoder();

		// log.info("1");
		long beforeTime = 0;
		long afterTime = 0;

		int retryCount = 10000;

		int firstIndex = -1;
		int differentCount = 0;

		class ReceivedMessageForwarderImpl implements ReceivedMiddleObjectForwarderIF {
			private final MessageProtocolIF messageProtocol;
			private AbstractMessage receivedMessage = null;

			public ReceivedMessageForwarderImpl(MessageProtocolIF messageProtocol) {
				this.messageProtocol = messageProtocol;
			}

			private AbstractMessageDecoder getMessageDecoder(ClassLoader classloader, String messageID)
					throws DynamicClassCallException {
				AbstractMessageDecoder messageDecoder = null;

				String clientMessageCodecClassFullName = IOPartDynamicClassNameUtil
						.getClientMessageCodecClassFullName(messageID);
				try {
					Object retObject = CommonStaticUtil.createtNewInstance(classloader,
							clientMessageCodecClassFullName);

					if (!(retObject instanceof MessageCodecIF)) {
						String errorMessage = new StringBuilder().append("this instance of ")
								.append(clientMessageCodecClassFullName)
								.append(" class that was created by client classloader[").append(classloader.hashCode())
								.append("] class is not a instance of MessageCodecIF class").toString();
						throw new DynamicClassCallException(errorMessage);
					}

					MessageCodecIF messageCodec = (MessageCodecIF) retObject;

					messageDecoder = messageCodec.getMessageDecoder();
				} catch (DynamicClassCallException e) {
					throw e;
				} catch (Exception e) {
					String errorMessage = new StringBuilder(
							"unknwon error::fail to get the client message decoder of the output message[")
									.append(messageID).append("], errmsg=").append(e.getMessage()).toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, errorMessage, e);

					throw new DynamicClassCallException(errorMessage);
				}

				return messageDecoder;

			}

			@Override
			public void putReceivedMiddleObject(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
					throws InterruptedException {
				try {
					ClassLoader classloader = this.getClass().getClassLoader();

					AbstractMessageDecoder messageDecoder = getMessageDecoder(classloader, messageID);

					receivedMessage = messageProtocol.O2M(messageDecoder, mailboxID, mailID, messageID,
							readableMiddleObject);

				} catch (DynamicClassCallException e) {
					String errorMessage = new StringBuilder(
							"fail to get the client message decoder of the output message").append("mailboxID=")
									.append(mailboxID).append(", mailID=").append(mailID).append(", messageID=")
									.append(messageID).append("], errmsg=").append(e.getMessage()).toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, errorMessage);

					ExceptionDeliveryRes selfExnRes = new ExceptionDeliveryRes();
					selfExnRes.setMailboxID(mailboxID);
					selfExnRes.setMailID(mailID);
					selfExnRes.setErrorPlace(ExceptionDelivery.ErrorPlace.CLIENT);
					selfExnRes.setErrorType(ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class));

					selfExnRes.setErrorMessageID(messageID);
					selfExnRes.setErrorReason(errorMessage);

					receivedMessage = selfExnRes;
				} catch (BodyFormatException e) {

					String errorMessage = new StringBuilder(
							"fail to decode the var 'readableMiddleObject' of the output message").append("mailboxID=")
									.append(mailboxID).append(", mailID=").append(mailID).append(", messageID=")
									.append(messageID).append("], errmsg=").append("").append(e.getMessage())
									.toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, errorMessage);

					ExceptionDeliveryRes selfExnRes = new ExceptionDeliveryRes();
					selfExnRes.setMailboxID(mailboxID);
					selfExnRes.setMailID(mailID);
					selfExnRes.setErrorPlace(ExceptionDelivery.ErrorPlace.CLIENT);
					selfExnRes.setErrorType(ExceptionDelivery.ErrorType.valueOf(BodyFormatException.class));

					selfExnRes.setErrorMessageID(messageID);
					selfExnRes.setErrorReason(errorMessage);

					receivedMessage = selfExnRes;
				} catch (Exception e) {
					String errorMessage = new StringBuilder(
							"unknwon error::fail to get the client message decoder of the output message[")
									.append("mailboxID=").append(mailboxID).append(", mailID=").append(mailID)
									.append(", messageID=").append(messageID).append("], errmsg=")
									.append(e.getMessage()).toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, errorMessage);

					ExceptionDeliveryRes selfExnRes = new ExceptionDeliveryRes();
					selfExnRes.setMailboxID(mailboxID);
					selfExnRes.setMailID(mailID);
					selfExnRes.setErrorPlace(ExceptionDelivery.ErrorPlace.CLIENT);
					selfExnRes.setErrorType(ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class));

					selfExnRes.setErrorMessageID(messageID);
					selfExnRes.setErrorReason(errorMessage);

					receivedMessage = selfExnRes;
				}
			}

			public AbstractMessage getReceivedMessage() {
				return receivedMessage;
			}
		}

		ReceivedMessageForwarderImpl receivedMessageForwarder = new ReceivedMessageForwarderImpl(thbMessageProtocol);

		StringBuilder testStringBuilder = new StringBuilder();

		for (int i = 0; i < 2500; i++) {
			testStringBuilder.append("한글");
		}

		ExceptionDeliveryRes expectedThrowExceptionRes = new ExceptionDeliveryRes();
		expectedThrowExceptionRes.setErrorPlace(ExceptionDelivery.ErrorPlace.SERVER);
		expectedThrowExceptionRes.setErrorType(ExceptionDelivery.ErrorType.BodyFormatException);
		expectedThrowExceptionRes.setErrorMessageID("Echo");
		expectedThrowExceptionRes.setErrorReason(testStringBuilder.toString());

		expectedThrowExceptionRes.setMailboxID(1);
		expectedThrowExceptionRes.setMailID(3);

		beforeTime = System.nanoTime();

		for (int i = 0; i < retryCount; i++) {
			long beforeLocalTime = new Date().getTime();

			IncomingStream isr = new IncomingStream(streamCharsetFamily, dataPacketBufferMaxCntPerMessage, 
					wrapBufferPool);
			try {
				thbMessageProtocol.M2S(expectedThrowExceptionRes, selfExnEncoder, isr);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.log(Level.WARNING, "" + e.getMessage(), e);
				fail(errorMessage);
			}

			// log.info("2");

			// log.info("3");
			// FIXME!

			try {
				thbMessageProtocol.S2O(isr, receivedMessageForwarder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.log(Level.WARNING, "" + e.getMessage(), e);
				fail(errorMessage);
			}

			if (null == receivedMessageForwarder.getReceivedMessage()) {
				fail("추출한 출력 메시지가 없습니다");
			}

			try {
				AbstractMessage resObj = receivedMessageForwarder.getReceivedMessage();

				if (!(resObj instanceof ExceptionDeliveryRes)) {
					fail("resObj is not a instance of ThrowExceptionRes class");
				}

				ExceptionDeliveryRes acutalThrowExceptionRes = (ExceptionDeliveryRes) resObj;

				assertEquals("SelfExn 입력과 출력 메시지 비교", expectedThrowExceptionRes.toString(), acutalThrowExceptionRes.toString());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.log(Level.WARNING, "" + e.getMessage(), e);
				fail(errorMessage);
			}

			long afterLocalTime = new Date().getTime();
			if ((-1 == firstIndex) && (afterLocalTime == beforeLocalTime)) {
				firstIndex = i;
			}

			if (afterLocalTime != beforeLocalTime) {
				// log.info("case[{}]::afterLocalTime != beforeLocalTime", i);
				differentCount++;
			}
		}

		afterTime = System.nanoTime();

		long totalTime = afterTime - beforeTime;

		System.out.printf("%d 번 시간차=%d micro second, 평균=%d micro second, firstIndex=%d, differentCount=%d", retryCount,
				TimeUnit.MICROSECONDS.convert(totalTime, TimeUnit.NANOSECONDS),
				TimeUnit.MICROSECONDS.convert(totalTime / retryCount, TimeUnit.NANOSECONDS), firstIndex,
				differentCount);
		System.out.println();
	}

}
