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
package kr.pe.codda.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.io.ServerOutgoingStreamIF;
import kr.pe.codda.common.io.StreamBuffer;

/**
 * 서버용 출력 스트림
 * @author Won Jonghoon
 *
 */
public class ServerOutgoingStream implements ServerOutgoingStreamIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final SelectionKey ownerSelectionKey;
	private final int streamBufferQueueCapacity;

	private final ArrayDeque<StreamBuffer> streamBufferArrayDeque;
	private transient int streamBufferCount = 0;
	private transient StreamBuffer workingStreamBuffer = null;

	/**
	 * 생성자
	 * @param ownerSelectionKey 서버용 출력 스트림을 갖는 셀렉션 키 
	 * @param outputStreamBufferQueueCapacity 출력 메시지가 담기는 스트림 버퍼 큐 크기
	 */
	public ServerOutgoingStream(SelectionKey ownerSelectionKey, int outputStreamBufferQueueCapacity) {
		if (null == ownerSelectionKey) {
			throw new IllegalArgumentException("the parameter ownerSelectionKey is null");
		}

		if (outputStreamBufferQueueCapacity <= 0) {
			throw new IllegalArgumentException(
					"the parameter outputStreamBufferQueueCapacity is less than or equal to zero");
		}

		this.streamBufferQueueCapacity = outputStreamBufferQueueCapacity;
		this.ownerSelectionKey = ownerSelectionKey;
		streamBufferArrayDeque = new ArrayDeque<StreamBuffer>(outputStreamBufferQueueCapacity);
	}

	@Override
	public boolean offer(StreamBuffer messageStreamBuffer) throws InterruptedException {
		if (null == messageStreamBuffer) {
			throw new IllegalArgumentException("the parameter messageStreamBuffer is null");
		}

		// FIXME!
		// log.info("call offer in server");

		if (streamBufferCount > streamBufferQueueCapacity) {

			String errorMessage = new StringBuilder().append("최대 출력 스트림 갯수[").append(streamBufferQueueCapacity)
					.append("]를 초과").toString();
			log.warning(errorMessage);

			return false;
		}
		
		streamBufferCount++;
		streamBufferArrayDeque.addLast(messageStreamBuffer);

		if (null == workingStreamBuffer) {
			workingStreamBuffer = messageStreamBuffer;
		}

		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() | SelectionKey.OP_WRITE);

		return true;

	}

	@Override
	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreWrapBufferException {
		int ret;

		if (null == workingStreamBuffer) {
			// FIXMe!
			// log.info("workingStreamBuffer is null");

			ret = 0;
		} else {
			// FIXME!
			// log.info("workingStreamBuffer is not null,
			// remaint="+workingStreamBuffer.remaining());

			ret = workingStreamBuffer.write(writableSocketChannel);

			// FIXME!
			// log.info("workingStreamBuffer is not null, ret="+ret +",
			// workingStreamBuffer.hasRemaining="+workingStreamBuffer.hasRemaining());

			if (! workingStreamBuffer.hasRemaining()) {

				streamBufferCount--;
				streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();

				if (0 == streamBufferCount) {
					// FIXME!
					// log.info("송신할 스트림 없음");

					workingStreamBuffer = null;
					ret = -1;
					/** socket write event turn off */
					ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() & ~SelectionKey.OP_WRITE);
				} else {
					workingStreamBuffer = streamBufferArrayDeque.peekFirst();

					// FIXME!
					// log.info("작업 버퍼의 내용 송신 완료로 인한 새 작업 버퍼로 교체");
				}

			}
		}
		
		// FIXME!
		// log.info("ret="+ret);

		return ret;

	}
	/*
	 * public boolean hasRemaing() { boolean hasRemaing = false;
	 * 
	 * synchronized (monitor) { hasRemaing = outputStreamBufferArrayDeque.isEmpty();
	 * }
	 * 
	 * return hasRemaing; }
	 */

	/*
	private void turnOnSocketWriteMode() throws CancelledKeyException {
		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() | SelectionKey.OP_WRITE);
	}

	private void turnOffSocketWriteMode() throws CancelledKeyException {
		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() & ~SelectionKey.OP_WRITE);
	}
	*/

	/*
	 * private void turnOnSocketReadMode() throws CancelledKeyException {
	 * ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() |
	 * SelectionKey.OP_READ); }
	 * 
	 * private void turnOffSocketReadMode() throws CancelledKeyException {
	 * ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() &
	 * ~SelectionKey.OP_READ); }
	 */

	@Override
	public void close() {
		while (!streamBufferArrayDeque.isEmpty()) {
			streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();
		}
	}

}
