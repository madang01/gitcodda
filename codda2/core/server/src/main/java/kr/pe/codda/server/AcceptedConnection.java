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

package kr.pe.codda.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.ServerOutgoingStreamIF;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMiddleObjectForwarderIF;
import kr.pe.codda.common.type.ExceptionDelivery;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ServerTaskMangerIF;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 서버에 접속하는 클라이언트 자원 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptedConnection implements ServerIOEventHandlerIF, ReceivedMiddleObjectForwarderIF, LoginManagerIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final SelectionKey personalSelectionKey;
	private final SocketChannel acceptedSocketChannel;
	private final String projectName;
	
	// private final long socketTimeout;
	// private final int serverInputMessageQueueCapacity;
	// private final int serverOutputMessageQueueCapacity;
	private final MessageProtocolIF messageProtocol;
	
	private ServerIOEvenetControllerIF serverIOEvenetController = null;
	private ServerTaskMangerIF serverTaskManager = null;

	private final IncomingStream incomingStream;
	private final ServerOutgoingStreamIF outgoingStream;

	private ProjectLoginManagerIF projectLoginManager = null;
	private String loginID = null;

	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다 */
	private java.util.Date finalReadTime = null;

	// private final Object monitorOfServerMailID = new Object();
	/** 클라이언트에 할당되는 서버 편지 식별자 */
	private int serverMailID = Integer.MIN_VALUE;

	/**
	 * 생성자
	 * @param personalSelectionKey 접속을 허락받은 셀렉션 키
	 * @param acceptedSocketChannel 접속을 허락받은 소켓 채널
	 * @param projectName 프로젝트 이름
	 * @param socketTimeout 소켓 타임 아웃 시간
	 * @param streamCharsetFamily 문자셋, 문자셋 디코더 그리고 문자셋 인코더 묶음
	 * @param serverDataPacketBufferMaxCntPerMessage 메시지 1개당 랩 버퍼 최대 갯수
	 * @param serverOutputMessageQueueCapacity 출력 메시지 큐 크기
	 * @param projectLoginManager 로그린 관리자
	 * @param messageProtocol 메시지 프로토콜
	 * @param wrapBufferPool 랩 버퍼 폴
	 * @param serverIOEvenetController 서버 입출력 이벤트 제어자
	 * @param serverTaskManager 서버 타스크 관리자
	 */
	public AcceptedConnection(SelectionKey personalSelectionKey, SocketChannel acceptedSocketChannel,
			String projectName, long socketTimeout, StreamCharsetFamily streamCharsetFamily, 
			int serverDataPacketBufferMaxCntPerMessage, 
			int serverOutputMessageQueueCapacity,
			ProjectLoginManagerIF projectLoginManager,
			MessageProtocolIF messageProtocol, WrapBufferPoolIF wrapBufferPool,
			ServerIOEvenetControllerIF serverIOEvenetController, ServerTaskMangerIF serverTaskManager) {

		if (null == personalSelectionKey) {
			throw new IllegalArgumentException("the parameter personalSelectionKey is null");
		}

		if (null == acceptedSocketChannel) {
			throw new IllegalArgumentException("the parameter acceptedSocketChannel is null");
		}

		if (socketTimeout < 0) {
			throw new IllegalArgumentException("the parameter socketTimeOut is less than zero");
		}

		if (serverOutputMessageQueueCapacity <= 0) {
			throw new IllegalArgumentException(
					"the parameter serverOutputMessageQueueCapacity is less than or equal to zero");
		}

		if (null == projectLoginManager) {
			throw new IllegalArgumentException("the parameter projectLoginManager is null");
		}

		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}

		if (null == wrapBufferPool) {
			throw new IllegalArgumentException("the parameter wrapBufferPool is null");
		}

		if (null == serverIOEvenetController) {
			throw new IllegalArgumentException("the parameter serverIOEvenetController is null");
		}

		this.personalSelectionKey = personalSelectionKey;
		this.acceptedSocketChannel = acceptedSocketChannel;
		this.projectName = projectName;
		// this.socketTimeout = socketTimeout;
		// this.serverInputMessageQueueCapacity = serverInputMessageQueueCapacity;
		// this.serverOutputMessageQueueCapacity = serverOutputMessageQueueCapacity;
		this.projectLoginManager = projectLoginManager;
		this.messageProtocol = messageProtocol;
		
		this.serverIOEvenetController = serverIOEvenetController;
		this.serverTaskManager = serverTaskManager;

		finalReadTime = new java.util.Date();

		incomingStream = new IncomingStream(streamCharsetFamily, serverDataPacketBufferMaxCntPerMessage, wrapBufferPool);
		outgoingStream = new ServerOutgoingStream(personalSelectionKey, serverOutputMessageQueueCapacity);
	}

	/*
	 * public SocketChannel getOwnerSC() { return acceptedSocketChannel; }
	 */

	/**
	 * 마지막으로 읽은 시간을 반환한다.
	 * 
	 * @return 마지막으로 읽은 시간
	 */
	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}

	/**
	 * 마지막으로 읽은 시간을 새롭게 설정한다.
	 */
	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}
	
	/**
	 * @return 1 증가한 후의 서버용 메일 식별자.
	 */
	public int getServerMailID() {
		// synchronized (monitorOfServerMailID) {
		if (Integer.MAX_VALUE == serverMailID) {
			serverMailID = Integer.MIN_VALUE;
		} else {
			serverMailID++;
		}
		return serverMailID;
		// }
	}

	@Override
	public void onRead(SelectionKey personalSelectionKey) throws Exception {
		// FIXME!
		// log.info("call onRead");
		int numberOfReadBytes;		
		do {
			numberOfReadBytes = incomingStream.read(acceptedSocketChannel);			
		} while (numberOfReadBytes > 0);
		
		setFinalReadTime();
		messageProtocol.S2O(incomingStream, this);

		if (numberOfReadBytes == -1) {
			String errorMessage = new StringBuilder("this socket channel[").append(acceptedSocketChannel.hashCode())
					.append("] has reached end-of-stream").toString();

			log.warning(errorMessage);
			close();
			return;
		}
		
		/*
		if (isr.size() > serverOutputMessageQueueCapacity / 2) {
			turnOffSocketReadMode();
		}
		*/

	}

	@Override
	public void onWrite(SelectionKey personalSelectionKey) throws Exception {
		// FIXME!
		// log.info("call onWrite");
		
		int numberOfWriteBytes;
		
		do {
			numberOfWriteBytes = outgoingStream.write(acceptedSocketChannel);
		} while (numberOfWriteBytes > 0);
		
	}

	/**
	 * 소켓 읽기 이벤트가 꺼져 있는 상태라면 더 이상 전송할 데이터 없는 상태가 되어 소켓 쓰기 이벤트가 꺼져 있거나 혹은 출력 메시지 큐가
	 * 최대 용량 25% 보다 작은 경우 소켓 읽기 이벤트 켜기
	 */
	/*
	private void turnOnSocketReadModeIfValid() {
		int interestOps = personalSelectionKey.interestOps();

		if (((interestOps & SelectionKey.OP_READ) == 0)) {
			if ((interestOps & SelectionKey.OP_WRITE) == 0
					|| outputMessageQueue.size() < serverOutputMessageQueueCapacity / 4) {
				turnOnSocketReadMode();
			}
		}
	}
	*/
	

	public int hashCode() {
		return acceptedSocketChannel.hashCode();
	}
	

	/**
	 * 서버 비지니스 로직에서 출력 메시지가 생겼을때 호출하는 메소드로 출력 메시지를 출력 메시지 큐에 넣고
	 *
	 * @param outputMessageStreamBuffer 출력 메시지 내용이 담긴 스트림 버퍼
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	public void addOutputMessage(StreamBuffer outputMessageStreamBuffer)
			throws InterruptedException {

		// log.info("outputMessageQueue.size={}, inputMessageCount={}",
		// outputMessageQueue.size(), inputMessageCount);]
		
		boolean isSuccess = outgoingStream.offer(outputMessageStreamBuffer);
		if (! isSuccess) {
			String errorMessage = new StringBuilder()
					.append("소켓 채널[")
					.append(acceptedSocketChannel)
					.append("]에 출력 메시지 내용이 담긴 스트림[")
					.append(outputMessageStreamBuffer.toHexStringForRemaingData())
					.append("] 추가하는데 실패하여 폐기합니다").toString();
			
			log.warning(errorMessage);
			
			outputMessageStreamBuffer.releaseAllWrapBuffers();
			return;
		}
	}

	@Override
	public void putReceivedMiddleObject(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {

		AbstractServerTask serverTask = null;
		try {
			serverTask = serverTaskManager.getValidServerTask(messageID);
		} catch (DynamicClassCallException e) {
			log.warning(e.getMessage());

			// ProtocolUtil.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);
			messageProtocol.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);

			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = e.getMessage();
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason, mailboxID, mailID,
					messageID, this, messageProtocol);

			return;
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder().append("unknown error::fail to get a input message[")
					.append(messageID).append("] server task").toString();
			log.log(Level.WARNING, errorMessage, e);

			messageProtocol.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);

			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = "fail to get a input message server task::" + e.getMessage();
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason, mailboxID, mailID,
					messageID, this, messageProtocol);
			return;
		}

		try {
			serverTask.execute(projectName, this, projectLoginManager, mailboxID, mailID, messageID,
					readableMiddleObject, messageProtocol, this);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {
			log.log(Level.WARNING, "unknwon error::fail to execute a input message server task", e);

			ExceptionDelivery.ErrorType errorType = ExceptionDelivery.ErrorType.valueOf(ServerTaskException.class);
			String errorReason = "fail to execute a input message server task::" + e.getMessage();
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason, mailboxID, mailID,
					messageID, this, messageProtocol);
			return;
		}
	}

	@Override
	public boolean isLogin() {
		if (null == loginID) {
			return false;
		}

		boolean isConnected = 
				acceptedSocketChannel.isConnected();

		return isConnected;
	}

	@Override
	public void registerLoginUser(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the parameter loginID is null");
		}
		this.loginID = loginID;
		projectLoginManager.registerloginUser(personalSelectionKey, loginID);
	}

	@Override
	public String getLoginID() {
		return loginID;
	}

	/**
	 * @return 간단하게 줄인 연결 정보가 담긴 문자열
	 */
	public String toSimpleInfomation() {
		StringBuilder builder = new StringBuilder();
		builder.append("personalSelectionKey=");
		builder.append(personalSelectionKey);
		builder.append(", acceptedSocketChannel=");
		builder.append(acceptedSocketChannel);
		return builder.toString();
	}

	public boolean isConnected() {
		return acceptedSocketChannel.isConnected();
	}

	private void releaseResources() {
		incomingStream.releaseAllWrapBuffers();
		outgoingStream.close();
		
		releaseLoginUserResource();
		
		log.info(new StringBuilder()
				.append("this accepted socket channel[hashcode=")
				.append(acceptedSocketChannel.hashCode())
				.append(", selection key=")
				.append(personalSelectionKey.hashCode())
				.append("]'s resources has been released").toString());
	}

	/** 로그 아웃시 할당 받은 자원을 해제한다. */
	private void releaseLoginUserResource() {
		if (null != loginID) {
			projectLoginManager.removeLoginUser(personalSelectionKey);			
		}
	}

	public void close() {
		try {
			acceptedSocketChannel.shutdownOutput();
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to shutdown output of the socket channel[")
					.append(acceptedSocketChannel.hashCode())
					.append("]").toString();
			log.log(Level.WARNING, errorMessage, e);
		}

		try {
			acceptedSocketChannel.close();
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to close the socket channel[")
					.append(acceptedSocketChannel.hashCode())
					.append("]").toString();
			
			log.log(Level.WARNING, errorMessage, e);
		}

		serverIOEvenetController.cancel(personalSelectionKey);

		releaseResources();
	}

}
