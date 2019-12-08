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

package kr.pe.codda.impl.message.BoardModifyReq;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardModifyReq message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardModifyReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardModifyReq");

		boardModifyReq.setRequestedUserID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "requestedUserID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyReq.setIp((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ip" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyReq.setBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyReq.setBoardNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyReq.setPwdHashBase64((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "pwdHashBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyReq.setSubject((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "subject" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyReq.setContents((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "contents" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyReq.setNextAttachedFileSeq((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "nextAttachedFileSeq" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyReq.setOldAttachedFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "oldAttachedFileCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int oldAttachedFile$2ListSize = boardModifyReq.getOldAttachedFileCnt();
		if (oldAttachedFile$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var oldAttachedFile$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object oldAttachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "oldAttachedFile", oldAttachedFile$2ListSize, middleReadableObject);
		java.util.List<BoardModifyReq.OldAttachedFile> oldAttachedFile$2List = new java.util.ArrayList<BoardModifyReq.OldAttachedFile>();
		for (int i2=0; i2 < oldAttachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("OldAttachedFile").append("[").append(i2).append("]").toString());
			Object oldAttachedFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), oldAttachedFile$2ArrayMiddleObject, i2);
			BoardModifyReq.OldAttachedFile oldAttachedFile$2 = new BoardModifyReq.OldAttachedFile();
			oldAttachedFile$2List.add(oldAttachedFile$2);

			oldAttachedFile$2.setAttachedFileSeq((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, oldAttachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardModifyReq.setOldAttachedFileList(oldAttachedFile$2List);

		boardModifyReq.setNewAttachedFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "newAttachedFileCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int newAttachedFile$2ListSize = boardModifyReq.getNewAttachedFileCnt();
		if (newAttachedFile$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var newAttachedFile$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object newAttachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "newAttachedFile", newAttachedFile$2ListSize, middleReadableObject);
		java.util.List<BoardModifyReq.NewAttachedFile> newAttachedFile$2List = new java.util.ArrayList<BoardModifyReq.NewAttachedFile>();
		for (int i2=0; i2 < newAttachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("NewAttachedFile").append("[").append(i2).append("]").toString());
			Object newAttachedFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), newAttachedFile$2ArrayMiddleObject, i2);
			BoardModifyReq.NewAttachedFile newAttachedFile$2 = new BoardModifyReq.NewAttachedFile();
			newAttachedFile$2List.add(newAttachedFile$2);

			newAttachedFile$2.setAttachedFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileName" // itemName
				, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, newAttachedFile$2MiddleWritableObject));

			newAttachedFile$2.setAttachedFileSize((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileSize" // itemName
				, kr.pe.codda.common.type.SingleItemType.LONG // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, newAttachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardModifyReq.setNewAttachedFileList(newAttachedFile$2List);

		pathStack.pop();

		return boardModifyReq;
	}
}