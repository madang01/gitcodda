package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import kr.pe.codda.weblib.common.AccessedUserInformation;

public abstract class AbstractMultipartServlet extends AbstractServlet {
	
	private static final long serialVersionUID = -6436777887426672536L;

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		if (! ServletFileUpload.isMultipartContent(req)) {
			String errorMessage = "the request doesn't contain multipart content";
			
			AccessedUserInformation  accessedUserformation = getAccessedUserInformationFromSession(req);
			
			String userID = (null == accessedUserformation) ? "guest" : accessedUserformation.getUserID();			
			
			String debugMessage = new StringBuilder(errorMessage).append(", userID=")
					.append(userID)
					.append(", ip=")
					.append(req.getRemoteAddr()).toString();			
			
			log.warning(debugMessage);		
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);		
			return;
		}		
		
		super.performPreTask(req, res);
	}
}
