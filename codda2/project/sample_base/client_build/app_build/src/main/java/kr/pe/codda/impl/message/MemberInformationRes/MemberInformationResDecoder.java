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

package kr.pe.codda.impl.message.MemberInformationRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * MemberInformationRes message decoder
 * @author Won Jonghoon
 *
 */
public final class MemberInformationResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		MemberInformationRes memberInformationRes = new MemberInformationRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberInformationRes");

		memberInformationRes.setTargetUserID((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "targetUserID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setNickname((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "nickname" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setEmail((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "email" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setRole((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "role" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setState((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "state" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setRegisteredDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "registeredDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setLastNicknameModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastNicknameModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setLastEmailModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastEmailModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setLastPasswordModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastPasswordModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberInformationRes.setLastStateModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "lastStateModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		pathStack.pop();

		return memberInformationRes;
	}
}