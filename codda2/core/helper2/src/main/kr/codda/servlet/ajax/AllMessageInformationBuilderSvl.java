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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import com.google.gson.Gson;

import kr.codda.model.AllMessageInformation;
import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.model.MessageInformation;
import kr.codda.util.FileLastModifiedComparator;
import kr.codda.util.XMLFileFilter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.codda.common.type.MessageTransferDirectionType;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * @author Won Jonghoon
 *
 */
public class AllMessageInformationBuilderSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7935851498801868716L;
	
	
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
		
		String selectedMainProjectName = coddaHelperSite.getSelectedMainProjectName();
		
		if (null == selectedMainProjectName) {
			String errorMessage = "failed to get a selected main project name of CoddaHelperSite";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		ArrayList<String> mainProjectNameList = coddaHelperSite.getMainProjectNameList();
		
		if (null == mainProjectNameList) {
			String errorMessage = "failed to get a main project name list of CoddaHelperSite";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		String selectedMainProjectMessageInfoPathString = ProjectBuildSytemPathSupporter.getProjectMessageInfoDirectoryPathString(installedPathString, selectedMainProjectName);
		
		File selectedMainProjectMessageInfoPath = new File(selectedMainProjectMessageInfoPathString);
		
		if (! selectedMainProjectMessageInfoPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the selected main project[")
					.append(selectedMainProjectName)
					.append("]'s message infomation path[")
					.append(selectedMainProjectMessageInfoPathString)
					.append("] doesn'exit").toString();
			
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		if (! selectedMainProjectMessageInfoPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the selected main project[")
					.append(selectedMainProjectName)
					.append("]'s message infomation path[")
					.append(selectedMainProjectMessageInfoPathString)
					.append("] is not a directory").toString();
			
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		File selectedMainProjectMessageInfoXMLFiles[] = selectedMainProjectMessageInfoPath.listFiles(new XMLFileFilter());
		if (null == selectedMainProjectMessageInfoXMLFiles) {
			String errorMessage = "the var selectedMainProjectMessageInfoXMLFiles is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		if (0 == selectedMainProjectMessageInfoXMLFiles.length) {
			String errorMessage = new StringBuilder()
					.append("there are no message info XML files in the selected main project[")
					.append(selectedMainProjectName)
					.append("]").toString();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		Arrays.sort(selectedMainProjectMessageInfoXMLFiles, new FileLastModifiedComparator());
		
		AllMessageInformation allMessageInformation = new AllMessageInformation();
		allMessageInformation.setInstalledPathString(installedPathString);
		allMessageInformation.setSelectedMainProjectName(selectedMainProjectName);
		allMessageInformation.setMainProjectNameList(mainProjectNameList);
		allMessageInformation.setSelectedMainProjectMessageInfoPathString(selectedMainProjectMessageInfoPathString);
		
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		
		for (int i = 0; i < selectedMainProjectMessageInfoXMLFiles.length; i++) {
			File projectMessageInfoXMLFile = selectedMainProjectMessageInfoXMLFiles[i];
			
			if (! projectMessageInfoXMLFile.isFile()) {
				String errorMessage = new StringBuilder()
						.append("the message info XML file[")
						.append(projectMessageInfoXMLFile.getName())
						.append("] of in the selected main proejct[")
						.append(selectedMainProjectName)
						.append("] is not a file").toString();
				
				Logger log = Logger.getGlobal();
				log.info(errorMessage);
				continue;
			}

			if (! projectMessageInfoXMLFile.canRead()) {
				String errorMessage = new StringBuilder()
						.append("It can not read the message info XML file[")
						.append(projectMessageInfoXMLFile.getName())
						.append("] of in the selected main proejct[")
						.append(selectedMainProjectName)
						.append("]").toString();
				
				Logger log = Logger.getGlobal();
				log.info(errorMessage);
				continue;
			}
			
			MessageInfoSAXParser messageInfoSAXParser = null;
			try {
				messageInfoSAXParser = new MessageInfoSAXParser();
			} catch (SAXException e) {
				String errorMessage = e.toString();
				Logger log = Logger.getGlobal();
				log.log(Level.WARNING, errorMessage, e);
				continue;
			}
			
			MessageInfo messageInfo = null;
			try {
				messageInfo = messageInfoSAXParser.parse(projectMessageInfoXMLFile, true);
			} catch (IllegalArgumentException | SAXException | IOException e) {
				String errorMessage = e.toString();
				Logger log = Logger.getGlobal();
				log.log(Level.WARNING, errorMessage, e);
				continue;
			}
			
			if (null == messageInfo) {
				String errorMessage = "the var messageInfo is null";
				
				Logger log = Logger.getGlobal();
				log.info(errorMessage);
				continue;
			}
			
			final String communicationDirection;
			if (messageInfo.getDirection() == MessageTransferDirectionType.FROM_ALL_TO_ALL) {
				communicationDirection = "client <-> server";
				
			} else if (messageInfo.getDirection() == MessageTransferDirectionType.FROM_CLIENT_TO_SERVER) {
				communicationDirection = "client -> server";
			} else if (messageInfo.getDirection() == MessageTransferDirectionType.FROM_SERVER_TO_CLINET) {
				communicationDirection = "server -> client";
			} else {
				communicationDirection = "no direction";
			}
			
			final File messageInfoXMLFile = messageInfo.getMessageInfoXMLFile(); 
			final String lastModifiedDate = sdf.format(messageInfo.getLastModified());
			final String contents;
			try {
				contents = new String(CommonStaticUtil.readFileToByteArray(messageInfoXMLFile, 10*1024*1024L), "UTF-8");
			} catch(IOException e) {
				String errorMessage = new StringBuilder()
						.append("최대 크기 10 Mbytes 까지의 메시지 식별자 파일[")
						.append(messageInfoXMLFile.getAbsolutePath())
						.append("] 읽기 실패, errmsg=")
						.append(e.getMessage()).toString();
				Logger log = Logger.getGlobal();
				log.warning(errorMessage);;
				continue;
			}

			MessageInformation newMessageInformation = new MessageInformation();
			newMessageInformation.setMessageID(messageInfo.getMessageID());
			newMessageInformation.setLastModifiedDate(lastModifiedDate);
			newMessageInformation.setCommunicationDirection(communicationDirection);
			newMessageInformation.setContents(contents);
			
			allMessageInformation.addMessageInformation(newMessageInformation);
			
			
			
		}
		
		String allMessageInformationJsonString = new Gson().toJson(allMessageInformation);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(allMessageInformationJsonString);
	}
}
