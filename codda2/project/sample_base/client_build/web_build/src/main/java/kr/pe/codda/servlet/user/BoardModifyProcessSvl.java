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
import org.apache.commons.io.FileUtils;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.BoardModifyRes.BoardModifyRes;
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

/**
 * 
 * WARNING! 수정전 조회를 통해 웹 파라미터(nextAttachedFileSeq)는 게시글 수정할때 자신이 첨부한 이미지 파일을 글속에
 * 넣을 수 있도록 한 게시글 수정이라는 일련의 과정에 필요한 변수이기때문에 게시글 수정 처리 결과 메시지(=BoardModifyRes)에서
 * 받는거로 변경하지 말것.
 * 
 * 이미지를 보여주기 위해서는 (1) 게시판 식별자, (2) 게시판 번호, (3) 첨부 파일 시퀀스 라는 3개 정보가 필요하다. 하여 이미지를
 * 본문글에 넣고자 할려면 '첨부 파일 시퀀스' 를 알아야 한다.
 * 
 * '다음 첨부 파일 시퀀스' 는 0부터 시작되며 다음 첨부 파일의 시퀀스 값을 지칭한다. 1개 첨부 파일 추가할때 마다 1씩 증가하며 게시판
 * 조회를 통해서 얻어 올 수 있다.
 * 
 * 이를 정리하면 아래와 같은 공식이 성립한다. '첨부 파일 시퀀스' = 변경전 '다음 첨부 파일 시퀀스' + 신규 첨부 파일 목록의 인덱스
 * 변경후 '다음 첨부 파일 시퀀스' = 변경전 '다음 첨부 파일 시퀀스' + 신규 첨부 파일 목록 갯수
 * 
 * 게시글 신규 작성할때에는 '다음 첨부 파일 시퀀스' 는 0 이지만 수정할때에는 조회를 통해 이를 얻어와야 한다. 이 값은 웹
 * 파라미터(nextAttachedFileSeq) 로 받아 변수 oldNextAttachedFileSeq 에 저장된다.
 * 
 * 첨부 파일명 형식은 '게시판식별자'+'게신판번호'+'첨부파일 시퀀스' 가 되며 첨부 파일 테이블의 키는
 * '게시판식별자'+'게신판번호'+'첨부파일 시퀀스' 이다. 첨부 파일 테이블 레코드와 첨부 파일명이 '게시판식별자'+'게신판번호'+'첨부파일
 * 시퀀스' 로 1:1로 일치 됨을 보장해야 하기위해서 게시글 수정 처리시 게시글 수정시 조회로 부터 끌고온 '다음 첨부 파일 시퀀스' 와
 * DB 의 '다음 첨부 파일 시퀀스' 일치 여부를 검사한다.
 * 
 */
public class BoardModifyProcessSvl extends AbstractMultipartServlet implements ImageFileURLGetterIF {

	private static final long serialVersionUID = 3391482674902127151L;

	private String installedPathString = null;
	private String mainProjectName = null;

	public BoardModifyProcessSvl() {
		super();

		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		mainProjectName = runningProjectConfiguration.getMainProjectName();
		installedPathString = runningProjectConfiguration.getInstalledPathString();
	}

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		BoardModifyRes boardModifyRes = doWork(req, res);

		req.setAttribute("boardModifyRes", boardModifyRes);

		final String goPage = "/sitemenu/community/BoardModifyProcess.jsp";
		printJspPage(req, res, goPage);
		return;
	}

	public BoardModifyRes doWork(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String paramSessionKeyBase64 = null;
		String paramIVBase64 = null;
		String paramBoardID = null;
		String paramBoardPwdCipherBase64 = null;
		String paramBoardNo = null;
		String paramSubject = null;
		String paramContents = null;
		String paramNextAttachedFileSeq = null;
		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();

		short boardID = -1;
		long boardNo = 0L;
		short nextAttachedFileSeq = 0;

		/**
		 * 참고) 공통단에서는 선택한 메뉴가 무엇인지를 정의합니다. 게시판 관련 메뉴들은 게시판 식별자라는 파라미터 값을 알아야 메뉴가 무엇인지 정할
		 * 수 있습니다. 멀티 파트 폼의 경우 입력 스트림 파싱하여 얻은 FileItem 목록을 통해서 파라미터 값을 얻어 올 수 있습니다. 그런데
		 * 입력 스트림 파싱은 입력 스트림을 소진시키기때문에 파싱한후 그 결과를 전달 받아 사용해야 합니다. 멤버 변수는 쓰레드 세이프 하지 않아
		 * request 객체로 전달 받습니다.
		 */
		@SuppressWarnings("unchecked")
		List<FileItem> fileItemList = (List<FileItem>) req
				.getAttribute(WebCommonStaticFinalVars.MULTIPART_PARSING_RESULT_ATTRIBUTE_OF_REQUEST);

		for (FileItem fileItem : fileItemList) {

			/*
			 * log.debug("fileItem={}, userId={}, ip={}", fileItem.toString(),
			 * getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
			 */

			if (fileItem.isFormField()) {
				String formFieldName = fileItem.getFieldName();
				String formFieldValue = fileItem.getString("UTF-8");

				/**************** 파라미터 시작 *******************/
				if (formFieldName.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)) {
					paramSessionKeyBase64 = formFieldValue;
				} else if (formFieldName.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)) {
					paramIVBase64 = formFieldValue;
				} else if (formFieldName.equals("boardID")) {
					paramBoardID = formFieldValue;
				} else if (formFieldName.equals("boardNo")) {
					paramBoardNo = formFieldValue;
				} else if (formFieldName.equals("pwd")) {
					paramBoardPwdCipherBase64 = formFieldValue;
				} else if (formFieldName.equals("subject")) {
					paramSubject = formFieldValue;
				} else if (formFieldName.equals("contents")) {
					paramContents = formFieldValue;
				} else if (formFieldName.equals("nextAttachedFileSeq")) {

					paramNextAttachedFileSeq = formFieldValue;
				} else if (formFieldName.equals("oldAttachedFileSeq")) {
					String paramOldAttachedFileSeq = formFieldValue;

					if ((newAttachedFileList.size()
							+ oldAttachedFileList.size()) == WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
						String errorMessage = new StringBuilder("총 첨부 파일 갯수[보존 원하는 구 첨부 파일 갯수=")
								.append(oldAttachedFileList.size()).append(", 신규첨부파일갯수=")
								.append(newAttachedFileList.size()).append("]가 최대 갯수[")
								.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
								.append("]를 초과하여 더 이상 보존을 원하는 구 첨부 파일[").append(paramOldAttachedFileSeq)
								.append("을 추가할 수 없습니다").toString();

						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}

					BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();
					try {
						oldAttachedFile.setAttachedFileSeq(Short.parseShort(paramOldAttachedFileSeq));
					} catch (NumberFormatException e) {
						String errorMessage = new StringBuilder(
								"자바 short 타입 변수인 파라미터  '첨부 파일 순번'(oldAttachedFileSeq)의 값[")
										.append(paramOldAttachedFileSeq).append("]이 잘못되었습니다").toString();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}

					if (oldAttachedFile.getAttachedFileSeq() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
						String errorMessage = new StringBuilder("파라미터  '첨부 파일 순번'(oldAttachedFileSeq)의 값[")
								.append(paramOldAttachedFileSeq).append("]이 unsigned byte 범위 최대값(=255)를 넘었습니다")
								.toString();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}

					if (oldAttachedFileList.contains(oldAttachedFile)) {
						String errorMessage = new StringBuilder("파라미터  '첨부 파일 순번'(oldAttachedFileSeq)의 값[")
								.append(paramOldAttachedFileSeq).append("]이 중복되었습니다").toString();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}

					oldAttachedFileList.add(oldAttachedFile);
				} else {
					String errorMessage = new StringBuilder().append("필요 없은 웹 파라미터 '").append(formFieldName)
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
					String errorMessage = new StringBuilder().append("'newAttachedFile' 로 정해진 신규 첨부 파일의 웹 파라미터 이름[")
							.append(formFieldName).append("]이 잘못되었습니다").toString();
					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if ((newAttachedFileList.size()
						+ oldAttachedFileList.size()) == WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
					String errorMessage = new StringBuilder("총 첨부 파일 갯수[보존 원하는 구 첨부 파일 갯수=")
							.append(oldAttachedFileList.size()).append(", 신규첨부파일갯수=").append(newAttachedFileList.size())
							.append("]가 최대 갯수[").append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
							.append("]를 초과하여 더 이상 신규 첨부 파일[").append(newAttachedFileName).append("] 을 추가할 수 없습니다")
							.toString();

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				/*
				 * log.info("fileName={}, fileContentType={}, fileSize={}", newAttachedFileName,
				 * newAttachedFileContentType, newAtttachedFileSize);
				 */

				for (char ch : newAttachedFileName.toCharArray()) {
					if (Character.isWhitespace(ch)) {
						String errorMessage = new StringBuilder("첨부 파일명[").append(newAttachedFileName)
								.append("]에 공백 문자가 존재합니다").toString();

						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					} else {
						for (char forbiddenChar : WebCommonStaticFinalVars.FILENAME_FORBIDDEN_CHARS) {
							if (ch == forbiddenChar) {
								String errorMessage = new StringBuilder("첨부 파일명[").append(newAttachedFileName)
										.append("]에 금지된 문자[").append(forbiddenChar).append("]가 존재합니다").toString();

								String debugMessage = null;
								throw new WebClientException(errorMessage, debugMessage);
							}
						}
					}
				}

				String lowerCaseFileName = newAttachedFileName.toLowerCase();

				if (!lowerCaseFileName.endsWith(".jpg") && !lowerCaseFileName.endsWith(".gif")
						&& !lowerCaseFileName.endsWith(".png")) {

					String errorMessage = new StringBuilder("첨부 파일[").append(newAttachedFileName)
							.append("]의 확장자는 jpg, gif, png 만 올 수 있습니다.").toString();

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (newAttachedFileSize == 0) {
					String errorMessage = "첨부 파일 크기가 0입니다";

					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (null == newAttachedFileContentType) {
					String errorMessage = new StringBuilder("알 수 없는 내용이 담긴 첨부 파일[").append(newAttachedFileName)
							.append("] 입니다").toString();
					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				if (!newAttachedFileContentType.equals("image/jpeg") && !newAttachedFileContentType.equals("image/png")
						&& !newAttachedFileContentType.equals("image/gif")) {
					String errorMessage = new StringBuilder("첨부 파일[").append(newAttachedFileName).append("][")
							.append(newAttachedFileContentType).append("]은 이미지 jpg, gif, png 만 올 수 있습니다").toString();
					String debugMessage = null;
					throw new WebClientException(errorMessage, debugMessage);
				}

				InputStream attachedFileInputStream = fileItem.getInputStream();
				try {
					String guessedContentType = URLConnection.guessContentTypeFromStream(attachedFileInputStream);
					if (null == guessedContentType) {
						String errorMessage = new StringBuilder("첨부 파일[").append(newAttachedFileName)
								.append("] 데이터 내용으로 파일 종류를 파악하는데 실패하였습니다").toString();
						String debugMessage = null;
						throw new WebClientException(errorMessage, debugMessage);
					}

					if (!guessedContentType.equals(newAttachedFileContentType)) {
						String errorMessage = new StringBuilder("전달 받은 첨부 파일[").append(newAttachedFileName)
								.append("] 종류[").append(newAttachedFileContentType)
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

				BoardModifyReq.NewAttachedFile newAttachedFile = new BoardModifyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName(newAttachedFileName);
				newAttachedFile.setAttachedFileSize(newAttachedFileSize);
				newAttachedFileList.add(newAttachedFile);
			}
		}
		
		// FIXME!
		try {
			boardID = ValueChecker.checkValidBoardID(paramBoardID);
		
			boardNo = ValueChecker.checkValidBoardNo(paramBoardNo);
			
			/**
			 * <pre>
			 * 웹 브라우저에서 동적으로 화면을 구성하는데 이때 주제 없이 본문만 입력하는 경우에 웹 파라미터 'subject'(=주제)를 생략한다.
			 * 하여 웹 파라미터 'subject'(=주제) 생략시 주제 없이 본문만 입력하는 경우 이므로 주제는 빈 문자열로 지정한다.
			 * 서버단에서는 주제에 관해서 클라이언트에 의존하지 않고 게시판 정보 테이블로 부터 필수 항목 여부를 따져서 필수인 경우 무조건 유효성 검사를 수행한다. 
			 * </pre>    
			 */
			if (null == paramSubject) {
				paramSubject = "";
			} else {
				ValueChecker.checkValidSubject(paramSubject);
			}
			
			ValueChecker.checkValidContents(paramContents);
			
			nextAttachedFileSeq = ValueChecker.checkValidNextAttachedFileSeq(paramNextAttachedFileSeq);			
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		
		if (null == paramSessionKeyBase64) {
			String errorMessage = "세션키를 넣어 주세요";
			String debugMessage = new StringBuilder("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY).append("' is null").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}

		if (null == paramIVBase64) {
			String errorMessage = "iv 값을 넣어 주세요";
			String debugMessage = new StringBuilder("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV).append("' is null")
					.toString();

			throw new WebClientException(errorMessage, debugMessage);
		}


		// log.info("sessionkeyBytes=[{}]",
		// HexUtil.getHexStringFromByteArray(sessionkeyBytes));
		// log.info("ivBytes=[{}]", HexUtil.getHexStringFromByteArray(ivBytes));

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
				

		String boardPwdHashBase64 = null;
		if (null == paramBoardPwdCipherBase64) {
			boardPwdHashBase64 = "";
		} else {

			byte[] boardPasswordBytes = webServerSymmetricKey
					.decrypt(CommonStaticUtil.Base64Decoder.decode(paramBoardPwdCipherBase64));

			try {
				ValueChecker.checkValidBoardPwd(boardPasswordBytes);
			} catch (IllegalArgumentException e) {
				Arrays.fill(boardPasswordBytes, (byte) 0);

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
						.append(WebCommonStaticFinalVars.BOARD_HASH_ALGORITHM).append("], errmsg=")
						.append(e.getMessage()).toString();
				throw new WebClientException(errorMessage, debugMessage);
			}

			md.update(boardPasswordBytes);
			boardPwdHashBase64 = CommonStaticUtil.Base64Encoder.encodeToString(md.digest());

			Arrays.fill(boardPasswordBytes, (byte) 0);
		}

		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);		

		List<BoardImageFileInformation> boardImageFileInformationList = new ArrayList<BoardImageFileInformation>();
		String newContents = BoardContentsWhiteParserMananger.getInstance().checkWhiteValue(this, boardImageFileInformationList, paramContents);

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(accessedUserformation.getUserID());
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(boardNo);
		boardModifyReq.setPwdHashBase64(boardPwdHashBase64);
		boardModifyReq.setSubject(paramSubject);
		boardModifyReq.setContents(newContents);
		boardModifyReq.setIp(req.getRemoteAddr());
		boardModifyReq.setNextAttachedFileSeq(nextAttachedFileSeq);
		boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());
		boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
		boardModifyReq.setNewAttachedFileList(newAttachedFileList);

		// log.debug("boardModifyReq={}", boardModifyReq.toString());

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardModifyReq);

		if (!(outputMessage instanceof BoardModifyRes)) {
			if (!(outputMessage instanceof MessageResultRes)) {
				String errorMessage = "게시판 수정이 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[").append(boardModifyReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[").append(outputMessage.toString()).append("] 도착").toString();

				throw new WebClientException(errorMessage, debugMessage);
			}

			MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
			String errorMessage = messageResultRes.getResultMessage();
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}

		BoardModifyRes boardModifyRes = (BoardModifyRes) outputMessage;

		
		moveAllDeletedFileToTempDirectory(boardModifyRes);
		
		saveAllBoardImageFile(boardModifyRes, boardImageFileInformationList);

		saveAllAttachedFile(boardModifyRes, nextAttachedFileSeq, fileItemList);
		
		// log.info("게시글[{}] 수정이 완료되었습니다", boardModifyRes.toString());
		
		

		return boardModifyRes;
	}
	
	private void moveAllDeletedFileToTempDirectory(BoardModifyRes boardModifyRes) {
		List<BoardModifyRes.DeletedAttachedFile> deletedAttachedFileSeqList = boardModifyRes
				.getDeletedAttachedFileList();
		for (BoardModifyRes.DeletedAttachedFile deletedAttachedFile : deletedAttachedFileSeqList) {
			short deletedAttachedFileSequence = deletedAttachedFile.getAttachedFileSeq();

			String shortFileNameOfDelectedAttachedFile = WebCommonStaticUtil.buildShortFileNameOfAttachedFile(boardModifyRes.getBoardID(),
					boardModifyRes.getBoardNo(), deletedAttachedFileSequence);

			String delectedAttachedFilePathString = WebCommonStaticUtil.buildAttachedFilePathString(installedPathString,
					mainProjectName, boardModifyRes.getBoardID(), boardModifyRes.getBoardNo(), deletedAttachedFileSequence);

			File delectedAttachedFile = new File(delectedAttachedFilePathString);

			String destFilePathString = new StringBuilder()
					.append(WebRootBuildSystemPathSupporter.getUserWebTempPathString(installedPathString,
							mainProjectName))
					.append(File.separator).append(shortFileNameOfDelectedAttachedFile).toString();

			File destFile = new File(destFilePathString);

			if (!delectedAttachedFile.exists()) {
				log.warning("게시글의 삭제된 첨부 파일[" + delectedAttachedFilePathString + "] 이 존재하지 않습니다");
				continue;
			}

			if (destFile.exists()) {
				boolean result = destFile.delete();

				if (result) {
					String errrorMessage = new StringBuilder().append("게시글의 삭제된 첨부 파일[")
							.append(delectedAttachedFilePathString).append("] 이 이동할 임시 경로[").append(destFilePathString)
							.append("]에 존재하여 임시 경로에 있는 파일 삭제하였습니다").toString();

					log.warning(errrorMessage);
				} else {
					String errrorMessage = new StringBuilder().append("게시글의 삭제된 첨부 파일[")
							.append(delectedAttachedFilePathString).append("] 이 이동할 임시 경로[").append(destFilePathString)
							.append("]에 존재하여 임시 경로에 있는 파일 삭제 시도했지만 실패하였습니다").toString();

					log.warning(errrorMessage);

					continue;
				}
			}

			try {
				FileUtils.moveFile(delectedAttachedFile, destFile);
			} catch (Exception e) {
				String errrorMessage = new StringBuilder().append("에러가 발생하여 게시글의 삭제된 첨부 파일[")
						.append(delectedAttachedFilePathString).append("]을 임시 디렉토리[").append(destFilePathString)
						.append("]로 이동하는데 실패하였습니다, errmsg=").append(e.getMessage()).toString();

				log.warning(errrorMessage);
				continue;
			}
		}
	}

	private void saveAllAttachedFile(BoardModifyRes boardModifyRes, short nextAttachedFileSeq, List<FileItem> fileItemList) {
		int indexOfNewAttachedFileList = 0;
		short newAttachedFileSeq = nextAttachedFileSeq;
		for (FileItem fileItem : fileItemList) {
			if (!fileItem.isFormField()) {
				String newAttachedFilePathString = WebCommonStaticUtil.buildAttachedFilePathString(installedPathString,
						mainProjectName, boardModifyRes.getBoardID(), boardModifyRes.getBoardNo(), newAttachedFileSeq);

				File newAttachedFile = new File(newAttachedFilePathString);
				try {
					fileItem.write(newAttachedFile);
				} catch (Exception e) {
					String errorMessage = new StringBuilder().append("게시글[").append(boardModifyRes.toString())
							.append("]의 임시로 저장된 신규 첨부 파일[index=").append(indexOfNewAttachedFileList)
							.append(", fileName=").append(fileItem.getName()).append(", size=")
							.append(fileItem.getSize()).append("]의 이름을 게시판 첨부 파일 이름 규칙에 맞도록 개명[")
							.append(newAttachedFilePathString).append("] 하는데 실패하였습니다").toString();

					log.log(Level.WARNING, errorMessage, e);
				}

				newAttachedFileSeq++;
				indexOfNewAttachedFileList++;
			}
		}
	}
	
	private void saveAllBoardImageFile(BoardModifyRes boardModifyRes, List<BoardImageFileInformation> boardImageFileInformationList) {
		for (BoardImageFileInformation boardImageFileInformation : boardImageFileInformationList) {
			String uploadImageFilePathString = WebCommonStaticUtil.buildUploadImageFilePathString(
					installedPathString, mainProjectName, boardImageFileInformation.getYyyyMMdd(),
					boardImageFileInformation.getDaySequence());
			File newUploadImageFile = new File(uploadImageFilePathString);
			
			FileOutputStream fos = null;			
			BufferedOutputStream bos = null;			
			try {
				fos = new FileOutputStream(newUploadImageFile);
				bos = new BufferedOutputStream(fos);
				
				bos.write(boardImageFileInformation.getBoardImageFileContents());
			} catch(Exception e) {
				
				String newImgTagSrcAttributeString = new StringBuilder()
						.append("/servlet/DownloadImage?yyyyMMdd=")
						.append(boardImageFileInformation.getYyyyMMdd())
						.append("&daySequence=")
						.append(boardImageFileInformation.getDaySequence()).toString();
				
				String errorMessage = new StringBuilder()
						.append("fail to save new image file[")
						.append(uploadImageFilePathString)
						.append("] whose the image url ")
						.append(newImgTagSrcAttributeString)
						.append("' in the board[boardID=")
						.append(boardModifyRes.getBoardID())
						.append(", boardNo=")
						.append(boardModifyRes.getBoardNo())
						.append("]").toString();
				
				log.log(Level.WARNING, errorMessage, e);
				
				continue;
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
		}
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
					String errorMessage = "게시글에 에 삽입된 이미지를 업로드 하는데 실패하였습니다";
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
			
			/*
			 * String uploadImageFilePathString =
			 * WebCommonStaticUtil.buildUploadImageFilePathString( installedPathString,
			 * mainProjectName, uploadImageRes.getYyyyMMdd(),
			 * uploadImageRes.getDaySequence()); File newUploadImageFile = new
			 * File(uploadImageFilePathString);
			 * 
			 * FileOutputStream fos = new FileOutputStream(newUploadImageFile);
			 * BufferedOutputStream bos = new BufferedOutputStream(fos); try {
			 * bos.write(imageFileContents); } finally { if (null != bos) { try {
			 * bos.close(); } catch(Exception e) {
			 * 
			 * } }
			 * 
			 * if (null != fos) { try { fos.close(); } catch(Exception e) {
			 * 
			 * } } }
			 */
			
			boardImageFileInformation.setYyyyMMdd(uploadImageRes.getYyyyMMdd());
			boardImageFileInformation.setDaySequence(uploadImageRes.getDaySequence());
			
			String newImgTagSrcAttributeString = new StringBuilder()
					.append("/servlet/DownloadImage?yyyyMMdd=")
					.append(uploadImageRes.getYyyyMMdd())
					.append("&daySequence=")
					.append(uploadImageRes.getDaySequence()).toString();
			
			
			return newImgTagSrcAttributeString;
			
		} catch(Exception e) {
			String errorMessage = "fail to replace old image tag having base64 to new image tag having image file url";
			log.log(Level.WARNING, errorMessage, e);
			throw new WhiteParserException(errorMessage);
			
		}	
	}
}
