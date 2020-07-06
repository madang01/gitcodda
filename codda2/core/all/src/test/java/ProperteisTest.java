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
import org.junit.Ignore;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.common.util.SequencedPropertiesUtil;

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

/**
 * @author Won Jonghoon
 *
 */
public class ProperteisTest {
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
	
	@SuppressWarnings("unchecked")
	@Ignore
	public void test() {
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		SequencedProperties targetSequencedProperties = null;
		
		sourceSequencedProperties.put("dbcp.${SUB_NAME}.dbcp_confige_file.value", "ttmp.txt");
		
		Enumeration<String> keys = sourceSequencedProperties.keys();
		
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value =  sourceSequencedProperties.getProperty(key);
			
			log.info("key="+key);
			log.info("value="+value);
		}
		
		String sourcePropertiesFilePathString = "D:\\temp.properties";
		
		try {
			SequencedPropertiesUtil.overwriteSequencedPropertiesFile(sourceSequencedProperties, "test", sourcePropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
		
		try {
			targetSequencedProperties = SequencedPropertiesUtil.loadSequencedPropertiesFile(sourcePropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
		
		keys = targetSequencedProperties.keys();
		
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value =  sourceSequencedProperties.getProperty(key);
			
			log.info("key="+key);
			log.info("value="+value);
		}
	}
}
