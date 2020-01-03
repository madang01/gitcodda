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

package kr.pe.codda.impl.message.BoardListRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardListRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardListResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		BoardListRes boardListRes = new BoardListRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardListRes");

		boardListRes.setBoardID((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardListRes.setBoardName((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardName" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardListRes.setBoardListType((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardListType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardListRes.setBoardWritePermissionType((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardWritePermissionType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardListRes.setPageNo((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "pageNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardListRes.setPageSize((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "pageSize" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardListRes.setTotal((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "total" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardListRes.setCnt((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int board$2ListSize = boardListRes.getCnt();
		if (board$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var board$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object board$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "board", board$2ListSize, receivedMiddleObject);
		java.util.List<BoardListRes.Board> board$2List = new java.util.ArrayList<BoardListRes.Board>();
		for (int i2=0; i2 < board$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Board").append("[").append(i2).append("]").toString());
			Object board$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), board$2ArrayMiddleObject, i2);
			BoardListRes.Board board$2 = new BoardListRes.Board();
			board$2List.add(board$2);

			board$2.setBoardNo((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "boardNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setGroupNo((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "groupNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setGroupSeq((Integer)
			singleItemDecoder.getValue(pathStack.peek()
				, "groupSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setParentNo((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "parentNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setDepth((Short)
			singleItemDecoder.getValue(pathStack.peek()
				, "depth" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setWriterID((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "writerID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setWriterNickname((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "writerNickname" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setViewCount((Integer)
			singleItemDecoder.getValue(pathStack.peek()
				, "viewCount" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setBoardSate((Byte)
			singleItemDecoder.getValue(pathStack.peek()
				, "boardSate" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setRegisteredDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "registeredDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setVotes((Integer)
			singleItemDecoder.getValue(pathStack.peek()
				, "votes" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setSubject((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "subject" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setLastModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			pathStack.pop();
		}

		boardListRes.setBoardList(board$2List);

		pathStack.pop();

		return boardListRes;
	}
}