package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

/**
 * 이미지 파일 URL 문자열을 반환하는 인터페이스. {@link BoardContentsWhiteParser#checkWhiteValue(ImageFileURLGetterIF, String)} 에서 사용된다 
 * 
 * @author Won Jonghoon
 *
 */
public interface ImageFileURLGetterIF {
	/**
	 * 이미지 파일 URL 문자열을 반환한다
	 * 
	 * @param boardImageFileInformation 이미지 파일 정보
	 * @return 이미지 파일 URL 문자열
	 * @throws WhiteParserException 에러 발생시 던지는 예외
	 */
	public String getImageFileURL(BoardImageFileInformation boardImageFileInformation) throws WhiteParserException;
}
