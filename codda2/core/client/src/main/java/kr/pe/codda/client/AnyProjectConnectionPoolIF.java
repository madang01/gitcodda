/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.client;

import java.io.IOException;

import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.message.AbstractMessage;

/**
 * 클라이언트 프로젝트 개발자 시선의 클라이언트용 서버 접속 API 인터페이스
 * @author Won Jonghoon
 *
 */
public interface AnyProjectConnectionPoolIF {
	
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, ConnectionPoolException, 
			IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException;
	
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, NotSupportedException, ConnectionPoolException, 
			IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException;
		
	public ConnectionIF createAsynThreadSafeConnection(String serverHost, int serverPort)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, NotSupportedException;
	
	public ConnectionIF createSyncThreadSafeConnection(String serverHost, int serverPort)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException;
	
	public String getPoolState();
}
