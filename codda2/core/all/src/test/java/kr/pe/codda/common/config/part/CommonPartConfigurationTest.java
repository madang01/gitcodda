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

import kr.pe.codda.common.config.part.CommonPartConfiguration;
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
		// commonPartConfiguration.setJDFAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		actualCommonPartConfiguration.setJDFMemberLoginPage("/sitemenu/member/MemberLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFMemberLoginPage(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForJDFMemberLoginPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJDFMemberLoginPage(), actualCommonPartConfiguration.getJDFMemberLoginPage());
	}

	
	@Test
	public void testToPropertiesForJDFMemberLoginPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		// SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		// commonPartConfiguration.setJDFAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		actualCommonPartConfiguration.setJDFMemberLoginPage("/sitemenu/member/MemberLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFMemberLoginPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForJDFAdminLoginPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJDFAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFAdminLoginPage(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForJDFAdminLoginPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJDFAdminLoginPage(), actualCommonPartConfiguration.getJDFAdminLoginPage());
	}
	
	
	@Test
	public void testToPropertiesForJDFAdminLoginPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJDFAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFAdminLoginPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForJDFSessionKeyRedirectPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJDFSessionKeyRedirectPage("/sessionKeyRedirect.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFSessionKeyRedirectPage(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForJDFSessionKeyRedirectPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJDFSessionKeyRedirectPage(), actualCommonPartConfiguration.getJDFSessionKeyRedirectPage());
	}
	
	@Test
	public void testToPropertiesForJDFSessionKeyRedirectPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJDFSessionKeyRedirectPage("/sessionKeyRedirect.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFSessionKeyRedirectPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForJDFErrorMessagePage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJDFErrorMessagePage("/errorMessagePage.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFErrorMessagePage(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForJDFErrorMessagePage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJDFErrorMessagePage(), actualCommonPartConfiguration.getJDFErrorMessagePage());
	}
	
	@Test
	public void testToPropertiesForJDFErrorMessagePage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJDFErrorMessagePage("/errorMessagePage.jsp");
		
		actualCommonPartConfiguration.toPropertiesForJDFErrorMessagePage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForJDFServletTrace() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJDFServletTrace(true);
		
		actualCommonPartConfiguration.toPropertiesForJDFServletTrace(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForJDFServletTrace(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJDFServletTrace(), actualCommonPartConfiguration.getJDFServletTrace());
	}
	
	
	@Test
	public void testToPropertiesForJDFServletTrace() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setJDFServletTrace(true);
		
		actualCommonPartConfiguration.toPropertiesForJDFServletTrace(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForRSAKeypairSourceOfSessionKey() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRSAKeypairSourceOfSessionKey(SessionKey.RSAKeypairSourceType.FILE);
		
		actualCommonPartConfiguration.toPropertiesForRSAKeypairSourceOfSessionKey(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForRSAKeypairSourceOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getRSAKeypairSourceOfSessionKey(), actualCommonPartConfiguration.getRSAKeypairSourceOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForRSAKeypairSourceOfSessionKey() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRSAKeypairSourceOfSessionKey(SessionKey.RSAKeypairSourceType.SERVER);
		
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
		actualCommonPartConfiguration.setRSAPublickeyFileOfSessionKey(tempFile.getAbsoluteFile());
		
		actualCommonPartConfiguration.toPropertiesForRSAPublickeyFileOfSessionKey(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForRSAPublickeyFileOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRSAPublickeyFileOfSessionKey(), actualCommonPartConfiguration.getRSAPublickeyFileOfSessionKey());
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
		actualCommonPartConfiguration.setRSAPublickeyFileOfSessionKey(tempFile);
		
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
		actualCommonPartConfiguration.setRSAPrivatekeyFileOfSessionKey(tempFile.getAbsoluteFile());
		
		actualCommonPartConfiguration.toPropertiesForRSAPrivatekeyFileOfSessionKey(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForRSAPrivatekeyFileOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRSAPrivatekeyFileOfSessionKey(), actualCommonPartConfiguration.getRSAPrivatekeyFileOfSessionKey());
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
		actualCommonPartConfiguration.setRSAPrivatekeyFileOfSessionKey(tempFile);
		
		actualCommonPartConfiguration.toPropertiesForRSAPrivatekeyFileOfSessionKey(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForRSAKeySizeOfSessionKey() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRSAKeySizeOfSessionKey(2048);
		
		actualCommonPartConfiguration.toPropertiesForRSAKeySizeOfSessionKey(sourceSequencedProperties);
		
		CommonPartConfiguration expectedCommonPartConfiguration = new CommonPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForRSAKeySizeOfSessionKey(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getRSAKeySizeOfSessionKey(), actualCommonPartConfiguration.getRSAKeySizeOfSessionKey());
	}
	
	@Test
	public void testToPropertiesForRSAKeySizeOfSessionKey() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		CommonPartConfiguration actualCommonPartConfiguration = new CommonPartConfiguration();
		actualCommonPartConfiguration.setRSAKeySizeOfSessionKey(2048);
		
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
			expectedCommonPartConfiguration.fromPropertiesForSymmetricKeyAlgorithmOfSessionKey(sourceSequencedProperties);
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
			expectedCommonPartConfiguration.fromPropertiesForSymmetricKeySizeOfSessionKey(sourceSequencedProperties);
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
			expectedCommonPartConfiguration.fromPropertiesForSymmetricIVSizeOfSessionKey(sourceSequencedProperties);
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
