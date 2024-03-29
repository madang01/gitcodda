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
 * 클라이언트 RSA 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ClientRSAIF {
	
	/**
	 * @return 공개키 복사본
	 */
	public byte[] getDupPublicKeyBytes();
	
	
	/**
	 * @param sourceBytes 공개키로 암호화될 대상 데이터
	 * @return 파라미티 'sourceBytes' 를 RAS 공개키로 암호화된 결과 바이트 배열
	 * @throws SymmetricException 암호화 관련 처리중 에러 발생시 던지는 예외
	 */
	public byte[] encrypt(byte sourceBytes[]) throws SymmetricException;
}
