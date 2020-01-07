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
package kr.pe.codda.common.sessionkey;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.CommonStaticUtil;


/**
 * 클라이언트 세션키
 * @author Won Jonghoon
 *
 */
public class ClientSessionKey implements ClientSessionKeyIF {
	private ClientRSAIF clientRSA = null;
	
	private byte[] symmetricKeyBytes;
	private byte[] ivBytes;
	
	private final ClientSymmetricKeyIF clientSymmetricKey;
	
	private byte[] sessionKeyBytes;
	
	/**
	 * <pre>
	 * 클라이언트 세션키.
	 *  
	 * 참고) 세션키는 대칭키를 RSA-공개키로 암호화 하여 얻은 암호문이다. 
	 *      웹브라우저에서 was 를 세션키를 보낼때  현재 사용중인 RSA 관련 자바 스크립트 라이브러리 'jsbn' 은  
	 *      RAS-공개키로 대칭키를 암호문으로 바꿀때 평문인 대칭키를 반듯이 문자열로만 받아야만 하는 제약이 있다.
	 *      하여  파라미터 'whetherToApplyBase64ToSymmetricKeyForSssionKey'(=세션키용 대칭키에 base64 적용 여부) 를 두어 이룰 구별하도록 한다.
	 * 
	 *      만약 파라미터 'whetherToApplyBase64ToSymmetricKeyForSssionKey'(=세션키용 대칭키에 base64 적용 여부) 가 참(=true)  이면
	 *      세션키는 대칭키 값을 base64 문자열로 만든 값을 RSA-공개키로 암호화 하여 얻은 값이 된다. 
	 *      하여 이때의 세션키 값은 '대칭키를 RSA-공개키로 암호화 하여 얻은 암호문' 이라는 세션키 정의에 맞지 않는다. 
	 * 
	 *      파라미터 'whetherToApplyBase64ToSymmetricKeyForSssionKey'(=세션키용 대칭키에 base64 적용 여부) 가 거짓(=false)  이면
	 *      세션키는 대칭키 값을 공개키로 암호화 하여 얻은 값이 된다. 
	 *      하여 이때의 세션키 값은 '대칭키를 RSA-공개키로 암호화 하여 얻은 암호문' 이라는 세션키 정의에 맞는다.
	 * 
	 * 역자 주) base64 문자열을 대칭키 값 그 자체로 이용할 경우 세션키 만들때 번거로운 변환 과정을 생략할 수 있지만 
	 *      대칭키 길이는 설정 파일에서 지정한 값들중 하나이어야 하는 제약 때문에 
	 *      base64 문자열을 대칭키 값 그 자체로 이용하는 방법은 대칭키 길이에 껴 맞추어야 하는데
	 *      가독성 차원에서 단점이 많아 선택하지 않았음.  
	 * </pre>
	 * 
	 * @param clientRSA 클라이언트 RSA
	 * @param symmetricKeyAlgorithm 대칭키 알고리즘
	 * @param symmetricKeySize 대칭키 크기
	 * @param symmetricIVSize 대칭키 iv 크기
	 * @param whetherToApplyBase64ToSymmetricKeyForSssionKey 세션키용 대칭키에 base64 적용 여부
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public ClientSessionKey(ClientRSAIF clientRSA, String symmetricKeyAlgorithm, int symmetricKeySize, int symmetricIVSize, boolean whetherToApplyBase64ToSymmetricKeyForSssionKey) throws SymmetricException {
		if (null == clientRSA) {
			throw new IllegalArgumentException("the parameter clientRSA is null");
		}
		
		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}
		
		if (symmetricKeySize <= 0) {
			throw new IllegalArgumentException("the parameter symmetricKeySize is less than or equal to zero");
		}
		
		if (symmetricIVSize <= 0) {
			throw new IllegalArgumentException("the parameter symmetricIVSize is less than or equal to zero");
		}
		
		this.clientRSA = clientRSA;
				
		symmetricKeyBytes = new byte[symmetricKeySize];
		ivBytes = new byte[symmetricIVSize];
		
		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
		    /** dead code */
			String errorMesssage = "fail to create a instance of SecureRandom class";
			throw new SymmetricException(errorMesssage);
		}
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		if (whetherToApplyBase64ToSymmetricKeyForSssionKey) {
			sessionKeyBytes = clientRSA.encrypt(CommonStaticUtil.Base64Encoder.encode(symmetricKeyBytes));
		} else {
			sessionKeyBytes = clientRSA.encrypt(symmetricKeyBytes);
		}
		
		clientSymmetricKey = new ClientSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
	}
	
	@Override
	public ClientSymmetricKeyIF getClientSymmetricKey() {
		return clientSymmetricKey;
	}

	@Override
	public final byte[] getDupSessionKeyBytes() {
		return Arrays.copyOf(sessionKeyBytes, sessionKeyBytes.length);
	}
	
	@Override
	public byte[] getDupPublicKeyBytes() {
		return clientRSA.getDupPublicKeyBytes();
	}
	
	
	@Override
	public byte[] getDupIVBytes() {
		return Arrays.copyOf(ivBytes, ivBytes.length);
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientSessionKey [sessionKeyBytes=");
		builder.append(Arrays.toString(sessionKeyBytes));
		builder.append(", publicKeyBytes=");
		builder.append(Arrays.toString(ivBytes));
		builder.append(", ivBytes=");
		builder.append(Arrays.toString(ivBytes));
		builder.append("]");
		return builder.toString();
	}
}
