/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package kr.pe.codda.common.config.subset;

import java.io.File;

import kr.pe.codda.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.codda.common.exception.CoddaConfigurationException;

/**
 * Warning! 비활성한 항목의 값은 쓰레기값이므로 
 * 환경 설정 파일을 읽어와서 Value Object 에 저장할때 건너뛰어 초기값인 null 값을 갖게 된다.
 * 따라서 Value Object 에서 값을 넘겨줄때 비활성 항목의 경우 
 * null 검사를 수행하여 ConfigErrorException 을 던져야 한다.
 * 
 * @author Won Jonghoon
 *
 */
public class DBCPParConfiguration {
	// private Logger log = LoggerFactory.getLogger(DBCPPartValueObject.class);
	
	private String dbcpName = null;
	private File dbcpConfigFile = null;
	
	private String prefexOfItemID = null;
	
	public DBCPParConfiguration(String dbcpName) {
		this.dbcpName = dbcpName;
		
		prefexOfItemID = new StringBuilder("dbcp.").append(dbcpName)
				.append(".").toString();
	}
	
	public String getDBCPName() {
		return dbcpName;
	}
	
	public File getDBCPConfigFile() {
		return dbcpConfigFile;
	}
	
	public void mapping(String itemKey, Object nativeValue) 
			throws IllegalArgumentException, CoddaConfigurationException, ClassCastException {
		if (null == itemKey) {
			throw new IllegalArgumentException("the parameter itemKey is null");
		}
		
		if (null == nativeValue) {
			throw new IllegalArgumentException("the parameter nativeValue is null");
		}
		
		if (! itemKey.startsWith(prefexOfItemID)) {
			String errorMessage = new StringBuilder("the parameter itemKey[")
			.append("] doesn't start with prefix[")
			.append(prefexOfItemID).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		/**
		 * no IndexOutOfBoundsException because the variable itemKey starts with the variable prefexOfItemID
		 */
		String itemID = itemKey.substring(prefexOfItemID.length());		
		
		
		if (itemID.equals(ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID)) {			
			if (null != nativeValue && !(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.dbcpConfigFile = (File) nativeValue;			
		} else {
			String errorMessage = new StringBuilder("unknown DBCP part item id(=the parameter itemIDInfo[")
			.append(itemID)
			.append("]), check it").toString();
			throw new CoddaConfigurationException(errorMessage);
		}
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DBCPPart [dbcpName=");
		builder.append(dbcpName);
		builder.append(", dbcpConfigFile=");
		builder.append(dbcpConfigFile);
		builder.append("]");
		return builder.toString();
	}	
}
