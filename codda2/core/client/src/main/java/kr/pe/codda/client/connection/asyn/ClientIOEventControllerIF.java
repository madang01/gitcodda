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
package kr.pe.codda.client.connection.asyn;

import java.nio.channels.SelectionKey;

/**
 * 클라이언트 입출력 이벤트 제어기 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ClientIOEventControllerIF {
	
	/**
	 * 연결을 하고 싶은 연결이 아직 확립되지 않은 비동기 연결을  {@link SelectionKey#OP_CONNECT} 를 관심 사항으로 selctor 에 등록한다.
	 * 
	 * @param asynInterestedConnection 연결을 하고 싶은 연결이 아직 확립되지 않은 비동기 연결
	 */
	public void addUnregisteredAsynConnection(ClientIOEventHandlerIF asynInterestedConnection);
	
	/**
	 * 클라이언트 입출력 이벤트 제어기의 selctor 를 깨운다.
	 */
	public void wakeup();
	
	/**
	 * 파라미터 'selectedKey'(취소를 원하는 세렉션 키) 를 selctor 에서 등록 취소한다. 
	 * 
	 * @param selectedKey 취소를 원하는 세렉션 키
	 */
	public void cancel(SelectionKey selectedKey);
}
