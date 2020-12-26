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
import kr.pe.codda.common.config.part.RunningProjectConfiguration;
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
	
	private final String titleOfConfigFile;

	private RunningProjectConfiguration runningProjectConfiguration = new RunningProjectConfiguration();

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
		
		titleOfConfigFile = new StringBuilder("project[").append(mainProjectName).append("]'s config file").toString();
	}


	/**
	 * 만약 서버 주소가 다르다면 새로운 서버 주소로 교체후 저장한다.
	 * 
	 * @param newServerHost 새로운 서버 호스트 주소
	 * @param newServerPort 새로운 서버 포트
	 * @throws IllegalArgumentException 파라미터 값이 null 이거나 잘못된 값이 들어올 경우 던지는 예외
	 * @throws IOException 저장시 에러 발생시 던지는 예외
	 */
	public void changeServerAddressIfDifferent(String newServerHost, int newServerPort) throws IllegalArgumentException, IOException {
		if (null == newServerHost) {
			throw new IllegalArgumentException("the parameter newServerHost is null");
		}

		if (newServerHost.equals("")) {
			throw new IllegalArgumentException("the parameter newServerHost is a empty string");
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(newServerHost)) {
			throw new IllegalArgumentException("the parameter newServerHost has any leading or tailing white space");
		}
				
		MainProjectPartConfiguration mainProjectPartConfiguration = runningProjectConfiguration.getMainProjectPartConfiguration();
		

		String oldSeverHost = mainProjectPartConfiguration.getServerHost();
		int oldServerPort = mainProjectPartConfiguration.getServerPort();

		if (newServerHost.equals(oldSeverHost) && newServerPort == oldServerPort) {
			return;
		}

		mainProjectPartConfiguration.setServerHost(newServerHost);
		mainProjectPartConfiguration.setServerPort(newServerPort);
		
		SequencedProperties configFileSequencedProperties = SequencedPropertiesUtil
				.loadSequencedPropertiesFile(configFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		
		mainProjectPartConfiguration.toPropertiesForServerHost(configFileSequencedProperties);
		mainProjectPartConfiguration.toPropertiesForServerPort(configFileSequencedProperties);		

		SequencedPropertiesUtil.overwriteSequencedPropertiesFile(configFileSequencedProperties,
				titleOfConfigFile, configFilePathString,
				CommonStaticFinalVars.SOURCE_FILE_CHARSET);
	}

	/**
	 * 기동중인 프로젝트 설정을 파라미터 '새 기동중인 프로젝트 설정' 로 한다.
	 * @param newRunningProjectConfiguration 신규 기동중인 프로젝트 설정
	 */
	public void setRunningProjectConfiguration(RunningProjectConfiguration newRunningProjectConfiguration) {
		if (null == newRunningProjectConfiguration) {
			throw new IllegalArgumentException("the parameter newRunningProjectConfiguration is null");
		}
		
		this.runningProjectConfiguration = newRunningProjectConfiguration;
	}

	/**
	 * @return 기동중인 프로젝트 설정
	 */
	public RunningProjectConfiguration getRunningProjectConfiguration() {
		return runningProjectConfiguration;
	}	
	

	/**
	 * 설정 파일 내용을 읽어와서 그 내용을 '기동중인 프로젝트 설정' 에 저장한후 의존성 검사를 수행후 읽어온 설정 파일 프로퍼티를 반환한다.
	 * @return 읽어온 설정 파일 프로퍼티를 반환한다.
	 * @throws IllegalArgumentException 내부 로직에서 파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws PartConfigurationException 있어야 하는 설정 파일 항목이 없거나 값이 잘못된 경우 던지는 예외
	 * @throws FileNotFoundException 설정 파일이 없는 경우 던지는 예외 
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 */
	public SequencedProperties loadConfigFile() throws IllegalArgumentException, PartConfigurationException, FileNotFoundException, IOException {
		SequencedProperties configSequencedProperties = SequencedPropertiesUtil
				.loadSequencedPropertiesFile(configFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		
		runningProjectConfiguration.fromProperties(configSequencedProperties);
		
		runningProjectConfiguration.checkForDependencies();
		
		return configSequencedProperties;
	}
	
	/**
	 * '기동중인 프로젝트 설정' 의 내용을 시퀀스 프로퍼티로 변환한후 설정 파일에 저장한다.
	 * 
	 * @throws FileNotFoundException 설정 파일이 없을때 던지는 예외
	 * @throws IOException 설정 파일 덮어 쓸때 IO 에러 발생시 던지는 예외
	 */
	public void saveConfigFile() throws FileNotFoundException, IOException {
		SequencedProperties targetSequencedProperties = new SequencedProperties();
		runningProjectConfiguration.toProperties(targetSequencedProperties);
		
		
		File sourcePropertiesFile = new File(configFilePathString);
		
		if (sourcePropertiesFile.exists()) {
			SequencedPropertiesUtil.overwriteSequencedPropertiesFile(targetSequencedProperties, titleOfConfigFile, configFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} else {
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(targetSequencedProperties, titleOfConfigFile, configFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		}
				
		
	}
	
	public void loadDefault() throws IllegalArgumentException, PartConfigurationException {
		SequencedProperties defaultSequencedProperties = new SequencedProperties();
		RunningProjectConfiguration runningProjectConfigurationWithoutInit = new RunningProjectConfiguration();		
		runningProjectConfigurationWithoutInit.toProperties(defaultSequencedProperties);
		RunningProjectConfiguration.applyIntalledPath(installedPathString, mainProjectName, defaultSequencedProperties);
		
		this.runningProjectConfiguration.fromProperties(defaultSequencedProperties);
		
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
	 * @return 메인 프로젝트 설정 파일의 제목
	 */
	public String getTitleOfConfigFile() {		
		return titleOfConfigFile;
	}

}
