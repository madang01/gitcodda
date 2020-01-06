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
import java.net.SocketTimeoutException;
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
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.ClientOutgoingStreamIF;
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMiddleObjectForwarderIF;

public class AsynThreadSafeSingleConnection
		implements AsynConnectionIF, ClientIOEventHandlerIF, ReceivedMiddleObjectForwarderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final String projectName;
	private final String serverHost;
	private final int serverPort;
	private final long socketTimeout;
	private final int clientDataPacketBufferMaxCntPerMessage;
	private final int clientAsynOutputMessageQueueCapacity;
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

	public AsynThreadSafeSingleConnection(String projectName, String serverHost, int serverPort, long socketTimeout,
			StreamCharsetFamily streamCharsetFamily, int clientDataPacketBufferMaxCntPerMessage,
			int clientAsynOutputMessageQueueCapacity, int clientSyncMessageMailboxCountPerAsynShareConnection,
			MessageProtocolIF messageProtocol, WrapBufferPoolIF wrapBufferPool, ClientTaskMangerIF clientTaskManger,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientIOEventControllerIF asynClientIOEventController) throws IOException {
		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;		
		this.clientDataPacketBufferMaxCntPerMessage = clientDataPacketBufferMaxCntPerMessage;
		this.clientAsynOutputMessageQueueCapacity = clientAsynOutputMessageQueueCapacity;
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

	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}

	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}
	
	private void addInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws DynamicClassCallException, NoMoreWrapBufferException, BodyFormatException,
			HeaderFormatException, IOException, InterruptedException {

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

		StreamBuffer inputMessageStreamBuffer = new StreamBuffer(streamCharsetFamily, clientDataPacketBufferMaxCntPerMessage, wrapBufferPool);
		try {
			messageProtocol.M2S(inputMessage, messageEncoder, inputMessageStreamBuffer);
			
			inputMessageStreamBuffer.flip();
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
		
		boolean isSuccess = outgoingStream.offer(inputMessageStreamBuffer, socketTimeout);
		if (! isSuccess) {
			String errorMessage = new StringBuilder()
					.append("소켓 채널[")
					.append(clientSC.hashCode())
					.append("]에 지정한 시간[")
					.append(socketTimeout)
					.append(" ms] 안에 입력 메시지 내용[")
					.append(inputMessage.toString())
					.append("]이 담긴 스트림을 추가하는데 실패하였습니다").toString();
			
			inputMessageStreamBuffer.releaseAllWrapBuffers();
			
			throw new SocketTimeoutException(errorMessage);
		}
		// long endTime = System.nanoTime();
		// log.info("addInputMessage elasped {} microseconds",TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

	}

	@Override
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreWrapBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {

		// synchronized (clientSC) {

		SyncMessageMailbox syncMessageMailbox = syncMessageMailboxQueue.poll(socketTimeout, TimeUnit.MILLISECONDS);
		if (null == syncMessageMailbox) {
			/** timeout */
			String errorMessage = "fail to get a SyncMessageMailbox queue element because of timeout";

			throw new ConnectionPoolTimeoutException(errorMessage);
		}

		AbstractMessage outputMessage = null;

		try {
			syncMessageMailbox.setMessageCodecManger(messageCodecManger);
			inputMessage.setMailboxID(syncMessageMailbox.getMailboxID());
			inputMessage.setMailID(syncMessageMailbox.getMailID());			

			addInputMessage(messageCodecManger, inputMessage);

			outputMessage = syncMessageMailbox.getSyncOutputMessage();
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
		
		incomingStream = new IncomingStream(streamCharsetFamily, clientDataPacketBufferMaxCntPerMessage, wrapBufferPool);
		outgoingStream = new ClientOutgoingStream(asynClientIOEventController, personalSelectionKey, clientAsynOutputMessageQueueCapacity);
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
		
		
		if (CommonStaticFinalVars.SERVER_ASYN_MAILBOX_ID == mailboxID) {
			try {
				AbstractClientTask clientTask = clientTaskManger.getClientTask(messageID);
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
			
		} else if (CommonStaticFinalVars.CLIENT_ASYN_MAILBOX_ID == mailboxID) {
			outgoingStream.decreaseOutputMessageCount();
			
			try {
				AbstractClientTask clientTask = clientTaskManger.getClientTask(messageID);
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

	public boolean isConnected() {
		return clientSC.isConnected();
	}

	public int hashCode() {
		return clientSC.hashCode();
	}

	@Override
	protected void finalize() {
		close();
	}
}
