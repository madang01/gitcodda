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

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

/**
 * 연결 폴 도우미, 소실된 연결 발생시 신규 연결을 폴에 등록하도록 도와주는 역활을 담당한다.
 * 
 * @author Won Jonghoon
 *
 */
public final class ConnectionPoolSupporter extends Thread implements ConnectionPoolSupporterIF {

	protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private long wakeupInterval = 0;

	private ConnectionPoolIF connectionPool = null;
	private SynchronousQueue<String> wakeupEventQueue = new SynchronousQueue<String>();

	public ConnectionPoolSupporter(long wakeupInterval) {
		if (wakeupInterval <= 0) {
			String errorMessage = new StringBuilder().append("the parameter wakeupInterval[").append(wakeupInterval)
					.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		this.wakeupInterval = wakeupInterval;
	}

	public void run() {
		log.info("연결 폴 후원자 시작");

		String reasonForWakingUp = null;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				reasonForWakingUp = wakeupEventQueue.poll(wakeupInterval, TimeUnit.MILLISECONDS);
				if (null != reasonForWakingUp) {
					String infoMessage = new StringBuilder().append("연결 폴 후원자 작업을 일찍 실행하는 사유[")
							.append(reasonForWakingUp).append("] 발생").toString();

					log.info(infoMessage);
				}

				// log.debug("start the work adding the all missing connection");
				log.info("reasonForWakingUp=" + reasonForWakingUp);

				try {
					connectionPool.fillAllConnection();
				} catch (InterruptedException e) {
					throw e;
				} catch (Exception e) {
					log.log(Level.WARNING, "연결 폴 후원자에서 통제된 에러 발생하여 루프 계속", e);
					continue;
				}

				log.fine("end the work adding the all missing connection");
			}
			log.warning("연결 폴 후원자::루프 종료");
		} catch (InterruptedException e) {
			log.warning("연결 폴 후원자::인터럽트에 의한 종료");
		} catch (Exception e) {
			log.log(Level.WARNING, "연결 폴 후원자::에러에 의한 종료", e);
		}
	}

	public void registerPool(ConnectionPoolIF connectionPool) {
		if (null == connectionPool) {
			throw new IllegalArgumentException("the parameter connectionPool is null");
		}
		this.connectionPool = connectionPool;
	}

	public void notice(String reasonForWakingUp) {
		wakeupEventQueue.offer(reasonForWakingUp);
	}
}
