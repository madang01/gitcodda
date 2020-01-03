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

package kr.pe.codda.impl.message.MemberSearchRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * MemberSearchRes message decoder
 * @author Won Jonghoon
 *
 */
public final class MemberSearchResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		MemberSearchRes memberSearchRes = new MemberSearchRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberSearchRes");

		memberSearchRes.setMemberState((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "memberState" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberSearchRes.setSearchID((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "searchID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberSearchRes.setFromDateString((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "fromDateString" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberSearchRes.setToDateString((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "toDateString" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberSearchRes.setPageNo((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "pageNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberSearchRes.setPageSize((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "pageSize" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberSearchRes.setIsNextPage((java.lang.Boolean)
		singleItemDecoder.getValue(pathStack.peek()
			, "isNextPage" // itemName
			, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		memberSearchRes.setCnt((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int member$2ListSize = memberSearchRes.getCnt();
		if (member$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var member$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object member$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "member", member$2ListSize, receivedMiddleObject);
		java.util.List<MemberSearchRes.Member> member$2List = new java.util.ArrayList<MemberSearchRes.Member>();
		for (int i2=0; i2 < member$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i2).append("]").toString());
			Object member$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), member$2ArrayMiddleObject, i2);
			MemberSearchRes.Member member$2 = new MemberSearchRes.Member();
			member$2List.add(member$2);

			member$2.setUserID((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "userID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setNickname((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "nickname" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setPasswordFailCount((Short)
			singleItemDecoder.getValue(pathStack.peek()
				, "passwordFailCount" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setRegisteredDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "registeredDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setLastNicknameModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastNicknameModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setLastEmailModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastEmailModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setLastPasswordModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastPasswordModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setLastStateModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValue(pathStack.peek()
				, "lastStateModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			pathStack.pop();
		}

		memberSearchRes.setMemberList(member$2List);

		pathStack.pop();

		return memberSearchRes;
	}
}