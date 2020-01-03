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

package kr.pe.codda.impl.message.BoardDetailRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardDetailRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardDetailResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		BoardDetailRes boardDetailRes = new BoardDetailRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardDetailRes");

		boardDetailRes.setBoardID((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setBoardName((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardName" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setBoardListType((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardListType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setBoardReplyPolicyType((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardReplyPolicyType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setBoardReplyPermssionType((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardReplyPermssionType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setBoardNo((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setGroupNo((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "groupNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setGroupSeq((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "groupSeq" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setParentNo((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "parentNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setDepth((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "depth" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setViewCount((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "viewCount" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setBoardSate((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "boardSate" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setVotes((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "votes" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setSubject((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "subject" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setContents((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "contents" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setFirstWriterID((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "firstWriterID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setFirstWriterNickname((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "firstWriterNickname" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setFirstWriterRole((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "firstWriterRole" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setFirstRegisteredDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "firstRegisteredDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setLastModifierID((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastModifierID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setLastModifierNickName((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastModifierNickName" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setLastModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setNextAttachedFileSeq((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "nextAttachedFileSeq" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setIsBoardPassword((java.lang.Boolean)
		singleItemDecoder.getValue(pathStack.peek()
			, "isBoardPassword" // itemName
			, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		boardDetailRes.setAttachedFileCnt((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "attachedFileCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int attachedFile$2ListSize = boardDetailRes.getAttachedFileCnt();
		if (attachedFile$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var attachedFile$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object attachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "attachedFile", attachedFile$2ListSize, receivedMiddleObject);
		java.util.List<BoardDetailRes.AttachedFile> attachedFile$2List = new java.util.ArrayList<BoardDetailRes.AttachedFile>();
		for (int i2=0; i2 < attachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i2).append("]").toString());
			Object attachedFile$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), attachedFile$2ArrayMiddleObject, i2);
			BoardDetailRes.AttachedFile attachedFile$2 = new BoardDetailRes.AttachedFile();
			attachedFile$2List.add(attachedFile$2);

			attachedFile$2.setAttachedFileSeq((Short)
			singleItemDecoder.getValue(pathStack.peek()
				, "attachedFileSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			attachedFile$2.setAttachedFileName((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "attachedFileName" // itemName
				, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			attachedFile$2.setAttachedFileSize((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "attachedFileSize" // itemName
				, kr.pe.codda.common.type.SingleItemType.LONG // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardDetailRes.setAttachedFileList(attachedFile$2List);

		boardDetailRes.setChildNodeCnt((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "childNodeCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int childNode$2ListSize = boardDetailRes.getChildNodeCnt();
		if (childNode$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var childNode$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object childNode$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "childNode", childNode$2ListSize, receivedMiddleObject);
		java.util.List<BoardDetailRes.ChildNode> childNode$2List = new java.util.ArrayList<BoardDetailRes.ChildNode>();
		for (int i2=0; i2 < childNode$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("ChildNode").append("[").append(i2).append("]").toString());
			Object childNode$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), childNode$2ArrayMiddleObject, i2);
			BoardDetailRes.ChildNode childNode$2 = new BoardDetailRes.ChildNode();
			childNode$2List.add(childNode$2);

			childNode$2.setBoardNo((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "boardNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setGroupSeq((Integer)
			singleItemDecoder.getValue(pathStack.peek()
				, "groupSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setParentNo((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "parentNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setDepth((Short)
			singleItemDecoder.getValue(pathStack.peek()
				, "depth" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setContents((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "contents" // itemName
				, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setVotes((Integer)
			singleItemDecoder.getValue(pathStack.peek()
				, "votes" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setBoardSate((Byte)
			singleItemDecoder.getValue(pathStack.peek()
				, "boardSate" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setFirstWriterID((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "firstWriterID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setFirstWriterNickname((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "firstWriterNickname" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setFirstWriterRole((Byte)
			singleItemDecoder.getValue(pathStack.peek()
				, "firstWriterRole" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setFirstRegisteredDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "firstRegisteredDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setLastModifierID((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastModifierID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setLastModifierNickName((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastModifierNickName" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setLastModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setNextAttachedFileSeq((Short)
			singleItemDecoder.getValue(pathStack.peek()
				, "nextAttachedFileSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setIsBoardPassword((java.lang.Boolean)
			singleItemDecoder.getValue(pathStack.peek()
				, "isBoardPassword" // itemName
				, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			childNode$2.setAttachedFileCnt((Integer)
			singleItemDecoder.getValue(pathStack.peek()
				, "attachedFileCnt" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childNode$2MiddleWritableObject));

			int attachedFile$3ListSize = childNode$2.getAttachedFileCnt();
			if (attachedFile$3ListSize < 0) {
				String errorMessage = new StringBuilder("the var attachedFile$3ListSize is less than zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object attachedFile$3ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "attachedFile", attachedFile$3ListSize, childNode$2MiddleWritableObject);
			java.util.List<BoardDetailRes.ChildNode.AttachedFile> attachedFile$3List = new java.util.ArrayList<BoardDetailRes.ChildNode.AttachedFile>();
			for (int i3=0; i3 < attachedFile$3ListSize; i3++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i3).append("]").toString());
				Object attachedFile$3MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), attachedFile$3ArrayMiddleObject, i3);
				BoardDetailRes.ChildNode.AttachedFile attachedFile$3 = new BoardDetailRes.ChildNode.AttachedFile();
				attachedFile$3List.add(attachedFile$3);

				attachedFile$3.setAttachedFileSeq((Short)
				singleItemDecoder.getValue(pathStack.peek()
					, "attachedFileSeq" // itemName
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$3MiddleWritableObject));

				attachedFile$3.setAttachedFileName((String)
				singleItemDecoder.getValue(pathStack.peek()
					, "attachedFileName" // itemName
					, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$3MiddleWritableObject));

				attachedFile$3.setAttachedFileSize((Long)
				singleItemDecoder.getValue(pathStack.peek()
					, "attachedFileSize" // itemName
					, kr.pe.codda.common.type.SingleItemType.LONG // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$3MiddleWritableObject));

				pathStack.pop();
			}

			childNode$2.setAttachedFileList(attachedFile$3List);

			pathStack.pop();
		}

		boardDetailRes.setChildNodeList(childNode$2List);

		pathStack.pop();

		return boardDetailRes;
	}
}