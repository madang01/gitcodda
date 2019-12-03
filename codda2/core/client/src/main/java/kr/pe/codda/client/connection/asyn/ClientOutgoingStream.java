package kr.pe.codda.client.connection.asyn;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.ClientOutgoingStreamIF;
import kr.pe.codda.common.io.StreamBuffer;

public class ClientOutgoingStream implements ClientOutgoingStreamIF {
	private final Object monitor = new Object();
	// private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final ClientIOEventControllerIF asynClientIOEventController;
	private final SelectionKey ownerSelectionKey;
	private final int streamBufferQueueCapacity;

	private final ArrayDeque<StreamBuffer> streamBufferArrayDeque;
	private transient int streamBufferCount = 0;
	private transient StreamBuffer workingStreamBuffer = null;

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

	/*
	public boolean offer(StreamBuffer messageStreamBuffer) throws InterruptedException {
		if (null == messageStreamBuffer) {
			throw new IllegalArgumentException("the parameter messageStreamBuffer is null");
		}

		// FIXME!				
		// log.info("call offer in server");

		synchronized (monitor) {
			if (streamBufferCount > streamBufferQueueCapacity) {

				String errorMessage = new StringBuilder().append("최대 출력 스트림 갯수[").append(streamBufferQueueCapacity)
						.append("]를 초과").toString();
				log.warning(errorMessage);

				return false;
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
	*/

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

	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreDataPacketBufferException {
		if (null == workingStreamBuffer) {
			return 0;
		}

		int ret = workingStreamBuffer.write(writableSocketChannel);
		

		if (! workingStreamBuffer.hasRemaining()) {

			synchronized (monitor) {

				streamBufferCount--;
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

				monitor.notify();
			}
		}

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

	private void turnOnSocketWriteMode() throws CancelledKeyException {
		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() | SelectionKey.OP_WRITE);
		
		asynClientIOEventController.wakeup();
	}

	private void turnOffSocketWriteMode() throws CancelledKeyException {
		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() & ~SelectionKey.OP_WRITE);
	}

	/*
	 * private void turnOnSocketReadMode() throws CancelledKeyException {
	 * ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() |
	 * SelectionKey.OP_READ); }
	 * 
	 * private void turnOffSocketReadMode() throws CancelledKeyException {
	 * ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() &
	 * ~SelectionKey.OP_READ); }
	 */

	public void close() {
		while (!streamBufferArrayDeque.isEmpty()) {
			streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();
		}
	}
}
