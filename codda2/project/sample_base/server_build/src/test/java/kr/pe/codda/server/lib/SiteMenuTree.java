package kr.pe.codda.server.lib;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.impl.task.server.ChildMenuAddReqServerTask;
import kr.pe.codda.impl.task.server.RootMenuAddReqServerTask;

public class SiteMenuTree {
	private Logger log = LoggerFactory.getLogger(SiteMenuTree.class);
	
	private final java.util.List<SiteMenuTreeNode> rootSiteMenuNodeList = new ArrayList<SiteMenuTreeNode>();
	private RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
	private ChildMenuAddReqServerTask childMenuAddReqServerTask = null;
	
	
	public SiteMenuTree() {
		
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
			childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		
	}
	
	private final HashMap<String, SiteMenuTreeNode> menuNameToTreeNodeHash =
			new HashMap<String, SiteMenuTreeNode>();
	
		
	public void addRootSiteMenuNode(SiteMenuTreeNode rootSiteMenuTreeNode) {
		rootSiteMenuNodeList.add(rootSiteMenuTreeNode);
	}
	
	
	private void makeChildSiteMenuRecordUsingChildSiteMenuTreeNode(
			String workingDBName,
			SiteMenuTreeNode parentSiteMenuTreeNode,
			SiteMenuTreeNode childSiteMenuTreeNode) {
		// log.info("parnetMenu={}", parnetMenu.toString());
		String requestedUserIDForAdmin = "admin";
		
		ChildMenuAddReq childMenuAddReq = new ChildMenuAddReq();
		childMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
		childMenuAddReq.setParentNo(parentSiteMenuTreeNode.getMenuNo());
		childMenuAddReq.setMenuName(childSiteMenuTreeNode.getMenuName());
		childMenuAddReq.setLinkURL(childSiteMenuTreeNode.getLinkURL());

		try {
			ChildMenuAddRes childMenuAddRes = childMenuAddReqServerTask.doWork(workingDBName, childMenuAddReq);
			childSiteMenuTreeNode.setMenuNo(childMenuAddRes.getMenuNo());
			childSiteMenuTreeNode.setParentNo(parentSiteMenuTreeNode.getMenuNo());
			childSiteMenuTreeNode.setDepth((short)(parentSiteMenuTreeNode.getDepth()+1));
			childSiteMenuTreeNode.setOrderSeq((short)menuNameToTreeNodeHash.size());
			
			if (null != menuNameToTreeNodeHash.get(childSiteMenuTreeNode.getMenuName())) {
				String errorMessage = new StringBuilder().append("부모 메뉴[").append(parentSiteMenuTreeNode.getMenuName())
						.append("]의 자식 메뉴[").append(childSiteMenuTreeNode.getMenuName()).append("] 이름 중복 발생").toString();
				log.warn(errorMessage);
				
				fail(errorMessage);
			}
			
			menuNameToTreeNodeHash.put(childSiteMenuTreeNode.getMenuName(), childSiteMenuTreeNode);
			
			for (SiteMenuTreeNode childchildSiteMenuTreeNode : childSiteMenuTreeNode.getChildSiteMenuNodeList()) {
				makeChildSiteMenuRecordUsingChildSiteMenuTreeNode(workingDBName, childSiteMenuTreeNode, childchildSiteMenuTreeNode);
			}


		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("부모 메뉴[").append(parentSiteMenuTreeNode.getMenuName())
					.append("]의 자식 메뉴[").append(childSiteMenuTreeNode.getMenuName()).append("] 추가 실패").toString();

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
	}
	
	public void toDBRecord(String workingDBName) {
		String requestedUserIDForAdmin = "admin";
		
		for (SiteMenuTreeNode  rootSiteMenuTreeNode : rootSiteMenuNodeList) {
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
			rootMenuAddReq.setMenuName(rootSiteMenuTreeNode.getMenuName());
			rootMenuAddReq.setLinkURL(rootSiteMenuTreeNode.getLinkURL());

			try {
				RootMenuAddRes rootMenuAddRes = rootMenuAddReqServerTask.doWork(workingDBName, rootMenuAddReq);

				rootSiteMenuTreeNode.setMenuNo(rootMenuAddRes.getMenuNo());
				rootSiteMenuTreeNode.setDepth((short)0);
				rootSiteMenuTreeNode.setOrderSeq((short)menuNameToTreeNodeHash.size());
				
				if (null != menuNameToTreeNodeHash.get(rootSiteMenuTreeNode.getMenuName())) {
					String errorMessage = new StringBuilder().append("루트 메뉴[").append(rootSiteMenuTreeNode.getMenuName())
							.append("] 이름 중복 발생").toString();
					log.warn(errorMessage);
					
					fail(errorMessage);
				}
				
				menuNameToTreeNodeHash.put(rootSiteMenuTreeNode.getMenuName(), rootSiteMenuTreeNode);
				
				
				for (SiteMenuTreeNode childSiteMenuTreeNode : rootSiteMenuTreeNode.getChildSiteMenuNodeList()) {
					makeChildSiteMenuRecordUsingChildSiteMenuTreeNode(workingDBName, rootSiteMenuTreeNode, childSiteMenuTreeNode);
				}
				
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("루트 메뉴[").append(rootSiteMenuTreeNode.getMenuName())
						.append("] 추가 실패").toString();

				log.warn(errorMessage, e);

				fail(errorMessage);
			}
		}
	}
	
	public SiteMenuTreeNode find(String menuName) {
		return menuNameToTreeNodeHash.get(menuName);
	}
	
}
