/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.pe.codda.impl.inner_message;

import kr.pe.codda.impl.message.MemberWithdrawReq.MemberWithdrawReq;

/**
 * 
 * 암호문으로 전달되는 {@link MemberWithdrawReq} 입력 메시지의 내용을 복호화한 클래스
 * 
 * 
 * @author Won Jonghoon
 *
 */
public class MemberWithdrawInnerReq {
	private String requestedUserID;
	private String ip;
	private byte[] passwordBytes;
	
	
	public String getRequestedUserID() {
		return requestedUserID;
	}
	public void setRequestedUserID(String requestedUserID) {
		this.requestedUserID = requestedUserID;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public byte[] getPasswordBytes() {
		return passwordBytes;
	}
	public void setPasswordBytes(byte[] passwordBytes) {
		this.passwordBytes = passwordBytes;
	}
	
	
	public String getMessageID() {
		return MemberWithdrawReq.class.getSimpleName();
	}
	
}
