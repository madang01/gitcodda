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

package kr.pe.codda.impl.message.ExceptionDeliveryRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * ExceptionDeliveryRes message decoder
 * @author Won Jonghoon
 *
 */
public final class ExceptionDeliveryResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		ExceptionDeliveryRes exceptionDeliveryRes = new ExceptionDeliveryRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("ExceptionDeliveryRes");

		exceptionDeliveryRes.setErrorPlace((kr.pe.codda.common.type.ExceptionDelivery.ErrorPlace)
		singleItemDecoder.getValue(pathStack.peek()
			, "errorPlace" // itemName
			, kr.pe.codda.common.type.SingleItemType.EXCEPTION_DELIVERY_ERROR_PLACE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		exceptionDeliveryRes.setErrorType((kr.pe.codda.common.type.ExceptionDelivery.ErrorType)
		singleItemDecoder.getValue(pathStack.peek()
			, "errorType" // itemName
			, kr.pe.codda.common.type.SingleItemType.EXCEPTION_DELIVERY_ERROR_TYPE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		exceptionDeliveryRes.setErrorMessageID((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "errorMessageID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, "ISO-8859-1" // nativeItemCharset
			, receivedMiddleObject));

		exceptionDeliveryRes.setErrorReason((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "errorReason" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, "utf8" // nativeItemCharset
			, receivedMiddleObject));

		pathStack.pop();

		return exceptionDeliveryRes;
	}
}