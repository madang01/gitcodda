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

public class OrderedItemSet {
	private ArrayList<AbstractItemInfo> itemInfoList = new ArrayList<AbstractItemInfo>();
	private HashMap<String, AbstractItemInfo> itemInfoHash = new HashMap<String, AbstractItemInfo>();

	public void addItemInfo(AbstractItemInfo itemInfo) {
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
	
	public boolean isRegisted(String itemIName) {
		if (null ==  itemIName) {
			throw new IllegalArgumentException("the parameter itemIName is null");
		}
		return itemInfoHash.containsKey(itemIName);
	}
	
	public AbstractItemInfo getItemInfo(String itemIName) {
		if (null ==  itemIName) {
			throw new IllegalArgumentException("the parameter itemIName is null");
		}
		return itemInfoHash.get(itemIName);
	}
	
	public List<AbstractItemInfo> getItemInfoList() {
		return itemInfoList;
	}
	
	public String toString() {
		StringBuffer toStringStringBuffer = new StringBuffer();
		
		boolean isFirst = true;
		for (AbstractItemInfo itemInfo : itemInfoList) {
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
