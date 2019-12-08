package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class GeneralConverterReturningLongBetweenMinAndMaxTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

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
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValueOf_theParameterItemValueIsNull() {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				(long) 10, (long) 20);
		try {
			minMaxConverter.valueOf(null);
			
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
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				(long) 10, (long) 20);
		try {
			minMaxConverter.valueOf("");
			
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
	public void testValueOf_theParameterItemValueIsNotNumber() {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				(long) 10, (long) 20);
		try {
			minMaxConverter.valueOf("a");
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue[a] is not a number of java.lang.Long";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testValueOf_theParameterItemValueIsTooBig() {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				(long) 10, (long) 20);

		try {
			minMaxConverter.valueOf("12345");
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue[12345] is greater than max[20]";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testValueOf_OK() {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				(long) 10, Long.MAX_VALUE);

		long expectedValue;
		long returnedValue;

		expectedValue = 1234343434343333333L;
		returnedValue = minMaxConverter.valueOf(String.valueOf(expectedValue));

		assertEquals("the expected value comparison", returnedValue, expectedValue);
	}

}
