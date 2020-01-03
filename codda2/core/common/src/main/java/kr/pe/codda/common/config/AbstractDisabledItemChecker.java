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

package kr.pe.codda.common.config;

import java.util.HashSet;
import java.util.Properties;

import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

/**
 * 비활성 항목 검사기, 이 클래스는 쓰레드에 안전하지 않다.
 * 
 * @author Won Jonghoon
 *
 */
public abstract class AbstractDisabledItemChecker {
	
	protected ItemIDInfo<?> dependentSourceItemIDInfo  = null;
	protected ItemIDInfo<?> dependentTargetItemIDInfo = null;
	protected HashSet<String> disabledConditionStringSet = new HashSet<String>();
	
	/**
	 * 생성자
	 * @param dependentSourceItemIDInfo 비활성화 대상 항목 식별자 정보, '비활성으로 이끄는 항목 정보' 가 비활성 상태를 지정하는 문자열 집합의 값을 가질 경우에 비활성된다.
	 * @param dependentTargetItemIDInfo 비활성으로 이끄는 항목 정보
	 * @param disabledConditionStrings '비활성으로 이끄는 항목 정보'의 값으로 이루어진 비활성 상태 문자열 배열
	 * @throws IllegalArgumentException 파라미터 값들이 null 이거나 값이 잘못되어 있을 경우 던지는 예외
	 */
	public AbstractDisabledItemChecker(ItemIDInfo<?> dependentSourceItemIDInfo, ItemIDInfo<?> dependentTargetItemIDInfo, String[] disabledConditionStrings) throws IllegalArgumentException {
		if (null == dependentSourceItemIDInfo) {
			String errorMessage = new StringBuilder("the parameter disabeldTargetItemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == dependentTargetItemIDInfo) {
			String errorMessage = new StringBuilder("the parameter dependentItemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == disabledConditionStrings) {
			String errorMessage = new StringBuilder("the parameter disabledConditionStrings is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (0 == disabledConditionStrings.length) {			
			String errorMessage = new StringBuilder("the parameter disabledConditionStrings is a zero size string array").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		ItemIDInfo.ConfigurationPart configurationPartOfDependentSourceItemID = dependentSourceItemIDInfo.getConfigurationPart();
		ItemIDInfo.ConfigurationPart configurationPartOfDependentTargetItemID = dependentTargetItemIDInfo.getConfigurationPart();
		
		if (!configurationPartOfDependentTargetItemID.equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			if (!configurationPartOfDependentTargetItemID.equals(configurationPartOfDependentSourceItemID)) {
				String errorMessage = new StringBuilder(
						"the dependent target item id[")
				.append(dependentTargetItemIDInfo.getItemID())
				.append("]'s configuration part[")
				.append(configurationPartOfDependentTargetItemID)
				.append("] must be one of common part")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(" or equal to the dependent source item id[")
				.append(dependentSourceItemIDInfo.getItemID())
				.append("]'s configuration part[")
				.append(configurationPartOfDependentSourceItemID)
				.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		
		AbstractNativeValueConverter<?> dependentTargetItemValueConverter = dependentTargetItemIDInfo.getItemValueConverter();
		
		if (!(dependentTargetItemValueConverter instanceof AbstractSetTypeNativeValueConverter)) {
			String errorMessage = new StringBuilder(
					"parameter dependentTargetItemIDInfo[")
			.append(dependentTargetItemIDInfo.getItemID()).append("]'s nativeValueConverter[")
					.append(dependentTargetItemValueConverter.getClass().getName())
					.append("] is not inherited by AbstractSetTypeNativeValueConverter")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}	
		
		
		this.dependentSourceItemIDInfo  = dependentSourceItemIDInfo;
		this.dependentTargetItemIDInfo = dependentTargetItemIDInfo;		
		
		for (String disabledConditionString  : disabledConditionStrings) {
			disabledConditionStringSet.add(disabledConditionString);
		}
	}
	
	/**
	 * @param prefixOfItemID 비활성화 대상 항목 키에서 추출된 항목 식별자 앞의 접두어
	 * @param sourceProperties 설정 파일이 담긴 프로퍼티
	 * @return 비활성화 대상 항목의 비활성 여부
	 * @throws IllegalArgumentException 파라미터 값들이 null 이거나 값이 잘못되어 있을 경우 던지는 예외
	 */
	public boolean isDisabled(String prefixOfItemID, Properties sourceProperties) throws IllegalArgumentException {
		if (null == prefixOfItemID) {
			String errorMessage = new StringBuilder("parameter prefixOfItem is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == sourceProperties) {
			String errorMessage = new StringBuilder("parameter sourceProperties is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}		
		
		String dependentSoruceItemKey = new StringBuilder(prefixOfItemID).append(getDependentSourceItemIDInfo()).toString();
		String dependentSoruceItemValue = 
				sourceProperties.getProperty(dependentSoruceItemKey);
		if (null == dependentSoruceItemValue) {
			String errorMessage = new StringBuilder("the parameter sourceProperties's key(=the variable dependentSoruceItemKey[")
			.append(dependentSoruceItemKey)
			.append("]) consisting of the parameter prefixOfItem and the variable dependentSourceItemId does not exist.").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String dependentTargetItemID = getDependentTargetItemIDInfo();
		String dependentTargetItemKey = null;		
		if (dependentTargetItemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			dependentTargetItemKey = dependentTargetItemID;
		} else {
			dependentTargetItemKey = new StringBuilder(prefixOfItemID).append(
					dependentTargetItemID).toString();
		}
		String dependentTargetItemValue = sourceProperties.getProperty(dependentTargetItemKey);
		if (null == dependentTargetItemValue) {
			String errorMessage = new StringBuilder("the parameter sourceProperties's key(=the variable dependentTargetItemKey[")
			.append(dependentTargetItemKey)
			.append("]) consisting of the parameter prefixOfItem and the variable dependentTargetItemIDInfo's itemID does not exist.").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		return disabledConditionStringSet.contains(dependentTargetItemValue);
	}
	
	/**
	 * @return 의존성 주체 항목 식별자(=비활성화 대상 항목 식별자)
	 */
	public final String getDependentSourceItemIDInfo() {
		return dependentSourceItemIDInfo.getItemID();
	}

	/**
	 * @return 의존성 대상 항목 식별자(=비활성으로 이끄는 항목 식별자)
	 */
	public final String getDependentTargetItemIDInfo() {
		return dependentTargetItemIDInfo.getItemID();
	}

	/*
	public final HashSet<String> getDisabledConditionStringSet() {
		return disabledConditionStringSet;
	}
	*/
}
