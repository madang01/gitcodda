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

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * DocumentListRes message decoder
 * @author Won Jonghoon
 *
 */
public final class DocumentListResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		DocumentListRes documentListRes = new DocumentListRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("DocumentListRes");

		documentListRes.setPageNo((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "pageNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		documentListRes.setPageSize((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "pageSize" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		documentListRes.setTotal((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "total" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		documentListRes.setCnt((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int document$2ListSize = documentListRes.getCnt();
		if (document$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var document$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object document$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "document", document$2ListSize, receivedMiddleObject);
		java.util.List<DocumentListRes.Document> document$2List = new java.util.ArrayList<DocumentListRes.Document>();
		for (int i2=0; i2 < document$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Document").append("[").append(i2).append("]").toString());
			Object document$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), document$2ArrayMiddleObject, i2);
			DocumentListRes.Document document$2 = new DocumentListRes.Document();
			document$2List.add(document$2);

			document$2.setDocumentNo((Long)
			singleItemDecoder.getValue(pathStack.peek()
				, "documentNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, document$2MiddleWritableObject));

			document$2.setDocumentSate((Byte)
			singleItemDecoder.getValue(pathStack.peek()
				, "documentSate" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
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

			document$2.setLastModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, document$2MiddleWritableObject));

			pathStack.pop();
		}

		documentListRes.setDocumentList(document$2List);

		pathStack.pop();

		return documentListRes;
	}
}