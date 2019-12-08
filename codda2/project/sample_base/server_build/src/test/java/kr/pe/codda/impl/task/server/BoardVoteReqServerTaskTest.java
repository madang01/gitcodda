package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.impl.message.BoardVoteReq.BoardVoteReq;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardVoteReqServerTaskTest extends AbstractBoardTest {	
	@Test
	public void testDoTask() {
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = Mockito.mock(ToLetterCarrier.class);
		
		final short boardID = 3;
		
		BoardVoteReq inObj = new BoardVoteReq();
		inObj.setBoardID(boardID);
		inObj.setBoardNo(7);
		inObj.setRequestedUserID("test02");
		inObj.setIp("127.0.0.1");		
		
		BoardVoteReqServerTask boardVoteReqServerTask = null;
		try {
			boardVoteReqServerTask = new BoardVoteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			boardVoteReqServerTask.doTask(mainProjectName, 
					personalLoginManagerMock, toLetterCarrierMock, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
