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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.part.RunningProjectConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ItemViewType;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.common.util.SequencedPropertiesUtil;

/**
 * @author Won Jonghoon
 *
 */
public class RunningConfigurationModel {
	private final String installedPathString;
	private final String runningProjectName;
	
	private final String configFilePathString;
	private final SequencedProperties loadedConfigFileSequencedProperties;
	
	
	private ArrayList<PartModelIF> partModelList = new ArrayList<PartModelIF>();

	
	public RunningConfigurationModel(String installedPathString, String runningProjectName)
			throws PartConfigurationException, FileNotFoundException, IOException {
		
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}
		
		if (null == runningProjectName) {
			throw new IllegalArgumentException("the parameter runningProjectName is null");
		}
		
		String configFilePathString = ProjectBuildSytemPathSupporter.getProejctConfigFilePathString(installedPathString, runningProjectName);

		SequencedProperties configFileSequencedProperties = SequencedPropertiesUtil
				.loadSequencedPropertiesFile(configFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);

		this.installedPathString = installedPathString;
		this.runningProjectName = runningProjectName;
		this.configFilePathString = configFilePathString;
		this.loadedConfigFileSequencedProperties = configFileSequencedProperties;
		
		buildPartModelList(configFileSequencedProperties);
		
		checkIfAllRequiredConfigItemExist();
	}
	
	/**
	 * 설정 프로퍼티 파일에서 값을 갖는 필수 항목이 없다면 예외를 던진다. "<파트이름>.<식별자>.value" 혹은 "<파트이름>.<서브파트이름>.<식별자>.value" 키가 없는 경우
	 * 
	 * @throws PartConfigurationException 설정 프로퍼티 파일에서 값을 갖는 필수 항목이 없다면 예외
	 */
	public void checkIfAllRequiredConfigItemExist() throws PartConfigurationException {
		for (PartModelIF partModel : partModelList) {
			
			if (partModel instanceof MainPartModel) {
				MainPartModel mainPartModel = (MainPartModel) partModel;
				
				for (ConfigItemInfo configItemInfo : mainPartModel.getConfigItemInfoList()) {
					if (null == configItemInfo.getValue()) {
						String itemKey = new StringBuilder().append(configItemInfo.getID())
								.append(RunningProjectConfiguration.VALUE_KEY_SUFFIX).toString();
						
						String errorMessage = new StringBuilder().append("the config file[").append(configFilePathString)
								.append("]'s key[").append(itemKey).append("] does exist")
								.toString();

						throw new PartConfigurationException(itemKey, errorMessage);
					}
				}
				
				
			} else {
				ListPartModel listPartModel = (ListPartModel) partModel;
				
				for (SubPartModel subPartModel : listPartModel.getSubPartModelList()) {
					for (ConfigItemInfo configItemInfo : subPartModel.getConfigItemInfoList()) {
						if (null == configItemInfo.getValue()) {
							String itemKey = new StringBuilder().append(configItemInfo.getID())
									.append(RunningProjectConfiguration.VALUE_KEY_SUFFIX).toString();
							
							String errorMessage = new StringBuilder().append("the config file[").append(configFilePathString)
									.append("]'s key[").append(itemKey).append("] does exist")
									.toString();

							throw new PartConfigurationException(itemKey, errorMessage);
						}
					}
				}
				
			}
			
		}
	}
	
	
	public void buildPartModelList(SequencedProperties configFileSequencedProperties) throws PartConfigurationException {
		@SuppressWarnings("unchecked")
		java.util.Enumeration<String> keys = configFileSequencedProperties.keys();

		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = configFileSequencedProperties.getProperty(key);

			if (key.endsWith(RunningProjectConfiguration.DESC_KEY_SUFFIX)) {

				String id = key.substring(0, key.length() - RunningProjectConfiguration.DESC_KEY_SUFFIX.length());

				ConfigItemInfo configItemInfo = getRegisterdConfigItemInfoIfNecessaryThenBuildPartModel(key, id);
				configItemInfo.setDesc(value);

			} else if (key.endsWith(RunningProjectConfiguration.VALUE_KEY_SUFFIX)) {
				String id = key.substring(0, key.length() - RunningProjectConfiguration.VALUE_KEY_SUFFIX.length());

				ConfigItemInfo configItemInfo = getRegisterdConfigItemInfoIfNecessaryThenBuildPartModel(key, id);

				configItemInfo.setValue(value);

			} else if (key.endsWith(RunningProjectConfiguration.ITEM_VIEW_TYPE_KEY_SUFFIX)) {
				String id = key.substring(0,
						key.length() - RunningProjectConfiguration.ITEM_VIEW_TYPE_KEY_SUFFIX.length());

				ConfigItemInfo configItemInfo = getRegisterdConfigItemInfoIfNecessaryThenBuildPartModel(key, id);

				final ItemViewType itemViewType;
				try {
					itemViewType = ItemViewType.valueOf(value.toUpperCase());
				} catch (Exception e) {
					String errorMessage = new StringBuilder().append("the config file[").append(configFilePathString)
							.append("]'s key[").append(key).append("] is bad becase its value[").append(value)
							.append("] is not a ItemViewType").toString();

					throw new PartConfigurationException(key, errorMessage);
				}

				configItemInfo.setItemViewType(itemViewType);
			}
		}
	}

	
	public boolean isMainPart(String subPartNameListValue) {
		return (null == subPartNameListValue);
	}
	
	public ConfigItemInfo getRegisterdConfigItemInfoIfNecessaryThenBuildPartModel(String key, String id) throws PartConfigurationException {
		StringTokenizer st = new StringTokenizer(id, ".");

		if (st.countTokens() < 2) {
			String errorMessage = new StringBuilder().append("the config file[").append(configFilePathString)
					.append("]'s key[").append(key).append("] is bad becase it has no more part name").toString();

			throw new PartConfigurationException(key, errorMessage);
		}

		String partName = st.nextToken();	
		
		String subPartNameListKey = new StringBuilder().append(partName)
				.append(RunningProjectConfiguration.SUB_PART_NAME_LIST_KEY_SUFFIX).toString();

		String subPartNameListValue = loadedConfigFileSequencedProperties.getProperty(subPartNameListKey);

		if (isMainPart(subPartNameListValue)) {			

			PartModelIF partModel = getPartModel(partName);
			
			final MainPartModel mainPartModel;

			if (null == partModel) {
				mainPartModel = new MainPartModel(partName);

				partModelList.add(mainPartModel);
			} else {
				mainPartModel = (MainPartModel) partModel;
			}			

			ConfigItemInfo oldConfigItemInfo = mainPartModel.getConfigItemInfo(id);

			if (null == oldConfigItemInfo) {
				ConfigItemInfo newConfigItemInfo = new ConfigItemInfo(id);
				mainPartModel.addConfigItemInfo(newConfigItemInfo);

				return newConfigItemInfo;
			}

			return oldConfigItemInfo;
		}			

		if (st.countTokens() < 2) {
			String errorMessage = new StringBuilder().append("the config file[").append(configFilePathString)
					.append("]'s key[").append(key).append("] is bad becase it has no more sub part name")
					.toString();

			throw new PartConfigurationException(key, errorMessage);
		}

		String subPartName = st.nextToken();		
		

		PartModelIF partModel = getPartModel(partName);
		
		final ListPartModel listPartModel;

		if (null == partModel) {
			listPartModel = new ListPartModel(partName);
			
			StringTokenizer subPartNameStringTokenizer = new StringTokenizer(subPartNameListValue, ",");
			while (subPartNameStringTokenizer.hasMoreTokens()) {
				String token = subPartNameStringTokenizer.nextToken().trim();
				SubPartModel newSubPartModel = new SubPartModel(partName, token);

				listPartModel.addSubPartModel(newSubPartModel);
			}
			
			partModelList.add(listPartModel);
			
		} else {
			listPartModel = (ListPartModel) partModel;
		}
		
		
		SubPartModel oldSubPartModel = listPartModel.getSubPartModel(subPartName);

		if (null == oldSubPartModel) {
			
			String errorMessage = new StringBuilder().append("the config file[").append(configFilePathString)
					.append("]'s key[").append(key).append("] is bad becase its sub part name[")
					.append("] is not a element of sub part name list[").append(subPartNameListValue).append("]")
					.toString();

			throw new PartConfigurationException(key, errorMessage);
		}

		ConfigItemInfo oldConfigItemInfo = oldSubPartModel.getConfigItemInfo(id);

		if (null == oldConfigItemInfo) {
			ConfigItemInfo newConfigItemInfo = new ConfigItemInfo(id);
			oldSubPartModel.addConfigItemInfo(newConfigItemInfo);

			return newConfigItemInfo;
		}
		
		return oldConfigItemInfo;
	}
	
	
	public String getInstalledPathString() {
		return installedPathString;
	}


	public String getRunningProjectName() {
		return runningProjectName;
	}


	public String getConfigFilePathString() {
		return configFilePathString;
	}

	public SequencedProperties getLoadedConfigFileSequencedProperties() {
		return loadedConfigFileSequencedProperties;
	}

	public ArrayList<PartModelIF> getPartModelList() {
		return partModelList;
	}

	public PartModelIF getPartModel(String partName) {
		for (PartModelIF partModel : partModelList) {
			if (partModel.getPartName().equals(partName)) {
				return partModel;
			}
		}

		return null;
	}
}
