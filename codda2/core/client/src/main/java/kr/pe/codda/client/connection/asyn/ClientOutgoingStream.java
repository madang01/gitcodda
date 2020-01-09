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
package kr.pe.codda.client.connection.asyn;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.io.ClientOutgoingStreamIF;
import kr.pe.codda.common.io.StreamBuffer;

/**
 * 클라이언트용 송신 스트림, 내부적으로는 스트립 버퍼 환영 큐(=ArrayDeque)로 관리한다.
 * @author Won Jonghoon
 *
 */
public class ClientOutgoingStream implements ClientOutgoingStreamIF {
	private final Object monitor = new Object();
	// private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final ClientIOEventControllerIF asynClientIOEventController;
	private final SelectionKey ownerSelectionKey;
	private final int streamBufferQueueCapacity;

	private final ArrayDeque<StreamBuffer> streamBufferArrayDeque;
	private transient int streamBufferCount = 0;
	private transient StreamBuffer workingStreamBuffer = null;

	/**
	 * 생성자
	 * @param asynClientIOEventController 비동기 클라이언트 입출력 이벤트 제어자
	 * @param ownerSelectionKey 소유 세렉션 키
	 * @param outputStreamBufferQueueCapacity 메시지가 담기는 '스트림 버퍼'를 원소로 갖는 환영 큐 크기
	 */
	public ClientOutgoingStream(ClientIOEventControllerIF asynClientIOEventController, 
			SelectionKey ownerSelectionKey, int outputStreamBufferQueueCapacity) {
		if (null == asynClientIOEventController) {
			throw new IllegalArgumentException("the parameter asynClientIOEventController is null");
		}
		
		if (null == ownerSelectionKey) {
			throw new IllegalArgumentException("the parameter ownerSelectionKey is null");
		}

		if (outputStreamBufferQueueCapacity <= 0) {
			throw new IllegalArgumentException(
					"the parameter outputStreamBufferQueueCapacity is less than or equal to zero");
		}

		this.asynClientIOEventController = asynClientIOEventController;
		this.streamBufferQueueCapacity = outputStreamBufferQueueCapacity;
		this.ownerSelectionKey = ownerSelectionKey;
		streamBufferArrayDeque = new ArrayDeque<StreamBuffer>(outputStreamBufferQueueCapacity);
	}

	@Override
	public boolean offer(StreamBuffer messageStreamBuffer, long timeout) throws InterruptedException {
		if (null == messageStreamBuffer) {
			throw new IllegalArgumentException("the parameter messageStreamBuffer is null");
		}

		// FIXME!
		// log.info("call offer in client");

		synchronized (monitor) {			
			if (streamBufferCount == streamBufferQueueCapacity) {
				// FIXME!
				// log.info("최대 치 도달에 따른  기다림 시작");

				monitor.wait(timeout);

				// FIXME!
				// log.info("최대 치 도달에 따른  기다림 종료");

				if (streamBufferCount == streamBufferQueueCapacity) {
					return false;
				}
			}

			messageStreamBuffer.setLastBufferLimitUsingLimit();
			streamBufferCount++;
			streamBufferArrayDeque.addLast(messageStreamBuffer);

			if (null == workingStreamBuffer) {
				workingStreamBuffer = messageStreamBuffer;
			}

			turnOnSocketWriteMode();
			
			return true;
		}
	}
	
	@Override
	public void decreaseOutputMessageCount() {
		synchronized (monitor) {
			if (streamBufferCount > 0) {
				streamBufferCount--;
			}

			monitor.notify();
		}
	}

	@Override
	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreWrapBufferException {
		if (null == workingStreamBuffer) {
			return 0;
		}

		int ret = workingStreamBuffer.write(writableSocketChannel);
		

		if (! workingStreamBuffer.hasRemaining()) {

			synchronized (monitor) {
				streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();

				if (0 == streamBufferCount) {
					// FIXME!
					// log.info("송신할 스트림 없음");

					workingStreamBuffer = null;
					/** socket write event turn off */
					ret = -1;

					turnOffSocketWriteMode();
				} else {
					workingStreamBuffer = streamBufferArrayDeque.peekFirst();

					// FIXME!
					// log.info("작업 버퍼의 내용 송신 완료로 인한 새 작업 버퍼로 교체");
				}
			}
		}

		return ret;

	}
	

	/**
	 * SelectionKey.OP_WRITE 등록
	 * @throws CancelledKeyException 멤버 변수 'ownerSelectionKey' 가 접속 종료등으로 등록 취소 되었을 경우 던지는 예외 
	 */
	private void turnOnSocketWriteMode() throws CancelledKeyException {
		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() | SelectionKey.OP_WRITE);
		
		asynClientIOEventController.wakeup();
	}

	/**
	 * SelectionKey.OP_WRITE 취소
	 * @throws CancelledKeyException 멤버 변수 'ownerSelectionKey' 가 접속 종료등으로 등록 취소 되었을 경우 던지는 예외
	 */
	private void turnOffSocketWriteMode() throws CancelledKeyException {
		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() & ~SelectionKey.OP_WRITE);
	}

	@Override
	public void close() {
		while (!streamBufferArrayDeque.isEmpty()) {
			streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();
		}
	}
}
