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
import java.io.FileNotFoundException;
import java.io.IOException;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.part.MainProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.common.util.SequencedPropertiesUtil;

/**
 * 코다 설정, 설정 파일 로드및 저장 관련 기능을 담당함.
 * 
 * @author Won Jonghoon
 *
 */
public class CoddaConfiguration {
	// private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final String mainProjectName;
	private final String installedPathString;

	private final String configFilePathString;

	private DefaultConfiguration defaultConfiguration = new DefaultConfiguration();

	private SequencedProperties configSequencedProperties = null;

	/**
	 * 생성자
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @throws IllegalArgumentException 파라미터 값이 잘못되엇을 경우 던지는 예외
	 */
	public CoddaConfiguration(String installedPathString, String mainProjectName)
			throws IllegalArgumentException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException("the parameter installedPathString is a empty string");
		}
				

		File installedPath = new File(installedPathString);

		if (!installedPath.exists()) {
			String errorMessage = new StringBuilder(
					"the installed path(=the parameter installedPathString[")
							.append(installedPathString).append("]) doesn't exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!installedPath.isDirectory()) {
			String errorMessage = new StringBuilder(
					"the installed path(=the parameter installedPathString[")
							.append(installedPathString).append("]) is not a directory").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}

		if (mainProjectName.equals("")) {
			throw new IllegalArgumentException("the parameter mainProjectName is a empty string");
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(mainProjectName)) {
			throw new IllegalArgumentException("the parameter mainProjectName has leading or tailing white space");
		}

		this.mainProjectName = mainProjectName;
		this.installedPathString = installedPathString;		
		
		configFilePathString = ProjectBuildSytemPathSupporter
				.getProejctConfigFilePathString(installedPathString, mainProjectName);
	}


	/**
	 * 만약 서버 주소가 다르다면 새로운 서버 주소로 교체후 저장한다.
	 * 
	 * @param newServerHost 새로운 서버 호스트 주소
	 * @param newServerPort 새로운 서버 포트
	 * @throws IllegalArgumentException 파라미터 값이 null 이거나 잘못된 값이 들어올 경우 던지는 예외
	 * @throws IllegalStateException 컨피그 파일을 먼저 로딩 안하고 호출할 경우 던지는 예외
	 * @throws IOException 저장시 에러 발생시 던지는 예외
	 */
	public void changeServerAddressIfDifferent(String newServerHost, int newServerPort) throws IllegalArgumentException, IllegalStateException, IOException {
		if (null == newServerHost) {
			throw new IllegalArgumentException("the parameter newServerHost is null");
		}

		if (newServerHost.equals("")) {
			throw new IllegalArgumentException("the parameter newServerHost is a empty string");
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(newServerHost)) {
			throw new IllegalArgumentException("the parameter newServerHost has any leading or tailing white space");
		}
		
		if (null == configSequencedProperties) {
			throw new IllegalStateException("config file not load");
		}
		
		
		MainProjectPartConfiguration mainProjectPartConfiguration = defaultConfiguration.getMainProjectPartConfiguration();
		

		String oldSeverHost = mainProjectPartConfiguration.getServerHost();
		int oldServerPort = mainProjectPartConfiguration.getServerPort();

		if (newServerHost.equals(oldSeverHost) && newServerPort == oldServerPort) {
			return;
		}

		mainProjectPartConfiguration.setServerHost(newServerHost);
		mainProjectPartConfiguration.setServerPort(newServerPort);
		
		mainProjectPartConfiguration.toPropertiesForServerHost(configSequencedProperties);
		mainProjectPartConfiguration.toPropertiesForServerPort(configSequencedProperties);		

		SequencedPropertiesUtil.overwriteSequencedPropertiesFile(configSequencedProperties,
				getConfigPropertiesTitle(), configFilePathString,
				CommonStaticFinalVars.SOURCE_FILE_CHARSET);
	}

	
	public void setDefaultConfiguration(DefaultConfiguration defaultConfiguration) {
		if (null == defaultConfiguration) {
			throw new IllegalArgumentException("the parameter defaultConfiguration is null");
		}
		
		this.defaultConfiguration = defaultConfiguration;
	}

	public DefaultConfiguration getDefaultConfiguration() {
		return defaultConfiguration;
	}	
	
	
	public void load() throws IllegalArgumentException, PartConfigurationException, FileNotFoundException, IOException {
		SequencedProperties configSequencedProperties = SequencedPropertiesUtil
				.loadSequencedPropertiesFile(configFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		
		defaultConfiguration.fromProperteisWithDependencyCheck(configSequencedProperties);
		
		/** 에러 없는 경우에 설정 */
		this.configSequencedProperties = configSequencedProperties;
	}
		
	
	/**
	 * @return 메인 프로젝트 이름
	 */
	public String getMainProjectName() {
		return mainProjectName;
	}

	/**
	 * @return 설치 경로
	 */
	public String getInstalledPathString() {
		return installedPathString;
	}

	/** 
	 * @return 설정 파일 내용이 담긴 시퀀스 프로퍼티
	 */
	/*
	public SequencedProperties getConfigurationSequencedPropties() {
		return configSequencedProperties;
	}
	*/

	/**
	 * @return 설정 파일의 제목
	 */
	public String getConfigPropertiesTitle() {
		return getConfigPropertiesTitle(mainProjectName);
	}

	/**
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return 지정한 '메인 프로젝트 이름' 을 갖는 설정 파일 제목
	 */
	public static String getConfigPropertiesTitle(String mainProjectName) {
		return new StringBuilder("project[").append(mainProjectName).append("]'s config file").toString();
	}
}
