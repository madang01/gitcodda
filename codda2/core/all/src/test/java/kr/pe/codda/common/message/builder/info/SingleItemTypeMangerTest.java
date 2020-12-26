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
import kr.pe.codda.common.type.MessageSingleItemType;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class SingleItemTypeMangerTest {	
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
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testGetSingleItemType_sigleItemID를통해얻은SigleItemType맞는지검사() {
		for (MessageSingleItemType expectedSingleItemType : MessageSingleItemType.values()) {
			MessageSingleItemType actualSingleItemType = MessageSingleItemTypeManger.getInstance().getSingleItemType(expectedSingleItemType.getItemTypeID());
			
			assertEquals("sigleItemID 를 통해 얻은 SigleItemType 맞는지 검사", expectedSingleItemType, actualSingleItemType);
		}
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
	public void testGetMesgXSLInputSream_메시지정보파일을스키마가지정된SAX파싱하기_OK() {
		
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			String errorMessage = e.toString();
			log.log(Level.WARNING, errorMessage, e);
			fail("fail to create a instance of MessageInfoSAXParser class");
		}
		MessageInfo messageInfo = null;
		try {
			File tempXMLFile = new File("AllDataType3.xml");
			tempXMLFile.deleteOnExit();

			String contents = buildContentsOfAllDataTypeXMLFile("AllDataType3");

			CommonStaticUtil.createNewFile(tempXMLFile, contents, Charset.defaultCharset());
			
			messageInfo = messageInfoSAXParser.parse(tempXMLFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = e.toString();
			log.log(Level.WARNING, errorMessage, e);
			fail("fail to parse the message information file");
		}
		
		log.info("schema가 지정된 sax 파싱 성공, MessageInfo=" + messageInfo.toString());
	}
	
	@Test
	public void testGetMessageXSLStr() {
		MessageSingleItemTypeManger singleItemTypeManger = MessageSingleItemTypeManger.getInstance();
		
		log.info("the message information xsl file=" + singleItemTypeManger.getMessageXSLStr());
	}
	
	
}
