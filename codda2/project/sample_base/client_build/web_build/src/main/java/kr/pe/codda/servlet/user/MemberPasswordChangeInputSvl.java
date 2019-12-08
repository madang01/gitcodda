package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractUserLoginServlet;

public class MemberPasswordChangeInputSvl extends AbstractUserLoginServlet {

	private static final long serialVersionUID = -7030193576587322541L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		printJspPage(req, res, "/jsp/my/MemberPasswordChangeInput.jsp");
		return;
	}

}
