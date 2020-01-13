package kr.pe.codda.weblib.summernote.whitevaluechecker;

import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;

/**
 * 허락된 a 태그의 href 속성 값 검사기
 * 
 * @author Won Jonghoon
 *
 */
public class ATagHrefAttrWhiteValueChecker implements AttributeWhiteValueChekerIF {
	private final String tagName = "a";
	private final String attributeName = "href";

	private final char[] whiteSpecalCharsOfURL = { ':', '/', '?', '#', '=', '&', '.', '_', '-', '\'', '%', '~' };

	private final String[] schemes = new String[] { "http", "https" };

	private final int MIN_LENGTH_OF_HOST_NAME = 4;

	/**
	 * @param sourceChar 검사 대상 문자
	 * @return URL 에 허용된 특수 문자 여부
	 */
	private boolean isWhiteSpecalCharsOfURL(char sourceChar) {
		for (char targetChar : whiteSpecalCharsOfURL) {
			if (sourceChar == targetChar) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();
			throw new WhiteParserException(errorMessage);
		}

		if ("".equals(attributeValue)) {
			return;
		}

		char[] charArrayOfAttributeValue = attributeValue.toCharArray();

		int startIndexOfValue = 0;

		if ('/' != charArrayOfAttributeValue[0]) {
			String key = null;
			for (int i = 0; i < charArrayOfAttributeValue.length; i++) {
				char ch = charArrayOfAttributeValue[i];

				if (':' == ch) {

					if (i == 0) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("]'s value[")
								.append(attributeValue)
								.append("] is a valid url string because URL schema is a empty string").toString();
						throw new WhiteParserException(errorMessage);
					}

					if (i >= (charArrayOfAttributeValue.length - 1 - MIN_LENGTH_OF_HOST_NAME)) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("]'s value[")
								.append(attributeValue).append("] is a valid url string because host name is too short")
								.toString();
						throw new WhiteParserException(errorMessage);
					}

					key = new String(charArrayOfAttributeValue, 0, i);
					startIndexOfValue = i + 1;
					break;
				}
			}

			if (null == key) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is a valid url string because it does not have a separactor charactor ':'")
						.toString();
				throw new WhiteParserException(errorMessage);
			}

			boolean isInvalidURLSchema = true;
			for (String urlSchema : schemes) {
				if (urlSchema.equals(key)) {
					isInvalidURLSchema = false;
					break;
				}
			}

			if (isInvalidURLSchema) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is a valid url string because it does have a bad URL scema")
						.toString();
				throw new WhiteParserException(errorMessage);
			}
		}

		for (int i = startIndexOfValue; i < charArrayOfAttributeValue.length; i++) {
			char ch = charArrayOfAttributeValue[i];
			
/*********************** <' 문자 막기 위한 작업 시작 ************************************************/
			/**
			 * 'XSS Filter Evasion Cheat Sheet#Character escape sequences' 참고 주소 : https://www.owasp.org/index.php/XSS_Filter_Evasion_Cheat_Sheet
			 * 
			 * 금지 문자 '<' 와 금지 문자 '<' 를 만들 수 있는 역슬래쉬('\\') 문자 금지  
			 */
			if (('<' == ch) || ('\\' == ch)) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] has a disallowed characater, maybe '<'or '\\'").toString();
				throw new WhiteParserException(errorMessage);
			}
			
			/**
			 * 대소문자 구별 없이 '%3c' 문자열 불허
			 */
			if ('c' == Character.toLowerCase(ch)) {
				if (('3' == Character.toLowerCase(charArrayOfAttributeValue[i - 1])) && ('%' == charArrayOfAttributeValue[i - 2])) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
							.append("] has a disallowed characater, it has a disallowed string '&3c'").toString();
					throw new WhiteParserException(errorMessage);
				}
			}

			/**
			 * '&#' 문자열 불허
			 */
			if ('#' == ch) {				
				if ('&' == charArrayOfAttributeValue[i - 1]) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
							.append("] has a disallowed characater, it has a disallowed string '&#'").toString();
					throw new WhiteParserException(errorMessage);
				}
			}

			/**
			 * 대소 문자 구별 없이 '&lt' 문자열 불허
			 */
			if ('t' == Character.toLowerCase(ch)) {				
				if (('l' == Character.toLowerCase(charArrayOfAttributeValue[i - 1])) 
						&& ('&' == charArrayOfAttributeValue[i - 2])) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
							.append("] has a disallowed characater, it has a disallowed string '&lt'").toString();
					throw new WhiteParserException(errorMessage);
				}
			}
/*********************** <' 문자 막기 위한 작업 종료 ************************************************/
			
			
			if (!isWhiteSpecalCharsOfURL(ch) && !Character.isDigit(ch) && !CommonStaticUtil.isEnglish(ch)) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] has a disallowed characater, it does not a valid url string").toString();
				throw new WhiteParserException(errorMessage);
			}
		}
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
