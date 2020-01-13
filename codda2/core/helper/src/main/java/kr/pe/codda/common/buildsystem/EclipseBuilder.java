package kr.pe.codda.common.buildsystem;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.common.util.CommonStaticUtil;

public class EclipseBuilder {
	private Logger log = LoggerFactory.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private String installedPathString = null;

	public EclipseBuilder(String installedPathString) throws BuildSystemException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}

		checkValidPath("the installed path", installedPathString);

		this.installedPathString = installedPathString;
	}

	private void checkValidPath(String title, String targetPathString) throws BuildSystemException {
		File targetPath = new File(targetPathString);
		if (!targetPath.exists()) {
			String errorMessage = new StringBuilder(title).append("[").append(targetPathString)
					.append("] does not exist").toString();
			throw new BuildSystemException(errorMessage);
		}

		if (!targetPath.isDirectory()) {
			String errorMessage = new StringBuilder(title).append("[").append(targetPathString)
					.append("] isn't a directory").toString();
			throw new BuildSystemException(errorMessage);
		}
	}
	
	private void saveCoddaAllEclipeProjectXMLFile(String eclipseProjectName, EclipsePath[] eclipsePathList)
			throws BuildSystemException {
		String commonResourcesPathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);

		String coddaCoreAllEclipeProjectXMLFilePathString = new StringBuilder().append(commonResourcesPathString)
				.append(File.separator).append("eclipse").append(File.separator).append("workbench")
				.append(File.separator).append(eclipseProjectName).append(File.separator).append(".project").toString();

		// log.info("coddaCoreAllEclipeProjectXMLFilePathString=[{}]", coddaCoreAllEclipeProjectXMLFilePathString);

		File coddaCoreAllEclipeProjectXMLFile = new File(coddaCoreAllEclipeProjectXMLFilePathString);

		StringBuilder coddaCoreAllEclipeProjectXMLFileContentsStringBuilder = new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(CommonStaticFinalVars.NEWLINE)
				.append("<projectDescription>").append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<name>")
				.append(eclipseProjectName)
				.append("</name>")
				.append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<comment></comment>").append(CommonStaticFinalVars.NEWLINE).append("	").append("<projects>")
				.append(CommonStaticFinalVars.NEWLINE).append("	").append("</projects>")
				.append(CommonStaticFinalVars.NEWLINE).append("	").append("<buildSpec>")
				.append(CommonStaticFinalVars.NEWLINE).append("		").append("<buildCommand>")
				.append(CommonStaticFinalVars.NEWLINE).append("			")
				.append("<name>org.eclipse.jdt.core.javabuilder</name>").append(CommonStaticFinalVars.NEWLINE)
				.append("			").append("<arguments>").append(CommonStaticFinalVars.NEWLINE).append("			")
				.append("</arguments>").append(CommonStaticFinalVars.NEWLINE).append("		").append("</buildCommand>")
				.append(CommonStaticFinalVars.NEWLINE).append("	").append("</buildSpec>")
				.append(CommonStaticFinalVars.NEWLINE).append("	").append("<natures>")
				.append(CommonStaticFinalVars.NEWLINE).append("		")
				.append("<nature>org.eclipse.jdt.core.javanature</nature>").append(CommonStaticFinalVars.NEWLINE)
				.append("	").append("</natures>").append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<linkedResources>");

		String installedPathStringForEclipseRelativePath = installedPathString.replaceAll("\\\\", "/");

		for (EclipsePath eclipsePath : eclipsePathList) {
			coddaCoreAllEclipeProjectXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE)
					.append("		").append("<link>").append(CommonStaticFinalVars.NEWLINE).append("			")
					.append("<name>").append(eclipsePath.getPathName()).append("</name>")
					.append(CommonStaticFinalVars.NEWLINE).append("			").append("<type>2</type>")
					.append(CommonStaticFinalVars.NEWLINE).append("			").append("<location>")
					.append(installedPathStringForEclipseRelativePath).append("/").append(eclipsePath.getRelativePath())
					.append("</location>").append(CommonStaticFinalVars.NEWLINE).append("		").append("</link>");
		}

		coddaCoreAllEclipeProjectXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("</linkedResources>").append(CommonStaticFinalVars.NEWLINE).append("</projectDescription>");

		String coddaCoreAllEclipeProjectXMLFileContents = coddaCoreAllEclipeProjectXMLFileContentsStringBuilder
				.toString();

		// log.info("coddaCoreAllEclipeProjectXMLFileContents=[{}]", coddaCoreAllEclipeProjectXMLFileContents);

		
		try {
			CommonStaticUtil.saveFile(coddaCoreAllEclipeProjectXMLFile, coddaCoreAllEclipeProjectXMLFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to create the eclise .project file[")
					.append(coddaCoreAllEclipeProjectXMLFilePathString)
					.append("] becase io error occured").toString();
			
			log.warn(errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
	}

	private void saveCoddaAllEclipeClasspathXMLFile(String eclipseProjectName, EclipsePath[] eclipsePathList,
			String[] eclipseLibiaryRelativePathStringList) throws BuildSystemException {
		String commonResourcesPathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);

		String coddaCoreAllEclipeClasspathXMLFilePathString = new StringBuilder().append(commonResourcesPathString)
				.append(File.separator).append("eclipse").append(File.separator).append("workbench")
				.append(File.separator).append(eclipseProjectName).append(File.separator).append(".classpath")
				.toString();

		// log.info("coddaCoreAllEclipeClasspathXMLFilePathString=[{}]", coddaCoreAllEclipeClasspathXMLFilePathString);

		File coddaCoreAllEclipeClasspathXMLFile = new File(coddaCoreAllEclipeClasspathXMLFilePathString);

		StringBuilder coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder = new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(CommonStaticFinalVars.NEWLINE)
				.append("<classpath>").append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.junit.JUNIT_CONTAINER/4\"/>")
				.append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>")
				.append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<classpathentry kind=\"output\" path=\"bin\"/>");

		for (EclipsePath eclipsePath : eclipsePathList) {
			coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE).append("	")
					.append("<classpathentry kind=\"src\" path=\"").append(eclipsePath.getPathName()).append("\"/>");
		}

		String installedPathStringForEclipseRelativePath = installedPathString.replaceAll("\\\\", "/");

		for (String eclipseLibiaryRelativePathString : eclipseLibiaryRelativePathStringList) {
			coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE).append("	")
					.append("<classpathentry kind=\"lib\" path=\"").append(installedPathStringForEclipseRelativePath)
					.append("/").append(eclipseLibiaryRelativePathString).append("\"/>");
		}

		coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE)
				.append("</classpath>");

		String coddaCoreAllEclipeClasspathXMLFileContentsString = coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder
				.toString();

		// log.info("coddaCoreAllEclipeClasspathXMLFileContentsString=[{}]", coddaCoreAllEclipeClasspathXMLFileContentsString);
		
		
		try {
			CommonStaticUtil.saveFile(coddaCoreAllEclipeClasspathXMLFile, coddaCoreAllEclipeClasspathXMLFileContentsString,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to create the eclise .classpath file[")
					.append(coddaCoreAllEclipeClasspathXMLFilePathString)
					.append("] becase io error occured").toString();
			
			log.warn(errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
	}
	

	public void saveCoddaCoreAllEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda2_core_all";

		EclipsePath[] eclipsePathList = { new EclipsePath("core_common_main_src", "core/common/src/main/java"),				
				new EclipsePath("core_client_main_src", "core/client/src/main/java"),
				new EclipsePath("core_server_main_src", "core/server/src/main/java"),
				new EclipsePath("core_all_test_src", "core/all/src/test/java") };

		String[] eclipseLibiaryRelativePathStringList = { 
				"core/common/lib/main/ex/gson-2.8.5.jar",
				"core/server/lib/main/ex/commons-dbcp2-2.0.1.jar",
				"core/server/lib/main/ex/commons-pool2-2.5.0.jar",
				
				"core/common/lib/test/byte-buddy-1.7.9.jar", 
				"core/common/lib/test/byte-buddy-agent-1.7.9.jar",
				"core/common/lib/test/objenesis-2.6.jar", 
				"core/common/lib/test/mockito-core-2.13.4.jar"};

		saveCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		saveCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
	

	public void saveCoddaCoreHelperEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda2_helper";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "core/helper/src/main/java"),
				new EclipsePath("test_src", "core/helper/src/test/java")};

		String[] eclipseLibiaryRelativePathStringList = { 
				"core/helper/lib/main/in/codda-core-common.jar",
				
				"core/helper/lib/main/in/jgoodies-common.jar",
				"core/helper/lib/main/in/jgoodies-forms.jar",
				"core/helper/lib/main/in/commons-cli-1.4.jar",
				"core/helper/lib/main/in/commons-io-2.6.jar",				
				
				"core/helper/lib/main/in/logback-classic-1.2.3.jar",
				"core/helper/lib/main/in/logback-core-1.2.3.jar",
				"core/helper/lib/main/in/slf4j-api-1.7.25.jar",
				"core/helper/lib/main/in/jcl-over-slf4j-1.7.25.jar",
				"core/helper/lib/main/in/jul-to-slf4j-1.7.26.jar",				
				
				"core/helper/lib/test/commons-exec-1.3.jar",
				
				"core/helper/lib/test/byte-buddy-1.7.9.jar",
				"core/helper/lib/test/byte-buddy-agent-1.7.9.jar",
				"core/helper/lib/test/objenesis-2.6.jar",
				"core/helper/lib/test/mockito-core-2.13.4.jar"
		};

		saveCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		saveCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
	
	public void saveCoddaSampleBaseServerEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda2_sample_base_server";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "project/sample_base/server_build/src/main/java"),
				new EclipsePath("test_src", "project/sample_base/server_build/src/test/java")};

		String[] eclipseLibiaryRelativePathStringList = { 
				"project/sample_base/server_build/corelib/in/codda-core-all.jar",
				
				"project/sample_base/server_build/corelib/ex/gson-2.8.5.jar",				
				"project/sample_base/server_build/corelib/ex/commons-dbcp2-2.0.1.jar",
				"project/sample_base/server_build/corelib/ex/commons-pool2-2.5.0.jar",
				
				"project/sample_base/server_build/lib/main/ex/logback-classic-1.2.3.jar",
				"project/sample_base/server_build/lib/main/ex/logback-core-1.2.3.jar",
				"project/sample_base/server_build/lib/main/ex/slf4j-api-1.7.25.jar",
				"project/sample_base/server_build/lib/main/ex/jcl-over-slf4j-1.7.25.jar",
				"project/sample_base/server_build/lib/main/ex/jul-to-slf4j-1.7.26.jar",
				
				
				"project/sample_base/server_build/lib/main/ex/oracle-mail-1.4.7.jar",
				
				"project/sample_base/server_build/lib/main/ex/jooq-3.10.6.jar",
				"project/sample_base/server_build/lib/main/ex/jooq-codegen-3.10.6.jar",
				"project/sample_base/server_build/lib/main/ex/jooq-meta-3.10.6.jar",
				"project/sample_base/server_build/lib/main/ex/mariadb-java-client-2.4.1.jar",				
				
				"project/sample_base/server_build/lib/test/byte-buddy-1.7.9.jar", 
				"project/sample_base/server_build/lib/test/byte-buddy-agent-1.7.9.jar",				
				"project/sample_base/server_build/lib/test/objenesis-2.6.jar",
				"project/sample_base/server_build/lib/test/mockito-core-2.13.4.jar",
				"project/sample_base/server_build/lib/test/greenmail-1.5.11.jar",
		};

		saveCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		saveCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
	
	public void saveCoddaSampleBaseAppClientEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda2_sample_base_appclient";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "project/sample_base/client_build/app_build/src/main/java"),
				new EclipsePath("test_src", "project/sample_base/client_build/app_build/src/test/java")};

		String[] eclipseLibiaryRelativePathStringList = {
				"project/sample_base/client_build/app_build/corelib/in/codda-core-all.jar",
				
				"project/sample_base/client_build/app_build/corelib/in/gson-2.8.5.jar",

				"project/sample_base/client_build/app_build/corelib/in/commons-dbcp2-2.0.1.jar",
				"project/sample_base/client_build/app_build/corelib/in/commons-pool2-2.5.0.jar",				
				
				"project/sample_base/client_build/app_build/lib/main/in/logback-classic-1.2.3.jar",
				"project/sample_base/client_build/app_build/lib/main/in/logback-core-1.2.3.jar",
				"project/sample_base/client_build/app_build/lib/main/in/slf4j-api-1.7.25.jar",
				"project/sample_base/client_build/app_build/lib/main/in/jcl-over-slf4j-1.7.25.jar",
				"project/sample_base/client_build/app_build/lib/main/in/jul-to-slf4j-1.7.26.jar"
		};

		saveCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		saveCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
	
	public void saveCoddaSampleBaseWebClientEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda2_sample_base_webclient";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "project/sample_base/client_build/web_build/src/main/java"),
				new EclipsePath("test_src", "project/sample_base/client_build/web_build/src/test/java"),
				new EclipsePath("WWW_ROOT", "project/sample_base/user_web_app_base/ROOT"),
				new EclipsePath("ADMIN_ROOT", "project/sample_base/admin_web_app_base/ROOT")};

		String[] eclipseLibiaryRelativePathStringList = {
				"project/sample_base/client_build/web_build/corelib/ex/codda-core-all.jar",
				
				"project/sample_base/client_build/web_build/corelib/ex/gson-2.8.5.jar",

				"project/sample_base/client_build/web_build/corelib/ex/commons-dbcp2-2.0.1.jar",
				"project/sample_base/client_build/web_build/corelib/ex/commons-pool2-2.5.0.jar",				
				
				"project/sample_base/client_build/web_build/lib/main/ex/commons-fileupload-1.3.2.jar",
				"project/sample_base/client_build/web_build/lib/main/ex/commons-io-2.6.jar",
				"project/sample_base/client_build/web_build/lib/main/ex/commons-lang3-3.7.jar",
				"project/sample_base/client_build/web_build/lib/main/ex/commons-text-1.3.jar",				
				"project/sample_base/client_build/web_build/lib/main/ex/jsoup-1.12.1.jar",
				"project/sample_base/client_build/web_build/lib/main/ex/simplecaptcha-1.2.1.jar",
				
				
				"project/sample_base/client_build/web_build/lib/test/httpcore-4.4.10.jar",
				"project/sample_base/client_build/web_build/lib/test/httpmime-4.5.6.jar",
				
				"project/sample_base/client_build/web_build/lib/test/byte-buddy-1.7.9.jar",
				"project/sample_base/client_build/web_build/lib/test/byte-buddy-agent-1.7.9.jar",				 
				"project/sample_base/client_build/web_build/lib/test/objenesis-2.6.jar",
				"project/sample_base/client_build/web_build/lib/test/mockito-core-2.13.4.jar",
				
				"project/sample_base/client_build/web_build/lib/test/logback-classic-1.2.3.jar",
				"project/sample_base/client_build/web_build/lib/test/logback-core-1.2.3.jar",
				"project/sample_base/client_build/web_build/lib/test/slf4j-api-1.7.25.jar",
				"project/sample_base/client_build/web_build/lib/test/jcl-over-slf4j-1.7.25.jar",
				"project/sample_base/client_build/web_build/lib/test/jul-to-slf4j-1.7.26.jar"								
		};

		saveCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		saveCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
}
