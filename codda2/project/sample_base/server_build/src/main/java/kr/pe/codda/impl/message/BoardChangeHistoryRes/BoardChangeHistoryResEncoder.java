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

package kr.pe.codda.impl.message.BoardChangeHistoryRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardChangeHistoryRes message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardChangeHistoryResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		BoardChangeHistoryRes boardChangeHistoryRes = (BoardChangeHistoryRes)messageObj;
		encodeBody(boardChangeHistoryRes, singleItemEncoder, middleObjectToSend);
	}


	private void encodeBody(BoardChangeHistoryRes boardChangeHistoryRes, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardChangeHistoryRes");


		singleItemEncoder.putValue(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardChangeHistoryRes.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "boardNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardChangeHistoryRes.getBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "boardListType"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, boardChangeHistoryRes.getBoardListType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "parentNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardChangeHistoryRes.getParentNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "groupNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardChangeHistoryRes.getGroupNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "boardChangeHistoryCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardChangeHistoryRes.getBoardChangeHistoryCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		java.util.List<BoardChangeHistoryRes.BoardChangeHistory> boardChangeHistory$2List = boardChangeHistoryRes.getBoardChangeHistoryList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == boardChangeHistory$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardChangeHistoryRes.getBoardChangeHistoryCnt()) {
				String errorMessage = new StringBuilder("the var boardChangeHistory$2List is null but the value referenced by the array size[boardChangeHistoryRes.getBoardChangeHistoryCnt()][").append(boardChangeHistoryRes.getBoardChangeHistoryCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int boardChangeHistory$2ListSize = boardChangeHistory$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardChangeHistoryRes.getBoardChangeHistoryCnt() != boardChangeHistory$2ListSize) {
				String errorMessage = new StringBuilder("the var boardChangeHistory$2ListSize[").append(boardChangeHistory$2ListSize).append("] is not same to the value referenced by the array size[boardChangeHistoryRes.getBoardChangeHistoryCnt()][").append(boardChangeHistoryRes.getBoardChangeHistoryCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object boardChangeHistory$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObject(pathStack.peek(), "boardChangeHistory", boardChangeHistory$2ListSize, middleObjectToSend);
			for (int i2=0; i2 < boardChangeHistory$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("BoardChangeHistory").append("[").append(i2).append("]").toString());
				Object boardChangeHistory$2MiddleWritableObject = singleItemEncoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), boardChangeHistory$2ArrayMiddleObject, i2);
				BoardChangeHistoryRes.BoardChangeHistory boardChangeHistory$2 = boardChangeHistory$2List.get(i2);

				singleItemEncoder.putValue(pathStack.peek(), "historySeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, boardChangeHistory$2.getHistorySeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardChangeHistory$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "subject"
					, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, boardChangeHistory$2.getSubject() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardChangeHistory$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "contents"
					, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
					, boardChangeHistory$2.getContents() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardChangeHistory$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "writerID"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, boardChangeHistory$2.getWriterID() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardChangeHistory$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "writerNickname"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, boardChangeHistory$2.getWriterNickname() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardChangeHistory$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "registeredDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, boardChangeHistory$2.getRegisteredDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardChangeHistory$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}