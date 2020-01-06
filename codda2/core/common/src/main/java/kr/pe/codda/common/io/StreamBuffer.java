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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.CharsetDecoderException;
import kr.pe.codda.common.exception.CharsetEncoderException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.util.HexUtil;

/**
 * <pre>
 * 소켓 읽기 혹은 쓰기에 사용되어 지는 '스트림 버퍼'.
 *  
 * 스트림 버퍼 용량은 정해져 있으며 그 용량 안에서 스트림 버퍼 크기를 정할 수 있고
 * 스트림 버퍼 용량을 감당할 만큼의 크기를 갖는 랩 버퍼 배열이 있고 배열 원소인 랩 버퍼는 필요할때  앞에서 부터 꽉 차게 채워진다.
 * 스트림 버퍼로 운영할 핵심 멤버 변수로는 스트림 버퍼 '위치', '크기', '용량', 
 * '랩 버퍼 배열', ' 바이트 버퍼 배열', '랩 버퍼 폴' 이 있다.
 * 
 * 생성자는 외부로 부터 랩 버퍼 배열을 받는냐 자체 생산하냐 이렇게 2가지 종류가 있으며 
 * 첫번째는 랩 버퍼를 자체 생산하는 {@link #StreamBuffer(StreamCharsetFamily, int, WrapBufferPoolIF)} 이고
 * 마지막 두번째는 외부로 부터 랩 버퍼 배열을 받는 {@link #StreamBuffer(StreamCharsetFamily, WrapBuffer[], ByteBuffer[], long, WrapBufferPoolIF)} 이 있다.
 *  
 * '스트림 버퍼' 구현의 핵심 아이디어는 위치 기반으로 한 입출력이다. 
 * 이것을 위한 전제 조건이 있는데 스트림 버퍼 '용량' 안에 놓인 바이트 버퍼는 입출력이 자유로운 백지 상태이어야 한다.  
 * 백지 상태라 함은 예를 들면 바이트 버퍼의 position 속성 값은 0 이 되고 
 * 바이트 버퍼의 limit 속성 값은 스트림 버퍼 '용량' 에 의해서 정해지는데 
 * 대부분은 limit 속성 값으로 바이트 버퍼의 capacity 값을 갖는다.
 * 스트림 버퍼 '위치'로 부터 읽기/쓰기 작업할 (1) 바이트 버퍼의 인덱스와 (2) 바이트버퍼 내 위치를 얻어 
 * 바이트 버퍼의 position 속성을 (2) 항에서 얻은 위치로 이동하여 입출력 작업을 수행한다.   
 *  
 * 참고1) 랩 버퍼는 공유 자원으로 랩 버퍼 폴로 부터 얻을 있고 
 * 반듯이 쓰임을 다한 후에는 랩 버퍼 폴로 반환을 해 주어야 한다.
 * 
 * 참고2) 필요할때 마다 채워진다는 말은 예를 들면 랩 버퍼 1개의 용량이 1024 이고 최대 10개로 지정하여 '스트림 버퍼' 를 생성하면
 * 스트림 버퍼의 용량은 1024 * 10 = 10240 byte 로 정해지며
 * '스트림 버퍼' 위치는 0 으로 초기화 되고 크기는 용량과 같은 10240 byte 로 정해진다.
 * 그리고 랩 버퍼 배열과 바이트 배열은  크기가 10 인 배열로 초기화 된다.
 * 초기화된 상태라  배열의 원소는 모두 null 값을 갖는다.
 * 만약 초기 상태에서 '스트림 버퍼' 위치를 1055 로 설정하고 데이터를 쓰거라 혹은 읽은 경우
 * '스트림 버퍼' 의 랩버퍼 배열 인엑스 0과 1에 각각 랩버퍼 폴로 부터 랩버퍼를 받아 할당되고
 * 위치 1055 는 해당하는 인덱스는 1055/1024=1 이 되고
 * 버퍼내 위치는 1055%1024=31가 되어 
 * 인덱스 1의 바이트 버퍼의 positon 속성값을 31 로 설정하여 그곳에서 데이터를 쓰거나 읽게 됩니다.
 *  
 * 참고3) WARNING! this class is not thread-safe class.
 * </pre>
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

	private final long startTime;

	/**
	 * 외부에서 랩 버퍼 배열을 공수하는 생성자
	 * 
	 * @param streamCharsetFamily   문자셋과 문자셋 인코더/디코더 묶음
	 * @param sourceWrapBufferArray 랩 버퍼 배열
	 * @param sourceByteBufferArray 바이트 버퍼 배열 , 참고) 랩버퍼에서 파생된 바이트 버퍼가 아닐 경우 에러 처리됨.
	 * @param newLimit              스트림 크기를 지정한다, 스트림 크기는 0에서 부터 파라미터 '랩 버퍼 배열' 크기 *
	 *                              바이트 버퍼 크기까지 가능하다
	 * @param wrapBufferPool        랩 버퍼 폴
	 * @throws IllegalArgumentException 파라미터 값이 잘못된 경우 던지는 예외, 예) 바이트 버퍼가 1:1 대응하는 랩
	 *                                  버퍼에 종속되어 있지 않을 경우 던지는 예외
	 */
	public StreamBuffer(StreamCharsetFamily streamCharsetFamily, WrapBuffer sourceWrapBufferArray[],
			ByteBuffer sourceByteBufferArray[], long newLimit, WrapBufferPoolIF wrapBufferPool)
			throws IllegalArgumentException {
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

		startTime = System.nanoTime();
	}

	/**
	 * 파라미터 'maxOfWrapBuffer' 에 맞추어 배열 크기를 갖는 랩 버퍼 배열을 자체 생성하는 생성자
	 * 
	 * @param streamCharsetFamily 문자셋과 문자셋 인코더/디코더 묶음
	 * @param maxNumberOfWrapBuffer     랩버퍼 최대 갯수
	 * @param wrapBufferPool      랩 버퍼 폴
	 * @throws IllegalArgumentException 파라미터 값이 잘못된 경우에 던지는 예외
	 */
	public StreamBuffer(StreamCharsetFamily streamCharsetFamily, int maxNumberOfWrapBuffer, WrapBufferPoolIF wrapBufferPool)
			throws IllegalArgumentException {
		if (null == streamCharsetFamily) {
			throw new IllegalArgumentException("the parameter streamCharsetFamily is null");
		}

		if (maxNumberOfWrapBuffer <= 0) {
			throw new IllegalArgumentException("the parameter maxNumberOfWrapBuffer is less than or equal to zero");
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

		capacity = (long) maxNumberOfWrapBuffer * dataPacketBufferSize;
		limit = capacity;

		wrapBufferArray = new WrapBuffer[maxNumberOfWrapBuffer];
		byteBufferArray = new ByteBuffer[maxNumberOfWrapBuffer];

		startTime = System.nanoTime();
	}

	/**
	 * @return 버퍼 위치
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * 파라미터 'newPosition' 를 새 위치로 '스트림 버퍼'의 위치를 설정한다.
	 * 
	 * @param newPosition 새 위치
	 * @throws IllegalArgumentException 파라미터 'newPosition' 값이 잘못되었을 경우 던지는 예외
	 */
	public void setPosition(long newPosition) throws IllegalArgumentException {
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

	/**
	 * @return 버퍼 크기
	 */
	public long getLimit() {
		return limit;
	}

	/**
	 * 파라미터 'newLimit' 를 '스트림 버퍼'의 새로운 크기로 설정한다.
	 * 
	 * @param newLimit 새로운 버퍼 크기
	 * @throws IllegalArgumentException 파라미터 'newLimit' 값이 잘못되었을 경우 던지는 예외
	 */
	public void setLimit(long newLimit) throws IllegalArgumentException {
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

	/**
	 * @return 버퍼 용량
	 */
	public long getCapacity() {
		return capacity;
	}

	/**
	 * @return 스트림 바이트 오더
	 */
	public ByteOrder getByteOder() {
		return byteOrder;
	}

	/**
	 * @return 스트림 문자셋
	 */
	public Charset getCharset() {
		return defaultCharset;
	}

	/**
	 * @return 남아 있는 용량, 남아 있는 용량은 버퍼크기(=limit) 에 현재 위치(=position)를 뺀 수이다.
	 */
	public long remaining() {
		return limit - position;
	}

	/**
	 * @return 남은 용량이 있는 경우 참을 남은 용량이 없으면 거짓을 반환
	 */
	public boolean hasRemaining() {
		return ((limit - position) != 0);
	}

	/**
	 * Flips this buffer. The limit is set to the current position and then the
	 * position is set to zero.
	 */
	public void flip() {
		limit = position;
		position = 0;
	}

	/**
	 * 버퍼 상태를 클리어 한다.
	 */
	public void clear() {
		limit = capacity;
		position = 0;
	}

	/**
	 * <pre>
	 * '스트림 버퍼' 에서 읽기 혹은 쓰기 대상 버퍼의 인덱스인 파라미터 'ioDemandIndex' 까지 랩 버퍼가 미 확보 되었다면 
	 * '마지막 랩 버퍼 인덱스' 이후 부터 파라미터 'ioDemandIndex'까지 랩 버퍼를 채워 넣는다.
	 * 이러한 작업은 '마지막 버퍼 인덱스' 는 랩 버퍼를 확보한 마지막 인덱스임을 보장하며 수행된다.
	 * 이렇게   '마지막 버퍼 인덱스' 는 랩 버퍼를 확보한 마지막 인덱스임을 보장하는 이유는 
	 * 메소드 {@link #releaseAllWrapBuffers} 에서 처음부터 '마지막 버퍼 인덱스' 까지 있는 랩 버퍼를 랩 버퍼 폴에 반환하기 때문이다.
	 * </pre>
	 * 
	 * @param ioDemandIndex 읽기 혹은 쓰기 작업할 스트림 버퍼 인덱스
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	private void fillWrapBuffer(int ioDemandIndex) throws NoMoreWrapBufferException {
		if (ioDemandIndex < 0) {
			throw new IllegalArgumentException("the parameter newLastIndex is less than zero");
		}

		if (ioDemandIndex >= wrapBufferArray.length) {
			String errorMessage = new StringBuilder().append("the parameter newLastIndex[").append(ioDemandIndex)
					.append("] is greater than or equal to wrapBufferArray.length[").append(wrapBufferArray.length)
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		while (lastBufferIndex < ioDemandIndex) {
			final WrapBuffer WrapBuffer = wrapBufferPool.pollDataPacketBuffer();

			lastBufferIndex++;

			wrapBufferArray[lastBufferIndex] = WrapBuffer;
			byteBufferArray[lastBufferIndex] = wrapBufferArray[lastBufferIndex].getByteBuffer();
			byteBufferArray[lastBufferIndex].order(byteOrder);
		}

	}

	/**
	 * '스트림 버퍼'에 파라미터 'byte 타입 값'을 실질적으로 저장한다.
	 * 
	 * @param value byte 타입 값
	 * @throws BufferOverflowException         바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	private void doPutByte(byte value) throws BufferOverflowException, NoMoreWrapBufferException {
		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);

		workingByteBuffer.put(value);

		/** 버퍼에 대한 쓰기 작업 완료 후 버퍼 위치 속성 초기화 */
		workingByteBuffer.position(0);

		position++;
	}

	/**
	 * '스트림 버퍼'에 파라미터 'byte 타입 값'을 저장한다.
	 * 
	 * @param value byte 타입 값
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public void putByte(byte value) throws BufferOverflowException, NoMoreWrapBufferException {
		if ((limit - position) < 1) {
			throw new BufferOverflowException();
		}

		doPutByte(value);
	}

	/**
	 * '스트림 버퍼'에 파라미터 'short 타입 값'을 unsigned byte 타입으로 저장한다.
	 * 
	 * @param value unsigned byte 타입 값 범위를 갖는 'short 타입 값'
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 'short 타입 값' 이 unsigned byte
	 *                                         type 에 부함되지 않았을 경우 던지는 예외
	 */
	public void putUnsignedByte(short value)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'int 타입 값'을 unsigned byte 타입으로 저장한다.
	 * 
	 * @param value unsigend byte 타입 값 범위를 갖는 'int 타입 값'
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 'int 타입 값' 이 unsigned byte 타입 에
	 *                                         부함되지 않았을 경우 던지는 예외
	 */
	public void putUnsignedByte(int value)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'long 타입 값'을 unsigned byte 타입으로 저장한다.
	 * 
	 * @param value unsigend byte 타입 값 범위를 갖는 'long 타입 값'
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 'long 타입 값' 이 unsigned byte 타입 에
	 *                                         부함되지 않았을 경우 던지는 예외
	 */
	public void putUnsignedByte(long value)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'int 타입 값'을 short 타입으로 실질적으로 저장한다. WARNING! 이 메소드는 '스트림 버퍼'에서
	 * 실질적으로 short 타입 값 쓰기를 수행하는 공통 모듈로 파라미터 검사를 위임하므로 파라미터 값 검사가 없다
	 * 
	 * @param value short 타입 값 범위를 갖는 int 타입 값
	 * @throws BufferOverflowException         바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	private void doPutShort(int value) throws BufferOverflowException, NoMoreWrapBufferException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'short 타입 값'을 저장한다, 내부적으로는 1 바이트씩 쪼개서 저장함.
	 * 
	 * @param value short 타입 값
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public void putShort(short value) throws BufferOverflowException, NoMoreWrapBufferException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'int 타입 값'을 unsigned short 타입으로 저장한다.
	 * 
	 * @param value unsigned short 타입 값 범위를 갖는 'int 타입 값'
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 'int 타입 값' 이 unsigned short 타입 에
	 *                                         부함되지 않았을 경우 던지는 예외
	 */
	public void putUnsignedShort(int value)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'long 타입 값'을 unsigned short 타입으로 저장한다.
	 * 
	 * @param value unsigned short 타입 값 범위를 갖는 'long 타입 값'
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 'long 타입 값' 이 unsigned short 타입
	 *                                         에 부함되지 않았을 경우 던지는 예외
	 */
	public void putUnsignedShort(long value)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'int 타입 값'을 실질적으로 저장한다.
	 * 
	 * @param value int 타입 값
	 * @throws BufferOverflowException         바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	private void doPutInt(int value) throws BufferOverflowException, NoMoreWrapBufferException {

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

	/**
	 * '스트림 버퍼'에 파라미터 'int 타입 값'을 저장한다.
	 * 
	 * @param value int 타입 값
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public void putInt(int value) throws BufferOverflowException, NoMoreWrapBufferException {
		if ((limit - position) < 4) {
			throw new BufferOverflowException();
		}

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

	/**
	 * '스트림 버퍼'에 파라미터 'long 타입 값'을 unsigned int 타입으로 저장한다.
	 * 
	 * @param value unsigned int 타입 값의 범위를 갖는 long 타입 값
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 'long 타입 값' 이 unsigned int 타입에
	 *                                         부합되지 않을때 던지는 예외
	 */
	public void putUnsignedInt(long value)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'long 타입 값'을 저장한다.
	 * 
	 * @param value long 타입 값
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public void putLong(long value) throws BufferOverflowException, NoMoreWrapBufferException {
		if ((limit - position) < 8) {
			throw new BufferOverflowException();
		}

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

	/**
	 * '스트림 버퍼'에 파라미터 'src' 배열에서 파라미터 'offset' 부터 시작하여 파라미터 'length' 만큼 실질적으로 저장한다.
	 * WARNING! 이 메소드는 '스트림 버퍼'에서 실질적으로 배열의 일부분에 대한 쓰기를 수행하는 공통 모듈로 파라미터 검사를 위임하므로
	 * 파라미터 값 검사가 없다
	 * 
	 * @param src    저장할 데이터가 담긴 바이트 배열
	 * @param offset 배열내 저장할 시작 위치
	 * @param length 저장을 원하는 길이
	 * @throws BufferOverflowException         바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	private void doPutBytes(byte src[], int offset, int length)
			throws BufferOverflowException, NoMoreWrapBufferException {

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

	/**
	 * '스트림 버퍼'에 파라미터 'src' 배열에서 파라미터 'offset' 부터 시작하여 파라미터 'length' 만큼 저장한다.
	 * 
	 * @param src    저장할 데이터가 담긴 바이트 배열
	 * @param offset 배열내 저장할 시작 위치
	 * @param length 저장을 원하는 길이
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public void putBytes(byte[] src, int offset, int length)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'src' 배열 전체를 저장한다.
	 * 
	 * @param src 저장할 데이터가 담긴 바이트 배열
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public void putBytes(byte[] src)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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
	 * @param src 저장할 데이터가 담긴 바이트 배열
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public void putBytes2(byte[] src)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'src' ByteBuffer 전체를 저장한다.
	 * 
	 * @param src 저장할 데이터가 담긴 ByteBuffer
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public void putBytes(ByteBuffer src)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException {
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

	/**
	 * '스트림 버퍼'에 파라미터 'fixedLength' 만큼의 고정 크기로 파마티터 'src' 문자열을 저장한다.
	 * 
	 * @param fixedLength 고정 길이
	 * @param src         저장할 문자열
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putFixedLengthString(int fixedLength, String src) throws BufferOverflowException,
			NoMoreWrapBufferException, IllegalArgumentException, CharsetEncoderException {
		putFixedLengthString(fixedLength, src, defaultCharsetEncoder);
	}

	/**
	 * '스트림 버퍼'에 파라미터 'fixedLength' 만큼의 고정 크기로 파마티터 'src' 문자열을 저장한다.
	 * 
	 * @param fixedLength          고정 길이
	 * @param src                  저장할 문자열
	 * @param wantedCharsetEncoder 저장을 원하는 문자열
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, NoMoreWrapBufferException, IllegalArgumentException,
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

		doPutBytes(strBytes, 0, fixedLength);
	}

	/**
	 * '스트림 버퍼'에 파마티터 'src' 문자열을 저장한다.
	 * 
	 * @param src 저장할 문자열
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putAllString(String src) throws BufferOverflowException, NoMoreWrapBufferException,
			IllegalArgumentException, CharsetEncoderException {
		putAllString(src, defaultCharset);
	}

	/**
	 * '스트림 버퍼'에 파마티터 'src' 문자열을 파라미터 'wantedCharset' 문자셋으로 저장한다.
	 * 
	 * @param src           저장할 문자열
	 * @param wantedCharset 저장을 원하는 문자젯
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putAllString(String src, Charset wantedCharset) throws BufferOverflowException,
			NoMoreWrapBufferException, IllegalArgumentException, CharsetEncoderException {
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

		doPutBytes(strBytes, 0, strBytes.length);
	}

	/**
	 * '스트림 버퍼'에 파마티터 'src' 문자열을 파라미터 'wantedCharset' 문자셋을 갖는 UnsignedByte 파스칼 문자열로
	 * 저장한다.
	 * 
	 * @param src           저장할 문자열
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putUBPascalString(String src) throws BufferOverflowException, NoMoreWrapBufferException,
			IllegalArgumentException, CharsetEncoderException {
		putUBPascalString(src, defaultCharset);
	}

	/**
	 * '스트림 버퍼'에 파마티터 'src' 문자열을 파라미터 'wantedCharset' 문자셋을 갖는 UnsignedByte 파스칼 문자열로
	 * 저장한다.
	 * 
	 * @param src           저장할 문자열
	 * @param wantedCharset 원하는 문자셋
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putUBPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreWrapBufferException, CharsetEncoderException {
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
			String errorMessage = new StringBuilder().append("the length[").append(strBytes.length)
					.append("] of bytes encoding the parameter src as a charset[").append(wantedCharset.name())
					.append("] is greater than the unsigned byte max[").append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("]").toString();

			throw new IllegalArgumentException(errorMessage);
		}

		if ((limit - position - 1) < strBytes.length) {
			throw new BufferOverflowException();
		}

		doPutByte((byte) strBytes.length);

		doPutBytes(strBytes, 0, strBytes.length);
	}

	/**
	 * '스트림 버퍼'에 파마티터 'src' 문자열을 파라미터 'wantedCharset' 문자셋을 갖는 UnsignedShort 파스칼 문자열로
	 * 저장한다.
	 * 
	 * @param src           저장할 문자열
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putUSPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreWrapBufferException, CharsetEncoderException {
		putUSPascalString(src, defaultCharset);
	}

	/**
	 * '스트림 버퍼'에 파마티터 'src' 문자열을 파라미터 'wantedCharset' 문자셋을 갖는 UnsignedShort 파스칼 문자열로
	 * 저장한다.
	 * 
	 * @param src           저장할 문자열
	 * @param wantedCharset 원하는 문자셋
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putUSPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreWrapBufferException, CharsetEncoderException {
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
			String errorMessage = new StringBuilder().append("the length[").append(strBytes.length)
					.append("] of bytes encoding the parameter src as a charset[").append(wantedCharset.name())
					.append("] is greater than the unsigned short max[")
					.append(CommonStaticFinalVars.UNSIGNED_SHORT_MAX).append("]").toString();

			throw new IllegalArgumentException(errorMessage);
		}

		if ((limit - position - 2) < strBytes.length) {
			throw new BufferOverflowException();
		}

		doPutShort(strBytes.length);

		doPutBytes(strBytes, 0, strBytes.length);
	}

	/**
	 * '스트림 버퍼'에 파마티터 'src' 문자열을 파라미터 'wantedCharset' 문자셋을 갖는 Integer 파스칼 문자열로 저장한다.
	 * 
	 * @param src           저장할 문자열
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putSIPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreWrapBufferException, CharsetEncoderException {
		putSIPascalString(src, defaultCharset);
	}

	/**
	 * '스트림 버퍼'에 파마티터 'src' 문자열을 파라미터 'wantedCharset' 문자셋을 갖는 Integer 파스칼 문자열로 저장한다.
	 * 
	 * @param src           저장할 문자열
	 * @param wantedCharset 원하는 문자셋
	 * @throws BufferOverflowException         '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 오버프로시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetEncoderException         문자셋 인코딩 실패시 던지는 예외
	 */
	public void putSIPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreWrapBufferException, CharsetEncoderException {
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

		doPutBytes(strBytes, 0, strBytes.length);
	}

	/**
	 * @return byte 타입 값
	 * @throws BufferUnderflowException        바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	private byte doGetByte() throws BufferUnderflowException, NoMoreWrapBufferException {
		int bufferIndexOfStartPostion = (int) (position / dataPacketBufferSize);
		int bufferOffsetOfStartPostion = (int) (position % dataPacketBufferSize);

		ByteBuffer workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		if (null == workingByteBuffer) {
			fillWrapBuffer(bufferIndexOfStartPostion);
			workingByteBuffer = byteBufferArray[bufferIndexOfStartPostion];
		}

		workingByteBuffer.position(bufferOffsetOfStartPostion);

		byte returnByte = workingByteBuffer.get();

		/** 버퍼에 대한 읽기 작업 완료 후 버퍼 위치 속성 초기화 */
		workingByteBuffer.position(0);

		position++;

		return returnByte;
	}

	/**
	 * @return byte 타입 값
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public byte getByte() throws BufferUnderflowException, NoMoreWrapBufferException {
		if ((limit - position) < 1) {
			throw new BufferUnderflowException();
		}

		return doGetByte();
	}

	/**
	 * @return unsigned byte 타입 값
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public short getUnsignedByte() throws BufferUnderflowException, NoMoreWrapBufferException {
		if ((limit - position) < 1) {
			throw new BufferUnderflowException();
		}

		final short retValue = (short) (doGetByte() & 0xff);
		return retValue;
	}

	/**
	 * @return short 타입 값
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public short getShort() throws BufferUnderflowException, NoMoreWrapBufferException {
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

	/**
	 * @return unsigned short 타입 값
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public int getUnsignedShort() throws BufferUnderflowException, NoMoreWrapBufferException {
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

	/**
	 * @return integer 타입 값
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public int getInt() throws BufferUnderflowException, NoMoreWrapBufferException {
		if ((limit - position) < 4) {
			throw new BufferUnderflowException();
		}

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

	/**
	 * @return unsigned integer 타입 값
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public long getUnsignedInt() throws BufferUnderflowException, NoMoreWrapBufferException {
		if ((limit - position) < 4) {
			throw new BufferUnderflowException();
		}

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

	/**
	 * @return long 타입 값
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public long getLong() throws BufferUnderflowException, NoMoreWrapBufferException {
		if ((limit - position) < 8) {
			throw new BufferUnderflowException();
		}

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

	/**
	 * WARNING! 이 메소드는 '스트림 버퍼'에서 실질적으로 파라미터 'length' 로 지정한 byte 만큼 읽기를 수행하는 공통 모듈로
	 * 파라미터 값 검사를 위임하므로 파라미터 값 검사가 없다.
	 * 
	 * @param length 원하는 바이트 배열 크기
	 * @return 파라미터 'length' 만큼의 바이트 배열
	 * @throws BufferUnderflowException        바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	private byte[] doGetBytes(int length) throws BufferUnderflowException, NoMoreWrapBufferException {
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

	/**
	 * @param length 원하는 바이트 배열 크기
	 * @return 파라미터 'length' 만큼의 바이트 배열
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 'length' 값이 음수일 경우 던지는 예외
	 */
	public byte[] getBytes(int length)
			throws BufferUnderflowException, NoMoreWrapBufferException, IllegalArgumentException {
		if (length < 0) {
			String errorMessage = new StringBuilder().append("the parameter length[").append(length)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if ((limit - position) < length) {
			throw new BufferUnderflowException();
		}

		return doGetBytes(length);
	}

	/**
	 * stream buffer 의 내용을 파라미터 'length' 양만큼 읽어와서 파라미터 'dst' 배열에 파라미터 'offset' 이
	 * 지정하는 위치에서 부터 저장한다. WARNING! 이 메소드는 '스트림 버퍼'에서 실질적으로 파라미터 'length' 로 지정한 byte
	 * 만큼 읽기를 수행하는 공통 모듈로 파라미터 값 검사를 위임하므로 파라미터 값 검사가 없다.
	 * 
	 * @param dst    읽어온 데이터가 저장될 배열
	 * @param offset 파라미터 'dst' 배열에 저장할 위치
	 * @param length 저장할 데이터 크기
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	private void doGetBytes(byte dst[], int offset, int length)
			throws BufferUnderflowException, NoMoreWrapBufferException {

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

	/**
	 * stream buffer 의 내용을 파라미터 'length' 양만큼 읽어와서 파라미터 'dst' 배열에 파라미터 'offset' 이
	 * 지정하는 위치에서 부터 저장한다.
	 * 
	 * @param dst    읽어온 데이터가 저장될 배열
	 * @param offset 파라미터 'dst' 배열에 저장할 위치
	 * @param length 저장할 데이터 크기
	 * @throws BufferUnderflowException        '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는
	 *                                         예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException        파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public void getBytes(byte[] dst, int offset, int length)
			throws BufferUnderflowException, NoMoreWrapBufferException, IllegalArgumentException {
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
					.append("] is greater than or equal to the parameter dst's length[").append(dst.length).append("]")
					.toString();
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

	/**
	 * WARNING! 이 메소드는 '스트림 버퍼'에서 실질적으로 파라미터 'length' 로 지정한 byte 만큼 읽어 파라미터
	 * 'stringCharset' 로 지정한 문자열로 디코딩하여 얻은 문자열을 반환하는 공통 모듈로 파라미터 값 검사를 위임하므로 파라미터 값
	 * 검사가 없다.
	 * 
	 * @param length        문자열의 바이트 수
	 * @param stringCharset 문자셋
	 * @return 파라미터 'length' 만큼 읽어와 파라미터 'stringCharset' 문자셋으로 디코딩하여 얻은 문자열
	 * @throws BufferUnderflowException        바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException         문자셋으로 디코딩할때 실패시 던지는 예외
	 */
	private String doGetString(int length, Charset stringCharset)
			throws BufferUnderflowException, NoMoreWrapBufferException, CharsetDecoderException {

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

	/** 
	 * @param fixedLength 문자열로 변활될 바이트 수
	 * @param wantedCharsetDecoder 원하는 문자셋
	 * @return 파라미터 'fixedLength' 로 지정한 크기 만큼 파라미터 'wantedCharsetDecoder' 로 지정된 문자젯으로 디코딩 하여 얻은 문자열
	 * @throws BufferUnderflowException  '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException 문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public String getFixedLengthString(final int fixedLength, final CharsetDecoder wantedCharsetDecoder)
			throws BufferUnderflowException, NoMoreWrapBufferException, CharsetDecoderException,
			IllegalArgumentException {
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

	/** 
	 * @param fixedLength 문자열로 변활될 바이트 수
	 * @return 파라미터 'fixedLength' 로 지정한 크기 만큼 디폴트 문자젯으로 디코딩 하여 얻은 문자열
	 * @throws BufferUnderflowException  '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException 문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public String getFixedLengthString(final int fixedLength) throws BufferUnderflowException,
			NoMoreWrapBufferException, CharsetDecoderException, IllegalArgumentException {
		return getFixedLengthString(fixedLength, defaultCharsetDecoder);
	}

	/**
	 * @return 남아 있는 모든 데이터를 디폴트 문자셋으로 디코딩 하여 얻은 문자열
	 * @throws IllegalStateException 남아 있는 데이터 양이 integer.max 보다 클 경우 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetDecoderException  문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public String getAllString() throws IllegalStateException, IllegalArgumentException, CharsetDecoderException,
			NoMoreWrapBufferException {
		return getAllString(defaultCharset);
	}

	/**
	 * @param wantedCharset 원하는 문자셋
	 * @return 남아 있는 모든 데이터를 파라미터 'wantedCharset' 로 지정한 문자셋으로 디코딩 하여 얻은 문자열
	 * @throws IllegalStateException 남아 있는 데이터 양이 integer.max 보다 클 경우 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CharsetDecoderException  문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public String getAllString(Charset wantedCharset) throws NoMoreWrapBufferException, CharsetDecoderException,
			IllegalArgumentException, IllegalStateException {
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

	
	/**
	 * @return 디폴트 문자셋으로 디코딩하여 얻은 signed integer 파스칸 문자열
	 * @throws BufferUnderflowException  '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException 문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public String getSIPascalString() throws BufferUnderflowException, NoMoreWrapBufferException,
			CharsetDecoderException, IllegalArgumentException {
		return getSIPascalString(defaultCharset);
	}

	/**
	 * @param wantedCharset 원하는 문자셋
	 * @return 파라미터 'wantedCharset' 로 지정된 문자셋을 갖는 signed integer 파스칸 문자열
	 * @throws BufferUnderflowException  '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException 문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public String getSIPascalString(Charset wantedCharset) throws BufferUnderflowException,
			NoMoreWrapBufferException, CharsetDecoderException, IllegalArgumentException {
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

	/**
	 * @return 디폴트 문자셋으로 디코딩하여 얻은 unsigned short 파스칸 문자열
	 * @throws BufferUnderflowException  '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException 문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public String getUSPascalString() throws BufferUnderflowException, NoMoreWrapBufferException,
			CharsetDecoderException, IllegalArgumentException {
		return getUSPascalString(defaultCharset);
	}

	/**
	 * @param wantedCharset 원하는 문자셋
	 * @return 파라미터 'wantedCharset' 로 지정된 문자셋으로 디코딩하여 얻은 unsigned short 파스칸 문자열
	 * @throws BufferUnderflowException  '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException 문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public String getUSPascalString(Charset wantedCharset) throws BufferUnderflowException,
			NoMoreWrapBufferException, CharsetDecoderException, IllegalArgumentException {
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

	/**
	 * @return 디폴트 문자셋으로 디코딩하여 얻은 unsigned byte 파스칸 문자열
	 * @throws BufferUnderflowException  '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException 문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public String getUBPascalString() throws BufferUnderflowException, NoMoreWrapBufferException,
			CharsetDecoderException, IllegalArgumentException {
		return getUBPascalString(defaultCharset);
	}

	/**
	 * @param wantedCharset 원하는 문자셋
	 * @return 파라미터 'wantedCharset' 로 지정된 문자셋으로 디코딩하여 얻은 unsigned byte 파스칸 문자열
	 * @throws BufferUnderflowException  '스트림 버퍼' 혹은 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws CharsetDecoderException 문자셋으로 디코딩할때 실패시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public String getUBPascalString(Charset wantedCharset) throws BufferUnderflowException,
			NoMoreWrapBufferException, CharsetDecoderException, IllegalArgumentException {
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

	/**
	 * 파라미터 'n' 만큼 위치 속성을 증가한다, 단 파라미터 'n' 값은 0 보다 크고 남은 용량 보다 작거나 같아야 한다.
	 * @param n 위치 증가를 원하는 크기
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws IllegalArgumentException 파라미터 'n' 이 음수이거나 남은 용량 보다 크가면 던지는 예외 
	 */
	public void skip(long n) throws NoMoreWrapBufferException {
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
	 * @param size MD5  하고자 하는 크기
	 * @return 파라미터 'size' 에서 지정한 양 만큼 MD5 한 결과
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public byte[] getMD5WithoutChange(long size) throws NoMoreWrapBufferException {
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
		 * INFO! 변수 endWorkingIndex 까지 버퍼들이 채워 졌기때문에 변수 bufferIndexOfStartPostion 를 갖는
		 * 버퍼는 무조건 존재한다
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
	 * @throws IllegalStateException 점검을 하여 스트림 버퍼 상태가 잘못되었을 경우 던지는 예외
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
							.append("]이 버퍼의 'capacity 속성 값'[").append(dataPacketBufferSize).append("] 과 다릅니다")
							.toString();
					throw new IllegalStateException(errorMessage);
				}
			}
		}
	}

	/**
	 * 소켓 채널로 부터 데이터를 읽어 '위치' 속성 값이 지정하는 위치에 해당하는 버퍼에 저장하고 읽어온 바이트 수 만큼 '위치' 속성을 증가한다.
	 * 
	 * @param readableSocketChannel 이 '스트림 버퍼'의 원 소유자인 소켓 채널
	 * @return 소켓 채널로 부터 읽어 들인 바이트 수
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 * @throws BufferUnderflowException '위치' 속성에 대응하는 바이트 버퍼에서 버퍼 언더 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public int read(SocketChannel readableSocketChannel)
			throws IOException, BufferUnderflowException, NoMoreWrapBufferException {
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
			workingByteBuffer.limit(bufferOffsetOfStartPostion + (int) remaining());

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

	/**
	 * '위치' 속성 값이 지정하는 위치에 해당하는 버퍼의 내용을 소켓 채널에 쓰고 실질적으로 쓰여진 양만큼 '위치' 속성 값을 증가한다.
	 * 
	 * @param writableSocketChannel 소켓 채널
	 * @return 실질적으로 소켓에 쓰여진 양
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 * @throws BufferOverflowException '위치' 속성에 대응하는 바이트 버퍼에서 버퍼 오버 플로우일때 던지는 예외
	 * @throws NoMoreWrapBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 */
	public int write(SocketChannel writableSocketChannel)
			throws IOException, BufferOverflowException, NoMoreWrapBufferException {
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
			workingByteBuffer.limit(bufferOffsetOfStartPostion + (int) remaining());

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
	 * 마지막 버퍼의 limit 속성 값을 {@link #limit} 속성 값에 맞추어 설정한다. WARNING! 빠른 처리를 위하여 에러 처리
	 * 루틴 생략, 소켓을 통해 보낼 메시지 내용을 담고 있는 경우에 호출된다.
	 */
	public void setLastBufferLimitUsingLimit() {
		long endPosition = limit - 1;
		int bufferIndexOfEndPostion = (int) (endPosition / dataPacketBufferSize);
		int bufferOffsetOfEndPostion = (int) (endPosition % dataPacketBufferSize);

		/**
		 * WARNING! 빠른 처리를 위하여 byteBufferArray[bufferIndexOfEndPostion] 이 null 일 경우 처리
		 * 루틴 생략, 메시지
		 */
		byteBufferArray[bufferIndexOfEndPostion].limit(bufferOffsetOfEndPostion + 1);
	}

	public void printElapsedTime(String title) {
		long endTime = System.nanoTime();

		String infoMessage = new StringBuilder().append(title).append(" 경과 시간[")
				.append(TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS))
				.append("] microseconds").toString();

		log.info(infoMessage);
	}

	/**
	 * 처음 부터 '마지막 버퍼 인덱스' 까지 랩 버퍼를 랩 버퍼 폴에 반환하고 '마지막 버퍼 인덱스' 값을 -1 로 설정한다.
	 */
	public void releaseAllWrapBuffers() {

		for (int i = 0; i <= lastBufferIndex; i++) {
			wrapBufferPool.putDataPacketBuffer(wrapBufferArray[i]);
			wrapBufferArray[i] = null;
			byteBufferArray[i] = null;
		}

		lastBufferIndex = -1;
	}

	/**
	 * @return 남은 데이터의 헥사 스트링
	 */
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
