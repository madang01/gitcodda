package kr.pe.codda.common.classloader;

import java.util.HashSet;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class SystemClassDeterminer implements SystemClassDeterminerIF {

	private HashSet<String> sytemClassFullNameSetHavingDynamicClassName = new HashSet<String>();
	
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
