/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.pe.codda.client.connection.sync;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.config.part.MainProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ClientConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.server.AnyProjectServer;

/**
 * @author Won Jonghoon
 *
 */
public class SimpleClinetSokcetTest {
private static Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private static final String CODDA_TEMP_ROOT_PATH_STRING = "simple_client_socket_test";
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
		if ( CODDA_TEMP_ROOT_PASTH.exists()) {
			
			String errorMessage = new StringBuilder()
						.append("가짜 루트 경로[")
						.append(CODDA_TEMP_ROOT_PASTH.getAbsolutePath())
					.append("]가 이미 존재합니다, 가짜 루트 경로는 단위 테스트 후 삭제 되기때문에 확인이 필요하여 단위 테스트를 중지합니다").toString();
			log.warning(errorMessage);

			isTempRootDirectoryExistError = true;
			
			fail(errorMessage);
		}
		
		File coddaAPPINFClassDirectory = new File(CommonStaticUtil.toOSPathString(CODDA_TEMP_ROOT_PATH_STRING + "/project/sample_base/server_build/APP-INF/classes"));
		
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
		
		File coddaResoruceDirectory = new File(CommonStaticUtil.toOSPathString(CODDA_TEMP_ROOT_PATH_STRING + "/project/sample_base/resources"));
		
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
	
	
	private MainProjectPartConfiguration buildMainProjectPartConfiguration(String projectName, String serverHost, int serverPort,
			int clientConnectionCount, int clientConnectionMaxCount, MessageProtocolType messageProtocolType,
			boolean whetherClientWrapBufferIsDirect, ClientConnectionType clientConnectionType, int serverMaxClients, int clientMailboxCountPerAsynShareConnection) throws PartConfigurationException {
		 
		
		MainProjectPartConfiguration projectPartConfigurationForTest = new MainProjectPartConfiguration();
		
		SequencedProperties sequencedPropertiesHavingDefault = new SequencedProperties();
		projectPartConfigurationForTest.toProperties(sequencedPropertiesHavingDefault);
		projectPartConfigurationForTest.fromProperties(sequencedPropertiesHavingDefault);
		
		projectPartConfigurationForTest.setServerHost(serverHost);
		projectPartConfigurationForTest.setServerPort(serverPort);
		projectPartConfigurationForTest.setClientConnectionCount(clientConnectionCount);
		projectPartConfigurationForTest.setClientConnectionMaxCount(clientConnectionMaxCount);
		projectPartConfigurationForTest.setMessageProtocolType(messageProtocolType);
		projectPartConfigurationForTest.setWhetherClientWrapBufferIsDirect(whetherClientWrapBufferIsDirect);
		projectPartConfigurationForTest.setClientConnectionType(clientConnectionType);		
		projectPartConfigurationForTest.setServerMaxClients(serverMaxClients);		
		
		
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SOURCE_FILE_CHARSET;
		long clientMonitorTimeInterval = 60 * 1000 * 5L;
		int clientWrapBufferMaxCntPerMessage = 50;
		int clientWrapBufferSize = 2048;
		int clientWrapBufferPoolSize = 10000;
		long clientConnectionTimeout = 5000L;
		
		// int clientMailboxCountPerAsynShareConnection = 2;
		int clientAsynInputMessageQueueCapacity = 5;
		int clientAsynOutputMessageQueueCapacity = 5;
		
		boolean whetherServerWrapBufferIsDirect = true;
		int serverWrapBufferMaxCntPerMessage = 80;
		int serverWrapBufferSize = clientWrapBufferSize;
		int serverWrapBufferPoolSize = 10000;
		int serverInputMessageQueueCapacity = 5;
		int serverOutputMessageQueueCapacity = 1000;
		
		
		projectPartConfigurationForTest.setByteOrder(byteOrder);
		projectPartConfigurationForTest.setCharset(charset);		
		projectPartConfigurationForTest.setClientMonitorTimeInterval(clientMonitorTimeInterval);
		projectPartConfigurationForTest.setClientWrapBufferMaxCntPerMessage(clientWrapBufferMaxCntPerMessage);
		projectPartConfigurationForTest.setClientWrapBufferSize(clientWrapBufferSize);
		projectPartConfigurationForTest.setClientWrapBufferPoolSize(clientWrapBufferPoolSize);
		projectPartConfigurationForTest.setClientConnectionTimeout(clientConnectionTimeout);		
		
		// projectPartConfigurationForTest.setClientConnectionPoolSupporterTimeInterval(clientConnectionPoolSupporterTimeInterval);
		projectPartConfigurationForTest.setClientMailboxCountPerAsynShareConnection(clientMailboxCountPerAsynShareConnection);
		projectPartConfigurationForTest.setClientAsynInputMessageQueueCapacity(clientAsynInputMessageQueueCapacity);
		projectPartConfigurationForTest.setClientAsynOutputMessageQueueCapacity(clientAsynOutputMessageQueueCapacity);
		projectPartConfigurationForTest.setWhetherServerWrapBufferIsDirect(whetherServerWrapBufferIsDirect);
		projectPartConfigurationForTest.setServerWrapBufferMaxCntPerMessage(serverWrapBufferMaxCntPerMessage);
		projectPartConfigurationForTest.setServerWrapBufferSize(serverWrapBufferSize);
		projectPartConfigurationForTest.setServerWrapBufferPoolSize(serverWrapBufferPoolSize);
		projectPartConfigurationForTest.setServerInputMessageQueueCapacity(serverInputMessageQueueCapacity);
		projectPartConfigurationForTest.setServerOutputMessageQueueCapacity(serverOutputMessageQueueCapacity);
		

		return projectPartConfigurationForTest;
	}
	
	@Test
	public void test단순클라이언트용소켓을이용하여서버와메시지교환테스트() {
		String testProjectName = "sample_test";
		MainProjectPartConfiguration projectPartConfigurationForTest = null;
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.THB;
		boolean clientDataPacketBufferIsDirect = false;
		String serverHost = null;
		int serverPort;
		int numberOfThread = 2;
		int clientConnectionCount = 0;
		int clientConnectionMaxCount = 5;
		int serverMaxClients = 1000;
				
		// host = "172.30.1.16";
		serverHost = "localhost";
		serverPort = 9293;
		
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					serverHost,  serverPort,
					clientConnectionCount,
					clientConnectionMaxCount,
					messageProtocolTypeForTest,
					clientDataPacketBufferIsDirect,
					ClientConnectionType.SYNC, serverMaxClients, numberOfThread);

		} catch (Exception e) {
			log.log(Level.WARNING, "error", e);

			String errorMessage = new StringBuilder()
					.append("fail to mapping configuration's item value to ProjectPartConfiguration's item value::errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
		
		// log.info("" + projectPartConfigurationForTest.getClientConnectionCount());
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPath.getAbsolutePath(), 
							mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPath.getAbsolutePath(), mainProjectName);
			
			anyProjectServerForTest = new AnyProjectServer(testProjectName, serverAPPINFClassPathString,
					projectResourcesPathString,
					projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to start a server", e);
			fail("fail to start a server");
		}
		
		
		byte[] emptyMesageStreamBytesForTHB = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0c, 0x05, 0x45, 0x6d, 0x70, 0x74, 0x79, 0x00, 0x01, (byte)0x80, 0x00, 0x00, 0x01};
		byte[] buffer = new byte[emptyMesageStreamBytesForTHB.length];
		
		
		Socket clientSocket = null;
		try {
			clientSocket =new Socket(serverHost, serverPort);
			
			clientSocket.setKeepAlive(true);
			clientSocket.setSoTimeout(5000);			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail ("UnknownHostException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail ("IOException");
		} catch (Exception e) {
			e.printStackTrace();
			fail ("unknown error");
		}  
		
		OutputStream clientOutputStream = null;
		InputStream clientInputStream = null;
		
		try {
			
			try {
				clientOutputStream = clientSocket.getOutputStream();
				
				clientOutputStream.write(emptyMesageStreamBytesForTHB);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail ("IOException");
			} catch (Exception e) {
				e.printStackTrace();
				fail ("unknown error");
			}
			
			try {
				clientInputStream = clientSocket.getInputStream();
				
				int sum = 0;
				int n;
				do {
					n = clientInputStream.read(buffer);
					
					sum += n;
					
					log.info("recevied buffer="+HexUtil.getHexStringFromByteArray(buffer, 0, n));
					
				} while (sum < emptyMesageStreamBytesForTHB.length);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail ("IOException");
			} catch (Exception e) {
				e.printStackTrace();
				fail ("unknown error");
			}			
			
		} finally {
			/*
			if (null != clientOutputStream) {
				try {
					clientOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			if (null != clientInputStream) {
				try {
					clientInputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			*/
			
			
			try {
				clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		anyProjectServerForTest.stopServer();
	}
}
