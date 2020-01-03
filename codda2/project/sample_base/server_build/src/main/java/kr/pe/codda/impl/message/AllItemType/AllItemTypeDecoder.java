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

package kr.pe.codda.impl.message.AllItemType;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * AllItemType message decoder
 * @author Won Jonghoon
 *
 */
public final class AllItemTypeDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object receivedMiddleObject) throws BodyFormatException {
		AllItemType allItemType = new AllItemType();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("AllItemType");

		allItemType.setByteVar1((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "byteVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setByteVar2((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "byteVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setByteVar3((Byte)
		singleItemDecoder.getValue(pathStack.peek()
			, "byteVar3" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedByteVar1((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedByteVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedByteVar2((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedByteVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedByteVar3((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedByteVar3" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setShortVar1((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "shortVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setShortVar2((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "shortVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setShortVar3((Short)
		singleItemDecoder.getValue(pathStack.peek()
			, "shortVar3" // itemName
			, kr.pe.codda.common.type.SingleItemType.SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedShortVar1((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedShortVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedShortVar2((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedShortVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedShortVar3((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedShortVar3" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setIntVar1((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "intVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setIntVar2((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "intVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setIntVar3((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "intVar3" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedIntVar1((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedIntVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedIntVar2((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedIntVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setUnsignedIntVar3((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "unsignedIntVar3" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setLongVar1((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "longVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setLongVar2((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "longVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setLongVar3((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "longVar3" // itemName
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setStrVar1((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "strVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setStrVar2((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "strVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setStrVar3((String)
		singleItemDecoder.getValue(pathStack.peek()
			, "strVar3" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setBytesVar1((byte[])
		singleItemDecoder.getValue(pathStack.peek()
			, "bytesVar1" // itemName
			, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_BYTES // itemType
			, 7 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setBytesVar2((byte[])
		singleItemDecoder.getValue(pathStack.peek()
			, "bytesVar2" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_VARIABLE_LENGTH_BYTES // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setSqldate((java.sql.Date)
		singleItemDecoder.getValue(pathStack.peek()
			, "sqldate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_DATE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setSqltimestamp((java.sql.Timestamp)
		singleItemDecoder.getValue(pathStack.peek()
			, "sqltimestamp" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setIsFalse((java.lang.Boolean)
		singleItemDecoder.getValue(pathStack.peek()
			, "isFalse" // itemName
			, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setIsTrue((java.lang.Boolean)
		singleItemDecoder.getValue(pathStack.peek()
			, "isTrue" // itemName
			, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		allItemType.setCnt((Integer)
		singleItemDecoder.getValue(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		int member$2ListSize = allItemType.getCnt();
		if (member$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var member$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object member$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "member", member$2ListSize, receivedMiddleObject);
		java.util.List<AllItemType.Member> member$2List = new java.util.ArrayList<AllItemType.Member>();
		for (int i2=0; i2 < member$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i2).append("]").toString());
			Object member$2MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), member$2ArrayMiddleObject, i2);
			AllItemType.Member member$2 = new AllItemType.Member();
			member$2List.add(member$2);

			member$2.setMemberID((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "memberID" // itemName
				, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
				, 30 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setMemberName((String)
			singleItemDecoder.getValue(pathStack.peek()
				, "memberName" // itemName
				, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
				, 30 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setCnt((Integer)
			singleItemDecoder.getValue(pathStack.peek()
				, "cnt" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			int item$3ListSize = member$2.getCnt();
			if (item$3ListSize < 0) {
				String errorMessage = new StringBuilder("the var item$3ListSize is less than zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object item$3ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "item", item$3ListSize, member$2MiddleWritableObject);
			java.util.List<AllItemType.Member.Item> item$3List = new java.util.ArrayList<AllItemType.Member.Item>();
			for (int i3=0; i3 < item$3ListSize; i3++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Item").append("[").append(i3).append("]").toString());
				Object item$3MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), item$3ArrayMiddleObject, i3);
				AllItemType.Member.Item item$3 = new AllItemType.Member.Item();
				item$3List.add(item$3);

				item$3.setItemID((String)
				singleItemDecoder.getValue(pathStack.peek()
					, "itemID" // itemName
					, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
					, 30 // itemSize
					, null // nativeItemCharset
					, item$3MiddleWritableObject));

				item$3.setItemName((String)
				singleItemDecoder.getValue(pathStack.peek()
					, "itemName" // itemName
					, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
					, 30 // itemSize
					, null // nativeItemCharset
					, item$3MiddleWritableObject));

				item$3.setItemCnt((Integer)
				singleItemDecoder.getValue(pathStack.peek()
					, "itemCnt" // itemName
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, item$3MiddleWritableObject));

				int subItem$4ListSize = item$3.getItemCnt();
				if (subItem$4ListSize < 0) {
					String errorMessage = new StringBuilder("the var subItem$4ListSize is less than zero").toString();
					throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
				}

				Object subItem$4ArrayMiddleObject = singleItemDecoder.getArrayMiddleObject(pathStack.peek(), "subItem", subItem$4ListSize, item$3MiddleWritableObject);
				java.util.List<AllItemType.Member.Item.SubItem> subItem$4List = new java.util.ArrayList<AllItemType.Member.Item.SubItem>();
				for (int i4=0; i4 < subItem$4ListSize; i4++) {
					pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("SubItem").append("[").append(i4).append("]").toString());
					Object subItem$4MiddleWritableObject= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek(), subItem$4ArrayMiddleObject, i4);
					AllItemType.Member.Item.SubItem subItem$4 = new AllItemType.Member.Item.SubItem();
					subItem$4List.add(subItem$4);

					subItem$4.setSubItemID((String)
					singleItemDecoder.getValue(pathStack.peek()
						, "subItemID" // itemName
						, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
						, -1 // itemSize
						, null // nativeItemCharset
						, subItem$4MiddleWritableObject));

					subItem$4.setSubItemName((String)
					singleItemDecoder.getValue(pathStack.peek()
						, "subItemName" // itemName
						, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
						, -1 // itemSize
						, null // nativeItemCharset
						, subItem$4MiddleWritableObject));

					subItem$4.setItemCnt((Integer)
					singleItemDecoder.getValue(pathStack.peek()
						, "itemCnt" // itemName
						, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
						, -1 // itemSize
						, null // nativeItemCharset
						, subItem$4MiddleWritableObject));

					pathStack.pop();
				}

				item$3.setSubItemList(subItem$4List);

				pathStack.pop();
			}

			member$2.setItemList(item$3List);

			pathStack.pop();
		}

		allItemType.setMemberList(member$2List);

		allItemType.setLongVar4((Long)
		singleItemDecoder.getValue(pathStack.peek()
			, "longVar4" // itemName
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, receivedMiddleObject));

		pathStack.pop();

		return allItemType;
	}
}