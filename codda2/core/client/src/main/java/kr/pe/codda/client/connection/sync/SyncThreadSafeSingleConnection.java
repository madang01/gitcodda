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
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;

/**
 * 쓰레드 세이프 한 동기 단일 연결, 폴에 소속되지 않고 개별적 요청시 생성된다. 
 * 
 * @author Won Jonghoon
 *
 */
public final class SyncThreadSafeSingleConnection implements SyncConnectionIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private final Object monitor = new Object();

	
	private final MessageProtocolIF messageProtocol;
	private final int clientDataPacketBufferSize;

	private final SocketChannel clientSC;
	private final Socket clientSocket;	
	// private final int clientDataPacketBufferMaxCntPerMessage;
	// private final StreamCharsetFamily streamCharsetFamily;	
	// private final WrapBufferPoolIF wrapBufferPool;
	private final StreamBuffer inputMessageStreamBuffer;
	private final InputStream clientInputStream;
	private final OutputStream clientOutputStream;
	//private final int mailboxID = CommonStaticFinalVars.SYNC_MAILBOX_START_ID;
	private transient int mailID = Integer.MIN_VALUE;
	private transient java.util.Date finalReadTime = new java.util.Date();	
	private final IncomingStream incomingStream;
	private final byte[] socketBuffer;
	private final SyncOutputMessageReceiver syncOutputMessageReceiver;
	

	/**
	 * 생성자
	 * 
	 * @param serverHost 서버 호스트 주소
	 * @param serverPort 서버 포트 번호
	 * @param socketTimeout 소켓 타임 아웃 시간
	 * @param streamCharsetFamily 문자셋, 문자셋 디코더 그리고 문자셋 인코더 묶음
	 * @param clientDataPacketBufferMaxCntPerMessage 메시지당 랩 버퍼 최대 갯수
	 * @param clientDataPacketBufferSize 메시지 1개당 랩 버퍼 최대 갯수
	 * @param messageProtocol 메시지 프로토콜
	 * @param wrapBufferPool 랩 버퍼 폴
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 */
	public SyncThreadSafeSingleConnection(String serverHost, int serverPort, long socketTimeout, 
			StreamCharsetFamily streamCharsetFamily,
			int clientDataPacketBufferMaxCntPerMessage,
			int clientDataPacketBufferSize,
			MessageProtocolIF messageProtocol,
			WrapBufferPoolIF wrapBufferPool)
			throws IOException {
		
		this.messageProtocol = messageProtocol;
		this.clientDataPacketBufferSize = clientDataPacketBufferSize;
		// this.clientDataPacketBufferMaxCntPerMessage = clientDataPacketBufferMaxCntPerMessage;
		// this.streamCharsetFamily = streamCharsetFamily;
		// this.wrapBufferPool = wrapBufferPool;
		
		inputMessageStreamBuffer = messageProtocol.createNewMessageStreamBuffer();
		socketBuffer = new byte[clientDataPacketBufferSize];
		incomingStream = new IncomingStream(streamCharsetFamily, clientDataPacketBufferMaxCntPerMessage, wrapBufferPool);
		syncOutputMessageReceiver = new SyncOutputMessageReceiver(messageProtocol);

		clientSC = SocketChannel.open();
		clientSC.configureBlocking(true);
		clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		clientSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);

		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);

		clientSocket = clientSC.socket();
		try {
			clientSocket.setSoTimeout((int) socketTimeout);
			clientSocket.connect(serverAddress, (int) socketTimeout);
			clientInputStream = clientSocket.getInputStream();
			clientOutputStream = clientSocket.getOutputStream();
		} catch (IOException e) {
			clientSC.close();

			String errorMessage = new StringBuilder().append("an io error occurred when connecting to the server[host=")
					.append(serverHost).append(", serverPort=").append(serverPort)
					.append("] or building the I / O stream, errmsg=").append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);

			throw e;
		} catch (Exception e) {

			clientSC.close();

			String errorMessage = new StringBuilder()
					.append("an unknown error occurred when connecting to the server[host=").append(serverHost)
					.append(", serverPort=").append(serverPort).append("] or building the I / O stream, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);

			throw new IOException();
		}
	}


	/**
	 * 마지막으로 읽은 시간을 현재 시간으로 갱신한다.
	 */
	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}
	
	/**
	 * @return 마지막으로 읽은 시간
	 */
	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}
	
	/**
	 * 연결이 가진 자원을 해제한다.
	 */
	private void releaseResources() {
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
			clientSC.close();
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to close the socket channel[")
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
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreWrapBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException {

		final AbstractMessage outputMessage;
		final AbstractMessageEncoder messageEncoder;
		
		synchronized (monitor) {
			inputMessageStreamBuffer.clear();
			
			syncOutputMessageReceiver.ready(messageCodecManger);

			if (Integer.MAX_VALUE == mailID) {
				mailID = Integer.MIN_VALUE;
			} else {
				mailID++;
			}

			inputMessage.setMailboxID(CommonStaticFinalVars.SYNC_MAILBOX_START_ID);
			inputMessage.setMailID(mailID);

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

			while ( inputMessageStreamBuffer.hasRemaining()) {
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
				throw new IOException(errorMessage);
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
				String errorMessage = "메시지 추출 후 잔존 데이터가 남아 있습니다";
				close();
				throw new IOException(errorMessage);
			}
			
			if (syncOutputMessageReceiver.isError()) {
				close();
				throw new IOException(syncOutputMessageReceiver.getErrorMessage());
			}
			
			outputMessage = syncOutputMessageReceiver.getReceiveMessage();
		}		

		return outputMessage;
	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			IOException, NoMoreWrapBufferException, DynamicClassCallException, BodyFormatException {
		throw new NotSupportedException(
				"this connection doesn't support this method because it is a blocking mode connection and no sharing connection between threads");
	}

	@Override
	public void close() {
		try {
			clientSC.close();
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("fail to close this connection[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warning(errorMessage);
		}

		releaseResources();
	}

	@Override
	public boolean isConnected() {
		return clientSocket.isConnected();
	}

	
}
