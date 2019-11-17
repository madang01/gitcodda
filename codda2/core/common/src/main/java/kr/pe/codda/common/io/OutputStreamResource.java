package kr.pe.codda.common.io;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class OutputStreamResource {
	private final Object monitor = new Object();

	private final SocketChannel ownerSocketChannel;
	private final int outputStreamBufferQueueCapacity;
	private final long socketTimeout;

	private final ArrayDeque<StreamBuffer> outputStreamBufferArrayDeque;
	private int outputStreamBufferCount = 0;
	private transient StreamBuffer workingStreamBuffer = null;

	public OutputStreamResource(SocketChannel ownerSocketChannel, int outputStreamBufferQueueCapacity, long socketTimeout) {
		if (null == ownerSocketChannel) {
			throw new IllegalArgumentException("the parameter ownerSocketChannel is null");
		}
		
		if (outputStreamBufferQueueCapacity <= 0) {
			throw new IllegalArgumentException("the parameter outputStreamBufferQueueCapacity is less than or equal to zero");
		}
		
		if (socketTimeout <= 0) {
			throw new IllegalArgumentException("the parameter socketTimeout is less than or equal to zero");
		}

		this.ownerSocketChannel = ownerSocketChannel;
		this.outputStreamBufferQueueCapacity = outputStreamBufferQueueCapacity;
		this.socketTimeout = socketTimeout;

		outputStreamBufferArrayDeque = new ArrayDeque<StreamBuffer>(outputStreamBufferQueueCapacity);
	}
	
	public void addOutputStreamBuffer(StreamBuffer outputMessageStreamBuffer) throws InterruptedException {
		if (null == outputMessageStreamBuffer) {
			throw new IllegalArgumentException("the parameter outputMessageStreamBuffer is null");
		}

		synchronized (monitor) {
			if (outputStreamBufferCount > outputStreamBufferQueueCapacity) {
				// drop
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				String errorMessage = new StringBuilder()
						.append("소켓[hashCode=")
						.append(ownerSocketChannel.hashCode())
						.append("]이 가질 수 있는 최대 출력 스트림 갯수[")
						.append(outputStreamBufferQueueCapacity)
						.append("]를 초과하여 추가할 수 없기때문에 파라미터로 넘어온 출력 스트림[")
						.append(outputMessageStreamBuffer.toHexStringForRemaingData())
						.append("]을 폐기합니다").toString();
				log.log(Level.INFO, errorMessage);
								
				outputMessageStreamBuffer.close();				
				return;
			}
			
			outputStreamBufferCount++;
			outputStreamBufferArrayDeque.addLast(outputMessageStreamBuffer);
			
			if (null == workingStreamBuffer) {
				workingStreamBuffer = outputMessageStreamBuffer;
			}
		}
	}
	

	public void addInputMessageStreamBuffer(StreamBuffer inputMessageStreamBuffer) throws SocketTimeoutException, InterruptedException {
		if (null == inputMessageStreamBuffer) {
			throw new IllegalArgumentException("the parameter inputMessageStreamBuffer is null");
		}

		synchronized (monitor) {
			if (outputStreamBufferCount > outputStreamBufferQueueCapacity) {
				monitor.wait(socketTimeout);
				if (outputStreamBufferCount >= outputStreamBufferQueueCapacity) {
					
					inputMessageStreamBuffer.close();
					
					throw new SocketTimeoutException(
							"fail to add output stream buffer becase max of output stream buffer reseached");
				}
			}

			outputStreamBufferCount++;
			outputStreamBufferArrayDeque.addLast(inputMessageStreamBuffer);
			
			if (null == workingStreamBuffer) {
				workingStreamBuffer = inputMessageStreamBuffer;
			}
		}
	}

	public int write() throws IOException, NoMoreDataPacketBufferException {		
		if (null == workingStreamBuffer) {
			return 0;
		}
		
		int ret = workingStreamBuffer.write(ownerSocketChannel);

		if (! workingStreamBuffer.hasRemaining()) {
			synchronized (monitor) {
				outputStreamBufferCount--;
				outputStreamBufferArrayDeque.removeFirst().close();
				
				if (0  == outputStreamBufferCount) {
					workingStreamBuffer = null;
					ret = -1;
				} else {
					workingStreamBuffer = outputStreamBufferArrayDeque.peekFirst();
				}
				
				monitor.notify();
			}
		}

		return ret;

	}
	/*
	public boolean hasRemaing() {
		boolean hasRemaing = false;
		
		synchronized (monitor) {
			hasRemaing = outputStreamBufferArrayDeque.isEmpty();
		}
		
		return hasRemaing;
	}
	*/
	
	
	public void close() {
		while(! outputStreamBufferArrayDeque.isEmpty()) {
			outputStreamBufferArrayDeque.removeFirst().close();
		}
	}
}
