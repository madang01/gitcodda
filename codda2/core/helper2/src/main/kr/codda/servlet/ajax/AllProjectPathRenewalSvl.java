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
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.pe.codda.common.buildsystem.ProjectBuilder;
import kr.pe.codda.common.exception.BuildSystemException;

/**
 * @author Won Jonghoon
 *
 */
public class AllProjectPathRenewalSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6117200637136330020L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
	
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance(); 
		String installedPathString = coddaHelperSite.getInstalledPathString();
		
		if (null == installedPathString) {
			String errorMessage = "failed to get a installed directory of CoddaHelperSite";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		ArrayList<String> mainProjectNameList = coddaHelperSite.getMainProjectNameList();
		
		
		for (String mainProjectName : mainProjectNameList) {
			ProjectBuilder projectBuilder = null;
			try {
				projectBuilder = new ProjectBuilder(installedPathString, mainProjectName);
				
				projectBuilder.applyInstalledPath();
			} catch (BuildSystemException e) {
				String errorMessage = e.getMessage();
				
				Logger.getGlobal().warning(errorMessage);
				
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.setContentType("text/html");
				res.setCharacterEncoding("utf-8");
				res.getWriter().print("메인프로젝트[" + mainProjectName + "] 에서 에러 발생, 사유:" + errorMessage);
				return;
			}
		}
		
		/*
		AllInstalledMainProjectInformation installedMainProjectInformation = new AllInstalledMainProjectInformation();
		installedMainProjectInformation.setInstalledPathString(installedPathString);
		installedMainProjectInformation.setMainProjectNameList(mainProjectNameList);
		
		
		String installedMainProjectInformationJsonString = new Gson().toJson(installedMainProjectInformation);
		*/
		// mainProjectNameListAppliedAsInstalledPathJsonString
		String mainProjectNameListAppliedInstalledPathJsonString = new Gson().toJson(mainProjectNameList);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(mainProjectNameListAppliedInstalledPathJsonString);
	}
}
