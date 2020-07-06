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
import kr.pe.codda.common.config.part.CommonPartConfiguration;
import kr.pe.codda.common.config.part.DBCPParConfiguration;
import kr.pe.codda.common.config.part.MainProjectPartConfiguration;
import kr.pe.codda.common.config.part.SubProjectPartConfiguration;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.GUIItemType;
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
	
	private MainProjectPartConfiguration mainProjectPartConfiguration = new MainProjectPartConfiguration();
	
	
	private List<String> subProjectNameList = new ArrayList<String>();
	private HashMap<String, SubProjectPartConfiguration> subProjectPartConfigurationHash = new HashMap<String, SubProjectPartConfiguration>();
	
	
	/**
	 * 설정 파일의 내용을 담고 있는 시퀀스 프로퍼티에 설치 경로와 프로젝트를 적용하다.  
	 * 
	 * @param installedPathString 설치 경로 문자열
	 * @param mainProjectName 메인 프로젝트 이름
	 * @param sourceSequencedProperties 설정 파일의 내용을 담고 있는 시퀀스 프로퍼티
	 * @throws IllegalArgumentException 파라미터가 null 인 경우 던지는 예외
	 * @throws IllegalStateException 파라미터 '설정 파일의 내용을 담고 있는 시퀀스 프로퍼티'가 설정 파일 형식에 맞지 않는 경우 던지는 예외
	 */
	public static void applyIntalledPath(String installedPathString, String mainProjectName, SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, IllegalStateException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}
		
		if (null == sourceSequencedProperties) {
			throw new IllegalArgumentException("the parameter sourceSequencedProperties is null");
		}
		
		
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
							.append("change the key[")
							.append(itemKey)
							.append("]'s value[")
							.append(oldItemValue)
							.append("] to new value[")
							.append(newItemValue)
							.append("]").toString();
					
					Logger log = Logger.getLogger(CommonPartConfiguration.class.getName());
					log.info(logMessage);
				}
			}
			
		}
	}	

	/**
	 * @param title 서브 이름 집합의 이름
	 * @param subNameListKey 서브 이름 목록 키
	 * @param subNameListValue 서브 이름 목록 값
	 * @return 콤마를 구분자로 하는 서브 이름 목록 값으로 부터 추출한 서브 이름들을 집합에 넣어 반환한다.
	 * @throws PartConfigurationException 서브 이름들중 중복된 것이 있으면 던지는 예외
	 */
	private Set<String> buildSubNameSet(String title, String subNameListKey, String subNameListValue) throws PartConfigurationException {
		Set<String> nameSet = new HashSet<>();
		StringTokenizer tokens = new StringTokenizer(subNameListValue, ",");

		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String name = token.trim();
			if (nameSet.contains(name)) {
				String errorMessage = new StringBuilder()
						.append(title)
						.append("'s sub name[").append(name)	.append("] is over").toString();
				throw new PartConfigurationException(subNameListKey, errorMessage);
			}

			nameSet.add(name);
		}
		
		return nameSet;
	}
	

	private Set<String> buildDBCPSubNameSet(SequencedProperties sourceSequencedProperties) throws PartConfigurationException {
		String dbcpNameListValue = sourceSequencedProperties.getProperty(DBCP_NAME_LIST_KEY_STRING);
		
		if (null == dbcpNameListValue) {
			String errorMessage = new StringBuilder("the dbcp name list key(=")
					.append(DBCP_NAME_LIST_KEY_STRING)
					.append(") was not found in the parameter sourceSequencedProperties").toString();
			throw new PartConfigurationException(DBCP_NAME_LIST_KEY_STRING, errorMessage);
		}
		
		dbcpNameListValue = dbcpNameListValue.trim();
		
		if (dbcpNameListValue.isEmpty()) {
			String errorMessage = new StringBuilder("the dbcp name list key(=")
					.append(DBCP_NAME_LIST_KEY_STRING)
					.append(")'s value is empty").toString();
			throw new PartConfigurationException(DBCP_NAME_LIST_KEY_STRING, errorMessage);
		}
		
		
		Set<String> dbcpNameSet = buildSubNameSet("the dbcp", DBCP_NAME_LIST_KEY_STRING, dbcpNameListValue);		
		
		return dbcpNameSet;
	}
	
	
	private Set<String> buildSubProjectSubNameSet(SequencedProperties sourceSequencedProperties) throws PartConfigurationException {
		String subProjectNameListValue = sourceSequencedProperties.getProperty(SUBPROJECT_NAME_LIST_KEY_STRING);
		
		if (null == subProjectNameListValue) {
			String errorMessage = new StringBuilder("the sub project name list key(=")
					.append(SUBPROJECT_NAME_LIST_KEY_STRING)
					.append(") was not found in the parameter sourceSequencedProperties").toString();
			throw new PartConfigurationException(SUBPROJECT_NAME_LIST_KEY_STRING, errorMessage);
		}
		
		subProjectNameListValue = subProjectNameListValue.trim();
		
		if (subProjectNameListValue.isEmpty()) {
			String errorMessage = new StringBuilder("the sub project name list key(=")
					.append(SUBPROJECT_NAME_LIST_KEY_STRING)
					.append(")'s value is empty").toString();
			throw new PartConfigurationException(SUBPROJECT_NAME_LIST_KEY_STRING, errorMessage);
		}
		
		Set<String> subProjectNameSet = buildSubNameSet("the sub project", SUBPROJECT_NAME_LIST_KEY_STRING, subProjectNameListValue);
		
		
		return subProjectNameSet;
	}
	
	
	public void addDBCPParConfiguration(DBCPParConfiguration newDBCPParConfiguration) throws IllegalArgumentException {
		if (null == newDBCPParConfiguration) {
			throw new IllegalArgumentException("the parameter newDBCPParConfiguration is null");
		}
		
		String newDBCPName = newDBCPParConfiguration.getDBCPName();
		
		for (String dbcpName : dbcpNameList) {
			
			if (dbcpName.equals(newDBCPName)) {
				String errorMessage = new StringBuilder()
						.append("the parameter newDBCPParConfiguration[")
						.append(newDBCPName)
						.append("] was already registered").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
		}
		
		dbcpNameList.add(newDBCPName);
		dbcpPartConfigurationHash.put(newDBCPName, newDBCPParConfiguration);
	}
	
	public void removeDBCPParConfiguration(String dbcpName) {
		if (null == dbcpName) {
			throw new IllegalArgumentException("the parameter dbcpName is null");
		}
		
		if (dbcpPartConfigurationHash.containsKey(dbcpName)) {
			dbcpNameList.remove(dbcpName);
			dbcpPartConfigurationHash.remove(dbcpName);
		}
	}
	
	public DBCPParConfiguration getDBCPParConfiguration(String dbcpName) throws IllegalArgumentException {
		
		if (null == dbcpName) {
			throw new IllegalArgumentException("the parameter dbcpName is null");
		}
		
		return dbcpPartConfigurationHash.get(dbcpName);
		
	}
	
	
	public List<String> getDBCPNameList() {
		return dbcpNameList;
	}
	
	public CommonPartConfiguration getCommonPartConfiguration() {
		return commonPartConfiguration;
	}
	
	public MainProjectPartConfiguration  getMainProjectPartConfiguration() {
		return mainProjectPartConfiguration;
	}
	
	public void addSubProjectPartConfiguration(SubProjectPartConfiguration newSubProjectPartConfiguration) throws IllegalArgumentException {
		if (null == newSubProjectPartConfiguration) {
			throw new IllegalArgumentException("the parameter newSubProjectPartConfiguration is null");
		}
		
		
		String newSubProjectName = newSubProjectPartConfiguration.getSubProjectName();
		
		for (String subProjectName : subProjectNameList) {
			
			if (subProjectName.equals(newSubProjectName)) {
				String errorMessage = new StringBuilder()
						.append("the parameter newSubProjectPartConfiguration[")
						.append(newSubProjectName)
						.append("] was already registered").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
		}
		
		subProjectNameList.add(newSubProjectName);
		subProjectPartConfigurationHash.put(newSubProjectName, newSubProjectPartConfiguration);
	}
	
	public void removeSubProjectPartConfiguration(String subProjectName) {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}
		
		if (subProjectPartConfigurationHash.containsKey(subProjectName)) {
			subProjectNameList.remove(subProjectName);
			subProjectPartConfigurationHash.remove(subProjectName);
		}
	}

	public SubProjectPartConfiguration getSubProjectPartConfiguration(String subProjectName) throws IllegalArgumentException {
		
		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}
		
		return subProjectPartConfigurationHash.get(subProjectName);		
	}		

	
	public List<String> getSubProjectNameList() {
		return subProjectNameList;
	}
	
	public void fromProperteisWithDependencyCheck(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {

		commonPartConfiguration.fromProperties(sourceSequencedProperties);
		commonPartConfiguration.checkForDependencies();
				
		Set<String> dbcpNameSet = buildDBCPSubNameSet(sourceSequencedProperties);
		
		dbcpNameList.clear();
		dbcpPartConfigurationHash.clear();
		for (String dbcpName : dbcpNameSet) {
			DBCPParConfiguration dbcpParConfiguration = new DBCPParConfiguration(dbcpName);

			dbcpParConfiguration.fromProperties(sourceSequencedProperties);
			dbcpParConfiguration.checkForDependencies();
			
			dbcpNameList.add(dbcpName);
			dbcpPartConfigurationHash.put(dbcpName, dbcpParConfiguration);
		}
		
		mainProjectPartConfiguration.fromProperties(sourceSequencedProperties);
		mainProjectPartConfiguration.checkForDependencies();
		
		
		Set<String> subProjectNameSet = buildSubProjectSubNameSet(sourceSequencedProperties);
		
		subProjectNameList.clear();
		subProjectPartConfigurationHash.clear();
		for (String subProjectName : subProjectNameSet) {
			SubProjectPartConfiguration subProjectPartConfiguration = new SubProjectPartConfiguration(subProjectName);
			
			subProjectPartConfiguration.fromProperties(sourceSequencedProperties);
			subProjectPartConfiguration.checkForDependencies();
			
			subProjectNameList.add(subProjectName);
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
		for (String subProjectName : subProjectNameList) {
			if (isFirst) {
				isFirst = false;
			} else {
				subProjectNameListValueBuilder.append(",");
			}
			
			subProjectNameListValueBuilder.append(subProjectName);
		}
		
		return subProjectNameListValueBuilder.toString();
	}
	
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException, IllegalStateException {
		commonPartConfiguration.toProperties(targetSequencedProperties);
		
		targetSequencedProperties.put(DBCP_NAME_LIST_KEY_STRING, buildDBCPNameListValue());
		
		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpParConfiguration = dbcpPartConfigurationHash.get(dbcpName);
			if (null == dbcpParConfiguration) {
				// FIXME!, dead code but defence code
				throw new IllegalStateException("the var 'dbcpParConfiguration' is null, dbcp hash has no key for dbcp name");
			}
			
			dbcpParConfiguration.toProperties(targetSequencedProperties);
		}
		
		mainProjectPartConfiguration.toProperties(targetSequencedProperties);
		
		targetSequencedProperties.put(SUBPROJECT_NAME_LIST_KEY_STRING, buildSubProjectNameListValue());
		
		for (String subProjectName : subProjectNameList) {
			SubProjectPartConfiguration subProjectPartConfiguration = subProjectPartConfigurationHash.get(subProjectName);
			if (null == subProjectPartConfiguration) {
				// FIXME!, dead code
				throw new IllegalStateException("the var 'subProjectPartConfiguration' is null, sub project hash has no key for sub project name");
			}
			
			subProjectPartConfiguration.toProperties(targetSequencedProperties);
		}
	}
}
