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
package kr.pe.codda.common.config.part;

/**
 * @author Won Jonghoon
 *
 */
public class SubProjectPartConfiguration extends AbstractProjectPartConfiguration {
	public static final String PART_NAME = "subproject";
	private final String subProjectName;
	private final String prefixBeforeItemID;
	
	
	public SubProjectPartConfiguration(String subProjectName) {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}
		
		this.subProjectName = subProjectName;

		prefixBeforeItemID = new StringBuilder()
				.append(PART_NAME)
				.append(".").append(subProjectName)
				.append(".").toString();
		
	}
	
	public String getPartName() {
		return PART_NAME;
	}
	
	public String getPrefixBeforeItemID() {
		return prefixBeforeItemID;
	}
	
	/**
	 * @return 서브 프로젝트 이름
	 */
	public String getSubProjectName() {
		return subProjectName;
	}
	
}
