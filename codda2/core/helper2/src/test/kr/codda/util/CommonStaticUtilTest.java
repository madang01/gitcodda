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
package kr.codda.util;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileSystemView;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import kr.pe.codda.common.exception.BuildSystemException;

/**
 * @author Won Jonghoon
 *
 */
class CommonStaticUtilTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
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
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Ignore
	void testGetMainProjectNameList() {
		Logger log = Logger.getGlobal();
		
		String installedPathString = "D:\\gitcodda\\codda2";
		
		List<String> mainProjectNameList = null;
		
		try {
			mainProjectNameList = CommonStaticUtil.getMainProjectNameList(installedPathString);
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			
			log.warning(errorMessage);
			
			fail("fail to get a main project name list");
		}
		
		
		String mainProjectNameListJsonString = new Gson().toJson(mainProjectNameList);
		
		log.info(mainProjectNameListJsonString);
	}
	
	@Test
	void testGetValidPathListToSaveIOSourceFiles() {
		Logger log = Logger.getGlobal();
		
		final String installedPathString = "D:\\gitcodda\\codda2";
		final String mainProjectName = "sample_test";
		final String messageID = "Echo";
		
		try {
			ArrayList<File> validPathListToSaveIOSourceFiles =  CommonStaticUtil.getValidPathListToSaveIOSourceFiles(installedPathString, mainProjectName, messageID);
			
			
			for (File validPathToSaveIOSourceFiles : validPathListToSaveIOSourceFiles) {
				log.info(validPathToSaveIOSourceFiles.getAbsolutePath());
			}
			
		} catch (BuildSystemException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail("not yet");
		}
		
	}
	
	@Test
	void test() {
		Logger log = Logger.getGlobal();
		
		FileSystemView fsv = FileSystemView.getFileSystemView();
		
		File[] drives = File.listRoots();
		if (drives != null && drives.length > 0) {
		    for (File aDrive : drives) {
		    	log.info("Drive Letter: " + aDrive);
		    	log.info("\tType: " + fsv.getSystemTypeDescription(aDrive));
		    	log.info("\tTotal space: " + aDrive.getTotalSpace());
		    	log.info("\tFree space: " + aDrive.getFreeSpace());
		    }
		}
	}
	
	@Test
	void testCDROM() {
		File f = new File("e:\\");
		
		Logger.getGlobal().info("CDROM e:\\ exist="+f.exists());
		Logger.getGlobal().info("CDROM e:\\ isDirectory="+f.isDirectory());
	}

}
