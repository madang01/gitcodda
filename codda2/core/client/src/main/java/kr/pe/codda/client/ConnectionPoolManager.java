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

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.part.RunningProjectConfiguration;
import kr.pe.codda.common.config.part.MainProjectPartConfiguration;
import kr.pe.codda.common.config.part.SubProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;

/**
 * 클라이언트 프로젝트 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class ConnectionPoolManager {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	/** 모니터 객체 */
	// private final Object monitor = new Object();
	private List<String> subProjectNameList = null;
	private HashMap<String, AnyProjectConnectionPoolIF> subProjectConnectionPoolHash = new HashMap<String, AnyProjectConnectionPoolIF>();

	private AnyProjectConnectionPoolIF mainProjectConnectionPool = null;

	private final String mainProjectName;

	private AnyProjectConnectionPoolMonitor anyProjectConnectionPoolMonitor = null;

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ClientProjectManagerHolder {
		static final ConnectionPoolManager singleton = new ConnectionPoolManager();
	}

	
	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드 
	 * @return ConnectionPoolManager 객체
	 */
	public static ConnectionPoolManager getInstance() {
		return ClientProjectManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * 
	 * @throws NoMoreWrapBufferException
	 */
	private ConnectionPoolManager() {
		CoddaConfiguration coddaConfiguration = CoddaConfigurationManager.getInstance()
				.getCoddaConfiguration();
		
		mainProjectName = coddaConfiguration.getMainProjectName();
		
		RunningProjectConfiguration runningProjectConfiguration = coddaConfiguration.getRunningProjectConfiguration();
		
		MainProjectPartConfiguration mainProjectPartConfiguration = runningProjectConfiguration.getMainProjectPartConfiguration();		

		try {
			mainProjectConnectionPool = new AnyProjectConnectionPool(mainProjectName, mainProjectPartConfiguration);
		} catch (Exception e) {
			String errorMessage = new StringBuilder("fail to initialize a main project connection pool[")
					.append(mainProjectName).append("]").toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}
		

		subProjectNameList = runningProjectConfiguration.SUBPROJECT.getNameList();

		for (String subProjectName : subProjectNameList) {
			AnyProjectConnectionPool subClientProject = null;
			try {
				SubProjectPartConfiguration subProjectPartConfiguration = runningProjectConfiguration.SUBPROJECT.getProjectPartConfiguration(subProjectName);
				
				subClientProject = new AnyProjectConnectionPool(subProjectName, subProjectPartConfiguration);
				subProjectConnectionPoolHash.put(subProjectName, subClientProject);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to initialize a sub project connection pool[")
						.append(subProjectName).append("] of main project[").append(mainProjectName).append("]")
						.toString();
				log.log(Level.SEVERE, errorMessage, e);
				System.exit(1);
			}
		}
		
		anyProjectConnectionPoolMonitor = new AnyProjectConnectionPoolMonitor(
				mainProjectPartConfiguration.getClientMonitorTimeInterval());
		anyProjectConnectionPoolMonitor.start();
	}

	/**
	 * 프로젝트 이름에 해당하는 외부에서 바라보는 시각을 가지는 클라이언트 프로젝트를 얻는다.
	 * 
	 * @param subProjectName 서브 프로젝트 이름
	 * @return 프로젝트 이름에 해당하는 외부 시각 클라이언트 프로젝트
	 * @throws IllegalStateException 서브 프로젝트 연결 폴 생성시 에러가 발생한 상태일 경우 던지는 예외
	 */
	public AnyProjectConnectionPoolIF getSubProjectConnectionPool(String subProjectName) throws IllegalStateException {
		AnyProjectConnectionPoolIF subProjectConnectionPool = subProjectConnectionPoolHash.get(subProjectName);
		if (null == subProjectConnectionPool) {
			String errorMessage = new StringBuilder("fail to initialize a sub project connection pool[")
					.append(subProjectName).append("] of main project[").append(mainProjectName).append("]").toString();
			throw new IllegalStateException(errorMessage);
		}

		return subProjectConnectionPool;
	}


	/**
	 * 프롲게트 연결 폴 모니터 쓰레드
	 */
	private class AnyProjectConnectionPoolMonitor extends Thread {
		private long monitorTimeInterval;

		/**
		 * 생성자
		 * @param monitorTimeInterval 모니터 간격, 단위 ms.
		 */
		public AnyProjectConnectionPoolMonitor(long monitorTimeInterval) {
			this.monitorTimeInterval = monitorTimeInterval;
		}

		@Override
		public void run() {
			log.info("AnyProjectConnectionPoolMonitor start");
			try {
				while (!Thread.currentThread().isInterrupted()) {
					log.info(getPoolState());
					Thread.sleep(monitorTimeInterval);
				}
			} catch (InterruptedException e) {
				log.info("AnyProjectConnectionPoolMonitor::interrupr");
			} catch (Exception e) {
				log.log(Level.INFO, "AnyProjectConnectionPoolMonitor::unknow error", e);
			}
			log.info("AnyProjectConnectionPoolMonitor end");
		}

	}

	/**
	 * @return 폴 상태 문자열
	 */
	private String getPoolState() {
		StringBuilder pollStateStringBuilder = new StringBuilder();
		pollStateStringBuilder.append("main projectName[");
		pollStateStringBuilder.append(mainProjectName);
		pollStateStringBuilder.append("]'s AnyProjectConnectionPool state");
		pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		pollStateStringBuilder.append(mainProjectConnectionPool.getPoolState());

		for (String subProjectName : subProjectNameList) {
			AnyProjectConnectionPoolIF subProjectConnectionPool = subProjectConnectionPoolHash.get(subProjectName);

			pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			pollStateStringBuilder.append("sub projectName[");
			pollStateStringBuilder.append(subProjectName);
			pollStateStringBuilder.append("]'s AnyProjectConnectionPool state");
			pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			pollStateStringBuilder.append(subProjectConnectionPool.getPoolState());
		}
		return pollStateStringBuilder.toString();
	}
}
