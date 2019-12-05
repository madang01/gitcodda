package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CustomLogFormatter;

public class GeneralConverterReturningRegularFileTest {
	
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private GeneralConverterReturningRegularFile nativeValueConverter = null;
	private File returnedValue = null;

	// private String canonicalPathStringOfExpectedValue = null;
	private File expectedValue = null;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		CustomLogFormatter formatter = new CustomLogFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Before
	public void setup() {
		boolean isWritePermissionChecking = true;

		try {
			expectedValue = File.createTempFile("test", "");
			expectedValue.deleteOnExit();
		} catch (IOException e1) {
			fail("fail to create a temp file, errormessage=" + e1.getMessage());
		}

		log.info("temp file=[" + expectedValue.getAbsolutePath()+"]");

		boolean isSuccess = expectedValue
				.setWritable(isWritePermissionChecking);
		if (!isSuccess) {
			fail("fail to set writable[" + isWritePermissionChecking + "]");
		}

		/*
		 * try { canonicalPathStringOfExpectedValue =
		 * expectedValue.getCanonicalPath(); } catch (IOException e) {
		 * fail("fail to get a canonical path of a temp file, errormessage="
		 * +e.getMessage()); }
		 */

		nativeValueConverter = new GeneralConverterReturningRegularFile(
				isWritePermissionChecking);
	}
	
	@After
	public void tearDown() throws Exception {
	}
	

	
	@Test
	public void testValueOf_OK() {
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue
					.getAbsolutePath());
			
			assertEquals("the expected value comparison", returnedValue.getCanonicalPath(), expectedValue.getCanonicalPath());
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}

	}

	
	@Test
	public void testValueOf_theParameterItemValueIsNull() {
		try {
			returnedValue = nativeValueConverter.valueOf(null);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	
	@Test
	public void testValueOf_theParameterItemValueIsEmpty() {
		try {
			returnedValue = nativeValueConverter.valueOf("");
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter itemValue is empty";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}

	}

	@Ignore
	public void testValueOf_theParameterItemValueIsUnreadableFile() {
		/**
		 * Warning! 파일 읽기 권한 검사 테스트는 파일 읽기 권한 비활성이 필요한데 이 기능은 운영체제 종속된 기능이므로 생략함.
		 * "Window7 32bit Home Premium K" 에서는 읽기 권한이 강제적으로 활성화 되어 있어 이를 비활성화
		 * 시킬수없다. 비활성 시킬 수 없지만 "읽기 거부 권한"이 있어 이를 활성화 시키면 읽기를 막을 수 있다. 다만 이렇게 할
		 * 경우 File.canRead 로는 알 수 없고 실제적인 파일 읽기 작업중에 어렴품이 알 수 있다. 옐르 들면
		 * FileInputStream 를 새로 생성할때 "액세스가 거부되었습니다" 라는 사유로 FileNotFoundException
		 * 이 발생한다. 이것에 대한 테스틑 java.io.FileTest#
		 * testCanRead_Win7_UserWhoIsNotAdmin_ReadingYesAndReadingDenyYesFile
		 * 참조할것.
		 */
	}

	@Test
	public void testValueOf_theParameterItemValueIsBadFileThatIsDirectory() {
		String itemValue = ".";
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the file that is the parameter itemValue[")
					.append(itemValue)
					.append("] is not a regular file").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testValueOf_theParameterItemValueIsBadFileThatDoestNotExist() {
		String itemValue = "aabb$sd$s";
		
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the file that is the parameter itemValue[")
					.append(itemValue)
					.append("] does not exist").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	

	@Test
	public void testValueOf_theParameterItemValueIsUnwritableFile() {

		boolean isSuccess = expectedValue.setWritable(false);
		if (!isSuccess) {
			String errorMessage = "fail to set writable[false]";
			log.warning(errorMessage);
			fail(errorMessage);
		}

		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue
					.getAbsolutePath());
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the file(=the parameter itemValue[")
					.append(expectedValue.getAbsolutePath())
					.append("]) doesn't hava permission to write").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
}
