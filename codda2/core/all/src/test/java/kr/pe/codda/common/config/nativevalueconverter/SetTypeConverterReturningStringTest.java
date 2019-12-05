package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
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

public class SetTypeConverterReturningStringTest {	
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private SetTypeConverterReturningString nativeValueConverter = null;
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
		nativeValueConverter = new SetTypeConverterReturningString("AES", "DESede", "DES");
	}
	
	@After
	public void tearDown() throws Exception {
	}

	
	
	
	@Test
	public void testConstructor_NoParameter() {
		try {
			new SetTypeConverterReturningString();
			
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter parmValueSet is empty";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testConstructor_TrimString() {
		try {
			new SetTypeConverterReturningString("AES", "DESede", "DES ");
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder()
					.append("the parameter parmValueSet[")
					.append(2)
					.append("]'s value[")
					.append("DES ")
					.append("] has leading or tailing white space").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	
	@Test
	public void testValueOf_OK() {
		Iterator<String> iter = nativeValueConverter.getItemValueSet().iterator();
		while(iter.hasNext()) {
			String expectedValue = iter.next();
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
	public void testValueOf_theParameterItemValueIsNotAElementOfSet_대소문자틀림() {
		final String itemValue = "aES";
		
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
