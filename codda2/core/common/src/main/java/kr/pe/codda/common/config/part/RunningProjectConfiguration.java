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
import kr.pe.codda.common.type.KeyTypeOfConfieProperties;
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
	
	public final static String DESC_KEY_SUFFIX = ".desc";
	public final static String VALUE_KEY_SUFFIX = ".value";
	public final static String ITEM_VIEW_TYPE_KEY_SUFFIX = ".item_view_type";
	public final static String SET_KEY_SUFFIX = ".set";
	public final static String FILE_KEY_SUFFIX = ".file";
	public final static String PATH_KEY_SUFFIX = ".path";
	// public final static String LIST_KEY_SUFFIX = ".list";
	public final static String SUBKEY_OF_SUB_PART_NAME_LIST_ITEMKEY = "sub_part_name_list";
	
	
	private SessionkeyPartConfiguration sessionkeyPartConfiguration = new SessionkeyPartConfiguration();
	
	public ListTypePartConfiguration<DBCPParConfiguration> DBCP = new ListTypePartConfiguration<DBCPParConfiguration>("dbcp", DBCPParConfiguration.class);

	private MainProjectPartConfiguration mainProjectPartConfiguration = new MainProjectPartConfiguration();

	public ListTypePartConfiguration<SubProjectPartConfiguration> SUBPROJECT = new ListTypePartConfiguration<SubProjectPartConfiguration>("subproject", SubProjectPartConfiguration.class);
	
	
	/**
	 * 파라미터 '이름 집합' 의 원소를 토큰으로 그리고 콤마를 구별자로 하여 구성한 문자열로 반환한다.  
	 * @param nameSet 이름 집합
	 * @return  파라미터 '이름 집합' 의 원소를 토큰으로 그리고 콤마를 구별자로 하여 구성한 문자열
	 */
	public static String toSetTypeValue(Set<String> nameSet) {
		StringBuilder valueStringBuilder = new StringBuilder();
		boolean isFirst = true;
		for(String name : nameSet) {
			if (isFirst) {				
				isFirst = false;
			} else {
				valueStringBuilder.append(", ");
			}
			
			valueStringBuilder.append(name);
		}
		
		return valueStringBuilder.toString();
	}
	
	/**
	 * 콤마를 구별자로 하는 파라미터 '집합 유형 값' 을 입력 받아 파싱한 토큰들을 원소들로 구성한 '이름 집합'을 반환한다.
	 * @param setTypeValue 콤마를 구별자로 하는 '집합 유형 값'
	 * @return 콤마를 구별자로 하는 파라미터 '집합 유형 값' 을 입력 받아 파싱한 토큰들을 원소들로 구성한 '이름 집합'
	 */	
	public static Set<String> fromSetTypeValue(String setTypeValue) {
		Set<String> valueSet = new HashSet<String>();
		
		StringTokenizer tokens = new StringTokenizer(setTypeValue, ",");	

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
	
	
	/**
	 * 파라미터 '키를 이루는 항목 식별자 이전까지의 접두어'와 '항목 식별자' 그리고 '설정 파일의 키 유형' 에 해당하는 키를 반환한다. 
	 *     
	 * @param prefixBeforeItemID 키를 이루는 항목 식별자 이전까지의 접두어
	 * @param itemID 항목 식별자
	 * @param keyTypeOfConfieFile 설정 파일의 키 유형, (1) 설병(desc), (2) 값(value), (3) 항목 뷰 유형(item_view_type), (4) 집합(set), (5) 파일(file), (6) 경로(path), 
	 * @return 파라미터 '키를 이루는 항목 식별자 이전까지의 접두어'와 '항목 식별자' 그리고 '설정 파일의 키 유형' 에 해당하는 키
	 * @throws IllegalArgumentException 각 파라미터 값이 널인 경우 혹은 '설정 파일의 키 유형' 이 알 수 없는 값인 경우 던지는 예외
	 */
	public static String buildKeyOfConfigFile(String prefixBeforeItemID, String itemID, KeyTypeOfConfieProperties keyTypeOfConfieFile) throws  IllegalArgumentException {
		if (null == prefixBeforeItemID) {
			throw new IllegalArgumentException("the parameter prefixBeforeItemID is null");
		}
		
		if (null == itemID) {
			throw new IllegalArgumentException("the parameter itemID is null");
		}
		
		if (null == keyTypeOfConfieFile) {
			throw new IllegalArgumentException("the parameter keyTypeOfConfieFile is null");
		}
		
		StringBuilder firstStringBuilder = new StringBuilder().append(prefixBeforeItemID)
				.append(itemID);
		
		final String key;
				
		if (KeyTypeOfConfieProperties.DESC.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(DESC_KEY_SUFFIX).toString();
		} else if (KeyTypeOfConfieProperties.VALUE.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(VALUE_KEY_SUFFIX).toString();		
		} else if (KeyTypeOfConfieProperties.ITEM_VIEW_TYPE.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(ITEM_VIEW_TYPE_KEY_SUFFIX).toString();
		} else if (KeyTypeOfConfieProperties.SET.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(SET_KEY_SUFFIX).toString();
		} else if (KeyTypeOfConfieProperties.FILE.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(FILE_KEY_SUFFIX).toString();
		} else if (KeyTypeOfConfieProperties.PATH.equals(keyTypeOfConfieFile)) {
			key = firstStringBuilder.append(PATH_KEY_SUFFIX).toString();			
		} else {
			throw new IllegalArgumentException("unknown key type of config file");
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

			if (key.endsWith(ITEM_VIEW_TYPE_KEY_SUFFIX)) {
				String value = sourceSequencedProperties.getProperty(key);

				final ItemViewType itemViewType;

				if (null == value) {
					itemViewType = ItemViewType.DATA;
				} else {
					value = value.toUpperCase();

					itemViewType = ItemViewType.valueOf(value);
				}

				if (ItemViewType.FILE.equals(itemViewType) || ItemViewType.PATH.equals(itemViewType)) {
					String subkey = key.substring(0, key.length() - ITEM_VIEW_TYPE_KEY_SUFFIX.length());

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
	
	
	/**
	 * @return 세션키 파트 설정
	 */
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
	
	/**
	 *  
	 * 모든 파트 설정의 값들중 null 이 존재하면 상태 이상 예외를 던진다.
	 * 
	 * @throws IllegalStateException 모든 파트 설정의 값들중 null 이 존재하면 던지는 예외
	 */
	public void checkNull() throws IllegalStateException {
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
	
	/**
	 * <pre>
	 * 주어진 파트 이름에 해당하는 신규 파트 설정을 반환한다, 단 주어진 파트 이름에 해당하는 파트가 없다면 null 을 반환한다.
	 * WARNING! 상속 받을 경우 반듯이 추가된 파트에 대해서 처리를 해주어야 한다.
	 * </pre>
	 * 
	 * @param partName 반환을 원하는 파트 이름 
	 * @return 주어진 파트 이름에 해당하는 신규 파트 설정, 단 주어진 파트 이름에 해당하는 파트가 없다면 null 을 반환한다.
	 */
	public PartConfigurationIF createNewPartConfiguration(String partName) {		
		if (null == partName) {
			throw new IllegalArgumentException("the parameter partName is null");
		}
		
		if (SessionkeyPartConfiguration.PART_NAME.equals(partName)) {
			return new SessionkeyPartConfiguration();
		}
		
		if (MainProjectPartConfiguration.PART_NAME.equals(partName)) {
			return new MainProjectPartConfiguration();
		}
		
		return null;
	}
	
	/**
	 * <pre>
	 * 주어진 목록형 파트 이름과 서브 파트이름에 해당하는 신규 파트 설정을 반환한다, 단 주어진 목록형 파트 이름 혹은 서브 파트이름이 없다면 null 을 반환한다.
	 * WARNING! 상속 받을 경우 반듯이 추가된 목록형 파트에 대해서 처리를 해주어야 한다.
	 * </pre>
	 * 
	 * @param partName 목록형 파트 이름
	 * @param subPartName 서브 파트이름
	 * @return 주어진 목록형 파트 이름과 서브 파트이름에 해당하는 신규 파트 설정, 단 주어진 목록형 파트 이름 혹은 서브 파트이름이 없다면 null 을 반환한다.
	 */
	public PartConfigurationIF createNewPartConfiguration(String partName, String subPartName) {
		if (null == partName) {
			throw new IllegalArgumentException("the parameter partName is null");
		}
		
		if (null == subPartName) {
			throw new IllegalArgumentException("the parameter subPartName is null");
		}
		
		if (DBCPParConfiguration.PART_NAME.equals(partName)) {
			return new DBCPParConfiguration(subPartName);
		}
		
		if (SubProjectPartConfiguration.PART_NAME.equals(partName)) {
			return new SubProjectPartConfiguration(subPartName);
		}
		
		return null;
	}
}
