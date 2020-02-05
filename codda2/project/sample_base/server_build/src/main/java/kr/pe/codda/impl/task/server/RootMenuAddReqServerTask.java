package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.DBAutoCommitTaskIF;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.ParameterServerTaskException;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.RollbackServerTaskException;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class RootMenuAddReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<RootMenuAddReq, RootMenuAddRes> {

	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this,
				(RootMenuAddReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	
	public RootMenuAddRes doWork(final String dbcpName, final RootMenuAddReq rootMenuAddReq) throws Exception {
		
		RootMenuAddRes outputMessage = ServerDBUtil.execute(dbcpName, this,
				rootMenuAddReq);
		
		return outputMessage;
	}
	
	@Override
	public RootMenuAddRes doWork(final DSLContext dsl, final RootMenuAddReq rootMenuAddReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == rootMenuAddReq) {
			throw new ParameterServerTaskException("the parameter rootMenuAddReq is null");
		}

		// FIXME!
		log.info(rootMenuAddReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(rootMenuAddReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}

		final UByte menuSequenceID = SequenceType.MENU.getSequenceID();
		final RootMenuAddRes rootMenuAddRes = new RootMenuAddRes();

		ServerDBUtil.checkUserAccessRights(dsl, "루트 메뉴 추가 서비스", PermissionType.ADMIN,
				rootMenuAddReq.getRequestedUserID());

		/** '메뉴 순서' 를 위한 lock */
		Record menuSeqRecord = dsl.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB).where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID))
				.forUpdate().fetchOne();

		if (null == menuSeqRecord) {
			String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
					.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger rootMenuNo = menuSeqRecord.getValue(SB_SEQ_TB.SQ_VALUE);

		if (rootMenuNo.longValue() == UInteger.MAX_VALUE) {

			String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID).append("]의 시퀀스가 최대치에 도달하였습니다")
					.toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		int seqUpdateCnt = dsl.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(1))
				.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID)).execute();

		if (0 == seqUpdateCnt) {
			String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID).append("]의 시퀀스 갱신하는데 실패하였습니다")
					.toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		short newOrderSeq = dsl
				.select(JooqSqlUtil.getIfField(SB_SITEMENU_TB.ORDER_SQ.max(), 0, SB_SITEMENU_TB.ORDER_SQ.max().add(1)))
				.from(SB_SITEMENU_TB).fetchOne(0, Short.class);

		if (newOrderSeq >= CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {

			String errorMessage = "메뉴 갯수가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		int rootMenuInsertCount = dsl.insertInto(SB_SITEMENU_TB).set(SB_SITEMENU_TB.MENU_NO, rootMenuNo)
				.set(SB_SITEMENU_TB.PARENT_NO, UInteger.valueOf(0)).set(SB_SITEMENU_TB.DEPTH, UByte.valueOf(0))
				.set(SB_SITEMENU_TB.ORDER_SQ, UByte.valueOf(newOrderSeq))
				.set(SB_SITEMENU_TB.MENU_NM, rootMenuAddReq.getMenuName())
				.set(SB_SITEMENU_TB.LINK_URL, rootMenuAddReq.getLinkURL()).execute();

		if (0 == rootMenuInsertCount) {

			String errorMessage = new StringBuilder().append("루트 메뉴 추가하는데 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		rootMenuAddRes.setMenuNo(rootMenuNo.longValue());
		rootMenuAddRes.setOrderSeq(newOrderSeq);

		log.info("루트 메뉴[번호:{}, 메뉴명:{}, URL:{}] 추가 완료", rootMenuAddRes.getMenuNo(), rootMenuAddReq.getMenuName(),
				rootMenuAddReq.getLinkURL());

		return rootMenuAddRes;

	}

}
