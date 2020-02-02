package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardBlockReq.BoardBlockReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
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

public class BoardBlockReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<BoardBlockReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public BoardBlockReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		/*
		 * AbstractMessage outputMessage =
		 * doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardBlockReq)
		 * inputMessage);
		 */

		AbstractMessage outputMessage = ServerDBUtil.doDBAutoTransationWork(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (BoardBlockReq) inputMessage);

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	private boolean isChildNode(UShort fromGroupSeq, UShort toGroupSeq) {
		return (fromGroupSeq.intValue() != toGroupSeq.intValue());
	}

	public MessageResultRes doWork(final DSLContext dsl, final BoardBlockReq boardBlockReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}
		if (null == boardBlockReq) {
			throw new ParameterServerTaskException("the parameter boardBlockReq is null");
		}

		// FIXME!
		log.info(boardBlockReq.toString());
		
		try {
			ValueChecker.checkValidRequestedUserID(boardBlockReq.getRequestedUserID());
			ValueChecker.checkValidIP(boardBlockReq.getIp());
			ValueChecker.checkValidBoardID(boardBlockReq.getBoardID());
			ValueChecker.checkValidBoardNo(boardBlockReq.getBoardNo());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		String requestedUserID = boardBlockReq.getRequestedUserID();
		UByte boardID = UByte.valueOf(boardBlockReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardBlockReq.getBoardNo());

		StringBuilder resultMessageStringBuilder = new StringBuilder();

		ServerDBUtil.checkUserAccessRights(dsl, "게시글 차단 서비스", PermissionType.ADMIN, requestedUserID);

		Record2<String, Byte> boardInforRecord = dsl.select(SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.LIST_TYPE)
				.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne();

		if (null == boardInforRecord) {

			String errorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(boardID.shortValue())
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		String boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
		byte boardListTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);

		BoardListType boardListType = null;

		try {
			boardListType = BoardListType.valueOf(boardListTypeValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new RollbackServerTaskException(errorMessage);
		}

		/** 차단할 게시글에 속한 그룹의 루트 노드에 해당하는 레코드에 락을 건다 */
		UInteger groupNo = ServerDBUtil.lockRootRecordOfBoardGroup(dsl, boardID, boardNo);

		Record4<UShort, UInteger, UByte, Byte> boardRecord = dsl
				.select(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH, SB_BOARD_TB.BOARD_ST)
				.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
				.fetchOne();

		if (null == boardRecord) {

			String errorMessage = "2.해당 게시글이 존재 하지 않습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		UShort groupSeq = boardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
		UInteger parentNo = boardRecord.getValue(SB_BOARD_TB.PARENT_NO);
		UByte depth = boardRecord.getValue(SB_BOARD_TB.DEPTH);
		byte boardState = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);

		BoardStateType boardStateType = null;
		try {
			boardStateType = BoardStateType.valueOf(boardState);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("게시글의 상태 값[").append(boardState).append("]이 잘못되었습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (BoardStateType.DELETE.equals(boardStateType)) {

			String errorMessage = "해당 게시글은 삭제된 글입니다";
			throw new RollbackServerTaskException(errorMessage);
		} else if (BoardStateType.BLOCK.equals(boardStateType)) {
			String errorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";
			throw new RollbackServerTaskException(errorMessage);
		} else if (BoardStateType.TREEBLOCK.equals(boardStateType)) {
			String errorMessage = "해당 게시글은 관리자에 의해 차단된 글에 속한 글입니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		UShort fromGroupSeq = groupSeq;
		// UShort toGroupSeq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(dsl,
		// boardID, groupSeq, parentNo);
		UShort toGroupSeq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(dsl, boardID, groupNo, groupSeq, depth);

		int updateCount = dsl.update(SB_BOARD_TB).set(SB_BOARD_TB.BOARD_ST, BoardStateType.BLOCK.getValue())
				.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).execute();

		if (isChildNode(fromGroupSeq, toGroupSeq)) {
			updateCount += dsl.update(SB_BOARD_TB).set(SB_BOARD_TB.BOARD_ST, BoardStateType.TREEBLOCK.getValue())
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.GROUP_NO.eq(groupNo))
					.and(SB_BOARD_TB.GROUP_SQ.lt(fromGroupSeq)).and(SB_BOARD_TB.GROUP_SQ.ge(toGroupSeq))
					.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue())).execute();
		}

		if (BoardListType.TREE.equals(boardListType)) {
			// 계층형 목록일때 목록 갯수에 정상 상태에서 차단상태로된 모든 갯수 감소
			dsl.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.sub(updateCount))
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
		} else {
			// 그룹 루트만으로 이루어진 목록일때 그룹 루트에 대한 차단시에만 목록 갯수 1 감소
			if (0L == parentNo.longValue()) {
				dsl.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.sub(1))
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
			}
		}

		ServerDBUtil.insertSiteLog(dsl, requestedUserID, boardBlockReq.toString(),
				new java.sql.Timestamp(System.currentTimeMillis()), boardBlockReq.getIp());

		resultMessageStringBuilder.append(boardName).append(" 게시판[").append(boardID).append("]의 글")
				.append(boardNo.longValue()).append("]을 차단했습니다");

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(boardBlockReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(resultMessageStringBuilder.toString());

		return messageResultRes;
	}

}
