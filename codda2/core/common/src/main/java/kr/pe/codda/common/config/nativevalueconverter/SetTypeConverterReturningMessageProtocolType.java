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

package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.type.MessageProtocolType;

/**
 * 열거형 {@link MessageProtocolType} 의 원소들로 이루어진 집합을 갖는 MessageProtocolType 타입 값 변환기
 * 
 * @author "Won Jonghoon"
 * 
 */
public class SetTypeConverterReturningMessageProtocolType extends
		AbstractSetTypeNativeValueConverter<MessageProtocolType> {

	public SetTypeConverterReturningMessageProtocolType() {
		super(MessageProtocolType.class);
	}

	@Override
	protected void initItemValueSet() {
		MessageProtocolType[] nativeValues = MessageProtocolType
				.values();
		for (int i = 0; i < nativeValues.length; i++) {
			itemValueSet.add(nativeValues[i].toString());
		}
	}

	@Override
	public String getSetName() {
		return "the message protocol set";
	}

	@Override
	public MessageProtocolType valueOf(String itemValue)
			throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		MessageProtocolType returnValue = null;
		try {
			returnValue = MessageProtocolType.valueOf(itemValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not a element of ")
					.append(getSetName()).append(getStringFromSet()).toString();
			throw new IllegalArgumentException(errorMessage);
		}

		return returnValue;
	}
}
