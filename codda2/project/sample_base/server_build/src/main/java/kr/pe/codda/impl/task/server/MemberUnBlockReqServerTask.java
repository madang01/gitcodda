package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MemberUnBlockReq.MemberUnBlockReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
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

public class MemberUnBlockReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MemberUnBlockReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (MemberUnBlockReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public MessageResultRes doWork(final String dbcpName, final MemberUnBlockReq memberUnBlockReq) throws Exception {
		MessageResultRes outputMessage = ServerDBUtil.execute(dbcpName, this, memberUnBlockReq);
		
		return outputMessage;
	}
	
	@Override
	public MessageResultRes doWork(final DSLContext dsl, final MemberUnBlockReq memberUnBlockReq) throws Exception {

		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == memberUnBlockReq) {
			throw new ParameterServerTaskException("the parameter memberUnBlockReq is null");
		}

		log.info(memberUnBlockReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(memberUnBlockReq.getRequestedUserID());
			ValueChecker.checkValidIP(memberUnBlockReq.getIp());
			ValueChecker.checkValidUnBlockUserID(memberUnBlockReq.getTargetUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		if (memberUnBlockReq.getRequestedUserID().equals(memberUnBlockReq.getTargetUserID())) {
			String errorMessage = "자기 자신을 차단 해제 할 수 없습니다";
			throw new ParameterServerTaskException(errorMessage);
		}

		ServerDBUtil.checkUserAccessRights(dsl, "회원 차단 해제 서비스", PermissionType.ADMIN,
				memberUnBlockReq.getRequestedUserID());

		/** 차단 해제 대상 회원 레코드 락 */
		Record2<Byte, Byte> memberRecordOfTargetUserID = dsl.select(SB_MEMBER_TB.STATE, SB_MEMBER_TB.ROLE)
				.from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(memberUnBlockReq.getTargetUserID())).forUpdate()
				.fetchOne();

		if (null == memberRecordOfTargetUserID) {
			String errorMessage = new StringBuilder("차단 해제 대상 사용자[").append(memberUnBlockReq.getTargetUserID())
					.append("]가 회원 테이블에 존재하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte memberRoleOfTargetUserID = memberRecordOfTargetUserID.getValue(SB_MEMBER_TB.ROLE);
		MemberRoleType memberRoleTypeOfTargetUserID = null;
		try {
			memberRoleTypeOfTargetUserID = MemberRoleType.valueOf(memberRoleOfTargetUserID);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("알 수 없는 회원[").append(memberUnBlockReq.getTargetUserID())
					.append("]의 역활[").append(memberRoleOfTargetUserID).append("] 값입니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (!MemberRoleType.MEMBER.equals(memberRoleTypeOfTargetUserID)) {
			String errorMessage = new StringBuilder().append("차단 해제 대상 회원[id=")
					.append(memberUnBlockReq.getTargetUserID()).append(", 역활=")
					.append(memberRoleTypeOfTargetUserID.name()).append("]이 일반 회원이 아닙니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte memeberStateOfTargetUserID = memberRecordOfTargetUserID.getValue(SB_MEMBER_TB.STATE);
		MemberStateType memberStateTypeOfTargetUserID = null;
		try {
			memberStateTypeOfTargetUserID = MemberStateType.valueOf(memeberStateOfTargetUserID);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("알 수 없는 회원[").append(memberUnBlockReq.getTargetUserID())
					.append("]의 상태[").append(memeberStateOfTargetUserID).append("] 값입니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (!MemberStateType.BLOCK.equals(memberStateTypeOfTargetUserID)) {
			String errorMessage = new StringBuilder("차단 해제 대상 사용자[사용자아이디=").append(memberUnBlockReq.getTargetUserID())
					.append(", 상태=").append(memberStateTypeOfTargetUserID.getName()).append("]는 차단된 사용자가 아닙니다")
					.toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		Timestamp lastStateModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());

		dsl.update(SB_MEMBER_TB).set(SB_MEMBER_TB.STATE, MemberStateType.OK.getValue())
				.set(SB_MEMBER_TB.LAST_STATE_MOD_DT, lastStateModifiedDate)
				.where(SB_MEMBER_TB.USER_ID.eq(memberUnBlockReq.getTargetUserID())).execute();

		String logText = new StringBuilder().append("아이디 '").append(memberUnBlockReq.getTargetUserID())
				.append("' 회원 차단").toString();

		ServerDBUtil.insertSiteLog(dsl, memberUnBlockReq.getRequestedUserID(), logText, lastStateModifiedDate,
				memberUnBlockReq.getIp());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(memberUnBlockReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("차단된 사용자[")
				.append(memberUnBlockReq.getTargetUserID()).append("]에 대한 제제를 해제하였습니다").toString());

		return messageResultRes;
	}

}
