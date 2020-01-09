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

package kr.pe.codda.client.connection;

import java.io.IOException;

import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;

/**
 * 연결 폴 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ConnectionPoolIF {
	/**
	 * @return 폴에 있는 연결
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws ConnectionPoolTimeoutException 연결 폴 타임 아웃 발생시 던지는 예외
	 * @throws ConnectionPoolException 연결 폴 처리중 에러 발생시 던지는 예외
	 */
	public ConnectionIF getConnection() throws InterruptedException, ConnectionPoolTimeoutException, ConnectionPoolException;
	
	/**
	 * 폴에 연결 반환
	 * @param conn 연결
	 * @throws ConnectionPoolException 연결 폴 처리중 에러 발생시 던지는 예외
	 */
	public void release(ConnectionIF conn) throws ConnectionPoolException;
	
	
	/**
	 * <pre>
	 * 연결 확립된 연결을 가득 채운다, 단 비동기 폴의 경우에 동기 폴과는 달리 지연처리 기법을 통해서 연결을 채운다.
	 * 
	 * 참고) '비동기 클라이언트 입출력 이벤트 제어자' 에서 연결 이벤트를 받아 연결 확립을 시켜 주기때문에
	 * 연결 확립되지 않았고 연결 확립시 이 폴로 등록 요청할 연결들을 '비동기 클라이언트 입출력 이벤트 제어자'에 연결 이벤트를 받도록 등록해 준다.
	 * 결과적으로 연결이 되면 이 폴에 등록된다.
	 * </pre> 
	 * 
	 * @throws NoMoreWrapBufferException 랩버퍼 폴에 랩버퍼 요구하였는데 없는 경우 던지는 예외
	 * @throws IOException 소켓 타임 아웃 포함 입출력 에러 발생시 던지는 예외
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	public void fillAllConnection() throws NoMoreWrapBufferException, IOException, InterruptedException;
		
	/**
	 * @return 폴 상태 문자열
	 */
	public String getPoolState();
}
