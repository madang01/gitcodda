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

import kr.pe.codda.common.type.ItemViewType;

/**
 * @author Won Jonghoon
 *
 */
public class ConfigItemInfo {
	private final String id;
	
	private String value = null;	
	private String desc = null;	
	private ItemViewType itemViewType = null;
	
	
	
	public ConfigItemInfo(String id) {
		if (null == id) {
			throw new IllegalArgumentException("the paramter id is null");
		}
		/*
		 * if (null == value) { throw new
		 * IllegalArgumentException("the paramter value is null"); }
		 * 
		 * if (null != desc) { if (desc.isEmpty()) { throw new
		 * IllegalArgumentException("the paramter desc is empty"); }
		 * 
		 * if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(desc)) { throw new
		 * IllegalArgumentException("the paramter desc has a leading or tailing white space"
		 * ); } }
		 * 
		 * if (null == itemViewType) { throw new
		 * IllegalArgumentException("the paramter itemViewType is null"); }
		 * 
		 * this.id = key; this.value = value; this.desc = desc; this.itemViewType =
		 * itemViewType;
		 */
		
		this.id = id;
	}
	
	public String getID() {
		return id;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	public String getDesc() {
		return desc;
	}



	public void setDesc(String desc) {
		this.desc = desc;
	}



	public ItemViewType getItemViewType() {
		return itemViewType;
	}



	public void setItemViewType(ItemViewType itemViewType) {
		this.itemViewType = itemViewType;
	}
	
}
