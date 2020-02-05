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

import java.sql.Timestamp;

import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.server.lib.MemberRoleType;

/**
 * @author Won Jonghoon
 *
 */
public class MemberRegisterDecryptionReq {
	private MemberRoleType memberRoleType;
	private String userID;
	private String nickname;
	private String email;
	private byte[] passwordBytes;
	private Timestamp registeredDate;
	private String ip;
	
	
	public MemberRoleType getMemberRoleType() {
		return memberRoleType;
	}
	public void setMemberRoleType(MemberRoleType memberRoleType) {
		this.memberRoleType = memberRoleType;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public byte[] getPasswordBytes() {
		return passwordBytes;
	}
	public void setPasswordBytes(byte[] passwordBytes) {
		this.passwordBytes = passwordBytes;
	}
	public Timestamp getRegisteredDate() {
		return registeredDate;
	}
	public void setRegisteredDate(Timestamp registeredDate) {
		this.registeredDate = registeredDate;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getMessageID() {
		return MemberRegisterReq.class.getSimpleName();
	}
	
}
