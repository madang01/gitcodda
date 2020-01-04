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

import kr.pe.codda.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * 빈 문자열 포함하여 앞뒤로 공백 문자가 없도록 허락한 문자열만 허용하는 문자열 변환기  
 * @author "Won Jonghoon"
 *
 */
public class SetTypeConverterReturningString extends AbstractSetTypeNativeValueConverter<String> {
	
	/**
	 * 생성자
	 * @param allowedValueSet 허락한 문자열
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public SetTypeConverterReturningString(String ... allowedValueSet) throws IllegalArgumentException {
		super(String.class);
		
		if (allowedValueSet.length == 0) {
			String errorMessage = "the parameter parmValueSet is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (int i=0; i < allowedValueSet.length; i++) {
			String itemValue = allowedValueSet[i];
			if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(itemValue)) {
				String errorMessage = new StringBuilder()
				.append("the parameter parmValueSet[")
				.append(i)
				.append("]'s value[")
				.append(itemValue)
				.append("] has leading or tailing white space").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			itemValueSet.add(itemValue);
		}
	}
	
	@Override
	protected void initItemValueSet() {
		/** 생성자에서 직접 문자열 파라미터들로 받아 문자열 집합을 구성하므로  이곳에서 처리는 필요 없다.  */
		
	}

	@Override
	public String getSetName() {
		return "the string set";
	}
	
	@Override
	public String valueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! itemValueSet.contains(itemValue)) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
			.append(itemValue)
			.append("] is not a element of ")
			.append(getSetName())
			.append(getStringFromSet())
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}	
		
		return itemValue;
	}

	

}
