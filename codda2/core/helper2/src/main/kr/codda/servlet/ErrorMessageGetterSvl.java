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
package kr.codda.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.util.HtmlContentsBuilder;

/**
 * @author Won Jonghoon
 *
 */
public class ErrorMessageGetterSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6898934378745228565L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance(); 
		String errorMessage = coddaHelperSite.getErrorMessage(); 

		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
	}
}
