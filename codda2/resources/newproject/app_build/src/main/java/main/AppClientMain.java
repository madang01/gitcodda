package main;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import kr.pe.codda.applib.sessionkey.RSAPublickeyGetterBuilder;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.Echo.Echo;



public class AppClientMain {
	private void setupLogbackForJDKLogger() throws IllegalStateException {
		java.util.logging.LogManager.getLogManager().reset();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}
	
	private void setupLogbackEnvromenetVariable(String installedPathString, String mainProejct) throws IllegalStateException {
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
		
		
	}
	
	public void setup() {
		CoddaConfiguration runningCoddaConfiguration = CoddaConfigurationManager.getInstance().getRunningProjectConfiguration();

		String installedPathString = runningCoddaConfiguration.getInstalledPathString();
		String mainProejct = runningCoddaConfiguration.getMainProjectName();
		
		setupLogbackEnvromenetVariable(installedPathString, mainProejct);
		setupLogbackForJDKLogger();
	}
	
	public void doWork() {		
		Logger log = LoggerFactory.getLogger("kr.pe.codda");
		
		log.info("start");
		
		try {
			ClientSessionKeyManager.getInstance().createNewClientSessionKey(RSAPublickeyGetterBuilder.build().getMainProjectPublickeyBytes(), false);
		} catch (SymmetricException | InterruptedException e) {
			log.warn("fail to getting the main project's instance of the ClientSessionKey class", e);
			System.exit(1);
		}
		
		log.info("successfully getting the main project's instance of the ClientSessionKey class");
				
		
		java.util.Random random = new java.util.Random();
		
		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());
				
		AbstractMessage messageFromServer = null;
		try {
			messageFromServer = ConnectionPoolManager.getInstance().getMainProjectConnectionPool().sendSyncInputMessage(
					ClientMessageCodecManger.getInstance(), echoInObj);
			
			if (messageFromServer instanceof Echo) {
				Echo echoOutObj = (Echo)messageFromServer;
				if ((echoInObj.getRandomInt() == echoOutObj.getRandomInt()) && (echoInObj.getStartTime() == echoOutObj.getStartTime())) {
					log.info("성공::echo 메시지 입력/출력 동일함");
				} else {
					log.info("실패::echo 메시지 입력/출력 다름");
				}
			} else {
				log.warn("messageFromServer={}", messageFromServer.toString());
			}
			
			System.exit(0);
		} catch (Exception e) {
			log.warn("Exception", e);
			
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		AppClientMain appClientMain =  new AppClientMain();
		appClientMain.setup();
		appClientMain.doWork();
	}
}