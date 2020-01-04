package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import org.jooq.Record2;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuModifyReq.MenuModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MenuModifyReqServerTask extends AbstractServerTask {	
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);
	
	public MenuModifyReqServerTask() throws DynamicClassCallException {
		super();
	}

	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);		
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}
	
	
	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (MenuModifyReq)inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch(ServerTaskException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());
			
			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=")
					.append(e.getMessage())
					.append(", inObj=")
					.append(inputMessage.toString()).toString();
			
			log.warn(errorMessage, e);
						
			sendErrorOutputMessage("메뉴 수정하는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
		
	public MessageResultRes doWork(String dbcpName, MenuModifyReq menuModifyReq) throws Exception {
		// FIXME!
		log.info(menuModifyReq.toString());
		
		try {
			ValueChecker.checkValidRequestedUserID(menuModifyReq.getRequestedUserID());
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerTaskException(errorMessage);
		}
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, "메뉴 수정 서비스", PermissionType.ADMIN, menuModifyReq.getRequestedUserID());
			
			Record2<String, String> menuRecord = create.select(SB_SITEMENU_TB.MENU_NM, SB_SITEMENU_TB.LINK_URL)
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuModifyReq.getMenuNo())))
			.fetchOne();
			
			if (null == menuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("수정할 메뉴[")
						.append(menuModifyReq.getMenuNo())
						.append("]가 존재하지 않습니다").toString();
				throw new ServerTaskException(errorMessage);
			}
			
			
			String oldMenuName = menuRecord.getValue(SB_SITEMENU_TB.MENU_NM);
			String oldMenuLinkURL = menuRecord.getValue(SB_SITEMENU_TB.LINK_URL);
			
			
			int menuUpdateCount = create.update(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.MENU_NM, menuModifyReq.getMenuName())
			.set(SB_SITEMENU_TB.LINK_URL, menuModifyReq.getLinkURL())
			.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuModifyReq.getMenuNo())))
			.execute();
			
			if (0 == menuUpdateCount) {
				
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("메뉴[")
						.append(menuModifyReq.getMenuNo())
						.append("] 수정이 실패하였습니다").toString();
				throw new ServerTaskException(errorMessage);
			}
			
			conn.commit();	
			
			log.info("메뉴 수정전 {메뉴명[{}], URL[{}]}, 수정후 {메뉴명[{}], URL[{}]}", 
					oldMenuName,
					oldMenuLinkURL,
					menuModifyReq.getMenuName(),
					menuModifyReq.getLinkURL());
		});
		
		
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(menuModifyReq.getMessageID());
		messageResultRes.setIsSuccess(true);		
		messageResultRes.setResultMessage(new StringBuilder()
				.append("메뉴[")
				.append(menuModifyReq.getMenuNo())
				.append("] 수정 처리가 완료되었습니다").toString());
		
		return messageResultRes;
	}
}
