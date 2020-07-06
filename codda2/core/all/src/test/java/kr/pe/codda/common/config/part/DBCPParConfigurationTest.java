/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.pe.codda.common.config.part;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.config.part.DBCPParConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class DBCPParConfigurationTest {
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
	public void testToValueForJDFMemberLoginPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		DBCPParConfiguration actualDBCPParConfiguration = new DBCPParConfiguration("lt_sb_db");
		actualDBCPParConfiguration.setDBCPConfigFile(new File("D:\\gitcodda\\codda2\\project\\sample_base\\resources\\dbcp\\dbcp.lt_sb_db.properties"));
		
		actualDBCPParConfiguration.toPropertiesForDBCPConfigFile(sourceSequencedProperties);
		
		DBCPParConfiguration expectedDBCPParConfiguration = new DBCPParConfiguration("lt_sb_db");
		
		try {
			expectedDBCPParConfiguration.fromPropertiesForDBCPConfigFile(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedDBCPParConfiguration.getDBCPConfigFile(), actualDBCPParConfiguration.getDBCPConfigFile());
	}

	
	@Test
	public void testToPropertiesForJDFMemberLoginPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		DBCPParConfiguration actualDBCPParConfiguration = new DBCPParConfiguration("lt_sb_db");
		actualDBCPParConfiguration.setDBCPConfigFile(new File("D:\\gitcodda\\codda2\\project\\sample_base\\resources\\dbcp\\dbcp.lt_sb_db.properties"));
		
		actualDBCPParConfiguration.toPropertiesForDBCPConfigFile(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}

}
