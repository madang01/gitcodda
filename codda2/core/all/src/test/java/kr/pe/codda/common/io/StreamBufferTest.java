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

package kr.pe.codda.common.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.junit.Ignore;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.HexUtil;

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
	public void testStreamBuffer_theParameterStreamCharsetFamilyIsNull() {
		try {
			new StreamBuffer(null, 1, null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter streamCharsetFamily is null";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testStreamBuffer_theParameterWrapBufferPoolIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		try {
			new StreamBuffer(streamCharsetFamily, 1, null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter wrapBufferPool is null";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testStreamBuffer_theParameterDataPacketBufferMaxCountIsLessThanOrEqualToZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 2048, 10);

		try {
			new StreamBuffer(streamCharsetFamily, 0, wrapBufferPool);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter maxOfWrapBuffer is less than or equal to zero";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}

		try {
			new StreamBuffer(streamCharsetFamily, -1, wrapBufferPool);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter maxOfWrapBuffer is less than or equal to zero";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}

		try {
			new StreamBuffer(streamCharsetFamily, -1000000, wrapBufferPool);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expecedErrorMessage = "the parameter maxOfWrapBuffer is less than or equal to zero";
			String actualErrorMessage = e.getMessage();
			assertEquals(expecedErrorMessage, actualErrorMessage);
		}
	}

	@Test
	public void testStreamBuffer_ok() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1024, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 5, wrapBufferPool);

		assertEquals(Charset.defaultCharset(), sb.getCharset());

		assertEquals(ByteOrder.LITTLE_ENDIAN, sb.getByteOder());

		assertEquals(1024 * 5, sb.getLimit());

		assertEquals(1024 * 5, sb.getCapacity());

		wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 2048, 10);

		sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

		assertEquals(ByteOrder.BIG_ENDIAN, sb.getByteOder());

		assertEquals(2048 * 3, sb.getLimit());
		assertEquals(2048 * 3, sb.getCapacity());
	}

	@Test
	public void testSetPosition_lessThanZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);
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
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		byte values[] = { Byte.MIN_VALUE, -11, -1, 0, 1, 21, Byte.MAX_VALUE };

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
			
			sb.releaseAllWrapBuffers();
		}
	}

	@Test
	public void testPutByte_첫번째버퍼첫번째위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutByte_첫번째버퍼마지막위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutByte_중간버퍼첫번째위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutByte_중간버퍼마지막위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutByte_마지막버퍼마지막위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutByte_꽉찬상태에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_음수() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_최대값초가() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_최소최대중간값() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		short values[] = { 0, 1, 21, CommonStaticFinalVars.UNSIGNED_BYTE_MAX };

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
			
			sb.releaseAllWrapBuffers();
		}
	}

	@Test
	public void testPutUnsignedByte_첫번째버퍼첫번째위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_첫번째버퍼마지막위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_중간버퍼첫번째위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_중간버퍼마지막위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_마지막버퍼첫번째위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_마지막버퍼마지막위치에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedByte_꽉찬상태에서쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutShort_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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

		for (long targetPosition = sb.getLimit() - 2 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
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

		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutShort_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
			
			sb.releaseAllWrapBuffers();
		}
	}

	@Test
	public void testPutUnsignedShort_음수() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedShort_최대값초과() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedShort_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);
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

		for (long targetPosition = sb.getLimit() - 2 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
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
		
		sb.releaseAllWrapBuffers();

	}

	@Test
	public void testPutUnsignedShort_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
			
			sb.releaseAllWrapBuffers();
		}		
	}

	@Test
	public void testPutInt_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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

		for (long targetPosition = sb.getLimit() - 4 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutInt_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
			
			sb.releaseAllWrapBuffers();
		}
	}

	@Test
	public void testPutUnsignedInt_음수() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedInt_최대값초과() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedInt_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUnsignedInt_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
			
			sb.releaseAllWrapBuffers();
		}

	}

	@Test
	public void testPutLong_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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

		for (long targetPosition = sb.getLimit() - 8 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutLong_최소최대포함한값들로버퍼끝에서다름버퍼까지쓰고읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, streamByteOrder, 512, 10);

			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
			
			sb.releaseAllWrapBuffers();
		}
	}

	@Test
	public void testPutBytes_전체바이트배열_TheParameterSrcIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_전체바이트배열_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_전체바이트배열_쓰기읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_부분지정바이트배열_TheParameterSrcIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_부분지정바이트배열__thePameterOffsetIsLessThanZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_부분지정바이트배열__thePameterOffsetIsGreaterThanOrEqualToSourceByteArrayLength() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_부분지정바이트배열__thePameterLengthIsLessThanZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_부분지정바이트배열_theSumOfThePameterOffsetAndthePameterLengthIsGreaterThanSourceByteArrayLength() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_부분지정바이트배열_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_부분지정바이트배열_전체쓰기읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_부분지정바이트배열_부분쓰기읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		byte[] sourceBytes = new byte[1200];
		int offset = 4;
		int length = 10;
		Random random = new Random();
		random.nextBytes(sourceBytes);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_바이트버퍼_TheParameterSrcIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_바이트버퍼_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

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
		for (long targetPosition = sb.getLimit() - sourceByteBuffer.remaining() + 1; targetPosition <= sb
				.getLimit(); targetPosition++) {
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_바이트버퍼_전체쓰기읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);

		ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceBytes);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutBytes_바이트버퍼_부분쓰기읽기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		byte[] sourceBytes = new byte[1200];
		final int offset = 4;
		final int length = 10;
		Random random = new Random();
		random.nextBytes(sourceBytes);

		ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceBytes);
		sourceByteBuffer.position(offset);
		sourceByteBuffer.limit(offset + length);

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
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
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutFixedLengthString_theParameterFixedLengthIsLessThanZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		int fixedLength = -1;
		String src = "안녕하세요";
		CharsetEncoder wantedCharsetEncoder = Charset.defaultCharset().newEncoder();

		try {
			sb.putFixedLengthString(fixedLength, src, wantedCharsetEncoder);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter fixedLength[").append(fixedLength)
					.append("] is less than zero").toString();

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutFixedLengthString_theParameterSrcIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutFixedLengthString_theParameterWantedCharsetEncoderIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutFixedLengthString_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

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

		for (long targetPosition = sb.getLimit() - fixedLength + 1; targetPosition <= sb.getLimit(); targetPosition++) {
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutFixedLengthString_OK() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 100, wrapBufferPool);

		int fixedLength = 30;
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutAllString_theParameterSrcIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

		Charset testCharset = Charset.forName("EUC-KR");

		try {
			sb.putAllString(null, testCharset);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutAllString_theParameterWantedCharsetIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

		try {
			sb.putAllString("한글사랑", null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutAllString_OK() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

		Charset wantedCharset = Charset.forName("EUC-KR");

		String expectedValue = "한글사랑";

		try {
			sb.putAllString(expectedValue, wantedCharset);
			long newLimit = sb.getPosition();

			sb.setPosition(0);
			sb.setLimit(newLimit);
			String acutalValue = sb.getAllString(wantedCharset);

			assertEquals(expectedValue, acutalValue);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}


	@Test
	public void testPutUBPascalString_theParameterSrcIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

		try {
			sb.putUBPascalString(null, null);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUBPascalString_theParameterWantedCharsetIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

		try {
			sb.putUBPascalString("한글사랑", null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUBPascalString_OK() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

		Charset wantedCharset = Charset.forName("EUC-KR");

		String expectedValue = "한글사랑";

		try {
			sb.putUBPascalString(expectedValue, wantedCharset);

			sb.setPosition(0);
			String acutalValue = sb.getUBPascalString(wantedCharset);

			assertEquals(expectedValue, acutalValue);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUSPascalString_theParameterSrcIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

		try {
			sb.putUSPascalString(null, null);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUSPascalString_theParameterWantedCharsetIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

		try {
			sb.putUSPascalString("한글사랑", null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutUSPascalString_OK() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

		Charset wantedCharset = Charset.forName("EUC-KR");

		String expectedValue = "한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑";

		try {
			sb.putUSPascalString(expectedValue, wantedCharset);

			System.out.printf("문자열 bytes=%d", sb.getPosition());
			System.out.println();

			sb.setPosition(0);
			String acutalValue = sb.getUSPascalString(wantedCharset);

			assertEquals(expectedValue, acutalValue);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutSIPascalString_theParameterSrcIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

		try {
			sb.putSIPascalString(null, null);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutSIPascalString_theParameterWantedCharsetIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 20, wrapBufferPool);

		try {
			sb.putSIPascalString("한글사랑", null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testPutSIPascalString_OK() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);

		Charset wantedCharset = Charset.forName("EUC-KR");

		String expectedValue = "한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑한글사랑";

		try {
			sb.putSIPascalString(expectedValue, wantedCharset);

			System.out.printf("문자열 bytes=%d", sb.getPosition());
			System.out.println();

			sb.setPosition(0);
			String acutalValue = sb.getSIPascalString(wantedCharset);

			assertEquals(expectedValue, acutalValue);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testgetByte_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetUnsignedByte_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetShort_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
		try {
			sb.setPosition(sb.getLimit() - 2);
			sb.getShort();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long targetPosition = sb.getLimit() - 2 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetUnsignedShort_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
		try {
			sb.setPosition(sb.getLimit() - 2);
			sb.getUnsignedShort();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long targetPosition = sb.getLimit() - 2 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			sb.setPosition(targetPosition);

			try {
				sb.getUnsignedShort();
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetInt_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
		try {
			sb.setPosition(sb.getLimit() - 4);
			sb.getInt();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long targetPosition = sb.getLimit() - 4 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			sb.setPosition(targetPosition);

			try {
				sb.getInt();
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetUnsignedInt_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
		try {
			sb.setPosition(sb.getLimit() - 4);
			sb.getUnsignedInt();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long targetPosition = sb.getLimit() - 4 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			sb.setPosition(targetPosition);

			try {
				sb.getUnsignedInt();
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetLong_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
		try {
			sb.setPosition(sb.getLimit() - 8);
			sb.getLong();
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long targetPosition = sb.getLimit() - 8 + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			sb.setPosition(targetPosition);

			try {
				sb.getLong();
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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetBytes_theParameterLengthIsLessThanZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		int length = -1;

		try {
			sb.getBytes(0);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		try {
			sb.getBytes(length);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetBytes_목적지지정_theParameterDstIsNull() {
		
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		final int length = 10;

		try {
			
			sb.getBytes(null, 17, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter dst is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}
	
	@Test
	public void testGetBytes_목적지지정_theParameterOffsetIsLessThanZero() {		
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		final int length = 10;
		final int offset = -1;
		byte[] dst = new byte[1024];

		try {			
			sb.getBytes(dst, offset, length);
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter offset[").append(offset)
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
	public void testGetBytes_목적지지정_theParameterOffsetIsEqualToLengthOfTheParameterDst() {
		
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		byte[] dst = new byte[1024];
		final int length = 10;
		final int offset = dst.length;
		
		try {
			
			sb.getBytes(dst, offset, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter offset[").append(offset)
					.append("] is greater than or equal to the parameter dst's length[")
					.append(dst.length)
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
	public void testGetBytes_목적지지정_theParameterLengthIsLessThanZero() {
		
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		byte[] dst = new byte[1024];
		final int length = -1;
		final int offset = dst.length - 1;
		
		try {
			
			sb.getBytes(dst, offset, length);

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
	public void testGetBytes_목적지지정_theSumOfParameterOffsetAndParameterLengthIsGreaterThanLengthOfTheParameterDst() {
		
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		byte[] dst = new byte[1024];
		final int length = 2;
		final int offset = dst.length - 1;
		
		try {
			
			sb.getBytes(dst, offset, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String acutalErrorMessage = e.getMessage();
			long sumOfOffsetAndLength = ((long) offset + length);
			String expectedErrorMessage = new StringBuilder().append("the sum[").append(sumOfOffsetAndLength)
					.append("] of the parameter offset[").append(offset).append("] and the parameter length[")
					.append(length).append("] is greater than the parameter dst's length[").append(dst.length)
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
	public void testGetBytes_목적지지정_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		byte[] dst = new byte[1024];
		final int length = 10;
		final int offset = 17;
		
		try {
			sb.setPosition(sb.getLimit() - length);
			sb.getBytes(dst, offset, length);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		for (int i=0; i < length; i++) {
			try {
				sb.setPosition(sb.getLimit() - length + i + 1);
				sb.getBytes(dst, offset, length);

				fail("no BufferUnderflowException");
			} catch (BufferUnderflowException e) {
				System.out.printf("BufferUnderflowException::sb.remaining()=%d, length=%d", sb.remaining(), length);
				System.out.println();
				
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
	public void testGetBytes_목적지지정_정상_부분() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		Random random = new Random();
		
		byte[] src = new byte[1024];
		
		byte[] dst = new byte[1024];
		final int length = 10;
		final int offset = 17;
		
		
		random.nextBytes(src);
		
		try {
			sb.putBytes(src, offset, length);
			
			sb.setPosition(0);
			sb.getBytes(dst, offset, length);
		
			for (int i=0, j=offset; i < length; i++, j++) {
				assertEquals(i+" 번째 원소 비교", src[j], dst[j]);
			}
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}
	
	@Test
	public void testGetBytes_목적지지정_정상_전체() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		Random random = new Random();
		
		byte[] src = new byte[1024];
		
		byte[] dst = new byte[1024];
		final int length = 1024;
		final int offset = 0;
		
		
		random.nextBytes(src);
		
		try {
			sb.putBytes(src, offset, length);
			
			sb.setPosition(0);
			sb.getBytes(dst, offset, length);			
			
			assertArrayEquals(src, dst);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
	}
	
	@Test
	public void testGetBytes_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		final int length = 10;

		try {
			sb.setPosition(sb.getLimit() - length);
			byte[] result = sb.getBytes(length);

			assertEquals(length, result.length);

		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long targetPosition = sb.getLimit() - length + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			sb.setPosition(targetPosition);

			try {
				sb.getBytes(length);

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetFixedLengthString_theParameterFixedLengthIsLessThanZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		int fixedLength = -1;

		try {
			sb.getFixedLengthString(fixedLength, null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("the parameter fixedLength[").append(fixedLength)
					.append("] is less than zero").toString();

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetFixedLengthString_theParameterWantedCharsetDecoderIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		int fixedLength = 1;

		try {
			sb.getFixedLengthString(fixedLength, null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharsetDecoder is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetFixedLengthString_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());

		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		Charset wantedCharset = Charset.forName("EUC-KR");

		final int fixedLength = 10;

		try {
			sb.setPosition(sb.getLimit() - fixedLength);
			sb.getFixedLengthString(fixedLength, wantedCharset.newDecoder());
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long targetPosition = sb.getLimit() - fixedLength + 1; targetPosition <= sb.getLimit(); targetPosition++) {
			sb.setPosition(targetPosition);

			try {
				sb.getFixedLengthString(fixedLength, wantedCharset.newDecoder());

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
		
		sb.releaseAllWrapBuffers();
	}

	@Test
	public void testGetAllString_theParameterWantedCharsetIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		try {
			sb.getAllString(null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}
	
	@Ignore
	public void testGetAllString_버퍼용량이Integer보다큰경우예외던지는지확인() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 4096, 524300);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 524288, wrapBufferPool);

		try {
			sb.getAllString();

			fail("no IllegalStateException");
		} catch (IllegalStateException e) {
			String acutalErrorMessage = e.getMessage();
			// String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			// assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}
	
	
	@Test
	public void testGetUBPascalString_theParameterWantedCharsetIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		try {
			sb.getUBPascalString(null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}
	
	@Test
	public void testGetUBPascalString_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
		
		String expectedValue = "ab";
		Charset wantedCharset = Charset.forName("UTF-8");
		
		int length = 1 + expectedValue.getBytes(wantedCharset).length; 
		long newPosition = sb.getLimit() - length;
		
		try {
			sb.setPosition(newPosition);
			sb.putUBPascalString(expectedValue, wantedCharset);
			
			sb.setPosition(newPosition);			
			String actualValue = sb.getUBPascalString(wantedCharset);
			
			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long newLimit = sb.getCapacity() - 1; newLimit >= newPosition; newLimit--) {
			sb.setPosition(newPosition);
			sb.setLimit(newLimit);

			try {
				sb.getUBPascalString(wantedCharset);

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
		
		sb.releaseAllWrapBuffers();
	}
	
	@Test
	public void testGetUSPascalString_theParameterWantedCharsetIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		try {
			sb.getUSPascalString(null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
	}
	
	@Test
	public void testGetUSPascalString_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
		
		String expectedValue = "ab";
		Charset wantedCharset = Charset.forName("UTF-8");
		
		int length = 2 + expectedValue.getBytes(wantedCharset).length; 
		long newPosition = sb.getLimit() - length;
		
		try {
			sb.setPosition(newPosition);
			sb.putUSPascalString(expectedValue, wantedCharset);
			
			sb.setPosition(newPosition);			
			String actualValue = sb.getUSPascalString(wantedCharset);
			
			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long newLimit = sb.getCapacity() - 1; newLimit >= newPosition; newLimit--) {
			sb.setPosition(newPosition);
			sb.setLimit(newLimit);

			try {
				sb.getUSPascalString(wantedCharset);

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
		
		sb.releaseAllWrapBuffers();
	}
	
	@Test
	public void testGetSIPascalString_theParameterWantedCharsetIsNull() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);

		try {
			sb.getSIPascalString(null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter wantedCharset is null";

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}
		
		sb.releaseAllWrapBuffers();
		
	}
	
	@Test
	public void testGetSIPascalString_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 1000);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 10, wrapBufferPool);
		
		String expectedValue = "ab";
		Charset wantedCharset = Charset.forName("UTF-8");
		
		int length = 4 + expectedValue.getBytes(wantedCharset).length; 
		long newPosition = sb.getLimit() - length;
		
		try {
			sb.setPosition(newPosition);
			sb.putSIPascalString(expectedValue, wantedCharset);
			
			sb.setPosition(newPosition);			
			String actualValue = sb.getSIPascalString(wantedCharset);
			
			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		for (long newLimit = sb.getCapacity() - 1; newLimit >= newPosition; newLimit--) {
			sb.setPosition(newPosition);
			sb.setLimit(newLimit);

			try {
				sb.getSIPascalString(wantedCharset);

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
		
		sb.releaseAllWrapBuffers();
	}
	
	@Test
	public void testSkip_theParameterNIsLessThanOrEqualToZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);
		
		try {
			
			try {
				sb.skip(0);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String expecedErrorMessage = new StringBuilder().append("the parameter n[").append(0)
						.append("] is less than or equal to zero").toString();
				String actualErrorMessage = e.getMessage();
				assertEquals(expecedErrorMessage, actualErrorMessage);
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
			
			try {
				sb.skip(-1);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String expecedErrorMessage = new StringBuilder().append("the parameter n[").append(-1)
						.append("] is less than or equal to zero").toString();
				String actualErrorMessage = e.getMessage();
				assertEquals(expecedErrorMessage, actualErrorMessage);
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		} finally {
			sb.releaseAllWrapBuffers();
		}		
	}
	
	@Test
	public void testSkip_버퍼에남은용량이부족한경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 2, wrapBufferPool);
		
		try {
			
			try {
				sb.skip(sb.getLimit());
				
				sb.setPosition(0);
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
			
			try {
				sb.skip(sb.getLimit() + 1);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String expecedErrorMessage = new StringBuilder().append("the parameter n[").append(sb.getLimit()+1)
						.append("] is greater than remaing bytes[")
						.append(sb.getLimit())
						.append("]").toString();
				String actualErrorMessage = e.getMessage();
				assertEquals(expecedErrorMessage, actualErrorMessage);
				
				System.out.println(actualErrorMessage);
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		} finally {
			sb.releaseAllWrapBuffers();
		}		
	}
	
	@Test
	public void testGetMD5WithoutChange_버퍼3개중2개에꽉찬() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		// Charset wantedCharset = Charset.forName("UTF-8");
		
		byte[] sourceBytes = new byte[512*2];
		Random random = new Random();
		random.nextBytes(sourceBytes);
		
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		md5.reset();
		md5.update(sourceBytes);
		
		byte[] expectedValues = md5.digest();
		
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);
		
		try {
			
			try {				
				long newPosition = sb.getPosition();
				sb.putBytes(sourceBytes);
				sb.putByte((byte)19);
				
				sb.setPosition(newPosition);
				byte[] actualValues = sb.getMD5WithoutChange(sourceBytes.length);
				
				assertArrayEquals(expectedValues, actualValues);
				
				sb.checkValid();
				
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}			
		} finally {
			sb.releaseAllWrapBuffers();
		}	
	}
	
	@Test
	public void testGetMD5WithoutChange_버퍼3개중앞뒤로중간에걸친() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		
		byte[] sourceBytes = new byte[1200];
		Random random = new Random();
		random.nextBytes(sourceBytes);
		
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		md5.update(sourceBytes);
		
		byte[] expectedValues = md5.digest();
		
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 512, 10);

		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, 3, wrapBufferPool);
		
		try {
			try {
				sb.skip(9);
				sb.putByte((byte)17);
				
				long newPosition = sb.getPosition();
				sb.putBytes(sourceBytes);
				sb.putByte((byte)19);
				
				sb.setPosition(newPosition);
				byte[] actualValues = sb.getMD5WithoutChange(sourceBytes.length);
				
				assertArrayEquals(expectedValues, actualValues);
				
				sb.checkValid();
				
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}			
		} finally {
			sb.releaseAllWrapBuffers();
		}	
	}
	
	@Test
	public void test() {
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
			
		
		byte[] retBytes = md5.digest();
		
		if (null == retBytes) {
			System.out.println("1.retBytes is null");
		} else {
			System.out.printf("1.%s", HexUtil.getHexStringFromByteArray(retBytes));
			System.out.println();
		}
		
		retBytes = md5.digest();
		
		if (null == retBytes) {
			System.out.println("2.retBytes is null");
		} else {
			System.out.printf("2.%s", HexUtil.getHexStringFromByteArray(retBytes));
			System.out.println();
		}
		
	}
}
