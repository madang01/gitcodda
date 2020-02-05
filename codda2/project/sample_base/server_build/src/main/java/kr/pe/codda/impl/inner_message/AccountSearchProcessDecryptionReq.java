package kr.pe.codda.impl.inner_message;

import kr.pe.codda.impl.message.AccountSearchProcessReq.AccountSearchProcessReq;
import kr.pe.codda.server.lib.AccountSearchType;

/**
 * 암호문으로 전달되는 {@link AccountSearchProcessReq} 입력 메시지의 내용을 복호화한 클래스 
 * 
 * @author Won Jonghoon
 *
 */
public class AccountSearchProcessDecryptionReq {
	private AccountSearchType accountSearchType;
	private String email;
	private String secretAuthenticationValue;
	private byte[] newPasswordBytes;
	private String ip;
	
	
	public AccountSearchType getAccountSearchType() {
		return accountSearchType;
	}
	public void setAccountSearchType(AccountSearchType accountSearchType) {
		this.accountSearchType = accountSearchType;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSecretAuthenticationValue() {
		return secretAuthenticationValue;
	}
	public void setSecretAuthenticationValue(String secretAuthenticationValue) {
		this.secretAuthenticationValue = secretAuthenticationValue;
	}
	public byte[] getNewPasswordBytes() {
		return newPasswordBytes;
	}
	public void setNewPasswordBytes(byte[] newPasswordBytes) {
		this.newPasswordBytes = newPasswordBytes;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
}
