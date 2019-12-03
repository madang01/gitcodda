package kr.pe.codda.client.connection.sync;

import static org.junit.Assert.fail;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.client.AnyProjectConnectionPool;
import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.CustomLogFormatter;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.Empty.Empty;
import kr.pe.codda.server.AnyProjectServer;

public class SyncThreadSafeSingleConnectionTest {
	private static Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private static final String CODDA_TEMP_ROOT_PATH_STRING = "codda_sync_threadsafe_conn_test";
	private static final File CODDA_TEMP_ROOT_PASTH = new File(CODDA_TEMP_ROOT_PATH_STRING);
	
	private static boolean isTempRootDirectoryExistError = false;
	
	private static File installedPath;
	private final String mainProjectName = "sample_base";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		CustomLogFormatter formatter = new CustomLogFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);

		//////////////////////////////////////////		
		if ( CODDA_TEMP_ROOT_PASTH.exists()) {
			
			String errorMessage = new StringBuilder()
						.append("가짜 루트 경로[")
						.append(CODDA_TEMP_ROOT_PASTH.getAbsolutePath())
					.append("]가 이미 존재합니다, 가짜 루트 경로는 단위 테스트 후 삭제 되기때문에 확인이 필요하여 단위 테스트를 중지합니다").toString();
			log.warning(errorMessage);

			isTempRootDirectoryExistError = true;
			
			fail(errorMessage);
		}
		
		File coddaAPPINFClassDirectory = new File(CommonStaticUtil.changeRelativePathStringToOSPathString(CODDA_TEMP_ROOT_PATH_STRING + "/project/sample_base/server_build/APP-INF/classes"));
		
		log.info(coddaAPPINFClassDirectory.getAbsolutePath());		
		
		try {
			boolean isSuccess = coddaAPPINFClassDirectory.mkdirs();
			if (! isSuccess) {
				fail("가짜 프로젝트 서버 동적 클래스 경로 만들기 실패");
			}
			
			
		} catch(Exception e) {
			log.log(Level.WARNING, "가짜 프로젝트 서버 동적 클래스 경로 만들기 실패", e);
			fail("가짜 프로젝트 서버 동적 클래스 경로 만들기 실패");
		}
		
		File coddaResoruceDirectory = new File(CommonStaticUtil.changeRelativePathStringToOSPathString(CODDA_TEMP_ROOT_PATH_STRING + "/project/sample_base/resources"));
		
		log.info(coddaResoruceDirectory.getAbsolutePath());		
		
		try {
			boolean isSuccess = coddaResoruceDirectory.mkdirs();
			if (! isSuccess) {
				fail("가짜 프로젝트 리소스 경로 만들기 실패");
			}
			
		} catch(Exception e) {
			log.log(Level.WARNING, "가짜 프로젝트 리소스 경로 만들기 실패", e);
			fail("가짜 프로젝트 리소스 경로 만들기 실패");
		}
		
		
		File installedBasePath = new File(".");

		if (!installedBasePath.exists()) {
			fail("the installed path doesn't exist");
		}

		if (!installedBasePath.isDirectory()) {
			fail("the installed path isn't a directory");
		}

		String installedPathString = new StringBuilder(installedBasePath.getAbsolutePath()).append(File.separator)
				.append(CODDA_TEMP_ROOT_PATH_STRING).toString();

		installedPath = new File(installedPathString);

		/*
		if (!installedPath.exists()) {
			fail("the installed path doesn't exist");
		}

		if (!installedPath.isDirectory()) {
			fail("the installed path isn't a directory");
		}
		*/
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {		
		if (! isTempRootDirectoryExistError) {			
			CommonStaticUtil.deleteDirectory(CODDA_TEMP_ROOT_PASTH);
		}				
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName,
			String host, int port,
			int clientConnectionCount,
			MessageProtocolType messageProtocolType,
			boolean clientDataPacketBufferIsDirect,
			ConnectionType connectionType)
			throws CoddaConfigurationException {		
		 
		
		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);
		
		//String host="localhost";
		//int port=9090;
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SOURCE_FILE_CHARSET;		
		int messageIDFixedSize=20;
		//MessageProtocolType messageProtocolType = MessageProtocolType.DHB;					
		long clientMonitorTimeInterval = 60*1000*5L;
		// boolean clientDataPacketBufferIsDirect=true;
		int clientDataPacketBufferMaxCntPerMessage=50;
		int clientDataPacketBufferSize=2048;
		int clientDataPacketBufferPoolSize=1000;
		//ConnectionType connectionType = ConnectionType.ASYN_PRIVATE;
		long clientSocketTimeout = 5000L;			
		// int clientConnectionCount = 2;
		int clientConnectionMaxCount = 4;
		long clientConnectionPoolSupporterTimeInterval = 600000L;
		int clientAsynPirvateMailboxCntPerPublicConnection = 2;
		int clientAsynInputMessageQueueSize = 5;
		int clientAsynOutputMessageQueueSize = 5;
		long clientAsynSelectorWakeupInterval = 1L;			
		int clientAsynExecutorPoolSize =2;
		long serverMonitorTimeInterval = 5000L;
		boolean serverDataPacketBufferIsDirect=true;
		int serverDataPacketBufferMaxCntPerMessage=50;
		int serverDataPacketBufferSize=2048;
		int serverDataPacketBufferPoolSize=1000;
		int serverMaxClients = 10;		
		int serverInputMessageQueueSize = 5;
		int serverOutputMessageQueueSize = 5;
		
		projectPartConfigurationForTest.build(host, 
				port,
				byteOrder,
				charset,				
				messageIDFixedSize,
				messageProtocolType,		
				clientMonitorTimeInterval,
				clientDataPacketBufferIsDirect,
				clientDataPacketBufferMaxCntPerMessage,
				clientDataPacketBufferSize,
				clientDataPacketBufferPoolSize,
				connectionType,
				clientSocketTimeout,			
				clientConnectionCount,
				clientConnectionMaxCount,
				clientConnectionPoolSupporterTimeInterval,
				clientAsynPirvateMailboxCntPerPublicConnection,
				clientAsynInputMessageQueueSize,
				clientAsynOutputMessageQueueSize,
				clientAsynSelectorWakeupInterval,
				clientAsynExecutorPoolSize,
				serverMonitorTimeInterval,
				serverDataPacketBufferIsDirect,
				serverDataPacketBufferMaxCntPerMessage,
				serverDataPacketBufferSize,
				serverDataPacketBufferPoolSize,
				serverMaxClients,
				serverInputMessageQueueSize,
				serverOutputMessageQueueSize);

		return projectPartConfigurationForTest;
	}

	@Test
	public void testSendSyncInputMessage_threadSafeOK() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;
		boolean clientDataPacketBufferIsDirect = false;
		String serverHost = null;
		int serverPort;
		int numberOfThread = 3;
		int clientConnectionCount = 0;
		int retryCount = 10000;
		ArrayBlockingQueue<String> noticeBlockingQueue = new ArrayBlockingQueue<String>(numberOfThread); 
		
		// host = "172.30.1.16";
		serverHost = "localhost";
		serverPort = 9293;
		
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					serverHost,  serverPort,
					clientConnectionCount,
					messageProtocolTypeForTest,
					clientDataPacketBufferIsDirect,
					ConnectionType.SYNC);

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		log.info("" + projectPartConfigurationForTest.getClientConnectionCount());
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPath.getAbsolutePath(), 
							mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPath.getAbsolutePath(), mainProjectName);
			
			anyProjectServerForTest = new AnyProjectServer(serverAPPINFClassPathString,
					projectResourcesPathString,
					projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to start a server", e);
			fail("fail to start a server");
		}		
		
		AnyProjectConnectionPoolIF  anyProjectConnectionPool  = null;
		
		try {
			anyProjectConnectionPool = new AnyProjectConnectionPool(projectPartConfigurationForTest);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to create a asyn no-share connection pool", e);
			fail("fail to create a asyn no-share connection pool");
		}		
		
		ConnectionIF connection = null;
		try {
			connection = anyProjectConnectionPool.createSyncThreadSafeConnection(serverHost, serverPort);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to create a sync thread safe connection", e);
			fail("fail to create a sync thread safe connection");
		}		
		
		class ThreadSafeTester implements Runnable {
			private ConnectionIF connection = null;
			private int retryCount;
			private ArrayBlockingQueue<String> noticeBlockingQueue = null;
			
			public ThreadSafeTester(ConnectionIF connection, int retryCount, ArrayBlockingQueue<String> noticeBlockingQueue) {
				this.connection = connection;
				this.retryCount = retryCount;
				this.noticeBlockingQueue = noticeBlockingQueue;
			}			

			@Override
			public void run() {
				log.info("start " + Thread.currentThread().getName());				
				
				Empty emptyReq = new Empty();
				
				try {
					long startTime = System.nanoTime();
					
					for (int i=0; i < retryCount; i++) {
						AbstractMessage emptyRes =  connection.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), emptyReq);				
						if (!(emptyRes instanceof Empty)) {
							fail("empty 메시지 수신 실패");
						}

						if (! emptyReq.messageHeaderInfo.equals(emptyRes.messageHeaderInfo)) {
							fail("수신한 empty 메시지의 메시지 헤더가 송신한 empty 메시지의 메시지 헤더와 다릅니다");
						}
					}
					
					long endTime = System.nanoTime();
					
					String infoMessage = new StringBuilder()
							.append(retryCount)
							.append(" 회 평균시간[")
							.append(TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) / retryCount)
							.append("] microseconds").toString();
					log.info(infoMessage);		
					
					
					
				} catch (Exception e) {
					log.log(Level.WARNING, "error", e);

					String errorMessage = String.format(
							"fail to get a output message::%s",
							e.getMessage());

					fail(errorMessage);
				}
				
				noticeBlockingQueue.offer(Thread.currentThread().getName());
			}
		}		
		
		Thread[] threadSafeTester = new Thread[numberOfThread];
		
		for (int i=0; i < numberOfThread; i++) {
			threadSafeTester[i] = new Thread(new ThreadSafeTester(connection, retryCount, noticeBlockingQueue));
			threadSafeTester[i].start();
		}		
		
		for (int i=0; i < numberOfThread; i++) {
			String endThreadName = null;
			try {
				endThreadName = noticeBlockingQueue.take();
				
				String infoMesage = new StringBuilder()
						.append("end thread[")
						.append(endThreadName)
						.append("]").toString();
				
				log.info(infoMesage);
			} catch (InterruptedException e) {
			}
		}
		
		connection.close();
	}

}
