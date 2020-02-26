package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.inner_message.MemberPasswordChangeDecryptionReq;
import kr.pe.codda.impl.message.MemberPasswordChangeReq.MemberPasswordChangeReq;
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

/**
 * 회원 비밀번호 변경 서버 비지니스 로직 클래스
 * @author Won Jonghoon
 *
 */
public class MemberPasswordChangeReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MemberPasswordChangeDecryptionReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(MemberPasswordChangeReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
				(MemberPasswordChangeReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public MessageResultRes doWork(String dbcpName, MemberPasswordChangeReq memberPasswordChangeReq) throws Exception {

		if (null == dbcpName) {
			throw new ParameterServerTaskException("the parameter dbcpName is null");
		}

		if (null == memberPasswordChangeReq) {
			throw new ParameterServerTaskException("the parameter memberPasswordChangeReq is null");
		}

		// FIXME!
		log.info(memberPasswordChangeReq.toString());

		String oldPwdCipherBase64 = memberPasswordChangeReq.getOldPwdCipherBase64();
		String newPwdCipherBase64 = memberPasswordChangeReq.getNewPwdCipherBase64();
		String sessionKeyBase64 = memberPasswordChangeReq.getSessionKeyBase64();
		String ivBase64 = memberPasswordChangeReq.getIvBase64();

		if (null == oldPwdCipherBase64) {
			String errorMessage = "변경전 비밀번호를 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		if (null == newPwdCipherBase64) {
			String errorMessage = "변경후 비밀번호를 입력해 주세요";
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

		byte[] oldPwdCipherBytes = null;
		byte[] newPwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;

		try {
			oldPwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(oldPwdCipherBase64);
		} catch (Exception e) {
			String errorMessage = "변경전 비밀번호 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ParameterServerTaskException(errorMessage);
		}

		try {
			newPwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(newPwdCipherBase64);
		} catch (Exception e) {
			String errorMessage = "비밀번호 암호문은 베이스64로인코딩되지 않았습니다";
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

		final byte[] oldPasswordBytes;
		try {
			oldPasswordBytes = serverSymmetricKey.decrypt(oldPwdCipherBytes);
		} catch (IllegalArgumentException e) {

			String errorMessage = "변경전 비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "변경전 비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		}

		final byte[] newPasswordBytes;

		try {
			newPasswordBytes = serverSymmetricKey.decrypt(newPwdCipherBytes);
		} catch (IllegalArgumentException e) {

			String errorMessage = "변경후 비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "변경후 비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		}

		MemberPasswordChangeDecryptionReq memberPasswordChangeDecryptionReq = new MemberPasswordChangeDecryptionReq();
		memberPasswordChangeDecryptionReq.setRequestedUserID(memberPasswordChangeReq.getRequestedUserID());
		memberPasswordChangeDecryptionReq.setNewPasswordBytes(newPasswordBytes);
		memberPasswordChangeDecryptionReq.setOldPasswordBytes(oldPasswordBytes);
		memberPasswordChangeDecryptionReq.setIp(memberPasswordChangeReq.getIp());

		MessageResultRes outputMessage = ServerDBUtil.execute(dbcpName, this,
				memberPasswordChangeDecryptionReq);

		return outputMessage;
	}
	

	public MessageResultRes doWork(String dbcpName, MemberPasswordChangeDecryptionReq memberPasswordChangeDecryptionReq) throws Exception {
		MessageResultRes outputMessage = ServerDBUtil.execute(dbcpName, this,
				memberPasswordChangeDecryptionReq);

		return outputMessage;
	}

	@Override
	public MessageResultRes doWork(final DSLContext dsl, MemberPasswordChangeDecryptionReq memberPasswordChangeDecryptionReq)
			throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == memberPasswordChangeDecryptionReq) {
			throw new ParameterServerTaskException("the parameter memberPasswordChangeDecryptionReq is null");
		}

		String requestedUserID = memberPasswordChangeDecryptionReq.getRequestedUserID();
		byte[] oldPasswordBytes = memberPasswordChangeDecryptionReq.getOldPasswordBytes();
		byte[] newPasswordBytes = memberPasswordChangeDecryptionReq.getNewPasswordBytes();
		String ip = memberPasswordChangeDecryptionReq.getIp();

		try {
			ValueChecker.checkValidRequestedUserID(requestedUserID);
			ValueChecker.checkValidOldPwd(oldPasswordBytes);
			ValueChecker.checkValidOldPwd(newPasswordBytes);
			ValueChecker.checkValidIP(ip);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final StringBuilder resultMessageStringBuilder = new StringBuilder();

		Record6<String, Byte, Byte, UByte, String, String> memberRecord = dsl
				.select(SB_MEMBER_TB.NICKNAME, SB_MEMBER_TB.ROLE, SB_MEMBER_TB.STATE, SB_MEMBER_TB.PWD_FAIL_CNT,
						SB_MEMBER_TB.PWD_BASE64, SB_MEMBER_TB.PWD_SALT_BASE64)
				.from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(memberPasswordChangeDecryptionReq.getRequestedUserID()))
				.forUpdate().fetchOne();

		if (null == memberRecord) {
			String errorMessage = new StringBuilder("비밀번호 변경 요청자[")
					.append(memberPasswordChangeDecryptionReq.getRequestedUserID()).append("]가 존재하지 않습니다").toString();
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

			String errorMessage = new StringBuilder("회원[").append(memberPasswordChangeDecryptionReq.getRequestedUserID())
					.append("]의 멤버 구분[").append(memberRole).append("]이 잘못되었습니다").toString();

			// log.warn(errorMessage);

			throw new RollbackServerTaskException(errorMessage);
		}

		if (MemberRoleType.GUEST.equals(memberRoleType)) {

			String errorMessage = "비밀번호 변경은 회원만 가능합니다";

			throw new RollbackServerTaskException(errorMessage);
		}

		MemberStateType memberStateType = null;
		try {
			memberStateType = MemberStateType.valueOf(memberState);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("비밀번호 변경 요청자[")
					.append(memberPasswordChangeDecryptionReq.getRequestedUserID()).append("]의 멤버 상태[").append(memberState)
					.append("]가 잘못되었습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (!MemberStateType.OK.equals(memberStateType)) {

			String errorMessage = new StringBuilder().append("비밀번호 변경 요청자[")
					.append(memberPasswordChangeDecryptionReq.getRequestedUserID()).append("] 상태[")
					.append(memberStateType.getName()).append("]가 정상이 아닙니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES <= pwdFailedCount) {

			String errorMessage = new StringBuilder("최대 비밀번호 실패 횟수[")
					.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
					.append("] 이상으로 비밀번호가 틀렸습니다, 관리자를 통해 비밀번호 초기화를 해 주세요").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte[] oldPwdSaltBytes = CommonStaticUtil.Base64Decoder.decode(pwdSaltBase64);

		PasswordPairOfMemberTable oldPasswordPairOfMemberTable = ServerDBUtil
				.toPasswordPairOfMemberTable(oldPasswordBytes, oldPwdSaltBytes);

		if (!pwdBase64.equals(oldPasswordPairOfMemberTable.getPasswordBase64())) {
			int countOfPwdFailedCountUpdate = dsl.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount + 1))
					.where(SB_MEMBER_TB.USER_ID.eq(memberPasswordChangeDecryptionReq.getRequestedUserID())).execute();

			if (0 == countOfPwdFailedCountUpdate) {

				String errorMessage = "비밀 번호 실패 횟수 갱신이 실패하였습니다";
				throw new RollbackServerTaskException(errorMessage);
			}

			ServerDBUtil.insertSiteLog(dsl, memberPasswordChangeDecryptionReq.getRequestedUserID(), "비밀번호 변경 비밀번호 틀림",
					new java.sql.Timestamp(System.currentTimeMillis()), memberPasswordChangeDecryptionReq.getIp());

			String errorMessage = "비밀 번호가 틀렸습니다";
			throw new CommitServerTaskException(errorMessage);
		}

		Timestamp lastPwdModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());

		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			/** dead code */
			log.error("NoSuchAlgorithmException", e);
			System.exit(1);
		}

		byte[] newPwdSaltBytes = new byte[8];
		random.nextBytes(newPwdSaltBytes);

		PasswordPairOfMemberTable newPasswordPairOfMemberTable = ServerDBUtil
				.toPasswordPairOfMemberTable(newPasswordBytes, newPwdSaltBytes);

		dsl.update(SB_MEMBER_TB).set(SB_MEMBER_TB.PWD_BASE64, newPasswordPairOfMemberTable.getPasswordBase64())
				.set(SB_MEMBER_TB.PWD_SALT_BASE64, newPasswordPairOfMemberTable.getPasswordSaltBase64())
				.set(SB_MEMBER_TB.LAST_PWD_MOD_DT, lastPwdModifiedDate)
				.where(SB_MEMBER_TB.USER_ID.eq(memberPasswordChangeDecryptionReq.getRequestedUserID())).execute();

		ServerDBUtil.insertSiteLog(dsl, memberPasswordChangeDecryptionReq.getRequestedUserID(), "비밀번호 변경 완료",
				lastPwdModifiedDate, memberPasswordChangeDecryptionReq.getIp());

		resultMessageStringBuilder.append(nickname).append("님의 비밀번호 변경 처리가 완료되었습니다");

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(memberPasswordChangeDecryptionReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(resultMessageStringBuilder.toString());

		return messageResultRes;

	}
}
