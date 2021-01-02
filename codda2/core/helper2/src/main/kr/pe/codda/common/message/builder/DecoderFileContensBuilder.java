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
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.type.MessageItemInfoType;
import kr.pe.codda.common.type.MessageSingleItemType;
import kr.pe.codda.common.util.CommonStaticUtil;


public class DecoderFileContensBuilder extends AbstractSourceFileBuildre {

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

	public void addSingleItemInfoPart(StringBuilder contentsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			MessageSingleItemInfo singleItemInfo) {		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append(varNameOfSetOwner);
		contentsStringBuilder.append(".set");
		contentsStringBuilder.append(singleItemInfo.getFirstUpperItemName());
		contentsStringBuilder.append("((");
		contentsStringBuilder.append(singleItemInfo.getJavaLangClassCastingTypeOfItemType());
		contentsStringBuilder.append(")");
		
		// singleItemDecoder.getValue(sigleItemPath0
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("singleItemDecoder.getValue(pathStack.peek()");		
		
		// , "byteVar1" // itemName
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", \"");
		contentsStringBuilder.append(singleItemInfo.getItemName());
		contentsStringBuilder.append("\" // itemName");
		
		// itemType
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(MessageSingleItemType.class.getName());
		contentsStringBuilder.append(".");
		
		contentsStringBuilder.append(MessageSingleItemTypeManger.getInstance()
				.getSingleItemType(singleItemInfo.getItemTypeID()).name());
		contentsStringBuilder.append(" // itemType");
		// , SingleItemType.UB_PASCAL_STRING
		
		// itemSize
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(singleItemInfo.getItemSize());
		contentsStringBuilder.append(" // itemSize");
		
		// itemCharset
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");				
		/*SingleItemType itemType = singleItemInfo.getItemType();
		if (itemType.equals(SingleItemType.FIXED_LENGTH_STRING) ||
				itemType.equals(SingleItemType.UB_PASCAL_STRING) ||
				itemType.equals(SingleItemType.US_PASCAL_STRING) ||
				itemType.equals(SingleItemType.SI_PASCAL_STRING)) {*/
		String nativeItemCharset = singleItemInfo.getNativeItemCharset();
		if (null == nativeItemCharset) {
			contentsStringBuilder.append("null");
		} else {
			contentsStringBuilder.append("\"");
			contentsStringBuilder.append(nativeItemCharset);
			contentsStringBuilder.append("\"");
		}				
		contentsStringBuilder.append(" // nativeItemCharset");
		
		
		// , middleReadableObject));
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append("));");
	}
	
	public void addArraySizeVarDeclarationPart(StringBuilder contentsStringBuilder, int depth, String varNameOfSetOwner, MessageArrayInfo arrayInfo) {
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("int ");
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" = ");		
		/** 배열 크기 지정 방식에 따른 배열 크기 지정 */
		if (arrayInfo.getArrayCntType().equals("reference")) {
			contentsStringBuilder.append(varNameOfSetOwner);
			contentsStringBuilder.append(".get");
			contentsStringBuilder.append(arrayInfo.getArrayCntValue().substring(0, 1).toUpperCase());
			contentsStringBuilder.append(arrayInfo.getArrayCntValue().substring(1));
			contentsStringBuilder.append("();");
		} else {
			contentsStringBuilder.append(arrayInfo.getArrayCntValue());
			contentsStringBuilder.append(";");
		}
	}
	
	public void addArraySizeCheckPart(StringBuilder contentsStringBuilder, int depth, String varNameOfSetOwner, MessageArrayInfo arrayInfo) {	
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("if (");
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" < 0) {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);	
		contentsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" is less than zero\").toString();");

		// throw new BodyFormatException(errorMessage);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("throw new ");
		contentsStringBuilder.append(BodyFormatException.class.getName());
		contentsStringBuilder.append("(errorMessage);");
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("}");
	}
	
	public void addArrayInfoPart(StringBuilder contentsStringBuilder, int depth, String path, 
			String varvarNameOfSetOwner, String middleObjVarName,
			MessageArrayInfo arrayInfo) {
		String newPath = new StringBuilder(path).append(".").append(arrayInfo.getFirstUpperItemName()).toString();
		
		/** 배열 크기 변수 선언및 정의 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		addArraySizeVarDeclarationPart(contentsStringBuilder, depth, varvarNameOfSetOwner, arrayInfo);
		
		/** 양수인 배열 크기 검사 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addArraySizeCheckPart(contentsStringBuilder, depth, varvarNameOfSetOwner, arrayInfo);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// Object memberMiddleReadArray = singleItemDecoder.getArrayMiddleObject(sigleItemPath0, "member", memberListSize, middleReadableObject);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("Object ");
		contentsStringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		
		contentsStringBuilder.append(" = singleItemDecoder.getArrayMiddleObject(");
		// the parameter path
		contentsStringBuilder.append("pathStack.peek()");
		contentsStringBuilder.append(", ");
		// the parameter arrayName
		contentsStringBuilder.append("\"");
		contentsStringBuilder.append(arrayInfo.getItemName());		
		contentsStringBuilder.append("\"");
		contentsStringBuilder.append(", ");
		// the parameter arrayCntValue		
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));				
		contentsStringBuilder.append(", ");
		// the parameter receivedMiddleObject
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(");");
		
		/** 배열 변수 선언및 정의 */
		// List<AllDataType.Member> memberList = new ArrayList<AllDataType.Member>();
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("java.util.List<");
		contentsStringBuilder.append(newPath);
		contentsStringBuilder.append("> ");
		contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" = new java.util.ArrayList<");
		contentsStringBuilder.append(newPath);
		contentsStringBuilder.append(">();");
		
		// for (int i=0; i < memberListSize; i++) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("for (int ");
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append("=0; ");
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append(" < ");
		contentsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append("; ");
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append("++) {");
		
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(arrayInfo.getFirstUpperItemName())
				.append("\").append(\"[\").append(")
				.append(getCountVarName(depth))
				.append(").append(\"]\").toString());");
		
		// Object memberMiddleReadObj = singleItemDecoder.getMiddleObjectFromArrayMiddleObject(sigleItemPath1, memberMiddleReadArray, i);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("Object ");
		contentsStringBuilder.append(getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append("= singleItemDecoder.getMiddleObjectFromArrayMiddleObject(pathStack.peek()");		
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(", ");
		contentsStringBuilder.append(getCountVarName(depth));
		contentsStringBuilder.append(");");
		
		// AllDataType.Member member = new AllDataType.Member();
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(newPath);
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(" = new ");
		contentsStringBuilder.append(newPath);
		contentsStringBuilder.append("();");
		
		// memberList.add(member);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(".add(");
		contentsStringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(");");				
		
		addOrderedItemSetPart(contentsStringBuilder, depth+1
				, newPath
				, getArrayVarObjName(depth, arrayInfo.getItemName())
				, getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName())
				, arrayInfo.getOrderedItemSet());	
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("pathStack.pop();");		
						
		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("}");
		
		// allDataType.setMemberList(memberList);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append(varvarNameOfSetOwner);
		contentsStringBuilder.append(".set");
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append("List(");
		contentsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contentsStringBuilder.append(");");
	}
	
	public void addGroupInfoPart(StringBuilder contentsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			MessageGroupInfo groupInfo) {
		String newPath = new StringBuilder(path).append(".").append(groupInfo.getFirstUpperItemName()).toString();
		
		/** 그룹 변수 선언  */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append(newPath);
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contentsStringBuilder.append(" = new ");
		contentsStringBuilder.append(newPath);
		contentsStringBuilder.append("();");
		
		/** 그룹 중간 객체 변수 선언 */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("Object ");
		contentsStringBuilder.append(getGroupMiddleObjVarName(depth, groupInfo.getItemName()));		
		contentsStringBuilder.append(" = singleItemDecoder.getGroupMiddleObject(");
		// the parameter path
		contentsStringBuilder.append("pathStack.peek()");
		contentsStringBuilder.append(", ");
		// the parameter groupName
		contentsStringBuilder.append("\"");
		contentsStringBuilder.append(groupInfo.getItemName());		
		contentsStringBuilder.append("\"");
		contentsStringBuilder.append(", ");		
		// the parameter receivedMiddleObject
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(");");
		
		/** path stack push */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(groupInfo.getFirstUpperItemName())
				.append("\").toString());");
		
		addOrderedItemSetPart(contentsStringBuilder, depth
				, newPath
				, getGroupVarObjName(depth, groupInfo.getItemName())
				, getGroupMiddleObjVarName(depth, groupInfo.getItemName())
				, groupInfo.getOrderedItemSet());
		
		/** path stack pop */
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("pathStack.pop();");
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append(varNameOfSetOwner);
		contentsStringBuilder.append(".set");
		contentsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contentsStringBuilder.append("(");
		contentsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contentsStringBuilder.append(");");
	}
	
	public void addOrderedItemSetPart(StringBuilder contentsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName, OrderedMessageItemSet orderedItemSet) {
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
		for (AbstractMessageItemInfo itemInfo:itemInfoList) {
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
	
	public String buildStringOfFileContents(String author,
			MessageInfo messageInfo) {
		
		final String middleObjVarName = "receivedMiddleObject";
		final int depth = 0;

		String messageID = messageInfo.getMessageID();
		String firstLowerMessageID = messageInfo.getFirstLowerMessageID();		
		
		StringBuilder contentsStringBuilder = new StringBuilder();
		
		// contentsStringBuilder.append(buildStringOfLincensePart());
		addLincensePart(contentsStringBuilder);
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addPackageDeclarationPart(contentsStringBuilder, messageID);		
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		Class<?> importClazzes[] = {
				BodyFormatException.class,
				AbstractMessage.class,
				AbstractMessageDecoder.class,
				SingleItemDecoderIF.class
		};
		addImportDeclarationsPart(contentsStringBuilder, importClazzes);		
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addSourceFileDescriptionPart(contentsStringBuilder, messageID, author, "message decoder");
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append("public final class ");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append("Decoder extends AbstractMessageDecoder {");
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("@Override");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object ");
		contentsStringBuilder.append(middleObjVarName);
		contentsStringBuilder.append(") throws BodyFormatException {");
		
		// AllDataType allDataType = new AllDataType();
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(firstLowerMessageID);
		contentsStringBuilder.append(" = new ");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append("();");
		
		if (! messageInfo.getOrderedItemSet().getItemInfoList().isEmpty()) {
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
			/** java.util.Stack is thread-safe but LinkedList is not thread-safe */
			contentsStringBuilder.append("java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();");

			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
			contentsStringBuilder.append("pathStack.push(");
			contentsStringBuilder.append("\"");
			contentsStringBuilder.append(messageID);
			contentsStringBuilder.append("\");");
			
			addOrderedItemSetPart(contentsStringBuilder, 2, messageID, firstLowerMessageID, middleObjVarName, messageInfo.getOrderedItemSet());
			
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);			
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
			contentsStringBuilder.append("pathStack.pop();");
		}		
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// return allDataType;
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("return ");
		contentsStringBuilder.append(firstLowerMessageID);
		contentsStringBuilder.append(";");
		
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("}");
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("}");
		
		return contentsStringBuilder.toString();
	}

}
