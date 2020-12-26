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

package kr.pe.codda.common.protocol;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.type.MessageSingleItemType;

/**
 * 단일 항목 디코더 인터페이스. 프로토콜별로 구현된다.
 * @author "Won Jonghoon"
 *
 */
public interface SingleItemDecoderIF {
	
	/**
	 * 지정한 중간 객체로 부터 값을 얻어온다.
	 * @param path 경로
	 * @param itemName 항목 이름
	 * @param singleItemType 항목 타입
	 * @param itemSize 항목 크기
	 * @param nativeItemCharset 문자셋
	 * @param middleObject 중간 객체
	 * @return 지정한 중간 객체로 부터 값
	 * @throws BodyFormatException 프로토콜 규약을 어겼을 경우 던지는 예외
	 */
	public Object getValue(String path, String itemName, MessageSingleItemType singleItemType, 
			int itemSize, String nativeItemCharset, Object middleObject)
			throws BodyFormatException;
	
	/**
	 * <pre>
	 * "중간 다리 역활 읽기 객체" 로 부터 배열 정보를 가지고 배열 객체를 얻는다.
	 * 
	 * 참고) "배열 크기를 지정하는 방식"은 2가지가 있으며 첫번째 직접(direct)은 "배열 크기 값" 에 배열 크기를 정하는 방식이며 
	 * 마지막 두번째 참조(reference)은  "배열 크기 값"에 간접 참조하는 변수명의 값으로 배열 크기를 지정하는 방식이다.
	 * </pre>  
	 * @param path 메시지 항목의 경로, ex) AllDataType.memberList[1]
	 * @param arrayName 배열 이름 
	 * @param arrayCntValue 배열 크기 값
	 * @param middleObject 중간 다리 역활 읽기 객체
	 * @return  "중간 다리 역활 읽기 객체" 로 부터 배열 정보를 가지고 배열 객체
	 * @throws BodyFormatException  프로토콜 규약을 어겼을 경우 던지는 예외
	 */
	public Object getArrayMiddleObject(String path, String arrayName, int arrayCntValue, Object middleObject)
			throws BodyFormatException;
	
	/**
	 * "배열 객체" 로 부터 지정된 인덱스에 있는 객체를 반환한다.
	 * @param path 메시지 항목의 경로, ex) AllDataType.memberList[1]
	 * @param arrayMiddleObject 배열 객체
	 * @param inx 배열 인덱스
	 * @return "배열 객체" 로 부터 지정된 인덱스에 있는 객체
	 * @throws BodyFormatException 프로토콜 규약을 어겼을 경우 던지는 예외
	 */
	public Object getMiddleObjectFromArrayMiddleObject(String path, Object arrayMiddleObject, int inx) throws BodyFormatException;
	
	
	/**
	 * 그룹 중간 객체를 반환한다.
	 * @param path 경로
	 * @param groupName 그룹이름
	 * @param middleObject 중간 객체
	 * @return 그룹 중간 객체
	 * @throws BodyFormatException 프로토콜 규약을 어겼을 경우 던지는 예외
	 */
	public Object getGroupMiddleObject(String path, String groupName, Object middleObject)
			throws BodyFormatException;
	
	/**
	 * 중간 객체의 유효성을 검사한다. 이 메소드는 메시지 디코딩이 끝난 후에 호출된다. DHB, THB 프로토콜 같은 경우 남아 있는 데이터가 있는지 검사하기 위한 용도이다.
	 * @param middleObject 중간 객체
	 * @throws BodyFormatException 프로토콜 규약을 어겼을 경우 던지는 예외
	 */
	public void checkValid(Object middleObject) throws BodyFormatException;
}



