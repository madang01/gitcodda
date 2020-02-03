package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record5;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.inner_message.MemberWithdrawInnerReq;
import kr.pe.codda.impl.message.MemberWithdrawReq.MemberWithdrawReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
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

public class MemberWithdrawReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MemberWithdrawInnerReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public MemberWithdrawReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
				(MemberWithdrawReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public MessageResultRes doWork(String dbcpName, MemberWithdrawReq memberWithdrawReq) throws Exception {
		// FIXME!
		log.info(memberWithdrawReq.toString());

		String pwdCipherBase64 = memberWithdrawReq.getPwdCipherBase64();
		String sessionKeyBase64 = memberWithdrawReq.getSessionKeyBase64();
		String ivBase64 = memberWithdrawReq.getIvBase64();

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

		byte[] pwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;

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
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 대칭키 생성 실패, ")
					.append(memberWithdrawReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "대칭키 생성 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ParameterServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알수 없는 이유로 대칭키 생성 실패, ")
					.append(memberWithdrawReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "대칭키 생성 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ParameterServerTaskException(errorMessage);
		}

		byte[] passwordBytes = null;
		try {
			passwordBytes = serverSymmetricKey.decrypt(pwdCipherBytes);
		} catch (IllegalArgumentException e) {
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 비밀번호 복호화 실패, ")
					.append(memberWithdrawReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "비밀번호 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ParameterServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알 수 없는 에러로 인한 비밀번호 복호화 실패, ")
					.append(memberWithdrawReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "비밀번호 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ParameterServerTaskException(errorMessage);
		}

		MemberWithdrawInnerReq memberWithdrawInnerReq = new MemberWithdrawInnerReq();
		memberWithdrawInnerReq.setRequestedUserID(memberWithdrawReq.getRequestedUserID());
		memberWithdrawInnerReq.setIp(memberWithdrawReq.getIp());
		memberWithdrawInnerReq.setPasswordBytes(passwordBytes);

		MessageResultRes outputMessage = ServerDBUtil
				.execute(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, memberWithdrawInnerReq);

		return outputMessage;
	}

	@Override
	public MessageResultRes doWork(final DSLContext dsl, final MemberWithdrawInnerReq memberWithdrawInnerReq) throws Exception {
		
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == memberWithdrawInnerReq) {
			throw new ParameterServerTaskException("the parameter memberWithdrawInnerReq is null");
		}

		final String requestedUserID = memberWithdrawInnerReq.getRequestedUserID();
		final String ip = memberWithdrawInnerReq.getIp();
		final byte[] passwordBytes = memberWithdrawInnerReq.getPasswordBytes();
		final String messageID = memberWithdrawInnerReq.getMessageID();

		try {
			ValueChecker.checkValidRequestedUserID(requestedUserID);
			ValueChecker.checkValidIP(ip);
			ValueChecker.checkValidLoginPwd(passwordBytes);			
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		/** 탈퇴 대상 회원 레코드 락 */
		Record5<Byte, Byte, UByte, String, String> memberRecordOfRequestedUserID = dsl
				.select(SB_MEMBER_TB.ROLE, SB_MEMBER_TB.STATE, SB_MEMBER_TB.PWD_FAIL_CNT, SB_MEMBER_TB.PWD_BASE64,
						SB_MEMBER_TB.PWD_SALT_BASE64)
				.from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).forUpdate().fetchOne();

		if (null == memberRecordOfRequestedUserID) {
			String errorMessage = new StringBuilder("회원 탈퇴 요청자[").append(requestedUserID).append("]가 회원 테이블에 존재하지 않습니다")
					.toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte memberRole = memberRecordOfRequestedUserID.getValue(SB_MEMBER_TB.ROLE);
		byte memberState = memberRecordOfRequestedUserID.get(SB_MEMBER_TB.STATE);
		short pwdFailedCount = memberRecordOfRequestedUserID.get(SB_MEMBER_TB.PWD_FAIL_CNT).shortValue();
		String pwdBase64 = memberRecordOfRequestedUserID.get(SB_MEMBER_TB.PWD_BASE64);
		String pwdSaltBase64 = memberRecordOfRequestedUserID.get(SB_MEMBER_TB.PWD_SALT_BASE64);

		MemberRoleType memberRoleType = null;
		try {
			memberRoleType = MemberRoleType.valueOf(memberRole);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("회원 탈퇴 요청자[").append(requestedUserID).append("]의 멤버 역활 유형[")
					.append(memberRole).append("]이 잘못되어있습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (!MemberRoleType.MEMBER.equals(memberRoleType)) {

			String errorMessage = new StringBuilder().append("회원 탈퇴 요청자[역활:").append(memberRoleType.getName())
					.append("]가 일반 회원이 아닙니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		MemberStateType memberStateTypeOfRequestedUserID = null;
		try {
			memberStateTypeOfRequestedUserID = MemberStateType.valueOf(memberState);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("회원 탈퇴 요청자[").append(requestedUserID).append("]의 상태[")
					.append(memberState).append("]가 잘못 되어 있습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (!MemberStateType.OK.equals(memberStateTypeOfRequestedUserID)) {

			String errorMessage = new StringBuilder("탈퇴 대상 사용자[").append(requestedUserID).append("]의 상태[")
					.append(memberStateTypeOfRequestedUserID.getName()).append("]가 정상이 아닙니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES <= pwdFailedCount) {

			String errorMessage = new StringBuilder("최대 비밀번호 실패 횟수[")
					.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
					.append("] 이상으로 비밀번호가 틀려 탈퇴하실 수 없습니다, 먼저 비밀번호 찾기 혹은 관리자를 통해 비밀번호 최대 실패 횟수를 초기화 하시기 바랍니다")
					.toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte[] pwdSaltBytes = CommonStaticUtil.Base64Decoder.decode(pwdSaltBase64);

		PasswordPairOfMemberTable passwordPairOfMemberTable = ServerDBUtil.toPasswordPairOfMemberTable(passwordBytes,
				pwdSaltBytes);

		if (!pwdBase64.equals(passwordPairOfMemberTable.getPasswordBase64())) {
			int countOfPwdFailedCountUpdate = dsl.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount + 1))
					.where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).execute();

			if (0 == countOfPwdFailedCountUpdate) {

				String errorMessage = "비밀 번호 실패 횟수 갱신이 실패하였습니다";
				throw new RollbackServerTaskException(errorMessage);
			}

			ServerDBUtil.insertSiteLog(dsl, requestedUserID, "회원 탈퇴 비밀번호 틀림",
					new java.sql.Timestamp(System.currentTimeMillis()), ip);

			String errorMessage = "비밀 번호가 틀렸습니다";
			throw new CommitServerTaskException(errorMessage);
		}

		Timestamp lastStateModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());

		dsl.update(SB_MEMBER_TB).set(SB_MEMBER_TB.STATE, MemberStateType.WITHDRAWAL.getValue())
				.set(SB_MEMBER_TB.LAST_STATE_MOD_DT, lastStateModifiedDate)
				.where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).execute();

		ServerDBUtil.insertSiteLog(dsl, requestedUserID, "회원 탈퇴 완료", lastStateModifiedDate, ip);

		String successResultMessage = new StringBuilder()
				.append("회원 '").append(requestedUserID)
				.append("' 님의 탈퇴 처리가 완료되었습니다").toString();

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(messageID);
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(successResultMessage);

		return messageResultRes;
	}
}
