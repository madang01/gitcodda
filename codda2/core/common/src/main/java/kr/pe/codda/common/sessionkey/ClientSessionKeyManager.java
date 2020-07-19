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

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.part.RunningProjectConfiguration;
import kr.pe.codda.common.config.part.SessionkeyPartConfiguration;
import kr.pe.codda.common.exception.SymmetricException;

/**
 * 클라이언트 세션키 관리자
 * @author Won Jonghoon
 *
 */
public final class ClientSessionKeyManager {
	private static String symmetricKeyAlgorithm;
	private static int symmetricKeySize;
	private static int symmetricIVSize;
	
	// private static ClientSessionKeyIF mainClientSessionKey = null;	
	// private static ConcurrentHashMap<String, ClientSessionKeyIF> subProjectNameToClientSessionKeyHash = new ConcurrentHashMap<String, ClientSessionKeyIF>();
	// private static AllSubProjectPartConfiguration allSubProjectPartConfiguration;
	
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ClientSessionKeyManagerHolder {
		static final ClientSessionKeyManager singleton = new ClientSessionKeyManager();
	}

	
	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드 
	 * @return ClientSessionKeyManager 객체
	 */
	public static ClientSessionKeyManager getInstance() {
		return ClientSessionKeyManagerHolder.singleton;
	}
	
	private ClientSessionKeyManager() {
		CoddaConfiguration coddaConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getCoddaConfiguration();	
		
		
		RunningProjectConfiguration runningProjectConfiguration = coddaConfiguration.getRunningProjectConfiguration();
		
		SessionkeyPartConfiguration sessionkeyPartConfiguration = runningProjectConfiguration.getSessionkeyPartConfiguration();
		
		symmetricKeyAlgorithm = sessionkeyPartConfiguration.getSymmetricKeyAlgorithm();
		symmetricKeySize = sessionkeyPartConfiguration.getSymmetricKeySize();
		symmetricIVSize = sessionkeyPartConfiguration.getSymmetricIVSize();
		// allSubProjectPartConfiguration = runningProjectConfiguration.getAllSubProjectPartConfiguration();		
	}

	/*
	public synchronized ClientSessionKeyIF getMainProjectClientSessionKey() throws SymmetricException, InterruptedException {
		if (null == mainClientSessionKey) {
			
			byte[] publicKeyBytes = clientRSAPublickeyGetter.getMainProjectPublickeyBytes();
			mainClientSessionKey = new ClientSessionKey(new ClientRSA(publicKeyBytes), symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isBase64);
		}
		return mainClientSessionKey;
	}
	
	public synchronized ClientSessionKeyIF getSubProjectClientSessionKey(String subProjectName, AbstractRSAPublickeyGetter clientRSAPublickeyGetter, boolean isBase64) throws IllegalArgumentException, SymmetricException {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}
				
		if (! allSubProjectPartConfiguration.isRegistedSubProjectName(subProjectName)) {				
			throw new IllegalArgumentException("the parameter subProjectName is not registered in configuration file");
		}
		
		ClientSessionKeyIF subClientSessionKey = subProjectNameToClientSessionKeyHash.get(subProjectName);
		
		if (null == subClientSessionKey) {
			byte[] publicKeyBytes = clientRSAPublickeyGetter.getSubProjectPublickeyBytes(subProjectName);
			subClientSessionKey = new ClientSessionKey(new ClientRSA(publicKeyBytes), symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isBase64);	
			subProjectNameToClientSessionKeyHash.put(subProjectName, subClientSessionKey);
		}
		 
		return subClientSessionKey;
	}
	
	*/
	
	/**
	 * <pre>
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
	 * @param publicKeyBytes 공개키 값
	 * @param isSymmetricKeyForSessionKeyBase64 세션키용 대칭키에 base64 적용 여부 
	 * @return 파라미터 'isBase64'(=공개키에 base64 적용 여부) 가 적용된 파라미티 'publicKeyBytes'(=공개키 값) 를 갖는 신규 클라이언트 세션키 객체
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public ClientSessionKeyIF createNewClientSessionKey(byte[] publicKeyBytes, boolean isSymmetricKeyForSessionKeyBase64) throws SymmetricException {
		if (null == publicKeyBytes) {
			throw new IllegalArgumentException("the parameter publicKeyBytes is null");
		}
		
		return new ClientSessionKey(new ClientRSA(publicKeyBytes), symmetricKeyAlgorithm, symmetricKeySize, symmetricIVSize, isSymmetricKeyForSessionKeyBase64);
	}
}
