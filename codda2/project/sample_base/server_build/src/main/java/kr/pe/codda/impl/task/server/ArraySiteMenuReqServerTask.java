package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.util.ArrayList;

import org.jooq.Record;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class ArraySiteMenuReqServerTask extends AbstractServerTask {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);
	// final UInteger rootParnetNo = UInteger.valueOf(0);
	
	public ArraySiteMenuReqServerTask() throws DynamicClassCallException {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (ArraySiteMenuReq)inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch(ServerServiceException e) {
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
						
			sendErrorOutputMessage("배형형 메뉴 조회가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public ArraySiteMenuRes doWork(String dbcpName, ArraySiteMenuReq arraySiteMenuReq) throws Exception {
		// FIXME!
		log.info(arraySiteMenuReq.toString());
		
		try {
			ValueChecker.checkValidRequestedUserID(arraySiteMenuReq.getRequestedUserID());
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		final java.util.List<ArraySiteMenuRes.Menu> menuList = new ArrayList<ArraySiteMenuRes.Menu>();
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, 
					"배열형 메뉴 조회 서비스", PermissionType.ADMIN, arraySiteMenuReq.getRequestedUserID());
						
			
			Result<Record6<UInteger, UInteger, UByte, UByte, String, String>> menuListResult = create.select(SB_SITEMENU_TB.MENU_NO, 
					SB_SITEMENU_TB.PARENT_NO, 
					SB_SITEMENU_TB.DEPTH, 
					SB_SITEMENU_TB.ORDER_SQ,					
					SB_SITEMENU_TB.MENU_NM,
					SB_SITEMENU_TB.LINK_URL)
			.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx1"))
			.orderBy(SB_SITEMENU_TB.ORDER_SQ.asc())
			.fetch();			
						
			// buildMenuListRes(menuList, create, rootParnetNo);		
			for (Record menuListRecord : menuListResult) {
				ArraySiteMenuRes.Menu menu = new ArraySiteMenuRes.Menu();
				
				UInteger menuNo = menuListRecord.getValue(SB_SITEMENU_TB.MENU_NO);
				
				menu.setMenuNo(menuNo.longValue());
				menu.setParentNo(menuListRecord.getValue(SB_SITEMENU_TB.PARENT_NO).longValue());
				menu.setDepth(menuListRecord.getValue(SB_SITEMENU_TB.DEPTH).shortValue());
				menu.setOrderSeq(menuListRecord.getValue(SB_SITEMENU_TB.ORDER_SQ).shortValue());
				menu.setMenuName(menuListRecord.getValue(SB_SITEMENU_TB.MENU_NM));
				menu.setLinkURL(menuListRecord.getValue(SB_SITEMENU_TB.LINK_URL));
				menuList.add(menu);
			}			
			
			conn.commit();
		});
		
		
		ArraySiteMenuRes menuListRes = new ArraySiteMenuRes();
		menuListRes.setMenuList(menuList);
		menuListRes.setCnt(menuList.size());
		
		return menuListRes;	
	}
}
