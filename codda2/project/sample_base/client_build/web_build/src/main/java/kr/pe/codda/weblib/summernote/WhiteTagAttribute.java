package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

/**
 * 화이트 리스트({@link WhiteList})에 등록된 태그({@link WhiteTag})에 허락된 속성
 * 
 * @author Won Jonghoon
 *
 */
public class WhiteTagAttribute {
	private final String tagName;
	private final String attributeName;
	private final AttributeWhiteValueChekerIF attributeWhiteValueChecker;
	
	/**
	 * 허락된 속성 생성자
	 * 
	 * @param tagName 속성이 속한 태그 이름
	 * @param attributeName 속성 이름
	 * @param attributeWhiteValueChecker 허락되지 않은 속성 값 검사기
	 * @throws IllegalArgumentException 태그 이름 이나 속성 이름 혹은  '허락되지 않은 속성 값 검사기' 가 null 인 경우 던지는 예외
	 * @throws WhiteParserException '허락되지 않은 속성 값 검사기' 의 태그와 속성값이 불일치 할 경우 던지는 예외
	 */
	public WhiteTagAttribute(String tagName, String attributeName, AttributeWhiteValueChekerIF attributeWhiteValueChecker) throws IllegalArgumentException, WhiteParserException {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		if (null == attributeWhiteValueChecker) {
			throw new IllegalArgumentException("the paramter attributeWhiteValueChecker is null");
		}
		
		
		this.tagName = tagName;
		this.attributeName = attributeName;
		this.attributeWhiteValueChecker = attributeWhiteValueChecker;
		
		throwExceptionIfWhiteValueCheckerIsInvalidTagNameOrAttributeName();
	}
	
	/**
	 * @return '속성 값에 대한 XSS 공격 검사기'
	 */
	public AttributeWhiteValueChekerIF getAttributeValueXSSAtackChecker() {
		return attributeWhiteValueChecker;
	}
	
	/**
	 * 속성 값에 대한 허락되지 않는 값 포함 여부를 검사하여 있다면 예외를 던진다
	 * 
	 * @param attributeValue 속성 값
	 * @throws WhiteParserException 허용되지 않는 내용이 포함되었다면 던지는 예외  
	 */
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		if (null == attributeValue) {
			throw new IllegalArgumentException("the paramter attributeValue is null");
		}
		
		attributeWhiteValueChecker.throwExceptionIfNoWhiteValue(attributeValue);
	}
	
	/**
	 * '허락되지 않은 속성 값 검사기' 태그와 속성값이 불일치 할 경우 예외를 던진다
	 * 
	 * @throws WhiteParserException '속성 값에 대한 XSS 공격 검사기' 태그와 속성값이 불일치 할 경우 던지는 예외
	 */
	public void throwExceptionIfWhiteValueCheckerIsInvalidTagNameOrAttributeName() throws WhiteParserException {
		if (! tagName.equals(attributeWhiteValueChecker.getTagName())) {
			String errorMesssage = new StringBuilder()
					.append("the var tagName[")
					.append(tagName)
					.append("] is diffrent from the tagName[")
					.append(attributeWhiteValueChecker.getTagName())
					.append("] of Handler").toString();
			throw new WhiteParserException(errorMesssage);
		}
		
		if (! attributeName.equals(attributeWhiteValueChecker.getAttributeName())) {
			String errorMesssage = new StringBuilder()
					.append("the var attributeName[")
					.append(attributeName)
					.append("] is diffrent from the attributeName[")
					.append(attributeWhiteValueChecker.getAttributeName())
					.append("] of XSSAttackChecker").toString();
			throw new WhiteParserException(errorMesssage);
		}
	}
	
}
