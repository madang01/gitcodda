package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import org.jooq.Record6;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq;
import kr.pe.codda.impl.message.MemberLoginRes.MemberLoginRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.PasswordPairOfMemberTable;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MemberLoginReqServerTask extends AbstractServerTask {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public MemberLoginReqServerTask() throws DynamicClassCallException {
		super();
	}

	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
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
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(MemberLoginReq) inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());

			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=").append(e.getMessage())
					.append(", inObj=").append(inputMessage.toString()).toString();

			log.warn(errorMessage, e);

			sendErrorOutputMessage("사용자 로그인이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public MemberLoginRes doWork(String dbcpName, MemberLoginReq memberLoginReq) throws Exception {
		// FIXME!
		log.info(memberLoginReq.toString());

		String idCipherBase64 = memberLoginReq.getIdCipherBase64();
		String pwdCipherBase64 = memberLoginReq.getPwdCipherBase64();
		String sessionKeyBase64 = memberLoginReq.getSessionKeyBase64();
		String ivBase64 = memberLoginReq.getIvBase64();

		if (null == idCipherBase64) {
			String errorMessage = "아이디를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}

		if (null == pwdCipherBase64) {
			String errorMessage = "비밀번호를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}

		if (null == sessionKeyBase64) {
			String errorMessage = "세션키를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}

		if (null == ivBase64) {
			String errorMessage = "세션키 소금값을 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidIP(memberLoginReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		

		byte[] idCipherBytes = null;
		byte[] pwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;

		try {
			idCipherBytes = CommonStaticUtil.Base64Decoder.decode(idCipherBase64);
		} catch (Exception e) {
			String errorMessage = "아이디 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}

		try {
			pwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(pwdCipherBase64);
		} catch (Exception e) {
			String errorMessage = "비밀번호 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		try {
			sessionKeyBytes = CommonStaticUtil.Base64Decoder.decode(sessionKeyBase64);
		} catch (Exception e) {
			String errorMessage = "세션키는 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}

		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(ivBase64);
		} catch (Exception e) {
			String errorMessage = "세션키 소금값은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}

		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyIF serverSessionkey = ServerSessionkeyManager.getInstance()
					.getMainProjectServerSessionkey();

			serverSymmetricKey = serverSessionkey.createNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);

		} catch (IllegalArgumentException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		}

		final String userID;

		try {
			userID = getDecryptedString(idCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		}

		final byte[] passwordBytes;

		try {
			passwordBytes = serverSymmetricKey.decrypt(pwdCipherBytes);
		} catch (IllegalArgumentException e) {

			String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidUserID(userID);
			ValueChecker.checkValidLoginPwd(passwordBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		final MemberLoginRes memberLoginRes = new MemberLoginRes();
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			Record6<String, Byte, Byte, UByte, String, String> memberRecord = create
					.select(SB_MEMBER_TB.NICKNAME, SB_MEMBER_TB.ROLE, SB_MEMBER_TB.STATE, SB_MEMBER_TB.PWD_FAIL_CNT,
							SB_MEMBER_TB.PWD_BASE64, SB_MEMBER_TB.PWD_SALT_BASE64)
					.from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(userID)).forUpdate().fetchOne();

			if (null == memberRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("아이디[").append(userID).append("]가 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
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
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("회원[").append(userID).append("]의 멤버 구분[").append(memberRole)
						.append("]이 잘못되었습니다").toString();

				// log.warn(errorMessage);

				throw new ServerServiceException(errorMessage);
			}

			if (MemberRoleType.GUEST.equals(memberRoleType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = "손님은 로그인 할 수 없습니다";
			
				ServerDBUtil.insertSiteLog(conn, create, log, userID,
						new StringBuilder().append("[경고] 손님 아이디[")
						.append(userID)
						.append("]로 로그인 시도").toString(),
						new java.sql.Timestamp(System.currentTimeMillis()), memberLoginReq.getIp());
				conn.commit();

				throw new ServerServiceException(errorMessage);
			}

			MemberStateType memberStateType = null;
			try {
				memberStateType = MemberStateType.valueOf(memberState);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("회원[").append(userID).append("]의 멤버 상태[").append(memberState)
						.append("]가 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			if (memberStateType.equals(MemberStateType.BLOCK)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("차단된 회원[").append(userID).append("] 입니다").toString();
				throw new ServerServiceException(errorMessage);
			} else if (memberStateType.equals(MemberStateType.WITHDRAWAL)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("탈퇴한 회원[").append(userID).append("] 입니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			if (ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES <= pwdFailedCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("최대 허용된 횟수[")
						.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
						.append("]까지 비밀 번호가 틀려 더 이상 로그인 하실 수 없습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			byte[] pwdSaltBytes = CommonStaticUtil.Base64Decoder.decode(pwdSaltBase64);

			PasswordPairOfMemberTable passwordPairOfMemberTable = ServerDBUtil
					.toPasswordPairOfMemberTable(passwordBytes, pwdSaltBytes);

			if (!pwdBase64.equals(passwordPairOfMemberTable.getPasswordBase64())) {
				/*
				 * update SB_MEMBER_TB set pwd_fail_cnt=#{pwdFailCount}, mod_dt=sysdate() where
				 * user_id=#{userId} and member_gb=1 and member_st=0
				 */
				int countOfPwdFailedCountUpdate = create.update(SB_MEMBER_TB)
						.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount + 1))
						.where(SB_MEMBER_TB.USER_ID.eq(userID)).execute();

				if (0 == countOfPwdFailedCountUpdate) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = "비밀 번호 실패 횟수 갱신이 실패하였습니다";
					throw new ServerServiceException(errorMessage);
				}

				conn.commit();

				String errorMessage = new StringBuilder().append(pwdFailedCount + 1).append(" 회 비밀 번호가 틀렸습니다")
						.toString();

				ServerDBUtil.insertSiteLog(conn, create, log, userID, errorMessage,
						new java.sql.Timestamp(System.currentTimeMillis()), memberLoginReq.getIp());

				conn.commit();

				throw new ServerServiceException(errorMessage);
			}

			if (pwdFailedCount > 0) {
				create.update(SB_MEMBER_TB).set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0))
				.where(SB_MEMBER_TB.USER_ID.eq(userID)).execute();
			}			

			conn.commit();

			ServerDBUtil.insertSiteLog(conn, create, log, userID,
					new StringBuilder().append(memberRoleType.getName()).append(" 로그인").toString(),
					new java.sql.Timestamp(System.currentTimeMillis()), memberLoginReq.getIp());
			conn.commit();
			
			memberLoginRes.setUserID(userID);
			memberLoginRes.setUserName(nickname);
			memberLoginRes.setMemberRole(memberRoleType.getValue());
		});		

		return memberLoginRes;
	}
}
