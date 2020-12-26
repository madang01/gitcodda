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
package kr.pe.codda.common.config;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.exception.PartConfigurationException;

/**
 * @author Won Jonghoon
 *
 */
public class RunningConfigurationModelTest extends AbstractJunitTest {
	
	@Test
	public void test() {
		
		try {
			
			String configFilePathString = ProjectBuildSytemPathSupporter.getProejctConfigFilePathString(installedPath.getAbsolutePath(), mainProjectName);
			
			log.info(configFilePathString);
			
			
			RunningConfigurationModel runningConfigurationModel = new RunningConfigurationModel(installedPath.getAbsolutePath(), mainProjectName);
			
			for (PartModelIF partModel : runningConfigurationModel.getPartModelList()) {
				log.info(partModel.getPartName());
			}
		} catch (FileNotFoundException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage);
			fail("config file not found");
		} catch (PartConfigurationException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage);
			fail("config file not found");
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}

}
