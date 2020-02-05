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
import kr.pe.codda.impl.message.MenuMoveDownReq.MenuMoveDownReq;
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

public class MenuMoveDownReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MenuMoveDownReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (MenuMoveDownReq) inputMessage);

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	public MessageResultRes doWork(final String dbcpName, final MenuMoveDownReq menuMoveDownReq) throws Exception {
		MessageResultRes outputMessage = ServerDBUtil.execute(dbcpName, this, menuMoveDownReq);
		
		return outputMessage;
	}
	
	@Override
	public MessageResultRes doWork(final DSLContext dsl, final MenuMoveDownReq menuMoveDownReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == menuMoveDownReq) {
			throw new ParameterServerTaskException("the parameter menuMoveDownReq is null");
		}
		// FIXME!
		log.info(menuMoveDownReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(menuMoveDownReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		final UByte menuSequenceID = SequenceType.MENU.getSequenceID();
		final UInteger sourceMenuNo = UInteger.valueOf(menuMoveDownReq.getMenuNo());

		ServerDBUtil.checkUserAccessRights(dsl, "메뉴 하단 이동 서비스", PermissionType.ADMIN,
				menuMoveDownReq.getRequestedUserID());

		/** '메뉴 순서' 를 위한 lock */
		Record menuSeqRecord = dsl.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB).where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID))
				.forUpdate().fetchOne();

		if (null == menuSeqRecord) {
			String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
					.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		Record3<UInteger, UByte, UByte> sourceMenuRecord = dsl
				.select(SB_SITEMENU_TB.PARENT_NO, SB_SITEMENU_TB.DEPTH, SB_SITEMENU_TB.ORDER_SQ).from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.MENU_NO.eq(sourceMenuNo)).fetchOne();

		if (null == sourceMenuRecord) {
			String errorMessage = new StringBuilder().append("지정한 메뉴[").append(menuMoveDownReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger sourceParetNo = sourceMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
		UByte sourceDepth = sourceMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);
		UByte sourceOrderSeq = sourceMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ);

		Record3<UInteger, UByte, UByte> targetMenuRecord = null;
		try {
			SelectHavingStep<Record1<UByte>> firstYoungerBrowereQuery = dsl
					.select(SB_SITEMENU_TB.ORDER_SQ.min().as(SB_SITEMENU_TB.ORDER_SQ)).from(SB_SITEMENU_TB)
					.where(SB_SITEMENU_TB.PARENT_NO.eq(sourceParetNo)).and(SB_SITEMENU_TB.DEPTH.eq(sourceDepth))
					.and(SB_SITEMENU_TB.ORDER_SQ.gt(sourceOrderSeq));

			targetMenuRecord = dsl.select(SB_SITEMENU_TB.MENU_NO, SB_SITEMENU_TB.DEPTH, SB_SITEMENU_TB.ORDER_SQ)
					.from(SB_SITEMENU_TB).where(SB_SITEMENU_TB.ORDER_SQ.eq(firstYoungerBrowereQuery)).fetchOne();
		} catch (TooManyRowsException e) {
			String errorMessage = new StringBuilder().append("지정한 메뉴[").append(menuMoveDownReq.getMenuNo())
					.append("]보다 한칸 낮은 메뉴가 다수 존재합니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		if (null == targetMenuRecord) {
			String errorMessage = new StringBuilder().append("지정한 메뉴[").append(menuMoveDownReq.getMenuNo())
					.append("]보다 한칸 낮은 메뉴가 존재하지 않습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger targetMenuNo = targetMenuRecord.getValue(SB_SITEMENU_TB.MENU_NO);
		// UInteger targetParetNo = targetMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
		UByte targetDepth = targetMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);
		UByte targetOrderSeq = targetMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ);

		int sourceGroupListSize = targetOrderSeq.shortValue() - sourceOrderSeq.shortValue();
		int targetGroupListSize = 1;
		HashSet<UInteger> targetGroupMenuNoSet = new HashSet<UInteger>();
		targetGroupMenuNoSet.add(targetMenuNo);

		Result<Record2<UInteger, UByte>> targetGroupSiteMenuResult = dsl
				.select(SB_SITEMENU_TB.MENU_NO, SB_SITEMENU_TB.DEPTH).from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.ORDER_SQ.gt(targetOrderSeq)).orderBy(SB_SITEMENU_TB.ORDER_SQ.asc()).fetch();

		for (Record2<UInteger, UByte> targetGroupSiteMenuRecord : targetGroupSiteMenuResult) {
			UInteger targetGroupSiteMenuNo = targetGroupSiteMenuRecord.getValue(SB_SITEMENU_TB.MENU_NO);
			UByte targetGroupSiteMenuDepth = targetGroupSiteMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);

			if (targetGroupSiteMenuDepth.shortValue() <= targetDepth.shortValue()) {
				break;
			}

			targetGroupMenuNoSet.add(targetGroupSiteMenuNo);
			targetGroupListSize++;
		}

		/**
		 * 하단 이동 요청한 메뉴보다 한칸 낮은 메뉴 그룹을 하단 이동 요청한 메뉴 위치로 전부 이동
		 */
		int targetMenuUpdateCount = dsl.update(SB_SITEMENU_TB)
				.set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.sub(sourceGroupListSize))
				.where(SB_SITEMENU_TB.ORDER_SQ.greaterOrEqual(targetOrderSeq))
				.and(SB_SITEMENU_TB.ORDER_SQ.lt(UByte.valueOf(targetOrderSeq.shortValue() + targetGroupListSize)))
				.execute();

		if (0 == targetMenuUpdateCount) {

			String errorMessage = new StringBuilder().append("메뉴[").append(menuMoveDownReq.getMenuNo())
					.append("]의 한칸 위 메뉴[").append(targetMenuNo).append("] 순서를 조정하는데  실패하였습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		/**
		 * 하단 이동 요청한 메뉴 그룹을 하단 이동 요청한 메뉴 위치로 전부 이동
		 */
		int sourceMenuUpdateCount = dsl.update(SB_SITEMENU_TB)
				.set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.add(targetGroupListSize))
				.where(SB_SITEMENU_TB.ORDER_SQ.greaterOrEqual(sourceOrderSeq))
				.and(SB_SITEMENU_TB.ORDER_SQ.lt(UByte.valueOf(sourceOrderSeq.shortValue() + sourceGroupListSize)))
				.and(SB_SITEMENU_TB.MENU_NO.notIn(targetGroupMenuNoSet)).execute();

		if (0 == sourceMenuUpdateCount) {
			String errorMessage = new StringBuilder().append("메뉴[").append(menuMoveDownReq.getMenuNo())
					.append("] 순서를 한칸 위로 조정하는데  실패하였습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		log.info("원본 메뉴[번호:{}, 순서:{}] <--하단 메뉴 이동에 따른 순서 뒤바뀜--> 목적지 메뉴[번호:{}, 순서:{}]", menuMoveDownReq.getMenuNo(),
				sourceOrderSeq, targetMenuNo, targetOrderSeq);

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(menuMoveDownReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("메뉴[").append(menuMoveDownReq.getMenuNo())
				.append("]의 상단 이동 처리가 완료되었습니다").toString());

		return messageResultRes;
	}
}
