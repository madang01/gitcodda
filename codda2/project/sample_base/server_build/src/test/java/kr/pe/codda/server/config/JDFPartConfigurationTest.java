package kr.pe.codda.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import kr.pe.codda.common.util.SequencedProperties;

public class JDFPartConfigurationTest {
	org.slf4j.Logger log = LoggerFactory.getLogger(JDFPartConfigurationTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		java.util.logging.LogManager.getLogManager().reset();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
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
	public void testToValueForJDFMemberLoginPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		// SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		// commonPartConfiguration.setJDFAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		actualCommonPartConfiguration.setJDFMemberLoginPage("/sitemenu/member/MemberLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForMemberLoginPage(sourceSequencedProperties);
		
		JDFPartConfiguration expectedCommonPartConfiguration = new JDFPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForMemberLoginPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.warn("fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}		
		
		assertEquals(expectedCommonPartConfiguration.getJDFMemberLoginPage(), actualCommonPartConfiguration.getJDFMemberLoginPage());
	}

	
	@Test
	public void testToPropertiesForJDFMemberLoginPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		// SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		// commonPartConfiguration.setJDFAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		actualCommonPartConfiguration.setJDFMemberLoginPage("/sitemenu/member/MemberLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForMemberLoginPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
		
	}
	
	
	@Test
	public void testToValueForJDFAdminLoginPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		actualCommonPartConfiguration.setJDFAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForAdminLoginPage(sourceSequencedProperties);
		
		JDFPartConfiguration expectedCommonPartConfiguration = new JDFPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForAdminLoginPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.warn("fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJDFAdminLoginPage(), actualCommonPartConfiguration.getJDFAdminLoginPage());
	}
	
	
	@Test
	public void testToPropertiesForJDFAdminLoginPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		actualCommonPartConfiguration.setJDFAdminLoginPage("/sitemenu/member/AdminLoginInput.jsp");
		
		actualCommonPartConfiguration.toPropertiesForAdminLoginPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForJDFSessionKeyRedirectPage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		actualCommonPartConfiguration.setJDFSessionKeyRedirectPage("/sessionKeyRedirect.jsp");
		
		actualCommonPartConfiguration.toPropertiesForSessionKeyRedirectPage(sourceSequencedProperties);
		
		JDFPartConfiguration expectedCommonPartConfiguration = new JDFPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForSessionKeyRedirectPage(sourceSequencedProperties);
		} catch (Exception e) {
			log.warn("fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJDFSessionKeyRedirectPage(), actualCommonPartConfiguration.getJDFSessionKeyRedirectPage());
	}
	
	@Test
	public void testToPropertiesForJDFSessionKeyRedirectPage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		actualCommonPartConfiguration.setJDFSessionKeyRedirectPage("/sessionKeyRedirect.jsp");
		
		actualCommonPartConfiguration.toPropertiesForSessionKeyRedirectPage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	
	@Test
	public void testToValueForJDFErrorMessagePage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		actualCommonPartConfiguration.setJDFErrorMessagePage("/errorMessagePage.jsp");
		
		actualCommonPartConfiguration.toPropertiesForErrorMessagePage(sourceSequencedProperties);
		
		JDFPartConfiguration expectedCommonPartConfiguration = new JDFPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForErrorMessagePage(sourceSequencedProperties);
		} catch (Exception e) {
			log.warn("fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedCommonPartConfiguration.getJDFErrorMessagePage(), actualCommonPartConfiguration.getJDFErrorMessagePage());
	}
	
	@Test
	public void testToPropertiesForJDFErrorMessagePage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		actualCommonPartConfiguration.setJDFErrorMessagePage("/errorMessagePage.jsp");
		
		actualCommonPartConfiguration.toPropertiesForErrorMessagePage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForJDFServletTrace() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		actualCommonPartConfiguration.setJDFServletTrace(true);
		
		actualCommonPartConfiguration.toPropertiesForServletTrace(sourceSequencedProperties);
		
		JDFPartConfiguration expectedCommonPartConfiguration = new JDFPartConfiguration();
		
		try {
			expectedCommonPartConfiguration.fromPropertiesForServletTrace(sourceSequencedProperties);
		} catch (Exception e) {
			log.warn("fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}
		
		assertEquals(expectedCommonPartConfiguration.getJDFServletTrace(), actualCommonPartConfiguration.getJDFServletTrace());
	}
	
	
	@Test
	public void testToPropertiesForJDFServletTrace() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		JDFPartConfiguration actualCommonPartConfiguration = new JDFPartConfiguration();
		actualCommonPartConfiguration.setJDFServletTrace(true);
		
		actualCommonPartConfiguration.toPropertiesForServletTrace(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}

}
