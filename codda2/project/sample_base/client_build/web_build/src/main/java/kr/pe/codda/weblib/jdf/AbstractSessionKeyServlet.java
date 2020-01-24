/**
 * 
 * @(#) .java
 * Copyright 1999-2000 by  LG-EDS Systems, Inc.,
 * Information Technology Group, Application Architecture Team, 
 * Application Intrastructure Part.
 * 236-1, Hyosung-2dong, Kyeyang-gu, Inchun, 407-042, KOREA.
 * All rights reserved.
 *  
 * NOTICE !      You can copy or redistribute this code freely, 
 * but you should not remove the information about the copyright notice 
 * and the author.
 *  
 * @author  WonYoung Lee, wyounglee@lgeds.lg.co.kr.
 */

package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;

import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.exception.NoSessionKeyParameterException;
import kr.pe.codda.weblib.exception.WebClientException;

/**
 * <pre>
 * 세션키 추상화 클래스.
 * (1) 비밀번호와 같은 암호화가 필요한 파라미터가 있는 경우 혹은 
 * (2) 페이지 자체 내용중 암호화가 필요한 경우
 * 에 상속 받는 추상화 클래스이다.
 * 
 * WARNING! 외부에서 특정 주소의 페이지를 호출할때 
 * 세션키와 관련된 파라미터 값들을 제외한 주소를 알려주는것이 서로에게 편리하다.
 * 또 사이트 메뉴는 정적인 값으로 처리해야 메뉴 커서를 위치 시킬 수 있으므로 동적 성질인 세션키 관련 파라미터를 제외할 수 밖에 없다.
 * 하여 이런 경우에 대한 처리가 필요하기때문에 
 * 세션키 관련 파라미터가 없는 경우  
 * 파라미터들을 보존하며 세션키 관련 파라미터 값을 가져와 처리하도록 한다.
 * 오버헤드이므로 오남용 하지 않도록 한다.
 * </pre>
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractSessionKeyServlet extends AbstractServlet {
	
	protected ServerSymmetricKeyIF buildServerSymmetricKey(HttpServletRequest req, boolean isRedirectIfThereIsNoSessionkeyParameter) throws NoSessionKeyParameterException, WebClientException {
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		if (null == paramSessionKeyBase64) {
			// log.info("req.getRequestURI=[{}]", req.getRequestURI());

			// req.setAttribute("requestURI", req.getRequestURI());
			// printJspPage(req, res, JDF_SESSION_KEY_REDIRECT_PAGE);
			if (isRedirectIfThereIsNoSessionkeyParameter) {
				throw new NoSessionKeyParameterException();
			} else {
				String errorMessage = new StringBuilder()
						.append("enter the web parmaeter '")
						.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
						.append("'").toString();
				throw new WebClientException(errorMessage, null);
			}
		}
		
		if (null == paramIVBase64) {
			if (isRedirectIfThereIsNoSessionkeyParameter) {
				throw new NoSessionKeyParameterException();
			} else {
				String errorMessage = new StringBuilder()
						.append("enter the web parmaeter '")
						.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
						.append("'").toString();
				throw new WebClientException(errorMessage, null);
			}
		}


		// log.info("paramSessionKeyBase64=[{}]", paramSessionKeyBase64);
		// log.info("paramIVBase64=[{}]", paramIVBase64);
		// System.out.println("paramSessionKeyBase64="+paramSessionKeyBase64);
		// System.out.println("paramIVBase64="+paramIVBase64);

		// log.info("req.getRequestURI=[{}]", req.getRequestURI());
		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = CommonStaticUtil.Base64Decoder.decode(paramSessionKeyBase64);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("the parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY).append("'[")
					.append(paramSessionKeyBase64).append("] is not a base64 encoding string").toString();

			String debugMessage = new StringBuilder(errorMessage).append(", errmsg=").append(e.getMessage()).toString();

			throw new WebClientException(errorMessage, debugMessage);
		}
		byte[] ivBytes = null;
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(paramIVBase64);
		} catch (Exception e) {

			String errorMessage = new StringBuilder().append("the parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV).append("'[")
					.append(paramIVBase64).append("] is not a base64 encoding string").toString();

			String debugMessage = new StringBuilder(errorMessage).append(", errmsg=").append(e.getMessage()).toString();

			throw new WebClientException(errorMessage, debugMessage);
		}

		// log.info("sessionkeyBytes=[{}]",
		// HexUtil.getHexStringFromByteArray(sessionkeyBytes));
		// log.info("ivBytes=[{}]", HexUtil.getHexStringFromByteArray(ivBytes));

		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		ServerSymmetricKeyIF symmetricKeyFromSessionkey = null;

		try {
			ServerSessionkeyIF webServerSessionkey  = serverSessionkeyManager.getMainProjectServerSessionkey();
			symmetricKeyFromSessionkey = webServerSessionkey.createNewInstanceOfServerSymmetricKey(true, sessionkeyBytes,
					ivBytes);
		} catch (Exception e) {
			String errorMessage = "fail to dsl a new instance of ServerSymmetricKeyIF class";
			
			String debugMessage = new StringBuilder(errorMessage).append(", paramSessionKeyBase64=[").append(paramSessionKeyBase64)
					.append("], paramIVBase64=[").append(paramIVBase64).append("], errmsg=").append(e.getMessage())
					.toString();
			
			throw new WebClientException(errorMessage, debugMessage);
		}

		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SYMMETRIC_KEY_FROM_SESSIONKEY,
				symmetricKeyFromSessionkey);
		
		return symmetricKeyFromSessionkey;
	}
}
