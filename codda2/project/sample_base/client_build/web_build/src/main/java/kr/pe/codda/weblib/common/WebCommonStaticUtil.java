package kr.pe.codda.weblib.common;

import java.io.File;

import org.apache.commons.lang3.time.FastDateFormat;

import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;

public abstract class WebCommonStaticUtil {
	
	public static String buildShortFileNameOfAttachedFile(short boardID, long boardNo, short attachedFileSeq) {
		return new StringBuilder(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_PREFIX).append("_BoardID")
				.append(boardID).append("_BoardNo")
				.append(boardNo).append("_Seq").append(attachedFileSeq)
				.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_SUFFIX).toString();
	}
	
	public static String buildAttachedFilePathString(String installedPathString, 
			String mainProjectName, short boardID, long boardNo, short attachedFileSeq) {
		return new StringBuilder(WebRootBuildSystemPathSupporter.getUserWebUploadPathString(installedPathString, mainProjectName)).append(File.separator)
				.append(buildShortFileNameOfAttachedFile(boardID, boardNo, attachedFileSeq)).toString();
	}
	
	/**
	 * 경로를 제외한 순수 업로드 이미지 파일명을 반환한다.
	 * 
	 * @param yyyyMMdd 년(yyyy)월(MM)일(dd) 8글자
	 * @param daySequence 일자별 시퀀스
	 * @return 경로를 제외한 순수 업로드 이미지 파일명
	 */
	public static String buildShortFileNameOfUplodadImageFile(String yyyyMMdd, long daySequence) {
		return new StringBuilder(WebCommonStaticFinalVars.WEBSITE_UPLOAD_IMAGE_FILE_PREFIX).append("_yyyyMMdd")
				.append(yyyyMMdd).append("_daySequence")
				.append(daySequence)
				.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_SUFFIX).toString();
	}
	
	/**
	 * 업로드 이미지 파일 전체 경로명을 반환한다
	 * 
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @param yyyyMMdd 년(yyyy)월(MM)일(dd) 8글자
	 * @param daySequence 일자별 시퀀스
	 * @return 업로드 이미지 파일 전체 경로명
	 */
	public static String buildUploadImageFilePathString(String installedPathString, 
			String mainProjectName, String yyyyMMdd, long daySequence) {
		return new StringBuilder(WebRootBuildSystemPathSupporter.getUserWebUploadPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append(buildShortFileNameOfUplodadImageFile(yyyyMMdd, daySequence)).toString();
	}
	
	public static FastDateFormat FULL_DATE_FORMAT = FastDateFormat.getInstance( "yyyy년 MM월 dd일 HH시 mm분 ss초");
	public static FastDateFormat SIMPLE_DATE_FORMAT = FastDateFormat.getInstance( "yyyy.MM.dd HHmmss");
	
	
}
