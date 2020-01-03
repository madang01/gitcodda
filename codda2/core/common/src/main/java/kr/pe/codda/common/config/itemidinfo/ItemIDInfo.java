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

package kr.pe.codda.common.config.itemidinfo;


import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.AbstractMinMaxConverter;
import kr.pe.codda.common.config.AbstractNativeValueConverter;
import kr.pe.codda.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;

/**
 * 항목 식별자  환경 설정 정보 클래스
 * 
 * @author "Won Jonghoon"
 * 
 */
public class ItemIDInfo<T> {
	protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	public enum ConfigurationPart {
		DBCP, COMMON, PROJECT
	};

	public enum ViewType {
		TEXT, FILE, PATH, SINGLE_SET
	};

	private ConfigurationPart configurationPart;
	private ViewType viewType;
	private String itemID;
	private String description;

	private String defaultValue;
	private boolean isDefaultValueCheck;
	private AbstractNativeValueConverter<T> itemValueConverter;

	/**
	 * 환경 변수 값을 검사하기 위한 정보 클래스 생성자
	 * 
	 * @param configPart
	 *            환경 설정 항목들이 속한 파트
	 * @param itemViewType
	 *            환경 설정 도구에서 항목 값 표현 방식
	 * @param itemID
	 *            항목 식별자
	 * @param description
	 *            환경 변수 설명
	 * @param defaultValue
	 *            디폴트 값
	 * @param isDefaultValueCheck
	 *            객체 생성시 디폴트 값 검사 수행 여부, 잘못된 값을 가져도 당장 검사하지 않고 사용자가 후에 환경설정 도구에서 이를 인지하여 고치도록 할때 false 를 넣는다.
	 *            예를 들면 파일 관련 항목은 파일이 반듯이 존재해야 하는데 환경 설정 항목 정보 구성시 
	 * @param nativeValueConverter
	 *            문자열인 환경 설정 파일의 값을 언어 종속적 값으로 바꾸어 주는 변환기
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 던지는 예외
	 * @throws CoddaConfigurationException
	 *             디폴트 값 검사 수행시 디폴트 값이 잘못된 경우 던지는 예외
	 */
	public ItemIDInfo(ConfigurationPart configPart,
			ViewType itemViewType, String itemID,
			String description, String defaultValue,
			boolean isDefaultValueCheck,
			AbstractNativeValueConverter<T> nativeValueConverter)
			throws IllegalArgumentException, CoddaConfigurationException {
		if (null == configPart) {
			String errorMessage = "the parameter configPart is null";
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == itemViewType) {
			String errorMessage = "the parameter itemViewType is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == itemID) {
			String errorMessage = "the parameter itemID is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemID.equals("")) {
			String errorMessage = "the parameter itemID is a empty string";
			throw new IllegalArgumentException(errorMessage);
		}
		
		int firstIndex = itemID.indexOf(".value");
		if (firstIndex < 0) {
			String errorMessage = new StringBuilder("parameter itemID[")
			.append(itemID)
			.append("] must end with '.value'").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (0 == firstIndex) {
			String errorMessage = new StringBuilder("parameter itemID[")
			.append(itemID)
			.append("] must not start with '.value'").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemID.length() != (firstIndex+".value".length())) {
			String errorMessage = new StringBuilder("the parameter itemID[")
			.append(itemID)
			.append("] has '.value' string in the middle").toString();
			throw new IllegalArgumentException(errorMessage);
		}	

		if (null == description) {
			String errorMessage = "the parameter description is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (description.equals("")) {
			String errorMessage = "the parameter description is a empty string";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == defaultValue) {
			String errorMessage = "the parameter defaultValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == nativeValueConverter) {
			String errorMessage = "the parameter itemValueConverter is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (isDefaultValueCheck) {
			try {
				nativeValueConverter.valueOf(defaultValue);
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder(
						"the parameter defaultValue[").append(defaultValue)
						.append("] test failed").toString();
				
				log.log(Level.INFO, errorMessage, e);
				
				
				throw new IllegalArgumentException(new StringBuilder(errorMessage)
				.append(", errormessage=").append(e.getMessage()).toString());
			}
		}

		if (itemViewType == ViewType.SINGLE_SET) {
			if (!(nativeValueConverter instanceof AbstractSetTypeNativeValueConverter)) {
				String errorMessage = "parameter configItemViewType is a signle view type  "
						+ "but parameter itemValidator object is not a instance of  a SingleSetValueGetterIF type";
				throw new IllegalArgumentException(errorMessage);
			}
		}	

		this.configurationPart = configPart;
		this.viewType = itemViewType;
		this.itemID = itemID;
		this.description = description;
		this.defaultValue = defaultValue;
		this.isDefaultValueCheck = isDefaultValueCheck;
		this.itemValueConverter = nativeValueConverter;
	}
	
	/**
	 * 항목 설명하는 키 문자열를 반환한다
	 * @param prefixOfItemID 항목의 키 접두어 문자열
	 * @return 항목 설명하는 키 문자열
	 */
	public String getItemDescKey(String prefixOfItemID) {
		if (null == prefixOfItemID) {
			throw new IllegalArgumentException("the paramter prefixOfItemID is null");
		}
		String postfix = ".value";
		int lastIndexOfPostfix = itemID.lastIndexOf(postfix);			
		String itemDescKey = new StringBuilder(prefixOfItemID)
		.append(itemID.substring(0, lastIndexOfPostfix)).append(".desc").toString();
		
		return itemDescKey;
		
	}

	/**
	 * @return 환경 설정 GUI 프로그램에서 항목의 뷰 종류
	 */
	public ViewType getViewType() {
		return viewType;
	}

	/**
	 * @return 항목이 속한 설정 파트
	 */
	public ConfigurationPart getConfigurationPart() {
		return configurationPart;
	}

	/**
	 * @return 항목 식별자
	 */
	public String getItemID() {
		return itemID;
	}

	/**
	 * @return 디폴트 값
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return 항목 값 변환기
	 */
	public AbstractNativeValueConverter<T> getItemValueConverter() {
		return itemValueConverter;
	}

	/**
	 * @return 디폴트 값 검사 유무 
	 */
	public boolean isDefaultValueCheck() {
		return isDefaultValueCheck;
	}
	
	/** 
	 * @return 단일 값 항목의 경우에만 미리 정의한 항목의 값 집합을 반환하며 그외 경우에는 null 을 반환한다.
	 */
	public Set<String> getItemValueSet() {
		Set<String> itemSet = null;
		if (viewType.equals(ItemIDInfo.ViewType.SINGLE_SET)) {
			AbstractSetTypeNativeValueConverter<?> setTypeNativeConvter = 
					(AbstractSetTypeNativeValueConverter<?>)itemValueConverter;
			itemSet = setTypeNativeConvter.getItemValueSet();
		}
		return itemSet;
	}

	/**
	 * @return 항목 설명 문구
	 */
	public String getDescription() {
		StringBuilder descriptBuilder = new StringBuilder(description);

		descriptBuilder.append(", default value[");
		descriptBuilder.append(defaultValue);
		descriptBuilder.append("]");

		if (itemValueConverter instanceof AbstractSetTypeNativeValueConverter) {
			AbstractSetTypeNativeValueConverter<?> copyitemValueGetter = (AbstractSetTypeNativeValueConverter<?>) itemValueConverter;
			descriptBuilder.append(", ");
			descriptBuilder.append(copyitemValueGetter.getSetName());
			descriptBuilder.append(copyitemValueGetter.getItemValueSet().toString());
		} else if (itemValueConverter instanceof AbstractMinMaxConverter) {
			AbstractMinMaxConverter<?> minMaxConverter = (AbstractMinMaxConverter<?>) itemValueConverter;
			descriptBuilder.append(", min[");
			descriptBuilder.append(minMaxConverter.getMin());
			descriptBuilder.append("], max[");
			descriptBuilder.append(minMaxConverter.getMax());			
			descriptBuilder.append("]");
		}

		return descriptBuilder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigItem [configPart=");
		builder.append(configurationPart);
		builder.append(", viewType=");
		builder.append(viewType);
		builder.append(", itemID=");
		builder.append(itemID);
		builder.append(", description=");
		builder.append(description);
		builder.append(", defaultValue=");
		builder.append(defaultValue);
		builder.append(", isDefaultValueCheck=");
		builder.append(isDefaultValueCheck);
		builder.append(", itemValueGetter=");
		builder.append(itemValueConverter);
		builder.append("]");
		return builder.toString();
	}
}
