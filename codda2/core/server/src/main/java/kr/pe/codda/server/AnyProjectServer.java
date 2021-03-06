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
package kr.pe.codda.server;

import java.io.File;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.part.MainProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.io.WrapBufferPool;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.codda.common.protocol.thb.THBMessageProtocol;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.server.classloader.ServerClassLoaderFactory;
import kr.pe.codda.server.task.DynamicClassWatcher;
import kr.pe.codda.server.task.ServerTaskManger;


/**
 * 프로젝트 서버
 * @author Won Jonghoon
 *
 */
public class AnyProjectServer {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	// private final String serverAPPINFClassPathString;
	// private final String projectResourcesPathString;
	// private final String projectName;
	// private final StreamCharsetFamily streamCharsetFamily;
	// private final int serverInputMessageQueueCapacity;
	// private final int serverOutputMessageQueueCapacity;
	// private final ByteOrder projectByteOrder;	
	//private final MessageProtocolType messageProtocolType;
	// private final boolean serverDataPacketBufferIsDirect;
	// private final int serverDataPacketBufferMaxCntPerMessage;
	// private final int serverDataPacketBufferSize;
	// private final int serverDataPacketBufferPoolSize;	
	
	
	private final WrapBufferPoolIF wrapBufferPool;	
	private final ServerTaskManger serverTaskManager;	
	private final ServerIOEventController serverIOEventController;
	private DynamicClassWatcher dynamicClassWatcher = null;
	
	/**
	 * 생성자
	 * @param projectName 프로젝트 이름
	 * @param serverAPPINFClassPathString 서버 클래스로더의 동적 클래스 경로 문자열
	 * @param projectResourcesPathString 서버 클래스로더의 리소스 경로 문자열
	 * @param mainProjectPartConfiguration 프로젝트 설정
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 경우 던지는 예외
	 * @throws PartConfigurationException 설정 관련 작업중 에러 발생시 던지는 예외
	 */
	public AnyProjectServer(String projectName, String serverAPPINFClassPathString,
			String projectResourcesPathString, MainProjectPartConfiguration mainProjectPartConfiguration)
			throws NoMoreWrapBufferException, PartConfigurationException {
		
		// this.projectName = projectName;
		
		
		final String serverHost = mainProjectPartConfiguration.getServerHost();
		final int serverPort = mainProjectPartConfiguration.getServerPort();
		final int serverMaxClient = mainProjectPartConfiguration.getServerMaxClients();
		final long connectionTimeout = mainProjectPartConfiguration.getClientConnectionTimeout();
		final StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(mainProjectPartConfiguration.getCharset());
		final ByteOrder projectByteOrder = mainProjectPartConfiguration.getByteOrder();
		// final int serverInputMessageQueueCapacity = projectPartConfiguration.getServerInputMessageQueueCapacity();
		final int serverOutputMessageQueueCapacity = mainProjectPartConfiguration.getServerOutputMessageQueueCapacity();
		final MessageProtocolType messageProtocolType = mainProjectPartConfiguration.getMessageProtocolType();
		final boolean serverDataPacketBufferIsDirect = mainProjectPartConfiguration.getWhetherServerWrapBufferIsDirect();		
		final int serverDataPacketBufferMaxCntPerMessage = mainProjectPartConfiguration.getServerWrapBufferMaxCntPerMessage();
		final int serverDataPacketBufferSize = mainProjectPartConfiguration.getServerWrapBufferSize();
		final int serverDataPacketBufferPoolSize = mainProjectPartConfiguration.getServerWrapBufferPoolSize();
		
		
		this.wrapBufferPool = new WrapBufferPool(serverDataPacketBufferIsDirect, 
				projectByteOrder, 
				serverDataPacketBufferSize, 
				serverDataPacketBufferPoolSize);
		

		MessageProtocolIF messageProtocol = null;
		switch (messageProtocolType) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol( 
						serverDataPacketBufferMaxCntPerMessage,
						streamCharsetFamily, 
						wrapBufferPool);
	
				break;
			}
			/*case DJSON: {
				messageProtocol = new DJSONMessageProtocol(
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPool);
				break;
			}*/
			case THB: {
				messageProtocol = new THBMessageProtocol( 
						serverDataPacketBufferMaxCntPerMessage, 
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
		
		
		
		ServerClassLoaderFactory serverClassLoaderFactory = 
				new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		
		serverTaskManager = new ServerTaskManger(serverClassLoaderFactory);
		
		try {
			File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
			dynamicClassWatcher = new DynamicClassWatcher(serverAPPINFClassPath, true, serverTaskManager);
			
			dynamicClassWatcher.start();
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("서버 동적 클래스 경로  변경 감시자 생성및 실행이 실패하였습니다").toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}
		
		serverIOEventController = new ServerIOEventController(projectName,
				serverHost, serverPort,
				serverMaxClient,
				connectionTimeout,
				streamCharsetFamily,
				serverDataPacketBufferMaxCntPerMessage,
				serverOutputMessageQueueCapacity,
				messageProtocol,
				wrapBufferPool, serverTaskManager);
	}

	
	/**
	 * 서버 시작
	 */	
	synchronized public void startServer() {
		serverIOEventController.start();
	}

	/**
	 * 서버 종료
	 */
	synchronized public void stopServer() {
		// serverProjectMonitor.interrupt();
		dynamicClassWatcher.close();

		if (! serverIOEventController.isInterrupted()) {
			serverIOEventController.interrupt();
		}
	}	

	
	/**
	 * @return 서버 프로젝트 상태 문자열
	 */
	public String getProjectServerState() {
		StringBuilder pollStateStringBuilder = new StringBuilder();		
		pollStateStringBuilder.append("dataPacketBufferPool.activeSize=");
		pollStateStringBuilder.append(wrapBufferPool.size());
		pollStateStringBuilder.append(", ");
		pollStateStringBuilder.append("dataPacketBufferPool.size=");
		pollStateStringBuilder.append(wrapBufferPool.capacity());
		pollStateStringBuilder.append(", ");
		pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		pollStateStringBuilder.append("numberOfAcceptedConnection=");
		pollStateStringBuilder.append(serverIOEventController.getNumberOfAcceptedConnection());
		
		// pollStateStringBuilder.append(inputMessageReaderPool.getPoolState());
		return pollStateStringBuilder.toString();
	}

}
