package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuDeleteReq.MenuDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MenuDeleteReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MenuDeleteReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (MenuDeleteReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	public MessageResultRes doWork(final String dbcpName, MenuDeleteReq menuDeleteReq) throws Exception {
		MessageResultRes outputMessage = ServerDBUtil.execute(dbcpName, this, menuDeleteReq);
		
		return outputMessage;
	}

	@Override
	public MessageResultRes doWork(final DSLContext dsl, MenuDeleteReq menuDeleteReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == menuDeleteReq) {
			throw new ParameterServerTaskException("the parameter menuDeleteReq is null");
		}

		// FIXME!
		log.info(menuDeleteReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(menuDeleteReq.getRequestedUserID());			
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final UByte menuSequenceID = SequenceType.MENU.getSequenceID();

		ServerDBUtil.checkUserAccessRights(dsl, "메뉴 삭제 서비스", PermissionType.ADMIN, menuDeleteReq.getRequestedUserID());

		/** 삭제에 따른 '메뉴 순서' 조정을 위한 lock */
		Record menuSeqRecord = dsl.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB).where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID))
				.forUpdate().fetchOne();

		if (null == menuSeqRecord) {

			String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
					.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		Record1<UByte> deleteMenuRecord = dsl.select(SB_SITEMENU_TB.ORDER_SQ).from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuDeleteReq.getMenuNo()))).fetchOne();

		if (null == deleteMenuRecord) {

			String errorMessage = new StringBuilder().append("삭제할 메뉴[").append(menuDeleteReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		boolean isParentMenuRecord = dsl.fetchExists(dsl.selectOne().from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.PARENT_NO.eq(UInteger.valueOf(menuDeleteReq.getMenuNo()))));

		if (isParentMenuRecord) {

			String errorMessage = new StringBuilder().append("자식이 있는 메뉴[").append(menuDeleteReq.getMenuNo())
					.append("]는 삭제 할 수 없습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		int menuDeleteCount = dsl.delete(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuDeleteReq.getMenuNo()))).execute();

		if (0 == menuDeleteCount) {

			String errorMessage = new StringBuilder().append("메뉴[").append(menuDeleteReq.getMenuNo())
					.append("] 삭제가 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		/** 삭제할 메뉴 이후의 순서 보정 */
		dsl.update(SB_SITEMENU_TB).set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.sub(1))
				.where(SB_SITEMENU_TB.ORDER_SQ.gt(deleteMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ))).execute();

		log.info("메뉴[{}] 삭제 처리가 완료되었습니다", menuDeleteReq.getMenuNo());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(menuDeleteReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("메뉴[").append(menuDeleteReq.getMenuNo())
				.append("] 삭제 처리가 완료되었습니다").toString());

		return messageResultRes;
	}
}
