package junitlib;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class AbstractJunitTest {
	protected Logger log = LoggerFactory.getLogger("kr.pe.codda");
	
	// protected static File installedBasePath = null;
	private final static String installedPathString = "D:\\gitcodda\\codda2"; 
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
		
		java.util.logging.LogManager.getLogManager().reset();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		
				
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

	@Before
	public void setUp() {		
	}

	@After
	public void tearDown() {		
	}
}
