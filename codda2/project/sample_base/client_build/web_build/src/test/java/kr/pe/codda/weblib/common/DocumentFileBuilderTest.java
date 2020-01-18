package kr.pe.codda.weblib.common;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class DocumentFileBuilderTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private static String installedPathString = null;
	private static File installedPath = null;
	private static File wasLibPath = null;
	private final static String mainProjectName = "sample_base";
	
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
		
		////////////////////////
		
		
		
		installedPathString = "D:\\gitcodda\\codda2";
		
		installedPath = new File(installedPathString);
		
		if (!installedPath.exists()) {
			fail("the installed path doesn't exist");
		}

		
		if (!installedPath.isDirectory()) {
			fail("the installed path isn't a directory");
		}

		wasLibPath = new File("D:\\apache-tomcat-8.5.50\\lib");
		// wasLibPath = new File("/usr/share/tomcat8/lib");
		if (!wasLibPath.exists()) {
			fail("the was libaray path doesn't exist");
		}

		if (!wasLibPath.isDirectory()) {
			fail("the was libaray path isn't a directory");
		}

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME, mainProjectName);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH, installedPathString);
	}

	@Test
	public void test() {
		AccessedUserInformation accessedUserInformation = new AccessedUserInformation(false, null, null, null);
		
		
		String paramFileName = "CoddaHowTo.html";
		String relativeURL = "/sitemenu/doc/" + paramFileName;
		String title = "코다 데비안 개발환경 구축 HOWTO";
		String contents = "<h2>0. 시작하기 앞서 알아 두어야할 사항</h2>";
		
		
		
		String documentFileContents = DocumentFileBuilder.build(accessedUserInformation, relativeURL, title, contents);
		
		
		
		log.info(documentFileContents);
		
		String documentFilePathString = new StringBuilder()
				.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPathString, mainProjectName))
				.append(File.separatorChar)
				.append("sitemenu")
				.append(File.separatorChar)
				.append("doc")
				.append(File.separatorChar)
				.append(paramFileName).toString();
		
		log.info(documentFilePathString);
		
	}

}
