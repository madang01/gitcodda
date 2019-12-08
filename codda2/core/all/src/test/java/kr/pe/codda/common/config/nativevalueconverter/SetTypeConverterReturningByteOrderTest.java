package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
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

public class SetTypeConverterReturningByteOrderTest {
	
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private SetTypeConverterReturningByteOrder nativeValueConverter = null;
	private ByteOrder returnedValue = null;	
	
	
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
		try {
			nativeValueConverter = new SetTypeConverterReturningByteOrder();
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	
	
	@Test
	public void testValueOf_OK() {	
		ByteOrder expectedValue = null;
				
		/**
		 * info)  ByteOrder.LITTLE_ENDIAN.toString() equals to 'LITTLE_ENDIAN', 
		 *        ByteOrder.BIG_ENDIAN.toString() equals to 'BIG_ENDIAN',
		 */
		ByteOrder[] byteOrders = {ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN};
		for (int i=0; i < byteOrders.length; i++) {
			expectedValue = byteOrders[i];
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
	public void testValueOf_theParameterItemValueIsBad_대소문자틀린경우() {
		final String itemValue = "BIG_eNDIAN"; 
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not an element of ")
					.append(nativeValueConverter.getSetName()).append(nativeValueConverter.getStringFromSet())
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	@Test
	public void testValueOf_theParameterItemValueIsBad_엉뚱한문자열() {
		final String itemValue = "aabbc1c";
		
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not an element of ")
					.append(nativeValueConverter.getSetName()).append(nativeValueConverter.getStringFromSet())
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
}
