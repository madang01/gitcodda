/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.weblib.summernote.whitevaluechecker;

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
import kr.pe.codda.weblib.exception.WhiteParserException;

public class ATagHrefAttrWhiteValueCheckerTest {
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
	public void testThrowExceptionIfNoWhiteValue_상대주소_OK() {
		
		String attributeValue = "/sitemenu/doc/resource/MemberLoginReq/MemberLoginReq.java";
		
		ATagHrefAttrWhiteValueChecker aTagHrefAttrWhiteValueChecker = new ATagHrefAttrWhiteValueChecker();
		try {
			aTagHrefAttrWhiteValueChecker.throwExceptionIfNoWhiteValue(attributeValue);
			
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("unknown error");
		}
		
	}
	
	@Test
	public void testThrowExceptionIfNoWhiteValue_절대주소_OK() {
		
		String attributeValue = "https://stackoverflow.com/questions/7109143/what-characters-are-valid-in-a-url";
		
		ATagHrefAttrWhiteValueChecker aTagHrefAttrWhiteValueChecker = new ATagHrefAttrWhiteValueChecker();
		try {
			aTagHrefAttrWhiteValueChecker.throwExceptionIfNoWhiteValue(attributeValue);
			
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("unknown error");
		}
		
	}
	
	@Test
	public void testThrowExceptionIfNoWhiteValue_잘못된URL스키마() {
		
		String attributeValue = "ftp://stackoverflow.com/questions/7109143/what-characters-are-valid-in-a-url";
		
		ATagHrefAttrWhiteValueChecker aTagHrefAttrWhiteValueChecker = new ATagHrefAttrWhiteValueChecker();
		try {
			aTagHrefAttrWhiteValueChecker.throwExceptionIfNoWhiteValue(attributeValue);
			
			fail("no WhiteParserException");
		} catch(WhiteParserException e) {
			String expectedErrorMessage = new StringBuilder()
					.append("the tag name[a]'s attribte[href]'s value[")
					.append(attributeValue)
					.append("] is a valid url string because it does have a bad URL scema").toString();
			
			String errorMesage = e.getMessage();
			
			assertEquals(expectedErrorMessage, errorMesage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("unknown error");
		}
	}

	@Test
	public void testThrowExceptionIfNoWhiteValue_특수문자_OK() {
			
		String attributeValue = "http://www.sinnori.pe.kr/tmp?aa='c'";
		
		ATagHrefAttrWhiteValueChecker aTagHrefAttrWhiteValueChecker = new ATagHrefAttrWhiteValueChecker();
		try {
			aTagHrefAttrWhiteValueChecker.throwExceptionIfNoWhiteValue(attributeValue);
			
			
		} catch(Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testThrowExceptionIfNoWhiteValue_공격_경우1() {
			
		String attributeValue = "http:jav&#x0D;ascript:alert('XSS');";
		
		ATagHrefAttrWhiteValueChecker aTagHrefAttrWhiteValueChecker = new ATagHrefAttrWhiteValueChecker();
		try {
			aTagHrefAttrWhiteValueChecker.throwExceptionIfNoWhiteValue(attributeValue);
			
			fail("no WhiteParserException");
		} catch(WhiteParserException e) {
			String expectedErrorMessage = new StringBuilder()
					.append("the tag name[a]'s attribte[href]'s value[")
					.append(attributeValue)
					.append("] has a disallowed characater, it has a disallowed string '&#'").toString();
			
			String errorMesage = e.getMessage();
			
			log.info(errorMesage);
			
			assertEquals(expectedErrorMessage, errorMesage);
		} catch(Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("unknown error");
		}
	}
	
	/**
	 * Null breaks up JavaScript directive
	 */
	@Test
	public void testThrowExceptionIfNoWhiteValue_공격_경우2() {
			
		String attributeValue = "http:java\\0script:alert(\\\"XSS\\\")>";
		
		ATagHrefAttrWhiteValueChecker aTagHrefAttrWhiteValueChecker = new ATagHrefAttrWhiteValueChecker();
		try {
			aTagHrefAttrWhiteValueChecker.throwExceptionIfNoWhiteValue(attributeValue);
			
			fail("no WhiteParserException");
		} catch(WhiteParserException e) {
			String expectedErrorMessage = new StringBuilder()
					.append("the tag name[a]'s attribte[href]'s value[")
					.append(attributeValue)
					.append("] has a disallowed characater, maybe '<'or '\\'").toString();
			
			String errorMesage = e.getMessage();
			
			log.info(errorMesage);
			
			assertEquals(expectedErrorMessage, errorMesage);
		} catch(Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("unknown error");
		}
	}
	
	/**
	 * /index.html?message=%3Cscript%3Ealert(%27XSS%27);%3C/script%3E
	 */
	@Test
	public void testThrowExceptionIfNoWhiteValue_공격_경우3() {
			
		String attributeValue = "/index.html?message=%3Cscript%3Ealert(%27XSS%27);%3C/script%3E";
		
		ATagHrefAttrWhiteValueChecker aTagHrefAttrWhiteValueChecker = new ATagHrefAttrWhiteValueChecker();
		try {
			aTagHrefAttrWhiteValueChecker.throwExceptionIfNoWhiteValue(attributeValue);
			
			fail("no WhiteParserException");
		} catch(WhiteParserException e) {
			String expectedErrorMessage = new StringBuilder()
					.append("the tag name[a]'s attribte[href]'s value[")
					.append(attributeValue)
					.append("] has a disallowed characater, it has a disallowed string '&3c'").toString();
			
			String errorMesage = e.getMessage();
			
			log.info(errorMesage);
			
			assertEquals(expectedErrorMessage, errorMesage);
		} catch(Exception e) {
			log.log(Level.WARNING, "unknwon error", e);
			fail("unknown error");
		}
	}
	// 
}
