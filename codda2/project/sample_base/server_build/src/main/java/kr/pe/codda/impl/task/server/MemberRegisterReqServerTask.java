package kr.pe.codda.impl.task.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MemberRegisterReqServerTask extends AbstractServerTask {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);
	
	public MemberRegisterReqServerTask() throws DynamicClassCallException {
		super();
	}

	private String getDecryptedString(byte[] cipherBytes, ServerSymmetricKeyIF serverSymmetricKey)
			throws InterruptedException, IllegalArgumentException, SymmetricException {
		byte[] valueBytes = serverSymmetricKey.decrypt(cipherBytes);
		String decryptedString = new String(valueBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		return decryptedString;
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (MemberRegisterReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public MessageResultRes doWork(String dbcpName, MemberRegisterReq memberRegisterReq) throws Exception {
		// FIXME!
		log.info(memberRegisterReq.toString());

		String idCipherBase64 = memberRegisterReq.getIdCipherBase64();
		String pwdCipherBase64 = memberRegisterReq.getPwdCipherBase64();
		String nicknameCipherBase64 = memberRegisterReq.getNicknameCipherBase64();
		String emailCipherBase64 = memberRegisterReq.getEmailCipherBase64();
		String sessionKeyBase64 = memberRegisterReq.getSessionKeyBase64();
		String ivBase64 = memberRegisterReq.getIvBase64();
		String ip = memberRegisterReq.getIp();

		if (null == idCipherBase64) {
			String errorMessage = "아이디를 입력해 주세요";
			throw new ServerTaskException(errorMessage);
		}

		if (null == pwdCipherBase64) {
			String errorMessage = "비밀번호를 입력해 주세요";
			throw new ServerTaskException(errorMessage);
		}

		if (null == nicknameCipherBase64) {
			String errorMessage = "별명을 입력해 주세요";
			throw new ServerTaskException(errorMessage);
		}

		if (null == emailCipherBase64) {
			String errorMessage = "이메일을 입력해 주세요";
			throw new ServerTaskException(errorMessage);
		}


		if (null == sessionKeyBase64) {
			String errorMessage = "세션키를 입력해 주세요";
			throw new ServerTaskException(errorMessage);
		}

		if (null == ivBase64) {
			String errorMessage = "세션키 소금값을 입력해 주세요";
			throw new ServerTaskException(errorMessage);
		}
		
		/*
		 * try { ValueChecker.checkValidIP(ip); } catch (IllegalArgumentException e) {
		 * String errorMessage = e.getMessage(); throw new
		 * ServerTaskException(errorMessage); }
		 */

		byte[] idCipherBytes = null;
		byte[] pwdCipherBytes = null;
		byte[] nicknameCipherBytes = null;
		byte[] emailCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;

		try {
			idCipherBytes = CommonStaticUtil.Base64Decoder.decode(idCipherBase64);
		} catch (Exception e) {
			String errorMessage = "아이디 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ServerTaskException(errorMessage);
		}

		try {
			pwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(pwdCipherBase64);
		} catch (Exception e) {
			String errorMessage = "비밀번호 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ServerTaskException(errorMessage);
		}

		try {
			nicknameCipherBytes = CommonStaticUtil.Base64Decoder.decode(nicknameCipherBase64);
		} catch (Exception e) {
			String errorMessage = "별명은 베이스64로 인코딩되지 않았습니다";
			throw new ServerTaskException(errorMessage);
		}

		try {
			emailCipherBytes = CommonStaticUtil.Base64Decoder.decode(emailCipherBase64);
		} catch (Exception e) {
			String errorMessage = "비밀번호 분실 힌트는 베이스64로 인코딩되지 않았습니다";
			throw new ServerTaskException(errorMessage);
		}


		try {
			sessionKeyBytes = CommonStaticUtil.Base64Decoder.decode(sessionKeyBase64);
		} catch (Exception e) {
			String errorMessage = "세션키는 베이스64로 인코딩되지 않았습니다";
			throw new ServerTaskException(errorMessage);
		}

		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(ivBase64);
		} catch (Exception e) {
			String errorMessage = "세션키 소금값은 베이스64로 인코딩되지 않았습니다";
			throw new ServerTaskException(errorMessage);
		}

		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyIF serverSessionkey = ServerSessionkeyManager.getInstance()
					.getMainProjectServerSessionkey();
			serverSymmetricKey = serverSessionkey.createNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 대칭키 생성 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "대칭키 생성 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알수 없는 이유로 대칭키 생성 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "대칭키 생성 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		}

		String userID = null;
		String nickname = null;
		String email = null;		 

		try {
			userID = getDecryptedString(idCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 아이디 복호화 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "아이디 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알 수 없는 에러로 인한 아이디 복호화 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "아이디 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		}		

		try {
			nickname = getDecryptedString(nicknameCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 별명 복호화 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "별명 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알 수 없는 에러로 인한 별명 복호화 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "별명 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		}		

		try {
			email = getDecryptedString(emailCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 비밀번호 힌트 복호화 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "비밀번호 힌트 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알 수 없는 에러로 인한 비밀번호 힌트 복호화 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "비밀번호 힌트 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		}
		

		byte[] passwordBytes = null;
		try {
			passwordBytes = serverSymmetricKey.decrypt(pwdCipherBytes);
		} catch (IllegalArgumentException e) {
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 비밀번호 복호화 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "비밀번호 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알 수 없는 에러로 인한 비밀번호 복호화 실패, ")
					.append(memberRegisterReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "비밀번호 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerTaskException(errorMessage);
		}

		ServerDBUtil.registerMember(dbcpName, MemberRoleType.MEMBER, userID, nickname, email, passwordBytes,
				new java.sql.Timestamp(System.currentTimeMillis()), ip);
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(memberRegisterReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage("회원 가입 성공하였습니다");

		return messageResultRes;
	}
}
