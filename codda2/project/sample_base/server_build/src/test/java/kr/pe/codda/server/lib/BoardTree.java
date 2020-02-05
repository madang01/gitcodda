package kr.pe.codda.server.lib;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.task.server.BoardReplyReqServerTask;
import kr.pe.codda.impl.task.server.BoardWriteReqServerTask;

public class BoardTree {
	private Logger log = LoggerFactory.getLogger(BoardTree.class);
	
	private final Stack<BoardTreeNode> boardTreeNodeStack =
			new Stack<BoardTreeNode>();
	
	private  final BoardWriteReqServerTask boardWriteReqServerTask;
	private  final BoardReplyReqServerTask boardReplyReqServerTask;	
	
	private final List<BoardTreeNode> rootBoardTreeNodeList = new ArrayList<BoardTreeNode>();
	
	private final HashMap<String, BoardTreeNode> subjectToBoardTreeNodeHash =
			new HashMap<String, BoardTreeNode>();
	
	public BoardTree() {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
			boardReplyReqServerTask = new BoardReplyReqServerTask();
	}
	
	
	private void makeRootBoardTreeRecordUsingRootBoardTreeNode(String workingDBName, BoardTreeNode rootBoardTreeNode) {		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(rootBoardTreeNode.getWriterID());
		boardWriteReq.setBoardID(rootBoardTreeNode.getBoardID());
		boardWriteReq.setSubject(rootBoardTreeNode.getSubject());
		boardWriteReq.setContents(rootBoardTreeNode.getContents());
		boardWriteReq.setIp("172.16.0.1");
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short)attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(workingDBName, boardWriteReq);
		} catch(ServerTaskException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		if (null != subjectToBoardTreeNodeHash.get(rootBoardTreeNode.getSubject())) {
			log.info("루트 게시판[{}] 이름 중복", rootBoardTreeNode.getSubject());
			fail("게시판 이름 중복");
		}
		
		boardTreeNodeStack.push(rootBoardTreeNode);
		subjectToBoardTreeNodeHash.put(rootBoardTreeNode.getSubject(), rootBoardTreeNode);
		
		rootBoardTreeNode.setBoardNo(boardWriteRes.getBoardNo());
		rootBoardTreeNode.setGroupNo(boardWriteRes.getBoardNo());
		rootBoardTreeNode.setDepth((short)0);

		List<BoardTreeNode> childBoardTreeNodeList = rootBoardTreeNode.getChildBoardTreeNodeList();
		
		
		for (BoardTreeNode childBoardTreeNode : childBoardTreeNodeList) {
			makeChildBoardRecordUsingChildBoardTreeNode(
					workingDBName, rootBoardTreeNode.getGroupNo(), 
					rootBoardTreeNode.getBoardNo(), 
					(short)1,
					childBoardTreeNode);
		}
	}
	
	private void makeChildBoardRecordUsingChildBoardTreeNode(String workingDBName,
			long groupNo,
			long parentNo,
			short depth,
			BoardTreeNode childBoardTreeNode) {		
		// UShort parentOrderSeq = UShort.valueOf(0);
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setBoardID(childBoardTreeNode.getBoardID());
		boardReplyReq.setParentBoardNo(parentNo);
		boardReplyReq.setSubject(childBoardTreeNode.getSubject());
		boardReplyReq.setContents(childBoardTreeNode.getContents());		
		boardReplyReq.setRequestedUserID(childBoardTreeNode.getWriterID());
		boardReplyReq.setIp("127.0.0.1");		
		
		List<BoardReplyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardReplyReq.NewAttachedFile>();
		boardReplyReq.setNewAttachedFileCnt((short)newAttachedFileList.size());
		boardReplyReq.setNewAttachedFileList(newAttachedFileList);
		
		BoardReplyRes boardReplyRes = null;
		try {
			boardReplyRes = boardReplyReqServerTask.doWork(workingDBName, boardReplyReq);
			// log.info(boardReplyRes.toString());
		} catch(ServerTaskException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}		
		
		if (null != subjectToBoardTreeNodeHash.get(childBoardTreeNode.getSubject())) {
			log.info("자식 게시판[{}] 이름 중복", childBoardTreeNode.getSubject());
			fail("게시판 이름 중복");
		}
		
		boardTreeNodeStack.push(childBoardTreeNode);
		subjectToBoardTreeNodeHash.put(childBoardTreeNode.getSubject(), childBoardTreeNode);
		
		childBoardTreeNode.setGroupNo(groupNo);
		childBoardTreeNode.setParentNo(parentNo);
		childBoardTreeNode.setBoardNo(boardReplyRes.getBoardNo());
		childBoardTreeNode.setDepth(depth);
		
		List<BoardTreeNode> childChildBoardTreeNodeList = childBoardTreeNode.getChildBoardTreeNodeList();
		
		
		for (BoardTreeNode childChildBoardTreeNode : childChildBoardTreeNodeList) {
			makeChildBoardRecordUsingChildBoardTreeNode(
					workingDBName,groupNo, childBoardTreeNode.getBoardNo(), 
					(short)(depth + 1),
					childChildBoardTreeNode);
		}
	}
	
	
	public void makeDBRecord(String workingDBName) {
		
		for (BoardTreeNode rootBoardTreeNode : rootBoardTreeNodeList) {
			boardTreeNodeStack.clear();
			
			makeRootBoardTreeRecordUsingRootBoardTreeNode(workingDBName, rootBoardTreeNode);
			
			int groupSeq = 0;
			while (! boardTreeNodeStack.isEmpty()) {
				BoardTreeNode boardTreeNode = boardTreeNodeStack.pop();
				boardTreeNode.setGroupSeq(groupSeq);
				groupSeq++;
			}
		}
	}
	
	public void addRootBoardTreeNode(BoardTreeNode rootBoardTreeNode) {
		rootBoardTreeNodeList.add(rootBoardTreeNode);
	}
	
	public static BoardTreeNode makeBoardTreeNodeWithoutTreeInfomation(short boardID,  
			String writerID, String subject, String content) {
		BoardTreeNode boardTreeNode = new BoardTreeNode();
		boardTreeNode.setBoardID(boardID);
		
		boardTreeNode.setWriterID(writerID);
		boardTreeNode.setSubject(subject);
		boardTreeNode.setContents(content);
		
		return boardTreeNode;
	}
	
	public int getTotal() {
		int total = 0;
		
		if (null != rootBoardTreeNodeList) {
			for (BoardTreeNode rootBoardTreeNode : rootBoardTreeNodeList) {
				total += rootBoardTreeNode.getTotal();
			}
		}
		
		return total;
	}
	
	public int getHashSize() {
		return subjectToBoardTreeNodeHash.size();
	}
	
	public BoardTreeNode find(String subject) {
		if (null == subject) {
			throw new IllegalArgumentException("the parameter subject is null");
		}
		
		
		return subjectToBoardTreeNodeHash.get(subject);
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoardTree [rootBoardTreeNodeList=");
		builder.append(rootBoardTreeNodeList);
		builder.append("]");
		return builder.toString();
	}
}
