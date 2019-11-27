package kr.pe.codda.client.connection.asyn.noshare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.SocketTimeoutException;
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
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.common.util.CustomLogFormatter;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.Empty.Empty;
import kr.pe.codda.server.AnyProjectServer;

public class AsynNoShareConnectionTest {
	private static Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
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
		// File installedBasePath = new File("/home/madang01/gitmadang");
		File installedBasePath = new File("D:\\gitmadang");

		if (!installedBasePath.exists()) {
			fail("the installed path doesn't exist");
		}

		if (!installedBasePath.isDirectory()) {
			fail("the installed path isn't a directory");
		}

		String installedPathString = new StringBuilder(installedBasePath.getAbsolutePath()).append(File.separator)
				.append(CommonStaticFinalVars.ROOT_PROJECT_NAME).toString();

		installedPath = new File(installedPathString);

		if (!installedPath.exists()) {
			fail("the installed path doesn't exist");
		}

		if (!installedPath.isDirectory()) {
			fail("the installed path isn't a directory");
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

	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName, String host, int port,
			int clientConnectionCount, int clientConnectionMaxCount, MessageProtocolType messageProtocolType,
			boolean clientDataPacketBufferIsDirect, ConnectionType connectionType, int serverMaxClients)
			throws CoddaConfigurationException {

		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);

		// String host="localhost";
		// int port=9090;
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SOURCE_FILE_CHARSET;
		int messageIDFixedSize = 20;
		// MessageProtocolType messageProtocolType = MessageProtocolType.DHB;
		long clientMonitorTimeInterval = 60 * 1000 * 5L;
		// boolean clientDataPacketBufferIsDirect=true;
		int clientDataPacketBufferMaxCntPerMessage = 50;
		int clientDataPacketBufferSize = 2048;
		int clientDataPacketBufferPoolSize = 10000;
		// ConnectionType connectionType = ConnectionType.ASYN_PRIVATE;
		long clientSocketTimeout = 5000L;
		// int clientConnectionCount = 2;
		// int clientConnectionMaxCount = 4;
		long clientConnectionPoolSupporterTimeInterval = 600000L;
		int clientAsynPirvateMailboxCntPerPublicConnection = 2;
		int clientAsynInputMessageQueueSize = 5;
		int clientAsynOutputMessageQueueSize = 5;
		long clientAsynSelectorWakeupInterval = 1L;
		int clientAsynExecutorPoolSize = 2;
		long serverMonitorTimeInterval = 5000L;
		boolean serverDataPacketBufferIsDirect = true;
		int serverDataPacketBufferMaxCntPerMessage = 80;
		int serverDataPacketBufferSize = 2048;
		int serverDataPacketBufferPoolSize = 10000;
		// int serverMaxClients = 10;
		int serverInputMessageQueueSize = 5;
		int serverOutputMessageQueueSize = 5;

		projectPartConfigurationForTest.build(host, port, byteOrder, charset, messageIDFixedSize, messageProtocolType,
				clientMonitorTimeInterval, clientDataPacketBufferIsDirect, clientDataPacketBufferMaxCntPerMessage,
				clientDataPacketBufferSize, clientDataPacketBufferPoolSize, connectionType, clientSocketTimeout,
				clientConnectionCount, clientConnectionMaxCount, clientConnectionPoolSupporterTimeInterval,
				clientAsynPirvateMailboxCntPerPublicConnection, clientAsynInputMessageQueueSize,
				clientAsynOutputMessageQueueSize, clientAsynSelectorWakeupInterval, clientAsynExecutorPoolSize,
				serverMonitorTimeInterval, serverDataPacketBufferIsDirect, serverDataPacketBufferMaxCntPerMessage,
				serverDataPacketBufferSize, serverDataPacketBufferPoolSize, serverMaxClients,
				serverInputMessageQueueSize, serverOutputMessageQueueSize);

		return projectPartConfigurationForTest;
	}

	@Test
	public void testSendSyncInputMessage_singleThreadOk() {
		String host = "localhost";
		int port = 9092;
		boolean clientDataPacketBufferIsDirect = true;
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;
		int clientConnectionCount = 2;
		int clientConnectionMaxCount = clientConnectionCount;
		int serverMaxClients = clientConnectionCount;
		int retryCount = 10000;

		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName, host, port,
					clientConnectionCount, clientConnectionMaxCount, messageProtocolTypeForTest,
					clientDataPacketBufferIsDirect, ConnectionType.ASYN, serverMaxClients);

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}

		// log.info("{}", projectPartConfigurationForTest.getClientConnectionCount());

		AnyProjectServer anyProjectServerForTest = null;
		try {
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPath.getAbsolutePath(), mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter
					.getProjectResourcesDirectoryPathString(installedPath.getAbsolutePath(), mainProjectName);

			anyProjectServerForTest = new AnyProjectServer(serverAPPINFClassPathString, projectResourcesPathString,
					projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to start a server", e);
			fail("fail to start a server");
		}

		Empty emptyReq = new Empty();

		AnyProjectConnectionPoolIF anyProjectConnectionPool = null;

		try {
			anyProjectConnectionPool = new AnyProjectConnectionPool(projectPartConfigurationForTest);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to create a asyn no-share connection pool", e);
			fail("fail to create a asyn no-share connection pool");
		}

		try {

			long startTime = System.nanoTime();

			for (int i = 0; i < retryCount; i++) {
				// long localStartTime = System.nanoTime();
				
				AbstractMessage emptyRes = anyProjectConnectionPool
						.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), emptyReq);
				
				/*
				long localSEndTime = System.nanoTime();
				
				String infoMessage = new StringBuilder()						
						.append("걸린 시간[")
						.append(TimeUnit.MICROSECONDS.convert((localSEndTime - localStartTime), TimeUnit.NANOSECONDS))
						.append("] microseconds").toString();
				
				log.info(infoMessage);
				*/
				
				
				if (!(emptyRes instanceof Empty)) {
					fail("empty 메시지 수신 실패");
				}

				if (!emptyReq.messageHeaderInfo.equals(emptyRes.messageHeaderInfo)) {
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

			String errorMessage = String.format("fail to get a output message::%s", e.getMessage());

			fail(errorMessage);
		}
	}

	public static class SyncThreadSafeTester implements Runnable {
		private AnyProjectConnectionPoolIF anyProjectConnectionPool = null;
		private int retryCount;
		private ArrayBlockingQueue<String> noticeBlockingQueue = null;

		public SyncThreadSafeTester(AnyProjectConnectionPoolIF anyProjectConnectionPool, int retryCount,
				ArrayBlockingQueue<String> noticeBlockingQueue) {
			this.anyProjectConnectionPool = anyProjectConnectionPool;
			this.retryCount = retryCount;
			this.noticeBlockingQueue = noticeBlockingQueue;
		}

		@Override
		public void run() {
			log.info("start");
			try {
				long startTime = System.nanoTime();

				for (int i = 0; i < retryCount; i++) {
					// log.info("{}::{} 횟수", Thread.currentThread().getName(), i);
					Empty emptyReq = new Empty();
					try {
						AbstractMessage outputMessage = anyProjectConnectionPool
								.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), emptyReq);

						assertEquals(emptyReq.messageHeaderInfo.toString(), outputMessage.messageHeaderInfo.toString());
					} catch (ConnectionPoolTimeoutException e) {
						log.info("connection pool timeout");
						continue;

					} catch (Exception e) {
						log.log(Level.WARNING, "unknwon error", e);
						continue;
					}

					// Thread.sleep(0, 5);
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

				/*
				 * String errorMessage = String.format( "fail to get a output message::%s",
				 * e.getMessage());
				 */

				// fail(errorMessage);
			}

			noticeBlockingQueue.offer(Thread.currentThread().getName());
		}
	}

	@Test
	public void testSendSyncInputMessage_threadSafeOk() {
		String host = "localhost";
		int port = 9094;
		boolean clientDataPacketBufferIsDirect = true;
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;
		int clientConnectionCount = 3;
		int clientConnectionMaxCount = clientConnectionCount;
		int serverMaxClients = clientConnectionCount;

		int numberOfThread = 3;
		ArrayBlockingQueue<String> noticeBlockingQueue = new ArrayBlockingQueue<String>(numberOfThread);

		int retryCount = 10000;

		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName, host, port,
					clientConnectionCount, clientConnectionMaxCount, messageProtocolTypeForTest,
					clientDataPacketBufferIsDirect, ConnectionType.ASYN, serverMaxClients);

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}

		// log.info("{}", projectPartConfigurationForTest.getClientConnectionCount());

		AnyProjectServer anyProjectServerForTest = null;
		try {
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPath.getAbsolutePath(), mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter
					.getProjectResourcesDirectoryPathString(installedPath.getAbsolutePath(), mainProjectName);

			anyProjectServerForTest = new AnyProjectServer(serverAPPINFClassPathString, projectResourcesPathString,
					projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to start a server", e);
			fail("fail to start a server");
		}

		AnyProjectConnectionPoolIF anyProjectConnectionPool = null;

		try {
			anyProjectConnectionPool = new AnyProjectConnectionPool(projectPartConfigurationForTest);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to create a asyn no-share connection pool", e);
			fail("fail to create a asyn no-share connection pool");
		}

		/*
		 * class ThreadSafeTester implements Runnable { private
		 * AnyProjectConnectionPoolIF anyProjectConnectionPool = null; private int
		 * retryCount; private ArrayBlockingQueue<String> noticeBlockingQueue = null;
		 * 
		 * public ThreadSafeTester(AnyProjectConnectionPoolIF anyProjectConnectionPool,
		 * int retryCount, ArrayBlockingQueue<String> noticeBlockingQueue) {
		 * this.anyProjectConnectionPool = anyProjectConnectionPool; this.retryCount =
		 * retryCount; this.noticeBlockingQueue = noticeBlockingQueue; }
		 * 
		 * @Override public void run() { log.info("start {}",
		 * Thread.currentThread().getName()); try { long startTime = System.nanoTime();
		 * 
		 * for (int i=0; i < retryCount; i++) { try { Empty emptyReq = new Empty();
		 * AbstractMessage emptyRes =
		 * anyProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.
		 * getInstance(), emptyReq);
		 * 
		 * if (!(emptyRes instanceof Empty)) { fail("empty 메시지 수신 실패"); }
		 * 
		 * if (! emptyReq.messageHeaderInfo.equals(emptyRes.messageHeaderInfo)) {
		 * fail("수신한 empty 메시지의 메시지 헤더가 송신한 empty 메시지의 메시지 헤더와 다릅니다"); } }
		 * catch(SocketTimeoutException e) { continue; } }
		 * 
		 * long endTime = System.nanoTime(); log.info("{} {} 회 평균시간[{}] microseconds",
		 * Thread.currentThread().getName(), retryCount,
		 * TimeUnit.MICROSECONDS.convert((endTime - startTime),
		 * TimeUnit.NANOSECONDS)/retryCount);
		 * 
		 * 
		 * 
		 * } catch (Exception e) { log.warn("error", e);
		 * 
		 * String errorMessage = String.format( "fail to get a output message::%s",
		 * e.getMessage());
		 * 
		 * fail(errorMessage); }
		 * 
		 * noticeBlockingQueue.offer(Thread.currentThread().getName()); } }
		 */

		Thread[] threadSafeTester = new Thread[numberOfThread];

		for (int i = 0; i < numberOfThread; i++) {
			threadSafeTester[i] = new Thread(
					new SyncThreadSafeTester(anyProjectConnectionPool, retryCount, noticeBlockingQueue));
			threadSafeTester[i].start();
		}

		for (int i = 0; i < numberOfThread; i++) {
			String endThreadName = null;
			try {
				endThreadName = noticeBlockingQueue.take();
				
				System.out.printf("end thread[%s]", endThreadName);
				System.out.println();
			} catch (InterruptedException e) {
			}
		}
	}

	@Test
	public void testSendAsynInputMessage_singleThreadOk() {
		String host = "localhost";
		int port = 9091;
		boolean clientDataPacketBufferIsDirect = true;
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.THB;
		int clientConnectionCount = 2;
		int clientConnectionMaxCount = clientConnectionCount;
		int serverMaxClients = clientConnectionCount;

		int retryCount = 1000;

		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName, host, port,
					clientConnectionCount, clientConnectionMaxCount, messageProtocolTypeForTest,
					clientDataPacketBufferIsDirect, ConnectionType.ASYN, serverMaxClients);

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}

		// log.info("{}", projectPartConfigurationForTest.getClientConnectionCount());

		AnyProjectServer anyProjectServerForTest = null;
		try {
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPath.getAbsolutePath(), mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter
					.getProjectResourcesDirectoryPathString(installedPath.getAbsolutePath(), mainProjectName);

			anyProjectServerForTest = new AnyProjectServer(serverAPPINFClassPathString, projectResourcesPathString,
					projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to start a server", e);
			fail("fail to start a server");
		}

		Empty emptyReq = new Empty();

		AnyProjectConnectionPoolIF anyProjectConnectionPool = null;

		try {
			anyProjectConnectionPool = new AnyProjectConnectionPool(projectPartConfigurationForTest);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to create a asyn no-share connection pool", e);
			fail("fail to create a asyn no-share connection pool");
		}
		int i = 0;
		try {
			for (; i < retryCount; i++) {
				long startTime = System.nanoTime();

				try {
					anyProjectConnectionPool.sendAsynInputMessage(ClientMessageCodecManger.getInstance(), emptyReq);
				} catch (SocketTimeoutException e) {
					System.out.printf("socket timeout, emptyReq=%s", emptyReq.messageHeaderInfo.toString());
					System.out.println();
				}

				long endTime = System.nanoTime();

				String infoMessage = new StringBuilder()
						.append(retryCount)
						.append(" 회 평균시간[")
						.append(TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) / retryCount)
						.append("] microseconds").toString();
				log.info(infoMessage);
			}

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = String.format("fail to get a %d 번째 output message::%s", i, e.getMessage());

			fail(errorMessage);
		}

		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
		}
	}

	public static class AsynThreadSafeTester implements Runnable {
		private AnyProjectConnectionPoolIF anyProjectConnectionPool = null;
		private int retryCount;
		private ArrayBlockingQueue<String> noticeBlockingQueue = null;

		public AsynThreadSafeTester(AnyProjectConnectionPoolIF anyProjectConnectionPool, int retryCount,
				ArrayBlockingQueue<String> noticeBlockingQueue) {
			this.anyProjectConnectionPool = anyProjectConnectionPool;
			this.retryCount = retryCount;
			this.noticeBlockingQueue = noticeBlockingQueue;
		}

		@Override
		public void run() {
			log.info("start");
			try {
				long startTime = System.nanoTime();

				for (int i = 0; i < retryCount; i++) {
					// log.info("{}::{} 횟수", Thread.currentThread().getName(), i);
					Empty emptyReq = new Empty();
					try {
						anyProjectConnectionPool.sendAsynInputMessage(ClientMessageCodecManger.getInstance(), emptyReq);
					} catch (ConnectionPoolTimeoutException e) {
						log.warning("socket timeout, errmsg=" + e.getMessage());
						continue;

					} catch (Exception e) {
						log.log(Level.WARNING, "unknwon error", e);
						continue;
					}

					Thread.sleep(0, 5);
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

				/*
				 * String errorMessage = String.format( "fail to get a output message::%s",
				 * e.getMessage());
				 */

				// fail(errorMessage);
			}

			noticeBlockingQueue.offer(Thread.currentThread().getName());
		}
	}

	@Test
	public void testSendAsynInputMessage_threadSafeOk() {
		String host = "localhost";
		int port = 9093;
		boolean clientDataPacketBufferIsDirect = true;
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.THB;
		int clientConnectionCount = 3;
		int clientConnectionMaxCount = clientConnectionCount;
		int serverMaxClients = clientConnectionCount;

		int numberOfThread = 3;
		ArrayBlockingQueue<String> noticeBlockingQueue = new ArrayBlockingQueue<String>(numberOfThread);

		int retryCount = 10000;

		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName, host, port,
					clientConnectionCount, clientConnectionMaxCount, messageProtocolTypeForTest,
					clientDataPacketBufferIsDirect, ConnectionType.ASYN, serverMaxClients);

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}

		// log.info("{}", projectPartConfigurationForTest.getClientConnectionCount());

		AnyProjectServer anyProjectServerForTest = null;
		try {
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPath.getAbsolutePath(), mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter
					.getProjectResourcesDirectoryPathString(installedPath.getAbsolutePath(), mainProjectName);

			anyProjectServerForTest = new AnyProjectServer(serverAPPINFClassPathString, projectResourcesPathString,
					projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to start a server", e);
			fail("fail to start a server");
		}

		AnyProjectConnectionPoolIF anyProjectConnectionPool = null;

		try {
			anyProjectConnectionPool = new AnyProjectConnectionPool(projectPartConfigurationForTest);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to create a asyn no-share connection pool", e);
			fail("fail to create a asyn no-share connection pool");
		}

		Thread[] threadSafeTester = new Thread[numberOfThread];

		for (int i = 0; i < numberOfThread; i++) {
			threadSafeTester[i] = new Thread(
					new AsynThreadSafeTester(anyProjectConnectionPool, retryCount, noticeBlockingQueue));
			threadSafeTester[i].start();
		}

		for (int i = 0; i < numberOfThread; i++) {
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

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
	}

}
