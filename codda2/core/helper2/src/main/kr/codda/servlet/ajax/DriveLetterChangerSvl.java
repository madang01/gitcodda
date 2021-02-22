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
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;

/**
 * @author Won Jonghoon
 *
 */
public class DriveLetterChangerSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8591395006113710695L;
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String newSelectedDriveLetter = req.getParameter("newSelectedDriveLetter");
		
		if (null == newSelectedDriveLetter) {
			String errorMessage = "파라미터 '변경할 드라이브 문자'(newSelectedDriveLetter)를 넣어주세요";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
		
		coddaHelperSite.setSelectedDriveLetter(newSelectedDriveLetter);
		coddaHelperSite.setCurrentWorkingPathString(coddaHelperSite.getDriveLetterToCurrentWorkingDirectoryHash().get(newSelectedDriveLetter));
		
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(newSelectedDriveLetter);
	}
}
