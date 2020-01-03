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

/**
 * jdbc connection url 항목 유효성 검사기  
 * @author "Won Jonghoon"
 *
 */
public class SetTypeConverterReturningInteger extends AbstractSetTypeNativeValueConverter<Integer> {
	
	public SetTypeConverterReturningInteger(String ... parmValueSet) throws IllegalArgumentException {
		super(Integer.class);
		
		if (parmValueSet.length == 0) {
			String errorMessage = "parameter parmValueSet is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		for (String value : parmValueSet) {			
			try {
				Integer.parseInt(value);
			} catch(NumberFormatException e) {
				String errorMessage = new StringBuilder("the elemment[")
				.append(value)
				.append("] of parmValueSet is not integer type").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			itemValueSet.add(value);
		}
	}
	
	@Override
	protected void initItemValueSet() {
		/** 생성자에서 직접 정수 문자열 파라미터들로 받아 정수 문자열 집합을 구성하므로  이곳에서 처리는 필요 없다.  */
	}

	@Override
	public String getSetName() {
		return "the integer set";
	}
	
	
	
	@Override
	public Integer valueOf(String itemValue) throws IllegalArgumentException {
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
		
		try {
			return Integer.valueOf(itemValue);
		} catch(NumberFormatException e) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
			.append(itemValue)
			.append("] is not integer type").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
}
