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

import java.nio.ByteOrder;

import kr.pe.codda.common.config.AbstractSetTypeNativeValueConverter;

public class SetTypeConverterReturningByteOrder extends
		AbstractSetTypeNativeValueConverter<ByteOrder> {
	public SetTypeConverterReturningByteOrder() {
		super(ByteOrder.class);
	}

	@Override
	protected void initItemValueSet() {
		itemValueSet.add(ByteOrder.LITTLE_ENDIAN.toString());
		itemValueSet.add(ByteOrder.BIG_ENDIAN.toString());
	}

	@Override
	public String getSetName() {
		return "the byteorder set";
	}

	@Override
	public ByteOrder valueOf(String itemValue)
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

		ByteOrder ret = null;
		if (itemValue.equals(ByteOrder.LITTLE_ENDIAN.toString())) {
			ret = ByteOrder.LITTLE_ENDIAN;
		} else {
			ret = ByteOrder.BIG_ENDIAN;
		}

		return ret;
	}
}
