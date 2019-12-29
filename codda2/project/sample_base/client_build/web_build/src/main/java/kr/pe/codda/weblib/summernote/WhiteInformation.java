package kr.pe.codda.weblib.summernote;

import java.util.HashMap;

import kr.pe.codda.weblib.exception.WhiteParserException;

public class WhiteInformation {
	private HashMap<String, WhiteTag> whiltTagHash = new HashMap<String, WhiteTag>();
	
	public void addTag(String tagName) throws IllegalArgumentException {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		whiltTagHash.put(tagName, new WhiteTag(tagName));
	}
	
	
	public void addTags(String ... tagNames) throws IllegalArgumentException {
		if (null == tagNames) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		for (int i=0; i < tagNames.length; i++) {
			String tagName = tagNames[i];
			if (null == tagName) {
				String errorMessage = new StringBuilder("the parameter tagNames[index=")
						.append(i)
						.append("] is null").toString();
				
				throw new IllegalArgumentException(errorMessage);
			}
			
			addTag(tagName);
		}
	}

	/*
	public void addAttribute(String tagName, String attributeName) throws IllegalArgumentException, WhitePageException {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		WhiteTag whiteTag = whiltTagHash.get(tagName);
		
		if (null == whiteTag) {
			String errorMesssage = new StringBuilder()
					.append("the parameter tagName[")
					.append(tagName)
					.append("] is not a white tag").toString();
			throw new WhitePageException(errorMesssage);
		}
		
		whiteTag.add(attributeName);
	}
	*/
	
	public void addAttribute(String tagName, String attributeName, AttributeValueXSSAttackChekerIF attributeValueXSSAtackChecker) throws IllegalArgumentException, WhiteParserException {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		if (null == attributeValueXSSAtackChecker) {
			throw new IllegalArgumentException("the paramter attributeValueXSSAtackChecker is null");
		}
		
		WhiteTag whiteTag = whiltTagHash.get(tagName);
		
		if (null == whiteTag) {
			String errorMesssage = new StringBuilder()
					.append("the parameter tagName[")
					.append(tagName)
					.append("] is a disallowed tag").toString();
			throw new WhiteParserException(errorMesssage);
		}
		
		whiteTag.add(attributeName, attributeValueXSSAtackChecker);
	
	}
	
	public WhiteTag getWhiteTag(String tagName) throws WhiteParserException {
		WhiteTag whiteTag = whiltTagHash.get(tagName);
		
		if (null == whiteTag) {
			String errorMesssage = new StringBuilder()
					.append("the parameter tagName[")
					.append(tagName)
					.append("] is a disallowed tag").toString();
			
			throw new WhiteParserException(errorMesssage);
		}
		
		return whiteTag;
	}
	
	
}
