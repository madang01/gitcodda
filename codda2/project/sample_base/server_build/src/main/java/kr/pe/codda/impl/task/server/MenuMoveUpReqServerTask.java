package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.util.HashSet;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectHavingStep;
import org.jooq.exception.TooManyRowsException;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuMoveUpReq.MenuMoveUpReq;
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

public class MenuMoveUpReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MenuMoveUpReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(MenuMoveUpReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (MenuMoveUpReq) inputMessage);

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	
	public MessageResultRes doWork(final String dbcpName, MenuMoveUpReq menuMoveUpReq) throws Exception {
		MessageResultRes outputMessage = ServerDBUtil.execute(dbcpName, this, menuMoveUpReq);
		
		return outputMessage;
	}

	@Override
	public MessageResultRes doWork(final DSLContext dsl, MenuMoveUpReq menuMoveUpReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == menuMoveUpReq) {
			throw new ParameterServerTaskException("the parameter menuMoveUpReq is null");
		}

		// FIXME!
		log.info(menuMoveUpReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(menuMoveUpReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final UByte menuSequenceID = SequenceType.MENU.getSequenceID();
		final UInteger sourceMenuNo = UInteger.valueOf(menuMoveUpReq.getMenuNo());

		ServerDBUtil.checkUserAccessRights(dsl, "메뉴 상단 이동 서비스", PermissionType.ADMIN,
				menuMoveUpReq.getRequestedUserID());

		/** '메뉴 순서' 를 위한 lock */
		Record menuSeqRecord = dsl.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB).where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID))
				.forUpdate().fetchOne();

		if (null == menuSeqRecord) {
			String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
					.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		/** 상단으로 이동 요청한 메뉴 레코드 가져오기 */
		Record3<UInteger, UByte, UByte> sourceMenuRecord = dsl
				.select(SB_SITEMENU_TB.PARENT_NO, SB_SITEMENU_TB.DEPTH, SB_SITEMENU_TB.ORDER_SQ).from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.MENU_NO.eq(sourceMenuNo)).fetchOne();

		if (null == sourceMenuRecord) {
			String errorMessage = new StringBuilder().append("지정한 메뉴[").append(menuMoveUpReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger sourceParetNo = sourceMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
		UByte sourceDepth = sourceMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);
		UByte sourceOrderSeq = sourceMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ);

		/** 상단으로 이동 요청한 메뉴 보다 한칸 높은 메뉴 레코드 가져오기 */
		Record2<UInteger, UByte> targetMenuRecord = null;

		try {
			SelectHavingStep<Record1<UByte>> lastOlderBrowereQuery = dsl
					.select(SB_SITEMENU_TB.ORDER_SQ.max().as(SB_SITEMENU_TB.ORDER_SQ)).from(SB_SITEMENU_TB)
					.where(SB_SITEMENU_TB.PARENT_NO.eq(sourceParetNo)).and(SB_SITEMENU_TB.DEPTH.eq(sourceDepth))
					.and(SB_SITEMENU_TB.ORDER_SQ.lt(sourceOrderSeq));

			targetMenuRecord = dsl.select(SB_SITEMENU_TB.MENU_NO, SB_SITEMENU_TB.ORDER_SQ).from(SB_SITEMENU_TB)
					.where(SB_SITEMENU_TB.ORDER_SQ.eq(lastOlderBrowereQuery)).fetchOne();

			// .fetchOne();
		} catch (TooManyRowsException e) {

			String errorMessage = new StringBuilder().append("지정한 메뉴[").append(menuMoveUpReq.getMenuNo())
					.append("]보다 한칸 높은 메뉴가 다수 존재합니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		if (null == targetMenuRecord) {

			String errorMessage = new StringBuilder().append("지정한 메뉴[").append(menuMoveUpReq.getMenuNo())
					.append("]보다 한칸 높은 메뉴가 존재하지 않습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger targetMenuNo = targetMenuRecord.getValue(SB_SITEMENU_TB.MENU_NO);
		UByte targetOrderSeq = targetMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ);

		int targetGroupListSize = sourceOrderSeq.shortValue() - targetOrderSeq.shortValue();
		int sourceGroupListSize = 1;
		HashSet<UInteger> sourceGroupMenuNoSet = new HashSet<UInteger>();
		sourceGroupMenuNoSet.add(sourceMenuNo);

		Result<Record2<UInteger, UByte>> sourceGroupSiteMenuResult = dsl
				.select(SB_SITEMENU_TB.MENU_NO, SB_SITEMENU_TB.DEPTH).from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.ORDER_SQ.gt(sourceOrderSeq)).orderBy(SB_SITEMENU_TB.ORDER_SQ.asc()).fetch();

		for (Record2<UInteger, UByte> sourceGroupSiteMenuRecord : sourceGroupSiteMenuResult) {
			UInteger sourceGroupSiteMenuNo = sourceGroupSiteMenuRecord.getValue(SB_SITEMENU_TB.MENU_NO);
			UByte sourceGroupSiteMenuDepth = sourceGroupSiteMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);

			if (sourceGroupSiteMenuDepth.shortValue() <= sourceDepth.shortValue()) {
				break;
			}

			sourceGroupMenuNoSet.add(sourceGroupSiteMenuNo);
			sourceGroupListSize++;
		}

		/**
		 * 상단 이동 요청한 메뉴 그룹을 상단 이동 요청한 메뉴 위치로 전부 이동
		 */
		int sourceMenuUpdateCount = dsl.update(SB_SITEMENU_TB)
				.set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.sub(targetGroupListSize))
				.where(SB_SITEMENU_TB.ORDER_SQ.greaterOrEqual(sourceOrderSeq))
				.and(SB_SITEMENU_TB.ORDER_SQ.lt(UByte.valueOf(sourceOrderSeq.shortValue() + sourceGroupListSize)))
				.execute();

		if (0 == sourceMenuUpdateCount) {

			String errorMessage = new StringBuilder().append("메뉴[").append(menuMoveUpReq.getMenuNo())
					.append("] 순서를 한칸 위로 조정하는데  실패하였습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		/**
		 * 상단 이동 요청한 메뉴보다 한칸 높은 메뉴 그룹을 상단 이동 요청한 메뉴 위치로 전부 이동
		 */
		int targetMenuUpdateCount = dsl.update(SB_SITEMENU_TB)
				.set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.add(sourceGroupListSize))
				.where(SB_SITEMENU_TB.ORDER_SQ.greaterOrEqual(targetOrderSeq))
				.and(SB_SITEMENU_TB.ORDER_SQ.lt(UByte.valueOf(targetOrderSeq.shortValue() + targetGroupListSize)))
				.and(SB_SITEMENU_TB.MENU_NO.notIn(sourceGroupMenuNoSet)).execute();

		if (0 == targetMenuUpdateCount) {

			String errorMessage = new StringBuilder().append("메뉴[").append(menuMoveUpReq.getMenuNo())
					.append("]의 한칸 위 메뉴[").append(targetMenuNo).append("] 순서를 조정하는데  실패하였습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		log.info("원본 메뉴[번호:{}, 순서:{}] <--상단 메뉴 이동에 따른 순서 뒤바뀜--> 목적지 메뉴[번호:{}, 순서:{}]", menuMoveUpReq.getMenuNo(),
				sourceOrderSeq, targetMenuNo, targetOrderSeq);

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(menuMoveUpReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("메뉴[").append(menuMoveUpReq.getMenuNo())
				.append("]의 상단 이동 처리가 완료되었습니다").toString());

		return messageResultRes;
	}
}
