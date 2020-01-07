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

public class RSAPublickeyReturnerManger {

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class RSAPublickeyGetterHolder {
		static final AbstractRSAPublickeyReturner singleton = new RSAPublickeyReturner();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static AbstractRSAPublickeyReturner getInstance() {
		return RSAPublickeyGetterHolder.singleton;
	}

	private RSAPublickeyReturnerManger() {
	}

}
