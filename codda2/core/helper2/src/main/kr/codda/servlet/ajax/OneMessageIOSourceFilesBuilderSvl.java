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
import kr.pe.codda.common.buildsystem.pathsupporter.AppClientBuildSystemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.WebClientBuildSystemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.builder.IOPartDynamicClassFileContentsBuilderManager;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.codda.common.type.ReadWriteMode;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * @author Won Jonghoon
 *
 */
public class OneMessageIOSourceFilesBuilderSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1665874355497052040L;
	
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
		
		
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			String errorMessage = e.toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		MessageInfo sourceMessageInfo = null;
		try {
			sourceMessageInfo = messageInfoSAXParser.parse(sourceMainProjectMessageInfoXMLFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = e.toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		if (null == sourceMessageInfo) {
			String errorMessage = "the var sourceMessageInfo is null";
			
			Logger log = Logger.getGlobal();
			log.warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		ArrayList<File> validPathListToSaveIOSourceFiles = getValidRootPathListToSaveIOSourceFiles(installedPathString, targetMainProjectName);
		
		try {
			saveIOSourceFiles(validPathListToSaveIOSourceFiles, ioSourceFileAuthor, isSelectedIO, isSelectedCommunicationDirection, sourceMessageInfo);
		} catch(RuntimeException e) {
			String errorMessage = new StringBuilder().append("생성한 입출력 소스 파일들 저장 실패, 상세이유=")
					.append(e.getMessage()).toString();
			Logger log = Logger.getGlobal();
			log.log(Level.WARNING, errorMessage, e);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("알수 없는 에러로 생성한 입출력 소스 파일들 저장 실패, 상세이유=")
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
		res.getWriter().write(sourceMessageID);
	}
	
	private ArrayList<File> getValidRootPathListToSaveIOSourceFiles(String installedPathString, String targetMainProjectName) {
		ArrayList<File> validPathListToSaveIOSourceFiles = new ArrayList<File>();


		String serverIOSourcePathString = ServerBuildSytemPathSupporter.getServerIOSourcePath(installedPathString, targetMainProjectName);
		
		try {
			File serverIOSourcePath = CommonStaticUtil.toValidPath(serverIOSourcePathString, ReadWriteMode.ONLY_WRITE);
			
			validPathListToSaveIOSourceFiles.add(serverIOSourcePath);
		} catch (RuntimeException e) {
			String errorMessge = new StringBuilder()
					.append("the server path[")
					.append(serverIOSourcePathString)
					.append("] of the main projec to save IO source files is not valid, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.fine(errorMessge);
		}

		String appClientIOSourcePathString = AppClientBuildSystemPathSupporter
				.getAppClientIOSourcePath(installedPathString, targetMainProjectName);
		
		try {
			File appClientIOSourcePath = CommonStaticUtil.toValidPath(appClientIOSourcePathString,
					ReadWriteMode.ONLY_WRITE);
			
			validPathListToSaveIOSourceFiles.add(appClientIOSourcePath);
		} catch (RuntimeException e) {
			String errorMessge = new StringBuilder()
					.append("the app client path[")
					.append(appClientIOSourcePathString)
					.append("] of the main projec to save IO source files is not valid, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.fine(errorMessge);
		}
		
		String webClientIOSourcePathString = WebClientBuildSystemPathSupporter
				.getWebClinetIOSourcePath(installedPathString, targetMainProjectName);
		
		try {
			File webClientIOSourcePath = CommonStaticUtil.toValidPath(webClientIOSourcePathString,
					ReadWriteMode.ONLY_WRITE);
			validPathListToSaveIOSourceFiles.add(webClientIOSourcePath);
		} catch (RuntimeException e) {
			String errorMessge = new StringBuilder()
					.append("the app client path[")
					.append(webClientIOSourcePathString)
					.append("] of the main projec to save IO source files is not valid, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.fine(errorMessge);
		}

		return validPathListToSaveIOSourceFiles;
	}
	
	private void saveIOSourceFiles(ArrayList<File> validRootPathListToSaveIOSourceFiles, String ioSourceFileAuthor,
			boolean isSelectedIO, boolean isSelectedCommunicationDirection, MessageInfo sourceMessageInfo) throws RuntimeException {
		
		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager
				.getInstance();

		for (File validRootPathToSaveIOSourceFiles : validRootPathListToSaveIOSourceFiles) {
			String messageID = sourceMessageInfo.getMessageID();
			String messagePathStringToSaveIOSoruceFiles = validRootPathToSaveIOSourceFiles.getAbsolutePath() + File.separator + messageID;
			File messagePathToSaveIOSoruceFiles = new File(messagePathStringToSaveIOSoruceFiles);
			if (!messagePathToSaveIOSoruceFiles.exists()) {
				boolean result = messagePathToSaveIOSoruceFiles.mkdir();
				if (!result) {
					String errorMessage = String.format("fail to create the target message[%s] path[%s]", messageID,
							messagePathStringToSaveIOSoruceFiles);
					
					throw new RuntimeException(errorMessage);
				}
			}

			if (! messagePathToSaveIOSoruceFiles.canWrite()) {
				String errorMessage = String.format("the target message[%s] path[%s] can't be written", messageID,
						messagePathToSaveIOSoruceFiles.getAbsolutePath());
				
				throw new RuntimeException(errorMessage);
			}

			if (isSelectedIO) {
				File messageFile = new File(messagePathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + messageID + ".java");
				File messageEncoderFile = new File(
						messagePathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + messageID + "Encoder.java");
				File messageDecoderFile = new File(
						messagePathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + messageID + "Decoder.java");

				String fileNickname = null;
				fileNickname = "the message class";
				try {
					CommonStaticUtil.saveFile(messageFile, ioFileSetContentsBuilderManager.getMessageSourceFileContents(
							 ioSourceFileAuthor, sourceMessageInfo), CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageFile.getAbsolutePath(), e.toString());
					
					
					throw new RuntimeException(errorMessage);
				}

				fileNickname = "the message encoder class";
				try {
					CommonStaticUtil.saveFile(
									messageEncoderFile, ioFileSetContentsBuilderManager
											.getEncoderSourceFileContents(ioSourceFileAuthor, sourceMessageInfo),
									CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageEncoderFile.getAbsolutePath(), e.toString());
					
					
					throw new RuntimeException(errorMessage);
				}

				fileNickname = "the message decoder class";
				try {
					CommonStaticUtil.saveFile(
									messageDecoderFile, ioFileSetContentsBuilderManager
											.getDecoderSourceFileContents(ioSourceFileAuthor, sourceMessageInfo),
									CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageDecoderFile.getAbsolutePath(), e.toString());
					
					
					throw new RuntimeException(errorMessage);
				}

			}

			if (isSelectedCommunicationDirection) {
				File messageServerCodecFile = new File(
						messagePathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + messageID + "ServerCodec.java");
				File messageClientCodecFile = new File(
						messagePathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + messageID + "ClientCodec.java");

				String fileNickname = null;
				fileNickname = "the server codec class";
				try {
					CommonStaticUtil.saveFile(
							messageServerCodecFile, ioFileSetContentsBuilderManager
									.getServerCodecSourceFileContents(sourceMessageInfo.getDirection(), messageID, ioSourceFileAuthor),
							CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageServerCodecFile.getAbsolutePath(), e.toString());
					
					
					throw new RuntimeException(errorMessage);
				}

				fileNickname = "the client codec class";
				try {
					CommonStaticUtil.saveFile(
							messageClientCodecFile, ioFileSetContentsBuilderManager
									.getClientCodecSourceFileContents(sourceMessageInfo.getDirection(), messageID, ioSourceFileAuthor),
							CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageClientCodecFile.getAbsolutePath(), e.toString());
					
					throw new RuntimeException(errorMessage);
				}
			}
		}
	}
}
