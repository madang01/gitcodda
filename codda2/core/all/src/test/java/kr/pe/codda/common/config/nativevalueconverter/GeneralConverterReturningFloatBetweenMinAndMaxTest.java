package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
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

public class GeneralConverterReturningFloatBetweenMinAndMaxTest {

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
		GeneralConverterReturningFloatBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningFloatBetweenMinAndMax((float)10.3, (float)20.7);
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
		GeneralConverterReturningFloatBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningFloatBetweenMinAndMax((float)10.3, (float)20.7);
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
		GeneralConverterReturningFloatBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningFloatBetweenMinAndMax((float)10.3, (float)20.7);
		
		final String itemValue = "a";
		try {
			minMaxConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not a number of ")
					.append(minMaxConverter.getGenericType().getName())
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testValueOf_theParameterItemValueIsGreaterThanMax() {
		GeneralConverterReturningFloatBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningFloatBetweenMinAndMax((float)10.3, (float)20.7);
		final String itemValue = "12345";
		
		try {
			minMaxConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[").append(itemValue)
					.append("] is greater than max[").append(minMaxConverter.getMax()).append("]").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testValueOf_theParameterItemValueIsNotNumberOfDouble() {
		GeneralConverterReturningFloatBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningFloatBetweenMinAndMax((float)10.3, (float)20.7);
		
		BigDecimal bd1 = BigDecimal.valueOf(Float.MAX_VALUE);
		BigDecimal bd2 = BigDecimal.valueOf(Float.MAX_VALUE);
		BigDecimal bd3 = bd1.add(bd2);
		
		final String itemValue = bd3.toString();
		
		try {
			minMaxConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not a number of ")
					.append(minMaxConverter.getGenericType().getName())
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testValueOf_OK() {
		GeneralConverterReturningFloatBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningFloatBetweenMinAndMax((float)10.3, (float)20.7);
		
		double expectedValue;
		double returnedValue;
		
		expectedValue = 12;
		returnedValue = minMaxConverter.valueOf(String.valueOf(expectedValue));
		
		
		assertEquals("the expected value comparison", returnedValue, expectedValue, 0.1);		
	}
}
