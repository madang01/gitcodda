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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class MessageInfoSAXParserTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	MessageInfoSAXParser messageInfoSAXParser = null;

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
		/**
		 * Warning 로케일 설정 생략하지 말것. xml 파싱시 xsl에서 정의한 규칙에 어긋난 경우 로케일 설정에 따라 메시지를 보여주기때문에,
		 * 상황에 맞는 메시지가 나왔는지 점검을 위해서 기준이 되는 로케일로 영문을 선택하였다.
		 */
		Locale enLocale = new Locale("en-US");
		Locale.setDefault(enLocale);

		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * private String getFilePathStringForJunitTestFile(String shortFileName) { File
	 * installedPath = new File(".");
	 * 
	 * String testDataXmlFilePathString = new
	 * StringBuilder(installedPath.getAbsolutePath()) .append(File.separator)
	 * .append("core") .append(File.separator) .append("all")
	 * .append(File.separator) .append("src") .append(File.separator)
	 * .append("test") .append(File.separator) .append("resources")
	 * .append(File.separator) .append("message_info_xml_testdata")
	 * .append(File.separator) .append(shortFileName).toString();
	 * 
	 * return testDataXmlFilePathString; }
	 */


	@Test
	public void testGetMessageIDFromXMLFilePathString_NullParameter_messageInformationXMLFilePathString() {
		String expectedMessage = "the parameter messageInformationXMLFilePathString is null";
		String xmlFilePathString = null;
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (expectedMessage.equals(errorMessage))
				return;
		}
		fail(new StringBuilder("'").append(expectedMessage).append("' test failed").toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_EmptyString_messageInformationXMLFilePathString() {
		String expectedMessage = "the parameter messageInformationXMLFilePathString is a empty string";
		String xmlFilePathString = "";
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (expectedMessage.equals(errorMessage))
				return;
		}
		fail(new StringBuilder("'").append(expectedMessage).append("' test failed").toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_길이가작은경우() {
		String testTitle = "메시지 정보 파일명(메시지식별자+.xml) 길이가 작은 경우";
		String xmlFilePathString = ".xml";
		String expectedMessage = new StringBuilder()
				.append("the parameter messageInformationXMLFilePathString[")
				.append(xmlFilePathString)
				.append("]'s length[")
				.append(xmlFilePathString.length())
				.append("] is too small, its length must be greater than ")
				.append(MessageInfoSAXParser.XML_EXTENSION_SUFFIX.length()).toString();
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_messageInformationXMLFilePathString_확장자가xml이아닌경우() {
		String testTitle = "메시지 정보 파일명 길이가 4보다 커야 한다는 조건은 만족하지만 확장자가 xml 이 아닌 경우";
		String xmlFilePathString = "abcde";
		String expectedMessage = new StringBuilder()
				.append("the parameter messageInformationXMLFilePathString[")
				.append(xmlFilePathString)
				.append("]'s suffix is not '.xml'").toString();
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_잘못된메시지식별자를파일명으로가지는xml확장자파일명() {
		String testTitle = "잘못된 메시지 식별자를 파일명으로 가지는 xml 확장자 파일명";
		String messageID = "a";
		String xmlFilePathString = messageID + ".xml";
		String expectedMessage = new StringBuilder()
				.append("the parameter messageInformationXMLFilePathString[")
				.append(xmlFilePathString)
				.append("] has a invalid message id[")
				.append(messageID)
				.append("], (note) the message information XML file name format is '<messageID>.xml'").toString();
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_messageInformationXMLFilePathString_부모경로를갖지만메시지식별자가없고xml확장자를갖는파일명() {
		String testTitle = "부모 경로를 갖지만 메시지 식별자가 없고 xml 확장자를 갖는 파일명";
		String xmlFilePathString = File.separator + ".xml";
		String expectedMessage = new StringBuilder()
				.append("fail to get message id from the parameter messageInformationXMLFilePathString[")
				.append(xmlFilePathString)
				.append("] that is '<messageID>.xml' format file name").toString();
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_messageInformationXMLFilePathString_xml확장자를갖지않는파일명() {
		String testTitle = "xml 확장자를 갖지 않는 파일명";
		String xmlFilePathString = "a.xml2";
		String expectedMessage = new StringBuilder()
				.append("the parameter messageInformationXMLFilePathString[")
				.append(xmlFilePathString)
				.append("]'s suffix is not '.xml'").toString();
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString__ExpectedValueComparison() {
		String xmlFilePathString = "Ab.xml";
		String expectedValue = "Ab";
		String returnedValue = null;
		try {
			returnedValue = messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING, "IllegalArgumentException", e);
			fail(e.getMessage());
		}

		assertEquals("the expected value comparison", returnedValue, expectedValue);
	}

	private String buildContentsOfAllDataTypeXMLFile(String messageID) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference 변수참조, direct 직접입력");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : 메시지는 서버에서 클라이언트로 혹은 클라이언트에서 서버로 양쪽 모두에서 전송되지 않는다.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : 메시지는 서버에서 클라이언트로만 전송된다.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : 메시지는 클라이언트에서 서버로만 전송된다.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : 메시지는 서버에서 클라이언트로도 혹은 클라이언트에서 서버로 양쪽 모두에서 전송된다.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		
		stringBuilder.append("<messageID>");
		stringBuilder.append(messageID);
		stringBuilder.append("</messageID>");
		
		
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"<desc>\uACE0\uC815 \uD06C\uAE30 \uC2A4\uD2B8\uB9BC\uC5D0\uC11C \uBAA8\uB4E0 \uB370\uC774\uD130 \uD0C0\uC785\uC744 \uD14C\uC2A4\uD2B8 \uD558\uAE30 \uC704\uD55C \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar2\" type=\"byte\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar3\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar1\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar2\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar3\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar1\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar2\" type=\"short\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar3\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar1\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar2\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar3\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar1\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar2\" type=\"integer\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar3\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar1\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar2\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar3\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar1\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar2\" type=\"long\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar3\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar1\" type=\"ub pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar2\" type=\"us pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar3\" type=\"si pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar1\" type=\"fixed length byte[]\" size=\"7\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar2\" type=\"si variable length byte[]\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqldate\" type=\"java sql date\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqltimestamp\" type=\"java sql timestamp\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sizeOfmember\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"reference\" cntvalue=\"sizeOfmember\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member2\" cnttype=\"direct\" cntvalue=\"1\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sizeOfmember2\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar4\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");

		return stringBuilder.toString();
	}

	@Test
	public void testParse_ok() {
		String testTitle = "정상적인 경우";

		try {
			File tempXMLFile = new File("AllDataType.xml");
			tempXMLFile.deleteOnExit();

			String contents = buildContentsOfAllDataTypeXMLFile("AllDataType");

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());

			MessageInfo messageInfo = messageInfoSAXParser.parse(tempXMLFile, true);

			log.info(messageInfo.toString());
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);
			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}

	@Test
	public void testParse_XSLRuleError_파일크기0인파일() {
		String testTitle = "파일 크기 0인 파일";

		try {
			File tempXMLFile = new File("Zero.xml");
			tempXMLFile.deleteOnExit();

			String contents = "";

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());

			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");

		} catch (SAXException e) {
			String errorMessage = e.getMessage();

			log.info(errorMessage);

			final String expectedMessage = "Premature end of file.";

			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);

				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}

		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}

	}

	private String buildContentsOfBadRootElementXMLFile(String messageID) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder
				.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"root\" cnttype=\"direct\" cntvalue=\"1\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		
		stringBuilder.append("<messageID>");
		stringBuilder.append(messageID);
		stringBuilder.append("</messageID>");
		
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"<desc>\uACE0\uC815 \uD06C\uAE30 \uC2A4\uD2B8\uB9BC\uC5D0\uC11C \uBAA8\uB4E0 \uB370\uC774\uD130 \uD0C0\uC785\uC744 \uD14C\uC2A4\uD2B8 \uD558\uAE30 \uC704\uD55C \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar2\" type=\"byte\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar3\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar1\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar2\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar3\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar1\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar2\" type=\"short\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar3\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar1\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar2\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar3\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar1\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar2\" type=\"integer\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar3\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar1\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar2\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar3\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar1\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar2\" type=\"long\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar3\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar1\" type=\"ub pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar2\" type=\"us pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar3\" type=\"si pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar1\" type=\"fixed length byte[]\" size=\"7\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar2\" type=\"si variable length byte[]\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqldate\" type=\"java sql date\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqltimestamp\" type=\"java sql timestamp\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"cnt\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");

		return stringBuilder.toString();
	}

	@Test
	public void testParse_XSLRuleError_메시지ROOT태그가아닌것을ROOT태그로사용() {
		String testTitle = "메시지 ROOT 태그가 아닌것을 ROOT 태그로 사용";

		try {
			File tempXMLFile = new File("BadRootElement.xml");
			tempXMLFile.deleteOnExit();

			String contents = buildContentsOfBadRootElementXMLFile("BadRootElement");

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());

			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");

		} catch (SAXException e) {
			String errorMessage = e.getMessage();

			log.info(errorMessage);

			String expectedMessage = "Cannot find the declaration of element";

			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);

				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}

		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}

	private String buildContentsOfNoMessageIDTagXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder
				.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"<desc>\uACE0\uC815 \uD06C\uAE30 \uC2A4\uD2B8\uB9BC\uC5D0\uC11C \uBAA8\uB4E0 \uB370\uC774\uD130 \uD0C0\uC785\uC744 \uD14C\uC2A4\uD2B8 \uD558\uAE30 \uC704\uD55C \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar2\" type=\"byte\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar3\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar1\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar2\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar3\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar1\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar2\" type=\"short\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar3\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar1\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar2\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar3\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar1\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar2\" type=\"integer\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar3\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar1\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar2\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar3\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar1\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar2\" type=\"long\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar3\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar1\" type=\"ub pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar2\" type=\"us pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar3\" type=\"si pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar1\" type=\"fixed length byte[]\" size=\"7\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar2\" type=\"si variable length byte[]\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqldate\" type=\"java sql date\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqltimestamp\" type=\"java sql timestamp\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"cnt\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(
				"\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}

	@Test
	public void testParse_XSLRuleError_messageID태그가없는경우() {
		String testTitle = "messageID 태그가 없는 경우";

		try {
			File tempXMLFile = new File("NoMessageIDTag.xml");
			tempXMLFile.deleteOnExit();

			String contents = buildContentsOfNoMessageIDTagXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());

			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");
		} catch (SAXException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);

			String expectedMessage = "One of '{messageID}' is expected.";			
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}	
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfSingleItemNoNameAttributeXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>SingleItemNoNameAttribute</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uB2E8\uC77C\uD56D\uBAA9 \uD0DC\uADF8\uC5D0\uC11C '\uC774\uB984' \uC18D\uC131\uC774 \uBE60\uC9C4 \uACBD\uC6B0 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}

	@Test
	public void testParse_XSLRuleError_단일항목_이름속성생략() {
		String testTitle = "단일 항목 이름 속성 생략";

		try {
			File tempXMLFile = new File("SingleItemNoNameAttribute.xml");
			tempXMLFile.deleteOnExit();

			String contents = buildContentsOfSingleItemNoNameAttributeXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());

			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");		
		} catch (SAXException e) {
			String errorMessage = e.getMessage();

			log.info(errorMessage);
			
			String expectedMessage = "Attribute 'name' must appear on element 'singleitem'.";
			
			if (-1 == errorMessage.indexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfSingleItemNoTypeAttributeXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>SingleItemNoTypeAttribute</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uB2E8\uC77C\uD56D\uBAA9 \uD0DC\uADF8\uC5D0\uC11C '\uD0C0\uC785' \uC18D\uC131\uC774 \uBE60\uC9C4 \uACBD\uC6B0 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}

	@Test
	public void testParse_XSLRuleError_단일항목_타입속성생략() {
		String testTitle = "단일 항목 타입 속성 생략";
		
		// File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemNoTypeAttribute.xml"));
		try {
			File tempXMLFile = new File("SingleItemNoTypeAttribute.xml");
			tempXMLFile.deleteOnExit();			

			String contents = buildContentsOfSingleItemNoTypeAttributeXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			

			messageInfoSAXParser.parse(tempXMLFile, true);			

			fail("no SAXException");		
		} catch (SAXException e) {
			String errorMessage = e.getMessage();

			log.info(errorMessage);
			
			String expectedMessage = "Attribute 'type' must appear on element 'singleitem'.";
			
			if (-1 == errorMessage.indexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfSingleItemDoubleNameAttributeXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>SingleItemDoubleNameAttribute</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>2\uBC88 \uC911\uBCF5\uB418\uB294 '\uC774\uB984' \uC18D\uC131\uC744 \uAC16\uB294 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}

	@Test
	public void testParse_XSLRuleError_단일항목_2번중복되는이름속성() {
		String testTitle = "단일 항목 2번 중복되는 이름 속성";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemDoubleNameAttribute.xml"));
		
		try {
			// SingleItemNameDuplication
			File tempXMLFile = new File("SingleItemDoubleNameAttribute.xml");
			tempXMLFile.deleteOnExit();			

			String contents = buildContentsOfSingleItemDoubleNameAttributeXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");		
		} catch (SAXException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "Attribute \"name\" was already specified for element \"singleitem\".";
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfSingleItemDoubleDefaultValueAttributeXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>SingleItemDoubleDefaultValueAttribute</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>2\uBC88 \uC911\uBCF5\uB418\uB294 '\uC774\uB984' \uC18D\uC131\uC744 \uAC16\uB294 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" defaultValue=\"10\" defaultValue=\"20\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}
	

	@Test
	public void testParse_XSLRuleError_단일항목_2번중복되는디폴트값속성() {
		String testTitle = "단일 항목 2번 중복되는 디폴트값 속성";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemDoubleDefaultValueAttribute.xml"));
		
		try {
			
			File tempXMLFile = new File("SingleItemDoubleDefaultValueAttribute.xml");
			tempXMLFile.deleteOnExit();			

			String contents = buildContentsOfSingleItemDoubleDefaultValueAttributeXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");		
		} catch (SAXException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "Attribute \"defaultValue\" was already specified for element \"singleitem\".";
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	
	private String buildContentsOfArrayItemNoNameAttributeXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>ArrayItemNoNameAttribute</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uBC30\uC5F4 \uD0DC\uADF8\uC5D0\uC11C '\uC774\uB984' \uC18D\uC131\uC774 \uBE60\uC9C4 \uACBD\uC6B0 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sizeOfmember\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array cnttype=\"reference\" cntvalue=\"sizeOfmember\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}
	

	@Test
	public void testParse_XSLRuleError_배열_이름속성생략() {
		String testTitle = "배열 이름 속성 생략";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemNoNameAttribute.xml"));
		
		try {
			File tempXMLFile = new File("ArrayItemNoNameAttribute.xml");
			tempXMLFile.deleteOnExit();			

			String contents = buildContentsOfArrayItemNoNameAttributeXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			
			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");		
		} catch (SAXException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "Attribute 'name' must appear on element 'array'.";
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}

	private String buildContentsOfArrayItemNoCntTypeAttributeXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>ArrayItemNoCntTypeAttribute</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uBC30\uC5F4 \uD0DC\uADF8\uC5D0\uC11C '\uBC18\uBCF5 \uD69F\uC218 \uD0C0\uC785' \uC18D\uC131\uC774 \uBE60\uC9C4 \uACBD\uC6B0 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sizeOfmember\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cntvalue=\"sizeOfmember\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}
	
	@Test
	public void testParse_XSLRuleError_배열_반복횟수타입속성생략() {
		String testTitle = "배열 '반복 횟수 타입' 속성 생략";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemNoCntTypeAttribute.xml"));
		
		try {
			File tempXMLFile = new File("ArrayItemNoCntTypeAttribute.xml");
			tempXMLFile.deleteOnExit();			

			String contents = buildContentsOfArrayItemNoCntTypeAttributeXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");
		} catch (SAXException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "Attribute 'cnttype' must appear on element 'array'.";
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}

	
	private String buildContentsOfArrayItemNoCntValueAttributeXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" single item value type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t fixed length string, ub|us|si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t \tfixed length byte[], ub|us|si variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t \t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdefaultValue : Warning! \uD30C\uC2A4\uCE7C \uBB38\uC790\uC5F4 \uD0C0\uC785\uC5D0 \uB514\uD3F4\uD2B8 \uAC12\uC744 \uC9C0\uC815\uD560\uB54C ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uC1A1\uC218\uC2E0 \uC5D0\uB7EC\uAC00 \uBC1C\uC0DD\uD560 \uC218 \uC788\uAE30\uB54C\uBB38\uC5D0 \uC8FC\uC758\uAC00 \uD544\uC694\uD558\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uC774\uB294 \uC1A1\uC218\uC2E0\uC2DC \uD30C\uC2A4\uCE7C \uBB38\uC790\uC5F4 \uAE38\uC774\uC5D0 \uC81C\uC57D\uC774 \uC788\uB294\uB370 ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uC774 \uC81C\uC57D\uC744 \uC5B4\uAE34 \uB514\uD3F4\uD2B8 \uAC12\uC744 \uC9C0\uC815\uD588\uAE30\uB54C\uBB38\uC774\uB2E4. ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uC774 \uC81C\uC57D\uC744 \uAC80\uC0AC\uD558\uC9C0 \uC54A\uACE0 \uB514\uD3F4\uD2B8 \uAC12\uC744 \uD5C8\uC6A9\uD558\uB294 \uC774\uC720\uB294 ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uAC80\uC0AC \uC218\uD589 \uC2DC\uC810\uC744 \uC1A1\uC218\uC2E0 \uD560\uB54C\uB85C \uBCF4\uB958 \uD588\uAE30\uB54C\uBB38\uC774\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uAC80\uC0AC \uC218\uD589 \uC2DC\uC810\uC744 \uC1A1\uC218\uC2E0 \uD560\uB54C\uB85C \uBCF4\uB958\uD55C \uC774\uC720\uB294 \uB2E4\uC74C\uACFC \uAC19\uB2E4. ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uBE44\uC9C0\uB2C8\uC2A4 \uB85C\uC9C1 \uC218\uD589 \uACFC\uC815\uC5D0\uC11C \uD30C\uC2A4\uCE7C \uBB38\uC790\uC5F4 \uD0C0\uC785 \uD56D\uBAA9\uC740 ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uC1A1\uC218\uC2E0\uC2DC \uC81C\uC57D\uC0AC\uD56D\uACFC \uC0C1\uAD00\uC5C6\uB294 \uAC12\uC744 \uAC00\uC9C8 \uC218 \uC788\uAE30\uB54C\uBB38\uC774\uB2E4. ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uB610\uD55C \uBD80\uAC00\uC801\uC778 \uC774\uC720\uC911 \uD558\uB098\uB294 \uD30C\uC2A4\uCE7C \uBB38\uC790\uC5F4 \uAE38\uC774\uB294 ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uC9C0\uC815\uD55C \uBB38\uC790\uC14B\uC744 \uAC16\uB294 \uBC14\uC774\uD2B8 \uBC30\uC5F4\uC758 \uD06C\uAE30\uC778\uB370, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uD30C\uC2A4\uCE7C \uBB38\uC790\uC5F4 \uD56D\uBAA9\uC758 \uBB38\uC790\uC14B\uC740 \uC635\uC158\uC774\uACE0, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uBBF8 \uC9C0\uC815\uC2DC \uAC16\uAC8C \uB418\uB294 \uBB38\uC790\uC14B\uC740 ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uC791\uC5C5\uC911\uC778 \uD504\uB85C\uC81D\uD2B8\uC758 \uD658\uACBD\uBCC0\uC218 '\uBB38\uC790\uC14B' \uC778\uB370 ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(" \uC791\uC5C5\uC911\uC778 \uD504\uB85C\uC81D\uD2B8\uB294 \uC1A1\uC218\uC2E0\uB54C \uACB0\uC815\uB418\uAE30\uB54C\uBB38\uC774\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>ArrayItemNoCntValueAttribute</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uBC30\uC5F4 \uD0DC\uADF8\uC5D0\uC11C '\uBC18\uBCF5 \uD69F\uC218 \uAC12' \uC18D\uC131\uC774 \uBE60\uC9C4 \uACBD\uC6B0 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sizeOfmember\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"reference\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	
	@Test
	public void testParse_XSLRuleError_배열_반복횟수값속성생략() {
		String testTitle = "배열 '반복 횟수 값' 속성 생략";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemNoCntValueAttribute.xml"));
		
		try {
			File tempXMLFile = new File("ArrayItemNoCntValueAttribute.xml");
			tempXMLFile.deleteOnExit();			

			String contents = buildContentsOfArrayItemNoCntValueAttributeXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");
		} catch (SAXException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "Attribute 'cntvalue' must appear on element 'array'.";
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}

	private String buildContentsOfBadMessageIDXMLFile(String messageID) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>");
		stringBuilder.append(messageID);
		stringBuilder.append("</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uBA54\uC2DC\uC9C0 \uC815\uBCF4 \uD30C\uC77C\uBA85\uC5D0 \uD3EC\uD568\uB41C \uBA54\uC2DC\uC9C0 \uC2DD\uBCC4\uC790\uC640 \uB2E4\uB978 \uBA54\uC2DC\uC9C0 \uC2DD\uBCC4\uC790\uB97C \uAC00\uC9C0\uB294 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar2\" type=\"byte\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar3\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar1\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar2\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar3\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar1\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar2\" type=\"short\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar3\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar1\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar2\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar3\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar1\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar2\" type=\"integer\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar3\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar1\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar2\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar3\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar1\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar2\" type=\"long\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar3\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar1\" type=\"ub pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar2\" type=\"us pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar3\" type=\"si pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar1\" type=\"fixed length byte[]\" size=\"7\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar2\" type=\"si variable length byte[]\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqldate\" type=\"java sql date\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqltimestamp\" type=\"java sql timestamp\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"cnt\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member2\" cnttype=\"direct\" cntvalue=\"1\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	@Test
	public void testParse_xsl만족하지만잘못된문서_메시지아이디파일명과messageID태그의값불일치() {
		String testTitle = "메시지 아이디 파일명과 messageID 태그의 값 불일치";
		String messageIDTagValue = "IAmNotBadMessageID";
		String messageIDOfFileName = "BadMessageID";
		// File xmlFile = new File(getFilePathStringForJunitTestFile(messageIDOfFileName + ".xml"));
		File tempXMLFile = new File("BadMessageID.xml");
		tempXMLFile.deleteOnExit();	
		
		try {
			String contents = buildContentsOfBadMessageIDXMLFile(messageIDTagValue);

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			messageInfoSAXParser.parse(tempXMLFile, true);
			

			fail("no SAXException");
		} catch (SAXException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = new StringBuilder()
					.append("The tag \"messageid\"'s value[")
					.append(messageIDTagValue)
					.append("] is different from message id[")
					.append(messageIDOfFileName)
					.append("] of '<message id>.xml' format file[")
					.append(tempXMLFile.getAbsolutePath())
					.append("]").toString();
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfBadDirectionXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], variable length byte[] ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>BadDirection</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>AAABBFROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uC798\uBABB\uB41C \uD1B5\uC2E0 \uBC29\uD5A5\uC131 \uAC12\uC744 \uAC00\uC9C0\uB294 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"randomInt\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"startTime\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_잘못된통신방향성() {
		String testTitle = "잘못된 통신 방향성";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("BadDirection.xml"));
		
		try {
			File tempXMLFile = new File("BadDirection.xml");
			tempXMLFile.deleteOnExit();	
			String contents = buildContentsOfBadDirectionXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			
			messageInfoSAXParser.parse(tempXMLFile, true);

		
			fail("no SAXException");
		} catch (SAXException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "is not a member of direction set[FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL]";
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfSingleItemNameDuplicationXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>SingleItemNameDuplication</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uC911\uBCF5 \uD56D\uBAA9\uC744 \uAC16\uB294 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar2\" type=\"byte\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar3\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar1\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar2\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar3\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar1\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar2\" type=\"short\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar3\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar1\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar2\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar3\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar1\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar2\" type=\"integer\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar3\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar1\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar2\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar3\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar1\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar2\" type=\"long\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar3\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar1\" type=\"ub pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar2\" type=\"us pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar3\" type=\"si pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar1\" type=\"fixed length byte[]\" size=\"7\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar2\" type=\"si variable length byte[]\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqldate\" type=\"java sql date\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqltimestamp\" type=\"java sql timestamp\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"cnt\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member2\" cnttype=\"direct\" cntvalue=\"1\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_단일항목_중복항목() {
		String testTitle = "단일 항목-중복";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemNameDuplication.xml"));
		String duplicationTagName = "itemID";
		
		try {
			File tempXMLFile = new File("SingleItemNameDuplication.xml");
			tempXMLFile.deleteOnExit();	
			String contents = buildContentsOfSingleItemNameDuplicationXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");
		} catch (SAXException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "this single item name[" + duplicationTagName + "] was duplicated";
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}

	private String buildContentsOfSingleItemNumberTypeBadDefaultValueXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>SingleItemNumberTypeBadDefaultValue</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uC22B\uC790\uD615 \uB2E8\uC77C \uD56D\uBAA9\uC758 \uB514\uD3F4\uD2B8\uAC12\uC73C\uB85C \uBB38\uC790\uB97C \uB123\uC740 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar2\" type=\"byte\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar3\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar1\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar2\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar3\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar1\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar2\" type=\"short\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar3\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar1\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar2\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar3\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar1\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar2\" type=\"integer\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar3\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar1\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar2\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar3\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar1\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar2\" type=\"long\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar3\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sizeOfmember\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"reference\" cntvalue=\"sizeOfmember\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" defaultValue=\"ab\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	@Test
	public void testParse_xsl만족하지만잘못된문서_단일항목_숫자형_디폴트값문자() {
		String testTitle = "단일 항목-숫자형-디폴트값 문자";
		
		// File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemNumberTypeBadDefaultValue.xml"));
		
		
		try {
			File tempXMLFile = new File("SingleItemNumberTypeBadDefaultValue.xml");
			tempXMLFile.deleteOnExit();	
			String contents = buildContentsOfSingleItemNumberTypeBadDefaultValueXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			
			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");
		} catch (SAXException e) {			
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "fail to parses the string argument(=this 'integer' type single item[itemCnt]'s attribute 'defaultValue' value[ab]) as a signed decimal integer";
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfArrayItemNameDuplicationXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>ArrayItemNameDuplication</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uBC30\uC5F4 \uC774\uB984\uC774 \uAC19\uC740 \uAE4A\uC774\uC758 \uC911\uBCF5\uC778 \uC911\uBCF5 \uD56D\uBAA9\uC744 \uAC16\uB294 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar2\" type=\"byte\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar3\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar1\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar2\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedByteVar3\" type=\"unsigned byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar1\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar2\" type=\"short\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"shortVar3\" type=\"short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar1\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar2\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedShortVar3\" type=\"unsigned short\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar1\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar2\" type=\"integer\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"intVar3\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar1\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar2\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"unsignedIntVar3\" type=\"unsigned integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar1\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar2\" type=\"long\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar3\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar1\" type=\"ub pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar2\" type=\"us pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"strVar3\" type=\"si pascal string\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar1\" type=\"fixed length byte[]\" size=\"7\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"bytesVar2\" type=\"si variable length byte[]\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqldate\" type=\"java sql date\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"sqltimestamp\" type=\"java sql timestamp\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"cnt\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"byteVar1\" cnttype=\"direct\" cntvalue=\"1\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"reference\" cntvalue=\"cnt\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_배열항목_중복() {
		String testTitle = "배열 항목-중복";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemNameDuplication.xml"));
		String duplicationTagName = "byteVar1";
		
		try {
			File tempXMLFile = new File("ArrayItemNameDuplication.xml");
			tempXMLFile.deleteOnExit();	
			String contents = buildContentsOfArrayItemNameDuplicationXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			
			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");
		} catch (SAXException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "this array item name[" + duplicationTagName + "] was duplicated";
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfArrayItemDirectBadSizeXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], ub variable length byte[], ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tus variable length byte[], si variable length byte[]");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tjava sql date, java sql timestamp, boolean");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>ArrayItemDirectBadSize</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uC9C1\uC811 \uC785\uB825 \uBC29\uC2DD\uC758 \uBC30\uC5F4\uC758 \uD06C\uAE30\uC5D0 \uBB38\uC790\uB97C \uB123\uC740 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar1\" type=\"byte\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar2\" type=\"byte\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"byteVar3\" type=\"byte\" defaultValue=\"123\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar1\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar2\" type=\"long\" />\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"longVar3\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<array name=\"member\" cnttype=\"direct\" cntvalue=\"1\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberID\" type=\"fixed length string\" size=\"30\" defaultValue=\"king\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"memberName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<singleitem name=\"cnt\" type=\"integer\" defaultValue=\"10\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<array name=\"item\" cnttype=\"direct\" cntvalue=\"hello\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemID\" type=\"fixed length string\" size=\"30\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemName\" type=\"fixed length string\" size=\"30\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<singleitem name=\"itemCnt\" type=\"integer\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</array>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		return stringBuilder.toString();
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_배열항목_크기직접입력방식에서문자인크기() {
		String testTitle = "배열 항목-크기 직접 입력 방식에서 문자인 크기";
		// File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemDirectBadSize.xml"));
		
		try {
			File tempXMLFile = new File("ArrayItemDirectBadSize.xml");
			tempXMLFile.deleteOnExit();	
			String contents = buildContentsOfArrayItemDirectBadSizeXMLFile();

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			

			messageInfoSAXParser.parse(tempXMLFile, true);

			fail("no SAXException");
		} catch (SAXException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedMessage = "fail to parses the string argument(=this array item[item]'s attribute 'cntvalue' value[hello]) as a signed decimal integer";
			
			if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
				log.log(Level.WARNING, errorMessage, e);
				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);

			fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
		}
	}
	
	private String buildContentsOfEchoXMLFile() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\titem type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tub pascal string, us pascal string, si pascal string, ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tfixed length byte[], variable length byte[] ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tarray counter type : reference \uBCC0\uC218\uCC38\uC870, direct \uC9C1\uC811\uC785\uB825");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\tdirection : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(1) FROM_NONE_TO_NONE : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB418\uC9C0 \uC54A\uB294\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(2) FROM_SERVER_TO_CLINET : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(3) FROM_CLIENT_TO_SERVER : \uBA54\uC2DC\uC9C0\uB294 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C\uB9CC \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t(4) FROM_ALL_TO_ALL : \uBA54\uC2DC\uC9C0\uB294 \uC11C\uBC84\uC5D0\uC11C \uD074\uB77C\uC774\uC5B8\uD2B8\uB85C\uB3C4 \uD639\uC740 \uD074\uB77C\uC774\uC5B8\uD2B8\uC5D0\uC11C \uC11C\uBC84\uB85C \uC591\uCABD \uBAA8\uB450\uC5D0\uC11C \uC804\uC1A1\uB41C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<message>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<messageID>Echo</messageID>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<direction>FROM_ALL_TO_ALL</direction>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<desc>\uC5D0\uCF54 \uBA54\uC2DC\uC9C0</desc>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"randomInt\" type=\"integer\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<singleitem name=\"startTime\" type=\"long\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</message>");
		stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}

	@Test
	public void testParse_재사용하여반복사용가능한지여부판단을위한여러번호출() {
		String testTitle = "중복 항목";
		String subTestTitle = null;

		{
			subTestTitle = "메시지 아이디 파일명과 messageID 태그의 값 불일치";
			String messageIDTagValue = "IAmNotBadMessageID";
			String messageIDOfFileName = "BadMessageID2";
			// xmlFile = new File(getFilePathStringForJunitTestFile(messageIDOfFileName + ".xml"));
			File tempXMLFile = new File(messageIDOfFileName +".xml");
			tempXMLFile.deleteOnExit();
			
			try {				
				String contents = buildContentsOfBadMessageIDXMLFile(messageIDTagValue);

				CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
				
				
				messageInfoSAXParser.parse(tempXMLFile, true);

				fail("no SAXException");			
			} catch (SAXException e) {
				String errorMessage = e.getMessage();
				
				log.info(errorMessage);
				
				String expectedMessage = new StringBuilder()
						.append("The tag \"messageid\"'s value[")
						.append(messageIDTagValue)
						.append("] is different from message id[")
						.append(messageIDOfFileName)
						.append("] of '<message id>.xml' format file[")
						.append(tempXMLFile.getAbsolutePath())
						.append("]").toString();

				if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
					log.log(Level.WARNING, errorMessage, e);
					fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
				}
			} catch (Exception e) {
				log.log(Level.WARNING, e.toString(), e);

				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		}

		{
			subTestTitle = "첫번째 정상";
			
			try {
				File tempXMLFile = new File("AllDataType2.xml");
				tempXMLFile.deleteOnExit();

				String contents = buildContentsOfAllDataTypeXMLFile("AllDataType2");

				CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
				
				MessageInfo messageInfo = messageInfoSAXParser.parse(tempXMLFile, true);

				log.info(messageInfo.toString());				
			} catch (Exception e) {
				log.log(Level.WARNING, e.toString(), e);
				fail(new StringBuilder("'").append(testTitle).append("-").append(subTestTitle).append("' test failed")
						.toString());
			}
		}

		{
			subTestTitle = "메시지 ROOT 태그가 아닌것을 ROOT 태그로 사용";

			
			try {
				File tempXMLFile = new File("BadRootElement2.xml");
				tempXMLFile.deleteOnExit();

				String contents = buildContentsOfBadRootElementXMLFile("BadRootElement2");

				CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
				
				messageInfoSAXParser.parse(tempXMLFile, true);

				fail("no SAXException");
		
			} catch (SAXException e) {
				log.log(Level.WARNING, e.toString(), e);
				String errorMessage = e.getMessage();
				
				String expectedMessage = "Cannot find the declaration of element 'array'";
				
				if (-1 == errorMessage.lastIndexOf(expectedMessage)) {
					log.log(Level.WARNING, errorMessage, e);
					fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
				}
			} catch (Exception e) {
				log.log(Level.WARNING, e.toString(), e);

				fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
			}
		}
		
		{
			subTestTitle = "두번째 정상";
			// xmlFile = new File(getFilePathStringForJunitTestFile("Echo.xml"));
			try {
				File tempXMLFile = new File("Echo.xml");
				tempXMLFile.deleteOnExit();

				String contents = buildContentsOfEchoXMLFile();

				CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
				
				MessageInfo messageInfo = messageInfoSAXParser.parse(tempXMLFile, true);

				log.info(messageInfo.toString());
			} catch (IllegalArgumentException | SAXException | IOException e) {
				log.log(Level.WARNING, e.toString(), e);
				fail(new StringBuilder("'").append(testTitle).append("-").append(subTestTitle).append("' test failed")
						.toString());
			}
		}
	}
}
