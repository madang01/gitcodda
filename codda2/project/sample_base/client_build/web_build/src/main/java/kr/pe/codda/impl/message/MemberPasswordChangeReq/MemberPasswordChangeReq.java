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

package kr.pe.codda.impl.message.MemberPasswordChangeReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * MemberPasswordChangeReq message
 * @author Won Jonghoon
 *
 */
public class MemberPasswordChangeReq extends AbstractMessage {
	private String requestedUserID;
	private String oldPwdCipherBase64;
	private String newPwdCipherBase64;
	private String sessionKeyBase64;
	private String ivBase64;
	private String ip;

	public String getRequestedUserID() {
		return requestedUserID;
	}

	public void setRequestedUserID(String requestedUserID) {
		this.requestedUserID = requestedUserID;
	}
	public String getOldPwdCipherBase64() {
		return oldPwdCipherBase64;
	}

	public void setOldPwdCipherBase64(String oldPwdCipherBase64) {
		this.oldPwdCipherBase64 = oldPwdCipherBase64;
	}
	public String getNewPwdCipherBase64() {
		return newPwdCipherBase64;
	}

	public void setNewPwdCipherBase64(String newPwdCipherBase64) {
		this.newPwdCipherBase64 = newPwdCipherBase64;
	}
	public String getSessionKeyBase64() {
		return sessionKeyBase64;
	}

	public void setSessionKeyBase64(String sessionKeyBase64) {
		this.sessionKeyBase64 = sessionKeyBase64;
	}
	public String getIvBase64() {
		return ivBase64;
	}

	public void setIvBase64(String ivBase64) {
		this.ivBase64 = ivBase64;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("memberPasswordChangeReq[");
		builder.append("requestedUserID=");
		builder.append(requestedUserID);
		builder.append(", oldPwdCipherBase64=");
		builder.append(oldPwdCipherBase64);
		builder.append(", newPwdCipherBase64=");
		builder.append(newPwdCipherBase64);
		builder.append(", sessionKeyBase64=");
		builder.append(sessionKeyBase64);
		builder.append(", ivBase64=");
		builder.append(ivBase64);
		builder.append(", ip=");
		builder.append(ip);
		builder.append("]");
		return builder.toString();
	}
}