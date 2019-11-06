/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.common.message.builder.info;

import kr.pe.codda.common.type.ItemInfoType;

/**
 * 배열 정보 클래스. 배열 이름, 배열 크기, 배열에 속한 항목 그룹 정보를 가지고 있다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ArrayInfo extends AbstractItemInfo {
	private OrderedItemSet orderedItemSet = new OrderedItemSet();

	private String arrayName = null;
	
	private String arrayFirstUpperName = null;
	/**
	 * 배열의 반복 횟수 지정 방식(cnttype)은 2가지가 있다.<br/>
	 * (1) 직접(direct) : 고정 크기 지정방식으로 배열 반복 횟수에는 배열의 반복 횟수 값이 저장되며,<br/>
	 * (2) 참조(reference) : 가변 크기 지정방식으로 배열 반복 횟수는 참조하는 항목의 값이다.
	 */
	private String arrayCntType = null;
	/**
	 * 배열의 반복 횟수(cntvalue) "배열의 반복 횟수 지정 방식"이 직접(direct) 이면 배열 반복 횟수를 반환하며,<br/>
	 * 참조(reference)일 경우에는 참조하는 항목 이름을 반환한다.<br/>
	 * 참조하는 항목은 숫자형으로 배열과 같은 단계로 반듯이 앞에 나와야 한다.<br/>
	 * 이렇게 앞에 나와야 하는 이유는 배열 정보를 읽어와서 <br/>
	 * 배열 정보를 저장하기 전에 참조 변수가 같은 레벨에서 존재하며 숫자형인지 판단을 하기 위해서이다.<br/>
	 * 메시지 정보 파일을 순차적으로 읽기 때문에 배열 뒤에 위치하면 알 수가 없다
	 */
	private String arrayCntValue = null;

	/**
	 * 생성자
	 * 
	 * @param arrayName
	 *            배열 이름
	 * @param arrayCntType
	 *            배열의 반복 횟수 지정 방식
	 * @param arrayCntValue
	 *            배열 반복 횟수
	 */
	public ArrayInfo(String arrayName, String arrayCntType, String arrayCntValue) throws IllegalArgumentException {
		if (null == arrayName) {			
			throw new IllegalArgumentException("the parmamter arrayName is null");
		}
		if (null == arrayCntType) {
			throw new IllegalArgumentException("the parmamter arrayCntType is null");
		}
		if (null == arrayCntValue) {
			throw new IllegalArgumentException("the parmamter arrayCntValue is null");
		}
		
		if (arrayName.length() < 2) {
			String errorMessage = String.format("the number[%d] of character of the parameter arrayName is less than two", arrayName.length());
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		if (arrayCntType.equals("direct")) {
			int arrayCount = -1;
			if (null != arrayCntValue) {
				try {
					arrayCount = Integer.parseInt(arrayCntValue);

				} catch (NumberFormatException num_e) {
					String errorMessage = new StringBuilder("fail to parses the string argument(=this array item[")
					.append(arrayName).append("]'s attribute 'cntvalue' value[")
					.append(arrayCntValue).append("]) as a signed decimal integer")
					.append("").toString();
					throw new IllegalArgumentException(errorMessage);
				}
			}

			if (arrayCount <= 0) {				
				String errorMessage = new StringBuilder("this array item[")
				.append(arrayName).append("]'s attribute 'cntvalue' value[")
				.append(arrayCntValue).append("] is less than or equals to zero")
				.append("").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		} else if (arrayCntType.equals("reference")) {
			/** nothing */
		} else {
			String errorMessage = new StringBuilder("this array item[")
			.append(arrayName).append("]'s attribute 'cnttype' value[")
			.append(arrayCntType).append("] is not an element of direction set[direct, reference]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.arrayName = arrayName;
		this.arrayFirstUpperName = arrayName.substring(0, 1).toUpperCase() + arrayName.substring(1);
		this.arrayCntType = arrayCntType;
		this.arrayCntValue = arrayCntValue;
	}

	/**
	 * 배열의 반복 횟수 지정 방식을 반환한다.
	 * 
	 * @return 배열의 반복 횟수 지정 방식
	 */
	public String getArrayCntType() {
		return arrayCntType;
	}

	/**
	 * 배열 반복 횟수를 반환한다.
	 * 
	 * @return 배열 반복 횟수
	 */
	public String getArrayCntValue() {
		return arrayCntValue;
	}

	/**
	 * 배열 이름을 반환한다.
	 * 
	 * @return 배열 이름
	 */
	public String getArrayName() {
		return arrayName;
	}
	
	
	@Override
	public String getFirstUpperItemName() {
		return arrayFirstUpperName;
	}
	
	public OrderedItemSet getOrderedItemSet() {
		return orderedItemSet;
	}

	/******************* AbstractItemInfo start ***********************/
	@Override
	public String getItemName() {
		return arrayName;
	}

	@Override
	public ItemInfoType getItemInfoType() {
		return ItemInfoType.ARRAY;
	}
	/******************* AbstractItemInfo end ***********************/
	
	@Override
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{ arrayName=[");
		strBuff.append(arrayName);
		strBuff.append("], arrayCntType=[");
		strBuff.append(arrayCntType);
		strBuff.append("], arrayCntValue=[");
		strBuff.append(arrayCntValue);
		strBuff.append("], {");		
		strBuff.append(orderedItemSet.toString());
		strBuff.append("}}");

		return strBuff.toString();
	}
}
