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
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.message.AbstractMessage;

/**
 * 연결 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ConnectionIF {
	/**
	 * 입력 메시지를 보낸후 응답을 기다려 출력 메시지를 반환한다, 지정한 시간이 초과 될 경우 타임 아웃 발생한다. 
	 *  
	 * @param messageCodecManger 메시지 코덱 관리자
	 * @param inputMessage 입력 메시지
	 * @return 입력 메시지를 보낸 후 수신한 출력 메시지
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws IOException 소켓 타임 아웃 포함 입출력 에러 발생시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외, 단 서버로 부터 올 수 있다.
	 * @throws DynamicClassCallException 동적 클래스 처리중 에러 발생디 던지는 예외, 단 서버로 부터 올 수 있다.
	 * @throws BodyFormatException 바디 구성에 문제가 있을 경우 던지는 예외, 단 서버로 부터 올 수 있다.
	 * @throws ServerTaskException 서버 비지니스 로직 처리중 에러 발생시 던지는 예외
	 * @throws ServerTaskPermissionException 서버 비지니스 로직에 대한 접근 권한을 갖고 있지 못할때 던지는 예외
	 */
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreWrapBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException;
	
	/**
	 * 입력 메시지를 보낸다, WARNING! 동기 연결은 이 메소드를 지원하지 않는다, 참고) 연결이 소유한 '송신 스트림' 에 추가될때까지 대기 시간이 있을 수 있다. 
	 * 
	 * @param messageCodecManger 메시지 코덱 관리자
	 * @param inputMessage 입력 메시지
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws NotSupportedException 연결 종류가 동기일 경우 던지는 예외, 동기 연결은 이 메소드를 지원하지 않는다.
	 * @throws IOException 소켓 타임 아웃 포함 입출력 에러 발생시 던지는 예외
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외
	 * @throws DynamicClassCallException 동적 클래스 처리중 에러 발생디 던지는 예외
	 * @throws BodyFormatException 바디 구성에 문제가 있을 경우 던지는 예외
	 */
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, NotSupportedException,
			IOException, NoMoreWrapBufferException, DynamicClassCallException, BodyFormatException;
	
	/**
	 * 연결을 닫는다.
	 */
	public void close();	
	
	/**
	 * @return 해쉬코드
	 */
	public int hashCode();
	
	/**
	 * @return 연결 여부
	 */
	public boolean isConnected();
}
