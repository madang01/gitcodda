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

package kr.pe.codda.client;

import java.io.IOException;

import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;

/**
 * 프로젝트 연결 폴 인터페이스
 *  
 * @author Won Jonghoon
 *
 */
public interface AnyProjectConnectionPoolIF {
	
	/**
	 * 입력 메시지를 보낸 후 응답을 기다려 출력 메시지를 받아 반환한다.
	 * 
	 * @param messageCodecManger 메시지 코덱 관리자
	 * @param inputMessage 입력 메시지
	 * @return 입력 메시지를 보낸 후 응답을 기다려 얻은 출력 메시지
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws ConnectionPoolException 연결 폴 관련 처리중 에러 발생시 던지는 예외
	 * @throws IOException 입출력 에러 발생시 던진는 예외
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩 버퍼가 없을 경우 던지는 예외
	 * @throws DynamicClassCallException 동적 클래스 관련 에러 발생시 던지는 예외
	 * @throws BodyFormatException 바디 구성할대 에러 발생시 던지는 예외
	 * @throws ServerTaskException 서버 비지니스 로직 처리중 에러 발생시 던지는 예외
	 */
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, ConnectionPoolException, 
			IOException, NoMoreWrapBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException;
	
	/**
	 * 입력 메시지를 보낸다, WARNING! 동기 연결은 이 메소드를 지원하지 않는다, 참고) 연결이 소유한 '송신 스트림' 에 추가될때까지 대기 시간이 있을 수 있다. 
	 * 
	 * @param messageCodecManger 메시지 코덱 관리자
	 * @param inputMessage 입력 메시지
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws NotSupportedException 연결 종류가 동기일 경우 던지는 예외, 동기 연결은 이 메소드를 지원하지 않는다.
	 * @throws ConnectionPoolException 연결 폴 조작시 에러가 발생할 경우 던지는 예외  
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외
	 * @throws DynamicClassCallException 동적 클래스 처리중 에러 발생디 던지는 예외
	 * @throws BodyFormatException 바디 구성에 문제가 있을 경우 던지는 예외
	 */
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, NotSupportedException, ConnectionPoolException, 
			IOException, NoMoreWrapBufferException, DynamicClassCallException, BodyFormatException;

	/**
	 * @param serverHost 서버 호스트 주소
	 * @param serverPort 서버 포트 번호
	 * @return 쓰레드 세이프한 비동기 단일 연결
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외
	 * @throws NotSupportedException 연결 종류가 동기일 경우 던지는 예외, 동기 연결은 이 메소드를 지원하지 않는다.
	 */
	public ConnectionIF createAsynShareSingleConnection(String serverHost, int serverPort)
			throws InterruptedException, IOException, NoMoreWrapBufferException, NotSupportedException;
	
	/**
	 * @param serverHost 서버 호스트 주소
	 * @param serverPort 서버 포트 번호
	 * @return 쓰레드 세이프한 동기 단일 연결
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외
	 */
	public ConnectionIF createSyncShareSingleConnection(String serverHost, int serverPort)
			throws InterruptedException, IOException, NoMoreWrapBufferException;
	
	
	/**
	 * @return 폴 상태 문자열
	 */
	public String getPoolState();
}
