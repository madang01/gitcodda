package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.impl.message.TreeSiteMenuReq.TreeSiteMenuReq;
import kr.pe.codda.impl.message.TreeSiteMenuRes.TreeSiteMenuRes;

public class TreeSiteMenuReqServerTaskTest extends AbstractBoardTest {
	// final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	
	
	@Test
	public void testDoService_ok() {
		String requestedUserID = "admin";
		
		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		treeSiteMenuReq.setRequestedUserID(requestedUserID);
		
		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = null;
		try {
			treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			TreeSiteMenuRes treeSiteMenuRes = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
			
			log.info(treeSiteMenuRes.toString());
			
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error");
		}
	}

}
