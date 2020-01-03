/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package kr.pe.codda.common.buildsystem.pathsupporter;

import static org.junit.Assert.assertEquals;

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

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class WebClientBuildSystemPathSupporterTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	private final File installedPath = new File(".");

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
	public void testGetWebClientBuildPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getClientBuildBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("web_build")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		String returnedValue = WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		
		String returnedValue = WebClientBuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientAntPropertiesFilePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.WEBCLIENT_ANT_PROPRTEIS_FILE_NAME_VALUE).toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/	
		
		String returnedValue = WebClientBuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	@Test
	public void testGetWebClinetIOSourcePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator)).toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		*/
		String returnedValue = WebClientBuildSystemPathSupporter
				.getWebClinetIOSourcePath(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
}
