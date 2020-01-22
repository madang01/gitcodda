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
import kr.pe.codda.impl.message.DocumentListReq.DocumentListReq;
import kr.pe.codda.impl.message.DocumentListRes.DocumentListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.DocumentSateSearchType;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class DocumentManagerSvl extends AbstractAdminLoginServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8533825872824996704L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		
		/**************** 파라미터 시작 *******************/
		String paramPageNo = req.getParameter("pageNo");
		String paramDocumentSateSearchType = req.getParameter("documentSateSearchType");
		/**************** 파라미터 종료 *******************/
		
		final int pageNo;
		
		if (null == paramPageNo) {
			pageNo = 1;
		} else {
			try {
				pageNo = ValueChecker.checkValidPageNoAndPageSize(paramPageNo, WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String debugMessage = null;
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}
		
		
		final DocumentSateSearchType documentSateSearchType;
		if (null == paramDocumentSateSearchType) {
			documentSateSearchType = DocumentSateSearchType.OK;
		} else {
			try {
				documentSateSearchType = DocumentSateSearchType.valueOf(paramDocumentSateSearchType, String.class);
			} catch(IllegalArgumentException e) {
				String errorMessage = "the web parameter 'documentSateSearchType' is bad";
				String debugMessage = null;
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}
		
		DocumentListReq documentListReq = new DocumentListReq();
		documentListReq.setRequestedUserID(getAccessedUserInformationFromSession(req).getUserID());
		documentListReq.setPageNo(pageNo);
		documentListReq.setPageSize(WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		documentListReq.setDocumentSateSearchType(documentSateSearchType.getValue());
				
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), documentListReq);
		
		if (!(outputMessage instanceof DocumentListRes)) {
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
				String errorMessage = "게시판 목록 조회가 실패하였습니다";
				String debugMessage = messageResultRes.toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);	
				return;
			} else {
				String errorMessage = "게시판 목록 조회가 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(documentListReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.severe(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		} 
		
		DocumentListRes documentListRes = (DocumentListRes)outputMessage;
		req.setAttribute("documentListRes", documentListRes);
		req.setAttribute("documentSateSearchType", documentSateSearchType);
		printJspPage(req, res, "/sitemenu/doc/DocumentManager.jsp");
		
	}

}
