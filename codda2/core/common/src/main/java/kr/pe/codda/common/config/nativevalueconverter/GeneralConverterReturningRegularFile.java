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

import java.io.File;

import kr.pe.codda.common.config.AbstractNativeValueConverter;

public class GeneralConverterReturningRegularFile extends
		AbstractNativeValueConverter<File> {

	private boolean isWritePermissionChecking;
	
	public GeneralConverterReturningRegularFile(boolean isWritePermissionChecking) {
		super(File.class);
		
		this.isWritePermissionChecking = isWritePermissionChecking;
	}
	

	@Override
	public File valueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		File returnValue = new File(itemValue);

		if (! returnValue.exists()) {
			String errorMessage = new StringBuilder(
					"the file that is the parameter itemValue[")
					.append(itemValue).append("] does not exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!returnValue.isFile()) {
			String errorMessage = new StringBuilder(
					"the file that is the parameter itemValue[")
					.append(itemValue).append("] is not a regular file")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!returnValue.canRead()) {
			String errorMessage = new StringBuilder(
					"the file(=the parameter itemValue[")
					.append(itemValue).append("]) doesn't hava permission to read").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		
		if (isWritePermissionChecking && !returnValue.canWrite()) {
			String errorMessage = new StringBuilder(
					"the file(=the parameter itemValue[")
					.append(itemValue).append("]) doesn't hava permission to write")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnValue;
	}
}
