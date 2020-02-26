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
package kr.pe.codda.server.classloader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;

/**
 * @author Won Jonghoon
 *
 */
public class ServerClassLoaderFactoryTest {
	
	protected Logger log = Logger.getLogger(ServerClassLoaderFactoryTest.class.getName()); 

	protected final static String installedPathString = "D:\\gitcodda\\codda2";
	protected final static File installedPath = new File(installedPathString);
	protected final static String mainProjectName = "sample_base";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!installedPath.exists()) {
			fail("the installed path doesn't exist");
		}

		if (!installedPath.isDirectory()) {
			fail("the installed path isn't a directory");
		}

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME, mainProjectName);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH, installedPathString);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testToClassFullNameIfAppInfClassFile() {
		String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, 
						mainProjectName);
		String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		ServerClassLoaderFactory serverClassLoaderFactory = null;
		
		try {
			serverClassLoaderFactory = new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		} catch (CoddaConfigurationException e) {
			fail("fail to create a instance of ServerClassLoaderFactory");
		}
		
		String expectedClassFullName = "kr.pe.codda.impl.task.server.EchoServerTask";
		
		File appInfClassFile = new File(serverClassLoaderFactory.getClassFilePathString(expectedClassFullName));
		
		String acutalClassFullName = serverClassLoaderFactory.toClassFullNameIfAppInfClassFile(appInfClassFile);
		
		assertEquals(expectedClassFullName, acutalClassFullName);
	}

}
