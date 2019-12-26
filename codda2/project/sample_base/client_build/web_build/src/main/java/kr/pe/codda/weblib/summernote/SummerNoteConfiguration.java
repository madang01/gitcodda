package kr.pe.codda.weblib.summernote;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class SummerNoteConfiguration {
	private final boolean isFontNames;
	private final String[] fontNameList;
	
	public SummerNoteConfiguration(String[] fontNameList) {
		if (null == fontNameList) {
			throw new IllegalArgumentException("the parameter fontNameList is null");
		}
		
		if (0 == fontNameList.length) {
			isFontNames = false;
		} else {
			isFontNames = true;
		}
		
		this.fontNameList = fontNameList;

		/*
		fontNameList = new String[] { 
				"Arial",
				"Arial Black",
				"Comic Sans MS",
				"Courier New",
				"Courier New",
				"Helvetica",
				"Impact",
				"Tahoma",
				"Times New Roman",
				"Verdana"
		};	
		*/	
	}
	
	
	public boolean isFontFamaily() {
		return isFontNames;
	}
	
	
	
	public String[] getFontNameList() {
		return fontNameList;
	}
	
	public String buildToolBarString() {
		StringBuilder toolBarStringBuilder =  new StringBuilder()
				.append("toolbar: ['fontStyle', ['style', 'bold', 'italic', 'underline', 'strikethrough', 'clear]]");
				
		
		if (isFontNames) {
			toolBarStringBuilder
			.append(",").append(CommonStaticFinalVars.NEWLINE).append("['fontname', ['fontname']]");
		}		
		
		toolBarStringBuilder
		.append(",").append(CommonStaticFinalVars.NEWLINE).append("['color', ['color']]")
		.append(",").append(CommonStaticFinalVars.NEWLINE).append(" ['para', ['ul', 'ol', 'paragraph']]")
		.append(",").append(CommonStaticFinalVars.NEWLINE).append("['table', ['table']]")
		.append(",").append(CommonStaticFinalVars.NEWLINE).append("['insert', ['link', 'picture', 'video']]")
		.append(",").append(CommonStaticFinalVars.NEWLINE).append("['view', ['fullscreen', 'codeview', 'help']]");
		
		return toolBarStringBuilder.toString();
	}
	
	public String buildPopoverStringForFontFamaily() {
		StringBuilder popoverStringBuilder =  new StringBuilder().append("popover: {");
		
		popoverStringBuilder.append(CommonStaticFinalVars.NEWLINE).append("fontNames : [");
		
		for (String fontName : fontNameList) {
			popoverStringBuilder.append("'")
			.append(fontName).append("', ");
		}
		
		popoverStringBuilder.append(CommonStaticFinalVars.NEWLINE).append("]");
		
		return popoverStringBuilder.toString();
	}
	
	public String buildInitString() {
		StringBuilder initStringBuilder = new StringBuilder(buildToolBarString());
		
		if (isFontNames) {
			initStringBuilder.append(CommonStaticFinalVars.NEWLINE).append(buildPopoverStringForFontFamaily());
		}
		
		return initStringBuilder.toString();
	}
	
}
