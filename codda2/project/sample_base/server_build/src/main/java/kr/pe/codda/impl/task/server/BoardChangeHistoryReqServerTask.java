package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardChangeHistoryReq.BoardChangeHistoryReq;
import kr.pe.codda.impl.message.BoardChangeHistoryRes.BoardChangeHistoryRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardChangeHistoryReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<BoardChangeHistoryReq, BoardChangeHistoryRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public BoardChangeHistoryReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (BoardChangeHistoryReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public BoardChangeHistoryRes doWork(final DSLContext dsl, final BoardChangeHistoryReq boardChangeHistoryReq)
			throws Exception {
		
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}
		if (null == boardChangeHistoryReq) {
			throw new ParameterServerTaskException("the parameter boardChangeHistoryReq is null");
		}

		/** FIXME! */
		log.info(boardChangeHistoryReq.toString());
		
		try {
			ValueChecker.checkValidRequestedUserID(boardChangeHistoryReq.getRequestedUserID());
			ValueChecker.checkValidBoardID(boardChangeHistoryReq.getBoardID());
			ValueChecker.checkValidBoardNo(boardChangeHistoryReq.getBoardNo());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}

		final UByte boardID = UByte.valueOf(boardChangeHistoryReq.getBoardID());
		final UInteger boardNo = UInteger.valueOf(boardChangeHistoryReq.getBoardNo());

		final List<BoardChangeHistoryRes.BoardChangeHistory> boardChangeHistoryList = new ArrayList<BoardChangeHistoryRes.BoardChangeHistory>();
		

		Record1<Byte> boardInforRecord = dsl.select(SB_BOARD_INFO_TB.LIST_TYPE).from(SB_BOARD_INFO_TB)
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne();

		if (null == boardInforRecord) {
			String errorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(boardID.shortValue())
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte boardListTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);

		BoardListType boardListType = null;
		try {
			boardListType = BoardListType.valueOf(boardListTypeValue);
		} catch (IllegalArgumentException e) {

			String errorMessage = e.getMessage();
			throw new RollbackServerTaskException(errorMessage);
		}

		ServerDBUtil.checkUserAccessRights(dsl, "게시글 수정 이력 조회 서비스", PermissionType.GUEST,
				boardChangeHistoryReq.getRequestedUserID());

		Record2<UInteger, UInteger> boardRecord = dsl.select(SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.GROUP_NO)
				.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
				.fetchOne();

		if (null == boardRecord) {
			String errorMessage = "지정한 게시글이 존재 하지 않습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger parentNo = boardRecord.get(SB_BOARD_TB.PARENT_NO);
		UInteger groupNo = boardRecord.get(SB_BOARD_TB.GROUP_NO);

		Result<Record6<UByte, String, String, String, String, Timestamp>> boardHistoryResult = dsl
				.select(SB_BOARD_HISTORY_TB.HISTORY_SQ, SB_BOARD_HISTORY_TB.SUBJECT, SB_BOARD_HISTORY_TB.CONTENTS,
						SB_BOARD_HISTORY_TB.REGISTRANT_ID, SB_MEMBER_TB.NICKNAME, SB_BOARD_HISTORY_TB.REG_DT)
				.from(SB_BOARD_HISTORY_TB).innerJoin(SB_MEMBER_TB)
				.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_HISTORY_TB.REGISTRANT_ID))
				.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
				.orderBy(SB_BOARD_HISTORY_TB.HISTORY_SQ.asc()).fetch();

		for (Record6<UByte, String, String, String, String, Timestamp> boardHistoryRecord : boardHistoryResult) {
			UByte historySeq = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.HISTORY_SQ);
			String subject = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.SUBJECT);
			String contents = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.CONTENTS);
			String writerID = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.REGISTRANT_ID);
			String writerNickname = boardHistoryRecord.get(SB_MEMBER_TB.NICKNAME);
			Timestamp registeredDate = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.REG_DT);

			BoardChangeHistoryRes.BoardChangeHistory boardChangeHistory = new BoardChangeHistoryRes.BoardChangeHistory();
			boardChangeHistory.setHistorySeq(historySeq.shortValue());
			boardChangeHistory.setSubject((null == subject) ? "" : subject);
			boardChangeHistory.setContents(contents);
			boardChangeHistory.setWriterID(writerID);
			boardChangeHistory.setWriterNickname(writerNickname);
			boardChangeHistory.setRegisteredDate(registeredDate);

			boardChangeHistoryList.add(boardChangeHistory);
		}

		final BoardChangeHistoryRes boardChangeHistoryRes = new BoardChangeHistoryRes();
		boardChangeHistoryRes.setBoardID(boardID.shortValue());
		boardChangeHistoryRes.setBoardNo(boardNo.longValue());
		boardChangeHistoryRes.setBoardListType(boardListType.getValue());
		boardChangeHistoryRes.setParentNo(parentNo.longValue());
		boardChangeHistoryRes.setGroupNo(groupNo.longValue());
		boardChangeHistoryRes.setBoardChangeHistoryCnt(boardChangeHistoryList.size());
		boardChangeHistoryRes.setBoardChangeHistoryList(boardChangeHistoryList);

		return boardChangeHistoryRes;
	}

}
