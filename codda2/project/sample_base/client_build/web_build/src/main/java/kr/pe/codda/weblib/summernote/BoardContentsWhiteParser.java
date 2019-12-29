package kr.pe.codda.weblib.summernote;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.UploadImageReq.UploadImageReq;
import kr.pe.codda.impl.message.UploadImageRes.UploadImageRes;
import kr.pe.codda.weblib.common.WebCommonStaticUtil;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.exception.WhiteParserException;

public class BoardContentsWhiteParser {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	
	/**
	 * 
	 * 
	 * @param installedPathString
	 * @param mainProjectName
	 * @param contents
	 * @return
	 * @throws IllegalArgumentException
	 * @throws WhiteParserException
	 */
	public String parseWithXssAttackCheck(String installedPathString, String mainProjectName, String contents) throws IllegalArgumentException, WhiteParserException {
		if (null == contents) {
			throw new IllegalArgumentException("the parameter contents is null");
		}
		
		Document contentsDocument = Jsoup.parse(contents);
		
		WhiteInformation whiteTagInformation = SampleBaseUserSiteWhiteInformationManager.getInstance();
		
		Element bodyElement = contentsDocument.body();
		
		Elements allElements = bodyElement.getAllElements();
		
		// ArrayList<BoardImageFileInformation> boardImageFileInformationList = new ArrayList<BoardImageFileInformation>();		
		
		int countOfElmements = allElements.size();
		
		
		for (int i=0; i < countOfElmements; i++) {
			
			Element element = allElements.get(i);
			
			String tagName = element.tagName();
			
			// FIXME!
			// log.info(tagName);
			
			
			
			if ("#root".equals(tagName)) {
				continue;
			}
						
			WhiteTag whiteTag = whiteTagInformation.getWhiteTag(tagName);
			
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
				
				BoardImageFileInformation boardImageFileInformation = new BoardImageFileInformation();
							
				ImgTagSrcValueXSSAtackChecker imgTagSrcValueXSSAtackChecker = (ImgTagSrcValueXSSAtackChecker)srcTagAttribute.getAttributeValueXSSAtackChecker();
				
				
				imgTagSrcValueXSSAtackChecker.checkXSSAttack(srcAttributeValue, boardImageFileInformation);				
				
				
				String dataFileNameAttributeValue = attributes.get("data-filename");
				if (null == dataFileNameAttributeValue || dataFileNameAttributeValue.isEmpty()) {
					String errorMessage = "img 태그에서 data-filename 속성이 없습니다";
					throw new WhiteParserException(errorMessage);
				}				
				
				WhiteTagAttribute dataFileNameTagAttribute = whiteTag.getWhiteTagAttribute("data-filename");
				
				ImgTagDataFileNameValueXSSAttackChecker imgTagDataFileNameValueXSSAttackChecker =  (ImgTagDataFileNameValueXSSAttackChecker)dataFileNameTagAttribute.getAttributeValueXSSAtackChecker();
				
				imgTagDataFileNameValueXSSAttackChecker.checkXSSAttack(dataFileNameAttributeValue, boardImageFileInformation);
				
				
				// FIXME!
				try {
					byte[] imageFileContents = boardImageFileInformation.getBoardImageFileContents();			
					
					UploadImageReq uploadImageReq = new UploadImageReq();
					// uploadImageReq.setRequestedUserID(accessedUserformation.getUserID());
					
					uploadImageReq.setImageFileName(boardImageFileInformation.getBoardImageFileName());
					uploadImageReq.setFileSize(imageFileContents.length);
					
					
					AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
							.getInstance().getMainProjectConnectionPool();
					AbstractMessage outputMessage = mainProjectConnectionPool
							.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), uploadImageReq);

					if (!(outputMessage instanceof UploadImageRes)) {
						if (outputMessage instanceof MessageResultRes) {
							MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
							String errorMessage = "게시판 상세 조회가 실패하였습니다";
							String debugMessage = messageResultRes.toString();
							throw new WebClientException(errorMessage, debugMessage);
						} else {
							String errorMessage = "게시판 상세 조회가 실패했습니다";
							String debugMessage = new StringBuilder("입력 메시지[").append(uploadImageReq.getMessageID())
									.append("]에 대한 비 정상 출력 메시지[").append(outputMessage.toString()).append("] 도착").toString();

							log.severe(debugMessage);

							throw new WebClientException(errorMessage, debugMessage);
						}
					}

					UploadImageRes uploadImageRes = (UploadImageRes) outputMessage;
					
					String uploadImageFilePathString = WebCommonStaticUtil.buildUploadImageFilePathString(
							installedPathString, mainProjectName, uploadImageRes.getYyyyMMdd(),
							uploadImageRes.getDaySequence());
					File newUploadImageFile = new File(uploadImageFilePathString);
					
					FileOutputStream fos = new FileOutputStream(newUploadImageFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					try {				
						bos.write(imageFileContents);				
					} finally {
						if (null != bos) {
							try {
								bos.close();
							} catch(Exception e) {
								
							}
						}
						
						if (null != fos) {
							try {
								fos.close();
							} catch(Exception e) {
								
							}
						}
					}
					
					String newImgTagSrcAttributeString = new StringBuilder()
							.append("/servlet/DownloadImage?yyyyMMdd=")
							.append(uploadImageRes.getYyyyMMdd())
							.append("&daySequence=")
							.append(uploadImageRes.getDaySequence()).toString();
					
					
					attributes.put("src", newImgTagSrcAttributeString);
					attributes.remove("data-filename");
					
				} catch(Exception e) {
					String errorMessage = "fail to replace old image tag having base64 to new image tag having image file url";
					log.log(Level.WARNING, errorMessage, e);
					throw new WhiteParserException(errorMessage);
					
				}
				
				// boardImageFileInformationList.add(boardImageFileInformation);
			}
			
							
			for (org.jsoup.nodes.Attribute att : attributes.asList()) {
				String attributeName = att.getKey();
				
				
				/** img tag 의 src 와 dafa-filename 속성들은 앞에서 XSS 공격 여부를 판단했기때문에 이 과정을 건너 뛴다 */
				if ("img".equals(tagName)) {
					if ("src".equals(attributeName)) {
						
						continue;
					}				
					if ("data-filename".equals(attributeName)) {
						continue;
					}
				}
				
				WhiteTagAttribute whiteTagAttribute = whiteTag.getWhiteTagAttribute(attributeName);
				whiteTagAttribute.checkXSSAttack(att.getValue());	
			}
		}
		
		
		
		
		String newConents = bodyElement.html();
		
		/*
		int beginIndex = "<body>".length();
		int endIndex = newConents.length() - "</body>".length() + 1;
		
		log.info("beginIndex="+beginIndex);
		log.info("endIndex="+endIndex);
		*/
		
		// newConents = newConents.substring(beginIndex, endIndex);;
		
		return newConents;
	}
}
