package kr.pe.codda.servlet.user;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;


@SuppressWarnings("serial")
public class JDFSessionKeySvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		/**
		 * WARNING! 지우질 말것, 이 페이지는 암호 페이지이기때문에 웹 파라비터로 세션키를 얻어와 그 세션키로 부터 얻은 대칭키를 약속한 request 속성 키 값에 저장되어 있어야 옳바로 동작한다. 
		 */
		buildServerSymmetricKey(req, true);
		
		String goPage = "/sitemenu/util/JDFSessionKey.jsp";
		printJspPage(req, res, goPage);	
	}
}
