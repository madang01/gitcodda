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

package kr.pe.codda.impl.message.DownloadImageRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * DownloadImageRes message decoder
 * @author Won Jonghoon
 *
 */
public final class DownloadImageResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		DownloadImageRes downloadImageRes = new DownloadImageRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("DownloadImageRes");

		downloadImageRes.setYyyyMMdd((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "yyyyMMdd" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		downloadImageRes.setDaySequence((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "daySequence" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		downloadImageRes.setImageFileName((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "imageFileName" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		downloadImageRes.setFileSize((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "fileSize" // itemName
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		pathStack.pop();

		return downloadImageRes;
	}
}