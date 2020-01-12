package kr.pe.codda.weblib.summernote.whitevaluechecker;

import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;

/**
 * 허락된 p 태그의 style 속성 값 검사기
 * 
 * @author Won Jonghoon
 *
 */
public class PTagStyleWhiteValueChecker implements AttributeWhiteValueChekerIF {
	private final String tagName = "p";
	private final String attributeName = "style";
	
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();

			/*
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);
			return false;
			*/
			throw new WhiteParserException(errorMessage);
		}
		
		if ("".equals(attributeValue)) {
			return;
		}
		
		attributeValue = attributeValue.trim();

		String[] items = attributeValue.split(";");

		if (null == items || 0 == items.length) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] is a bad style becase the var items is null or empty").toString();

			/*
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);

			return false;
			*/
			throw new WhiteParserException(errorMessage);
		}

		// text-align: right;
		for (String item : items) {
			
			String[] itemSet = item.split(":");

			if (2 != itemSet.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var itemSet of the var item[").append(item)
						.append("] is not a set of key and value").toString();

				/*
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);

				return false;
				*/
				throw new WhiteParserException(errorMessage);
			}

			String key = itemSet[0].trim();
			String value = itemSet[1].trim();
			
			// line-height: 1.42857;
			if ("line-height".equals(key)) {
				try {
					
					Double nativeValue = Double.parseDouble(value);
					
					if (nativeValue.isInfinite() || nativeValue.isNaN()) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("] has a bad value[")
								.append(attributeValue).append("] becase line-height[").append(value)
								.append("] is a double type valid  value").toString();
						
						throw new WhiteParserException(errorMessage);
					}
					
					continue;
				} catch(NumberFormatException e) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase line-height[").append(value)
							.append("] is a double type value").toString();
					
					throw new WhiteParserException(errorMessage);
				}
			}
			
			
			// text-align: center;
			if ("text-align".equals(key)) {
				if ("left".equals(value) || "center".equals(value) || "right".equals(value)
						|| "justify".equals(value)) {
					continue;
				}
			}

			if ("margin-top".equals(key) || "margin-bottom".equals(key) || "margin-left".equals(key) || "margin-right".equals(key)) {
				int len = value.length();
				if (value.length() < 3) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase number of its character is less than minum 3").toString();
					throw new WhiteParserException(errorMessage);
				}
				
				String prefix = value.substring(0,  len - 2);
				String suffix = value.substring(len - 2);
				
				if (! "px".equals(suffix)) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase its suffix is not 'px'").toString();
					throw new WhiteParserException(errorMessage);
				}
 				
				try {
					Integer.parseInt(prefix);

					continue;
				} catch (NumberFormatException e) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase its prefix is not number").toString();
					throw new WhiteParserException(errorMessage);
				}
			}

			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] has a bad value[").append(attributeValue)
					.append("] becase the var itemSet of the var item[").append(item).append("] is a disallowed value")
					.toString();
			
			throw new WhiteParserException(errorMessage);
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
