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
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.common.util.SequencedPropertiesUtil;

/**
 * @author Won Jonghoon
 *
 */
public class ServletSystemLibPathSaverSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9181016964919714693L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String servletSystemLibPathString = req.getParameter("servletSystemLibPath");
		if (null == servletSystemLibPathString) {
			String errorMessage = "the parameter servletSystemLibPath is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
		final String installedPathString = coddaHelperSite.getInstalledPathString();
		final String selectedMainProjectName = coddaHelperSite.getSelectedMainProjectName();
		
		final String webClientAntPropertiesFilePathString = WebClientBuildSystemPathSupporter.getWebClientAntPropertiesFilePath(installedPathString, selectedMainProjectName);
		
		final String titleOfConfigFile = "project[" + selectedMainProjectName  + "]'s ant config file";
		final SequencedProperties targetSequencedProperties = new SequencedProperties();
		targetSequencedProperties.put("servlet.systemlib.path", servletSystemLibPathString);
		
		
		File servletSystemLibPath = new File(servletSystemLibPathString);
		
		ProcessResultResponse processResultResponse = new ProcessResultResponse();
		
		if (! servletSystemLibPath.exists()) {
			
			boolean isSuccess = false;
			String successrMessage = new StringBuilder().append("서블릿 시스템 라이브러리 경로[")
					.append(servletSystemLibPathString)
					.append("]가 존재하지 않습니다").toString();
			processResultResponse.setSuccess(isSuccess);
			processResultResponse.setMessage(successrMessage);
			
		} else if (! servletSystemLibPath.isDirectory()) {
			boolean isSuccess = false;
			String successrMessage = new StringBuilder().append("서블릿 시스템 라이브러리 경로[")
					.append(servletSystemLibPathString)
					.append("]가 디렉토리가 아닙니다").toString();
			processResultResponse.setSuccess(isSuccess);
			processResultResponse.setMessage(successrMessage);			
		} else {
			final boolean isSuccess = true;
			final String successrMessage;
			
			File webClientAntPropertiesFile = new File(webClientAntPropertiesFilePathString);
			
			if (webClientAntPropertiesFile.exists()) {
				SequencedPropertiesUtil.overwriteSequencedPropertiesFile(targetSequencedProperties, titleOfConfigFile, webClientAntPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				
				successrMessage = new StringBuilder().append("신규 webAnt.properties 파일의 서블릿 시스템 라이브러리 경로를 성공적으로 저장하였습니다").toString();
			} else {
				SequencedPropertiesUtil.createNewSequencedPropertiesFile(targetSequencedProperties, titleOfConfigFile, webClientAntPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				
				successrMessage = new StringBuilder().append("기존 webAnt.properties 파일의 서블릿 시스템 라이브러리 경로를 성공적으로 수정하였습니다").toString();
			}
			
			
			processResultResponse.setSuccess(isSuccess);
			processResultResponse.setMessage(successrMessage);
		}
		
		String processResultResponseJsonString = new Gson().toJson(processResultResponse);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(processResultResponseJsonString);
	}

}
