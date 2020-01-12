package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ATagHrefAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ATagRelAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ATagStyleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ATagTargetAttrValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.H1TagSytleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.H2TagSytleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.H3TagSytleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.H4TagSytleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.H5TagSytleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.H6TagSytleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagClassAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagDataFileNameAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagSrcAtrrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagSytleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.PTagStyleWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.PreTagStyleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.SpanTagStyleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.TableTagClassAttrWhiteValueChecker;

/**
 * 프로젝트 'sample_base' 사용자 웹사이트의 화이트 목록 
 * @author Won Jonghoon
 *
 */
public class SampleBaseUserSiteWhiteList extends WhiteList {

	/**
	 * 생성자
	 * @throws IllegalArgumentException 내부 메소드 호출할때 파라미터를 잘못 넘긴 경우 던지는 예외 
	 * @throws WhiteParserException 값에 허락되지 않은 문자가 포함시 던진는 예외
	 */
	public SampleBaseUserSiteWhiteList() throws IllegalArgumentException, WhiteParserException {
		addTags("a", "b", "blockquote", "br", "code", "dd", "dl", "dt", "em", "i", "li", "ol", "p", "pre",
				"q", "small", "span", "strike", "strong", "sub", "sup", "u", "ul",
				"html","head", "body", "h1", "h2", "h3", "h4", "h5", "h6", "font", "img", "table", "tbody", "tr", "td");
		
		addAttribute("a", "href", new ATagHrefAttrWhiteValueChecker());		
		addAttribute("a", "rel", new ATagRelAttrWhiteValueChecker());
		addAttribute("a", "target", new ATagTargetAttrValueChecker());
		addAttribute("a", "style", new ATagStyleAttrWhiteValueChecker());
		addAttribute("span", "style", new SpanTagStyleAttrWhiteValueChecker());
		addAttribute("p", "style", new PTagStyleWhiteValueChecker());
		addAttribute("table", "class", new TableTagClassAttrWhiteValueChecker());
		addAttribute("img", "style", new ImgTagSytleAttrWhiteValueChecker());
		addAttribute("img", "src", new ImgTagSrcAtrrWhiteValueChecker());
		addAttribute("img", "data-filename", new ImgTagDataFileNameAttrWhiteValueChecker());
		addAttribute("img", "class", new ImgTagClassAttrWhiteValueChecker());		
		addAttribute("h1", "style", new H1TagSytleAttrWhiteValueChecker());
		addAttribute("h2", "style", new H2TagSytleAttrWhiteValueChecker());
		addAttribute("h3", "style", new H3TagSytleAttrWhiteValueChecker());
		addAttribute("h4", "style", new H4TagSytleAttrWhiteValueChecker());
		addAttribute("h5", "style", new H5TagSytleAttrWhiteValueChecker());
		addAttribute("h6", "style", new H6TagSytleAttrWhiteValueChecker());
		addAttribute("pre", "style", new PreTagStyleAttrWhiteValueChecker());
	}
		
}
