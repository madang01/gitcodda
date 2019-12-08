package kr.pe.codda.common.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class IncomingStreamTest {

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
	public void testCutMessageInputStream_theParameterWantedSizeToCutIsLessThanOrEqualToZero() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 512, 10);

		IncomingStream isr = new IncomingStream(streamCharsetFamily, 3, wrapBufferPool);

		int[] wantedSizesToCut = { 0, -1, -2 };

		for (int wantedSizeToCut : wantedSizesToCut) {
			try {
				isr.cutMessageInputStream(wantedSizeToCut);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String acutalErrorMessage = e.getMessage();
				String expectedErrorMessage = new StringBuilder().append("the parameter wantedSizeToCut[")
						.append(wantedSizeToCut).append("] is less than or equal to zero").toString();

				System.out.println(acutalErrorMessage);

				assertEquals(expectedErrorMessage, acutalErrorMessage);
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			}
		}
	}

	@Test
	public void testCutMessageInputStream_theParameterWantedSizeToCutIsGreaterThanInputStreamSize() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 512, 10);

		IncomingStream isr = new IncomingStream(streamCharsetFamily, 3, wrapBufferPool);

		
		try {
			isr.putByte((byte) 12);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

		long position = isr.getPosition();
		long wantedSizeToCut = position + 1;

		try {
			isr.cutMessageInputStream(wantedSizeToCut);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder()
					.append("the parameter wantedSizeToCut[")
					.append(wantedSizeToCut)
					.append("] is greater than the var position[")
					.append(position)
					.append("] which means the size of input streame").toString();

			System.out.println(acutalErrorMessage);

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		}

	}
	
	@Test
	public void testCutMessageInputStream_전체자르기() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 512, 10);
		

		int numbersOfReceviedBytes[] = {1, 100, 512, 513, 700, 1024};
		byte[] sourceBytes;
		
		Random random = new Random();
		
		
		for (int numberOfReceviedBytes : numbersOfReceviedBytes) {
			sourceBytes = new byte[numberOfReceviedBytes];
			random.nextBytes(sourceBytes);
			
			IncomingStream isr = new IncomingStream(streamCharsetFamily, 3, wrapBufferPool);
			
			try {
				isr.putBytes(sourceBytes);
				StreamBuffer sb = isr.cutMessageInputStream(numberOfReceviedBytes);
				
				try {
					isr.checkValid();
					
					//Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					//log.log(Level.INFO, sb.toString());
					
					assertEquals(numberOfReceviedBytes, (int)sb.remaining());
					
					byte[] acutalBytes = sb.getBytes((int)sb.remaining());

					assertArrayEquals(sourceBytes, acutalBytes);
				} finally {
					sb.releaseAllWrapBuffers();
				}
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "unknown error", e);
				fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
			} finally {
				isr.releaseAllWrapBuffers();
			}			
		}		
	}
	
	@Test
	public void testCutMessageInputStream_잔존데이터_잘려진스트림음담고있는마지막랩버퍼에잔존데이터없는경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 512, 100);
		Random random = new Random();
		byte[] sourceBytes = new byte[wrapBufferPool.getDataPacketBufferSize()*2+wrapBufferPool.getDataPacketBufferSize()/2];
		random.nextBytes(sourceBytes);
		
		int cuttingSize = wrapBufferPool.getDataPacketBufferSize();
		
		IncomingStream isr = new IncomingStream(streamCharsetFamily, 5, wrapBufferPool);		
		try {
			isr.putBytes(sourceBytes);
			
			StreamBuffer sb = isr.cutMessageInputStream(cuttingSize);
			
			try {
				isr.checkValid();
				
				// Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				// log.log(Level.INFO, sb.toString());
				
				assertEquals(cuttingSize, (int)sb.remaining());
				
				byte[] acutalBytes = sb.getBytes((int)sb.remaining());
				
				for (int i=0; i < acutalBytes.length; i++) {
					if (acutalBytes[i] != sourceBytes[i]) {
						String errorMessage = new StringBuilder()
								.append("acutalBytes[")
								.append(i)
								.append("] is dfferent from sourceBytes[")
								.append(i)
								.append("]").toString();
						fail(errorMessage);
					}
				}

				long oldPosition = isr.getPosition();
				isr.setPosition(0);
				byte[] remaingBytes = isr.getBytes((int)oldPosition);
				
				for (int i=0; i < remaingBytes.length; i++) {
					if (remaingBytes[i] != sourceBytes[i+cuttingSize]) {
						String errorMessage = new StringBuilder()
								.append("remaingBytes[")
								.append(i)
								.append("] is dfferent from sourceBytes[")
								.append(i+cuttingSize)
								.append("]").toString();
						fail(errorMessage);
					}
				}
				
			} finally {
				sb.releaseAllWrapBuffers();
			}
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		} finally {
			isr.releaseAllWrapBuffers();
		}	
	}
	
	@Test
	public void testCutMessageInputStream_잔존데이터_잘려진스트림에잔존데이터전체가있는경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 512, 100);
		Random random = new Random();
		byte[] sourceBytes = new byte[wrapBufferPool.getDataPacketBufferSize()*2+wrapBufferPool.getDataPacketBufferSize()/2];
		random.nextBytes(sourceBytes);
		
		int cuttingSize = wrapBufferPool.getDataPacketBufferSize()*2 + 10;
		
		IncomingStream isr = new IncomingStream(streamCharsetFamily, 5, wrapBufferPool);		
		try {
			isr.putBytes(sourceBytes);
			
			StreamBuffer sb = isr.cutMessageInputStream(cuttingSize);
			
			try {
				isr.checkValid();
				
				// Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				// log.log(Level.INFO, sb.toString());
				
				assertEquals(cuttingSize, (int)sb.remaining());
				
				byte[] acutalBytes = sb.getBytes((int)sb.remaining());
				
				for (int i=0; i < acutalBytes.length; i++) {
					if (acutalBytes[i] != sourceBytes[i]) {
						String errorMessage = new StringBuilder()
								.append("acutalBytes[")
								.append(i)
								.append("] is dfferent from sourceBytes[")
								.append(i)
								.append("]").toString();
						fail(errorMessage);
					}
				}
				
				long oldPosition = isr.getPosition();
				isr.setPosition(0);
				byte[] remaingBytes = isr.getBytes((int)oldPosition);
				
				for (int i=0; i < remaingBytes.length; i++) {
					if (remaingBytes[i] != sourceBytes[i+cuttingSize]) {
						String errorMessage = new StringBuilder()
								.append("remaingBytes[")
								.append(i)
								.append("] is dfferent from sourceBytes[")
								.append(i+cuttingSize)
								.append("]").toString();
						fail(errorMessage);
					}
				}

				
			} finally {
				sb.releaseAllWrapBuffers();
			}
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		} finally {
			isr.releaseAllWrapBuffers();
		}	
	}
	
	@Test
	public void testCutMessageInputStream_잔존데이터_잘려진스트림에잔존데이터의일부분만있는경우() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 512, 100);
		Random random = new Random();
		byte[] sourceBytes = new byte[wrapBufferPool.getDataPacketBufferSize()*2+wrapBufferPool.getDataPacketBufferSize()/2];
		random.nextBytes(sourceBytes);
		
		int cuttingSize = wrapBufferPool.getDataPacketBufferSize() - wrapBufferPool.getDataPacketBufferSize()/2;
		
		IncomingStream isr = new IncomingStream(streamCharsetFamily, 5, wrapBufferPool);		
		try {
			isr.putBytes(sourceBytes);
			
			StreamBuffer sb = isr.cutMessageInputStream(cuttingSize);
			
			try {
				isr.checkValid();
				
				// Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				// log.log(Level.INFO, sb.toString());
				
				assertEquals(cuttingSize, (int)sb.remaining());
				
				byte[] acutalBytes = sb.getBytes((int)sb.remaining());
				
				for (int i=0; i < acutalBytes.length; i++) {
					if (acutalBytes[i] != sourceBytes[i]) {
						String errorMessage = new StringBuilder()
								.append("acutalBytes[")
								.append(i)
								.append("] is dfferent from sourceBytes[")
								.append(i)
								.append("]").toString();
						fail(errorMessage);
					}
				}
				
				long oldPosition = isr.getPosition();
				isr.setPosition(0);
				byte[] remaingBytes = isr.getBytes((int)oldPosition);
				
				for (int i=0; i < remaingBytes.length; i++) {
					if (remaingBytes[i] != sourceBytes[i+cuttingSize]) {
						String errorMessage = new StringBuilder()
								.append("remaingBytes[")
								.append(i)
								.append("] is dfferent from sourceBytes[")
								.append(i+cuttingSize)
								.append("]").toString();
						fail(errorMessage);
					}
				}

				
			} finally {
				sb.releaseAllWrapBuffers();
			}
		} catch (Exception e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
		} finally {
			isr.releaseAllWrapBuffers();
		}	
	}
	
	
	@Test
	public void testCutMessageInputStream_무작위테스트() {
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.forName("UTF-8"));
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.BIG_ENDIAN, 512, 100);
		
		int sourceBytesLengthArray[] = {1, 100, 512, 520, 1024, 1050};
		
		for (int sourceBytesLength : sourceBytesLengthArray) {
			Random random = new Random();
			byte[] sourceBytes = new byte[sourceBytesLength];
			random.nextBytes(sourceBytes);		
			
			for (int cuttingSize = 1; cuttingSize <= sourceBytesLength; cuttingSize++) {
				IncomingStream isr = new IncomingStream(streamCharsetFamily, 5, wrapBufferPool);		
				try {
					isr.putBytes(sourceBytes);
					
					StreamBuffer sb = isr.cutMessageInputStream(cuttingSize);
					
					try {
						isr.checkValid();
						// sb.checkValid();
						
						// Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
						// log.log(Level.INFO, sb.toString());
						
						assertEquals(cuttingSize, (int)sb.remaining());
						
						byte[] acutalBytes = sb.getBytes((int)sb.remaining());
						
						for (int i=0; i < acutalBytes.length; i++) {
							if (acutalBytes[i] != sourceBytes[i]) {
								String errorMessage = new StringBuilder()
										.append("acutalBytes[")
										.append(i)
										.append("] is dfferent from sourceBytes[")
										.append(i)
										.append("]").toString();
								fail(errorMessage);
							}
						}
						
						long oldPosition = isr.getPosition();
						isr.setPosition(0);
						byte[] remaingBytes = isr.getBytes((int)oldPosition);
						
						for (int i=0; i < remaingBytes.length; i++) {
							if (remaingBytes[i] != sourceBytes[i+cuttingSize]) {
								String errorMessage = new StringBuilder()
										.append("remaingBytes[")
										.append(i)
										.append("] is dfferent from sourceBytes[")
										.append(i+cuttingSize)
										.append("]").toString();
								fail(errorMessage);
							}
						}

						
					} finally {
						sb.releaseAllWrapBuffers();
					}
				} catch (Exception e) {
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, "unknown error", e);
					fail("알수 없는 에러발생, 에러 내용은 로그 참조할것");
				} finally {
					isr.releaseAllWrapBuffers();
				}	
			}
			
		}
	}

}
