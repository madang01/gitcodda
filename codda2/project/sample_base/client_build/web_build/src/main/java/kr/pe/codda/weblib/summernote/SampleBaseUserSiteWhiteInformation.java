package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

public class SampleBaseUserSiteWhiteInformation extends WhiteInformation {

	public SampleBaseUserSiteWhiteInformation() throws IllegalArgumentException, WhiteParserException {
		addTags("a", "b", "blockquote", "br", "code", "dd", "dl", "dt", "em", "i", "li", "ol", "p", "pre",
				"q", "small", "span", "strike", "strong", "sub", "sup", "u", "ul",
				"html","head", "body", "h1", "h2", "h3", "h4", "h5", "h6", "font", "img", "table", "tbody", "tr", "td");
		
		addAttribute("a", "href", new ATagHrefValueXSSAttackChecker());		
		addAttribute("a", "rel", new ATagRelValueXSSAttackChecker());
		addAttribute("span", "style", new SpanTagStyleValueXSSAttackChecker());
		addAttribute("p", "style", new PTagStyleValueXSSAttackChecker());
		addAttribute("table", "class", new TableTagClassValueXSSAttackChecker());
		addAttribute("img", "style", new ImgTagSytleXSSAttackChecker());
		addAttribute("img", "src", new ImgTagSrcValueXSSAtackChecker());
		addAttribute("img", "data-filename", new ImgTagDataFileNameValueXSSAttackChecker());
		addAttribute("img", "class", new ImgTagClassValueXSSAttackChecker());
	}
		
}
