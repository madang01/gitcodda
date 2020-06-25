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
 * 지정한 값들의 집합에 속한 값들만 허용하는 Integer 타입 값 변환기
 *   
 * @author "Won Jonghoon"
 *
 */
public class SetTypeConverterReturningInteger extends AbstractSetTypeNativeValueConverter<Integer> {
	
	/**
	 * 생성자
	 * @param allowedIntegerValueSet 허용을 원하는 정수값 가변 변수
	 * 
	 * @throws IllegalArgumentException 허용을 원하는 정수값 가변 변수 값을 지정 안했을 경우 혹은 허용을 원하는 정수값 가변 변수에 정수가 아닌 값이 포함된 경우 던지는 예외
	 */
	public SetTypeConverterReturningInteger(String... allowedIntegerValueSet) throws IllegalArgumentException {
		super(Integer.class);
		
		if (allowedIntegerValueSet.length == 0) {
			String errorMessage = "parameter parmValueSet is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		for (String value : allowedIntegerValueSet) {			
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
