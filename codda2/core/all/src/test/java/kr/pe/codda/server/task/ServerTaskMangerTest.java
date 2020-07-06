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
package kr.pe.codda.server.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.server.classloader.ServerClassLoaderFactory;

/**
 * @author Won Jonghoon
 *
 */
public class ServerTaskMangerTest {
	
	protected Logger log = Logger.getLogger(ServerTaskMangerTest.class.getName()); 

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
	public void testServerTaskManger_ok() {
		String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, 
						mainProjectName);
		String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		ServerClassLoaderFactory serverClassLoaderFactory = null;
		
		try {
			serverClassLoaderFactory = new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		} catch (IllegalArgumentException e) {
			fail("fail to create a instance of ServerClassLoaderFactory");
		}
		
		@SuppressWarnings("unused")
		ServerTaskManger ServerTaskManger = 
				new ServerTaskManger(serverClassLoaderFactory);
	}

	
	@Test
	public void testCheckMessageClass_OK() {
		String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, 
						mainProjectName);
		String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		ServerClassLoaderFactory serverClassLoaderFactory = null;
		
		try {
			serverClassLoaderFactory = new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		} catch (IllegalArgumentException e) {
			fail("fail to create a instance of ServerClassLoaderFactory");
		}
		
		ServerTaskManger ServerTaskManger = new ServerTaskManger(serverClassLoaderFactory);
		
		
		String classFullName = "kr.pe.codda.impl.message.Echo.Echo";
		String actualMessageID = ServerTaskManger.checkMessageClass(classFullName);
		
		assertEquals("Echo", actualMessageID);
	}
	
	
	@Test
	public void testCheckMessageClass_패키지명없는경우() {
		String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, 
						mainProjectName);
		String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		ServerClassLoaderFactory serverClassLoaderFactory = null;
		
		try {
			serverClassLoaderFactory = new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		} catch (IllegalArgumentException e) {
			fail("fail to create a instance of ServerClassLoaderFactory");
		}
		
		ServerTaskManger ServerTaskManger = new ServerTaskManger(serverClassLoaderFactory);
		
		
		String classFullName = "TEcho";
		
		try {
			ServerTaskManger.checkMessageClass(classFullName);
		
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedErrorMessage = "the var endIndex is -1";
			
			assertEquals(expectedErrorMessage, errorMessage);			
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testCheckMessageClass_패키지명1개인경우() {
		String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, 
						mainProjectName);
		String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		ServerClassLoaderFactory serverClassLoaderFactory = null;
		
		try {
			serverClassLoaderFactory = new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		} catch (IllegalArgumentException e) {
			fail("fail to create a instance of ServerClassLoaderFactory");
		}
		
		ServerTaskManger ServerTaskManger = new ServerTaskManger(serverClassLoaderFactory);
		
		
		String classFullName = "Tmp.TEcho";
		try {
			ServerTaskManger.checkMessageClass(classFullName);
		
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedErrorMessage = "the var beginIndex is -1";
			
			assertEquals(expectedErrorMessage, errorMessage);			
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testCheckMessageClass_기대한메시지식별자가빈문자열인경우() {		
		
		String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, 
						mainProjectName);
		String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		ServerClassLoaderFactory serverClassLoaderFactory = null;
		
		try {
			serverClassLoaderFactory = new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		} catch (IllegalArgumentException e) {
			fail("fail to create a instance of ServerClassLoaderFactory");
		}
		
		ServerTaskManger ServerTaskManger = new ServerTaskManger(serverClassLoaderFactory);
		
		
		String classFullName = "Tmp..TEcho";
		try {
			ServerTaskManger.checkMessageClass(classFullName);
		
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedErrorMessage = "the expected message id that is a substring from the var beginIndex to the var endIndex is a empty string";
			
			assertEquals(expectedErrorMessage, errorMessage);			
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testBuildServerTaskDepencySet_입력메시지가노출되지않은경우() {
		String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, 
						mainProjectName);
		String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		ServerClassLoaderFactory serverClassLoaderFactory = null;
		
		try {
			serverClassLoaderFactory = new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		} catch (IllegalArgumentException e) {
			fail("fail to create a instance of ServerClassLoaderFactory");
		}
		
		ServerTaskManger ServerTaskManger = new ServerTaskManger(serverClassLoaderFactory);
				
		String messageID = "Echo";
		
		Set<String> expectedSet = new LinkedHashSet<String>();
		
		expectedSet.add("kr.pe.codda.impl.task.server.EchoServerTask");
		expectedSet.add("kr.pe.codda.impl.message.Echo.EchoServerCodec");
		expectedSet.add("kr.pe.codda.impl.message.Echo.EchoEncoder");
		expectedSet.add("kr.pe.codda.impl.message.Echo.EchoDecoder");
		
		try {
			Set<String> serverTaskDepencySet = ServerTaskManger.buildServerTaskDepencySet(messageID);
			
			log.info(serverTaskDepencySet.toString());
			
			assertEquals(expectedSet.size(), serverTaskDepencySet.size());		
			
			expectedSet.removeAll(serverTaskDepencySet);
			
			assertEquals(true, expectedSet.isEmpty());
			
		} catch (DynamicClassCallException e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testBuildServerTaskDepencySet_추가적인동적클래스가있는경우() {
		String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, 
						mainProjectName);
		String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		ServerClassLoaderFactory serverClassLoaderFactory = null;
		
		try {
			serverClassLoaderFactory = new ServerClassLoaderFactory(serverAPPINFClassPathString, projectResourcesPathString);
		} catch (IllegalArgumentException e) {
			fail("fail to create a instance of ServerClassLoaderFactory");
		}
		
		ServerTaskManger ServerTaskManger = new ServerTaskManger(serverClassLoaderFactory);
		
		
		String messageID = "MemberLoginReq";
		
		Set<String> expectedSet = new LinkedHashSet<String>();
		
		expectedSet.add("kr.pe.codda.impl.task.server.MemberLoginReqServerTask");
		
		expectedSet.add("kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq");
		expectedSet.add("kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqServerCodec");
		expectedSet.add("kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqDecoder");
				
		
		expectedSet.add("kr.pe.codda.impl.inner_message.MemberLoginDecryptionReq");
		
		expectedSet.add("kr.pe.codda.impl.message.MemberLoginRes.MemberLoginRes");		
		expectedSet.add("kr.pe.codda.impl.message.MemberLoginRes.MemberLoginResServerCodec");
		expectedSet.add("kr.pe.codda.impl.message.MemberLoginRes.MemberLoginResEncoder");		
		
		
		try {
			Set<String> serverTaskDepencySet = ServerTaskManger.buildServerTaskDepencySet(messageID);
			
			log.info(serverTaskDepencySet.toString());
			
			assertEquals(expectedSet.size(), serverTaskDepencySet.size());		
			
			expectedSet.removeAll(serverTaskDepencySet);
			
			assertEquals(true, expectedSet.isEmpty());
			
			
		} catch (DynamicClassCallException e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
}
