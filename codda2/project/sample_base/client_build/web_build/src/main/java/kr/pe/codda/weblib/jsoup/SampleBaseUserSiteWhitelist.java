package kr.pe.codda.weblib.jsoup;

import java.util.logging.Logger;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

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
				.addTags("body", "h1", "h2", "h3", "h4", "h5", "h6", "font", "img") // jsop 가 body tag 자동으로 넣기때문에 화이트에 추가함
				.addAttributes("span", "style").addAttributes("font", "face").addAttributes("li", "style")
				.addAttributes("img", "src").addAttributes("img", "style").removeProtocols("a", "href", "ftp");
	}

	@Override
	protected boolean isSafeTag(String tagName) {
		boolean isSafe = super.isSafeTag(tagName);
		
		if (!isSafe) {
			String errorMessage = new StringBuilder()
					.append("the tag[")
					.append(tagName)
					.append("] is a disallowed tag").toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);
			
			return isSafe;
		}

		return isSafe;
	}

	@Override
	protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
		boolean isSafe = super.isSafeAttribute(tagName, el, attr);

		if (!isSafe) {
			String errorMessage = new StringBuilder()
					.append("the tag name[")
					.append(tagName)
					.append("]'s attribte[")
					.append(attr.getKey())
					.append("] is a disallowed attribute or bad protocol").toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);
			
			return isSafe;
		}

		if ("img".equals(tagName) && "src".equals(attr.getKey())) {
			String imageURL = attr.getValue();
			char[] charsOfImageURL = imageURL.toCharArray();
			if (charsOfImageURL.length < minLengthOfImageURL) {
				String errorMessage = new StringBuilder()
						.append("the tag name[")
						.append(tagName)
						.append("]'s attribte[")
						.append(attr.getKey())
						.append("]'s value[")
						.append(attr.getValue())
						.append("] is a bad image url becase its length is less than ")
						.append(minLengthOfImageURL).toString();
				
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);
				
				isSafe = false;

				return isSafe;
			}
			
			int i=0;

			for (; i < charsOfFirstPartOfImageURL.length; i++) {
				char expectedChar = charsOfFirstPartOfImageURL[i];
				char acutalChar = charsOfImageURL[i];

				if (expectedChar != acutalChar) {					
					String errorMessage = new StringBuilder()
							.append("the tag name[")
							.append(tagName)
							.append("]'s attribte[")
							.append(attr.getKey())
							.append("]'s value[")
							.append(attr.getValue())
							.append("] is a bad image url becase auctal its first acutal part is diffrent from expected first part")
							.toString();
					
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);
					
					isSafe = false;

					return isSafe;
				}
			}
			
			for (int j=0; j < 8; j++, i++) {
				char acutalChar = charsOfImageURL[i];
				
				if (Character.compare(acutalChar, '0') < 0 || Character.compare(acutalChar, '9') > 0) {
					String errorMessage = new StringBuilder()
							.append("the tag name[")
							.append(tagName)
							.append("]'s attribte[")
							.append(attr.getKey())
							.append("]'s value[")
							.append(attr.getValue())
							.append("] is a bad image url becase it web parameter 'yyyyMMdd''s value is not a 8 digits")
							.toString();
					
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);
					
					isSafe = false;

					return isSafe;
				}
			}
			
			for (int j=0; j < charsOfSecondPartOfImageURL.length; j++, i++) {
				char expectedChar = charsOfSecondPartOfImageURL[j];
				char acutalChar = charsOfImageURL[i];				
				
				if (expectedChar != acutalChar) {					
					String errorMessage = new StringBuilder()
							.append("the tag name[")
							.append(tagName)
							.append("]'s attribte[")
							.append(attr.getKey())
							.append("]'s value[")
							.append(attr.getValue())
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
					String errorMessage = new StringBuilder()
							.append("the tag name[")
							.append(tagName)
							.append("]'s attribte[")
							.append(attr.getKey())
							.append("]'s value[")
							.append(attr.getValue())
							.append("] is a bad image url becase it web parameter 'daySequence''s value is not a number")
							.toString();
					
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.warning(errorMessage);
					
					isSafe = false;

					return isSafe;
				}
			}
		} else if ("a".equals(tagName) && "href".equals(attr.getKey())) {
			// FIXME!
			String url = attr.getValue();
			if (! UrlValidator.getInstance().isValid(url)) {
				String errorMessage = new StringBuilder()
						.append("the tag name[")
						.append(tagName)
						.append("]'s attribte[")
						.append(attr.getKey())
						.append("]'s value[")
						.append(attr.getValue())
						.append("] is a bad url")
						.toString();
				
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.warning(errorMessage);
				
				
				isSafe = false;

				return isSafe;
			}
		}

		return isSafe;
	}

}