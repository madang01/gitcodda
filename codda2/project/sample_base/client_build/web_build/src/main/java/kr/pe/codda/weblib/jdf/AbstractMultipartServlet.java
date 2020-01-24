package kr.pe.codda.weblib.jdf;

import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

public abstract class AbstractMultipartServlet extends AbstractServlet {
	
	private static final long serialVersionUID = -6436777887426672536L;

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		
		
		if (! ServletFileUpload.isMultipartContent(req)) {
			
			String errorMessage = "the request doesn't contain multipart content";
			
			AccessedUserInformation  accessedUserformation = getAccessedUserInformationFromSession(req);
			
			String userID = (null == accessedUserformation) ? "guest" : accessedUserformation.getUserID();			
			
			String debugMessage = new StringBuilder(errorMessage).append(", userID=")
					.append(userID)
					.append(", ip=")
					.append(req.getRemoteAddr()).toString();			
			
			log.warning(debugMessage);		
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);		
			return;
		}
		
		String paramBoardID = null;
		
		/**
		 * - 참고 - 
		 * 아쉽게도 DiskFileItemFactory 와 ServletFileUpload 클래스가 쓰레드 세이프 한지 알 수 없다
		 * 다만 request 마다 새롭게 객체를 생성하는 방법을 권장하기에 이에 따른다
		 */				
		// Create a factory for disk-based file items
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

		// Set factory constraints
		diskFileItemFactory.setSizeThreshold(WebCommonStaticFinalVars.APACHE_FILEUPLOAD_MAX_MEMORY_SIZE);
		
		diskFileItemFactory.setRepository(USER_WEB_TEMP_PATH);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
		// upload.setHeaderEncoding("UTF-8");
		// log.info("upload.getHeaderEncoding={}", upload.getHeaderEncoding());
		// log.info("req.getCharacterEncoding={}", req.getCharacterEncoding());

		// Set overall request size constraint
		upload.setSizeMax(WebCommonStaticFinalVars.TOTAL_ATTACHED_FILE_MAX_SIZE);
		upload.setFileSizeMax(WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);

		// Parse the request
		List<FileItem> fileItemList = null;
		try {
			/**
			 * WARNING! 파싱은 request 가 가진  입력 스트림을 소진합니다, 하여 파싱후 그 결과를 전달해 주어야 합니다.
			 * 이때 쓰레드 세이프 문제 때문에 변수 fileItemList 는 멤버 변수가 아닌 request 객체를 통해 전달합니다.
			 */
			fileItemList = upload.parseRequest(req);					
			req.setAttribute(WebCommonStaticFinalVars.MULTIPART_PARSING_RESULT_ATTRIBUTE_OF_REQUEST, fileItemList);

			
			/** 메뉴 그룹이 게시판인 경우 게시판 식별자 추가 */
			for (FileItem fileItem : fileItemList) {
				if (fileItem.isFormField()) {
					String formFieldName = fileItem.getFieldName();
					String formFieldValue = fileItem.getString("UTF-8");
					if (formFieldName.equals("boardID")) {
						paramBoardID = formFieldValue;
						break;
					}
				}
			}
		} catch (FileUploadException e) {
			log.log(Level.WARNING, "fail to parse a multipart request", e);

			/**
			 * <pre>
			 * 멀티 파트 폼 파싱 실패는 비정상적인 경우 메뉴 그룹 URL을 로직의 간편성을 위해서 루트로 바꾼다.
			 * 이는 일반 사용자들 웹사이트에서 메뉴 그룹이 게시판인 경우에도 비용을 들이지 않고 잘 동작 시키기 위함이다.
			 * </pre>
			 */
			req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL, "/");

			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.PrintWriter writer = new java.io.PrintWriter(bos);
			e.printStackTrace(writer);
			writer.flush();

			String errorMessage = bos.toString();

			try {
				writer.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// String errString = "Programmer's Exception: " +logMsg +
			// CommonStaticFinal.NEWLINE + bos.toString();
			// Logger.err.println(this, errString);
			String debugMessage = new StringBuilder("Programmer's Exception: ")
					.append(CommonStaticFinalVars.NEWLINE).append(errorMessage).toString();

			String userMessage = "알 수 없는 에러가 발생하였습니다";
			printErrorMessagePage(req, res, userMessage, debugMessage);
			return;
		}
		
		
		String menuGroupURL = (String)req.getAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL);
		
		/**
		 * 일반 사용자들 웹사이트에서 메뉴 그룹이 게시판인 경우 메뉴 그룹 URL 의 경우 파라미터 'boardID'(=게시판 식별자) 가 추가적으로 붙는다
		 * 멀티 파트 폼에서 파라미터 'boardID'(=게시판 식별자) 를 얻어와서 메뉴 그룹 URL에 파라미터 'boardID'(=게시판 식별자) 가 추가적으로 붙인다. 
		 */
		if ("/servlet/BoardList".equals(menuGroupURL)) {
			short boardID = 0;
			if (null != paramBoardID) {
				try {
					boardID = Short.parseShort(paramBoardID);

					if (boardID < 0) {
						boardID = 0;
					} else if (boardID > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
						boardID = 0;
					}
				} catch (IllegalArgumentException e) {
				}
			}
			
			String newMenuGroupURL = new StringBuilder(menuGroupURL)
					.append("?boardID=").append(boardID).toString();
			
			req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL, newMenuGroupURL);
		}
		
		super.performPreTask(req, res);
	}
	
	
}
