package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Timestamp;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.server.LoginManagerIF;
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

public class BoardWriteReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<BoardWriteReq, BoardWriteRes> {
	private Logger log = LoggerFactory.getLogger(BoardWriteReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (BoardWriteReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}	
	
	public BoardWriteRes doWork(String dbcpName, final BoardWriteReq boardWriteReq) throws Exception {
		BoardWriteRes outputMessage = ServerDBUtil.execute(
				dbcpName, this, boardWriteReq);
		
		return outputMessage;
	}

	@Override
	public BoardWriteRes doWork(final DSLContext dsl, final BoardWriteReq boardWriteReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}
		if (null == boardWriteReq) {
			throw new ParameterServerTaskException("the parameter boardWriteReq is null");
		}

		// FIXME!
		log.info(boardWriteReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(boardWriteReq.getRequestedUserID());
			ValueChecker.checkValidIP(boardWriteReq.getIp());
			ValueChecker.checkValidBoardID(boardWriteReq.getBoardID());
			ValueChecker.checkValidBoardPasswordHashBase64(boardWriteReq.getPwdHashBase64());
			ValueChecker.checkValidSubject(boardWriteReq.getSubject());
			ValueChecker.checkValidContents(boardWriteReq.getContents());
			ValueChecker.checkValidAttachedFilCount(boardWriteReq.getNewAttachedFileCnt());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		if (boardWriteReq.getNewAttachedFileCnt() > 0) {
			int newAttachedFileCnt = boardWriteReq.getNewAttachedFileCnt();
			List<BoardWriteReq.NewAttachedFile> newAttachedFileList = boardWriteReq.getNewAttachedFileList();

			for (int i = 0; i < newAttachedFileCnt; i++) {
				BoardWriteReq.NewAttachedFile newAttachedFile = newAttachedFileList.get(i);
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

		final UByte boardID = UByte.valueOf(boardWriteReq.getBoardID());
		final UByte nextAttachedFileSeq = UByte.valueOf(boardWriteReq.getNewAttachedFileCnt());
		final String boardPasswordHashBase64 = (null == boardWriteReq.getPwdHashBase64()) ? ""
				: boardWriteReq.getPwdHashBase64();

		final BoardWriteRes boardWriteRes = new BoardWriteRes();

		/**
		 * '게시판 식별자 정보'(SB_BOARD_INFO_TB) 테이블에는 '다음 게시판 번호'가 있어 락을 건후 1 증가 시키고 가져온 값은
		 * '게시판 번호'로 사용한다
		 */
		Record3<String, Byte, UInteger> boardInforRecord = dsl
				.select(SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE,
						SB_BOARD_INFO_TB.NEXT_BOARD_NO)
				.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).forUpdate().fetchOne();

		if (null == boardInforRecord) {

			String errorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(boardID.shortValue())
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		String boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
		byte boardWritePermssionTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE);
		UInteger boardNo = boardInforRecord.get(SB_BOARD_INFO_TB.NEXT_BOARD_NO);

		if (boardNo.longValue() == CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {

			String errorMessage = new StringBuilder().append(boardName).append(" 게시판[").append(boardID.shortValue())
					.append("]은 최대 갯수까지 글이 등록되어 더 이상 글을 추가 할 수 없습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		PermissionType boardWritePermissionType = null;

		try {
			boardWritePermissionType = PermissionType.valueOf("본문글 쓰기", boardWritePermssionTypeValue);
		} catch (IllegalArgumentException e) {

			String errorMessage = e.getMessage();
			throw new RollbackServerTaskException(errorMessage);
		}

		MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(dsl, "게시판 본문 글 등록 서비스",
				boardWritePermissionType, boardWriteReq.getRequestedUserID());

		if (MemberRoleType.GUEST.equals(memberRoleTypeOfRequestedUserID)) {
			if (boardPasswordHashBase64.isEmpty()) {
				String errorMessage = "손님의 경우 반듯이 게시글에 대한 비밀번호를 입력해야 합니다";
				throw new RollbackServerTaskException(errorMessage);
			}
		}

		dsl.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, SB_BOARD_INFO_TB.NEXT_BOARD_NO.add(1))
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();

		int boardInsertCount = dsl.insertInto(SB_BOARD_TB).set(SB_BOARD_TB.BOARD_ID, boardID)
				.set(SB_BOARD_TB.BOARD_NO, boardNo).set(SB_BOARD_TB.GROUP_NO, boardNo)
				.set(SB_BOARD_TB.GROUP_SQ, UShort.valueOf(0)).set(SB_BOARD_TB.PARENT_NO, UInteger.valueOf(0L))
				.set(SB_BOARD_TB.DEPTH, UByte.valueOf(0)).set(SB_BOARD_TB.VIEW_CNT, Integer.valueOf(0))
				.set(SB_BOARD_TB.BOARD_ST, BoardStateType.OK.getValue())
				.set(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ, nextAttachedFileSeq)
				.set(SB_BOARD_TB.PWD_BASE64, (boardPasswordHashBase64.isEmpty() ? null : boardPasswordHashBase64))
				.execute();

		if (0 == boardInsertCount) {
			String errorMessage = "게시판 본문 글 등록이 실패하였습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

		int boardHistoryInsertCount = dsl.insertInto(SB_BOARD_HISTORY_TB).set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID)
				.set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo).set(SB_BOARD_HISTORY_TB.HISTORY_SQ, UByte.valueOf(0))
				.set(SB_BOARD_HISTORY_TB.SUBJECT, boardWriteReq.getSubject())
				.set(SB_BOARD_HISTORY_TB.CONTENTS, boardWriteReq.getContents())
				.set(SB_BOARD_HISTORY_TB.REGISTRANT_ID, boardWriteReq.getRequestedUserID())
				.set(SB_BOARD_HISTORY_TB.IP, boardWriteReq.getIp()).set(SB_BOARD_HISTORY_TB.REG_DT, registeredDate)
				.execute();

		if (0 == boardHistoryInsertCount) {
			String errorMessage = "게시판 본문 글 내용을 저장하는데 실패하였습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		if (boardWriteReq.getNewAttachedFileCnt() > 0) {
			int attachedFileListIndex = 0;

			for (BoardWriteReq.NewAttachedFile attachedFileForRequest : boardWriteReq.getNewAttachedFileList()) {
				int boardFileListInsertCount = dsl.insertInto(SB_BOARD_FILELIST_TB)
						.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID).set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
						.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ, UByte.valueOf(attachedFileListIndex))
						.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME, attachedFileForRequest.getAttachedFileName())
						.set(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE, attachedFileForRequest.getAttachedFileSize())
						.execute();

				if (0 == boardFileListInsertCount) {
					String errorMessage = "게시판 첨부 파일을  저장하는데 실패하였습니다";
					log.warn("게시판 첨부 파일 목록내 인덱스[{}]의 첨부 파일 이름을 저장하는데 실패하였습니다", attachedFileListIndex);

					throw new RollbackServerTaskException(errorMessage);
				}

				attachedFileListIndex++;
			}
		}

		dsl.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.add(1))
				.set(SB_BOARD_INFO_TB.TOTAL, SB_BOARD_INFO_TB.TOTAL.add(1)).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
				.execute();

		ServerDBUtil.insertMemberActivityHistory(dsl, boardWriteReq.getRequestedUserID(),
				memberRoleTypeOfRequestedUserID, MemberActivityType.WRITE, boardID, boardNo, registeredDate);

		boardWriteRes.setBoardID(boardID.shortValue());
		boardWriteRes.setBoardNo(boardNo.longValue());

		return boardWriteRes;
	}
}
