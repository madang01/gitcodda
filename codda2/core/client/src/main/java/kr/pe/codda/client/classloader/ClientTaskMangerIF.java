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

package kr.pe.codda.client.classloader;

import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.exception.DynamicClassCallException;

/**
 * 클라이언트 타스크 관리자 인터페이스
 * 
 * @author Won Jonghoon
 *
 */
public interface ClientTaskMangerIF {
	
	/**
	 * 파라미터 'messageID'(=메시지 식별자) 와 1:1 대응하는 유효한 클라이언트 타스크를 반환한다.
	 * 유효한 클라이언트 타스크란 클라이언트 타스크 클래스 파일을 적재한 시간과 클라이언트 타스크 클래스 파일이 변경된 시간이 일치되는 클라이언트 타스크를 말한다.
	 * 
	 * @param messageID 메시지 식별자
	 * @return 파라미터 'messageID'(=메시지 식별자) 와 1:1 대응하는 유효한 클라이언트 타스트
	 * @throws DynamicClassCallException 동적 클래스 관련 처리중 에러 발생시 던지는 예외
	 */
	public AbstractClientTask getValidClientTask(String messageID) throws DynamicClassCallException;
}