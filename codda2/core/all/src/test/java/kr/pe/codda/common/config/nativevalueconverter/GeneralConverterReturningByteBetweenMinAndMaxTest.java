package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;

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

public class GeneralConverterReturningByteBetweenMinAndMaxTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
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
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_NullParamter_itemValue() throws Exception {
		GeneralConverterReturningByteBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningByteBetweenMinAndMax((byte)10, (byte)20);
		try {
			minMaxConverter.valueOf(null);
		} catch(IllegalArgumentException e) {
			log.log(Level.WARNING, "null paramter", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_EmptyString() throws Exception {
		GeneralConverterReturningByteBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningByteBetweenMinAndMax((byte)10, (byte)20);
		try {
			minMaxConverter.valueOf("");
		} catch(IllegalArgumentException e) {
			log.log(Level.WARNING, "empty string paramter", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_ValidButBadParameter_NotNumber() throws Exception {
		GeneralConverterReturningByteBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningByteBetweenMinAndMax((byte)10, (byte)20);
		try {
			minMaxConverter.valueOf("a");
		} catch(IllegalArgumentException e) {
			log.log(Level.WARNING, "not number", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_ValidButBadParameter_TooBig() throws Exception {
		GeneralConverterReturningByteBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningByteBetweenMinAndMax((byte)10, (byte)20);
		
		
		try {
			minMaxConverter.valueOf("12345");
		} catch(IllegalArgumentException e) {
			log.log(Level.WARNING, "bigger than max of byte", e);
			throw e;
		}
	}
	
	
	@Test
	public void testValueOf_ExpectedValueComparison() {
		GeneralConverterReturningByteBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningByteBetweenMinAndMax((byte)10, (byte)20);
		
		byte expectedValue;
		byte returnedValue;
		
		expectedValue = 12;
		returnedValue = minMaxConverter.valueOf(String.valueOf(expectedValue));
		
		assertEquals("the expected value comparison", returnedValue, expectedValue);		
	}

}
