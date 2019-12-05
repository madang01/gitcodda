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
import kr.pe.codda.common.util.CustomLogFormatter;

public class GeneralConverterReturningNoTrimStringTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private GeneralConverterReturningNoTrimString nativeValueConverter = null;
	private String returnedValue = null;

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
		nativeValueConverter = new GeneralConverterReturningNoTrimString();
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testValueOf_OK() {
		String expectedValue = null;
		
		expectedValue = "aabc";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			assertEquals("the expected value comparison", returnedValue, expectedValue);			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	
	@Test
	public void testValueOf_theParameterItemValueIsNull() {
		String expectedValue = null;
		
		// expectedValue = "aabc";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

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
		String expectedValue = "";
		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

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
	public void testValueOf_theParameterItemValueIsOneSpaceChar() {
		String expectedValue = null;
		
		expectedValue = " ";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue["+ expectedValue + "] has leading or tailing white space";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testValueOf_theParameterItemValueHasWhitSpaceAtHead() {
		String expectedValue = null;
		
		expectedValue = " a \tb \t";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue["+ expectedValue + "] has leading or tailing white space";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testValueOf_theParameterItemValueHasWhitSpaceAtHeadAndTail() {
		String expectedValue = null;
		
		expectedValue = " ab\nc ";
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue["+ expectedValue + "] has leading or tailing white space";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testValueOf_theParameterItemValueHasWhitSpaceAtTail() {
		String expectedValue = null;
		
		expectedValue = " ab\nc \t";
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue["+ expectedValue + "] has leading or tailing white space";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
}
