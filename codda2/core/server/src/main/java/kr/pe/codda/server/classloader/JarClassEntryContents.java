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

/**
 * jar 파일에 포함된 클래스 파일에 관한 (1) 파일 경로 문자열 (2) 클래스 전체 이름 (3) 클래스 파일 내용 묶음 클래스 
 * 
 * @author Won Jonghoon
 *
 */
public class JarClassEntryContents {
	private String ownerJarFilePathString;
	private String classFullName;
	private byte[] classFileContents;	
	
	/**
	 * 생성자
	 * @param ownerJarFilePathString jar 파일 경로 문자열
	 * @param classFullName 클래스 전체 이름
	 * @param classFileContents 클래스 파일 파일 내용
	 */
	public JarClassEntryContents(String ownerJarFilePathString, String classFullName, byte[] classFileContents) {
		this.ownerJarFilePathString = ownerJarFilePathString;
		this.classFullName = classFullName;
		this.classFileContents = classFileContents;
	}	
	
	/**
	 * @return 파일 경로 문자열
	 */
	public String getOwnerJarFilePathString() {
		return ownerJarFilePathString;
	}
	
	/**
	 * @return 클래스 전체 이름
	 */
	public String getClassFullName() {
		return classFullName;
	}
	
	/**
	 * @return 클래스 파일 파일 내용
	 */
	public byte[] getClassFileContents() {
		return classFileContents;
	}	
}
