package kr.pe.codda.common.config.part;

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
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.SequencedProperties;

public class SessionkeyPartConfigurationTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	/**
	 * @throws java.lang.Exception
	 */
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
	public void testToValueForRSAKeypairSource() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setRSAKeypairSource(SessionKey.RSAKeypairSourceType.FILE);
		
		actualSessionkeyPartConfiguration.toPropertiesForRSAKeypairSource(sourceSequencedProperties);
		
		SessionkeyPartConfiguration expectedCommonPartConfiguration = new SessionkeyPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForRSAKeypairSource(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getRSAKeypairSource(), actualSessionkeyPartConfiguration.getRSAKeypairSource());
	}
	
	@Test
	public void testToPropertiesForRSAKeypairSource() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setRSAKeypairSource(SessionKey.RSAKeypairSourceType.SERVER);
		
		actualSessionkeyPartConfiguration.toPropertiesForRSAKeypairSource(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForRSAPublickeyFile() {		
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tmp", ".dat");
			tempFile.deleteOnExit();
		} catch (IOException e) {			
			fail("fail to create a temp file");
		}
		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setRSAPublickeyFile(tempFile.getAbsoluteFile());
		
		actualSessionkeyPartConfiguration.toPropertiesForRSAPublickeyFile(sourceSequencedProperties);
		
		SessionkeyPartConfiguration expectedCommonPartConfiguration = new SessionkeyPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForRSAPublickeyFile(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRSAPublickeyFile(), actualSessionkeyPartConfiguration.getRSAPublickeyFile());
	}
	
	@Test
	public void testToPropertiesForRSAPublickeyFile() {	
		
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tmp", ".dat");
			tempFile.deleteOnExit();
		} catch (IOException e) {			
			fail("fail to create a temp file");
		}
		
		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setRSAPublickeyFile(tempFile);
		
		actualSessionkeyPartConfiguration.toPropertiesForRSAPublickeyFile(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForRSAPrivatekeyFile() {		
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tmp", ".dat");
			tempFile.deleteOnExit();
		} catch (IOException e) {			
			fail("fail to create a temp file");
		}
		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setRSAPrivatekeyFile(tempFile.getAbsoluteFile());
		
		actualSessionkeyPartConfiguration.toPropertiesForRSAPrivatekeyFile(sourceSequencedProperties);
		
		SessionkeyPartConfiguration expectedCommonPartConfiguration = new SessionkeyPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForRSAPrivatekeyFile(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRSAPrivatekeyFile(), actualSessionkeyPartConfiguration.getRSAPrivatekeyFile());
	}
	
	@Test
	public void testToPropertiesForRSAPrivatekeyFile() {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tmp", ".dat");
			tempFile.deleteOnExit();
		} catch (IOException e) {			
			fail("fail to create a temp file");
		}
		
		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setRSAPrivatekeyFile(tempFile);
		
		actualSessionkeyPartConfiguration.toPropertiesForRSAPrivatekeyFile(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForRSAKeySize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setRSAKeySize(2048);
		
		actualSessionkeyPartConfiguration.toPropertiesForRSAKeySize(sourceSequencedProperties);
		
		SessionkeyPartConfiguration expectedCommonPartConfiguration = new SessionkeyPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForRSAKeySize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRSAKeySize(), actualSessionkeyPartConfiguration.getRSAKeySize());
	}
	
	@Test
	public void testToPropertiesForRSAKeySize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setRSAKeySize(2048);
		
		actualSessionkeyPartConfiguration.toPropertiesForRSAKeySize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForSymmetricKeyAlgorithm() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setSymmetricKeyAlgorithm("DES");
		
		actualSessionkeyPartConfiguration.toPropertiesForSymmetricKeyAlgorithm(sourceSequencedProperties);
		
		
		SessionkeyPartConfiguration expectedCommonPartConfiguration = new SessionkeyPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForSymmetricKeyAlgorithm(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getSymmetricKeyAlgorithm(), actualSessionkeyPartConfiguration.getSymmetricKeyAlgorithm());
	}
	
	@Test
	public void testToPropertiesForSymmetricKeyAlgorithm() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setSymmetricKeyAlgorithm("DES");
		
		actualSessionkeyPartConfiguration.toPropertiesForSymmetricKeyAlgorithm(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForSymmetricKeySize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setSymmetricKeySize(24);
		
		actualSessionkeyPartConfiguration.toPropertiesForSymmetricKeySize(sourceSequencedProperties);
		
		
		SessionkeyPartConfiguration expectedCommonPartConfiguration = new SessionkeyPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForSymmetricKeySize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getSymmetricKeySize(), actualSessionkeyPartConfiguration.getSymmetricKeySize());
	}
	
	@Test
	public void testToPropertiesForSymmetricKeySize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setSymmetricKeySize(24);
		
		actualSessionkeyPartConfiguration.toPropertiesForSymmetricKeySize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForSymmetricIVSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setSymmetricIVSize(24);
		
		actualSessionkeyPartConfiguration.toPropertiesForSymmetricIVSize(sourceSequencedProperties);
		
		
		SessionkeyPartConfiguration expectedCommonPartConfiguration = new SessionkeyPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForSymmetricIVSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getSymmetricIVSize(), actualSessionkeyPartConfiguration.getSymmetricIVSize());
	}
	
	@Test
	public void testToPropertiesForSymmetricIVSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		SessionkeyPartConfiguration actualSessionkeyPartConfiguration = new SessionkeyPartConfiguration();
		actualSessionkeyPartConfiguration.setSymmetricIVSize(24);
		
		actualSessionkeyPartConfiguration.toPropertiesForSymmetricIVSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
}
