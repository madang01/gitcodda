package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.inner_message.AccountSearchProcessDecryptionReq;
import kr.pe.codda.impl.message.AccountSearchProcessReq.AccountSearchProcessReq;
import kr.pe.codda.impl.message.AccountSearchProcessRes.AccountSearchProcessRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.AccountSearchType;
import kr.pe.codda.server.lib.CommitServerTaskException;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PasswordPairOfMemberTable;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class AccountSearchProcessReqServerTask extends AbstractServerTask implements DBAutoCommitTaskIF<AccountSearchProcessDecryptionReq, AccountSearchProcessRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);


	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {		
		
		AccountSearchProcessReq accountSearchProcessReq = (AccountSearchProcessReq)inputMessage;
		log.info(accountSearchProcessReq.toString());

		AccountSearchType accountSearchType = null;
		try {
			accountSearchType = AccountSearchType.valueOf(accountSearchProcessReq.getAccountSearchType());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		String emailCipherBase64 = accountSearchProcessReq.getEmailCipherBase64();
		String secretAuthenticationValueCipherBase64 = accountSearchProcessReq
				.getSecretAuthenticationValueCipherBase64();
		String newPwdCipherBase64 = accountSearchProcessReq.getNewPwdCipherBase64();
		String sessionKeyBase64 = accountSearchProcessReq.getSessionKeyBase64();
		String ivBase64 = accountSearchProcessReq.getIvBase64();

		if (null == emailCipherBase64) {
			String errorMessage = "이메일를 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		if (null == secretAuthenticationValueCipherBase64) {
			String errorMessage = "아이디/비밀버호 찾기용 비밀 값을 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			if (null == newPwdCipherBase64) {
				String errorMessage = "비밀번호를 입력해 주세요";
				throw new ParameterServerTaskException(errorMessage);
			}
		}

		if (null == sessionKeyBase64) {
			String errorMessage = "세션키를 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		if (null == ivBase64) {
			String errorMessage = "세션키 소금값을 입력해 주세요";
			throw new ParameterServerTaskException(errorMessage);
		}

		byte[] emailCipherBytes = null;
		byte[] secretAuthenticationValueCipherBytes = null;
		byte[] newPwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;

		try {
			emailCipherBytes = CommonStaticUtil.Base64Decoder.decode(emailCipherBase64);
		} catch (Exception e) {
			String errorMessage = "이메일 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ParameterServerTaskException(errorMessage);
		}

		try {
			secretAuthenticationValueCipherBytes = CommonStaticUtil.Base64Decoder
					.decode(secretAuthenticationValueCipherBase64);
		} catch (Exception e) {
			String errorMessage = "아이디 혹은 비밀번호 찾기용 비밀값 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ParameterServerTaskException(errorMessage);
		}

		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			try {
				newPwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(newPwdCipherBase64);
			} catch (Exception e) {
				String errorMessage = "새로운 비밀번호 암호문은 베이스64로 인코딩되지 않았습니다";
				throw new ParameterServerTaskException(errorMessage);
			}
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

		String email = null;
		String secretAuthenticationValue = null;
		byte[] newPasswordBytes = null;

		try {
			email = getDecryptedString(emailCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException | SymmetricException e) {
			String errorMessage = "이메일에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		}

		try {
			secretAuthenticationValue = getDecryptedString(secretAuthenticationValueCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException | SymmetricException e) {
			String errorMessage = "이메일에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ParameterServerTaskException(errorMessage);
		}

		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			try {
				newPasswordBytes = serverSymmetricKey.decrypt(newPwdCipherBytes);
			} catch (IllegalArgumentException e) {

				String errorMessage = "새로운 비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);
				throw new ParameterServerTaskException(errorMessage);
			} catch (SymmetricException e) {
				String errorMessage = "새로운 비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);
				throw new ParameterServerTaskException(errorMessage);
			}
		}		
		
		AccountSearchProcessDecryptionReq accountSearchProcessDecryptionReq = new AccountSearchProcessDecryptionReq();
		accountSearchProcessDecryptionReq.setAccountSearchType(accountSearchType);
		accountSearchProcessDecryptionReq.setEmail(email);		
		accountSearchProcessDecryptionReq.setSecretAuthenticationValue(secretAuthenticationValue);
		accountSearchProcessDecryptionReq.setNewPasswordBytes(newPasswordBytes);
		accountSearchProcessDecryptionReq.setIp(accountSearchProcessReq.getIp());
		
		AbstractMessage outputMessage = ServerDBUtil.execute(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, accountSearchProcessDecryptionReq);

		toLetterCarrier.addSyncOutputMessage(outputMessage);

	}

	private String getDecryptedString(byte[] cipherBytes, ServerSymmetricKeyIF serverSymmetricKey)
			throws InterruptedException, IllegalArgumentException, SymmetricException {
		byte[] valueBytes = serverSymmetricKey.decrypt(cipherBytes);
		String decryptedString = new String(valueBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		return decryptedString;
	}

	public AccountSearchProcessRes doWork(String dbcpName, AccountSearchProcessDecryptionReq accountSearchProcessDecryptionReq) throws Exception {
		AccountSearchProcessRes outputMessage = ServerDBUtil.execute(dbcpName, this, accountSearchProcessDecryptionReq);
		
		return outputMessage;
	}
	
	@Override
	public AccountSearchProcessRes doWork(final DSLContext dsl, final AccountSearchProcessDecryptionReq accountSearchProcessDecryptionReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == accountSearchProcessDecryptionReq) {
			throw new ParameterServerTaskException("the parameter accountSearchProcessDecryptionReq is null");
		}
		
		final AccountSearchType accountSearchType = accountSearchProcessDecryptionReq.getAccountSearchType();
		final String email = accountSearchProcessDecryptionReq.getEmail();		
		final String secretAuthenticationValue = accountSearchProcessDecryptionReq.getSecretAuthenticationValue();		
		final byte[] newPasswordBytes = accountSearchProcessDecryptionReq.getNewPasswordBytes();
		final String ip = accountSearchProcessDecryptionReq.getIp();		
		
		try {
			ValueChecker.checkValidEmail(email);
			ValueChecker.checkValidSecretAuthenticationValue(secretAuthenticationValue);

			if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
				ValueChecker.checkValidPasswordChangePwd(newPasswordBytes);
			}

			ValueChecker.checkValidIP(ip);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		

		final String title;
		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			title = "비밀번호 찾기";
		} else {
			title = "아이디 찾기";
		}
		
		
		Record2<String, String> memberRecord = dsl.select(SB_MEMBER_TB.USER_ID, SB_MEMBER_TB.NICKNAME)
				.from(SB_MEMBER_TB).where(SB_MEMBER_TB.EMAIL.eq(email))
				.and(SB_MEMBER_TB.ROLE.eq(MemberRoleType.MEMBER.getValue())).forUpdate().fetchOne();

		if (null == memberRecord) {
			String errorMessage = "입력한 이메일에 해당하는 일반 회원이 없습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		String userID = memberRecord.get(SB_MEMBER_TB.USER_ID);
		String nickname = memberRecord.get(SB_MEMBER_TB.NICKNAME);

		Record3<UByte, String, Timestamp> passwordSearchRequestRecord = dsl
				.select(SB_ACCOUNT_SERARCH_TB.FAIL_CNT, SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE,
						SB_ACCOUNT_SERARCH_TB.LAST_REQ_DT)
				.from(SB_ACCOUNT_SERARCH_TB).where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID)).fetchOne();

		if (null == passwordSearchRequestRecord) {

			String errorMessage = "아이디 혹은 비밀번호 찾기 준비 단계가 생략되었습니다";

			throw new RollbackServerTaskException(errorMessage);
		}

		UByte failCount = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_TB.FAIL_CNT);
		String sourceSecretAuthenticationValue = passwordSearchRequestRecord
				.get(SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE);
		Timestamp lastRegisteredDate = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_TB.LAST_REQ_DT);

		if (ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE == failCount.shortValue()) {

			String errorMessage = new StringBuilder().append(title).append("로 비밀 값 틀린 횟수가  최대 횟수 ")
					.append(ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE)
					.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		long elapsedTime = (new java.util.Date().getTime() - lastRegisteredDate.getTime());

		if (elapsedTime > ServerCommonStaticFinalVars.TIMEOUT_OF_PASSWORD_SEARCH_SERVICE) {
			String errorMessage = new StringBuilder().append(title).append("에서 비밀 값 입력 제한 시간[")
					.append(ServerCommonStaticFinalVars.TIMEOUT_OF_PASSWORD_SEARCH_SERVICE)
					.append(" ms]을 초과하여 더 이상 진행할 수 없습니다, 처음 부터 다시 시작해 주시기 바랍니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		if (!sourceSecretAuthenticationValue.equals(secretAuthenticationValue)) {
			dsl.update(SB_ACCOUNT_SERARCH_TB).set(SB_ACCOUNT_SERARCH_TB.FAIL_CNT, SB_ACCOUNT_SERARCH_TB.FAIL_CNT.add(1))
					.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID)).execute();

			Timestamp lastPwdModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());

			ServerDBUtil.insertSiteLog(dsl, userID, new StringBuilder().append(title).append(" ")
					.append(failCount.byteValue() + 1).append("회 비밀 값 틀림").toString(), lastPwdModifiedDate, ip);

			String errorMessage = new StringBuilder().append(title).append(" ").append(failCount.byteValue() + 1)
					.append("회 비밀 값이 틀렸습니다, 처음 부터 다시 시도해 주시기 바랍니다").toString();

			throw new CommitServerTaskException(errorMessage);
		}

		dsl.update(SB_ACCOUNT_SERARCH_TB).set(SB_ACCOUNT_SERARCH_TB.IS_FINISHED, "Y")
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID)).execute();

		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			byte[] newPwdSaltBytes = new byte[8];
			SecureRandom random = null;
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				/** dead code */
				log.error("NoSuchAlgorithmException", e);
				System.exit(1);
			}
			random.nextBytes(newPwdSaltBytes);

			PasswordPairOfMemberTable newPasswordPairOfMemberTable = ServerDBUtil
					.toPasswordPairOfMemberTable(newPasswordBytes, newPwdSaltBytes);

			Timestamp lastPwdModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());

			dsl.update(SB_MEMBER_TB).set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0))
					.set(SB_MEMBER_TB.PWD_BASE64, newPasswordPairOfMemberTable.getPasswordBase64())
					.set(SB_MEMBER_TB.PWD_SALT_BASE64, newPasswordPairOfMemberTable.getPasswordSaltBase64())
					.set(SB_MEMBER_TB.LAST_PWD_MOD_DT, lastPwdModifiedDate).where(SB_MEMBER_TB.USER_ID.eq(userID))
					.execute();

			ServerDBUtil.insertSiteLog(dsl, userID, "비밀 번호 찾기 완료", lastPwdModifiedDate, ip);

		} else {
			Timestamp lastPwdModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());

			ServerDBUtil.insertSiteLog(dsl, userID, "아이디 찾기 완료", lastPwdModifiedDate, ip);
		}
		
		final AccountSearchProcessRes accountSearchProcessRes = new AccountSearchProcessRes();
		accountSearchProcessRes.setAccountSearchType(accountSearchType.getValue());
		accountSearchProcessRes.setUserID(userID);
		accountSearchProcessRes.setNickname(nickname);
		
		return accountSearchProcessRes;
	}
	
	
}
