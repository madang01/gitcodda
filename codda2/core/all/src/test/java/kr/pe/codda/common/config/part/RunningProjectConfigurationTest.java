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
	public void testToProperties_모든항목() {
		RunningProjectConfiguration runningProjectConfiguration = new RunningProjectConfiguration();
		
		DBCPParConfiguration newDBCPParConfigurationForSampleBase = new DBCPParConfiguration("sb_db");
		DBCPParConfiguration newDBCPParConfigurationForGeneralTestOfSampleBase = new DBCPParConfiguration("gt_sb_db");
		DBCPParConfiguration newDBCPParConfigurationForLoadTestOfSampleBase = new DBCPParConfiguration("lt_sb_db");
		
		runningProjectConfiguration.addDBCPParConfiguration(newDBCPParConfigurationForSampleBase);
		runningProjectConfiguration.addDBCPParConfiguration(newDBCPParConfigurationForGeneralTestOfSampleBase);
		runningProjectConfiguration.addDBCPParConfiguration(newDBCPParConfigurationForLoadTestOfSampleBase);
		
		SubProjectPartConfiguration newSubProjectPartConfigurationForSampleTest = new SubProjectPartConfiguration("sample_test");
		runningProjectConfiguration.addSubProjectPartConfiguration(newSubProjectPartConfigurationForSampleTest);
		
		/** 'RSA 키쌍 출처' 값의 디폴트는 서버로 공개키와 비밀키 파일 2개 항목이 null 이 되기때문에 'RSA 키쌍 출처' 를 외부 파일로 바꾸어 공개키와 비밀키 파일 2개 항목이 null 되는것을 방지함 */
		CommonPartConfiguration commonPartConfiguration = runningProjectConfiguration.getCommonPartConfiguration();		
		commonPartConfiguration.setRSAKeypairSourceOfSessionKey(SessionKey.RSAKeypairSourceType.FILE);
		
		
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
