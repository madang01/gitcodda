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

/**
 * 서버 입출력 이벤트 제어기 핸들러 인터페이스 
 * @author Won Jonghoon
 *
 */
public interface ServerIOEventHandlerIF {
	/**
	 * 연결 객체의 OP_READ 이벤트 발생시 호출되는 메소드, 예외를 던지지 않도록 구현되었지만 예외를 멋어난 예외는 발생할 수 있다
	 * @param selectedKey selector 에서 OP_READ 이벤트를 발생한 키
	 * @throws Exception 예상 못한 예외
	 */
	public void onRead(SelectionKey selectedKey) throws Exception;
	
	/**
	 * 연결 객체의 OP_WRITE 이벤트 발생시 호출되는 메소드, 예외를 던지지 않도록 구현되었지만 예외를 멋어난 예외는 발생할 수 있다
	 * @param selectedKey selectedKey selector 에서 OP_WRITE 이벤트를 발생한 키
	 * @throws Exception 예상 못한 예외
	 */
	public void onWrite(SelectionKey selectedKey) throws Exception;
	
	
	/**
	 * 자원 해제시킨다.
	 */
	public void close();
	
	/**
	 * @return 해쉬 코드
	 */
	public int hashCode();
}
