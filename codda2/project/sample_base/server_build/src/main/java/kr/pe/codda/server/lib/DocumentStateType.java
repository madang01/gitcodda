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

package kr.pe.codda.server.lib;

/**
 * 문서 상태 유형 열거형
 * 
 * @author Won Jonghoon
 *
 */
public enum DocumentStateType {
	OK((byte)'Y', "정상"), DELETE((byte)'D', "삭제"); 
	
	private byte documentStateTypeValue;
	private String documentStateTypeName;

	private DocumentStateType(byte documentStateTypeValue, String documentStateTypeName) {
		this.documentStateTypeValue = documentStateTypeValue;
		this.documentStateTypeName = documentStateTypeName;
	}
	
	/**
	 * @return 문서 상태 유형 값
	 */
	public byte getValue() {
		return documentStateTypeValue;
	}
	
	/**
	 * @return 문서 상태 유형 이름
	 */
	public String getName() {
		return documentStateTypeName;
	}
	
	/**
	 * @param documentStateTypeValue 문서 상태 유형 값
	 * @return 파라미터 'documentStateTypeValue' 와 값이 일치하는 문서 상태 유형
	 * @throws IllegalArgumentException 파라미터 'documentStateTypeValue'(=문서 상태 유형 값) 와 일치하는 값을 갖는 문서 상태 유형이 없을 경우 던지는 예외
	 */
	public static DocumentStateType valueOf(byte documentStateTypeValue) throws IllegalArgumentException {		
		
		DocumentStateType[] documentStateTypes = DocumentStateType.values();
		for (DocumentStateType documentStateType : documentStateTypes) {
			if (documentStateType.getValue() == documentStateTypeValue) {
				return documentStateType;
			}
		}
		
		throw new IllegalArgumentException("문서 상태 유형 값["+documentStateTypeValue+"]이 잘못되었습니다");
	}
}
