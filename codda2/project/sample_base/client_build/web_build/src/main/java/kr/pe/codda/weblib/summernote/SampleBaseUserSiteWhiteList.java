package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ATagHrefAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ATagRelAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ATagTargetAttrValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagClassAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagDataFileNameAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagSrcAtrrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.ImgTagSytleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.PTagStyleWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.SpanTagStyleAttrWhiteValueChecker;
import kr.pe.codda.weblib.summernote.whitevaluechecker.TableTagClassAttrWhiteValueChecker;

public class SampleBaseUserSiteWhiteList extends WhiteList {

	public SampleBaseUserSiteWhiteList() throws IllegalArgumentException, WhiteParserException {
		addTags("a", "b", "blockquote", "br", "code", "dd", "dl", "dt", "em", "i", "li", "ol", "p", "pre",
				"q", "small", "span", "strike", "strong", "sub", "sup", "u", "ul",
				"html","head", "body", "h1", "h2", "h3", "h4", "h5", "h6", "font", "img", "table", "tbody", "tr", "td");
		
		addAttribute("a", "href", new ATagHrefAttrWhiteValueChecker());		
		addAttribute("a", "rel", new ATagRelAttrWhiteValueChecker());
		addAttribute("a", "target", new ATagTargetAttrValueChecker());
		addAttribute("span", "style", new SpanTagStyleAttrWhiteValueChecker());
		addAttribute("p", "style", new PTagStyleWhiteValueChecker());
		addAttribute("table", "class", new TableTagClassAttrWhiteValueChecker());
		addAttribute("img", "style", new ImgTagSytleAttrWhiteValueChecker());
		addAttribute("img", "src", new ImgTagSrcAtrrWhiteValueChecker());
		addAttribute("img", "data-filename", new ImgTagDataFileNameAttrWhiteValueChecker());
		addAttribute("img", "class", new ImgTagClassAttrWhiteValueChecker());
	}
		
}
