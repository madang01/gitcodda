package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
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
import kr.pe.codda.common.util.CustomLogFormatter;

public class GeneralConverterReturningCharsetTest {
	
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private GeneralConverterReturningCharset nativeValueConverter = null;
	private Charset returnedValue = null;
	private Charset defaultCharset = Charset.defaultCharset();
	private Charset utf8Charset = Charset.forName("UTF8");
	
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
	public void setup() {
		nativeValueConverter = new GeneralConverterReturningCharset();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testValueOf_OK_defalutCharset() {
		Charset expectedValue = null;
		
		expectedValue = defaultCharset;
		
		try {
			returnedValue = nativeValueConverter.valueOf(defaultCharset.name());

			String infoMessage = new StringBuilder()
					.append("default::charset name=[")
					.append(returnedValue.name())
					.append("], display name=[")
					.append(returnedValue.displayName())
					.append("], alias=")
					.append(returnedValue.aliases().toString()).toString();
			
			log.info(infoMessage);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		assertEquals("the expected value comparison", returnedValue, expectedValue);
	}

	@Test
	public void testValueOf_OK_UTF8CharsetAliasName() {
		Charset expectedValue = null;
		
		expectedValue = utf8Charset;
		/** alias test start */
		try {
			returnedValue = nativeValueConverter.valueOf("UTF8");
			
			String infoMessage = new StringBuilder()
					.append("utf8::charset name=[")
					.append(returnedValue.name())
					.append("], display name=[")
					.append(returnedValue.displayName())
					.append("], alias=")
					.append(returnedValue.aliases().toString()).toString();

			log.info(infoMessage);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		
		assertEquals("the expected value comparison", returnedValue, expectedValue);

		try {
			returnedValue = nativeValueConverter.valueOf("unicode-1-1-utf-8");
			
			String infoMessage = new StringBuilder()
					.append("utf8::charset name=[")
					.append(returnedValue.name())
					.append("], display name=[")
					.append(returnedValue.displayName())
					.append("], alias=")
					.append(returnedValue.aliases().toString()).toString();

			log.info(infoMessage);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		
		assertEquals("the expected value comparison", returnedValue, expectedValue);

		/** alias test end */
	}

	@Test
	public void testValueOf_OK_NotCaseSensitive() {
		Charset expectedValue = null;
		
		expectedValue = utf8Charset;
		
		/** 대소문자 구분 여부 테스트 시작 */
		try {
			returnedValue = nativeValueConverter.valueOf("Unicode-1-1-utf-8");

			String infoMessage = new StringBuilder()
					.append("utf8::charset name=[")
					.append(returnedValue.name())
					.append("], display name=[")
					.append(returnedValue.displayName())
					.append("], alias=")
					.append(returnedValue.aliases().toString()).toString();

			log.info(infoMessage);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		assertEquals("the expected value comparison", returnedValue, expectedValue);

		try {
			returnedValue = nativeValueConverter.valueOf("uTF-8");

			String infoMessage = new StringBuilder()
					.append("utf8::charset name=[")
					.append(returnedValue.name())
					.append("], display name=[")
					.append(returnedValue.displayName())
					.append("], alias=")
					.append(returnedValue.aliases().toString()).toString();

			log.info(infoMessage);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		
		assertEquals("the expected value comparison", returnedValue, expectedValue);
		/** 대소문자 구분 여부 테스트 종료 */
	}

	
	@Test
	public void testValueOf_theParameterItemValueIsNull() {
		try {
			returnedValue = nativeValueConverter.valueOf(null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}

	}

	
	
	@Test
	public void testValueOf_theParameterItemValueIsEmpty() {
		try {
			returnedValue = nativeValueConverter.valueOf("");

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue is empty";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	
	
	@Test
	public void testValueOf_theParameterItemValueIsNotCharsetName() {
		try {
			returnedValue = nativeValueConverter.valueOf("king");

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue[king] is a bad charset name";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
}
