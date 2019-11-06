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


package kr.pe.codda.common.exception;

/**
 * 알수 없는 메시지 항목 타입명을 받았을때 던지는 예외 
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class UnknownItemTypeException extends Exception {
	/**
	 * 생성자
	 * @param errorMessage 에러 메시지
	 */
	public UnknownItemTypeException(String errorMessage) {
		super(errorMessage);
	}
}
