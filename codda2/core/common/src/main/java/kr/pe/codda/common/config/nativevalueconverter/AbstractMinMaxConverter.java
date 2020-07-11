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

import java.util.Comparator;

/**
 * 최소 최대 값 변환기 추상화 클래스
 * @author Won Jonghoon
 *
 * @param <T> 제너럴 타입
 */
public abstract class AbstractMinMaxConverter<T extends Number> extends AbstractNativeValueConverter<T> {

	protected T min;
	protected T max;
	private Comparator<T> typeComparator = null;

	/**
	 * 생성자
	 * @param min 최소
	 * @param max 최대
	 * @param typeComparator 제너릭 타입 값 비교자
	 * @param genericType 제터릭 타입
	 */
	public AbstractMinMaxConverter(T min, T max, Comparator<T> typeComparator, Class<T> genericType) {
		super(genericType);

		if (null == min) {
			String errorMessage = new StringBuilder("the parameter min is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == max) {
			String errorMessage = new StringBuilder("the parameter max is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == typeComparator) {
			String errorMessage = new StringBuilder("the parameter typeComparator is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (typeComparator.compare(min, max) > 0) {
			String errorMessage = new StringBuilder("the parameter min[").append(min)
					.append("] is greater than parameter max[").append(max).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.min = min;
		this.max = max;
		this.typeComparator = typeComparator;
	}

	/**
	 * {@link #valueOf(String)} 에서 호출되는 내부용 메소드로 {@link #valueOf(String)} 에서 최소 최대값
	 * 제약이라는 공통 부분을 제외한 지정한 타입으로 입력받은 문자열 타입의 항목의 값을 변환하는 기능을 담당한다.
	 * 
	 * @param itemValue 항목의 값
	 * @return 지정한 타입으로 변환된 입력받은 문자열 타입의 항목의 값
	 * @throws IllegalArgumentException 항목의 값이 잘못되었을 경우 던지는 예외
	 */
	abstract protected T doValueOf(String itemValue) throws IllegalArgumentException;

	/**
	 * @return 최소 값
	 */
	public T getMin() {
		return min;
	}

	/**
	 * @return 최대 값
	 */
	public T getMax() {
		return max;
	}

	/**
	 * @return 제너릭 타입 값 비교자
	 */
	public Comparator<T> getTypeComparator() {
		return typeComparator;
	}

	@Override
	public T valueOf(String itemValue) throws IllegalArgumentException {
		T returnedValue = doValueOf(itemValue);

		if (typeComparator.compare(returnedValue, min) < 0) {
			String errorMessage = new StringBuilder("the parameter itemValue[").append(itemValue)
					.append("] is less than min[").append(min).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (typeComparator.compare(returnedValue, max) > 0) {
			String errorMessage = new StringBuilder("the parameter itemValue[").append(itemValue)
					.append("] is greater than max[").append(max).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		return returnedValue;
	}
}
