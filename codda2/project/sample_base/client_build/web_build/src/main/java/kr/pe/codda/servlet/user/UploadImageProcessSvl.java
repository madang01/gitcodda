package kr.pe.codda.servlet.user;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.google.gson.Gson;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.UploadImageReq.UploadImageReq;
import kr.pe.codda.impl.message.UploadImageRes.UploadImageRes;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.common.WebCommonStaticUtil;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractMultipartServlet;

public class UploadImageProcessSvl extends AbstractMultipartServlet {

	private final String installedPathString;
	private final String mainProjectName;

	public UploadImageProcessSvl() {
		super();

		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		mainProjectName = runningProjectConfiguration.getMainProjectName();
		installedPathString = runningProjectConfiguration.getInstalledPathString();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6220740371760329658L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {

		UploadImageRes uploadImageRes = doWork(req, res);
		
		String uploadImageResJsonString = new Gson().toJson(uploadImageRes);
		
		res.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
		res.setCharacterEncoding("UTF-8"); // You want world domination, huh?
		res.getWriter().write(uploadImageResJsonString);

		// req.setAttribute("uploadImageRes", uploadImageRes);
		// printJspPage(req, res, "/jsp/community/UploadImageProcess.jsp");
		
		
		return;
	}

	public UploadImageRes doWork(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
		

		UploadImageReq uploadImageReq = new UploadImageReq();

		@SuppressWarnings("unchecked")
		List<FileItem> fileItemList = (List<FileItem>) req.getAttribute(WebCommonStaticFinalVars.MULTIPART_PARSING_RESULT_ATTRIBUTE_OF_REQUEST);

		if (fileItemList.isEmpty()) {
			String errorMessage = "업로드 이미지를 넣어주세요";

			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		if (fileItemList.size() != 1) {
			String errorMessage = "업로드 이미지는 한번에 1개만 처리가능합니다";

			String debugMessage = new StringBuilder(errorMessage).append(", 업로드 이미지 갯수=[").append(fileItemList.size())
					.append("]").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}

		for (FileItem fileItem : fileItemList) {
			if (!fileItem.isFormField()) {
				String formFieldName = fileItem.getFieldName();
				String uploadImageFileName = fileItem.getName();
				String uploadImageFileContentType = fileItem.getContentType();
				long uploadImageFileSize = fileItem.getSize();
				
				if (!formFieldName.equals("uplodImageFile")) {
					String errorMessage = new StringBuilder().append("'uplodImageFile' 로 정해진 첨부 파일의 웹 파라미터 이름[")
							.append(formFieldName).append("]이 잘못되었습니다").toString();
					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}
				

				try {
					ValueChecker.checkValidFileName(uploadImageFileName);
				} catch (IllegalArgumentException e) {
					String errorMessage = "이미지 파일 이름에 금지된 글자가 포함되었습니다";

					String debugMessage = e.getMessage();
					throw new WebClientException(errorMessage, debugMessage);
				}

				String lowerCaseFileName = uploadImageFileName.toLowerCase();

				if (!lowerCaseFileName.endsWith(".jpg") && !lowerCaseFileName.endsWith(".gif")
						&& !lowerCaseFileName.endsWith(".png")) {

					String errorMessage = new StringBuilder("이미지 파일[").append(uploadImageFileName)
							.append("]의 확장자는 jpg, gif, png 만 올 수 있습니다.").toString();

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (null == uploadImageFileContentType) {
					String errorMessage = new StringBuilder("알 수 없는 내용이 담긴 첨부 파일[").append(uploadImageFileName)
							.append("] 입니다").toString();

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (!uploadImageFileContentType.equals("image/jpeg") && !uploadImageFileContentType.equals("image/png")
						&& !uploadImageFileContentType.equals("image/gif")) {
					String errorMessage = new StringBuilder("업로드 이미지 파일[").append(uploadImageFileName).append("][")
							.append(uploadImageFileContentType).append("]은 이미지 jpg, gif, png 만 올 수 있습니다").toString();
					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				InputStream attachedFileInputStream = fileItem.getInputStream();
				try {
					String guessedContentType = URLConnection.guessContentTypeFromStream(attachedFileInputStream);
					if (null == guessedContentType) {
						String errorMessage = new StringBuilder("업로드 이미지 파일[").append(uploadImageFileName)
								.append("] 데이터 내용으로 파일 종류를 파악하는데 실패하였습니다").toString();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}

					if (!guessedContentType.equals(uploadImageFileContentType)) {
						String errorMessage = new StringBuilder("전달 받은 업로드 이미지 파일[").append(uploadImageFileName)
								.append("] 종류[").append(uploadImageFileContentType)
								.append("]와 첨부 파일 데이터 내용으로 추정된 파일 종류[").append(guessedContentType).append("]가 다릅니다")
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

				uploadImageReq.setImageFileName(uploadImageFileName);
				uploadImageReq.setFileSize(uploadImageFileSize);
			}
		}

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();

		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), uploadImageReq);

		if (!(outputMessage instanceof UploadImageRes)) {
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
				String errorMessage = "게시판 상세 조회가 실패하였습니다";
				String debugMessage = messageResultRes.toString();
				throw new WebClientException(errorMessage, debugMessage);
			} else {
				String errorMessage = "게시판 상세 조회가 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[").append(uploadImageReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[").append(outputMessage.toString()).append("] 도착").toString();

				log.severe(debugMessage);

				throw new WebClientException(errorMessage, debugMessage);
			}
		}

		UploadImageRes uploadImageRes = (UploadImageRes) outputMessage;

		for (FileItem fileItem : fileItemList) {
			if (!fileItem.isFormField()) {
				String uploadImageFilePathString = WebCommonStaticUtil.buildUploadImageFilePathString(
						installedPathString, mainProjectName, uploadImageRes.getYyyyMMdd(),
						uploadImageRes.getDaySequence());
				File newUploadImageFile = new File(uploadImageFilePathString);

				try {
					fileItem.write(newUploadImageFile);
				} catch (Exception e) {
					String errorMessage = new StringBuilder().append("입출력 에러가 발생하여 업로드 이미지 파일[")
							.append(uploadImageReq.getImageFileName()).append("][").append(uploadImageFilePathString)
							.append("] 를 저장하는데 실패하였습니다").toString();

					log.log(Level.WARNING, errorMessage, e);
				}
			}
		}
		
		return uploadImageRes;
	}

}
