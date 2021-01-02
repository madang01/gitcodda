package kr.pe.codda.common.message.builder;

import java.util.List;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.builder.info.AbstractMessageItemInfo;
import kr.pe.codda.common.message.builder.info.MessageArrayInfo;
import kr.pe.codda.common.message.builder.info.MessageGroupInfo;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.MessageSingleItemInfo;
import kr.pe.codda.common.message.builder.info.MessageSingleItemTypeManger;
import kr.pe.codda.common.message.builder.info.OrderedMessageItemSet;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;
import kr.pe.codda.common.type.MessageItemInfoType;
import kr.pe.codda.common.type.MessageSingleItemType;
import kr.pe.codda.common.util.CommonStaticUtil;

public class EncoderFileContensBuilder extends AbstractSourceFileBuildre {

	public String getCountVarName(int depth) {
		StringBuilder countVarNameBuilder = new StringBuilder();
		countVarNameBuilder.append("i");
		countVarNameBuilder.append(depth);
		return countVarNameBuilder.toString();
	}

	public String getArrayMiddleObjVarName(int depth, String arrayName) {
		StringBuilder contentsStringBuilder = new StringBuilder();
		contentsStringBuilder.append(arrayName);
		contentsStringBuilder.append("$");
		contentsStringBuilder.append(depth);
		contentsStringBuilder.append("ArrayMiddleObject");
		return contentsStringBuilder.toString();
	}

	public String getElementObjVarNameOfArrayMiddleObject(int depth, String arrayName) {
		StringBuilder contentsStringBuilder = new StringBuilder();
		contentsStringBuilder.append(arrayName);
		contentsStringBuilder.append("$");
		contentsStringBuilder.append(depth);
		contentsStringBuilder.append("MiddleWritableObject");
		return contentsStringBuilder.toString();
	}	

	public String getArrayVarObjName(int depth, String arrayName) {
		StringBuilder contentsStringBuilder = new StringBuilder(arrayName);
		contentsStringBuilder.append("$");
		contentsStringBuilder.append(depth);
		return contentsStringBuilder.toString();
	}

	public String getArrayListVarObjName(int depth, String arrayName) {
		StringBuilder contentsStringBuilder = new StringBuilder(getArrayVarObjName(depth, arrayName));		
		contentsStringBuilder.append("List");
		return contentsStringBuilder.toString();
	}

	public String getArrayListSizeVarObjName(int depth, String arrayName) {
		StringBuilder contentsStringBuilder = new StringBuilder(getArrayListVarObjName(depth, arrayName));
		contentsStringBuilder.append("Size");
		return contentsStringBuilder.toString();
	}

	public String getGroupMiddleObjVarName(int depth, String groupName) {
		StringBuilder contentsStringBuilder = new StringBuilder();
		contentsStringBuilder.append(groupName);
		contentsStringBuilder.append("$");
		contentsStringBuilder.append(depth);
		contentsStringBuilder.append("WritableMiddleObject");
		return contentsStringBuilder.toString();
	}
	
	public String getGroupVarObjName(int depth, String groupName) {
		StringBuilder contentsStringBuilder = new StringBuilder(groupName);
		contentsStringBuilder.append("$");
		contentsStringBuilder.append(depth);
		return contentsStringBuilder.toString();
	}

	public String getReferenceVariableGetMethodString(String varNameOfSetOwner, String referenceCountVarName) {
		if (referenceCountVarName.length() < 2) {
			String errorMessage = String.format("the character number of the parameter referenceCountVarName[%s] is less than two", referenceCountVarName);
			throw new IllegalArgumentException(errorMessage);
		}
		
		StringBuilder contentsStringBuilder = new StringBuilder();
		contentsStringBuilder.append(varNameOfSetOwner);
		contentsStringBuilder.append(".get");
		contentsStringBuilder.append(referenceCountVarName.substring(0, 1).toUpperCase());
		contentsStringBuilder.append(referenceCountVarName.substring(1));
		contentsStringBuilder.append("()");
		return contentsStringBuilder.toString();
	}

	public void addSingleItemInfoPart(StringBuilder contentsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			MessageSingleItemInfo singleItemInfo) {;
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("singleItemEncoder.putValue(");
		// the Parameter path
		contentsStringBuilder.append("pathStack.peek()");
		contentsStringBuilder.append(", \"");
		// the parameter itemName
		contentsStringBuilder.append(singleItemInfo.getItemName());
		contentsStringBuilder.append("\"");
		// the parameter singleItemType		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(MessageSingleItemType.class.getName());
		contentsStringBuilder.append(".");
		contentsStringBuilder
				.append(MessageSingleItemTypeManger.getInstance().getSingleItemType(singleItemInfo.getItemTypeID()).name());
		contentsStringBuilder.append(" // itemType");

		// the parameter itemValue
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(varNameOfSetOwner);
		contentsStringBuilder.append(".get");
		contentsStringBuilder.append(singleItemInfo.getFirstUpperItemName());
		contentsStringBuilder.append("() // itemValue");

		// the parameter itemSize
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(singleItemInfo.getItemSize());
		contentsStringBuilder.append(" // itemSize");

		// the parameter nativeItemCharset
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");
		String nativeItemCharset = singleItemInfo.getNativeItemCharset();
		if (null == nativeItemCharset) {
			contentsStringBuilder.append("null");
		} else {
			contentsStringBuilder.append("\"");
			contentsStringBuilder.append(nativeItemCharset);
			contentsStringBuilder.append("\"");
		}
		contentsStringBuilder.append(" // nativeItemCharset");
		
		// the parameter middleObjectToSend
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(");");
	}
	
	public void addNullCaseArrayValidationPart(StringBuilder contentsStringBuilder, int depth, String varNameOfSetOwner, MessageArrayInfo arrayInfo) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */");

		if (arrayInfo.getArrayCntType().equals("reference")) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			/**
			 * <pre>
			 * if (0 != <변수명>.get<첫문자자가 대문자인 참조변수명>()) {
			 * </pre>
			 */

			// if (0 != allDataTypeInObj.getCnt()) {
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
			contentsStringBuilder.append("if (0 != ");			
			contentsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contentsStringBuilder.append(") {");			
			
			
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);			
			contentsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			contentsStringBuilder.append(" is null but the value referenced by the array size[");
			contentsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contentsStringBuilder.append("][\").append(");
			contentsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contentsStringBuilder.append(").append(\"] is not zero\").toString();");

			// throw new BodyFormatException(errorMessage);
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
			contentsStringBuilder.append("throw new ");
			contentsStringBuilder.append(BodyFormatException.class.getName());
			contentsStringBuilder.append("(errorMessage);");

			// }
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
			contentsStringBuilder.append("}");

		} else {
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
			contentsStringBuilder.append("if (0 != ");
			contentsStringBuilder.append(arrayInfo.getArrayCntValue());
			contentsStringBuilder.append(") {");
			
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
			// String errorMessage = new StringBuilder("the var member$1List is null but the value defined by array size[3] is not zero").toString();
			/*contentsStringBuilder.append("String errorMessage = new StringBuilder(\"the var \").append(\"");
			contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			contentsStringBuilder.append("\").append(\"is null but the value defined by array size\").append(\"[");
			contentsStringBuilder.append(arrayInfo.getArrayCntValue());
			contentsStringBuilder.append("] is not zero\").toString();");*/
			
			contentsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			contentsStringBuilder.append(" is null but the value defined by array size[");
			contentsStringBuilder.append(arrayInfo.getArrayCntValue());
			contentsStringBuilder.append("] is not zero\").toString();");
			

			// throw new BodyFormatException(errorMessage);
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
			contentsStringBuilder.append("throw new ");
			contentsStringBuilder.append(BodyFormatException.class.getName());
			contentsStringBuilder.append("(errorMessage);");

			// }
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
			contentsStringBuilder.append("}");
		}
	}

	/** 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
	public void addPartCheckingListSizeIsValid(StringBuilder contentsStringBuilder, int depth, String varNameOfSetOwner, MessageArrayInfo arrayInfo) {

		// if (memberListSize != allDataTypeInObj.getCnt()) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("if (");		
		if (arrayInfo.getArrayCntType().equals("reference")) {
			contentsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));			
		} else {
			contentsStringBuilder.append(arrayInfo.getArrayCntValue());
		}		
		contentsStringBuilder.append(" != ");
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getArrayName()));
		contentsStringBuilder.append(") {");

		// String errorMessage = new StringBuilder(allDataTypeInObjSingleItemPath)
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		
		if (arrayInfo.getArrayCntType().equals("reference")) {			
			contentsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			contentsStringBuilder.append("[\").append(");
			contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			contentsStringBuilder.append(").append(\"] is not same to the value referenced by the array size[");
			contentsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contentsStringBuilder.append("][\").append(");
			contentsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contentsStringBuilder.append(").append(\"]\").toString();");
			
		} else {
			// String errorMessage = new StringBuilder("the var member$1ListSize[").append(member$1ListSize).append("] is not same to the value defined by array size[3]").toString();
			contentsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			contentsStringBuilder.append("[\").append(");
			contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			contentsStringBuilder.append(").append(\"] is not same to the value defined by array size[");
			contentsStringBuilder.append(arrayInfo.getArrayCntValue());
			contentsStringBuilder.append("]\").toString();");
			
		}

		// throw new BodyFormatException(errorMessage);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("throw new ");
		contentsStringBuilder.append(BodyFormatException.class.getName());
		contentsStringBuilder.append("(errorMessage);");

		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("}");
	}

	public void addNotNullCaseArrayValidationPart(StringBuilder contentsStringBuilder, int depth, String path, String nameOfSetOwner,
			String middleObjVarName, MessageArrayInfo arrayInfo) {
		String newPath = new StringBuilder(path).append(".").append(arrayInfo.getFirstUpperItemName()).toString();

		/** 배열 변수 선언및 정의 */
		// int memberListSize = memberList.size();
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("int ");
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" = ");
		contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(".size();");

		/** 배열 값이 null 이 아닐때에는 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */");

		/** 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
		addPartCheckingListSizeIsValid(contentsStringBuilder, depth, nameOfSetOwner, arrayInfo);

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		/** 이 배열을 위한 중간 객체 가져오기 */
		// Object memberArrayMiddleObject =
		// singleItemEncoder.getArrayMiddleObject(allDataTypeInObjSingleItemPath,
		// "member", memberList.length, middleWritableObject);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("Object ");
		/*
		 * contentsStringBuilder.append(arrayInfo.getItemName());
		 * contentsStringBuilder.append("MiddleWriteArray");
		 */
		contentsStringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" = singleItemEncoder.getArrayMiddleObject(");		
		// the parameter path
		contentsStringBuilder.append("pathStack.peek()");
		contentsStringBuilder.append(", ");
		// the parameter arrayName
		contentsStringBuilder.append("\"");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("\", ");
		// the parameter arrayCntValue
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(", ");
		// the parameter middleObjectToSend
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(");");

		// for (int i=0; i < memberListSize; i++) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("for (int ");
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append("=0; ");
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append(" < ");
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append("; ");
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append("++) {");

		
		// pathStack.push(newStringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i).append("]").toString());
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(arrayInfo.getFirstUpperItemName())
				.append("\").append(\"[\").append(")
				.append(getCountVarName(depth))
				.append(").append(\"]\").toString());");

		// Object memberMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(memberSingleItemPath, memberMiddleWriteArray, i);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("Object ");
		contentsStringBuilder.append(getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" = singleItemEncoder.getMiddleObjectFromArrayMiddleObject(");	
		// the parameter path
		contentsStringBuilder.append("pathStack.peek(), ");
		// the parameter arrayObj
		contentsStringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(", ");
		// the parameter inx
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append(");");

		// AllDataType.Member member = memberList.get(i);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		/*
		 * contentsStringBuilder.append(path); contentsStringBuilder.append(".");
		 * contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		 */
		contentsStringBuilder.append(newPath);
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" = ");
		contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(".get(");
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append(");");

		addOrderedItemSetPart(contentsStringBuilder, depth + 2
				, newPath
				, getArrayVarObjName(depth, arrayInfo.getItemName())
				, getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName())
				, arrayInfo.getOrderedItemSet());

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// pathStack.pop();
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("pathStack.pop();");

		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("}");
	}

	public void addArrayInfoPart(StringBuilder contentsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			MessageArrayInfo arrayInfo) {		
		/** 배열 변수 선언및 정의 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("java.util.List<");
		contentsStringBuilder.append(path);
		contentsStringBuilder.append(".");
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append("> ");
		contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" = ");
		contentsStringBuilder.append(varNameOfSetOwner);
		contentsStringBuilder.append(".get");
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append("List();");

		/** 배열 정보와 배열 크기 일치 검사 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		/** 주석 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("/** 배열 정보와 배열 크기 일치 검사 */");

		/** if (null == memberList) { */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("if (null == ");
		contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(") {");

		addNullCaseArrayValidationPart(contentsStringBuilder, depth, varNameOfSetOwner, arrayInfo);

		// } else {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("} else {");

		// buildStringOfPartWhoseListIsNotNullAtArray
		addNotNullCaseArrayValidationPart(contentsStringBuilder, depth, path, varNameOfSetOwner,
				middleObjVarName, arrayInfo);				

		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("}");
	}
	
	public void addGroupInfoPart(StringBuilder contentsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			MessageGroupInfo groupInfo) {
		String newPath = new StringBuilder(path).append(".").append(groupInfo.getFirstUpperItemName()).toString();	

		/** 변수 선언 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);		
		contentsStringBuilder.append(newPath);
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contentsStringBuilder.append(" = ");
		contentsStringBuilder.append(varNameOfSetOwner);
		contentsStringBuilder.append(".get");
		contentsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contentsStringBuilder.append("();");
		
		/** if (null == group1$2) { */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("if (null == ");
		contentsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contentsStringBuilder.append(") {");		
		/** 	String errorMessage = "the var group1$1 is null"; */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("String errorMessage = \"the var ");
		contentsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contentsStringBuilder.append(" is null\";");		
		/** 	throw new BodyFormatException(errorMessage); */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);		 
		contentsStringBuilder.append("throw new ");
		contentsStringBuilder.append(BodyFormatException.class.getName());
		contentsStringBuilder.append("(errorMessage);");
		/** } */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("}");		
		
		/** group 쓰기 가능한 중간 객체 얻기 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("Object ");
		contentsStringBuilder.append(getGroupMiddleObjVarName(depth, groupInfo.getItemName()));
		contentsStringBuilder.append(" = singleItemEncoder.getGroupMiddleObject(");
		// the parameter path
		contentsStringBuilder.append("pathStack.peek(), ");
		// the parameter groupName
		contentsStringBuilder.append("\"");
		contentsStringBuilder.append(groupInfo.getItemName());
		contentsStringBuilder.append("\", ");
		// the parameter middleObjectToSend
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(");");
		
		/** path stack push */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"");
		contentsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contentsStringBuilder.append("\").toString());");
		
		addOrderedItemSetPart(contentsStringBuilder, depth, newPath, getGroupVarObjName(depth, groupInfo.getItemName()),
				getGroupMiddleObjVarName(depth, groupInfo.getItemName()), groupInfo.getOrderedItemSet());
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		/** pathStack.pop(); */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("pathStack.pop();");
	}


	public void addOrderedItemSetPart(StringBuilder contentsStringBuilder, int depth, String path, String varNameOfSetOwner,
			String middleObjVarName, OrderedMessageItemSet orderedItemSet) {
		if (depth < 0) {
			String errorMessage = String.format("the parameter depth[%d] is less than zero", depth);
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == path) {
			String errorMessage = "the parameter path is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == varNameOfSetOwner) {
			String errorMessage = "the parameter varNameOfSetOwner is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == middleObjVarName) {
			String errorMessage = "the parameter middleObjVarName is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == orderedItemSet) {
			String errorMessage = "the parameter orderedItemSet is null";
			throw new IllegalArgumentException(errorMessage);
		}

		List<AbstractMessageItemInfo> itemInfoList = orderedItemSet.getItemInfoList();
		for (AbstractMessageItemInfo itemInfo : itemInfoList) {
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);

			MessageItemInfoType itemInfoType = itemInfo.getMessageItemInfoType();
			switch (itemInfoType) {
			case SINGLE: {
				MessageSingleItemInfo singleItemInfo = (MessageSingleItemInfo) itemInfo;
				addSingleItemInfoPart(contentsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, singleItemInfo);
				break;
			}
			case ARRAY: {
				MessageArrayInfo arrayInfo = (MessageArrayInfo) itemInfo;
				addArrayInfoPart(contentsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, arrayInfo);
				break;
			}
			case GROUP: {
				MessageGroupInfo groupInfo = (MessageGroupInfo) itemInfo;
				addGroupInfoPart(contentsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, groupInfo);
				break;
			}
			default: {
				log.severe("unknwon item type[" + itemInfoType.toString() + "]");
				System.exit(1);
			}
			}
		}
	}

	
	public void addEncodeMethodPart(StringBuilder contentsStringBuilder, String messageID, String firstLowerMessageID, String middleObjVarName) {
		final int depth = 1;
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object ");
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(") throws Exception {");

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(firstLowerMessageID);
		contentsStringBuilder.append(" = (");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append(")messageObj;");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("encodeBody(");
		contentsStringBuilder.append(firstLowerMessageID);
		contentsStringBuilder.append(", singleItemEncoder, ");
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(");");
		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("}");
	}

	public void addEncodeBodyMethodPart(StringBuilder contentsStringBuilder, String messageID, String firstLowerMessageID,
			String middleObjVarName, MessageInfo messageInfo) {
		int depth = 1;
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("private void encodeBody(");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(firstLowerMessageID);
		contentsStringBuilder.append(", SingleItemEncoderIF singleItemEncoder, Object ");
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(") throws Exception {");

		if (! messageInfo.getOrderedItemSet().getItemInfoList().isEmpty()) {
			// Stack<String> pathStack = new Stack<String>();
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
			/** java.util.Stack is thread-safe but LinkedList is not thread-safe */
			contentsStringBuilder.append("java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();");

			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
			contentsStringBuilder.append("pathStack.push(");
			contentsStringBuilder.append("\"");
			contentsStringBuilder.append(messageID);
			contentsStringBuilder.append("\");");
			
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			addOrderedItemSetPart(contentsStringBuilder, depth+1, messageID, firstLowerMessageID, middleObjVarName,
					messageInfo.getOrderedItemSet());
			
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
			contentsStringBuilder.append("pathStack.pop();");
		}		
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("}");
	}

	public String buildStringOfFileContents(String author,
			MessageInfo messageInfo) {

		final String middleObjVarName = "middleObjectToSend";
		final int depth = 0;

		String messageID = messageInfo.getMessageID();
		String firstLowerMessageID = messageInfo.getFirstLowerMessageID();

		StringBuilder contentsStringBuilder = new StringBuilder();

		addLincensePart(contentsStringBuilder);
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		addPackageDeclarationPart(contentsStringBuilder, messageID);
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		Class<?> importClazzes[] = {
				AbstractMessage.class,
				AbstractMessageEncoder.class,
				SingleItemEncoderIF.class 
		};
		addImportDeclarationsPart(contentsStringBuilder, importClazzes);

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addSourceFileDescriptionPart(contentsStringBuilder, messageID, author, "message encoder");

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append("public final class ");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append("Encoder extends AbstractMessageEncoder {");

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("@Override");
		// encode(AbstractMessage, SingleItemEncoderIF, Object) 메소드 파트 문자열
		addEncodeMethodPart(contentsStringBuilder, messageID, firstLowerMessageID, middleObjVarName);

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addEncodeBodyMethodPart(contentsStringBuilder, messageID, firstLowerMessageID, middleObjVarName, messageInfo);

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append("}");
		return contentsStringBuilder.toString();
	}
}
