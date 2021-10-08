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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import kr.codda.model.CoddaHelperSite;
import kr.codda.model.CoddaHelperSiteManager;
import kr.codda.model.ConfigFileInformation;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * 코다 설정 파일 내용과 관련 정보를 가져오는 서블릿  
 * @author Won Jonghoon
 *
 */
public class ConfigFileInformationGetterSvl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8093184906378532373L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		CoddaHelperSite coddaHelperSite = CoddaHelperSiteManager.getInstance(); 
		String installedPathString = coddaHelperSite.getInstalledPathString();
		
		if (null == installedPathString) {
			String errorMessage = "failed to get a installed directory of CoddaHelperSite";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		String selectedMainProjectName = coddaHelperSite.getSelectedMainProjectName();
		
		if (null == selectedMainProjectName) {
			String errorMessage = "failed to get a selected main project name of CoddaHelperSite";
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		CoddaConfiguration coddaConfiguration = new CoddaConfiguration(installedPathString, selectedMainProjectName);
		
		SequencedProperties configSequencedProperties = null;
		
		try {
			configSequencedProperties = coddaConfiguration.loadConfigFile();
		} catch (IllegalArgumentException | FileNotFoundException | PartConfigurationException e) {
			String errorMessage = e.getMessage();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		} catch (IOException e) {
			
			String errorMessage = new StringBuilder().append("fail to read config file[")
					.append(coddaConfiguration.getConfigFilePathString())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			Logger.getGlobal().warning(errorMessage);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		} catch(Exception e) {
			
			String errorMessage = new StringBuilder().append("unknown error occurred when reading configuration file[")
					.append(coddaConfiguration.getConfigFilePathString())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			Logger.getGlobal().log(Level.WARNING, errorMessage, e);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(errorMessage);
			return;
		}
		
		
		String configFileJsonString = configSequencedProperties.toJSONString();
		
		
		ConfigFileInformation configFileInformation = new ConfigFileInformation();
		configFileInformation.setInstalledPathString(installedPathString);
		configFileInformation.setSelectedMainProjectName(selectedMainProjectName);
		configFileInformation.setConfigFilePathString(coddaConfiguration.getConfigFilePathString());
		configFileInformation.setConfigFileJsonString(configFileJsonString);
		
		
		String configFileInformationJsonString = new Gson().toJson(configFileInformation);
		
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		res.getWriter().print(configFileInformationJsonString);
	}

	
}
