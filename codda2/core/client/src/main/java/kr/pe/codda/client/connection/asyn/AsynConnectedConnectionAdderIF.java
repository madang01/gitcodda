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

/**
 * 연결 확립된 비동기 연결 추가자 인터페이스
 * @author Won Jonghoon
 *
 */
public interface AsynConnectedConnectionAdderIF {
	/**
	 * 메시지 송수신이 가능한 연결 확립된 연결을 파라미티러 받아 등록 시킨다
	 * 
	 * @param connectedAsynConnection 연결 확립된 연결
	 */
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection);
	
	/**
	 * <pre>
	 * '미 등록된 연결'이 연결 확립을 못해 폐기되는 후속 조취로 
	 * 이 '미 등록된 연결'의 연결 확립을 부탁한 주체자의 '미 등록된 연결 갯수'를 하나 줄인다
	 * </pre>
	 * 
	 * @param unregisteredAsynConnection 연결 확립을 못해 폐기된 '미 등록된 연결'
	 */
	public void subtractOneFromNumberOfUnregisteredConnections(AsynConnectionIF unregisteredAsynConnection);
}
