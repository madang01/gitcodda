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
package kr.codda.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Logger;

import kr.codda.common.type.FileChooserType;
import kr.codda.main.HelperServer;
import kr.codda.model.CurrentWokingPathInformation;
import kr.pe.codda.common.buildsystem.pathsupporter.AppClientBuildSystemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.WebClientBuildSystemPathSupporter;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.common.message.builder.IOPartDynamicClassFileContentsBuilderManager;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.type.ReadWriteMode;

/**
 * @author Won Jonghoon
 *
 */
public abstract class CommonStaticUtil {
	public static final Charset PAGE_CHARSET = Charset.forName("UTF-8"); 
	
	
	public static String readErrorPageContents() throws IOException {
		
		return readPageContents("/webapp/error.html");
	}
	
	
	public static String readPageContents(String fileURL) throws IOException {
		
		String content = null;
		InputStream is = HelperServer.class.getResourceAsStream(fileURL);
		try {
			
			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);
			
			content = new String(buffer, PAGE_CHARSET);
		} finally {
			if (null != is) {
				is.close();
			}
		}
		return content;
	}
	
	public static CurrentWokingPathInformation buildCurrentWokingPathInformation(FileChooserType fileChooserType, File currentWorkingPath) {
		CurrentWokingPathInformation currentWokingPathInformation = new CurrentWokingPathInformation();
		
		currentWokingPathInformation.setCurrentWorkingPathString(currentWorkingPath.getAbsolutePath());
		
		if (FileChooserType.FILE.equals(fileChooserType)) {			
			for (File childFile : currentWorkingPath.listFiles()) {
				if (childFile.isDirectory()) {
					currentWokingPathInformation.addDirectoryPathString(childFile.getName());
				} else if (childFile.isFile()) {
					currentWokingPathInformation.addFilePathString(childFile.getName());
				}
			}
		} else {
			for (File childFile : currentWorkingPath.listFiles()) {
				if (childFile.isDirectory()) {
					currentWokingPathInformation.addDirectoryPathString(childFile.getName());
				}
			}
		}
		
		
		return currentWokingPathInformation;
		
	}
	
	public static ArrayList<String> getMainProjectNameList(String installedPathString) throws BuildSystemException {
		String projectBasePathString = ProjectBuildSytemPathSupporter.getProjectBasePathString(installedPathString);

		File projectBasePath = new File(projectBasePathString);
		if (! projectBasePath.exists()) {
			String errorMessage = String.format(
					"the codda installed path(=parameter installedPathString[%s])'s the project base path[%s] doesn't exist",
					installedPathString, projectBasePathString);

			// log.warn(errorMessage);

			throw new BuildSystemException(errorMessage); 
		}

		if (!projectBasePath.isDirectory()) {
			String errorMessage = String.format(
					"the codda installed path(=parameter installedPathString[%s])'s the project base path[%s] is not a direcotry",
					installedPathString, projectBasePathString);
			// log.warn(errorMessage);

			throw new BuildSystemException(errorMessage);
		}

		if (!projectBasePath.canRead()) {
			String errorMessage = String.format(
					"the codda installed path(=parameter installedPathString[%s])'s the project base path[%s] doesn't hava permission to read",
					installedPathString, projectBasePathString);
			// log.warn(errorMessage);

			throw new BuildSystemException(errorMessage);
		}

		ArrayList<String> mainProjectNameList = new ArrayList<String>();
		
		File[] projectBasePathList = projectBasePath.listFiles();
		
		if (null == projectBasePathList) {
			String errorMessage = "the var projectBasePathList is null";
			
			throw new BuildSystemException(errorMessage);
		}

		for (File fileOfList : projectBasePathList) {
			if (fileOfList.isDirectory()) {
				if (!fileOfList.canRead()) {
					String errorMessage = String.format(
							"the project base path[%s] doesn't hava permission to read",
							fileOfList.getAbsolutePath());
					// log.warn(errorMessage);

					throw new BuildSystemException(errorMessage);
				}

				if (!fileOfList.canWrite()) {
					String errorMessage = String.format(
							"the project base path[%s] doesn't hava permission to write",
							fileOfList.getAbsolutePath());
					// log.warn(errorMessage);

					throw new BuildSystemException(errorMessage);
				}

				mainProjectNameList.add(fileOfList.getName());
			}
		}
		
		return mainProjectNameList;
	}
	
	/**
	 * <pre>
	 * 지정한 설치 경로에 있는 지정한 메인 프로젝트 속 지정한 메시지 식별자에 맞는 입출력 소스 파일들 위치 목록을 반환한다.
	 * 단 이때 신규 메시지에 대한 입출력 소스 파일들을 저장할 경로가 없는 경우 '신규 메시지 식별자'로 자식 경로로 새롭게 만든다.
	 * 
	 * 참고 : 한 프로젝트에서는 최대 3군대의 경로에서 입출력 소스 파일들이 위치한다.
	 * 첫번째 서버, 두번째 응용어플 클라이언트, 마지막 세번째 웹 클라이언트
	 * </pre>
	 * 
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트
	 * @param messageID 메시지 식별자
	 * @return 지정한 설치 경로에 있는 지정한 메인 프로젝트 속 지정한 메시지 식별자에 맞는 입출력 소스 파일들 위치 목록
	 * @throws BuildSystemException 예외 발생시 던진는 예외
	 */
	public static  ArrayList<File> getValidPathListToSaveIOSourceFiles(String installedPathString, String mainProjectName, String messageID) throws BuildSystemException {
		ArrayList<File> validPathListToSaveIOSourceFiles = new ArrayList<File>();

		String serverIOSourceRootPathString = ServerBuildSytemPathSupporter.getServerIOSourcePath(installedPathString, mainProjectName);
		
		try {
			File serverIOSourceRootPath = kr.pe.codda.common.util.CommonStaticUtil.toValidPath(serverIOSourceRootPathString, ReadWriteMode.ONLY_WRITE);
			
			String serverIOSourcePathString = serverIOSourceRootPath.getAbsolutePath() + File.separator + messageID;
			File serverIOSourcePath = new File(serverIOSourcePathString);
			if (! serverIOSourcePath.exists()) {
				boolean result = serverIOSourcePath.mkdir();
				
				if (! result) {
					String errorMessage = new StringBuilder().append("can't create the path[")
							.append(serverIOSourcePathString)
							.append("] to save io source files of the message[")
							.append(messageID)
							.append("]").toString();
					
					throw new BuildSystemException(errorMessage);
				}
				
				if (! serverIOSourcePath.canWrite()) {
					String errorMessage = new StringBuilder().append("the path[")
							.append(serverIOSourcePathString)
							.append("] to save io source files has no write permission").toString();
					
					throw new BuildSystemException(errorMessage);
				}
			}

			validPathListToSaveIOSourceFiles.add(serverIOSourcePath);
		} catch (RuntimeException e) {
			String errorMessge = new StringBuilder()
					.append("the server path[")
					.append(serverIOSourceRootPathString)
					.append("] of the main projec to save IO source files is not valid, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.fine(errorMessge);
		}

		String appClientIOSourceRootPathString = AppClientBuildSystemPathSupporter
				.getAppClientIOSourcePath(installedPathString, mainProjectName);
		
		try {
			File appClientIOSourceRootPath = kr.pe.codda.common.util.CommonStaticUtil.toValidPath(appClientIOSourceRootPathString,
					ReadWriteMode.ONLY_WRITE);
			
			String appClientIOSourcePathString = appClientIOSourceRootPath.getAbsolutePath() + File.separator + messageID;
			File appClientIOSourcePath = new File(appClientIOSourcePathString);
			
			if (! appClientIOSourcePath.exists()) {
				boolean result = appClientIOSourcePath.mkdir();
				
				if (! result) {
					String errorMessage = new StringBuilder().append("can't create the path[")
							.append(appClientIOSourcePathString)
							.append("] to save io source files of the message[")
							.append(messageID)
							.append("]").toString();
					
					throw new BuildSystemException(errorMessage);
				}
				
				if (! appClientIOSourcePath.canWrite()) {
					String errorMessage = new StringBuilder().append("the path[")
							.append(appClientIOSourcePathString)
							.append("] to save io source files has no write permission").toString();
					
					throw new BuildSystemException(errorMessage);
				}
			}

			validPathListToSaveIOSourceFiles.add(appClientIOSourcePath);
		} catch (RuntimeException e) {
			String errorMessge = new StringBuilder()
					.append("the app client path[")
					.append(appClientIOSourceRootPathString)
					.append("] of the main projec to save IO source files is not valid, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.fine(errorMessge);
		}
		
		String webClientIOSourceRootPathString = WebClientBuildSystemPathSupporter
				.getWebClinetIOSourcePath(installedPathString, mainProjectName);
		
		try {
			File webClientIOSourceRootPath = kr.pe.codda.common.util.CommonStaticUtil.toValidPath(webClientIOSourceRootPathString,
					ReadWriteMode.ONLY_WRITE);
			
			String webClientIOSourcePathString = webClientIOSourceRootPath.getAbsolutePath() + File.separator + messageID;
			File webClientIOSourcePath = new File(webClientIOSourcePathString);
			
			
			if (! webClientIOSourcePath.exists()) {
				boolean result = webClientIOSourcePath.mkdir();
				
				if (! result) {
					String errorMessage = new StringBuilder().append("can't create the path[")
							.append(webClientIOSourcePathString)
							.append("] to save io source files of the message[")
							.append(messageID)
							.append("]").toString();
					
					throw new BuildSystemException(errorMessage);
				}
				
				if (! webClientIOSourcePath.canWrite()) {
					String errorMessage = new StringBuilder().append("the path[")
							.append(webClientIOSourcePathString)
							.append("] to save io source files has no write permission").toString();
					
					throw new BuildSystemException(errorMessage);
				}
			}
			
			validPathListToSaveIOSourceFiles.add(webClientIOSourcePath);
		} catch (RuntimeException e) {
			String errorMessge = new StringBuilder()
					.append("the app client path[")
					.append(webClientIOSourceRootPathString)
					.append("] of the main projec to save IO source files is not valid, errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getGlobal();
			log.fine(errorMessge);
		}

		return validPathListToSaveIOSourceFiles;
	}
	
	/**
	 * 지정한 경로에 지정한 입출력 파트의 입출력 소스 파일들 저장 여부와 지정한 통신 방향 파트의 입출력 소스 파일들 저장 여부에 맞추어서 지정한 메시지 정보를 바탕으로 입출력 파일을 만든다.
	 * 
	 * @param pathToSaveIOSoruceFiles 입출력 소스 파일들을 저장할 위치
	 * @param ioSourceFileAuthor 입출력 소스 파일 작성자
	 * @param isSelectedIO 입출력 파트의 입출력 소스 파일들 저장 여부 
	 * @param isSelectedCommunicationDirection 통신 방향 파타의 입출력 소스 파일 저장 여부
	 * @param sourceMessageInfo 메시지 정보
	 * @throws BuildSystemException 예외 발생시 던진는 예외
	 */
	public static void saveIOSourceFiles(File pathToSaveIOSoruceFiles, String ioSourceFileAuthor,
			boolean isSelectedIO, boolean isSelectedCommunicationDirection, MessageInfo sourceMessageInfo) throws BuildSystemException {
		
		 
		
		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager
				.getInstance();
		
		String messageID = sourceMessageInfo.getMessageID();
		
		if (isSelectedIO) {
			File messageFile = new File(pathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + IOPartDynamicClassNameUtil.getMessageClassShortName(messageID) + ".java");
			File messageEncoderFile = new File(
					pathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + IOPartDynamicClassNameUtil.getMessageEncoderClassShortName(messageID) + ".java");
			File messageDecoderFile = new File(
					pathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + IOPartDynamicClassNameUtil.getMessageDecoderClassShortName(messageID) + ".java");

			try {
				kr.pe.codda.common.util.CommonStaticUtil.saveFile(messageFile, ioFileSetContentsBuilderManager.getMessageSourceFileContents(
						 ioSourceFileAuthor, sourceMessageInfo), CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = new StringBuilder().append("fail to save the message file[")
						.append(messageFile.getAbsolutePath())
						.append("]::errmsg=").toString();
				
				throw new BuildSystemException(errorMessage);
			}
			try {
				kr.pe.codda.common.util.CommonStaticUtil.saveFile(
								messageEncoderFile, ioFileSetContentsBuilderManager
										.getEncoderSourceFileContents(ioSourceFileAuthor, sourceMessageInfo),
								CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = new StringBuilder().append("fail to save the message encoder file[")
						.append(messageEncoderFile.getAbsolutePath())
						.append("]::errmsg=").toString();
				
				
				throw new BuildSystemException(errorMessage);
			}

			try {
				kr.pe.codda.common.util.CommonStaticUtil.saveFile(
								messageDecoderFile, ioFileSetContentsBuilderManager
										.getDecoderSourceFileContents(ioSourceFileAuthor, sourceMessageInfo),
								CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = new StringBuilder().append("fail to save the message decoder file[")
						.append(messageDecoderFile.getAbsolutePath())
						.append("]::errmsg=").toString();
				
				
				throw new BuildSystemException(errorMessage);
			}
		}

		if (isSelectedCommunicationDirection) {
			File messageServerCodecFile = new File(
					pathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + IOPartDynamicClassNameUtil.getServerMessageCodecClassShortName(messageID) + ".java");
			File messageClientCodecFile = new File(
					pathToSaveIOSoruceFiles.getAbsolutePath() + File.separator + IOPartDynamicClassNameUtil.getClientMessageCodecClassShortName(messageID) + ".java");
			
			try {
				kr.pe.codda.common.util.CommonStaticUtil.saveFile(
						messageServerCodecFile, ioFileSetContentsBuilderManager
								.getServerCodecSourceFileContents(sourceMessageInfo.getDirection(), messageID, ioSourceFileAuthor),
						CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = new StringBuilder().append("fail to save the message server codec file[")
						.append(messageServerCodecFile.getAbsolutePath())
						.append("]::errmsg=").toString();
				
				
				throw new BuildSystemException(errorMessage);
			}

			
			try {
				kr.pe.codda.common.util.CommonStaticUtil.saveFile(
						messageClientCodecFile, ioFileSetContentsBuilderManager
								.getClientCodecSourceFileContents(sourceMessageInfo.getDirection(), messageID, ioSourceFileAuthor),
						CommonStaticFinalVars.SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = new StringBuilder().append("fail to save the message client codec file[")
						.append(messageServerCodecFile.getAbsolutePath())
						.append("]::errmsg=").toString();
				
				throw new BuildSystemException(errorMessage);
			}
		}
	}
	
	public static  ArrayList<String> buildDriveLetterList() {
		ArrayList<String> driveLetterList = new ArrayList<String>(); 
		File[] drives = File.listRoots();
		if (drives != null && drives.length > 0) {
		    for (File aDrive : drives) {
		    	String driveLetter = aDrive.getAbsolutePath().substring(0, 2);
		    	driveLetterList.add(driveLetter);
		    }
		}
		return driveLetterList;
	}
}
