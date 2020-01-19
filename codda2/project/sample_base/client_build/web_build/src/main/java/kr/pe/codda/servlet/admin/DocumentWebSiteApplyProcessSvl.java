package kr.pe.codda.servlet.admin;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.DocumentViewReq.DocumentViewReq;
import kr.pe.codda.impl.message.DocumentViewRes.DocumentViewRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.DocumentFileBuilder;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class DocumentWebSiteApplyProcessSvl extends AbstractAdminLoginServlet {
	private final String installedPathString;
	private final String mainProjectName;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5393582918369214299L;
	
	
	public DocumentWebSiteApplyProcessSvl() {
		super();
		
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager
				.getInstance().getRunningProjectConfiguration();
		mainProjectName = runningProjectConfiguration.getMainProjectName();
		installedPathString = runningProjectConfiguration
				.getInstalledPathString();
	}
	

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
		
		
		AccessedUserInformation accessedUserInformation = getAccessedUserInformationFromSession(req);
		
		DocumentViewReq documentViewReq = new DocumentViewReq();
		documentViewReq.setRequestedUserID(accessedUserInformation.getUserID());
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
		
		String relativeURL = new StringBuilder()
				.append("/sitemenu/doc/")
				.append(documentViewRes.getFileName()).toString();
		
		String documentFilePathString = new StringBuilder()
				.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPathString, mainProjectName))
				.append(File.separatorChar)
				.append("sitemenu")
				.append(File.separatorChar)
				.append("doc")
				.append(File.separatorChar)
				.append(documentViewRes.getFileName()).toString();
		
		File documentFile = new File(documentFilePathString);
		
		String documentFileContents = DocumentFileBuilder.build(accessedUserInformation, relativeURL, documentViewRes.getSubject(), documentViewRes.getContents());
		
		CommonStaticUtil.saveFile(documentFile, documentFileContents, CommonStaticFinalVars.SOURCE_FILE_CHARSET);	
		
		req.setAttribute("documentViewRes", documentViewRes);
		
		printJspPage(req, res, "/sitemenu/doc/DocumentWebSiteApplyProcess.jsp");
	}

}
