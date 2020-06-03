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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.OutgoingStreamTimeoutException;
import kr.pe.codda.common.exception.RetryException;
import kr.pe.codda.common.exception.TimeoutDelayException;
import kr.pe.codda.common.io.ClientOutgoingStreamIF;
import kr.pe.codda.common.io.StreamBuffer;

/**
 * 클라이언트용 송신 스트림, 내부적으로는 스트립 버퍼 환영 큐(=ArrayDeque)로 관리한다.
 * 
 * @author Won Jonghoon
 *
 */
public class ClientOutgoingStream implements ClientOutgoingStreamIF {
	protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final ReentrantLock lock = new ReentrantLock();

	private final ClientIOEventControllerIF asynClientIOEventController;
	private final SelectionKey ownerSelectionKey;
	private final int streamBufferQueueCapacity;

	private final ArrayDeque<StreamBuffer> finishedStreamBufferArrayDeque;
	private final ArrayDeque<StreamBuffer> workingStreamBufferArrayDeque;
	private final long aliveTimePerWrapBuffer;

	private transient StreamBuffer workingStreamBuffer = null;

	/**
	 * 생성자
	 * 
	 * @param asynClientIOEventController     비동기 클라이언트 입출력 이벤트 제어자
	 * @param ownerSelectionKey               소유 세렉션 키
	 * @param outputStreamBufferQueueCapacity 메시지가 담기는 '스트림 버퍼'를 원소로 갖는 환영 큐 크기
	 * @param aliveTimePerWrapBuffer          랩버퍼 1개당 생존 시간, 단위 : nanoseconds
	 */
	public ClientOutgoingStream(ClientIOEventControllerIF asynClientIOEventController, SelectionKey ownerSelectionKey,
			int outputStreamBufferQueueCapacity, long aliveTimePerWrapBuffer) {
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

		if (aliveTimePerWrapBuffer <= 0) {
			throw new IllegalArgumentException("the parameter aliveTimePerWrapBuffer is less than or equal to zero");
		}

		this.asynClientIOEventController = asynClientIOEventController;
		this.ownerSelectionKey = ownerSelectionKey;
		this.streamBufferQueueCapacity = outputStreamBufferQueueCapacity;
		this.aliveTimePerWrapBuffer = aliveTimePerWrapBuffer;

		workingStreamBufferArrayDeque = new ArrayDeque<StreamBuffer>(streamBufferQueueCapacity);
		finishedStreamBufferArrayDeque = new ArrayDeque<StreamBuffer>(streamBufferQueueCapacity);
	}

	@Override
	public void add(StreamBuffer messageStreamBuffer, long timeout)
			throws OutgoingStreamTimeoutException, RetryException, TimeoutDelayException, InterruptedException {
		if (null == messageStreamBuffer) {
			throw new IllegalArgumentException("the parameter messageStreamBuffer is null");
		}

		// FIXME!
		// log.info("call offer in client");

		boolean isLocked = lock.tryLock(timeout, TimeUnit.MILLISECONDS);

		if (!isLocked) {
			throw new OutgoingStreamTimeoutException("fail to get this client outgoing stream's lock");
		}

		try {
			long lockBeginTime = System.nanoTime();
			long endTimeForTimeout = lockBeginTime + timeout * CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS;

			final int streamBufferCount = finishedStreamBufferArrayDeque.size() + workingStreamBufferArrayDeque.size();
			
			// FIXME!
			// log.info("streamBufferCount="+streamBufferCount);

			if (streamBufferCount == streamBufferQueueCapacity) {
				// FIXME!
				// log.info("최대 치 도달에 따른 기다림 시작");

				if (finishedStreamBufferArrayDeque.isEmpty()) {
					throw new RetryException();
				}

				StreamBuffer finishedStreamBuffer = finishedStreamBufferArrayDeque.peekFirst();
				long expiredTime = finishedStreamBuffer.getExpiredTime();
				
				/*
				// FIXME!
				log.info("lockBeginTime="+lockBeginTime);
				log.info("expiredTime="+expiredTime);
				log.info("endTimeForTimeout="+endTimeForTimeout);
				log.info("비교결과1="+ (lockBeginTime < expiredTime));
				log.info("비교결과2="+ (endTimeForTimeout < expiredTime));
				*/
				

				if (lockBeginTime < expiredTime) {

					if (endTimeForTimeout < expiredTime) {
						throw new TimeoutDelayException(timeout - (System.nanoTime() - lockBeginTime) / CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS);
					}
					long waitingTime = (expiredTime - lockBeginTime);

					long millis = waitingTime / CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS;
					int nanos = (int) (waitingTime % CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS);

					Thread.sleep(millis, nanos);

				}

				finishedStreamBufferArrayDeque.removeFirst();

			}

			workingStreamBufferArrayDeque.addLast(messageStreamBuffer);

			if (null == workingStreamBuffer) {
				workingStreamBuffer = messageStreamBuffer;
			}

			turnOnSocketWriteMode();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreWrapBufferException {
		if (null == workingStreamBuffer) {
			return 0;
		}

		int ret = workingStreamBuffer.write(writableSocketChannel);

		if (! workingStreamBuffer.hasRemaining()) {

			boolean isLocked = lock.tryLock();

			if (isLocked) {
				try {

					StreamBuffer finishedStreamBuffer = workingStreamBufferArrayDeque.removeFirst();
					finishedStreamBuffer.releaseAllWrapBuffers();

					finishedStreamBuffer.setExpiredTimeBasedOnPosition(aliveTimePerWrapBuffer);
					finishedStreamBufferArrayDeque.add(finishedStreamBuffer);
					
					// FIXME!
					// log.info("작업중인 스트림을 종료된 스트림으로 이동");

					if (workingStreamBufferArrayDeque.isEmpty()) {
						// FIXME!
						// log.info("송신할 스트림 없음");

						workingStreamBuffer = null;
						/** socket write event turn off */
						ret = -1;

						turnOffSocketWriteMode();
					} else {
						workingStreamBuffer = workingStreamBufferArrayDeque.peekFirst();

						// FIXME!
						// log.info("작업 버퍼의 내용 송신 완료로 인한 새 작업 버퍼로 교체");
					}
				} finally {
					lock.unlock();
				}
			}
		}

		return ret;

	}

	/**
	 * SelectionKey.OP_WRITE 등록
	 * 
	 * @throws CancelledKeyException 멤버 변수 'ownerSelectionKey' 가 접속 종료등으로 등록 취소 되었을
	 *                               경우 던지는 예외
	 */
	private void turnOnSocketWriteMode() throws CancelledKeyException {
		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() | SelectionKey.OP_WRITE);

		asynClientIOEventController.wakeup();
	}

	/**
	 * SelectionKey.OP_WRITE 취소
	 * 
	 * @throws CancelledKeyException 멤버 변수 'ownerSelectionKey' 가 접속 종료등으로 등록 취소 되었을
	 *                               경우 던지는 예외
	 */
	private void turnOffSocketWriteMode() throws CancelledKeyException {
		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() & ~SelectionKey.OP_WRITE);
	}

	@Override
	public void close() {
		while (!workingStreamBufferArrayDeque.isEmpty()) {
			workingStreamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();
		}
	}
}
