package kr.pe.codda.impl.inner_message;

import kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq;

/**
 * 암호문으로 전달되는 {@link MemberLoginReq} 입력 메시지의 내용을 복호화한 클래스
 * 
 * 
 * @author Won Jonghoon
 *
 */
public class MemberLoginDecryptionReq {
	
	private String memberID;
	private byte[] passwordBytes;
	private String ip;	
	
	public String getMemberID() {
		return memberID;
	}
	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}
	public byte[] getPassword() {
		return passwordBytes;
	}
	public void setPassword(byte[] passwordBytes) {
		this.passwordBytes = passwordBytes;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
	
}
