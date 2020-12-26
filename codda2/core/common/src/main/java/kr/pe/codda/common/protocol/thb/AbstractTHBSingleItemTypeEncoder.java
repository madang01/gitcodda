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

package kr.pe.codda.common.protocol.thb;

import java.nio.BufferOverflowException;

import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.type.MessageSingleItemType;

/**
 * THB 프로토콜 단일 항목 타입의 인코더 추상화 클래스
 * @author Won Jonghoon
 *
 */
public abstract class AbstractTHBSingleItemTypeEncoder {
	/**
	 * 파라미터 'nativeItemValue' 의 값을 파라미터 'messageStreamBufferToSend' 에 넣는 추상화 메소드
	 *  
	 * @param itemTypeID 단일 항목 타입 식별자
	 * @param itemName 단일 항목 이름
	 * @param nativeItemValue 단일 항목 값
	 * @param itemSize 단일 항목 크기, 단일 항목 타입이 배열 처럼 크기를 요구할 경우 유효하다.
	 * @param nativeItemCharset 문자셋, 단일 항목 타입이 문자열 계열일 경우 유효하다.
	 * @param messageStreamBufferToSend 보내고자 하는 메시지 내용을 담을 '스트림 버퍼'
	 * @throws Exception 처리중 에러 발생시 던지는 예외
	 */
	abstract public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
			String nativeItemCharset, StreamBuffer messageStreamBufferToSend)
			throws Exception;
	
	/**
	 * 파라미터 'itemTypeID'(=항목 타입 식별자) 을 파라미터 'messageStreamBufferToSend'(=보내고자 하는 메시지 내용을 담을 '스트림 버퍼') 에 저장한다.
	 *   
	 * @param itemTypeID 항목 타입 식별자
	 * @param messageStreamBufferToSend 보내고자 하는 메시지 내용을 담을 '스트림 버퍼'
	 * @throws BufferOverflowException '스트림 버퍼' 에서 버퍼 오버 플로우가 발생한 경우 던지는 예외
	 * @throws IllegalArgumentException 잘못된 파라미터가 있을때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에서 랩버퍼를 요구했지만 랩 버퍼가 없을때 던지는 예외
	 */
	protected void writeItemID(int itemTypeID, StreamBuffer messageStreamBufferToSend) throws BufferOverflowException, IllegalArgumentException, NoMoreWrapBufferException {
		messageStreamBufferToSend.putUnsignedByte(itemTypeID);
	}
	
	/**
	 * @return 단일 항목 타입
	 */
	abstract public MessageSingleItemType getSingleItemType();
}
