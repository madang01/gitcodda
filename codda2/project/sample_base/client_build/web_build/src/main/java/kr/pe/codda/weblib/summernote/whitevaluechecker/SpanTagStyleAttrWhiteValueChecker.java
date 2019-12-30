package kr.pe.codda.weblib.summernote.whitevaluechecker;

import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;
import kr.pe.codda.weblib.summernote.SummerNoteConfiguration;
import kr.pe.codda.weblib.summernote.SummerNoteConfigurationManger;

public class SpanTagStyleAttrWhiteValueChecker implements AttributeWhiteValueChekerIF {
	// Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private final String tagName = "span";
	private final String attributeName = "style";

	private final String prefixOfRGB = "rgb(";
	private final char[] charArrayOfPrefixOfRGB = prefixOfRGB.toCharArray();

	private final char separactorOfRGB = ',';
	private final char suffixOfRGB = ')';

	// private final String dobloeQuotation = "\"";
	
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
		
		String[] itemList = attributeValue.split(";");

		if (null == itemList || 0 == itemList.length) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] is a bad style becase the var items is null or empty").toString();

			throw new WhiteParserException(errorMessage);
		}

		for (String item : itemList) {
			item = item.trim();

			String[] property = item.split(":");

			if (null == property || 0 == property.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var itemSet of the var item[").append(item)
						.append("] is null or empty").toString();

				throw new WhiteParserException(errorMessage);
			}

			if (2 != property.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var itemSet of the var item[").append(item)
						.append("] is not a set of key and value").toString();

				throw new WhiteParserException(errorMessage);
			}

			String key = property[0].trim();
			String value = property[1].trim();

			/**
			 * 허용된 값 : font-weight: bold; font-style: italic; text-decoration-line:
			 * underline line-through; background-color: rgb(0, 0, 0);
			 */
			if ("font-weight".equals(key) && "bold".equals(value)) {
				// font-weight: bold;
				// OK
				continue;
			}

			if ("font-style".equals(key) && "italic".equals(value)) {
				// font-style: italic;
				continue;
			}

			if ("font-family".equals(key)) {
				// font-family: &quot;Arial Black&quot;;

				SummerNoteConfiguration summerNoteConfiguration = SummerNoteConfigurationManger.getInstance();

				if (summerNoteConfiguration.isNoFontFamily()) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue)
							.append("] becase 'font-family' is not allowed to the SummerNote Cconfiguration")
							.toString();

					throw new WhiteParserException(errorMessage);
				}
				
				/**
				 * 폰트 이름은 2개 이상의 문자열로 구성될 경우 따옴표('"')로 감싸여 있으므로 폰트 이름 비교할때에는 제거가 필요하다   
				 */
				value = value.replace("\"", "");
				
				boolean isFontName = summerNoteConfiguration.isFontName(value);

				if (isFontName) {
					continue;
				}

				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
						.append(value).append("] is a disallowed value").toString();

				throw new WhiteParserException(errorMessage);
			}

			if ("text-decoration-line".equals(key)) {
				// text-decoration-line: underline line-through;
				if ("underline".equals(value)) {
					// OK
					continue;
				}

				if ("line-through".equals(value)) {
					// OK
					continue;
				}

				String[] elements = value.split(" ");

				if (null == elements || 2 != elements.length) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var elements is null or its length is not two")
							.toString();

					/*
					 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					 * log.warning(errorMessage);
					 * 
					 * return false;
					 */

					throw new WhiteParserException(errorMessage);
				}

				if ("underline".equals(elements[0]) && "line-through".equals(elements[1])) {
					continue;
				}

				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase  it is not 'underline line-through'").toString();

				/*
				 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				 * log.warning(errorMessage); return false;
				 */
				throw new WhiteParserException(errorMessage);

			}

			if ("background-color".equals(key) || "color".equals(key)) {
				int i = 0;

				char[] charArrayOfValue = value.toCharArray();

				if (charArrayOfValue.length < 10) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("]'s length is less than 10").toString();

					throw new WhiteParserException(errorMessage);
				}

				if (charArrayOfValue[charArrayOfValue.length - 1] != suffixOfRGB) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("]'s last chracter is not a '").append(suffixOfRGB).append("'")
							.toString();

					throw new WhiteParserException(errorMessage);
				}

				for (; i < charArrayOfPrefixOfRGB.length; i++) {
					if (charArrayOfPrefixOfRGB[i] != charArrayOfValue[i]) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("] has a bad value[")
								.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
								.append(value).append("] is bad becase it doen't begin 'rgb('").toString();

						throw new WhiteParserException(errorMessage);
					}
				}

				String redString = null;
				String greenString = null;
				String blackString = null;
				StringBuilder colorStringBuilder = new StringBuilder();

				for (; i < charArrayOfValue.length; i++) {
					char ch = charArrayOfValue[i];
					if (ch == separactorOfRGB) {
						if (null == redString) {
							redString = colorStringBuilder.toString();
							colorStringBuilder.setLength(0);
						} else {
							greenString = colorStringBuilder.toString();
							colorStringBuilder.setLength(0);
						}
					} else if (ch == suffixOfRGB) {
						if (null != greenString) {
							blackString = colorStringBuilder.toString();
						}
						break;
					} else {
						colorStringBuilder.append(ch);
					}
				}

				if (null == redString || null == greenString || null == blackString) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase it doesn't have rgb").toString();

					throw new WhiteParserException(errorMessage);
				}

				redString = redString.trim();
				greenString = greenString.trim();
				blackString = blackString.trim();

				int red = -1;
				int green = -1;
				int black = -1;

				try {
					red = Integer.parseInt(redString);
					green = Integer.parseInt(greenString);
					black = Integer.parseInt(blackString);
				} catch (NumberFormatException e) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase it is not rgb").toString();

					throw new WhiteParserException(errorMessage);
				}

				if (red < 0 && red > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase red[").append(red)
							.append("] is less than zero or greater than 255").toString();

					throw new WhiteParserException(errorMessage);
				}

				if (green < 0 && green > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase green[").append(green)
							.append("] is less than zero or greater than 255").toString();

					throw new WhiteParserException(errorMessage);
				}

				if (black < 0 && black > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase black[").append(black)
							.append("] is less than zero or greater than 255").toString();

					throw new WhiteParserException(errorMessage);
				}

				// background-color: rgb(0,0,0);
				continue;
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
