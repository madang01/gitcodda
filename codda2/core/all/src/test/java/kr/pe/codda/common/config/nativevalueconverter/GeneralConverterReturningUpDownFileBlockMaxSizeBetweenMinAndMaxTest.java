/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

// GeneralConverterReturningUpDownFileBlockMaxSize
public class GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMaxTest {
	
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax nativeValueConverter = null;
	private Integer returnedValue = null;
	private Integer expectedValue = null;

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
		nativeValueConverter = new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(
				600, 2048);
	}
	
	@After
	public void tearDown() throws Exception {
	}

	

	@Test
	public void testGeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax_theParmaeterMinIsLessThanZero() {
		final int min = -1;
		final int max = 1;
		
		try {
			nativeValueConverter = new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(min, max);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter min[")
					.append(min).append("] is less than zero").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testGeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax_theParmaeterMaxIsLessThan1024() {
		final int min = 2;
		final int max = 1023;
		
		try {
			new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(min, max);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter max[")
					.append(max).append("] is less than 1024").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testGeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax_theParamterMinIsGreaterThanTheParameterMax() {
		final int min = 1025;
		final int max = 1024;
		
		try {
			new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(min, max);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter min[")
					.append(min).append("] is greater than parameter max[")
					.append(max).append("]").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	
	@Test
	public void testValueOf_ExpectedValueComparison() {
		expectedValue = Integer.valueOf(1024);
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue
					.toString());
			
			assertEquals("the expected value comparison", returnedValue, expectedValue);
			
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

	@Test
	public void testValueOf_ValidButBadParameter_theParameterItemValueIsNotDigit()
			throws Exception {
		final String itemValue = "abc";
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not integer type")
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}

	}

	@Test
	public void testValueOf_ValidButBadParameter_theParameterItemValueIsTooBigNumber() {
		final String itemValue = String.valueOf(Integer.MAX_VALUE + 1L);
		
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not integer type")
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testValueOf_ValidButBadParameter_theParameterItemValueIsLessThanMin()
			throws Exception {
		final String itemValue = String.valueOf(nativeValueConverter.getMin() - 1);
		
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is less than min[")
					.append(nativeValueConverter.getMin()).append("]").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}

	}

	@Test
	public void testValueOf_theParameterItemValueIsGreaterThanMax() {
		final String itemValue = String.valueOf(nativeValueConverter.getMax() + 1L);
		
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is greater than max[")
					.append(nativeValueConverter.getMax()).append("]").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testValueOf_theParameterItemValueIsNot1024Multiple() {
		final String itemValue = "1023";
		
		try {
			returnedValue = nativeValueConverter.valueOf(itemValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not a multiple of 1024")
					.toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

}
