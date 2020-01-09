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
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;

/**
 * 동기 비공유 연결 폴
 * @author Won Jonghoon
 *
 */
public class SyncNoShareConnectionPool implements ConnectionPoolIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private final Object monitor = new Object();
	
	//private ProjectPartConfiguration projectPartConfiguration = null;	
	private final String serverHost;
	private final int serverPort;
	private final long socketTimeout;
	private final StreamCharsetFamily streamCharsetFamily;
	private final int clientConnectionCount; 
	private final int clientDataPacketBufferMaxCntPerMessage;
	
	
	private MessageProtocolIF messageProtocol = null; 
	private WrapBufferPoolIF wrapBufferPool = null;	
	
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	
	private ArrayDeque<SyncNoShareConnection> connectionQueue = null;
	private int numberOfConnection = 0;
	
	/**
	 * 생성자
	 * @param serverHost 서버 호스트 주소
	 * @param serverPort 서버 포트 번호
	 * @param socketTimeout 소켓 타임 아웃 시간
	 * @param streamCharsetFamily 문자셋, 문자셋 디코더 그리고 문자셋 인코더 묶음
	 * @param clientDataPacketBufferMaxCntPerMessage 메시지 1개당 랩 버퍼 최대 갯수
	 * @param clientConnectionCount 연결 갯수
	 * @param messageProtocol 메시지 프로토콜
	 * @param wrapBufferPool 랩 버퍼 폴
	 * @param connectionPoolSupporter 연결 폴 도우미
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에서 랩 버퍼가 없을때 던지는 예외
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 * @throws ConnectionPoolException 연결 폴 조작중 에러 발생시 던지는 예외
	 */
	public SyncNoShareConnectionPool(String serverHost,
			int serverPort,
			long socketTimeout,
			StreamCharsetFamily streamCharsetFamily,
			int clientDataPacketBufferMaxCntPerMessage,
			int clientConnectionCount,
			MessageProtocolIF messageProtocol, 
			WrapBufferPoolIF wrapBufferPool,
			ConnectionPoolSupporterIF connectionPoolSupporter) throws NoMoreWrapBufferException, IOException, ConnectionPoolException {
		if (null == serverHost) {
			throw new IllegalArgumentException("the parameter serverHost is null");
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
				
		if (null == wrapBufferPool) {
			throw new IllegalArgumentException("the parameter wrapBufferPool is null");
		}
		
		if (null == connectionPoolSupporter) {
			throw new IllegalArgumentException("the parameter connectionPoolSupporter is null");
		}
				
		
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;
		this.streamCharsetFamily = streamCharsetFamily;
		this.clientDataPacketBufferMaxCntPerMessage = clientDataPacketBufferMaxCntPerMessage;
		this.clientConnectionCount = clientConnectionCount;		
		
		this.messageProtocol = messageProtocol;
		this.wrapBufferPool = wrapBufferPool;
		this.connectionPoolSupporter = connectionPoolSupporter;
				
		
		connectionQueue = new ArrayDeque<SyncNoShareConnection>(clientConnectionCount);
		
		try {			
			fillAllConnection();
		} catch(NoMoreWrapBufferException e) {
			log.warning("stops adding SyncNoShareConnection because a data packet buffer error has occurred");
			
		} catch(IOException e) {
			log.log(Level.WARNING, "stops adding SyncNoShareConnection because an I/O error has occurred", e);
		
			log.info("removes all of the SyncNoShareConnection from the queue because an I/O error has occurred");
			while (! connectionQueue.isEmpty()) {
				SyncNoShareConnection syncNoShareConnection = connectionQueue.removeFirst();
				syncNoShareConnection.close();
				
				
				String infoMessage = new StringBuilder()
						.append("removes the SyncNoShareConnection[")
						.append(syncNoShareConnection.hashCode())
						.append("] from the queue because an I/O error has occurred").toString();
				
				log.info(infoMessage);
			}
			numberOfConnection = 0;
		} catch(Exception e) {
			log.log(Level.WARNING, "stops adding SyncNoShareConnection because an unknown error has occurred", e);
			
			log.info("removes all of the SyncNoShareConnection from the queue because an I/O error has occurred");
			while (! connectionQueue.isEmpty()) {
				SyncNoShareConnection syncNoShareConnection = connectionQueue.removeFirst();
				syncNoShareConnection.close();
				
				String infoMessage = new StringBuilder()
						.append("removes the SyncNoShareConnection[")
						.append(syncNoShareConnection.hashCode())
						.append("] from the queue because an I/O error has occurred").toString();
				
				log.info(infoMessage);
			}
			numberOfConnection = 0;
		}
		
		connectionPoolSupporter.registerPool(this);
	}

	@Override
	public ConnectionIF getConnection() throws InterruptedException, ConnectionPoolTimeoutException, ConnectionPoolException {
		SyncNoShareConnection syncNoShareConnection = null;
		boolean loop = false;
		
		long currentSocketTimeOut = socketTimeout;
		long startTime = System.currentTimeMillis();

		synchronized (monitor) {
			do {
				if (0 == numberOfConnection) {
					connectionPoolSupporter.notice("no more connection");
					throw new ConnectionPoolException("check server is alive or something is bad");
				}
				
				if (connectionQueue.isEmpty()) {					
					monitor.wait(currentSocketTimeOut);					
					
					if (connectionQueue.isEmpty()) {						
						throw new ConnectionPoolTimeoutException("1.synchronized no-share connection pool timeout");
					}
				}	
				
				syncNoShareConnection = connectionQueue.pollFirst();				

				if (syncNoShareConnection.isConnected()) {
					syncNoShareConnection.queueOut();
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
							.append(syncNoShareConnection.hashCode()).append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;
					
					String warnMessage = new StringBuilder()
							.append(reasonForLoss)
							.append(", numberOfConnection=")
							.append(numberOfConnection).toString();
					

					log.warning(warnMessage);

					connectionPoolSupporter.notice(reasonForLoss);
					
					currentSocketTimeOut -= (System.currentTimeMillis() - startTime);
					if (currentSocketTimeOut <= 0) {
						throw new ConnectionPoolTimeoutException("2.synchronized no-share connection pool timeout");
					}
				}

			} while (loop);
		}

		return syncNoShareConnection;
	}

	@Override
	public void release(ConnectionIF conn) throws ConnectionPoolException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.log(Level.WARNING, errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof SyncNoShareConnection)) {
			String errorMessage = "the parameter conn is not instace of SyncNoShareConnection class";
			log.log(Level.WARNING, errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		SyncNoShareConnection syncNoShareConnection = (SyncNoShareConnection) conn;

		synchronized (monitor) {
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (syncNoShareConnection.isInQueue()) {
				String errorMessage = new StringBuilder()
						.append("the parameter conn[")
						.append(conn.hashCode())
						.append("] allready was released").toString();

				log.log(Level.WARNING, errorMessage, new Throwable());
				throw new ConnectionPoolException(errorMessage);
			}

			/**
			 * 큐에 넣어진 상태로 변경
			 */
			syncNoShareConnection.queueIn();

			if (! syncNoShareConnection.isConnected()) {
				/**
				 * <pre>
				 * 반환된 연결이 닫힌 경우 폐기한다. 단  이러한 작업은 큐에 넣어진 상태에서 이루어 져야 한다.
				 * 왜냐하면 연결은 참조 포인트가 없어 gc 될때 큐에 넣어진 상태가 아니면 로그를 남기는데
				 * 정상적인 반환이므로 gc 될때 로그를 남기지 말아야 하기때문이다.  
				 * </pre>   
				 */
				numberOfConnection--;

				String reasonForLoss = new StringBuilder("반환된 연결[")
						.append(syncNoShareConnection.hashCode())
						.append("]이 닫혀있어 폐기").toString();

				String warnMessage = new StringBuilder()
						.append(reasonForLoss)
						.append(", numberOfConnection=")
						.append(numberOfConnection).toString();
				

				log.warning(warnMessage);

				connectionPoolSupporter.notice(reasonForLoss);
				return;
			}

			connectionQueue.addLast(syncNoShareConnection);
			monitor.notify();
		}
		
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
	public void fillAllConnection() throws NoMoreWrapBufferException, IOException, InterruptedException {
		synchronized (monitor) {
			while (numberOfConnection  < clientConnectionCount) {
				
				SyncNoShareConnection syncNoShareConnection = new SyncNoShareConnection(serverHost,
							serverPort,
							socketTimeout,
							streamCharsetFamily, 
							clientDataPacketBufferMaxCntPerMessage, 
							messageProtocol, wrapBufferPool);
						
				
				String infoMesage = new StringBuilder()
						.append("the SyncNoShareConnection[")
						.append(syncNoShareConnection.hashCode())
						.append("] has been connected").toString();
				
				log.info(infoMesage);
				
				
				connectionQueue.addLast(syncNoShareConnection);
				numberOfConnection++;
			}
		}
		
	}
}
