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

package kr.pe.codda.impl.message.DocumentChangeHistoryReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * DocumentChangeHistoryReq message encoder
 * @author Won Jonghoon
 *
 */
public final class DocumentChangeHistoryReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		DocumentChangeHistoryReq documentChangeHistoryReq = (DocumentChangeHistoryReq)messageObj;
		encodeBody(documentChangeHistoryReq, singleItemEncoder, middleObjectToSend);
	}


	private void encodeBody(DocumentChangeHistoryReq documentChangeHistoryReq, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("DocumentChangeHistoryReq");


		singleItemEncoder.putValue(pathStack.peek(), "requestedUserID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, documentChangeHistoryReq.getRequestedUserID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, documentChangeHistoryReq.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "documentNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, documentChangeHistoryReq.getDocumentNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "pageNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, documentChangeHistoryReq.getPageNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "pageSize"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, documentChangeHistoryReq.getPageSize() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		pathStack.pop();
	}
}