/**
 * 
 * @(#) .java
 * Copyright 1999-2000 by  LG-EDS Systems, Inc.,
 * Information Technology Group, Application Architecture Team, 
 * Application Intrastructure Part.
 * 236-1, Hyosung-2dong, Kyeyang-gu, Inchun, 407-042, KOREA.
 * All rights reserved.
 *  
 * NOTICE !      You can copy or redistribute this code freely, 
 * but you should not remove the information about the copyright notice 
 * and the author.
 *  
 * @author  WonYoung Lee, wyounglee@lgeds.lg.co.kr.
 */

package kr.pe.codda.weblib.jdf;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.exception.NoSessionKeyParameterException;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.exception.WhiteParserException;

/**
 * <pre>
 * get 방식과 post 방식 추상화 시켜 하나로 바라보게 해 주는 JDF 프레임 최상의 추상 클래스. 
 * 복사자&수정자 : Won Jonghoon
 * 복사&수정 내용 : 응용에 따라 약간 수정
 * </pre>
 */
@SuppressWarnings("serial")
public abstract class JDFBaseServlet extends AbstractBaseServlet {
	/**
	 * <pre>
	 * WARNING! 설정파일에서 '어드민 사이트 로그인 입력 페이지'와 
	 *	'유저 사이트 로그인 입력 페이지'를 
	 *	로그인 입력 페이지 1개로 통합하지 말것.
	 *	Tomcat 은 1개 JVM 에서  가상 호스트 서비스를 지원한다. 
	 *	sample_base 프로젝트는 어드민 사이트와 유저 사이트를  
	 *	Tomcat 가상 호스트로 운영한다.
	 *	각 사이트는 코다 설정 파일에서 
	 *	지정한 별도의 로그인 입력 페이지를 갖는다.
	 *	하여 이를 통합하면 다른 한쪽은 사이트에 맞지 않는 
	 *	로그인 입력 페이지가 보여지는 문제를 갖게된다.
	 * </pre>
	 */
	protected String JDF_MEMBER_LOGIN_INPUT_PAGE = null;
	protected String JDF_ADMIN_LOGIN_INPUT_PAGE = null;
	protected String JDF_SESSION_KEY_REDIRECT_PAGE = null;
	protected String JDF_ERROR_MESSAGE_PAGE = null;
	protected boolean JDF_SERVLET_TRACE = true;

	protected File USER_WEB_TEMP_PATH = null;

	/**
	 * BaseServlet constructor comment.
	 */
	public JDFBaseServlet() {
		super();

		CoddaConfigurationManager configurationManager = CoddaConfigurationManager.getInstance();
		CoddaConfiguration runningProjectConfiguration = configurationManager.getRunningProjectConfiguration();

		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();

		JDF_MEMBER_LOGIN_INPUT_PAGE = commonPart.getJDFMemberLoginPage();
		JDF_ADMIN_LOGIN_INPUT_PAGE = commonPart.getJDFAdminLoginPage();
		JDF_SESSION_KEY_REDIRECT_PAGE = commonPart.getJDFSessionKeyRedirectPage();
		JDF_ERROR_MESSAGE_PAGE = commonPart.getJDFErrorMessagePage();
		JDF_SERVLET_TRACE = commonPart.getJDFServletTrace();

		String mainProjectName = runningProjectConfiguration.getMainProjectName();
		String installedPathString = runningProjectConfiguration.getInstalledPathString();
		String userWebTempPathString = WebRootBuildSystemPathSupporter.getUserWebTempPathString(installedPathString,
				mainProjectName);

		USER_WEB_TEMP_PATH = new File(userWebTempPathString);
	}

	/**
	 * Performs the HTTP GET operation; the default implementation reports an HTTP
	 * BAD_REQUEST error. Overriding this method to support the GET operation also
	 * automatically supports the HEAD operation. (HEAD is a GET that returns no
	 * body in the response; it just returns the request HEADer fields.)
	 * 
	 * <p>
	 * Servlet writers who override this method should read any data from the
	 * request, set entity headers in the response, access the writer or output
	 * stream, and, finally, write any response data. The headers that are set
	 * should include content type, and encoding. If a writer is to be used to write
	 * response data, the content type must be set before the writer is accessed. In
	 * general, the servlet implementor must write the headers before the response
	 * data because the headers can be flushed at any time after the data starts to
	 * be written.
	 * 
	 * <p>
	 * Setting content length allows the servlet to take advantage of HTTP
	 * "connection keep alive". If content length can not be set in advance, the
	 * performance penalties associated with not using keep alives will sometimes be
	 * avoided if the response entity fits in an internal buffer.
	 * 
	 * <p>
	 * Entity data written for a HEAD request is ignored. Servlet writers can, as a
	 * simple performance optimization, omit writing response data for HEAD methods.
	 * If no response data is to be written, then the content length field must be
	 * set explicitly.
	 * 
	 * <P>
	 * The GET operation is expected to be safe: without any side effects for which
	 * users might be held responsible. For example, most form queries have no side
	 * effects. Requests intended to change stored data should use some other HTTP
	 * method. (There have been cases of significant security breaches reported
	 * because web-based applications used GET inappropriately.)
	 * 
	 * <P>
	 * The GET operation is also expected to be idempotent: it can safely be
	 * repeated. This is not quite the same as being safe, but in some common
	 * examples the requirements have the same result. For example, repeating
	 * queries is both safe and idempotent (unless payment is required!), but buying
	 * something or modifying data is neither safe nor idempotent.
	 * 
	 * @param req  HttpServletRequest that encapsulates the request to the servlet
	 * @param resp HttpServletResponse that encapsulates the response from the
	 *             servlet
	 * 
	 * @exception IOException      if detected when handling the request
	 * @exception ServletException if the request could not be handled
	 * 
	 * @see javax.servlet.ServletResponse#setContentType
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		/*
		 * Enumeration<String> keys = req.getParameterNames(); while
		 * (keys.hasMoreElements()) { String paramKey = keys.nextElement();
		 * 
		 * log.info("get::key:{},value={}", paramKey, req.getParameter(paramKey)); }
		 */

		performBasePreTask(req, res);
	}

	/**
	 * 
	 * Performs the HTTP POST operation; the default implementation reports an HTTP
	 * BAD_REQUEST error. Servlet writers who override this method should read any
	 * data from the request (for example, form parameters), set entity headers in
	 * the response, access the writer or output stream and, finally, write any
	 * response data using the servlet output stream. The headers that are set
	 * should include content type, and encoding. If a writer is to be used to write
	 * response data, the content type must be set before the writer is accessed. In
	 * general, the servlet implementor must write the headers before the response
	 * data because the headers can be flushed at any time after the data starts to
	 * be written.
	 * 
	 * <p>
	 * If HTTP/1.1 chunked encoding is used (that is, if the transfer-encoding
	 * header is present), then the content-length header should not be set. For
	 * HTTP/1.1 communications that do not use chunked encoding and HTTP 1.0
	 * communications, setting content length allows the servlet to take advantage
	 * of HTTP "connection keep alive". For just such communications, if content
	 * length can not be set, the performance penalties associated with not using
	 * keep alives will sometimes be avoided if the response entity fits in an
	 * internal buffer.
	 * 
	 * <P>
	 * This method does not need to be either "safe" or "idempotent". Operations
	 * requested through POST can have side effects for which the user can be held
	 * accountable. Specific examples including updating stored data or buying
	 * things online.
	 * 
	 * @param req  HttpServletRequest that encapsulates the request to the servlet
	 * @param resp HttpServletResponse that encapsulates the response from the
	 *             servlet
	 * 
	 * @exception IOException      if detected when handling the request
	 * @exception ServletException if the request could not be handled
	 * 
	 * @see javax.servlet.ServletResponse#setContentType
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		/*
		 * Enumeration<String> keys = req.getParameterNames(); while
		 * (keys.hasMoreElements()) { String paramKey = keys.nextElement();
		 * 
		 * log.info("post::key:{},value={}", paramKey, req.getParameter(paramKey)); }
		 */

		performBasePreTask(req, res);
	}

	/**
	 * <pre>
	 * 아래와 같은 JDF 핵심 로직을 수행하는 메소드, WARNING! 선행 작업 재 정의는 이 메소드가 아닌 {@link #performPreTask} 를 이용할것.
	 * 
	 * (1) get, post 모드에 상관없이 하나로 처리 될 수 있도록 함 
	 * (2) 로그 추적
	 * (3) 에러 처리
	 * (4) 사용자 정의용 선행 작업 호출
	 * </pre>
	 * 
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void performBasePreTask(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			ServerSessionkeyIF webServerSessionkey  = serverSessionkeyManager.getMainProjectServerSessionkey();
			req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING, webServerSessionkey.getModulusHexStrForWeb());
		} catch(Exception e) {
			String errorMessage = "fail to set modulusHexString to request";
			
			log.log(Level.WARNING, errorMessage, e);			

			String debugMessage = new StringBuilder()
					.append(errorMessage)
					.append(", errmsg=")
					.append(e.getMessage()).toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		String menuGroupURL = this
				.getInitParameter(WebCommonStaticFinalVars.SERVLET_INIT_PARM_KEY_NAME_OF_MENU_GROUP_URL);
		if (null == menuGroupURL) {
			String errorMessage = new StringBuilder()
					.append("the servlet init parameter '")
					.append(WebCommonStaticFinalVars.SERVLET_INIT_PARM_KEY_NAME_OF_MENU_GROUP_URL)
					.append("' is null in requestURI[")
					.append(req.getRequestURI())
					.append("]").toString();
			
			log.warning(errorMessage);
			menuGroupURL = "/";
		} else {
			
			
			/**
			 * 일반 사용자들 웹사이트에서 메뉴 그룹이 게시판인 경우 메뉴 그룹 URL 의 경우 파라미터 'boardID'(=게시판 식별자) 가 추가적으로 붙는다.
			 * 멀티 파트 폼에서 파라미터 'boardID'(=게시판 식별자)를 얻는 방법과 일반 폼 에서 파라미터 'boardID'(=게시판 식별자)를 얻는 방법은 전혀 다르다.
			 * 하여 공통으로 처리를 할 수 없기때문에 멀티 파트의 경우 멀티 파트 담당하는곳에서 처리를 하고 
			 * 이곳에서는 멀티 파트가 아닌 경우만  메뉴 그룹 URL에 파라미터 'boardID'(=게시판 식별자) 가 추가적으로 붙인다.
			 */
			if ("/servlet/BoardList".equals(menuGroupURL)) {
				
				if (! ServletFileUpload.isMultipartContent(req)) {
					String paramBoardID = req.getParameter("boardID");
					
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
					
					menuGroupURL = new StringBuilder(menuGroupURL)
							.append("?boardID=").append(boardID).toString();
				}
				
			}
		}

		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL, menuGroupURL);

		String traceLogBaseMsg = null;
		long start = 0, end = 0;
		if (JDF_SERVLET_TRACE) {
			// logMsg = req.getRequestURI() + ":" + req.getRemoteHost() + "(" +
			// req.getRemoteAddr() + ")";
			StringBuilder traceLogBaseMsgBuilder = new StringBuilder(req.getRequestURI()).append(":")
					.append(req.getRemoteHost()).append("(").append(req.getRemoteAddr()).append(")");

			String user = req.getRemoteUser();
			if (user != null) {
				// logMsg += ":" + user;
				traceLogBaseMsgBuilder.append(":").append(user);
			}

			traceLogBaseMsg = traceLogBaseMsgBuilder.toString();

			start = System.currentTimeMillis();
			
			String infoMessage = new StringBuilder()
					.append(traceLogBaseMsg)
					.append(":calling").toString();			
			
			log.info(infoMessage);
		}

		try {
			performPreTask(req, res);
		} catch (NoSessionKeyParameterException e) {
			req.setAttribute("requestURI", req.getRequestURI());
			printJspPage(req, res, JDF_SESSION_KEY_REDIRECT_PAGE);
		} catch (ServerTaskException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			
		} catch (ConnectionPoolException e) {
			log.log(Level.WARNING, "server connection fail", e);

			String errorMessage = e.getMessage();

			String debugMessage = new StringBuilder("Programmer's Exception: ").append(traceLogBaseMsg)
					.append(CommonStaticFinalVars.NEWLINE).append(errorMessage).toString();

			String userMessage = "서버 접속이 실패하였습니다";
			printErrorMessagePage(req, res, userMessage, debugMessage);
		} catch (WhiteParserException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			
			AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);

			String logMessage = new StringBuilder().append(errorMessage)
					.append(", userID=[").append(accessedUserformation.getUserID()).append("], ip=[")
					.append(req.getRemoteAddr()).append("]").toString();

			log.warning(logMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
		} catch (WebClientException e) {
			String errorMessage = e.getErrorMessage();
			String debugMessage = e.getDebugMessage();

			AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
			
			String logMessage = new StringBuilder()
					.append((null == debugMessage) ? errorMessage : debugMessage)
					.append(", userID=[")
					.append(accessedUserformation.getUserID())
					.append("], ip=[")
					.append(req.getRemoteAddr())
					.append("]").toString();

			log.warning(logMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
		} catch (Exception | java.lang.Error e) {
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			
			java.io.PrintWriter writer = new java.io.PrintWriter(bos);
			try {
				e.printStackTrace(writer);
				writer.flush();
			} finally {
				try {
					writer.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
						
			String errorMessage = bos.toString();

			AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);

			String debugMessage = new StringBuilder("Programmer's Exception: ").append(traceLogBaseMsg)
					.append(CommonStaticFinalVars.NEWLINE).append(errorMessage)
					.append(", userID=[")
					.append(accessedUserformation.getUserID())
					.append("], ip=[")
					.append(req.getRemoteAddr())
					.append("]").toString();
			
			log.warning(debugMessage);

			String userMessage = "알 수 없는 에러가 발생하였습니다";
			printErrorMessagePage(req, res, userMessage, debugMessage);
		}

		if (JDF_SERVLET_TRACE) {
			end = System.currentTimeMillis();
			// Logger.sys.println(this, logMsg + ":end(elapsed=" + (end-start) +
			// ")" + CommonStaticFinal.NEWLINE);
			log.info(new StringBuilder(traceLogBaseMsg).append(":end(elapsed=").append((end - start)).append(")")
					.append(CommonStaticFinalVars.NEWLINE).toString());
		}
	}

	/**
	 * 사용자 재 정의가 가능한 선행 작업 메소드
	 * 
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 */
	protected abstract void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception;

	/**
	 * Sends a temporary redirect response to the client using the specified
	 * redirect location URL. The URL must be absolute (for example,
	 * <code><em>https://hostname/path/file.html</em></code>). Relative URLs are not
	 * permitted here.
	 * 
	 * @param req      javax.servlet.http.HttpServletRequest
	 * @param res      javax.servlet.http.HttpServletResponse
	 * @param location the redirect location URL
	 * @exception IOException If an I/O error has occurred.
	 */
	protected void printHtmlPage(HttpServletRequest req, HttpServletResponse res, String location) {
		try {
			res.sendRedirect(location);
		} catch (IOException e) {
			log.log(Level.WARNING, "fail to call method sendRedirect", e);

			StringBuilder debugMessageBuilder = new StringBuilder("IOException::File Not Found, location=")
					.append(location);
			printErrorMessagePage(req, res, " 에러가 발생하여 서블릿 정적 페이지 이동이 실패하였습니다.", debugMessageBuilder.toString());
		}
	}

	private String buildErrorStackMessage(HttpServletRequest req, HttpServletResponse res, Throwable e) {

		StringBuilder debugMessageStringBuilder = new StringBuilder();
		debugMessageStringBuilder.append("JSP Call Error: ");
		debugMessageStringBuilder.append(this.getClass().getName());
		debugMessageStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		debugMessageStringBuilder.append("Request URI: ");
		debugMessageStringBuilder.append(req.getRequestURI());
		debugMessageStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		String user = req.getRemoteUser();
		if (user != null) {
			debugMessageStringBuilder.append("User : ");
			debugMessageStringBuilder.append(user);
			debugMessageStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		}

		debugMessageStringBuilder.append("User Location  : ");
		debugMessageStringBuilder.append(req.getRemoteHost());
		debugMessageStringBuilder.append("(");
		debugMessageStringBuilder.append(req.getRemoteAddr());
		debugMessageStringBuilder.append(")");
		debugMessageStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		java.io.ByteArrayOutputStream bos = null;
		java.io.PrintWriter writer = null;

		try {
			bos = new java.io.ByteArrayOutputStream();
			writer = new java.io.PrintWriter(bos);

			e.printStackTrace(writer);
			writer.flush();

			debugMessageStringBuilder.append(bos.toString());
		} catch (Exception e1) {
			log.log(Level.WARNING, "error", e1);
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (Exception e1) {
				}
			}
			if (null != bos) {
				try {
					bos.close();
				} catch (Exception e1) {

				}
			}
		}

		return debugMessageStringBuilder.toString();
	}

	/**
	 * Sends a temporary redirect response to the client using the specified
	 * redirect location of jsp file. The URL must be absolute (for example,
	 * <code><em>/example/result.jsp</em></code>). Relative URLs are not permitted
	 * here.
	 * 
	 * @param req     javax.servlet.http.HttpServletRequest
	 * @param res     javax.servlet.http.HttpServletResponse
	 * @param jspfile the redirect location URL of jsp file.
	 */
	protected void printJspPage(HttpServletRequest req, HttpServletResponse res, String jspfile) {
		try {
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(jspfile);

			dispatcher.forward(req, res);
		} catch (Exception | Error e) {
			log.log(Level.WARNING, "fail to call method forward", e);
			
			try {

				String userMessage = "에러가 발생하였습니다, 서비스 이용중에 불편을 드려 죄송합니다";
				String debugMessage = buildErrorStackMessage(req, res, e);

				printErrorMessagePage(req, res, userMessage, debugMessage);
			} catch (Exception ex) {
				/** 더 이상 에러 처리 없이 종료한다 */
				log.log(Level.WARNING, "fail to write a user defined error message page", ex);				
			}
		}
	}

	/**
	 * <pre>
	 * 이 Method는 반드시 프로젝트에서 구현해서 사용해야 함.
	 * 왜냐면, 프로젝트마다 Message를 보여주는 화면이 다를 수 있기 때문.
	 * 
	 * </pre>
	 * 
	 * @param req          javax.servlet.http.HttpServletRequest
	 * @param res          javax.servlet.http.HttpServletResponse
	 * @param userMessage  최종 사용자에게 보여질 메세지
	 * @param debugMessage 개발시점에서 개발자가 Debugging을 위해 보는 메세지, 통상 운영시는 보이지 않도록 함.
	 */
	protected void printErrorMessagePage(HttpServletRequest req, HttpServletResponse res, String userMessage,
			String debugMessage) {

		if (null == userMessage) {
			userMessage = "user messsage is null";
		}

		if (null == debugMessage) {
			debugMessage = "debug messsage is null";
		}

		req.setAttribute("debugMessage", debugMessage);
		req.setAttribute("userMessage", userMessage);

		printJspPage(req, res, JDF_ERROR_MESSAGE_PAGE);
	}
}
