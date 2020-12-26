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

/**
 * @author Won Jonghoon
 *
 */
public class CoddaHelperSite {
	private String installedPathString;
	private String currentWorkingPathString;
	private String errorMessage;
	
	
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
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	
}