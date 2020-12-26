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
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.common.type.MessageSingleItemType;

/**
 * THB 프로토콜 단일 항목 타입의 디코더 매칭자
 * @author Won Jonghoon
 *
 */
public class THBSingleItemTypeDecoderMatcher implements THBSingleItemTypeDecoderMatcherIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private Charset streamCharset = null;
	private CharsetDecoder streamCharsetDecoder = null;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	private final AbstractTHBSingleItemTypeDecoder[] thbSingleItemDecoderList = new AbstractTHBSingleItemTypeDecoder[] { 
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
	
	/**
	 * 생성자
	 * @param streamCharsetFamily 문자셋, 문자셋 인코더 그리고 문자셋 디코더 묶음
	 */
	public THBSingleItemTypeDecoderMatcher(StreamCharsetFamily streamCharsetFamily) {
		if (null == streamCharsetFamily) {
			throw new IllegalArgumentException("the parameter streamCharsetFamily is null");
		}
		this.streamCharset = streamCharsetFamily.getCharset();
		this.streamCharsetDecoder = streamCharsetFamily.getCharsetDecoder();
		this.streamCodingErrorActionOnMalformedInput = streamCharsetDecoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = streamCharsetDecoder.unmappableCharacterAction();
		
		checkValidTHBSingleItemDecoderList();
	}
	
	/**
	 * 변수 thbSingleItemDecoderList 의 유효성 검사
	 */
	private void checkValidTHBSingleItemDecoderList() {
		MessageSingleItemType[] singleItemTypes = MessageSingleItemType.values();
		
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
			MessageSingleItemType expectedSingleItemType = singleItemTypes[i];
			MessageSingleItemType actualSingleItemType = thbSingleItemDecoderList[i].getSingleItemType();
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

	/**
	 * 'exception delivery error place' 타입의 단일 항목 디코더 구현자
	 * @author Won Jonghoon
	 *
	 */
	private final class THBExceptionDeliveryErrorPlaceSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return ExceptionDelivery.ErrorPlace.valueOf(binaryInputStream.getByte());
		}
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.EXCEPTION_DELIVERY_ERROR_PLACE;
		}
	}	
	
	/**
	 * 'exception delivery error type' 타입의 단일 항목 디코더 구현자
	 * @author Won Jonghoon
	 *
	 */
	private final class THBExceptionDeliveryErrorTypeSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return ExceptionDelivery.ErrorType.valueOf(binaryInputStream.getByte());
		}
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.EXCEPTION_DELIVERY_ERROR_TYPE;
		}
	}
	
	
	/** THB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBByteSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getByte();
		}

		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.BYTE;
		}
			
	}

	/** THB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedByteSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {

		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getUnsignedByte();
		}		
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.UNSIGNED_BYTE;
		}
	}

	/** THB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBShortSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getShort();
		}
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.SHORT;
		}
	}

	/** THB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedShortSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getUnsignedShort();
		}		
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.UNSIGNED_SHORT;
		}
	}

	/** THB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBIntSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getInt();
		}
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.INTEGER;
		}
	}

	/** THB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedIntSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getUnsignedInt();
		}
		
		@Override		
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.UNSIGNED_INTEGER;
		}
	}

	/** THB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBLongSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getLong();
		}		
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.LONG;
		}
	}

	/** THB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBPascalStringSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {			
			Charset itemCharset = streamCharset;
			
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
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.UB_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSPascalStringSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			Charset itemCharset = streamCharset;
			
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
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.US_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIPascalStringSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			Charset itemCharset = streamCharset;
			
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
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.SI_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthStringSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
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
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** THB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBVariableLengthBytesSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			short len = binaryInputStream.getUnsignedByte();
			return binaryInputStream.getBytes(len);
		}
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.UB_VARIABLE_LENGTH_BYTES;
		}
	}

	/** THB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSVariableLengthBytesSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			int len = binaryInputStream.getUnsignedShort();
			return binaryInputStream.getBytes(len);
		}	
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.US_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIVariableLengthBytesSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			int len = binaryInputStream.getInt();
			return binaryInputStream.getBytes(len);
		}		
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.SI_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthBytesSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
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
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.FIXED_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBJavaSqlDateSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream) throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			long javaSqlDateLongValue = binaryInputStream.getLong();			
			return new java.sql.Date(javaSqlDateLongValue);
		}
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.JAVA_SQL_DATE;
		}
	}
	
	/** THB 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBJavaSqlTimestampSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				String nativeItemCharset, StreamBuffer binaryInputStream) throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			long javaSqlDateLongValue = binaryInputStream.getLong();			
			return new java.sql.Timestamp(javaSqlDateLongValue);
		}
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.JAVA_SQL_TIMESTAMP;
		}
	}
	
	/** THB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBBooleanSingleItemDecoder extends AbstractTHBSingleItemTypeDecoder {
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
		
		@Override
		public MessageSingleItemType getSingleItemType() {
			return MessageSingleItemType.BOOLEAN;
		}
	}
	
	@Override
	public AbstractTHBSingleItemTypeDecoder getSingleItemDecoder(int itemTypeID) {
		return thbSingleItemDecoderList[itemTypeID];
	}
}
