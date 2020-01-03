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

import kr.pe.codda.common.config.AbstractNativeValueConverter;
import kr.pe.codda.common.util.CommonStaticUtil;

public class GeneralConverterReturningEmptyOrNoTrimString extends AbstractNativeValueConverter<String> {

	public GeneralConverterReturningEmptyOrNoTrimString() {
		super(String.class);
	}

	@Override
	public String valueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}				
		
		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(itemValue)) {
			String errorMessage = new StringBuilder()
			.append("the parameter itemValue[")
			.append(itemValue)
			.append("] has leading or tailing white space").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		/*String trimValue = itemValue.trim();
		if (!trimValue.equals(itemValue)) {
			String errorMessage = "parameter itemValue have a trim string";
			throw new IllegalArgumentException(errorMessage);
		}*/
		
		return itemValue;
	}

}
