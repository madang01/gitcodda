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

import kr.pe.codda.common.type.MessageItemInfoType;

public class MessageGroupInfo extends AbstractMessageItemInfo {	
	private OrderedMessageItemSet orderedItemSet = new OrderedMessageItemSet();
	private String groupName = null;
	private String groupFirstUpperName = null;
	
	public MessageGroupInfo(String groupName) {
		if (null == groupName) {
			throw new IllegalArgumentException("the parameter groupName is null");
		}
		
		if (groupName.length() < 2) {
			String errorMessage = new StringBuilder()
					.append("the number[")
					.append(groupName.length())
					.append("] of character of the parameter groupName is less than two").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.groupName = groupName;
		this.groupFirstUpperName = groupName.substring(0, 1).toUpperCase() + groupName.substring(1);
	}
	
	public OrderedMessageItemSet getOrderedItemSet() {
		return orderedItemSet;
	}

	@Override
	public String getItemName() {
		return groupName;
	}

	@Override
	public String getFirstUpperItemName() {
		return groupFirstUpperName;
	}

	@Override
	public MessageItemInfoType getMessageItemInfoType() {
		return MessageItemInfoType.GROUP;
	}

	
	
}
