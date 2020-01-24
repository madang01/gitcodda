package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.common.AccessedUserInformation;

@SuppressWarnings("serial")
public abstract class AbstractAdminLoginServlet extends AbstractSessionKeyServlet {	

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
		
		if (! accessedUserformation.isAdmin()) {
			String requestURI = req.getRequestURI();
			req.setAttribute("requestURI", requestURI);
			
			
			printJspPage(req, res, JDF_ADMIN_LOGIN_INPUT_PAGE);
			return;
		}
		
		super.performPreTask(req, res);
	}
}
