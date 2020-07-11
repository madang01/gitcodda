package kr.pe.codda.common.config.part;

import static org.junit.Assert.fail;

import java.util.Enumeration;
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
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.SequencedProperties;

public class RunningProjectConfigurationTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	/**
	 * @throws java.lang.Exception
	 */
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
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToProperties() {
		RunningProjectConfiguration runningProjectConfiguration = new RunningProjectConfiguration();
		
		runningProjectConfiguration.getCommonPartConfiguration().setRSAKeypairSourceOfSessionKey(SessionKey.RSAKeypairSourceType.FILE);
		
		
		SequencedProperties configFileSequencedProperties = new SequencedProperties();
		
		runningProjectConfiguration.toProperties(configFileSequencedProperties);
		
		@SuppressWarnings("unchecked")
		Enumeration<String> keyEnumeration = (Enumeration<String>)configFileSequencedProperties.keys();
		
		while(keyEnumeration.hasMoreElements()) {
			String key = keyEnumeration.nextElement();
			
			String value = configFileSequencedProperties.getProperty(key);
			
			
			// log.info(key+"=" + value);
			System.out.printf("%s=%s", key, value);
			System.out.println();
			
		}
		
		RunningProjectConfiguration.applyIntalledPath("D:\\gitcodda\\codda2", "sample_base", configFileSequencedProperties);
		
		try {
			runningProjectConfiguration.fromProperties(configFileSequencedProperties);			
			runningProjectConfiguration.checkVadlidation();
		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);
			
			fail("error occur, errmsg="+e.getMessage());
		}
	}

}
