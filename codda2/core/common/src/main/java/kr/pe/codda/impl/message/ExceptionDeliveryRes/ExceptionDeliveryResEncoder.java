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

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * ExceptionDeliveryRes message encoder
 * @author Won Jonghoon
 *
 */
public final class ExceptionDeliveryResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		ExceptionDeliveryRes exceptionDeliveryRes = (ExceptionDeliveryRes)messageObj;
		encodeBody(exceptionDeliveryRes, singleItemEncoder, middleObjectToSend);
	}


	private void encodeBody(ExceptionDeliveryRes exceptionDeliveryRes, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("ExceptionDeliveryRes");


		singleItemEncoder.putValue(pathStack.peek(), "errorPlace"
			, kr.pe.codda.common.type.SingleItemType.EXCEPTION_DELIVERY_ERROR_PLACE // itemType
			, exceptionDeliveryRes.getErrorPlace() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "errorType"
			, kr.pe.codda.common.type.SingleItemType.EXCEPTION_DELIVERY_ERROR_TYPE // itemType
			, exceptionDeliveryRes.getErrorType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "errorMessageID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, exceptionDeliveryRes.getErrorMessageID() // itemValue
			, -1 // itemSize
			, "ISO-8859-1" // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "errorReason"
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, exceptionDeliveryRes.getErrorReason() // itemValue
			, -1 // itemSize
			, "utf8" // nativeItemCharset
			, middleObjectToSend);

		pathStack.pop();
	}
}