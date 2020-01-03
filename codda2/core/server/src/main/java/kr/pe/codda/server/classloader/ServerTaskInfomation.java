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
package kr.pe.codda.server.classloader;

import java.io.File;

import kr.pe.codda.server.task.AbstractServerTask;

public class ServerTaskInfomation {
	private File serverTaskClassFile = null;
	private long loadedTime = 0;
	private AbstractServerTask serverTask = null;
	
	public ServerTaskInfomation(File serverTaskClassFile, AbstractServerTask serverTask) {
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
