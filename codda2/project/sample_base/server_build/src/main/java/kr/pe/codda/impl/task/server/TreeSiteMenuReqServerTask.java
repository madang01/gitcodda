package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.util.ArrayList;
import java.util.HashMap;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.TreeSiteMenuReq.TreeSiteMenuReq;
import kr.pe.codda.impl.message.TreeSiteMenuRes.TreeSiteMenuRes;
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

public class TreeSiteMenuReqServerTask extends AbstractServerTask
		implements DBAutoCommitTaskIF<TreeSiteMenuReq, TreeSiteMenuRes> {
	private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);
	// final UInteger rootParnetNo = UInteger.valueOf(0);

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		AbstractMessage outputMessage = ServerDBUtil.execute(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, this,
				(TreeSiteMenuReq) inputMessage);

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	
	public TreeSiteMenuRes doWork(final String dbcpName, final TreeSiteMenuReq treeSiteMenuReq) throws Exception {
		TreeSiteMenuRes outputMessage = ServerDBUtil.execute(dbcpName, this,treeSiteMenuReq);
		
		return outputMessage;
	}

	@Override
	public TreeSiteMenuRes doWork(final DSLContext dsl, final TreeSiteMenuReq treeSiteMenuReq) throws Exception {
		if (null == dsl) {
			throw new ParameterServerTaskException("the parameter dsl is null");
		}

		if (null == treeSiteMenuReq) {
			throw new ParameterServerTaskException("the parameter treeSiteMenuReq is null");
		}

		// FIXME!
		log.info(treeSiteMenuReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(treeSiteMenuReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ParameterServerTaskException(errorMessage);
		}

		java.util.List<TreeSiteMenuRes.Menu> rootMenuList = new ArrayList<TreeSiteMenuRes.Menu>();

		ServerDBUtil.checkUserAccessRights(dsl, "계층형 메뉴 조회 서비스", PermissionType.ADMIN,
				treeSiteMenuReq.getRequestedUserID());

		HashMap<UInteger, TreeSiteMenuRes.Menu> menuHash = new HashMap<UInteger, TreeSiteMenuRes.Menu>();

		Result<Record6<UInteger, UInteger, UByte, UByte, String, String>> menuListResult = dsl
				.select(SB_SITEMENU_TB.MENU_NO, SB_SITEMENU_TB.PARENT_NO, SB_SITEMENU_TB.DEPTH, SB_SITEMENU_TB.ORDER_SQ,
						SB_SITEMENU_TB.MENU_NM, SB_SITEMENU_TB.LINK_URL)
				.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx1")).orderBy(SB_SITEMENU_TB.ORDER_SQ.asc()).fetch();

		for (Record menuListRecord : menuListResult) {
			TreeSiteMenuRes.Menu menu = new TreeSiteMenuRes.Menu();

			UInteger menuNo = menuListRecord.getValue(SB_SITEMENU_TB.MENU_NO);
			UInteger parentNo = menuListRecord.getValue(SB_SITEMENU_TB.PARENT_NO);

			menu.setMenuNo(menuNo.longValue());
			menu.setParentNo(parentNo.longValue());
			menu.setDepth(menuListRecord.getValue(SB_SITEMENU_TB.DEPTH).shortValue());
			menu.setOrderSeq(menuListRecord.getValue(SB_SITEMENU_TB.ORDER_SQ).shortValue());
			menu.setMenuName(menuListRecord.getValue(SB_SITEMENU_TB.MENU_NM));
			menu.setLinkURL(menuListRecord.getValue(SB_SITEMENU_TB.LINK_URL));

			java.util.List<TreeSiteMenuRes.Menu> childMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
			menu.setChildMenuListSize(childMenuList.size());
			menu.setChildMenuList(childMenuList);

			menuHash.put(menuNo, menu);

			if (menu.getDepth() == 0) {
				rootMenuList.add(menu);
			} else {
				TreeSiteMenuRes.Menu parentMenu = menuHash.get(parentNo);
				if (null == parentMenu) {
					String errorMessage = "정렬된 메뉴 목록이 잘못 되었습니다";
					String debugMessage = new StringBuilder(errorMessage).append(", 정렬된 메뉴 목록에서 부모 [").append(parentNo)
							.append("]가 있는 메뉴[").append(menuNo).append("]의 부모가 해쉬에 존재하지 않습니다").toString();
					log.info(debugMessage);

					throw new RollbackServerTaskException(errorMessage);
				}
				java.util.List<TreeSiteMenuRes.Menu> parentChildMenuList = parentMenu.getChildMenuList();
				parentChildMenuList.add(menu);
				parentMenu.setChildMenuListSize(parentChildMenuList.size());
			}
		}

		TreeSiteMenuRes treeSiteMenuRes = new TreeSiteMenuRes();
		treeSiteMenuRes.setRootMenuList(rootMenuList);
		treeSiteMenuRes.setRootMenuListSize(rootMenuList.size());

		return treeSiteMenuRes;
	}
}
