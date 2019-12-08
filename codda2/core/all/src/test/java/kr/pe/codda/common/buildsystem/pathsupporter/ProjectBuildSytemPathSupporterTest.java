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

public class ProjectBuildSytemPathSupporterTest {
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
	public void testGetProjectBasePathString() {
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(installedPathString)
				.append(File.separator).append("project").toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		String actualValue = ProjectBuildSytemPathSupporter
				.getProjectBasePathString(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetProjectPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectBasePathString(installedPathString))
				.append(File.separator).append(mainProjectName).toString();
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectConfigDirectoryPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("config").toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProjectConfigDirectoryPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectConfigDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.CONFIG_FILE_NAME).toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProejctConfigFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectResourcesDirectoryPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("resources").toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	@Test
	public void testGetProjectLogbackConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.LOGBACK_LOG_FILE_NAME).toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProjectLogbackConfigFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	
	@Test
	public void testGetProjectDBCPConfigFilePathString() {
		final String mainProjectName = "sample_base";
		final String installedPathString = installedPath.getAbsolutePath();
		final String dbcpName = "SB_DB";
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append("dbcp")
				.append(File.separator)
				.append("dbcp.")
				.append(dbcpName)
				.append(".properties")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProjectDBCPConfigFilePathString(installedPathString, mainProjectName, dbcpName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectMessageInfoDirectoryPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append("message_info").toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/		
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectMessageInfoFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String messageID = "Echo";
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append(messageID).append(".xml")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/		
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProjectMessageInfoFilePathString(installedPathString, mainProjectName, messageID);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAKeypairPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append("rsa_keypair")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAPublickeyFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME)
				.toString();
		
		/*log.info("expectedValue=" + expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		*/
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getSessionKeyRSAPublickeyFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAPrivatekeyFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME)
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	*/	
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getSessionKeyRSAPrivatekeyFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetClientBuildBasePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("client_build")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getClientBuildBasePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectEmailPropertiesFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append("email.properties")
				.toString();
		
		log.info("expectedValue=" + expectedValue);
		
		/*
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		*/
		
		
		String returnedValue = ProjectBuildSytemPathSupporter
				.getProjectEmailPropertiesFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
}
