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
package kr.pe.codda.server;

public interface LoginManagerIF {
	/**
	 * @return 로그인 여부
	 */
	public boolean isLogin();
	
	/**
	 * 로그인 성공했을때 호출되는 메소드로 로그인 아이디를 등록한다, 단 로그인 아이디가 null 이면 예외를 던진다.
	 * @param loginID 로그인 아이디
	 * @throws IllegalArgumentException 만약 지정한 로그인 아이다가 null 인 경우 던지는 예외
	 */
	public void registerLoginUser(String loginID) throws IllegalArgumentException;
	
	/**
	 * @return 로그인 아이디를 반환한다, 단 로그인 안했을 경우 null 을 반환 
	 */
	public String getLoginID();
}
