/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package kr.pe.codda.common.protocol.thb;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.common.type.SingleItemType;

public class THBSingleItemDecoderMatcher implements THBSingleItemDecoderMatcherIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private CharsetDecoder streamCharsetDecoder = null;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	private final AbstractTHBSingleItemDecoder[] thbSingleItemDecoderList = new AbstractTHBSingleItemDecoder[] { 
			new THBExceptionDeliveryErrorPlaceSingleItemDecoder(), new THBExceptionDeliveryErrorTypeSingleItemDecoder(),
			new THBByteSingleItemDecoder(), new THBUnsignedByteSingleItemDecoder(), 
			new THBShortSingleItemDecoder(), new THBUnsignedShortSingleItemDecoder(),
			new THBIntSingleItemDecoder(), new THBUnsignedIntSingleItemDecoder(), 
			new THBLongSingleItemDecoder(), new THBUBPascalStringSingleItemDecoder(),
			new THBUSPascalStringSingleItemDecoder(), new THBSIPascalStringSingleItemDecoder(), 
			new THBFixedLengthStringSingleItemDecoder(), new THBUBVariableLengthBytesSingleItemDecoder(), 
			new THBUSVariableLengthBytesSingleItemDecoder(), new THBSIVariableLengthBytesSingleItemDecoder(), 
			new THBFixedLengthBytesSingleItemDecoder(), 
			new THBJavaSqlDateSingleItemDecoder(), new THBJavaSqlTimestampSingleItemDecoder(),
			new THBBooleanSingleItemDecoder()
	};
	
	
	public THBSingleItemDecoderMatcher(CharsetDecoder streamCharsetDecoder) {
		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.streamCodingErrorActionOnMalformedInput = streamCharsetDecoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = streamCharsetDecoder.unmappableCharacterAction();
		
		checkValidTHBSingleItemDecoderList();
	}
	
	private void checkValidTHBSingleItemDecoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (thbSingleItemDecoderList.length != singleItemTypes.length) {
			String errorMessage = new StringBuilder()
					.append("the var thbSingleItemDecoderList.length[")
					.append(thbSingleItemDecoderList.length)
					.append("] is not differnet from the array var singleItemTypes.length[")
					.append(singleItemTypes.length)
					.append("]").toString();
			
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = thbSingleItemDecoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				String errorMessage = new StringBuilder()
						.append("the var thbSingleItemDecoderList[")
						.append(i)
						.append("'s SingleItemType[")
						.append(actualSingleItemType.toString())
						.append("] is not the expected SingleItemType[")
						.append(expectedSingleItemType.toString())
						.append("]").toString();
				
				log.log(Level.SEVERE, errorMessage);
				System.exit(1);
			}
		}
	}

	private final class THBExceptionDeliveryErrorPlaceSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return ExceptionDelivery.ErrorPlace.valueOf(binaryInputStream.getByte());
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.EXCEPTION_DELIVERY_ERROR_PLACE;
		}
	}	
	
	
	private final class THBExceptionDeliveryErrorTypeSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return ExceptionDelivery.ErrorType.valueOf(binaryInputStream.getByte());
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.EXCEPTION_DELIVERY_ERROR_TYPE;
		}
	}
	
	
	/** THB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBByteSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getByte();
		}

		public SingleItemType getSingleItemType() {
			return SingleItemType.BYTE;
		}
			
	}

	/** THB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedByteSingleItemDecoder extends AbstractTHBSingleItemDecoder {

		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getUnsignedByte();
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_BYTE;
		}
	}

	/** THB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBShortSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getShort();
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SHORT;
		}
	}

	/** THB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedShortSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getUnsignedShort();
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_SHORT;
		}
	}

	/** THB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBIntSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getInt();
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.INTEGER;
		}
	}

	/** THB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedIntSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getUnsignedInt();
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_INTEGER;
		}
	}

	/** THB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBLongSingleItemDecoder extends AbstractTHBSingleItemDecoder {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getLong();
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.LONG;
		}
	}

	/** THB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBPascalStringSingleItemDecoder extends AbstractTHBSingleItemDecoder {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {			
			Charset itemCharset = streamCharsetDecoder.charset();
			
			if (null != nativeItemCharset) {				
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				
			}
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getUBPascalString(itemCharset);
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSPascalStringSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			Charset itemCharset = streamCharsetDecoder.charset();
			
			if (null != nativeItemCharset) {
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getUSPascalString(itemCharset);
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIPascalStringSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			Charset itemCharset = streamCharsetDecoder.charset();
			
			if (null != nativeItemCharset) {
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getSIPascalString(itemCharset);
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthStringSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			if (itemSize < 0) {
				String errorMesage = new StringBuilder("the parameter itemSize[")
						.append(itemSize)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMesage);
			}
			
			CharsetDecoder resultCharsetDecoder = streamCharsetDecoder;
			if (null != nativeItemCharset) {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				resultCharsetDecoder = itemCharset.newDecoder();
				resultCharsetDecoder.onMalformedInput(streamCodingErrorActionOnMalformedInput);
				resultCharsetDecoder.onUnmappableCharacter(streamCodingErrorActionOnUnmappableCharacter);
			}
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getFixedLengthString(itemSize, resultCharsetDecoder);
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** THB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBVariableLengthBytesSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			short len = binaryInputStream.getUnsignedByte();
			return binaryInputStream.getBytes(len);
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_VARIABLE_LENGTH_BYTES;
		}
	}

	/** THB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSVariableLengthBytesSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			int len = binaryInputStream.getUnsignedShort();
			return binaryInputStream.getBytes(len);
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIVariableLengthBytesSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			int len = binaryInputStream.getInt();
			return binaryInputStream.getBytes(len);
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthBytesSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			if (itemSize < 0) {
				String errorMesage = new StringBuilder("the parameter itemSize[")
						.append(itemSize)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMesage);
			}
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getBytes(itemSize);
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBJavaSqlDateSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream) throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			long javaSqlDateLongValue = binaryInputStream.getLong();			
			return new java.sql.Date(javaSqlDateLongValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_DATE;
		}
	}
	
	/** THB 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBJavaSqlTimestampSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream) throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			long javaSqlDateLongValue = binaryInputStream.getLong();			
			return new java.sql.Timestamp(javaSqlDateLongValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_TIMESTAMP;
		}
	}
	
	/** THB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBBooleanSingleItemDecoder extends AbstractTHBSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream) throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			byte itemValue = binaryInputStream.getByte();
			
			if (itemValue != 0 && itemValue != 1) {
				String errorMesssage = new StringBuilder()
						.append("this boolean type single item[")
						.append(itemName)
						.append("]'s value[")
						.append(itemValue)
						.append("] is bad, boolean type's value must be zero or one").toString();
				throw new BodyFormatException(errorMesssage);
			}
				
			return (0 != itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BOOLEAN;
		}
	}
	
	public AbstractTHBSingleItemDecoder get(int itemTypeID) {
		return thbSingleItemDecoderList[itemTypeID];
	}
}
