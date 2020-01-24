package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;

import org.jooq.Record1;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDownloadFileReq.BoardDownloadFileReq;
import kr.pe.codda.impl.message.BoardDownloadFileRes.BoardDownloadFileRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardDownloadFileReqServerTask extends AbstractServerTask {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public BoardDownloadFileReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardDownloadFileReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public BoardDownloadFileRes doWork(String dbcpName, BoardDownloadFileReq boardDownloadFileReq) throws Exception {
		// FIXME!
		log.info(boardDownloadFileReq.toString());
		
		try {
			ValueChecker.checkValidBoardID(boardDownloadFileReq.getBoardID());
			ValueChecker.checkValidBoardNo(boardDownloadFileReq.getBoardNo());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}

		UByte boardID = UByte.valueOf(boardDownloadFileReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardDownloadFileReq.getBoardNo());
		UByte attachedFileSeq = UByte.valueOf(boardDownloadFileReq.getAttachedFileSeq());
		
		final BoardDownloadFileRes boardDownloadFileRes = new BoardDownloadFileRes();
		
		ServerDBUtil.execute(dbcpName, (conn, dsl) -> {
			
			ServerDBUtil.checkUserAccessRights(conn, dsl, log, "게시글 첨부 파일 다운로드 서비스", PermissionType.MEMBER, boardDownloadFileReq.getRequestedUserID());

			Record1<Byte> boardRecord = dsl.select(SB_BOARD_TB.BOARD_ST).from(SB_BOARD_TB)
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).forUpdate()
					.fetchOne();

			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글이 존재 하지 않습니다").toString();
				throw new ServerTaskException(errorMessage);
			}

			byte boardStateTypeValue = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);

			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(boardStateTypeValue);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시글의 상태 값[").append(boardStateTypeValue).append("]이 잘못되었습니다")
						.toString();
				throw new ServerTaskException(errorMessage);
			}

			if (BoardStateType.DELETE.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글은 삭제된 글입니다").toString();
				throw new ServerTaskException(errorMessage);
			} else if (BoardStateType.BLOCK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글은 관리자에 의해 블락된 글입니다").toString();
				throw new ServerTaskException(errorMessage);
			}

			
			
			Record1<String>  fileListRecord = dsl.select(SB_BOARD_FILELIST_TB.ATTACHED_FNAME)
					.from(SB_BOARD_FILELIST_TB)
			.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo))
			.and(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ.eq(attachedFileSeq)).fetchOne();
			
			if (null == fileListRecord) {
				String errorMessage = "지정한 첨부 파일 정보가 존재하지 않습니다";
				throw new ServerTaskException(errorMessage);
			}
			
			
			String attachedFileName = fileListRecord.getValue(SB_BOARD_FILELIST_TB.ATTACHED_FNAME);

			conn.commit();
			
			boardDownloadFileRes.setBoardID(boardDownloadFileReq.getBoardID());
			boardDownloadFileRes.setBoardNo(boardDownloadFileReq.getBoardNo());
			boardDownloadFileRes.setAttachedFileSeq(boardDownloadFileReq.getAttachedFileSeq());
			boardDownloadFileRes.setAttachedFileName(attachedFileName);	
		});				

		return boardDownloadFileRes;
	}
}
