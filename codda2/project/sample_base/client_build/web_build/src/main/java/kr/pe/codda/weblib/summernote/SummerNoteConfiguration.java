package kr.pe.codda.weblib.summernote;

import java.util.HashSet;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class SummerNoteConfiguration {
	private final boolean isNoFontFamily;
	private final String[] fontNameList;
	private final HashSet<String> fontNameSet = new HashSet<String>();
	
	public SummerNoteConfiguration(String[] fontNameList) {
		if (null == fontNameList) {
			throw new IllegalArgumentException("the parameter fontNameList is null");
		}
		
		if (0 == fontNameList.length) {
			isNoFontFamily = true;
		} else {
			isNoFontFamily = false;
		}
		
		this.fontNameList = fontNameList;
		
		for (String fontName : fontNameList) {
			fontNameSet.add(fontName);
		}

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
	
	
	public boolean isNoFontFamily() {
		return isNoFontFamily;
	}
	
	
	public boolean isFontName(String fontName) {
		if (null == fontName) {
			throw new IllegalArgumentException("the parameter fontName is null");
		}
		
		return fontNameSet.contains(fontName);
	}
	
	
	private String buildTabString(int depth) {
		StringBuilder tabStringBuilder = new StringBuilder();
		
		for (int i=0; i < depth; i++) {
			tabStringBuilder.append("\t");
		}
		
		return tabStringBuilder.toString();
	}
	
	public String buildToolBarString(int depth) {
		StringBuilder toolBarStringBuilder =  new StringBuilder()
				.append(buildTabString(depth)).append("toolbar: [")
				.append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth+1)).append("['fontStyle', ['style', 'bold', 'italic', 'underline', 'strikethrough', 'clear']]");
				
		
		if (isNoFontFamily) {
			toolBarStringBuilder
			.append(",").append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth+1)).append("['fontname', ['fontname']]");
		}		
		
		toolBarStringBuilder
		.append(",").append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth+1)).append("['color', ['color']]")
		.append(",").append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth+1)).append("['para', ['ul', 'ol', 'paragraph']]")
		.append(",").append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth+1)).append("['table', ['table']]")
		.append(",").append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth+1)).append("['insert', ['link', 'picture', 'video']]")
		.append(",").append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth+1)).append("['view', ['fullscreen', 'codeview', 'help']]")
		.append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth)).append("]");
		
		return toolBarStringBuilder.toString();
	}
	
	public String buildPopoverStringForFont(int depth) {
		StringBuilder popoverStringBuilder =  new StringBuilder()
				.append(buildTabString(depth)).append("popover: {");
		
		popoverStringBuilder.append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth+1)).append("fontNames : [");
		
		for (String fontName : fontNameList) {
			popoverStringBuilder.append("'")
			.append(fontName).append("', ");
		}
		
		popoverStringBuilder.append(buildTabString(depth)).append("]")
		.append(CommonStaticFinalVars.NEWLINE).append(buildTabString(depth)).append("}");
		
		
		return popoverStringBuilder.toString();
	}
	
	public String buildInitializationOptionsString(int depth) {
		StringBuilder initStringBuilder = new StringBuilder(buildToolBarString(depth));
		
		if (isNoFontFamily) {
			initStringBuilder.append(",").append(CommonStaticFinalVars.NEWLINE).append(buildPopoverStringForFont(depth));
		}
		
		return initStringBuilder.toString();
	}
	
}
