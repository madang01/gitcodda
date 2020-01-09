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

/**
 * 연결 폴 도우미 인터페이스, 소실된 연결 발생시 신규 연결을 폴에 등록하도록 도와주는 역활을 담당한다. 
 * 
 * @author Won Jonghoon
 *
 */
public interface ConnectionPoolSupporterIF {
	/**
	 * 연결이 소실 되었을 경우 호출되는 메소드
	 * @param reasonForLoss 깨우는 사유
	 */
	public void notice(String reasonForLoss);
	/**
	 * 연결 폴 도우미에 연결 폴 등록
	 * @param connectionPool 연결 폴
	 */
	public void registerPool(ConnectionPoolIF connectionPool);
}
