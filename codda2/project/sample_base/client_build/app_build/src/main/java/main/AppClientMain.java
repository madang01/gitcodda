package main;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

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
	
	public void start(int numberOfThread) {		
		ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();		
		Thread[] threadSafeTester = new Thread[numberOfThread];

		for (int i=0; i < numberOfThread; i++) {
			threadSafeTester[i] = new Thread(new ConnectionPoolThreadSafeTester(mainProjectConnectionPool));
			// threadSafeTester[i] = new Thread(new SigleConnectionThreadSafeTester(mainProjectConnectionPool));
			threadSafeTester[i].start();
		}	
	}

	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger("kr.pe.codda");
		
		int numberOfThread = 3;
		if (args.length > 0) {
			String firstArgument = args[0];
			log.info("the first argument(=numberOfThread)=[{}]", firstArgument);
		
			try {
				numberOfThread = Integer.parseInt(firstArgument);
			} catch(NumberFormatException e) {
				log.info("args.length={}, the first argument(=numberOfThread)[{}] is not a integer", 
						args.length, firstArgument);
			}
		}
		
		log.info("numberOfThread={}", numberOfThread);
		
		if (args.length == 0) {
			log.warn("파라미터를 입력해 주세요");
			System.exit(1);
		}
		
		
		
		AppClientMain appClientMain =  new AppClientMain();
		appClientMain.setup();
		appClientMain.start(numberOfThread);
		
		/*class SigleConnectionThreadSafeTester implements Runnable {
			private InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
			
			private AnyProjectConnectionPoolIF mainProjectConnectionPool = null;
			
			
			public SigleConnectionThreadSafeTester(AnyProjectConnectionPoolIF mainProjectConnectionPool) {
				this.mainProjectConnectionPool = mainProjectConnectionPool;
			}			

			@Override
			public void run() {
				log.info("start {}", Thread.currentThread().getName());
				
				String serverHost = null;
				int serverPort;
				
				CoddaConfigurationManager coddaConfigurationManager = CoddaConfigurationManager.getInstance();
				CoddaConfiguration coddaConfiguration = coddaConfigurationManager.getRunningProjectConfiguration();
				ProjectPartConfiguration mainProjectPartConfiguration = coddaConfiguration.getMainProjectPartConfiguration();
				serverHost = mainProjectPartConfiguration.getServerHost();
				serverPort = mainProjectPartConfiguration.getServerPort();
				
				ConnectionIF connection = null;				
				
				java.util.Random random = new java.util.Random();
				
				long startTime = 0;
				long endTime = 0;
				
				
				
				try {
					while (! Thread.currentThread().isInterrupted()) {
						
						if (null == connection) {
							try {
								startTime = System.nanoTime();	
								
								if (ConnectionType.ASYN.equals(mainProjectPartConfiguration.getConnectionType())) {
									connection = mainProjectConnectionPool.createAsynThreadSafeConnection(serverHost, serverPort);
								} else {
									connection = mainProjectConnectionPool.createSyncThreadSafeConnection(serverHost, serverPort);
								}
																
								endTime = System.nanoTime();
								log.info("{} 연결 경과 시간[{}] microseconds",
										mainProjectPartConfiguration.getConnectionType().name(),
										TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
								
								
							} catch (Exception e) {
								log.warn("fail to create a intance of ConnectionIF class", e);
								
								try {
									Thread.sleep(5000L);
								} catch (InterruptedException e1) {
									log.error("this thread[{}] InterruptedException", Thread.currentThread().getName());
									return;
								}
								
								continue;
							}
						}				
						
						
						Echo echoReq = new Echo();
						echoReq.setRandomInt(random.nextInt());
						echoReq.setStartTime(System.nanoTime());
						
						AbstractMessage outputMessage = null;
						try {	
							outputMessage = connection.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), echoReq);
							log.info("메시지 송수신 경과 시간[{}] microseconds",
									TimeUnit.MICROSECONDS.convert((System.nanoTime() - echoReq.getStartTime()), TimeUnit.NANOSECONDS));
							
						} catch (Exception e) {
							log.warn("error", e);
							connection.close();
							connection = null;
							continue;
						}				
						if (outputMessage instanceof Echo) {
							Echo echoRes = (Echo) outputMessage;
							if ((echoReq.getRandomInt() != echoRes.getRandomInt()) 
									|| (echoReq.getStartTime() != echoRes.getStartTime())) {
								log.error("실패::echo 메시지 입력/출력 다름");
								System.exit(1);
							}
						} else {
							log.error("실패::출력 메시지[{}]가 echo 가 아님", outputMessage.toString());
							System.exit(1);
						}
						
						// Thread.sleep(1000L);
					}			
					
				} catch (Exception e) {
					log.warn("unknow error", e);
				}
			}
		}*/
		
		
				
		/*
		java.util.Random random = new java.util.Random();

		long startTime = 0;
		long endTime = 0;

		int retryCount = 1000000;

		startTime = System.nanoTime();

		for (int i = 0; i < retryCount; i++) {
			Echo echoInObj = new Echo();
			echoInObj.setRandomInt(random.nextInt());
			echoInObj.setStartTime(new java.util.Date().getTime());

			AbstractMessage messageFromServer = null;
			try {
				messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(echoInObj);

				if (messageFromServer instanceof Echo) {
					Echo echoOutObj = (Echo) messageFromServer;
					if ((echoInObj.getRandomInt() == echoOutObj.getRandomInt())
							&& (echoInObj.getStartTime() == echoOutObj.getStartTime())) {
						// log.info("성공::echo 메시지 입력/출력 동일함");
					} else {
						log.info("실패::echo 메시지 입력/출력 다름");
					}
				} else {
					log.warn("messageFromServer={}", messageFromServer.toString());
				}
			} catch (Exception e) {
				log.warn("SocketTimeoutException", e);
			}
		}

		endTime = System.nanoTime();

		log.info("loop count[{}], average time[{} microseconds]", retryCount,
				TimeUnit.MICROSECONDS.convert((endTime - startTime)/retryCount, TimeUnit.NANOSECONDS));
		
		System.exit(0);*/

	}
}
