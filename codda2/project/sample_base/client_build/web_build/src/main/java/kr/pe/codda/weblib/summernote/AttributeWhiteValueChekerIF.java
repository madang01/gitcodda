package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

/**
 * 태그의 속성 값에 대한 허락 받은 값 여부 검사기 
 * 
 * @author Won Jonghoon
 *
 */
public interface AttributeWhiteValueChekerIF {
	
	/**
	 * 지정한 속성 값이 허락한 값이 아니면 예외를 던진다
	 * @param attributeValue 속성 값
	 * @throws WhiteParserException 지정한 속성 값이 허락한 값이 아니면 던지는 예외
	 */
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException;
	
	public String getTagName();
	public String getAttributeName();
}
