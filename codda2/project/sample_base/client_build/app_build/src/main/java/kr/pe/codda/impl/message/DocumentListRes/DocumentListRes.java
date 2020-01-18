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

package kr.pe.codda.impl.message.DocumentListRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * DocumentListRes message
 * @author Won Jonghoon
 *
 */
public class DocumentListRes extends AbstractMessage {
	private int pageNo;
	private int pageSize;
	private long total;
	private int cnt;

	public static class Document {
		private long documentNo;
		private byte documentSate;
		private String fileName;
		private String subject;
		private java.sql.Timestamp lastModifiedDate;

		public long getDocumentNo() {
			return documentNo;
		}

		public void setDocumentNo(long documentNo) {
			this.documentNo = documentNo;
		}
		public byte getDocumentSate() {
			return documentSate;
		}

		public void setDocumentSate(byte documentSate) {
			this.documentSate = documentSate;
		}
		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
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
			builder.append("Document[");
			builder.append("documentNo=");
			builder.append(documentNo);
			builder.append(", documentSate=");
			builder.append(documentSate);
			builder.append(", fileName=");
			builder.append(fileName);
			builder.append(", subject=");
			builder.append(subject);
			builder.append(", lastModifiedDate=");
			builder.append(lastModifiedDate);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<Document> documentList;

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
	public java.util.List<Document> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(java.util.List<Document> documentList) {
		this.documentList = documentList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("documentListRes[");
		builder.append("pageNo=");
		builder.append(pageNo);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append(", total=");
		builder.append(total);
		builder.append(", cnt=");
		builder.append(cnt);

		builder.append(", documentList=");
		if (null == documentList) {
			builder.append("null");
		} else {
			int documentListSize = documentList.size();
			if (0 == documentListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < documentListSize; i++) {
					Document document = documentList.get(i);
					if (0 == i) {
						builder.append("document[");
					} else {
						builder.append(", document[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(document.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}