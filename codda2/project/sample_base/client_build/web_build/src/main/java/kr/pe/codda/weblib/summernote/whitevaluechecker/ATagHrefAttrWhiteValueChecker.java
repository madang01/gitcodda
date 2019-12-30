package kr.pe.codda.weblib.summernote.whitevaluechecker;

import org.apache.commons.validator.routines.UrlValidator;

import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;

public class ATagHrefAttrWhiteValueChecker implements AttributeWhiteValueChekerIF {	
	private final String tagName = "a";
	private final String attributeName = "href";
	
	private final String[] whilteProtocolList = new String[] {
		"http", "https"
	};
		
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();

			// Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			// log.warning(errorMessage);
			// return false;
			throw new WhiteParserException(errorMessage);
		}
		
		
		
		if ("".equals(attributeValue)) {
			return;
		}

		
		boolean isValid = UrlValidator.getInstance().isValid(attributeValue);		

		if (! isValid) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value[").append(attributeValue)
					.append("] is not a valid url").toString();

			// Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			// log.warning(errorMessage);
			throw new WhiteParserException(errorMessage);
		}
		
		for (String whileProtocal : whilteProtocolList) {
			if (0 == attributeValue.indexOf(whileProtocal)) {
				return;
			}
		}
		
		String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
				.append(attributeName).append("]'s value[").append(attributeValue)
				.append("] is a invalid url").toString();

		// Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
		// log.warning(errorMessage);
		
		throw new WhiteParserException(errorMessage);	}

	@Override
	public String getTagName() {
		return tagName;
	}

	@Override
	public String getAttributeName() {
		return attributeName;
	}

}
