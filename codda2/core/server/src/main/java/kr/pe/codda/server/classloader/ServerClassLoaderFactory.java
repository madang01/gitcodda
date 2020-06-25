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
package kr.pe.codda.server.classloader;

import java.io.File;

import kr.pe.codda.common.classloader.SystemClassDeterminer;
import kr.pe.codda.common.classloader.SystemClassVerifierIF;
import kr.pe.codda.common.exception.PartConfigurationException;

/**
 * 서버 클래스 로더 팩토리
 * @author Won Jonghoon
 *
 */
public class ServerClassLoaderFactory {
	private final String serverAPPINFClassPathString;
	private final String projectResourcesPathString;
	private SystemClassVerifierIF excludedDynamicClassManager = new SystemClassDeterminer();
	
	
	private final String serverAPPINFClassPathStringForClassFullName;
	
	private final int lengthOfServerAPPINFClassPathStringForClassFullName;	
	private final int minOfAppInfClassFilePathStringLength;
	
	
	
	/**
	 * 생성자
	 * @param serverAPPINFClassPathString 서버 클래스의 동적 클래스 경로 문자열
	 * @param projectResourcesPathString 서버 클래스의 리소스 경로 문자열
	 * @throws PartConfigurationException 설정 관련 에러 발생시 던지는 예외
	 */
	public ServerClassLoaderFactory(String serverAPPINFClassPathString,
			String projectResourcesPathString) throws PartConfigurationException {
		if (null == serverAPPINFClassPathString) {
			throw new IllegalArgumentException("the parameter serverAPPINFClassPathString is null");
		}
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (! serverAPPINFClassPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter serverAPPINFClassPathString[")
					.append(serverAPPINFClassPathString)
					.append("] do not exist").toString();
		 	throw new PartConfigurationException(errorMessage);
		}
		
		if (! serverAPPINFClassPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter serverAPPINFClassPathString[")
					.append(serverAPPINFClassPathString)
					.append("] isn't a directory").toString();
		 	throw new PartConfigurationException(errorMessage);
		}
		
		if (null == projectResourcesPathString) {
			throw new IllegalArgumentException("the parameter projectResourcesPathString is null");
		}
		
		File projectResourcesPath = new File(projectResourcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter projectResourcesPathString[")
					.append(projectResourcesPathString)
					.append("] doesn't exist").toString();
		 	throw new PartConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter projectResourcesPathString[")
					.append(projectResourcesPathString)
					.append("] isn't a directory").toString();
		 	throw new PartConfigurationException(errorMessage);
		}
		
		this.serverAPPINFClassPathString = serverAPPINFClassPathString;
		this.projectResourcesPathString = projectResourcesPathString;
		
		
		serverAPPINFClassPathStringForClassFullName = new StringBuilder()
				.append(serverAPPINFClassPathString)
				.append(File.separatorChar).toString();
		lengthOfServerAPPINFClassPathStringForClassFullName = serverAPPINFClassPathStringForClassFullName.length();
		minOfAppInfClassFilePathStringLength = lengthOfServerAPPINFClassPathStringForClassFullName + 1 + ".class".length();
	}
	
	/**
	 * @return 신규 서버 클래스 로더 객체
	 */
	public ServerClassLoader createServerClassLoader() {
		return new ServerClassLoader(this, projectResourcesPathString, excludedDynamicClassManager);
	}
	
	/**
	 * 동적으로 로딩할 주어진 클래스 이름을 가지는 클래스 파일 경로를 반환한다.
	 * 
	 * @param classFullName
	 *            클래스 파일 경로를 얻고자 하는 클래스 이름
	 * @return 주어진 클래스 이름을 가지는 클래스 파일 경로
	 */
	public String getClassFilePathString(String classFullName) {
		String classFileName = new StringBuilder(serverAPPINFClassPathString).append(File.separator)
				.append(classFullName.replace(".", File.separator)).append(".class").toString();
		return classFileName;
	}
	
	/**
	 * 서버 동적 클래스 파일들이 위치하는 APP-INF 파일인 경우 해당 경로를 기준으로 하는 클래스 전체 이름을 반환한다.
	 * 단, APP-INF 경로 밑에 파일이 아닌 경우 {@link IllegalArgumentException} 를 던진다. 
	 * 
	 * @param appInfClassFile APP-INF 파일
	 * @return APP-INF 파일인 경우 해당 경로를 기준으로 하는 클래스 전체 이름
	 * @throws IllegalArgumentException 파라마터 'APP-INF 파일' 이 null 인 경우 혹은 APP-INF 경로 밑의 파일이 아닌 경우 던지는 예외
	 */
	public String toClassFullNameIfAppInfClassFile(File appInfClassFile) throws IllegalArgumentException {
		if (null == appInfClassFile) {
			throw new IllegalArgumentException("the parameter appInfClassFile is null");
		}

		String appInfClassFilePathString = appInfClassFile.getAbsolutePath();
		
		if (! appInfClassFilePathString.endsWith(".class")) {
			String errorMessage = new StringBuilder()
					.append("the parameter appInfClassFile[")
					.append(appInfClassFilePathString)
					.append("] is not a class file").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		int appInfClassFilePathStringLength = appInfClassFilePathString.length();
		
		if (appInfClassFilePathStringLength < minOfAppInfClassFilePathStringLength) {
			String errorMessage = new StringBuilder()
					.append("the parameter appInfClassFile[")
					.append(appInfClassFilePathString)
					.append("]'s length is less than min[")
					.append(minOfAppInfClassFilePathStringLength)
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! appInfClassFilePathString.startsWith(serverAPPINFClassPathStringForClassFullName)) {
			String errorMessage = new StringBuilder()
					.append("the parameter appInfClassFile[")
					.append(appInfClassFilePathString)
					.append("] is not a APP-INF file").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String middle = appInfClassFilePathString.substring(lengthOfServerAPPINFClassPathStringForClassFullName, appInfClassFilePathString.length() - ".class".length());

		String classFulleName = middle.replace(File.separatorChar, '.');

		return classFulleName;
	}
}
