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

package kr.pe.codda.impl.message.ArraySiteMenuRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * ArraySiteMenuRes message decoder
 * @author Won Jonghoon
 *
 */
public final class ArraySiteMenuResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		ArraySiteMenuRes arraySiteMenuRes = new ArraySiteMenuRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("ArraySiteMenuRes");

		arraySiteMenuRes.setCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int menu$2ListSize = arraySiteMenuRes.getCnt();
		if (menu$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var menu$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object menu$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "menu", menu$2ListSize, middleReadableObject);
		java.util.List<ArraySiteMenuRes.Menu> menu$2List = new java.util.ArrayList<ArraySiteMenuRes.Menu>();
		for (int i2=0; i2 < menu$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Menu").append("[").append(i2).append("]").toString());
			Object menu$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), menu$2ArrayMiddleObject, i2);
			ArraySiteMenuRes.Menu menu$2 = new ArraySiteMenuRes.Menu();
			menu$2List.add(menu$2);

			menu$2.setMenuNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "menuNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, menu$2MiddleWritableObject));

			menu$2.setParentNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "parentNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, menu$2MiddleWritableObject));

			menu$2.setDepth((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "depth" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, menu$2MiddleWritableObject));

			menu$2.setOrderSeq((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "orderSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, menu$2MiddleWritableObject));

			menu$2.setMenuName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "menuName" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, menu$2MiddleWritableObject));

			menu$2.setLinkURL((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "linkURL" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, menu$2MiddleWritableObject));

			pathStack.pop();
		}

		arraySiteMenuRes.setMenuList(menu$2List);

		pathStack.pop();

		return arraySiteMenuRes;
	}
}