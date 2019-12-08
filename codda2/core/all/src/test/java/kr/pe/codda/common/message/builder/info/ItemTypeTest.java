package kr.pe.codda.common.message.builder.info;

import static org.junit.Assert.assertEquals;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.SingleItemType;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class ItemTypeTest {
	
private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
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
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_ordinal와itemTypeID같은지검사() {
		SingleItemType singleItemType = SingleItemType.UNSIGNED_SHORT;
		
		assertEquals("ItemTypeID 를 순차적으로 잘 정의했는지  테스트", singleItemType.ordinal(), singleItemType.getItemTypeID());
	}
	
	
	@Test
	public void testGetItemTypeID_ItemTypeID를순차적으로잘정의했는지검사() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType singleItemType = singleItemTypes[i];
			assertEquals("ItemTypeID 를 순차적으로 잘 정의했는지  테스트", i, singleItemType.getItemTypeID());
		}
	}
	
	
	@Test
	public void test() {
		SingleItemType singleItemType = SingleItemType.UNSIGNED_SHORT;
		
		log.info("singleItemType.name=" + singleItemType.name());
	}
}
