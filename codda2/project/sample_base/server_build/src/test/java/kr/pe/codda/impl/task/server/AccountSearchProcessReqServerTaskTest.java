package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.jooq.Record1;
import org.junit.Ignore;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.inner_message.AccountSearchProcessDecryptionReq;
import kr.pe.codda.impl.inner_message.MemberRegisterDecryptionReq;
import kr.pe.codda.impl.message.AccountSearchReadyReq.AccountSearchReadyReq;
import kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq;
import kr.pe.codda.impl.message.MemberLoginRes.MemberLoginRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.lib.AccountSearchType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class AccountSearchProcessReqServerTaskTest extends AbstractBoardTest {
	

	@Test
	public void 비밀번호찾기처리_존재하지않은회원() {
		String email = "nobody@codda.pe.kr";
		String ip = "127.0.0.1";

		AccountSearchProcessDecryptionReq accountSearchProcessDecryptionReq = new AccountSearchProcessDecryptionReq();
		accountSearchProcessDecryptionReq.setAccountSearchType(AccountSearchType.ID);
		accountSearchProcessDecryptionReq.setEmail(email);
		accountSearchProcessDecryptionReq.setSecretAuthenticationValue("aa");
		accountSearchProcessDecryptionReq.setNewPasswordBytes(null);
		accountSearchProcessDecryptionReq.setIp(ip);

		AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();

		try {
			passwordSearchProcessReqServerTask.doWork(TEST_DBCP_NAME, accountSearchProcessDecryptionReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "입력한 이메일에 해당하는 일반 회원이 없습니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 처리 실패");
		}
	}

	@Test
	public void 비밀번호찾기처리_준비단계없이호출된경우() {
		String email = "test02@codda.pe.kr";
		String ip = "127.0.0.1";

		AccountSearchProcessDecryptionReq accountSearchProcessDecryptionReq = new AccountSearchProcessDecryptionReq();
		accountSearchProcessDecryptionReq.setAccountSearchType(AccountSearchType.ID);
		accountSearchProcessDecryptionReq.setEmail(email);
		accountSearchProcessDecryptionReq.setSecretAuthenticationValue("aa");
		accountSearchProcessDecryptionReq.setNewPasswordBytes(null);
		accountSearchProcessDecryptionReq.setIp(ip);

		AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();

		try {
			passwordSearchProcessReqServerTask.doWork(TEST_DBCP_NAME, accountSearchProcessDecryptionReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "아이디 혹은 비밀번호 찾기 준비 단계가 생략되었습니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 처리 실패");
		}
	}

	@Test
	public void 비밀번호찾기처리_비밀값틀린횟수최대값초과() {
		String testID = "test03";
		String email = "k9200544@hanmail.net";
		String ip = "127.0.0.1";

		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (dsl) -> {

				dsl.delete(SB_SITE_LOG_TB).execute();

				dsl.delete(SB_ACCOUNT_SERARCH_TB).where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID)).execute();

				dsl.delete(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(testID)).execute();
			});
		} catch (Exception e) {
			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		}

		{

			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "이메일테스터";

			MemberRegisterDecryptionReq memberRegisterDecryptionReq = new MemberRegisterDecryptionReq();
			memberRegisterDecryptionReq.setMemberRoleType(MemberRoleType.MEMBER);
			memberRegisterDecryptionReq.setUserID(testID);
			memberRegisterDecryptionReq.setNickname(nickname);
			memberRegisterDecryptionReq.setEmail(email);
			memberRegisterDecryptionReq.setPasswordBytes(passwordBytes);
			memberRegisterDecryptionReq.setRegisteredDate(new java.sql.Timestamp(System.currentTimeMillis()));
			memberRegisterDecryptionReq.setIp(ip);

			MemberRegisterReqServerTask memberRegisterReqServerTask = new MemberRegisterReqServerTask();

			try {
				ServerDBUtil.execute(TEST_DBCP_NAME, memberRegisterReqServerTask, memberRegisterDecryptionReq);
			} catch (ServerTaskException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(testID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to dsl a test ID");
			}
		}

		// FIXME!
		GreenMail greenMail = new GreenMail(ServerSetup.ALL); // uses test ports by default
		greenMail.start();
		try {
			AccountSearchReadyReq passwordSearchReadyReq = new AccountSearchReadyReq();
			passwordSearchReadyReq.setEmail(email);
			passwordSearchReadyReq.setIp(ip);

			AccountSearchReadyReqServerTask passwordSearchReadyReqServerTask = new AccountSearchReadyReqServerTask();

			try {
				MessageResultRes messageResultRes = passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME,
						passwordSearchReadyReq);
				log.info(messageResultRes.toString());
				if (!messageResultRes.getIsSuccess()) {
					fail("비밀번호 찾기 준비 실패");
				}
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("비밀번호 찾기 준비 실패");
			}

			AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();

			AccountSearchProcessDecryptionReq accountSearchProcessDecryptionReq = new AccountSearchProcessDecryptionReq();
			accountSearchProcessDecryptionReq.setAccountSearchType(AccountSearchType.ID);
			accountSearchProcessDecryptionReq.setEmail(email);
			accountSearchProcessDecryptionReq.setSecretAuthenticationValue("aa");
			accountSearchProcessDecryptionReq.setNewPasswordBytes(null);
			accountSearchProcessDecryptionReq.setIp(ip);

			for (int i = 0; i < ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE; i++) {
				try {
					passwordSearchProcessReqServerTask.doWork(TEST_DBCP_NAME, accountSearchProcessDecryptionReq);

					fail("no ServerTaskException");
				} catch (ServerTaskException e) {
					String errorMessage = e.getMessage();
					String expectedErrorMessage = new StringBuilder().append("아이디 찾기 ").append(i + 1)
							.append("회 비밀 값이 틀렸습니다, 처음 부터 다시 시도해 주시기 바랍니다").toString();

					assertEquals(expectedErrorMessage, errorMessage);
				} catch (Exception e) {
					log.warn("unknown error", e);
					fail("비밀번호 찾기 처리 실패");
				}
			}

			try {
				passwordSearchProcessReqServerTask.doWork(TEST_DBCP_NAME, accountSearchProcessDecryptionReq);

				fail("no ServerTaskException");
			} catch (ServerTaskException e) {
				String errorMessage = e.getMessage();
				String expectedErrorMessage = new StringBuilder().append("아이디 찾기로 비밀 값 틀린 횟수가  최대 횟수 ")
						.append(ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE)
						.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();

				assertEquals(expectedErrorMessage, errorMessage);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("비밀번호 찾기 처리 실패");
			}
		} finally {
			greenMail.stop();
		}
	}

	@Ignore
	public void 비밀번호찾기처리_시간초과() {
		String testID = "test03";
		String email = "k9200544@hanmail.net";
		String ip = "127.0.0.1";

		MemberRegisterReqServerTask memberRegisterReqServerTask = new MemberRegisterReqServerTask();

		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (dsl) -> {

				dsl.delete(SB_SITE_LOG_TB).execute();

				dsl.delete(SB_ACCOUNT_SERARCH_TB).where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID)).execute();

				dsl.delete(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(testID)).execute();
			});
		} catch (Exception e) {
			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		}

		{

			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "이메일테스터";

			MemberRegisterDecryptionReq memberRegisterDecryptionReq = new MemberRegisterDecryptionReq();
			memberRegisterDecryptionReq.setMemberRoleType(MemberRoleType.MEMBER);
			memberRegisterDecryptionReq.setUserID(testID);
			memberRegisterDecryptionReq.setNickname(nickname);
			memberRegisterDecryptionReq.setEmail(email);
			memberRegisterDecryptionReq.setPasswordBytes(passwordBytes);
			memberRegisterDecryptionReq.setRegisteredDate(new java.sql.Timestamp(System.currentTimeMillis()));
			memberRegisterDecryptionReq.setIp(ip);

			try {

				ServerDBUtil.execute(TEST_DBCP_NAME, memberRegisterReqServerTask, memberRegisterDecryptionReq);

			} catch (ServerTaskException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(testID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to dsl a test ID");
			}
		}

		GreenMail greenMail = new GreenMail(ServerSetup.ALL); // uses test ports by default
		greenMail.start();
		try {
			AccountSearchReadyReq passwordSearchReadyReq = new AccountSearchReadyReq();
			passwordSearchReadyReq.setEmail(email);
			passwordSearchReadyReq.setIp(ip);

			AccountSearchReadyReqServerTask passwordSearchReadyReqServerTask = new AccountSearchReadyReqServerTask();

			try {
				MessageResultRes messageResultRes = passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME,
						passwordSearchReadyReq);
				log.info(messageResultRes.toString());
				if (!messageResultRes.getIsSuccess()) {
					fail("비밀번호 찾기 준비 실패");
				}
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("비밀번호 찾기 준비 실패");
			}

			try {
				Thread.sleep(ServerCommonStaticFinalVars.TIMEOUT_OF_PASSWORD_SEARCH_SERVICE);
			} catch (InterruptedException e) {
				log.warn("unknown error", e);
				fail("it failed to sleep");
			}

			AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();

			AccountSearchProcessDecryptionReq accountSearchProcessDecryptionReq = new AccountSearchProcessDecryptionReq();
			accountSearchProcessDecryptionReq.setAccountSearchType(AccountSearchType.ID);
			accountSearchProcessDecryptionReq.setEmail(email);
			accountSearchProcessDecryptionReq.setSecretAuthenticationValue("aa");
			accountSearchProcessDecryptionReq.setNewPasswordBytes(null);
			accountSearchProcessDecryptionReq.setIp(ip);

			try {
				passwordSearchProcessReqServerTask.doWork(TEST_DBCP_NAME, accountSearchProcessDecryptionReq);

				fail("no ServerTaskException");
			} catch (ServerTaskException e) {
				String errorMessage = e.getMessage();
				String expectedErrorMessage = new StringBuilder().append("아이디 찾기에서 비밀 값 입력 제한 시간[")
						.append(ServerCommonStaticFinalVars.TIMEOUT_OF_PASSWORD_SEARCH_SERVICE)
						.append(" ms]을 초과하여 더 이상 진행할 수 없습니다, 처음 부터 다시 시작해 주시기 바랍니다").toString();

				assertEquals(expectedErrorMessage, errorMessage);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("비밀번호 찾기 처리 실패");
			}
		} finally {
			greenMail.stop();
		}
	}

	@Test
	public void 첫번째_로그인비밀번호최대횟수만큼틀림_두번째_비밀번호찾기성공_마지막_회원로그인_OK() {
		String testID = "test03";
		String email = "k9200544@hanmail.net";
		String ip = "127.0.0.1";

		MemberRegisterReqServerTask memberRegisterReqServerTask = new MemberRegisterReqServerTask();

		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (dsl) -> {
				dsl.delete(SB_SITE_LOG_TB).execute();

				dsl.delete(SB_ACCOUNT_SERARCH_TB).where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID)).execute();

				dsl.delete(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(testID)).execute();
			});
		} catch (Exception e) {
			log.warn("unknwon error", e);
			fail("'비밀번호 찾기 처리' 단위테스트를 위한 DB 환경 초기화 실패");
		}

		{

			byte[] passwordBytes = new byte[] { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2',
					(byte) '3', (byte) '4', (byte) '$' };
			String nickname = "이메일테스터";

			MemberRegisterDecryptionReq memberRegisterDecryptionReq = new MemberRegisterDecryptionReq();
			memberRegisterDecryptionReq.setMemberRoleType(MemberRoleType.MEMBER);
			memberRegisterDecryptionReq.setUserID(testID);
			memberRegisterDecryptionReq.setNickname(nickname);
			memberRegisterDecryptionReq.setEmail(email);
			memberRegisterDecryptionReq.setPasswordBytes(passwordBytes);
			memberRegisterDecryptionReq.setRegisteredDate(new java.sql.Timestamp(System.currentTimeMillis()));
			memberRegisterDecryptionReq.setIp(ip);

			try {
				ServerDBUtil.execute(TEST_DBCP_NAME, memberRegisterReqServerTask, memberRegisterDecryptionReq);
			} catch (ServerTaskException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(testID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to dsl a test ID");
			}
		}

		byte passwordBytes[] = new byte[] { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2',
				(byte) '3', (byte) '4', (byte) '*' };

		GreenMail greenMail = new GreenMail(ServerSetup.ALL); // uses test ports by default
		greenMail.start();
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();

			ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
			ClientSessionKeyIF clientSessionKey = null;
			try {
				clientSessionKey = clientSessionKeyManager.createNewClientSessionKey(
						serverSessionkeyManager.getMainProjectServerSessionkey().getDupPublicKeyBytes(), false);
			} catch (SymmetricException e) {
				fail("fail to get a ClientSessionKey");
			}

			ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();

			byte[] idCipherTextBytes = null;
			try {
				idCipherTextBytes = clientSymmetricKey.encrypt(testID.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
			} catch (Exception e) {
				fail("fail to encrypt id");
			}
			byte[] passwordCipherTextBytes = null;

			try {
				passwordCipherTextBytes = clientSymmetricKey.encrypt(passwordBytes);
			} catch (Exception e) {
				fail("fail to encrypt password");
			}

			Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);

			MemberLoginReq inObj = new MemberLoginReq();
			inObj.setIdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(idCipherTextBytes));
			inObj.setPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(passwordCipherTextBytes));
			inObj.setSessionKeyBase64(
					CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes()));
			inObj.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes()));
			inObj.setIp(ip);

			MemberLoginReqServerTask loginReqServerTask = new MemberLoginReqServerTask();

			for (int i = 0; i < ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES; i++) {
				try {
					loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);

					fail("no ServerTaskException");
				} catch (ServerTaskException e) {
					String errorMessage = e.getMessage();
					String expectedErrorMessage = new StringBuilder().append(i + 1).append(" 회 비밀 번호가 틀렸습니다")
							.toString();

					assertEquals(expectedErrorMessage, errorMessage);
				} catch (Exception e) {
					log.warn("fail to execuate doTask", e);
					fail("fail to execuate doTask");
				}
			}

			try {
				loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);

				fail("no ServerTaskException");
			} catch (ServerTaskException e) {
				String errorMessage = e.getMessage();
				String expectedErrorMessage = new StringBuilder("최대 허용된 횟수[")
						.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
						.append("]까지 비밀 번호가 틀려 더 이상 로그인 하실 수 없습니다").toString();

				assertEquals(expectedErrorMessage, errorMessage);
			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}

			AccountSearchReadyReq passwordSearchReadyReq = new AccountSearchReadyReq();
			passwordSearchReadyReq.setEmail(email);
			passwordSearchReadyReq.setIp(ip);

			AccountSearchReadyReqServerTask passwordSearchReadyReqServerTask = new AccountSearchReadyReqServerTask();

			try {
				MessageResultRes messageResultRes = passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME,
						passwordSearchReadyReq);
				log.info(messageResultRes.toString());
				if (!messageResultRes.getIsSuccess()) {
					fail("비밀번호 찾기 준비 실패");
				}
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("비밀번호 찾기 준비 실패");
			}

			// final HashMap<String, String> hash = new HashMap<String, String>();

			

			// String secretAuthenticationValue = hash.get("secretAuthenticationValue");

			AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();

			passwordBytes = new byte[] { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2',
					(byte) '3', (byte) '4', (byte) '&' };

			AccountSearchProcessDecryptionReq accountSearchProcessDecryptionReq = new AccountSearchProcessDecryptionReq();
			accountSearchProcessDecryptionReq.setAccountSearchType(AccountSearchType.PASSWORD);
			accountSearchProcessDecryptionReq.setEmail(email);
			accountSearchProcessDecryptionReq.setNewPasswordBytes(passwordBytes);
			accountSearchProcessDecryptionReq.setIp(ip);
			
			/** 비밀 인증 값 세팅 */
			try {
				ServerDBUtil.execute(TEST_DBCP_NAME, (dsl) -> {

					Record1<String> passwordSearchReqRecord = dsl.select(SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE)
							.from(SB_ACCOUNT_SERARCH_TB).where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID)).fetchOne();

					String secretAuthenticationValue = passwordSearchReqRecord
							.get(SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE);
					
					accountSearchProcessDecryptionReq.setSecretAuthenticationValue(secretAuthenticationValue);
				});
			} catch (Exception e) {
				log.warn("unknwon error", e);
				fail("'비밀번호 찾기 처리' 단위테스트를 위한 DB 환경 초기화 실패");
			}
			
			

			try {
				passwordSearchProcessReqServerTask.doWork(TEST_DBCP_NAME, accountSearchProcessDecryptionReq);

			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("비밀번호 찾기 처리 실패");
			}

			passwordBytes = new byte[] { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2',
					(byte) '3', (byte) '4', (byte) '&' };

			try {
				passwordCipherTextBytes = clientSymmetricKey.encrypt(passwordBytes);
			} catch (Exception e) {
				fail("fail to encrypt password");
			}

			Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);

			inObj = new MemberLoginReq();
			inObj.setIdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(idCipherTextBytes));
			inObj.setPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(passwordCipherTextBytes));
			inObj.setSessionKeyBase64(
					CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes()));
			inObj.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes()));
			inObj.setIp(ip);

			try {
				MemberLoginRes memberLoginRes = loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
				log.info(memberLoginRes.toString());
			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		} finally {
			greenMail.stop();
		}
	}
}
