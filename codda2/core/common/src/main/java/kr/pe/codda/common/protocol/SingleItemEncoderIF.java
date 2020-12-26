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
 * 단일 항목 인코더 인터페이스. 프로토콜별로 구현된다.
 * @author "Won Jonghoon"
 *
 */
public interface SingleItemEncoderIF {
	
	/**
	 * 중간 객체에 값을 저장한다. 
	 * @param path 값이 저장되는 경로명
	 * @param itemName 항목 이름
	 * @param singleItemType 항목 타입
	 * @param itemValue 항목 값
	 * @param itemSize 항목 크기
	 * @param nativeItemCharset 문자셋
	 * @param middleObject 값을 저장할 중간 객체
	 * @throws Exception 처리중 에러 발생시 던지는 예외
	 */
	public void putValue(String path, String itemName, MessageSingleItemType singleItemType, 
			Object itemValue, int itemSize, String nativeItemCharset, Object middleObject)
			throws Exception;
	
	/**
	 * 배열 중간 객체를 얻어 온다.
	 * @param path 경로
	 * @param arrayName 배열 이름
	 * @param arrayCntValue 배열 원소 갯수
	 * @param middleObject 배열을 얻어올 중간 객체
	 * @return 배열 중간 객체
	 * @throws BodyFormatException 프로토콜 규약을 어겼을 경우 던지는 예외
	 */
	public Object getArrayMiddleObject(String path, String arrayName,
			int arrayCntValue, Object middleObject)
			throws BodyFormatException;
			
	/**
	 * "배열 중간 객체" 로 부터 지정된 인덱스에 있는 원소인 '중간 객체'를 반환한다.
	 * @param path 메시지 항목의 경로, ex) AllDataType.memberList[1]
	 * @param arrayMiddleObj 배열 객체
	 * @param inx 배열 인덱스
	 * @return "배열 객체" 로 부터 지정된 인덱스에 있는 객체
	 * @throws BodyFormatException "배열 객체" 로 부터 지정된 인덱스에 있는 객체를 반환할때 에러 발생시 던지는 예외
	 */
	public Object getMiddleObjectFromArrayMiddleObject(String path, Object arrayMiddleObj, int inx) throws BodyFormatException;
	
	/**
	 * 그룹 중간 객체를 얻어온다. 참고 : 현재 베타 테스트중
	 * @param path 경로
	 * @param groupName 그룹 이름
	 * @param middleObject 중간 객체
	 * @return 그룹 중간 객체
	 * @throws BodyFormatException 프로토콜 규약을 어겼을 경우 던지는 예외
	 */
	public Object getGroupMiddleObject(String path, String groupName, Object middleObject)
			throws BodyFormatException;
}



