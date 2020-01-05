package kr.pe.codda.weblib.summernote;

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

public class SummerNoteConfigurationTest {
	protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	
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
	public void testSummerNoteConfiguration_theParameterFontNameListIsNull() {
		try {
			new SummerNoteConfiguration(null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter fontNameList is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSummerNoteConfiguration_theParameterFontNameListIsEmpty() {
		try {
			SummerNoteConfiguration summerNoteConfiguration = new SummerNoteConfiguration(new String[0]);
			
			boolean expected = true;
			
			assertEquals(expected, summerNoteConfiguration.isFontFamilyNotDefined());
			
			String acutalInitializationOptionsString = summerNoteConfiguration.buildInitializationOptionsString(0);
			
			log.info(acutalInitializationOptionsString);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	
	@Test
	public void testSummerNoteConfiguration_theParameterFontNameListIsNotEmpty() {
		try {
			SummerNoteConfiguration summerNoteConfiguration = new SummerNoteConfiguration(new String[] {"Arial", "Arial Black"});
			
			boolean expected = false;
			
			assertEquals(expected, summerNoteConfiguration.isFontFamilyNotDefined());
			
			String acutalInitializationOptionsString = summerNoteConfiguration.buildInitializationOptionsString(0);
			
			log.info(acutalInitializationOptionsString);
			
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}
