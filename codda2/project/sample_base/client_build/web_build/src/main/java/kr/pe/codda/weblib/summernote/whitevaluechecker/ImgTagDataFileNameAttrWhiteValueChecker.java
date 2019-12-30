package kr.pe.codda.weblib.summernote.whitevaluechecker;

import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;
import kr.pe.codda.weblib.summernote.BoardImageFileInformation;

public class ImgTagDataFileNameAttrWhiteValueChecker implements AttributeWhiteValueChekerIF {
	private final String tagName = "img";
	private final String attributeName = "data-filename";


	/**
	 * 이 메소드는 사용해서는 안되는 메소드이다. 파서에서는 이 클래스 객체를 얻어와서 {@link #checkXSSAttack(String, BoardImageFileInformation)} 를 사용한다.
	 */
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		throw new WhiteParserException("이 메소드를 사용하지 마세요!");
	}

	
	/**
	 * 지정한 값이 XSS 공격 코드가 있다면 예외를 던진다. 이때 값에서 추출한 이미지 파일 이름을 '게시글 이미지 파일 정보' 객체에 저장한다. 
	 * 
	 * @param attributeValue img 태그의 src 속성 값
	 * @param boardImageFileInformation 속성 값에서 추출된 '이미지 파일 이름'을 저장할 '게시글 이미지 파일 정보' 객체
	 * @throws WhiteParserException xss 공격 코드가 있을 경우 혹은 처리중 에러가 있을 경우 던지는 예외
	 */
	public void checkXSSAttack(String attributeValue, BoardImageFileInformation boardImageFileInformation)
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
