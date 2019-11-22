package kr.pe.codda.common.io;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.CharsetDecoderException;
import kr.pe.codda.common.exception.CharsetEncoderException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.util.HexUtil;

/**
 * 소켓 읽기 혹은 쓰기에 사용될 이진 데이터 스트림이 담기는 버퍼 클래스. WARNING! this class is not
 * thread-safe class.
 * 
 * @author Won Jonghoon
 *
 */
public class StreamBuffer {
	protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	protected final WrapBufferPoolIF wrapBufferPool;
	protected final StreamCharsetFamily streamCharsetFamily;

	protected final Charset defaultCharset;
	protected final CharsetEncoder defaultCharsetEncoder;
	protected final CharsetDecoder defaultCharsetDecoder;

	protected final int dataPacketBufferSize;
	protected final ByteOrder byteOrder;

	protected long position = 0L;
	protected long limit = 0L;
	protected final long capacity;

	protected final WrapBuffer wrapBufferArray[];
	protected final ByteBuffer byteBufferArray[];
	protected int lastBufferIndex = -1;

	public StreamBuffer(StreamCharsetFamily streamCharsetFamily, WrapBuffer sourceWrapBufferArray[],
			ByteBuffer sourceByteBufferArray[], long newLimit, WrapBufferPoolIF wrapBufferPool) {
		if (null == streamCharsetFamily) {
			throw new IllegalArgumentException("the parameter streamCharsetFamily is null");
		}

		if (null == sourceWrapBufferArray) {
			throw new IllegalArgumentException("the parameter sourceWrapBufferArray is null");
		}

		if (0 == sourceWrapBufferArray.length) {
			throw new IllegalArgumentException("the parameter sourceWrapBufferArray' length is zero");
		}

		if (null == sourceByteBufferArray) {
			throw new IllegalArgumentException("the parameter sourceByteBufferArray is null");
		}

		if (sourceByteBufferArray.length != sourceWrapBufferArray.length) {
			throw new IllegalArgumentException(
					"the parameter sourceWrapBufferArray's length is different from the parameter sourceByteBufferArray's length");
		}		

		if (null == wrapBufferPool) {
			throw new IllegalArgumentException("the parameter wrapBufferPool is null");
		}

		this.streamCharsetFamily = streamCharsetFamily;
		this.wrapBufferArray = sourceWrapBufferArray;
		this.byteBufferArray = sourceByteBufferArray;		
		this.wrapBufferPool = wrapBufferPool;

		defaultCharset = streamCharsetFamily.getCharset();
		defaultCharsetEncoder = streamCharsetFamily.getCharsetEncoder();
		defaultCharsetDecoder = streamCharsetFamily.getCharsetDecoder();

		byteOrder = wrapBufferPool.getByteOrder();
		dataPacketBufferSize = wrapBufferPool.getDataPacketBufferSize();
		capacity = dataPacketBufferSize * sourceWrapBufferArray.length;
		
		if (newLimit < 0) {
			throw new IllegalArgumentException("the parameter newLimit is less than zero");
		}

		if (newLimit > capacity) {
			String errorMessage = new StringBuilder().append("the parameter newLimit[").append(newLimit)
					.append("] is greater than capacity[").append(capacity).append("]").toString(); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		limit = newLimit;
		lastBufferIndex = sourceWrapBufferArray.length - 1;

		// sourceByteBufferArray[endtWorkingIndex].limit(endWorkingOffset + 1);

		/** 파라미터 wrapBufferArray 와 파라미터 byteBufferArray 의 유효성 검사 */
		for (int i = 0; i < sourceWrapBufferArray.length; i++) {
			if (null == sourceWrapBufferArray[i]) {
				String errorMessage = new StringBuilder().append("the parameter sourceWrapBufferArray[").append(i)
						.append("] is null").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (null == sourceByteBufferArray[i]) {
				String errorMessage = new StringBuilder().append("the parameter sourceByteBufferArray[").append(i)
						.append("] is null").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (sourceWrapBufferArray[i].getByteBuffer() != sourceByteBufferArray[i]) {
				String errorMessage = new StringBuilder().append("the parameter sourceWrapBufferArray[").append(i)
						.append("]'s ByteBuffer is the parameter sourceByteBufferArray[").append(i).append("]")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (0 != sourceByteBufferArray[i].position()) {
				String errorMessage = new StringBuilder().append("the parameter sourceByteBufferArray[").append(i)
						.append("]'s position[").append(sourceByteBufferArray[i].position()).append("] is not zero")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (sourceByteBufferArray[i].capacity() != sourceByteBufferArray[i].limit()) {
				String errorMessage = new StringBuilder().append("the parameter sourceByteBufferArray[").append(i)
						.append("]'s position[").append(sourceByteBufferArray[i].position()).append("] is not zero")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
	}

	public StreamBuffer(StreamCharsetFamily streamCharsetFamily, int maxOfWrapBuffer, WrapBufferPoolIF wrapBufferPool) {
		if (null == streamCharsetFamily) {
			throw new IllegalArgumentException("the parameter streamCharsetFamily is null");
		}

		if (maxOfWrapBuffer <= 0) {
			throw new IllegalArgumentException("the parameter maxOfWrapBuffer is less than or equal to zero");
		}

		if (null == wrapBufferPool) {
			throw new IllegalArgumentException("the parameter wrapBufferPool is null");
		}

		this.streamCharsetFamily = streamCharsetFamily;
		this.wrapBufferPool = wrapBufferPool;

		defaultCharset = streamCharsetFamily.getCharset();
		defaultCharsetEncoder = streamCharsetFamily.getCharsetEncoder();
		defaultCharsetDecoder = streamCharsetFamily.getCharsetDecoder();

		byteOrder = wrapBufferPool.getByteOrder();
		dataPacketBufferSize = wrapBufferPool.getDataPacketBufferSize();

		capacity = (long) maxOfWrapBuffer * dataPacketBufferSize;
		limit = capacity;

		wrapBufferArray = new WrapBuffer[maxOfWrapBuffer];
		byteBufferArray = new ByteBuffer[maxOfWrapBuffer];
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
		
		/*
		// FIXME!		
		long oldEndPosition = (limit - 1);
		int bufferIndexForOldEndpostion = (int) (oldEndPosition / dataPacketBufferSize);
		
		long newEndPosition = (newLimit - 1);
		int bufferIndexForNewEndpostion = (int) (newEndPosition / dataPacketBufferSize);
		int bufferOffsetForNewEndPosition = (int) (newEndPosition % dataPacketBufferSize);
		
		ByteBuffer byteBufferForOldEndPostion = byteBufferArray[bufferIndexForOldEndpostion];
		ByteBuffer byteBufferForNewEndPostion = byteBufferArray[bufferIndexForNewEndpostion];
		
		byteBufferForOldEndPostion.limit(dataPacketBufferSize);
		byteBufferForNewEndPostion.limit(bufferOffsetForNewEndPosition + 1);
		*/

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

	public long remaining() {
		return limit - position;
	}

	public boolean hasRemaining() {
		return ((limit - position) == 0) ? true : false;
	}
	
	public void flip() {
		limit = position;
		position = 0;
	}
	
	public void clear() {
		limit = capacity;
		position = 0;
	}

	/**
	 * 읽기/쓰기 작업 하기전 필요한 버퍼를 확보를 목적으로 하는 메소드로 '읽기 혹은 쓰기 작업 완료 후 위치' 를 입력 받아 그 위치까지
	 * 필요한 버퍼를 확보한다.
	 * 
	 * @param size 읽기 혹은 쓰기 작업에 필요한 바이트수
	 * @throws NoMoreDataPacketBufferException
	 */
	protected void fillWrapBuffer(int newLastIndex) throws NoMoreDataPacketBufferException {
		if (newLastIndex < 0) {
			throw new IllegalArgumentException("the parameter newLastIndex is less than zero");
		}

		if (newLastIndex >= wrapBufferArray.length) {
			String errorMessage = new StringBuilder().append("the parameter newLastIndex[").append(newLastIndex)
					.append("] is greater than or equal to wrapBufferArray.length[").append(wrapBufferArray.length)
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (newLastIndex > lastBufferIndex) {
			for (int i = lastBufferIndex + 1; i <= newLastIndex; i++) {
				wrapBufferArray[i] = wrapBufferPool.pollDataPacketBuffer();
				byteBufferArray[i] = wrapBufferArray[i].getByteBuffer();
				byteBufferArray[i].order(byteOrder);
			}

			lastBufferIndex = newLastIndex;
		}
	}

	private void doPutByte(byte value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);

		// FIXME! debug
		// log.log(Level.INFO, new
		// StringBuilder().append("before::buffer[").append(bufferIndexOfStartPostion).append("]=").append(workingByteBuffer.toString()).toString());

		workingByteBuffer.put(value);

		/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
		workingByteBuffer.position(0);

		position++;
	}

	public void putByte(byte value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		if ((limit - position) < 1) {
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
		if ((limit - position) < 2) {
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

		if ((limit - position) < 2) {
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
			intBuffer = new byte[] { (byte) ((value & 0xff000000) >> 24), (byte) ((value & 0xff0000) >> 16),
					(byte) ((value & 0xff00) >> 8), (byte) (value & 0xff) };

		} else {
			intBuffer = new byte[] { (byte) (value & 0xff), (byte) ((value & 0xff00) >> 8),
					(byte) ((value & 0xff0000) >> 16), (byte) ((value & 0xff000000) >> 24) };
		}

		doPutBytes(intBuffer, 0, intBuffer.length);
	}

	public void putInt(int value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		if ((limit - position) < 4) {
			throw new BufferOverflowException();
		}

		/*
		 * byte t4 = (byte) (value & 0xff); byte t3 = (byte) ((value & 0xff00) >> 8);
		 * byte t2 = (byte) ((value & 0xff0000) >> 16); byte t1 = (byte) ((value &
		 * 0xff000000) >> 24);
		 * 
		 * if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) { doPutByte(t1); doPutByte(t2);
		 * doPutByte(t3); doPutByte(t4); } else { doPutByte(t4); doPutByte(t3);
		 * doPutByte(t2); doPutByte(t1); }
		 */

		final byte[] intBuffer;

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			intBuffer = new byte[] { (byte) ((value & 0xff000000) >> 24), (byte) ((value & 0xff0000) >> 16),
					(byte) ((value & 0xff00) >> 8), (byte) (value & 0xff) };

		} else {
			intBuffer = new byte[] { (byte) (value & 0xff), (byte) ((value & 0xff00) >> 8),
					(byte) ((value & 0xff0000) >> 16), (byte) ((value & 0xff000000) >> 24) };
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

		if ((limit - position) < 4) {
			throw new BufferOverflowException();
		}

		doPutInt((int) value);
	}

	public void putLong(long value) throws BufferOverflowException, NoMoreDataPacketBufferException {
		if ((limit - position) < 8) {
			throw new BufferOverflowException();
		}

		/*
		 * byte t8 = (byte) (value & 0xffL); byte t7 = (byte) ((value & 0xff00L) >> 8);
		 * byte t6 = (byte) ((value & 0xff0000L) >> 16); byte t5 = (byte) ((value &
		 * 0xff000000L) >> 24); byte t4 = (byte) ((value & 0xff00000000L) >> 32); byte
		 * t3 = (byte) ((value & 0xff0000000000L) >> 40); byte t2 = (byte) ((value &
		 * 0xff000000000000L) >> 48); byte t1 = (byte) ((value & 0xff00000000000000L) >>
		 * 56);
		 * 
		 * if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) { doPutByte(t1); doPutByte(t2);
		 * doPutByte(t3); doPutByte(t4); doPutByte(t5); doPutByte(t6); doPutByte(t7);
		 * doPutByte(t8); } else { doPutByte(t8); doPutByte(t7); doPutByte(t6);
		 * doPutByte(t5); doPutByte(t4); doPutByte(t3); doPutByte(t2); doPutByte(t1); }
		 */

		final byte[] intBuffer;

		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			intBuffer = new byte[] { (byte) ((value & 0xff00000000000000L) >> 56),
					(byte) ((value & 0xff000000000000L) >> 48), (byte) ((value & 0xff0000000000L) >> 40),
					(byte) ((value & 0xff00000000L) >> 32), (byte) ((value & 0xff000000L) >> 24),
					(byte) ((value & 0xff0000L) >> 16), (byte) ((value & 0xff00L) >> 8), (byte) (value & 0xffL) };

		} else {
			intBuffer = new byte[] { (byte) (value & 0xffL), (byte) ((value & 0xff00L) >> 8),
					(byte) ((value & 0xff0000L) >> 16), (byte) ((value & 0xff000000L) >> 24),
					(byte) ((value & 0xff00000000L) >> 32), (byte) ((value & 0xff0000000000L) >> 40),
					(byte) ((value & 0xff000000000000L) >> 48), (byte) ((value & 0xff00000000000000L) >> 56) };
		}

		doPutBytes(intBuffer, 0, 8);
	}

	private void doPutBytes(byte src[], int offset, int length)
			throws BufferOverflowException, NoMoreDataPacketBufferException {
		/*
		 * for (int i = 0; i < length; i++) { doPutByte(src[offset + i]); }
		 */

		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);

		do {

			int numberOfBytesRemainingInWorkBuffer = workingByteBuffer.remaining();
			if (length <= numberOfBytesRemainingInWorkBuffer) {
				workingByteBuffer.put(src, offset, length);
				position += length;
				/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
				workingByteBuffer.position(0);
				break;
			}

			workingByteBuffer.put(src, offset, numberOfBytesRemainingInWorkBuffer);

			/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);

			offset += numberOfBytesRemainingInWorkBuffer;
			length -= numberOfBytesRemainingInWorkBuffer;
			position += numberOfBytesRemainingInWorkBuffer;

			if (byteBufferArray.length == (bufferIndexOfStartPostion + 1)) {
				/** dead code::어디선가 position 과 limit 을 잘못 조작했을 경우 발생한다 */
				log.log(Level.SEVERE, "no more next buffer becase the var bufferIndexOfStartPostion is max");
				System.exit(1);
			}

			bufferIndexOfStartPostion++;
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
			if (null == workingByteBuffer) {
				fillWrapBuffer(bufferIndexOfStartPostion);
				workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
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

		if ((limit - position) < length) {
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

		if ((limit - position) < src.length) {
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

		if ((limit - position) < src.remaining()) {
			throw new BufferOverflowException();
		}

		/*
		 * do { doPutByte(src.get()); } while (src.hasRemaining());
		 */

		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);

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

			if (byteBufferArray.length == (bufferIndexOfStartPostion + 1)) {
				/** dead code::어디선가 position 과 limit 을 잘못 조작했을 경우 발생한다 */
				log.log(Level.SEVERE, "no more next buffer becase the var bufferIndexOfStartPostion is max");
				System.exit(1);
			}
			bufferIndexOfStartPostion++;
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
			if (null == workingByteBuffer) {
				fillWrapBuffer(bufferIndexOfStartPostion);
				workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
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
			String errorMessage = new StringBuilder().append("the parameter fixedLength[").append(fixedLength)
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

		if ((limit - position) < fixedLength) {
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
		 * for (int i = 0; i < strBytes.length; i++) { doPutByte(strBytes[i]); }
		 */
		doPutBytes(strBytes, 0, fixedLength);
	}

	public void putAllString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, CharsetEncoderException {
		putAllString(src, defaultCharset);
	}

	public void putAllString(String src, Charset wantedCharset) throws BufferOverflowException,
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

		if ((limit - position) < strBytes.length) {
			throw new BufferOverflowException();
		}

		/*
		 * for (int i = 0; i < strBytes.length; i++) { doPutByte(strBytes[i]); }
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

		if ((limit - position - 1) < strBytes.length) {
			throw new BufferOverflowException();
		}

		doPutByte((byte) strBytes.length);

		/*
		 * for (int i = 0; i < strBytes.length; i++) { doPutByte(strBytes[i]); }
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

		if ((limit - position - 2) < strBytes.length) {
			throw new BufferOverflowException();
		}

		doPutShort(strBytes.length);

		/*
		 * for (int i = 0; i < strBytes.length; i++) { doPutByte(strBytes[i]); }
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

		if ((limit - position - 4) < strBytes.length) {
			throw new BufferOverflowException();
		}

		doPutInt(strBytes.length);

		/*
		 * for (int i = 0; i < strBytes.length; i++) { doPutByte(strBytes[i]); }
		 */
		doPutBytes(strBytes, 0, strBytes.length);
	}

	private byte doGetByte() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);

		// FIXME! debug
		// log.log(Level.INFO, new
		// StringBuilder().append("before::buffer[").append(bufferIndexOfStartPostion).append("]=").append(workingByteBuffer.toString()).toString());

		byte returnByte = workingByteBuffer.get();

		/** 버퍼에 대한 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
		workingByteBuffer.position(0);

		position++;

		return returnByte;
	}

	public byte getByte() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if ((limit - position) < 1) {
			throw new BufferUnderflowException();
		}

		return doGetByte();
	}

	public short getUnsignedByte() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if ((limit - position) < 1) {
			throw new BufferUnderflowException();
		}

		final short retValue = (short) (doGetByte() & 0xff);
		return retValue;
	}

	public short getShort() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if ((limit - position) < 2) {
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
		if ((limit - position) < 2) {
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
		if ((limit - position) < 4) {
			throw new BufferUnderflowException();
		}

		/*
		 * final byte t1; final byte t2; final byte t3; final byte t4;
		 * 
		 * if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) { t1 = doGetByte(); t2 =
		 * doGetByte(); t3 = doGetByte(); t4 = doGetByte(); } else { t4 = doGetByte();
		 * t3 = doGetByte(); t2 = doGetByte(); t1 = doGetByte(); }
		 * 
		 * final int retValue = (((t1 & 0xff) << 24) | ((t2 & 0xff) << 16) | ((t3 &
		 * 0xff) << 8) | (t4 & 0xff));
		 * 
		 */
		final int retValue;
		final byte[] intBuffer = doGetBytes(4);
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			retValue = (((intBuffer[0] & 0xff) << 24) | ((intBuffer[1] & 0xff) << 16) | ((intBuffer[2] & 0xff) << 8)
					| (intBuffer[3] & 0xff));
		} else {
			retValue = (((intBuffer[3] & 0xff) << 24) | ((intBuffer[2] & 0xff) << 16) | ((intBuffer[1] & 0xff) << 8)
					| (intBuffer[0] & 0xff));
		}

		return retValue;
	}

	public long getUnsignedInt() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if ((limit - position) < 4) {
			throw new BufferUnderflowException();
		}

		/*
		 * final byte t1; final byte t2; final byte t3; final byte t4;
		 * 
		 * if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) { t1 = doGetByte(); t2 =
		 * doGetByte(); t3 = doGetByte(); t4 = doGetByte(); } else { t4 = doGetByte();
		 * t3 = doGetByte(); t2 = doGetByte(); t1 = doGetByte(); }
		 * 
		 * final long retValue = (((t1 & 0xffL) << 24) | ((t2 & 0xffL) << 16) | ((t3 &
		 * 0xffL) << 8) | (t4 & 0xffL));
		 */

		final long retValue;
		final byte[] intBuffer = doGetBytes(4);
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			retValue = (((intBuffer[0] & 0xffL) << 24) | ((intBuffer[1] & 0xffL) << 16) | ((intBuffer[2] & 0xffL) << 8)
					| (intBuffer[3] & 0xffL));
		} else {
			retValue = (((intBuffer[3] & 0xffL) << 24) | ((intBuffer[2] & 0xffL) << 16) | ((intBuffer[1] & 0xffL) << 8)
					| (intBuffer[0] & 0xffL));
		}

		return retValue;
	}

	public long getLong() throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if ((limit - position) < 8) {
			throw new BufferUnderflowException();
		}

		/*
		 * final byte t1; final byte t2; final byte t3; final byte t4; final byte t5;
		 * final byte t6; final byte t7; final byte t8;
		 * 
		 * if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) { t1 = doGetByte(); t2 =
		 * doGetByte(); t3 = doGetByte(); t4 = doGetByte(); t5 = doGetByte(); t6 =
		 * doGetByte(); t7 = doGetByte(); t8 = doGetByte(); } else { t8 = doGetByte();
		 * t7 = doGetByte(); t6 = doGetByte(); t5 = doGetByte(); t4 = doGetByte(); t3 =
		 * doGetByte(); t2 = doGetByte(); t1 = doGetByte(); }
		 * 
		 * final long retValue = (((t1 & 0xffL) << 56) | ((t2 & 0xffL) << 48) | ((t3 &
		 * 0xffL) << 40) | ((t4 & 0xffL) << 32) | ((t5 & 0xffL) << 24) | ((t6 & 0xffL)
		 * << 16) | ((t7 & 0xffL) << 8) | (t8 & 0xffL));
		 */
		final long retValue;
		final byte[] longBuffer = doGetBytes(8);
		if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
			retValue = (((longBuffer[0] & 0xffL) << 56) | ((longBuffer[1] & 0xffL) << 48)
					| ((longBuffer[2] & 0xffL) << 40) | ((longBuffer[3] & 0xffL) << 32)
					| ((longBuffer[4] & 0xffL) << 24) | ((longBuffer[5] & 0xffL) << 16) | ((longBuffer[6] & 0xffL) << 8)
					| (longBuffer[7] & 0xffL));
		} else {
			retValue = (((longBuffer[7] & 0xffL) << 56) | ((longBuffer[6] & 0xffL) << 48)
					| ((longBuffer[5] & 0xffL) << 40) | ((longBuffer[4] & 0xffL) << 32)
					| ((longBuffer[3] & 0xffL) << 24) | ((longBuffer[2] & 0xffL) << 16) | ((longBuffer[1] & 0xffL) << 8)
					| (longBuffer[0] & 0xffL));
		}

		return retValue;
	}

	private byte[] doGetBytes(int length) throws NoMoreDataPacketBufferException {
		byte dstBytes[] = new byte[length];
		int offset = 0;

		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);

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

			if (byteBufferArray.length == (bufferIndexOfStartPostion + 1)) {
				/** dead code::어디선가 position 과 limit 을 잘못 조작했을 경우 발생한다 */
				log.log(Level.SEVERE, "no more next buffer becase the var bufferIndexOfStartPostion is max");
				System.exit(1);
			}

			bufferIndexOfStartPostion++;
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
			if (null == workingByteBuffer) {
				fillWrapBuffer(bufferIndexOfStartPostion);
				workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
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

		if ((limit - position) < length) {
			throw new BufferUnderflowException();
		}

		/*
		 * byte dstBytes[] = new byte[length];
		 * 
		 * for (int i = 0; i < length; i++) { dstBytes[i] = doGetByte(); } return
		 * dstBytes;
		 */

		return doGetBytes(length);
	}
	
	private void doGetBytes(byte dst[], int offset, int length)
			throws BufferUnderflowException, NoMoreDataPacketBufferException {
		/*
		 * for (int i = 0; i < length; i++) { doPutByte(src[offset + i]); }
		 */

		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);

		do {

			int numberOfBytesRemainingInWorkBuffer = workingByteBuffer.remaining();
			if (length <= numberOfBytesRemainingInWorkBuffer) {
				workingByteBuffer.get(dst, offset, length);
				position += length;
				/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
				workingByteBuffer.position(0);
				break;
			}

			workingByteBuffer.get(dst, offset, numberOfBytesRemainingInWorkBuffer);

			/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);

			offset += numberOfBytesRemainingInWorkBuffer;
			length -= numberOfBytesRemainingInWorkBuffer;
			position += numberOfBytesRemainingInWorkBuffer;

			if (byteBufferArray.length == (bufferIndexOfStartPostion + 1)) {
				/** dead code::어디선가 position 과 limit 을 잘못 조작했을 경우 발생한다 */
				log.log(Level.SEVERE, "no more next buffer becase the var bufferIndexOfStartPostion is max");
				System.exit(1);
			}

			bufferIndexOfStartPostion++;
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
			if (null == workingByteBuffer) {
				fillWrapBuffer(bufferIndexOfStartPostion);
				workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
			}

		} while (true);

	}
	
	public void getBytes(byte[] dst, int offset, int length) throws BufferUnderflowException, NoMoreDataPacketBufferException {
		if (null == dst) {
			throw new IllegalArgumentException("the parameter dst is null");
		}
		
		if (offset < 0) {
			String errorMessage = new StringBuilder().append("the parameter offset[").append(offset)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (offset >= dst.length) {
			String errorMessage = new StringBuilder().append("the parameter offset[").append(offset)
					.append("] is greater than or equal to the parameter dst's length[")
					.append(dst.length)
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (length < 0) {
			String errorMessage = new StringBuilder().append("the parameter length[").append(length)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		long sumOfOffsetAndLength = ((long) offset + length);
		if (sumOfOffsetAndLength > dst.length) {
			String errorMessage = new StringBuilder().append("the sum[").append(sumOfOffsetAndLength)
					.append("] of the parameter offset[").append(offset).append("] and the parameter length[")
					.append(length).append("] is greater than the parameter dst's length[").append(dst.length)
					.append("]").toString();

			throw new IllegalArgumentException(errorMessage);
		}

		if ((limit - position) < length) {
			throw new BufferUnderflowException();
		}
		
		doGetBytes(dst, offset, length);
	}

	private String doGetString(int length, Charset stringCharset)
			throws BufferUnderflowException, CharsetDecoderException, NoMoreDataPacketBufferException {

		/*
		 * byte dstBytes[] = new byte[length]; for (int i = 0; i < length; i++) {
		 * dstBytes[i] = doGetByte(); }
		 */
		byte dstBytes[] = doGetBytes(length);

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

		if ((limit - position) < fixedLength) {
			throw new BufferUnderflowException();
		}

		if (null == wantedCharsetDecoder) {
			throw new IllegalArgumentException("the parameter wantedCharsetDecoder is null");
		}

		if (0 == fixedLength) {
			return "";
		}

		byte dstBytes[] = doGetBytes(fixedLength);
		ByteBuffer dstByteBuffer = ByteBuffer.wrap(dstBytes);

		CharBuffer dstCharBuffer = null;

		try {
			dstCharBuffer = wantedCharsetDecoder.decode(dstByteBuffer);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to get a new String. read data hex[")
					.append(HexUtil.getHexStringFromByteArray(dstBytes)).append("], charset[")
					.append(wantedCharsetDecoder.charset().name()).append("]").toString();

			throw new CharsetDecoderException(errorMessage);
		}

		return dstCharBuffer.toString();

	}

	public String getFixedLengthString(final int fixedLength) throws BufferUnderflowException, IllegalArgumentException,
			CharsetDecoderException, NoMoreDataPacketBufferException {
		return getFixedLengthString(fixedLength, defaultCharsetDecoder);
	}

	public String getAllString() throws IllegalStateException, IllegalArgumentException, CharsetDecoderException,
			NoMoreDataPacketBufferException {
		return getAllString(defaultCharset);
	}

	public String getAllString(Charset wantedCharset) throws IllegalStateException, IllegalArgumentException,
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
					.append("]가  '자바 문자열 최대 크기'(=Integer.MAX) 보다 큽니다").toString();
			log.log(Level.INFO, errorMessage);
			throw new IllegalStateException(errorMessage);
		}

		// return getFixedLengthString((int) numberOfBytesRemaining,
		// wantedCharset.newDecoder());
		return doGetString((int) numberOfBytesRemaining, wantedCharset);

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

		if ((limit - position) < length) {
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

		if ((limit - position) < length) {
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

		if ((limit - position) < length) {
			throw new BufferUnderflowException();
		}

		if (0 == length) {
			return "";
		}

		return doGetString(length, wantedCharset);
	}

	public void skip(long n) throws NoMoreDataPacketBufferException {
		if (n <= 0) {
			String errorMessage = new StringBuilder().append("the parameter n[").append(n)
					.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if ((limit - position) < n) {
			String errorMessage = new StringBuilder().append("the parameter n[").append(n)
					.append("] is greater than remaing bytes[").append(limit - position).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		position += n;
	}

	/**
	 * @return 버퍼 position 부터 (limit - position) 양 만큼 MD5 한 결과
	 * @throws NoMoreDataPacketBufferException
	 */
	public byte[] getMD5WithoutChange(long size) throws NoMoreDataPacketBufferException {
		if (size < 0) {
			throw new IllegalArgumentException("the parameter size is less than zero");
		}

		if (size > remaining()) {
			throw new IllegalArgumentException("the parameter size is greater than remaing bytes");
		}

		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (0 == size) {
			return md5.digest();
		}

		long endPosition = position + size - 1;

		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		int bufferIndexOfEndPostion = (int) (endPosition / dataPacketBufferSize);
		int bufferOffsetOfEndPostion = (int) (endPosition % dataPacketBufferSize);

		ByteBuffer endWorkingByteBuffer = byteBufferArray[bufferIndexOfEndPostion];
		if (null == endWorkingByteBuffer) {
			fillWrapBuffer(bufferIndexOfEndPostion);
			endWorkingByteBuffer = byteBufferArray[bufferIndexOfEndPostion];
		}

		endWorkingByteBuffer.limit(bufferOffsetOfEndPostion + 1);

		/**
		 * INFO! 변수 endWorkingIndex 까지 버퍼들이 채워 졌기때문에 변수 bufferIndexOfStartPostion 를 갖는 버퍼는 무조건
		 * 존재한다
		 */
		ByteBuffer startWorkingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		startWorkingByteBuffer.position(bufferOffsetOfStartPostion);

		for (int i = bufferIndexOfStartPostion; i <= bufferIndexOfEndPostion; i++) {
			ByteBuffer currentWorkingByteBuffer = byteBufferArray[i];

			md5.update(currentWorkingByteBuffer);

			/** 버퍼 속성 복귀 */
			currentWorkingByteBuffer.position(0);
			currentWorkingByteBuffer.limit(dataPacketBufferSize);
		}

		return md5.digest();
	}

	/**
	 * 단위 테스트용 특수 메소드로 (1) '마지막으로 할당된 버퍼의 인덱스' 의 유효성 검사와 (2) 읽기 혹은 쓰기 작업 완료후 작업 할때
	 * 변경한 '위치 속성 값'을 처음 상태인 0 값으로 바르게 복원했는지 여부를 검사한다. 바이트 버퍼 배열을 검사하여 (1) '마지막으로
	 * 할당된 버퍼의 인덱스' 보다 큰 인덱스를 갖는 할당된 버퍼가 존재할 경우와 (2) '위치 속성 값' 이 0이 아닌 경우 예외를 던진다.
	 * 
	 * @throws IllegalStateException
	 */
	public void checkValid() throws IllegalStateException {
		/*
		 * System.out.
		 * printf("capacity=[%d], limit=[%d], bufferIndexForEndpostion=[%d], bufferOffsetForEndPosition=[%d]"
		 * , capacity, limit, bufferIndexForEndpostion, bufferOffsetForEndPosition);
		 * System.out.println();
		 */

		for (int i = 0; i < byteBufferArray.length; i++) {
			if (null != byteBufferArray[i]) {
				if (i > lastBufferIndex) {
					String errorMessage = new StringBuilder()
							.append("'마지막으로 할당된 버퍼의 인덱스' 가 잘못되었습니다, '마지막으로 할당된 버퍼의 인덱스'[").append(lastBufferIndex)
							.append("] 보다 큰 인덱스 [").append(i).append("] 를 갖는 할당된 버퍼가 존재합니다").toString();
					throw new IllegalStateException(errorMessage);
				}

				if (byteBufferArray[i].position() != 0) {
					String errorMessage = new StringBuilder().append("인덱스 [").append(i)
							.append("] 를 갖는 할당된 버퍼의 'position 속성 값'[").append(byteBufferArray[i].position())
							.append("]이 0 이 아닙니다").toString();
					throw new IllegalStateException(errorMessage);
				}

				if (byteBufferArray[i].limit() != dataPacketBufferSize) {
					String errorMessage = new StringBuilder().append("인덱스 [").append(i)
							.append("] 를 갖는 할당된 버퍼의 'limit 속성 값'[").append(byteBufferArray[i].limit())
							.append("]이 버퍼의 'capacity 속성 값'[").append(dataPacketBufferSize)
							.append("] 과 다릅니다").toString();
					throw new IllegalStateException(errorMessage);
				}
			}
		}
	}

	public int read(SocketChannel readableSocketChannel)
			throws IOException, BufferUnderflowException, NoMoreDataPacketBufferException {
		if (null == readableSocketChannel) {
			throw new IllegalArgumentException("the parameter readableSocketChannel is null");
		}

		if (position == limit) {
			throw new BufferUnderflowException();
		}

		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);		
		
		final int n;
		
		if (remaining() < workingByteBuffer.remaining()) {
			int oldLimit = workingByteBuffer.limit();
			workingByteBuffer.limit(bufferOffsetOfStartPostion + (int)remaining());
			
			n = readableSocketChannel.read(workingByteBuffer);
			
			/** 버퍼에 대한 소켓 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);
			workingByteBuffer.limit(oldLimit);
			
			if (n > 0) {
				position += n;
			}			
			
		} else {
			n = readableSocketChannel.read(workingByteBuffer);
			
			/** 버퍼에 대한 소켓 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);
			
			if (n > 0) {
				position += n;
			}
		}

		return n;
	}

	public int write(SocketChannel writableSocketChannel)
			throws IOException, BufferOverflowException, NoMoreDataPacketBufferException {
		if (null == writableSocketChannel) {
			throw new IllegalArgumentException("the parameter writableSocketChannel is null");
		}

		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);
		
			
		final int n;
		
		if (remaining() < workingByteBuffer.remaining()) {
			int oldLimit = workingByteBuffer.limit();
			workingByteBuffer.limit(bufferOffsetOfStartPostion + (int)remaining());
			
			n = writableSocketChannel.write(workingByteBuffer);
			
			/** 버퍼에 대한 소켓 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);
			workingByteBuffer.limit(oldLimit);
			
			position += n;
		} else {
			n = writableSocketChannel.write(workingByteBuffer);
			
			/** 버퍼에 대한 소켓 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
			workingByteBuffer.position(0);
			
			position += n;
		}
		

		return n;
	}
	
	/**
	 * 마지막 버퍼의 limit 속성 값을 {@link #limit} 속성 값에 맞추어 설정한다. WARNING! 빠른 처리를 위하여 에러 처리 루틴 생략, 
	 * 소켓을 통해 보낼 메시지 내용을 담고 있는 경우에 호출된다.    
	 */
	public void setLastBufferLimitUsingLimit() {
		long endPosition = limit - 1;
		int bufferIndexOfEndPostion = (int) (endPosition / dataPacketBufferSize);
		int bufferOffsetOfEndPostion = (int) (endPosition % dataPacketBufferSize);
		
		/**
		 * WARNING! 빠른 처리를 위하여 byteBufferArray[bufferIndexOfEndPostion] 이 null 일 경우 처리 루틴 생략, 메시지  
		 */
		byteBufferArray[bufferIndexOfEndPostion].limit(bufferOffsetOfEndPostion + 1);
	}

	public void releaseAllWrapBuffers() {
		// FIXME!
		// log.log(Level.INFO, "call", new Throwable());

		for (int i = 0; i <= lastBufferIndex; i++) {
			wrapBufferPool.putDataPacketBuffer(wrapBufferArray[i]);
			wrapBufferArray[i] = null;
			byteBufferArray[i] = null;
		}

		lastBufferIndex = -1;
	}

	public String toHexStringForRemaingData() {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i <= lastBufferIndex; i++) {
			builder.append(HexUtil.getHexStringForRemaingOfByteBuffer(byteBufferArray[i]));
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		final int maxLen = 5;
		StringBuilder builder = new StringBuilder();
		builder.append("StreamBuffer [defaultCharset=");
		builder.append(defaultCharset.name());
		builder.append(", dataPacketBufferSize=");
		builder.append(dataPacketBufferSize);
		builder.append(", byteOrder=");
		builder.append(byteOrder);
		builder.append(", position=");
		builder.append(position);
		builder.append(", limit=");
		builder.append(limit);
		builder.append(", capacity=");
		builder.append(capacity);
		builder.append(", wrapBufferArray=");
		builder.append(wrapBufferArray != null
				? Arrays.asList(wrapBufferArray).subList(0, Math.min(wrapBufferArray.length, maxLen))
				: null);
		builder.append(", byteBufferArray=");
		builder.append(byteBufferArray != null
				? Arrays.asList(byteBufferArray).subList(0, Math.min(byteBufferArray.length, maxLen))
				: null);
		builder.append(", lastIndex=");
		builder.append(lastBufferIndex);
		builder.append("]");
		return builder.toString();
	}
}
