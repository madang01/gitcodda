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

package kr.pe.codda.common.config.dependoninactivechecker;

import java.io.File;

import kr.pe.codda.common.config.AbstractDisabledItemChecker;
import kr.pe.codda.common.config.AbstractNativeValueConverter;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;

/**
 * 이 클래스는 환경 변수 "RSA 키 쌍 소스에 의존하는 "RSA 키 쌍이 있는
 * 경로"(="sessionkey.rsa_keysize.value") 항목의 활성화 여부를 결정 하기위한 빈 글래스입니다. 실질적인 처리는
 * 상속 받은 AbstractDependOnSkiper 에서 모두 처리를 합니다. 빈 클래스이지만 코드 가독성 증진을 목적으로 어떤 환경
 * 변수를 처리하는지 클래스 이름을 표시하기 위한 클래스입니다.
 * 
 * "RSA 키 쌍이 있는 경로" 항목은 "RSA 키 쌍 소스" 항목에 의존하여 만약 "RSA 키 쌍 소스" 값이 "API" 이면 비활성
 * 그렇지 않고 "RSA 키 쌍 소스" 값이 "File" 이면 활성화 됩니다.
 * 
 * @author Won Jonghoon
 * 
 */
public class RSAKeyFileDisabledItemChecker extends
		AbstractDisabledItemChecker {

	/**
	 * 생성자
	 * @param disabeldTargetItemIDInfo 비활성화 대상 항목 식별자 정보
	 * @param dependentItemIDInfo 비활성을 이끄는 항목 정보
	 * @param disabledConditionStrings 비활성 상태 문자열 배열
	 * @throws IllegalArgumentException 파라미터 값들이 null 이거나 비활성 상태 문자열 배열의 크기가 0인 경우 혹은 기대한 타입이 아닌 경우 던지는 예외
	 */
	public RSAKeyFileDisabledItemChecker(
			ItemIDInfo<?> disabeldTargetItemIDInfo,
			ItemIDInfo<?> dependentItemIDInfo, String[] disabledConditionStrings)
			throws IllegalArgumentException {
		super(disabeldTargetItemIDInfo, dependentItemIDInfo,
				disabledConditionStrings);

		AbstractNativeValueConverter<?> disabledItemValueConverter = disabeldTargetItemIDInfo
				.getItemValueConverter();

		if (!disabledItemValueConverter.getGenericType().getName().equals(
				File.class.getName())) {
			String errorMessage = new StringBuilder(
					"the parameter disabeldTargetItemIDInfo[")
					.append(disabeldTargetItemIDInfo.getItemID())
					.append("]'s nativeValueConverter generic type[")
					.append(disabledItemValueConverter
							.getGenericType().getName())
					.append("] is not java.io.File").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		AbstractNativeValueConverter<?> dependentItemValueConverter = dependentItemIDInfo
				.getItemValueConverter();
		if (!(dependentItemValueConverter instanceof SetTypeConverterOfSessionKeyRSAKeypairSource)) {
			String errorMessage = new StringBuilder(
					"the parameter dependentItemIDInfo[")
					.append(dependentItemIDInfo.getItemID())
					.append("]'s nativeValueConverter[")
					.append(dependentItemValueConverter.getClass()
							.getName())
					.append("] is not a instance of SetTypeConverterOfSessionKeyRSAKeypairSource")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}

}
