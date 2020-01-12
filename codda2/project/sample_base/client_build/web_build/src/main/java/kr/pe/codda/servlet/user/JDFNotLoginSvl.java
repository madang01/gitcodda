package kr.pe.codda.servlet.user;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractServlet;


/**
 * 비 로그인 JDF 상속 서블릿
 * @author Won Jonghoon
 *
 */
public class JDFNotLoginSvl extends AbstractServlet {
	
	private static final long serialVersionUID = 7042189810188588931L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
				
		// String title = "Not Login Test:MVC2 model ok";
		
		Enumeration<String> headerNames = req.getHeaderNames();
		
		Hashtable<String, String> headerInformationHash = new  Hashtable<String, String>();
		
		while (headerNames.hasMoreElements()) {
		    String key = (String)headerNames.nextElement();
		    String value = req.getHeader(key);
		    headerInformationHash.put(key, value);
        }
	
		req.setAttribute("headerInformationHash", headerInformationHash);		
		printJspPage(req, res, "/sitemenu/util/JDFNotLogin.jsp");
	}
}
