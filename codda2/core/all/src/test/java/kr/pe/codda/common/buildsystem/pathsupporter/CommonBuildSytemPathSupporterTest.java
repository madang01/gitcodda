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

public class CommonBuildSytemPathSupporterTest {
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
	public void testGetCommonTempPathString() {
		String installedPathString = installedPath.getAbsolutePath();

		String expectedCommonTempPathString = new StringBuilder(installedPathString).append(File.separator)
				.append("temp").toString();

		log.info("expectedCommonTempPathString=" + expectedCommonTempPathString);

		/*
		if (!(new File(expectedCommonTempPathString)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/

		String actualCommonTempPathString = CommonBuildSytemPathSupporter.getCommonTempPathString(installedPathString);

		assertEquals("Codda temp directory path validation", expectedCommonTempPathString, actualCommonTempPathString);
	}

	@Test
	public void testGetCommonLogPathString() {
		String installedPathString = installedPath.getAbsolutePath();

		String expectedValue = new StringBuilder(installedPathString).append(File.separator).append("log").toString();

		log.info("expectedValue=" + expectedValue);

		/*
		 * if (!(new File(expectedValue)).exists()) {
		 * fail("the file(=the variable expectedValue) doesn't exist"); }
		 */

		String actualValue = CommonBuildSytemPathSupporter.getCommonLogPathString(installedPathString);

		assertEquals("the expected value comparison", expectedValue, actualValue);
	}

	@Test
	public void testGetCommonResourcesPathString() {
		String installedPathString = installedPath.getAbsolutePath();

		String expectedValue = new StringBuilder(installedPathString).append(File.separator).append("resources")
				.toString();

		log.info("expectedValue=" + expectedValue);

		/*
		 * if (!(new File(expectedValue)).exists()) {
		 * fail("the file(=the variable expectedValue) doesn't exist"); }
		 */

		String actualValue = CommonBuildSytemPathSupporter.getCommonResourcesPathString(installedPathString);

		assertEquals("the expected value comparison", expectedValue, actualValue);
	}

	@Test
	public void testGetCommonMessageInfoDirectoryPathString() {
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(
				CommonBuildSytemPathSupporter.getCommonResourcesPathString(installedPathString)).append(File.separator)
						.append("message_info").toString();

		log.info("expectedValue=" + expectedValue);

		/*
		 * if (!(new File(expectedValue)).exists()) {
		 * fail("the file(=the variable expectedValue) doesn't exist"); }
		 */

		String actualValue = CommonBuildSytemPathSupporter.getCommonMessageInfoDirectoryPathString(installedPathString);

		assertEquals("the expected value comparison", expectedValue, actualValue);
	}

	@Test
	public void testGetMessageInfoFilePathStringFromRootResources() {
		String installedPathString = installedPath.getAbsolutePath();
		String messageID = "SelfExnRes";
		String expectedValue = new StringBuilder(
				CommonBuildSytemPathSupporter.getCommonMessageInfoDirectoryPathString(installedPathString))
						.append(File.separator).append(messageID).append(".xml").toString();

		log.info("expectedValue=" + expectedValue);

		/*
		 * if (!(new File(expectedValue)).exists()) {
		 * fail("the file(=the variable expectedValue) doesn't exist"); }
		 */

		String actualValue = CommonBuildSytemPathSupporter.getCommonMessageInfoFilePathString(installedPathString,
				messageID);

		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
}
