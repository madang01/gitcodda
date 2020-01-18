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

package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.DocumentViewReq.DocumentViewReq;
import kr.pe.codda.impl.message.DocumentViewRes.DocumentViewRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class DocumentViewSvl extends AbstractAdminLoginServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1217432137857170069L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramDocumentNo = req.getParameter("documentNo");
		/**************** 파라미터 종료 *******************/
		
		final long documentNo;
		try {
			documentNo = Long.parseLong(paramDocumentNo);
		} catch(IllegalArgumentException e) {
			String errorMessage = "the web paramter 'documentNo' is not a long type value";
			String debugMessage = new StringBuilder()
					.append(errorMessage)
					.append(", documentNo=[")
					.append(paramDocumentNo)
					.append("]").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
		
		DocumentViewReq documentViewReq = new DocumentViewReq();
		documentViewReq.setRequestedUserID(accessedUserformation.getUserID());
		documentViewReq.setIp(req.getRemoteAddr());
		documentViewReq.setDocumentNo(documentNo);
		
		// FIXME!
		//log.info("inObj={}, userId={}, ip={}", inObj.toString(), userId, req.getRemoteAddr());
		
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), documentViewReq);
		
		if (! (outputMessage instanceof DocumentViewRes)){
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
				String errorMessage = messageResultRes.getResultMessage();
				String debugMessage = null;

				throw new WebClientException(errorMessage, debugMessage);
			} else {
				String errorMessage = "개별 문서 조회가 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(documentViewReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				throw new WebClientException(errorMessage, debugMessage);
			}
		}
		
		
		DocumentViewRes documentViewRes = (DocumentViewRes)outputMessage;
		req.setAttribute("documentViewRes", documentViewRes);		
		
		printJspPage(req, res, "/sitemenu/doc/DocumentView.jsp");
	}
	

}
