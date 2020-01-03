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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.pe.codda.common.config.subset.DBCPParConfiguration;

/**
 * 모든 DBCP 파트 설정 관리자
 * @author Won Jonghoon
 *
 */
public class DBCPPartConfigurationManager {	
	private List<String> dbcpNameList = new ArrayList<String>();
	private HashMap<String, DBCPParConfiguration> dbcpPartConfigurationHash = new HashMap<String, DBCPParConfiguration>();
	
	/*
	public void clear() {
		dbcpNameList.clear();
		dbcpPartConfigurationHash.clear();
	}
	*/
	
	/**
	 * dbcp 파트 설정을 추가한다.
	 * @param dbcpParConfiguration dbcp 파트 설정
	 * @throws IllegalArgumentException 지정한 'dbcp 파트 설정' 이 null 인 경우 혹은 이미 등록된 경우에 던지는 예외
	 */
	public void addDBCPParConfiguration(DBCPParConfiguration dbcpParConfiguration) throws IllegalArgumentException {		
		if (null == dbcpParConfiguration) {
			throw new IllegalArgumentException("the paramter dbcpParConfiguration is null");
		}

		String dbcpName = dbcpParConfiguration.getDBCPName();
		
		if (isRegistedDBCPName(dbcpName)) {
			throw new IllegalArgumentException("the paramter dbcpParConfiguration's dbcp name was registed");
		}

		dbcpNameList.add(dbcpName);
		dbcpPartConfigurationHash.put(dbcpName, dbcpParConfiguration);
	}
	
	/**
	 * @return 등록된 dbcp 이름 목록
	 */
	public List<String> getDBCPNameList() {
		return dbcpNameList;
	}

	/**
	 * @param dbcpName dbcp 이름
	 * @return 지정한 'dbcp 이름' 의 등록 여부
	 * @throws IllegalArgumentException 지정한 'dbcp 이름' 이 null 인 경우 던지는 예외
	 */
	public boolean isRegistedDBCPName(String dbcpName) throws IllegalArgumentException {
		if (null == dbcpName) {
			throw new IllegalArgumentException("the paramter dbcpName is null");
		}
		
		return dbcpNameList.contains(dbcpName);
	}
	

	/**
	 * @param dbcpName dbcp 이름
	 * @return 지정한 'dbcp 이름'을 갖는 'dbcp part 설정', 단 없을 경우 null 을 반환한다.
	 * @throws IllegalArgumentException 지정한 'dbcp 이름' 이 null 인 경우 던지는 예외
	 */
	public DBCPParConfiguration getDBCPPartConfiguration(String dbcpName) throws IllegalArgumentException {
		if (null == dbcpName) {
			throw new IllegalArgumentException("the paramter dbcpName is null");
		}
		
		return dbcpPartConfigurationHash.get(dbcpName);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("AllDBCPPart [dbcpNameList=");
		builder.append(dbcpNameList != null ? dbcpNameList.subList(0,
				Math.min(dbcpNameList.size(), maxLen)) : null);
		builder.append("]");
		return builder.toString();
	}
}
