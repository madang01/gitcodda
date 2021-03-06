package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Timestamp;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.BoardStateType;
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

public class BoardReplyReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<BoardReplyReq, BoardReplyRes> {
	private Logger log = LoggerFactory.getLogger(BoardReplyReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (BoardReplyReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	public BoardReplyRes doWork(String dbcpName, final BoardReplyReq boardReplyReq) throws Exception {
		BoardReplyRes outputMessage = ServerDBUtil.execute(
				dbcpName, this, boardReplyReq);
		
		return outputMessage;
		
	}

	@Override
	public BoardReplyRes doWork(final DSLContext dsl, final BoardReplyReq boardReplyReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == boardReplyReq) {
			throw new ParameterServerTaskException("the parameter boardReplyReq is null");
		}

		// FIXME!
		log.info(boardReplyReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(boardReplyReq.getRequestedUserID());
			ValueChecker.checkValidIP(boardReplyReq.getIp());
			ValueChecker.checkValidBoardID(boardReplyReq.getBoardID());
			ValueChecker.checkValidParentBoardNo(boardReplyReq.getParentBoardNo());
			ValueChecker.checkValidBoardPasswordHashBase64(boardReplyReq.getPwdHashBase64());
			ValueChecker.checkValidContents(boardReplyReq.getContents());
			ValueChecker.checkValidAttachedFilCount(boardReplyReq.getNewAttachedFileCnt());

		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		if (boardReplyReq.getNewAttachedFileCnt() > 0) {
			int newAttachedFileCnt = boardReplyReq.getNewAttachedFileCnt();
			List<BoardReplyReq.NewAttachedFile> newAttachedFileList = boardReplyReq.getNewAttachedFileList();

			for (int i = 0; i < newAttachedFileCnt; i++) {
				BoardReplyReq.NewAttachedFile newAttachedFile = newAttachedFileList.get(i);
				try {
					ValueChecker.checkValidFileName(newAttachedFile.getAttachedFileName());
				} catch (IllegalArgumentException e) {
					String errorMessage = new StringBuilder().append(i).append("번째 파일 이름 유효성 검사 에러 메시지::")
							.append(e.getMessage()).toString();
					throw new ParameterServerTaskException(errorMessage);
				}

				if (newAttachedFile.getAttachedFileSize() <= 0) {
					String errorMessage = new StringBuilder().append(i).append("번째 파일[")
							.append(newAttachedFile.getAttachedFileName()).append("] 크기가 0보다 작거나 같습니다").toString();
					throw new ParameterServerTaskException(errorMessage);
				}
			}
		}

		final UByte boardID = UByte.valueOf(boardReplyReq.getBoardID());
		final UInteger parentBoardNo = UInteger.valueOf(boardReplyReq.getParentBoardNo());
		final String boardPasswordHashBase64 = (null == boardReplyReq.getPwdHashBase64()) ? ""
				: boardReplyReq.getPwdHashBase64();
		final BoardReplyRes boardReplyRes = new BoardReplyRes();

		/**
		 * '게시판 식별자 정보'(SB_BOARD_INFO_TB) 테이블에는 '다음 게시판 번호'가 있어 락을 건후 1 증가 시키고 가져온 값은
		 * '게시판 번호'로 사용한다
		 */
		Record5<String, Byte, Byte, Byte, UInteger> boardInforRecord = dsl
				.select(SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.LIST_TYPE, SB_BOARD_INFO_TB.REPLY_POLICY_TYPE,
						SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, SB_BOARD_INFO_TB.NEXT_BOARD_NO)
				.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).forUpdate().fetchOne();

		if (null == boardInforRecord) {

			String errorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(boardID.shortValue())
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		String boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
		byte boardListTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
		byte boardReplyPolicyTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE);
		byte boardReplyPermssionTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE);
		UInteger boardNo = boardInforRecord.get(SB_BOARD_INFO_TB.NEXT_BOARD_NO);

		if (boardNo.longValue() == CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder().append(boardName).append(" 게시판[").append(boardID.shortValue())
					.append("]은 최대 갯수까지 글이 등록되어 더 이상 글을 추가 할 수 없습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		BoardListType boardListType = null;
		try {
			boardListType = BoardListType.valueOf(boardListTypeValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (BoardListType.TREE.equals(boardListType)) {
			try {
				ValueChecker.checkValidSubject(boardReplyReq.getSubject());
			} catch (IllegalArgumentException e) {

				String errorMessage = e.getMessage();
				throw new RollbackServerTaskException(errorMessage);
			}
		}

		BoardReplyPolicyType boardReplyPolicyType = null;
		try {
			boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardReplyPolicyTypeValue);
		} catch (IllegalArgumentException e) {

			String errorMessage = e.getMessage();
			throw new RollbackServerTaskException(errorMessage);
		}

		PermissionType boardReplyPermissionType = null;

		try {
			boardReplyPermissionType = PermissionType.valueOf("", boardReplyPermssionTypeValue);
		} catch (IllegalArgumentException e) {

			String errorMessage = e.getMessage();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (BoardReplyPolicyType.NO_REPLY.equals(boardReplyPolicyType)) {

			String errorMessage = new StringBuilder().append(boardName).append(" 게시판[").append(boardID)
					.append("]은 댓글 쓰기가 금지되었습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(dsl, "게시판 댓글 등록 서비스",
				boardReplyPermissionType, boardReplyReq.getRequestedUserID());

		if (MemberRoleType.GUEST.equals(memberRoleTypeOfRequestedUserID)) {
			if (boardPasswordHashBase64.isEmpty()) {

				String errorMessage = "손님의 경우 반듯이 게시글에 대한 비밀번호를 입력해야 합니다";
				throw new RollbackServerTaskException(errorMessage);
			}
		}

		dsl.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, SB_BOARD_INFO_TB.NEXT_BOARD_NO.add(1))
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();

		/** 댓글의 부모 게시글에 속한 그룹의 루트 노드에 해당하는 레코드에 락을 건다 */
		UInteger groupNoOfParent = ServerDBUtil.lockRootRecordOfBoardGroup(dsl, boardID, parentBoardNo);

		Record3<UShort, UInteger, UByte> parentBoardRecord = dsl
				.select(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH).from(SB_BOARD_TB)
				.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(parentBoardNo)).fetchOne();

		if (null == parentBoardRecord) {
			String errorMessage = new StringBuilder().append("부모 게시글[boardID=").append(boardID)
					.append(", parentBoardNo=").append(parentBoardNo).append("]이 존재 하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UShort groupSeqOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
		UInteger parentNoOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.PARENT_NO);
		UByte depthOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.DEPTH);

		if (BoardReplyPolicyType.ONLY_ROOT.equals(boardReplyPolicyType)) {
			// 본문에만 댓글
			if (0L != parentNoOfParentBoard.longValue()) {

				String errorMessage = new StringBuilder().append(boardName).append(" 게시판[").append(boardID)
						.append("]은 본문에대한 댓글만이 허용되었습니다").toString();
				throw new RollbackServerTaskException(errorMessage);
			}
		}

		UShort toGroupSeq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(dsl, boardID, groupNoOfParent,
				groupSeqOfParentBoard, depthOfParentBoard);

		Table<Record3<UByte, UInteger, UShort>> b = dsl
				.select(SB_BOARD_TB.BOARD_ID, SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ)
				.from(SB_BOARD_TB.forceIndex("sb_board_idx1")).where(SB_BOARD_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_TB.GROUP_NO.eq(groupNoOfParent)).and(SB_BOARD_TB.GROUP_SQ.ge(toGroupSeq))
				.orderBy(SB_BOARD_TB.GROUP_SQ.desc()).asTable("b");

		dsl.update(SB_BOARD_TB.innerJoin(b).on(SB_BOARD_TB.BOARD_ID.eq(b.field(SB_BOARD_TB.BOARD_ID)))
				.and(SB_BOARD_TB.GROUP_NO.eq(b.field(SB_BOARD_TB.GROUP_NO)))
				.and(SB_BOARD_TB.GROUP_SQ.eq(b.field(SB_BOARD_TB.GROUP_SQ))))
				.set(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.GROUP_SQ.add(1)).execute();

		int boardInsertCount = dsl
				.insertInto(SB_BOARD_TB, SB_BOARD_TB.BOARD_ID, SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.GROUP_NO,
						SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH, SB_BOARD_TB.VIEW_CNT,
						SB_BOARD_TB.BOARD_ST, SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ, SB_BOARD_TB.PWD_BASE64)
				.select(dsl
						.select(SB_BOARD_TB.BOARD_ID, DSL.val(boardNo).as(SB_BOARD_TB.BOARD_NO), SB_BOARD_TB.GROUP_NO,
								DSL.val(toGroupSeq).as(SB_BOARD_TB.GROUP_SQ),
								DSL.val(UInteger.valueOf(boardReplyReq.getParentBoardNo())).as(SB_BOARD_TB.PARENT_NO),
								SB_BOARD_TB.DEPTH.add(1).as(SB_BOARD_TB.DEPTH), DSL.val(0).as(SB_BOARD_TB.VIEW_CNT),
								DSL.val(BoardStateType.OK.getValue()).as(SB_BOARD_TB.BOARD_ST),
								DSL.val(UByte.valueOf(boardReplyReq.getNewAttachedFileCnt()))
										.as(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ),
								DSL.val(boardPasswordHashBase64.isEmpty() ? null : boardPasswordHashBase64)
										.as(SB_BOARD_TB.PWD_BASE64))
						.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID))
						.and(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardReplyReq.getParentBoardNo()))))
				.execute();

		if (0 == boardInsertCount) {
			String errorMessage = "댓글 저장하는데 실패하였습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

		int boardHistoryInsertCount = dsl.insertInto(SB_BOARD_HISTORY_TB).set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID)
				.set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo).set(SB_BOARD_HISTORY_TB.HISTORY_SQ, UByte.valueOf(0))
				.set(SB_BOARD_HISTORY_TB.SUBJECT,
						(BoardListType.ONLY_GROUP_ROOT.equals(boardListType) ? null : boardReplyReq.getSubject()))
				.set(SB_BOARD_HISTORY_TB.CONTENTS, boardReplyReq.getContents())
				.set(SB_BOARD_HISTORY_TB.REGISTRANT_ID, boardReplyReq.getRequestedUserID())
				.set(SB_BOARD_HISTORY_TB.IP, boardReplyReq.getIp()).set(SB_BOARD_HISTORY_TB.REG_DT, registeredDate)
				.execute();

		if (0 == boardHistoryInsertCount) {
			String errorMessage = "댓글 내용을 저장하는데 실패하였습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		if (boardReplyReq.getNewAttachedFileCnt() > 0) {
			int attachedFileListIndex = 0;
			for (BoardReplyReq.NewAttachedFile newAttachedFile : boardReplyReq.getNewAttachedFileList()) {
				int boardFileListInsertCount = dsl.insertInto(SB_BOARD_FILELIST_TB)
						.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID).set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
						.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ, UByte.valueOf(attachedFileListIndex))
						.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME, newAttachedFile.getAttachedFileName())
						.set(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE, newAttachedFile.getAttachedFileSize()).execute();

				if (0 == boardFileListInsertCount) {
					String errorMessage = "댓글의 첨부 파일을  저장하는데 실패하였습니다";
					log.warn("댓글의 첨부 파일 목록내 인덱스[{}]의 첨부 파일 이름을 저장하는데 실패하였습니다", attachedFileListIndex);

					throw new ServerTaskException(errorMessage);
				}

				attachedFileListIndex++;
			}
		}

		if (BoardListType.TREE.equals(boardListType)) {
			// 계층형 목록의 경우 댓글시 목록 갯수와 전체 글수 각각 1증가
			dsl.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.add(1))
					.set(SB_BOARD_INFO_TB.TOTAL, SB_BOARD_INFO_TB.TOTAL.add(1))
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
		} else {
			// 그룹 루트 목록의 경우 댓글시 전체 글수만 1 증가
			dsl.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.TOTAL, SB_BOARD_INFO_TB.TOTAL.add(1))
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
		}

		ServerDBUtil.insertMemberActivityHistory(dsl, boardReplyReq.getRequestedUserID(),
				memberRoleTypeOfRequestedUserID, MemberActivityType.REPLY, boardID, boardNo, registeredDate);

		boardReplyRes.setBoardID(boardID.shortValue());
		boardReplyRes.setBoardNo(boardNo.longValue());

		return boardReplyRes;
	}
}
