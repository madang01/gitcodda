package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.jooq.tables.SbBoardHistoryTb;
import kr.pe.codda.jooq.tables.SbBoardTb;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardListReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<BoardListReq, BoardListRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public BoardListReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.doDBAutoTransationWork(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (BoardListReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public BoardListRes doWork(final DSLContext dsl, final BoardListReq boardListReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == boardListReq) {
			throw new ParameterServerTaskException("the parameter boardListReq is null");
		}

		// FIXME!
		log.info(boardListReq.toString());

		try {
			ValueChecker.checkValidBoardID(boardListReq.getBoardID());
			ValueChecker.checkValidPageNoAndPageSize(boardListReq.getPageNo(), boardListReq.getPageSize());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final UByte boardID = UByte.valueOf(boardListReq.getBoardID());

		final int pageNo = boardListReq.getPageNo();
		final int pageSize = boardListReq.getPageSize();
		final int offset = (pageNo - 1) * pageSize;

		final java.util.List<BoardListRes.Board> boardList = new ArrayList<BoardListRes.Board>();

		final BoardListRes boardListRes = new BoardListRes();

		ServerDBUtil.checkUserAccessRights(dsl, "게시글 목록 조회 서비스", PermissionType.GUEST,
				boardListReq.getRequestedUserID());

		Record3<String, Byte, Byte> boardInforRecord = dsl
				.select(SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.LIST_TYPE, SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE)
				.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).forUpdate().fetchOne();

		if (null == boardInforRecord) {
			String errorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(boardID.shortValue())
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		String boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
		byte boardListTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
		byte boardWritePermissionTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE);
		// byte boardReplyPolicyTypeValue =
		// boardInforRecord.get(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE);

		BoardListType boardListType = null;
		try {
			boardListType = BoardListType.valueOf(boardListTypeValue);

			PermissionType.valueOf("본문 쓰기", boardWritePermissionTypeValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new RollbackServerTaskException(errorMessage);
		}

		int total = dsl.select(SB_BOARD_INFO_TB.CNT).from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
				.fetchOne(0, Integer.class);

		SbBoardTb a = SB_BOARD_TB.as("a");
		SbBoardHistoryTb b = SB_BOARD_HISTORY_TB.as("b");
		SbBoardHistoryTb c = SB_BOARD_HISTORY_TB.as("c");

		HashSet<UInteger> boardNoSet = new HashSet<UInteger>();

		if (BoardListType.TREE.equals(boardListType)) {
			Table<Record4<UByte, UInteger, UShort, Byte>> d = dsl.select(a.BOARD_ID, a.GROUP_NO, a.GROUP_SQ, a.BOARD_ST)
					.from(a.forceIndex("sb_board_idx1")).where(a.BOARD_ID.eq(boardID))
					.and(a.BOARD_ST.eq(BoardStateType.OK.getValue())).orderBy(a.GROUP_NO.desc(), a.GROUP_SQ.desc())
					.offset(offset).limit(pageSize).asTable("b");

			Result<Record1<UInteger>> boardResult = dsl.select(a.BOARD_NO).from(a).innerJoin(d)
					.on(a.BOARD_ID.eq(d.field(SB_BOARD_TB.BOARD_ID))).and(a.GROUP_NO.eq(d.field(SB_BOARD_TB.GROUP_NO)))
					.and(a.GROUP_SQ.eq(d.field(SB_BOARD_TB.GROUP_SQ))).and(a.BOARD_ST.eq(d.field(SB_BOARD_TB.BOARD_ST)))
					.fetch();

			for (Record1<UInteger> boardRecord : boardResult) {
				UInteger boardNo = boardRecord.get(SB_BOARD_TB.BOARD_NO);
				boardNoSet.add(boardNo);
			}

		} else {
			Table<Record4<UByte, UInteger, UInteger, Byte>> d = dsl
					.select(a.BOARD_ID, a.PARENT_NO, a.BOARD_NO, a.BOARD_ST).from(a.forceIndex("sb_board_idx2"))
					.where(a.BOARD_ID.eq(boardID)).and(a.PARENT_NO.eq(UInteger.valueOf(0)))
					.and(a.BOARD_ST.eq(BoardStateType.OK.getValue())).orderBy(a.BOARD_NO.desc()).offset(offset)
					.limit(pageSize).asTable("b");

			Result<Record1<UInteger>> boardResult = dsl.select(a.BOARD_NO).from(a).innerJoin(d)
					.on(a.BOARD_ID.eq(d.field(SB_BOARD_TB.BOARD_ID)))
					.and(a.PARENT_NO.eq(d.field(SB_BOARD_TB.PARENT_NO)))
					.and(a.BOARD_NO.eq(d.field(SB_BOARD_TB.BOARD_NO))).and(a.BOARD_ST.eq(d.field(SB_BOARD_TB.BOARD_ST)))
					.fetch();

			for (Record1<UInteger> boardRecord : boardResult) {
				UInteger boardNo = boardRecord.get(SB_BOARD_TB.BOARD_NO);
				boardNoSet.add(boardNo);
			}

		}

		if (!boardNoSet.isEmpty()) {
			Result<Record13<UInteger, UInteger, UShort, UInteger, UByte, Integer, Byte, Object, String, Timestamp, String, Object, Timestamp>> boardResult = null;
			boardResult = dsl
					.select(a.field(SB_BOARD_TB.BOARD_NO), a.field(SB_BOARD_TB.GROUP_NO), a.field(SB_BOARD_TB.GROUP_SQ),
							a.field(SB_BOARD_TB.PARENT_NO), a.field(SB_BOARD_TB.DEPTH), a.field(SB_BOARD_TB.VIEW_CNT),
							a.field(SB_BOARD_TB.BOARD_ST),
							dsl.selectCount().from(SB_BOARD_VOTE_TB)
									.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(a.field(SB_BOARD_TB.BOARD_ID)))
									.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(a.field(SB_BOARD_TB.BOARD_NO))).asField("votes"),
							b.SUBJECT, b.REG_DT.as("last_mod_date"), c.REGISTRANT_ID,
							dsl.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB)
									.where(SB_MEMBER_TB.USER_ID.eq(c.REGISTRANT_ID))
									.asField(SB_MEMBER_TB.NICKNAME.getName()),
							c.REG_DT.as("first_reg_date"))
					.from(a).innerJoin(c).on(c.BOARD_ID.eq(a.field(SB_BOARD_TB.BOARD_ID)))
					.and(c.BOARD_NO.eq(a.field(SB_BOARD_TB.BOARD_NO))).and(c.HISTORY_SQ.eq(UByte.valueOf(0)))
					.innerJoin(b).on(b.BOARD_ID.eq(a.field(SB_BOARD_TB.BOARD_ID)))
					.and(b.BOARD_NO.eq(a.field(SB_BOARD_TB.BOARD_NO)))
					.and(b.HISTORY_SQ.eq(
							dsl.select(b.HISTORY_SQ.max()).from(b).where(b.BOARD_ID.eq(a.field(SB_BOARD_TB.BOARD_ID)))
									.and(b.BOARD_NO.eq(a.field(SB_BOARD_TB.BOARD_NO)))))
					.where(a.BOARD_ID.eq(boardID)).and(a.BOARD_NO.in(boardNoSet))
					.orderBy(a.field(SB_BOARD_TB.GROUP_NO).desc(), a.field(SB_BOARD_TB.GROUP_SQ).desc()).fetch();

			for (Record boardRecord : boardResult) {
				UInteger boardNo = boardRecord.getValue(SB_BOARD_TB.BOARD_NO);
				UInteger groupNo = boardRecord.getValue(SB_BOARD_TB.GROUP_NO);
				UShort groupSequence = boardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
				UInteger parentNo = boardRecord.getValue(SB_BOARD_TB.PARENT_NO);
				UByte depth = boardRecord.getValue(SB_BOARD_TB.DEPTH);
				int viewCount = boardRecord.getValue(SB_BOARD_TB.VIEW_CNT);
				byte boardStateValue = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);
				int votes = boardRecord.getValue("votes", Integer.class);
				String subject = boardRecord.getValue(SB_BOARD_HISTORY_TB.SUBJECT);
				Timestamp lastModifiedDate = boardRecord.getValue("last_mod_date", Timestamp.class);
				String firstWriterID = boardRecord.getValue(SB_BOARD_HISTORY_TB.REGISTRANT_ID);
				String firstWriterNickName = boardRecord.getValue(SB_MEMBER_TB.NICKNAME);
				Timestamp firstRegisteredDate = boardRecord.getValue("first_reg_date", Timestamp.class);

				if (null == subject) {
					subject = "";
				}

				BoardListRes.Board board = new BoardListRes.Board();
				board.setBoardNo(boardNo.longValue());
				board.setGroupNo(groupNo.longValue());
				board.setGroupSeq(groupSequence.intValue());
				board.setParentNo(parentNo.longValue());
				board.setDepth(depth.shortValue());
				board.setWriterID(firstWriterID);
				board.setViewCount(viewCount);
				board.setBoardSate(boardStateValue);
				board.setRegisteredDate(firstRegisteredDate);
				board.setWriterNickname(firstWriterNickName);
				board.setVotes(votes);
				board.setSubject(subject);
				board.setLastModifiedDate(lastModifiedDate);

				// log.info(board.toString());
				boardList.add(board);
			}
		}

		boardListRes.setBoardID(boardID.shortValue());
		boardListRes.setBoardName(boardName);
		boardListRes.setBoardListType(boardListTypeValue);
		boardListRes.setBoardWritePermissionType(boardWritePermissionTypeValue);
		boardListRes.setPageNo(pageNo);
		boardListRes.setPageSize(pageSize);
		boardListRes.setTotal(total);
		boardListRes.setCnt(boardList.size());
		boardListRes.setBoardList(boardList);

		return boardListRes;
	}
}
