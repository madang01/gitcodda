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

package kr.pe.codda.impl.message.DownloadImageRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * DownloadImageRes message
 * @author Won Jonghoon
 *
 */
public class DownloadImageRes extends AbstractMessage {
	private String yyyyMMdd;
	private long daySequence;
	private String imageFileName;
	private long fileSize;

	public String getYyyyMMdd() {
		return yyyyMMdd;
	}

	public void setYyyyMMdd(String yyyyMMdd) {
		this.yyyyMMdd = yyyyMMdd;
	}
	public long getDaySequence() {
		return daySequence;
	}

	public void setDaySequence(long daySequence) {
		this.daySequence = daySequence;
	}
	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("downloadImageRes[");
		builder.append("yyyyMMdd=");
		builder.append(yyyyMMdd);
		builder.append(", daySequence=");
		builder.append(daySequence);
		builder.append(", imageFileName=");
		builder.append(imageFileName);
		builder.append(", fileSize=");
		builder.append(fileSize);
		builder.append("]");
		return builder.toString();
	}
}