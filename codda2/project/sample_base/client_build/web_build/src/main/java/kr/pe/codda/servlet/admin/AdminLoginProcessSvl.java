package kr.pe.codda.servlet.admin;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq;
import kr.pe.codda.impl.message.MemberLoginRes.MemberLoginRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.MemberRoleType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 관리자 로그인 처리 서블릿
 * @author Won Jonghoon
 *
 */
public class AdminLoginProcessSvl extends AbstractServlet {

	private static final long serialVersionUID = -8458712103045075706L;	
	
	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		String paramUserIDCipherBase64 = req.getParameter("userID");
		String paramPwdCipherBase64 = req.getParameter("pwd");
		/**************** 파라미터 종료 *******************/

		
		if (null == paramSessionKeyBase64) {
			String errorMessage = "the request parameter paramSessionKeyBase64 is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramIVBase64) {
			String errorMessage = "the request parameter paramIVBase64 is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramUserIDCipherBase64) {
			String errorMessage = "the request parameter userID is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramPwdCipherBase64) {
			String errorMessage = "the request parameter paramPwdCipherBase64 is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		
		/*if (successURL.indexOf('/') != 0) {
			String errorMessage = "the request parameter successURL doesn't begin a char '/'";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}*/

		// log.info(req.getParameterMap().toString());

		// req.setAttribute("isSuccess", Boolean.FALSE);
				

		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = CommonStaticUtil.Base64Decoder.decode(paramSessionKeyBase64);
		} catch(Exception e) {			
			String errorMessage = "세션키 파라미터가 잘못되었습니다";
			String debugMessage = new StringBuilder()
			.append("the parameter '")
			.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
			.append("'[")
			.append(paramSessionKeyBase64)
			.append("] is not a base64 encoding string, errmsg=")
			.append(e.getMessage()).toString();
			
			log.warning(debugMessage);
			
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(paramIVBase64);
		} catch(Exception e) {			
			String errorMessage = "세션키 소금 파라미터가 잘못되었습니다";
			String debugMessage = new StringBuilder()
			.append("the parameter '")
			.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
			.append("'[")
			.append(paramIVBase64)
			.append("] is not a base64 encoding string, errmsg=")
			.append(e.getMessage()).toString();
			
			log.warning(debugMessage);
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		ServerSessionkeyIF webServerSessionkey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		} catch (SymmetricException e) {
			String errorMessage = "fail to get a ServerSessionkeyManger class instance";
			log.log(Level.WARNING, errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		ServerSymmetricKeyIF symmetricKeyFromSessionkey = null;
		try {
			symmetricKeyFromSessionkey = webServerSessionkey.createNewInstanceOfServerSymmetricKey(true, sessionkeyBytes, ivBytes);
		} catch(IllegalArgumentException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.log(Level.WARNING, errorMessage, e);
			
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch(SymmetricException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.log(Level.WARNING, errorMessage, e);
			
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
				
		
		// FIXME!
		// log.info("한글 대칭키 암호문 base64=" + CommonStaticUtil.Base64Encoder.encodeToString(symmetricKeyFromSessionkey.encrypt("한글".getBytes("UTF8"))));

		byte[] userIDBytes = symmetricKeyFromSessionkey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramUserIDCipherBase64));
		byte[] passwordBytes = symmetricKeyFromSessionkey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwdCipherBase64));

		String userId = new String(userIDBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		// String password = new String(passwordBytes,

		// log.info("id=[{}], password=[{}]", userId, password);
		
		// FIXME!
		// log.info("userID=[{}]", new String(userIDBytes, "UTF8"));

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		
		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
		binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());

		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), binaryPublicKeyReq);

		if (!(binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(binaryPublicKeyOutputMessage.toString())
					.append("] 도착").toString();
			
			log.severe(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		
		BinaryPublicKey binaryPublicKeyRes = (BinaryPublicKey) binaryPublicKeyOutputMessage;
		byte[] binaryPublicKeyBytes = binaryPublicKeyRes.getPublicKeyBytes();

		ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager.getInstance()
				.createNewClientSessionKey(binaryPublicKeyBytes, false);

		byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();
		byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		
		MemberLoginReq memberLoginReq = new MemberLoginReq();
		memberLoginReq.setIdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(userIDBytes)));
		memberLoginReq.setPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(passwordBytes)));
		memberLoginReq.setSessionKeyBase64(CommonStaticUtil.Base64Encoder.encodeToString(sessionKeyBytesOfServer));
		memberLoginReq.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(ivBytesOfServer));
		memberLoginReq.setIp(req.getRemoteAddr());

		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), memberLoginReq);
		
		if (!(outputMessage instanceof MemberLoginRes)) {
			
			if ((outputMessage instanceof MessageResultRes)) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;

				printErrorMessagePage(req, res, messageResultRes.getResultMessage(), null);
				return;
			}
			
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(memberLoginReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.severe(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}				
		
		MemberLoginRes memberLoginRes = (MemberLoginRes) outputMessage;
		
		
		MemberRoleType memberRoleType = null;

		try {
			memberRoleType = MemberRoleType.valueOf(memberLoginRes.getMemberRole());
		} catch (IllegalArgumentException e) {
			String errorMessage = "일반 유저 로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("사용자[")
					.append(memberLoginRes.getUserID())
					.append("]의 멤버 종류[")
					.append(memberLoginRes.getMemberRole())
					.append("] 가 잘못되었습니다").toString();

			log.severe(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! MemberRoleType.ADMIN.equals(memberRoleType)) {
			String errorMessage = "관리자 로그인이 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력한 회원[아이디=")
					.append(memberLoginRes.getUserID())
					.append(", 역활=")
					.append(memberRoleType.getName())
					.append("]은 관리자가 아닙니다").toString();

			log.severe(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		HttpSession httpSession = req.getSession();
		httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION,
				new AccessedUserInformation(true, userId, memberLoginRes.getUserName(), memberRoleType));
		
		
		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SYMMETRIC_KEY_FROM_SESSIONKEY, 
				symmetricKeyFromSessionkey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());

		printJspPage(req, res, "/sitemenu/member/AdminLoginProcess.jsp");
		return;		
	}
}
