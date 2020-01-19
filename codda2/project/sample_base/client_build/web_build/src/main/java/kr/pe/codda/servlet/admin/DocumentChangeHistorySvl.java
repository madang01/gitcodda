package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.DocumentChangeHistoryReq.DocumentChangeHistoryReq;
import kr.pe.codda.impl.message.DocumentChangeHistoryRes.DocumentChangeHistoryRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class DocumentChangeHistorySvl extends AbstractAdminLoginServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2586424343882338661L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		
		/**************** 파라미터 시작 *******************/
		String paramPageNo = req.getParameter("pageNo");
		String paramDocumentNo = req.getParameter("documentNo");
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
		
		DocumentChangeHistoryReq documentChangeHistoryReq = new DocumentChangeHistoryReq();
		documentChangeHistoryReq.setRequestedUserID(accessedUserformation.getUserID());
		documentChangeHistoryReq.setIp(req.getRemoteAddr());
		documentChangeHistoryReq.setDocumentNo(documentNo);
		documentChangeHistoryReq.setPageNo(pageNo);
		documentChangeHistoryReq.setPageSize(WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), documentChangeHistoryReq);
		
		if (! (outputMessage instanceof DocumentChangeHistoryRes)){
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
				String errorMessage = messageResultRes.getResultMessage();
				String debugMessage = null;

				throw new WebClientException(errorMessage, debugMessage);
			} else {
				String errorMessage = "개별 문서 변경 이력 조회가 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(documentChangeHistoryReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				throw new WebClientException(errorMessage, debugMessage);
			}
		}
		
		
		DocumentChangeHistoryRes documentChangeHistoryRes = (DocumentChangeHistoryRes)outputMessage;
		req.setAttribute("documentChangeHistoryRes", documentChangeHistoryRes);		
		
		printJspPage(req, res, "/sitemenu/doc/DocumentChangeHistory.jsp");
	}

}
