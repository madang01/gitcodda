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
package kr.codda.servlet.ajax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import kr.codda.common.type.FileChooserType;
import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.model.CurrentWokingPathInformation;
import kr.codda.util.CommonStaticUtil;

/**
 * @author Won Jonghoon
 *
 */
public class CurrentWokingPathInformationGetterSvl extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -887335147201037496L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String parmFileChooserType = req.getParameter("fileChooserType");
		if (null == parmFileChooserType) {
			String errorMessage = "the parameter fileChooserType is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		final FileChooserType fileChooserType;
		if (parmFileChooserType.equals(FileChooserType.FILE.toString())) {
			fileChooserType = FileChooserType.FILE;
		} else if (parmFileChooserType.equals(FileChooserType.PATH.toString())) {
			fileChooserType = FileChooserType.PATH;
		} else {
			String errorMessage = new StringBuilder()
					.append("the parameter fileChooserType[")
					.append(parmFileChooserType)
					.append("] is bad becase it is not FileChooserType[")
					.append(FileChooserType.PATH.toString())
					.append(", ")
					.append(FileChooserType.FILE.toString())
					.append("]").toString();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}		
		
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
		
		final CurrentWokingPathInformation currentWokingPathInformation;
		
		try {
			currentWokingPathInformation = doWork(fileChooserType, coddaHelperSite);
		} catch(IllegalStateException e) {
			String errorMessage = e.getMessage();
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("unknonw error, errmsg=").append(e.getMessage()).toString();
			
			Logger.getGlobal().log(Level.WARNING, errorMessage, e);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		String currentWokingPathInformationJsonString = new Gson().toJson(currentWokingPathInformation);
		

		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		// res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfCurrentWorkingPathInformationGetterPage(currentWokingPathInformationJsonString));
		res.getWriter().print(currentWokingPathInformationJsonString);
	}
	
	public CurrentWokingPathInformation doWork(FileChooserType fileChooserType, CoddaHelperSite coddaHelperSite) throws Exception {
		
		String currentWorkingPathString = coddaHelperSite.getCurrentWorkingPathString();
		if (null == currentWorkingPathString) {
			Logger.getGlobal().severe("the var currentWorkingPathString is null");
			System.exit(1);
		}
		
		File currentWorkingPath = new File(currentWorkingPathString);
		
		if (! currentWorkingPath.exists()) {
			String newCurrentWorkingPathString = null;
			
			if (File.separator.equals("\\")) {
				newCurrentWorkingPathString = new StringBuilder().append(coddaHelperSite.getSelectedDriveLetter()).append("\\").toString();
			} else {
				newCurrentWorkingPathString = "/";
			}
			
			currentWorkingPath = new File(newCurrentWorkingPathString);
			
			Logger.getGlobal().info(new StringBuilder().append("change the virtual current working path[")
					.append(currentWorkingPathString)
					.append("] to root path because it does not exist").toString());
			
			
			
			if (! currentWorkingPath.exists()) {

				try {
					currentWorkingPath = new File(".").getCanonicalFile();
					
					newCurrentWorkingPathString = currentWorkingPath.getAbsolutePath();
				} catch(Exception e) {
					String errorMessage = "failed to get a real current working directory's canonical Path";
					
					Logger.getGlobal().severe(errorMessage);
					System.exit(1);
				}
				
				String newSelectedDriverLetter = newCurrentWorkingPathString.substring(0, 2);  
				
				ArrayList<String> driveLetterList = coddaHelperSite.getDriveLetterList();
				
				if (! driveLetterList.contains(newSelectedDriverLetter)) {
					String errorMessage = new StringBuilder()
							.append("the real current working path[")
							.append(newCurrentWorkingPathString)
							.append("]'s driver letter is not a element of the drive letter list")
							.append(driveLetterList.toString()).toString();
					
					Logger.getGlobal().warning(errorMessage);
					
					throw new IllegalStateException(errorMessage);
				}
				
				
				coddaHelperSite.setSelectedDriveLetter(newSelectedDriverLetter);
				Logger.getGlobal().info(new StringBuilder().append("change the virtual current working path[")
						.append(currentWorkingPathString)
						.append("] to the real current working path[")
						.append(currentWorkingPath.getAbsolutePath())
						.append("] because its root file system does not exist").toString());
			}
			
			
			coddaHelperSite.setCurrentWorkingPathString(newCurrentWorkingPathString);
			coddaHelperSite.getDriveLetterToCurrentWorkingDirectoryHash().put(coddaHelperSite.getSelectedDriveLetter(), newCurrentWorkingPathString);
			
		} else if (! currentWorkingPath.isDirectory()) {
			String newCurrentWorkingPathString = null;
			if (File.separator.equals("\\")) {
				newCurrentWorkingPathString = new StringBuilder().append(coddaHelperSite.getSelectedDriveLetter()).append("\\").toString();
			} else {
				newCurrentWorkingPathString = "/";
			}

			currentWorkingPath = new File(newCurrentWorkingPathString);
			
			Logger.getGlobal().info(new StringBuilder().append("change the virtual current working path[")
					.append(currentWorkingPathString)
					.append("] to root path because it is not a direcotry").toString());
			
			
			
			if (! currentWorkingPath.exists()) {
				
				
				try {
					currentWorkingPath = new File(".").getCanonicalFile();
					
					newCurrentWorkingPathString = currentWorkingPath.getAbsolutePath();
				} catch(Exception e) {
					String errorMessage = "failed to get a real current working directory's canonical Path";
					
					Logger.getGlobal().severe(errorMessage);
					System.exit(1);
				}
			
				String newSelectedDriverLetter = newCurrentWorkingPathString.substring(0, 2);
				
				ArrayList<String> driveLetterList = coddaHelperSite.getDriveLetterList();
				
				if (! driveLetterList.contains(newSelectedDriverLetter)) {
					String errorMessage = new StringBuilder()
							.append("the real current working path[")
							.append(newCurrentWorkingPathString)
							.append("]'s driver letter is not a element of the drive letter list")
							.append(driveLetterList.toString()).toString();
					
					Logger.getGlobal().warning(errorMessage);
					
					throw new IllegalStateException(errorMessage);
				}
				
				
				coddaHelperSite.setSelectedDriveLetter(newSelectedDriverLetter);
				
				Logger.getGlobal().info(new StringBuilder().append("change the virtual current working path[")
						.append(currentWorkingPathString)
						.append("] to the real current working path[")
						.append(currentWorkingPath.getAbsolutePath())
						.append("] because its root file system does not exist").toString());
			} 
			
			coddaHelperSite.setCurrentWorkingPathString(newCurrentWorkingPathString);
			coddaHelperSite.getDriveLetterToCurrentWorkingDirectoryHash().put(coddaHelperSite.getSelectedDriveLetter(), newCurrentWorkingPathString);
		}
		
		
		CurrentWokingPathInformation currentWokingPathInformation = CommonStaticUtil.buildCurrentWokingPathInformation(fileChooserType, currentWorkingPath);
		return currentWokingPathInformation;
	}

}
