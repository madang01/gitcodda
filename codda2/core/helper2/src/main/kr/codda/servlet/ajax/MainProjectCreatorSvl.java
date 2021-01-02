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

import com.google.gson.Gson;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.model.MainProjectCreatorResponse;
import kr.pe.codda.common.buildsystem.ProjectBuilder;
import kr.pe.codda.common.exception.BuildSystemException;

/**
 * 메인 프로젝트 생성 서블릿
 * @author Won Jonghoon
 *
 */
public class MainProjectCreatorSvl extends HttpServlet {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4806825874993110267L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String newMainProjectName = req.getParameter("newMainProjectName");
		if (null == newMainProjectName) {
			String errorMessage = "the paramter newMainProjectName is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance(); 
		String installedPathString = coddaHelperSite.getInstalledPathString();
		
		if (null == installedPathString) {
			String errorMessage = "failed to get a installed directory";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		ProjectBuilder projectBuilder = null;
		try {
			projectBuilder = new ProjectBuilder(installedPathString, newMainProjectName);
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		final boolean isServer = true;
		final boolean isAppClient = true;
		final boolean isWebClient = false;
		final String servletSystemLibraryPathString = "";
		try {
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		MainProjectCreatorResponse mainProjectCreatorResponse = new MainProjectCreatorResponse();
		mainProjectCreatorResponse.setInstalledPathString(installedPathString);
		mainProjectCreatorResponse.setNewMainProjectName(newMainProjectName);
		
		String mainProjectCreatorResponseJsonString = new Gson().toJson(mainProjectCreatorResponse);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(mainProjectCreatorResponseJsonString);
		
	}
}
