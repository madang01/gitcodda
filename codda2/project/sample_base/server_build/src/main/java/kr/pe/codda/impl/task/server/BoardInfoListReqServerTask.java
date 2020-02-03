package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardInfoListReq.BoardInfoListReq;
import kr.pe.codda.impl.message.BoardInfoListRes.BoardInfoListRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardInfoListReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<BoardInfoListReq, BoardInfoListRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public BoardInfoListReqServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (BoardInfoListReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public BoardInfoListRes doWork(final DSLContext dsl, BoardInfoListReq boardInfoListReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == boardInfoListReq) {
			throw new ParameterServerTaskException("the parameter boardInfoListReq is null");
		}

		// FIXME!
		log.info(boardInfoListReq.toString());

		try {
			ValueChecker.checkValidWriterID(boardInfoListReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		List<BoardInfoListRes.BoardInfo> boardInfoList = new ArrayList<BoardInfoListRes.BoardInfo>();

		ServerDBUtil.checkUserAccessRights(dsl, "게시판 정보 목록 조회 서비스", PermissionType.ADMIN,
				boardInfoListReq.getRequestedUserID());

		Result<Record9<UByte, String, Byte, Byte, Byte, Byte, Long, Long, UInteger>> boardInfoListResult = dsl
				.select(SB_BOARD_INFO_TB.BOARD_ID, SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.LIST_TYPE,
						SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE,
						SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.TOTAL,
						SB_BOARD_INFO_TB.NEXT_BOARD_NO)
				.from(SB_BOARD_INFO_TB).fetch();

		for (Record9<UByte, String, Byte, Byte, Byte, Byte, Long, Long, UInteger> boardInfoRecord : boardInfoListResult) {
			UByte boardID = boardInfoRecord.get(SB_BOARD_INFO_TB.BOARD_ID);
			String boardName = boardInfoRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
			byte boardListTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
			byte boardReplyPolicyTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE);
			byte boardWritePermissionTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE);
			byte boardReplyPermissionTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE);
			long cnt = boardInfoRecord.get(SB_BOARD_INFO_TB.CNT);
			long total = boardInfoRecord.get(SB_BOARD_INFO_TB.TOTAL);
			UInteger nextBoardNo = boardInfoRecord.get(SB_BOARD_INFO_TB.NEXT_BOARD_NO);

			BoardInfoListRes.BoardInfo boardInfo = new BoardInfoListRes.BoardInfo();
			boardInfo.setBoardID(boardID.shortValue());
			boardInfo.setBoardName(boardName);
			boardInfo.setBoardListType(boardListTypeValue);
			boardInfo.setBoardReplyPolicyType(boardReplyPolicyTypeValue);
			boardInfo.setBoardWritePermissionType(boardWritePermissionTypeValue);
			boardInfo.setBoardReplyPermissionType(boardReplyPermissionTypeValue);
			boardInfo.setCnt(cnt);
			boardInfo.setTotal(total);
			boardInfo.setNextBoardNo(nextBoardNo.longValue());

			boardInfoList.add(boardInfo);
		}

		BoardInfoListRes boardInfoListRes = new BoardInfoListRes();
		boardInfoListRes.setCnt(boardInfoList.size());
		boardInfoListRes.setBoardInfoList(boardInfoList);

		return boardInfoListRes;
	}
}
