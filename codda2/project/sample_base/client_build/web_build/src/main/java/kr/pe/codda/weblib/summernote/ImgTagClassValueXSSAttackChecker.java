package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

public class ImgTagClassValueXSSAttackChecker implements AttributeValueXSSAttackChekerIF {	
	private final String tagName = "img";
	private final String attributeName = "class";
	
	@Override
	public void checkXSSAttack(String attributeValue) throws WhiteParserException{
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

		if ("table table-bordered".equals(attributeValue)) {
			return;
		}

		String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
				.append(attributeName).append("]'s value[").append(attributeValue)
				.append("] is a disallowed value").toString();

		/*
		Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
		log.warning(errorMessage);

		return false;
		*/
		throw new WhiteParserException(errorMessage);
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
