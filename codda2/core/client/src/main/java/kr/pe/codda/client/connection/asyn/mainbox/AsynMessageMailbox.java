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

package kr.pe.codda.client.connection.asyn.mainbox;

import java.util.concurrent.atomic.AtomicInteger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

/**
 * 메일 박스 추상화 클래스
 * @author Won Jonghoon
 *
 */
public abstract class AsynMessageMailbox {
	private static AtomicInteger mailID = new AtomicInteger(Integer.MIN_VALUE);
	// private transient static int mailID = Integer.MIN_VALUE;

	/**
	 * @return 클라이언트 비동기 메일 박스 식별자
	 */
	public static int getMailboxID() {
		return CommonStaticFinalVars.COUNT_ASYN_MAILBOX_ID;
	}

	/*
	public synchronized static int getNextMailID() {
		if (Integer.MAX_VALUE == mailID) {
			mailID = Integer.MIN_VALUE;
		} else {
			mailID++;
		}
		return mailID;
	}
	*/	
	/**
	 * @return 다음 메일 식별자
	 */
	public static int getNextMailID() {
		return mailID.incrementAndGet();
	}
}
