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

package kr.pe.codda.client.connection.asyn.noshare;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
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
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.RetryException;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;

/**
 * 비동기 비공유 연결 폴
 * @author Won Jonghoon
 *
 */
public class AsynNoShareConnectionPool implements ConnectionPoolIF, AsynConnectedConnectionAdderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	//private final Object monitor = new Object();
	
	private final ReentrantLock lock = new ReentrantLock();

	private final String projectName;
	private final String serverHost;
	private final int serverPort;
	private final long socketTimeout;
	private final StreamCharsetFamily streamCharsetFamily;
	private final int clientWrapBufferMaxCntPerMessage;
	private final int clientAsynInputMessageQueueCapacity;
	private final long aliveTimePerWrapBuffer;
	private final long retryIntervaTimeToAddInputMessage;
	private final long millisecondsOfRetryIntervaTimeToGetConnection;
	private final int nanosecondsOfRetryIntervaTimeToGetConnection;
	private final int clientConnectionCount; 
	
	
	
	private MessageProtocolIF messageProtocol = null; 
	private ClientTaskMangerIF clientTaskManger = null;
	private WrapBufferPoolIF wrapBufferPool = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;

	
	private ArrayDeque<AsynNoShareConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;	
	private transient int numberOfUnregisteredConnections = 0;

	private ClientIOEventControllerIF asynClientIOEventController = null;
	
	/**
	 * 생성자
	 * @param projectName 프로젝트 이름
	 * @param serverHost 서버 호스트 주소
	 * @param serverPort 서버 포트 번호
	 * @param socketTimeout  소켓 타임 아웃 시간, 단위 : milliseconds
	 * @param streamCharsetFamily 스트림을 다룰때 자주 사용하는 문자셋 관련 객체들 묶음
	 * @param clientWrapBufferMaxCntPerMessage 메시지 1개당 랩 버퍼 최대 갯수
	 * @param clientAsynInputMessageQueueCapacity 비동기 입력 메시지 큐 최대 수용 능력
	 * @param aliveTimePerWrapBuffer 랩버퍼 1개당 생존 시간, 단위 : nanoseconds
	 * @param retryIntervaTimeToAddInputMessage '송신중인 입력 메시지 스트림 큐' 에  메시지 스트림을 다시 추가하는 간격, 단위 nanoseconds
	 * @param retryIntervaTimeToGetConnection 연결을 얻기 위한 재 시도 간격, 단위 nanoseconds
	 * @param clientConnectionCount 클라이언트 연결 갯수
	 * @param messageProtocol 메시지 프로토콜
	 * @param clientTaskManger 클라이언트 타스크 관리자
	 * @param wrapBufferPool 래 버퍼 폴
	 * @param connectionPoolSupporter 연결 폴 도우미
	 * @param asynClientIOEventController 비동기 클라이언트 입출력 이벤트 제어자
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 */
	public AsynNoShareConnectionPool(String projectName, String serverHost, int serverPort, long socketTimeout,
			StreamCharsetFamily streamCharsetFamily,
			int clientWrapBufferMaxCntPerMessage,
			int clientAsynInputMessageQueueCapacity,
			long aliveTimePerWrapBuffer, 
			long retryIntervaTimeToAddInputMessage,
			long retryIntervaTimeToGetConnection,
			int clientConnectionCount,
			MessageProtocolIF messageProtocol, 
			ClientTaskMangerIF clientTaskManger,
			WrapBufferPoolIF wrapBufferPool,
			ConnectionPoolSupporterIF connectionPoolSupporter,
			ClientIOEventControllerIF asynClientIOEventController)
			throws NoMoreWrapBufferException, IOException {
		

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
		this.clientWrapBufferMaxCntPerMessage = clientWrapBufferMaxCntPerMessage;
		this.clientAsynInputMessageQueueCapacity = clientAsynInputMessageQueueCapacity;
		this.aliveTimePerWrapBuffer = aliveTimePerWrapBuffer;
		this.retryIntervaTimeToAddInputMessage = retryIntervaTimeToAddInputMessage;
		this.millisecondsOfRetryIntervaTimeToGetConnection = retryIntervaTimeToGetConnection / CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS;;
		this.nanosecondsOfRetryIntervaTimeToGetConnection = (int)(retryIntervaTimeToGetConnection % CommonStaticFinalVars.ONE_MILLISECONDS_EXPRESSED_IN_NANOSECONDS);
		
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
	public ConnectionIF getConnection() throws InterruptedException, ConnectionPoolTimeoutException {
		
		long lockBeginTime = System.nanoTime();		
		long newTimeout = TimeUnit.NANOSECONDS.convert(socketTimeout, TimeUnit.MILLISECONDS);
		long endTimeForTimeout = lockBeginTime + newTimeout;
		
		AsynNoShareConnection asynNoShareConnection = null;
		while (true) {
			
			try {
				asynNoShareConnection = doGetConnection(endTimeForTimeout, newTimeout);
				break;
			} catch(RetryException e) {
				Thread.sleep(millisecondsOfRetryIntervaTimeToGetConnection, nanosecondsOfRetryIntervaTimeToGetConnection);
				
				
				newTimeout = endTimeForTimeout - System.nanoTime();
				
				if (newTimeout <= 0) {
					throw new ConnectionPoolTimeoutException("2.asynchronized no-share connection pool timeout");
				}
			}
		}
		
		
		return asynNoShareConnection;
		
	}
	
	public AsynNoShareConnection doGetConnection(long endTimeForTimeout, long newTimeout) throws InterruptedException, RetryException, ConnectionPoolTimeoutException {
		boolean isLocked = lock.tryLock(newTimeout, TimeUnit.NANOSECONDS);

		if (!isLocked) {
			throw new ConnectionPoolTimeoutException("fail to get a asyn noshare connection pool lock");
		}
		
		AsynNoShareConnection asynNoShareConnection = null;
		
		try {
			
			while (true) {				
				asynNoShareConnection = connectionQueue.removeFirst();
				
				if (null == asynNoShareConnection) {
					throw new RetryException();
				}
				
				
				if (asynNoShareConnection.isConnected()) {
					asynNoShareConnection.queueOut();
					break;
				}				
				
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
				
				newTimeout = endTimeForTimeout - System.nanoTime();
				
				if (newTimeout <= 0) {
					throw new ConnectionPoolTimeoutException("1.asynchronized no-share connection pool timeout");
				}
			}
			
		} finally {
			lock.unlock();
		}
		
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
	
		lock.lock();
		try {
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
			
		} finally {
			lock.unlock();
		}
		
	}
	
	@Override
	public void fillAllConnection() throws NoMoreWrapBufferException, IOException, InterruptedException {
		/*
		log.info("numberOfUnregisteredConnection={}, numberOfConnection={}," +
				"clientConnectionCount={}",
				numberOfUnregisteredConnection,
				numberOfConnection,
				clientConnectionCount);*/
		
		lock.lock();
		try {			
			while (numberOfUnregisteredConnections
					< (clientConnectionCount - numberOfConnection)) {				
				
				ClientIOEventHandlerIF unregisteredAsynConnection = newUnregisteredConnection();
				numberOfUnregisteredConnections++;
				asynClientIOEventController.addUnregisteredAsynConnection(unregisteredAsynConnection);
			}
		} finally {
			lock.unlock();
		}
		
		asynClientIOEventController.wakeup();
	}
	
	@Override
	public void subtractOneFromNumberOfUnregisteredConnections(AsynConnectionIF unregisteredAsynConnection) {
		lock.lock();
		try {
			numberOfUnregisteredConnections--;
		} finally {
			lock.unlock();
		}
		
		String infoMessage = new StringBuilder()
				.append("the var numberOfUnregisteredConnections subtract one unregistered connection[")
				.append(unregisteredAsynConnection.hashCode())
				.append("]").toString();
		
		log.info(infoMessage);
	}

	/**
	 * @return 연결 확립 되지 않은 신규 연결
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 */
	private ClientIOEventHandlerIF newUnregisteredConnection() throws NoMoreWrapBufferException, IOException {
		

		ClientIOEventHandlerIF asynInterestedConnection = new AsynNoShareConnection(projectName,
				serverHost,
				serverPort,
				socketTimeout,
				streamCharsetFamily,				
				clientWrapBufferMaxCntPerMessage,
				clientAsynInputMessageQueueCapacity,
				aliveTimePerWrapBuffer, 
				retryIntervaTimeToAddInputMessage, 
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

		lock.lock();
		try {
			connectionQueue.addLast((AsynNoShareConnection)connectedAsynConnection);
			numberOfConnection++;
			numberOfUnregisteredConnections--;
		} finally {
			lock.unlock();
		}
		
		/*long endTime = System.nanoTime();
		
		log.info("Successfully added the connected connection[{}] to this connection pool, errasped {} micoseconds",
				connectedAsynConnection.hashCode(), 
				TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));*/
	}
}
