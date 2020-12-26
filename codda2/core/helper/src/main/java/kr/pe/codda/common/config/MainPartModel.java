/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.pe.codda.common.config;

import java.util.ArrayList;

/**
 * @author Won Jonghoon
 *
 */
public class MainPartModel implements PartModelIF {
	private final String partName;
	
	private ArrayList<ConfigItemInfo> configItemInfoList = new ArrayList<ConfigItemInfo>();
	
	private Object[][] tableModelValues;
	
	
	public MainPartModel(String partName) {
		if (null == partName) {
			throw new IllegalArgumentException("the parameter partName is null");
		}
		
		this.partName = partName;
	}
	
	
	public String getPartName() {
		return partName;
	}
	
	public ConfigItemInfo getConfigItemInfo(String id) {
		for (ConfigItemInfo configItemInfo : configItemInfoList) {
			if (configItemInfo.getID().equals(id)) {
				return configItemInfo;
			}
		}
		
		return null;
	}
	
	public void addConfigItemInfo(ConfigItemInfo newConfigItemInfo) {
		if (null == newConfigItemInfo) {
			throw new IllegalArgumentException("the parameter newConfigItemInfo is null");
		}
		
		configItemInfoList.add(newConfigItemInfo);
	}

	public ArrayList<ConfigItemInfo> getConfigItemInfoList() {
		return configItemInfoList;
	}
}
