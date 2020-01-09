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
 * 서버 입출력 이벤트 제어자 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ServerIOEvenetControllerIF {
	/**
	 * 서버 입출력 이벤트 제어자의 selector 에 등록된 셀렉션 키를 등록 취소 시킵니다.
	 * @param selectedKey 서버 입출력 이벤트 제어자의 selector 에 등록된 셀렉션 키 
	 */
	public void cancel(SelectionKey selectedKey);
}
