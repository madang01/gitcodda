package kr.pe.codda.weblib.summernote;

import java.util.HashMap;

import kr.pe.codda.weblib.exception.WhiteParserException;

/**
 * 화이트 리스트 클래스로 내부적으로는 허락된 태그({@link WhiteTag})를 해쉬로 관리한다. 
 * 
 * @author Won jonghoon
 *
 */
public abstract class WhiteList {
	private HashMap<String, WhiteTag> whiltTagHash = new HashMap<String, WhiteTag>();
	
	/**
	 * 허락한 태그를 등록한다
	 * 
	 * @param tagName 태그 이름
	 * @throws IllegalArgumentException 태그 이름이 null 이면 던지는 예외
	 */
	public void addTag(String tagName) throws IllegalArgumentException {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		whiltTagHash.put(tagName, new WhiteTag(tagName));
	}
	
	/**
	 * 다수의 허락한 태그들을 등록한다
	 * 
	 * @param tagNames 다수의 허락한 태그들
	 * @throws IllegalArgumentException 허락한 태그 이름들이  null 이면 던지는 예외
	 */
	public void addTags(String ... tagNames) throws IllegalArgumentException {
		if (null == tagNames) {
			throw new IllegalArgumentException("the paramter tagNames is null");
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

	/**
	 * 
	 * @param tagName
	 * @param attributeName
	 * @param attributeWhiteValueChecker
	 * @throws IllegalArgumentException
	 * @throws WhiteParserException
	 */
	public void addAttribute(String tagName, String attributeName, AttributeWhiteValueChekerIF attributeWhiteValueChecker) throws IllegalArgumentException, WhiteParserException {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		if (null == attributeWhiteValueChecker) {
			throw new IllegalArgumentException("the paramter attributeWhiteValueChecker is null");
		}
		
		WhiteTag whiteTag = whiltTagHash.get(tagName);
		
		if (null == whiteTag) {
			String errorMesssage = new StringBuilder()
					.append("the parameter tagName[")
					.append(tagName)
					.append("] is a disallowed tag").toString();
			throw new WhiteParserException(errorMesssage);
		}
		
		whiteTag.add(attributeName, attributeWhiteValueChecker);
	
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
