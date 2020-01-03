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

public class GeneralConverterReturningEmptyOrNoTrimStringTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private GeneralConverterReturningEmptyOrNoTrimString nativeValueConverter = null;
	private String returnedValue = null;

	
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
		nativeValueConverter = new GeneralConverterReturningEmptyOrNoTrimString();
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testValueOf_OK_EmptyString() {
		String expectedValue = null;
		
		expectedValue = "";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			assertEquals("the expected value comparison", returnedValue, expectedValue);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
		
		
	}

	
	@Test
	public void testValueOf_OK_NotEmptyString() {
		String expectedValue = null;
		
		expectedValue = "aabc";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			assertEquals("the expected value comparison", returnedValue, expectedValue);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	
	@Test
	public void testValueOf_theParameterItemValueIsNull() {
		String expectedValue = null;
		
		// expectedValue = "aabc";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

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
	public void testValueOf_theParameterItemValueIsOneSpaceChar() {
		String expectedValue = null;
		
		expectedValue = " ";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder()
					.append("the parameter itemValue[")
					.append(expectedValue)
					.append("] has leading or tailing white space").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testValueOf_theParameterItemValueHasWhitSpaceCharAtHead() {
		String expectedValue = null;
		
		expectedValue = " a \tb \t";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder()
					.append("the parameter itemValue[")
					.append(expectedValue)
					.append("] has leading or tailing white space").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testValueOf_theParameterItemValueHasWhitSpaceCharAtHeadAndTail() {
		String expectedValue = null;
		
		expectedValue = " ab\nc ";
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder()
					.append("the parameter itemValue[")
					.append(expectedValue)
					.append("] has leading or tailing white space").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testValueOf_theParameterItemValueHasWhitSpaceCharAtTail() {
		String expectedValue = null;
		
		expectedValue = "ab\nc \t";
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = new StringBuilder()
					.append("the parameter itemValue[")
					.append(expectedValue)
					.append("] has leading or tailing white space").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.log(Level.WARNING, errorMessage, e);
			
			fail("unknown error");
		}
	}
}