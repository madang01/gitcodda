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

package kr.pe.codda.impl.message.MemberAllInformationRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * MemberAllInformationRes message decoder
 * @author Won Jonghoon
 *
 */
public final class MemberAllInformationResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		MemberAllInformationRes memberAllInformationRes = new MemberAllInformationRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberAllInformationRes");

		memberAllInformationRes.setNickname((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "nickname" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setEmail((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "email" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setRole((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "role" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setState((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "state" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setPasswordFailedCount((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "passwordFailedCount" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setRegisteredDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "registeredDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setLastNicknameModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastNicknameModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setLastEmailModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastEmailModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setLastPasswordModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastPasswordModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberAllInformationRes.setLastStateModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastStateModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		pathStack.pop();

		return memberAllInformationRes;
	}
}