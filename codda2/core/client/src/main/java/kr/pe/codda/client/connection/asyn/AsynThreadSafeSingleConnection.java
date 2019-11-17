package kr.pe.codda.client.connection.asyn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.CancelledKeyException;
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
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.InputStreamResource;
import kr.pe.codda.common.io.OutputStreamResource;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageForwarderIF;

public class AsynThreadSafeSingleConnection
		implements AsynConnectionIF, ClientIOEventHandlerIF, ReceivedMessageForwarderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	// private final Object writeMonitor = new Object();
	// private final Object readMonitor = new Object();
	private final String projectName;
	private final String serverHost;
	private final int serverPort;
	private final long socketTimeout;
	// int syncMessageMailboxCountPerAsynShareConnection;
	private final MessageProtocolIF messageProtocol;
	// private WrapBufferPoolIF wrapBufferPool = null;
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
	private final InputStreamResource isr;
	private final OutputStreamResource osr;

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
		// this.syncMessageMailboxCountPerAsynShareConnection = syncMessageMailboxCountPerAsynShareConnection;
		this.messageProtocol = messageProtocol;
		// this.wrapBufferPool = wrapBufferPool;
		this.clientTaskManger = clientTaskManger;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.asynClientIOEventController = asynClientIOEventController;

		this.syncMessageMailboxQueue = new ArrayBlockingQueue<SyncMessageMailbox>(
				clientSyncMessageMailboxCountPerAsynShareConnection);
		this.syncMessageMailboxArray = new SyncMessageMailbox[clientSyncMessageMailboxCountPerAsynShareConnection + 1];
		for (int i = 1; i < syncMessageMailboxArray.length; i++) {
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

		isr = new InputStreamResource(streamCharsetFamily, clientDataPacketBufferMaxCntPerMessage, wrapBufferPool);
		osr = new OutputStreamResource(clientSC, clientAsynOutputMessageQueueCapacity, socketTimeout);
	}

	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}

	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}
	
	private void addInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException,
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

		StreamBuffer inputMessageStreamBuffer = null;
		try {
			inputMessageStreamBuffer = messageProtocol.M2S(inputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
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
		
		osr.addInputMessageStreamBuffer(inputMessageStreamBuffer);

		try {
			turnOnSocketWriteMode();				
		} catch (CancelledKeyException e) {
			String errorMessage = new StringBuilder().append("fail to turn on  'OP_WRITE'[socket channel=")
					.append(clientSC.hashCode())
					.append("] becase CancelledKeyException occured")
					.toString();

			log.warning(errorMessage);
			throw new IOException(errorMessage);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to turn on  'OP_WRITE'[socket channel=")
					.append(clientSC.hashCode())
					.append("] becase unknown error occured")
					.toString();

			log.log(Level.WARNING, errorMessage, e);
			throw new IOException(errorMessage);
		}

		// long endTime = System.nanoTime();
		// log.info("addInputMessage elasped {} microseconds",TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

	}

	@Override
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
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
			inputMessage.messageHeaderInfo.mailboxID = syncMessageMailbox.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = syncMessageMailbox.getMailID();

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
			throws InterruptedException, NotSupportedException, IOException, NoMoreDataPacketBufferException,
			DynamicClassCallException, BodyFormatException {
		inputMessage.messageHeaderInfo.mailboxID = AsynMessageMailbox.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = AsynMessageMailbox.getNextMailID();

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

		isr.close();
		osr.close();

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
	}

	public void doSubtractOneFromNumberOfUnregisteredConnections() {
		asynConnectedConnectionAdder.subtractOneFromNumberOfUnregisteredConnections(this);
	}
	
	private void turnOnSocketWriteMode() throws CancelledKeyException {
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() | SelectionKey.OP_WRITE);

		// log.info("call turn on OP_WRITE[{}]",
		// acceptedSocketChannel.hashCode());
	}

	private void turnOffSocketWriteMode() throws CancelledKeyException {
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() & ~SelectionKey.OP_WRITE);

		// log.info("call turn off OP_WRITE[{}]",
		// acceptedSocketChannel.hashCode());
	}

	public void turnOnSocketReadMode() throws CancelledKeyException {
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() | SelectionKey.OP_READ);

		// log.info("call turn on OP_READ[{}]",
		// acceptedSocketChannel.hashCode());
	}

	public void turnOffSocketReadMode() throws CancelledKeyException {
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() & ~SelectionKey.OP_READ);

		// log.info("call turn off OP_READ[{}]",
		// acceptedSocketChannel.hashCode());
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
	public void putReceivedMessage(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {
		if (CommonStaticFinalVars.ASYN_MAILBOX_ID == mailboxID) {
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
			if (mailboxID <= 0 || mailboxID > syncMessageMailboxArray.length) {
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

		int numberOfReadBytes = isr.read(clientSC);

		while (numberOfReadBytes > 0) {
			setFinalReadTime();
			messageProtocol.S2O(isr, this);

			numberOfReadBytes = isr.read(clientSC);
		}

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

		int numberOfWriteBytes = osr.write();

		while (numberOfWriteBytes > 0) {
			numberOfWriteBytes = osr.write();
		}

		if (-1 == numberOfWriteBytes) {
			try {
				turnOffSocketWriteMode();
			} catch (CancelledKeyException e) {
				String errorMessage = new StringBuilder().append("fail to turn off  'OP_WRITE'[socket channel=")
						.append(clientSC.hashCode()).append("] becase CancelledKeyException occured").toString();

				log.warning(errorMessage);
				throw new IOException(errorMessage);
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to turn off  'OP_WRITE'[socket channel=")
						.append(clientSC.hashCode()).append("] becase unknown error occured").toString();

				log.log(Level.WARNING, errorMessage, e);
				throw new IOException(errorMessage);
			}
		}
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
