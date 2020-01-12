package kr.pe.codda.servlet.user;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 자바 스크립트 CryptoJS 라이브러리에서 제공하는 해쉬(=메시지 다이제스트) 함수와 자바 결과 일치 테스트<br/>
 * 해쉬 함수 목록 (1) MD5 (2) SHA1 (3) SHA-256 (4) SHA-512 가 있다.
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class JSMessageDigestInputSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {		
		
		printJspPage(req, res, "/sitemenu/util/JSMessageDigestInput.jsp");
	}
	
}
