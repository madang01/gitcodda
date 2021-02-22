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
package kr.codda.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import kr.codda.util.CommonStaticUtil;

/**
 * @author Won Jonghoon
 *
 */
public class CoddaHelperSite {
	private String installedPathString;
	private String currentWorkingPathString;
	private String selectedMainProjectName;
	private ArrayList<String> mainProjectNameList;
	
	private String errorMessage;

	private String selectedDriveLetter;
	private HashMap<String, String> driveLetterToCurrentWorkingDirectoryHash = new HashMap<String, String>();
	private ArrayList<String> driveLetterList;
	
	public CoddaHelperSite() {
		try {
			currentWorkingPathString = new File(".").getCanonicalPath();
		} catch(Exception e) {
			String errorMessage = "failed to get a current working directory's canonical Path";
			
			Logger.getGlobal().severe(errorMessage);
			System.exit(1);			
		}
		
		if ("\\".equals(File.separator)) {
			selectedDriveLetter = currentWorkingPathString.substring(0, 2);
			
			driveLetterList = CommonStaticUtil.buildDriveLetterList();
			
			if (! driveLetterList.contains(selectedDriveLetter)) {
				String errorMessage = new StringBuilder()
						.append("the real current working path[")
						.append(currentWorkingPathString)
						.append("]'s driver letter is not a element of the drive letter list")
						.append(driveLetterList.toString()).toString();
				
				Logger.getGlobal().severe(errorMessage);
				
				System.exit(1);
			}
			
			driveLetterToCurrentWorkingDirectoryHash.clear(); 		
			for (String driveLetter  : driveLetterList) {
				driveLetterToCurrentWorkingDirectoryHash.put(driveLetter, driveLetter + File.separator);
			}
			
			driveLetterToCurrentWorkingDirectoryHash.put(selectedDriveLetter, File.separator);
		}
	}

	public String getInstalledPathString() {
		return installedPathString;
	}

	public void setInstalledPathString(String installedPathString) {
		this.installedPathString = installedPathString;
	}

	public String getCurrentWorkingPathString() {
		return currentWorkingPathString;
	}

	public void setCurrentWorkingPathString(String currentWorkingPathString) {
		this.currentWorkingPathString = currentWorkingPathString;
		// driveLetterToCurrentWorkingDirectoryHash.put(selectedDriveLetter, currentWorkingPathString);
	}

	public String getSelectedMainProjectName() {
		return selectedMainProjectName;
	}

	public void setSelectedMainProjectName(String selectedMainProjectName) {
		this.selectedMainProjectName = selectedMainProjectName;
	}

	public ArrayList<String> getMainProjectNameList() {
		return mainProjectNameList;
	}

	public void setMainProjectNameList(ArrayList<String> mainProjectNameList) {
		this.mainProjectNameList = mainProjectNameList;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSelectedDriveLetter() {
		if ("/".equals(File.separator)) {
			throw new IllegalStateException("thhis service is for Windows OS only");
		}
		
		return selectedDriveLetter;
	}

	public void setSelectedDriveLetter(String selectedDriveLetter) {
		if ("/".equals(File.separator)) {
			throw new IllegalStateException("thhis service is for Windows OS only");
		}
		
		this.selectedDriveLetter = selectedDriveLetter;
		// this.currentWorkingPathString = driveLetterToCurrentWorkingDirectoryHash.get(selectedDriveLetter);
	}	

	public ArrayList<String> getDriveLetterList() {
		if ("/".equals(File.separator)) {
			throw new IllegalStateException("thhis service is for Windows OS only");
		}
		
		return driveLetterList;
	}

	public void setDriveLetterList(ArrayList<String> driveLetterList) {
		if ("/".equals(File.separator)) {
			throw new IllegalStateException("thhis service is for Windows OS only");
		}
		
		this.driveLetterList = driveLetterList;
		
		/*
		driveLetterToCurrentWorkingDirectoryHash.clear();
		for (String driveLetter : driveLetterList) {
			if (driveLetter.equals(selectedDriveLetter)) {
				driveLetterToCurrentWorkingDirectoryHash.put(driveLetter, currentWorkingPathString);
			} else {
				driveLetterToCurrentWorkingDirectoryHash.put(driveLetter, File.separator);
			}
		}
		*/
		
	}

	public HashMap<String, String> getDriveLetterToCurrentWorkingDirectoryHash() {
		return driveLetterToCurrentWorkingDirectoryHash;
	}

	@Override
	public String toString() {
		final int maxLen = 30;
		StringBuilder builder = new StringBuilder();
		builder.append("CoddaHelperSite [installedPathString=");
		builder.append(installedPathString);
		builder.append(", currentWorkingPathString=");
		builder.append(currentWorkingPathString);
		builder.append(", selectedMainProjectName=");
		builder.append(selectedMainProjectName);
		builder.append(", mainProjectNameList=");
		builder.append(mainProjectNameList != null ? toString(mainProjectNameList, maxLen) : null);
		builder.append(", errorMessage=");
		builder.append(errorMessage);
		builder.append(", selectedDriveLetter=");
		builder.append(selectedDriveLetter);
		builder.append(", driveLetterToCurrentWorkingDirectoryHash=");
		builder.append(driveLetterToCurrentWorkingDirectoryHash != null
				? toString(driveLetterToCurrentWorkingDirectoryHash.entrySet(), maxLen)
				: null);
		builder.append(", driveLetterList=");
		builder.append(driveLetterList != null ? toString(driveLetterList, maxLen) : null);
		builder.append("]");
		return builder.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
}
