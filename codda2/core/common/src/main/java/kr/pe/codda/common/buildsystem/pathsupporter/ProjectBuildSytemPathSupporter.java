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

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.LogType;

/**
 * 프로젝트 경로 관련 추상화 클래스, 단 응용 클라이언트, 웹 클라이언트, 웹 루트, 서버 관련 경로는 담당 추상화 클래스가 담당한다. 
 * 
 * @author Won Jonghoon
 *
 */
public abstract class ProjectBuildSytemPathSupporter {
	 
	/**
	 * @param installedPathString 설치 경로
	 * @return project base path => [installed path]/project
	 */
	public static String getProjectBasePathString(
			String installedPathString) {
		if (null == installedPathString) {
			throw new IllegalArgumentException(
					"the parameter installedPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter installedPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(installedPathString);
		strBuilder.append(File.separator);
		strBuilder.append("project");
		return strBuilder.toString();
	}
	
	/**   */
	/**
	 * 
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return [project path] => [project base path]/[main project name]
	 */
	public static String getProjectPathString(String installedPathString,  String mainProjectName) {
		if (null == mainProjectName) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is null");
		}
		
		if (mainProjectName.equals("")) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is a empty string");
		}
		

		StringBuilder strBuilder = new StringBuilder(
				getProjectBasePathString(installedPathString));
		strBuilder.append(File.separator);
		strBuilder.append(mainProjectName);
		return strBuilder.toString();
	}
	
	
	/** 
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @param logType 로그 종류, 참고 : {@link LogType}
	 * @return log path => [project path]/log/[log type name]
	 */
	public static String getProjectLogPathString(String installedPathString, String mainProjectName, LogType logType) {		
		if (null == logType) {
			throw new IllegalArgumentException(
					"the parameter logType is null");
		}
		

		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("log");
		strBuilder.append(File.separator);		
		strBuilder.append(logType.toString().toLowerCase());		
		return strBuilder.toString();
	}

	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return project config path => [proejct path]/config
	 */
	public static String getProjectConfigDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("config");
		return strBuilder.toString();
	}

	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return project config file path => [project config path]/[project config short file name]
	 */
	public static String getProejctConfigFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectConfigDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.CONFIG_FILE_NAME);
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return project resources path => [project path]/resources
	 */
	public static String getProjectResourcesDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return project email properties file => [project path]/resources/email.properties
	 */
	public static String getProjectEmailPropertiesFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("email.properties");
		
		return strBuilder.toString();
	}
	
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return logback config file path => [project resources path]/[logack log short file name] 
	 */
	public static String getProjectLogbackConfigFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.LOGBACK_LOG_FILE_NAME);
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @param dbcpName
	 * @return DBCP configuration file path => [project path]/resources/dbcp/dbcp.[dbcp name].properties
	 */
	public static String getProjectDBCPConfigFilePathString(String installedPathString, String mainProjectName,
			 String dbcpName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("dbcp");
		strBuilder.append(File.separator);		
		strBuilder.append("dbcp.");
		strBuilder.append(dbcpName);
		strBuilder.append(".properties");

		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return message info path => [project path]/resources/message_info
	 */
	public static String getProjectMessageInfoDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @param messageID 메시지 식별자
	 * @return message info path => [project path]/resources/message_info/[message id].xml
	 */
	public static String getProjectMessageInfoFilePathString(String installedPathString, String mainProjectName, String messageID) {
		StringBuilder strBuilder = new StringBuilder(getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append(messageID)
				.append(".xml");
		return strBuilder.toString();
	}
		
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return RSA keypair path => [project path]/resources/rsa_keypair
	 */
	public static String getSessionKeyRSAKeypairPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("rsa_keypair");
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return RSA Publickey file => [RSA keypair path]/[publickey short file name]
	 */
	public static String getSessionKeyRSAPublickeyFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME);
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return RSA Publickey file => [RSA keypair path]/[privatekey short file name]
	 */
	public static String getSessionKeyRSAPrivatekeyFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME);
		return strBuilder.toString();
	}
	
	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return db initialization path => [project path]/resources/db_initialization
	 */
	public static String getDBInitializationDirecotryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append("db_initialization");
		return strBuilder.toString();
	}
	

	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @return client build base path => [project path]/client_build
	 */
	public static String getClientBuildBasePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("client_build");

		return strBuilder.toString();
	}
	
	/**
	 * @param separator 구별자
	 * @return src[separator]main[separator]java
	 */
	public static String getJavaMainSourceDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder("src")
			.append(separator).append("main")
			.append(separator).append("java"); 
		return relativePathStringBuilder.toString();
	}
	
	
	/**
	 * @param separator 구별자
	 * @return src[separator]main[separator]test
	 */
	public static String getJavaTestSourceDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder("src")
			.append(separator).append("main")
			.append(separator).append("test")
			.append(separator); 
		return relativePathStringBuilder.toString();
	}
	
	/**
	 * @param separator 구별자
	 * @return src[separator]main[separator]java[separator]+ {@link CommonStaticFinalVars#BASE_PACKAGE_NAME} 에서 패키지 구별자 '.' 문자를 [separator]로 치환한 문자열
	 */
	public static String getJavaSourceBaseDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder(getJavaMainSourceDirectoryRelativePath(separator))
			.append(separator);
		
		for (char ch : CommonStaticFinalVars.BASE_PACKAGE_NAME.toCharArray()) {
			if (ch == '.') {
				relativePathStringBuilder.append(separator);
			} else {
				relativePathStringBuilder.append(ch);
			}
		};
		
		return relativePathStringBuilder.toString();
	}
	
	
	/**
	 * @param separator 구별자
	 * @return src[separator]main[separator]java[separator]+ {@link CommonStaticFinalVars#BASE_MESSAGE_CLASS_FULL_NAME} 에서 패키지 구별자 '.' 문자를 [separator]로 치환한 문자열
	 */
	public static String getMessageIOSourceBaseDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder(getJavaMainSourceDirectoryRelativePath(separator))
			.append(separator);
		
		// kr.pe.codda.message package to relative path
		for (char ch : CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME.toCharArray()) {
			if (ch == '.') {
				relativePathStringBuilder.append(separator);
			} else {
				relativePathStringBuilder.append(ch);
			}
		};
		
		return relativePathStringBuilder.toString();
	}
	
	/**
	 * @param separator 구별자
	 * @return src[separator]main[separator]java[separator]+ {@link CommonStaticFinalVars#BASE_SERVER_TASK_CLASS_FULL_NAME} 에서 패키지 구별자 '.' 문자를 [separator]로 치환한 문자열
	 */
	public static String getServerTaskDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder(getJavaMainSourceDirectoryRelativePath(separator))
			.append(separator);

		for (char ch : CommonStaticFinalVars.BASE_SERVER_TASK_CLASS_FULL_NAME.toCharArray()) {
			if (ch == '.') {
				relativePathStringBuilder.append(separator);
			} else {
				relativePathStringBuilder.append(ch);
			}
		};
		
		return relativePathStringBuilder.toString();
	}
	
}
