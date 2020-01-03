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

public class GeneralConverterReturningIntegerBetweenMinAndMax extends AbstractMinMaxConverter<Integer> {	
	public GeneralConverterReturningIntegerBetweenMinAndMax(Integer min, Integer max) {
		super(min, max, ComparableComparator.<Integer>comparableComparator(), Integer.class);		
	}

	@Override
	protected Integer doValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Integer returnedValue = null;
		try {
			returnedValue = Integer.valueOf(itemValue);

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
