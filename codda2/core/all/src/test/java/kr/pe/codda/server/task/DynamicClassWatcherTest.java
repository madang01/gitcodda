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
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * @author Won Jonghoon
 *
 */
public class DynamicClassWatcherTest {

	private Logger log = Logger.getLogger(DynamicClassWatcherTest.class.getName());

	private static File serverDynamicClassPath = null;

	/**
	 * <임시 경로>/classes 경로 생성
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		String tempPathString = System.getProperty("java.io.tmpdir");
		String tempClassPathString = new StringBuilder().append(tempPathString).append(File.separatorChar)
				.append("classes").toString();

		serverDynamicClassPath = new File(tempClassPathString);

		boolean result = serverDynamicClassPath.mkdir();
		if (!result) {
			fail("fail to create a directroy[" + tempClassPathString + "]");
		}

	}

	/**
	 * 생성한 <임시 경로>/classes 경로 삭제
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		CommonStaticUtil.deleteDirectory(serverDynamicClassPath);
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

	/**
	 * <pre>
	 * <임시 경로>/classes/impl 와 <임시 경로>/classes/impl/message 경로를 만들어 
	 * <임시 경로>/classes/impl/tmp01.tmp 파일
	 * 그리고 <임시 경로>/classes/impl/message/tmp02.tmp 파일을 만든 후 
	 * <임시 경로>/classes  경로 감시자를 동작시킨 후 
	 *  <임시 경로>/classes/impl/tmp01.tmp 와 <임시 경로>/classes/impl/message/tmp02.tmp 을 각각 수정을 하여
	 *  수정 이벤트가 잘 전달되는지를 검사하다.
	 * </pre>
	 */
	@Test
	public void test_하위디렉토리2군데각파일1개씩수정_ok() {

		// <임시 경로>/classes/impl
		String implPathString = new StringBuilder().append(serverDynamicClassPath.getAbsolutePath())
				.append(File.separatorChar).append("impl").toString();

		File implPath = new File(implPathString);

		boolean result = implPath.mkdir();

		if (!result) {
			fail("fail to create a directroy[" + implPathString + "]");
		}

		// <임시 경로>/classes/impl/tmp01.tmp

		String tmp01FilePathString = new StringBuilder().append(implPathString).append(File.separatorChar)
				.append("tmp01.tmp").toString();

		File tmp01File = new File(tmp01FilePathString);

		try {
			CommonStaticUtil.createNewFile(tmp01File, "hello01", CommonStaticFinalVars.DEFUALT_CHARSET);
		} catch (Exception e) {
			fail("fail to create a file[" + tmp01FilePathString + "]");
		}

		// <임시 경로>/classes/impl/message
		String messagePathString = new StringBuilder().append(implPathString).append(File.separatorChar)
				.append("message").toString();

		File messagePath = new File(messagePathString);

		result = messagePath.mkdir();

		if (!result) {
			fail("fail to create a directroy[" + messagePathString + "]");
		}

		// <임시 경로>/classes/impl/message/tmp02.tmp

		String tmp02FilePathString = new StringBuilder().append(messagePathString).append(File.separatorChar)
				.append("tmp02.tmp").toString();

		File tmp02File = new File(tmp02FilePathString);

		try {
			CommonStaticUtil.createNewFile(tmp02File, "hello02", CommonStaticFinalVars.DEFUALT_CHARSET);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to create a file", e);
			fail("fail to create a file[" + tmp02FilePathString + "]");
		}

		class ServerDynamicClassFileModifyEventListenerMock implements AppInfClassFileModifyEventListener {
			private final ArrayDeque<String> expectedFileNameQueue = new ArrayDeque<String>();

			public ServerDynamicClassFileModifyEventListenerMock() {
				/**
				 * 테스트 시나리오 (1) classes/impl/tmp01.tmp 의 내용 수정 (2)
				 * classes/impl/message/tmp02.tmp 의 내용 수정
				 */
				expectedFileNameQueue.add(tmp01File.getAbsolutePath());
				expectedFileNameQueue.add(tmp02File.getAbsolutePath());
			}

			public void onAppInfClassFileModify(File ModifiedDynamicClassFile) {
				log.info("recevied file : " + ModifiedDynamicClassFile.getAbsolutePath());

				assertEquals(expectedFileNameQueue.removeFirst(), ModifiedDynamicClassFile.getAbsolutePath());
			}

			/**
			 * '가짜 서버 동적 클래스 수정 이벤트 수신자' 의 큐는 수정 예정된 파일 2개를 갖고 있고 전달 받은을때마다 소모된다. 수정 시나리오가
			 * 예상대로 2번 동작하면 0 을 반환한다.
			 */
			public int getQueueSize() {
				return expectedFileNameQueue.size();
			}
		}

		ServerDynamicClassFileModifyEventListenerMock serverDynamicClassFileModifyEventListenerMock = new ServerDynamicClassFileModifyEventListenerMock();

		try {
			DynamicClassWatcher dcw = new DynamicClassWatcher(serverDynamicClassPath, true,
					serverDynamicClassFileModifyEventListenerMock);

			dcw.start();
		} catch (Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("Not yet implemented");
		}

		try {
			CommonStaticUtil.overwriteFile(tmp01File, "hello01-01", CommonStaticFinalVars.DEFUALT_CHARSET);
		} catch (Exception e) {
			fail("fail to create a file[" + tmp02FilePathString + "]");
		}


		try {
			CommonStaticUtil.overwriteFile(tmp02File, "hello02-01", CommonStaticFinalVars.DEFUALT_CHARSET);
		} catch (Exception e) {
			fail("fail to create a file[" + tmp02FilePathString + "]");
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		/**
		 * '가짜 서버 동적 클래스 수정 이벤트 수신자' 의 큐는 수정 예정된 파일 2개를 갖고 있고 전달 받은을때마다 소모된다.
		 */
		assertEquals("테스트 시나리오상 수정한 파일 2개가 잘 전달되었는지 검사", 0,
				serverDynamicClassFileModifyEventListenerMock.getQueueSize());
	}

	@Test
	public void test_와처등록후새로운디렉토리와그밑에신규파일추가_ok() {
		class ServerDynamicClassFileModifyEventListenerMock implements AppInfClassFileModifyEventListener {

			public void onAppInfClassFileModify(File ModifiedDynamicClassFile) {
				log.info("recevied file : " + ModifiedDynamicClassFile.getAbsolutePath());
			}			
		}

		ServerDynamicClassFileModifyEventListenerMock serverDynamicClassFileModifyEventListenerMock = new ServerDynamicClassFileModifyEventListenerMock();

		try {
			DynamicClassWatcher dcw = new DynamicClassWatcher(serverDynamicClassPath, true,
					serverDynamicClassFileModifyEventListenerMock);

			dcw.start();
		} catch (Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("Not yet implemented");
		}
		
		
		// <임시 경로>/classes/impl
		String implPathString = new StringBuilder().append(serverDynamicClassPath.getAbsolutePath())
				.append(File.separatorChar).append("impl").toString();

		File implPath = new File(implPathString);

		boolean result = implPath.mkdir();

		if (!result) {
			fail("fail to create a directroy[" + implPathString + "]");
		}

		// <임시 경로>/classes/impl/tmp01.tmp

		String tmp01FilePathString = new StringBuilder().append(implPathString).append(File.separatorChar)
				.append("tmp01.tmp").toString();

		File tmp01File = new File(tmp01FilePathString);

		try {
			CommonStaticUtil.createNewFile(tmp01File, "hello01", CommonStaticFinalVars.DEFUALT_CHARSET);
		} catch (Exception e) {
			fail("fail to create a file[" + tmp01FilePathString + "]");
		}
		
		
		try {
			CommonStaticUtil.deleteDirectory(implPath);
		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
			fail("fail to delete a directory[" + implPathString + "]");
		}
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
				
		result = implPath.mkdir();

		if (!result) {
			fail("fail to create a directroy[" + implPathString + "]");
		}
		
		String tmp02FilePathString = new StringBuilder().append(implPathString).append(File.separatorChar)
				.append("tmp02.tmp").toString();

		File tmp02File = new File(tmp02FilePathString);

		try {
			CommonStaticUtil.createNewFile(tmp02File, "hello02", CommonStaticFinalVars.DEFUALT_CHARSET);
		} catch (Exception e) {
			fail("fail to create a file[" + tmp02FilePathString + "]");
		}
	}
}
