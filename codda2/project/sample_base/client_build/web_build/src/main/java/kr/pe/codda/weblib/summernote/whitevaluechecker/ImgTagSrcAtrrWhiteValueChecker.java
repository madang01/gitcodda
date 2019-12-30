package kr.pe.codda.weblib.summernote.whitevaluechecker;

import java.util.Base64;

import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;
import kr.pe.codda.weblib.summernote.BoardImageFileInformation;

/**
 * <pre>
 * img 태그에는 src 속성 값에 xss attack 여부 검사는 파서에서 직접 하여 유효성 검사를 목적으로 하는 필요 없지만 속성 등록시 핸들러 등록을 반듯이 해야 하기때문에  
 * </pre>  
 * 
 * @author Won Jonghoon
 *
 */
public class ImgTagSrcAtrrWhiteValueChecker implements AttributeWhiteValueChekerIF {	
	private final String tagName = "img";
	private final String attributeName = "src";
	
	// private String imageFileMimeType = null;
	// private byte[] imageFileContents = null;
	
	private final String[] imageFileValidMimeTypeList = new String[] {						
			"image/bmp",
			"image/png",
			"image/gif",
			"image/jpeg"
		};
	
	
	/**
	 * 이 메소드는 사용해서는 안되는 메소드이다. 파서에서는 이 클래스 객체를 얻어와서 {@link #checkXSSAttack(String, BoardImageFileInformation)} 를 사용한다. 
	 */
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {		
		throw new WhiteParserException("이 메소드를 사용하지 마세요!");
	}	
	
	/**
	 * 지정한 값이 XSS 공격 코드가 있다면 예외를 던진다. 이때 값에서 추출한 이미지 파일의 mime 종류와 이미지 파일 내용을 '게시글 이미지 파일 정보' 객체에 저장한다. 
	 * 
	 * @param attributeValue img 태그의 src 속성 값
	 * @param boardImageFileInformation 속성 값에서 추출된 '이미지 파일 mime 종류' 와 '이미지 파일 내용'을 저장할 '게시글 이미지 파일 정보' 객체
	 * @throws WhiteParserException xss 공격 코드가 있을 경우 혹은 처리중 에러가 있을 경우 던지는 예외
	 */
	public void checkXSSAttack(String attributeValue, BoardImageFileInformation boardImageFileInformation) throws WhiteParserException {
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
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is empty").toString();

			/*
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.warning(errorMessage);
			return false;
			*/
			throw new WhiteParserException(errorMessage);
		}
		
		if (0 == attributeValue.indexOf("data:image")) {
			// System.out.println("111111111111111111");
			
			if (attributeValue.length() > ("data:".length() + 1 + ";base64,".length() + 1)) {
				// System.out.println("222222222222222222");
				
				int inx = attributeValue.indexOf(";base64,", "data:".length());
				
				// System.out.println("333333:inx="+inx);
				
				if (inx > 0) {
					String imageFileMimeType = attributeValue.substring("data:".length(), inx);
					String imageFileBase64String = attributeValue.substring(inx + ";base64,".length());
					
					// System.out.println("4444444444444:imageFileMimeType="+imageFileMimeType);
					// System.out.println("5555555:imageFileBase64String="+imageFileBase64String);
					
					
					for (String imageFileValidMimeType : imageFileValidMimeTypeList) {
						if (imageFileValidMimeType.equals(imageFileMimeType)) {
							
							try {
								byte[] imageFileContents = Base64.getDecoder().decode(imageFileBase64String);

								boardImageFileInformation.setBoardImageMimeType(imageFileMimeType);
								boardImageFileInformation.setBoardImageFileContents(imageFileContents);
								
								return;
							} catch(Exception e) {
								String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
										.append(attributeName).append("]'s value[")
										.append(attributeValue)
										.append("] is a dissallowed value becase it's base64 string is not valid").toString();

								/*
								Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
								log.warning(errorMessage);
								return false;
								*/
								throw new WhiteParserException(errorMessage);
							}
						}
					}
				}
			}
		}
		
		
		String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
				.append(attributeName).append("]'s value[")
				.append(attributeValue)
				.append("] is not a base64 image").toString();

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
