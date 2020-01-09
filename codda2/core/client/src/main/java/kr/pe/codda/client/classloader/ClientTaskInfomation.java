/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package kr.pe.codda.client.classloader;

import java.io.File;

import kr.pe.codda.client.task.AbstractClientTask;

/**
 * 클라이언트 타스크와 클라이언트 타스크 클래스 파일을 속성으로 갖는 '클라이언트 타스크 정보'
 * 
 * @author Won Jonghoon
 *
 */
public class ClientTaskInfomation {
	private File clientTaskClassFile = null;
	private long loadedTime = 0;
	private AbstractClientTask clientTask = null;
	
	/**
	 * 생성자, WARNING! 파라미터 'clientTaskClassFile'(=클라이언트 타스크 클래스 파일) 로 부터 파라미터 'clientTask'(=클라이언트 타스크) 가 생성된것을 기본 전제로 한다. 
	 * 
	 * @param clientTaskClassFile 클라이언트 타스크 클래스 파일
	 * @param clientTask 클라이언트 타스크
	 */
	public ClientTaskInfomation(File clientTaskClassFile, AbstractClientTask clientTask) {
		if (null == clientTaskClassFile) {
			String errorMessage = "the parmater clientTaskClassFile is null";			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! clientTaskClassFile.exists()) {
			String errorMessage = new StringBuilder("the client task file[")
					.append(clientTaskClassFile.getAbsolutePath())
					.append("] was not found").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == clientTask) {
			String errorMessage = "the parmater clientTask is null";			
			throw new IllegalArgumentException(errorMessage);
		}	
		
		
		this.clientTaskClassFile = clientTaskClassFile;
		this.clientTask = clientTask;
		this.loadedTime = clientTaskClassFile.lastModified();
	}
	
	/**
	 * @return 클라이언트 타스크 클래스 파일 수정 여부
	 */
	public boolean isModifed() {
		long lastModifedTime = clientTaskClassFile.lastModified();
		return (loadedTime != lastModifedTime);
	}

	/**
	 * @return 클라이언트 타스크
	 */
	public AbstractClientTask getClientTask() {
		return clientTask;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientTaskInfomation [clientTaskClassFile=");
		builder.append(clientTaskClassFile.getAbsolutePath());
		builder.append(", loadedTime=");
		builder.append(loadedTime);
		builder.append(", clientTask hashCode=");
		builder.append(clientTask.hashCode());
		builder.append("]");
		return builder.toString();
	}
}

