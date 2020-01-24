package kr.pe.codda.servlet.user;

import java.util.Arrays;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq;
import kr.pe.codda.impl.message.MemberLoginRes.MemberLoginRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.MemberRoleType;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;

public class MemberLoginProcessSvl extends AbstractSessionKeyServlet {

	private static final long serialVersionUID = -5979668829130203071L;
	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
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

		/*
		 * if (successURL.indexOf('/') != 0) { String errorMessage =
		 * "the request parameter successURL doesn't begin a char '/'"; String
		 * debugMessage = errorMessage; printErrorMessagePage(req, res, errorMessage,
		 * debugMessage); return; }
		 */

		// log.info(req.getParameterMap().toString());

		// req.setAttribute("isSuccess", Boolean.FALSE);

		ServerSymmetricKeyIF webServerSymmetricKey = buildServerSymmetricKey(req, false);

		/**
		 * WARNING! 보안 권고 사항에 따라 로그인 정보들을 자바 스트링으로 만들지 않기 위해서 비밀번호외 항목들은 유효성 검사를 수행하지 않는다   
		 */
		byte[] userIDBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramUserIDCipherBase64));
		byte[] passwordBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwdCipherBase64));
		
		try {
			ValueChecker.checkValidLoginPwd(passwordBytes);
		} catch (IllegalArgumentException e) {
			Arrays.fill(passwordBytes, (byte)0);
			
			String errorMessage = e.getMessage();
			String debugMessage = null;

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		// log.info("id=[{}], password=[{}]", userId, password);

		// FIXME!
		// log.info("userID=[{}]", new String(userIDBytes, "UTF8"));

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		
		
		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		ServerSessionkeyIF webServerSessionkey  = serverSessionkeyManager.getMainProjectServerSessionkey();
		binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());

		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), binaryPublicKeyReq);

		if (!(binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[").append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[").append(binaryPublicKeyOutputMessage.toString()).append("] 도착")
					.toString();

			log.log(Level.WARNING, debugMessage);

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
		memberLoginReq.setIdCipherBase64(
				CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(userIDBytes)));
		memberLoginReq.setPwdCipherBase64(
				CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(passwordBytes)));
		memberLoginReq.setSessionKeyBase64(CommonStaticUtil.Base64Encoder.encodeToString(sessionKeyBytesOfServer));
		memberLoginReq.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(ivBytesOfServer));
		memberLoginReq.setIp(req.getRemoteAddr());
		
		Arrays.fill(passwordBytes, (byte)0);

		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), memberLoginReq);

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
		
		if (MemberRoleType.GUEST.equals(memberRoleType)) {
			String errorMessage = "일반 유저 로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("특수 사용자[")
					.append(memberLoginRes.getUserID())
					.append("]는 손님으로 로그인 할 수 없습니다").toString();

			log.severe(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		HttpSession httpSession = req.getSession();
		httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION,
				new AccessedUserInformation(true, memberLoginRes.getUserID(), memberLoginRes.getUserName(), memberRoleType));
		/*
		 * httpSession .setAttribute(
		 * WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_ID,
		 * userLoginRes);
		 */
		/*
		 * httpSession .setAttribute(
		 * WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_ROLE_TYPE,
		 * memberRoleType);
		 */

		
		printJspPage(req, res, "/sitemenu/member/MemberLoginProcess.jsp");
		return;

	}

}
