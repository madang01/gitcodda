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
package kr.pe.codda.common.message.builder.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

/**
 * 순서화된 항목 정보 집합
 * 
 * @author Won Jonghoon
 *
 */
public class OrderedMessageItemSet {
	private ArrayList<AbstractMessageItemInfo> itemInfoList = new ArrayList<AbstractMessageItemInfo>();
	private HashMap<String, AbstractMessageItemInfo> itemInfoHash = new HashMap<String, AbstractMessageItemInfo>();

	/**
	 * 항목 정보를 추가한다.
	 * 
	 * @param itemInfo 항목 정보
	 */
	public void addItemInfo(AbstractMessageItemInfo itemInfo) {
		if (null ==  itemInfo) {
			throw new IllegalArgumentException("the parameter itemInfo is null");
		}
		
		if (isRegisted(itemInfo.getItemName())) {
			String errorMessage = new StringBuilder()
					.append("the parameter itemInfo[")
					.append(itemInfo.toString())
					.append(" was registed").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemInfoList.add(itemInfo);
		itemInfoHash.put(itemInfo.getItemName(), itemInfo);
	}
	
	/**
	 * @param itemIName 항목 이름
	 * @return 파라미터 'itemIName' 값을 항목 이름으로 갖는 항목 정보 등록 유무
	 */
	public boolean isRegisted(String itemIName) {
		if (null ==  itemIName) {
			throw new IllegalArgumentException("the parameter itemIName is null");
		}
		return itemInfoHash.containsKey(itemIName);
	}
	
	/**
	 * @param itemIName 항목 이름
	 * @return 파라미터 'itemIName' 값을 항목 이름으로 갖는 항목 정보, 없다면 null 을 반환한다.
	 */
	public AbstractMessageItemInfo getItemInfo(String itemIName) {
		if (null ==  itemIName) {
			throw new IllegalArgumentException("the parameter itemIName is null");
		}
		return itemInfoHash.get(itemIName);
	}
	
	/**
	 * @return 항목 정보 목록
	 */
	public List<AbstractMessageItemInfo> getItemInfoList() {
		return itemInfoList;
	}
	
	public String toString() {
		StringBuffer toStringStringBuffer = new StringBuffer();
		
		boolean isFirst = true;
		for (AbstractMessageItemInfo itemInfo : itemInfoList) {
			if (isFirst) {
				isFirst  = false;
			} else {
				toStringStringBuffer.append(", ");
			}
			
			toStringStringBuffer.append(CommonStaticFinalVars.NEWLINE);			
			toStringStringBuffer.append(itemInfo.toString());
		}
		
		return toStringStringBuffer.toString();
	}
}
