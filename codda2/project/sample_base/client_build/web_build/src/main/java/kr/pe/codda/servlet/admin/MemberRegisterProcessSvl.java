package kr.pe.codda.servlet.admin;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
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
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;
import nl.captcha.Captcha;

public class MemberRegisterProcessSvl extends AbstractSessionKeyServlet {

	private static final long serialVersionUID = 2742729909269799873L;
	
	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		MessageResultRes messageResultRes = null;
		
		try {
			messageResultRes = doWork(req, res);
		} catch (WebClientException e) {
			String errorMessage = e.getErrorMessage();
			String debugMessage = e.getDebugMessage();

			AccessedUserInformation  accessedUserformation = getAccessedUserInformationFromSession(req);
			
			String  logMessage = new StringBuilder()
					.append("errmsg=")
					.append(errorMessage)
					.append(CommonStaticFinalVars.NEWLINE)
					.append(", debugmsg=")
					.append(debugMessage)
					.append(", userID=")
					.append(accessedUserformation.getUserID())
					.append(", ip=")
					.append(req.getRemoteAddr())
					.append(", errmsg=")
					.append(e.getMessage())
					.toString();
					
			log.warning(logMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (Exception e) {
			AccessedUserInformation  accessedUserformation = getAccessedUserInformationFromSession(req);
			
			String errorMessage = "회원 가입이 실패하였습니다";
			String debugMessage = new StringBuilder()
					.append(errorMessage)
					.append(", userID=")
					.append(accessedUserformation.getUserID())
					.append(", ip=")
					.append(req.getRemoteAddr())
					.append(", errmsg=")
					.append(e.getMessage())
					.toString();
			
			log.log(Level.WARNING, debugMessage, e);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! messageResultRes.getIsSuccess()) {
			printErrorMessagePage(req, res, messageResultRes.getResultMessage(), null);
			return;
		}
		
		printJspPage(req, res, "/sitemenu/member/MemberRegisterProcess.jsp");
		return;
	}
	
	public MessageResultRes doWork(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		/**************** 파라미터 시작 *******************/	
		String paramUserIDBase64 = req.getParameter("userID");
		String paramPwdBase64 = req.getParameter("pwd");
		String paramNicknameBase64 = req.getParameter("nickname");		
		String paramEmailBae64 = req.getParameter("email");
		String paramCaptchaAnswerBase64 = req.getParameter("captchaAnswer");
		/**************** 파라미터 종료 *******************/
		
		// log.info(req.getParameterMap().toString());		
		if (null == paramUserIDBase64) {
			String errorMessage = "아이디를 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		if (null == paramPwdBase64) {
			String errorMessage = "비밀번호를 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		if (null == paramNicknameBase64) {
			String errorMessage = "별명을 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		if (null == paramEmailBae64) {
			String errorMessage = "이메일 주소를 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		if (null == paramCaptchaAnswerBase64) {
			String errorMessage = "Captcha 값을 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		ServerSymmetricKeyIF webServerSymmetricKey = buildServerSymmetricKey(req, false);
	
		/**
		 * WARNING! 보안 권고 사항에 따라 회원 가입 정보들을 자바 스트링으로 만들지 않기 위해서 비밀번호외 항목들은 유효성 검사를 수행하지 않는다   
		 */
		byte[] userIdBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramUserIDBase64));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwdBase64));
		byte[] nicknameBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramNicknameBase64));
		byte[] emailBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramEmailBae64));		
		byte[] captchaAnswerBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramCaptchaAnswerBase64));
		
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;

			throw new WebClientException(errorMessage, debugMessage);
		}
		
		String captchaAnswer = new String(captchaAnswerBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		
		HttpSession httpSession = req.getSession();
		Captcha captcha = (Captcha) httpSession.getAttribute(Captcha.NAME);
		
		if (null == captcha) {
			String errorMessage = "캡차(Captcha) 세션 값이 없습니다. 캡차(Captcha)을 새로 생성하여 다시 시도해 주세요";			
			String debugMessage = null;
			
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		if (! captcha.isCorrect(captchaAnswer)) {
			String errorMessage = "입력한 Captcha 값이 틀렸습니다.";
			
			String debugMessage = new StringBuilder()
					.append("사용자가 입력한 Captcha 값[")
					.append(captchaAnswer)
					.append("]과 내부 Captcha 값[")
					.append(captcha.getAnswer())
					.append("]이 다릅니다").toString();			
			
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		httpSession.removeAttribute(Captcha.NAME);
	
		// log.info("userId=[{}]", userId);
	
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		
		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		ServerSessionkeyIF webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());
		
		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), binaryPublicKeyReq);					
		if (! (binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
			String errorMessage = "회원 가입이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(binaryPublicKeyOutputMessage.toString())
					.append("] 도착").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}
		
		BinaryPublicKey binaryPublicKeyOutObj = (BinaryPublicKey) binaryPublicKeyOutputMessage;
		byte[] binaryPublicKeyBytes = binaryPublicKeyOutObj.getPublicKeyBytes();
		ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager.getInstance()
				.createNewClientSessionKey(binaryPublicKeyBytes, false);
		
		
		
		byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();								
		byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();

		MemberRegisterReq memberRegisterReq = new MemberRegisterReq();		
		
		memberRegisterReq.setIdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(userIdBytes)));
		memberRegisterReq.setPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(passwordBytes)));
		memberRegisterReq.setNicknameCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(nicknameBytes)));
		memberRegisterReq.setEmailCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(emailBytes)));
		memberRegisterReq.setSessionKeyBase64(CommonStaticUtil.Base64Encoder.encodeToString(sessionKeyBytesOfServer));
		memberRegisterReq.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(ivBytesOfServer));
		memberRegisterReq.setIp(req.getRemoteAddr());

		AbstractMessage memberRegisterOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), memberRegisterReq);					
		if (! (memberRegisterOutputMessage instanceof MessageResultRes)) {
			String errorMessage = "회원 가입이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(memberRegisterReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(memberRegisterOutputMessage.toString())
					.append("] 도착").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}
		
		MessageResultRes messageResultRes = (MessageResultRes)memberRegisterOutputMessage;
		
		return messageResultRes;
	}

}
