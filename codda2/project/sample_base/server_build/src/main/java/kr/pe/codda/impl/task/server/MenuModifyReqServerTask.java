package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuModifyReq.MenuModifyReq;
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

public class MenuModifyReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<MenuModifyReq, MessageResultRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(
				ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this, (MenuModifyReq) inputMessage);

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public MessageResultRes doWork(final DSLContext dsl, final MenuModifyReq menuModifyReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == menuModifyReq) {
			throw new ParameterServerTaskException("the parameter menuModifyReq is null");
		}

		// FIXME!
		log.info(menuModifyReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(menuModifyReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}
		

		ServerDBUtil.checkUserAccessRights(dsl, "메뉴 수정 서비스", PermissionType.ADMIN, menuModifyReq.getRequestedUserID());

		Record2<String, String> menuRecord = dsl.select(SB_SITEMENU_TB.MENU_NM, SB_SITEMENU_TB.LINK_URL)
				.from(SB_SITEMENU_TB).where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuModifyReq.getMenuNo())))
				.fetchOne();

		if (null == menuRecord) {
			String errorMessage = new StringBuilder().append("수정할 메뉴[").append(menuModifyReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		String oldMenuName = menuRecord.getValue(SB_SITEMENU_TB.MENU_NM);
		String oldMenuLinkURL = menuRecord.getValue(SB_SITEMENU_TB.LINK_URL);

		int menuUpdateCount = dsl.update(SB_SITEMENU_TB).set(SB_SITEMENU_TB.MENU_NM, menuModifyReq.getMenuName())
				.set(SB_SITEMENU_TB.LINK_URL, menuModifyReq.getLinkURL())
				.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuModifyReq.getMenuNo()))).execute();

		if (0 == menuUpdateCount) {
			String errorMessage = new StringBuilder().append("메뉴[").append(menuModifyReq.getMenuNo())
					.append("] 수정이 실패하였습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		log.info("메뉴 수정전 {메뉴명[{}], URL[{}]}, 수정후 {메뉴명[{}], URL[{}]}", oldMenuName, oldMenuLinkURL,
				menuModifyReq.getMenuName(), menuModifyReq.getLinkURL());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(menuModifyReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("메뉴[").append(menuModifyReq.getMenuNo())
				.append("] 수정 처리가 완료되었습니다").toString());

		return messageResultRes;
	}
}
