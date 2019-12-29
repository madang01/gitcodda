package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

public class ImgTagSytleXSSAttackChecker implements AttributeValueXSSAttackChekerIF {
	private final String tagName = "img";
	private final String attributeName = "style";

	@Override
	public void checkXSSAttack(String attributeValue) throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();

			/*
			 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			 * log.warning(errorMessage); return false;
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
			 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			 * log.warning(errorMessage);
			 * 
			 * return false;
			 */
			throw new WhiteParserException(errorMessage);
		}

		for (String item : items) {
			item = item.trim();
			
			String[] itemSet = item.split(":");

			if (2 != itemSet.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var itemSet of the var item[").append(item)
						.append("] is not a set of key and value").toString();

				/*
				 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				 * log.warning(errorMessage);
				 * 
				 * return false;
				 */
				throw new WhiteParserException(errorMessage);
			}

			String key = itemSet[0].trim();
			String value = itemSet[1].trim();

			// style="width: 1086px;"
			// float: right;
			if ("width".equals(key)) {
				if ("50%".equals(value) || "25%".equals(value)) {
					continue;
				}

				int inx = value.lastIndexOf("px");
				if (inx > 0) {
					String widthString = value.substring(0, inx);

					try {
						Integer.parseInt(widthString);

						continue;
					} catch (NumberFormatException e) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("] has a bad value[")
								.append(attributeValue).append("] becase width[").append(value)
								.append("] is a disallowed value").toString();

						/*
						 * 
						 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
						 * log.warning(errorMessage);
						 * 
						 * return false;
						 */
						throw new WhiteParserException(errorMessage);
					}
				}
			}

			if ("float".equals(key)) {
				if ("right;".equals(value) || "left;".equals(value)) {
					continue;
				}

				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase float[").append(value)
						.append("] is a disallowed value").toString();

				/*
				 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				 * log.warning(errorMessage);
				 * 
				 * return false;
				 */

				throw new WhiteParserException(errorMessage);
			}
			
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] has a bad value[").append(attributeValue)
					.append("] becase it is a disallowed value").toString();
			
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
