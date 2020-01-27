package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;

import org.jooq.Record1;
import org.jooq.types.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardInfoModifyReq.BoardInfoModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * WARNING! 게시판 수정은 비용이 많이 드는 작업이므로 운영중에 사용하지 말것  
 * 
 * @author Won Jonghoon
 *
 */
public class BoardInfoModifyReqServerTask extends AbstractServerTask {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public BoardInfoModifyReqServerTask() throws DynamicClassCallException {
		super();
	}
	
	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
				(BoardInfoModifyReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	public MessageResultRes doWork(String dbcpName, BoardInfoModifyReq boardInfoModifyReq) throws Exception {
		// FIXME!
		log.info(boardInfoModifyReq.toString());
		
		final BoardReplyPolicyType boardReplyPolicyType;
		final PermissionType boardWritePermissionType;
		final PermissionType boardReplyPermissionType;
		
		try {
			ValueChecker.checkValidWriterID(boardInfoModifyReq.getRequestedUserID());
			ValueChecker.checkValidBoardName(boardInfoModifyReq.getBoardName());
			
			boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardInfoModifyReq.getBoardReplyPolicyType());
			
			boardWritePermissionType = PermissionType.valueOf("본문 쓰기", boardInfoModifyReq.getBoardWritePermissionType());
			boardReplyPermissionType = PermissionType.valueOf("댓글 쓰기", boardInfoModifyReq.getBoardReplyPermissionType());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}		
		
		UByte boardID = UByte.valueOf(boardInfoModifyReq.getBoardID());
		String boardName = boardInfoModifyReq.getBoardName();
		
		ServerDBUtil.execute(dbcpName, (conn, dsl) -> {
			
			ServerDBUtil.checkUserAccessRights(dsl, log, "게시판 정보 수정 서비스", PermissionType.ADMIN, boardInfoModifyReq.getRequestedUserID());
			
			Record1<UByte> boardInfoRecord = dsl.select(SB_BOARD_INFO_TB.BOARD_ID).from(SB_BOARD_INFO_TB)
			.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne();

			if (null == boardInfoRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "지정한 게시판 식별자에 대한 게시판 정보가 존재하지 않습니다";
				throw new ServerTaskException(errorMessage);
			}			
			
			
			int countOfUpdate = dsl.update(SB_BOARD_INFO_TB)
						.set(SB_BOARD_INFO_TB.BOARD_NAME, boardName)
						.set(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, boardReplyPolicyType.getValue())
						.set(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, boardWritePermissionType.getValue())
						.set(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, boardReplyPermissionType.getValue())
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();									
			
			if (0 == countOfUpdate) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("게시판 정보[")
						.append(boardInfoModifyReq.getBoardID())
						.append("]를 수정하는데 실패하였습니다").toString();
				throw new ServerTaskException(errorMessage);
			}
						
			conn.commit();
		});
		

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(boardInfoModifyReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("게시판 정보[")
				.append(boardInfoModifyReq.getBoardID())
				.append("]를 수정하였습니다").toString());
		

		return messageResultRes;
	}

}
