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
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ItemViewType;
import kr.pe.codda.common.type.KeyTypeOfConfieProperties;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class DBCPParConfiguration implements PartConfigurationIF {	
	private Logger log = Logger.getLogger(DBCPParConfiguration.class.getName());
	

	public static final String PART_NAME = "dbcp";
	
	
	private final String dbcpName;
	private final String prefixBeforeItemID;
	
	
	public final static String itemIDOfDBCPConfigFile = "dbcp_confige_file";
	private File dbcpConfigFile = null;
	
	
	public DBCPParConfiguration(String dbcpName) {
		if (null == dbcpName) {
			throw new IllegalArgumentException("the parameter dbcpName is null");
		}
		
		
		this.dbcpName = dbcpName;
		
		prefixBeforeItemID = new StringBuilder()
				.append(PART_NAME)
				.append(".").append(dbcpName)
				.append(".").toString();
	}	
	
	@Override
	public String getPartName() {
		return PART_NAME;
	}
	
	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, PartConfigurationException {
		if (null == sourceSequencedProperties) {
			throw new IllegalArgumentException("the parameter sourceSequencedProperties is null");
		}
		
		fromPropertiesForDBCPConfigFile(sourceSequencedProperties);
	}
	
	@Override
	public void checkForDependencies() throws PartConfigurationException {
		/** nothing */
	}
	
	public void fromPropertiesForDBCPConfigFile(SequencedProperties sourceSequencedProperties) throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration
				.buildKeyOfConfigFile(prefixBeforeItemID, itemIDOfDBCPConfigFile, KeyTypeOfConfieProperties.VALUE);
				
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
		String itemDescKey = RunningProjectConfiguration
				.buildKeyOfConfigFile(prefixBeforeItemID, itemIDOfDBCPConfigFile, KeyTypeOfConfieProperties.DESC);
				
		String itemDescValue = "dbcp 설정 파일 경로를 입력해 주세요";		
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration
				.buildKeyOfConfigFile(prefixBeforeItemID, itemIDOfDBCPConfigFile, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == dbcpConfigFile) ? "" : dbcpConfigFile.getAbsolutePath();		
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration
				.buildKeyOfConfigFile(prefixBeforeItemID, itemIDOfDBCPConfigFile, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.FILE.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		String dbcpConfigFileKey = RunningProjectConfiguration
				.buildKeyOfConfigFile(prefixBeforeItemID, itemIDOfDBCPConfigFile, KeyTypeOfConfieProperties.FILE);
		String dbcpConfigFileValue = "resources/dbcp/dbcp."+dbcpName+".properties";
		targetSequencedProperties.put(dbcpConfigFileKey, dbcpConfigFileValue);
	}
	
	public String getDBCPName() {
		return dbcpName;
	}
	
	public File getDBCPConfigFile() {
		return dbcpConfigFile;
	}
	
	public void setDBCPConfigFile(File dbcpConfigFile) {		
		this.dbcpConfigFile = dbcpConfigFile;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DBCPParConfiguration [dbcpConfigFile=");
		builder.append(dbcpConfigFile);
		builder.append("]");
		return builder.toString();
	}	
}
