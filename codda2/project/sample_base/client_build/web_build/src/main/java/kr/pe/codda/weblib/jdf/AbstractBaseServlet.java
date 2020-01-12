package kr.pe.codda.weblib.jdf;



import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

@SuppressWarnings("serial")
public abstract class AbstractBaseServlet extends HttpServlet {
	protected Logger log = Logger.getLogger(AbstractBaseServlet.class.getName());
	
	
	public AccessedUserInformation getAccessedUserInformationFromSession(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		
		Object accessedUserformationOfHttpSession = httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION);
		
		if (null == accessedUserformationOfHttpSession) {
			return WebCommonStaticFinalVars.GUEST_USER_SESSION_INFORMATION;
		}
				
		AccessedUserInformation  accessedUserformation= (AccessedUserInformation) accessedUserformationOfHttpSession;
		
		return accessedUserformation;
	}
}
