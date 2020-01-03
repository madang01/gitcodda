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

package kr.pe.codda.common.type;

/**
 * 세션키
 * @author Won Jonghoon
 *
 */
public abstract class SessionKey {
	/**
	 * 공개키 쌍이 저장된 장소 타입 {서버, 파일}, 참고로 서버이면 프로그램 내부에서 공개키 쌍을 생성하여 이용한다는것을 말함.   
	 * @author Won Jonghoon
	 *
	 */
	public enum RSAKeypairSourceType {
		SERVER, FILE;
	}
}
