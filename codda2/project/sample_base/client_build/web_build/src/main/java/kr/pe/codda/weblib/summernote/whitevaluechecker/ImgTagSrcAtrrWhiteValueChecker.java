package kr.pe.codda.weblib.summernote.whitevaluechecker;

import java.util.HashSet;

import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;
import kr.pe.codda.weblib.summernote.BoardImageFileInformation;

/**
 * 허락된 img 태그의 src 속성 값 검사기
 * 
 * @author Won Jonghoon
 *
 */
public class ImgTagSrcAtrrWhiteValueChecker implements AttributeWhiteValueChekerIF {
	private final String tagName = "img";
	private final String attributeName = "src";

	/**
	 * WARNING! 이미지 파일 마임 타입 수정시 {@link #throwExceptionIfBase64ImageDataIsNotWhiteValue} 메소드 로직을 잘 살펴 보아야 한다. 왜냐하면 이미지 파일 마임 타입의 길이가 최소 9자이고 최대 문자수를 갖는 것으로 'image/jpeg' 로 가정하고 로직이 전개되었기때문이다. 
	 */
	@SuppressWarnings("serial")
	private final HashSet<String> imageFileMimeTypeSet = new HashSet<String>() {
		{

			String[] imageFileMimeTypeList = new String[] { "image/bmp", "image/png", "image/gif", "image/jpeg" };

			for (String imageFileMimeType : imageFileMimeTypeList) {
				add(imageFileMimeType);
			}
		}
	};

	@SuppressWarnings("serial")
	private final HashSet<String> imageFileExtSet = new HashSet<String>() {
		{

			String[] imageFileExtList = new String[] { "bmp", "png", "gif", "jpg" };

			for (String imageFileExt : imageFileExtList) {
				add(imageFileExt);
			}
		}
	};

	private final int MAX_LENGTH_OF_URL = 254;

	private final String firstPartOfImageURL = "/servlet/DownloadImage?yyyyMMdd=";
	private final String secondPartOfImageURL = "&daySequence=";

	private final int minLengthOfImageURL = firstPartOfImageURL.length() + 8 + secondPartOfImageURL.length() + 1;
	private final char[] charsOfFirstPartOfImageURL = firstPartOfImageURL.toCharArray();
	private final char[] charsOfSecondPartOfImageURL = secondPartOfImageURL.toCharArray();

	private final int MIN_LENGTH_OF_BASE64_DATA = "data:".length() + 1 + ";base64,".length() + 1;

	/**
	 * 이 메소드는 사용해서는 안되는 메소드이다. 파서에서는 이 클래스 객체를 얻어와서
	 * {@link #throwExceptionIfNoWhiteValue(String, BoardImageFileInformation)} 를
	 * 사용한다.
	 */
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		throw new WhiteParserException("이 메소드를 사용하지 마세요!");
	}

	/**
	 * 지정한 속성 값이 허락한 값이 아니면 예외를 던진다.
	 * 
	 * @param attributeValue            img 태그의 src 속성 값
	 * @param boardImageFileInformation 속성 값에서 추출된 '이미지 파일 mime 종류' 와 '이미지 파일 내용'을
	 *                                  저장할 '게시글 이미지 파일 정보' 객체
	 * @throws WhiteParserException 허락 받지 않는 문자가 발견된 경우 혹은 처리중 에러가 있을 경우 던지는 예외
	 */
	public void throwExceptionIfNoWhiteValue(String attributeValue, BoardImageFileInformation boardImageFileInformation)
			throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();
			throw new WhiteParserException(errorMessage);
		}

		int attributeValueLength = attributeValue.length();

		if (0 == attributeValueLength) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is empty").toString();
			throw new WhiteParserException(errorMessage);
		}
		
		if (null == boardImageFileInformation) {
			String errorMessage = "the paramter boardImageFileInformation is null";
			throw new WhiteParserException(errorMessage);
		}

		/**
		 * <pre>
		 * src 값은 3가지 경우가 있다.
		 * 
		 * 첫번재 : 베이스 64 이미지 데이터, 'data:<image file mime type>;base64,<base64 string>' 형태를 갖는 값
		 * 두번째 : 다운로드 이미지 파일 URL 값, 이 값은 베이스 64 이미지 데이터에서 이미지 파일 정보를 추출하여 다운 로드할 수 있는 외부 파일 URL 로 변환한 값이다. 
		 *        예시) /servlet/DownloadImage?yyyyMMdd=20191229&amp;daySequence=19 
		 * 세번째 : 웹 루트 밑에 이미지 경로에 있는 이미지 파일 URL 값, 예시) /img/howto/codda_helper_screenshot01.png
		 * </pre>
		 * 
		 */
		String firstChar = attributeValue.substring(0, 1);

		if (firstChar.equals("/")) {
			/**
			 * 첫번째 경우에서 최소 글자수는 22 자이고 두번째 경우에 최소 글자수는 55 이고 세번째 경우에 최소 글자수는 10 이다.
			 */
			if (attributeValueLength < 10) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("]' length is less than 10 that is a min length").toString();

				throw new WhiteParserException(errorMessage);
			}

			String secondChar = attributeValue.substring(1, 2);

			if (secondChar.equals("i")) {
				// img 태그밑 이미지 URL
				throwExceptionIfImageFileURLIsNotWhiteValue(attributeValue, attributeValueLength);
			} else {
				// /servlet/DownloadImage?yyyyMMdd=20191229&daySequence=19
				throwExceptionIfDownloadImageFileURLIsNotWhiteValue(attributeValue, attributeValueLength);
			}
		} else {
			throwExceptionIfBase64ImageDataIsNotWhiteValue(attributeValue, attributeValueLength, boardImageFileInformation);
		}
	}

	/**
	 * 웹 루트 밑에 이미지 경로에 있는 이미지 파일 URL 값이 화이트가 아니면 예외를 던진다.
	 * 
	 * @param attributeValue 웹 루트 밑에 이미지 경로에 있는 이미지 파일 URL 값
	 * @param attributeValueLength 웹 루트 밑에 이미지 경로에 있는 이미지 파일 URL 값의 길이
	 * @throws WhiteParserException 웹 루트 밑에 이미지 경로에 있는 이미지 파일 URL 값이 화이트가 아니면 던지는 예외
	 */
	private void throwExceptionIfImageFileURLIsNotWhiteValue(String attributeValue, int attributeValueLength)
			throws WhiteParserException {
		String prefix = attributeValue.substring(0, 5);

		if (prefix.equals("/img/")) {

			if (attributeValueLength > MAX_LENGTH_OF_URL) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is not a image file URL in a img directory becase it's length is greater than ")
						.append(MAX_LENGTH_OF_URL).toString();

				throw new WhiteParserException(errorMessage);
			}

			String sepacator = attributeValue.substring(attributeValueLength - 4, attributeValueLength - 3);

			if (!sepacator.equals(".")) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is not a img directory image file URL becase it includes a disallowed charactor")
						.toString();

				throw new WhiteParserException(errorMessage);
			}

			String suffix = attributeValue.substring(attributeValueLength - 3);

			if (!imageFileExtSet.contains(suffix)) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is not a image file URL becase it includes a disallowed charactor").toString();

				throw new WhiteParserException(errorMessage);
			}

			char[] charArrayForAttributeValue = attributeValue.toCharArray();

			int len = charArrayForAttributeValue.length - 4;

			for (int i = 5; i < len; i++) {
				char ch = charArrayForAttributeValue[i];
				
				if (('/' == ch) && ('/' != charArrayForAttributeValue[i - 1])) {
					continue;
				} else if ('_' == ch) {
					continue;
				} else if (ch >= 'a' && ch <= 'z') {
					continue;
				} else if (ch >= 'A' && ch <= 'A') {
					continue;
				} else if (ch >= '0' && ch <= '9') {
					continue;
				}

				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is not a image file URL in a img directory becase it includes a disallowed charactor")
						.toString();

				throw new WhiteParserException(errorMessage);
			}
		}
	}

	/**
	 * 다운로드 이미지 파일 URL 값이 화이트가 아니면 예외를 던진다.
	 * 
	 * @param attributeValue 다운로드 이미지 파일 URL 값
	 * @param attributeValueLength 다운로드 이미지 파일 URL 값의 길이
	 * @throws WhiteParserException 다운로드 이미지 파일 URL 값이 화이트가 아니면 던지는 예외
	 */
	private void throwExceptionIfDownloadImageFileURLIsNotWhiteValue(String attributeValue, int attributeValueLength)
			throws WhiteParserException {
		if (attributeValueLength > MAX_LENGTH_OF_URL) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value[").append(attributeValue)
					.append("] is not a download image file URL becase its length is greater than ")
					.append(MAX_LENGTH_OF_URL).toString();

			throw new WhiteParserException(errorMessage);
		}

		char[] charsOfImageURL = attributeValue.toCharArray();
		if (charsOfImageURL.length < minLengthOfImageURL) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value[").append(attributeValue)
					.append("] is a bad download image url becase its length is less than ").append(minLengthOfImageURL)
					.toString();

			throw new WhiteParserException(errorMessage);
		}

		int i = 0;

		for (; i < charsOfFirstPartOfImageURL.length; i++) {
			char expectedChar = charsOfFirstPartOfImageURL[i];
			char acutalChar = charsOfImageURL[i];

			if (expectedChar != acutalChar) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is a bad download image url becase acutal first part is diffrent from expected first part")
						.toString();

				throw new WhiteParserException(errorMessage);
			}
		}

		for (int j = 0; j < 8; j++, i++) {
			char ch = charsOfImageURL[i];

			if (! (ch >= '0' && ch <= '9')) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is a bad download image url becase it web parameter 'yyyyMMdd''s value is not a 8 digits")
						.toString();

				throw new WhiteParserException(errorMessage);
			}
		}

		for (int j = 0; j < charsOfSecondPartOfImageURL.length; j++, i++) {
			char expectedChar = charsOfSecondPartOfImageURL[j];
			char acutalChar = charsOfImageURL[i];

			if (expectedChar != acutalChar) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is a bad download image url becase auctal its second acutal part is diffrent from expected second part")
						.toString();

				throw new WhiteParserException(errorMessage);
			}
		}

		for (; i < charsOfImageURL.length; i++) {
			char acutalChar = charsOfImageURL[i];

			if (Character.compare(acutalChar, '0') < 0 || Character.compare(acutalChar, '9') > 0) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is a bad download image url becase it web parameter 'daySequence''s value is not a number")
						.toString();

				throw new WhiteParserException(errorMessage);
			}
		}
	}

	/**
	 * <pre>
	 * 베이스64 이미지 데이터 값이 화이트가 아니면 예외를 던지며 화이트인 경우 파라미터 'boardImageFileInformation' 에 파싱한 이미지 파일 정보를 저장한다. 
	 * 이미지 파일 정보는 서블릿에서 다운로드 할 수 있는 외부 파일 URL 로 바꾸는 작업에 사용된다.
	 * </pre>
	 * 
	 * @param attributeValue 베이스64 이미지 데이터 값
	 * @param attributeValueLength 베이스64 이미지 데이터 값의 길이
	 * @param boardImageFileInformation 이미지 파일 정보
	 * @throws WhiteParserException 베이스64 이미지 데이터 값이 화이트가 아니면 던지는 예외
	 */
	private void throwExceptionIfBase64ImageDataIsNotWhiteValue(String attributeValue, int attributeValueLength,
			BoardImageFileInformation boardImageFileInformation) throws WhiteParserException {
		// data:image/png;base64,
		if (attributeValueLength <= MIN_LENGTH_OF_BASE64_DATA) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
					.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
					.append("] is not a base64 image data becase its length is less than min[")
					.append(MIN_LENGTH_OF_BASE64_DATA)
					.append("]").toString();

			throw new WhiteParserException(errorMessage);
		}

		// System.out.println("222222222222222222");
		
		String firstPartString = attributeValue.substring(0, "data:image".length());
		
		if (! firstPartString.equals("data:image")) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
					.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
					.append("] is not a base64 image data becase its length is less than min[")
					.append(MIN_LENGTH_OF_BASE64_DATA)
					.append("]").toString();

			throw new WhiteParserException(errorMessage);
		}
		

		/**
		 * 이미지 파일 마임 타입의 최소 글자수는 9이며 최대 글자수를 갖는것은 'image/jpeg' 로 10글자이다. 
		 */
		
		String imageFileMimeType = attributeValue.substring("data:".length(), "data:".length() + 10);
		
		if (! imageFileMimeType.equals("image/jpeg")) {
			imageFileMimeType = imageFileMimeType.substring(0, 9);
			
			if (! imageFileMimeTypeSet.contains(imageFileMimeType)) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
						.append("] is not a base64 image data value becase it's image file mime type is not valid").toString();

				throw new WhiteParserException(errorMessage);
			}
		}

		String imageFileBase64String = attributeValue.substring("data:".length() + imageFileMimeType.length() + ";base64,".length());
		
		final byte[] imageFileContents;
		try {
			
			imageFileContents = CommonStaticUtil.Base64Decoder.decode(imageFileBase64String);		
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
					.append("]'s attribte[").append(attributeName).append("]'s value[").append(attributeValue)
					.append("] is not a base64 image data becase it's base64 string is not valid").toString();
			
			throw new WhiteParserException(errorMessage);
		}

		boardImageFileInformation.setBoardImageMimeType(imageFileMimeType);
		boardImageFileInformation.setBoardImageFileContents(imageFileContents);
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
