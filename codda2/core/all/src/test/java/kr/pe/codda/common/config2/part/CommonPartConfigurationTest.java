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
package kr.pe.codda.common.config2.part;

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

/**
 * @author Won Jonghoon
 *
 */
public class CommonPartConfigurationTest {
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
	public void testToValueForJDFMemberLoginPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		// SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		// commonPartConfiguration.setJdfAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		actualCommonPartConfiguration.setJdfMemberLoginPage("/sitemenu/member/MemberLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFMemberLoginPage(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForJDFMemberLoginPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJdfMemberLoginPage(), actualCommonPartConfiguration.getJdfMemberLoginPage());
	}

	
	@Test
	public void testToPropertiesForJDFMemberLoginPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		// SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		// commonPartConfiguration.setJdfAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		actualCommonPartConfiguration.setJdfMemberLoginPage("/sitemenu/member/MemberLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFMemberLoginPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForJDFAdminLoginPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJdfAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFAdminLoginPage(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForJDFAdminLoginPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJdfAdminLoginPage(), actualCommonPartConfiguration.getJdfAdminLoginPage());
	}
	
	
	@Test
	public void testToPropertiesForJDFAdminLoginPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJdfAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFAdminLoginPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForJDFSessionKeyRedirectPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJdfSessionKeyRedirectPage("/sessionKeyRedirect.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFSessionKeyRedirectPage(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForJDFSessionKeyRedirectPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJdfSessionKeyRedirectPage(), actualCommonPartConfiguration.getJdfSessionKeyRedirectPage());
	}
	
	@Test
	public void testToPropertiesForJDFSessionKeyRedirectPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJdfSessionKeyRedirectPage("/sessionKeyRedirect.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFSessionKeyRedirectPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForJDFErrorMessagePage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJdfErrorMessagePage("/errorMessagePage.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFErrorMessagePage(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForJDFErrorMessagePage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJdfErrorMessagePage(), actualCommonPartConfiguration.getJdfErrorMessagePage());
	}
	
	@Test
	public void testToPropertiesForJDFErrorMessagePage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJdfErrorMessagePage("/errorMessagePage.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFErrorMessagePage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForJDFServletTrace() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJdfServletTrace(true);
		
		actualCommonPartConfiguration.toPropertiesForJDFServletTrace(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForJDFServletTrace(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJdfServletTrace(), actualCommonPartConfiguration.getJdfServletTrace());
	}
	
	
	@Test
	public void testToPropertiesForJDFServletTrace() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJdfServletTrace(true);
		
		actualCommonPartConfiguration.toPropertiesForJDFServletTrace(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForRSAKeypairSourceOfSessionKey() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRsaKeypairSourceOfSessionKey(SessionKey.RSAKeypairSourceType.FILE);
		
		actualCommonPartConfiguration.toPropertiesForRSAKeypairSourceOfSessionKey(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForRSAKeypairSourceOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getRsaKeypairSourceOfSessionKey(), actualCommonPartConfiguration.getRsaKeypairSourceOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForRSAKeypairSourceOfSessionKey() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRsaKeypairSourceOfSessionKey(SessionKey.RSAKeypairSourceType.SERVER);
		
		actualCommonPartConfiguration.toPropertiesForRSAKeypairSourceOfSessionKey(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForRSAPublickeyFileOfSessionKey() {		
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tmp", ".dat");
			tempFile.deleteOnExit();
		} catch (IOException e) {			
			fail("fail to create a temp file");
		}
		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRsaPublickeyFileOfSessionKey(tempFile.getAbsoluteFile());
		
		actualCommonPartConfiguration.toPropertiesForRSAPublickeyFileOfSessionKey(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForRSAPublickeyFileOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRsaPublickeyFileOfSessionKey(), actualCommonPartConfiguration.getRsaPublickeyFileOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForRSAPublickeyFileOfSessionKey() {	
		
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tmp", ".dat");
			tempFile.deleteOnExit();
		} catch (IOException e) {			
			fail("fail to create a temp file");
		}
		
		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRsaPublickeyFileOfSessionKey(tempFile);
		
		actualCommonPartConfiguration.toPropertiesForRSAPublickeyFileOfSessionKey(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForRSAPrivatekeyFileOfSessionKey() {		
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tmp", ".dat");
			tempFile.deleteOnExit();
		} catch (IOException e) {			
			fail("fail to create a temp file");
		}
		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRsaPrivatekeyFileOfSessionKey(tempFile.getAbsoluteFile());
		
		actualCommonPartConfiguration.toPropertiesForRSAPrivatekeyFileOfSessionKey(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForRSAPrivatekeyFileOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRsaPrivatekeyFileOfSessionKey(), actualCommonPartConfiguration.getRsaPrivatekeyFileOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForRSAPrivatekeyFileOfSessionKey() {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tmp", ".dat");
			tempFile.deleteOnExit();
		} catch (IOException e) {			
			fail("fail to create a temp file");
		}
		
		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRsaPrivatekeyFileOfSessionKey(tempFile);
		
		actualCommonPartConfiguration.toPropertiesForRSAPrivatekeyFileOfSessionKey(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForRSAKeySizeOfSessionKey() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRsaKeySizeOfSessionKey(2048);
		
		actualCommonPartConfiguration.toPropertiesForRSAKeySizeOfSessionKey(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForRSAKeySizeOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRsaKeySizeOfSessionKey(), actualCommonPartConfiguration.getRsaKeySizeOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForRSAKeySizeOfSessionKey() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRsaKeySizeOfSessionKey(2048);
		
		actualCommonPartConfiguration.toPropertiesForRSAKeySizeOfSessionKey(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForSymmetricKeyAlgorithmOfSessionKey() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setSymmetricKeyAlgorithmOfSessionKey("DES");
		
		actualCommonPartConfiguration.toPropertiesForSymmetricKeyAlgorithmOfSessionKey(sourceSequencedProperties);
		
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForSymmetricKeyAlgorithmOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getSymmetricKeyAlgorithmOfSessionKey(), actualCommonPartConfiguration.getSymmetricKeyAlgorithmOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForSymmetricKeyAlgorithmOfSessionKey() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setSymmetricKeyAlgorithmOfSessionKey("DES");
		
		actualCommonPartConfiguration.toPropertiesForSymmetricKeyAlgorithmOfSessionKey(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForSymmetricKeySizeOfSessionKey() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setSymmetricKeySizeOfSessionKey(24);
		
		actualCommonPartConfiguration.toPropertiesForSymmetricKeySizeOfSessionKey(sourceSequencedProperties);
		
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForSymmetricKeySizeOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getSymmetricKeySizeOfSessionKey(), actualCommonPartConfiguration.getSymmetricKeySizeOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForSymmetricKeySizeOfSessionKey() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setSymmetricKeySizeOfSessionKey(24);
		
		actualCommonPartConfiguration.toPropertiesForSymmetricKeySizeOfSessionKey(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForSymmetricIVSizeOfSessionKey() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setSymmetricIVSizeOfSessionKey(24);
		
		actualCommonPartConfiguration.toPropertiesForSymmetricIVSizeOfSessionKey(sourceSequencedProperties);
		
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.toValueForSymmetricIVSizeOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getSymmetricIVSizeOfSessionKey(), actualCommonPartConfiguration.getSymmetricIVSizeOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForSymmetricIVSizeOfSessionKey() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setSymmetricIVSizeOfSessionKey(24);
		
		actualCommonPartConfiguration.toPropertiesForSymmetricIVSizeOfSessionKey(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
}
