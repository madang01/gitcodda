package kr.pe.codda.servlet.user;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.MemberRoleType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;

public class HardCodingMemberLoginProcessSvl extends AbstractSessionKeyServlet {
	
	class FreePassUserInfo {
		private String userID = null;
		private String userName = null;
		private String password = null;
		private MemberRoleType memberType = null;
		
		public FreePassUserInfo(String userID, String userName, 
				String password, MemberRoleType memberType) {
			super();
			this.userID = userID;
			this.userName = userName;
			this.password = password;
			this.memberType = memberType;
		}

		public String getUserID() {
			return userID;
		}
		
		public String getUserName() {
			return userName;
		}

		public String getPassword() {
			return password;
		}

		public MemberRoleType getMemberType() {
			return memberType;
		}
	}

	private static final long serialVersionUID = -4900821586755098845L;
	
	private HashMap<String, FreePassUserInfo> freePassUserInfoHash = null;
	
	public HardCodingMemberLoginProcessSvl() {
		freePassUserInfoHash = new HashMap<String, FreePassUserInfo>();
		
		freePassUserInfoHash.put("admin", new FreePassUserInfo("admin", "관리자", "test1234$", MemberRoleType.ADMIN));
		freePassUserInfoHash.put("test01", new FreePassUserInfo("test01", "테스터1", "test1234$", MemberRoleType.MEMBER));
		freePassUserInfoHash.put("test02", new FreePassUserInfo("test02", "테스터2", "test1234$", MemberRoleType.MEMBER));
	}

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramUserIDCipherBase64 = req.getParameter("userID");
		String paramPwdCipherBase64 = req.getParameter("pwd");
		/**************** 파라미터 종료 *******************/

		
		if (null == paramUserIDCipherBase64) {
			String errorMessage = "the request parameter userID is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramPwdCipherBase64) {
			String errorMessage = "the request parameter paramPwdCipherBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		
		/*if (successURL.indexOf('/') != 0) {
			String errorMessage = "the request parameter successURL doesn't begin a char '/'";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}*/

		log.info("param userID=[" + paramUserIDCipherBase64 + "]");		
		log.info("param pwd=[" + paramPwdCipherBase64 + "]");

		// req.setAttribute("isSuccess", Boolean.FALSE);
		ServerSymmetricKeyIF webServerSymmetricKey = buildServerSymmetricKey(req, false);
		
		byte[] userIDBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramUserIDCipherBase64));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwdCipherBase64));

		String userID = new String(userIDBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		String password = new String(passwordBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		
		FreePassUserInfo freePassUserInfo = freePassUserInfoHash.get(userID);
		
		if (null == freePassUserInfo) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			
			String debugMessage = new StringBuilder("사용자 아이디[")
			.append(userID)
			.append("]가 존재하지 않습니다").toString();
			
			log.warning(debugMessage);
			
			printErrorMessagePage(req, res, 
					errorMessage, 
					debugMessage);
			return;
		}
		
		if (! freePassUserInfo.getPassword().equals(password)) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			
			String debugMessage = new StringBuilder("사용자 아이디[")
			.append(userID)
			.append("]의 비밀번호가 틀렸습니다").toString();
			
			log.warning(debugMessage);
			
			printErrorMessagePage(req, res, 
					errorMessage, 
					debugMessage);
			return;
		}
		
		HttpSession httpSession = req.getSession();
		
		httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION,
				new AccessedUserInformation(true, freePassUserInfo.getUserID(), freePassUserInfo.getUserName(),
						freePassUserInfo.getMemberType()));

		printJspPage(req, res, "/sitemenu/member/UserLoginOKCallBack.jsp");
		return;		
	}

}
