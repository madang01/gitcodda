package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.inner_message.MemberLoginInnerReq;
import kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq;
import kr.pe.codda.impl.message.MemberLoginRes.MemberLoginRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.CommitServerTaskException;
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

/**
 * 회원 로그인 서버 비지니스 로직 클래스
 * 
 * @author Won Jonghoon
 *
 */
public class MemberLoginReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MemberLoginInnerReq, MemberLoginRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public MemberLoginReqServerTask() throws DynamicClassCallException {
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

		AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
				(MemberLoginReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	/**
	 * 암호문인 로그인 입력 메시지를 받아 로그인 성공시 로그인 로그인 처리후 로그인 성공 출력 메시지를 반환한다, 로그인 실패시 예외를 던진다.
	 * 
	 * @param dbcpName dbcp 이름
	 * @param memberLoginReq 암호문인 로그인 입력 메시지
	 * @return 로그인 성공 출력 메시지
	 * @throws Exception 로그인 실패 혹은 에러 발생시 던지는 예외
	 */
	public MemberLoginRes doWork(final String dbcpName, MemberLoginReq memberLoginReq) throws Exception {
		String idCipherBase64 = memberLoginReq.getIdCipherBase64();
		String pwdCipherBase64 = memberLoginReq.getPwdCipherBase64();
		String sessionKeyBase64 = memberLoginReq.getSessionKeyBase64();
		String ivBase64 = memberLoginReq.getIvBase64();

		if (null == idCipherBase64) {
			String errorMessage = "아이디를 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		if (null == pwdCipherBase64) {
			String errorMessage = "비밀번호를 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		if (null == sessionKeyBase64) {
			String errorMessage = "세션키를 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		if (null == ivBase64) {
			String errorMessage = "세션키 소금값을 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		try {
			ValueChecker.checkValidIP(memberLoginReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		byte[] idCipherBytes = null;
		byte[] pwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;

		try {
			idCipherBytes = CommonStaticUtil.Base64Decoder.decode(idCipherBase64);
		} catch (Exception e) {
			String errorMessage = "아이디 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ParameterServerTaskException(errorMessage);
		}

		try {
			pwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(pwdCipherBase64);
		} catch (Exception e) {
			String errorMessage = "비밀번호 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ParameterServerTaskException(errorMessage);
		}
		try {
			sessionKeyBytes = CommonStaticUtil.Base64Decoder.decode(sessionKeyBase64);
		} catch (Exception e) {
			String errorMessage = "세션키는 베이스64로 인코딩되지 않았습니다";
			throw new ParameterServerTaskException(errorMessage);
		}

		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(ivBase64);
		} catch (Exception e) {
			String errorMessage = "세션키 소금값은 베이스64로 인코딩되지 않았습니다";
			throw new ParameterServerTaskException(errorMessage);
		}

		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyIF serverSessionkey = ServerSessionkeyManager.getInstance()
					.getMainProjectServerSessionkey();

			serverSymmetricKey = serverSessionkey.createNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);

		} catch (IllegalArgumentException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		}

		final String memberID;

		try {
			memberID = getDecryptedString(idCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		}

		final byte[] passwordBytes;

		try {
			passwordBytes = serverSymmetricKey.decrypt(pwdCipherBytes);
		} catch (IllegalArgumentException e) {

			String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		}

		MemberLoginInnerReq memberLoginInnerReq = new MemberLoginInnerReq();

		memberLoginInnerReq.setMemberID(memberID);
		memberLoginInnerReq.setPassword(passwordBytes);
		memberLoginInnerReq.setIp(memberLoginReq.getIp());

		MemberLoginRes outputMessage = ServerDBUtil.doDBAutoTransationWork(dbcpName, this, memberLoginInnerReq);

		return outputMessage;
	}
	
	/**
	 * 단위 테스트용 메소드로 복호화된 로그인 입력 메시지를 입력 받아 로그인 처리후 로그인 성공 출력 메시지를 반환한다, 로그인 실패시 예외를 던진다.
	 * 
	 * @param dbcpName dbcp 이름
	 * @param memberLoginInnerReq 복호화된 로그인 입력 메시지
	 * @return 로그인 성공 출력 메시지
	 * @throws Exception 로그인 실패 혹은 에러 발생시 던지는 예외
	 */
	public MemberLoginRes doWork(final String dbcpName, MemberLoginInnerReq memberLoginInnerReq) throws Exception {
		MemberLoginRes outputMessage = ServerDBUtil.doDBAutoTransationWork(dbcpName, this, memberLoginInnerReq);
		
		return outputMessage;
	}

	@Override
	public MemberLoginRes doWork(final DSLContext dsl, MemberLoginInnerReq memberLoginInnerReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == memberLoginInnerReq) {
			throw new ParameterServerTaskException("the parameter memberLoginInnerReq is null");
		}
		
		
		// FIXME!
		log.info(memberLoginInnerReq.toString());

		final String memberID = memberLoginInnerReq.getMemberID();
		final byte[] passwordBytes = memberLoginInnerReq.getPassword();
		final String ip = memberLoginInnerReq.getIp();

		try {
			ValueChecker.checkValidUserID(memberID);
			ValueChecker.checkValidLoginPwd(passwordBytes);
			ValueChecker.checkValidIP(ip);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}		

		Record6<String, Byte, Byte, UByte, String, String> memberRecord = dsl
				.select(SB_MEMBER_TB.NICKNAME, SB_MEMBER_TB.ROLE, SB_MEMBER_TB.STATE, SB_MEMBER_TB.PWD_FAIL_CNT,
						SB_MEMBER_TB.PWD_BASE64, SB_MEMBER_TB.PWD_SALT_BASE64)
				.from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(memberID)).forUpdate().fetchOne();

		if (null == memberRecord) {

			String errorMessage = new StringBuilder("아이디[").append(memberID).append("]가 존재하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		String nickname = memberRecord.get(SB_MEMBER_TB.NICKNAME);
		byte memberRole = memberRecord.get(SB_MEMBER_TB.ROLE);
		byte memberState = memberRecord.get(SB_MEMBER_TB.STATE);
		short pwdFailedCount = memberRecord.get(SB_MEMBER_TB.PWD_FAIL_CNT).shortValue();
		String pwdBase64 = memberRecord.get(SB_MEMBER_TB.PWD_BASE64);
		String pwdSaltBase64 = memberRecord.get(SB_MEMBER_TB.PWD_SALT_BASE64);

		MemberRoleType memberRoleType = null;

		try {
			memberRoleType = MemberRoleType.valueOf(memberRole);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("회원[").append(memberID).append("]의 멤버 구분[").append(memberRole)
					.append("]이 잘못되었습니다").toString();

			// log.warn(errorMessage);

			throw new RollbackServerTaskException(errorMessage);
		}

		if (MemberRoleType.GUEST.equals(memberRoleType)) {
			String errorMessage = "손님은 로그인 할 수 없습니다";

			ServerDBUtil.insertSiteLog(dsl, memberID,
					new StringBuilder().append("[경고] 손님 아이디[").append(memberID).append("]로 로그인 시도").toString(),
					new java.sql.Timestamp(System.currentTimeMillis()), ip);

			throw new CommitServerTaskException(errorMessage);
		}

		MemberStateType memberStateType = null;
		try {
			memberStateType = MemberStateType.valueOf(memberState);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("회원[").append(memberID).append("]의 멤버 상태[").append(memberState)
					.append("]가 잘못되었습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (memberStateType.equals(MemberStateType.BLOCK)) {

			String errorMessage = new StringBuilder("차단된 회원[").append(memberID).append("] 입니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		} else if (memberStateType.equals(MemberStateType.WITHDRAWAL)) {

			String errorMessage = new StringBuilder("탈퇴한 회원[").append(memberID).append("] 입니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES <= pwdFailedCount) {

			String errorMessage = new StringBuilder("최대 허용된 횟수[")
					.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
					.append("]까지 비밀 번호가 틀려 더 이상 로그인 하실 수 없습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte[] pwdSaltBytes = CommonStaticUtil.Base64Decoder.decode(pwdSaltBase64);

		PasswordPairOfMemberTable passwordPairOfMemberTable = ServerDBUtil.toPasswordPairOfMemberTable(passwordBytes,
				pwdSaltBytes);

		if (!pwdBase64.equals(passwordPairOfMemberTable.getPasswordBase64())) {
			/*
			 * update SB_MEMBER_TB set pwd_fail_cnt=#{pwdFailCount}, mod_dt=sysdate() where
			 * user_id=#{userId} and member_gb=1 and member_st=0
			 */
			int countOfPwdFailedCountUpdate = dsl.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount + 1))
					.where(SB_MEMBER_TB.USER_ID.eq(memberID)).execute();

			if (0 == countOfPwdFailedCountUpdate) {

				String errorMessage = "비밀 번호 실패 횟수 갱신이 실패하였습니다";
				throw new RollbackServerTaskException(errorMessage);
			}

			String errorMessage = new StringBuilder().append(pwdFailedCount + 1).append(" 회 비밀 번호가 틀렸습니다").toString();

			ServerDBUtil.insertSiteLog(dsl, memberID, errorMessage, new java.sql.Timestamp(System.currentTimeMillis()),
					ip);

			throw new CommitServerTaskException(errorMessage);
		}

		if (pwdFailedCount > 0) {
			dsl.update(SB_MEMBER_TB).set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0))
					.where(SB_MEMBER_TB.USER_ID.eq(memberID)).execute();
		}

		ServerDBUtil.insertSiteLog(dsl, memberID,
				new StringBuilder().append(memberRoleType.getName()).append(" 로그인").toString(),
				new java.sql.Timestamp(System.currentTimeMillis()), ip);

		final MemberLoginRes memberLoginRes = new MemberLoginRes();
		memberLoginRes.setUserID(memberID);
		memberLoginRes.setUserName(nickname);
		memberLoginRes.setMemberRole(memberRoleType.getValue());

		return memberLoginRes;
	}
}
