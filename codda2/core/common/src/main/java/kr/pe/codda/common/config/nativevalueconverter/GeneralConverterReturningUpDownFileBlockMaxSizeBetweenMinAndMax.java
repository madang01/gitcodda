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

/**
 * 1024 배수로 최소 최대 크기가 정해진 파일 송수신 파일 블락 값 변환기, 단 최대값은 1024 보다 크거나 같아야 한다.
 * 
 * @author "Won Jonghoon"
 * 
 */
public class GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax extends
		AbstractNativeValueConverter<Integer> {
	private final int min;
	private final int max;

	/**
	 * 생성자
	 * 
	 * @param min 최소값, 주) 1024 배수 검사 없음
	 * @param max 최대값, 단 최대값은 1024보다 크거나 같아야 한다. 주) 1024 배수 검사 없음
	 * @throws IllegalArgumentException 최소값이 최대값 보다 클때 던지는 예외
	 */
	public GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(int min, int max)
			throws IllegalArgumentException {
		super(Integer.class);
		if (min < 0) {
			String errorMessage = new StringBuilder("the parameter min[")
					.append(min).append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}		
		
		if (max < 1024) {
			String errorMessage = new StringBuilder("the parameter max[")
					.append(max).append("] is less than 1024").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (min > max) {
			String errorMessage = new StringBuilder("the parameter min[")
					.append(min).append("] is greater than parameter max[")
					.append(max).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		/*
		 * if (min % 1024 != 0) { String errorMessage = new
		 * StringBuilder("parameter min[") .append(min)
		 * .append("] is not a multiple of 1024").toString(); throw new
		 * ConfigException(errorMessage); }
		 * 
		 * if (max % 1024 != 0) { String errorMessage = new
		 * StringBuilder("parameter max[") .append(max)
		 * .append("] is not a multiple of 1024").toString(); throw new
		 * ConfigException(errorMessage); }
		 */

		this.min = min;
		this.max = max;
	}

	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	@Override
	public Integer valueOf(String itemValue)
			throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		int returnedValue;
		try {
			returnedValue = Integer.parseInt(itemValue);
		} catch (NumberFormatException e) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not integer type")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (returnedValue < min) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is less than min[")
					.append(min).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (returnedValue > max) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is greater than max[")
					.append(max).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (returnedValue % 1024 != 0) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not a multiple of 1024")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		return returnedValue;
	}
}
