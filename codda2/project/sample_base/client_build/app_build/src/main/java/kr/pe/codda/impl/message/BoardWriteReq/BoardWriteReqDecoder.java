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

package kr.pe.codda.impl.message.BoardWriteReq;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardWriteReq message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardWriteReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardWriteReq");

		boardWriteReq.setRequestedUserID((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "requestedUserID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardWriteReq.setIp((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "ip" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardWriteReq.setBoardID((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardWriteReq.setPwdHashBase64((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "pwdHashBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardWriteReq.setSubject((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "subject" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardWriteReq.setContents((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "contents" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardWriteReq.setNewAttachedFileCnt((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "newAttachedFileCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int newAttachedFile$2ListSize = boardWriteReq.getNewAttachedFileCnt();
		if (newAttachedFile$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var newAttachedFile$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object newAttachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "newAttachedFile", newAttachedFile$2ListSize, receivedMiddleObject);
		java.util.List<BoardWriteReq.NewAttachedFile> newAttachedFile$2List = new java.util.ArrayList<BoardWriteReq.NewAttachedFile>();
		for (int i2=0; i2 < newAttachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("NewAttachedFile").append("[").append(i2).append("]").toString());
			Object newAttachedFile$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), newAttachedFile$2ArrayMiddleObject, i2);
			BoardWriteReq.NewAttachedFile newAttachedFile$2 = new BoardWriteReq.NewAttachedFile();
			newAttachedFile$2List.add(newAttachedFile$2);

			newAttachedFile$2.setAttachedFileName((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "attachedFileName" // itemName
				, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, newAttachedFile$2MiddleWritableObject));

			newAttachedFile$2.setAttachedFileSize((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "attachedFileSize" // itemName
				, kr.pe.codda.common.type.SingleItemType.LONG // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, newAttachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardWriteReq.setNewAttachedFileList(newAttachedFile$2List);

		pathStack.pop();

		return boardWriteReq;
	}
}