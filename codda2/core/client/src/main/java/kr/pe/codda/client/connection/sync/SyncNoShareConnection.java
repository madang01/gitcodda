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
package kr.pe.codda.client.connection.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;

/**
 * Note that this implementation is not synchronized.
 * 
 * @author Won jonghoon
 *
 */
public final class SyncNoShareConnection implements SyncConnectionIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	
	private final MessageProtocolIF messageProtocol;
	private final int clientDataPacketBufferSize;
	// private WrapBufferPoolIF wrapBufferPool = null;

	// private final String serverHost;
	// private final int serverPort;
	// private final long socketTimeout;
	
	// private final int clientDataPacketBufferMaxCntPerMessage;
	// private final StreamCharsetFamily streamCharsetFamily;	
	// private final WrapBufferPoolIF wrapBufferPool;
	private final StreamBuffer inputMessageStreamBuffer;
	private final byte[] socketBuffer;

	private final SocketChannel clientSC;
	private final Socket clientSocket;
	private final InputStream clientInputStream;
	private final OutputStream clientOutputStream;
	// private final int mailboxID = CommonStaticFinalVars.SYNC_MAILBOX_START_ID;
	private transient int mailID = Integer.MIN_VALUE;
	private transient java.util.Date finalReadTime = new java.util.Date();
	private boolean isQueueIn = true;
	private final SyncOutputMessageReceiver syncOutputMessageReceiver;
	private final IncomingStream incomingStream;
	

	public SyncNoShareConnection(String serverHost, int serverPort, long socketTimeout, 
			StreamCharsetFamily streamCharsetFamily,
			int clientDataPacketBufferMaxCntPerMessage,
			int clientDataPacketBufferSize,
			MessageProtocolIF messageProtocol,
			WrapBufferPoolIF wrapBufferPool)
			throws IOException {
		// this.serverHost = serverHost;
		// this.serverPort = serverPort;
		// this.socketTimeout = socketTimeout;
		// this.streamCharsetFamily = streamCharsetFamily;
		// this.clientDataPacketBufferSize = clientDataPacketBufferSize;
		this.messageProtocol = messageProtocol;
		this.clientDataPacketBufferSize = clientDataPacketBufferSize;
		// this.clientDataPacketBufferMaxCntPerMessage = clientDataPacketBufferMaxCntPerMessage;
		// this.streamCharsetFamily = streamCharsetFamily;
		// this.wrapBufferPool = wrapBufferPool;

		inputMessageStreamBuffer = messageProtocol.createNewMessageStreamBuffer();
		socketBuffer = new byte[clientDataPacketBufferSize];		
		syncOutputMessageReceiver = new SyncOutputMessageReceiver(messageProtocol);
		
		incomingStream = new IncomingStream(streamCharsetFamily, clientDataPacketBufferMaxCntPerMessage, wrapBufferPool);

		// openSocketChannel();
		clientSC = SocketChannel.open();
		clientSC.configureBlocking(true);
		clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		clientSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);

		// connectAndBuildIOStream();
		
		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
		
		clientSocket = clientSC.socket();
		try {
			clientSocket.setSoTimeout((int) socketTimeout);
			clientSocket.connect(serverAddress, (int) socketTimeout);
			clientInputStream = clientSocket.getInputStream();
			clientOutputStream = clientSocket.getOutputStream();
		} catch (IOException e) {
			close();

			String errorMessage = new StringBuilder()
					.append("an io error occurred when connecting to the server or building the I / O stream, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);

			throw e;
		} catch (Exception e) {
			close();

			String errorMessage = new StringBuilder().append(
					"an unknown error occurred when connecting to the server or building the I / O stream, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);

			throw new IOException();
		}
	}


	@Override
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreWrapBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {
		inputMessageStreamBuffer.clear();		
		
		if (Integer.MAX_VALUE == mailID) {
			mailID = Integer.MIN_VALUE;
		} else {
			mailID++;
		}

		syncOutputMessageReceiver.ready(messageCodecManger);
		inputMessage.setMailboxID(CommonStaticFinalVars.SYNC_MAILBOX_START_ID);
		inputMessage.setMailID(mailID);

		AbstractMessageEncoder messageEncoder = null;

		try {
			messageEncoder = messageCodecManger.getMessageEncoder(inputMessage.getMessageID());
		} catch (DynamicClassCallException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}

		try {
			messageProtocol.M2S(inputMessage, messageEncoder, inputMessageStreamBuffer);
		} catch (NoMoreWrapBufferException e) {
			throw e;
		} catch (BodyFormatException e) {
			throw e;
		} catch (HeaderFormatException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}
		
		inputMessageStreamBuffer.flip();
		

		while (inputMessageStreamBuffer.hasRemaining()) {
			int length = (int)Math.min(clientDataPacketBufferSize, inputMessageStreamBuffer.remaining());
			
			inputMessageStreamBuffer.getBytes(socketBuffer, 0, length);
			try {
				clientOutputStream.write(socketBuffer, 0, length);
			} catch (IOException e) {
				String errorMessage = new StringBuilder().append("the io error occurred while writing a input message[")
						.append(inputMessage.toString())
						.append("] to the socket[")
						.append(clientSC.hashCode()).append("], errmsg=")
						.append(e.getMessage()).toString();
				close();
				throw new IOException(errorMessage);
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("the unknown error occurred while writing a input message[")
						.append(inputMessage.toString())
						.append("] to the socket[")
						.append(clientSC.hashCode()).append("], errmsg=")
						.append(e.getMessage()).toString();
				log.log(Level.WARNING, errorMessage, e);
				close();
				throw new IOException(errorMessage);
			}
		}
		
		inputMessageStreamBuffer.releaseAllWrapBuffers();

		try {
			do {
				int numberOfReadBytes = clientInputStream.read(socketBuffer);
				// int numberOfReadBytes = receivedDataOnlyStream.read(clientInputStream, socketBuffer);
				
				if (numberOfReadBytes > 0) {
					incomingStream.putBytes(socketBuffer, 0, numberOfReadBytes);
					
					setFinalReadTime();

					messageProtocol.S2O(incomingStream, syncOutputMessageReceiver);
				}

				if (numberOfReadBytes == -1) {
					String errorMessage = new StringBuilder("this socket channel[").append(clientSC.hashCode())
							.append("] has reached end-of-stream").toString();

					log.warning(errorMessage);
					close();
					throw new IOException(errorMessage);
				}

				

				// log.info("numberOfReadBytes={}, readableMiddleObjectWrapperQueue.isEmpty={}",
				// numberOfReadBytes, readableMiddleObjectWrapperQueue.isEmpty());
			} while (! syncOutputMessageReceiver.isReceivedMessage() && ! syncOutputMessageReceiver.isError());

		} catch (NoMoreWrapBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			close();

			throw new NoMoreWrapBufferException(errorMessage);
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("the io error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();			
			close();
			log.log(Level.WARNING, errorMessage, e);
			
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("the unknown error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			close();
			throw new IOException(errorMessage);
		} finally {
			Arrays.fill(socketBuffer, CommonStaticFinalVars.ZERO_BYTE);
		}
		
		if (0 != incomingStream.getPosition()) {
			String errorMessage = "메시지 추출 후 잔존 데이터가 남아 있습니다::"+incomingStream.getPosition();
			close();
			throw new IOException(errorMessage);
		}
		
		if (syncOutputMessageReceiver.isError()) {
			close();
			throw new IOException(syncOutputMessageReceiver.getErrorMessage());
		}

		AbstractMessage outputMessage = syncOutputMessageReceiver.getReceiveMessage();

		return outputMessage;
	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, NotSupportedException, IOException, NoMoreWrapBufferException,
			DynamicClassCallException, BodyFormatException {
		throw new NotSupportedException(
				"this connection doesn't support this method because it is a blocking mode connection and no sharing connection between threads");
	}

	@Override
	public void close() {
		if (null != clientInputStream) {
			try {
				clientInputStream.close();
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to close the input stream of socket channel[")
						.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
				log.warning(errorMessage);
			}
		}

		if (null != clientOutputStream) {
			try {
				clientOutputStream.close();
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to close the output stream of socket channel[")
						.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
				log.warning(errorMessage);
			}
		}

		try {
			clientSocket.close();
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to close this connection[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warning(errorMessage);
		}

		inputMessageStreamBuffer.releaseAllWrapBuffers();
		
		incomingStream.releaseAllWrapBuffers();

		String infoMessage = new StringBuilder().append("this connection[")
				.append(clientSC.hashCode())
				.append("]'s resources has been released").toString();
		
		log.info(infoMessage);
	}

	@Override
	public boolean isConnected() {
		return clientSocket.isConnected();
	}

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	protected void queueIn() {
		isQueueIn = true;
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	protected void queueOut() {
		isQueueIn = false;
	}

	public boolean isInQueue() {
		return isQueueIn;
	}

	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}

	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}

	@Override
	protected void finalize() {
		String infoMessage = new StringBuilder().append("the SyncNoShareConnection[")
				.append(clientSC.hashCode())
				.append("]] instance call the 'finalize' method").toString();
		
		log.info(infoMessage);
		close();
	}
}
