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

package kr.pe.codda.common.type;

/** 
 * <pre>
 * 메시지 전송 방향 종류 열거형 타입
 * 
 * (1) FROM_NONE_TO_NONE : 메시지는 서버에서 클라이언트로 혹은 클라이언트에서 서버로 양쪽 모두에서 전송되지 않는다.
 * (2) FROM_SERVER_TO_CLINET : 메시지는 서버에서 클라이언트로만 전송된다.
 * (3) FROM_CLIENT_TO_SERVER : 메시지는 클라이언트에서 서버로만 전송된다.
 * (4) FROM_ALL_TO_ALL : 메시지는 서버에서 클라이언트로도 혹은 클라이언트에서 서버로 양쪽 모두에서 전송된다.
 * </pre> 
 */
public enum MessageTransferDirectionType {	
	FROM_NONE_TO_NONE,
	FROM_SERVER_TO_CLINET, 
	FROM_CLIENT_TO_SERVER,
	FROM_ALL_TO_ALL;
}
