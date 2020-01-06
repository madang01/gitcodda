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

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.common.type.SingleItemType;

/**
 * THB 프로토콜 단일 항목 타입의 인코더 매칭자
 * @author Won Jonghoon
 *
 */
public class THBSingleItemTypeEncoderMatcher implements THBSingleItemTypeEncoderMatcherIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME); 
	
	
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	private final AbstractTHBSingleItemTypeEncoder[] thbSingleItemEncoderList = new AbstractTHBSingleItemTypeEncoder[] { 
			new THBExceptionDeliveryErrorPlaceSingleItemEncoder(), new THBExceptionDeliveryErrorTypeSingleItemEncoder(),
			new THBByteSingleItemEncoder(), new THBUnsignedByteSingleItemEncoder(), 
			new THBShortSingleItemEncoder(), new THBUnsignedShortSingleItemEncoder(),
			new THBIntSingleItemEncoder(), new THBUnsignedIntSingleItemEncoder(), 
			new THBLongSingleItemEncoder(), new THBUBPascalStringSingleItemEncoder(),
			new THBUSPascalStringSingleItemEncoder(), new THBSIPascalStringSingleItemEncoder(), 
			new THBFixedLengthStringSingleItemEncoder(), new THBUBVariableLengthBytesSingleItemEncoder(), 
			new THBUSVariableLengthBytesSingleItemEncoder(), new THBSIVariableLengthBytesSingleItemEncoder(), 
			new THBFixedLengthBytesSingleItemEncoder(), 
			new THBJavaSqlDateSingleItemEncoder(), new THBJavaSqlTimestampSingleItemEncoder(),
			new THBBooleanSingleItemEncoder()
	};
	
	
	/**
	 * 생성자
	 * @param streamCharsetFamily 문자셋, 문자셋 인코더 그리고 문자셋 디코더 묶음
	 */
	public THBSingleItemTypeEncoderMatcher(StreamCharsetFamily streamCharsetFamily) {
		if (null == streamCharsetFamily) {
			throw new IllegalArgumentException("the parameter streamCharsetFamily is null");
		}
		
		CharsetEncoder streamCharsetEncoder = streamCharsetFamily.getCharsetEncoder();
		
		this.streamCodingErrorActionOnMalformedInput = streamCharsetEncoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = streamCharsetEncoder.unmappableCharacterAction();
		
		checkValidTHBSingleItemEncoderList();
	}
	
	/**
	 * 변수 thbSingleItemEncoderList 의 유효성 검사
	 */
	private void checkValidTHBSingleItemEncoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (thbSingleItemEncoderList.length != singleItemTypes.length) {
			String errorMessage = new StringBuilder()
					.append("the var thbSingleItemEncoderList.length[")
					.append(thbSingleItemEncoderList.length)
					.append("] is not differnet from the array var singleItemTypes.length[")
					.append(singleItemTypes.length)
					.append("]").toString();
			
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = thbSingleItemEncoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				String errorMessage = new StringBuilder()
						.append("the var thbSingleItemEncoderList[")
						.append(i)
						.append("]'s SingleItemType[")
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
	 * 'exception delivery error place' 타입의 단일 항목 인코더 구현자
	 * 
	 * @author Won Jonghoon
	 *
	 */
	private final class THBExceptionDeliveryErrorPlaceSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws BufferOverflowException, BufferOverflowException, NoMoreWrapBufferException {			
			ExceptionDelivery.ErrorPlace itemValue = (ExceptionDelivery.ErrorPlace) nativeItemValue;

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(itemValue.getErrorPlaceByte());
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.EXCEPTION_DELIVERY_ERROR_PLACE;
		}
	}
	
	/**
	 * 'exception delivery error type' 타입의 단일 항목 인코더 구현자
	 * @author Won Jonghoon
	 *
	 */
	private final class THBExceptionDeliveryErrorTypeSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws BufferOverflowException, BufferOverflowException, NoMoreWrapBufferException {			
			ExceptionDelivery.ErrorType itemValue = (ExceptionDelivery.ErrorType) nativeItemValue;

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(itemValue.getErrorTypeByte());
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.EXCEPTION_DELIVERY_ERROR_TYPE;
		}
	}

		
	
	/**
	 * ''byte 타입의 단일 항목 인코더 구현자
	 * @author Won Jonghoon
	 *
	 */
	private final class THBByteSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {			
			byte itemValue = (Byte) nativeItemValue;
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(itemValue);
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.BYTE;
		}
	}

	/** THB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedByteSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {

		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			short itemValue = (Short) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedByte(itemValue);
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_BYTE;
		}
	}

	/** THB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBShortSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			short itemValue = (Short) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putShort(itemValue);
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.SHORT;
		}
	}

	/** THB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedShortSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			int itemValue = (Integer) nativeItemValue;

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedShort(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_SHORT;
		}
	}

	/** THB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBIntSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			int itemValue = (Integer) nativeItemValue;
			
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putInt(itemValue);
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.INTEGER;
		}
	}

	/** THB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedIntSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			long itemValue = (Long) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedInt(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_INTEGER;
		}
	}

	/** THB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBLongSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			long itemValue = (Long) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(itemValue);
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.LONG;
		}
	}

	/** THB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBPascalStringSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			String itemValue = (String) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == nativeItemCharset) {
				binaryOutputStream.putUBPascalString(itemValue);
			} else {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				binaryOutputStream.putUBPascalString(itemValue, itemCharset);
			}
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSPascalStringSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			String itemValue = (String) nativeItemValue;
			
			
			writeItemID(itemTypeID, binaryOutputStream);
			if (null == nativeItemCharset) {
				binaryOutputStream.putUSPascalString(itemValue);
			} else {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				
				binaryOutputStream.putUSPascalString(itemValue, itemCharset);
			}
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIPascalStringSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			String itemValue = (String) nativeItemValue;			
			
			writeItemID(itemTypeID, binaryOutputStream);
			if (null == nativeItemCharset) {
				binaryOutputStream.putSIPascalString(itemValue);
			} else {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				binaryOutputStream.putSIPascalString(itemValue, itemCharset);
			}
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthStringSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			if (itemSize < 0) {
				String errorMesage = new StringBuilder("the parameter itemSize[")
						.append(itemSize)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMesage);
			}
			
			String itemValue = (String) nativeItemValue;
			
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == nativeItemCharset) {
				binaryOutputStream.putFixedLengthString(itemSize, itemValue);
			} else {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				
				CharsetEncoder userDefinedCharsetEncoder =  itemCharset.newEncoder();
				userDefinedCharsetEncoder.onMalformedInput(streamCodingErrorActionOnMalformedInput);
				userDefinedCharsetEncoder.onUnmappableCharacter(streamCodingErrorActionOnUnmappableCharacter);
				
				binaryOutputStream.putFixedLengthString(itemSize, itemValue, userDefinedCharsetEncoder);
			}

		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** THB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBVariableLengthBytesSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			writeItemID(itemTypeID, binaryOutputStream);			
			
			byte itemValue[] = (byte[]) nativeItemValue;
			binaryOutputStream.putUnsignedByte(itemValue.length);
			binaryOutputStream.putBytes(itemValue);
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_VARIABLE_LENGTH_BYTES;
		}
	}

	/** THB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSVariableLengthBytesSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			writeItemID(itemTypeID, binaryOutputStream);
			
			byte itemValue[] = (byte[]) nativeItemValue;
			binaryOutputStream.putUnsignedShort(itemValue.length);
			binaryOutputStream.putBytes(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIVariableLengthBytesSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			byte itemValue[] = (byte[]) nativeItemValue;
			binaryOutputStream.putInt(itemValue.length);
			binaryOutputStream.putBytes(itemValue);
		}
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthBytesSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, StreamBuffer binaryOutputStream)
				throws Exception {
			if (itemSize < 0) {
				String errorMesage = new StringBuilder("the parameter itemSize[")
						.append(itemSize)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMesage);
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			
			byte itemValue[] = (byte[]) nativeItemValue;
			
			byte resultBytes[] = Arrays.copyOf(itemValue, itemSize);
			binaryOutputStream.putBytes(resultBytes);
		}
		
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBJavaSqlDateSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue,
				int itemSize, String nativeItemCharset,
				StreamBuffer binaryOutputStream) throws Exception {
			
			java.sql.Date itemValue = (java.sql.Date)nativeItemValue;
			long resultValue = itemValue.getTime();
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(resultValue);
			
		}
		
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_DATE;
		}
	}
	
	/** THB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBJavaSqlTimestampSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue,
				int itemSize, String nativeItemCharset,
				StreamBuffer binaryOutputStream) throws Exception {			
			java.sql.Timestamp itemValue = (java.sql.Timestamp)nativeItemValue;
			long resultValue = itemValue.getTime();
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(resultValue);			
		}
		
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_TIMESTAMP;
		}
	}
	
	/** THB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBBooleanSingleItemEncoder extends AbstractTHBSingleItemTypeEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue,
				int itemSize, String nativeItemCharset,
				StreamBuffer binaryOutputStream) throws Exception {			
			boolean itemValue = (Boolean)nativeItemValue;		
			
			byte resultValue = (itemValue) ? 1 : CommonStaticFinalVars.ZERO_BYTE;
						
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(resultValue);				
		}
		
		
		@Override
		public SingleItemType getSingleItemType() {
			return SingleItemType.BOOLEAN;
		}
	}
	
	@Override
	public AbstractTHBSingleItemTypeEncoder getSingleItemEncoder(int itemTypeID) {
		return thbSingleItemEncoderList[itemTypeID];
	}
}
