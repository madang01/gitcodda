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

package kr.pe.codda.common.io;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.exception.OutgoingStreamTimeoutException;
import kr.pe.codda.common.exception.RetryException;
import kr.pe.codda.common.exception.TimeoutDelayException;

/**
 * 클라이언트용 출력 스트림 인터페이스
 * 
 * @author Won Jonghoon
 *
 */
public interface ClientOutgoingStreamIF {
	/**
	 * 클라이언트용 출력 스트림에 지정한 시간(단위 ms) 동안 대기하여 파라미터 '메시지 스트림 버퍼' 를 추가한다.
	 * @param messageStreamBuffer 메시지 스트림 버퍼
	 * @param timeout 타임 아웃 시간, 단위 : milliseconds
	 * @throws OutgoingStreamTimeoutException 지정한 '타임 아웃 시간' 동안 입력 메시지 스트림을 서버측에 보낼 스트림 큐에 넣기 위한 락을 획득 하지 못한 경우 던지는 예외
	 * @throws RetryException 서버로 보낼 스트림 큐중 송신중인 메시지 스트림 큐가 가득차서 던지는 예외로 타임아웃 시간 안에 재시도를 목적으로 한다.
	 * @throws TimeoutDelayException 지정한 '타임 아웃 시간' 안에도  입력 메시지 스트림을 서버측에 보낼 스트림 큐에 넣을 수 없을때 던지는 예외로 파라미터러 넘긴 시간 만큼 대기후 타임 아웃 예외를 던진다.
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	public void add(StreamBuffer messageStreamBuffer, long timeout) throws OutgoingStreamTimeoutException, RetryException, TimeoutDelayException, InterruptedException;
	
	/**
	 * 소켓 채널 쓰기를 수행하여 쓴 바이트수를 반환한다, 단 0 혹은 출력 스트림의 종료를 알리는 -1 을 반환할 수 있다.
	 * 
	 * @param writableSocketChannel 출력 스트림의 소유 소켓 채널
	 * @return 소켓 채널 쓰기를 수행하여 쓴 바이트수, 단 0 혹은 출력 스트림의 종료를 알리는 -1 을 반환할 수 있다.
	 * @throws IOException 입출력 에러시 던지는 예외
	 * @throws NoMoreWrapBufferException 클라이언트용 출력 스트림은 성장형 스트림으로 성장을 위한 버퍼 자원 부족시 던지는 예외
	 */
	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreWrapBufferException;
	
		
	/**
	 * 클라이언트용 출력 스트림을 이룬 공유 자원인 버퍼들을 해제한다.
	 */
	public void close();
}
