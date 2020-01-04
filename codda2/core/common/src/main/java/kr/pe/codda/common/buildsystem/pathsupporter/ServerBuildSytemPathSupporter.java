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
public abstract class ServerBuildSytemPathSupporter {
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return server build path =&gt; [project path]/server_build
	 */
	public static String getServerBuildPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("server_build");
		return strBuilder.toString();
	}

	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return server build.xml =&gt; [project path]/server_build/build.xml
	 */
	public static String getServerAntBuildXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return APP-INF path =&gt; [server build path]/APP-INF
	 */
	public static String getServerAPPINFPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("APP-INF");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return APP-INF class path =&gt; [APP-INF path]/classes
	 */
	public static String getServerAPPINFClassPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerAPPINFPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("classes");		
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [server build path]/[message source file's relative path]
	 */
	public static String getServerIOSourcePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator));
		
		return strBuilder.toString();
	}
}
