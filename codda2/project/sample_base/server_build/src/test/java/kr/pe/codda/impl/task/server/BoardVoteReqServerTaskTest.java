package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junitlib.AbstractBoardTest;
import kr.pe.codda.impl.message.BoardVoteReq.BoardVoteReq;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;

public class BoardVoteReqServerTaskTest extends AbstractBoardTest {
	
	
	@Test
	public void testDoTask_OK() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";
		BoardWriteReqServerTask boardWriteReqServerTask = new BoardWriteReqServerTask();
		

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("투표::제목");
		boardWriteReq.setContents("투표::내용");
		boardWriteReq.setIp("172.16.0.1");
		

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME, boardWriteReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardVoteReq inObj = new BoardVoteReq();
		inObj.setBoardID(boardID);
		inObj.setBoardNo(boardWriteRes.getBoardNo());
		inObj.setRequestedUserID("test02");
		inObj.setIp("127.0.0.1");		
		
		BoardVoteReqServerTask boardVoteReqServerTask = new BoardVoteReqServerTask();
		
		try {
			boardVoteReqServerTask.doWork(TEST_DBCP_NAME, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
