package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;

import org.jooq.DSLContext;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardInfoDeleteReq.BoardInfoDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardInfoDeleteReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<BoardInfoDeleteReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public BoardInfoDeleteReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (BoardInfoDeleteReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public MessageResultRes doWork(final DSLContext dsl, final BoardInfoDeleteReq boardInfoDeleteReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == boardInfoDeleteReq) {
			throw new ParameterServerTaskException("the parameter boardInfoDeleteReq is null");
		}

		// FIXME!
		log.info(boardInfoDeleteReq.toString());

		try {
			ValueChecker.checkValidWriterID(boardInfoDeleteReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		UByte boardID = UByte.valueOf(boardInfoDeleteReq.getBoardID());

		ServerDBUtil.checkUserAccessRights(dsl, "게시판 정보 삭제 서비스", PermissionType.ADMIN,
				boardInfoDeleteReq.getRequestedUserID());

		boolean isBoardInfoRecord = dsl
				.fetchExists(dsl.select().from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)));

		if (!isBoardInfoRecord) {

			String errorMessage = "지정한 게시판 식별자에 대한 게시판 정보가 존재하지 않습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		boolean isBoardRecord = dsl.fetchExists(dsl.select().from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)));

		if (isBoardRecord) {

			String errorMessage = "삭제를 원하는 게시판 식별자를 갖는 게시글이 존재하여 게시판 정보를 삭제 할 수 없습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		int countOfDelete = dsl.deleteFrom(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();

		if (0 == countOfDelete) {

			String errorMessage = new StringBuilder().append("게시판 정보[").append(boardInfoDeleteReq.getBoardID())
					.append("]를 삭제하는데 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(boardInfoDeleteReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("게시판 정보[").append(boardInfoDeleteReq.getBoardID())
				.append("]를 삭제하였습니다").toString());

		return messageResultRes;
	}

}