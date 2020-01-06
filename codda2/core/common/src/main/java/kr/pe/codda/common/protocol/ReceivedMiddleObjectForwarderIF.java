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

package kr.pe.codda.common.protocol;

/**
 * 수신한 중간 객체 전달자 인터페이스.
 * 
 * @author Won Jonghoon
 *
 */
public interface ReceivedMiddleObjectForwarderIF {
	/**
	 * 수신한 메시지로 부터 변화된 중간 객체를 전달한다.
	 *
	 * @param mailboxID 수신한 메시지의 메일 박스 식별자
	 * @param mailID 수신한 메시지의 메일 식별자
	 * @param messageID 수신한 메시지의 메시지 식별자
	 * @param receviedMiddleObject 수신한 메시지로 부터 변화된 중간 객체
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	public void putReceivedMiddleObject(int mailboxID, int mailID, String messageID, Object receviedMiddleObject) throws InterruptedException;
}
