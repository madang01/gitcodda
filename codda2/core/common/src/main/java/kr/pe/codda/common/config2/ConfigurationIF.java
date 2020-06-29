/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.pe.codda.common.config2;

import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * 설정 인터페이스
 * 
 * @author Won Jonghoon
 *
 */
public interface ConfigurationIF {
	/**
	 * 설정 파일로 부터 읽어 들인 시퀀스 프로퍼티로 부터 값을 추출한다.
	 *  
	 * @param sourceSequencedProperties 값을 추출할 설정 파일로 부터 읽어 들인 시퀀스 프로퍼티
	 * @throws IllegalArgumentException 설정 파일로 부터 읽어 들인 시퀀스 프로퍼티가 null 인 경우 던지는 예외
	 * @throws PartConfigurationException 설정 파일로 부터 읽어 들인 시퀀스 프로퍼티의 키와 값이 잘못된 경우 던지는 예외 
	 */
	public void toValue(SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, PartConfigurationException;
	
	
	public void checkForDependencies(SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, PartConfigurationException;
	
	
	/**
	 * 설정 파일에 저장할 시퀀스 프로퍼티에 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 값이 저장될 설정 파일에 저장할 시퀀스 프로퍼티   
	 * @throws IllegalArgumentException 설정 파일에 저장할 시퀀스 프로퍼티가 null 인 경우 던지는 예외
	 */
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException;	 
}
