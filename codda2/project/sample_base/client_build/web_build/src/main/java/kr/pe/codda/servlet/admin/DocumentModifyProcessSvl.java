/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.servlet.admin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.DocumentModifyReq.DocumentModifyReq;
import kr.pe.codda.impl.message.DocumentModifyRes.DocumentModifyRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.UploadImageReq.UploadImageReq;
import kr.pe.codda.impl.message.UploadImageRes.UploadImageRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticUtil;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;
import kr.pe.codda.weblib.summernote.BoardContentsWhiteParserMananger;
import kr.pe.codda.weblib.summernote.BoardImageFileInformation;
import kr.pe.codda.weblib.summernote.ImageFileURLGetterIF;

/**
 * 문서 수정 처리
 * @author Won Jonghoon
 *
 */
public class DocumentModifyProcessSvl extends AbstractAdminLoginServlet implements ImageFileURLGetterIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4361347286097820440L;
	
	private final String installedPathString;
	private final String mainProjectName;
	
	/**
	 * 생성자
	 */
	public DocumentModifyProcessSvl() {
		super();
		
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager
				.getInstance().getRunningProjectConfiguration();
		mainProjectName = runningProjectConfiguration.getMainProjectName();
		installedPathString = runningProjectConfiguration
				.getInstalledPathString();
	}
	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramDocumentNo = req.getParameter("documentNo");
		String paramFileName = req.getParameter("fileName");
		String paramSubject = req.getParameter("subject");
		String paramContents = req.getParameter("contents");
		/**************** 파라미터 종료 *******************/
		
		if (null == paramDocumentNo) {
			String errorMessage = "the web paramter 'documentNo' 를 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		final long documentNo;
		try {
			documentNo = Long.parseLong(paramDocumentNo);
		} catch(IllegalArgumentException e) {
			String errorMessage = "the web paramter 'documentNo' is not a long type value";
			String debugMessage = new StringBuilder()
					.append(errorMessage)
					.append(", documentNo=[")
					.append(paramDocumentNo)
					.append("]").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		try {
			ValueChecker.checkValidFileName(paramFileName);
			ValueChecker.checkValidSubject(paramSubject);
			ValueChecker.checkValidContents(paramContents);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		
		String newContents = BoardContentsWhiteParserMananger.getInstance().checkWhiteValue(this, paramContents);
		
		AccessedUserInformation accessedUserInformation = getAccessedUserInformationFromSession(req);
		
		DocumentModifyReq documentModifyReq = new DocumentModifyReq();
		documentModifyReq.setRequestedUserID(accessedUserInformation.getUserID());
		documentModifyReq.setIp(req.getRemoteAddr());
		documentModifyReq.setDocumentNo(documentNo);
		documentModifyReq.setFileName(paramFileName);
		documentModifyReq.setSubject(paramSubject);
		documentModifyReq.setContents(newContents);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), documentModifyReq);

		if (!(outputMessage instanceof DocumentModifyRes)) {
			if ((outputMessage instanceof MessageResultRes)) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
				String errorMessage = messageResultRes.getResultMessage();
				String debugMessage = null;

				throw new WebClientException(errorMessage, debugMessage);
			} else {
				String errorMessage = "문서 수정이 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(documentModifyReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString()).append("] 도착").toString();

				throw new WebClientException(errorMessage, debugMessage);
			}
		}

		DocumentModifyRes documentModifyRes = (DocumentModifyRes) outputMessage;

		req.setAttribute("documentModifyRes", documentModifyRes);

		final String goPage = "/sitemenu/doc/DocumentModifyProcess.jsp";
		printJspPage(req, res, goPage);
		return;
	}

	
	@Override
	public String getImageFileURL(BoardImageFileInformation boardImageFileInformation) throws WhiteParserException {
		
		try {
			byte[] imageFileContents = boardImageFileInformation.getBoardImageFileContents();			
			
			UploadImageReq uploadImageReq = new UploadImageReq();
			// uploadImageReq.setRequestedUserID(accessedUserformation.getUserID());
			
			uploadImageReq.setImageFileName(boardImageFileInformation.getBoardImageFileName());
			uploadImageReq.setFileSize(imageFileContents.length);		
			
			AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
					.getInstance().getMainProjectConnectionPool();
			
			AbstractMessage outputMessage = mainProjectConnectionPool
					.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), uploadImageReq);

			if (!(outputMessage instanceof UploadImageRes)) {
				if (outputMessage instanceof MessageResultRes) {
					MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
					String errorMessage = "문서에 삽입된 이미지를 업로드 하는데 실패하였습니다";
					String debugMessage = messageResultRes.toString();
					throw new WebClientException(errorMessage, debugMessage);
				} else {
					String errorMessage = "문서에 삽입된 이미지를 업로드 하는데 실패하였습니다";
					String debugMessage = new StringBuilder("입력 메시지[").append(uploadImageReq.getMessageID())
							.append("]에 대한 비 정상 출력 메시지[").append(outputMessage.toString()).append("] 도착").toString();

					log.severe(debugMessage);

					throw new WebClientException(errorMessage, debugMessage);
				}
			}

			UploadImageRes uploadImageRes = (UploadImageRes) outputMessage;
			
			String uploadImageFilePathString = WebCommonStaticUtil.buildUploadImageFilePathString(
					installedPathString, mainProjectName, uploadImageRes.getYyyyMMdd(),
					uploadImageRes.getDaySequence());
			File newUploadImageFile = new File(uploadImageFilePathString);
			
			FileOutputStream fos = new FileOutputStream(newUploadImageFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			try {				
				bos.write(imageFileContents);				
			} finally {
				if (null != bos) {
					try {
						bos.close();
					} catch(Exception e) {
						
					}
				}
				
				if (null != fos) {
					try {
						fos.close();
					} catch(Exception e) {
						
					}
				}
			}			
			
			String newImgTagSrcAttributeValue = new StringBuilder()
					.append("/servlet/DownloadImage?yyyyMMdd=")
					.append(uploadImageRes.getYyyyMMdd())
					.append("&daySequence=")
					.append(uploadImageRes.getDaySequence()).toString();
			
			return newImgTagSrcAttributeValue;
			
		} catch(Exception e) {
			String errorMessage = "fail to replace old image tag having base64 to new image tag having image file url";
			log.log(Level.WARNING, errorMessage, e);
			throw new WhiteParserException(errorMessage);
			
		}
	}

}
