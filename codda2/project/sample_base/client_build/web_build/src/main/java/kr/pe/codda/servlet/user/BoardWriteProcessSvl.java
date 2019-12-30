package kr.pe.codda.servlet.user;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.UploadImageReq.UploadImageReq;
import kr.pe.codda.impl.message.UploadImageRes.UploadImageRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.common.WebCommonStaticUtil;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.jdf.AbstractMultipartServlet;
import kr.pe.codda.weblib.summernote.BoardContentsWhiteParserMananger;
import kr.pe.codda.weblib.summernote.BoardImageFileInformation;
import kr.pe.codda.weblib.summernote.ImageFileURLGetterIF;

public class BoardWriteProcessSvl extends AbstractMultipartServlet implements ImageFileURLGetterIF {

	private static final long serialVersionUID = 6954552927880470265L;

	private String installedPathString = null;
	private String mainProjectName = null;
	
	public BoardWriteProcessSvl() {
		super();
		
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager
				.getInstance().getRunningProjectConfiguration();
		mainProjectName = runningProjectConfiguration.getMainProjectName();
		installedPathString = runningProjectConfiguration
				.getInstalledPathString();
	}

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		BoardWriteRes boardWriteRes = doWork(req, res);		

		req.setAttribute("boardWriteRes", boardWriteRes);

		final String goPage = "/jsp/community/BoardWriteProcess.jsp";
		printJspPage(req, res, goPage);
		return;
	}

	public BoardWriteRes doWork(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String paramSessionKeyBase64 = null;
		String paramIVBase64 = null;
		//String paramBoardID = null;
		String paramBoardPwdCipherBase64 = null;
		String paramSubject = null;
		String paramContents = null;
		List<BoardWriteReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		short boardID = -1;
		
		/**
		 * 참고) 공통단에서는 선택한 메뉴가 무엇인지를 정의합니다.
		 * 게시판 관련 메뉴들은 게시판 식별자라는 파라미터 값을 알아야 메뉴가 무엇인지 정할 수 있습니다.
		 * 멀티 파트 폼의 경우 입력 스트림 파싱하여 얻은 FileItem 목록을 통해서 파라미터 값을 얻어 올 수 있습니다. 
		 * 그런데 입력 스트림 파싱은 입력 스트림을 소진시키기때문에 파싱한후 그 결과를 전달 받아 사용해야 합니다. 
		 * 멤버 변수는 쓰레드 세이프 하지 않아 request 객체로 전달 받습니다. 
		 */
		@SuppressWarnings("unchecked")
		List<FileItem> fileItemList = (List<FileItem>)req.getAttribute(WebCommonStaticFinalVars.MULTIPART_PARSING_RESULT_ATTRIBUTE_OF_REQUEST);

		for (FileItem fileItem : fileItemList) {
			/*
			 * log.debug("fileItem={}, userId={}, ip={}", fileItem.toString(),
			 * getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
			 */

			if (fileItem.isFormField()) {
				String formFieldName = fileItem.getFieldName();
				String formFieldValue = fileItem.getString("UTF-8");

				// log.info("form field's name={} and value={}", formFieldName,
				// formFieldValue);

				/**************** 파라미터 시작 *******************/
				if (formFieldName
						.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)) {
					paramSessionKeyBase64 = formFieldValue;
				} else if (formFieldName
						.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)) {
					paramIVBase64 = formFieldValue;
				} else if (formFieldName.equals("boardID")) {
					try {
						boardID = ValueChecker.checkValidBoardID(formFieldValue);
					} catch(IllegalArgumentException e) {
						String errorMessage = e.getMessage();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}
					
					// paramBoardID = formFieldValue;
				} else if (formFieldName.equals("pwd")) {
					paramBoardPwdCipherBase64 = formFieldValue;
				} else if (formFieldName.equals("subject")) {	
					try {					
						ValueChecker.checkValidSubject(formFieldValue);						
					} catch(IllegalArgumentException e) {
						String errorMessage = e.getMessage();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}
					
					paramSubject = formFieldValue;
				} else if (formFieldName.equals("contents")) {
					try {
						ValueChecker.checkValidContents(formFieldValue);						
					} catch(IllegalArgumentException e) {
						String errorMessage = e.getMessage();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}
					
					paramContents = formFieldValue;
				} else {
					String errorMessage = new StringBuilder()
							.append("필요 없은 웹 파라미터 '")
							.append(formFieldName)
							.append("' 전달 받음").toString();
					log.warning(errorMessage);
				}
				/**************** 파라미터 종료 *******************/

			} else {
				String formFieldName = fileItem.getFieldName();
				String newAttachedFileName = fileItem.getName();
				String newAttachedFileContentType = fileItem.getContentType();
				long newAttachedFileSize = fileItem.getSize();

				if (!formFieldName.equals("newAttachedFile")) {
					String errorMessage = new StringBuilder()
							.append("'newAttachedFile' 로 정해진 첨부 파일의 웹 파라미터 이름[")
							.append(formFieldName).append("]이 잘못되었습니다")
							.toString();
					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (newAttachedFileList.size() == WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
					String errorMessage = new StringBuilder("첨부 파일은 최대 ")
							.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
							.append(" 까지만 허용됩니다").toString();

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				/*
				 * log.info("fileName={}, fileContentType={}, fileSize={}",
				 * newAttachedFileName, newAttachedFileContentType,
				 * newAtttachedFileSize);
				 */

				try {
					ValueChecker.checkValidFileName(newAttachedFileName);
				} catch(IllegalArgumentException e) {
					String errorMessage = "첨부 파일 이름에 금지된 글자가 포함되었습니다";

					String debugMessage = e.getMessage();
					throw new WebClientException(errorMessage,
							debugMessage);
				}

				String lowerCaseFileName = newAttachedFileName.toLowerCase();

				if (!lowerCaseFileName.endsWith(".jpg")
						&& !lowerCaseFileName.endsWith(".gif")
						&& !lowerCaseFileName.endsWith(".png")) {

					String errorMessage = new StringBuilder("첨부 파일[")
							.append(newAttachedFileName)
							.append("]의 확장자는 jpg, gif, png 만 올 수 있습니다.")
							.toString();

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (newAttachedFileSize == 0) {
					String errorMessage = new StringBuilder("첨부 파일[")
							.append(newAttachedFileName).append("]의 크기가 0 입니다")
							.toString();

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (null == newAttachedFileContentType) {
					String errorMessage = new StringBuilder(
							"알 수 없는 내용이 담긴 첨부 파일[").append(newAttachedFileName)
							.append("] 입니다").toString();

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (! newAttachedFileContentType.equals("image/jpeg")
						&& ! newAttachedFileContentType.equals("image/png")
						&& ! newAttachedFileContentType.equals("image/gif")) {
					String errorMessage = new StringBuilder("첨부 파일[")
							.append(newAttachedFileName).append("][")
							.append(newAttachedFileContentType)
							.append("]은 이미지 jpg, gif, png 만 올 수 있습니다")
							.toString();
					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				InputStream attachedFileInputStream = fileItem.getInputStream();
				try {
					String guessedContentType = URLConnection
							.guessContentTypeFromStream(attachedFileInputStream);
					if (null == guessedContentType) {
						String errorMessage = new StringBuilder("첨부 파일[")
								.append(newAttachedFileName)
								.append("] 데이터 내용으로 파일 종류를 파악하는데 실패하였습니다")
								.toString();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}

					if (! guessedContentType.equals(newAttachedFileContentType)) {
						String errorMessage = new StringBuilder("전달 받은 첨부 파일[")
								.append(newAttachedFileName).append("] 종류[")
								.append(newAttachedFileContentType)
								.append("]와 첨부 파일 데이터 내용으로 추정된 파일 종류[")
								.append(guessedContentType).append("]가 다릅니다")
								.toString();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}
				} finally {
					try {
						attachedFileInputStream.close();
					} catch (IOException e) {
					}
				}

				BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName(newAttachedFileName);
				newAttachedFile.setAttachedFileSize(newAttachedFileSize);
				newAttachedFileList.add(newAttachedFile);
			}
		}

		if (null == paramSessionKeyBase64) {
			String errorMessage = "세션키를 넣어 주세요";
			String debugMessage = new StringBuilder("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
					.append("' is null").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}

		if (null == paramIVBase64) {
			String errorMessage = "iv 값을 넣어 주세요";
			String debugMessage = new StringBuilder("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
					.append("' is null").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}

		// log.info("parmSessionKeyBase64={}", parmSessionKeyBase64);
		// log.info("parmIVBase64={}", parmIVBase64);*/

		ServerSessionkeyIF webServerSessionkey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager
					.getInstance();
			webServerSessionkey = serverSessionkeyManager
					.getMainProjectServerSessionkey();
		} catch (SymmetricException e) {
			log.log(Level.WARNING, "SymmetricException", e);

			String errorMessage = "fail to initialize ServerSessionkeyManger instance";
			String debugMessage = new StringBuilder()
					.append("fail to initialize ServerSessionkeyManger instance, errmsg=")
					.append(e.getMessage()).toString();
			throw new WebClientException(errorMessage, debugMessage);
		}

		
		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = CommonStaticUtil.Base64Decoder.decode(paramSessionKeyBase64);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
					.append("' is not a base64 string").toString();

			String debugMessage = new StringBuilder()
					.append("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
					.append("'[").append(paramSessionKeyBase64)
					.append("] is not a base64 string, errmsg=")
					.append(e.getMessage()).toString();
			
			log.warning(debugMessage);

			throw new WebClientException(errorMessage, debugMessage);
		}

		byte[] ivBytes = null;
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(paramIVBase64);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
					.append("' is not a base64 string").toString();

			String debugMessage = new StringBuilder()
					.append("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
					.append("'[").append(paramIVBase64)
					.append("] is not a base64 string, errmsg=")
					.append(e.getMessage()).toString();
			
			log.warning(debugMessage);

			throw new WebClientException(errorMessage, debugMessage);
		}

		// log.info("sessionkeyBytes.length={}", sessionkeyBytes.length);
		ServerSymmetricKeyIF webServerSymmetricKey = null;
		try {
			webServerSymmetricKey = webServerSessionkey
					.createNewInstanceOfServerSymmetricKey(true, sessionkeyBytes,
							ivBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.log(Level.WARNING, errorMessage, e);

			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();

			throw new WebClientException(errorMessage, debugMessage);
		} catch (SymmetricException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.log(Level.WARNING, errorMessage, e);

			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}
		

		req.setAttribute(
				WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());
		req.setAttribute(
				WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SYMMETRIC_KEY_FROM_SESSIONKEY,
				webServerSymmetricKey);
		
		
		
		String pwdHashBase64 = null;
		if (null == paramBoardPwdCipherBase64) {
			pwdHashBase64 = "";
		} else {
			
			
			byte[] boardPasswordBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder
					.decode(paramBoardPwdCipherBase64));
			
			try {
				ValueChecker.checkValidBoardPwd(boardPasswordBytes);
			} catch(IllegalArgumentException e) {
				Arrays.fill(boardPasswordBytes, (byte)0);
				
				String errorMessage = e.getMessage();
				String debugMessage = null;
				throw new WebClientException(errorMessage, debugMessage);
			}			
			
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance(WebCommonStaticFinalVars.BOARD_HASH_ALGORITHM);
			} catch (NoSuchAlgorithmException e) {
				String errorMessage = "fail to get a MessageDigest class instance";
				log.log(Level.WARNING, errorMessage, e);			
				
				String debugMessage = new StringBuilder("the 'algorithm'[")
						.append(WebCommonStaticFinalVars.BOARD_HASH_ALGORITHM)
						.append("], errmsg=")
						.append(e.getMessage()).toString();
				throw new WebClientException(errorMessage, debugMessage);
			}
			
			md.update(boardPasswordBytes);
			pwdHashBase64 = CommonStaticUtil.Base64Encoder.encodeToString(md.digest());
			
			Arrays.fill(boardPasswordBytes, (byte)0);
		}
		
		
		// FIXME!
		/*
		int startInxOfImgTag = paramContents.indexOf("<img");
		int endInxOfMimeType = paramContents.indexOf(";base64,", startInxOfImgTag);
		*/
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
		
		
		String newContents = BoardContentsWhiteParserMananger.getInstance().checkXssAttack(this, paramContents);
		

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(accessedUserformation.getUserID());
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setPwdHashBase64(pwdHashBase64);
		boardWriteReq.setSubject(paramSubject);
		boardWriteReq.setContents(newContents);
		boardWriteReq.setIp(req.getRemoteAddr());
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileList.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileList);

		// log.info("inObj={}", boardWriteReq.toString());
		// System.out.println(boardWriteReq.toString());

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardWriteReq);

		if ((outputMessage instanceof MessageResultRes)) {
			MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
			String errorMessage = messageResultRes.getResultMessage();
			String debugMessage = null;

			throw new WebClientException(errorMessage, debugMessage);
		} else if (!(outputMessage instanceof BoardWriteRes)) {
			String errorMessage = "게시판 쓰기가 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardWriteReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString()).append("] 도착").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}

		BoardWriteRes boardWriteRes = (BoardWriteRes) outputMessage;

		// log.info("본문글[{}] 등록이 완료되었습니다", boardWriteRes.toString());

		if (! fileItemList.isEmpty()) {
			int indexOfNewAttachedFileList = 0;
			short newAttachedFileSeq = 0;
			
			for (FileItem fileItem : fileItemList) {
				if (! fileItem.isFormField()) {
					String newAttachedFilePathString = WebCommonStaticUtil
							.buildAttachedFilePathString(installedPathString,
									mainProjectName, boardID,
									boardWriteRes.getBoardNo(), newAttachedFileSeq);

					File newAttachedFile = new File(newAttachedFilePathString);
					try {
						fileItem.write(newAttachedFile);
					} catch (Exception e) {
						String errorMessage = new StringBuilder().append("본문 글[")
								.append(boardWriteRes.toString())
								.append("]의 임시로 저장된 신규 첨부 파일[index=")
								.append(indexOfNewAttachedFileList)
								.append(", fileName=").append(fileItem.getName())
								.append(", size=").append(fileItem.getSize())
								.append("]의 이름을 게시판 첨부 파일 이름 규칙에 맞도록 개명[")
								.append(newAttachedFilePathString)
								.append("] 하는데 실패하였습니다").toString();

						log.log(Level.WARNING, errorMessage, e);
					}

					newAttachedFileSeq++;
					indexOfNewAttachedFileList++;
				}
			}
		}

		return boardWriteRes;
	}
	
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
					String errorMessage = "게시글에 삽입된 이미지를 업로드 하는데 실패하였습니다";
					String debugMessage = messageResultRes.toString();
					throw new WebClientException(errorMessage, debugMessage);
				} else {
					String errorMessage = "게시글에 삽입된 이미지를 업로드 하는데 실패하였습니다";
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
