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

package kr.pe.codda.common.classloader;

/**
 * 내장중인 시스템 클래스 검증기 인터페이스
 * @author Won Jonghoon
 *
 */
public interface SystemClassVerifierIF {
	
	/**
	 * @param classFullName 클래스 이름
	 * @return 시스템 클래스 여부, 지정한 클래스 이름이 시스템 클래스이면 참을 반환하고 아니면 거짓을 반환한다
	 */
	public boolean isSystemClassName(String classFullName);
}
