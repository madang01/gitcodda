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
 * 공통 경로 관련  추상화 클래스, 설치 경로의 예 윈도 =&gt; "D:\gitcodda\codda2", 리눅스  "[개인 계정의 홈]/gitcodda/codda2"
 * 
 * @author Won Jonghoon
 *
 */
public abstract class CommonBuildSytemPathSupporter {
	
	/**
	 * @param installedPathString 설치 경로
	 * @return [installed path]/temp, 메시지 정보 파일로 부터 메시지 정보 파일을 저장할 임시 디렉토리
	 */
	public static String getCommonTempPathString(String installedPathString) {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException("the parameter InstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(installedPathString);
		strBuilder.append(File.separator);
		strBuilder.append("temp");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @return [installed path]/log
	 */
	public static String getCommonLogPathString(String installedPathString) {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException("the parameter installedPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(installedPathString);
		strBuilder.append(File.separator);
		strBuilder.append("log");
		return strBuilder.toString();
	}

	/**
	 * @param installedPathString 설치 경로
	 * @return common resource path =&gt; [installed path]/resources
	 */
	public static String getCommonResourcesPathString(String installedPathString) {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter InstalledPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException("the parameter InstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(installedPathString);
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @return message info path =&gt; [common resource path]/message_info
	 */
	public static String getCommonMessageInfoDirectoryPathString(String installedPathString) {
		StringBuilder strBuilder = new StringBuilder(getCommonResourcesPathString(installedPathString))
				.append(File.separator).append("message_info");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param messageID 메시지 식별자
	 * @return message info file path =&gt; [message info path]/[message id].xml
	 */
	public static String getCommonMessageInfoFilePathString(String installedPathString, String messageID) {
		StringBuilder strBuilder = new StringBuilder(getCommonMessageInfoDirectoryPathString(installedPathString))
				.append(File.separator).append(messageID).append(".xml");
		return strBuilder.toString();
	}

}
