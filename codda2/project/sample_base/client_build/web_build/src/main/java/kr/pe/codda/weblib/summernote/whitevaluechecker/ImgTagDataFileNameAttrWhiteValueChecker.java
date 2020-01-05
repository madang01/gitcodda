package kr.pe.codda.weblib.summernote.whitevaluechecker;

import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;
import kr.pe.codda.weblib.summernote.BoardImageFileInformation;

/**
 * <pre>
 * 허락된 img 태그의 data-filename 속성 값 검사기. 
 * img tag 의 data-filename 속성은 src 속성 값이 'data:<image file mime type>;base64,<base64 string>' 형태의 '베이스 64 이미지 데이터' 인 경우 필요한 속성이다.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class ImgTagDataFileNameAttrWhiteValueChecker implements AttributeWhiteValueChekerIF {
	private final String tagName = "img";
	private final String attributeName = "data-filename";


	/**
	 * 이 메소드는 사용해서는 안되는 메소드이다. 파서에서는 이 클래스 객체를 얻어와서 {@link #throwExceptionIfNoWhiteValue(String, BoardImageFileInformation)} 를 사용한다.
	 */
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		throw new WhiteParserException("이 메소드를 사용하지 마세요!");
	}

	
	/**
	 * 지정한 값에 허락되지 않은 문자가 있다면 예외를 던진다. 이때 값에서 추출한 이미지 파일 이름을 '게시글 이미지 파일 정보' 객체에 저장한다. 
	 * 
	 * @param attributeValue img 태그의 src 속성 값
	 * @param boardImageFileInformation 속성 값에서 추출된 '이미지 파일 이름'을 저장할 '게시글 이미지 파일 정보' 객체
	 * @throws WhiteParserException 지정한 값에 허락되지 않은 문자가 있다면 던지는 예외
	 */
	public void throwExceptionIfNoWhiteValue(String attributeValue, BoardImageFileInformation boardImageFileInformation)
			throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();
			throw new WhiteParserException(errorMessage);
		}

		if ("".equals(attributeValue)) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is empty").toString();
			throw new WhiteParserException(errorMessage);
		}

		try {
			ValueChecker.checkValidFileName(attributeValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is bad, errmsg=").append(e.getMessage()).toString();
			throw new WhiteParserException(errorMessage);
		}

		boardImageFileInformation.setBoardImageFileName(attributeValue);
	}

	@Override
	public String getTagName() {
		return tagName;
	}

	@Override
	public String getAttributeName() {
		return attributeName;
	}

}
