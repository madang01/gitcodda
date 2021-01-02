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
import kr.codda.model.MainProjectRemoverResponse;
import kr.pe.codda.common.buildsystem.ProjectBuilder;
import kr.pe.codda.common.exception.BuildSystemException;

/**
 * 메인 프로젝트 삭제 서블릿
 * @author Won Jonghoon
 *
 */
public class MainProjectRemoverSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4852133133604510525L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String mainProjectNameToDelete = req.getParameter("mainProjectNameToDelete");
		if (null == mainProjectNameToDelete) {
			String errorMessage = "the paramter mainProjectNameToDelete is null";
			
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
			projectBuilder = new ProjectBuilder(installedPathString, mainProjectNameToDelete);
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		try {
			projectBuilder.dropProject();
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		MainProjectRemoverResponse mainProjectRemoverResponse = new MainProjectRemoverResponse();
		mainProjectRemoverResponse.setInstalledPathString(installedPathString);
		mainProjectRemoverResponse.setMainProjectNameToDelete(mainProjectNameToDelete);
		
		String mainProjectRemoverResponseJsonString = new Gson().toJson(mainProjectRemoverResponse);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(mainProjectRemoverResponseJsonString);
		
	}

}
