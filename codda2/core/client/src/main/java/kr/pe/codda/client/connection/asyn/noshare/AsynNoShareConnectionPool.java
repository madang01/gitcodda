package kr.pe.codda.client.connection.asyn.noshare;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.ClientIOEventControllerIF;
import kr.pe.codda.client.connection.asyn.ClientIOEventHandlerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;

public class AsynNoShareConnectionPool implements ConnectionPoolIF, AsynConnectedConnectionAdderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	private final Object monitor = new Object();

	private final String projectName;
	private final String serverHost;
	private final int serverPort;
	private final long socketTimeout;
	private final StreamCharsetFamily streamCharsetFamily;
	private final int clientDataPacketBufferMaxCntPerMessage;
	private final int clientAsynOutputMessageQueueCapacity;
	private final int clientConnectionCount; 
	
	
	
	private MessageProtocolIF messageProtocol = null; 
	private ClientTaskMangerIF clientTaskManger = null;
	private WrapBufferPoolIF wrapBufferPool = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;

	
	private ArrayDeque<AsynNoShareConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;	
	private transient int numberOfUnregisteredConnections = 0;

	private ClientIOEventControllerIF asynClientIOEventController = null;

	public AsynNoShareConnectionPool(String projectName, String serverHost, int serverPort, long socketTimeout,
			StreamCharsetFamily streamCharsetFamily,
			int clientDataPacketBufferMaxCntPerMessage,
			int clientAsynOutputMessageQueueCapacity,
			int clientConnectionCount,
			MessageProtocolIF messageProtocol, 
			ClientTaskMangerIF clientTaskManger,
			WrapBufferPoolIF wrapBufferPool,
			ConnectionPoolSupporterIF connectionPoolSupporter,
			ClientIOEventControllerIF asynClientIOEventController)
			throws NoMoreDataPacketBufferException, IOException {
		

		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		if (null == clientTaskManger) {
			throw new IllegalArgumentException("the parameter clientTaskManger is null");
		}
		
		if (null == wrapBufferPool) {
			throw new IllegalArgumentException("the parameter wrapBufferPool is null");
		}

		if (null == connectionPoolSupporter) {
			throw new IllegalArgumentException("the parameter connectionPoolSupporter is null");
		}

		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;
		this.streamCharsetFamily = streamCharsetFamily;
		this.clientDataPacketBufferMaxCntPerMessage = clientDataPacketBufferMaxCntPerMessage;
		this.clientAsynOutputMessageQueueCapacity = clientAsynOutputMessageQueueCapacity;		
		this.clientConnectionCount = clientConnectionCount;
		
		this.messageProtocol = messageProtocol;
		this.clientTaskManger = clientTaskManger;
		this.wrapBufferPool = wrapBufferPool;
		this.connectionPoolSupporter = connectionPoolSupporter;
		this.asynClientIOEventController = asynClientIOEventController;

		connectionQueue = new ArrayDeque<AsynNoShareConnection>(clientConnectionCount);

		connectionPoolSupporter.registerPool(this);
	}


	@Override
	public ConnectionIF getConnection() throws InterruptedException, ConnectionPoolTimeoutException, ConnectionPoolException {
		AsynNoShareConnection asynNoShareConnection = null;
		boolean loop = false;

		long currentSocketTimeOut = socketTimeout;
		long startTime = System.nanoTime();

		synchronized (monitor) {
			do {				
				if (0 == numberOfConnection) {
					connectionPoolSupporter.notice("no more connection");
					throw new ConnectionPoolException("check server is alive or something is bad");
				}
				
				if (connectionQueue.isEmpty()) {					
					monitor.wait(currentSocketTimeOut);					
					
					if (connectionQueue.isEmpty()) {						
						throw new ConnectionPoolTimeoutException("1.asynchronized no-share connection pool timeout");
					}
				}
				
				asynNoShareConnection = connectionQueue.pollFirst();				
				
				if (asynNoShareConnection.isConnected()) {
					asynNoShareConnection.queueOut();
					loop = false;
				} else {
					loop = true;
					
					/**
					 * <pre>
					 * 폴에서 꺼낸 연결이 닫힌 경우 폐기한다. 단  이러한 작업은 큐에 넣어진 상태에서 이루어 져야 한다.
					 * 왜냐하면 연결은 참조 포인트가 없어 gc 될때 큐에 넣어진 상태가 아니면 로그를 남기는데
					 * 사용자 한테 넘기기전 폐기이므로 gc 될때 로그를 남기지 말아야 하기때문이다.  
					 * </pre>   
					 */
					String reasonForLoss = new StringBuilder("폴에서 꺼낸 연결[")							
							.append(asynNoShareConnection.hashCode()).append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;
					
					String warnMessage = new StringBuilder()
							.append(reasonForLoss)
							.append(", numberOfConnection=")
							.append(numberOfConnection).toString();
					

					log.warning(warnMessage);

					connectionPoolSupporter.notice(reasonForLoss);
					
					long elapsedTime = TimeUnit.MICROSECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS);
					
					currentSocketTimeOut -= elapsedTime;
					
					if (currentSocketTimeOut <= 0) {
						throw new ConnectionPoolTimeoutException("2.asynchronized no-share connection pool timeout");
					}
				}

			} while (loop);
		}
		
		/*long endTime = System.nanoTime();
		log.info("getConnection::elasped {} microseconds, connectionQueue.size={}",
				TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS),
				connectionQueue.size());*/

		return asynNoShareConnection;
	}

	@Override
	public void release(ConnectionIF conn) throws ConnectionPoolException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.log(Level.WARNING, errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof AsynNoShareConnection)) {
			String errorMessage = "the parameter conn is not instace of AsynNoShareConnection class";
			log.log(Level.WARNING, errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		AsynNoShareConnection asynNoShareConnection = (AsynNoShareConnection) conn;
	
		synchronized (monitor) {
			
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (asynNoShareConnection.isInQueue()) {
				String errorMessage = new StringBuilder()
				.append("the paramter conn[")
				.append(conn.hashCode())
				.append("] allready was in connection queue").toString();
				log.log(Level.WARNING, errorMessage, new Throwable());
				throw new ConnectionPoolException(errorMessage);
			}

			/**
			 * 큐에 넣어진 상태로 변경
			 */
			asynNoShareConnection.queueIn();

			if (! asynNoShareConnection.isConnected()) {
				/**
				 * <pre>
				 * 반환된 연결이 닫힌 경우 폐기한다. 단  이러한 작업은 큐에 넣어진 상태에서 이루어 져야 한다.
				 * 왜냐하면 연결은 참조 포인트가 없어 gc 될때 큐에 넣어진 상태가 아니면 로그를 남기는데
				 * 정상적인 반환이므로 gc 될때 로그를 남기지 말아야 하기때문이다.  
				 * </pre>   
				 */
				numberOfConnection--;

				String reasonForLoss = new StringBuilder("반환된 연결[")
						.append(asynNoShareConnection.hashCode())
						.append("]이 닫혀있어 폐기").toString();
				
				String warnMessage = new StringBuilder()
						.append(reasonForLoss)
						.append(", numberOfConnection=")
						.append(numberOfConnection).toString();
				

				log.warning(warnMessage);

				connectionPoolSupporter.notice(reasonForLoss);
				return;
			}

			connectionQueue.addLast(asynNoShareConnection);
			
			monitor.notify();
		}
		
	}
	
	public void fillAllConnection() throws NoMoreDataPacketBufferException, IOException, InterruptedException {
		/*
		log.info("numberOfUnregisteredConnection={}, numberOfConnection={}," +
				"clientConnectionCount={}",
				numberOfUnregisteredConnection,
				numberOfConnection,
				clientConnectionCount);*/
		
		synchronized (monitor) {				
			while (numberOfUnregisteredConnections
					< (clientConnectionCount - numberOfConnection)) {				
				
				ClientIOEventHandlerIF unregisteredAsynConnection = newUnregisteredConnection();
				numberOfUnregisteredConnections++;
				asynClientIOEventController.addUnregisteredAsynConnection(unregisteredAsynConnection);
			}
		}
		
		asynClientIOEventController.wakeup();
	}
	
	@Override
	public void subtractOneFromNumberOfUnregisteredConnections(AsynConnectionIF unregisteredAsynConnection) {
		synchronized (monitor) {
			numberOfUnregisteredConnections--;
		}
		
		String infoMessage = new StringBuilder()
				.append("the var numberOfUnregisteredConnections subtract one unregistered connection[")
				.append(unregisteredAsynConnection.hashCode())
				.append("]").toString();
		
		log.info(infoMessage);
	}

	private ClientIOEventHandlerIF newUnregisteredConnection() throws NoMoreDataPacketBufferException, IOException {
		

		ClientIOEventHandlerIF asynInterestedConnection = new AsynNoShareConnection(projectName,
				serverHost,
				serverPort,
				socketTimeout,
				streamCharsetFamily,				
				clientDataPacketBufferMaxCntPerMessage,
				clientAsynOutputMessageQueueCapacity,
				messageProtocol, wrapBufferPool, clientTaskManger, this, 
				asynClientIOEventController);
		return asynInterestedConnection;
	}

	@Override
	public String getPoolState() {
		return new StringBuilder()
		.append("numberOfConnection=")
		.append(numberOfConnection)
		.append(", connectionQueue.size=")
		.append(connectionQueue.size()).toString();
	}

	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) {
		/*if (0 == numberOfUnregisteredConnection) {
			throw new ConnectionPoolException(
					"fail to add a connection because the var numberOfInterrestedConnection is zero");
		}*/
		
		// long startTime = System.nanoTime();

		synchronized (monitor) {
			connectionQueue.addLast((AsynNoShareConnection)connectedAsynConnection);
			numberOfConnection++;
			numberOfUnregisteredConnections--;
			monitor.notify();
		}
		
		/*long endTime = System.nanoTime();
		
		log.info("Successfully added the connected connection[{}] to this connection pool, errasped {} micoseconds",
				connectedAsynConnection.hashCode(), 
				TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));*/
	}
}
