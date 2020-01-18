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

package kr.pe.codda.impl.message.DocumentListRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * DocumentListRes message encoder
 * @author Won Jonghoon
 *
 */
public final class DocumentListResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		DocumentListRes documentListRes = (DocumentListRes)messageObj;
		encodeBody(documentListRes, singleItemEncoder, middleObjectToSend);
	}


	private void encodeBody(DocumentListRes documentListRes, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("DocumentListRes");


		singleItemEncoder.putValue(pathStack.peek(), "pageNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, documentListRes.getPageNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "pageSize"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, documentListRes.getPageSize() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "total"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, documentListRes.getTotal() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, documentListRes.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		java.util.List<DocumentListRes.Document> document$2List = documentListRes.getDocumentList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == document$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != documentListRes.getCnt()) {
				String errorMessage = new StringBuilder("the var document$2List is null but the value referenced by the array size[documentListRes.getCnt()][").append(documentListRes.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int document$2ListSize = document$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (documentListRes.getCnt() != document$2ListSize) {
				String errorMessage = new StringBuilder("the var document$2ListSize[").append(document$2ListSize).append("] is not same to the value referenced by the array size[documentListRes.getCnt()][").append(documentListRes.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object document$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObject(pathStack.peek(), "document", document$2ListSize, middleObjectToSend);
			for (int i2=0; i2 < document$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Document").append("[").append(i2).append("]").toString());
				Object document$2MiddleWritableObject = singleItemEncoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), document$2ArrayMiddleObject, i2);
				DocumentListRes.Document document$2 = document$2List.get(i2);

				singleItemEncoder.putValue(pathStack.peek(), "documentNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, document$2.getDocumentNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, document$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "documentSate"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, document$2.getDocumentSate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, document$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "fileName"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, document$2.getFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, document$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "subject"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, document$2.getSubject() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, document$2MiddleWritableObject);

				singleItemEncoder.putValue(pathStack.peek(), "lastModifiedDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, document$2.getLastModifiedDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, document$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}