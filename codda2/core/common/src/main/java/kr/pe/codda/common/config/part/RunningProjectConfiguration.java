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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ItemViewType;
import kr.pe.codda.common.type.KeyTypeOfConfieFile;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * 기동중인 프로젝트 설정. 기동중인 프로젝트 설정은 (1) DBCP 파트 목록 (2) 공통 파트 (3) 메인 프로젝트 파트 (4)
 * 서브 프로젝트 파트 목록 이렇게 4개로 구성된다.
 * 
 * @author Won Jonghoon
 *
 */
public class RunningProjectConfiguration implements ConfigurationIF {
	
	private SessionkeyPartConfiguration sessionkeyPartConfiguration = new SessionkeyPartConfiguration();
	
	public ListTypePartConfiguration<DBCPParConfiguration> DBCP = new ListTypePartConfiguration<DBCPParConfiguration>("dbcp", DBCPParConfiguration.class);

	private MainProjectPartConfiguration mainProjectPartConfiguration = new MainProjectPartConfiguration();

	public ListTypePartConfiguration<SubProjectPartConfiguration> SUBPROJECT = new ListTypePartConfiguration<SubProjectPartConfiguration>("subject", SubProjectPartConfiguration.class);

	public static String toSetValue(Set<String> valueSet) {
		StringBuilder valueStringBuilder = new StringBuilder();
		boolean isFirst = true;
		for(String value : valueSet) {
			if (isFirst) {				
				isFirst = false;
			} else {
				valueStringBuilder.append(", ");
			}
			
			valueStringBuilder.append(value);
		}
		
		return valueStringBuilder.toString();
	}
	
	public static Set<String> fromSetValue(String setValue) {
		Set<String> valueSet = new HashSet<String>();
		
		StringTokenizer tokens = new StringTokenizer(setValue, ",");	

		int inx=0;
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String element = token.trim();
			
			if (element.isEmpty()) {
				String errorMessage = new StringBuilder()
						.append("the ").append(element).append(" set[")
						.append(inx)
						.append("]'s element is empty").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(element)) {
				String errorMessage = new StringBuilder()
						.append("the set[")
						.append(inx)
						.append("]'s element has a leading or tailing white space").toString();
				throw new IllegalArgumentException(errorMessage);
			}
						
			if (valueSet.contains(element)) {
				String errorMessage = new StringBuilder()
						.append("the set[")
						.append(inx)
						.append("]'s element[").append(element)
						.append("] already was registered").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			valueSet.add(element);
		}
		
		return valueSet;
	}
	
	public static String buildKeyOfConfigFile(String prefixBeforeItemID, String itemID, KeyTypeOfConfieFile keyTypeOfConfieFile) {
		StringBuilder firstStringBuilder = new StringBuilder().append(prefixBeforeItemID)
				.append(itemID);
		
		String key = "unknown key type of config file";
		
		if (KeyTypeOfConfieFile.DESC.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".desc").toString();
		} else if (KeyTypeOfConfieFile.VALUE.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".value").toString();
		} else if (KeyTypeOfConfieFile.SET.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".set").toString();
		} else if (KeyTypeOfConfieFile.ITEM_VIEW_TYPE.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(".item_view_type").toString();
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

			if (key.endsWith(".item_view_type")) {
				String value = sourceSequencedProperties.getProperty(key);

				final ItemViewType itemViewType;

				if (null == value) {
					itemViewType = ItemViewType.DATA;
				} else {
					value = value.toUpperCase();

					itemViewType = ItemViewType.valueOf(value);
				}

				if (ItemViewType.FILE.equals(itemViewType) || ItemViewType.PATH.equals(itemViewType)) {
					String subkey = key.substring(0, key.length() - ".item_view_type".length());

					String guiProjectHomeBaseRelativePathKey = new StringBuilder().append(subkey).append(".")
							.append(itemViewType.name().toLowerCase()).toString();

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

					Logger log = Logger.getLogger(RunningProjectConfiguration.class.getName());
					log.info(logMessage);
				}
			}

		}
	}
	
	
	public SessionkeyPartConfiguration getSessionkeyPartConfiguration() {
		return sessionkeyPartConfiguration;
	}

	/**
	 * @return 메인 프로젝트 파트 설정
	 */
	public MainProjectPartConfiguration getMainProjectPartConfiguration() {
		return mainProjectPartConfiguration;
	}

	
	
	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {

		
		sessionkeyPartConfiguration.fromProperties(sourceSequencedProperties);

		DBCP.fromProperties(sourceSequencedProperties);

		mainProjectPartConfiguration.fromProperties(sourceSequencedProperties);
		
		SUBPROJECT.fromProperties(sourceSequencedProperties);
	}
	
	@Override
	public void checkForDependencies() throws PartConfigurationException {
		sessionkeyPartConfiguration.checkForDependencies();
		
		DBCP.checkForDependencies();
		
		mainProjectPartConfiguration.checkForDependencies();
		
		SUBPROJECT.checkForDependencies();
	}

	@Override
	public void toProperties(SequencedProperties targetSequencedProperties)
			throws IllegalArgumentException, IllegalStateException {
		sessionkeyPartConfiguration.toProperties(targetSequencedProperties);

		DBCP.toProperties(targetSequencedProperties);

		mainProjectPartConfiguration.toProperties(targetSequencedProperties);

		SUBPROJECT.toProperties(targetSequencedProperties);
	}
	
	public void checkVadlidation() throws IllegalStateException {
		boolean isValid;
		for (String dbcpName : DBCP.getNameList()) {
			DBCPParConfiguration dbcpParConfiguration = DBCP.getProjectPartConfiguration(dbcpName);
			isValid = dbcpParConfiguration.toString().indexOf("null") >= 0;
			
			if (isValid) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(dbcpParConfiguration.toString());
				
				throw new IllegalStateException("the dbcp["+dbcpName+"] part configuration includes null");
			}
		}
		/*
		isValid = jdfPartConfiguration.toString().indexOf("null") >= 0;
		if (isValid) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(jdfPartConfiguration.toString());
			
			throw new IllegalStateException("the common part configuration includes null");
		}
		*/
		
		isValid = mainProjectPartConfiguration.toString().indexOf("null") >= 0;
		if (isValid) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(mainProjectPartConfiguration.toString());
			
			throw new IllegalStateException("the main-project part configuration includes null");
		}
		
		for (String subProjectName : SUBPROJECT.getNameList()) {
			SubProjectPartConfiguration subProjectPartConfiguration = SUBPROJECT.getProjectPartConfiguration(subProjectName);
			isValid = subProjectPartConfiguration.toString().indexOf("null") >= 0;
			
			if (isValid) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(subProjectPartConfiguration.toString());
				
				throw new IllegalStateException("the sub-project["+subProjectName+"] part configuration includes null");
			}
		}
	}
	
	
}
