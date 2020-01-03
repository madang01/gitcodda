package kr.pe.codda.weblib;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.fileupload.FileItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class FileItemMockTest {

	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final static String installedPathString = "D:\\gitcodda\\codda2";	
	private final static File installedPath = new File(installedPathString);
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

//////////////////////////////////////////////////

		if (!installedPath.exists()) {
			fail("the installed path doesn't exist");
		}

		if (!installedPath.isDirectory()) {
			fail("the installed path isn't a directory");
		}

		wasLibPath = new File("D:\\apache-tomcat-8.5.32\\lib");
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
	public void testOK() {
		String selectedUploadFilePathString = new StringBuilder()
				.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath.getAbsolutePath(),
						mainProjectName))
				.append(File.separator).append("images").append(File.separator).append("sinnori_server_framework01.png")
				.toString();

		File selectedUploadFile = new File(selectedUploadFilePathString);

		if (!selectedUploadFile.exists()) {
			String errorMessage = new StringBuilder().append("업로드할 대상 파일[").append(selectedUploadFilePathString)
					.append("]이 존재하지 않습니다").toString();

			log.warning(errorMessage);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}

		if (!selectedUploadFile.isFile()) {
			String errorMessage = new StringBuilder().append("업로드할 대상 파일[").append(selectedUploadFilePathString)
					.append("]이 일반 파일이 아닙니다").toString();

			log.warning(errorMessage);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}

		FileItem fileItem = null;
		try {
			fileItem = new FileItemMock(selectedUploadFile, "newAttachedFile");
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to create a instance of FileItemMock class", e);
			fail("fail to create a instance of FileItemMock class");
		}

		log.info(fileItem.toString());
	}

}
