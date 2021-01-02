package kr.pe.codda.common.buildsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import kr.pe.codda.common.buildsystem.pathsupporter.AppClientBuildSystemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.WebClientBuildSystemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.part.RunningProjectConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.message.builder.IOPartDynamicClassFileContentsBuilderManager;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.codda.common.type.LogType;
import kr.pe.codda.common.type.MessageTransferDirectionType;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.common.util.SequencedPropertiesUtil;

/**
 * 프로젝트 빌더
 * 
 * @author Won Jonghoon
 *
 */
public class ProjectBuilder {
	// private Logger log = Logger.getGlobal();
	
	public static final String AUTHOR = "Won Jonghoon";
	public static final String JVM_OPTIONS_OF_SERVER = "-server -Xmx2048m -Xms1024m";
	public static final String JVM_OPTIONS_OF_APP_CLIENT = "-Xmx2048m -Xms1024m";
	
	private final String[] messageIDList = {"Echo", "PublicKeyReq",  "PublicKeyRes"};

	private String mainProjectName;
	private String installedPathString;

	private String projectPathString;

	public ProjectBuilder(String installedPathString, String mainProjectName) throws BuildSystemException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}
		
		checkValidPath("the installed path", installedPathString);
		
		
		String projectBasePathString = ProjectBuildSytemPathSupporter.getProjectBasePathString(installedPathString);
		
		checkValidPath("the project base path", projectBasePathString);
				
		
		String projectPathString= ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName);
		
		File projectPath = new File(projectPathString);
		
		
		if (projectPath.exists()) {
			if (! projectPath.isDirectory()) {
				String errorMessage = new StringBuilder("the project path[")
						.append(projectPathString).append("] isn't a directory")
						.toString();
				throw new BuildSystemException(errorMessage);
			}
		} else {
			/** if not exist then nothing */
		}
				
		
		this.installedPathString = installedPathString;
		this.mainProjectName = mainProjectName;
		this.projectPathString = projectPathString;	
	}
	
	private void checkValidPath(String title, String targetPathString) throws BuildSystemException {
		File targetPath = new File(targetPathString);
		if (!targetPath.exists()) {
			String errorMessage = new StringBuilder(title).append("[")
					.append(targetPathString).append("] does not exist")
					.toString();
			throw new BuildSystemException(errorMessage);
		}

		if (!targetPath.isDirectory()) {
			String errorMessage = new StringBuilder(title).append("[")
					.append(targetPathString).append("] isn't a directory")
					.toString();
			throw new BuildSystemException(errorMessage);
		}
	}
	
	
	/**
	 * @return only whether the project path exists. Warning! this method does not care whether the project path is a directory. 
	 */
	public boolean whetherOnlyProjectPathExists() {
		String projectPathString= ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName);
		
		File projectPath = new File(projectPathString);
		
		return projectPath.exists();
	}
	
	public boolean whetherOnlyServerBuildPathExists() {
		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);
		File serverBuildPath = new File(serverBuildPathString);
		return serverBuildPath.exists();
	}
	
	public boolean whetherOnlyAppClientBuildPathExists() {
		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);
		File appClientBuildPath = new File(appClientBuildPathString);
		return appClientBuildPath.exists();
	}
	
	public boolean whetherOnlyWebClientBuildPathExists() {
		String webClientBuildPathString = WebClientBuildSystemPathSupporter.getWebClientBuildPathString(installedPathString, mainProjectName);
		File webClientBuildPath = new File(webClientBuildPathString);
		return webClientBuildPath.exists();
	}
	
	public boolean whetherOnlyWebRootPathExists() {
		String webRootPathString = WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPathString, mainProjectName);
		File webRootPath = new File(webRootPathString);
		return webRootPath.exists();
	}
	
	
			
	
	public boolean isValidServerAntBuildXMLFile() {
		String serverAntBuildXMLFilePathString = ServerBuildSytemPathSupporter.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);
		File serverAntBuildXMLFile = new File(serverAntBuildXMLFilePathString);
		
		if (serverAntBuildXMLFile.exists() && serverAntBuildXMLFile.isFile()) {
			return true;
		}
		
		// log.info("the server ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", serverAntBuildXMLFilePathString, serverAntBuildXMLFile.exists(), serverAntBuildXMLFile.isFile());
		
		return false;
	}
	
	public boolean isValidAppClientAntBuildXMLFile() {
		String appClientAntBuildFilePathString = AppClientBuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		File appClientAntBuildXMLFile = new File(appClientAntBuildFilePathString);
		
		if (appClientAntBuildXMLFile.exists() && appClientAntBuildXMLFile.isFile()) {
			return true;
		}
		
		// log.info("the app client ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", appClientAntBuildFilePathString, appClientAntBuildXMLFile.exists(), appClientAntBuildXMLFile.isFile());
		
		return false;
	}
	
	public boolean isValidWebClientAntBuildXMLFile() {
		String webClientAntBuildFilePathString = WebClientBuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		File webClientAntBuildXMLFile = new File(webClientAntBuildFilePathString);
		
		if (webClientAntBuildXMLFile.exists() && webClientAntBuildXMLFile.isFile()) {
			return true;
		}
		
		// log.info("the web client ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", webClientAntBuildFilePathString, webClientAntBuildXMLFile.exists(), webClientAntBuildXMLFile.isFile());
		
		return false;
	}

	public boolean isValidWebRootXMLFile() {
		String webRootXMLFilePathString = WebRootBuildSystemPathSupporter.getUserWebRootXMLFilePathString(installedPathString, mainProjectName);
		File webRootXMLFile = new File(webRootXMLFilePathString);
		
		if (webRootXMLFile.exists() && webRootXMLFile.isFile()) {
			return true;
		}
		
		// log.info("the web.xml file[{}] located at web root directory is bad :: whetherExist[{}] isFile[{}]",  webRootXMLFilePathString, webRootXMLFile.exists(), webRootXMLFile.isFile());
		
		return false;
	}
	
	
	public MainProjectBuildSystemState getNewInstanceOfMainProjectBuildSystemState() throws BuildSystemException {
		String projectPathString= ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName);
		
		File projectPath = new File(projectPathString);
		if (!projectPath.exists()) {
			String errorMessage = new StringBuilder("the project path[")
					.append(projectPathString).append("] does not exist")
					.toString();
			throw new BuildSystemException(errorMessage);
		}

		if (!projectPath.isDirectory()) {
			String errorMessage = new StringBuilder("the project path[")
					.append(projectPathString).append("] isn't a directory")
					.toString();
			throw new BuildSystemException(errorMessage);
		}
		
		String servletSystemLibrayPathString = null;
		boolean isServer = false;
		boolean isAppClient = false;
		boolean isWebClient = false;
		List<String> subProjectNameList = null;
		List<String> dbcpNameList = null;
		SequencedProperties configurationSequencedPropties = null;
		
		isServer = isValidServerAntBuildXMLFile();
		isAppClient = isValidAppClientAntBuildXMLFile();
		isWebClient = isValidWebClientAntBuildXMLFile();
		
		if (isWebClient) {
			if (!isValidWebRootXMLFile()) {
				String webXMLFilePathString = WebRootBuildSystemPathSupporter.getUserWebRootXMLFilePathString(installedPathString, mainProjectName);
				
				String errorMessage = String.format(
						"the project's WEB-INF/web.xml[%s] file doesn't exist",
						webXMLFilePathString);
				throw new BuildSystemException(errorMessage);
			}
			
			Properties webClientAntProperties = loadValidWebClientAntPropertiesFile();		
			
			servletSystemLibrayPathString = webClientAntProperties.getProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY);
		}
		
		if (null == servletSystemLibrayPathString) servletSystemLibrayPathString = "";		
		
		
		CoddaConfiguration coddaConfiguration = null;
		
		try {
			coddaConfiguration = new CoddaConfiguration(installedPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknow error, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
		
		RunningProjectConfiguration runningProjectConfiguration = coddaConfiguration.getRunningProjectConfiguration();
		
		dbcpNameList = runningProjectConfiguration.DBCP.getNameList();
		subProjectNameList = runningProjectConfiguration.SUBPROJECT.getNameList();	
				
		try {
			configurationSequencedPropties = coddaConfiguration.loadConfigFile();
		} catch (IllegalArgumentException e) {
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder().append("config file not found, errmsg=")
					.append(e.getMessage()).toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		} catch (PartConfigurationException e) {
			String errorMessage = new StringBuilder().append("bad config file, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("io error, errmsg=")
					.append(e.getMessage()).toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknow error, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
		
		return new MainProjectBuildSystemState(installedPathString, mainProjectName,
				isServer, isAppClient, isWebClient, servletSystemLibrayPathString,
				dbcpNameList, subProjectNameList, configurationSequencedPropties);
	}

	
	public void createProject(boolean isServer, boolean isAppClient, boolean isWebClient, 
			String servletSystemLibraryPathString) throws BuildSystemException {
		if (!isServer && !isAppClient && !isWebClient) {
			throw new IllegalArgumentException("You must choose one more build system type but isSerer=false, isAppClient=false, isWebClient=false");
		}
		
		if (null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		if (whetherOnlyProjectPathExists()) {
			String errorMessage = String.format("Warning! this project can't be created because the project path[%s] exists", projectPathString);
			throw new BuildSystemException(errorMessage);
		}
		
		createChildDirectories(isServer, isAppClient, isWebClient);
		createFiles(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
	}
	
	private void createCommonChildDirectories() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s common child direcotry creation task start");

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("config");
		childRelativeDirectoryList.add("resources/dbcp");
		childRelativeDirectoryList.add("resources/message_info");
		childRelativeDirectoryList.add("resources/rsa_keypair");
		
		for (LogType logType : LogType.values()) {
			childRelativeDirectoryList.add(new StringBuilder("log/").append(logType.toString().toLowerCase()).toString());
		}
				
		createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[" + mainProjectName + "]'s common child direcotry creation task end");
	}
	
	private void createServerBuildChildDirectories() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s server child direcotry creation task start");

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("server_build/APP-INF/classes");
		// childRelativeDirectoryList.add("server_build/APP-INF/resources");
		childRelativeDirectoryList.add("server_build/corelib/ex");
		childRelativeDirectoryList.add("server_build/corelib/in");
		childRelativeDirectoryList.add("server_build/lib/main/ex");
		childRelativeDirectoryList.add("server_build/lib/main/in");
		childRelativeDirectoryList.add("server_build/lib/test");
		
		childRelativeDirectoryList.add(new StringBuilder("server_build/")
				.append(ProjectBuildSytemPathSupporter.getServerTaskDirectoryRelativePath("/")).toString());
		childRelativeDirectoryList.add(new StringBuilder("server_build/")
				.append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/")).toString());		
		childRelativeDirectoryList.add("server_build/src/main/java/main");
		childRelativeDirectoryList.add("server_build/src/test/java");
		childRelativeDirectoryList.add("server_build/build");
		childRelativeDirectoryList.add("server_build/dist");
		
		createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[" + mainProjectName + "]'s server child direcotry creation task end");
	}
	
	private void createAppBuildChildDirectories() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s application child direcotry creation task start");
		
		
		

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("client_build/app_build/corelib/ex");
		childRelativeDirectoryList.add("client_build/app_build/corelib/in");
		childRelativeDirectoryList.add("client_build/app_build/lib/main/ex");
		childRelativeDirectoryList.add("client_build/app_build/lib/main/in");
		childRelativeDirectoryList.add("client_build/app_build/lib/test");
		
		childRelativeDirectoryList.add(
				new StringBuilder("client_build/app_build/").append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/")).toString());
		childRelativeDirectoryList.add("client_build/app_build/src/main/java/main");
		childRelativeDirectoryList.add("client_build/app_build/src/test/java");
		childRelativeDirectoryList.add("client_build/app_build/build");
		childRelativeDirectoryList.add("client_build/app_build/dist");
		
		createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[" + mainProjectName + "]'s application child direcotry creation task end");
	}
	
	private void createWebBuildChildDirectories() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web application child direcotry creation task start");

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("client_build/web_build/corelib/ex");
		childRelativeDirectoryList.add("client_build/web_build/corelib/in");
		childRelativeDirectoryList.add("client_build/web_build/lib/main/ex");
		childRelativeDirectoryList.add("client_build/web_build/lib/main/in");
		childRelativeDirectoryList.add("client_build/web_build/lib/test");
		childRelativeDirectoryList.add(
				new StringBuilder("client_build/web_build/").append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/")).toString());
		childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
				.append(ProjectBuildSytemPathSupporter.getJavaSourceBaseDirectoryRelativePath("/"))
				.append("/servlet").toString());
		childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
				.append(ProjectBuildSytemPathSupporter.getJavaSourceBaseDirectoryRelativePath("/"))
				.append("/weblib/common").toString());		
		childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
				.append(ProjectBuildSytemPathSupporter.getJavaSourceBaseDirectoryRelativePath("/"))
				.append("/weblib/htmlstring").toString());
		childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
				.append(ProjectBuildSytemPathSupporter.getJavaSourceBaseDirectoryRelativePath("/"))
				.append("/weblib/jdf").toString());
		childRelativeDirectoryList.add("client_build/web_build/src/test/java");
		childRelativeDirectoryList.add("client_build/web_build/build");
		childRelativeDirectoryList.add("client_build/web_build/dist");
		
		createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[" + mainProjectName + "]'s web application child direcotry creation task end");
	}
	
	private void createWebRootChildDirectories() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web root child direcotry creation task start");
		

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("web_app_base/upload");
		childRelativeDirectoryList.add("web_app_base/ROOT/WEB-INF/classes");
		childRelativeDirectoryList.add("web_app_base/ROOT/WEB-INF/lib");
		
		createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[" + mainProjectName + "]'s web root child direcotry creation task end");
	}
	
	private void createChildDirectories(boolean isServer, boolean isAppClient, boolean isWebClient)
			throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s child direcotry creation task start");
		createCommonChildDirectories();

		if (isServer) {
			createServerBuildChildDirectories();

		}

		if (isAppClient) {
			createAppBuildChildDirectories();
		}

		if (isWebClient) {
			createWebBuildChildDirectories();
			createWebRootChildDirectories();
		}

		

		log.info("main project[" + mainProjectName + "]'s child direcotry creation task end");
	}

	private void createFiles(boolean isServer, boolean isAppClient, boolean isWebClient, 
			String servletSystemLibraryPathString) throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s file creation task start");
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		createNewConfigFile();
		copySampleProjectResorucesToProjectResources();

		if (isServer) {
			createServerBuildSystemFiles();
		}

		if (isAppClient) {
			createAppClientBuildSystemFiles();
		}

		if (isWebClient) {
			createWebClientBuildSystemFiles(servletSystemLibraryPathString);
			createWebRootSampleFiles();
		}
		log.info("main project[" + mainProjectName + "]'s file creation task end");
	}
	
	private void copySampleProjectResorucesToProjectResources() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("the mainproject[" + mainProjectName + "]'s resources directory copy task start");

		String commonResourcesPathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		String projectResorucesPathString = ProjectBuildSytemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);

		String sampleProjectResourcesPathString = new StringBuilder(commonResourcesPathString).append(File.separator)
				.append("newproject").append(File.separator).append("resources").toString();

		String projectResourcesPathString = new StringBuilder(projectResorucesPathString).toString();

		File sampleProjectResourcesPath = new File(sampleProjectResourcesPathString);
		File projectResouresPath = new File(projectResourcesPathString);

		try {
			FileUtils.copyDirectory(sampleProjectResourcesPath, projectResouresPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the sample resoruces directory[")
					.append(sampleProjectResourcesPathString)
					.append("]  to the main project[").append(mainProjectName)
					.append("]'s the resources directory[").append(projectResourcesPathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("the mainproject[" + mainProjectName + "]'s resources directory copy task end");
	}

	private void createWebClientBuildSystemFiles(String servletSystemLibraryPathString) throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project["+ mainProjectName + "]'s web client build system files creation task start");
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}

		copyWebClientSampleFiles();
		createNewWebClientAntBuildXMLFile();
		createNewWebClientAntPropertiesFile(servletSystemLibraryPathString);
		createNewWebClientMessageIOFileSet();		

		log.info("main project["+ mainProjectName + "]'s web client build system files creation task end");
	}
	
	private void deleteWebCientBuildPath() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web client build path deletion task start");
		
		String webClientBuildPathString = WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);
		File webClientBuildPath = new File(webClientBuildPathString);
		
		try {
			FileUtils.forceDelete(webClientBuildPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to delete the web client build path[").append(webClientBuildPathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[" + mainProjectName + "]'s web client build path deletion task end");
	}

	
	private void createWebRootSampleFiles() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web root sample files creation task start");

		copyWebRootSampleFiles();

		log.info("main project[" + mainProjectName + "]'s web root sample files creation task end");
	}
	
	private void deleteWebRoot() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web root path deletion task start");
		
		String webRootPathString = WebRootBuildSystemPathSupporter
				.getUserWebRootPathString(installedPathString, mainProjectName);
		File webRootPath = new File(webRootPathString);
		
		try {
			FileUtils.forceDelete(webRootPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to delete the web root path[").append(webRootPathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[" + mainProjectName + "]'s web root path deletion task end");
	}

	private void copyWebRootSampleFiles() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web root sample files copy task start");

		String commonResourcePathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		String targetWebRootPathString = WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPathString, mainProjectName);

		String sourceWebRootPathString = new StringBuilder(commonResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("web_root").toString();

		File sourceWebRootPath = new File(sourceWebRootPathString);
		File targetWebRootPath = new File(targetWebRootPathString);

		try {
			FileUtils.copyDirectory(sourceWebRootPath, targetWebRootPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the main project[").append(mainProjectName)
					.append("]'s the source directory[").append(sourceWebRootPathString)
					.append("]  having sample source files to the target directory[").append(targetWebRootPathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s web root sample files copy task end");
	}

	private void createNewWebClientAntBuildXMLFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web client ant build.xml file creation task start");

		String webClientAntBuildXMLFilePahtString = WebClientBuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);

		File webClientAntBuildXMLFile = new File(webClientAntBuildXMLFilePahtString);

		try {
			byte[] readBytes = kr.pe.codda.common.util.CommonStaticUtil.readFileToByteArray(webClientAntBuildXMLFile, 1024L*1024L*10L);
			
			String webClientAntBuildXMLFileContents = new String(readBytes, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			
			
			webClientAntBuildXMLFileContents.replace("sample_test_webclient", mainProjectName);
						
			kr.pe.codda.common.util.CommonStaticUtil.overwriteFile(webClientAntBuildXMLFile, webClientAntBuildXMLFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s web client ant build.xml file[").append(webClientAntBuildXMLFilePahtString).append("]")
					.toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s web client ant build.xml file creation task end");
	}

	private void createNewWebClientMessageIOFileSet() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web client message io file set creation task start");

		String webClientBuildPathString = WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);
		
		String messageIOSetBasedirectoryPathString = kr.pe.codda.common.util.CommonStaticUtil
					.buildFilePathStringFromResourcePathAndRelativePathOfFile
					(webClientBuildPathString, ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/"));
		
		
		for (String messageID : messageIDList) {
			createNewMessageIOSet(messageID, AUTHOR, messageIOSetBasedirectoryPathString);
		}

		log.info("main project[" + mainProjectName + "]'s web client message io file set creation task end");
	}

	private void copyWebClientSampleFiles() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		
		log.info("mainproject[" + mainProjectName + "]'s web client sample source files copy task start");

		String commonResourcePathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		String webClientBuildPathString = WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);

		String sourceDirectoryPathString = new StringBuilder(commonResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("web_build").toString();

		String targetDirectoryPathString = new StringBuilder(webClientBuildPathString).toString();

		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);

		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the main project[").append(mainProjectName)
					.append("]'s the source directory[").append(sourceDirectoryPathString)
					.append("]  having sample source files to the target directory[").append(targetDirectoryPathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("mainproject[" + mainProjectName + "]'s web client sample source files copy task end");
	}

	private void createAppClientBuildSystemFiles() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		
		log.info("mainproject[" + mainProjectName + "]'s application client build system files creation task start");

		copyAppClientSampleFiles();
		createNewAppClientAntBuildXMLFile();
		createNewAppClientDosShellFile();
		createNewAppClientUnixShellFile();		
		createNewAppClientAllMessageIOFileSet();

		log.info("mainproject[" + mainProjectName + "]'s application client build system files creation task end");
	}
	
	private void deleteAppClientBuildPath() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("mainproject[" + mainProjectName + "]'s application client build path deletion task start");
		
		String appClientBuildPathString = AppClientBuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName);
		
		File appClientBuildPath = new File(appClientBuildPathString);
		
		try {
			FileUtils.forceDelete(appClientBuildPath);
		} catch (IOException e) {
			String errorMessage = String.format("fail to delete app client build path[%s]", appClientBuildPathString);
			log.log(Level.WARNING, errorMessage, e);
			

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("mainproject[" + mainProjectName + "]'s application client build path deletion task end");
	}

	private void createNewAppClientAntBuildXMLFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s application client ant build.xml file creation task start");

		String appClientAntBuildXMLFilePahtString = AppClientBuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(installedPathString, mainProjectName);

		File appClientAntBuildXMLFile = new File(appClientAntBuildXMLFilePahtString);

		try {
			byte[] readBytes = kr.pe.codda.common.util.CommonStaticUtil.readFileToByteArray(appClientAntBuildXMLFile, 1024L*1024L*10L);
			
			String appClientAntBuildXMLFileContents = new String(readBytes, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			
			appClientAntBuildXMLFileContents.replace("sample_test_appclient", mainProjectName);
						
			
			kr.pe.codda.common.util.CommonStaticUtil.overwriteFile(appClientAntBuildXMLFile, appClientAntBuildXMLFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s app client ant build.xml file[").append(appClientAntBuildXMLFilePahtString).append("]")
					.toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s application client ant build.xml file creation task end");
	}

	private void createNewAppClientDosShellFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s application client dos shell file creation task start");

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String appClientDosShellFileContents = BuildSystemFileContents.getDosShellContents(installedPathString,
				mainProjectName, JVM_OPTIONS_OF_APP_CLIENT, LogType.APPCLIENT, appClientBuildPathString,
				relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientDosShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.bat").toString();
		File appClientDosShellFile = new File(appClientDosShellFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(appClientDosShellFile, appClientDosShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s application client dos shell file[").append(appClientDosShellFilePathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s application client dos shell file creation task end");
	}

	private void overwriteAppClientDosShellFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s application client dos shell file  overwrite task start");

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String appClientDosShellFileContents = BuildSystemFileContents.getDosShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LogType.APPCLIENT, appClientBuildPathString,
				relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientDosShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.bat").toString();
		File appClientDosShellFile = new File(appClientDosShellFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.overwriteFile(appClientDosShellFile, appClientDosShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s application client dos shell file[").append(appClientDosShellFilePathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s application client dos shell file overwrite task end");
	}

	private void createNewAppClientUnixShellFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s application client unix shell file creation task start");

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String appClientUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LogType.APPCLIENT, appClientBuildPathString, relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientUnixShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.sh").toString();
		File appClientUnixShellFile = new File(appClientUnixShellFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(appClientUnixShellFile, appClientUnixShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s application client unix shell file[").append(appClientUnixShellFilePathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s application client unix shell file creation task end");
	}

	private void overwriteAppClientUnixShellFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s application client unix shell file overwrite task start");

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String appClientUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LogType.APPCLIENT, appClientBuildPathString, relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientUnixShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.sh").toString();
		File appClientUnixShellFile = new File(appClientUnixShellFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.overwriteFile(appClientUnixShellFile, appClientUnixShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s application client unix shell file[").append(appClientUnixShellFilePathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s application client unix shell file overwrite task end");
	}

	private void copyAppClientSampleFiles() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("application client sample source files copy task start");

		String commonResourcePathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		String applicationClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String sourceDirectoryPathString = new StringBuilder(commonResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("app_build").toString();

		String targetDirectoryPathString = applicationClientBuildPathString;

		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);

		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the main project[").append(mainProjectName)
					.append("]'s the build directory[").append(sourceDirectoryPathString)
					.append("]  having sample source files to the target directory[").append(targetDirectoryPathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("application client sample source files copy task end");
	}

	private void createNewAppClientAllMessageIOFileSet() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s application client message io file set creation task start");

		String appClientBuildPathString = AppClientBuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName);
		
		String messageIOSetBasedirectoryPathString = kr.pe.codda.common.util.CommonStaticUtil
					.buildFilePathStringFromResourcePathAndRelativePathOfFile
					(appClientBuildPathString, ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/"));
			
		for (String messageID : messageIDList) {
			createNewMessageIOSet(messageID, AUTHOR, messageIOSetBasedirectoryPathString);
		}		

		log.info("main project[" + mainProjectName + "]'s application client message io file set creation task end");
	}

	private void createNewMessageIOSet(String messageID, String author, String messageIOSetBasedirectoryPathString)
			throws BuildSystemException {
		createNewMessageIDDirectory(messageIOSetBasedirectoryPathString, messageID);
		createNewMessageSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
		createNewDecoderSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
		createNewEncoderSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
		createNewServerCodecSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
		createNewClientCodecSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
	}

	private void createServerBuildSystemFiles() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("server build system files creation task start");

		copyServerSampleFiles();
		createNewServerAntBuildXMLFile();
		createNewServerDosShellFile();
		createNewServerUnixShellFile();		
		createServerMessageIOFileSet();

		log.info("server build system files creation task end");
	}
	
	private void deleteServerBuildPath() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("mainproject[" + mainProjectName + "]'s server build path deletion task start");
		
		String serverBuildPathString = ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName);
		
		File serverBuildPath = new File(serverBuildPathString);
		
		try {
			FileUtils.forceDelete(serverBuildPath);
		} catch (IOException e) {
			String errorMessage = String.format("fail to delete server build path[%s]", serverBuildPathString);
			log.log(Level.WARNING, errorMessage, e);
			

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("mainproject[" + mainProjectName + "]'s server build path deletion task end");
	}

	private void createChildDirectoriesOfBasePath(String basePathStrig,
			List<String> childRelativeDirectoryList) throws BuildSystemException {
		
		
		for (String childRelativedirectory : childRelativeDirectoryList) {
			// String relativeDir = childDirectories[i];

			// log.info("relativeDir[{}]=[{}]", i, relativeDir);

			String childRealPathString = null;
			
			
			if (File.separator.equals("/")) {
				childRealPathString = new StringBuilder(basePathStrig)
						.append(File.separator).append(childRelativedirectory).toString();
			} else {
				childRealPathString = new StringBuilder(basePathStrig)
						.append(File.separator).append(childRelativedirectory.replaceAll("/", "\\\\")).toString();
			}
			

			File childRealPath = new File(childRealPathString);
			if (! childRealPath.exists()) {
				try {
					FileUtils.forceMkdir(childRealPath);
				} catch (IOException e) {
					String errorMessage = String.format(
							"fail to create a new path[%s][%s]", basePathStrig, childRelativedirectory);
					
					Logger log = Logger.getGlobal();
					log.log(Level.WARNING, errorMessage, e);
					throw new BuildSystemException(errorMessage);
				}

				Logger log = Logger.getGlobal();
				log.info("the new child relative direcotry[" + basePathStrig + "][" + childRelativedirectory + "] was created successfully");
			} else {
				Logger log = Logger.getGlobal();
				log.info("the child relative direcotry[" + basePathStrig + "][" + childRelativedirectory + "] exist, so nothing");
			}

			if (!childRealPath.isDirectory()) {
				String errorMessage = String.format(
						"the child relative direcotry[%s][%s] is not a real directory", basePathStrig, childRelativedirectory);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!childRealPath.canRead()) {
				String errorMessage = String.format(
						"the child relative direcotry[%s][%s] doesn't hava permission to read",
						basePathStrig, childRelativedirectory);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!childRealPath.canWrite()) {
				String errorMessage = String.format(
						"the child relative direcotry[%s][%s] doesn't hava permission to write",
						basePathStrig, childRelativedirectory);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

		}
	}
	private void createNewMessageIDDirectory(String messageIOSetBasedirectoryPathString, String messageID)
			throws BuildSystemException {
		List<String> childRelativeDirectoryList = new ArrayList<String>();
		childRelativeDirectoryList.add(messageID);
		createChildDirectoriesOfBasePath(messageIOSetBasedirectoryPathString,
				childRelativeDirectoryList);
	}

	private void createServerMessageIOFileSet() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s server message io file set creation task start");

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);
		String messageIOSetBasedirectoryPathString = kr.pe.codda.common.util.CommonStaticUtil
					.buildFilePathStringFromResourcePathAndRelativePathOfFile
					(serverBuildPathString, ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/"));
		

		
		
		for (String messageID : messageIDList) {
			createNewMessageIOSet(messageID, AUTHOR, messageIOSetBasedirectoryPathString);
		}
		
		log.info("main project[" + mainProjectName + "]'s server message io file set creation task end");
	}

	private MessageInfo getMessageInfo(String messageID) throws BuildSystemException {

		String echoMessageInfoFilePathString = new StringBuilder(
				ProjectBuildSytemPathSupporter.getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName))
						.append(File.separator).append(messageID).append(".xml").toString();
		File echoMessageInfoFile = new File(echoMessageInfoFilePathString);

		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			System.exit(1);
		}
		MessageInfo echoMessageInfo = null;
		try {
			echoMessageInfo = messageInfoSAXParser.parse(echoMessageInfoFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = new StringBuilder("fail to parse message information xml file[")
					.append(echoMessageInfoFile.getAbsolutePath()).append("]").toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		return echoMessageInfo;
	}

	private void createNewMessageSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString)
			throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s message file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task start");

		MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String messageFileContnets = ioFileSetContentsBuilderManager.getMessageSourceFileContents(author,
				messageInfo);
		
		String messageFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append(".java").toString();

		File messageFile = new File(messageFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(messageFile, messageFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(messageFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s message file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task end");
	}

	private void createNewDecoderSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString)
			throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s message decoder file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task start");

		MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String decoderFileContnets = ioFileSetContentsBuilderManager.getDecoderSourceFileContents(author,
				messageInfo);
		
		String decoderFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("Decoder.java").toString();

		File decoderFile = new File(decoderFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(decoderFile, decoderFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(decoderFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s message decoder file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task end");
	}

	private void createNewEncoderSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString)
			throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s message encoder file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task start");
		
		MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String encoderFileContnets = ioFileSetContentsBuilderManager.getEncoderSourceFileContents(author,
				messageInfo);
		
		String encoderFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("Encoder.java").toString();

		File encoderFile = new File(encoderFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(encoderFile, encoderFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(encoderFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s message encoder file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task end");
	}

	private void createNewServerCodecSourceFile(String messageID, String author,
			String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s message server codec file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task start");
		// MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String serverCodecFileContnets = ioFileSetContentsBuilderManager.getServerCodecSourceFileContents(
				MessageTransferDirectionType.FROM_ALL_TO_ALL, messageID, author);

		String serverCodecFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("ServerCodec.java").toString();

		File serverCodecFile = new File(serverCodecFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(serverCodecFile, serverCodecFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(serverCodecFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s message server codec file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task end");
	}

	private void createNewClientCodecSourceFile(String messageID, String author,
			String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s message client codec file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task start");
		// MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String clientCodecFileContnets = ioFileSetContentsBuilderManager.getClientCodecSourceFileContents(
				MessageTransferDirectionType.FROM_ALL_TO_ALL, messageID, author);

		String clientCodecFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("ClientCodec.java").toString();

		File clientCodecFile = new File(clientCodecFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(clientCodecFile, clientCodecFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(clientCodecFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s message client codec file[" + messageID + "][" + messageIOSetBasedirectoryPathString + "] creation task end");
	}

	

	private void copyServerSampleFiles() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("server sample source files copy task start");

		
		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String commonResourcePathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		
		String sourceDirectoryPathString = new StringBuilder(commonResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("server_build").toString();

		String targetDirectoryPathString = new StringBuilder(serverBuildPathString).toString();

		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);

		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the main project[").append(mainProjectName)
					.append("]'s the build directory[").append(sourceDirectoryPathString)
					.append("]  having sample source files to the target directory[").append(targetDirectoryPathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("server sample source files copy task end");
	}

	private void createNewServerDosShellFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s server dos shell file creation task start");

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String serverDosShellFileContents = BuildSystemFileContents.getDosShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LogType.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		/** Server.bat */
		String serverDosShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.bat").toString();
		File serverDosShellFile = new File(serverDosShellFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(serverDosShellFile, serverDosShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[").append(serverDosShellFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s server dos shell file creation task end");
	}

	private void overwriteServerDosShellFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s server dos shell file overwrite task start");

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String serverDosShellFileContents = BuildSystemFileContents.getDosShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LogType.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		/** Server.bat */
		String serverDosShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.bat").toString();
		File serverDosShellFile = new File(serverDosShellFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.overwriteFile(serverDosShellFile, serverDosShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[").append(serverDosShellFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s server dos shell file overwrite task end");
	}

	private void createNewServerUnixShellFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s server unix shell file creation task start");

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String serverUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LogType.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		String serverUnixShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.sh").toString();

		File serverUnixShellFile = new File(serverUnixShellFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.createNewFile(serverUnixShellFile, serverUnixShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server unix shell file[").append(serverUnixShellFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s server unix shell file creation task end");
	}

	private void overwriteServerUnixShellFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s server unix shell file overwrite task start");

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String serverUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LogType.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		String serverUnixShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.sh").toString();

		File serverUnixShellFile = new File(serverUnixShellFilePathString);

		try {
			kr.pe.codda.common.util.CommonStaticUtil.overwriteFile(serverUnixShellFile, serverUnixShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server unix shell file[").append(serverUnixShellFilePathString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s server unix shell file overwrite task end");
	}

	private void createNewServerAntBuildXMLFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s server ant build.xml file creation task start");

		String serverAntBuildXMLFilePahtString = ServerBuildSytemPathSupporter.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);

		File serverAntBuildXMLFile = new File(serverAntBuildXMLFilePahtString);

		try {
			byte[] readBytes = kr.pe.codda.common.util.CommonStaticUtil.readFileToByteArray(serverAntBuildXMLFile, 1024L*1024L*10L);
			
			String sererAntBuildXMLFileContents = new String(readBytes, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			
			sererAntBuildXMLFileContents.replace("sample_test_server", mainProjectName);
			
			String PREFIX_OF_DYNAMIC_CLASS_RELATIVE_PATH = 
					new StringBuilder().append(CommonStaticFinalVars.BASE_DYNAMIC_CLASS_FULL_NAME.replaceAll("\\.", "/"))
					.append("/**").toString();
			
			sererAntBuildXMLFileContents.replace("kr/pe/codda/impl/**", PREFIX_OF_DYNAMIC_CLASS_RELATIVE_PATH);
			
			kr.pe.codda.common.util.CommonStaticUtil.overwriteFile(serverAntBuildXMLFile, sererAntBuildXMLFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server ant build.xml file[").append(serverAntBuildXMLFilePahtString).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s server ant build.xml file creation task end");
	}

	private void createNewConfigFile() throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s config file creation task start");
		
		CoddaConfiguration coddaConfiguration = null;
		
		try {
			coddaConfiguration = new CoddaConfiguration(installedPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknow error, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
		
		try {
			coddaConfiguration.saveConfigFile();
		} catch (IOException e) {
			String configFilePathString = ProjectBuildSytemPathSupporter
					.getProejctConfigFilePathString(installedPathString, mainProjectName);
			
			String errorMessage = new StringBuilder("fail to save the main project's configuration file[").append(configFilePathString)
					.append("]").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[" + mainProjectName + "]'s config file creation task end");
	}
	
	private void createNewWebClientAntPropertiesFile(String servletSystemLibraryPathString)
			throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web client ant properties file creation task start");
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
		if (!servletSystemLibraryPath.exists()) {
			String errorMessage = new StringBuilder("the web client's servlet system library path[")
					.append(servletSystemLibraryPathString)
					.append("] doesn't exist").toString();

			throw new BuildSystemException(errorMessage);
		}
		
		String webClientAntPropertiesFilePathString = WebClientBuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		SequencedProperties antBuiltInProperties = new SequencedProperties();

		// antBuiltInProperties.setProperty(CommonStaticFinalVars.IS_TOMCAT_KEY, isTomcat ? "true" : "false");

		antBuiltInProperties.setProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY,
				servletSystemLibraryPathString);

		
		try {
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(antBuiltInProperties, getWebClientAntPropertiesTitle(),
					webClientAntPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the main project[")
					.append(mainProjectName)
					.append("]'s web client ant properties file[")
					.append(webClientAntPropertiesFilePathString)
					.append("] doesn't exist").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to create the main project[").append(mainProjectName)
					.append("]'s web client ant properties file[").append(webClientAntPropertiesFilePathString).append("]")
					.toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s web client ant properties file creation task end");
	}
	
	private void modifyWebClientAntPropertiesFile(String servletSystemLibraryPathString) throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s web client ant properties file modification task start");
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
		if (!servletSystemLibraryPath.exists()) {
			String errorMessage = new StringBuilder("the web client's servlet system library path[")
					.append(servletSystemLibraryPathString)
					.append("] doesn't exist").toString();

			throw new BuildSystemException(errorMessage);
		}
		
		String webClientAntPropertiesFilePathString = WebClientBuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		SequencedProperties antBuiltInProperties = new SequencedProperties();

		antBuiltInProperties.setProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY,
				servletSystemLibraryPathString);
		
		try {
			SequencedPropertiesUtil.overwriteSequencedPropertiesFile(antBuiltInProperties, getWebClientAntPropertiesTitle(),
					webClientAntPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the main project[")
					.append(mainProjectName)
					.append("]'s web client ant properties file[")
					.append(webClientAntPropertiesFilePathString)
					.append("] doesn't exist").toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to modify the main project[").append(mainProjectName)
					.append("]'s web client ant properties file[").append(webClientAntPropertiesFilePathString).append("]")
					.toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[" + mainProjectName + "]'s web client ant properties file modification task end");
	}
	private String getWebClientAntPropertiesTitle() {
		return new StringBuilder("project[").append(mainProjectName).append("]'s web client ant properteis file").toString();
	}
		
	private Properties loadValidWebClientAntPropertiesFile() throws BuildSystemException {		
		String webClientAntPropertiesFilePathString = WebClientBuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		SequencedProperties webClientAntProperties = null;
		try {
			webClientAntProperties = SequencedPropertiesUtil.loadSequencedPropertiesFile(webClientAntPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the main project[")
					.append(mainProjectName)
					.append("]'s web client ant properties file[")
					.append(webClientAntPropertiesFilePathString)
					.append("] doesn't exist").toString();

			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to load the main project[").append(mainProjectName)
					.append("]'s web client ant properties file[").append(webClientAntPropertiesFilePathString).append("]")
					.toString();

			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		if (!webClientAntProperties.containsKey(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY)) {			
			String errorMessage = String.format(
					"the web client ant properties file[%s] is bad because the key[%s] that means servlet system library path is not found",
					webClientAntPropertiesFilePathString, CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY);
			throw new BuildSystemException(errorMessage);
		}		
		
		return webClientAntProperties;
	}
	

	private void applyInstalledPathToConfigFile() throws BuildSystemException {
		String mainProejctConfigFilePathString = ProjectBuildSytemPathSupporter.getProejctConfigFilePathString(installedPathString, mainProjectName);
		CoddaConfiguration coddaConfiguration = null;
		
		try {
			coddaConfiguration = new CoddaConfiguration(installedPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknow error, errmsg=")
					.append(e.getMessage()).toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
		
		try {
			RunningProjectConfiguration.applyIntalledPath(installedPathString, mainProjectName, coddaConfiguration.loadConfigFile());
			
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to apply installed path to the main project[")
					.append(mainProjectName).append("] config file").toString();

			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (Exception e) {
			String errorMessage = new StringBuilder(
					"fail to apply installed path to the main project config file[")
							.append(mainProejctConfigFilePathString).append("]").toString();

			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
	}
		

	public void applyInstalledPath() throws BuildSystemException {
		EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPathString);
		eclipseBuilder.saveCoddaCoreAllEclipeWorkbenchFiles();
		eclipseBuilder.saveCoddaCoreHelperEclipeWorkbenchFiles();
		eclipseBuilder.saveCoddaSampleBaseServerEclipeWorkbenchFiles();
		eclipseBuilder.saveCoddaSampleBaseAppClientEclipeWorkbenchFiles();
		eclipseBuilder.saveCoddaSampleBaseWebClientEclipeWorkbenchFiles();
		
		applyInstalledPathToConfigFile();
		
		if (File.separator.equals("/")) {				
			/** unix shell */
			if (isValidServerAntBuildXMLFile()) {
				overwriteServerUnixShellFile();
			} else {
				createNewServerAntBuildXMLFile();
			}
			if (isValidAppClientAntBuildXMLFile()) {
				overwriteAppClientUnixShellFile();
			} else {
				createNewAppClientUnixShellFile();
			}
		} else {
			/** dos shell */
			if (isValidServerAntBuildXMLFile()) {
				overwriteServerDosShellFile();
			} else {
				createNewServerDosShellFile();
			}
			if (isValidAppClientAntBuildXMLFile()) {
				overwriteAppClientDosShellFile();
			} else {
				createNewAppClientDosShellFile();
			}
		}
	}

	public void overwriteConfigFile(SequencedProperties modifiedConfigSequencedProperties) throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "]'s config file overwrite task start");
		
		String mainProjectConfigFilePathString = ProjectBuildSytemPathSupporter.getProejctConfigFilePathString(installedPathString, mainProjectName);
		CoddaConfiguration coddaConfiguration = null;
		
		try {
			coddaConfiguration = new CoddaConfiguration(installedPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknow error, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}		
		
		try {
			SequencedPropertiesUtil.overwriteSequencedPropertiesFile(modifiedConfigSequencedProperties,
					coddaConfiguration.getTitleOfConfigFile(), 
					mainProjectConfigFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s configuration file").toString();
	
			log.log(Level.WARNING, errorMessage, e);
	
			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {			
			String errorMessage = new StringBuilder("fail to overwrite the main project's configuration file").append(mainProjectConfigFilePathString)
					.append("]").toString();
	
			log.log(Level.WARNING, errorMessage, e);
	
			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[" + mainProjectName + "]'s config file overwrite task end");
	}

	public SequencedProperties loadConfigPropertiesFile() throws BuildSystemException {
		CoddaConfiguration mainProjectConfiguration = null;
		SequencedProperties configSequencedProperties = null;
		
		try {
			mainProjectConfiguration = new CoddaConfiguration(installedPathString, mainProjectName);
			
			configSequencedProperties = mainProjectConfiguration.loadConfigFile();
		} catch (IllegalArgumentException e) {
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
	
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknow error, errmsg=")
					.append(e.getMessage()).toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
		
		return configSequencedProperties;
		
	}

	public void changeProjectState(boolean isServer, boolean isAppClient, 
			boolean isWebClient, String servletSystemLibraryPathString, 
			SequencedProperties modifiedConfigSequencedProperties) throws BuildSystemException {
		Logger log = Logger.getGlobal();
		log.info("main project[" + mainProjectName + "] changeProjectState method start");
		
		if (!isServer && !isAppClient && !isWebClient) {
			throw new IllegalArgumentException("You must choose one more build system type but isSerer=false, isAppClient=false, isWebClient=false");
		}
		
		if (isServer) {
			String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);
			File serverBuildPath = new File(serverBuildPathString);
			
			if (!serverBuildPath.exists()) {
				createServerBuildChildDirectories();
				createServerBuildSystemFiles();				
			} else {
				log.warning("the server build path[" + serverBuildPathString + "] exist, so skip creation of server build system");
			}	
		} else {
			if (isValidServerAntBuildXMLFile()) {
				deleteServerBuildPath();				
			} else {
				String serverAntBuildFilePathString = ServerBuildSytemPathSupporter
						.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);
				
				log.warning("the server buid.xml file[" + serverAntBuildFilePathString + "] is bad beacse it doesn't exist or is not a file, so skip deletion of server build system");
			}
		}
		
		if (isAppClient) {
			String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);
			File appClientBuildPath = new File(appClientBuildPathString);
			
			if (!appClientBuildPath.exists()) {
				createAppBuildChildDirectories();
				createAppClientBuildSystemFiles();				
			} else {				
				log.warning("the app client build path[" + appClientBuildPathString + "] exist, so skip creation of app client build system");
			}			
		} else {			
			
			if (isValidAppClientAntBuildXMLFile()) {
				deleteAppClientBuildPath();				
			} else {
				String appClientAntBuildFilePathString = AppClientBuildSystemPathSupporter
						.getAppClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
				
				
				log.warning("the app client buid.xml file[" + appClientAntBuildFilePathString + "] is bad beacse it doesn't exist or is not a file, so skip deletion of app client build system");
			}
			
		}
		
		if (isWebClient) {
			{
				String webClientBuildPathString = WebClientBuildSystemPathSupporter.getWebClientBuildPathString(installedPathString, mainProjectName);
				File webClientBuildPath = new File(webClientBuildPathString);
				
				if (!webClientBuildPath.exists()) {
					createWebBuildChildDirectories();
					createWebClientBuildSystemFiles(servletSystemLibraryPathString);
				} else {
					modifyWebClientAntPropertiesFile(servletSystemLibraryPathString);
					log.warning("the web client build path[" + webClientBuildPathString + "] exists, so only the web client's ant properties including servlet system library path  was updated for new servletSystemLibraryPathString[" + servletSystemLibraryPathString + "]");
				}		
			}
			{
				String webRootPathString = WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPathString, mainProjectName);
				File webRootPath = new File(webRootPathString);
				if (!webRootPath.exists()) {
					createWebRootChildDirectories();
					createWebRootSampleFiles();
				} else {
					log.warning("the web root path[" + webRootPathString + "] exists, so skip creation of web root");
				}
			}
		} else {
			if (isValidWebClientAntBuildXMLFile()) {
				deleteWebCientBuildPath();
			} else {
				String webClientAntBuildFilePathString = WebClientBuildSystemPathSupporter
						.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
				log.warning("the web client buid.xml file["+ webClientAntBuildFilePathString + "] is bad beacse it doesn't exist or is not a file, so skip deletion of web client build system");
			}			

			if (isValidWebRootXMLFile()) {
				deleteWebRoot();
			} else {
				String webRootXMLFilePathString = WebRootBuildSystemPathSupporter.getUserWebRootXMLFilePathString(installedPathString, mainProjectName);
				log.warning("the web.xml file[" + webRootXMLFilePathString + "] located at web root direcotry is bad beacse it doesn't exist or is not a file, so skip deletion of web root system");
			}
		}
		
		overwriteConfigFile(modifiedConfigSequencedProperties);
		
		log.info("main project[" + mainProjectName + "] changeProjectState method end");
	}
	
	
	/**
	 * if the project path exists and is a directory, then force-delete it
	 * @throws BuildSystemException This exception is thrown if the project path doesn't exit or is not a directory.
	 */
	public void dropProject() throws BuildSystemException {
		File projectPath = new File(projectPathString);

		if (!projectPath.exists()) {
			String errorMessage = new StringBuilder("the main project path[")
					.append(projectPathString).append("] does not exist")
					.toString();
			throw new BuildSystemException(errorMessage);
		}

		if (!projectPath.isDirectory()) {
			String errorMessage = new StringBuilder("the main project path[")
					.append(projectPathString).append("] is not a directory")
					.toString();
			throw new BuildSystemException(errorMessage);
		}

		try {
			FileUtils.forceDelete(projectPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to delete the main project path[")
					.append(projectPathString).append("]").toString();
			/** 상세 에러 추적용 */
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
	}
}
