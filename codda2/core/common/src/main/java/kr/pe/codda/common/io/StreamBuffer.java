package kr.pe.codda.common.io;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CharsetDecoderException;
import kr.pe.codda.common.exception.CharsetEncoderException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.util.HexUtil;

/**
 * 소켓 읽기 혹은 쓰기에 사용될 이진 데이터 스트림이 담기는 버퍼 클래스. WARNING! this class is not thread
 * safe class.
 * 
 * @author Won Jonghoon
 *
 */
public class StreamBuffer {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final WrapBufferPoolIF wrapBufferPool;
	private final Charset defaultCharset;

	private final CharsetEncoder defaultCharsetEncoder;
	private final CharsetDecoder defaultCharsetDecoder;

	private final int dataPacketBufferSize;
	private final ByteOrder byteOrder;

	private long position = 0L;
	private long limit = 0L;
	private final long capacity;

	private final WrapBuffer wrapBufferArray[];
	private final ByteBuffer byteBufferArray[];
	private int lastIndex = -1;

	public StreamBuffer(Charset defaultCharset, WrapBufferPoolIF wrapBufferPool, int dataPacketBufferMaxCount) {
		if (null == defaultCharset) {
			throw new IllegalArgumentException("the parameter defaultCharset is null");
		}

		if (null == wrapBufferPool) {
			throw new IllegalArgumentException("the parameter wrapBufferPool is null");
		}

		if (dataPacketBufferMaxCount <= 0) {
			throw new IllegalArgumentException("the parameter dataPacketBufferMaxCount is less than or equal to zero");
		}

		this.defaultCharset = defaultCharset;
		this.wrapBufferPool = wrapBufferPool;

		defaultCharsetEncoder = defaultCharset.newEncoder();
		defaultCharsetDecoder = defaultCharset.newDecoder();

		byteOrder = wrapBufferPool.getByteOrder();
		dataPacketBufferSize = wrapBufferPool.getDataPacketBufferSize();

		capacity = (long) dataPacketBufferMaxCount * dataPacketBufferSize;
		limit = capacity;

		wrapBufferArray = new WrapBuffer[dataPacketBufferMaxCount];
		byteBufferArray = new ByteBuffer[dataPacketBufferMaxCount];
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long newPosition) {
		if (newPosition < 0) {
			String errorMessage = new StringBuilder().append("the parameter newPosition[").append(newPosition)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (newPosition > limit) {
			String errorMessage = new StringBuilder().append("the parameter newPosition[").append(newPosition)
					.append("] is greater than limit[").append(limit).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.position = newPosition;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long newLimit) {
		if (newLimit < 0) {
			String errorMessage = new StringBuilder().append("the parameter newLimit[").append(newLimit)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (newLimit < position) {
			String errorMessage = new StringBuilder().append("the parameter newLimit[").append(newLimit)
					.append("] is less than position[").append(position).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (newLimit > capacity) {
			String errorMessage = new StringBuilder().append("the parameter newLimit[").append(newLimit)
					.append("] is greater than capacity[").append(capacity).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.limit = newLimit;
	}

	public long getCapacity() {
		return capacity;
	}

	public ByteOrder getByteOder() {
		return byteOrder;
	}

	public Charset getCharset() {
		return defaultCharset;
	}

	/**
	 * 읽기/쓰기 작업 하기전 필요한 버퍼를 확보를 목적으로 하는 메소드로 '읽기 혹은 쓰기 작업 완료 후 위치' 를 입력 받아 그 위치까지
	 * 필요한 버퍼를 확보한다.
	 * 
	 * @param size 읽기 혹은 쓰기 작업에 필요한 바이트수
	 * @throws NoMoreDataPacketBufferException
	 */
	private void fillWrapBuffer(int newLastIndex) throws NoMoreDataPacketBufferException {
		if (newLastIndex < 0) {
			throw new IllegalArgumentException("the parameter newLastIndex is less than zero");
		}

		if (newLastIndex >= wrapBufferArray.length) {
			String errorMessage = new StringBuilder().append("the parameter newLastIndex[").append(newLastIndex)
					.append("] is greater than or equal to wrapBufferArray.length[").append(wrapBufferArray.length)
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		/*
		 * if (newLastIndex > lastIndex) {
		 * 
		 * for (int i = lastIndex + 1; i <= newLastIndex; i++) { wrapBufferArray[i] =
		 * wrapBufferPool.pollDataPacketBuffer(); byteBufferArray[i] =
		 * wrapBufferArray[i].getByteBuffer(); byteBufferArray[i].order(byteOrder); }
		 * 
		 * lastIndex = newLastIndex; }
		 */

		while (lastIndex < newLastIndex) {
			lastIndex++;
			wrapBufferArray[lastIndex] = wrapBufferPool.pollDataPacketBuffer();
			byteBufferArray[lastIndex] = wrapBufferArray[lastIndex].getByteBuffer();
			byteBufferArray[lastIndex].order(byteOrder);
		}

	}

	private void doPutByte(byte value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		int startWorkingIndex = (int) (position / dataPacketBufferSize);
		int startWorkingOffset = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[startWorkingIndex];
		if (null == workingByteBuffer) {
			fillWrapBuffer(startWorkingIndex);
			workingByteBuffer = byteBufferArray[startWorkingIndex];
		}

		workingByteBuffer.position(startWorkingOffset);

		// FIXME! debug
		//log.log(Level.INFO, new StringBuilder().append("before::buffer[").append(startWorkingIndex).append("]=").append(workingByteBuffer.toString()).toString());

		workingByteBuffer.put(value);
		
		/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
		workingByteBuffer.position(0);

		position++;
	}

	public void putByte(byte value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 1)) {
			throw new BufferOverflowException();
		}

		doPutByte(value);
	}

	public void putUnsignedByte(short value)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("] of unsigned byte").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		putByte((byte) value);
	}

	public void putUnsignedByte(int value)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("] of unsigned byte").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		putByte((byte) value);
	}

	public void putUnsignedByte(long value)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("] of unsigned byte").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		putByte((byte) value);
	}

	private void doPutShort(int value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		byte t2 = (byte) (value & 0xff);
		byte t1 = (byte) ((value & 0xff00) >> 8);

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			doPutByte(t1);
			doPutByte(t2);
		} else {
			doPutByte(t2);
			doPutByte(t1);
		}
	}

	public void putShort(short value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 2)) {
			throw new BufferOverflowException();
		}

		byte t2 = (byte) (value & 0xff);
		byte t1 = (byte) ((value & 0xff00) >> 8);

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			doPutByte(t1);
			doPutByte(t2);
		} else {
			doPutByte(t2);
			doPutByte(t1);
		}
	}

	public void putUnsignedShort(int value)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_SHORT_MAX)
					.append("] of unsigned short").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (position > (limit - 2)) {
			throw new BufferOverflowException();
		}

		doPutShort(value);
	}

	public void putUnsignedShort(long value)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_SHORT_MAX)
					.append("] of unsigned short").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		putShort((short) value);
	}

	private void doPutInt(int value) throws BufferOverflowException, NoMoreDataPacketBufferException {		

		final byte[] intBuffer;	
		
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			intBuffer = new byte[]{(byte) ((value & 0xff000000) >> 24), (byte) ((value & 0xff0000) >> 16), (byte) ((value & 0xff00) >> 8), (byte) (value & 0xff)};
			
		} else {
			intBuffer = new byte[]{(byte) (value & 0xff), (byte) ((value & 0xff00) >> 8), (byte) ((value & 0xff0000) >> 16),  (byte) ((value & 0xff000000) >> 24)};
		}
		
		doPutBytes(intBuffer, 0, intBuffer.length);
	}

	public void putInt(int value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 4)) {
			throw new BufferOverflowException();
		}
		
		/*
		byte t4 = (byte) (value & 0xff);
		byte t3 = (byte) ((value & 0xff00) >> 8);
		byte t2 = (byte) ((value & 0xff0000) >> 16);
		byte t1 = (byte) ((value & 0xff000000) >> 24);

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			doPutByte(t1);
			doPutByte(t2);
			doPutByte(t3);
			doPutByte(t4);
		} else {
			doPutByte(t4);
			doPutByte(t3);
			doPutByte(t2);
			doPutByte(t1);
		}
		*/

		final byte[] intBuffer;	
		
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			intBuffer = new byte[]{(byte) ((value & 0xff000000) >> 24), (byte) ((value & 0xff0000) >> 16), (byte) ((value & 0xff00) >> 8), (byte) (value & 0xff)};
			
		} else {
			intBuffer = new byte[]{(byte) (value & 0xff), (byte) ((value & 0xff00) >> 8), (byte) ((value & 0xff0000) >> 16),  (byte) ((value & 0xff000000) >> 24)};
		}
		
		doPutBytes(intBuffer, 0, 4);
	}
		

	public void putUnsignedInt(long value)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (value > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder().append("the parameter value[").append(value)
					.append("] is greater than max[").append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
					.append("] of unsigned integer").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (position > (limit - 4)) {
			throw new BufferOverflowException();
		}

		doPutInt((int)value);
	}

	public void putLong(long value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 8)) {
			throw new BufferOverflowException();
		}
		
		/*
		byte t8 = (byte) (value & 0xffL);
		byte t7 = (byte) ((value & 0xff00L) >> 8);
		byte t6 = (byte) ((value & 0xff0000L) >> 16);
		byte t5 = (byte) ((value & 0xff000000L) >> 24);
		byte t4 = (byte) ((value & 0xff00000000L) >> 32);
		byte t3 = (byte) ((value & 0xff0000000000L) >> 40);
		byte t2 = (byte) ((value & 0xff000000000000L) >> 48);
		byte t1 = (byte) ((value & 0xff00000000000000L) >> 56);

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			doPutByte(t1);
			doPutByte(t2);
			doPutByte(t3);
			doPutByte(t4);
			doPutByte(t5);
			doPutByte(t6);
			doPutByte(t7);
			doPutByte(t8);
		} else {
			doPutByte(t8);
			doPutByte(t7);
			doPutByte(t6);
			doPutByte(t5);
			doPutByte(t4);
			doPutByte(t3);
			doPutByte(t2);
			doPutByte(t1);
		}
		*/
		
		final byte[] intBuffer;	
		
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			intBuffer = new byte[]{
					(byte) ((value & 0xff00000000000000L) >> 56),
					(byte) ((value & 0xff000000000000L) >> 48),
					(byte) ((value & 0xff0000000000L) >> 40),
					(byte) ((value & 0xff00000000L) >> 32),					
					(byte) ((value & 0xff000000L) >> 24), 
					(byte) ((value & 0xff0000L) >> 16), 
					(byte) ((value & 0xff00L) >> 8), 
					(byte) (value & 0xffL)};
			
		} else {
			intBuffer = new byte[]{
					(byte) (value & 0xffL), 
					(byte) ((value & 0xff00L) >> 8), 
					(byte) ((value & 0xff0000L) >> 16),  
					(byte) ((value & 0xff000000L) >> 24),
					(byte) ((value & 0xff00000000L) >> 32),
					(byte) ((value & 0xff0000000000L) >> 40),
					(byte) ((value & 0xff000000000000L) >> 48),
					(byte) ((value & 0xff00000000000000L) >> 56)};
		}
		
		doPutBytes(intBuffer, 0, 8);
	}
	

	private void doPutBytes(byte values[], int offset, int length)
			throws BufferOverflowException, NoMoreDataPacketBufferException {
		/*
		for (int i = 0; i < length; i++) {
			doPutByte(src[offset + i]);
		}
		*/
		
		int startWorkingIndex = (int) (position / dataPacketBufferSize);
		int startWorkingOffset = (int) (position % dataPacketBufferSize);
		
		ByteBuffer workingByteBuffer = byteBufferArray[startWorkingIndex];
		if (null == workingByteBuffer) {
			fillWrapBuffer(startWorkingIndex);
			workingByteBuffer = byteBufferArray[startWorkingIndex];
		}

		workingByteBuffer.position(startWorkingOffset);

		do {			

			int numberOfBytesRemainingInWorkBuffer = workingByteBuffer.remaining();
			if (length <= numberOfBytesRemainingInWorkBuffer) {
				workingByteBuffer.put(values, offset, length);
				position += length;
				/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
				workingByteBuffer.position(0);
				break;
			}
			
			workingByteBuffer.put(values, offset, numberOfBytesRemainingInWorkBuffer);
			
			/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);

			offset += numberOfBytesRemainingInWorkBuffer;
			length -= numberOfBytesRemainingInWorkBuffer;
			position += numberOfBytesRemainingInWorkBuffer;

			if (byteBufferArray.length == (startWorkingIndex + 1)) {
				/** dead code::어디선가 position 과 limit 을  잘못 조작했을 경우 발생한다 */
				log.log(Level.SEVERE, "no more next buffer becase the var startWorkingIndex is max");
				System.exit(1);
			}
			
			startWorkingIndex++;
			workingByteBuffer = byteBufferArray[startWorkingIndex];
			if (null == workingByteBuffer) {
				fillWrapBuffer(startWorkingIndex);
				workingByteBuffer = byteBufferArray[startWorkingIndex];
			}

		} while (true);

	}

	public void putBytes(byte[] src, int offset, int length)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (offset < 0) {
			String errorMessage = new StringBuilder().append("the parameter offset[").append(offset)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (offset >= src.length) {
			String errorMessage = new StringBuilder().append("the parameter offset[").append(offset)
					.append("] is greater than and equal to the parameter src's length[").append(src.length).append("]")
					.toString();

			throw new IllegalArgumentException(errorMessage);
		}

		if (length < 0) {
			String errorMessage = new StringBuilder().append("the parameter length[").append(length)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		long sumOfOffsetAndLength = ((long) offset + length);
		if (sumOfOffsetAndLength > src.length) {
			String errorMessage = new StringBuilder().append("the sum[").append(sumOfOffsetAndLength)
					.append("] of the parameter offset[").append(offset).append("] and the parameter length[")
					.append(length).append("] is greater than the parameter src's length[").append(src.length)
					.append("]").toString();

			throw new IllegalArgumentException(errorMessage);
		}

		if (0 == length) {
			return;
		}

		if (position > (limit - length)) {
			throw new BufferOverflowException();
		}

		
		doPutBytes(src, offset, length);
	}

	public void putBytes(byte[] src)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (position > (limit - src.length)) {
			throw new BufferOverflowException();
		}
		
		doPutBytes(src, 0, src.length);
	}

	/**
	 * doPutBytes 와 속도 비교를 위한 특수 메소드로 바이트 배열을 루프 돌면서 doPutByte 메소들 이용하여 출력한다.
	 * 
	 * @param src
	 * @throws BufferOverflowException
	 * @throws IllegalArgumentException
	 * @throws NoMoreDataPacketBufferException
	 */
	public void putBytes2(byte[] src)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (position > (limit - src.length)) {
			throw new BufferOverflowException();
		}
				
		for (int i = 0; i < src.length; i++) {
			doPutByte(src[i]);
		}
	}
	
	public void putBytes(ByteBuffer src)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (position > (limit - src.remaining())) {
			throw new BufferOverflowException();
		}
		
	
		/*
		 * do { doPutByte(src.get()); } while (src.hasRemaining());
		 */
		
		int startWorkingIndex = (int) (position / dataPacketBufferSize);
		int startWorkingOffset = (int) (position % dataPacketBufferSize);
		
		ByteBuffer workingByteBuffer = byteBufferArray[startWorkingIndex];
		if (null == workingByteBuffer) {
			fillWrapBuffer(startWorkingIndex);
			workingByteBuffer = byteBufferArray[startWorkingIndex];
		}

		workingByteBuffer.position(startWorkingOffset);
		
		do {
			int numberOfBytesRemainingInWorkBuffer = workingByteBuffer.remaining();
			if (src.remaining() <= numberOfBytesRemainingInWorkBuffer) {				
				position += src.remaining();
				workingByteBuffer.put(src);
				/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
				workingByteBuffer.position(0);
				break;
			}
			
			int oldLimit = src.limit();
			src.limit(src.position() + numberOfBytesRemainingInWorkBuffer);

			workingByteBuffer.put(src);
			/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);
			
			src.limit(oldLimit);
			position += numberOfBytesRemainingInWorkBuffer;

			if (byteBufferArray.length == (startWorkingIndex + 1)) {
				/** dead code::어디선가 position 과 limit 을  잘못 조작했을 경우 발생한다 */
				log.log(Level.SEVERE, "no more next buffer becase the var startWorkingIndex is max");
				System.exit(1);
			}
			startWorkingIndex++;
			workingByteBuffer = byteBufferArray[startWorkingIndex];
			if (null == workingByteBuffer) {
				fillWrapBuffer(startWorkingIndex);
				workingByteBuffer = byteBufferArray[startWorkingIndex];
			}

		} while (true);		
	}

	public void putFixedLengthString(int fixedLength, String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException {
		putFixedLengthString(fixedLength, src, defaultCharsetEncoder);
	}

	public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException,
			CharsetEncoderException {
		if (fixedLength < 0) {
			String errorMessage = new StringBuilder().append("the parameter fixedLength[")
					.append(fixedLength)
					.append("] is less than zero").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharsetEncoder == null) {
			throw new IllegalArgumentException("the parameter wantedCharsetEncoder is null");
		}

		if (0 == fixedLength) {
			return;
		}

		if (position > (limit - fixedLength)) {
			throw new BufferOverflowException();
		}

		byte strBytes[] = new byte[fixedLength];
		Arrays.fill(strBytes, CommonStaticFinalVars.ZERO_BYTE);
		ByteBuffer strByteBuffer = ByteBuffer.wrap(strBytes);

		CharBuffer strCharBuffer = CharBuffer.wrap(src);
		try {
			wantedCharsetEncoder.encode(strCharBuffer, strByteBuffer, true);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get a charset[")
					.append(wantedCharsetEncoder.charset().name()).append("] bytes of the parameter src[").append(src)
					.append("], errmsg=").append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}

		/*
		for (int i = 0; i < strBytes.length; i++) {
			doPutByte(strBytes[i]);
		}
		*/
		doPutBytes(strBytes, 0, fixedLength);
	}

	public void putStringAll(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, CharsetEncoderException {
		putStringAll(src, defaultCharset);
	}

	public void putStringAll(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get a charset[").append(wantedCharset.name())
					.append("] bytes of the parameter src[").append(src).append("], errmsg=").append(e.getMessage())
					.toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}

		if (position > (limit - strBytes.length)) {
			throw new BufferOverflowException();
		}

		/*
		for (int i = 0; i < strBytes.length; i++) {
			doPutByte(strBytes[i]);
		}
		*/
		doPutBytes(strBytes, 0, strBytes.length);
	}

	public void putPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, CharsetEncoderException {
		putUBPascalString(src, defaultCharset);
	}

	public void putPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException {
		putUBPascalString(src, wantedCharset);
	}

	public void putUBPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, CharsetEncoderException {
		putUBPascalString(src, defaultCharset);
	}

	public void putUBPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get a charset[").append(wantedCharset.name())
					.append("] bytes of the parameter src[").append(src).append("], errmsg=").append(e.getMessage())
					.toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}

		if (strBytes.length > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = String.format(
					"the length[%d] of bytes encoding the parameter src as a charset[%s] is greater than the unsigned byte max[%d]",
					strBytes.length, wantedCharset.name(), CommonStaticFinalVars.UNSIGNED_BYTE_MAX);

			throw new IllegalArgumentException(errorMessage);
		}

		if (position > (limit - strBytes.length - 1)) {
			throw new BufferOverflowException();
		}

		doPutByte((byte) strBytes.length);

		/*
		for (int i = 0; i < strBytes.length; i++) {
			doPutByte(strBytes[i]);
		}
		*/
		doPutBytes(strBytes, 0, strBytes.length);
	}

	public void putUSPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, CharsetEncoderException {
		putUSPascalString(src, defaultCharset);
	}

	public void putUSPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get a charset[").append(wantedCharset.name())
					.append("] bytes of the parameter src[").append(src).append("], errmsg=").append(e.getMessage())
					.toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}

		if (strBytes.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			String errorMessage = String.format(
					"the length[%d] of bytes encoding the parameter src as a charset[%s] is greater than the unsigned short max[%d]",
					strBytes.length, wantedCharset.name(), CommonStaticFinalVars.UNSIGNED_SHORT_MAX);

			throw new IllegalArgumentException(errorMessage);
		}

		if (position > (limit - strBytes.length - 2)) {
			throw new BufferOverflowException();
		}

		doPutShort(strBytes.length);

		/*
		for (int i = 0; i < strBytes.length; i++) {
			doPutByte(strBytes[i]);
		}
		*/
		doPutBytes(strBytes, 0, strBytes.length);
	}

	public void putSIPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, CharsetEncoderException {
		putSIPascalString(src, defaultCharset);
	}

	public void putSIPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get a charset[").append(wantedCharset.name())
					.append("] bytes of the parameter src[").append(src).append("], errmsg=").append(e.getMessage())
					.toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}

		if (position > (limit - strBytes.length - 4)) {
			throw new BufferOverflowException();
		}

		doPutInt(strBytes.length);

		/*
		for (int i = 0; i < strBytes.length; i++) {
			doPutByte(strBytes[i]);
		}
		*/
		doPutBytes(strBytes, 0, strBytes.length);
	}

	private byte doGetByte() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		int startWorkingIndex = (int) (position / dataPacketBufferSize);
		int startWorkingOffset = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[startWorkingIndex];
		if (null == workingByteBuffer) {
			fillWrapBuffer(startWorkingIndex);
			workingByteBuffer = byteBufferArray[startWorkingIndex];
		}

		workingByteBuffer.position(startWorkingOffset);

		// FIXME! debug
		// log.log(Level.INFO, new StringBuilder().append("before::buffer[").append(startWorkingIndex).append("]=").append(workingByteBuffer.toString()).toString());

		byte returnByte = workingByteBuffer.get();
		
		/** 버퍼에 대한 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
		workingByteBuffer.position(0);

		position++;

		return returnByte;
	}

	public byte getByte() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 1)) {
			throw new BufferUnderflowException();
		}

		return doGetByte();
	}

	public short getUnsignedByte() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 1)) {
			throw new BufferUnderflowException();
		}

		final short retValue = (short) (doGetByte() & 0xff);
		return retValue;
	}

	public short getShort() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 2)) {
			throw new BufferUnderflowException();
		}

		final byte t1;
		final byte t2;

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			t1 = doGetByte();
			t2 = doGetByte();
		} else {
			t2 = doGetByte();
			t1 = doGetByte();
		}

		final short retValue = (short) (((t1 & 0xff) << 8) | (t2 & 0xff));

		return retValue;
	}

	public int getUnsignedShort() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 2)) {
			throw new BufferUnderflowException();
		}

		final byte t1;
		final byte t2;

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			t1 = doGetByte();
			t2 = doGetByte();
		} else {
			t2 = doGetByte();
			t1 = doGetByte();
		}

		final int retValue = (((t1 & 0xff) << 8) | (t2 & 0xff));

		return retValue;
	}

	public int getInt() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 4)) {
			throw new BufferUnderflowException();
		}

		/*
		final byte t1;
		final byte t2;
		final byte t3;
		final byte t4;

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			t1 = doGetByte();
			t2 = doGetByte();
			t3 = doGetByte();
			t4 = doGetByte();
		} else {
			t4 = doGetByte();
			t3 = doGetByte();
			t2 = doGetByte();
			t1 = doGetByte();
		}

		final int retValue = (((t1 & 0xff) << 24) | ((t2 & 0xff) << 16) | ((t3 & 0xff) << 8) | (t4 & 0xff));
		
		*/
		final int retValue;
		final byte[] intBuffer = doGetBytes(4);
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			retValue = (((intBuffer[0] & 0xff) << 24) | ((intBuffer[1] & 0xff) << 16) | ((intBuffer[2] & 0xff) << 8) | (intBuffer[3] & 0xff));
		} else {
			retValue = (((intBuffer[3] & 0xff) << 24) | ((intBuffer[2] & 0xff) << 16) | ((intBuffer[1] & 0xff) << 8) | (intBuffer[0] & 0xff));
		}

		return retValue;
	}

	public long getUnsignedInt() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 4)) {
			throw new BufferUnderflowException();
		}

		/*
		final byte t1;
		final byte t2;
		final byte t3;
		final byte t4;

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			t1 = doGetByte();
			t2 = doGetByte();
			t3 = doGetByte();
			t4 = doGetByte();
		} else {
			t4 = doGetByte();
			t3 = doGetByte();
			t2 = doGetByte();
			t1 = doGetByte();
		}

		final long retValue = (((t1 & 0xffL) << 24) | ((t2 & 0xffL) << 16) | ((t3 & 0xffL) << 8) | (t4 & 0xffL));
		*/
		
		final long retValue;
		final byte[] intBuffer = doGetBytes(4);
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			retValue = (((intBuffer[0] & 0xffL) << 24) | ((intBuffer[1] & 0xffL) << 16) | ((intBuffer[2] & 0xffL) << 8) | (intBuffer[3] & 0xffL));
		} else {
			retValue = (((intBuffer[3] & 0xffL) << 24) | ((intBuffer[2] & 0xffL) << 16) | ((intBuffer[1] & 0xffL) << 8) | (intBuffer[0] & 0xffL));
		}		

		return retValue;
	}

	public long getLong() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if (position > (limit - 8)) {
			throw new BufferUnderflowException();
		}

		/*
		final byte t1;
		final byte t2;
		final byte t3;
		final byte t4;
		final byte t5;
		final byte t6;
		final byte t7;
		final byte t8;

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			t1 = doGetByte();
			t2 = doGetByte();
			t3 = doGetByte();
			t4 = doGetByte();
			t5 = doGetByte();
			t6 = doGetByte();
			t7 = doGetByte();
			t8 = doGetByte();
		} else {
			t8 = doGetByte();
			t7 = doGetByte();
			t6 = doGetByte();
			t5 = doGetByte();
			t4 = doGetByte();
			t3 = doGetByte();
			t2 = doGetByte();
			t1 = doGetByte();
		}

		final long retValue = (((t1 & 0xffL) << 56) | ((t2 & 0xffL) << 48) | ((t3 & 0xffL) << 40) | ((t4 & 0xffL) << 32)
				| ((t5 & 0xffL) << 24) | ((t6 & 0xffL) << 16) | ((t7 & 0xffL) << 8) | (t8 & 0xffL));
				*/
		final long retValue;
		final byte[] intBuffer = doGetBytes(8);
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			retValue = (((intBuffer[0] & 0xffL) << 56)
					| ((intBuffer[1] & 0xffL) << 48)
					| ((intBuffer[2] & 0xffL) << 40)
					| ((intBuffer[3] & 0xffL) << 32)
					| ((intBuffer[4] & 0xffL) << 24) 
					| ((intBuffer[5] & 0xffL) << 16) 
					| ((intBuffer[6] & 0xffL) << 8) 
					| (intBuffer[7] & 0xffL));
		} else {
			retValue = (((intBuffer[7] & 0xffL) << 56)
					| ((intBuffer[6] & 0xffL) << 48)
					| ((intBuffer[5] & 0xffL) << 40)
					| ((intBuffer[4] & 0xffL) << 32)
					| ((intBuffer[3] & 0xffL) << 24) 
					| ((intBuffer[2] & 0xffL) << 16) 
					| ((intBuffer[1] & 0xffL) << 8) 
					| (intBuffer[0] & 0xffL));
		}

		return retValue;
	}

	private String doGetString(int length, Charset stringCharset)
			throws BufferUnderflowException, CharsetDecoderException, NoMoreDataPacketBufferException {

		byte dstBytes[] = new byte[length];
		for (int i = 0; i < length; i++) {
			dstBytes[i] = doGetByte();
		}

		String dst = null;

		try {
			dst = new String(dstBytes, stringCharset);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get a new String. read data hex[")
					.append(HexUtil.getHexStringFromByteArray(dstBytes)).append("], charset[")
					.append(stringCharset.name()).append("]").toString();
			// log.warn(errorMessage, e);
			throw new CharsetDecoderException(errorMessage);
		}

		return dst;
	}

	public String getFixedLengthString(final int fixedLength, final CharsetDecoder wantedCharsetDecoder)
			throws BufferUnderflowException, IllegalArgumentException, CharsetDecoderException,
			NoMoreDataPacketBufferException {
		if (fixedLength < 0) {
			String errorMessage = new StringBuilder().append("the parameter fixedLength[").append(fixedLength)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (position > (limit - fixedLength)) {
			throw new BufferUnderflowException();
		}

		if (null == wantedCharsetDecoder) {
			throw new IllegalArgumentException("the parameter wantedCharsetDecoder is null");
		}

		if (0 == fixedLength) {
			return "";
		}

		return doGetString(fixedLength, wantedCharsetDecoder.charset());

	}

	public String getFixedLengthString(final int fixedLength) throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		return getFixedLengthString(fixedLength, defaultCharsetDecoder);
	}

	public String getStringAll() throws BufferUnderflowException, IllegalArgumentException, CharsetDecoderException,
			NoMoreDataPacketBufferException {
		return getStringAll(defaultCharset);
	}

	public String getStringAll(Charset wantedCharset) throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		if (limit == position) {
			return "";
		}

		long numberOfBytesRemaining = limit - position;
		if (numberOfBytesRemaining > Integer.MAX_VALUE) {
			/**
			 * 자바 문자열에 입력 가능한 바이트 배열의 크기는 Integer.MAX_VALUE 이다.
			 */
			String errorMessage = new StringBuilder().append("전체 문자열 크기[").append(numberOfBytesRemaining)
					.append("]가  Integer MAX 인 자바 문자열 최대 크기보다 큽니다").toString();
			log.log(Level.INFO, errorMessage);
			throw new BufferUnderflowException();
		}

		return getFixedLengthString((int) numberOfBytesRemaining, wantedCharset.newDecoder());

	}

	public String getPascalString() throws BufferUnderflowException, IllegalArgumentException, CharsetDecoderException,
			NoMoreDataPacketBufferException {
		return getUBPascalString(defaultCharset);
	}

	public String getPascalString(Charset wantedCharset) throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		return getUBPascalString(wantedCharset);
	}

	public String getSIPascalString() throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		return getSIPascalString(defaultCharset);
	}

	public String getSIPascalString(Charset wantedCharset) throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		int length = getInt();

		if (length < 0) {
			String errorMessage = new StringBuilder().append("the si pascal string's length[").append(length)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (position > (limit - length)) {
			throw new BufferUnderflowException();
		}

		if (0 == length) {
			return "";
		}

		return doGetString(length, wantedCharset);
	}

	public String getUSPascalString() throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		return getUSPascalString(defaultCharset);
	}

	public String getUSPascalString(Charset wantedCharset) throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		int length = getUnsignedShort();

		if (position > (limit - length)) {
			throw new BufferUnderflowException();
		}

		if (0 == length) {
			return "";
		}

		return doGetString(length, wantedCharset);
	}

	public String getUBPascalString() throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		return getUBPascalString(defaultCharset);
	}

	public String getUBPascalString(Charset wantedCharset) throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		short length = getUnsignedByte();

		if (position > (limit - length)) {
			throw new BufferUnderflowException();
		}

		if (0 == length) {
			return "";
		}

		return doGetString(length, wantedCharset);
	}
	
	private byte[] doGetBytes(int length) throws NoMoreDataPacketBufferException {
		byte dstBytes[] = new byte[length];
		int offset = 0;
		
		int startWorkingIndex = (int) (position / dataPacketBufferSize);
		int startWorkingOffset = (int) (position % dataPacketBufferSize);
		
		ByteBuffer workingByteBuffer = byteBufferArray[startWorkingIndex];
		if (null == workingByteBuffer) {
			fillWrapBuffer(startWorkingIndex);
			workingByteBuffer = byteBufferArray[startWorkingIndex];
		}

		workingByteBuffer.position(startWorkingOffset);

		do {			

			int numberOfBytesRemainingInWorkBuffer = workingByteBuffer.remaining();
			if (length <= numberOfBytesRemainingInWorkBuffer) {
				workingByteBuffer.get(dstBytes, offset, length);
				position += length;
				/** 버퍼에 대한 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
				workingByteBuffer.position(0);
				break;
			}
			
			workingByteBuffer.get(dstBytes, offset, numberOfBytesRemainingInWorkBuffer);
			
			/** 버퍼에 대한 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);

			offset += numberOfBytesRemainingInWorkBuffer;
			length -= numberOfBytesRemainingInWorkBuffer;
			position += numberOfBytesRemainingInWorkBuffer;

			if (byteBufferArray.length == (startWorkingIndex + 1)) {
				/** dead code::어디선가 position 과 limit 을  잘못 조작했을 경우 발생한다 */
				log.log(Level.SEVERE, "no more next buffer becase the var startWorkingIndex is max");
				System.exit(1);
			}
			
			startWorkingIndex++;
			workingByteBuffer = byteBufferArray[startWorkingIndex];
			if (null == workingByteBuffer) {
				fillWrapBuffer(startWorkingIndex);
				workingByteBuffer = byteBufferArray[startWorkingIndex];
			}

		} while (true);
		
		return dstBytes;
	}

	public byte[] getBytes(int length)
			throws BufferUnderflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		if (length < 0) {
			String errorMessage = new StringBuilder().append("the parameter length[").append(length)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (position > (limit - length)) {
			throw new BufferUnderflowException();
		}		
		
		/*
		byte dstBytes[] = new byte[length];

		for (int i = 0; i < length; i++) {
			dstBytes[i] = doGetByte();
		}
		return dstBytes;
		*/
		

		return doGetBytes(length);
	}
	
	/**
	 * 단위 테스트용 특수 메소드로 (1) '마지막으로 할당된 버퍼의 인덱스' 의 유효성 검사와 (2) 읽기 혹은 쓰기 작업 완료후 작업 할때 변경한 '위치 속성 값'을 처음 상태인 0 값으로 바르게 복원했는지 여부를 검사한다. 
	 * 바이트 버퍼 배열을 검사하여 (1) '마지막으로 할당된 버퍼의 인덱스' 보다 큰 인덱스를 갖는 할당된 버퍼가 존재할 경우와 (2) '위치 속성 값' 이 0이 아닌 경우 예외를 던진다.
	 * 
	 * @throws IllegalStateException
	 */
	public void checkValid() throws IllegalStateException {
		for (int i=0; i < byteBufferArray.length; i++) {
			if (null != byteBufferArray[i]) {
				if (i > lastIndex) {
					String errorMessage = new StringBuilder()
							.append("'마지막으로 할당된 버퍼의 인덱스' 가 잘못되었습니다, '마지막으로 할당된 버퍼의 인덱스'[")
							.append(lastIndex)
							.append("] 보다 큰 인덱스 [")
							.append(i)
							.append("] 를 갖는 할당된 버퍼가 존재합니다").toString();
					throw new IllegalStateException(errorMessage);
				}
				
				if (byteBufferArray[i].position() != 0) {
					String errorMessage = new StringBuilder()
							.append("인덱스 [")
							.append(i)
							.append("] 를 갖는 할당된 버퍼의 '위치 속성 값'[")
							.append(byteBufferArray[i].position())
							.append("]이 0 이 아닙니다").toString();
					throw new IllegalStateException(errorMessage);
				}
			}
		}
	}
}
