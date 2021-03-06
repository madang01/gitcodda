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
package kr.pe.codda.common.config;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * <pre>
 * 코다 설정 관리자.
 * 
 * 자바 시스템 환경 설정 변수 2를 참고하여 환경 설정 파일을 읽어와 적재한다.
 * 첫번째 {@link CommonStaticFinalVars#JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME} 는 메인 프로젝트 이름
 * 마지막 두번째 {@link CommonStaticFinalVars#JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH} 는 설치 경로 
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public final class CoddaConfigurationManager {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private CoddaConfiguration coddaConfiguration = null;

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class ConfigurationManagerHolder {
		static final CoddaConfigurationManager singleton = new CoddaConfigurationManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static CoddaConfigurationManager getInstance() {
		return ConfigurationManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private CoddaConfigurationManager() {
		String runningProjectName = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME);
		String installedPathString = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH);
		
		

		if (null == runningProjectName) {
			String errorMessage = new StringBuilder()
					.append("java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME)
					.append("' is required, ex) java -D")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME)
					.append("=[project name] -D")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH)
					.append("=[installed path]").toString();
			
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}

		if (runningProjectName.equals("")) {
			String errorMessage = new StringBuilder()
					.append("java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME)
					.append("' is a empty string").toString();
			
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(runningProjectName)) {
			String errorMessage = new StringBuilder()
					.append("java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME)
					.append("' has leading or tailing white space").toString();
			
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}

		if (null == installedPathString) {
			String errorMessage = new StringBuilder()
					.append("java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH)
					.append("' is required, ex) java -D")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME)
					.append("=[project name] -D")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH)
					.append("=[installed path]").toString();
			
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}

		if (installedPathString.equals("")) {
			String errorMessage = new StringBuilder()
					.append("java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH)
					.append("' is a empty string").toString();
			
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}

		File installedPath = new File(installedPathString);

		if (!installedPath.exists()) {
			String errorMessage = new StringBuilder("the installed path(=java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH).append("'s value[")
					.append(installedPathString).append("]) doesn't exist").toString();
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}

		if (!installedPath.isDirectory()) {
			String errorMessage = new StringBuilder("the installed path(=java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH).append("'s value[")
					.append(installedPathString).append("]) is not a directory").toString();
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}

		
		
		try {
			coddaConfiguration = new CoddaConfiguration(installedPathString, runningProjectName);
			
			
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder()
					.append("check java system proprties -D")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME)
					.append("=")
					.append(runningProjectName)					
					.append(" -D")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH)
					.append("=")
					.append(installedPathString)
					.append(", errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.SEVERE, errorMessage);

			System.exit(1);
		} catch (Exception e) {
			log.log(Level.SEVERE, "unknown error", e);

			System.exit(1);
		}
		/*
		
		String configFilePathString = ProjectBuildSytemPathSupporter
				.getProejctConfigFilePathString(installedPathString, runningProjectName);
		try {
			coddaConfiguration.loadConfigFile();
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the configuration file[")
					.append(configFilePathString).append("] doesn't exist").toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to read the configuration file[")
					.append(configFilePathString).append("]").toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		} catch (PartConfigurationException e) {
			String errorMessage = new StringBuilder("the itemkey[")
					.append(e.getItemKey())
					.append("]'s value in config file[")
					.append(configFilePathString).append("] is bad, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		} catch (Exception e) {
			log.log(Level.SEVERE, "unknown error", e);

			System.exit(1);
		}
		*/
	}

	/**
	 * @return 기동중인 코다 설정
	 */
	public CoddaConfiguration getCoddaConfiguration() {
		return coddaConfiguration;
	}

}
