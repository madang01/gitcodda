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
package kr.pe.codda.server.task;

import java.io.File;

/**
 * 서버 타스크와 서버 타스크 수정 여부를 판단할 정보를 갖고 있는 클래스.
 * 수정 여부는 자신 혹은 의조성을 갖는 동적 클래스 파일이 수정 되었을 경우이다.
 * 
 *  
 * @author Won Jonghoon
 *
 */
public class LoadedServerTask {
	private File serverTaskClassFile = null;
	private long loadedTime = 0;
	private AbstractServerTask serverTask = null;
	
	/**
	 * 생성자
	 * @param serverTaskClassFile 서버 타스크 클래스 파일
	 * @param serverTask 서버 타스트
	 */
	public LoadedServerTask(File serverTaskClassFile, AbstractServerTask serverTask) {
		if (null == serverTaskClassFile) {
			String errorMessage = "the parmater serverTaskClassFile is null";			
			throw new IllegalArgumentException(errorMessage);
		}
		
		/*if (! serverTaskClassFile.exists()) {
			String errorMessage = new StringBuilder("the server task file[")
					.append(serverTaskClassFile.getAbsolutePath())
					.append("] was not found").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}*/
		
		if (null == serverTask) {
			String errorMessage = "the parmater serverTask is null";			
			throw new IllegalArgumentException(errorMessage);
		}		
		
		/*if (! (serverTask.getClass().getClassLoader() instanceof SimpleClassLoader)) {
			throw new IllegalArgumentException("the parameter serverTask is not a instance of SimpleClassLoader class");
		}		*/
		
		
		this.serverTaskClassFile = serverTaskClassFile;
		this.serverTask = serverTask;
		this.loadedTime = serverTaskClassFile.lastModified();
	}
	
	public boolean isModifed() {
		if (! serverTaskClassFile.exists()) {
			return false;
		}
		
		long lastModifedTime = serverTaskClassFile.lastModified();
		return (loadedTime != lastModifedTime);
	}

	public AbstractServerTask getServerTask() {
		return serverTask;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerTaskObjectInfo [serverTaskClassFile=");
		builder.append(serverTaskClassFile.getAbsolutePath());
		builder.append(", loadedTime=");
		builder.append(loadedTime);
		builder.append(", serverTask hashCode=");
		builder.append(serverTask.hashCode());
		builder.append("]");
		return builder.toString();
	}	
}
