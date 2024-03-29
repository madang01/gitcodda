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

package kr.pe.codda.common.message.builder.info;

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

public class ArrayInfoTest {
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
	public void testArrayInfo_배열크기직접입력방식_문자인배열크기() {
		String testTitle = "배열 크기 직접 입력 방식_문자인 배열 크기";	
				
		String arrayName = "member";
		String arrayCntType = "direct";
		String arrayCntValue = "hello";
		
		String expectedMessage  = new StringBuilder("fail to parses the string argument(=this array item[")
		.append(arrayName).append("]'s attribute 'cntvalue' value[")
		.append(arrayCntValue)
		.append("]) as a signed decimal integer").toString();
		
		
		MessageArrayInfo arrayInfo = null;
		try {
			arrayInfo = new MessageArrayInfo(arrayName, arrayCntType,
				arrayCntValue);
			
			log.info(arrayInfo.toString());
			
			fail("no IllegalArgumentException");
			
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (! errorMessage.equals(expectedMessage)) {
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		}
	}

	
	@Test
	public void testArrayInfo_배열크기직접입력방식_0보다작은배열크기() {
		String testTitle = "배열 크기 직접 입력 방식_0보다 작은 배열 크기";	
				
		String arrayName = "member";
		String arrayCntType = "direct";
		String arrayCntValue = "-2";
		
		String expectedMessage  = new StringBuilder("this array item[")
		.append(arrayName).append("]'s attribute 'cntvalue' value[")
		.append(arrayCntValue)
		.append("] is less than or equals to zero").toString();
				
		MessageArrayInfo arrayInfo = null;
		try {
			arrayInfo = new MessageArrayInfo(arrayName, arrayCntType,
				arrayCntValue);
			
			log.info(arrayInfo.toString());
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (! errorMessage.equals(expectedMessage)) {
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		}
		
		
	}
	
	@Test
	public void testArrayInfo_잘못된배열크기방식지정() {
		String testTitle = "잘못된배열크기방식지정";	
				
		String arrayName = "member";
		String arrayCntType = "direct2";
		String arrayCntValue = "-2";
		
		String expectedMessage  = new StringBuilder("this array item[")
		.append(arrayName).append("]'s attribute 'cnttype' value[")
		.append(arrayCntType)
		.append("] is not an element of direction set[direct, reference]").toString();
		
		
		MessageArrayInfo arrayInfo = null;
		try {
			arrayInfo = new MessageArrayInfo(arrayName, arrayCntType,
				arrayCntValue);
			
			log.info(arrayInfo.toString());
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (! errorMessage.equals(expectedMessage)) {
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		}
		
		
	}
}
