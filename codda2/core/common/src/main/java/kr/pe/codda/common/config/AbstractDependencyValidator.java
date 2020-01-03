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

import java.util.Properties;

import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

/**
 * 의존성 검사기 추상화 클래스
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractDependencyValidator {

	protected ItemIDInfo<?> dependentSourceItemIDInfo = null;
	protected ItemIDInfo<?> dependentTargetItemIDInfo = null;
	
	/**
	 * 생성자
	 * 
	 * @param dependentSourceItemIDInfo '의존 주체 항목 식별자 정보'
	 * @param dependentTargetItemIDInfo '의존 대상 항목 식별자 정보'
	 * @throws IllegalArgumentException 파라미터 값이 null 인 경우 던지는 예외 
	 * 혹은 '의존 대상 항목 식별자 정보' 의 설정 파트가 공통이 아닌 경우 '의존 주체 항목 식별자 정보'와 '의존 대상 항목 식별자 정보' 의 설정 파트가 다른 경우 던지는 예외
	 */
	public AbstractDependencyValidator(ItemIDInfo<?> dependentSourceItemIDInfo, ItemIDInfo<?> dependentTargetItemIDInfo)
			throws IllegalArgumentException {

		if (null == dependentSourceItemIDInfo) {
			String errorMessage = "the parameter dependentSourceItemInfo is null";

			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == dependentTargetItemIDInfo) {
			String errorMessage = "the parameter dependentTargetItemInfo is null";

			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		ItemIDInfo.ConfigurationPart configurationPartOfDependentSourceItemID = dependentSourceItemIDInfo
				.getConfigurationPart();
		ItemIDInfo.ConfigurationPart configurationPartOfDependentTargetItemID = dependentTargetItemIDInfo
				.getConfigurationPart();

		if (! configurationPartOfDependentTargetItemID.equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			if (! configurationPartOfDependentTargetItemID.equals(configurationPartOfDependentSourceItemID)) {
				String errorMessage = new StringBuilder("the dependent target item id[")
						.append(dependentTargetItemIDInfo.getItemID()).append("]'s configuration part[")
						.append(configurationPartOfDependentTargetItemID).append("] must be one of common part")
						.append(CommonStaticFinalVars.NEWLINE).append(" or equal to the dependent source item id[")
						.append(dependentSourceItemIDInfo.getItemID()).append("]'s configuration part[")
						.append(configurationPartOfDependentSourceItemID).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		this.dependentSourceItemIDInfo = dependentSourceItemIDInfo;
		this.dependentTargetItemIDInfo = dependentTargetItemIDInfo;

	}

	/**
	 * 외부에 공개되는 메소드로 "환경 설정 파일 내용을 적재한 프로퍼티"와 "항목이 속한 파트의 접두어" 를 입력으로 받아 "의존 주체 항목
	 * 키"와 "의존 대상 항목의 키" 를 구하여 내부적으로 사용하는 사용자 정의 메소드
	 * {@link #isValid(Properties, String, String)} 를 호출한다.
	 * 
	 * @param sourceProperties
	 *            "환경 설정 파일 내용을 적재한 프로퍼티"
	 * @param prefixOfDependentSourceItemID
	 *            "항목 식별자가 속한 파트의 접두어"
	 * @return 의존관계가 성립하면 true 를 의존 관계가 성립하지 않으면 false 를 반환한다. 단 의존관계가 성립하지 않으면 대부분
	 *         예외를 던질수있다.
	 * @throws IllegalArgumentException
	 *             잘못된 파라미티터 값을 넣은 경우 혹은 의존관계가 성립하지 않을때 던지는 예외
	 */
	public boolean isValid(Properties sourceProperties, String prefixOfDependentSourceItemID)
			throws IllegalArgumentException {
		if (null == sourceProperties) {
			String errorMessage = "the parameter sourceProperties is null";
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == prefixOfDependentSourceItemID) {
			String errorMessage = "the parameter prefixOfDependentSourceItemID is null";
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		String dependentSourceItemKey = new StringBuilder(prefixOfDependentSourceItemID)
				.append(getDependentSourceItemID()).toString();

		String dependentTargetItemID = getDependentTargetItemID();
		String dependentTargetItemKey = null;
		if (dependentTargetItemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			dependentTargetItemKey = dependentTargetItemID;
		} else {
			dependentTargetItemKey = new StringBuilder(prefixOfDependentSourceItemID).append(dependentTargetItemID)
					.toString();
		}

		return isValid(sourceProperties, dependentSourceItemKey, dependentTargetItemKey);
	}

	/**
	 * {@link #isValid(Properties, String)} 메소드 내부에서 호출되는 사용자 정의 메소드이다. * Warning!
	 * 내부적으로 사용되는 메소드로 호출 금지
	 * 
	 * @param sourceProperties
	 *            "환경 설정 파일 내용을 적재한 프로퍼티"
	 * @param dependentSourceItemKey
	 *            "의존 주체 항목 키"
	 * @param dependentTargetItemKey
	 *            "의존 대상 항목의 키"
	 * @return 의존관계가 성립하면 true 를 의존 관계가 성립하지 않으면 false 를 반환한다. 단 의존관계가 성립하지 않으면 대부분
	 *         예외를 던질수있다.
	 * @throws IllegalArgumentException
	 *             잘못된 파라미티터 값을 넣은 경우 혹은 의존관계가 성립하지 않을때 던지는 예외
	 */
	public abstract boolean isValid(Properties sourceProperties, String dependentSourceItemKey,
			String dependentTargetItemKey) throws IllegalArgumentException;

	/**
	 * @return "의존 주체 항목 식별자"
	 */
	public String getDependentSourceItemID() {
		return dependentSourceItemIDInfo.getItemID();
	}

	/**
	 * @return "의존 대상 항목 식별자"
	 */
	public String getDependentTargetItemID() {
		return dependentTargetItemIDInfo.getItemID();
	}
}
