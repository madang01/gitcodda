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

package kr.pe.codda.common.config.iteminfo;


import java.util.logging.Logger;

import org.junit.Test;

import kr.pe.codda.common.config.AbstractNativeValueConverter;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo.ViewType;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class ItemIDInfoTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_NullParameter_configPart() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = null;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter configPart is null, errormessage=" + e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_NullParameter_configItemViewType() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = null;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter configItemViewType is null, errormessage=" + e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_NullParameter_itemID() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = null;
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";	
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter itemID is null, errormessage=" + e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_NullParameter_description() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = null;
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter description is null, errormessage=" + e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_NullParameter_defaultValue() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = null;
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter defaultValue is null, errormessage=" + e.getMessage());
			throw e;
		}
	}	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_NullParameter_nativeValueConverter() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter = null;
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter nativeValueConverter is null, errormessage=" + e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_EmptyParameter_itemID() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter itemID is a empty string, errormessage=" + e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_EmptyParameter_description() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter description is a empty string, errormessage=" + e.getMessage());
			throw e;
		}
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testItemIDInfo_ValidButBadParameter_defaultValue() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/tmp.jsp ";
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
			
		} catch (IllegalArgumentException e) {
			log.info("the parameter defaultValue has leading or tailing white space, errormessage=" + e.getMessage());
			throw e;
		}
	}
	
}
