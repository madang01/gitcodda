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

import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;


/**
 * 쓰레드 세이프하고 연결 확립한 비동기 단일 연결 추가자 
 * @author Won Jonghoon
 *
 */
public class AyncConnectedShareSingleConnectionAdder implements AsynConnectedConnectionAdderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private final Object monitor = new Object();
	private AsynConnectionIF connectedAsynConnection = null;
	private boolean isSocketTimeout=false;	 

	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) {
		synchronized (monitor) {
			if (isSocketTimeout) {
				log.warning("socket timeout occured so drop the connected asyn share connection");
				connectedAsynConnection.close();
				return;
			}
			this.connectedAsynConnection = connectedAsynConnection;
			monitor.notify();
		}
		
		String warnMessage = new StringBuilder()
				.append("add the connected asyn connection[")
				.append(connectedAsynConnection.hashCode())
				.append("]").toString();
		log.warning(warnMessage);
	}

	@Override
	public void subtractOneFromNumberOfUnregisteredConnections(AsynConnectionIF unregisteredAsynConnection) {
		String warnMessage = new StringBuilder()
				.append("remove the unregistered asyn connection[")
				.append(connectedAsynConnection.hashCode())
				.append("]").toString();
		
		log.warning(warnMessage);
	}	
	
	/**
	 * @param socketTimeout 소켓 타임 아웃 시간
	 * @return 쓰레드 세이프하고 연결 확립한 비동기 단일 연결
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 * @throws SocketTimeoutException 소켓 타임 아웃 발생시 던지는 예외
	 */
	public AsynConnectionIF poll(long socketTimeout) throws InterruptedException, SocketTimeoutException {
		synchronized (monitor) {
			if (null == connectedAsynConnection) {
				monitor.wait(socketTimeout);
				
				if (null == connectedAsynConnection) {
					isSocketTimeout = true;
					throw new SocketTimeoutException();
				}
			}
			
			return connectedAsynConnection;
		}
	}

}
