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
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.GUIItemType;
import kr.pe.codda.common.type.KeyTypeOfConfieFile;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * 기동중인 프로젝트 설정. 기동중인 프로젝트 설정은 (1) DBCP 파트 목록 (2) 공통 파트 (3) 메인 프로젝트 파트 (4)
 * 서브 프로젝트 파트 목록 이렇게 4개로 구성된다.
 * 
 * @author Won Jonghoon
 *
 */
public class RunningProjectConfiguration implements PartConfigurationIF {
	private List<String> dbcpNameList = new ArrayList<String>();
	private HashMap<String, DBCPParConfiguration> dbcpPartConfigurationHash = new HashMap<String, DBCPParConfiguration>();

	private CommonPartConfiguration commonPartConfiguration = new CommonPartConfiguration();

	private MainProjectPartConfiguration mainProjectPartConfiguration = new MainProjectPartConfiguration();

	private List<String> subProjectNameList = new ArrayList<String>();
	private HashMap<String, SubProjectPartConfiguration> subProjectPartConfigurationHash = new HashMap<String, SubProjectPartConfiguration>();

	
	public static String buildKeyOfConfigFile(String prefixBeforeItemID, String itemID, KeyTypeOfConfieFile keyTypeOfConfieFile) {
		StringBuilder firstStringBuilder = new StringBuilder().append(prefixBeforeItemID)
				.append(itemID);
		
		String key = "unknown key type of config file";
		
		if (KeyTypeOfConfieFile.DESC.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".desc").toString();
		} else if (KeyTypeOfConfieFile.VALUE.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".value").toString();
		} else if (KeyTypeOfConfieFile.GUI_ITEM_TYPE.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".gui_item_type").toString();
		} else if (KeyTypeOfConfieFile.FILE.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".file").toString();
		} else if (KeyTypeOfConfieFile.PATH.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".path").toString();
		}
		
		return key;
		
	}
	

	/**
	 * 설정 파일의 내용을 담고 있는 시퀀스 프로퍼티에 설치 경로와 프로젝트를 적용하다.
	 * 
	 * @param installedPathString       설치 경로 문자열
	 * @param mainProjectName           메인 프로젝트 이름
	 * @param sourceSequencedProperties 설정 파일의 내용을 담고 있는 시퀀스 프로퍼티
	 * @throws IllegalArgumentException 파라미터가 null 인 경우 던지는 예외
	 * @throws IllegalStateException    파라미터 '설정 파일의 내용을 담고 있는 시퀀스 프로퍼티'가 설정 파일 형식에
	 *                                  맞지 않는 경우 던지는 예외
	 */
	public static void applyIntalledPath(String installedPathString, String mainProjectName,
			SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, IllegalStateException {
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

		while (keys.hasMoreElements()) {
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

				if (!GUIItemType.DATA.equals(guiItemType)) {
					String subkey = key.substring(0, key.length() - ".gui_item_type".length());

					String guiProjectHomeBaseRelativePathKey = new StringBuilder().append(subkey).append(".")
							.append(guiItemType.name().toLowerCase()).toString();

					String guiProjectHomeBaseRelativePathValue = sourceSequencedProperties
							.getProperty(guiProjectHomeBaseRelativePathKey);

					if (null == guiProjectHomeBaseRelativePathValue) {
						String errorMessage = new StringBuilder().append("the value of required item[")
								.append(guiProjectHomeBaseRelativePathKey).append("] is null").toString();
						throw new IllegalStateException(errorMessage);
					}

					String itemKey = new StringBuilder().append(subkey).append(".value").toString();

					String oldItemValue = sourceSequencedProperties.getProperty(itemKey);

					if (null == oldItemValue) {
						String errorMessage = new StringBuilder().append("the value of required item[").append(itemKey)
								.append("] is null").toString();
						throw new IllegalStateException(errorMessage);
					}

					String projectHomePathString = ProjectBuildSytemPathSupporter
							.getProjectPathString(installedPathString, mainProjectName);

					final String newItemValue;
					if (projectHomePathString.endsWith(File.separator)) {
						newItemValue = new StringBuilder().append(projectHomePathString)
								.append(guiProjectHomeBaseRelativePathValue.replace("/", File.separator)).toString();
					} else {
						newItemValue = new StringBuilder().append(projectHomePathString).append(File.separatorChar)
								.append(guiProjectHomeBaseRelativePathValue.replace("/", File.separator)).toString();
					}

					sourceSequencedProperties.put(itemKey, newItemValue);

					String logMessage = new StringBuilder().append("change the key[").append(itemKey)
							.append("]'s value[").append(oldItemValue).append("] to new value[").append(newItemValue)
							.append("]").toString();

					Logger log = Logger.getLogger(CommonPartConfiguration.class.getName());
					log.info(logMessage);
				}
			}

		}
	}
	
	/**
	 * 
	 * @param partName 파트 마다 유일한 값을 갖는 첫번째 접두어
	 * @return 지정한 '첫번째 접두어' 를 갖는 이름 목록 키
	 */
	public String buildKeyForNameList(String firstPrefix) {
		return new StringBuilder().append(firstPrefix)
				.append(".name_list.value").toString();
	}

	/**
	 * @param title            서브 이름 집합의 이름
	 * @param subNameListKey   서브 이름 목록 키
	 * @param subNameListValue 서브 이름 목록 값
	 * @return 콤마를 구분자로 하는 서브 이름 목록 값으로 부터 추출한 서브 이름들을 집합에 넣어 반환한다.
	 * @throws PartConfigurationException 서브 이름들중 중복된 것이 있으면 던지는 예외
	 */
	private Set<String> buildSubNameSet(String title, String subNameListKey, String subNameListValue)
			throws PartConfigurationException {
		Set<String> nameSet = new HashSet<>();
		StringTokenizer tokens = new StringTokenizer(subNameListValue, ",");

		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String name = token.trim();
			if (nameSet.contains(name)) {
				String errorMessage = new StringBuilder().append(title).append("'s sub name[").append(name)
						.append("] is over").toString();
				throw new PartConfigurationException(subNameListKey, errorMessage);
			}

			nameSet.add(name);
		}

		return nameSet;
	}

	/**
	 * 주어진 시퀀스 프로퍼터리로 부터 DBCP 이름 목록 키의 값을 얻어와 콤마를 구분자로 추출한 DBCP 이름들이 담긴 집합을 반환한다.
	 * 
	 * @param sourceSequencedProperties DBCP 이름 목록 키의 값을 가진 시퀀스 프로퍼티
	 * @return 주어진 시퀀스 프로퍼터리로 부터 DBCP 이름 목록 키의 값을 얻어와 콤마를 구분자로 추출한 DBCP 이름들이 담긴 집합
	 * @throws PartConfigurationException 주어진 시퀀스 프로퍼티에 DBCP 이름 목록 키 값이 없을때 혹은 DBCP
	 *                                    이름이 중복될때 던지는 예외
	 */
	private Set<String> buildDBCPSubNameSet(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		
		final String dbcpNameListKey = buildKeyForNameList(DBCPParConfiguration.FIRST_PREFIX);
		
		String dbcpNameListValue = sourceSequencedProperties.getProperty(dbcpNameListKey);

		if (null == dbcpNameListValue) {
			String errorMessage = new StringBuilder("the dbcp name list key(=").append(dbcpNameListKey)
					.append(") was not found in the parameter sourceSequencedProperties").toString();
			throw new PartConfigurationException(dbcpNameListKey, errorMessage);
		}

		dbcpNameListValue = dbcpNameListValue.trim();

		if (dbcpNameListValue.isEmpty()) {
			return new HashSet<String>();
		}

		Set<String> dbcpNameSet = buildSubNameSet("the dbcp", dbcpNameListKey, dbcpNameListValue);

		return dbcpNameSet;
	}

	/**
	 * 주어진 시퀀스 프로퍼터리로 부터 서브 프로젝트 이름 목록 키의 값을 얻어와 콤마를 구분자로 추출한 서브 프로젝트 이름들이 담긴 집합을
	 * 반환한다.
	 * 
	 * @param sourceSequencedProperties 서브 프로젝트 이름 목록 키 값이 있는 시퀀스 프로퍼티
	 * @return 주어진 시퀀스 프로퍼터리로 부터 서브 프로젝트 이름 목록 키의 값을 얻어와 콤마를 구분자로 추출한 서브 프로젝트 이름들이
	 *         담긴 집합
	 * @throws PartConfigurationException 주어진 시퀀스 프로퍼티에 서브 프로제트 이름 목록 키 값이 없을때 혹은 서브
	 *                                    프로제트 이름이 중복될때 던지는 예외
	 */
	private Set<String> buildSubProjectSubNameSet(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		final String subProjectNameListKey = buildKeyForNameList(SubProjectPartConfiguration.FIRST_PREFIX);
		
		String subProjectNameListValue = sourceSequencedProperties.getProperty(subProjectNameListKey);

		if (null == subProjectNameListValue) {
			String errorMessage = new StringBuilder("the sub project name list key(=")
					.append(subProjectNameListKey)
					.append(") was not found in the parameter sourceSequencedProperties").toString();
			throw new PartConfigurationException(subProjectNameListKey, errorMessage);
		}

		subProjectNameListValue = subProjectNameListValue.trim();

		if (subProjectNameListValue.isEmpty()) {
			return new HashSet<String>();
		}

		Set<String> subProjectNameSet = buildSubNameSet("the sub project", subProjectNameListKey,
				subProjectNameListValue);

		return subProjectNameSet;
	}

	/**
	 * 신규 DBCP 파트의 설정을 추가한다.
	 * 
	 * @param newDBCPParConfiguration 신규 DBCP 파트의 설정
	 * @throws IllegalArgumentException 파라미터 '신규 DBCP 파트의 설정' 가 널인 경우 혹은 기 등록된 DBCP
	 *                                  파트의 설정이 있는 경우 던지는 예외
	 */
	public void addDBCPParConfiguration(DBCPParConfiguration newDBCPParConfiguration) throws IllegalArgumentException {
		if (null == newDBCPParConfiguration) {
			throw new IllegalArgumentException("the parameter newDBCPParConfiguration is null");
		}

		String newDBCPName = newDBCPParConfiguration.getDBCPName();

		for (String dbcpName : dbcpNameList) {

			if (dbcpName.equals(newDBCPName)) {
				String errorMessage = new StringBuilder().append("the parameter newDBCPParConfiguration[")
						.append(newDBCPName).append("] was already registered").toString();
				throw new IllegalArgumentException(errorMessage);
			}

		}

		dbcpNameList.add(newDBCPName);
		dbcpPartConfigurationHash.put(newDBCPName, newDBCPParConfiguration);
	}

	/**
	 * 지정한 DBCP 이름을 갖는 DBCP 파트 설정을 삭제한다.
	 * 
	 * @param 삭제를 원하는 DBCP 파트 설정의 이름
	 * @throws IllegalArgumentException 파라니터 '삭제를 원하는 DBCP 파트 설정의 이름' 이 널인 경우
	 * @return 지정한 DBCP 이름을 갖는 DBCP 파트 설정 삭제 처리 성공 여부
	 */
	public boolean removeDBCPParConfiguration(String dbcpName) throws IllegalArgumentException {
		if (null == dbcpName) {
			throw new IllegalArgumentException("the parameter dbcpName is null");
		}

		if (!dbcpPartConfigurationHash.containsKey(dbcpName)) {
			return false;
		}

		dbcpNameList.remove(dbcpName);
		dbcpPartConfigurationHash.remove(dbcpName);
		
		return true;
	}

	/**
	 * 지정한 DBCP 이름을 갖는 DBCP 파트 설정을 반환한다. 단 만약 지정한 DBCP 이름을 갖는 DBCP 파트 설정이 없는 경우 널 반환한다.
	 * 
	 * @param DBCP 파트 설정 이름
	 * @return 지정한 DBCP 이름을 갖는 DBCP 파트 설정, 만약 지정한 DBCP 이름을 갖는 DBCP 파트 설정이 없는 경우 널 반환
	 * @throws IllegalArgumentException 파라니터 'DBCP 파트 설정 이름' 이 널인 경우
	 */
	public DBCPParConfiguration getDBCPParConfiguration(String dbcpName) throws IllegalArgumentException {

		if (null == dbcpName) {
			throw new IllegalArgumentException("the parameter dbcpName is null");
		}

		return dbcpPartConfigurationHash.get(dbcpName);

	}

	/**
	 * @return DBCP 이름 목록
	 */
	public List<String> getDBCPNameList() {
		return dbcpNameList;
	}

	/**
	 * @return 공통 파트 설정
	 */
	public CommonPartConfiguration getCommonPartConfiguration() {
		return commonPartConfiguration;
	}

	/**
	 * @return 메인 프로젝트 파트 설정
	 */
	public MainProjectPartConfiguration getMainProjectPartConfiguration() {
		return mainProjectPartConfiguration;
	}

	/**
	 * 신규 서브 프로젝트 파트 설정 추가
	 * @param newSubProjectPartConfiguration 신규 서브 프로젝트 파트 설정
	 * @throws IllegalArgumentException 파라미터 '신규 서브 프로젝트 파트 설정' 가 널인경우 혹은 같은 이름을 갖는 서브 프로젝트 파트 설정이 존재할 경우
	 */
	public void addSubProjectPartConfiguration(SubProjectPartConfiguration newSubProjectPartConfiguration)
			throws IllegalArgumentException {
		if (null == newSubProjectPartConfiguration) {
			throw new IllegalArgumentException("the parameter newSubProjectPartConfiguration is null");
		}

		String newSubProjectName = newSubProjectPartConfiguration.getSubProjectName();

		for (String subProjectName : subProjectNameList) {

			if (subProjectName.equals(newSubProjectName)) {
				String errorMessage = new StringBuilder().append("the parameter newSubProjectPartConfiguration[")
						.append(newSubProjectName).append("] was already registered").toString();
				throw new IllegalArgumentException(errorMessage);
			}

		}

		subProjectNameList.add(newSubProjectName);
		subProjectPartConfigurationHash.put(newSubProjectName, newSubProjectPartConfiguration);
	}

	/**
	 * 지정한 이름을 갖는 서브 프로젝트 파트 설정을 삭제한다.
	 * @param subProjectName 삭제를 원하는 서브 프로젝트 파트 설정의 이름
	 * @return 지정한 이름을 갖는 서브 프로젝트 파트 설정을 삭제 처리 성공 여부
	 * @throws IllegalArgumentException 파라미터 '삭제를 원하는 서브 프로젝트 파트 설정의 이름' 이 널인 경우
	 */
	public boolean removeSubProjectPartConfiguration(String subProjectName)
			throws IllegalArgumentException {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}

		if (! subProjectPartConfigurationHash.containsKey(subProjectName)) {
			return false;
		}
		
		subProjectNameList.remove(subProjectName);
		subProjectPartConfigurationHash.remove(subProjectName);
		
		return true;

	}

	/**
	 * 지정한 이름을 갖는 서브 프로젝트 파트 설정을 반환한다. 단 지정한 이름을 갖는 서브 프로젝트 파트 설정이 없는 경우 널 반환한다.
	 * 
	 * @param subProjectName 서브 프로젝트 파트 설정의 이름
	 * @return 서브 프로젝트 파트 설정, 만약 지정한 이름을 갖는 서브 프로젝트 파트 설정이 없는 경우 널 반환
	 * @throws IllegalArgumentException 파라미터 '서브 프로젝트 파트 설정의 이름' 이 널인 경우
	 */
	public SubProjectPartConfiguration getSubProjectPartConfiguration(String subProjectName)
			throws IllegalArgumentException {

		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}

		return subProjectPartConfigurationHash.get(subProjectName);
	}

	/**
	 * @return 서브 프로젝트 파트 설정 목록
	 */
	public List<String> getSubProjectNameList() {
		return subProjectNameList;
	}

	
	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {

		commonPartConfiguration.fromProperties(sourceSequencedProperties);		

		Set<String> dbcpNameSet = buildDBCPSubNameSet(sourceSequencedProperties);

		dbcpNameList.clear();
		dbcpPartConfigurationHash.clear();
		for (String dbcpName : dbcpNameSet) {
			DBCPParConfiguration dbcpParConfiguration = new DBCPParConfiguration(dbcpName);

			dbcpParConfiguration.fromProperties(sourceSequencedProperties);
			
			dbcpNameList.add(dbcpName);
			dbcpPartConfigurationHash.put(dbcpName, dbcpParConfiguration);
		}

		mainProjectPartConfiguration.fromProperties(sourceSequencedProperties);
		

		Set<String> subProjectNameSet = buildSubProjectSubNameSet(sourceSequencedProperties);

		subProjectNameList.clear();
		subProjectPartConfigurationHash.clear();
		for (String subProjectName : subProjectNameSet) {
			SubProjectPartConfiguration subProjectPartConfiguration = new SubProjectPartConfiguration(subProjectName);

			subProjectPartConfiguration.fromProperties(sourceSequencedProperties);

			subProjectNameList.add(subProjectName);
			subProjectPartConfigurationHash.put(subProjectName, subProjectPartConfiguration);

		}
	}
	
	@Override
	public void checkForDependencies() throws PartConfigurationException {
		commonPartConfiguration.checkForDependencies();
		
		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpParConfiguration = dbcpPartConfigurationHash.get(dbcpName);
			dbcpParConfiguration.checkForDependencies();
		}
		
		mainProjectPartConfiguration.checkForDependencies();
		
		for (String subProjectName : subProjectNameList) {
			SubProjectPartConfiguration subProjectPartConfiguration = subProjectPartConfigurationHash.get(subProjectName);
			subProjectPartConfiguration.checkForDependencies();
		}
	}

	/**
	 * @return DBCP 이름 목록을 바탕으로 만든 DBCP 이름 목록 값
	 */
	private String convertDBCPNameListToDBCPNameListValue() {
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

	/**
	 * @return 서브 프로젝트 이름 목록을 바탕으로 만든 서브 프로젝트 이름 목록 값
	 */
	private String convertSubProjectNameListToSubProjectNameListValue() {
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

	@Override
	public void toProperties(SequencedProperties targetSequencedProperties)
			throws IllegalArgumentException, IllegalStateException {
		final String dbcpNameListKey = buildKeyForNameList(DBCPParConfiguration.FIRST_PREFIX);
		final String subProjectNameListKey = buildKeyForNameList(SubProjectPartConfiguration.FIRST_PREFIX);
		
		commonPartConfiguration.toProperties(targetSequencedProperties);

		targetSequencedProperties.put(dbcpNameListKey, convertDBCPNameListToDBCPNameListValue());

		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpParConfiguration = dbcpPartConfigurationHash.get(dbcpName);
			if (null == dbcpParConfiguration) {
				// FIXME!, dead code but defence code
				throw new IllegalStateException(
						"the var 'dbcpParConfiguration' is null, dbcp hash has no key for dbcp name");
			}

			dbcpParConfiguration.toProperties(targetSequencedProperties);
		}

		mainProjectPartConfiguration.toProperties(targetSequencedProperties);

		targetSequencedProperties.put(subProjectNameListKey, convertSubProjectNameListToSubProjectNameListValue());

		for (String subProjectName : subProjectNameList) {
			SubProjectPartConfiguration subProjectPartConfiguration = subProjectPartConfigurationHash
					.get(subProjectName);
			if (null == subProjectPartConfiguration) {
				// FIXME!, dead code
				throw new IllegalStateException(
						"the var 'subProjectPartConfiguration' is null, sub project hash has no key for sub project name");
			}

			subProjectPartConfiguration.toProperties(targetSequencedProperties);
		}
	}
	
	public void checkVadlidation() throws IllegalStateException {
		boolean isValid;
		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpParConfiguration = dbcpPartConfigurationHash.get(dbcpName);
			isValid = dbcpParConfiguration.toString().indexOf("null") >= 0;
			
			if (isValid) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(dbcpParConfiguration.toString());
				
				throw new IllegalStateException("the dbcp["+dbcpName+"] part configuration includes null");
			}
		}
		
		isValid = commonPartConfiguration.toString().indexOf("null") >= 0;
		if (isValid) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(commonPartConfiguration.toString());
			
			throw new IllegalStateException("the common part configuration includes null");
		}
		
		isValid = mainProjectPartConfiguration.toString().indexOf("null") >= 0;
		if (isValid) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(mainProjectPartConfiguration.toString());
			
			throw new IllegalStateException("the main-project part configuration includes null");
		}
		
		for (String subProjectName : subProjectNameList) {
			SubProjectPartConfiguration subProjectPartConfiguration = subProjectPartConfigurationHash
					.get(subProjectName);
			isValid = subProjectPartConfiguration.toString().indexOf("null") >= 0;
			
			if (isValid) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(subProjectPartConfiguration.toString());
				
				throw new IllegalStateException("the sub-project["+subProjectName+"] part configuration includes null");
			}
		}
	}
}
