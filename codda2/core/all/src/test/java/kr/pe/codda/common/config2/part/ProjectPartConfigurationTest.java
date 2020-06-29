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

import java.nio.ByteOrder;
import java.nio.charset.Charset;
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
import kr.pe.codda.common.type.ClientConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class ProjectPartConfigurationTest {
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
	public void testProjectPartConfiguration_서브프로젝트_theParameterSubProjectNameIsNull() {
		
		try {
			new ProjectPartConfiguration(ProjectType.SUB, null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "the parameter subProjectName is null";
			
			assertEquals(expectedErrorMessage, acutalErrorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testToValueForServerHost() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerHost("172.12.1.31");
		
		actualProjectPartConfiguration.toPropertiesForServerHost(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerHost(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerHost(), actualProjectPartConfiguration.getServerHost());
	}	
	
	@Test
	public void testToPropertiesForServerHost_서브프로젝트() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerHost("172.12.1.31");
		
		actualProjectPartConfiguration.toPropertiesForServerHost(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToPropertiesForServerHost_메인프로젝트() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.MAIN, null);
		actualProjectPartConfiguration.setServerHost("172.12.1.31");
		
		actualProjectPartConfiguration.toPropertiesForServerHost(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerPort() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerPort(9090);
		
		actualProjectPartConfiguration.toPropertiesForServerPort(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerPort(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerPort(), actualProjectPartConfiguration.getServerPort());
	}

	
	@Test
	public void testToPropertiesForServerPort() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerPort(9090);
		
		actualProjectPartConfiguration.toPropertiesForServerPort(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForByteOrder() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setByteOrder(ByteOrder.BIG_ENDIAN);
		
		actualProjectPartConfiguration.toPropertiesForByteOrder(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForByteOrder(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getByteOrder(), actualProjectPartConfiguration.getByteOrder());
	}	
	
	@Test
	public void testToPropertiesForByteOrder() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setByteOrder(ByteOrder.BIG_ENDIAN);
		
		actualProjectPartConfiguration.toPropertiesForByteOrder(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForCharset() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setCharset(Charset.defaultCharset());
		
		actualProjectPartConfiguration.toPropertiesForCharset(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForCharset(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getCharset(), actualProjectPartConfiguration.getCharset());
	}

	
	@Test
	public void testToPropertiesForCharset() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setCharset(Charset.defaultCharset());
		
		actualProjectPartConfiguration.toPropertiesForCharset(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForMessageProtocolType() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setMessageProtocolType(MessageProtocolType.DJSON);
		
		actualProjectPartConfiguration.toPropertiesForMessageProtocolType(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForMessageProtocolType(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getMessageProtocolType(), actualProjectPartConfiguration.getMessageProtocolType());
	}

	
	@Test
	public void testToPropertiesForMessageProtocolType() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setMessageProtocolType(MessageProtocolType.DJSON);
		
		actualProjectPartConfiguration.toPropertiesForMessageProtocolType(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}

	
	@Test
	public void testToValueForClientMonitorTimeInterval() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientMonitorTimeInterval(1234L);
		
		actualProjectPartConfiguration.toPropertiesForClientMonitorTimeInterval(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientMonitorTimeInterval(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientMonitorTimeInterval(), actualProjectPartConfiguration.getClientMonitorTimeInterval());
	}

	
	@Test
	public void testToPropertiesForClientMonitorTimeInterval() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientMonitorTimeInterval(1234L);
		
		actualProjectPartConfiguration.toPropertiesForClientMonitorTimeInterval(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForWhetherClientWrapBufferIsDirect() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setWhetherClientWrapBufferIsDirect(false);
		
		actualProjectPartConfiguration.toPropertiesForWhetherClientWrapBufferIsDirect(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForWhetherClientWrapBufferIsDirect(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getWhetherClientWrapBufferIsDirect(), actualProjectPartConfiguration.getWhetherClientWrapBufferIsDirect());
	}

	
	@Test
	public void testToPropertiesForWhetherClientWrapBufferIsDirect() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setWhetherClientWrapBufferIsDirect(false);
		
		actualProjectPartConfiguration.toPropertiesForWhetherClientWrapBufferIsDirect(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientWrapBufferMaxCntPerMessage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientWrapBufferMaxCntPerMessage(1234);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientWrapBufferMaxCntPerMessage(), actualProjectPartConfiguration.getClientWrapBufferMaxCntPerMessage());
	}

	
	@Test
	public void testToPropertiesForClientWrapBufferMaxCntPerMessage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientWrapBufferMaxCntPerMessage(1234);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientWrapBufferSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientWrapBufferSize(1234);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferSize(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientWrapBufferSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientWrapBufferSize(), actualProjectPartConfiguration.getClientWrapBufferSize());
	}

	
	@Test
	public void testToPropertiesForClientWrapBufferSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientWrapBufferSize(1234);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientWrapBufferPoolSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientWrapBufferPoolSize(4321);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferPoolSize(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientWrapBufferPoolSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientWrapBufferPoolSize(), actualProjectPartConfiguration.getClientWrapBufferPoolSize());
	}

	
	@Test
	public void testToPropertiesForClientWrapBufferPoolSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientWrapBufferPoolSize(4321);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferPoolSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionType() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionType(ClientConnectionType.SYNC);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionType(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientConnectionType(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionType(), actualProjectPartConfiguration.getClientConnectionType());
	}

	
	@Test
	public void testToPropertiesForClientConnectionType() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionType(ClientConnectionType.SYNC);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionType(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionTimeout() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionTimeout(5500L);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionTimeout(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientConnectionTimeout(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionTimeout(), actualProjectPartConfiguration.getClientConnectionTimeout());
	}

	
	@Test
	public void testToPropertiesForClientConnectionTimeout() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionTimeout(5500L);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionTimeout(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionCount() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionCount(7);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionCount(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientConnectionCount(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionCount(), actualProjectPartConfiguration.getClientConnectionCount());
	}

	
	@Test
	public void testToPropertiesForClientConnectionCount() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionCount(7);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionCount(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionMaxCount() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionMaxCount(9);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionMaxCount(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientConnectionMaxCount(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionMaxCount(), actualProjectPartConfiguration.getClientConnectionMaxCount());
	}

	
	@Test
	public void testToPropertiesForClientConnectionMaxCount() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionMaxCount(9);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionMaxCount(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionPoolSupporterTimeInterval() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionPoolSupporterTimeInterval(12345L);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionPoolSupporterTimeInterval(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientConnectionPoolSupporterTimeInterval(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionPoolSupporterTimeInterval(), actualProjectPartConfiguration.getClientConnectionPoolSupporterTimeInterval());
	}

	
	@Test
	public void testToPropertiesForClientConnectionPoolSupporterTimeInterval() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientConnectionPoolSupporterTimeInterval(12345L);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionPoolSupporterTimeInterval(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientMailboxCountPerAsynShareConnection() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientMailboxCountPerAsynShareConnection(17);
		
		actualProjectPartConfiguration.toPropertiesForClientMailboxCountPerAsynShareConnection(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientMailboxCountPerAsynShareConnection(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientMailboxCountPerAsynShareConnection(), actualProjectPartConfiguration.getClientMailboxCountPerAsynShareConnection());
	}

	
	@Test
	public void testToPropertiesForClientMailboxCountPerAsynShareConnection() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientMailboxCountPerAsynShareConnection(17);
		
		actualProjectPartConfiguration.toPropertiesForClientMailboxCountPerAsynShareConnection(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientAsynInputMessageQueueCapacity() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientAsynInputMessageQueueCapacity(17);
		
		actualProjectPartConfiguration.toPropertiesForClientAsynInputMessageQueueCapacity(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientAsynInputMessageQueueCapacity(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientAsynInputMessageQueueCapacity(), actualProjectPartConfiguration.getClientAsynInputMessageQueueCapacity());
	}

	
	@Test
	public void testToPropertiesForClientAsynInputMessageQueueCapacity() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientAsynInputMessageQueueCapacity(17);
		
		actualProjectPartConfiguration.toPropertiesForClientAsynInputMessageQueueCapacity(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientAsynOutputMessageQueueCapacity() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientAsynOutputMessageQueueCapacity(19);
		
		actualProjectPartConfiguration.toPropertiesForClientAsynOutputMessageQueueCapacity(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientAsynOutputMessageQueueCapacity(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientAsynOutputMessageQueueCapacity(), actualProjectPartConfiguration.getClientAsynOutputMessageQueueCapacity());
	}

	
	@Test
	public void testToPropertiesForClientAsynOutputMessageQueueCapacity() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientAsynOutputMessageQueueCapacity(19);
		
		actualProjectPartConfiguration.toPropertiesForClientAsynOutputMessageQueueCapacity(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientSelectorWakeupInterval() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientSelectorWakeupInterval(21L);
		
		actualProjectPartConfiguration.toPropertiesForClientSelectorWakeupInterval(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForClientSelectorWakeupInterval(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientSelectorWakeupInterval(), actualProjectPartConfiguration.getClientSelectorWakeupInterval());
	}

	
	@Test
	public void testToPropertiesForClientSelectorWakeupInterval() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setClientSelectorWakeupInterval(21L);
		
		actualProjectPartConfiguration.toPropertiesForClientSelectorWakeupInterval(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerMonitorTimeInterval() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerMonitorTimeInterval(29L);
		
		actualProjectPartConfiguration.toPropertiesForServerMonitorTimeInterval(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerMonitorTimeInterval(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerMonitorTimeInterval(), actualProjectPartConfiguration.getServerMonitorTimeInterval());
	}

	
	@Test
	public void testToPropertiesForServerMonitorTimeInterval() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerMonitorTimeInterval(29L);
		
		actualProjectPartConfiguration.toPropertiesForServerMonitorTimeInterval(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForWhetherServerWrapBufferIsDirect() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setWhetherServerWrapBufferIsDirect(false);
		
		actualProjectPartConfiguration.toPropertiesForWhetherServerWrapBufferIsDirect(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForWhetherServerWrapBufferIsDirect(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getWhetherServerWrapBufferIsDirect(), actualProjectPartConfiguration.getWhetherServerWrapBufferIsDirect());
	}

	
	@Test
	public void testToPropertiesForWhetherServerWrapBufferIsDirect() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setWhetherServerWrapBufferIsDirect(false);
		
		actualProjectPartConfiguration.toPropertiesForWhetherServerWrapBufferIsDirect(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerWrapBufferMaxCntPerMessage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerWrapBufferMaxCntPerMessage(21);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerWrapBufferMaxCntPerMessage(), actualProjectPartConfiguration.getServerWrapBufferMaxCntPerMessage());
	}

	
	@Test
	public void testToPropertiesForServerWrapBufferMaxCntPerMessage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerWrapBufferMaxCntPerMessage(21);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerWrapBufferSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerWrapBufferSize(8096);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferSize(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerWrapBufferSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerWrapBufferSize(), actualProjectPartConfiguration.getServerWrapBufferSize());
	}

	
	@Test
	public void testToPropertiesForServerWrapBufferSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerWrapBufferSize(8096);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerWrapBufferPoolSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerWrapBufferPoolSize(21);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferPoolSize(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerWrapBufferPoolSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerWrapBufferPoolSize(), actualProjectPartConfiguration.getServerWrapBufferPoolSize());
	}

	
	@Test
	public void testToPropertiesForServerWrapBufferPoolSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerWrapBufferPoolSize(21);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferPoolSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerMaxClients() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerMaxClients(20);
		
		actualProjectPartConfiguration.toPropertiesForServerMaxClients(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerMaxClients(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerMaxClients(), actualProjectPartConfiguration.getServerMaxClients());
	}

	
	@Test
	public void testToPropertiesForServerMaxClients() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerMaxClients(20);
		
		actualProjectPartConfiguration.toPropertiesForServerMaxClients(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerInputMessageQueueCapacity() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerInputMessageQueueCapacity(100);
		
		actualProjectPartConfiguration.toPropertiesForServerInputMessageQueueCapacity(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerInputMessageQueueCapacity(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerInputMessageQueueCapacity(), actualProjectPartConfiguration.getServerInputMessageQueueCapacity());
	}

	
	@Test
	public void testToPropertiesForServerInputMessageQueueCapacity() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerInputMessageQueueCapacity(100);
		
		actualProjectPartConfiguration.toPropertiesForServerInputMessageQueueCapacity(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerOutputMessageQueueCapacity() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerOutputMessageQueueCapacity(100);
		
		actualProjectPartConfiguration.toPropertiesForServerOutputMessageQueueCapacity(sourceSequencedProperties);
		
		ProjectPartConfiguration expectedProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		
		try {
			expectedProjectPartConfiguration.toValueForServerOutputMessageQueueCapacity(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerOutputMessageQueueCapacity(), actualProjectPartConfiguration.getServerOutputMessageQueueCapacity());
	}

	
	@Test
	public void testToPropertiesForServerOutputMessageQueueCapacity() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		ProjectPartConfiguration actualProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.SUB, "auth_pc1");
		actualProjectPartConfiguration.setServerOutputMessageQueueCapacity(100);
		
		actualProjectPartConfiguration.toPropertiesForServerOutputMessageQueueCapacity(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
}
