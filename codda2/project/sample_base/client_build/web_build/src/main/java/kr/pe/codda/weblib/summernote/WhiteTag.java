package kr.pe.codda.weblib.summernote;

import java.util.HashMap;

import kr.pe.codda.weblib.exception.WhiteParserException;


/**
 * 화이트 리스트({@link WhiteList})에 등록된 태그로 허락된 속성({@link WhiteTagAttribute}) 을 해쉬에 담아 관리한다
 * 
 * @author Won Jonghoon
 *
 */
public class WhiteTag {
	private final String tagName;
	private HashMap<String, WhiteTagAttribute> whiteAttributeHash = new HashMap<String, WhiteTagAttribute>();
	// private boolean isAttribute = false;
	
	// private final static WhiteAttributeValueHandlerIF noCheckWhiteAttributeValueHandler = new NoCheckWhiteAttributeHandler();
	
	/**
	 * 
	 * @param tagName 태그 이름
	 * @throws IllegalArgumentException 지정한 태그 이름이 null 인 경우 던지는 예외
	 */
	public WhiteTag(String tagName) throws IllegalArgumentException {
		if (null == tagName) {
			throw new IllegalArgumentException("the paramter tagName is null");
		}
		
		this.tagName = tagName;
	}
	
	/**
	 * @return 태그 이름
	 */
	public String getTagName() {
		return tagName;
	}
	
	/**
	 * 허락된 속성을 등록한다
	 * 
	 * @param attributeName 허락된 속성 이름
	 * @param attributeWhiteValueChecker 속성 값이 허락한 값인지 여부 검사기
	 * @throws IllegalArgumentException 속성 이름이  null 인 경우 혹은 '속성 값에 대한 XSS 공격 검사기' 가 null 인 경우 던지는 예외
	 * @throws WhiteParserException '속성 값에 대한 XSS 공격 검사기' 의 태그와 속성값이 불일치 할 경우 던지는 예외
	 */
	public void add(String attributeName, AttributeWhiteValueChekerIF attributeWhiteValueChecker) throws IllegalArgumentException, WhiteParserException {
		if (null == attributeName) {
			throw new IllegalArgumentException("the paramter attributeName is null");
		}
		
		if (null == attributeWhiteValueChecker) {
			throw new IllegalArgumentException("the paramter attributeWhiteValueChecker is null");
		}
		
		WhiteTagAttribute whiteTagAttribute = new WhiteTagAttribute(tagName, attributeName, attributeWhiteValueChecker);
		
		whiteAttributeHash.put(attributeName, whiteTagAttribute);
	}
	
	/**
	 * 
	 * @param attributeName 속성 이름
	 * @return 지정한 속성이 없다면 예외를 던지고 있다면 이 태그에 허락된 속성 객체를 반환한다
	 * @throws WhiteParserException 지정한 속성이 없다면 던지는 예외 
	 */
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
	
	/**
	 * @return 허락된 속성이 없으면 참(=true)을 반환하고 있다면 거짓(=false)을 반환한다
	 */
	public boolean isNoAttribute() {
		return whiteAttributeHash.isEmpty();
	}
}
