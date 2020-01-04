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

package kr.pe.codda.common.buildsystem.pathsupporter;

import java.io.File;

public class WebRootBuildSystemPathSupporter {
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [project path]/user_web_app_base 
	 */
	public static String getUserWebRootBasePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("user_web_app_base");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [project path]/user_web_app_base/upload
	 */
	public static String getUserWebUploadPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("upload");
		return strBuilder.toString();
	}
		
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [project path]/user_web_app_base/temp
	 */
	public static String getUserWebTempPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("temp");
		return strBuilder.toString();
	}	

	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [project path]/user_web_app_base/ROOT
	 */
	public static String getUserWebRootPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("ROOT");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [project path]/user_web_app_base/ROOT/WEB-INF
	 */
	public static String getUserWebINFPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebRootPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("WEB-INF");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [project path]/user_web_app_base/ROOT/WEB-INF/web.xml
	 */
	public static String getUserWebRootXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebINFPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web.xml");
		return strBuilder.toString();
	}	
}
