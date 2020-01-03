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

import kr.pe.codda.common.classloader.SystemClassDeterminer;
import kr.pe.codda.common.classloader.SystemClassVerifierIF;
import kr.pe.codda.common.classloader.ServerClassLoader;
import kr.pe.codda.common.exception.CoddaConfigurationException;

public class ServerClassLoaderFactory {
	private String serverAPPINFClassPathString = null;
	private String projectResourcesPathString = null;
	private SystemClassVerifierIF excludedDynamicClassManager = new SystemClassDeterminer();
	
	public ServerClassLoaderFactory(String serverAPPINFClassPathString,
			String projectResourcesPathString) throws CoddaConfigurationException {
		if (null == serverAPPINFClassPathString) {
			throw new IllegalArgumentException("the parameter serverAPPINFClassPathString is null");
		}
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (! serverAPPINFClassPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter serverAPPINFClassPathString[")
					.append(serverAPPINFClassPathString)
					.append("] do not exist").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (! serverAPPINFClassPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter serverAPPINFClassPathString[")
					.append(serverAPPINFClassPathString)
					.append("] isn't a directory").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (null == projectResourcesPathString) {
			throw new IllegalArgumentException("the parameter projectResourcesPathString is null");
		}
		
		File projectResourcesPath = new File(projectResourcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter projectResourcesPathString[")
					.append(projectResourcesPathString)
					.append("] doesn't exist").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter projectResourcesPathString[")
					.append(projectResourcesPathString)
					.append("] isn't a directory").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		this.serverAPPINFClassPathString = serverAPPINFClassPathString;
		this.projectResourcesPathString = projectResourcesPathString;
	}
	
	public ServerClassLoader createServerClassLoader() {
		return new ServerClassLoader(serverAPPINFClassPathString, projectResourcesPathString, excludedDynamicClassManager);
	}
}
