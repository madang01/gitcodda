package kr.pe.codda.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CustomLogFormatter;

public class StreamBufferTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// System.out.println("call setUpBeforeClass");

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
	public void testConstructor_theParameterDefaultCharsetIsNull() {
		try {
			new StreamBuffer(null, null, 1);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter defaultCharset is null";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testConstructor_theParameterWrapBufferPoolIsNull() {
		try {
			new StreamBuffer(Charset.defaultCharset(), null, 1);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter wrapBufferPool is null";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testConstructor_theParameterDataPacketBufferMaxCountIsLessThanOrEqualToZero() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 2048, 10);

		try {
			new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 0);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter dataPacketBufferMaxCount is less than or equal to zero";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}

		try {
			new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, -1);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter dataPacketBufferMaxCount is less than or equal to zero";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}

		try {
			new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, -1000000);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter dataPacketBufferMaxCount is less than or equal to zero";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testConstructor_ok() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1024, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 5);

		assertEquals(Charset.defaultCharset(), sb.getCharset());

		assertEquals(ByteOrder.LITTLE_ENDIAN, sb.getByteOder());

		assertEquals(1024 * 5, sb.getLimit());

		assertEquals(1024 * 5, sb.getCapacity());

		wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 2048, 10);

		sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		assertEquals(ByteOrder.BIG_ENDIAN, sb.getByteOder());

		assertEquals(2048 * 3, sb.getLimit());
		assertEquals(2048 * 3, sb.getCapacity());
	}

	@Test
	public void testSetPosition_lessThanZero() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		long newPosition = -1;
		try {
			sb.setPosition(newPosition);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = new StringBuilder().append("the parameter newPosition[").append(newPosition)
					.append("] is less than zero").toString();
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}

	}

	@Test
	public void testSetPosition_greaterThanLimit() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		long newPosition = sb.getLimit() + 1;
		try {
			sb.setPosition(newPosition);
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = new StringBuilder().append("the parameter newPosition[").append(newPosition)
					.append("] is greater than limit[").append(sb.getLimit()).append("]").toString();
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testSetPosition_ok() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		long newPosition = 0;
		try {
			sb.setPosition(newPosition);

			newPosition = 512;
			sb.setPosition(newPosition);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

	}

	@Test
	public void testSetLimit_lessThanZero() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		long newLimit = -1;
		try {
			sb.setLimit(newLimit);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = new StringBuilder().append("the parameter newLimit[").append(newLimit)
					.append("] is less than zero").toString();
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testSetLimit_lessThanPosition() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		long newPosition = 1;
		long newLimit = 0;
		try {
			sb.setPosition(newPosition);

			sb.setLimit(newLimit);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = new StringBuilder().append("the parameter newLimit[").append(newLimit)
					.append("] is less than position[").append(newPosition).append("]").toString();
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testSetLimit_greaterThanCapacity() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		long newLimit = sb.getCapacity() + 1;
		try {
			sb.setLimit(newLimit);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = new StringBuilder().append("the parameter newLimit[").append(newLimit)
					.append("] is greater than capacity[").append(sb.getCapacity()).append("]").toString();
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testSetLimit_ok() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);
		long newLimit = 0;
		try {
			sb.setLimit(newLimit);

			sb.setLimit(sb.getCapacity());
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutByte_최소최대중간값쓰고읽기() {
		byte values[] = { Byte.MIN_VALUE, -11, -1, 0, 1, 21, Byte.MAX_VALUE };

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

			for (byte value : values) {
				try {
					long oldPosition = sb.getPosition();
					sb.putByte(value);
					sb.checkValid();
					long newPosition = sb.getPosition();

					assertEquals(1L, newPosition - oldPosition);

					sb.setPosition(oldPosition);

					assertEquals(value, sb.getByte());

					sb.checkValid();

					newPosition = sb.getPosition();
					assertEquals(1L, newPosition - oldPosition);
				} catch (Exception e) {
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, "unknown error", e);
					fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
				}
			}
		}
	}

	@Test
	public void testPutByte_첫번째버퍼첫번째위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		try {

			long oldPosition = sb.getPosition();
			sb.putByte((byte) 0x21);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals((byte) 0x21, sb.getByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutByte_첫번째버퍼마지막위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			sb.setPosition(wrapBufferPool.getDataPacketBufferSize() - 1);

			long oldPosition = sb.getPosition();

			assertEquals(wrapBufferPool.getDataPacketBufferSize() - 1, oldPosition);

			sb.putByte((byte) 0x22);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals((byte) 0x22, sb.getByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutByte_중간버퍼첫번째위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			sb.setPosition(wrapBufferPool.getDataPacketBufferSize());

			long oldPosition = sb.getPosition();

			assertEquals(wrapBufferPool.getDataPacketBufferSize(), oldPosition);

			sb.putByte((byte) 0x23);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals((byte) 0x23, sb.getByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutByte_중간버퍼마지막위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			sb.setPosition(wrapBufferPool.getDataPacketBufferSize() * 2 - 1);

			long oldPosition = sb.getPosition();

			assertEquals(wrapBufferPool.getDataPacketBufferSize() * 2 - 1, oldPosition);

			sb.putByte((byte) 0x23);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals((byte) 0x23, sb.getByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutByte_마지막버퍼마지막위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			sb.setPosition(sb.getLimit() - 1);

			long oldPosition = sb.getPosition();

			assertEquals(sb.getLimit() - 1, oldPosition);

			sb.putByte((byte) 0x23);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals((byte) 0x23, sb.getByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutByte_꽉찬상태에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			sb.setPosition(sb.getLimit() - 1);

			long oldPosition = sb.getPosition();

			assertEquals(sb.getLimit() - 1, oldPosition);

			sb.putByte((byte) 0x23);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals((byte) 0x23, sb.getByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		try {
			sb.putByte((byte) 0x24);

			fail("no BufferOverflowException");
		} catch (BufferOverflowException e) {
			StackTraceElement se = e.getStackTrace()[0];
			/**
			 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
			 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
			 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
			 */
			assertEquals(StreamBuffer.class.getName(), se.getClassName());
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_음수() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		short value = -1;
		try {
			sb.putUnsignedByte(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_최대값초가() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		short value = CommonStaticFinalVars.UNSIGNED_BYTE_MAX + 1;
		try {
			sb.putUnsignedByte(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("] of unsigned byte").toString();

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_최소최대중간값() {
		short values[] = { 0, 1, 21, CommonStaticFinalVars.UNSIGNED_BYTE_MAX };

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

			for (short value : values) {

				try {
					long oldPosition = sb.getPosition();
					sb.putUnsignedByte(value);
					sb.checkValid();
					long newPosition = sb.getPosition();

					assertEquals(1L, newPosition - oldPosition);

					sb.setPosition(oldPosition);

					assertEquals(value, sb.getUnsignedByte());
					sb.checkValid();

					newPosition = sb.getPosition();
					assertEquals(1L, newPosition - oldPosition);

				} catch (Exception e) {
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, "unknown error", e);
					fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
				}
			}
		}
	}

	@Test
	public void testPutUnsignedByte_첫번째버퍼첫번째위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 2);

		try {

			short value = 0x21;
			long oldPosition = sb.getPosition();
			sb.putUnsignedByte(value);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals(value, sb.getUnsignedByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_첫번째버퍼마지막위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			long targetPosition = wrapBufferPool.getDataPacketBufferSize() - 1;
			short value = 0x21;

			sb.setPosition(targetPosition);

			long oldPosition = sb.getPosition();
			sb.putUnsignedByte(value);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals(value, sb.getUnsignedByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_중간버퍼첫번째위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			long targetPosition = wrapBufferPool.getDataPacketBufferSize();
			short value = 0x21;

			sb.setPosition(targetPosition);

			long oldPosition = sb.getPosition();
			sb.putUnsignedByte(value);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals(value, sb.getUnsignedByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_중간버퍼마지막위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			long targetPosition = wrapBufferPool.getDataPacketBufferSize() * 2 - 1;
			short value = 0x21;

			sb.setPosition(targetPosition);

			long oldPosition = sb.getPosition();
			sb.putUnsignedByte(value);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals(value, sb.getUnsignedByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_마지막버퍼첫번째위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			long targetPosition = wrapBufferPool.getDataPacketBufferSize() * 2;
			short value = 0x21;

			sb.setPosition(targetPosition);

			long oldPosition = sb.getPosition();
			sb.putUnsignedByte(value);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals(value, sb.getUnsignedByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_마지막버퍼마지막위치에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {

			long targetPosition = wrapBufferPool.getDataPacketBufferSize() * 3 - 1;
			short value = 0x21;

			sb.setPosition(targetPosition);

			long oldPosition = sb.getPosition();
			sb.putUnsignedByte(value);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals(value, sb.getUnsignedByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedByte_꽉찬상태에서쓰고읽기() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		short value = 0x21;

		try {

			long targetPosition = wrapBufferPool.getDataPacketBufferSize() * 3 - 1;

			sb.setPosition(targetPosition);

			long oldPosition = sb.getPosition();
			sb.putUnsignedByte(value);
			long newPosition = sb.getPosition();

			assertEquals(1L, newPosition - oldPosition);

			sb.setPosition(oldPosition);

			assertEquals(value, sb.getUnsignedByte());

			newPosition = sb.getPosition();
			assertEquals(1L, newPosition - oldPosition);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		try {
			sb.putUnsignedByte(value);

			fail("no BufferOverflowException");
		} catch (BufferOverflowException e) {
			StackTraceElement se = e.getStackTrace()[0];
			/**
			 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
			 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
			 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
			 */
			assertEquals(StreamBuffer.class.getName(), se.getClassName());
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutShort_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		short value = 21;

		/** 남은 용량이 2가 남은 상태에서 정상 처리되는지 확인 */
		try {
			long targetPosition = sb.getLimit() - 2;
			sb.setPosition(targetPosition);
			sb.putShort(value);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		
		for (long targetPosition=sb.getLimit() - 2 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			try {
				sb.setPosition(targetPosition);
				sb.putShort(value);

				fail("no BufferOverflowException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}

	}

	@Test
	public void testPutShort_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

			short[] values = { Short.MIN_VALUE, -11, -1, 0, 1, 21, Short.MAX_VALUE };

			try {
				for (short value : values) {
					for (int i = 0; i <= 2; i++) {
						long targetPosition = wrapBufferPool.getDataPacketBufferSize() - 2 + i;
						sb.setPosition(targetPosition);

						long oldPosition = sb.getPosition();
						sb.putShort(value);
						sb.checkValid();
						long newPosition = sb.getPosition();

						assertEquals(2L, newPosition - oldPosition);

						sb.setPosition(oldPosition);

						assertEquals(value, sb.getShort());
						sb.checkValid();

						newPosition = sb.getPosition();
						assertEquals(2L, newPosition - oldPosition);
					}
				}
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutUnsignedShort_음수() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		int value = -1;

		try {
			sb.putUnsignedShort(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedShort_최대값초과() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		int value = CommonStaticFinalVars.UNSIGNED_SHORT_MAX + 1;

		try {
			sb.putUnsignedShort(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_SHORT_MAX)
					.append("] of unsigned short").toString();

			// System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedShort_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);
		int value = 21;

		/** 남은 용량이 2가 남은 상태에서 정상 처리되는지 확인 */
		try {
			long targetPosition = sb.getLimit() - 2;
			sb.setPosition(targetPosition);
			sb.putUnsignedShort(value);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		
		for (long targetPosition=sb.getLimit() - 2 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			try {
				sb.setPosition(targetPosition);
				sb.putUnsignedShort(value);

				fail("no BufferOverflowException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}

	}

	@Test
	public void testPutUnsignedShort_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

			int[] values = { 0, 1, 21, CommonStaticFinalVars.UNSIGNED_SHORT_MAX };

			try {
				for (int value : values) {
					for (int i = 0; i <= 2; i++) {
						long targetPosition = wrapBufferPool.getDataPacketBufferSize() - 2 + i;
						sb.setPosition(targetPosition);

						long oldPosition = sb.getPosition();
						sb.putUnsignedShort(value);
						sb.checkValid();
						long newPosition = sb.getPosition();

						assertEquals(2L, newPosition - oldPosition);

						sb.setPosition(oldPosition);

						assertEquals(value, sb.getUnsignedShort());
						sb.checkValid();

						newPosition = sb.getPosition();
						assertEquals(2L, newPosition - oldPosition);
					}
				}
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutInt_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		int value = 21;

		/** 남은 용량이 4가 남은 상태에서 정상 처리되는지 확인 */
		try {
			long targetPosition = sb.getLimit() - 4;
			sb.setPosition(targetPosition);
			sb.putInt(value);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		
		for (long targetPosition=sb.getLimit() - 4 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			try {
				sb.setPosition(targetPosition);
				sb.putInt(value);

				fail("no BufferOverflowException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutInt_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

			int[] values = { Integer.MIN_VALUE, -11, -1, 0, 1, 21, Integer.MAX_VALUE };

			try {
				for (int value : values) {
					for (int i = 0; i <= 4; i++) {
						long targetPosition = wrapBufferPool.getDataPacketBufferSize() - 4 + i;
						sb.setPosition(targetPosition);

						long oldPosition = sb.getPosition();
						sb.putInt(value);
						sb.checkValid();
						long newPosition = sb.getPosition();

						assertEquals(4L, newPosition - oldPosition);

						sb.setPosition(oldPosition);

						assertEquals(value, sb.getInt());
						sb.checkValid();

						newPosition = sb.getPosition();
						assertEquals(4L, newPosition - oldPosition);
					}
				}
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutUnsignedInt_음수() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		long value = -1;

		try {
			sb.putUnsignedInt(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedInt_최대값초과() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		long value = CommonStaticFinalVars.UNSIGNED_INTEGER_MAX + 1;

		try {
			sb.putUnsignedInt(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
					.append("] of unsigned integer").toString();

			// System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutUnsignedInt_버퍼남은용량이4보다작은경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		long value = 21L;

		/** 남은 용량이 8 이 남은 상태에서 정상 처리되는지 확인 */
		try {
			long targetPosition = sb.getLimit() - 4;
			sb.setPosition(targetPosition);
			sb.putUnsignedInt(value);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		/** 남은 용량이 4 보다 작은 경우 예외를 던지는지 확인 */
		for (int i = 0; i <= 3; i++) {
			try {
				long targetPosition = sb.getLimit() - 3 + i;
				sb.setPosition(targetPosition);
				sb.putUnsignedInt(value);

				fail("no BufferOverflowException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutUnsignedInt_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

			long[] values = { 0, 1, 21, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX };

			try {
				for (long value : values) {
					for (int i = 0; i <= 4; i++) {
						long targetPosition = wrapBufferPool.getDataPacketBufferSize() - 4 + i;
						sb.setPosition(targetPosition);

						long oldPosition = sb.getPosition();
						sb.putUnsignedInt(value);
						sb.checkValid();
						long newPosition = sb.getPosition();

						assertEquals(4L, newPosition - oldPosition);

						sb.setPosition(oldPosition);

						assertEquals(value, sb.getUnsignedInt());
						sb.checkValid();

						newPosition = sb.getPosition();
						assertEquals(4L, newPosition - oldPosition);
					}
				}
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}

	}

	@Test
	public void testPutLong_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		long value = Integer.MAX_VALUE + 21;

		/** 남은 용량이 8이 남은 상태에서 정상 처리되는지 확인 */
		try {
			long targetPosition = sb.getLimit() - 8;
			sb.setPosition(targetPosition);
			sb.putLong(value);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		
		for (long targetPosition=sb.getLimit() - 8 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			try {
				sb.setPosition(targetPosition);
				sb.putLong(value);

				fail("no BufferOverflowException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutLong_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

			long[] values = { Long.MIN_VALUE, -11, -1, 0, 1, 21, Long.MAX_VALUE };

			try {
				for (long value : values) {
					for (int i = 0; i <= 8; i++) {
						long targetPosition = wrapBufferPool.getDataPacketBufferSize() - 8 + i;
						sb.setPosition(targetPosition);

						long oldPosition = sb.getPosition();
						sb.putLong(value);
						sb.checkValid();
						long newPosition = sb.getPosition();

						assertEquals(8L, newPosition - oldPosition);

						sb.setPosition(oldPosition);

						assertEquals(value, sb.getLong());
						sb.checkValid();

						newPosition = sb.getPosition();
						assertEquals(8L, newPosition - oldPosition);
					}
				}
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutBytes_전체바이트배열_TheParameterSrcIsNull() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {
			sb.putBytes((byte[]) null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter src is null";

			// System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutBytes_전체바이트배열_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		/** 배열 전체 */
		try {
			sb.setPosition(sb.getLimit() - sourceBytes.length);
			sb.putBytes(sourceBytes);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (int i = 1; i <= sourceBytes.length; i++) {
			long targetPostion = sb.getLimit() - sourceBytes.length + i;
			sb.setPosition(targetPostion);

			try {
				sb.putBytes(sourceBytes);

				fail("no IllegalArgumentException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutBytes_전체바이트배열_쓰기읽기() {
		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);

		int bufferSize = wrapBufferPool.getDataPacketBufferSize();

		for (int i = 0; i <= Math.max(bufferSize, sourceBytes.length); i++) {
			try {
				long targetPosition = i;
				sb.setPosition(targetPosition);

				long oldPosition = sb.getPosition();
				sb.putBytes(sourceBytes);
				sb.checkValid();
				long newPosition = sb.getPosition();

				assertEquals(sourceBytes.length, newPosition - oldPosition);

				sb.setPosition(oldPosition);

				byte[] acutalBytes = sb.getBytes(sourceBytes.length);
				sb.checkValid();

				Assert.assertArrayEquals(sourceBytes, acutalBytes);

				newPosition = sb.getPosition();
				assertEquals(sourceBytes.length, newPosition - oldPosition);

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutBytes_부분지정바이트배열_TheParameterSrcIsNull() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {
			sb.putBytes((byte[]) null, 0, 10);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter src is null";

			// System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutBytes_부분지정바이트배열__thePameterOffsetIsLessThanZero() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);
		int offset = -1;
		int length = 10;

		try {
			sb.putBytes(sourceBytes, offset, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter offset[").append(offset)
					.append("] is less than zero").toString();

			// System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutBytes_부분지정바이트배열__thePameterOffsetIsGreaterThanOrEqualToSourceByteArrayLength() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);
		int offset = sourceBytes.length;
		int length = 10;

		try {
			sb.putBytes(sourceBytes, offset, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter offset[").append(offset)
					.append("] is greater than and equal to the parameter src's length[").append(sourceBytes.length)
					.append("]").toString();

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutBytes_부분지정바이트배열__thePameterLengthIsLessThanZero() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);
		int offset = 0;
		int length = -1;

		try {
			sb.putBytes(sourceBytes, offset, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter length[").append(length)
					.append("] is less than zero").toString();

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutBytes_부분지정바이트배열_theSumOfThePameterOffsetAndthePameterLengthIsGreaterThanSourceByteArrayLength() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);
		int offset = 0;
		int length = sourceBytes.length + 1;

		try {
			sb.putBytes(sourceBytes, offset, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the sum[").append(offset + length)
					.append("] of the parameter offset[").append(offset).append("] and the parameter length[")
					.append(length).append("] is greater than the parameter src's length[").append(sourceBytes.length)
					.append("]").toString();

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutBytes_부분지정바이트배열_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);
		int offset = 0;
		int length = sourceBytes.length;

		/** 배열 부분 */
		try {
			sb.setPosition(sb.getLimit() - length);
			sb.putBytes(sourceBytes, offset, length);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (int i = 1; i <= length; i++) {
			long targetPostion = sb.getLimit() - length + i;
			sb.setPosition(targetPostion);

			try {
				sb.putBytes(sourceBytes, offset, length);

				fail("no IllegalArgumentException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutBytes_부분지정바이트배열_전체쓰기읽기() {
		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);

		int bufferSize = wrapBufferPool.getDataPacketBufferSize();

		for (int i = 0; i <= Math.max(bufferSize, sourceBytes.length); i++) {
			try {
				long targetPosition = i;
				sb.setPosition(targetPosition);

				long oldPosition = sb.getPosition();
				sb.putBytes(sourceBytes, 0, sourceBytes.length);
				sb.checkValid();
				long newPosition = sb.getPosition();

				assertEquals(sourceBytes.length, newPosition - oldPosition);

				sb.setPosition(oldPosition);

				byte[] acutalBytes = sb.getBytes(sourceBytes.length);
				sb.checkValid();

				Assert.assertArrayEquals(sourceBytes, acutalBytes);

				newPosition = sb.getPosition();
				assertEquals(sourceBytes.length, newPosition - oldPosition);

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutBytes_부분지정바이트배열_부분쓰기읽기() {
		byte[] sourceBytes = new byte[1200];
		int offset = 4;
		int length = 10;
		Random random = new Random();
		random.nextBytes(sourceBytes);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);

		int bufferSize = wrapBufferPool.getDataPacketBufferSize();

		for (int i = 0; i <= Math.max(bufferSize, length); i++) {
			try {
				long targetPosition = i;
				sb.setPosition(targetPosition);

				long oldPosition = sb.getPosition();
				sb.putBytes(sourceBytes, offset, length);
				sb.checkValid();
				long newPosition = sb.getPosition();

				assertEquals(length, newPosition - oldPosition);

				sb.setPosition(oldPosition);

				byte[] acutalBytes = sb.getBytes(length);
				sb.checkValid();

				newPosition = sb.getPosition();
				assertEquals(length, newPosition - oldPosition);

				for (int j = 0; j < length; j++) {
					if (acutalBytes[j] != sourceBytes[offset + j]) {
						String errorMessage = new StringBuilder().append("expected bytes array[").append(offset + j)
								.append("]'s value[").append(sourceBytes[offset + j])
								.append("] is different from acutal bytes[").append(j).append("]'s value[")
								.append(acutalBytes[j]).append("]").toString();
						fail(errorMessage);
					}
				}

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutBytes_바이트버퍼_TheParameterSrcIsNull() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		try {
			sb.putBytes((ByteBuffer) null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter src is null";

			// System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testPutBytes_바이트버퍼_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 3);

		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		/** ByteBuffer */
		ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceBytes);

		try {
			sb.setPosition(sb.getLimit() - sourceByteBuffer.remaining());
			sb.putBytes(sourceByteBuffer);
			sourceByteBuffer.rewind();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		// for (int i = 1; i <= sourceByteBuffer.remaining(); i++) {
		for (long targetPosition=sb.getLimit() - sourceByteBuffer.remaining() + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			sb.setPosition(targetPosition);

			try {
				sb.putBytes(sourceByteBuffer);
				sourceByteBuffer.rewind();

				fail("no IllegalArgumentException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutBytes_바이트버퍼_전체쓰기읽기() {
		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceBytes);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);

		int bufferSize = wrapBufferPool.getDataPacketBufferSize();

		for (int i = 0; i <= Math.max(bufferSize, sourceBytes.length); i++) {
			try {
				long targetPosition = i;
				sb.setPosition(targetPosition);

				long oldPosition = sb.getPosition();

				sb.putBytes(sourceByteBuffer);
				sourceByteBuffer.rewind();

				sb.checkValid();
				long newPosition = sb.getPosition();

				assertEquals(sourceBytes.length, newPosition - oldPosition);

				sb.setPosition(oldPosition);

				byte[] acutalBytes = sb.getBytes(sourceBytes.length);
				sb.checkValid();

				Assert.assertArrayEquals(sourceBytes, acutalBytes);

				newPosition = sb.getPosition();
				assertEquals(sourceBytes.length, newPosition - oldPosition);

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testPutBytes_바이트버퍼_부분쓰기읽기() {
		byte[] sourceBytes = new byte[1200];
		final int offset = 4;
		final int length = 10;
		Random random = new Random();
		random.nextBytes(sourceBytes);

		ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceBytes);
		sourceByteBuffer.position(offset);
		sourceByteBuffer.limit(offset + length);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);

		int bufferSize = wrapBufferPool.getDataPacketBufferSize();

		for (int i = 0; i <= Math.max(bufferSize, length); i++) {
			try {
				long targetPosition = i;
				sb.setPosition(targetPosition);

				long oldPosition = sb.getPosition();
				sb.putBytes(sourceByteBuffer);
				sourceByteBuffer.position(offset);
				sourceByteBuffer.limit(offset + length);
				sb.checkValid();
				long newPosition = sb.getPosition();

				assertEquals(length, newPosition - oldPosition);

				sb.setPosition(oldPosition);

				byte[] acutalBytes = sb.getBytes(length);
				sb.checkValid();

				newPosition = sb.getPosition();
				assertEquals(length, newPosition - oldPosition);

				for (int j = 0; j < length; j++) {
					if (acutalBytes[j] != sourceBytes[offset + j]) {
						String errorMessage = new StringBuilder().append("expected bytes array[").append(offset + j)
								.append("]'s value[").append(sourceBytes[offset + j])
								.append("] is different from acutal bytes[").append(j).append("]'s value[")
								.append(acutalBytes[j]).append("]").toString();
						fail(errorMessage);
					}
				}

			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	/**
	 * 속도 비교 결과 2 byte는 putByte 가 유리하고 4 byte 이상 부터는 putBytes 가 유리함.
	 * 
	 * ==> 2 byte, 버퍼 안쪽 putBytes::1000000 번, 총 소요 시간=[31] ms putByte::1000000 번, 총
	 * 소요 시간=[34] ms
	 * 
	 * ==> 2 byte, 버퍼에 걸치기 putBytes::1000000 번, 총 소요 시간=[43] ms putByte::1000000 번,
	 * 총 소요 시간=[34] ms
	 * 
	 * ==> 4 byte, 버퍼 안쪽 putBytes::1000000 번, 총 소요 시간=[29] ms putByte::1000000 번, 총
	 * 소요 시간=[57] ms
	 * 
	 * ==> 4 byte, 버퍼에 걸치::3byte+1byte putBytes::1000000 번, 총 소요 시간=[43] ms
	 * putByte::1000000 번, 총 소요 시간=[58] ms
	 * 
	 * 
	 * ==> 4 byte, 버퍼에 걸치::2byte+2byte putBytes::1000000 번, 총 소요 시간=[45] ms
	 * putByte::1000000 번, 총 소요 시간=[58] ms
	 * 
	 * ==> 4 byte, 버퍼에 걸치::1byte+3byte putBytes::1000000 번, 총 소요 시간=[45] ms
	 * putByte::1000000 번, 총 소요 시간=[59] ms
	 */

	@Test
	public void test속도비교_putBytesVsPutBytes2() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);

		byte[] sourceBytes = new byte[4];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		int retryCount = 1000000;
		long startPosition = 0;

		long startTime, endTime;

		startTime = System.currentTimeMillis();

		for (int i = 0; i < retryCount; i++) {
			try {
				sb.setPosition(startPosition);
				sb.putBytes(sourceBytes);
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}

		endTime = System.currentTimeMillis();

		long elapsedTime = endTime - startTime;

		System.out.printf("putBytes::%d 번, 총 소요 시간=[%d] ms", retryCount, elapsedTime);
		System.out.println();

		startTime = System.currentTimeMillis();

		for (int i = 0; i < retryCount; i++) {
			try {
				sb.setPosition(startPosition);
				sb.putBytes2(sourceBytes);
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}

		endTime = System.currentTimeMillis();

		elapsedTime = endTime - startTime;

		System.out.printf("putByte::%d 번, 총 소요 시간=[%d] ms", retryCount, elapsedTime);
		System.out.println();
	}
	
	@Test
	public void testPutFixedLengthString_theParameterFixedLengthIsLessThanZero() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);
		
		
		int fixedLength = -1;
		String src = "안녕하세요";
		CharsetEncoder wantedCharsetEncoder = Charset.defaultCharset().newEncoder();
		
		try {
			sb.putFixedLengthString(fixedLength, src, wantedCharsetEncoder);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter fixedLength[")
					.append(fixedLength)
					.append("] is less than zero").toString();

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}
	
	@Test
	public void testPutFixedLengthString_theParameterSrcIsNull() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);
		
		
		int fixedLength = 20;
		String src = null;
		CharsetEncoder wantedCharsetEncoder = Charset.defaultCharset().newEncoder();
		
		try {
			sb.putFixedLengthString(fixedLength, src, wantedCharsetEncoder);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter src is null";
			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}
	
	@Test
	public void testPutFixedLengthString_theParameterWantedCharsetEncoderIsNull() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);
		
		
		int fixedLength = 20;
		String src = "한글사랑";
		CharsetEncoder wantedCharsetEncoder = null;
		
		try {
			sb.putFixedLengthString(fixedLength, src, wantedCharsetEncoder);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharsetEncoder is null";
			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}
	
	@Test
	public void testPutFixedLengthString_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 20);
		
		
		int fixedLength = 20;
		String src = "한글사랑";
		Charset testCharset = Charset.forName("EUC-KR");
		
		try {
			sb.setPosition(sb.getLimit() - fixedLength);
			sb.putFixedLengthString(fixedLength, src, testCharset.newEncoder());
			
			sb.setPosition(sb.getLimit() - fixedLength);
			String acutalValue = sb.getFixedLengthString(fixedLength, testCharset.newDecoder());
			
			assertEquals(src.trim(), acutalValue.trim());
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		
		for (long targetPosition=sb.getLimit() - fixedLength + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			try {
				sb.setPosition(targetPosition);
				sb.putFixedLengthString(fixedLength, src, testCharset.newEncoder());
				
				fail("no BufferOverflowException");
			} catch (BufferOverflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}
	
	@Test
	public void testPutFixedLengthString_OK() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 20);
		
		
		int fixedLength = 20;
		String src = "한글사랑";
		Charset testCharset = Charset.forName("EUC-KR");
		
		try {
			sb.setPosition(sb.getLimit() - fixedLength);
			sb.putFixedLengthString(fixedLength, src, testCharset.newEncoder());
			
			sb.setPosition(sb.getLimit() - fixedLength);
			String acutalValue = sb.getFixedLengthString(fixedLength, testCharset.newDecoder());
			
			assertEquals(src.trim(), acutalValue.trim());
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}

	@Test
	public void testgetByte_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);
		try {
			sb.setPosition(sb.getLimit() - 1);
			sb.getByte();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		try {
			sb.getByte();
			fail("no BufferUnderflowException");
		} catch (BufferUnderflowException e) {
			StackTraceElement se = e.getStackTrace()[0];
			/**
			 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
			 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
			 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
			 */
			assertEquals(StreamBuffer.class.getName(), se.getClassName());
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}
	
	@Test
	public void testGetUnsignedByte_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);
		try {
			sb.setPosition(sb.getLimit() - 1);
			sb.getUnsignedByte();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		try {
			sb.getUnsignedByte();
			fail("no BufferUnderflowException");
		} catch (BufferUnderflowException e) {
			StackTraceElement se = e.getStackTrace()[0];
			/**
			 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
			 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
			 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
			 */
			assertEquals(StreamBuffer.class.getName(), se.getClassName());
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}
	
	@Test
	public void testGetShort_버퍼에남은용량이부족한경우() {
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(Charset.defaultCharset(), wrapBufferPool, 10);
		try {
			sb.setPosition(sb.getLimit() - 2);
			sb.getShort();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		for (long targetPosition=sb.getLimit() - 2 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			sb.setPosition(targetPosition);
			
			try {
				sb.getShort();
				fail("no BufferUnderflowException");
			} catch (BufferUnderflowException e) {
				StackTraceElement se = e.getStackTrace()[0];
				/**
				 * 참고) BufferOverflowException 는 (1) 읽기 혹은 쓰기를 할 만큼의 여유 공간 검사를 수행할때 (2)
				 * ByteBuffer 조작할때 발생한다. 하여 예외를 던진 클래스가 테스트 대상 클래스라면 읽기 혹은 쓰기 할 만큼의 여유 공간 검사때 던진
				 * 예외이고 그외의 경우면 ByteBuffer 조작했을때임을 알 수 있다.
				 */
				assertEquals(StreamBuffer.class.getName(), se.getClassName());
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
		
	}
}
