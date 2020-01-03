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

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardChangeHistoryRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardChangeHistoryResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		BoardChangeHistoryRes boardChangeHistoryRes = new BoardChangeHistoryRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardChangeHistoryRes");

		boardChangeHistoryRes.setBoardID((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardChangeHistoryRes.setBoardNo((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardChangeHistoryRes.setBoardListType((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardListType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardChangeHistoryRes.setParentNo((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "parentNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardChangeHistoryRes.setGroupNo((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "groupNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardChangeHistoryRes.setBoardChangeHistoryCnt((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardChangeHistoryCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int boardChangeHistory$2ListSize = boardChangeHistoryRes.getBoardChangeHistoryCnt();
		if (boardChangeHistory$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var boardChangeHistory$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object boardChangeHistory$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "boardChangeHistory", boardChangeHistory$2ListSize, receivedMiddleObject);
		java.util.List<BoardChangeHistoryRes.BoardChangeHistory> boardChangeHistory$2List = new java.util.ArrayList<BoardChangeHistoryRes.BoardChangeHistory>();
		for (int i2=0; i2 < boardChangeHistory$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("BoardChangeHistory").append("[").append(i2).append("]").toString());
			Object boardChangeHistory$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), boardChangeHistory$2ArrayMiddleObject, i2);
			BoardChangeHistoryRes.BoardChangeHistory boardChangeHistory$2 = new BoardChangeHistoryRes.BoardChangeHistory();
			boardChangeHistory$2List.add(boardChangeHistory$2);

			boardChangeHistory$2.setHistorySeq((Short)
			singleItemDecoder.getValue(pathStack.peek()
				, "historySeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardChangeHistory$2MiddleWritableObject));

			boardChangeHistory$2.setSubject((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "subject" // itemName
				, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardChangeHistory$2MiddleWritableObject));

			boardChangeHistory$2.setContents((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "contents" // itemName
				, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardChangeHistory$2MiddleWritableObject));

			boardChangeHistory$2.setWriterID((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "writerID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardChangeHistory$2MiddleWritableObject));

			boardChangeHistory$2.setWriterNickname((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "writerNickname" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardChangeHistory$2MiddleWritableObject));

			boardChangeHistory$2.setRegisteredDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "registeredDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardChangeHistory$2MiddleWritableObject));

			pathStack.pop();
		}

		boardChangeHistoryRes.setBoardChangeHistoryList(boardChangeHistory$2List);

		pathStack.pop();

		return boardChangeHistoryRes;
	}
}