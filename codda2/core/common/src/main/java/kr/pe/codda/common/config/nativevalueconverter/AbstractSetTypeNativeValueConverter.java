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

import java.util.HashSet;
import java.util.Set;



/**
 * 지정한 값의 집합을 갖는 제너릭 타입의 값 변환기 추상화 클래스
 * @author Won Jonghoon
 *
 * @param <E> 제너릭 타입
 */
public abstract class AbstractSetTypeNativeValueConverter<E> extends AbstractNativeValueConverter<E> {
	protected Set<String> itemValueSet = new HashSet<String>();


	/**
	 * 생성자
	 * @param genericTypeClass 제너릭 타입 클래스
	 */
	public AbstractSetTypeNativeValueConverter(Class<E> genericTypeClass) {
		super(genericTypeClass);
		initItemValueSet();
	}	
	
	/**
	 * 지정한 값의 집합 초기화 추상화 메소드
	 */
	abstract protected void initItemValueSet();
	
	/**
	 * @return 집합 이름 추상화 메소드
	 */
	abstract public String getSetName();
	
	
	/**
	 * @return 지정한 값의 집합
	 */
	public Set<String> getItemValueSet() {
		return itemValueSet;
	}

	/*public HashMap<String, E> getNativeValueHash() {
		return nativeValueHash;
	}*/

	/**
	 * @return 지정한 값의 집합 표현 문자열
	 */
	public String getStringFromSet() {		
		return itemValueSet.toString();
	}
}
