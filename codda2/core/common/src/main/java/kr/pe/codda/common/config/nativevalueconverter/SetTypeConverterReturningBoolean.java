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

public class SetTypeConverterReturningBoolean extends
		AbstractSetTypeNativeValueConverter<Boolean> {
	public SetTypeConverterReturningBoolean() {
		super(Boolean.class);
	}

	@Override
	protected void initItemValueSet() {
		itemValueSet.add(Boolean.FALSE.toString());
		itemValueSet.add(Boolean.TRUE.toString());
	}

	@Override
	public String getSetName() {
		return "the boolean set";
	}

	@Override
	public Boolean valueOf(String itemValue)
			throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		if (!itemValueSet.contains(itemValue)) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not an element of ")
					.append(getSetName()).append(getStringFromSet())
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		Boolean returnedValue = null;

		/**
		 * Warning) Boolean.valueOf("king") return Boolean.FALSE; if you want to
		 * use Boolean.valueOf method then you must check the parameter is the
		 * element of the boolean set;
		 */
		/*
		 * if (itemValue.equals(Boolean.FALSE.toString())) { returnedValue =
		 * Boolean.FALSE; } else if (itemValue.equals(Boolean.TRUE.toString()))
		 * { returnedValue = Boolean.TRUE; } else { String errorMessage = new
		 * StringBuilder("parameter itemValue[") .append(itemValue)
		 * .append("] is not a element of ") .append(getSetName())
		 * .append(getStringFromSet()) .append("]").toString(); throw new
		 * IllegalArgumentException(errorMessage); }
		 */

		returnedValue = Boolean.valueOf(itemValue);

		return returnedValue;
	}

}
