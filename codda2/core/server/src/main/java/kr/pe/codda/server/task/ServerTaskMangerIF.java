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
package kr.pe.codda.server.task;

import kr.pe.codda.common.exception.DynamicClassCallException;

/**
 * 서버 운영에 필요한 동적 클래스 객체들에 대한 캐쉬 관리자 인터페이스
 * @author "Won Jonghoon"
 *
 */
public interface ServerTaskMangerIF {
	/**
	 * 파라미터 'messageID'(=메시지 식별자) 와 1:1 대응하는 유효한 서버 타스크를 반환한다.
	 * 유효한 서버 타스크란 서버 타스크 클래스 파일을 적재한 시간과 서버 타스크 클래스 파일이 변경된 시간이 일치되는 서버 타스크를 말한다.
	 * 
	 * @param messageID 메시지 식별자
	 * @return 지정된 메시지 식별자와 1:1 대응하는 유효한 서버 타스크
	 * @throws DynamicClassCallException 동적 클래스를 다룰때 에러 발생시 던지는 예외
	 */
	public AbstractServerTask getValidServerTask(String messageID) throws DynamicClassCallException;
}
