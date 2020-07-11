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

package kr.pe.codda.client;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.client.classloader.ClientClassLoaderFactory;
import kr.pe.codda.client.classloader.ClientTaskManger;
import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.classloader.ClientTaskMangerUsingSystemClassLoader;
import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporter;
import kr.pe.codda.client.connection.asyn.AsynShareSingleConnection;
import kr.pe.codda.client.connection.asyn.AyncConnectedShareSingleConnectionAdder;
import kr.pe.codda.client.connection.asyn.ClientIOEventController;
import kr.pe.codda.client.connection.asyn.noshare.AsynNoShareConnectionPool;
import kr.pe.codda.client.connection.sync.SyncNoShareConnectionPool;
import kr.pe.codda.client.connection.sync.SyncShareSingleConnection;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.part.AbstractProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.io.WrapBufferPool;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.codda.common.protocol.thb.THBMessageProtocol;
import kr.pe.codda.common.type.ClassloaderType;
import kr.pe.codda.common.type.ClientConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;

/**
 * 프로젝트 연결 폴
 * @author Won Jonghoon
 *
 */
public final class AnyProjectConnectionPool implements AnyProjectConnectionPoolIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	// private ProjectPartConfiguration projectPartConfiguration = null;
	
	private final String projectName;
	private final String serverHost;
	private final int serverPort;
	private final long connectionTimeout;
	private final StreamCharsetFamily streamCharsetFamily;
	private final int clientAsynInputMessageQueueCapacity;
	private final long clientAsynAliveTimePerWrapBuffer;
	private final long clientAsynRetryIntervaTimeToAddInputMessage;
	private final long clientRetryIntervaTimeToGetConnection;
	private final int clientMailboxCountPerAsynShareConnection;

	private boolean whetherClientWrapBufferIsDirect;
	private int clientWrapBufferMaxCntPerMessage;
	private int clientWrapBufferSize;
	private int clientWrapBufferPoolSize;
	private ClientConnectionType clientConnectionType = null;	
	private long clientConnectionPoolSupporterTimeInterval;
	
	private ConnectionPoolIF connectionPool = null;	
	private ConnectionPoolSupporter connectionPoolSupporter = null;
	private WrapBufferPoolIF wrapBufferPool = null;
	private MessageProtocolIF messageProtocol = null;
	private ClientTaskMangerIF clientTaskManger = null;	
	private ClientIOEventController asynClientIOEventController = null;
	

	public AnyProjectConnectionPool(String projectName, AbstractProjectPartConfiguration projectPartConfiguration) throws NoMoreWrapBufferException, IOException, ConnectionPoolException, InterruptedException {
		// this.projectPartConfiguration = projectPartConfiguration;
		
		this.projectName = projectName;
		serverHost = projectPartConfiguration.getServerHost();
		serverPort = projectPartConfiguration.getServerPort();
		final ByteOrder byteOrder = projectPartConfiguration.getByteOrder();
		connectionTimeout = projectPartConfiguration.getClientConnectionTimeout();
		streamCharsetFamily = new StreamCharsetFamily(projectPartConfiguration.getCharset());
		clientAsynInputMessageQueueCapacity = projectPartConfiguration.getClientAsynInputMessageQueueCapacity();
		clientRetryIntervaTimeToGetConnection = projectPartConfiguration.getClientRetryIntervaTimeToGetConnection();
		// projectPartConfiguration.getClientAsynInputMessageQueueCapacity();
		
		
		/**
		 * FIXME! 변수 aliveTimePerWrapBuffer(랩버퍼 1개당 생존 시간, 단위 : nanoseconds) 는 임시적으로 하드 코딩 하지만 환경설정 파일에서 값을 얻어오도록 수정해야함,
		 * 100 MBytes bps => 초당 10 * 1024 * 1024 * 1024 bytes / seconds => 10 bytes / nanoseconds
		 * WrapBufer 4KBytes = 4 * 1024
		 * 디폴트 값은 400 nanoseconds 
		 */
		clientAsynAliveTimePerWrapBuffer = projectPartConfiguration.getClientAsynAliveTimePerWrapBuffer();
		clientAsynRetryIntervaTimeToAddInputMessage = projectPartConfiguration.getClientAsynRetryIntervaTimeToAddInputMessage();
		
		final int clientConnectionCount = projectPartConfiguration.getClientConnectionCount();
		clientMailboxCountPerAsynShareConnection = projectPartConfiguration.getClientMailboxCountPerAsynShareConnection();		
		
		
		// projectPartConfiguration.getClientConnectionMaxCount();		
		
		final MessageProtocolType messageProtocolType = projectPartConfiguration.getMessageProtocolType();
		whetherClientWrapBufferIsDirect = projectPartConfiguration.getWhetherClientWrapBufferIsDirect();
		clientWrapBufferMaxCntPerMessage = projectPartConfiguration.getClientWrapBufferMaxCntPerMessage();
		clientWrapBufferSize = projectPartConfiguration.getClientWrapBufferSize();
		clientWrapBufferPoolSize = projectPartConfiguration.getClientWrapBufferPoolSize();		
		clientConnectionType = projectPartConfiguration.getClientConnectionType();
		clientConnectionPoolSupporterTimeInterval = projectPartConfiguration.getClientConnectionPoolSupporterTimeInterval();
		
		// private final int clientSyncMessageMailboxCountPerAsynShareConnection
		
		
		
		wrapBufferPool = new WrapBufferPool(whetherClientWrapBufferIsDirect, byteOrder,
				clientWrapBufferSize,
				clientWrapBufferPoolSize);
		

		switch (messageProtocolType) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol(clientWrapBufferMaxCntPerMessage,
						streamCharsetFamily, wrapBufferPool);
	
				break;
			}
			/*
			 * case DJSON: { messageProtocol = new
			 * DJSONMessageProtocol(projectPartConfiguration.
			 * getDataPacketBufferMaxCntPerMessage(), charsetEncoderOfProject,
			 * charsetDecoderOfProject, wrapBufferPool); break; }
			 */
			case THB: {
				messageProtocol = new THBMessageProtocol(clientWrapBufferMaxCntPerMessage,
						streamCharsetFamily, wrapBufferPool);
				break;
			}
			default: {
				String errorMessage = new StringBuilder().append("지원하지 않은 프로젝트[")
						.append(projectName)
						.append("]의 메시지 프로토콜[")
						.append(messageProtocolType.toString())
						.append("] 입니다").toString();
				log.log(Level.SEVERE, errorMessage);
				System.exit(1);
			}
		}	
		
		
		connectionPoolSupporter = new ConnectionPoolSupporter(clientConnectionPoolSupporterTimeInterval);
		
		if (clientConnectionType.equals(ClientConnectionType.SYNC)) {
			connectionPool = new SyncNoShareConnectionPool(serverHost, serverPort, connectionTimeout,
					streamCharsetFamily, 
					clientWrapBufferMaxCntPerMessage,
					clientRetryIntervaTimeToGetConnection,
					clientConnectionCount,
					messageProtocol, wrapBufferPool,
					connectionPoolSupporter);
		} else {
			
			// FIXME! 환경 변수에 클래스로더 종류에 따라 설정하는 로직 필요함
			ClassloaderType clientClassloaderType = ClassloaderType.SYSTEM;		
			if (ClassloaderType.SYSTEM.equals(clientClassloaderType)) {
				clientTaskManger = new ClientTaskMangerUsingSystemClassLoader();
			} else {
				CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
						.getCoddaConfiguration();
				String installedPathString = runningProjectConfiguration.getInstalledPathString();
				
				String clientClassloaderClassPathString = new StringBuilder()
						.append(WebRootBuildSystemPathSupporter.getUserWebINFPathString(installedPathString, projectName))
						.append(File.separator)
						.append("classes")
						.toString();
				String clientClassloaderReousrcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, projectName); 
				ClientClassLoaderFactory clientClassLoaderFactory = null;
				try {
					clientClassLoaderFactory = new ClientClassLoaderFactory(clientClassloaderClassPathString, clientClassloaderReousrcesPathString);
				} catch (Exception e) {
					log.severe("fail to create a instance of ClientClassLoaderFactory class, errmsg=" + e.getMessage());
					System.exit(1);
				}
				clientTaskManger = new ClientTaskManger(clientClassLoaderFactory);
			}
			
			asynClientIOEventController = new ClientIOEventController(
					projectPartConfiguration.getClientSelectorWakeupInterval());
			
			ConnectionPoolIF asynConnectionPool = 
					new AsynNoShareConnectionPool(projectName, serverHost, serverPort, connectionTimeout,
							streamCharsetFamily,
							clientWrapBufferMaxCntPerMessage,
							clientAsynInputMessageQueueCapacity,
							clientAsynAliveTimePerWrapBuffer,
							clientAsynRetryIntervaTimeToAddInputMessage,
							clientRetryIntervaTimeToGetConnection,
							clientConnectionCount,
							messageProtocol, clientTaskManger, wrapBufferPool,					
					connectionPoolSupporter, asynClientIOEventController);
			
			connectionPool = asynConnectionPool;
			
			connectionPool.fillAllConnection();
			/** WARNING! 클라이언트 셀렉터 구동전에 반듯이 먼저 연결 폴 구성을 위한 연결들을 셀렉터에 선 등록해야 한다 */
			asynClientIOEventController.start();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}			
		} 
		
		connectionPoolSupporter.start();
	}
	

	@Override
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreWrapBufferException, BodyFormatException,
			DynamicClassCallException, ServerTaskException, ConnectionPoolException {
		/*long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();*/

		AbstractMessage outObj = null;
		ConnectionIF conn = connectionPool.getConnection();
		try {
			outObj = conn.sendSyncInputMessage(messageCodecManger, inputMessage);
		/*} catch (BodyFormatException e) {
			throw e;
		} catch (SocketTimeoutException e) {
			*//** 연결 종류 마다  각자 처리하며 이곳에서는 아무 행동하지 않음 *//*
			throw e;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("this connection[")
					.append(conn.hashCode())
					.append("] was closed because of IOException").toString();
			log.warn(errorMessage, e);
			
			conn.close();
			

			throw e;*/
		} finally {
			connectionPool.release(conn);
		}

		/*endTime = System.nanoTime();
		log.debug("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
*/
		return outObj;
	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			ConnectionPoolException, IOException, NoMoreWrapBufferException, DynamicClassCallException, BodyFormatException {
		// long startTime = System.nanoTime();
		
		//log.info("sendAsynInputMessage::start");

		ConnectionIF conn = connectionPool.getConnection();
		try {
			conn.sendAsynInputMessage(messageCodecManger, inputMessage);
		} finally {
			connectionPool.release(conn);
		}

		// long endTime = System.nanoTime();
		// log.info("elapsed={} microseconds", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
		//log.info("sendAsynInputMessage::end");
	}

	@Override
	public ConnectionIF createAsynShareSingleConnection(String serverHost, int serverPort) throws InterruptedException, IOException, NoMoreWrapBufferException, NotSupportedException {
		if (clientConnectionType.equals(ClientConnectionType.SYNC)) {
			throw new NotSupportedException("the connection type is sync, it must be asyn, check the connection type in configuration");
		}
		
		ConnectionIF connectedConnection = null;
		
		AyncConnectedShareSingleConnectionAdder syncConnectedShareSingleConnectionAdder = new AyncConnectedShareSingleConnectionAdder();
		AsynShareSingleConnection unregisteredAsynShareSingleConnection = 
				new AsynShareSingleConnection(projectName, serverHost,
						serverPort,
						connectionTimeout,
						streamCharsetFamily,
						clientWrapBufferMaxCntPerMessage,
						clientAsynInputMessageQueueCapacity,
						clientMailboxCountPerAsynShareConnection,
						clientAsynAliveTimePerWrapBuffer,
						clientAsynRetryIntervaTimeToAddInputMessage,
				messageProtocol, wrapBufferPool, clientTaskManger, syncConnectedShareSingleConnectionAdder, 
				asynClientIOEventController);		
		
		asynClientIOEventController.addUnregisteredAsynConnection(unregisteredAsynShareSingleConnection);
		asynClientIOEventController.wakeup();
				
		try {
			connectedConnection = syncConnectedShareSingleConnectionAdder.poll(connectionTimeout);
		} catch(SocketTimeoutException e) {
			String warnMessage = new StringBuilder().append("this connection[")
					.append(unregisteredAsynShareSingleConnection.hashCode())
					.append("] timeout occurred").toString();
			
			log.warning(warnMessage);
			
			/** WARNING! don't delete this code, this code is the code that closes the socket to cancel the connection registered in the selector */
			unregisteredAsynShareSingleConnection.close();
				
			
			throw e;
		}
		
		return connectedConnection;
	}
	
	@Override
	public ConnectionIF createSyncShareSingleConnection(String serverHost, int serverPort) throws InterruptedException, IOException, NoMoreWrapBufferException {
		ConnectionIF connectedConnection = null;
		
		connectedConnection = new SyncShareSingleConnection(serverHost,
				serverPort,
				connectionTimeout,
				streamCharsetFamily,
				clientWrapBufferMaxCntPerMessage,
				clientWrapBufferSize,
				messageProtocol, wrapBufferPool);
		
		return connectedConnection;
	}

	@Override
	public String getPoolState() {
		return connectionPool.getPoolState();
	}

}
