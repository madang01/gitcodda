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

import java.util.HashSet;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

/**
 * 내장하고 있는 시스템 클래스 판단자 구현체
 * WARNING! 이 클래스는 쓰레드에 안전하지 않음.
 * 
 * @author Won Jonghoon
 *
 */
public class SystemClassDeterminer implements SystemClassVerifierIF {

	private HashSet<String> sytemClassFullNameSetHavingDynamicClassName = new HashSet<String>();
	
	/**
	 * 생성자
	 */
	public SystemClassDeterminer() {
		
		String[] messageIDListOfDTOHavingDynamicClassName = { "SelfExnRes", "Empty" };

		for (String messageIDOfDTOHavingDynamicClassName : messageIDListOfDTOHavingDynamicClassName) {
			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getMessageClassFullName(messageIDOfDTOHavingDynamicClassName));
			sytemClassFullNameSetHavingDynamicClassName.add(
					IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageIDOfDTOHavingDynamicClassName));
			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageIDOfDTOHavingDynamicClassName));
			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(messageIDOfDTOHavingDynamicClassName));
			sytemClassFullNameSetHavingDynamicClassName.add(
					IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageIDOfDTOHavingDynamicClassName));
		}

		String[] messageIDListOfTaskHavingDynamicClassName = { "Empty" };

		for (String messageIDOfTaskHavingDynamicClassName : messageIDListOfTaskHavingDynamicClassName) {
			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getClientTaskClassFullName(messageIDOfTaskHavingDynamicClassName));

			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageIDOfTaskHavingDynamicClassName));
		}
	}

	@Override
	public boolean isSystemClassName(String classFullName) {
		boolean isSystemClassName = (0 != classFullName.indexOf(CommonStaticFinalVars.BASE_DYNAMIC_CLASS_FULL_NAME)) ||
				sytemClassFullNameSetHavingDynamicClassName.contains(classFullName);		
		return isSystemClassName;
	}
}
