package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
import kr.pe.codda.impl.message.MenuDeleteReq.MenuDeleteReq;
import kr.pe.codda.impl.message.MenuMoveUpReq.MenuMoveUpReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;

public class MenuMoveUpReqServerTaskTest extends AbstractBoardTest {
	// final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	
	@Test
	public void testDoService_ok() {
		String requestedUserID = "admin";
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		RootMenuAddRes toRootMenuAddRes = null;
		RootMenuAddRes fromRootMenuAddRes = null;
		
		{
			RootMenuAddReq toRootMenuAddReq = new RootMenuAddReq();
			toRootMenuAddReq.setRequestedUserID(requestedUserID);
			toRootMenuAddReq.setMenuName("temp3");
			toRootMenuAddReq.setLinkURL("/temp03");
			
			try {
				toRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, toRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setRequestedUserID(requestedUserID);
			fromRootMenuAddReq.setMenuName("temp4");
			fromRootMenuAddReq.setLinkURL("/temp04");
			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		
		
		/*{
			RootMenuAddReq otherRootMenuAddReq = new RootMenuAddReq();
			otherRootMenuAddReq.setMenuName("temp3");
			otherRootMenuAddReq.setLinkURL("/temp03");
			
			try {
				rootMenuAddReqServerTask.doWork(otherRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}*/
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setRequestedUserID(requestedUserID);
		menuUpMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = null;
		try {
			menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		try {
			MessageResultRes messageResultRes = menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		log.info("상단 이동 성공, fromRootMenuAddRes.menuNo={}, toRootMenuAddRes.menuNo={}", 
				fromRootMenuAddRes.getMenuNo(), toRootMenuAddRes.getMenuNo());
	}
	
	@Test
	public void testDoService_menuNoDoesNotExist() {
		String requestedUserID = "admin";
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		RootMenuAddRes fromRootMenuAddRes = null;		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setRequestedUserID(requestedUserID);
			fromRootMenuAddReq.setMenuName("temp1");
			fromRootMenuAddReq.setLinkURL("/temp01");
			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = null;
		try {
			menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setRequestedUserID(requestedUserID);
		menuDeleteReq.setMenuNo(fromRootMenuAddRes.getMenuNo());		
		
		try {
			MessageResultRes messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);
			
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setRequestedUserID(requestedUserID);
		menuUpMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = null;
		try {
			menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		try {
			menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);			
			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String expectedErrorMessage  = new StringBuilder()
					.append("지정한 메뉴[")
					.append(menuUpMoveReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();
			
			assertEquals(expectedErrorMessage, e.getMessage());
			
			log.info("없는 메뉴 번호를 메뉴 상단 이동하라고 요청 테스트 성공");
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
	}

	
	@Test
	public void testDoService_상단이동할수없는메뉴() {
		String requestedUserID = "admin";
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		RootMenuAddRes rootMenuAddRes = null;		
		{
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setRequestedUserID(requestedUserID);
			rootMenuAddReq.setMenuName("temp1");
			rootMenuAddReq.setLinkURL("/temp01");
			
			
			try {
				rootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		ChildMenuAddReqServerTask childMenuAddReqServerTask = null;
		try {
			childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		ChildMenuAddReq firstChildMenuAddReq = new ChildMenuAddReq();
		firstChildMenuAddReq.setRequestedUserID(requestedUserID);
		firstChildMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		firstChildMenuAddReq.setMenuName("temp1_1");
		firstChildMenuAddReq.setLinkURL("/temp01_1");
		
		ChildMenuAddRes firstChildMenuAddRes = null;
		try {
			firstChildMenuAddRes  = childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, firstChildMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setRequestedUserID(requestedUserID);
		menuUpMoveReq.setMenuNo(firstChildMenuAddRes.getMenuNo());
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = null;
		try {
			menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		try {
			menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);			
			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String expectedErrorMessage = new StringBuilder()
					.append("지정한 메뉴[")
					.append(firstChildMenuAddRes.getMenuNo())
					.append("]보다 한칸 높은 메뉴가 존재하지 않습니다").toString();
			
			assertEquals(expectedErrorMessage, e.getMessage());
			
			log.info("메뉴 상단 이동이 불가능한 메뉴의 상단 이동 요청 테스트 성공");
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
	}
}
