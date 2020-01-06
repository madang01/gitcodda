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

/**
 * 서버용 출력 스트림 인터페이스
 * 
 * @author Won Jonghoon
 *
 */
public interface ServerOutgoingStreamIF {
	/**
	 * 클라이언트용 출력 스트림에 파라미터 '메시지 스트림 버퍼' 를 추가한다, 단 최대 출력 메시지 수가 하드 코딩되어 정해져 있어 초과할 경우 버려진다.
	 * 
	 * @param messageStreamBuffer 메시지 스트림 버퍼
	 * @return 성공 여부
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	public boolean offer(StreamBuffer messageStreamBuffer) throws InterruptedException;
	
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
	 * 서버용 출력 스트림을 이룬 공유 자원인 버퍼들을 해제한다.
	 */
	public void close();
}
