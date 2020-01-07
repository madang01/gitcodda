/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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

public class SequencedPropertiesTest {
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
	public void test() {
		SequencedProperties oldSequencedProperties = new SequencedProperties();
		
		String prefix="temp";
		String suffix =".tmp";
				
		try {
			File originalTempFileForPathAttributeValue = File.createTempFile(prefix, suffix);
			originalTempFileForPathAttributeValue.deleteOnExit();
			
			oldSequencedProperties.put("path", originalTempFileForPathAttributeValue.getAbsolutePath());
			
			String pathAttributeValue = oldSequencedProperties.getProperty("path");
			
			log.info("1.pathAttributeValue=["+pathAttributeValue+"]");
			
			File tempFileForSequcnecProperties = File.createTempFile(prefix, suffix);
			tempFileForSequcnecProperties.deleteOnExit();
			
			FileWriter fw = new FileWriter(tempFileForSequcnecProperties);			
			
			oldSequencedProperties.store(fw, "temp properties");
			
			byte[] contetns = CommonStaticUtil.readFileToByteArray(tempFileForSequcnecProperties, 1024*1024*2);
			
			String fileContents = new String(contetns, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			
			log.info("fileContents=["+fileContents+"]");
			
			SequencedProperties newSequencedProperties = new SequencedProperties();
			
			FileInputStream fis = new FileInputStream(tempFileForSequcnecProperties);
			try {
				newSequencedProperties.load(fis);
			} finally {
				try {
					fis.close();
				} catch(Exception e) {
					
				}
			}
			
			pathAttributeValue = newSequencedProperties.getProperty("path");
			
			log.info("2.pathAttributeValue=["+pathAttributeValue+"]");
			
			File tempFileForPathAttributeValue = new File(pathAttributeValue);
			
			assertEquals("path 속성 값으로 지정한 임시 파일의 경로가 실제하는지 검사", true, tempFileForPathAttributeValue.exists());
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("알수 없는 에러 발생");
		}
	}

}
