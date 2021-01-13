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

import java.util.ArrayList;

/**
 * @author Won Jonghoon
 *
 */
public class AllMessageInformation {
	private String installedPathString;
	private ArrayList<String> mainProjectNameList;
	private String selectedMainProjectName;
	private String selectedMainProjectMessageInfoPathString;

	private ArrayList<MessageInformation> messageInformationList = new ArrayList<MessageInformation>();

	public String getInstalledPathString() {
		return installedPathString;
	}

	public void setInstalledPathString(String installedPathString) {
		this.installedPathString = installedPathString;
	}

	public ArrayList<String> getMainProjectNameList() {
		return mainProjectNameList;
	}

	public void setMainProjectNameList(ArrayList<String> mainProjectNameList) {
		this.mainProjectNameList = mainProjectNameList;
	}

	public String getSelectedMainProjectName() {
		return selectedMainProjectName;
	}

	public void setSelectedMainProjectName(String selectedMainProjectName) {
		this.selectedMainProjectName = selectedMainProjectName;
	}

	public ArrayList<MessageInformation> getMessageInformationList() {
		return messageInformationList;
	}
	
	
	public void addMessageInformation(MessageInformation newMessageInformation) {
		messageInformationList.add(newMessageInformation);
	}

	public String getSelectedMainProjectMessageInfoPathString() {
		return selectedMainProjectMessageInfoPathString;
	}

	public void setSelectedMainProjectMessageInfoPathString(String selectedMainProjectMessageInfoPathString) {
		this.selectedMainProjectMessageInfoPathString = selectedMainProjectMessageInfoPathString;
	}
	
	
	
}
