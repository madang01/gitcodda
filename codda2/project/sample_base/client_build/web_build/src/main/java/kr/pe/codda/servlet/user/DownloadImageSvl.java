package kr.pe.codda.servlet.user;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.DownloadImageReq.DownloadImageReq;
import kr.pe.codda.impl.message.DownloadImageRes.DownloadImageRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.WebCommonStaticUtil;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class DownloadImageSvl extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1018956017402948866L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		DownloadImageRes downloadImageRes = doWork(req, res);

		/**
		 * 참고 사이트 : http://goodcodes.tistory.com/14 JSP - File Download 2014/02/14 12:00
		 * 
		 * Posted in Tomcat & JSP by 흔들리는내마음
		 */

		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		String mainProjectName = runningProjectConfiguration.getMainProjectName();
		String installedPathString = runningProjectConfiguration.getInstalledPathString();

		String uploadImageFilePathString = WebCommonStaticUtil.buildUploadImageFilePathString(installedPathString,
				mainProjectName, downloadImageRes.getYyyyMMdd(), downloadImageRes.getDaySequence());
		File uploadImageFile = new File(uploadImageFilePathString);
		// SecureCoding 파일명을 받는 경우 ../ 에 대한 체크가 필요하다.(지정디렉토리 이외의 디렉토리 금지 루틴)

		if (!uploadImageFile.exists()) {
			String errorMessage = new StringBuilder().append("다운 로드 할 대상 파일[").append(uploadImageFilePathString)
					.append("]이 존재 하지 않습니다").toString();
			String debugMessage = "";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		// 브라우저 별 처리

		// 1. content-type의 세팅
		res.setContentType("application/octet-stream;charset=UTF-8");
		String imageFileName = downloadImageRes.getImageFileName();

		// 브라우저별 한글 인코딩
		if (getBrowser(req).equals("MSIE")) {
			// URLEncode하고 +문자만 공백으로 바꾸는 경우
			imageFileName = URLEncoder.encode(imageFileName, "UTF-8").replaceAll("\\+", "%20");
		} else if (getBrowser(req).equals("Chrome")) {
			// char단위로 검색하여 ~표시보다 char값이 높을 때(ascii코드값이 아닌경우)만 URLEncode한다.
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < imageFileName.length(); i++) {
				char c = imageFileName.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			imageFileName = sb.toString();
		} else {
			// latin1(8859_1)
			imageFileName = new String(imageFileName.getBytes("UTF-8"), "8859_1");
		}

		// 2. content-disposition의 세팅
		res.addHeader("Content-Disposition", "attachment;filename=\"" + imageFileName + "\"");
		byte[] bytes = new byte[4096];
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(uploadImageFile);
			bis = new BufferedInputStream(fis);
			OutputStream os = res.getOutputStream();
			int n = bis.read(bytes);
			while (n != -1) {
				os.write(bytes, 0, n);
				n = bis.read(bytes);
			}
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("다운로드 파일[").append(uploadImageFilePathString)
					.append("] 처리중 입출력 에러 발생, errmsg=").append(e.getMessage()).toString();
			log.warning(errorMessage);
		} finally {
			if (null != bis) {
				try {				
					bis.close();
				} catch (Exception e) {
				}
			}
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}			
		}
	}
	
	public DownloadImageRes doWork(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramYyyyMMdd = req.getParameter("yyyyMMdd");
		String paramDaySequence = req.getParameter("daySequence");
		/**************** 파라미터 종료 *******************/

		if (null == paramYyyyMMdd) {
			String errorMessage = "다운로드 이미지의 일자를 넣어주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		try {
			Long.parseLong(paramYyyyMMdd);
		} catch (NumberFormatException e) {
			String errorMessage = "다운로드 이미지의 일자가 잘못되었습니다";
			String debugMessage = new StringBuilder().append("the web parameter yyyyMMdd[").append(paramDaySequence)
					.append("] is not a long value").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}

		if (null == paramDaySequence) {
			String errorMessage = "다운로드 이미지의 일별 시퀀스를 넣어주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}

		long daySequence = -1;
		try {
			daySequence = Long.parseLong(paramDaySequence);
		} catch (NumberFormatException e) {
			String errorMessage = "다운로드 이미지의 일별 시퀀스가 잘못되었습니다";
			String debugMessage = new StringBuilder().append("the web parameter daySequence[").append(paramDaySequence)
					.append("] is not a long value").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		if (daySequence > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = "다운로드 이미지의 일별 시퀀스가 잘못되었습니다";
			String debugMessage = new StringBuilder().append("the web parameter daySequence[").append(paramDaySequence)
					.append("] is greter than unsigned integer max").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}

		DownloadImageReq downloadImageReq = new DownloadImageReq();
		downloadImageReq.setYyyyMMdd(paramYyyyMMdd);
		downloadImageReq.setDaySequence(daySequence);

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();

		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), downloadImageReq);

		if ((outputMessage instanceof MessageResultRes)) {
			MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
			String errorMessage = messageResultRes.getResultMessage();
			String debugMessage = "";
			throw new WebClientException(errorMessage, debugMessage);
		} else if (!(outputMessage instanceof DownloadImageRes)) {
			String errorMessage = "다운 로드 파일 정보를 얻는데 실패하였습니다";

			String debugMessage = new StringBuilder("입력 메시지[").append(downloadImageReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[").append(outputMessage.toString()).append("] 도착").toString();

			log.severe(debugMessage);

			throw new WebClientException(errorMessage, debugMessage);
		}

		DownloadImageRes downloadImageRes = (DownloadImageRes) outputMessage;
		
		return downloadImageRes;
	}

	// HTTP/1.1 헤더로부터 브라우저를 가져온다.
	private String getBrowser(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		if (header.indexOf("MSIE") > -1) {
			return "MSIE";
			// IE8 ~ IE11
		} else if (header.indexOf("Trident") > -1) {
			return "MSIE";
		} else if (header.indexOf("OPR") > -1) {
			return "OPR";
		} else if (header.indexOf("Chrome") > -1) {
			return "Chrome";
		} else if (header.indexOf("Opera") > -1) {
			return "Opera";
		} else if (header.indexOf("Firefox") > -1) {
			return "Firefox";
		} else if (header.indexOf("Safari") > -1) {
			return "Safari";
		} else {
			return "UNKOWN";
		}
	}
}
