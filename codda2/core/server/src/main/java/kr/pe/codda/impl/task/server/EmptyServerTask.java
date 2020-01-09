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

package kr.pe.codda.impl.task.server;


import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 클래스 풀 이름이 동적 클래스이지만 시스템 클래스 로더로 지정되어 내장된 'Empty' 메시지 서버 타스트 
 * @author Won Jonghoon
 *
 */
public class EmptyServerTask extends AbstractServerTask {

	/**
	 * 생성자
	 * @throws DynamicClassCallException 동적 클래스 작업중 에러 발생시 던지는 예외
	 */
	public EmptyServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		
		// FIXME!
		// log.info("call EmptyServerTask");
		
		toLetterCarrier.addBypassOutputMessage(inputMessage);
	}

}
