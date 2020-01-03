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

package kr.pe.codda.common.config.fileorpathstringgetter;

/**
 * 파일 혹은 경로 전체 이름 게터 추상화 클래스
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractFileOrPathStringReturner {
	protected String itemID;

	/**
	 * 생성자
	 * 
	 * @param itemID 항목 식별자
	 */
	public AbstractFileOrPathStringReturner(String itemID) {
		this.itemID = itemID;
	}

	/**
	 * 지정한 설치 경로, 지정한 프로젝트 이름 그리고 지정한 부가정보에 부합하는 파일 혹은 경로 전체 이름을 만들어 반환한다
	 * @param installedPathString 설치 경로
	 * @param mainProjectName 메인 프로젝트 이름
	 * @param etcParamter 부가 정보
	 * @return 지정한 설치 경로, 지정한 프로젝트 이름 그리고 지정한 부가정보에 부합하는 파일 혹은 디렉토리 전체 이름
	 */
	public abstract String getFileOrPathString(
			String installedPathString, String mainProjectName, 
			String... etcParamter);

	/**
	 * @return 항목 식별자
	 */
	public String getItemID() {
		return itemID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractFileOrPathStringGetter [itemID=");
		builder.append(itemID);
		builder.append("]");
		return builder.toString();
	}
}
