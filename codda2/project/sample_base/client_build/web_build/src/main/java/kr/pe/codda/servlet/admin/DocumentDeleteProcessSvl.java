package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.DocumentDeleteReq.DocumentDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class DocumentDeleteProcessSvl extends AbstractAdminLoginServlet {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1214148725059263730L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramDocumentNo = req.getParameter("documentNo");
		/**************** 파라미터 종료 *******************/
		
		
		if (null == paramDocumentNo) {
			String errorMessage = "the web paramter 'documentNo' 를 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
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
		
		AccessedUserInformation accessedUserInformation = getAccessedUserInformationFromSession(req);
		
		DocumentDeleteReq documentDeleteReq = new DocumentDeleteReq();
		documentDeleteReq.setRequestedUserID(accessedUserInformation.getUserID());
		documentDeleteReq.setIp(req.getRemoteAddr());
		documentDeleteReq.setDocumentNo(documentNo);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), documentDeleteReq);

		if (!(outputMessage instanceof MessageResultRes)) {
			String errorMessage = "문서 삭제가 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(documentDeleteReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString()).append("] 도착").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}

		MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
		
		if (! messageResultRes.getIsSuccess()) {
			String errorMessage = messageResultRes.getResultMessage();
			String debugMessage = null;

			throw new WebClientException(errorMessage, debugMessage);
		}

		final String goPage = "/sitemenu/doc/DocumentDeleteProcess.jsp";
		printJspPage(req, res, goPage);
		
	}

}
