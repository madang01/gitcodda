package kr.pe.codda.weblib.summernote;

import java.util.HashMap;

import kr.pe.codda.weblib.exception.WhiteParserException;

public class WhiteTag {
	private final String tagName;
	private HashMap<String, WhiteTagAttribute> whiteAttributeHash = new HashMap<String, WhiteTagAttribute>();
	private boolean isAttribute = false;
	
	// private final static WhiteAttributeValueHandlerIF noCheckWhiteAttributeValueHandler = new NoCheckWhiteAttributeHandler();
	
	public WhiteTag(String tagName) {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		this.tagName = tagName;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	/*
	public void add(String attributeName) {
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		
		whiteAttributeSet.put(attributeName, new WhiteTagAttribute(tagName, attributeName, noCheckWhiteAttributeValueHandler));
		
		isAttribute = true;
	}
	*/
	
	
	public void add(String attributeName, AttributeValueXSSAttackChekerIF attributeValueXSSAtackChecker) throws IllegalArgumentException, WhiteParserException {
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		if (null == attributeValueXSSAtackChecker) {
			throw new IllegalArgumentException("the paramter attributeValueXSSAtackChecker is null");
		}
		
		WhiteTagAttribute whiteTagAttribute = new WhiteTagAttribute(tagName, attributeName, attributeValueXSSAtackChecker);
		
		whiteAttributeHash.put(attributeName, whiteTagAttribute);
		
		isAttribute = true;
	}
	
	public WhiteTagAttribute getWhiteTagAttribute(String attributeName) throws WhiteParserException {
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		WhiteTagAttribute whiteTagAttribute = whiteAttributeHash.get(attributeName);
		
		if (null == whiteTagAttribute) {
			String errorMesssage = new StringBuilder()
					.append("tag[")
					.append(tagName)
					.append("]'s attribute[")
					.append(attributeName)
					.append("] is a disallowed attribute").toString();
			throw new WhiteParserException(errorMesssage);
		}		
		
		return whiteTagAttribute;
	}
	
	public boolean isAttribute() {
		return isAttribute;
	}
}
