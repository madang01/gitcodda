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
package kr.codda.servlet.ajax;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;

/**
 * @author Won Jonghoon
 *
 */
public class InstalledPathSetterSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7639183428880505802L;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String installedPathString = req.getParameter("installedPath");
		if (null == installedPathString) {
			String errorMessage = "the parameter installedPath is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			// res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
			
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		File installedPath = new File(installedPathString);
		
		if (! installedPath.exists()) {
			String errorMessage = "the parameter installedPath[" + installedPathString  + "] doesn't exist";
			
			Logger.getGlobal().warning(errorMessage);
			
			// res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		if (! installedPath.isDirectory()) {
			String errorMessage = "the parameter installedPath[" + installedPathString  + "] is not a directory";
			
			Logger.getGlobal().warning(errorMessage);
			
			// res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		try {
			installedPathString = installedPath.getCanonicalPath();
		} catch(Exception e) {
			String errorMessage = "failed to get a installed directory["+ installedPathString +"]'s canonical Path";
			
			Logger.getGlobal().warning(errorMessage);
			
			// res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
		coddaHelperSite.setInstalledPathString(installedPathString);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		// res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfInstalledPathChangerPage(installedPathString));
		res.getWriter().print(installedPathString);
		
	}

}
