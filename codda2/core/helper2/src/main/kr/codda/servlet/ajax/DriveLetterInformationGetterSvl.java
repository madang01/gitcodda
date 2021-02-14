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

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.model.DriveLetterInformation;

/**
 * @author Won Jonghoon
 *
 */
public class DriveLetterInformationGetterSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3238138332649480956L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
		
		String selectedDriveLetter = coddaHelperSite.getSelectedDriveLetter();
		ArrayList<String> driveLetterList = coddaHelperSite.getDriveLetterList();
		
		
		DriveLetterInformation driveLetterInformation = new DriveLetterInformation();
		driveLetterInformation.setSelectedDriveLetter(selectedDriveLetter);
		driveLetterInformation.setDriveLetterList(driveLetterList);		
		
		String driveLetterInformationJsonString = new Gson().toJson(driveLetterInformation);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(driveLetterInformationJsonString);
	}
}
