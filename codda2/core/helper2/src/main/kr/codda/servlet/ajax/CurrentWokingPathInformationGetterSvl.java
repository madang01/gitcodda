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
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

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
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance(); 
		String currentWorkingPathString = coddaHelperSite.getCurrentWorkingPathString();
		
		if (null == currentWorkingPathString) {
			
			try {
				currentWorkingPathString = new File(".").getCanonicalPath();
			} catch(Exception e) {
				String errorMessage = "failed to get a current working directory's canonical Path";
				
				Logger.getGlobal().warning(errorMessage);
				
				// res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.setContentType("text/html");
				res.setCharacterEncoding("utf-8");
				res.getWriter().print(errorMessage);
				return;
			}
			
			coddaHelperSite.setCurrentWorkingPathString(currentWorkingPathString);
		}
		
		CurrentWokingPathInformation currentWokingPathInformation = CommonStaticUtil.buildCurrentWokingPathInformation(currentWorkingPathString);
		
		String currentWokingPathInformationJsonString = new Gson().toJson(currentWokingPathInformation);
		

		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		// res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfCurrentWorkingPathInformationGetterPage(currentWokingPathInformationJsonString));
		res.getWriter().print(currentWokingPathInformationJsonString);
	}

}
