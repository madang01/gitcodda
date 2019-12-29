package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

public class WhiteTagAttribute {
	private final String tagName;
	private final String attributeName;
	private final AttributeValueXSSAttackChekerIF attributeValueXSSAtackChecker;
	
	public WhiteTagAttribute(String tagName, String attributeName, AttributeValueXSSAttackChekerIF attributeValueXSSAtackChecker) throws WhiteParserException {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		if (null == attributeValueXSSAtackChecker) {
			throw new IllegalArgumentException("the paramter attributeValueXSSAtackChecker is null");
		}
		
		
		this.tagName = tagName;
		this.attributeName = attributeName;
		this.attributeValueXSSAtackChecker = attributeValueXSSAtackChecker;
		
		throwExceptionIfHandlerIsNotValid();
	}
	
	
	public AttributeValueXSSAttackChekerIF getAttributeValueXSSAtackChecker() {
		return attributeValueXSSAtackChecker;
	}
	
	public void checkXSSAttack(String attributeValue) throws WhiteParserException {
		if (null == attributeValue) {
			throw new IllegalArgumentException("the paramter attributeValue is null");
		}
		
		attributeValueXSSAtackChecker.checkXSSAttack(attributeValue);
	}
	
	public void throwExceptionIfHandlerIsNotValid() throws WhiteParserException {
		if (! tagName.equals(attributeValueXSSAtackChecker.getTagName())) {
			String errorMesssage = new StringBuilder()
					.append("the var tagName[")
					.append(tagName)
					.append("] is diffrent from the tagName[")
					.append(attributeValueXSSAtackChecker.getTagName())
					.append("] of Handler").toString();
			throw new WhiteParserException(errorMesssage);
		}
		
		if (! attributeName.equals(attributeValueXSSAtackChecker.getAttributeName())) {
			String errorMesssage = new StringBuilder()
					.append("the var attributeName[")
					.append(attributeName)
					.append("] is diffrent from the attributeName[")
					.append(attributeValueXSSAtackChecker.getAttributeName())
					.append("] of XSSAttackChecker").toString();
			throw new WhiteParserException(errorMesssage);
		}
	}
	
}
