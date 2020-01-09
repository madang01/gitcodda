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

import java.nio.channels.SelectionKey;

/**
 * 프로젝트 로그인 관리자 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ProjectLoginManagerIF {
	
	/**
	 * 로그인 성공시 호출되는 메소드로 로그인 성공한 사용자 아이디를 로그인 성공 정보에 등록한다.
	 * 
	 * @param selectedKey 셀렉션키
	 * @param loginID 로그인 성공한 사용자 아이디
	 */
	public void registerloginUser(SelectionKey selectedKey, String loginID);
	
	/**
	 * 로그인 아웃시 호출되는 메소드
	 * @param selectedKey 로그 아웃 대상 셀렉션 키
	 */
	public void removeLoginUser(SelectionKey selectedKey);

	/**
	 * @param userID 사용자 아이디
	 * @return 로그인 여부
	 */
	public boolean isLogin(String userID);
	
	/**
	 * 
	 * @param loginID 로그인 아이디
	 * @return 셀렉션 키, 만약 파라미터 'loginID'(로그인 아이디) 가 로그인 성공 정보에 미 등록되어 있다면 null 을 반환한다. 
	 */
	public SelectionKey getSelectionKey(String loginID);
}
