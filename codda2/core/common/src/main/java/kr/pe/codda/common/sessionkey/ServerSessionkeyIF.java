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

import kr.pe.codda.common.exception.SymmetricException;

/**
 * 서버 세션키 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ServerSessionkeyIF {
	/**
	 * 내부적으로 파라미터 'whetherToApplyBase64ToSymmetricKeyForSssionKey' 를 false 로 하여
	 * {@link #createNewInstanceOfServerSymmetricKey(boolean, byte[], byte[]) } 를 호출한다.
	 * 
	 * @param sessionkeyBytes 세션키 값 
	 * @param ivBytes iv 값
	 * @return 파라미터 'sessionkeyBytes'(=세션키 값)과 파라미터 'ivBytes'(=iv 값)을 갖는 서버 세션키 객체
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public ServerSymmetricKeyIF createNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException;
	
	/**
	 * 입력으로 받은 파라미터들에에 맞게 서버 세션키 객체를 생성하여 반환한다.
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
	 *      
	 * @param whetherToApplyBase64ToSymmetricKeyForSssionKey 세션키용 대칭키에 base64 적용 여부
	 * @param sessionkeyBytes 세션키 값 
	 * @param ivBytes iv 값
	 * @return 서버 세션키 객체
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public ServerSymmetricKeyIF createNewInstanceOfServerSymmetricKey(boolean whetherToApplyBase64ToSymmetricKeyForSssionKey, 
			byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException;
	
	/**
	 * @return 모듈러스 헥사 문자열
	 */
	public String getModulusHexStrForWeb();
	
	/**
	 * @return 공개키 복사본
	 */
	public byte[] getDupPublicKeyBytes();
	
	/**
	 * @param encryptedBytesWithPublicKey 공개키로 암호화한 암호문
	 * @return 공개키로 암호화한 암호문을 개인키로 풀은 복호문
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public byte[] decryptUsingPrivateKey(byte[] encryptedBytesWithPublicKey) throws SymmetricException;	
}
