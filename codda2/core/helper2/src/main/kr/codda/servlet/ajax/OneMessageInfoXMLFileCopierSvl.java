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

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * @author Won Jonghoon
 *
 */
public class OneMessageInfoXMLFileCopierSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4916415607059977285L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String sourceMessageID = req.getParameter("sourceMessageID");
		if (null == sourceMessageID) {
			String errorMessage = "the parameter sourceMessageID is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		String targetMainProjectName = req.getParameter("targetMainProjectName");
		
		if (null == targetMainProjectName) {
			String errorMessage = "the parameter targetMainProjectName is null";
			
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
			String errorMessage = "failed to get a installed directory of CoddaHelperSite";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		String sourceMainProjectName = coddaHelperSite.getSelectedMainProjectName();
		
		if (null == sourceMainProjectName) {
			String errorMessage = "failed to get a selected main project name of CoddaHelperSite";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		if (sourceMainProjectName.equals(targetMainProjectName)) {
			String errorMessage = new StringBuilder()
					.append("메시지 정보 XML 파일은 동일한 메인 프로젝트[")
					.append(targetMainProjectName)
					.append("]로 복사 할 수 없습니다").toString();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		String messageInfoPathStringToSaveIOSourceFiles = ProjectBuildSytemPathSupporter.getProjectMessageInfoDirectoryPathString(installedPathString, targetMainProjectName);
		
		File messageInfoPathToSaveIOSourceFiles = new File(messageInfoPathStringToSaveIOSourceFiles);
		
		if (! messageInfoPathToSaveIOSourceFiles.exists()) {
			String errorMessage = new StringBuilder()
					.append("입출력 파일을 저장할 메인 프로젝트의 메시지 정보 파일들이 위치하는 경로[")
					.append(messageInfoPathStringToSaveIOSourceFiles)
					.append("]가 존재하지 않습니다").toString();
			
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		if (! messageInfoPathToSaveIOSourceFiles.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("입출력 파일을 저장할 메인 프로젝트의 메시지 정보 파일들이 위치하는 경로[")
					.append(messageInfoPathStringToSaveIOSourceFiles)
					.append("]가 디렉토리가 아닙니다").toString();
			
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		String sourceMainProjectMessageInfoXMLFilePathString = ProjectBuildSytemPathSupporter.getProjectMessageInfoFilePathString(installedPathString, sourceMainProjectName, sourceMessageID);
		File sourceMainProjectMessageInfoXMLFile = new File(sourceMainProjectMessageInfoXMLFilePathString);
		
		if (! sourceMainProjectMessageInfoXMLFile.exists()) {
			String errorMessage = new StringBuilder()
					.append("the selected main project's message info XML file[")
					.append(sourceMainProjectMessageInfoXMLFilePathString)
					.append("] doesn'exit").toString();
			
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		if (! sourceMainProjectMessageInfoXMLFile.isFile()) {
			String errorMessage = new StringBuilder()
					.append("the selected main project's message info XML file[")
					.append(sourceMainProjectMessageInfoXMLFilePathString)
					.append("] is not a file").toString();
			
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		String targetMainProjectMessageInfoXMLFilePathString = ProjectBuildSytemPathSupporter.getProjectMessageInfoFilePathString(installedPathString, targetMainProjectName, sourceMessageID);
		
		File targetMainProjectMessageInfoXMLFile = new File(targetMainProjectMessageInfoXMLFilePathString);
		
		try {
			CommonStaticUtil.copyTransferToFile(sourceMainProjectMessageInfoXMLFile, targetMainProjectMessageInfoXMLFile);
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("입출력 파일을 저장할 메인 프로젝트의 메시지 정보 파일[")
					.append(messageInfoPathStringToSaveIOSourceFiles)
					.append("]을 저장하는데 실패하였습니다, 상세 사유=").append(e.getMessage()).toString();
			
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().write(sourceMessageID);
	}

}
