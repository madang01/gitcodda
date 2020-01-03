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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.UnknownItemTypeException;
import kr.pe.codda.common.type.SingleItemType;

/**
 * <pre>
 * 메시지 정보를 이루는 단일 항목 타입 관리자
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class SingleItemTypeManger {
	private final Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private String messageXSLStr = null;

	private final SingleItemType[] singleItemTypes = SingleItemType.values();

	private final LinkedHashMap<String, Integer> itemTypeNameToIDHash = new LinkedHashMap<String, Integer>();
	private final HashMap<Integer, String> itemIDToItemTypeNameHash = new HashMap<Integer, String>();

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class ItemTypeMangerHolder {
		static final SingleItemTypeManger singleton = new SingleItemTypeManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SingleItemTypeManger getInstance() {
		return ItemTypeMangerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private SingleItemTypeManger() {

		checkValidAllSingleItemType();

		for (SingleItemType singleItemType : singleItemTypes) {
			int itemTypeID = singleItemType.getItemTypeID();
			String itemTypeName = singleItemType.getItemTypeName();
			itemTypeNameToIDHash.put(itemTypeName, itemTypeID);
			itemIDToItemTypeNameHash.put(itemTypeID, itemTypeName);
		}

		/** 신규 타입 추가시 구현 언어인 자바 타입등을 정의한 SingleItemInfo 에도 추가를 해 주어야 한다. */

		StringBuilder mesgXSLStringBuilder = new StringBuilder();
		mesgXSLStringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		mesgXSLStringBuilder.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t<xs:group name=\"itemgroup\">\n");
		mesgXSLStringBuilder.append("\t\t<xs:choice>\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:element name=\"singleitem\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:complexType>\n");

		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"desc\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");

		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"name\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"type\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");

		for (String itemTypeName : itemTypeNameToIDHash.keySet()) {
			mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"");
			mesgXSLStringBuilder.append(itemTypeName);
			mesgXSLStringBuilder.append("\" />\n");
		}

		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"size\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"charset\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"defaultValue\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:element>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:element name=\"array\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:sequence>\n");
		mesgXSLStringBuilder
				.append("\t\t\t\t\t\t<xs:group minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"itemgroup\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:sequence>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"name\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"cnttype\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"reference\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"direct\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"cntvalue\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:element>\n");
		mesgXSLStringBuilder.append("\t\t</xs:choice>\n");
		mesgXSLStringBuilder.append("\t</xs:group>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t<xs:element name=\"");
		mesgXSLStringBuilder.append(CommonStaticFinalVars.MESSAGE_INFO_XML_FILE_ROOT_TAG);
		mesgXSLStringBuilder.append("\">\n");
		mesgXSLStringBuilder.append("\t\t<xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:sequence>\n");

		mesgXSLStringBuilder.append("\t\t\t\t<xs:element name=\"messageID\" minOccurs=\"1\" maxOccurs=\"1\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:pattern value=\"[a-zA-Z][a-zA-Z1-9]+\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:element>\n");

		mesgXSLStringBuilder.append("\t\t\t\t<xs:element name=\"direction\" minOccurs=\"1\" maxOccurs=\"1\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:pattern value=\"[a-zA-Z][a-zA-Z1-9_]+\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:element>\n");

		mesgXSLStringBuilder
				.append("\t\t\t\t<xs:element name=\"desc\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:group minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"itemgroup\" />\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t</xs:element>\n");
		mesgXSLStringBuilder.append("</xs:schema>\n");

		messageXSLStr = mesgXSLStringBuilder.toString();

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File f = File.createTempFile("MessageXSL", ".tmp");

			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);

			bw.write(messageXSLStr);

			String errorMessage = new StringBuilder().append("the message information .xsl temporary file[")
					.append(f.getAbsolutePath()).append("] was created successfully").toString();

			log.log(Level.INFO, errorMessage);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return 정의된 모든 SingleItemType이 유효하면 true, 아니면 false 를 반환한다.
	 */
	private void checkValidAllSingleItemType() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		int[] arrayOfSingleItemTypeID = new int[singleItemTypes.length];
		Arrays.fill(arrayOfSingleItemTypeID, -1);
		for (SingleItemType singleItemType : singleItemTypes) {
			int singleItemTypeID = singleItemType.getItemTypeID();
			try {
				arrayOfSingleItemTypeID[singleItemTypeID] = singleItemTypeID;
			} catch (IndexOutOfBoundsException e) {
				String errorMessage = new StringBuilder().append("the SingleItemType[")
						.append(singleItemType.toString()).append("] has a wrong id, the id[").append(singleItemTypeID)
						.append("] is out of the range[0 ~ ").append(singleItemTypes.length - 1).append("]").toString();

				log.log(Level.SEVERE, errorMessage);
				System.exit(1);
			}
		}
		for (int i = 0; i < arrayOfSingleItemTypeID.length; i++) {
			int singleItemTypeID = arrayOfSingleItemTypeID[i];
			if (-1 == singleItemTypeID) {
				String errorMessage = new StringBuilder()
						.append("the SingleItemType class don't define the SingleItemType id[").append(i).append("]")
						.toString();

				log.log(Level.SEVERE, errorMessage);
				System.exit(1);
			}
		}
	}

	public String getMessageXSLStr() {
		return messageXSLStr;
	}

	public ByteArrayInputStream getMesgXSLInputSream() {
		ByteArrayInputStream xslByteArrayInputStream = new ByteArrayInputStream(
				messageXSLStr.getBytes(CommonStaticFinalVars.SOURCE_FILE_CHARSET));
		return xslByteArrayInputStream;
	}

	public int getItemTypeID(String itemTypeName) throws UnknownItemTypeException {
		if (null == itemTypeName) {
			throw new IllegalArgumentException("the parameter itemTypeName is null");
		}
		Integer itemTypeID = itemTypeNameToIDHash.get(itemTypeName);
		if (null == itemTypeID) {
			String errorMessage = new StringBuilder("the parameter itemTypeName[").append(itemTypeName)
					.append("] is not an element of item value type set")
					.append(getUnmodifiableItemTypeNameSet().toString()).toString();
			UnknownItemTypeException e = new UnknownItemTypeException(errorMessage);
			throw e;
		}
		return itemTypeID.intValue();
	}

	public String getItemTypeName(int itemTypeID) throws UnknownItemTypeException {
		String itemTypeName = itemIDToItemTypeNameHash.get(itemTypeID);
		if (null == itemTypeName) {
			String errorMessage = new StringBuilder()
					.append("unknown message item type id[")
					.append(itemTypeID)
					.append("]").toString();
			UnknownItemTypeException e = new UnknownItemTypeException(errorMessage);
			// log.warn(errorMessage, e);
			e.printStackTrace();
			throw e;
		}
		return itemTypeName;
	}

	public int getAllSingleItemTypeCount() {
		return singleItemTypes.length;
	}

	public SingleItemType getSingleItemType(int singleItemTypeID) {
		if (singleItemTypeID < 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter singleItemTypeID[")
					.append(singleItemTypeID)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (singleItemTypeID >= singleItemTypes.length) {
			String errorMessage = new StringBuilder()
					.append("the parameter singleItemTypeID[")
					.append(singleItemTypeID)
					.append("] is out of range(0 ~ ")
					.append(singleItemTypes.length - 1)
					.append(")").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		return singleItemTypes[singleItemTypeID];
	}

	public SingleItemType getSingleItemType(String itemTypeName) throws UnknownItemTypeException {
		if (null == itemTypeName) {
			throw new IllegalArgumentException("the parameter itemTypeName is null");
		}

		Integer itemTypeID = itemTypeNameToIDHash.get(itemTypeName);
		if (null == itemTypeID) {
			String errorMessage = new StringBuilder("the parameter itemTypeName[").append(itemTypeName)
					.append("] is not an element of item value type set")
					.append(getUnmodifiableItemTypeNameSet().toString()).toString();
			throw new UnknownItemTypeException(errorMessage);
		}

		return singleItemTypes[itemTypeID];
	}

	public Set<String> getUnmodifiableItemTypeNameSet() {
		Set<String> itemTypeNameSet = Collections.unmodifiableSet(itemTypeNameToIDHash.keySet());
		return itemTypeNameSet;
	}
}
