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
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.SequencedProperties;

public class ListTypePartConfigurationTest {
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
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_빈상태() {
		ListTypePartConfiguration<DBCPParConfiguration> DBCP = new ListTypePartConfiguration<DBCPParConfiguration>("dbcp", DBCPParConfiguration.class);
		SequencedProperties configFileSequencedProperties = new SequencedProperties();
		
		DBCP.toProperties(configFileSequencedProperties);
		
		@SuppressWarnings("unchecked")
		Enumeration<String> keyEnumeration = (Enumeration<String>)configFileSequencedProperties.keys();
		
		while(keyEnumeration.hasMoreElements()) {
			String key = keyEnumeration.nextElement();
			
			String value = configFileSequencedProperties.getProperty(key);
			
			
			// log.info(key+"=" + value);
			System.out.printf("%s=%s", key, value);
			System.out.println();
			
		}
		
		ListTypePartConfiguration<DBCPParConfiguration> newDBCP = new ListTypePartConfiguration<DBCPParConfiguration>("dbcp", DBCPParConfiguration.class);
		
		try {
			newDBCP.fromProperties(configFileSequencedProperties);
			
			
		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);
			
			fail("error occur, errmsg="+e.getMessage());
		}
		
		SequencedProperties targetSequencedProperties = new SequencedProperties();
		newDBCP.toProperties(targetSequencedProperties);
		
		@SuppressWarnings("unchecked")
		Enumeration<String> targetKeyEnumeration = (Enumeration<String>)targetSequencedProperties.keys();
		
		while(targetKeyEnumeration.hasMoreElements()) {
			String key = targetKeyEnumeration.nextElement();
			
			String value = targetSequencedProperties.getProperty(key);
			
			
			// log.info(key+"=" + value);
			System.out.printf("%s=%s", key, value);
			System.out.println();
			
		}
		
	}

	
	@Test
	public void test_1개추가() {
		ListTypePartConfiguration<DBCPParConfiguration> DBCP = new ListTypePartConfiguration<DBCPParConfiguration>("dbcp", DBCPParConfiguration.class);
	
		DBCPParConfiguration dbcpParConfigurationForSampleBase = new DBCPParConfiguration("sb_db");
		DBCP.addProjectPartConfiguration(dbcpParConfigurationForSampleBase.getDBCPName(), dbcpParConfigurationForSampleBase);
		
		SequencedProperties configFileSequencedProperties = new SequencedProperties();
		DBCP.toProperties(configFileSequencedProperties);
		
		@SuppressWarnings("unchecked")
		Enumeration<String> keyEnumeration = (Enumeration<String>)configFileSequencedProperties.keys();
		
		while(keyEnumeration.hasMoreElements()) {
			String key = keyEnumeration.nextElement();
			
			String value = configFileSequencedProperties.getProperty(key);
			
			
			// log.info(key+"=" + value);
			System.out.printf("%s=%s", key, value);
			System.out.println();
			
		}
	}
	
	@Test
	public void test_2개추가() {
		ListTypePartConfiguration<DBCPParConfiguration> DBCP = new ListTypePartConfiguration<DBCPParConfiguration>("dbcp", DBCPParConfiguration.class);
	
		DBCPParConfiguration dbcpParConfigurationForSampleBase = new DBCPParConfiguration("sb_db");
		DBCP.addProjectPartConfiguration(dbcpParConfigurationForSampleBase.getDBCPName(), dbcpParConfigurationForSampleBase);
		
		DBCPParConfiguration newDBCPParConfigurationForGeneralTestOfSampleBase = new DBCPParConfiguration("gt_sb_db");
		DBCP.addProjectPartConfiguration(newDBCPParConfigurationForGeneralTestOfSampleBase.getDBCPName(), newDBCPParConfigurationForGeneralTestOfSampleBase);
		
		SequencedProperties configFileSequencedProperties = new SequencedProperties();
		DBCP.toProperties(configFileSequencedProperties);
		
		@SuppressWarnings("unchecked")
		Enumeration<String> keyEnumeration = (Enumeration<String>)configFileSequencedProperties.keys();
		
		while(keyEnumeration.hasMoreElements()) {
			String key = keyEnumeration.nextElement();
			
			String value = configFileSequencedProperties.getProperty(key);
			
			
			// log.info(key+"=" + value);
			System.out.printf("%s=%s", key, value);
			System.out.println();
			
		}
	}
}
