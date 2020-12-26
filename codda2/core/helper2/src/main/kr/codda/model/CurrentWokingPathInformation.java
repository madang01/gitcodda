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

/**
 * @author Won Jonghoon
 *
 */
public class CurrentWokingPathInformation {
	private final String fileSeperator = File.separator;
	private String currentWorkingPathString;
	private ArrayList<String> chiledPathStringList = new ArrayList<String>();
	
	
	public String getFileSeperator() {
		return fileSeperator;
	}
	
	public String getCurrentWorkingPathString() {
		return currentWorkingPathString;
	}
	public void setCurrentWorkingPathString(String currentWorkingPathString) {
		this.currentWorkingPathString = currentWorkingPathString;
	}	
	
	public ArrayList<String> getChiledPathStringList() {
		return chiledPathStringList;
	}
	
	public void addChildPathString(String childPathString) {
		chiledPathStringList.add(childPathString);
	}
}
