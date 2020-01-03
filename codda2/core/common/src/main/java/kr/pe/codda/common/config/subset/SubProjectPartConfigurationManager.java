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

package kr.pe.codda.common.config.subset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.pe.codda.common.config.subset.ProjectPartConfiguration;

/**
 * 모든 서브 프로젝트 파트 설정
 * 
 * @author Won Jonghoon
 *
 */
public class SubProjectPartConfigurationManager {
	private List<String> subProjectNamelist = new ArrayList<String>();
	private HashMap<String, ProjectPartConfiguration> subProjectPartConfigurationHash = 
			new HashMap<String, ProjectPartConfiguration>();

	/*
	public void clear() {
		subProjectNamelist.clear();
		subProjectPartConfigurationHash.clear();
	}
	*/
	
	/**
	 * 서브 프로젝트 파트 설정을 추가한다.
	 * @param subProjectPartConfiguration 서브 프로젝트 파트 설정
	 * @throws IllegalArgumentException 지정한 '서브 프로젝트 파트 설정' 이 null 인 경우 혹은 이미 등록된 경우에 던지는 예외
	 */
	public void addSubProjectPartConfiguration(ProjectPartConfiguration subProjectPartConfiguration) throws IllegalArgumentException {
		if (null == subProjectPartConfiguration) {
			throw new IllegalArgumentException("the paramter subProjectPartConfiguration is null");
		}
		
		String subProjectName = subProjectPartConfiguration.getProjectName();
		
		if (isRegistedSubProjectName(subProjectName)) {
			throw new IllegalArgumentException("the paramter subProjectPartConfiguration's sub project name was registed");
		}
		
		subProjectNamelist.add(subProjectName);
		subProjectPartConfigurationHash.put(subProjectName, subProjectPartConfiguration);
	}
	
	/**
	 * @return 서브 프로젝트 이름 목록
	 */
	public List<String> getSubProjectNamelist() {
		return subProjectNamelist;
	}

	/**
	 * @param subProjectName 서브 프로젝트 이름
	 * @return 지정한 서브 프로젝트 이름을 갖는 '서브 프로젝트 설정' 등록 여부
	 * @throws IllegalArgumentException 지정한 '서브 프로젝트 이름' 이 null 인 경우 던지는 예외
	 */
	public boolean isRegistedSubProjectName(String subProjectName) throws IllegalArgumentException {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the paramter subProjectName is null");
		}
		
		return subProjectNamelist.contains(subProjectName);
	}

	/**
	 * @param subProjectName  서브 프로젝트 이름
	 * @return  지정한 서브 프로젝트 이름을 갖는 '서브 프로젝트 설정', 단 없으면 null 을 반환한다.
	 * @throws IllegalArgumentException 지정한 '서브 프로젝트 이름' 이 null 인 경우 던지는 예외
	 */
	public ProjectPartConfiguration getSubProjectPartConfiguration(String subProjectName) throws IllegalArgumentException {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the paramter subProjectName is null");
		}
		
		return subProjectPartConfigurationHash.get(subProjectName);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("AllSubProjectPartValueObject [subProjectNamelist=");
		builder.append(subProjectNamelist != null ? subProjectNamelist.subList(
				0, Math.min(subProjectNamelist.size(), maxLen)) : null);
		builder.append("]");
		return builder.toString();
	}
}
