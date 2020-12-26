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
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.model.CurrentWokingPathInformation;
import kr.codda.util.HtmlContentsBuilder;

/**
 * @author Won Jonghoon
 *
 */
public class CurrentWokingPathChangerSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8849364017919544254L;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String shortPathNameToMove = req.getParameter("shortPathNameToMove");
		if (null == shortPathNameToMove) {
			String errorMessage = "the parameter shortPathNameToMove is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
			return;
		}
		
		
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance(); 
		String currentWorkingPathString = coddaHelperSite.getCurrentWorkingPathString();
		
		if (null == currentWorkingPathString) {
			String errorMessage = "the var currentWorkingPathString is null";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
			return;
		}
		
		String newCurrentWorkingPathString = null;
		
		if (shortPathNameToMove.equals("..")) {
			File childFileToMove = new File(currentWorkingPathString+File.separator+"..");
			try {
				newCurrentWorkingPathString = childFileToMove.getCanonicalPath();
			} catch(Exception e) {
				String errorMessage = "failed to get the var newCurrentWorkingPathString that is a canonical path";
				
				Logger.getGlobal().warning(errorMessage);
				
				res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
				return;
			}
		} else {
			if (shortPathNameToMove.contains("..")) {
				String errorMessage = "the parameter shortPathNameToMove[" + shortPathNameToMove + "] is bad becase it includes ..";
				
				Logger.getGlobal().warning(errorMessage);
				
				res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
				return;
			}
			
			File childFileToMove = null;
			File currentWorkingPath = new File(currentWorkingPathString);
			for (File childFile : currentWorkingPath.listFiles()) {
				if (childFile.isDirectory()) {
					if (childFile.getName().equals(shortPathNameToMove)) {
						childFileToMove = childFile;
						break;
					}
				}
			}
			
			if (null == childFileToMove) {
				String errorMessage = "the parameter shortPathNameToMove[" + shortPathNameToMove + "] is no a child directory of the current working path[" + currentWorkingPathString + "]";
				
				Logger.getGlobal().warning(errorMessage);
				
				res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
				return;
			}
			
			try {
				newCurrentWorkingPathString = childFileToMove.getCanonicalPath();
			} catch(Exception e) {
				String errorMessage = "failed to get the var newCurrentWorkingPathString that is a canonical path";
				
				Logger.getGlobal().warning(errorMessage);
				
				res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfErrorMessageGetter(errorMessage));
				return;
			}
		}
		
		coddaHelperSite.setCurrentWorkingPathString(newCurrentWorkingPathString);
		
		CurrentWokingPathInformation newCurrentWokingPathInformation = new CurrentWokingPathInformation();
		
		newCurrentWokingPathInformation.setCurrentWorkingPathString(newCurrentWorkingPathString);
		
		File newCurrentWorkingPath = new File(newCurrentWorkingPathString);
		
		for (File childFile : newCurrentWorkingPath.listFiles()) {
			if (childFile.isDirectory()) {
				newCurrentWokingPathInformation.addChildPathString(childFile.getName());
			}
		}
		
		String newCurrentWokingPathInformationJsonString = new Gson().toJson(newCurrentWokingPathInformation);
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().println(HtmlContentsBuilder.buildHtmlContentsOfCurrentWorkingPathInformationGetterPage(newCurrentWokingPathInformationJsonString));
		
	}
}
