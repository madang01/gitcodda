package kr.pe.codda.weblib.summernote;

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

import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class SummerNoteConfigurationTest {
	protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	protected static File installedBasePath = null;
	protected static File installedPath = null;
	protected static File wasLibPath = null;
	protected final static String mainProjectName = "sample_base";
	
	private static void setupLogbackEnvromenetVariable(String installedPathString, String mainProejct) throws IllegalStateException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}
		
		String logbackConfigFilePathString = ProjectBuildSytemPathSupporter.getProjectLogbackConfigFilePathString(installedPathString, mainProejct);
		String rootLogPathString = CommonBuildSytemPathSupporter.getCommonLogPathString(installedPathString);
		
		
		{
			File logbackConfigFile = new File(logbackConfigFilePathString);		
			
			
			if (! logbackConfigFile.exists()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] doesn't exist").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			
			if (! logbackConfigFile.isFile()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] is not a normal file").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}


			if (! logbackConfigFile.canRead()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] does not have read permissions").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		}
		
		
		{
			File logPath = new File(rootLogPathString);
			
			if (! logPath.exists()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(rootLogPathString)
						.append("] doesn't exist").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			
			if (! logPath.isDirectory()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(rootLogPathString)
						.append("] is not a directory").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}


			if (! logPath.canWrite()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(rootLogPathString)
						.append("] is marked read-only").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		}
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOG_PATH,
				rootLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);
		
		/////////////////////////////////////////
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

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		installedBasePath = new File("D:\\gitcodda");
		
		if (! installedBasePath.exists()) {
			fail("the installed path doesn't exist");
		}
		
		if (! installedBasePath.isDirectory()) {
			fail("the installed path isn't a directory");
		}
		
		String installedPathString = new StringBuilder(installedBasePath.getAbsolutePath())
		.append(File.separator)
		.append(CommonStaticFinalVars.ROOT_PROJECT_NAME).toString();
				
		installedPath = new File(installedPathString);
		
		if (! installedPath.exists()) {
			fail("the installed path doesn't exist");
		}
		
		if (! installedPath.isDirectory()) {
			fail("the installed path isn't a directory");
		}
		
		wasLibPath = new File("D:\\apache-tomcat-8.5.32\\lib");
		// wasLibPath = new File("/usr/share/tomcat8/lib");
		if (! wasLibPath.exists()) {
			fail("the was libaray path doesn't exist");
		}
		
		if (! wasLibPath.isDirectory()) {
			fail("the was libaray path isn't a directory");
		}
				
		System
				.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME,
						mainProjectName);
		System
				.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH,
						installedPathString);
		
		try {
			setupLogbackEnvromenetVariable(installedPathString, mainProjectName);
		} catch(IllegalArgumentException | IllegalStateException e) {
			fail(e.getMessage());
		}
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
	public void testSummerNoteConfiguration_theParameterFontNameListIsNull() {
		try {
			new SummerNoteConfiguration(null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter fontNameList is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSummerNoteConfiguration_theParameterFontNameListIsEmpty() {
		try {
			SummerNoteConfiguration summerNoteConfiguration = new SummerNoteConfiguration(new String[0]);
			
			boolean expected = false;
			
			assertEquals(expected, summerNoteConfiguration.isFontNames());
			
			String acutalInitializationOptionsString = summerNoteConfiguration.buildInitializationOptionsString(0);
			
			log.info(acutalInitializationOptionsString);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	
	@Test
	public void testSummerNoteConfiguration_theParameterFontNameListIsNotEmpty() {
		try {
			SummerNoteConfiguration summerNoteConfiguration = new SummerNoteConfiguration(new String[] {"Arial"});
			
			boolean expected = true;
			
			assertEquals(expected, summerNoteConfiguration.isFontNames());
			
			String acutalInitializationOptionsString = summerNoteConfiguration.buildInitializationOptionsString(0);
			
			log.info(acutalInitializationOptionsString);
			
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}
