package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.security.SecureRandom;
import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.AccountSearchReadyReq.AccountSearchReadyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.AccountSearchType;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.EmilUtil;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class AccountSearchReadyReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<AccountSearchReadyReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchReadyReqServerTask.class);


	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		/*
		 * AbstractMessage outputMessage =
		 * doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (AccountSearchReadyReq)
		 * inputMessage);
		 */

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (AccountSearchReadyReq) inputMessage);

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	public MessageResultRes doWork(final String dbcpName, final AccountSearchReadyReq accountSearchReadyReq) throws Exception {
		MessageResultRes outputMessage = ServerDBUtil.execute(dbcpName, this, accountSearchReadyReq);
		
		return outputMessage;
	}

	@Override
	public MessageResultRes doWork(final DSLContext dsl, final AccountSearchReadyReq accountSearchReadyReq)
			throws Exception {
		// FIXME!
		log.info(accountSearchReadyReq.toString());

		final AccountSearchType accountSearchType;
		try {
			accountSearchType = AccountSearchType.valueOf(accountSearchReadyReq.getAccountSearchType());

			ValueChecker.checkValidEmail(accountSearchReadyReq.getEmail());
			ValueChecker.checkValidIP(accountSearchReadyReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}

		/**
		 * '아이디 혹은 비밀번호 찾기' 서비스는 오직 일반 회원에 한에서만 서비스가 제공됩니다. 회원 테이블에는 손님, 관리자 그리고 일반회원
		 * 이렇게 3종류가 존재하는데 이중 손님과 관리자 2개는 '아이디 혹은 비밀번호 찾기'서비스 대상자에서 제외합니다. 손님은 내부용 처리를 위해
		 * 존재할뿐 회원이 아니기때문에 제외하며 관리자는 보안상 허용해서는 안되기때문에 제외합니다.
		 */

		Record3<String, String, String> memberRecord = dsl
				.select(SB_MEMBER_TB.USER_ID, SB_MEMBER_TB.NICKNAME, SB_MEMBER_TB.EMAIL).from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.EMAIL.eq(accountSearchReadyReq.getEmail()))
				.and(SB_MEMBER_TB.ROLE.eq(MemberRoleType.MEMBER.getValue())).forUpdate().fetchOne();

		if (null == memberRecord) {

			String errorMessage = "입력한 이메일에 해당하는 일반 회원이 없습니다";

			throw new RollbackServerTaskException(errorMessage);
		}

		String userID = memberRecord.get(SB_MEMBER_TB.USER_ID);
		String nickname = memberRecord.get(SB_MEMBER_TB.NICKNAME);
		String email = memberRecord.get(SB_MEMBER_TB.EMAIL);

		Record2<UByte, UByte> passwordSearchRequestRecord = dsl
				.select(SB_ACCOUNT_SERARCH_TB.FAIL_CNT, SB_ACCOUNT_SERARCH_TB.RETRY_CNT).from(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID)).fetchOne();

		if (null == passwordSearchRequestRecord) {
			byte[] secretAuthenticationValueBytes = new byte[8];
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(secretAuthenticationValueBytes);

			String secretAuthenticationValue = CommonStaticUtil.Base64Encoder
					.encodeToString(secretAuthenticationValueBytes);

			Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

			dsl.insertInto(SB_ACCOUNT_SERARCH_TB).set(SB_ACCOUNT_SERARCH_TB.USER_ID, userID)
					.set(SB_ACCOUNT_SERARCH_TB.FAIL_CNT, UByte.valueOf(0))
					.set(SB_ACCOUNT_SERARCH_TB.RETRY_CNT, UByte.valueOf(1))
					.set(SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE, secretAuthenticationValue)
					.set(SB_ACCOUNT_SERARCH_TB.LAST_REQ_DT, registeredDate).set(SB_ACCOUNT_SERARCH_TB.IS_FINISHED, "N")
					.execute();

			EmilUtil.sendPasswordSearchEmail(accountSearchType, nickname, email, secretAuthenticationValue);

			String siteLogText = new StringBuilder().append(accountSearchType.getName()).append(" 찾기 신청").toString();

			ServerDBUtil.insertSiteLog(dsl, userID, siteLogText, registeredDate, accountSearchReadyReq.getIp());

		} else {
			UByte failCount = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_TB.FAIL_CNT);
			UByte retryCount = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_TB.RETRY_CNT);

			if (ServerCommonStaticFinalVars.MAX_RETRY_COUNT_OF_PASSWORD_SEARCH_SERVICE == retryCount.shortValue()) {

				String errorMessage = new StringBuilder().append("아이디 혹은 비밀번호 찾기 신청 횟수가 최대 횟수 ")
						.append(ServerCommonStaticFinalVars.MAX_RETRY_COUNT_OF_PASSWORD_SEARCH_SERVICE)
						.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();

				throw new RollbackServerTaskException(errorMessage);
			}

			if (ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE == failCount
					.shortValue()) {
				String errorMessage = new StringBuilder().append("아이디 혹은 비밀번호 찾기로 비밀 값 틀린 횟수가  최대 횟수 ")
						.append(ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE)
						.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();

				throw new RollbackServerTaskException(errorMessage);
			}

			byte[] newSecretAuthenticationValueBytes = new byte[8];
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(newSecretAuthenticationValueBytes);

			String secretAuthenticationValue = CommonStaticUtil.Base64Encoder
					.encodeToString(newSecretAuthenticationValueBytes);
			Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

			dsl.update(SB_ACCOUNT_SERARCH_TB)
					.set(SB_ACCOUNT_SERARCH_TB.RETRY_CNT, SB_ACCOUNT_SERARCH_TB.RETRY_CNT.add(1))
					.set(SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE, secretAuthenticationValue)
					.set(SB_ACCOUNT_SERARCH_TB.LAST_REQ_DT, registeredDate).set(SB_ACCOUNT_SERARCH_TB.IS_FINISHED, "N")
					.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID)).execute();

			EmilUtil.sendPasswordSearchEmail(accountSearchType, nickname, email, secretAuthenticationValue);

			String siteLogText = new StringBuilder().append(retryCount.shortValue() + 1)
					.append("회 아이디 혹은 비밀번호 찾기 신청[찾기 대상:").append(accountSearchType.getName()).append("]").toString();

			ServerDBUtil.insertSiteLog(dsl, userID, siteLogText, registeredDate, accountSearchReadyReq.getIp());
		}

		final MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(accountSearchReadyReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(
				new StringBuilder().append(accountSearchType.getName()).append(" 찾기 준비 단계 처리가 완료되었습니다").toString());

		return messageResultRes;
	}

}
