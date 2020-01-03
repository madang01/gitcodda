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

package kr.pe.codda.common.config.fileorpathstringgetter;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;

/**
 * 세션키에 사용되는 RSA 개인키 파일 경로 전체 이름 반환자
 * @author Won Jonghoon
 *
 */
public class SessionkeyRSAPrivatekeyFilePathStringReturner extends AbstractFileOrPathStringReturner {
	/**
	 * 생성자
	 * @param itemID 항목 식별자
	 */
	public SessionkeyRSAPrivatekeyFilePathStringReturner(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathString(
			String installedPathString,
			String mainProjectName, String ... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters has one more paramters");
		}
		
		return ProjectBuildSytemPathSupporter
				.getSessionKeyRSAPrivatekeyFilePathString(installedPathString, mainProjectName);
	}
}