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
package kr.codda.model;

/**
 * 코다 설치 경로 + 선택된 메인 프로젝트 이름 + 설정  파일 경로 + 설정 파일 내용 
 * 
 * @author Won Jonghoon
 *
 */
public class ConfigFileInformation {
	/**
	 * 설치 경로
	 */
	private String installedPathString;
	
	/**
	 * 선택된 메인 프로젝트 이름 
	 */
	private String selectedMainProjectName;
	
	/**
	 * 설정 파일 경로
	 */
	private String configFilePathString;
	
	/**
	 * 설정 파일의 내용을 담은 json 문자열
	 */
	private String configFileJsonString;

	public String getInstalledPathString() {
		return installedPathString;
	}

	public void setInstalledPathString(String installedPathString) {
		this.installedPathString = installedPathString;
	}

	public String getSelectedMainProjectName() {
		return selectedMainProjectName;
	}

	public void setSelectedMainProjectName(String selectedMainProjectName) {
		this.selectedMainProjectName = selectedMainProjectName;
	}

	public String getConfigFilePathString() {
		return configFilePathString;
	}

	public void setConfigFilePathString(String configFilePathString) {
		this.configFilePathString = configFilePathString;
	}

	public String getConfigFileJsonString() {
		return configFileJsonString;
	}

	public void setConfigFileJsonString(String configFileJsonString) {
		this.configFileJsonString = configFileJsonString;
	}
}
