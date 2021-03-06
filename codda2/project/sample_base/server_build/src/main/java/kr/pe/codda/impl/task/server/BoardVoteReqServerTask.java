package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardVoteReq.BoardVoteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.MemberActivityType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardVoteReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<BoardVoteReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(BoardVoteReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (BoardVoteReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	
	public MessageResultRes doWork(final String dbcpName, BoardVoteReq boardVoteReq) throws Exception {
		MessageResultRes outputMessage = ServerDBUtil.execute(
				dbcpName, this, boardVoteReq);
		
		return outputMessage;
	}

	@Override
	public MessageResultRes doWork(final DSLContext dsl, BoardVoteReq boardVoteReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}
		if (null == boardVoteReq) {
			throw new ParameterServerTaskException("the parameter boardVoteReq is null");
		}

		// FIXME!
		log.info(boardVoteReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(boardVoteReq.getRequestedUserID());
			ValueChecker.checkValidIP(boardVoteReq.getIp());
			ValueChecker.checkValidBoardID(boardVoteReq.getBoardID());
			ValueChecker.checkValidBoardNo(boardVoteReq.getBoardNo());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final UByte boardID = UByte.valueOf(boardVoteReq.getBoardID());
		final UInteger boardNo = UInteger.valueOf(boardVoteReq.getBoardNo());

		MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(dsl, "게시글 추천 서비스",
				PermissionType.MEMBER, boardVoteReq.getRequestedUserID());
		
		/** 추천할 게시글에 속한 그룹의 루트 노드에 해당하는 레코드에 락을 건다 */
		ServerDBUtil.lockRootRecordOfBoardGroup(dsl, boardID, boardNo);

		Record1<String> firstWriterBoardRecord = dsl.select(SB_BOARD_HISTORY_TB.REGISTRANT_ID).from(SB_BOARD_HISTORY_TB)
				.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
				.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0))).fetchOne();

		if (null == firstWriterBoardRecord) {
			String errorMessage = new StringBuilder("해당 게시글의 최초 작성자 정보가 존재 하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		String firstWriterID = firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.REGISTRANT_ID);

		if (firstWriterID.equals(boardVoteReq.getRequestedUserID())) {
			String errorMessage = new StringBuilder("자신의 글[").append(boardNo).append("]은 본인 스스로 추천할 수 없습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		boolean isVoted = dsl.fetchExists(dsl.select().from(SB_BOARD_VOTE_TB)
				.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_VOTE_TB.BOARD_NO.eq(boardNo))
				.and(SB_BOARD_VOTE_TB.USER_ID.eq(boardVoteReq.getRequestedUserID())));

		if (isVoted) {

			String errorMessage = "이미 추천을 하셨습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

		int countOfInsert = dsl.insertInto(SB_BOARD_VOTE_TB).set(SB_BOARD_VOTE_TB.BOARD_ID, boardID)
				.set(SB_BOARD_VOTE_TB.BOARD_NO, boardNo)
				.set(SB_BOARD_VOTE_TB.USER_ID, boardVoteReq.getRequestedUserID())
				.set(SB_BOARD_VOTE_TB.IP, boardVoteReq.getIp()).set(SB_BOARD_VOTE_TB.REG_DT, registeredDate).execute();

		if (0 == countOfInsert) {

			String errorMessage = "해당 글에 대한 추천이 실패하였습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		ServerDBUtil.insertMemberActivityHistory(dsl, boardVoteReq.getRequestedUserID(),
				memberRoleTypeOfRequestedUserID, MemberActivityType.VOTE, boardID, boardNo, registeredDate);

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(boardVoteReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage("게시글에 대한 추천이 성공하였습니다");

		return messageResultRes;
	}
}
