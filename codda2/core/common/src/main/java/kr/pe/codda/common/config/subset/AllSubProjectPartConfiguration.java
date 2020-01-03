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

public class AllSubProjectPartConfiguration {
	private List<String> subProjectNamelist = new ArrayList<String>();
	private HashMap<String, ProjectPartConfiguration> subProjectPartConfigurationHash = 
			new HashMap<String, ProjectPartConfiguration>();

	public void clear() {
		subProjectNamelist.clear();
		subProjectPartConfigurationHash.clear();
	}
	public void addSubProjectPartValueObject(ProjectPartConfiguration subProjectPartValueObject) {
		if (null == subProjectPartValueObject) {
			throw new IllegalArgumentException("the paramter subProjectPartValueObject is null");
		}
		
		String subProjectName = subProjectPartValueObject.getProjectName();
		subProjectNamelist.add(subProjectName);
		subProjectPartConfigurationHash.put(subProjectName, subProjectPartValueObject);
	}
	
	public List<String> getSubProjectNamelist() {
		return subProjectNamelist;
	}

	public boolean isRegistedSubProjectName(String subProjectName) {
		return (null != subProjectPartConfigurationHash.get(subProjectName));
	}

	public ProjectPartConfiguration getSubProjectPartConfiguration(String projectName) {
		return subProjectPartConfigurationHash.get(projectName);
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
