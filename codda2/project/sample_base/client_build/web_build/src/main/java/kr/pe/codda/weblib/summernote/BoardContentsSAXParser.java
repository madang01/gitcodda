package kr.pe.codda.weblib.summernote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagDataFileNameAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagSrcAtrrWhiteValueChecker;

public class BoardContentsSAXParser extends DefaultHandler {
	private final Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	/**
	 * 시작 태그와 끝 태그 짝 맞춤 확인용 시작 태그들을 담을 스택
	 */
	private Stack<String> tagStack = new Stack<String>();

	private ArrayList<BoardImageFileInformation> boardImageFileInformationList = new ArrayList<BoardImageFileInformation>();
	
	private WhiteList whiteTagInformation = null; 

	/**
	 * <pre>
	 * 참고 사이트 : https://html.spec.whatwg.org/multipage/syntax.html#elements-2 
	 * void elements : area, base, br, col, embed, hr, img, input, link, meta, param, source, track, wbr
	 * </pre>
	 */
	private final String[] htmlVoidElements = new String[] { "area", "base", "br", "col", "embed", "hr", "img",
			"input", "link", "meta", "param", "source", "track", "wbr" };

	/**
	 * <pre>
	 * 참고 사이트 : https://html.spec.whatwg.org/multipage/syntax.html#elements-2 
	 * void elements : area, base, br, col, embed, hr, img, input, link, meta, param, source, track, wbr
	 * </pre>
	 */
	private final HashSet<String> htmlVoidElementSet = new HashSet<String>(Arrays.asList(htmlVoidElements));
	
	
	public BoardContentsSAXParser() {
		try {
			whiteTagInformation = new SampleBaseUserSiteWhiteList();
		} catch (Exception e) {	
			log.log(Level.SEVERE, "fail to create a instance of SampleBaseUserSiteWhiteInformation class", e);
			System.exit(1);
		}
	}

	private void reset() {
		boardImageFileInformationList = new ArrayList<BoardImageFileInformation>();
		tagStack.clear();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		String startTag = qName.toLowerCase();

		if (! htmlVoidElementSet.contains(startTag)) {
			/** 닫지 않는 태그가 아닌 경우에만 짝 맞춤 검사를 위해서 tagStack 에 넣는다 */
			tagStack.push(startTag);
		}		
		
		WhiteTag whiteTag = whiteTagInformation.getWhiteTag(startTag);
		if (null == whiteTag) {
			String errorMessage = new StringBuilder("warning::the var startTag[").append(startTag)
					.append("] is not a white tag").toString();
			throw new SAXException(errorMessage);
		}
		
		if (whiteTag.isNoAttribute()) {
			/** 허락된 속성이 없는데 속성이 있는 경우 예외 던짐 */
			if (0 != attributes.getLength()) {
				String errorMessage = new StringBuilder("warning::the var startTag[").append(startTag)
						.append("] has no attribute but one more attribute[")
						.append(attributes.toString()).toString();
				throw new SAXException(errorMessage);
			}
		}
		
		if ("img".equals(startTag)) {
			BoardImageFileInformation boardImageFileInformation = new BoardImageFileInformation();
			
			String srcAttributeValue = attributes.getValue("src");
			
			if (null == srcAttributeValue) {
				String errorMessage = "img 태그에서 src 속성을 필수 인데 src 속성이 빠져 있음";
				throw new SAXException(errorMessage);
			}
			
			WhiteTagAttribute srcTagAttribute = whiteTag.getWhiteTagAttribute("src");
			
			if (null == srcTagAttribute) {
				String errorMessage = "the img tag's src atttibute is a not allowed attbitue";
				throw new SAXException(errorMessage);
			}
						
			ImgTagSrcAtrrWhiteValueChecker imgTagSrcValueXSSAtackChecker = (ImgTagSrcAtrrWhiteValueChecker)srcTagAttribute.getAttributeValueXSSAtackChecker();
			
			
			imgTagSrcValueXSSAtackChecker.checkXSSAttack(srcAttributeValue, boardImageFileInformation);
			
			
			
			String dataFileNameAttributeValue = attributes.getValue("data-filename");
			if (null == dataFileNameAttributeValue) {
				String errorMessage = "img tag needs src attribute but no src attribute";
				throw new SAXException(errorMessage);
			}
			
			
			WhiteTagAttribute dataFileNameTagAttribute = whiteTag.getWhiteTagAttribute("data-filename");
			
			if (null == dataFileNameTagAttribute) {
				String errorMessage = "the img tag's data-filename atttibute is a not allowed attbitue";
				throw new SAXException(errorMessage);
			}
			
			ImgTagDataFileNameAttrWhiteValueChecker imgTagDataFileNameValueXSSAttackChecker =  (ImgTagDataFileNameAttrWhiteValueChecker)dataFileNameTagAttribute.getAttributeValueXSSAtackChecker();
			
			imgTagDataFileNameValueXSSAttackChecker.checkXSSAttack(dataFileNameAttributeValue, boardImageFileInformation);
			
			boardImageFileInformationList.add(boardImageFileInformation);
		}
		
		final int countOfAttribute = attributes.getLength();
		for (int i=0;i < countOfAttribute; i++) {
			String attributeName = attributes.getQName(i);
			
			if (null == attributeName) {
				String errorMessage = new StringBuilder("warning::the var startTag[").append(startTag)
						.append("]'s attributes[index=")
						.append(i)
						.append("] is null").toString();
				throw new SAXException(errorMessage);
			}
			
			if (attributeName.isEmpty()) {
				String errorMessage = new StringBuilder("warning::the var startTag[").append(startTag)
						.append("]'s attributes[index=")
						.append(i)
						.append("] is a empty string, in other word not valid").toString();
				throw new SAXException(errorMessage);
			}
			
			/** img tag 의 src 와 dafa-filename 속성들은 앞에서 XSS 공격 여부를 판단했기때문에 이 과정을 건너 뛴다 */
			if ("img".equals(startTag)) {
				if ("src".equals(attributeName)) {
					continue;
				}				
				if ("data-filename".equals(attributeName)) {
					continue;
				}
			}
						
			WhiteTagAttribute whiteTagAttribute = whiteTag.getWhiteTagAttribute(attributeName);
			
			if (null == whiteTagAttribute) {
				String errorMessage = new StringBuilder("warning::the var startTag[").append(startTag)
						.append("]'s attributes[index=")
						.append(i)
						.append("] is not a disallowed attribute").toString();
				
				throw new SAXException(errorMessage);
			}
			
			String attributeValue = attributes.getValue(i);
			
			whiteTagAttribute.throwExceptionIfNoWhiteValue(attributeValue);			
		}		
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// String tagValue = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String endTag = qName.toLowerCase();

		String startTag = tagStack.peek();

		if (! startTag.equals(endTag)) {
			String errorMessage = new StringBuilder("warning::시작 태그와 끝 태드 매칭 실패, the var startTag[").append(startTag)
					.append("] is different from the var endTag[").append(endTag).append("]").toString();
			throw new SAXException(errorMessage);
		}

		tagStack.pop();
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		String startTag = "root";
		if (! tagStack.isEmpty()) {
			startTag = tagStack.peek();
		}
		
		String errorMessage = new StringBuilder("warning::").append(startTag).append(", ").append(e.toString())
				.toString();
		throw new SAXException(errorMessage);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		String startTag = "root";
		if (! tagStack.isEmpty()) {
			startTag = tagStack.peek();
		}
		
		String errorMessage = new StringBuilder("error::").append(startTag).append(", ").append(e.toString())
				.toString();
		throw new SAXException(errorMessage);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		String startTag = "root";
		if (! tagStack.isEmpty()) {
			startTag = tagStack.peek();
		}
		
		String errorMessage = new StringBuilder("fatalError::").append(startTag).append(", ")
				.append(e.toString()).toString();
		throw new SAXException(errorMessage);
	}

	public ArrayList<BoardImageFileInformation> parseWithXssAttackCheck(String contents)
			throws IllegalArgumentException, SAXException, IOException {
		if (null == contents) {
			throw new IllegalArgumentException("the parameter contents is null");
		}

		// String xmlFilePathString = xmlFile.getAbsolutePath();

		SAXParser saxParser;
		try {
			saxParser = SAXParserFactory.newInstance().newSAXParser();
			
		} catch (Exception | Error e) {
			log.log(Level.WARNING, e.getMessage(), e);
			throw new SAXException(e.getMessage());
		}

		synchronized (BoardContentsSAXParser.class) {
			
			ByteArrayInputStream bais = new ByteArrayInputStream(contents.getBytes());
			
			try {
				saxParser.parse(bais, this);

				/**
				 * WARNING! 코드 지우지 말것, 좀더 확실하게 Thread safe 함을 보장하기 위해서 멤버 변수의 값을 직접 넘기지 않고 로컬 임시
				 * 변수에 받아 넘기도록 처리함.
				 */
				final ArrayList<BoardImageFileInformation> tempBoardImageFileInformationList = boardImageFileInformationList;
				return tempBoardImageFileInformationList;
			} finally {
				reset();
			}
		}
	}
}
