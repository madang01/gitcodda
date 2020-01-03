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

import kr.pe.codda.common.config.AbstractMinMaxConverter;
import kr.pe.codda.common.util.ComparableComparator;

public class GeneralConverterReturningDoubleBetweenMinAndMax extends AbstractMinMaxConverter<Double> {	
	public GeneralConverterReturningDoubleBetweenMinAndMax(Double min, Double max) {
		super(min, max, ComparableComparator.<Double>comparableComparator(), Double.class);		
	}

	@Override
	protected Double doValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Double returnedValue = null;
		try {
			returnedValue = Double.valueOf(itemValue);
			
			/**
			 * WARNING! Double 은 특수하게 Double 로 표현할 수 없는 값이라도 NumberFormatException 으로 떨구지 않고 아래와 같은 상태 플래그 값을 설정함.
			 */
			if (returnedValue.isInfinite() || returnedValue.isNaN()) {
				String errorMessage = new StringBuilder("the parameter itemValue[")
						.append(itemValue).append("] is not a number of ")
						.append(getGenericType().getName())
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

		} catch (NumberFormatException e) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not a number of ")
					.append(getGenericType().getName())
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnedValue;
	}
}