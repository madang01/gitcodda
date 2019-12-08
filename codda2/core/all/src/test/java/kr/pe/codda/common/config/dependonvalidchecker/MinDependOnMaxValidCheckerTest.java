package kr.pe.codda.common.config.dependonvalidchecker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.util.Properties;
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

import kr.pe.codda.common.config.AbstractDependencyValidator;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningCharset;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class MinDependOnMaxValidCheckerTest {
	private static Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final static String prefixOfPart = "project.sample_base.";
	private final static Properties configFileProperties = new Properties();

	private ItemIDInfo<Long> longTypeDependedItemIDInfo = null;
	private ItemIDInfo<Long> longTypeDependentItemIDInfo = null;
	private AbstractDependencyValidator longTypeMinDependOnMaxValidChecker = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		JDKLoggerCustomFormatter formatter = new JDKLoggerCustomFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setup() {
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			longTypeDependedItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "22", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							2L, Long.MAX_VALUE - 10));

			configFileProperties.put(prefixOfPart
					+ dependentTargetItemID, "" + (Long.MAX_VALUE-30));

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			longTypeDependentItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수", "11", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1L, Long.MAX_VALUE - 11));

			configFileProperties.put(prefixOfPart
					+ dependentSourceItemID, "1");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (CoddaConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		try {
			longTypeMinDependOnMaxValidChecker = new MinAndMaxDependencyValidator<Long>(
					longTypeDependentItemIDInfo, longTypeDependedItemIDInfo,
					Long.class);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (CoddaConfigurationException e) {
			fail(e.getMessage());
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testConstructor_BadGeneralType_dependentTargetItemIDInfo() {
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			ItemIDInfo<?> dependentTargetItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "22", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							2L, Long.MAX_VALUE - 10));


			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			ItemIDInfo<?> dependentSourceItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수", "11", true,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							2, Integer.MAX_VALUE - 10));			
			
			@SuppressWarnings("unchecked")
			AbstractDependencyValidator minDependOnMaxValidChecker = new MinAndMaxDependencyValidator<Integer>(
					(ItemIDInfo<Integer>)dependentSourceItemIDInfo,
					(ItemIDInfo<Integer>)dependentTargetItemIDInfo,
					Integer.class);
			
			minDependOnMaxValidChecker.hashCode();

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {	
			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "this class's generic type T[java.lang.Integer] is different from the parameter dependentTargetConfigItem[server.pool.executor_processor.max_size.value]'s generic type T[java.lang.Long]";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unkwnon error", e);
		}
	}
	
	@Test
	public void testConstructor_BadGeneralType_dependentSourceItemIDInfo() {
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			ItemIDInfo<?> dependentTargetItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "22", true,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							2, Integer.MAX_VALUE - 10));


			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			ItemIDInfo<?> dependentSourceItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수", "11", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							2L, Long.MAX_VALUE - 10));
			
			@SuppressWarnings({"unchecked" })
			AbstractDependencyValidator minDependOnMaxValidChecker = new MinAndMaxDependencyValidator<Integer>(
					(ItemIDInfo<Integer>)dependentSourceItemIDInfo,
					(ItemIDInfo<Integer>)dependentTargetItemIDInfo,
					Integer.class);
			
			minDependOnMaxValidChecker.hashCode();

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {	
			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "this class's generic type T[java.lang.Integer] is different from the parameter dependentSourceConfigItem[server.pool.executor_processor.size.value]'s generic type T[java.lang.Long]";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unkwnon error", e);
		}		
	}
	
	@Test
	public void testConstructor_BadInstance_MaxNativeValueConverter() {
		ItemIDInfo<?> charsetTypeMaxItemIDInfo = null;
		ItemIDInfo<?> integerTypeMinItemIDInfo = null;
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			charsetTypeMaxItemIDInfo = new ItemIDInfo<Charset>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "1", false,
					new GeneralConverterReturningCharset());

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			integerTypeMinItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수",
					"1",
					false,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));

		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}

		try {
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependencyValidator minDependOnMaxValidChecker = new MinAndMaxDependencyValidator<Integer>(
					(ItemIDInfo<Integer>)integerTypeMinItemIDInfo, (ItemIDInfo<Integer>)charsetTypeMaxItemIDInfo,
					Integer.class);			
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter dependentTargetConfigItem[server.pool.executor_processor.max_size.value]'s dependentTargetItemValueConverter[kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningCharset] is not a instance of AbstractMinMaxConverter";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testConstructor_BadInstance_MinNativeValueConverter() {
		ItemIDInfo<?> integerTypeMaxItemIDInfo = null;
		ItemIDInfo<?> charsetTypeMinItemIDInfo = null;
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			integerTypeMaxItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "1", false,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			charsetTypeMinItemIDInfo = new ItemIDInfo<Charset>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수",
					"1",
					false,
					new GeneralConverterReturningCharset());

		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}

		try {
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependencyValidator minDependOnMaxValidChecker = new MinAndMaxDependencyValidator<Integer>(
					(ItemIDInfo<Integer>)charsetTypeMinItemIDInfo, (ItemIDInfo<Integer>)integerTypeMaxItemIDInfo,
					Integer.class);			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter dependentSourceConfigItem[server.pool.executor_processor.size.value]'s dependentSourceItemValueConverter[kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningCharset] is not a instance of AbstractMinMaxConverter";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testConstructor_GenericTypeDifferent_MaxNativeValueConverter() {
		ItemIDInfo<?> longTypeMaxItemIDInfo = null;
		ItemIDInfo<?> integerTypeMinItemIDInfo = null;
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			longTypeMaxItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "1", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1L, Long.MAX_VALUE));

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			integerTypeMinItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수",
					"1",
					true,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));

		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}

		try {
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependencyValidator minDependOnMaxValidChecker = new MinAndMaxDependencyValidator<Integer>(
					(ItemIDInfo<Integer>)integerTypeMinItemIDInfo, (ItemIDInfo<Integer>)longTypeMaxItemIDInfo,
					Integer.class);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "this class's generic type T[java.lang.Integer] is different from the parameter dependentTargetConfigItem[server.pool.executor_processor.max_size.value]'s generic type T[java.lang.Long]";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testConstructor_GenericTypeDifferent_MinNativeValueConverter() {
		ItemIDInfo<Integer> integerTypeMaxItemIDInfo = null;
		ItemIDInfo<?> longTypeMinItemIDInfo = null;
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			integerTypeMaxItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수",
					"1",
					true,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			longTypeMinItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수", "1", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1L, Long.MAX_VALUE));

		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}

		try {
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependencyValidator minDependOnMaxValidChecker = new MinAndMaxDependencyValidator<Integer>(
					(ItemIDInfo<Integer>)longTypeMinItemIDInfo, integerTypeMaxItemIDInfo,
					Integer.class);
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "this class's generic type T[java.lang.Integer] is different from the parameter dependentSourceConfigItem[server.pool.executor_processor.size.value]'s generic type T[java.lang.Long]";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testConstructor_NullParameter_dependentSourceItemIDInfo()
			throws Exception {
		try {
			new MinAndMaxDependencyValidator<Long>(
					null, longTypeDependedItemIDInfo, Long.class);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter dependentSourceItemInfo is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testConstructor_NullParameter_dependentTargetItemIDInfo()
			throws Exception {
		try {
			new MinAndMaxDependencyValidator<Long>(
					longTypeDependentItemIDInfo, null, Long.class);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter dependentTargetItemInfo is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testConstructor_NullParameter_genericTypeClass()
			throws Exception {
		try {
			new MinAndMaxDependencyValidator<Long>(
					longTypeDependentItemIDInfo, longTypeDependedItemIDInfo,
					null);
			
			fail ("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter genericTypeClass is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testIsValid_NullParameter_sourceProperties()
			throws Exception {
		try {
			longTypeMinDependOnMaxValidChecker.isValid(null, prefixOfPart);
			
			fail ("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter sourceProperties is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testIsValid_NullParameter_prefixOfPart() {
		try {
			longTypeMinDependOnMaxValidChecker.isValid(
					configFileProperties, null);
			
			fail ("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter prefixOfDependentSourceItemID is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testIsValid_ValidButBadParameter_sourceProperties_NoDependentSourceItemKey() {
		String dependentSourceItemID = "server.pool.executor_processor.size.value";
		String dependendtSourceItemKey = prefixOfPart + dependentSourceItemID;
		configFileProperties.remove(dependendtSourceItemKey);

		try {
			longTypeMinDependOnMaxValidChecker.isValid(
					configFileProperties, prefixOfPart);
			fail ("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter dependentSourceItemKey[project.sample_base.server.pool.executor_processor.size.value]) does not exist at the paramerter sourceProperties where the config file was loaded";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testIsValid_ValidButBadParameter_sourceProperties_NoDependentTargetItemKey() {
		String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
		String dependentTargetItemKey = prefixOfPart + dependentTargetItemID;
		configFileProperties.remove(dependentTargetItemKey);
		
		try {
			longTypeMinDependOnMaxValidChecker.isValid(
					configFileProperties, prefixOfPart);
			
			fail ("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter dependentTargetItemKey[project.sample_base.server.pool.executor_processor.max_size.value]) does not exist at the paramerter sourceProperties where the config file was loaded";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
	
	@Ignore 
	@Test
	public void testIsValid_ValidButBadParameter_prefixOfPart() throws Exception {
		/**
		 * ValidButBadParameter 테스트는 파라미터 값이 유효하지만 잘못된 값을 가지는 경우에 대한 테스트로
		 * isValid 메소드에서는 파라미터 sourceProperties 에 키 값이 존재 하지 않는 경우에 대한 테스트이다.
		 * 키 값이 존재 하지 안흔 경우는 2가지로 나뉘는데,
		 * 첫번째 키가 잘못되었을 경우와
		 * 마지막 두번째 있어야 할 키값이 없는 경우이다.
		 *  
		 * 키는 두개로 구성되는데 참고2 처럼 있어야할 키 값이 없는 경우로 각각 테스를 수행하므로
		 * 파마미터 prefixOfPart 가 잘못된 값을 가져 키값이 잘못되는 경우에 대한 테스트는 생략한다.
		 * 설정 파일의 경우 키 값이 없는 경우가 대부분 이므로 있어야할 키가 없는 테스트인 참고2를 우선하였다.
		 * 
		 * --- 참고1) sourceProperties 의 키 ---
		 * (1) dependentSourceItemKey = 파라미터 prefixOfPart + 생성자 파라미터 dependentSourceItemIDInfo.getItemID();
		 * (2) dependentTargetItemKey = 파라미터 prefixOfPart + 생성자 파라미터 dependentTargetItemIDInfo.getItemID();
		 *
		 * --- 참고2) 파라미터 sourceProperties 에 있어야할 키값이 없는 경우 테스트  ---
		 * (1) testIsValid_ValidButBadParameter_sourceProperties_NoDependentSourceItemKey
		 * (2) testIsValid_ValidButBadParameter_sourceProperties_NoDependentTargetItemKey
		 */
	}
	
	
	@Test
	public void testIsValid_ExpectedValueComparison() {				
		try {
			boolean returnedValue = longTypeMinDependOnMaxValidChecker.isValid(
					configFileProperties, prefixOfPart);
			
			final boolean expectedValue = true;
			
			assertEquals("the expected value comparison", returnedValue, expectedValue);
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}
	
	
}
