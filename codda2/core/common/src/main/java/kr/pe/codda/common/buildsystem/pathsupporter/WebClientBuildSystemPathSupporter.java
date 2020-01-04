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

/**
 * 프로젝트의 서버 빌드 경로 관련 추상화 클래스
 * 
 * @author Won Jonghoon
 *
 */
public class WebClientBuildSystemPathSupporter {
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return web client build path =&gt; [project path]/client_build/web_build
	 */
	public static String getWebClientBuildPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(ProjectBuildSytemPathSupporter.getClientBuildBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_build");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return web client build.xml =&gt; [web client build path]/build.xml
	 */
	public static String getWebClientAntBuildXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");

		return strBuilder.toString();
	}
	
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return ant.properties =&gt; [web client build path]/webAnt.properties
	 */
	public static String getWebClientAntPropertiesFilePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("webAnt.properties");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [web client build path]/[message source file's relative path]
	 */
	public static String getWebClinetIOSourcePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator));
		
		return strBuilder.toString();
	}
}
