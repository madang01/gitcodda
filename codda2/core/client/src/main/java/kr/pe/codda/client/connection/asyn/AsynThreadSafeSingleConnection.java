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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.connection.asyn.mainbox.AsynMessageMailbox;
import kr.pe.codda.client.connection.asyn.mainbox.SyncMessageMailbox;
import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.OutgoingStreamTimeoutException;
import kr.pe.codda.common.exception.RetryException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.TimeoutDelayException;
import kr.pe.codda.common.io.ClientOutgoingStreamIF;
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMiddleObjectForwarderIF;

/**
 * 쓰레드 세이프한 비동기 단일 연결, 폴에 소속되지 않고 개별적 요청시 생성된다. 
 * 
 * @author Won Jonghoon
 *
 */
public class AsynThreadSafeSingleConnection
		implements AsynConnectionIF, ClientIOEventHandlerIF, ReceivedMiddleObjectForwarderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final String projectName;
	private final String serverHost;
	private final int serverPort;
	private final long socketTimeout;
	private final int maxNumberOfWrapBufferPerMessage;
	private final int clientAsynInputMessageQueueCapacity;
	private final long aliveTimePerWrapBuffer;
	private final long retryIntervalMilliseconds;
	private final int retryIntervalNanoSeconds;
	private final StreamCharsetFamily streamCharsetFamily;	
	private final WrapBufferPoolIF wrapBufferPool;
	private final MessageProtocolIF messageProtocol;
	private final ClientTaskMangerIF clientTaskManger;
	private final AsynConnectedConnectionAdderIF asynConnectedConnectionAdder;
	private final ClientIOEventControllerIF asynClientIOEventController;

	private final SocketChannel clientSC;
	private SelectionKey personalSelectionKey = null;
	private java.util.Date finalReadTime = new java.util.Date();

	private SyncMessageMailbox[] syncMessageMailboxArray = null;
	private ArrayBlockingQueue<SyncMessageMailbox> syncMessageMailboxQueue = null;

	// private ArrayDeque<ArrayDeque<WrapBuffer>> inputMessageQueue = new
	// ArrayDeque<ArrayDeque<WrapBuffer>>();
	private IncomingStream incomingStream = null;
	private ClientOutgoingStreamIF outgoingStream = null;

	/**
	 * 생성자
	 * @param projectName 프로젝트 이름
	 * @param serverHost 서버 호스트 주소
	 * @param serverPort 서버 포트 번호
	 * @param socketTimeout 소켓 타임 아웃 시간, 단위 : milliseconds
	 * @param streamCharsetFamily 문자셋, 문자셋 디코더 그리고 문자셋 인코더 묶음
	 * @param clientDataPacketBufferMaxCntPerMessage 메시지 1개당 랩 버퍼 최대 갯수
	 * @param clientAsynInputMessageQueueCapacity 입력 메시지 큐 크기
	 * @param clientSyncMessageMailboxCountPerAsynShareConnection 소켓 공유할 메시지 박스 최대 갯수
	 * @param aliveTimePerWrapBuffer 랩버퍼 1개당 생존 시간, 단위 : nanoseconds 
	 * @param retryInterval 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격, 단위 nanoseconds, 참고) '송신이 끝난 입력 메시지 스트림 큐'가 비어 있고 '송신중인 입력 메시지 스트림 큐'가 가득 찬 경우에 타임 아웃 시간안에 일정 시간 대기후 '입력 메시지 스트림'을 '송신중인 입력 메시지 스트림 큐' 에  다시 넣기를 시도한다.
	 * @param messageProtocol 메시지 프로토콜
	 * @param wrapBufferPool 랩 버퍼 폴
	 * @param clientTaskManger 클라이언트 타스크 관리자
	 * @param asynConnectedConnectionAdder 비동기 연결 추가자
	 * @param asynClientIOEventController 비동기 클라이언트 입출력 이벤트 제어자
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 */
	public AsynThreadSafeSingleConnection(String projectName, String serverHost, int serverPort, long socketTimeout,
			StreamCharsetFamily streamCharsetFamily, int clientDataPacketBufferMaxCntPerMessage,
			int clientAsynInputMessageQueueCapacity, int clientSyncMessageMailboxCountPerAsynShareConnection,
			long aliveTimePerWrapBuffer, 
			long retryInterval,
			MessageProtocolIF messageProtocol, WrapBufferPoolIF wrapBufferPool, ClientTaskMangerIF clientTaskManger,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientIOEventControllerIF asynClientIOEventController) throws IOException {
		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;		
		this.maxNumberOfWrapBufferPerMessage = clientDataPacketBufferMaxCntPerMessage;
		this.clientAsynInputMessageQueueCapacity = clientAsynInputMessageQueueCapacity;
		this.aliveTimePerWrapBuffer = aliveTimePerWrapBuffer;
		
		retryIntervalMilliseconds = retryInterval / CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS;
		retryIntervalNanoSeconds = (int)(retryInterval % CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS);
		
		this.streamCharsetFamily = streamCharsetFamily;
		this.wrapBufferPool = wrapBufferPool;
		
		this.messageProtocol = messageProtocol;
		this.clientTaskManger = clientTaskManger;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.asynClientIOEventController = asynClientIOEventController;

		this.syncMessageMailboxQueue = new ArrayBlockingQueue<SyncMessageMailbox>(
				clientSyncMessageMailboxCountPerAsynShareConnection);
		this.syncMessageMailboxArray = new SyncMessageMailbox[clientSyncMessageMailboxCountPerAsynShareConnection + CommonStaticFinalVars.SYNC_MAILBOX_START_ID];
		
		
		for (int i = CommonStaticFinalVars.SYNC_MAILBOX_START_ID; i < syncMessageMailboxArray.length; i++) {
			SyncMessageMailbox syncMessageMailbox = new SyncMessageMailbox(this, i, socketTimeout, messageProtocol);
			syncMessageMailboxArray[i] = syncMessageMailbox;
			syncMessageMailboxQueue.offer(syncMessageMailbox);
		}

		// FIXME!
		// log.info("syncMessageMailboxQueue.size={}", syncMessageMailboxQueue.size());

		clientSC = SocketChannel.open();
		clientSC.configureBlocking(false);
		clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		clientSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);

		
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
	 * 송신 스트림에 입력 메시지를 추가한다.
	 * 
	 * @param messageCodecManger 메시지 코덱 관리자
	 * @param inputMessage 입력 메시지
	 * @throws DynamicClassCallException 동적 클래스 처리중 에러 발생디 던지는 예외
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외
	 * @throws BodyFormatException 바디 구성에 문제가 있을 경우 던지는 예외
	 * @throws HeaderFormatException 헤더 구성에 문제가 있을 경우 던지는 예외
	 * @throws IOException 소켓 타임 아웃 포함 입출력 에러 발생시 던지는 예외
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	private void addInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws DynamicClassCallException, NoMoreWrapBufferException, BodyFormatException,
			HeaderFormatException, IOException, InterruptedException {
		
		final long beginTime = System.nanoTime();
		long newTimeout = socketTimeout * CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS;
		final long endTime = beginTime + newTimeout;

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

		StreamBuffer inputMessageStreamBuffer = new StreamBuffer(streamCharsetFamily, maxNumberOfWrapBufferPerMessage, wrapBufferPool);
		try {
			messageProtocol.M2S(inputMessage, messageEncoder, inputMessageStreamBuffer);
			
			inputMessageStreamBuffer.flip();
			inputMessageStreamBuffer.setLastBufferLimitUsingLimit();
		} catch (NoMoreWrapBufferException e) {
			inputMessageStreamBuffer.releaseAllWrapBuffers();
			throw e;
		} catch (BodyFormatException e) {
			inputMessageStreamBuffer.releaseAllWrapBuffers();
			throw e;
		} catch (HeaderFormatException e) {
			inputMessageStreamBuffer.releaseAllWrapBuffers();
			throw e;
		} catch (Exception e) {
			inputMessageStreamBuffer.releaseAllWrapBuffers();
			
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}
		
		
		do {
			try {
				outgoingStream.add(inputMessageStreamBuffer, newTimeout);
				
				break;
			} catch(RetryException e) {
				Thread.sleep(retryIntervalMilliseconds, retryIntervalNanoSeconds);
				
				newTimeout = endTime - System.nanoTime();
				
				if (newTimeout <= 0) {
					String errorMessage = new StringBuilder()
							.append("working outgoing stream queue of this socket[")
							.append(clientSC.hashCode())
							.append("] is full").toString();
					
					throw new OutgoingStreamTimeoutException(errorMessage);
				}				
			} catch(TimeoutDelayException e) {
				inputMessageStreamBuffer.releaseAllWrapBuffers();
				
				Thread.sleep(e.getWaitingTime());
				
				String errorMessage = new StringBuilder()
						.append("throw OutgoingStreamTimeoutException after sleep while ")
						.append(e.getWaitingTime())
						.append(" milliseconds becase outgoing stream queue of this socket[")
						.append(clientSC.hashCode())
						.append("] is full").toString();
				
				throw new OutgoingStreamTimeoutException(errorMessage);
			} catch(InterruptedException | OutgoingStreamTimeoutException e) {
				inputMessageStreamBuffer.releaseAllWrapBuffers();
				
				throw e;
			} catch(Exception e) {
				log.log(Level.WARNING, "unknown error", e);
				
				inputMessageStreamBuffer.releaseAllWrapBuffers();
				
				throw e;
			}
		} while(true);		
		
		
		// long endTime = System.nanoTime();
		// log.info("addInputMessage elasped {} microseconds",TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

	}

	@Override
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreWrapBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException {

		// synchronized (clientSC) {

		SyncMessageMailbox syncMessageMailbox = syncMessageMailboxQueue.poll(socketTimeout, TimeUnit.MILLISECONDS);
		if (null == syncMessageMailbox) {
			/** timeout */
			String errorMessage = "fail to get a SyncMessageMailbox queue element because of timeout";

			throw new ConnectionPoolTimeoutException(errorMessage);
		}

		AbstractMessage outputMessage = null;

		try {
			inputMessage.setMailboxID(syncMessageMailbox.getMailboxID());
			inputMessage.setMailID(syncMessageMailbox.getMailID());			

			addInputMessage(messageCodecManger, inputMessage);

			outputMessage = syncMessageMailbox.getSyncOutputMessage(messageCodecManger);
		} finally {
			syncMessageMailboxQueue.offer(syncMessageMailbox);
		}

		// }

		return outputMessage;

	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, NotSupportedException, IOException, NoMoreWrapBufferException,
			DynamicClassCallException, BodyFormatException {
		inputMessage.setMailboxID(AsynMessageMailbox.getMailboxID());
		inputMessage.setMailID(AsynMessageMailbox.getNextMailID());		

		addInputMessage(messageCodecManger, inputMessage);
	}

	@Override
	public void close() {
		String infoMessage = new StringBuilder().append("socket channel[").append(clientSC.hashCode())
				.append("] closed").toString();
		log.info(infoMessage);

		try {
			clientSC.close();
		} catch (IOException e) {
			String warnMessage = new StringBuilder().append("fail to close the socket channel[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warning(warnMessage);
		}

		asynClientIOEventController.cancel(personalSelectionKey);

		if (null != incomingStream) {
			incomingStream.releaseAllWrapBuffers();
		}
		if (null != outgoingStream) {
			outgoingStream.close();
		}

	}

	@Override
	public SelectionKey register(Selector ioEventSelector, int wantedInterestOps) throws Exception {
		SelectionKey registeredSelectionKey = clientSC.register(ioEventSelector, wantedInterestOps);
		return registeredSelectionKey;
	}

	@Override
	public boolean doConnect() throws Exception {
		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
		boolean isSuceess = clientSC.connect(serverAddress);

		if (isSuceess) {
			isSuceess = clientSC.finishConnect();
		}

		return isSuceess;
	}

	public void doFinishConnect(SelectionKey selectedKey) {
		personalSelectionKey = selectedKey;
		asynConnectedConnectionAdder.addConnectedConnection(this);
		
		incomingStream = new IncomingStream(streamCharsetFamily, maxNumberOfWrapBufferPerMessage, wrapBufferPool);
		outgoingStream = new ClientOutgoingStream(asynClientIOEventController, personalSelectionKey, clientAsynInputMessageQueueCapacity, aliveTimePerWrapBuffer);
	}

	public void doSubtractOneFromNumberOfUnregisteredConnections() {
		asynConnectedConnectionAdder.subtractOneFromNumberOfUnregisteredConnections(this);
	}
	
	@Override
	public void onConnect(SelectionKey selectedKey) throws Exception {
		boolean isSuccess = clientSC.finishConnect();

		if (!isSuccess) {
			String errorMessage = new StringBuilder().append("fail to finish connection[").append(hashCode())
					.append("] becase the returned value is false").toString();
			throw new IOException(errorMessage);
		}

		selectedKey.interestOps(SelectionKey.OP_READ);

		doFinishConnect(selectedKey);
	}

	@Override
	public void putReceivedMiddleObject(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {
		
		
		if (CommonStaticFinalVars.NOCOUNT_ASYN_MAILBOX_ID == mailboxID) {
			try {
				AbstractClientTask clientTask = clientTaskManger.getValidClientTask(messageID);
				clientTask.execute(hashCode(), projectName, this, mailboxID, mailID, messageID, readableMiddleObject,
						messageProtocol);
			} catch (InterruptedException e) {
				throw e;
			} catch (Exception | Error e) {
				log.log(Level.WARNING, "unknwon error::fail to execute a output message client task", e);
				return;
			} finally {
				// readableMiddleObjectWrapper.closeReadableMiddleObject();
				messageProtocol.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);
			}
			
		} else if (CommonStaticFinalVars.COUNT_ASYN_MAILBOX_ID == mailboxID) {			
			
			try {
				AbstractClientTask clientTask = clientTaskManger.getValidClientTask(messageID);
				clientTask.execute(hashCode(), projectName, this, mailboxID, mailID, messageID, readableMiddleObject,
						messageProtocol);
			} catch (InterruptedException e) {
				throw e;
			} catch (Exception | Error e) {
				log.log(Level.WARNING, "unknwon error::fail to execute a output message client task", e);
				return;
			} finally {
				// readableMiddleObjectWrapper.closeReadableMiddleObject();
				messageProtocol.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);
			}
		} else {
			if (mailboxID < CommonStaticFinalVars.SYNC_MAILBOX_START_ID || mailboxID >= syncMessageMailboxArray.length) {
				String errorMessage = new StringBuilder("The synchronous output message[").append("mailboxID=")
						.append(mailboxID).append(", mailID=").append(mailID).append(", messageID=").append(messageID)
						.append("] was discarded because the paramter mailboxID is not valid").toString();

				log.warning(errorMessage);

				messageProtocol.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);

				return;
			}

			syncMessageMailboxArray[mailboxID].putSyncOutputMessage(mailboxID, mailID, messageID, readableMiddleObject);
		}
	}

	@Override
	public void onRead(SelectionKey selectedKey) throws Exception {

		int numberOfReadBytes;
		
		do {
			numberOfReadBytes = incomingStream.read(clientSC);
		}  while (numberOfReadBytes > 0);

		setFinalReadTime();
		messageProtocol.S2O(incomingStream, this);

		if (-1 == numberOfReadBytes) {
			String errorMessage = new StringBuilder("this socket channel[").append(clientSC.hashCode())
					.append("] has reached end-of-stream").toString();

			log.warning(errorMessage);
			close();
			return;
		}
	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws Exception {

		int numberOfWriteBytes;
		do {
			numberOfWriteBytes = outgoingStream.write(clientSC);
		} while (numberOfWriteBytes > 0);
	}

	@Override
	public boolean isConnected() {
		return clientSC.isConnected();
	}

	@Override
	public int hashCode() {
		return clientSC.hashCode();
	}

	@Override
	protected void finalize() {
		close();
	}
}
