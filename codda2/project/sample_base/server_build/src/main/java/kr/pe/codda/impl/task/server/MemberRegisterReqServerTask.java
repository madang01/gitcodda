package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.inner_message.MemberRegisterDecryptionReq;
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PasswordPairOfMemberTable;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MemberRegisterReqServerTask extends AbstractServerTask implements DBAutoCommitTaskIF<MemberRegisterDecryptionReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);
	
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
		
		
		MemberRegisterDecryptionReq memberRegisterDecryptionReq = new MemberRegisterDecryptionReq();
		memberRegisterDecryptionReq.setMemberRoleType(MemberRoleType.MEMBER);
		memberRegisterDecryptionReq.setUserID(userID);
		memberRegisterDecryptionReq.setNickname(nickname);
		memberRegisterDecryptionReq.setEmail(email);
		memberRegisterDecryptionReq.setPasswordBytes(passwordBytes);
		memberRegisterDecryptionReq.setRegisteredDate(new java.sql.Timestamp(System.currentTimeMillis()));
		memberRegisterDecryptionReq.setIp(ip);

		MessageResultRes messageResultRes = ServerDBUtil.execute(dbcpName, this, memberRegisterDecryptionReq);

		return messageResultRes;
	}
	
	public MessageResultRes doWork(String dbcpName, MemberRegisterDecryptionReq memberRegisterDecryptionReq) throws Exception {
		MessageResultRes messageResultRes = ServerDBUtil.execute(dbcpName, this, memberRegisterDecryptionReq);
		
		return messageResultRes;
	}

	@Override
	public MessageResultRes doWork(DSLContext dsl, MemberRegisterDecryptionReq memberRegisterDecryptionReq) throws Exception {
		
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == memberRegisterDecryptionReq) {
			throw new ParameterServerTaskException("the parameter memberRegisterDecryptionReq is null");
		}
		
		
		MemberRoleType memberRoleType = memberRegisterDecryptionReq.getMemberRoleType();
		String userID = memberRegisterDecryptionReq.getUserID();
		String nickname = memberRegisterDecryptionReq.getNickname();
		String email = memberRegisterDecryptionReq.getEmail();
		byte[] passwordBytes = memberRegisterDecryptionReq.getPasswordBytes();
		Timestamp registeredDate = memberRegisterDecryptionReq.getRegisteredDate();
		String ip = memberRegisterDecryptionReq.getIp();	
		String messageID = memberRegisterDecryptionReq.getMessageID();
		
		if (null == memberRoleType) {
			String errorMessage = "the parameter memberRoleType is null";
			throw new ServerTaskException(errorMessage);
		}

		try {
			ValueChecker.checkValidUserID(userID);
			ValueChecker.checkValidNickname(nickname);
			ValueChecker.checkValidEmail(email);
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			ValueChecker.checkValidIP(ip);
		} catch (IllegalArgumentException e) {
			throw new ParameterServerTaskException(e.getMessage());
		}

		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			/** dead code */
			log.error("NoSuchAlgorithmException", e);
			System.exit(1);
		}
		byte[] pwdSaltBytes = new byte[8];
		random.nextBytes(pwdSaltBytes);

		PasswordPairOfMemberTable passwordPairOfMemberTable = ServerDBUtil.toPasswordPairOfMemberTable(passwordBytes, pwdSaltBytes);
		
		
			boolean isSameIDMember = dsl.fetchExists(
					dsl.select(SB_MEMBER_TB.USER_ID).from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(userID)));

			if (isSameIDMember) {
				String errorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다").toString();
				throw new RollbackServerTaskException(errorMessage);
			}

			boolean isSameNicknameMember = dsl.fetchExists(
					dsl.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB).where(SB_MEMBER_TB.NICKNAME.eq(nickname)));

			if (isSameNicknameMember) {
				String errorMessage = new StringBuilder("기존 회원과 중복되는 별명[").append(nickname).append("] 입니다").toString();
				throw new RollbackServerTaskException(errorMessage);
			}

			int resultOfInsert = dsl.insertInto(SB_MEMBER_TB).set(SB_MEMBER_TB.USER_ID, userID)
					.set(SB_MEMBER_TB.NICKNAME, nickname)
					.set(SB_MEMBER_TB.PWD_BASE64, passwordPairOfMemberTable.getPasswordBase64())
					.set(SB_MEMBER_TB.PWD_SALT_BASE64, passwordPairOfMemberTable.getPasswordSaltBase64())
					.set(SB_MEMBER_TB.ROLE, memberRoleType.getValue())
					.set(SB_MEMBER_TB.STATE, MemberStateType.OK.getValue()).set(SB_MEMBER_TB.EMAIL, email)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0)).set(SB_MEMBER_TB.REG_DT, registeredDate)
					.set(SB_MEMBER_TB.LAST_NICKNAME_MOD_DT, registeredDate)
					.set(SB_MEMBER_TB.LAST_EMAIL_MOD_DT, registeredDate)
					.set(SB_MEMBER_TB.LAST_PWD_MOD_DT, registeredDate)
					.set(SB_MEMBER_TB.LAST_STATE_MOD_DT, registeredDate)
					.set(SB_MEMBER_TB.NEXT_ACTIVE_SQ, UInteger.valueOf(0)).execute();

			if (0 == resultOfInsert) {
				String errorMessage = "회원 등록하는데 실패하였습니다";
				throw new RollbackServerTaskException(errorMessage);
			}

			String logText = new StringBuilder().append("회원 가입 신청 아이디[").append(userID).append("], 회원 종류[")
					.append(memberRoleType.getName()).append("]").toString();

			ServerDBUtil.insertSiteLog(dsl, userID, logText, registeredDate, ip);
		
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(messageID);
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage("회원 가입 성공하였습니다");

		return messageResultRes;
	}
}
