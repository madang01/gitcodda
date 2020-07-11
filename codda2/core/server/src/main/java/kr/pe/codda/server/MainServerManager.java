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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.part.RunningProjectConfiguration;
import kr.pe.codda.common.config.part.MainProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.PartConfigurationException;



/**
 * 서버 프로젝트 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class MainServerManager {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	/** 모니터 객체 */
	// private final Object monitor = new Object();

	
	private HashMap<String, AnyProjectServer> subProjectServerHash = new HashMap<String, AnyProjectServer>(); 
	private AnyProjectServer mainProjectServer = null;
	private ServerProjectMonitor serverProjectMonitor = null;
	private String installedPathString = null;
	private String mainProjectName = null;

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class MainProjectServerManagerHolder {
		static final MainServerManager singleton = new MainServerManager();
	}
	
	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * @return MainServerManager 클래스 객체
	 */
	public static MainServerManager getInstance() {
		return MainProjectServerManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreWrapBufferException 
	 */
	private MainServerManager() {
		// try {
			CoddaConfiguration coddaConfiguration = 
					CoddaConfigurationManager.getInstance()
					.getCoddaConfiguration();			
			
			installedPathString = coddaConfiguration.getInstalledPathString();
			mainProjectName = coddaConfiguration.getMainProjectName();			
			
			RunningProjectConfiguration runningProjectConfiguration = coddaConfiguration.getRunningProjectConfiguration();
			MainProjectPartConfiguration mainProjectPartConfiguration = runningProjectConfiguration.getMainProjectPartConfiguration();
			
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPathString, 
							mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
			
			try {
				mainProjectServer = new AnyProjectServer(mainProjectName, serverAPPINFClassPathString, projectResourcesPathString, mainProjectPartConfiguration);
			} catch (NoMoreWrapBufferException e) {
				log.log(Level.WARNING, "NoMoreDataPacketBufferException", e);
			} catch (PartConfigurationException e) {
				log.log(Level.WARNING, "CoddaConfigurationException", e);
			}
						
			serverProjectMonitor = new ServerProjectMonitor(
					mainProjectPartConfiguration.getServerMonitorTimeInterval());
			serverProjectMonitor.start();
	}
	
	/**
	 * @param subProjectName 서브 프로젝트 이름
	 * @return 서버 프로젝트 이름에 1:1 대응하는 프로젝트 서버
	 * @throws IllegalStateException 서버 프로젝트 이름에 1:1 대응하는 프로젝트 서버가 없는 경우 던지는 예외
	 */
	public AnyProjectServer getSubProjectServer(String subProjectName) throws IllegalStateException {
		AnyProjectServer subProjectServer =  subProjectServerHash.get(subProjectName);
		if (null == subProjectServer) {
			String errorMessage = new StringBuilder("환경설정 파일에서 찾고자 하는 서버 프로젝트[")
					.append(subProjectName).append("] 가 존재하지 않습니다.").toString();
			log.severe(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		
		return subProjectServer;
	}
	
	/**
	 * @return 메인 프로젝트 서버
	 * @throws IllegalStateException 환경 설정 파일을 읽어와서 '메인 프로젝트 서버' 생성 실패했을 경우 던지는 예외
	 */
	public AnyProjectServer getMainProjectServer() throws IllegalStateException {
		if (null == mainProjectServer) {
			String errorMessage = "환경설정 파일에서 찾고자 하는 메인 프로젝트가 존재하지 않습니다";
			log.severe(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		
		return mainProjectServer;
	}
	
	/**
	 * 서버 상태 모니터링 쓰레드
	 * @author Won Jonghoon
	 *
	 */
	private class ServerProjectMonitor extends Thread {		
		private long serverMonitorTimeInterval;
		/**
		 * 생성자
		 * @param serverMonitorTimeInterval 서버 모니터링 간격
		 */
		public ServerProjectMonitor(long serverMonitorTimeInterval) {
			this.serverMonitorTimeInterval = serverMonitorTimeInterval;
		}
		
		@Override
		public void run() {
			log.info("ServerProjectMonitor start");
			try {
				while (!Thread.currentThread().isInterrupted()) {
					log.info(getServerState());
					
					Thread.sleep(serverMonitorTimeInterval);
				}
			} catch(InterruptedException e) {
				log.info("ServerProjectMonitor::interrupr");
			} catch(Exception e) {
				log.log(Level.WARNING, "ServerProjectMonitor::unknow error", e);
			}

			log.info("ServerProjectMonitor end");
		}
	}
	
	/**
	 * @return 서버 모니터링 정보
	 */
	public String getServerState() {
		StringBuilder pollStateStringBuilder = new StringBuilder();
		pollStateStringBuilder.append("main projectName[");
		pollStateStringBuilder.append(mainProjectName);
		pollStateStringBuilder.append("]'s AnyProjectServer state");
		pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		pollStateStringBuilder.append(mainProjectServer.getProjectServerState());
		
		return pollStateStringBuilder.toString();
	}
}
