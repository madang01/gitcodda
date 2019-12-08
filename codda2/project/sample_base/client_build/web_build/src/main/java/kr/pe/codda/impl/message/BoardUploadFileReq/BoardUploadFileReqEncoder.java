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

package kr.pe.codda.impl.message.BoardUploadFileReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardUploadFileReq message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardUploadFileReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardUploadFileReq boardUploadFileReq = (BoardUploadFileReq)messageObj;
		encodeBody(boardUploadFileReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardUploadFileReq boardUploadFileReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardUploadFileReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "requestedUserID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardUploadFileReq.getRequestedUserID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardUploadFileReq.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachId"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardUploadFileReq.getAttachId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "oldAttachedFileCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardUploadFileReq.getOldAttachedFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardUploadFileReq.OldAttachedFile> oldAttachedFile$2List = boardUploadFileReq.getOldAttachedFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == oldAttachedFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardUploadFileReq.getOldAttachedFileCnt()) {
				String errorMessage = new StringBuilder("the var oldAttachedFile$2List is null but the value referenced by the array size[boardUploadFileReq.getOldAttachedFileCnt()][").append(boardUploadFileReq.getOldAttachedFileCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int oldAttachedFile$2ListSize = oldAttachedFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardUploadFileReq.getOldAttachedFileCnt() != oldAttachedFile$2ListSize) {
				String errorMessage = new StringBuilder("the var oldAttachedFile$2ListSize[").append(oldAttachedFile$2ListSize).append("] is not same to the value referenced by the array size[boardUploadFileReq.getOldAttachedFileCnt()][").append(boardUploadFileReq.getOldAttachedFileCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object oldAttachedFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "oldAttachedFile", oldAttachedFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < oldAttachedFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("OldAttachedFile").append("[").append(i2).append("]").toString());
				Object oldAttachedFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), oldAttachedFile$2ArrayMiddleObject, i2);
				BoardUploadFileReq.OldAttachedFile oldAttachedFile$2 = oldAttachedFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, oldAttachedFile$2.getAttachSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, oldAttachedFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "newAttachedFileCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardUploadFileReq.getNewAttachedFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardUploadFileReq.NewAttachedFile> newAttachedFile$2List = boardUploadFileReq.getNewAttachedFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == newAttachedFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardUploadFileReq.getNewAttachedFileCnt()) {
				String errorMessage = new StringBuilder("the var newAttachedFile$2List is null but the value referenced by the array size[boardUploadFileReq.getNewAttachedFileCnt()][").append(boardUploadFileReq.getNewAttachedFileCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int newAttachedFile$2ListSize = newAttachedFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardUploadFileReq.getNewAttachedFileCnt() != newAttachedFile$2ListSize) {
				String errorMessage = new StringBuilder("the var newAttachedFile$2ListSize[").append(newAttachedFile$2ListSize).append("] is not same to the value referenced by the array size[boardUploadFileReq.getNewAttachedFileCnt()][").append(boardUploadFileReq.getNewAttachedFileCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object newAttachedFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "newAttachedFile", newAttachedFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < newAttachedFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("NewAttachedFile").append("[").append(i2).append("]").toString());
				Object newAttachedFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), newAttachedFile$2ArrayMiddleObject, i2);
				BoardUploadFileReq.NewAttachedFile newAttachedFile$2 = newAttachedFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileName"
					, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, newAttachedFile$2.getAttachedFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, newAttachedFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}