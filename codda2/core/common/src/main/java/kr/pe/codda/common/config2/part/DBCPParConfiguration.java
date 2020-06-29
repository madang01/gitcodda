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
package kr.pe.codda.common.config2.part;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.codda.common.config2.ConfigurationIF;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.GUIItemType;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class DBCPParConfiguration implements ConfigurationIF {	
	private Logger log = Logger.getLogger(DBCPParConfiguration.class.getName());
	
	
	private final String dbcpName;
	private final String prefexOfItemID;
	
	
	private final static String itemIDOfDBCPConfigFile = "dbcp_confige_file";
	private File dbcpConfigFile = null;
	
	
	public DBCPParConfiguration(String dbcpName) {
		
		this.dbcpName = dbcpName;
		
		prefexOfItemID = new StringBuilder("dbcp.").append(dbcpName)
				.append(".").toString();
	}	
	
	@Override
	public void toValue(SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, PartConfigurationException {
		if (null == sourceSequencedProperties) {
			throw new IllegalArgumentException("the parameter sourceSequencedProperties is null");
		}
		
		toValueForDBCPConfigFile(sourceSequencedProperties);
	}
	
	@Override
	public void checkForDependencies(SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, PartConfigurationException {
		/** nothing */
	}
	
	public void toValueForDBCPConfigFile(SequencedProperties sourceSequencedProperties) throws PartConfigurationException {
		String itemKey = new StringBuilder()
				.append(prefexOfItemID)
				.append(itemIDOfDBCPConfigFile)
				.append(".value").toString();
		
		String itemValue = sourceSequencedProperties.getProperty(itemKey);
		
		if (null == itemValue) {
			String errorMessage = new StringBuilder()
					.append("the item '")
					.append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
		
		GeneralConverterReturningRegularFile nativeValueConverter = new GeneralConverterReturningRegularFile(false);
		try {
			dbcpConfigFile = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[")
					.append(itemKey)
					.append("] value[")
					.append(itemValue)
					.append("] to value using the value converter[")
					.append(GeneralConverterReturningRegularFile.class.getName())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}	
	}
	
	@Override
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException {
		if (null == targetSequencedProperties) {
			throw new IllegalArgumentException("the parameter targetSequencedProperties is null");
		}
		
		toPropertiesForDBCPConfigFile(targetSequencedProperties);
	}
	
	public void toPropertiesForDBCPConfigFile(SequencedProperties targetSequencedProperties) {		
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDOfDBCPConfigFile).append(".desc").toString();
		String itemDescValue = "dbcp 설정 파일 경로를 입력해 주세요";		
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDOfDBCPConfigFile).append(".value").toString();
		String itemValue = dbcpConfigFile.getAbsolutePath();		
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDOfDBCPConfigFile).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.FILE.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);		
		
		String guiProjectHomeBaseRelativePathKey = new StringBuilder().append(prefexOfItemID).append(itemIDOfDBCPConfigFile).append(".file").toString();
		String guiProjectHomeBaseRelativePathValue = "resources/dbcp/dbcp."+dbcpName+".properties";
		targetSequencedProperties.put(guiProjectHomeBaseRelativePathKey, guiProjectHomeBaseRelativePathValue);
	}
	
	public String getDBCPName() {
		return dbcpName;
	}
	
	public File getDBCPConfigFile() {
		return dbcpConfigFile;
	}
	
	public void setDBCPConfigFile(File dbcpConfigFile) {
		if (null == dbcpConfigFile) {
			throw new IllegalArgumentException("the parameter dbcpConfigFile is null");
		}
		
		this.dbcpConfigFile = dbcpConfigFile;
	}
	
}
