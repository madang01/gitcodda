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
package kr.pe.codda.common.config2;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config2.part.CommonPartConfiguration;
import kr.pe.codda.common.config2.part.DBCPParConfiguration;
import kr.pe.codda.common.config2.part.ProjectPartConfiguration;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.GUIItemType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.common.util.SequencedProperties;


/**
 * @author Won Jonghoon
 *
 */
public class DefaultConfiguration {
	/** DBCP 목록 키 */
	public static final String DBCP_NAME_LIST_KEY_STRING = "dbcp.name_list.value";
	
	/** 서브 프로젝트 목록 키 */
	public static final String SUBPROJECT_NAME_LIST_KEY_STRING = "subproject.name_list.value";	
	
	private List<String> dbcpNameList = new ArrayList<String>();
	private HashMap<String, DBCPParConfiguration> dbcpPartConfigurationHash = new HashMap<String, DBCPParConfiguration>();
	
	
	private CommonPartConfiguration commonPartConfiguration = new CommonPartConfiguration();
	
	private ProjectPartConfiguration mainProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.MAIN, null);
	
	
	private List<String> subProjectList = new ArrayList<String>();
	private HashMap<String, ProjectPartConfiguration> subProjectPartConfigurationHash = new HashMap<String, ProjectPartConfiguration>();
	
	public static void applyIntalledPath(SequencedProperties sourceSequencedProperties,  String installedPathString, String mainProjectName) throws IllegalArgumentException, IllegalStateException {
		@SuppressWarnings("unchecked")
		Enumeration<String> keys = sourceSequencedProperties.keys();
		
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			
			if (key.endsWith(".gui_item_type")) {
				String value = sourceSequencedProperties.getProperty(key);
				
				final GUIItemType guiItemType;
				
				if (null == value) {
					guiItemType = GUIItemType.DATA;
				} else {
					value = value.toUpperCase();
					
					guiItemType = GUIItemType.valueOf(value);
				}				
				
				if (! GUIItemType.DATA.equals(guiItemType)) {
					String subkey = key.substring(0, key.length() - ".gui_item_type".length());
					
					String guiProjectHomeBaseRelativePathKey = 							
							new StringBuilder().append(subkey)
							.append(".")
							.append(guiItemType.name().toLowerCase()).toString();
					
					String guiProjectHomeBaseRelativePathValue = sourceSequencedProperties.getProperty(guiProjectHomeBaseRelativePathKey);
					
					if (null == guiProjectHomeBaseRelativePathValue) {
						String errorMessage = new StringBuilder()
								.append("the value of required item[")
								.append(guiProjectHomeBaseRelativePathKey)
								.append("] is null").toString();
						throw new IllegalStateException(errorMessage);
					}
					
					String itemKey = new StringBuilder().append(subkey)
							.append(".value").toString();
					
					String oldItemValue = sourceSequencedProperties.getProperty(itemKey);
					
					if (null == oldItemValue) {
						String errorMessage = new StringBuilder()
								.append("the value of required item[")
								.append(itemKey)
								.append("] is null").toString();
						throw new IllegalStateException(errorMessage);
					}
					
					
					String projectHomePathString = ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName);
					
					final String newItemValue;
					if (projectHomePathString.endsWith(File.separator)) {
						newItemValue = new StringBuilder()
								.append(projectHomePathString)
								.append(guiProjectHomeBaseRelativePathValue.replace("/", File.separator)).toString();
					} else {
						newItemValue = new StringBuilder()
								.append(projectHomePathString)
								.append(File.separatorChar)
								.append(guiProjectHomeBaseRelativePathValue.replace("/", File.separator)).toString();
					}
					
					sourceSequencedProperties.put(itemKey, newItemValue);
					
					String logMessage = new StringBuilder()
							.append("change the value[")
							.append(oldItemValue)
							.append("of the path or file type item[")
							.append(itemKey)
							.append("] to new value[")
							.append(newItemValue)
							.append("]").toString();
					
					Logger log = Logger.getLogger(CommonPartConfiguration.class.getName());
					log.info(logMessage);
				}
			}
			
		}
	}
	

	
	private void buildDBCPNameList(SequencedProperties sourceSequencedProperties) throws PartConfigurationException {
		String dbcpNameListValue = sourceSequencedProperties.getProperty(DBCP_NAME_LIST_KEY_STRING);
		
		if (null == dbcpNameListValue) {
			String errorMessage = new StringBuilder("the dbcp name list key(=")
					.append(DBCP_NAME_LIST_KEY_STRING)
					.append(") was not found in the parameter sourceSequencedProperties").toString();
			throw new PartConfigurationException(DBCP_NAME_LIST_KEY_STRING, errorMessage);
		}
		
		dbcpNameListValue = dbcpNameListValue.trim();
		Set<String> dbcpNameSet = new HashSet<>();

		if (!dbcpNameListValue.equals("")) {
			StringTokenizer tokens = new StringTokenizer(dbcpNameListValue, ",");

			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				String dbcpName = token.trim();
				if (dbcpNameSet.contains(dbcpName)) {
					String errorMessage = new StringBuilder("In the parameter sourceSequencedProperties, the dbcp name[").append(dbcpName)
							.append("] of the dbcp name list[")
							.append(dbcpNameListValue)
							.append("] is over").toString();
					throw new PartConfigurationException(DBCP_NAME_LIST_KEY_STRING, errorMessage);
				}

				dbcpNameList.add(dbcpName);
				dbcpNameSet.add(dbcpName);
			}
		}
	}
	
	
	private void buildSubProjectNameList(SequencedProperties sourceSequencedProperties) throws PartConfigurationException {
		String subProjectNameListValue = sourceSequencedProperties.getProperty(SUBPROJECT_NAME_LIST_KEY_STRING);
		
		if (null == subProjectNameListValue) {
			String errorMessage = new StringBuilder("the sub project name list key(=")
					.append(SUBPROJECT_NAME_LIST_KEY_STRING)
					.append(") was not found in the parameter sourceSequencedProperties").toString();
			throw new PartConfigurationException(SUBPROJECT_NAME_LIST_KEY_STRING, errorMessage);
		}
		
		Set<String> subProjectNameSet = new HashSet<>();
		if (!subProjectNameListValue.equals("")) {
			StringTokenizer tokens = new StringTokenizer(subProjectNameListValue, ",");

			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				String subProjectName = token.trim();
				if (subProjectNameSet.contains(subProjectName)) {
					String errorMessage = new StringBuilder("In the parameter sourceSequencedProperties, the sub project name[").append(subProjectName)
							.append("] of the sub project name list[")
							.append(subProjectNameListValue)
							.append("] is over").toString();
					throw new PartConfigurationException(SUBPROJECT_NAME_LIST_KEY_STRING, errorMessage);
				}

				subProjectList.add(subProjectName);
				subProjectNameSet.add(subProjectName);
			}
		}
	}

	
	public void toValueWithDependencyCheck(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {

		commonPartConfiguration.toValue(sourceSequencedProperties);
		commonPartConfiguration.checkForDependencies(sourceSequencedProperties);
		
		buildDBCPNameList(sourceSequencedProperties);
		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpParConfiguration = new DBCPParConfiguration(dbcpName);
			dbcpParConfiguration.toValue(sourceSequencedProperties);
			dbcpParConfiguration.checkForDependencies(sourceSequencedProperties);
			dbcpPartConfigurationHash.put(dbcpName, dbcpParConfiguration);
		}
		
		mainProjectPartConfiguration.toValue(sourceSequencedProperties);
		mainProjectPartConfiguration.checkForDependencies(sourceSequencedProperties);
		
		buildSubProjectNameList(sourceSequencedProperties);
		for (String subProjectName : subProjectList) {
			ProjectPartConfiguration subProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, subProjectName);
			subProjectPartConfiguration.toValue(sourceSequencedProperties);
			subProjectPartConfiguration.checkForDependencies(sourceSequencedProperties);
			subProjectPartConfigurationHash.put(subProjectName, subProjectPartConfiguration);
		}
	}
		
	
	private String buildDBCPNameListValue() {
		StringBuilder dbcpNameListValueBuilder = new StringBuilder();
		boolean isFirst = true;
		for (String dbcpName : dbcpNameList) {
			if (isFirst) {
				isFirst = false;
			} else {
				dbcpNameListValueBuilder.append(",");
			}
			
			dbcpNameListValueBuilder.append(dbcpName);
		}
		
		return dbcpNameListValueBuilder.toString();
	}
	
	private String buildSubProjectNameListValue() {
		StringBuilder subProjectNameListValueBuilder = new StringBuilder();
		boolean isFirst = true;
		for (String subProjectName : subProjectList) {
			if (isFirst) {
				isFirst = false;
			} else {
				subProjectNameListValueBuilder.append(",");
			}
			
			subProjectNameListValueBuilder.append(subProjectName);
		}
		
		return subProjectNameListValueBuilder.toString();
	}
	
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException {
		commonPartConfiguration.toProperties(targetSequencedProperties);
		
		targetSequencedProperties.put(DBCP_NAME_LIST_KEY_STRING, buildDBCPNameListValue());
		
		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpParConfiguration = dbcpPartConfigurationHash.get(dbcpName);
			dbcpParConfiguration.toProperties(targetSequencedProperties);
		}		
		
		targetSequencedProperties.put(SUBPROJECT_NAME_LIST_KEY_STRING, buildSubProjectNameListValue());
		
		for (String subProjectName : subProjectList) {
			ProjectPartConfiguration subProjectPartConfiguration = subProjectPartConfigurationHash.get(subProjectName);
			subProjectPartConfiguration.toProperties(targetSequencedProperties);
		}
	}
}
