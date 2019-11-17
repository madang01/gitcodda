package kr.pe.codda.common.protocol.dhb;

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
import kr.pe.codda.common.io.InputStreamResource;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPool;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageForwarderIF;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.CustomLogFormatter;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnResEncoder;

public class DHBMessageProtocolTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		CustomLogFormatter formatter = new CustomLogFormatter();
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
		StreamCharsetFamily scf = new StreamCharsetFamily(Charset.forName("utf-8"));

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

		DHBMessageProtocol dhbMessageProtocol = new DHBMessageProtocol(dataPacketBufferMaxCntPerMessage, scf,
				wrapBufferPool);

		SelfExnResEncoder selfExnEncoder = new SelfExnResEncoder();

		// log.info("1");
		long beforeTime = 0;
		long afterTime = 0;

		int retryCount = 10000;

		int firstIndex = -1;
		int differentCount = 0;

		class ReceivedMessageForwarderImpl implements ReceivedMessageForwarderIF {
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
					Object retObject = CommonStaticUtil.getNewObjectFromClassloader(classloader,
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
			public void putReceivedMessage(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
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

					SelfExnRes selfExnRes = new SelfExnRes();
					selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
					selfExnRes.messageHeaderInfo.mailID = mailID;
					selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
					selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(DynamicClassCallException.class));

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

					SelfExnRes selfExnRes = new SelfExnRes();
					selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
					selfExnRes.messageHeaderInfo.mailID = mailID;
					selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
					selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(BodyFormatException.class));

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

					SelfExnRes selfExnRes = new SelfExnRes();
					selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
					selfExnRes.messageHeaderInfo.mailID = mailID;
					selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
					selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(DynamicClassCallException.class));

					selfExnRes.setErrorMessageID(messageID);
					selfExnRes.setErrorReason(errorMessage);

					receivedMessage = selfExnRes;
				} finally {
					if (null != readableMiddleObject && readableMiddleObject instanceof StreamBuffer) {
						StreamBuffer sb = (StreamBuffer) readableMiddleObject;
						try {
							sb.close();
						} catch (Exception e) {
							String errorMessage = new StringBuilder()
									.append("fail to close the message body stream[messageID=").append(messageID)
									.append(", mailboxID=").append(mailboxID).append(", mailID=").append(mailID)
									.append("] body stream").toString();
							Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
							log.log(Level.WARNING, errorMessage, e);
						}
					}
				}
			}

			public AbstractMessage getReceivedMessage() {
				return receivedMessage;
			}
		}

		ReceivedMessageForwarderImpl receivedMessageForwarder = new ReceivedMessageForwarderImpl(dhbMessageProtocol);

		StringBuilder testStringBuilder = new StringBuilder();

		for (int i = 0; i < 2500; i++) {
			testStringBuilder.append("한글");
		}

		SelfExnRes expectedSelfExnRes = new SelfExnRes();
		expectedSelfExnRes.setErrorPlace(SelfExn.ErrorPlace.SERVER);
		expectedSelfExnRes.setErrorType(SelfExn.ErrorType.BodyFormatException);
		expectedSelfExnRes.setErrorMessageID("Echo");
		expectedSelfExnRes.setErrorReason(testStringBuilder.toString());

		expectedSelfExnRes.messageHeaderInfo.mailboxID = 1;
		expectedSelfExnRes.messageHeaderInfo.mailID = 3;

		beforeTime = System.nanoTime();

		for (int i = 0; i < retryCount; i++) {
			long beforeLocalTime = new Date().getTime();

			StreamBuffer sb = null;
			try {
				sb = dhbMessageProtocol.M2S(expectedSelfExnRes, selfExnEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.log(Level.WARNING, "" + e.getMessage(), e);
				fail(errorMessage);
			}

			// log.info("2");

			// log.info("3");
			// FIXME!

			InputStreamResource isr = (InputStreamResource) sb;
			isr.setPosition(isr.getLimit());
			isr.setLimit(isr.getCapacity());

			try {
				dhbMessageProtocol.S2O(isr, receivedMessageForwarder);
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

				if (!(resObj instanceof SelfExnRes)) {
					fail("resObj is not a instance of SelfExnRes class");
				}

				SelfExnRes acutalSelfExnRes = (SelfExnRes) resObj;

				assertEquals("SelfExn 입력과 출력 메시지 비교", expectedSelfExnRes.toString(), acutalSelfExnRes.toString());
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
