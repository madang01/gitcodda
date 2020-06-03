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
 * 시스템 클래스 로더 대상 판단자,  WARNING! 이 클래스는 쓰레드에 안전하지 않음.
 *  
 * @author Won Jonghoon
 *
 */
public class SystemClassDeterminer implements SystemClassVerifierIF {

	private HashSet<String> sytemClassFullNameSetHavingDynamicClassName = new HashSet<String>();
	
	
	private final String[] systemMessageIDListHavingDTO = {"ExceptionDeliveryRes", "Empty"};
	
	private final String[] systemMessageIDListHavingTask = { "Empty" };
	
	
	/**
	 * 생성자
	 */
	public SystemClassDeterminer() {	

		for (String systemMessageIDHavingDTO : systemMessageIDListHavingDTO) {
			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getMessageClassFullName(systemMessageIDHavingDTO));
			sytemClassFullNameSetHavingDynamicClassName.add(
					IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(systemMessageIDHavingDTO));
			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(systemMessageIDHavingDTO));
			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(systemMessageIDHavingDTO));
			sytemClassFullNameSetHavingDynamicClassName.add(
					IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(systemMessageIDHavingDTO));
		}		

		for (String systemMessageIDHavingTask : systemMessageIDListHavingTask) {
			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getClientTaskClassFullName(systemMessageIDHavingTask));

			sytemClassFullNameSetHavingDynamicClassName
					.add(IOPartDynamicClassNameUtil.getServerTaskClassFullName(systemMessageIDHavingTask));
		}
	}

	@Override
	public boolean isSystemClassName(String classFullName) {
		boolean isSystemClassName = (0 != classFullName.indexOf(CommonStaticFinalVars.BASE_DYNAMIC_CLASS_FULL_NAME)) ||
				sytemClassFullNameSetHavingDynamicClassName.contains(classFullName);		
		return isSystemClassName;
	}
	
}
