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
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class MainProjectPartConfigurationTest {
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
	public void testToValueForServerHost() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerHost("172.12.1.31");
		
		actualProjectPartConfiguration.toPropertiesForServerHost(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerHost(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerHost(), actualProjectPartConfiguration.getServerHost());
	}	
	
	@Test
	public void testToPropertiesForServerHost_서브프로젝트() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerHost("172.12.1.31");
		
		actualProjectPartConfiguration.toPropertiesForServerHost(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToPropertiesForServerHost_메인프로젝트() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerHost("172.12.1.31");
		
		actualProjectPartConfiguration.toPropertiesForServerHost(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerPort() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerPort(9090);
		
		actualProjectPartConfiguration.toPropertiesForServerPort(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerPort(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerPort(), actualProjectPartConfiguration.getServerPort());
	}

	
	@Test
	public void testToPropertiesForServerPort() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerPort(9090);
		
		actualProjectPartConfiguration.toPropertiesForServerPort(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForByteOrder() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setByteOrder(ByteOrder.BIG_ENDIAN);
		
		actualProjectPartConfiguration.toPropertiesForByteOrder(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForByteOrder(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getByteOrder(), actualProjectPartConfiguration.getByteOrder());
	}	
	
	@Test
	public void testToPropertiesForByteOrder() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setByteOrder(ByteOrder.BIG_ENDIAN);
		
		actualProjectPartConfiguration.toPropertiesForByteOrder(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForCharset() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setCharset(Charset.defaultCharset());
		
		actualProjectPartConfiguration.toPropertiesForCharset(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForCharset(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getCharset(), actualProjectPartConfiguration.getCharset());
	}

	
	@Test
	public void testToPropertiesForCharset() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setCharset(Charset.defaultCharset());
		
		actualProjectPartConfiguration.toPropertiesForCharset(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForMessageProtocolType() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setMessageProtocolType(MessageProtocolType.DJSON);
		
		actualProjectPartConfiguration.toPropertiesForMessageProtocolType(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForMessageProtocolType(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getMessageProtocolType(), actualProjectPartConfiguration.getMessageProtocolType());
	}

	
	@Test
	public void testToPropertiesForMessageProtocolType() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setMessageProtocolType(MessageProtocolType.DJSON);
		
		actualProjectPartConfiguration.toPropertiesForMessageProtocolType(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}

	
	@Test
	public void testToValueForClientMonitorTimeInterval() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientMonitorTimeInterval(1234L);
		
		actualProjectPartConfiguration.toPropertiesForClientMonitorTimeInterval(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientMonitorTimeInterval(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientMonitorTimeInterval(), actualProjectPartConfiguration.getClientMonitorTimeInterval());
	}

	
	@Test
	public void testToPropertiesForClientMonitorTimeInterval() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientMonitorTimeInterval(1234L);
		
		actualProjectPartConfiguration.toPropertiesForClientMonitorTimeInterval(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForWhetherClientWrapBufferIsDirect() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setWhetherClientWrapBufferIsDirect(false);
		
		actualProjectPartConfiguration.toPropertiesForWhetherClientWrapBufferIsDirect(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForWhetherClientWrapBufferIsDirect(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getWhetherClientWrapBufferIsDirect(), actualProjectPartConfiguration.getWhetherClientWrapBufferIsDirect());
	}

	
	@Test
	public void testToPropertiesForWhetherClientWrapBufferIsDirect() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setWhetherClientWrapBufferIsDirect(false);
		
		actualProjectPartConfiguration.toPropertiesForWhetherClientWrapBufferIsDirect(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientWrapBufferMaxCntPerMessage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientWrapBufferMaxCntPerMessage(1234);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientWrapBufferMaxCntPerMessage(), actualProjectPartConfiguration.getClientWrapBufferMaxCntPerMessage());
	}

	
	@Test
	public void testToPropertiesForClientWrapBufferMaxCntPerMessage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientWrapBufferMaxCntPerMessage(1234);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientWrapBufferSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientWrapBufferSize(1234);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferSize(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientWrapBufferSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientWrapBufferSize(), actualProjectPartConfiguration.getClientWrapBufferSize());
	}

	
	@Test
	public void testToPropertiesForClientWrapBufferSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientWrapBufferSize(1234);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientWrapBufferPoolSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientWrapBufferPoolSize(4321);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferPoolSize(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientWrapBufferPoolSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientWrapBufferPoolSize(), actualProjectPartConfiguration.getClientWrapBufferPoolSize());
	}

	
	@Test
	public void testToPropertiesForClientWrapBufferPoolSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientWrapBufferPoolSize(4321);
		
		actualProjectPartConfiguration.toPropertiesForClientWrapBufferPoolSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionType() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionType(ClientConnectionType.SYNC);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionType(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientConnectionType(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionType(), actualProjectPartConfiguration.getClientConnectionType());
	}

	
	@Test
	public void testToPropertiesForClientConnectionType() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionType(ClientConnectionType.SYNC);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionType(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionTimeout() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionTimeout(5500L);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionTimeout(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientConnectionTimeout(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionTimeout(), actualProjectPartConfiguration.getClientConnectionTimeout());
	}

	
	@Test
	public void testToPropertiesForClientConnectionTimeout() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionTimeout(5500L);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionTimeout(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionCount() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionCount(7);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionCount(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientConnectionCount(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionCount(), actualProjectPartConfiguration.getClientConnectionCount());
	}

	
	@Test
	public void testToPropertiesForClientConnectionCount() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionCount(7);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionCount(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionMaxCount() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionMaxCount(9);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionMaxCount(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientConnectionMaxCount(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionMaxCount(), actualProjectPartConfiguration.getClientConnectionMaxCount());
	}

	
	@Test
	public void testToPropertiesForClientConnectionMaxCount() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionMaxCount(9);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionMaxCount(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientConnectionPoolSupporterTimeInterval() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionPoolSupporterTimeInterval(12345L);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionPoolSupporterTimeInterval(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientConnectionPoolSupporterTimeInterval(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientConnectionPoolSupporterTimeInterval(), actualProjectPartConfiguration.getClientConnectionPoolSupporterTimeInterval());
	}

	
	@Test
	public void testToPropertiesForClientConnectionPoolSupporterTimeInterval() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientConnectionPoolSupporterTimeInterval(12345L);
		
		actualProjectPartConfiguration.toPropertiesForClientConnectionPoolSupporterTimeInterval(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientMailboxCountPerAsynShareConnection() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientMailboxCountPerAsynShareConnection(17);
		
		actualProjectPartConfiguration.toPropertiesForClientMailboxCountPerAsynShareConnection(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientMailboxCountPerAsynShareConnection(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientMailboxCountPerAsynShareConnection(), actualProjectPartConfiguration.getClientMailboxCountPerAsynShareConnection());
	}

	
	@Test
	public void testToPropertiesForClientMailboxCountPerAsynShareConnection() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientMailboxCountPerAsynShareConnection(17);
		
		actualProjectPartConfiguration.toPropertiesForClientMailboxCountPerAsynShareConnection(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientAsynInputMessageQueueCapacity() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientAsynInputMessageQueueCapacity(17);
		
		actualProjectPartConfiguration.toPropertiesForClientAsynInputMessageQueueCapacity(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientAsynInputMessageQueueCapacity(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientAsynInputMessageQueueCapacity(), actualProjectPartConfiguration.getClientAsynInputMessageQueueCapacity());
	}

	
	@Test
	public void testToPropertiesForClientAsynInputMessageQueueCapacity() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientAsynInputMessageQueueCapacity(17);
		
		actualProjectPartConfiguration.toPropertiesForClientAsynInputMessageQueueCapacity(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientAsynOutputMessageQueueCapacity() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientAsynOutputMessageQueueCapacity(19);
		
		actualProjectPartConfiguration.toPropertiesForClientAsynOutputMessageQueueCapacity(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientAsynOutputMessageQueueCapacity(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientAsynOutputMessageQueueCapacity(), actualProjectPartConfiguration.getClientAsynOutputMessageQueueCapacity());
	}

	
	@Test
	public void testToPropertiesForClientAsynOutputMessageQueueCapacity() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientAsynOutputMessageQueueCapacity(19);
		
		actualProjectPartConfiguration.toPropertiesForClientAsynOutputMessageQueueCapacity(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForClientSelectorWakeupInterval() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientSelectorWakeupInterval(21L);
		
		actualProjectPartConfiguration.toPropertiesForClientSelectorWakeupInterval(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForClientSelectorWakeupInterval(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getClientSelectorWakeupInterval(), actualProjectPartConfiguration.getClientSelectorWakeupInterval());
	}

	
	@Test
	public void testToPropertiesForClientSelectorWakeupInterval() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setClientSelectorWakeupInterval(21L);
		
		actualProjectPartConfiguration.toPropertiesForClientSelectorWakeupInterval(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerMonitorTimeInterval() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerMonitorTimeInterval(29L);
		
		actualProjectPartConfiguration.toPropertiesForServerMonitorTimeInterval(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerMonitorTimeInterval(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerMonitorTimeInterval(), actualProjectPartConfiguration.getServerMonitorTimeInterval());
	}

	
	@Test
	public void testToPropertiesForServerMonitorTimeInterval() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerMonitorTimeInterval(29L);
		
		actualProjectPartConfiguration.toPropertiesForServerMonitorTimeInterval(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForWhetherServerWrapBufferIsDirect() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setWhetherServerWrapBufferIsDirect(false);
		
		actualProjectPartConfiguration.toPropertiesForWhetherServerWrapBufferIsDirect(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForWhetherServerWrapBufferIsDirect(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getWhetherServerWrapBufferIsDirect(), actualProjectPartConfiguration.getWhetherServerWrapBufferIsDirect());
	}

	
	@Test
	public void testToPropertiesForWhetherServerWrapBufferIsDirect() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setWhetherServerWrapBufferIsDirect(false);
		
		actualProjectPartConfiguration.toPropertiesForWhetherServerWrapBufferIsDirect(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerWrapBufferMaxCntPerMessage() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerWrapBufferMaxCntPerMessage(21);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerWrapBufferMaxCntPerMessage(), actualProjectPartConfiguration.getServerWrapBufferMaxCntPerMessage());
	}

	
	@Test
	public void testToPropertiesForServerWrapBufferMaxCntPerMessage() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerWrapBufferMaxCntPerMessage(21);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerWrapBufferSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerWrapBufferSize(8096);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferSize(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerWrapBufferSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerWrapBufferSize(), actualProjectPartConfiguration.getServerWrapBufferSize());
	}

	
	@Test
	public void testToPropertiesForServerWrapBufferSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerWrapBufferSize(8096);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerWrapBufferPoolSize() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerWrapBufferPoolSize(21);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferPoolSize(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerWrapBufferPoolSize(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerWrapBufferPoolSize(), actualProjectPartConfiguration.getServerWrapBufferPoolSize());
	}

	
	@Test
	public void testToPropertiesForServerWrapBufferPoolSize() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerWrapBufferPoolSize(21);
		
		actualProjectPartConfiguration.toPropertiesForServerWrapBufferPoolSize(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerMaxClients() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerMaxClients(20);
		
		actualProjectPartConfiguration.toPropertiesForServerMaxClients(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerMaxClients(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerMaxClients(), actualProjectPartConfiguration.getServerMaxClients());
	}

	
	@Test
	public void testToPropertiesForServerMaxClients() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerMaxClients(20);
		
		actualProjectPartConfiguration.toPropertiesForServerMaxClients(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerInputMessageQueueCapacity() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerInputMessageQueueCapacity(100);
		
		actualProjectPartConfiguration.toPropertiesForServerInputMessageQueueCapacity(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerInputMessageQueueCapacity(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerInputMessageQueueCapacity(), actualProjectPartConfiguration.getServerInputMessageQueueCapacity());
	}

	
	@Test
	public void testToPropertiesForServerInputMessageQueueCapacity() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerInputMessageQueueCapacity(100);
		
		actualProjectPartConfiguration.toPropertiesForServerInputMessageQueueCapacity(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
	
	@Test
	public void testToValueForServerOutputMessageQueueCapacity() {		
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerOutputMessageQueueCapacity(100);
		
		actualProjectPartConfiguration.toPropertiesForServerOutputMessageQueueCapacity(sourceSequencedProperties);
		
		AbstractProjectPartConfiguration expectedProjectPartConfiguration = new MainProjectPartConfiguration();
		
		try {
			expectedProjectPartConfiguration.fromPropertiesForServerOutputMessageQueueCapacity(sourceSequencedProperties);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to call 'toValue'", e);
			
			fail("fail to call 'toValue'");
		}	
		
		
		assertEquals(expectedProjectPartConfiguration.getServerOutputMessageQueueCapacity(), actualProjectPartConfiguration.getServerOutputMessageQueueCapacity());
	}

	
	@Test
	public void testToPropertiesForServerOutputMessageQueueCapacity() {	
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		
		AbstractProjectPartConfiguration actualProjectPartConfiguration = new MainProjectPartConfiguration();
		actualProjectPartConfiguration.setServerOutputMessageQueueCapacity(100);
		
		actualProjectPartConfiguration.toPropertiesForServerOutputMessageQueueCapacity(sourceSequencedProperties);
		
		log.info(sourceSequencedProperties.toString());
	}
}
