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

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;

/**
 * 메시지 디코더 관리자 인터페이스
 * @author Won Jonghoon
 *
 */
public interface MessageDecoderManagerIF {
	/**
	 * @param messageID 메시지 식별자
	 * @return 지정한 메시지 식별자에 1:1 대응하는 메시지 디코더
	 * @throws DynamicClassCallException 동적 클래스 처리 관련 에러 발생시 던지는 예외 
	 */
	public AbstractMessageDecoder getMessageDecoder(String messageID) throws DynamicClassCallException;
}
