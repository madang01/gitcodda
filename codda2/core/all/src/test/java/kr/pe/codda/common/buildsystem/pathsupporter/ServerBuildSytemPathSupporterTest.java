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

public class ServerBuildSytemPathSupporterTest {
	
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
	public void testGetServerBuildPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("server_build")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAntBuildXMLFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAPPINFPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("APP-INF")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerAPPINFPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAPPINFClassPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ServerBuildSytemPathSupporter
				.getServerAPPINFPathString(installedPathString, mainProjectName))
				.append(File.separator).append("classes")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}*/
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerIOSourcePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator))
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}*/
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerIOSourcePath(installedPathString, mainProjectName);
		
		// log.info("returnedValue={}", returnedValue);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
}
