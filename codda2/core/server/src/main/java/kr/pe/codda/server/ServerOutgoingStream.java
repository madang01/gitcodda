package kr.pe.codda.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.ServerOutgoingStreamIF;
import kr.pe.codda.common.io.StreamBuffer;

public class ServerOutgoingStream implements ServerOutgoingStreamIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final SelectionKey ownerSelectionKey;
	private final int streamBufferQueueCapacity;

	private final ArrayDeque<StreamBuffer> streamBufferArrayDeque;
	private transient int streamBufferCount = 0;
	private transient StreamBuffer workingStreamBuffer = null;

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

		messageStreamBuffer.setLastBufferLimitUsingLimit();
		streamBufferCount++;
		streamBufferArrayDeque.addLast(messageStreamBuffer);

		if (null == workingStreamBuffer) {
			workingStreamBuffer = messageStreamBuffer;
		}

		ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() | SelectionKey.OP_WRITE & ~SelectionKey.OP_READ);

		return true;

	}

	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreDataPacketBufferException {
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

			if (!workingStreamBuffer.hasRemaining()) {

				streamBufferCount--;
				streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();

				if (0 == streamBufferCount) {
					// FIXME!
					// log.info("송신할 스트림 없음");

					workingStreamBuffer = null;
					/** socket write event turn off */
					ret = -1;

					ownerSelectionKey.interestOps(ownerSelectionKey.interestOps() & ~SelectionKey.OP_WRITE | SelectionKey.OP_READ);
				} else {
					workingStreamBuffer = streamBufferArrayDeque.peekFirst();

					// FIXME!
					// log.info("작업 버퍼의 내용 송신 완료로 인한 새 작업 버퍼로 교체");
				}

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

	public void close() {
		while (!streamBufferArrayDeque.isEmpty()) {
			streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();
		}
	}

}
