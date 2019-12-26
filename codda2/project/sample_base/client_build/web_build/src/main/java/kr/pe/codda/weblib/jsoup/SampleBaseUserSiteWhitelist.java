package kr.pe.codda.weblib.jsoup;

import java.util.logging.Logger;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.weblib.summernote.SummerNoteConfiguration;
import kr.pe.codda.weblib.summernote.SummerNoteConfigurationManger;

/**
 * WARNING! this class is not thread safety
 * 
 * 출처 :
 * https://stackoverflow.com/questions/31044850/cause-of-error-jsoup-isvalid
 * 
 * @author : Stephan
 * @modifier : Won Jonghoon
 *
 */
public class SampleBaseUserSiteWhitelist extends Whitelist {

	// private String firstCausesOfError = null;

	private final String firstPartOfImageURL = "/servlet/DownloadImage?yyyyMMdd=";
	private final String secondPartOfImageURL = "&daySequence=";

	private final int minLengthOfImageURL = firstPartOfImageURL.length() + 8 + secondPartOfImageURL.length() + 1;
	private final char[] charsOfFirstPartOfImageURL = firstPartOfImageURL.toCharArray();
	private final char[] charsOfSecondPartOfImageURL = secondPartOfImageURL.toCharArray();

	private final String prefixOfRGB = "rgb(";
	private final char[] charArrayOfPrefixOfRGB = prefixOfRGB.toCharArray();

	private final char separactorOfRGB = ',';
	private final char suffixOfRGB = ')';

	/**
	 * 주의점 1) Jsoup.base()를 기본으로 함 주의점 2) jsou 자체로 body tag 를 넣기때문에 기본에 없기때문에 반듯이 추가
	 * 필요함
	 */
	public SampleBaseUserSiteWhitelist() {
		super();

		this.addTags("a", "b", "blockquote", "br", "cite", "code", "dd", "dl", "dt", "em", "i", "li", "ol", "p", "pre",
				"q", "small", "span", "strike", "strong", "sub", "sup", "u", "ul") //

				.addAttributes("a", "href") //
				.addAttributes("blockquote", "cite") //
				.addAttributes("q", "cite") //

				.addProtocols("a", "href", "ftp", "http", "https", "mailto") //
				.addProtocols("blockquote", "cite", "http", "https") //
				.addProtocols("cite", "cite", "http", "https") //

				.addEnforcedAttribute("a", "rel", "nofollow") //

				// ** Customizations
				.addTags("body", "h1", "h2", "h3", "h4", "h5", "h6", "font", "img", "table", "tbody", "tr", "td")
				.addAttributes("span", "style").addAttributes("li", "style").addAttributes("img", "style")
				.addAttributes("img", "src").addAttributes("font", "face").addAttributes("table", "class")
				.removeProtocols("a", "href", "ftp");
		// class="table table-bordered"
	}

	@Override
	protected boolean isSafeTag(String tagName) {
		boolean isSafe = super.isSafeTag(tagName);

		if (!isSafe) {
			String errorMessage = new StringBuilder().append("the tag[").append(tagName).append("] is a disallowed tag")
					.toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);

			return isSafe;
		}

		return isSafe;
	}

	/**
	 * @param valueOfAttribute 'spag 태그의 style 속성의 값', 
	 * WARNING! 이 값은 ';' 을 분리자로 키와 값으로 구별된다. 단 값 예를 들면 폰트 이름의 경우 공백을 포함한 문자열인데 따옴표('"') 로 감싸서 하나의 값임을 표현한다. 
	 * 하여 이 값은 URL 인코딩을 되어  저장된다, 이때 URL 인코딩의 영향으로 따옴표('"')는'&quot;' 로 변환하여 저장되어 있다. 
	 * 문제는 이것이 구별자와 충돌이 나기때문에 구별하기 전에 URL decode 를 먼저 해 주어야 한다.
	 * 이때 전체를 URL 디코딩하지 않고 URL 인코딩의 영향으로 변환되는것이 따옴표 뿐이므로 단순하게 '&quot;' 을 따옴표('"') 로 문자열 치환한다.
	 *  
	 * @return 지정한 'spag 태그의 style 속성의 값'에 악성 코드 포함 여부
	 */
	private boolean spanTagStyle(String valueOfAttribute) {
		final String dobloeQuotation = "\""; 
		valueOfAttribute = valueOfAttribute.replace("&quot;", dobloeQuotation);
		
		final String tagName = "span";
		final String keyOfAttribute = "style";

		String[] items = valueOfAttribute.split(";");

		if (null == items || 0 == items.length) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(keyOfAttribute).append("] is a bad style becase the var items is null or empty").toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);

			return false;
		}

		for (String item : items) {
			String[] itemSet = item.split(":");

			if (null == itemSet || 0 == itemSet.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
						.append(valueOfAttribute).append("] becase the var itemSet of the var item[").append(item)
						.append("] is null or empty").toString();

				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);

				return false;
			}

			if (2 != itemSet.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
						.append(valueOfAttribute).append("] becase the var itemSet of the var item[").append(item)
						.append("] is not a set of key and value").toString();

				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);

				return false;
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
				return true;
			}

			if ("font-style".equals(key) && "italic".equals(value)) {
				// font-style: italic;
				return true;
			}

			if ("font-family".equals(key)) {
				// font-family: &quot;Arial Black&quot;;
				
				SummerNoteConfiguration summerNoteConfiguration = SummerNoteConfigurationManger.getInstance();
				
				if (! summerNoteConfiguration.isFontFamaily()) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute).append("] becase 'font-family' is not allowed to the SummerNote Cconfiguration").toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);
					
					return false;
				}
				
				String[] fontNameList = summerNoteConfiguration.getFontNameList();
				
				
				for (String fontName : fontNameList) {
					if (fontName.indexOf(" ") > 0) {
						String newFontName = new StringBuilder().append(dobloeQuotation).append(fontName).append(dobloeQuotation)
						.toString();
						
						if (newFontName.equals(value)) {
							return true;
						}
					} else if (fontName.equals(value)) {
						return true;
					}
				}				

				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
						.append(valueOfAttribute).append("] becase the var key[").append(key).append("]'s value[")
						.append(value).append("] is a disallowed value").toString();

				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);

				return false;

			}

			if ("text-decoration-line".equals(key)) {
				// text-decoration-line: underline line-through;
				if ("underline".equals(value)) {
					// OK
					return true;
				}

				if ("line-through".equals(value)) {
					// OK
					return true;
				}

				String[] elements = value.split(" ");

				if (null == elements || 2 != elements.length) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute)
							.append("] becase the var elements is null or its length is not two").toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
				}

				if ("underline".equals(elements[0]) && "line-through".equals(elements[1])) {
					return true;
				}

				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
						.append(valueOfAttribute).append("] becase  it is not 'underline line-through'").toString();

				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);
				return false;

			}
			
			
			if ("background-color".equals(key) || "color".equals(key)) {
				int i = 0;

				char[] charArrayOfValue = value.toCharArray();

				if (charArrayOfValue.length < 10) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("]'s length is less than 10").toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
				}

				if (charArrayOfValue[charArrayOfValue.length - 1] != suffixOfRGB) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("]'s last chracter is not a '").append(suffixOfRGB).append("'")
							.toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
				}

				for (; i < charArrayOfPrefixOfRGB.length; i++) {
					if (charArrayOfPrefixOfRGB[i] != charArrayOfValue[i]) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
								.append(valueOfAttribute).append("] becase the var key[").append(key)
								.append("]'s value[").append(value).append("] is bad becase it doen't begin 'rgb('")
								.toString();

						Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
						log.warning(errorMessage);

						return false;
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
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase it doesn't have rgb").toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
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
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase it is not rgb").toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
				}

				if (red < 0 && red > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase red[").append(red)
							.append("] is less than zero or greater than 255").toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
				}

				if (green < 0 && green > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase green[").append(green)
							.append("] is less than zero or greater than 255").toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
				}

				if (black < 0 && black > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
							.append(valueOfAttribute).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase black[").append(black)
							.append("] is less than zero or greater than 255").toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					return false;
				}

				// background-color: rgb(0,0,0);
				return true;
			}
			
			
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
					.append("]'s attribte[").append(keyOfAttribute).append("] has a bad value[")
					.append(valueOfAttribute).append("] becase the var itemSet of the var item[").append(item)
					.append("] is a disallowed value").toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);

			return false;
			
		}

		return true;
	}

	@Override
	protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
		boolean isSafe = super.isSafeAttribute(tagName, el, attr);

		if (!isSafe) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attr.getKey()).append("] is a disallowed attribute or bad protocol").toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);

			return isSafe;
		}

		if ("span".equals(tagName) && "style".equals(attr.getKey())) {
			// FIXME!		

			isSafe = spanTagStyle(attr.getValue());

			// } else if ("li".equals(tagName) && "style".equals(attr.getKey())) {
			// FIXME!
		} else if ("img".equals(tagName) && "style".equals(attr.getKey())) {
			// FIXME!
		} else if ("img".equals(tagName) && "src".equals(attr.getKey())) {
			String imageURL = attr.getValue();
			char[] charsOfImageURL = imageURL.toCharArray();
			if (charsOfImageURL.length < minLengthOfImageURL) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attr.getKey()).append("]'s value[").append(attr.getValue())
						.append("] is a bad image url becase its length is less than ").append(minLengthOfImageURL)
						.toString();

				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);

				isSafe = false;

				return isSafe;
			}

			int i = 0;

			for (; i < charsOfFirstPartOfImageURL.length; i++) {
				char expectedChar = charsOfFirstPartOfImageURL[i];
				char acutalChar = charsOfImageURL[i];

				if (expectedChar != acutalChar) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attr.getKey()).append("]'s value[").append(attr.getValue())
							.append("] is a bad image url becase auctal its first acutal part is diffrent from expected first part")
							.toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					isSafe = false;

					return isSafe;
				}
			}

			for (int j = 0; j < 8; j++, i++) {
				char acutalChar = charsOfImageURL[i];

				if (Character.compare(acutalChar, '0') < 0 || Character.compare(acutalChar, '9') > 0) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attr.getKey()).append("]'s value[").append(attr.getValue())
							.append("] is a bad image url becase it web parameter 'yyyyMMdd''s value is not a 8 digits")
							.toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					isSafe = false;

					return isSafe;
				}
			}

			for (int j = 0; j < charsOfSecondPartOfImageURL.length; j++, i++) {
				char expectedChar = charsOfSecondPartOfImageURL[j];
				char acutalChar = charsOfImageURL[i];

				if (expectedChar != acutalChar) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attr.getKey()).append("]'s value[").append(attr.getValue())
							.append("] is a bad image url becase auctal its second acutal part is diffrent from expected second part")
							.toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					isSafe = false;

					return isSafe;
				}
			}

			for (; i < charsOfImageURL.length; i++) {
				char acutalChar = charsOfImageURL[i];

				if (Character.compare(acutalChar, '0') < 0 || Character.compare(acutalChar, '9') > 0) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attr.getKey()).append("]'s value[").append(attr.getValue())
							.append("] is a bad image url becase it web parameter 'daySequence''s value is not a number")
							.toString();

					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);

					isSafe = false;

					return isSafe;
				}
			}
		} else if ("a".equals(tagName) && "href".equals(attr.getKey())) {
			String url = attr.getValue();
			if (!UrlValidator.getInstance().isValid(url)) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attr.getKey()).append("]'s value[").append(attr.getValue())
						.append("] is a bad url").toString();

				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);

				isSafe = false;

				return isSafe;
			}
		} else if ("font".equals(tagName) && "face".equals(attr.getKey())) {
			// FIXME!
		} else if ("table".equals(tagName) && "table table-bordered".equals(attr.getKey())) {
			// class="table table-bordered"
		} else {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attr.getKey()).append("]'s value[").append(attr.getValue())
					.append("] is a disallowed value").toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);

			isSafe = false;
		}

		return isSafe;
	}

}