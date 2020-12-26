/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.codda.util;

import org.apache.commons.text.StringEscapeUtils;

/**
 * @author Won Jonghoon
 *
 */
public class HtmlContentsBuilder {
	final static String NEWLINE = System.getProperty("line.separator");
	final static String TITLE_OF_HTML = "Codda Helper Web Service";
	
	
	
	
	
	public static String buildHtmlContentsOfErrorMessageGetter(String errorMessage) {
		
		
		String htmlCotents = new StringBuilder()
				.append("<!DOCTYPE html>").append(NEWLINE)
				.append("<html lang=\"ko\">").append(NEWLINE)
				.append("<head>").append(NEWLINE)
				.append("<meta charset=\"UTF-8\">").append(NEWLINE)
				.append("<title>").append(TITLE_OF_HTML).append("</title>").append(NEWLINE)
				.append("<script type=\"text/javascript\">").append(NEWLINE)
				.append("<!--").append(NEWLINE)
				.append("	function init() {").append(NEWLINE)
				.append("		parent.callbackForErrorMessageGetter(document.getElementById(\"errorMessage\").innerText);").append(NEWLINE)
				.append("	}").append(NEWLINE)
				.append(NEWLINE)
				.append("	window.onload = init;").append(NEWLINE)
				.append("//-->").append(NEWLINE)
				.append("</script>").append(NEWLINE)
				.append("</head>").append(NEWLINE)
				.append("<body>").append(NEWLINE)
				.append("	<div id=\"errorMessage\">")
				.append(StringEscapeUtils.escapeHtml4(errorMessage))
				.append("</div>").append(NEWLINE)
				.append("</body>").append(NEWLINE)
				.append("</html>").append(NEWLINE).toString();
		return htmlCotents;
	}
	
	public static String buildHtmlContentsOfCurrentWorkingPathInformationGetterPage(String currentWokingPathInformationJsonString) {
		
		
		String htmlCotents = new StringBuilder()
				.append("<!DOCTYPE html>").append(NEWLINE)
				.append("<html lang=\"ko\">").append(NEWLINE)
				.append("<head>").append(NEWLINE)
				.append("<meta charset=\"UTF-8\">").append(NEWLINE)
				.append("<title>").append(TITLE_OF_HTML).append("</title>").append(NEWLINE)
				.append("<script type=\"text/javascript\">").append(NEWLINE)
				.append("<!--").append(NEWLINE)
				.append("	function init() {").append(NEWLINE)
				.append("		parent.callbackForCurrentWokingPathInformationGetter(")
				.append(currentWokingPathInformationJsonString)
				.append(");").append(NEWLINE)
				.append("	}").append(NEWLINE)
				.append(NEWLINE)
				.append("	window.onload = init;").append(NEWLINE)
				.append("//-->").append(NEWLINE)
				.append("</script>").append(NEWLINE)
				.append("</head>").append(NEWLINE)
				.append("<body>").append("</body>").append(NEWLINE)
				.append("</html>").append(NEWLINE).toString();
		return htmlCotents;
	}
	
	

}
