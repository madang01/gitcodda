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

import kr.pe.codda.common.etc.CommonStaticFinalVars;

/**
 * 실질적인 입출력을 담당하는 클래스 이름 관련 유틸 
 * @author Won Jonghoon
 *
 */
public abstract class IOPartDynamicClassNameUtil {
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 메시지 클래스 짧은 이름
	 */
	public static String getMessageClassShortName(String messageID) {
		return messageID;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 메시지 클래스 전체 이름
	 */
	public static String getMessageClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getMessageClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 클라이언트 메시지 코덱 클래스 짧은 이름
	 */
	public static String getClientMessageCodecClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)				
				.append("ClientCodec").toString();
		return classFullName;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 클라이언트 메시지 코덱 클래스 전체 이름
	 */
	public static String getClientMessageCodecClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getClientMessageCodecClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 서버 메시지 코덱 클래스 짧은 이름
	 */
	public static String getServerMessageCodecClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ServerCodec").toString();
		return classFullName;
	}
	
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 서버 메시지 코덱 클래스 전체 이름
	 */
	public static String getServerMessageCodecClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getServerMessageCodecClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 서버 타스크 클래스 짧은 이름
	 */
	public static String getServerTaskClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ServerTask").toString();
		return classFullName;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 서버 타스크 클래스 전체 이름
	 */
	public static String getServerTaskClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_SERVER_TASK_CLASS_FULL_NAME)
				.append(".")
				.append(getServerTaskClassShortName(messageID)).toString();
		return classFullName;
	}
	
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 클라이언트 타스크 클래스 짧은 이름
	 */
	public static String getClientTaskClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ClientTask").toString();
		return classFullName;
	}
	
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 클라이언트 타스크 클래스 전체 이름
	 */
	public static String getClientTaskClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_CLIENT_TASK_CLASS_FULL_NAME)
				.append(".")
				.append(getClientTaskClassShortName(messageID)).toString();
		return classFullName;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 메시지 디코더 클래스 짧은 이름
	 */
	public static String getMessageDecoderClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("Decoder").toString();
		return classFullName;
	}
	
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 메시지 디코더 클래스 전체 이름
	 */
	public static String getMessageDecoderClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getMessageDecoderClassShortName(messageID)).toString();
		return classFullName;
	}
	
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 메시지 인코더 클래스 짧은 이름
	 */
	public static String getMessageEncoderClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("Encoder").toString();
		return classFullName;
	}
	
	/**
	 * @param messageID 메시지 식별자
	 * @return 메시지 인코더 클래스 전체 이름
	 */	
	public static String getMessageEncoderClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getMessageEncoderClassShortName(messageID)).toString();
		return classFullName;
	}
}
