package kr.pe.codda.weblib.summernote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagDataFileNameAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagSrcAtrrWhiteValueChecker;

public class BoardContentsWhiteParser {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
		
	/**주어진 '게시글 내용' 에 허락되지 않는 내용이 포함되었는지 검사를 수행하여 만약에 허락되지 않은 내용이 포함되었다면 예외를 던지고 그렇지 않고 허락된 내용만이라면  base64인 이미지 태그의 src 속성값을  주어진 '이미지 파일 URL 반환자'를 통해 얻은 값으로 치환한 게시글를 반환한다.
	 * 
	 * @param imageFileURLGetter 이미지 파일 URL 문자열 반환자    
	 * @param boardImageFileInformationList 게시글 이미지 파일 정보 목록
	 * @param contents 이미지 태그의 src 속성값이 base64 인 게시글
	 * @return base64인 이미지 태그의 src 속성값을  주어진 '이미지 파일 URL 반환자'를 통해 얻은 값으로 치환한 게시글
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을때 던지는 예외
	 * @throws WhiteParserException 처리중 에러가 발생할 경우 혹은 허락되지 않은 내용이 포함되었을 경우 던지는 예외
	 */
	public String checkWhiteValue(ImageFileURLGetterIF imageFileURLGetter, List<BoardImageFileInformation> boardImageFileInformationList, String contents) throws IllegalArgumentException, WhiteParserException {
		if (null == imageFileURLGetter) {
			throw new IllegalArgumentException("the parameter imageFileURLGetter is null");
		}
		
		if (null == contents) {
			throw new IllegalArgumentException("the parameter contents is null");
		}
		
		Document contentsDocument = null;
		
		try {
			contentsDocument = Jsoup.parse(contents);
		} catch(Exception e) {
			String errorMessage = new StringBuilder().toString();
			
			log.log(Level.WARNING, errorMessage, e);
			
			throw new WhiteParserException(errorMessage);
		}
		
		StringBuilder newContnetStringBuilder = new StringBuilder();
		int fromIndex = 0;
		
		
		WhiteList whiteList = SampleBaseUserSiteWhiteListManager.getInstance();
		
		Element bodyElement = contentsDocument.body();
		
		Elements allElements = bodyElement.getAllElements();
		
		int countOfElmements = allElements.size();
		
		for (int i=0; i < countOfElmements; i++) {
			
			Element element = allElements.get(i);
			
			String tagName = element.tagName();
			
			
			if ("#root".equals(tagName)) {
				continue;
			}
						
			WhiteTag whiteTag = whiteList.getWhiteTag(tagName);
			
			org.jsoup.nodes.Attributes attributes = element.attributes();
			
			if ("img".equals(tagName)) {
				String srcAttributeValue = attributes.get("src");
				if (null == srcAttributeValue || srcAttributeValue.isEmpty()) {
					String errorMessage = "img 태그에서 src 속성이 없습니다";
					throw new WhiteParserException(errorMessage);
				}
				
				WhiteTagAttribute srcTagAttribute = whiteTag.getWhiteTagAttribute("src");
				
				if (null == srcTagAttribute) {
					String errorMessage = "the img tag's src atttibute is a not allowed attbitue";
					throw new WhiteParserException(errorMessage);
				}
				
				
							
				ImgTagSrcAtrrWhiteValueChecker imgTagSrcAtrrWhiteValueChecker = (ImgTagSrcAtrrWhiteValueChecker)srcTagAttribute.getAttributeWhiteValueCheker();
				
				BoardImageFileInformation boardImageFileInformation =  new BoardImageFileInformation();
				
				imgTagSrcAtrrWhiteValueChecker.throwExceptionIfNoWhiteValue(srcAttributeValue, boardImageFileInformation);
				
				if (null != boardImageFileInformation.getBoardImageMimeType()) {
					String dataFileNameAttributeValue = attributes.get("data-filename");
					if (null == dataFileNameAttributeValue || dataFileNameAttributeValue.isEmpty()) {
						String errorMessage = "img 태그에서 data-filename 속성이 없습니다";
						throw new WhiteParserException(errorMessage);
					}				
					
					WhiteTagAttribute dataFileNameTagAttribute = whiteTag.getWhiteTagAttribute("data-filename");
					
					ImgTagDataFileNameAttrWhiteValueChecker imgTagDataFileNameAttrWhiteValueChecker =  (ImgTagDataFileNameAttrWhiteValueChecker)dataFileNameTagAttribute.getAttributeWhiteValueCheker();
					
					imgTagDataFileNameAttrWhiteValueChecker.throwExceptionIfNoWhiteValue(dataFileNameAttributeValue, boardImageFileInformation);
					
					
					String acutalContentType = null;
					
					try {
						acutalContentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(boardImageFileInformation.getBoardImageFileContents()));
					} catch(IOException e) {
						String errorMessage = new StringBuilder("입출력 에러가 발생하여 게시글에 포함된 이미지 파일의 내용 종류를 파악하는데 실패하였습니다").toString();
						throw new WhiteParserException(errorMessage);
					}
					
					if (null == acutalContentType) {
						String errorMessage = new StringBuilder("게시글에 포함된 이미지 파일의 내용 종류를 파악하는데 실패하였습니다").toString();
						throw new WhiteParserException(errorMessage);
					} 

					if (! acutalContentType.equals(boardImageFileInformation.getBoardImageMimeType())) {
						String errorMessage = new StringBuilder("게시글에 포함된 이미지 파일의 내용 종류[")
								.append(acutalContentType)
								.append("]가 이미지 태그에서 지정한 파일 내용 종류[")
								.append(boardImageFileInformation.getBoardImageMimeType())
								.append("]가 일치 하지 않습니다").toString();
						throw new WhiteParserException(errorMessage);
					}
					
					
					String newImgTagSrcAttributeString = imageFileURLGetter.getImageFileURL(boardImageFileInformation);
					
					attributes.put("src", newImgTagSrcAttributeString);
					attributes.remove("data-filename");
					
					
					/**
					 * WARNING! 속성 변경 후 새롭게 갱신되 내용으로 반환해 주는 Element#outerHtml 를 함부로 다른 메소드로 바꾸지 말것, 만약 이것을 Element#html 로 바꾸면 오동작함
					 */
					String newImgTagString = element.outerHtml();
					
					// log.info("[" + newImgTagString + "]");
					
					int beginIndex = contents.indexOf("<img", fromIndex);
					int endIndex = contents.indexOf(">", beginIndex + 10 + srcAttributeValue.length() + dataFileNameAttributeValue.length()) + 1;
					
					/*
					String tmp = contents.substring(beginIndex, endIndex);
					log.info("[" + tmp + "]");
					*/
					newContnetStringBuilder.append(contents.substring(fromIndex, beginIndex));
					newContnetStringBuilder.append(newImgTagString);
					
					fromIndex = endIndex;
					
					boardImageFileInformationList.add(boardImageFileInformation);
				} else {
					int beginIndex = contents.indexOf("<img", fromIndex);
					int endIndex = contents.indexOf(">", beginIndex + 10) + 1;
					
					String imageTagHavingURL = contents.substring(fromIndex, endIndex);					
					
					newContnetStringBuilder.append(imageTagHavingURL);
					
					fromIndex = endIndex;
				}
			}
			
							
			for (org.jsoup.nodes.Attribute att : attributes.asList()) {
				String attributeName = att.getKey();
				
				
				/** img tag 의 src 와 dafa-filename 속성은 앞에서 허락 받지 않은 문자 포함시 예외 처리를 했기 때문에 이 과정을 건너 뛴다 */
				if ("img".equals(tagName)) {
					if ("src".equals(attributeName)) {
						
						continue;
					}				
					if ("data-filename".equals(attributeName)) {
						continue;
					}
				}
				
				WhiteTagAttribute whiteTagAttribute = whiteTag.getWhiteTagAttribute(attributeName);
				whiteTagAttribute.throwExceptionIfNoWhiteValue(att.getValue());	
			}
		}		
		
		newContnetStringBuilder.append(contents.substring(fromIndex));
		
		String newConents = newContnetStringBuilder.toString();
		
		
		return newConents;
	}
}
