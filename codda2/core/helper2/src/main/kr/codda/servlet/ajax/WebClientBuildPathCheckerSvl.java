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
import kr.codda.model.ProcessResultResponse;
import kr.pe.codda.common.buildsystem.pathsupporter.WebClientBuildSystemPathSupporter;

/**
 * @author Won Jonghoon
 *
 */
public class WebClientBuildPathCheckerSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3014672205034607239L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String selectedMainProjectName = req.getParameter("selectedMainProjectName");
		
		if (null == selectedMainProjectName) {
			String errorMessage = "the parameter selectedMainProjectName is null";
			
			Logger.getGlobal().warning(errorMessage);
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
		String installedPathString = coddaHelperSite.getInstalledPathString();
		
		ProcessResultResponse processResultResponse = new ProcessResultResponse();
		
		
		String webClientBuildPathString = WebClientBuildSystemPathSupporter.getWebClientBuildPathString(installedPathString, selectedMainProjectName);
		File webClientBuildPath = new File(webClientBuildPathString);
		
		if (! webClientBuildPath.exists()) {
			boolean isSuccess = false;
			String errorMessage = new StringBuilder().append("웹 클라이언트 빌드 경로가 존재하지 않습니다").toString();
			
			
			processResultResponse.setSuccess(isSuccess);
			processResultResponse.setMessage(errorMessage);
			
		} else if (! webClientBuildPath.isDirectory()) {
			boolean isSuccess = false;
			String errorMessage = new StringBuilder().append("웹 클라이언트 빌드 경로[")
					.append(webClientBuildPathString)
					.append("]가 디렉토리가 아닙니다").toString();
			
			
			processResultResponse.setSuccess(isSuccess);
			processResultResponse.setMessage(errorMessage);
		} else {
			boolean isSuccess = true;
			String successMessage = "웹 클라이언트 빌드 경로가 정상입니다";
			
			
			processResultResponse.setSuccess(isSuccess);
			processResultResponse.setMessage(successMessage);
			
			coddaHelperSite.setSelectedMainProjectName(selectedMainProjectName);
		}
		
		
		String processResultResponseJsonString = new Gson().toJson(processResultResponse);
		
		// FIXME!
		// Logger.getGlobal().log(Level.INFO, processResultResponseJsonString);
		

		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(processResultResponseJsonString);
	}

}
