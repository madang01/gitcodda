package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
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

public class ChildMenuAddReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<ChildMenuAddReq, ChildMenuAddRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (ChildMenuAddReq) inputMessage);
		toLetterCarrier.addSyncOutputMessage(outputMessage);

	}
	

	public ChildMenuAddRes doWork(final String dbcpName, final ChildMenuAddReq childMenuAddReq) throws Exception {
		
		ChildMenuAddRes outputMessage = ServerDBUtil.execute(dbcpName, this,
				childMenuAddReq);
		
		return outputMessage;
	}

	@Override
	public ChildMenuAddRes doWork(final DSLContext dsl, ChildMenuAddReq childMenuAddReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == childMenuAddReq) {
			throw new ParameterServerTaskException("the parameter childMenuAddReq is null");
		}

		// FIXME!
		log.info(childMenuAddReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(childMenuAddReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}

		final UByte menuSequenceID = SequenceType.MENU.getSequenceID();
		final UInteger parentMenuNo = UInteger.valueOf(childMenuAddReq.getParentNo());
		final ChildMenuAddRes childMenuAddRes = new ChildMenuAddRes();

		ServerDBUtil.checkUserAccessRights(dsl, "자식 메뉴 등록 서비스", PermissionType.ADMIN,
				childMenuAddReq.getRequestedUserID());

		/** 자식 메뉴 추가에 따른 '메뉴 순서' 보장을 위한 lock */
		Record menuSeqRecord = dsl.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB).where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID))
				.forUpdate().fetchOne();

		if (null == menuSeqRecord) {
			String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
					.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger childMenuNo = menuSeqRecord.getValue(SB_SEQ_TB.SQ_VALUE);

		if (childMenuNo.longValue() == UInteger.MAX_VALUE) {

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

		int numberOfMenu = dsl.selectCount().from(SB_SITEMENU_TB).fetchOne().value1();

		if (numberOfMenu >= CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {

			String errorMessage = "메뉴 갯수가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다";
			throw new RollbackServerTaskException(errorMessage);
		}

		Record3<UInteger, UByte, UByte> parentMenuRecord = dsl
				.select(SB_SITEMENU_TB.PARENT_NO, SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.DEPTH).from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.MENU_NO.eq(parentMenuNo)).fetchOne();

		if (null == parentMenuRecord) {

			String errorMessage = new StringBuilder().append("부모 메뉴[").append(parentMenuNo).append("]가 존재하지 않습니다")
					.toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger parentParnetNo = parentMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
		UByte parentOrderSeq = parentMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ);
		UByte parentDepth = parentMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);

		if (parentDepth.shortValue() >= CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("자식 메뉴 깊이가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UByte fromOrderSeq = ServerDBUtil.getToOrderSeqOfRelativeRootMenu(dsl, parentOrderSeq, parentParnetNo);
		UByte newOrderSeq = UByte.valueOf(fromOrderSeq.shortValue() + 1);

		dsl.update(SB_SITEMENU_TB).set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.add(1))
				.where(SB_SITEMENU_TB.ORDER_SQ.ge(newOrderSeq)).execute();

		int childMenuInsertCount = dsl.insertInto(SB_SITEMENU_TB).set(SB_SITEMENU_TB.MENU_NO, childMenuNo)
				.set(SB_SITEMENU_TB.PARENT_NO, parentMenuNo)
				.set(SB_SITEMENU_TB.DEPTH, UByte.valueOf(parentDepth.shortValue() + 1))
				.set(SB_SITEMENU_TB.ORDER_SQ, newOrderSeq).set(SB_SITEMENU_TB.MENU_NM, childMenuAddReq.getMenuName())
				.set(SB_SITEMENU_TB.LINK_URL, childMenuAddReq.getLinkURL()).execute();

		if (0 == childMenuInsertCount) {
			String errorMessage = new StringBuilder().append("자식 메뉴 추가하는데 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		childMenuAddRes.setMenuNo(childMenuNo.longValue());
		childMenuAddRes.setOrderSeq(newOrderSeq.shortValue());

		log.info("자식 메뉴[부모 메뉴번호:{}, 번호:{}, 메뉴명:{}, URL:{}] 추가 완료", childMenuAddReq.getParentNo(),
				childMenuAddRes.getMenuNo(), childMenuAddReq.getMenuName(), childMenuAddReq.getLinkURL());

		return childMenuAddRes;

	}

}