package kr.pe.codda.weblib.summernote.whitevaluechecker;

import java.util.Base64;
import java.util.HashSet;

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

	private final String[] imageFileValidMimeTypeList = new String[] { "image/bmp", "image/png", "image/gif",
			"image/jpeg" };

	private final String[] imageFileExtList = new String[] { "bmp", "png", "gif", "jpg" };

	private final HashSet<String> imageFileExtSet = new HashSet<String>();

	public ImgTagSrcAtrrWhiteValueChecker() {
		for (String imageFileExt : imageFileExtList) {
			imageFileExtSet.add(imageFileExt);
		}
	}

	/**
	 * 이 메소드는 사용해서는 안되는 메소드이다. 파서에서는 이 클래스 객체를 얻어와서
	 * {@link #checkXSSAttack(String, BoardImageFileInformation)} 를 사용한다.
	 */
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		throw new WhiteParserException("이 메소드를 사용하지 마세요!");
	}

	/**
	 * 지정한 값이 XSS 공격 코드가 있다면 예외를 던진다. 이때 값에서 추출한 이미지 파일의 mime 종류와 이미지 파일 내용을 '게시글
	 * 이미지 파일 정보' 객체에 저장한다.
	 * 
	 * @param attributeValue            img 태그의 src 속성 값
	 * @param boardImageFileInformation 속성 값에서 추출된 '이미지 파일 mime 종류' 와 '이미지 파일 내용'을
	 *                                  저장할 '게시글 이미지 파일 정보' 객체
	 * @throws WhiteParserException xss 공격 코드가 있을 경우 혹은 처리중 에러가 있을 경우 던지는 예외
	 */
	public void checkXSSAttack(String attributeValue, BoardImageFileInformation boardImageFileInformation)
			throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();

			/*
			 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			 * log.warning(errorMessage); return false;
			 */
			throw new WhiteParserException(errorMessage);
		}

		int attributeValueLength = attributeValue.length();

		if (0 == attributeValueLength) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is empty").toString();

			/*
			 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			 * log.warning(errorMessage); return false;
			 */
			throw new WhiteParserException(errorMessage);
		}

		/**
		 * src 값은 3가지 경우가 있다.
		 * 
		 * 첫번재 : summernote 가 삽임한 이미지로 data:image 로 시작하는 베이스64로 삽입된 이미지, 두번째 : 게시판에서 본문
		 * 혹은 댓글 저장시 summernote 에서 삽입한 이미지 태그에 포함된 base64 데이터를 외부 파일 URL 로 변환한 결과 값, 예시)
		 * /servlet/DownloadImage?yyyyMMdd=20191229&amp;daySequence=19 세번째 :
		 * /img/[경로].[gif|jpg|png|bmp] 경로 밑에 있는 이미지에 대한 URL 주소
		 * 
		 */
		String firstChar = attributeValue.substring(0, 1);

		if (firstChar.equals("/")) {
			if (attributeValueLength < 10) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("]' length is less than 10 that is a min length").toString();

				throw new WhiteParserException(errorMessage);
			}

			String secondChar = attributeValue.substring(1, 2);

			if (secondChar.equals("i")) {
				// img 태그밑 이미지 URL

				String prefix = attributeValue.substring(0, 5);

				if (prefix.equals("/img/")) {

					if (attributeValueLength > 254) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("]'s value[")
								.append(attributeValue)
								.append("] is not a image file URL in a img directory becase it's length is greater than 254")
								.toString();

						throw new WhiteParserException(errorMessage);
					}

					String sepacator = attributeValue.substring(attributeValueLength - 4, attributeValueLength - 3);

					if (!sepacator.equals(".")) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("]'s value[")
								.append(attributeValue)
								.append("] is not a img directory image file URL becase it includes a disallowed charactor")
								.toString();

						throw new WhiteParserException(errorMessage);
					}

					String suffix = attributeValue.substring(attributeValueLength - 3);

					if (!imageFileExtSet.contains(suffix)) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("]'s value[")
								.append(attributeValue)
								.append("] is not a image file URL in a img directory becase it includes a disallowed charactor")
								.toString();

						throw new WhiteParserException(errorMessage);
					}

					char[] charArrayForAttributeValue = attributeValue.toCharArray();

					int len = charArrayForAttributeValue.length - 4;

					for (int i = 5; i < len; i++) {
						char ch = charArrayForAttributeValue[i];

						if ('.' == ch) {
							continue;
						} else if ('/' == ch) {
							continue;
						} else if (ch >= 'a' && ch <= 'z') {
							continue;
						} else if (ch >= 'A' && ch <= 'A') {
							continue;
						} else if (ch >= '0' && ch <= '9') {
							continue;
						} else {
							String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
									.append("]'s attribte[").append(attributeName).append("]'s value[")
									.append(attributeValue)
									.append("] is not a image file URL in a img directory becase it includes a disallowed charactor")
									.toString();

							throw new WhiteParserException(errorMessage);
						}
					}
				}

			} else {
				// /servlet/DownloadImage?yyyyMMdd=20191229&amp;daySequence=19
				if (attributeValueLength > 254) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
							.append("] is not a summernote image file URL becase its length is greater than 254")
							.toString();

					throw new WhiteParserException(errorMessage);
				}
				
				final String expectedPrefix = "/servlet/DownloadImage?yyyyMMdd=";
				final String expectdMiddle = "&daySequence=";
				
				
				int minLength = expectedPrefix.length() + 8 + expectdMiddle.length() + 1; 
				
				if (attributeValueLength < expectedPrefix.length() + 8 + expectdMiddle.length() + 1) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
							.append("] is not a summernote image file URL becase its length is greater than 254")
							.toString();

					throw new WhiteParserException(errorMessage);
				}
				

				String prefix = attributeValue.substring(0, expectedPrefix.length());

				if (!expectedPrefix.equals(prefix)) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
							.append("] is not a image file URL becase it includes a disallowed charactor").toString();

					throw new WhiteParserException(errorMessage);
				}
				
				String yyyyMMdd = attributeValue.substring(expectedPrefix.length(), expectedPrefix.length() + 8);
				
				String middle = attributeValue.substring(expectedPrefix.length() + 8, expectedPrefix.length() + 8 + expectdMiddle.length());
				
				if (!expectdMiddle.equals(middle)) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
							.append("] is not a image file URL becase it includes a disallowed charactor").toString();

					throw new WhiteParserException(errorMessage);
				}
				
				String daySequence = attributeValue.substring(minLength - 1);
			}

		} else {
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
							} catch (Exception e) {
								String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
										.append("]'s attribte[").append(attributeName).append("]'s value[")
										.append(attributeValue)
										.append("] is a dissallowed value becase it's base64 string is not valid")
										.toString();

								/*
								 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
								 * log.warning(errorMessage); return false;
								 */
								throw new WhiteParserException(errorMessage);
							}
						}
					}
				}
			}
		}

		String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
				.append(attributeName).append("]'s value[").append(attributeValue).append("] is not a base64 image")
				.toString();

		/*
		 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
		 * log.warning(errorMessage); return false;
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
