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
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.model.DriveLetterInformation;
import kr.codda.util.CommonStaticUtil;

/**
 * @author Won Jonghoon
 *
 */
public class DriveLetterListRebulderSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5005224851257160693L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
		String selectedDriveLetter = coddaHelperSite.getSelectedDriveLetter();
		String currentWorkingPathString = coddaHelperSite.getCurrentWorkingPathString();
		HashMap<String, String> driveLetterToCurrentWorkingDirectoryHash = coddaHelperSite
				.getDriveLetterToCurrentWorkingDirectoryHash();

		ArrayList<String> newDriveLetterList = CommonStaticUtil.buildDriveLetterList();
		
		if (! newDriveLetterList.contains(selectedDriveLetter)) {
			String newCurrentWorkingPathString = File.separator; 
			File currentWorkingPath = new File(newCurrentWorkingPathString);
			
			Logger.getGlobal().info(new StringBuilder().append("change the virtual current working path[")
					.append(currentWorkingPathString)
					.append("] to root path because it is not a direcotry").toString());
			
			if (! currentWorkingPath.exists()) {
				currentWorkingPath = new File(".");
				
				try {
					newCurrentWorkingPathString = currentWorkingPath.getCanonicalPath();
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
					
					Logger.getGlobal().severe(errorMessage);
					
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					res.setContentType("text/html");
					res.setCharacterEncoding("utf-8");
					res.getWriter().print(errorMessage);
					return;
				}
				
				
				coddaHelperSite.setSelectedDriveLetter(newSelectedDriverLetter);
				selectedDriveLetter = newSelectedDriverLetter;
				
				Logger.getGlobal().info(new StringBuilder().append("change the virtual current working path[")
						.append(currentWorkingPathString)
						.append("] to the real current working path[")
						.append(currentWorkingPath.getAbsolutePath())
						.append("] because its root file system does not exist").toString());
			} 
			
			coddaHelperSite.setCurrentWorkingPathString(newCurrentWorkingPathString);
			coddaHelperSite.getDriveLetterToCurrentWorkingDirectoryHash().put(coddaHelperSite.getSelectedDriveLetter(), newCurrentWorkingPathString);
			
			currentWorkingPathString = newCurrentWorkingPathString;
		}		

		driveLetterToCurrentWorkingDirectoryHash.clear();
		for (String newDriveLetter : newDriveLetterList) {
			if (newDriveLetter.equals(selectedDriveLetter)) {
				driveLetterToCurrentWorkingDirectoryHash.put(newDriveLetter, currentWorkingPathString);
			} else {
				driveLetterToCurrentWorkingDirectoryHash.put(newDriveLetter, File.separator);
			}
		}

		coddaHelperSite.setDriveLetterList(newDriveLetterList);

		DriveLetterInformation driveLetterInformation = new DriveLetterInformation();
		driveLetterInformation.setSelectedDriveLetter(selectedDriveLetter);
		driveLetterInformation.setDriveLetterList(newDriveLetterList);

		String driveLetterInformationJsonString = new Gson().toJson(driveLetterInformation);

		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(driveLetterInformationJsonString);

	}
}
