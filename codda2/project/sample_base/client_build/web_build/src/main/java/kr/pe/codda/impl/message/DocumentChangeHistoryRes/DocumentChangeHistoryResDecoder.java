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

package kr.pe.codda.impl.message.DocumentChangeHistoryRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * DocumentChangeHistoryRes message decoder
 * @author Won Jonghoon
 *
 */
public final class DocumentChangeHistoryResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		DocumentChangeHistoryRes documentChangeHistoryRes = new DocumentChangeHistoryRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("DocumentChangeHistoryRes");

		documentChangeHistoryRes.setDocumentNo((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "documentNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		documentChangeHistoryRes.setDocumentSate((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "documentSate" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		documentChangeHistoryRes.setPageNo((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "pageNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		documentChangeHistoryRes.setPageSize((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "pageSize" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		documentChangeHistoryRes.setTotal((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "total" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		documentChangeHistoryRes.setCnt((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int document$2ListSize = documentChangeHistoryRes.getCnt();
		if (document$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var document$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object document$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "document", document$2ListSize, receivedMiddleObject);
		java.util.List<DocumentChangeHistoryRes.Document> document$2List = new java.util.ArrayList<DocumentChangeHistoryRes.Document>();
		for (int i2=0; i2 < document$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Document").append("[").append(i2).append("]").toString());
			Object document$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), document$2ArrayMiddleObject, i2);
			DocumentChangeHistoryRes.Document document$2 = new DocumentChangeHistoryRes.Document();
			document$2List.add(document$2);

			document$2.setDocumentSeq((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "documentSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, document$2MiddleWritableObject));

			document$2.setFileName((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "fileName" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, document$2MiddleWritableObject));

			document$2.setSubject((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "subject" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, document$2MiddleWritableObject));

			document$2.setContents((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "contents" // itemName
				, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, document$2MiddleWritableObject));

			document$2.setRegisteredDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "registeredDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, document$2MiddleWritableObject));

			pathStack.pop();
		}

		documentChangeHistoryRes.setDocumentList(document$2List);

		pathStack.pop();

		return documentChangeHistoryRes;
	}
}