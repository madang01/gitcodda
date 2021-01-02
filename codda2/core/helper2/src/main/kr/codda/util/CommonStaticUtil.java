/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.codda.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import kr.codda.main.HelperServer;
import kr.codda.model.CurrentWokingPathInformation;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.exception.BuildSystemException;

/**
 * @author Won Jonghoon
 *
 */
public abstract class CommonStaticUtil {
	public static final Charset PAGE_CHARSET = Charset.forName("UTF-8"); 
	
	
	public static String readErrorPageContents() throws IOException {
		
		return readPageContents("/webapp/error.html");
	}
	
	
	public static String readPageContents(String fileURL) throws IOException {
		
		String content = null;
		InputStream is = HelperServer.class.getResourceAsStream(fileURL);
		try {
			
			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);
			
			content = new String(buffer, PAGE_CHARSET);
		} finally {
			if (null != is) {
				is.close();
			}
		}
		return content;
	}
	
	public static CurrentWokingPathInformation buildCurrentWokingPathInformation(String currentWorkingPathString) {
		CurrentWokingPathInformation currentWokingPathInformation = new CurrentWokingPathInformation();
		
		currentWokingPathInformation.setCurrentWorkingPathString(currentWorkingPathString);
		
		File newCurrentWorkingPath = new File(currentWorkingPathString);
		
		for (File childFile : newCurrentWorkingPath.listFiles()) {
			if (childFile.isDirectory()) {
				currentWokingPathInformation.addChildPathString(childFile.getName());
			}
		}
		
		return currentWokingPathInformation;
		
	}
	
	public static ArrayList<String> getMainProjectNameList(String installedPathString) throws BuildSystemException {
		String projectBasePathString = ProjectBuildSytemPathSupporter.getProjectBasePathString(installedPathString);

		File projectBasePath = new File(projectBasePathString);
		if (! projectBasePath.exists()) {
			String errorMessage = String.format(
					"the codda installed path(=parameter installedPathString[%s])'s the project base path[%s] doesn't exist",
					installedPathString, projectBasePathString);

			// log.warn(errorMessage);

			throw new BuildSystemException(errorMessage); 
		}

		if (!projectBasePath.isDirectory()) {
			String errorMessage = String.format(
					"the codda installed path(=parameter installedPathString[%s])'s the project base path[%s] is not a direcotry",
					installedPathString, projectBasePathString);
			// log.warn(errorMessage);

			throw new BuildSystemException(errorMessage);
		}

		if (!projectBasePath.canRead()) {
			String errorMessage = String.format(
					"the codda installed path(=parameter installedPathString[%s])'s the project base path[%s] doesn't hava permission to read",
					installedPathString, projectBasePathString);
			// log.warn(errorMessage);

			throw new BuildSystemException(errorMessage);
		}

		ArrayList<String> mainProjectNameList = new ArrayList<String>();
		
		File[] projectBasePathList = projectBasePath.listFiles();
		
		if (null == projectBasePathList) {
			String errorMessage = "the var projectBasePathList is null";
			
			throw new BuildSystemException(errorMessage);
		}

		for (File fileOfList : projectBasePathList) {
			if (fileOfList.isDirectory()) {
				if (!fileOfList.canRead()) {
					String errorMessage = String.format(
							"the project base path[%s] doesn't hava permission to read",
							fileOfList.getAbsolutePath());
					// log.warn(errorMessage);

					throw new BuildSystemException(errorMessage);
				}

				if (!fileOfList.canWrite()) {
					String errorMessage = String.format(
							"the project base path[%s] doesn't hava permission to write",
							fileOfList.getAbsolutePath());
					// log.warn(errorMessage);

					throw new BuildSystemException(errorMessage);
				}

				mainProjectNameList.add(fileOfList.getName());
			}
		}
		
		return mainProjectNameList;
	}
}
