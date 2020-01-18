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

package kr.pe.codda.impl.message.DocumentViewRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * DocumentViewRes message
 * @author Won Jonghoon
 *
 */
public class DocumentViewRes extends AbstractMessage {
	private long documentNo;
	private byte documentSate;
	private String fileName;
	private String subject;
	private String contents;
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
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
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
		builder.append("documentViewRes[");
		builder.append("documentNo=");
		builder.append(documentNo);
		builder.append(", documentSate=");
		builder.append(documentSate);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", contents=");
		builder.append(contents);
		builder.append(", lastModifiedDate=");
		builder.append(lastModifiedDate);
		builder.append("]");
		return builder.toString();
	}
}