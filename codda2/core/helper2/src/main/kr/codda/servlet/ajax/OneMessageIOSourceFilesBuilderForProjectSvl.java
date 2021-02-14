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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.util.CommonStaticUtil;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.MessageInfoSAXParser;

/**
 * @author Won Jonghoon
 *
 */
public class OneMessageIOSourceFilesBuilderForProjectSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1665874355497052040L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String selectedMessageID = req.getParameter("selectedMessageID");
		if (null == selectedMessageID) {
			String errorMessage = "the parameter selectedMessageID is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		String toMainProjectName = req.getParameter("toMainProjectName");
		if (null == toMainProjectName) {
			String errorMessage = "the parameter toMainProjectName is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		String ioSourceFileAuthor = req.getParameter("ioSourceFileAuthor");
		if (null == ioSourceFileAuthor) {
			String errorMessage = "the parameter ioSourceFileAuthor is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		String paramForIsSelectedIO = req.getParameter("isSelectedIO");
		if (null == paramForIsSelectedIO) {
			String errorMessage = "the parameter isSelectedIO is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		boolean isSelectedIO;
		if ("true".equals(paramForIsSelectedIO)) {
			isSelectedIO = true;
		} else if ("false".equals(paramForIsSelectedIO)) {
			isSelectedIO = false;
		} else {
			String errorMessage = new StringBuilder()
					.append("the parameter isSelectedIO[")
					.append(paramForIsSelectedIO)
					.append("] is bad becase it is not a boolean type").toString();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		String paramForIsSelectedCommunicationDirection = req.getParameter("isSelectedCommunicationDirection");
		if (null == paramForIsSelectedCommunicationDirection) {
			String errorMessage = "the parameter isSelectedCommunicationDirection is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		boolean isSelectedCommunicationDirection;
		if ("true".equals(paramForIsSelectedCommunicationDirection)) {
			isSelectedCommunicationDirection = true;
		} else if ("false".equals(paramForIsSelectedCommunicationDirection)) {
			isSelectedCommunicationDirection = false;
		} else {
			String errorMessage = new StringBuilder()
					.append("the parameter isSelectedCommunicationDirection[")
					.append(paramForIsSelectedIO)
					.append("] is bad becase it is not a boolean type").toString();
			
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
		
		String fromMainProjectName = coddaHelperSite.getSelectedMainProjectName();
		
		if (null == fromMainProjectName) {
			String errorMessage = "failed to get a selected main project name of CoddaHelperSite";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		try {
			buildIOSourceFilesForProject(installedPathString, fromMainProjectName, selectedMessageID, ioSourceFileAuthor, isSelectedIO, isSelectedCommunicationDirection, toMainProjectName);
		} catch(BuildSystemException e) {
			String errorMessage = e.getMessage();
			
			Logger log = Logger.getGlobal();
			log.warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("fail to save io source files of the message[")
					.append(selectedMessageID)
					.append("] because of unknown error, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().write(selectedMessageID);
	}
	
	/**
	 * 지정한 메시지 식별자에 1:1 대응하는 메시지 정보를 읽어와 지정한 메인 프로젝트에  입출력 소스 파일들들을 원하는 파트에 맞게 만들어 저장한다.
	 * 
	 * @param installedPathString 설치 경로
	 * @param fromMainProjectName 지정한 메시지 식별자를 읽어올 메인 프로젝트
	 * @param selectedMessageID 입출력 소스 파일들을 만들기 위해 사용자가 지정한 메시지 식별자
	 * @param ioSourceFileAuthor 입출력 소스 파일 저자
	 * @param isSelectedIO 입출력 파트에 해당하는 입출력 소스 파일 저장 여부
	 * @param isSelectedCommunicationDirection 통신 방향성 파트에 해당하는 입출력 소스 파일 저장 여부
	 * @param toMainProjectName 생성한 입출력 소스 파일을 저장할 메인 프로젝트 이름
	 * @throws BuildSystemException 에러 발생시 던지는 예외
	 */
	public void buildIOSourceFilesForProject(String installedPathString, String fromMainProjectName, String selectedMessageID, 
			String ioSourceFileAuthor, boolean isSelectedIO, boolean isSelectedCommunicationDirection, String toMainProjectName) throws BuildSystemException {
		
		String sourceMessageInfoXMLFilePathString = ProjectBuildSytemPathSupporter.getProjectMessageInfoFilePathString(installedPathString, fromMainProjectName, selectedMessageID);
		File sourceMessageInfoXMLFile = new File(sourceMessageInfoXMLFilePathString);
		
		if (! sourceMessageInfoXMLFile.exists()) {
			String errorMessage = new StringBuilder()
					.append("the selected main project's message info XML file[")
					.append(sourceMessageInfoXMLFilePathString)
					.append("] doesn'exit").toString();
			// Logger.getGlobal().warning(errorMessage);
			
			throw new BuildSystemException(errorMessage);
		}
		
		if (! sourceMessageInfoXMLFile.isFile()) {
			String errorMessage = new StringBuilder()
					.append("the selected main project's message info XML file[")
					.append(sourceMessageInfoXMLFilePathString)
					.append("] is not a file").toString();			
			
			throw new BuildSystemException(errorMessage);
		}
		
		
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			String errorMessage = e.toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			
			throw new BuildSystemException(errorMessage);
		}
		
		MessageInfo sourceMessageInfo = null;
		try {
			sourceMessageInfo = messageInfoSAXParser.parse(sourceMessageInfoXMLFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = e.toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			
			throw new BuildSystemException(errorMessage);
		}
		
		if (null == sourceMessageInfo) {
			String errorMessage = "the var sourceMessageInfo is null";
			
			throw new BuildSystemException(errorMessage);
		}
		
		ArrayList<File> validPathListToSaveIOSourceFiles = CommonStaticUtil.getValidPathListToSaveIOSourceFiles(installedPathString, toMainProjectName, selectedMessageID);
		
		for (File validPathToSaveIOSourceFiles : validPathListToSaveIOSourceFiles) {
			CommonStaticUtil.saveIOSourceFiles(validPathToSaveIOSourceFiles, ioSourceFileAuthor, isSelectedIO, isSelectedCommunicationDirection, sourceMessageInfo);
		}
	}
}
