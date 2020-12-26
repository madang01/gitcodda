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

import java.nio.BufferUnderflowException;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.UnknownItemTypeException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.message.builder.info.MessageSingleItemTypeManger;
import kr.pe.codda.common.type.MessageSingleItemType;

/**
 * THB 프로토콜 단일 항목 타입의 디코더 추상화 클래스
 * @author Won Jonghoon
 *
 */
public abstract class AbstractTHBSingleItemTypeDecoder {
	/**
	 * 스트림 버퍼로 부터 파라미터들을 참조하여 단일 항목 값을 반환하는  추상화 메소드 
	 * @param itemTypeID 단일 항목 타입 식별자
	 * @param itemName 단일 항목 이름
	 * @param itemSize 단일 항목 크기, 단일 항목 타입이 배열 처럼 크기를 요구할 경우만 유효하다.
	 * @param nativeItemCharset 단일 항목 문자셋, 단일 항목 타입이 문자열일 경우만 유효하다.
	 * @param receivedMessageStreamBuffer 수신한 메시지 내용이 담긴 스트림 버퍼
	 * @return 스트림 버퍼로 부터 파라미터들을 참조하여 단일 항목 값
	 * @throws Exception 처리중 에러 발생시 던지는 예외
	 */
	abstract public Object getValue(int itemTypeID, String itemName, int itemSize,
			String nativeItemCharset, StreamBuffer receivedMessageStreamBuffer) throws Exception;
	

	/**
	 * 스트림 버퍼로 부터 추출한 항목 타입 식별자와 파라미터 'itemTypeID'(=기대한 항목 타입 식별자) 가 다른 경우 예외를 던진다.
	 * 
	 * @param itemTypeID 기대한 항목 타입 식별자
	 * @param itemName 항목 이름
	 * @param receivedMessageStreamBuffer 수신한 메시지 내용이 담긴 스트림 버퍼
	 * @throws BufferUnderflowException '스트림 버퍼'에서 버퍼 언더 플로우 발생시 던지는 예외
	 * @throws BodyFormatException 스트림 버퍼로 부터 추출한 항목 타입 식별자와 파라미터 'itemTypeID'(=기대한 항목 타입 식별자) 가 다른 경우 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에서 랩버퍼를 요구했지만 랩 버퍼가 없을때 던지는 예외
	 */
	protected void throwExceptionIfItemTypeIsDifferent(int itemTypeID, String itemName,
			StreamBuffer receivedMessageStreamBuffer) throws BufferUnderflowException, BodyFormatException, NoMoreWrapBufferException {
		int receivedItemTypeID = receivedMessageStreamBuffer.getUnsignedByte();
		if (itemTypeID != receivedItemTypeID) {
			
			String itemTypeName = "unknown";
			try {
				itemTypeName = MessageSingleItemTypeManger.getInstance().getItemTypeName(itemTypeID);
			} catch (UnknownItemTypeException e) {
			}
			
			String receivedItemTypeName = "unknown";
			try {
				receivedItemTypeName = MessageSingleItemTypeManger.getInstance().getItemTypeName(receivedItemTypeID);
			} catch (UnknownItemTypeException e) {
			}
			
			String errorMesssage = new StringBuilder()
					.append("this single item type[id:")
					.append(itemTypeID)
					.append(", name=")
					.append(itemTypeName)
					.append("][")
					.append(itemName)
					.append("] is different from the received item type[id:")
					.append(receivedItemTypeID)
					.append(", name:")
					.append(receivedItemTypeName)
					.append("]").toString();
			throw new BodyFormatException(errorMesssage);
		}
	}	
	
	/**
	 * @return 단일 항목 타입
	 */
	abstract public MessageSingleItemType getSingleItemType();
}
