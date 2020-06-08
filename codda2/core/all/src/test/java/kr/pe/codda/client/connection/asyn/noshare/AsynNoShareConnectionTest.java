/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.Empty.Empty;
import kr.pe.codda.impl.task.client.EmptyClientTask;
import kr.pe.codda.server.AnyProjectServer;

public class AsynNoShareConnectionTest {
	private static Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private static final String CODDA_TEMP_ROOT_PATH_STRING = "codda_asyn_threadsafe_conn_test";
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

		JDKLoggerCustomFormatter formatter = new JDKLoggerCustomFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);

		//////////////////////////////////////////
		if (CODDA_TEMP_ROOT_PASTH.exists()) {

			String errorMessage = new StringBuilder().append("가짜 루트 경로[")
					.append(CODDA_TEMP_ROOT_PASTH.getAbsolutePath())
					.append("]가 이미 존재합니다, 가짜 루트 경로는 단위 테스트 후 삭제 되기때문에 확인이 필요하여 단위 테스트를 중지합니다").toString();
			log.warning(errorMessage);

			isTempRootDirectoryExistError = true;

			fail(errorMessage);
		}

		File coddaAPPINFClassDirectory = new File(CommonStaticUtil.toOSPathString(
				CODDA_TEMP_ROOT_PATH_STRING + "/project/sample_base/server_build/APP-INF/classes"));

		log.info(coddaAPPINFClassDirectory.getAbsolutePath());

		try {
			boolean isSuccess = coddaAPPINFClassDirectory.mkdirs();
			if (!isSuccess) {
				fail("가짜 프로젝트 서버 동적 클래스 경로 만들기 실패");
			}

		} catch (Exception e) {
			log.log(Level.WARNING, "가짜 프로젝트 서버 동적 클래스 경로 만들기 실패", e);
			fail("가짜 프로젝트 서버 동적 클래스 경로 만들기 실패");
		}

		File coddaResoruceDirectory = new File(CommonStaticUtil.toOSPathString(
				CODDA_TEMP_ROOT_PATH_STRING + "/project/sample_base/resources"));

		log.info(coddaResoruceDirectory.getAbsolutePath());

		try {
			boolean isSuccess = coddaResoruceDirectory.mkdirs();
			if (!isSuccess) {
				fail("가짜 프로젝트 리소스 경로 만들기 실패");
			}

		} catch (Exception e) {
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
		 * if (!installedPath.exists()) { fail("the installed path doesn't exist"); }
		 * 
		 * if (!installedPath.isDirectory()) {
		 * fail("the installed path isn't a directory"); }
		 */
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (!isTempRootDirectoryExistError) {
			CommonStaticUtil.deleteDirectory(CODDA_TEMP_ROOT_PASTH);
		}
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
		int serverOutputMessageQueueSize = 1000;

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

			String errorMessage = new StringBuilder().append(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::errmsg=")
					.append(e.getMessage()).toString();

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
				 * long localSEndTime = System.nanoTime();
				 * 
				 * String infoMessage = new StringBuilder() .append("걸린 시간[")
				 * .append(TimeUnit.MICROSECONDS.convert((localSEndTime - localStartTime),
				 * TimeUnit.NANOSECONDS)) .append("] microseconds").toString();
				 * 
				 * log.info(infoMessage);
				 */

				if (!(emptyRes instanceof Empty)) {
					fail("empty 메시지 수신 실패");
				}

				assertEquals(emptyReq.getMailboxID(), emptyRes.getMailboxID());
				assertEquals(emptyReq.getMailID(), emptyRes.getMailID());
			}

			long endTime = System.nanoTime();

			String infoMessage = new StringBuilder().append(retryCount).append(" 회 평균시간[")
					.append(TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) / retryCount)
					.append("] microseconds").toString();

			log.info(infoMessage);

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = new StringBuilder().append("fail to get a output message::errmsg=")
					.append(e.getMessage()).toString();

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

						assertEquals(emptyReq.getMailboxID(), outputMessage.getMailboxID());
						assertEquals(emptyReq.getMailID(), outputMessage.getMailID());
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

				String infoMessage = new StringBuilder().append(retryCount).append(" 회 평균시간[")
						.append(TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) / retryCount)
						.append("] microseconds").toString();

				log.info(infoMessage);

			} catch (Exception e) {
				log.log(Level.WARNING, "error", e);

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

			String errorMessage = new StringBuilder().append(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::errmsg=")
					.append(e.getMessage()).toString();

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
					new SyncThreadSafeTester(anyProjectConnectionPool, retryCount, noticeBlockingQueue));
			threadSafeTester[i].start();
		}

		for (int i = 0; i < numberOfThread; i++) {
			String endThreadName = null;
			try {
				endThreadName = noticeBlockingQueue.take();

				log.info("end thread[" + endThreadName + "]");
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

			String errorMessage = new StringBuilder().append(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::errmsg=")
					.append(e.getMessage()).toString();

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
			long startTime = System.nanoTime();
			
			for (; i < retryCount; i++) {
				

				try {
					anyProjectConnectionPool.sendAsynInputMessage(ClientMessageCodecManger.getInstance(), emptyReq);
				} catch (SocketTimeoutException e) {
					String errorMessage = new StringBuilder().append("SocketTimeoutException, emptyReq[mailboxID=")
							.append(emptyReq.getMailboxID()).append(", mailID=").append(emptyReq.getMailID())
							.append("]").toString();

					log.info(errorMessage);
				}
			}
			
			long endTime = System.nanoTime();

			String infoMessage = new StringBuilder().append(retryCount).append(" 회 평균시간[")
					.append(TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) / retryCount)
					.append("] microseconds").toString();
			log.info(infoMessage);

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = new StringBuilder().append("fail to get a ").append(i)
					.append(" 번째 output message::errmsg=").append(e.getMessage()).toString();

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

					// Thread.sleep(0, 5);
				}

				long endTime = System.nanoTime();

				String infoMessage = new StringBuilder().append(retryCount).append(" 회 평균시간[")
						.append(TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) / retryCount)
						.append("] microseconds").toString();
				log.info(infoMessage);

			} catch (Exception e) {
				log.log(Level.WARNING, "error", e);

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
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;
		int clientConnectionCount = 5;
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

			String errorMessage = new StringBuilder().append(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::errmsg=")
					.append(e.getMessage()).toString();

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
		
		EmptyClientTask.count = 0;

		for (int i = 0; i < numberOfThread; i++) {
			threadSafeTester[i] = new Thread(
					new AsynThreadSafeTester(anyProjectConnectionPool, retryCount, noticeBlockingQueue));
			threadSafeTester[i].start();
		}

		for (int i = 0; i < numberOfThread; i++) {
			String endThreadName = null;
			try {
				endThreadName = noticeBlockingQueue.take();

				String infoMesage = new StringBuilder().append("end thread[").append(endThreadName).append("]")
						.toString();

				log.info(infoMesage);
			} catch (InterruptedException e) {
			}
		}

		try {
			log.info("1.EmptyClientTask.count=" + EmptyClientTask.count);
			
			while (retryCount * numberOfThread != EmptyClientTask.count) {
				Thread.sleep(1000);
				
				log.info("2.EmptyClientTask.count=" + EmptyClientTask.count);
			}
		} catch (InterruptedException e) {
		}
	}

}
