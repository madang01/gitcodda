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
 * 서버에서 클라이언트가 보낸 세션키로 부터 생성되는 대칭키 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ServerSymmetricKeyIF {
	/**
	 * @param plainTextBytes 평문
	 * @return 암호문
	 * @throws IllegalArgumentException 파라미터 'plainTextBytes' 가 null 일 경우 던지는 예외
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException;
	
	/**
	 * 
	 * @param encryptedBytes 암호문
	 * @return 복호문
	 * @throws IllegalArgumentException 파라미터 'encryptedBytes' 가 null 일 경우 던지는 예외
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException;
}
