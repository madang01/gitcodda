/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.impl.message.BoardListRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardListRes message
 * @author Won Jonghoon
 *
 */
public class BoardListRes extends AbstractMessage {
	private short boardID;
	private String boardName;
	private byte boardListType;
	private byte boardWritePermissionType;
	private int pageNo;
	private int pageSize;
	private long total;
	private int cnt;

	public static class Board {
		private long boardNo;
		private long groupNo;
		private int groupSeq;
		private long parentNo;
		private short depth;
		private String writerID;
		private String writerNickname;
		private int viewCount;
		private byte boardSate;
		private java.sql.Timestamp registeredDate;
		private int votes;
		private String subject;
		private java.sql.Timestamp lastModifiedDate;

		public long getBoardNo() {
			return boardNo;
		}

		public void setBoardNo(long boardNo) {
			this.boardNo = boardNo;
		}
		public long getGroupNo() {
			return groupNo;
		}

		public void setGroupNo(long groupNo) {
			this.groupNo = groupNo;
		}
		public int getGroupSeq() {
			return groupSeq;
		}

		public void setGroupSeq(int groupSeq) {
			this.groupSeq = groupSeq;
		}
		public long getParentNo() {
			return parentNo;
		}

		public void setParentNo(long parentNo) {
			this.parentNo = parentNo;
		}
		public short getDepth() {
			return depth;
		}

		public void setDepth(short depth) {
			this.depth = depth;
		}
		public String getWriterID() {
			return writerID;
		}

		public void setWriterID(String writerID) {
			this.writerID = writerID;
		}
		public String getWriterNickname() {
			return writerNickname;
		}

		public void setWriterNickname(String writerNickname) {
			this.writerNickname = writerNickname;
		}
		public int getViewCount() {
			return viewCount;
		}

		public void setViewCount(int viewCount) {
			this.viewCount = viewCount;
		}
		public byte getBoardSate() {
			return boardSate;
		}

		public void setBoardSate(byte boardSate) {
			this.boardSate = boardSate;
		}
		public java.sql.Timestamp getRegisteredDate() {
			return registeredDate;
		}

		public void setRegisteredDate(java.sql.Timestamp registeredDate) {
			this.registeredDate = registeredDate;
		}
		public int getVotes() {
			return votes;
		}

		public void setVotes(int votes) {
			this.votes = votes;
		}
		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}
		public java.sql.Timestamp getLastModifiedDate() {
			return lastModifiedDate;
		}

		public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Board[");
			builder.append("boardNo=");
			builder.append(boardNo);
			builder.append(", groupNo=");
			builder.append(groupNo);
			builder.append(", groupSeq=");
			builder.append(groupSeq);
			builder.append(", parentNo=");
			builder.append(parentNo);
			builder.append(", depth=");
			builder.append(depth);
			builder.append(", writerID=");
			builder.append(writerID);
			builder.append(", writerNickname=");
			builder.append(writerNickname);
			builder.append(", viewCount=");
			builder.append(viewCount);
			builder.append(", boardSate=");
			builder.append(boardSate);
			builder.append(", registeredDate=");
			builder.append(registeredDate);
			builder.append(", votes=");
			builder.append(votes);
			builder.append(", subject=");
			builder.append(subject);
			builder.append(", lastModifiedDate=");
			builder.append(lastModifiedDate);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<Board> boardList;

	public short getBoardID() {
		return boardID;
	}

	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	public byte getBoardListType() {
		return boardListType;
	}

	public void setBoardListType(byte boardListType) {
		this.boardListType = boardListType;
	}
	public byte getBoardWritePermissionType() {
		return boardWritePermissionType;
	}

	public void setBoardWritePermissionType(byte boardWritePermissionType) {
		this.boardWritePermissionType = boardWritePermissionType;
	}
	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public java.util.List<Board> getBoardList() {
		return boardList;
	}

	public void setBoardList(java.util.List<Board> boardList) {
		this.boardList = boardList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardListRes[");
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", boardName=");
		builder.append(boardName);
		builder.append(", boardListType=");
		builder.append(boardListType);
		builder.append(", boardWritePermissionType=");
		builder.append(boardWritePermissionType);
		builder.append(", pageNo=");
		builder.append(pageNo);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append(", total=");
		builder.append(total);
		builder.append(", cnt=");
		builder.append(cnt);

		builder.append(", boardList=");
		if (null == boardList) {
			builder.append("null");
		} else {
			int boardListSize = boardList.size();
			if (0 == boardListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < boardListSize; i++) {
					Board board = boardList.get(i);
					if (0 == i) {
						builder.append("board[");
					} else {
						builder.append(", board[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(board.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}