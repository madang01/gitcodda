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
package kr.pe.codda.common.protocol.thb;

import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.type.SingleItemType;

/**
 * THB 단일 항목 디코더
 * @author "Won Jonghoon"
 *
 */
public class THBSingleItemDecoder implements SingleItemDecoderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	
	private THBSingleItemDecoderMatcherIF thbSingleItemDecoderMacher = null;
	
	public THBSingleItemDecoder(THBSingleItemDecoderMatcherIF thbSingleItemDecoderMacher) {
		if (null == thbSingleItemDecoderMacher) {
			throw new IllegalArgumentException("the parameter thbSingleItemDecoderMacher is null");
		}
		
		this.thbSingleItemDecoderMacher = thbSingleItemDecoderMacher;
	}	

	@Override
	public Object getValueFromReadableMiddleObject(String path, String itemName,
			SingleItemType singleItemType, int itemSize,
			String nativeItemCharset, Object readableMiddleObject) throws BodyFormatException {
		if (null == path) {
			throw new IllegalArgumentException("the parameter path is null");
		}
		
		if (null == itemName) {
			throw new IllegalArgumentException("the parameter itemName is null");
		}
		if (null == singleItemType) {
			throw new IllegalArgumentException("the parameter singleItemType is null");
		}
		
		if (null == readableMiddleObject) {
			throw new IllegalArgumentException("the parameter readableMiddleObject is null");
		}
		
		if (!(readableMiddleObject instanceof StreamBuffer)) {
			String errorMessage = new StringBuilder("the parameter readableMiddleObject's class[")
					.append(readableMiddleObject.getClass().getCanonicalName())
					.append("] is not a StreamBuffer class").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		int itemTypeID = singleItemType.getItemTypeID();
		String itemTypeName = singleItemType.getItemTypeName();
		
		StreamBuffer binaryInputStream = (StreamBuffer)readableMiddleObject;
		Object retObj = null;
		try {
			AbstractTHBSingleItemDecoder thbTypeSingleItemDecoder = thbSingleItemDecoderMacher.get(itemTypeID);
			
			retObj = thbTypeSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
		} catch(IllegalArgumentException e) {
			throw e;
		} catch(Exception | OutOfMemoryError e) {
			String errorMessage = new StringBuilder("fail to decode a single item value::")
					.append("{ path=[")
					.append(path)
					.append("], itemName=[")
					.append(itemName)
					.append("], itemType=[")
					.append(itemTypeName)
					.append("], itemSize=[")
					.append(itemSize)
					.append("], itemCharset=[")
					.append(nativeItemCharset)
					.append("] }, errmsg=[")
					.append(e.getMessage())
					.append("]").toString();
			
			log.log(Level.WARNING, errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}
		return retObj;
	}

	@Override
	public Object getArrayMiddleObjectFromReadableMiddleObject(String path, String arrayName,
			int arrayCntValue, Object readableMiddleObject)
			throws BodyFormatException {
		return readableMiddleObject;
	}

	@Override
	public Object getReadableMiddleObjFromArrayMiddleObject(String path, Object arrayObj, int inx	) throws BodyFormatException {
		return arrayObj;
	}	
	
	@Override
	public Object getGroupMiddleObjectFromReadableMiddleObject(String path, String groupName, Object readableMiddleObject)
			throws BodyFormatException {
		return readableMiddleObject;
	}
	
	@Override
	public void checkValid(Object readableMiddleObject) throws BodyFormatException {
		if (!(readableMiddleObject instanceof StreamBuffer)) {
			String errorMessage = new StringBuilder("the parameter readableMiddleObject's class[")
					.append(readableMiddleObject.getClass().getCanonicalName())
					.append("] is not a BinaryInputStreamIF class").toString();
			
			log.log(Level.WARNING, errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		StreamBuffer binaryInputStream = (StreamBuffer)readableMiddleObject;
		long remainingBytes = binaryInputStream.remaining();
		
		if (0 > remainingBytes) {
			String errorMessage = 
					new StringBuilder("a message was successfully extracted from the stream, but the remaing data[")
					.append(remainingBytes)
					.append("] exists").toString();
			throw new BodyFormatException(errorMessage);
		}
	}
}
