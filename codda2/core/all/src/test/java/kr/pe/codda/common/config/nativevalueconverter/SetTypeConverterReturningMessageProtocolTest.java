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
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class SetTypeConverterReturningMessageProtocolTest {	
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private SetTypeConverterReturningMessageProtocolType nativeValueConverter = null;
	
	
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
		nativeValueConverter = new SetTypeConverterReturningMessageProtocolType();
	}
	
	@After
	public void tearDown() throws Exception {
	}

	
	
	@Test
	public void testValueOf_OK() {
		MessageProtocolType expectedValue = null;
		MessageProtocolType returnedValue = null;
		
		MessageProtocolType[] messageProtocoles = MessageProtocolType.values();
		for (int i=0; i < messageProtocoles.length; i++) {
			expectedValue = messageProtocoles[i];
			try {
				returnedValue = nativeValueConverter.valueOf(expectedValue.toString());
				
				assertEquals("the expected value comparison", returnedValue, expectedValue);
			} catch (Exception e) {
				String errorMessage = e.getMessage();
				log.log(Level.WARNING, errorMessage, e);
				
				fail("unknown error");
			}
		}
	}
	
	
	@Test
	public void testValueOf_theParameterItemValueIsNull() {
		@SuppressWarnings("unused")
		MessageProtocolType returnedValue = null;
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
		@SuppressWarnings("unused")
		MessageProtocolType returnedValue = null;
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
	public void testValueOf_theParameterItemValueIsNotAElementOfSet_대소문자틀림() {
		final String itemValue = "dHB";
		
		@SuppressWarnings("unused")
		MessageProtocolType returnedValue = null;
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue)
					.append("] is not a element of ")
					.append(nativeValueConverter.getSetName())
					.append(nativeValueConverter.getStringFromSet())
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testValueOf_theParameterItemValueIsNotAElementOfSet_엉뚱한값() {
		final String itemValue = "aabbcc";
		
		@SuppressWarnings("unused")
		MessageProtocolType returnedValue = null;
		try {
			returnedValue = nativeValueConverter.valueOf("aabbcc");
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue)
					.append("] is not a element of ")
					.append(nativeValueConverter.getSetName())
					.append(nativeValueConverter.getStringFromSet())
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
}
