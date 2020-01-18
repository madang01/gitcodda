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

public enum DocumentSateSearchType {
	ALL((byte)'A', "전체"), OK((byte)'Y', "정상"), DELETE((byte)'D', "삭제"); 
	
	private byte documentSateSearchTypeValue;
	private String documentSateSearchTypeName;

	private DocumentSateSearchType(byte documentSateSearchTypeValue, String documentSateSearchTypeName) {
		this.documentSateSearchTypeValue = documentSateSearchTypeValue;
		this.documentSateSearchTypeName = documentSateSearchTypeName;
	}
	
	/**
	 * @return 문서 상태 검색 유형 값
	 */
	public byte getValue() {
		return documentSateSearchTypeValue;
	}
	
	/**
	 * @return 문서 상태 검색 유형 이름
	 */
	public String getName() {
		return documentSateSearchTypeName;
	}

	/**
	 * @param documentStateTypeValue 문서 상태 유형 값
	 * @return 파라미터 'documentStateTypeValue' 와 값이 일치하는 문서 상태 유형
	 * @throws IllegalArgumentException 파라미터 'documentStateTypeValue'(=문서 상태 유형 값) 와 일치하는 값을 갖는 문서 상태 유형이 없을 경우 던지는 예외
	 */
	public static DocumentSateSearchType valueOf(byte documentSateSearchTypeValue) throws IllegalArgumentException {		
		
		DocumentSateSearchType[] documentSateSearchTypes = DocumentSateSearchType.values();
		for (DocumentSateSearchType documentSateSearchType : documentSateSearchTypes) {
			if (documentSateSearchType.getValue() == documentSateSearchTypeValue) {
				return documentSateSearchType;
			}
		}
		
		throw new IllegalArgumentException("문서 상태 검색 유형 값["+documentSateSearchTypeValue+"]이 잘못되었습니다");
	}
}
