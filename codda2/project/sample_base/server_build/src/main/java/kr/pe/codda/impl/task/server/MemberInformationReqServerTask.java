package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record9;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MemberInformationReq.MemberInformationReq;
import kr.pe.codda.impl.message.MemberInformationRes.MemberInformationRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 
 * @author Won Jonghoon
 *
 */
public class MemberInformationReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MemberInformationReq, MemberInformationRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public MemberInformationReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.doDBAutoTransationWork(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (MemberInformationReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public MemberInformationRes doWork(final DSLContext dsl, final MemberInformationReq memberInformationReq)
			throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == memberInformationReq) {
			throw new ParameterServerTaskException("the parameter memberInformationReq is null");
		}
		// FIXME!
		log.info(memberInformationReq.toString());

		try {
			ValueChecker.checkValidUserID(memberInformationReq.getRequestedUserID());
			ValueChecker.checkValidActivtyTargetUserID(memberInformationReq.getTargetUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		@SuppressWarnings("unused")
		MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(dsl, "개인 정보 조회 서비스",
				PermissionType.GUEST, memberInformationReq.getRequestedUserID());

		Record9<String, String, Byte, Byte, Timestamp, Timestamp, Timestamp, Timestamp, Timestamp> targetUserMemberRecord = dsl
				.select(SB_MEMBER_TB.NICKNAME, SB_MEMBER_TB.EMAIL, SB_MEMBER_TB.ROLE, SB_MEMBER_TB.STATE,
						SB_MEMBER_TB.REG_DT, SB_MEMBER_TB.LAST_NICKNAME_MOD_DT, SB_MEMBER_TB.LAST_EMAIL_MOD_DT,
						SB_MEMBER_TB.LAST_PWD_MOD_DT, SB_MEMBER_TB.LAST_STATE_MOD_DT)
				.from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(memberInformationReq.getTargetUserID())).fetchOne();

		if (null == targetUserMemberRecord) {
			String errorMessage = new StringBuilder("개인 정보 조회 대상 사용자[").append(memberInformationReq.getTargetUserID())
					.append("]가 회원 테이블에 존재하지 않습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		String targetUserNickname = targetUserMemberRecord.get(SB_MEMBER_TB.NICKNAME);
		String email = targetUserMemberRecord.get(SB_MEMBER_TB.EMAIL);
		byte targetUserMemberRole = targetUserMemberRecord.get(SB_MEMBER_TB.ROLE);
		byte targetUserMemberState = targetUserMemberRecord.get(SB_MEMBER_TB.STATE);
		Timestamp targetUserRegisteredDate = targetUserMemberRecord.get(SB_MEMBER_TB.REG_DT);
		Timestamp targetUserLastNicknameModifiedDate = targetUserMemberRecord.get(SB_MEMBER_TB.LAST_NICKNAME_MOD_DT);
		Timestamp targetUserLastEmailModifiedDate = targetUserMemberRecord.get(SB_MEMBER_TB.LAST_EMAIL_MOD_DT);
		Timestamp targetUserLastPasswordModifiedDate = targetUserMemberRecord.get(SB_MEMBER_TB.LAST_PWD_MOD_DT);
		Timestamp targetUserLastStateModifiedDate = targetUserMemberRecord.get(SB_MEMBER_TB.LAST_STATE_MOD_DT);

		MemberRoleType targetUserMemberRoleType = null;
		try {
			targetUserMemberRoleType = MemberRoleType.valueOf(targetUserMemberRole);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("개인 정보 조회 대상 사용자[").append(memberInformationReq.getTargetUserID())
					.append("]의 멤버 구분[").append(targetUserMemberRole).append("]이 잘못되었습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		if (MemberRoleType.GUEST.equals(targetUserMemberRoleType)) {

			String errorMessage = "개인 정보 조회 대상은 회원만 가능합니다";

			throw new RollbackServerTaskException(errorMessage);
		}

		MemberStateType targetUserMemberStateType = null;
		try {
			targetUserMemberStateType = MemberStateType.valueOf(targetUserMemberState);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("개인 정보 조회 대상 사용자[").append(memberInformationReq.getTargetUserID())
					.append("]의 멤버 상태[").append(targetUserMemberState).append("]가 잘못되었습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		MemberInformationRes memberInformationRes = new MemberInformationRes();
		memberInformationRes.setTargetUserID(memberInformationReq.getTargetUserID());
		memberInformationRes.setNickname(targetUserNickname);
		memberInformationRes.setEmail(email);
		memberInformationRes.setRole(targetUserMemberRoleType.getValue());
		memberInformationRes.setState(targetUserMemberStateType.getValue());
		memberInformationRes.setRegisteredDate(targetUserRegisteredDate);
		memberInformationRes.setLastNicknameModifiedDate(targetUserLastNicknameModifiedDate);
		memberInformationRes.setLastEmailModifiedDate(targetUserLastEmailModifiedDate);
		memberInformationRes.setLastPasswordModifiedDate(targetUserLastPasswordModifiedDate);
		memberInformationRes.setLastStateModifiedDate(targetUserLastStateModifiedDate);

		return memberInformationRes;
	}

}
