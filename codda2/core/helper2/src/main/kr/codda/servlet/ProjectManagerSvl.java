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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.util.CommonStaticUtil;

/**
 * @author Won Jonghoon
 *
 */
public class ProjectManagerSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3045324650173672216L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

		/*
		String command = req.getParameter("command");

		if (null == command) {

			String errorMessage = "the parameter 'command' is null";
			CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
			coddaHelperSite.setErrorMessage(errorMessage);

			String errorPageContents = CommonStaticUtil.readErrorPageContents();
			
			res.setStatus(HttpServletResponse.SC_OK);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			
			PrintWriter pw = res.getWriter();
			pw.write(errorPageContents);

			return;

		}
		*/
		
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance();
		
		String installedPathString = coddaHelperSite.getInstalledPathString();
		
		if (null == installedPathString) {
			String installedPathInputPageContents = CommonStaticUtil.readPageContents("/webapp/sitemenu/project_manager/installedPathInputPage.html");
			
			res.setStatus(HttpServletResponse.SC_OK);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			
			PrintWriter pw = res.getWriter();
			pw.write(installedPathInputPageContents);
			
			return;
		} else {
			
			String parmInstalledPath = req.getParameter("installedPath");
			
			if (null == parmInstalledPath) {
				String errorMessage = "the parameter 'installedPath' is null";				
				coddaHelperSite.setErrorMessage(errorMessage);
				
				String errorPageContents = CommonStaticUtil.readErrorPageContents();
				
				res.setStatus(HttpServletResponse.SC_OK);
				res.setContentType("text/html");
				res.setCharacterEncoding("utf-8");
				
				PrintWriter pw = res.getWriter();
				pw.write(errorPageContents);

				return;
			}
			
			File installedPath = new File(parmInstalledPath);
			
			if (! installedPath.exists()) {
				String errorMessage = "the parameter 'installedPath'[" + parmInstalledPath + "] does not exist";				
				coddaHelperSite.setErrorMessage(errorMessage);
				
				String errorPageContents = CommonStaticUtil.readErrorPageContents();
				
				res.setStatus(HttpServletResponse.SC_OK);
				res.setContentType("text/html");
				res.setCharacterEncoding("utf-8");
				
				PrintWriter pw = res.getWriter();
				pw.write(errorPageContents);

				return;
			}
			
			if (! installedPath.isDirectory()) {
				String errorMessage = "the parameter 'installedPath'[" + parmInstalledPath + "] is not a directory";				
				coddaHelperSite.setErrorMessage(errorMessage);
				
				String errorPageContents = CommonStaticUtil.readErrorPageContents();
				
				res.setStatus(HttpServletResponse.SC_OK);
				res.setContentType("text/html");
				res.setCharacterEncoding("utf-8");
				
				PrintWriter pw = res.getWriter();
				pw.write(errorPageContents);

				return;
			}
			
			coddaHelperSite.setInstalledPathString(parmInstalledPath);
			
			
			String projectMangerPageContents = CommonStaticUtil.readPageContents("/webapp/sitemenu/project_manager/projectManger.html");
			
			res.setStatus(HttpServletResponse.SC_OK);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			
			PrintWriter pw = res.getWriter();
			pw.write(projectMangerPageContents);
			
		}

		
	}
}
