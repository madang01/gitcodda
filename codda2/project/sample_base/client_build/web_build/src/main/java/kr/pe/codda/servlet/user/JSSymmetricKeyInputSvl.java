package kr.pe.codda.servlet.user;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 자바 스크립트 CryptoJS 라이브러리에서 제공하는 대칭키 함수와 자바 결과 일치 테스트<br/>
 * 대칭키 함수 목록 (1) AES (2) DES (3)  DESede(=Triple DES)
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class JSSymmetricKeyInputSvl extends AbstractServlet {	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {		
		
		printJspPage(req, res, "/jsp/util/JSSymmetricKeyInput.jsp");
	}
}
