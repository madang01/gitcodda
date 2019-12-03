package kr.pe.codda.common.config.iteminfo;

import static org.junit.Assert.assertEquals;

import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfoManger;
import kr.pe.codda.common.util.CustomLogFormatter;

public class ItemIDInfoMangerTest {
	// private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
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
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testGetInstance() {
		ItemIDInfoManger.getInstance();
	}
	
	
	@Test
	public void testIsDisabled_true() {
		// String mainProjectName = "sample_test";
		// String installedPathString = installedPath.getAbsolutePath();
		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();
		
		String prefixOfItemID = "";
		Properties sourceProperties = new Properties();
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID, "SERVER");
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				"33333333333");
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				"33333333333");
		
		boolean expectedValue = true;
		
		boolean acutalValue = itemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		// log.info("acutalValue={}", acutalValue);
		
		assertEquals(expectedValue, acutalValue);
		
		
		acutalValue = itemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
		// SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID
		
	}
	
	@Test
	public void testIsDisabled_false() {
		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();
		
		String prefixOfItemID = "";
		Properties sourceProperties = new Properties();
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID, "FILE");
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				"33333333333");
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				"4444");
		
		boolean expectedValue = false;
		
		boolean acutalValue = itemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
		acutalValue = itemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
	}
	
	
	@Test
	public void testIsFileOrPathStringGetter() {
		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();
		boolean expectedValue = true;
		boolean acutalValue = false;
		
		expectedValue = true;
		acutalValue = itemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
		
		expectedValue = true;
		acutalValue = itemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
		
		expectedValue = false;
		acutalValue = itemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.CommonPartItemIDDefiner.JDF_MEMBER_LOGIN_PAGE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
	}	
	
}
