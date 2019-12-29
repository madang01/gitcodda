package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

public class SpanTagStyleValueXSSAttackChecker implements AttributeValueXSSAttackChekerIF {
	private final String tagName = "span";
	private final String attributeName = "style";
	
	private final String prefixOfRGB = "rgb(";
	private final char[] charArrayOfPrefixOfRGB = prefixOfRGB.toCharArray();

	private final char separactorOfRGB = ',';
	private final char suffixOfRGB = ')';
	
	private final String dobloeQuotation = "\"";

	
	/**
	 * @param attributeValue 'spag 태그의 style 속성의 값', WARNING! 이 값은 ';' 을 분리자로 키와
	 *                         값으로 구별된다. 단 값 예를 들면 폰트 이름의 경우 공백을 포함한 문자열인데 따옴표('"')
	 *                         로 감싸서 하나의 값임을 표현한다. 하여 이 값은 URL 인코딩을 되어 저장된다, 이때 URL
	 *                         인코딩의 영향으로 따옴표('"')는'&quot;' 로 변환하여 저장되어 있다. 문제는 이것이
	 *                         구별자와 충돌이 나기때문에 구별하기 전에 URL decode 를 먼저 해 주어야 한다. 이때
	 *                         전체를 URL 디코딩하지 않고 URL 인코딩의 영향으로 변환되는것이 따옴표 뿐이므로 단순하게
	 *                         '&quot;' 을 따옴표('"') 로 문자열 치환한다.
	 * 
	 * @return 지정한 'spag 태그의 style 속성의 값'에 악성 코드 포함 여부
	 */
	@Override
	public void checkXSSAttack(String attributeValue) throws WhiteParserException {
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
		
		attributeValue = attributeValue.replace("&quot;", dobloeQuotation);

		

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

		for (String item : items) {
			item = item.trim();
			
			String[] itemSet = item.split(":");

			if (null == itemSet || 0 == itemSet.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var itemSet of the var item[").append(item)
						.append("] is null or empty").toString();

				/*
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);

				return false;
				*/
				throw new WhiteParserException(errorMessage);
			}

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

				if (!summerNoteConfiguration.isFontNames()) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue)
							.append("] becase 'font-family' is not allowed to the SummerNote Cconfiguration")
							.toString();

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
					throw new WhiteParserException(errorMessage);
				}

				String[] fontNameList = summerNoteConfiguration.getFontNameList();

				for (String fontName : fontNameList) {
					if (fontName.indexOf(" ") > 0) {
						String newFontName = new StringBuilder().append(dobloeQuotation).append(fontName)
								.append(dobloeQuotation).toString();

						if (newFontName.equals(value)) {
							continue;
						}
						
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("] has a bad value[")
								.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
								.append(value).append("] is a disallowed value").toString();

						/*
						Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
						log.warning(errorMessage);

						return false;
						*/
						throw new WhiteParserException(errorMessage);
					} 
					
					if (fontName.equals(value)) {
						continue;
					}
					
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is a disallowed value").toString();

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
					throw new WhiteParserException(errorMessage);
				}
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
							.append(attributeValue)
							.append("] becase the var elements is null or its length is not two").toString();

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
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
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);
				return false;
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

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
					throw new WhiteParserException(errorMessage);
				}

				if (charArrayOfValue[charArrayOfValue.length - 1] != suffixOfRGB) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("]'s last chracter is not a '").append(suffixOfRGB).append("'")
							.toString();

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
					throw new WhiteParserException(errorMessage);
				}

				for (; i < charArrayOfPrefixOfRGB.length; i++) {
					if (charArrayOfPrefixOfRGB[i] != charArrayOfValue[i]) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("] has a bad value[")
								.append(attributeValue).append("] becase the var key[").append(key)
								.append("]'s value[").append(value).append("] is bad becase it doen't begin 'rgb('")
								.toString();
						/*
						Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
						log.warning(errorMessage);

						return false;
						*/
						
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

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
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

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
					throw new WhiteParserException(errorMessage);
				}

				if (red < 0 && red > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase red[").append(red)
							.append("] is less than zero or greater than 255").toString();

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
					throw new WhiteParserException(errorMessage);
				}

				if (green < 0 && green > 255) {					
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase green[").append(green)
							.append("] is less than zero or greater than 255").toString();

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
					throw new WhiteParserException(errorMessage);
				}

				if (black < 0 && black > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase black[").append(black)
							.append("] is less than zero or greater than 255").toString();

					/*
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
					*/
					
					throw new WhiteParserException(errorMessage);
				}

				// background-color: rgb(0,0,0);
				continue;
			}

			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] has a bad value[").append(attributeValue)
					.append("] becase the var itemSet of the var item[").append(item).append("] is a disallowed value")
					.toString();

			/*
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);

			return false;
			*/
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
