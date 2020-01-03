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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
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

public class SingleItemInfoTest {
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
	public void testSingleItemInfo_잘못된항목이름_한글자() {
		String testTitle = "잘못된 아이템 항목 이름-한글자";
		
		String expectedMessage  = "this single item's attribute 'name' value length is greater than or eqaul to 2";
		
		String itemName = "t";
		String itemTypeName = "byte";
		String itemDefaultValue = null;
		String itemSize = null;
		String itemCharset = null;
		
		
		SingleItemInfo singleItemInfo = null;
		
		try {
			singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
					itemDefaultValue, itemSize, itemCharset);
			
			log.info(singleItemInfo.toString());
			
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
	public void testSingleItemInfo_잘못된항목이름_xml로시작하여태그이름규칙에맞지않는이름() {
		String testTitle = "잘못된 아이템 항목 이름-xml로 시작하여 태그 규칙에 맞지 않는 이름";
		
		String expectedMessage  = "this single item name[xMlabc] must not begin with the string 'xml' that would match (('X'|'x') ('M'|'m') ('L'|'l'))";
		
		String itemName = "xMlabc";
		String itemTypeName = "byte";
		String itemDefaultValue = null;
		String itemSize = null;
		String itemCharset = null;
		
		
		SingleItemInfo singleItemInfo = null;
		try {
			singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
					itemDefaultValue, itemSize, itemCharset);
			
			singleItemInfo.hashCode();
			
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
	public void testSingleItemInfo_잘못된항목이름_태그이름범위를벗어난문자를가져태그이름규칙에맞지않는이름() {
		String testTitle = "잘못된 아이템 항목 이름-태그 이름 범위를 벗어난 문자를 가져 태그 규칙에 맞지 않는 이름";
		
		String expectedMessage  = "this single item name[;abcd] should be decided in accordance with java xml tag name rule";
		
		String itemName = ";abcd";
		String itemTypeName = "byte";
		String itemDefaultValue = null;
		String itemSize = null;
		String itemCharset = null;
		
		
		SingleItemInfo singleItemInfo = null;
		
		try {
			singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
					itemDefaultValue, itemSize, itemCharset);
			
			singleItemInfo.hashCode();
			
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
	public void testSingleItemInfo_잘못된항목값타입() {
		String testTitle = "잘못된 항목값 타입";
		
		String expectedMessage  = "this single item[a2]'s attribute 'type' value[byte22]) is not an element of item value type set";
		
		String itemName = "a2";
		String itemTypeName = "byte22";
		String itemDefaultValue = null;
		String itemSize = null;
		String itemCharset = null;
		
		
		SingleItemInfo singleItemInfo = null;
		
		try {
			singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
					itemDefaultValue, itemSize, itemCharset);
			
			singleItemInfo.hashCode();
			
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
	public void testSingleItemInfo_잘못된디폴트값_숫자형에문자열() {
		Set<String> numberTypeNameSet = new LinkedHashSet<String>();
		numberTypeNameSet.add("byte");
		numberTypeNameSet.add("unsigned byte");
		numberTypeNameSet.add("short");
		numberTypeNameSet.add("unsigned short");
		numberTypeNameSet.add("integer");
		numberTypeNameSet.add("unsigned integer");
		numberTypeNameSet.add("long");
		
		HashMap<String, String> numberTypeName2ParsingTypeNameHash = new HashMap<String, String>();
		numberTypeName2ParsingTypeNameHash.put("byte", "byte");
		numberTypeName2ParsingTypeNameHash.put("unsigned byte", "short");
		numberTypeName2ParsingTypeNameHash.put("short", "short");
		numberTypeName2ParsingTypeNameHash.put("unsigned short", "integer");
		numberTypeName2ParsingTypeNameHash.put("integer", "integer");
		numberTypeName2ParsingTypeNameHash.put("unsigned integer", "long");
		numberTypeName2ParsingTypeNameHash.put("long", "long");
		
		
		for (String numberTypeName : numberTypeNameSet) {
			String testTitle = new StringBuilder(numberTypeName).append("잘못된디폴트값_숫자형에문자열").toString();
			String expectedMessage  = new StringBuilder("fail to parses the string argument(=this '")
			.append(numberTypeName).append("' type single item[a2]'s attribute 'defaultValue' value[king]) as a signed decimal ")
			.append(numberTypeName2ParsingTypeNameHash.get(numberTypeName)).toString();
						
			String itemName = "a2";
			String itemTypeName = numberTypeName;
			String itemDefaultValue = "king";
			String itemSize = null;
			String itemCharset = null;
			
		
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
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
	
	@Test
	public void testSingleItemInfo_잘못된디폴트값_문자형앞뒤로공백() {
		Set<String> stringTypeNameSet = new LinkedHashSet<String>();
		stringTypeNameSet.add("ub pascal string");
		stringTypeNameSet.add("us pascal string");
		stringTypeNameSet.add("si pascal string");
		stringTypeNameSet.add("fixed length string");
		
		for (String stringTypeName : stringTypeNameSet) {
			String testTitle = new StringBuilder(stringTypeName).append("잘못된디폴트값_문자형앞뒤공백").toString();
			String expectedMessage  = new StringBuilder("this '")
			.append(stringTypeName)
			.append("' type single item[a2]'s attribute 'defaultValue' value[ king ] has hreading or traling white space")
			.toString();
						
			String itemName = "a2";
			String itemTypeName = stringTypeName;
			String itemDefaultValue = " king ";
			String itemSize = null;
			String itemCharset = null;
			
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
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
	
	@Test
	public void testSingleItemInfo_잘못된디폴트값_부호없는숫자형_음수() {		
		Set<String> unsignedNumberTypeNameSet = new LinkedHashSet<String>();
		unsignedNumberTypeNameSet.add("unsigned byte");
		unsignedNumberTypeNameSet.add("unsigned short");
		unsignedNumberTypeNameSet.add("unsigned integer");		
		
		for (String unsignedNumberTypeName : unsignedNumberTypeNameSet) {			
				String testTitle = new StringBuilder(unsignedNumberTypeName)
				.append("잘못된디폴트값_부호없는숫자형").append("_음수").toString();
				
				String expectedMessage  = new StringBuilder("this '")
				.append(unsignedNumberTypeName)
				.append("' type single item[a2]'s attribute 'defaultValue' value[-1] is less than zero")
				.toString();
				
				String itemName = "a2";
				String itemTypeName = unsignedNumberTypeName;
				String itemDefaultValue = "-1";
				String itemSize = null;
				String itemCharset = null;
				
				
				SingleItemInfo singleItemInfo = null;
				
				try {
					singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
							itemDefaultValue, itemSize, itemCharset);
					
					singleItemInfo.hashCode();
					
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
	
	@Test
	public void testSingleItemInfo_잘못된디폴트값_부호없는숫자형_최대값초과() {		
		Set<String> unsignedNumberTypeNameSet = new LinkedHashSet<String>();
		unsignedNumberTypeNameSet.add("unsigned byte");
		unsignedNumberTypeNameSet.add("unsigned short");
		unsignedNumberTypeNameSet.add("unsigned integer");
		
		HashMap<String, String> numberTypeName2MaxOverValueHash = new HashMap<String, String>();
		numberTypeName2MaxOverValueHash.put("unsigned byte", String.valueOf(CommonStaticFinalVars.UNSIGNED_BYTE_MAX+1));
		numberTypeName2MaxOverValueHash.put("unsigned short", String.valueOf(CommonStaticFinalVars.UNSIGNED_SHORT_MAX+1));
		numberTypeName2MaxOverValueHash.put("unsigned integer", String.valueOf(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX+1));
		
		HashMap<String, String> numberTypeName2MaxValueHash = new HashMap<String, String>();
		numberTypeName2MaxValueHash.put("unsigned byte", String.valueOf(CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		numberTypeName2MaxValueHash.put("unsigned short", String.valueOf(CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		numberTypeName2MaxValueHash.put("unsigned integer", String.valueOf(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX));
		
		for (String unsignedNumberTypeName : unsignedNumberTypeNameSet) {				
			String testTitle = new StringBuilder(unsignedNumberTypeName).append("잘못된디폴트값_부호없는숫자형").append("_최대값초과").toString();
			String expectedMessage  = new StringBuilder("this '")
			.append(unsignedNumberTypeName)
			.append("' type single item[a2]'s attribute 'defaultValue' value[")
			.append(numberTypeName2MaxOverValueHash.get(unsignedNumberTypeName))
			.append("] is greater than ")
			.append(unsignedNumberTypeName)
			.append(" max[")
			.append(numberTypeName2MaxValueHash.get(unsignedNumberTypeName)).append("]")
			.toString();
			
			String itemName = "a2";
			String itemTypeName = unsignedNumberTypeName;
			String itemDefaultValue = numberTypeName2MaxOverValueHash.get(unsignedNumberTypeName);
			String itemSize = null;
			String itemCharset = null;			
			
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
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
	
	@Test
	public void testSingleItemInfo_필수속성크기생략() {		
		Set<String> sizeDemandTypeNameSet = new LinkedHashSet<String>();
		sizeDemandTypeNameSet.add("fixed length string");
		sizeDemandTypeNameSet.add("fixed length byte[]");
		
		for (String sizeDemandTypeName : sizeDemandTypeNameSet) {
			String testTitle = new StringBuilder(sizeDemandTypeName).append("필수속성크기생략").toString();
			
			String expectedMessage  = new StringBuilder("this '")
			.append(sizeDemandTypeName).append("' type single item[a2] needs attribute 'size'").toString();
			
			String itemName = "a2";
			String itemTypeName = sizeDemandTypeName;
			String itemDefaultValue = null;
			String itemSize = null;
			String itemCharset = null;
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
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
	
	@Test
	public void testSingleItemInfo_음수인필수속성크기() {		
		Set<String> sizeDemandTypeNameSet = new LinkedHashSet<String>();
		sizeDemandTypeNameSet.add("fixed length string");
		sizeDemandTypeNameSet.add("fixed length byte[]");
		
		for (String sizeDemandTypeName : sizeDemandTypeNameSet) {
			String testTitle = new StringBuilder(sizeDemandTypeName).append("음수인필수속성크기").toString();
			
			String expectedMessage  = new StringBuilder("this '")
			.append(sizeDemandTypeName).append("' type single item[a2]'s attribute 'size' value[-1] must be greater than zero").toString();
			
			String itemName = "a2";
			String itemTypeName = sizeDemandTypeName;
			String itemDefaultValue = null;
			String itemSize = "-1";
			String itemCharset = null;
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				log.log(Level.WARNING, e.toString(), e);
				String errorMessage = e.getMessage();
				
				/*log.info("expectedMessage={}, \nerrorMessage={}, isSame={}", 
						expectedMessage, errorMessage,
						errorMessage.equals(expectedMessage));*/
				if (! errorMessage.equals(expectedMessage)) {
					fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());				
				}
			}
		}
	}
	
		
	@Test
	public void testSingleItemInfo_문자인필수속성크기() {		
		Set<String> sizeDemandTypeNameSet = new LinkedHashSet<String>();
		sizeDemandTypeNameSet.add("fixed length string");
		sizeDemandTypeNameSet.add("fixed length byte[]");
		
		for (String sizeDemandTypeName : sizeDemandTypeNameSet) {
			String testTitle = new StringBuilder(sizeDemandTypeName).append("음수인필수속성크기").toString();
			
			String expectedMessage  = new StringBuilder("this '")
			.append(sizeDemandTypeName).append("' type single item[a2]'s attribute 'size' value[king] is not integer").toString();
			
			String itemName = "a2";
			String itemTypeName = sizeDemandTypeName;
			String itemDefaultValue = null;
			String itemSize = "king";
			String itemCharset = null;
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				log.log(Level.WARNING, e.toString(), e);
				String errorMessage = e.getMessage();
				
				/*log.info("expectedMessage={}, \nerrorMessage={}, isSame={}", 
						expectedMessage, errorMessage,
						errorMessage.equals(expectedMessage));*/
				if (! errorMessage.equals(expectedMessage)) {
					fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());				
				}
			}
		}
	}
	
	@Test
	public void testSingleItemInfo_잘못된문자셋명() {		
		Set<String> charsetOptionTypeNameSet = new LinkedHashSet<String>();
		charsetOptionTypeNameSet.add("ub pascal string");
		charsetOptionTypeNameSet.add("us pascal string");
		charsetOptionTypeNameSet.add("si pascal string");
		charsetOptionTypeNameSet.add("fixed length string");
		
		
		for (String charsetOptionTypeName : charsetOptionTypeNameSet) {
			String testTitle = new StringBuilder(charsetOptionTypeName).append("잘못된문자셋명").toString();
			
			String expectedMessage  = new StringBuilder("this '")
			.append(charsetOptionTypeName).append("' type single item[a2]'s attribute 'charset' value[king] is a bad charset name").toString();
			
			String itemName = "a2";
			String itemTypeName = charsetOptionTypeName;
			String itemDefaultValue = null;
			String itemSize = null;
			String itemCharset = "king";
			
			if (itemTypeName.equals("fixed length string")) {
				itemSize = "10";						
			}
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				log.log(Level.WARNING, e.toString(), e);
				String errorMessage = e.getMessage();
				
				/*log.info("expectedMessage={}, errorMessage={}, isSame={}", 
						expectedMessage, errorMessage,
						errorMessage.equals(expectedMessage));*/
				if (! errorMessage.equals(expectedMessage)) {
					fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());				
				}
			}
		}
	}
	
		
	@Test
	public void testSingleItemInfo_미지원하는defaultValue속성지정() {
		Set<String> noSupportTypeNameSet = new LinkedHashSet<String>();		
		noSupportTypeNameSet.add("ub variable length byte[]");
		noSupportTypeNameSet.add("us variable length byte[]");
		noSupportTypeNameSet.add("si variable length byte[]");
		noSupportTypeNameSet.add("fixed length byte[]");
		noSupportTypeNameSet.add("java sql date");
		noSupportTypeNameSet.add("java sql timestamp");
		
		for (String noSupportTypeName : noSupportTypeNameSet) {
			String testTitle = new StringBuilder(noSupportTypeName).append("부가정보_미지원하는defaultValue속성지정").toString();
			
			String expectedMessage  = new StringBuilder("this '")
			.append(noSupportTypeName).append("' type single item[a2] doesn't support attribute 'defaultValue'").toString();
			
			String itemName = "a2";
			String itemTypeName = noSupportTypeName;
			String itemDefaultValue = "NotNullOk";
			String itemSize = null;
			String itemCharset = null;
			
			if (itemTypeName.equals("fixed length byte[]")) {
				itemSize = "10";						
			}
			
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				singleItemInfo.hashCode();
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
	
	@Test
	public void testSingleItemInfo_미지원하는size속성지정() {
		Set<String> noSupportTypeNameSet = new LinkedHashSet<String>();
		noSupportTypeNameSet.add("byte");
		noSupportTypeNameSet.add("unsigned byte");
		noSupportTypeNameSet.add("short");
		noSupportTypeNameSet.add("unsigned short");
		noSupportTypeNameSet.add("integer");
		noSupportTypeNameSet.add("unsigned integer");
		noSupportTypeNameSet.add("long");
		noSupportTypeNameSet.add("ub pascal string");
		noSupportTypeNameSet.add("us pascal string");
		noSupportTypeNameSet.add("si pascal string");
		noSupportTypeNameSet.add("ub variable length byte[]");
		noSupportTypeNameSet.add("us variable length byte[]");
		noSupportTypeNameSet.add("si variable length byte[]");
		noSupportTypeNameSet.add("java sql date");
		noSupportTypeNameSet.add("java sql timestamp");
		noSupportTypeNameSet.add("boolean");
		
		for (String noSupportTypeName : noSupportTypeNameSet) {
			String testTitle = new StringBuilder(noSupportTypeName).append("부가정보_미지원하는size속성지정").toString();
			
			String expectedMessage  = new StringBuilder("this '")
			.append(noSupportTypeName).append("' type single item[a2] doesn't support attribute 'size'").toString();
			
			String itemName = "a2";
			String itemTypeName = noSupportTypeName;
			String itemDefaultValue = null;
			String itemSize = "10";
			String itemCharset = null;
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
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
	
	@Test
	public void testSingleItemInfo_미지원하는charset속성지정() {
		Set<String> noSupportTypeNameSet = new LinkedHashSet<String>();
		noSupportTypeNameSet.add("byte");
		noSupportTypeNameSet.add("unsigned byte");
		noSupportTypeNameSet.add("short");
		noSupportTypeNameSet.add("unsigned short");
		noSupportTypeNameSet.add("integer");
		noSupportTypeNameSet.add("unsigned integer");
		noSupportTypeNameSet.add("long");
		noSupportTypeNameSet.add("ub variable length byte[]");
		noSupportTypeNameSet.add("us variable length byte[]");
		noSupportTypeNameSet.add("si variable length byte[]");
		noSupportTypeNameSet.add("fixed length byte[]");
		noSupportTypeNameSet.add("java sql date");
		noSupportTypeNameSet.add("java sql timestamp");
		noSupportTypeNameSet.add("boolean");
		
		for (String noSupportTypeName : noSupportTypeNameSet) {
			String testTitle = new StringBuilder(noSupportTypeName).append("부가정보_미지원하는size속성지정").toString();
			
			String expectedMessage  = new StringBuilder("this '")
			.append(noSupportTypeName).append("' type single item[a2] doesn't support attribute 'charset'").toString();
			
			String itemName = "a2";
			String itemTypeName = noSupportTypeName;
			String itemDefaultValue = null;
			String itemSize = null;
			String itemCharset = "utf8";
			
			if (noSupportTypeName.equals("fixed length byte[]")) {
				itemSize = "10";
			}
			
			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
				
				singleItemInfo.hashCode();
				
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
}
