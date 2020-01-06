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

import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;

/**
 * 랩 버퍼 폴
 * @author Won Jonghoon
 *
 */
public class WrapBufferPool implements WrapBufferPoolIF {

	private final Object monitor = new Object();

	private ArrayDeque<WrapBuffer> dataPacketBufferQueue = null;
	
	private boolean isDirect;
	private ByteOrder dataPacketBufferByteOrder = null;
	private int dataPacketBufferSize;
	private int wrapBufferPoolCapacity;

	/**
	 * 생성자
	 * @param isDirect ByteBuffer 직업 바이트 버퍼 여부
	 * @param byteOrder 바이트 오더
	 * @param wrapBufferCapacity 랩 버퍼 용량
	 * @param wrapBufferPoolCapacity 랩 버퍼 폴 용량, 초기 상태에서 가지고 있는 랩 버퍼 갯수
	 */
	public WrapBufferPool(boolean isDirect, ByteOrder byteOrder, int wrapBufferCapacity,
			int wrapBufferPoolCapacity) {
		if (null == byteOrder) {
			throw new IllegalArgumentException("the parameter dataPacketBufferByteOrder is null");
		}

		if (wrapBufferCapacity <= 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter dataPacketBufferSize[")
					.append(wrapBufferCapacity)
					.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (wrapBufferPoolCapacity <= 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter capacity[")
					.append(wrapBufferPoolCapacity)
					.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.isDirect = isDirect;
		this.dataPacketBufferByteOrder = byteOrder;
		this.dataPacketBufferSize = wrapBufferCapacity;
		this.wrapBufferPoolCapacity = wrapBufferPoolCapacity;

		dataPacketBufferQueue = new ArrayDeque<WrapBuffer>(wrapBufferPoolCapacity);

		try {

			for (int i = 0; i < wrapBufferPoolCapacity; i++) {
				WrapBuffer dataPacketBuffer = new WrapBuffer(isDirect, wrapBufferCapacity, byteOrder);
				dataPacketBuffer.setPoolBuffer(true);
				dataPacketBufferQueue.add(dataPacketBuffer);
				// allWrapBufferHashcodeSet.add(dataPacketBuffer.hashCode());
			}

			// log.info("the wrap buffer hashcode set={}",
			// allWrapBufferHashcodeSet.toString());
		} catch (OutOfMemoryError e) {
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			String errorMessage = "OutOfMemoryError";			
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}
	}

	@Override
	public WrapBuffer pollDataPacketBuffer() throws NoMoreWrapBufferException {
		WrapBuffer dataPacketBuffer = null;
		synchronized (monitor) {
			dataPacketBuffer = dataPacketBufferQueue.poll();
		}
		if (null == dataPacketBuffer) {
			String errorMessage = "no more wrap buffer in the wrap buffer polling queue";
			throw new NoMoreWrapBufferException(errorMessage);
		}

		dataPacketBuffer.queueOut();

		// queueOutWrapBufferHashcodeSet.add(dataPacketBuffer.hashCode());

		// FIXME!, 테스트후 삭제 필요
		/*
		 * { String infoMessage = String.
		 * format("the WrapBuffer[%d] is removed from the wrap buffer polling queue",
		 * dataPacketBuffer.hashCode()); log.info(infoMessage, new
		 * Throwable(infoMessage)); }
		 */

		return dataPacketBuffer;
		
	}

	@Override
	public void putDataPacketBuffer(WrapBuffer dataPacketBuffer) {
		if (null == dataPacketBuffer) {
			return;
		}
		
		if (! dataPacketBuffer.isPoolBuffer()) {
			String errorMessage = new StringBuilder()
					.append("the parameter dataPacketBuffer[")
					.append(dataPacketBuffer.hashCode())
					.append("] is not a wrap buffer of this wrapbuffer pool").toString();
			// log.warn(errorMessage, new Throwable(errorMessage));
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, new Throwable(errorMessage));
			
			
			throw new IllegalArgumentException(errorMessage);
		}

		/**
		 * 2번 연속 반환 막기
		 */
		synchronized (monitor) {
			if (dataPacketBuffer.isInQueue()) {
				String errorMessage = new StringBuilder()
						.append("the parameter dataPacketBuffer[")
						.append(dataPacketBuffer.hashCode())
						.append("] already was returned in this wrapbuffer pool").toString();
				// log.warn(errorMessage, new Throwable(errorMessage));
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, errorMessage, new Throwable(errorMessage));
				
				throw new IllegalArgumentException(errorMessage);
			}

			dataPacketBuffer.queueIn();
			dataPacketBufferQueue.add(dataPacketBuffer);

			// FIXME!, 테스트후 삭제 필요
			/*
			 * { String infoMessage = String.
			 * format("the parameter dataPacketBuffer[%d] is added to the wrap buffer polling queue"
			 * , dataPacketBuffer.hashCode()); log.info(infoMessage, new
			 * Throwable(infoMessage)); }
			 */
		}

	}

	@Override
	public final int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}

	public String getQueueState() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("capacity=[");
		strBuilder.append(wrapBufferPoolCapacity);
		strBuilder.append("], remaing size=[");
		strBuilder.append(dataPacketBufferQueue.size());
		strBuilder.append("]");
		return strBuilder.toString();
	}

	@Override
	public final ByteOrder getByteOrder() {
		return dataPacketBufferByteOrder;
	}

	@Override
	public final int capacity() {
		return wrapBufferPoolCapacity;
	}

	/**
	 * @return 랩 버퍼 폴의 원소 랩 버퍼가 직접 바이트 버퍼 인지 여부
	 */
	public boolean isDirect() {
		return isDirect;
	}
	
	@Override
	public int size() {
		return dataPacketBufferQueue.size();
	}
		
}
