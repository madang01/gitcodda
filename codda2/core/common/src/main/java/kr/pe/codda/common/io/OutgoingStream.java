package kr.pe.codda.common.io;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class OutgoingStream {
	private final Object monitor = new Object();

	// private final SocketChannel ownerSocketChannel;
	private final int streamBufferQueueCapacity;

	private final ArrayDeque<StreamBuffer> streamBufferArrayDeque;
	private transient int streamBufferCount = 0;
	private transient StreamBuffer workingStreamBuffer = null;

	public OutgoingStream(int outputStreamBufferQueueCapacity) {
		if (outputStreamBufferQueueCapacity <= 0) {
			throw new IllegalArgumentException(
					"the parameter outputStreamBufferQueueCapacity is less than or equal to zero");
		}

		this.streamBufferQueueCapacity = outputStreamBufferQueueCapacity;
		streamBufferArrayDeque = new ArrayDeque<StreamBuffer>(outputStreamBufferQueueCapacity);
	}

	public boolean offer(StreamBuffer messageStreamBuffer) throws InterruptedException {
		if (null == messageStreamBuffer) {
			throw new IllegalArgumentException("the parameter messageStreamBuffer is null");
		}

		synchronized (monitor) {
			if (streamBufferCount > streamBufferQueueCapacity) {
				/*
				 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME); String
				 * errorMessage = new StringBuilder() .append("최대 출력 스트림 갯수[")
				 * .append(outputStreamBufferQueueCapacity) .append("]를 초과").toString();
				 * log.info(errorMessage);
				 * 
				 */
				return false;
			}

			messageStreamBuffer.setLastBufferLimitUsingLimit();
			streamBufferCount++;
			streamBufferArrayDeque.addLast(messageStreamBuffer);

			if (null == workingStreamBuffer) {
				workingStreamBuffer = messageStreamBuffer;
			}

			return true;
		}
	}

	public boolean offer(StreamBuffer messageStreamBuffer, long timeout)
			throws InterruptedException {
		if (null == messageStreamBuffer) {
			throw new IllegalArgumentException("the parameter messageStreamBuffer is null");
		}

		synchronized (monitor) {
			if (streamBufferCount == streamBufferQueueCapacity) {
				monitor.wait(timeout);
				
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

			return true;
		}
	}

	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreDataPacketBufferException {
		int ret;
		
		synchronized (monitor) {
			if (null == workingStreamBuffer) {
				ret = 0;
			} else {
				ret = workingStreamBuffer.write(writableSocketChannel);

				if (! workingStreamBuffer.hasRemaining()) {

					streamBufferCount--;
					streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();

					if (0 == streamBufferCount) {
						workingStreamBuffer = null;
						/** socket write event turn off */
						ret = -1;
					} else {
						workingStreamBuffer = streamBufferArrayDeque.peekFirst();
					}

					monitor.notify();
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

	public void close() {
		while (!streamBufferArrayDeque.isEmpty()) {
			streamBufferArrayDeque.removeFirst().releaseAllWrapBuffers();
		}
	}
}
