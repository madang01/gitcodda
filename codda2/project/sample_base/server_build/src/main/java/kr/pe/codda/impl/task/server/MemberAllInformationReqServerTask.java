package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record10;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MemberAllInformationReq.MemberAllInformationReq;
import kr.pe.codda.impl.message.MemberAllInformationRes.MemberAllInformationRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MemberAllInformationReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MemberAllInformationReq, MemberAllInformationRes> {
	private Logger log = LoggerFactory.getLogger(MemberAllInformationReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (MemberAllInformationReq) inputMessage);

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	public MemberAllInformationRes doWork(final String dbcpName, final MemberAllInformationReq userInformationReq) throws Exception {
		MemberAllInformationRes outputMessage = ServerDBUtil.execute(dbcpName, this, userInformationReq);
		
		return outputMessage;
	}

	@Override
	public MemberAllInformationRes doWork(final DSLContext dsl, final MemberAllInformationReq userInformationReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == userInformationReq) {
			throw new ParameterServerTaskException("the parameter userInformationReq is null");
		}

		// FIXME!
		log.info(userInformationReq.toString());

		final String requestedUserID = userInformationReq.getRequestedUserID();
		final String targetUserID = userInformationReq.getTargetUserID();

		try {
			ValueChecker.checkValidRequestedUserID(requestedUserID);
			ValueChecker.checkValidUnBlockUserID(targetUserID);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(dsl, "사용자 정보 조회 서비스",
				PermissionType.MEMBER, requestedUserID);

		if (!MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
			/** 관리자가 아닌 경우 본인 여부 확인 */
			if (!requestedUserID.equals(targetUserID)) {
				String errorMessage = "타인의 사용자 정보는 검색할 수 없습니다";
				throw new RollbackServerTaskException(errorMessage);
			}
		}

		Record10<String, Byte, Byte, String, UByte, Timestamp, Timestamp, Timestamp, Timestamp, Timestamp> memberRecordOfTargetUserID = dsl
				.select(SB_MEMBER_TB.NICKNAME, SB_MEMBER_TB.STATE, SB_MEMBER_TB.ROLE, SB_MEMBER_TB.EMAIL,
						SB_MEMBER_TB.PWD_FAIL_CNT, SB_MEMBER_TB.REG_DT, SB_MEMBER_TB.LAST_NICKNAME_MOD_DT,
						SB_MEMBER_TB.LAST_EMAIL_MOD_DT, SB_MEMBER_TB.LAST_PWD_MOD_DT, SB_MEMBER_TB.LAST_STATE_MOD_DT)
				.from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(targetUserID)).fetchOne();

		if (null == memberRecordOfTargetUserID) {

			String errorMessage = new StringBuilder("회원 조회를 원하는 사용자[").append(targetUserID)
					.append("]가 회원 테이블에 존재하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		String nickname = memberRecordOfTargetUserID.get(SB_MEMBER_TB.NICKNAME);
		byte memeberState = memberRecordOfTargetUserID.get(SB_MEMBER_TB.STATE);
		byte role = memberRecordOfTargetUserID.get(SB_MEMBER_TB.ROLE);
		String email = memberRecordOfTargetUserID.get(SB_MEMBER_TB.EMAIL);
		UByte passwordFailedCount = memberRecordOfTargetUserID.get(SB_MEMBER_TB.PWD_FAIL_CNT);
		Timestamp registeredDate = memberRecordOfTargetUserID.get(SB_MEMBER_TB.REG_DT);
		Timestamp lastNicknameModifiedDate = memberRecordOfTargetUserID.get(SB_MEMBER_TB.LAST_NICKNAME_MOD_DT);
		Timestamp lastEmailModifiedDate = memberRecordOfTargetUserID.get(SB_MEMBER_TB.LAST_EMAIL_MOD_DT);
		Timestamp lastPasswordModifiedDate = memberRecordOfTargetUserID.get(SB_MEMBER_TB.LAST_PWD_MOD_DT);
		Timestamp lastStateModifiedDate = memberRecordOfTargetUserID.get(SB_MEMBER_TB.LAST_STATE_MOD_DT);

		final MemberAllInformationRes userInformationRes = new MemberAllInformationRes();
		userInformationRes.setNickname(nickname);
		userInformationRes.setState(memeberState);
		userInformationRes.setRole(role);
		userInformationRes.setEmail(email);
		userInformationRes.setPasswordFailedCount(passwordFailedCount.shortValue());
		userInformationRes.setRegisteredDate(registeredDate);
		userInformationRes.setLastNicknameModifiedDate(lastNicknameModifiedDate);
		userInformationRes.setLastEmailModifiedDate(lastEmailModifiedDate);
		userInformationRes.setLastPasswordModifiedDate(lastPasswordModifiedDate);
		userInformationRes.setLastStateModifiedDate(lastStateModifiedDate);

		return userInformationRes;
	}

}
