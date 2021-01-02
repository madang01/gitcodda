package kr.pe.codda.common.message.builder;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.type.MessageTransferDirectionType;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientCodecFileContensBuilder extends AbstractSourceFileBuildre {

	public String buildStringOfFileContents(MessageTransferDirectionType messageTransferDirectionType, String messageID,
			String author) {
		final int depth = 0;

		StringBuilder contentsStringBuilder = new StringBuilder();
		addLincensePart(contentsStringBuilder);

		// pachage
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addPackageDeclarationPart(contentsStringBuilder, messageID);

		// import
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		Class<?> importClazzes[] = { DynamicClassCallException.class, AbstractMessageDecoder.class,
				AbstractMessageEncoder.class, MessageCodecIF.class };
		addImportDeclarationsPart(contentsStringBuilder, importClazzes);

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addSourceFileDescriptionPart(contentsStringBuilder, messageID, author, "client codec");

		// public final class EchoClientCodec implements MessageCodecIF {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		
		contentsStringBuilder.append("public final class ");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append("ClientCodec implements MessageCodecIF {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		switch (messageTransferDirectionType) {
			case FROM_ALL_TO_ALL:
			case FROM_SERVER_TO_CLINET: {
				/** 디코더가 필요한 경우 */
				// private AbstractMessageDecoder messageDecoder = new EmptyDecoder();
				contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
				contentsStringBuilder.append("private AbstractMessageDecoder messageDecoder = new ");
				contentsStringBuilder.append(messageID);
				contentsStringBuilder.append("Decoder();");
				break;
			}
			default: {
				/** 디코더가 필요 없는 경우 */
				break;
			}
		}
		
		switch (messageTransferDirectionType) {
			case FROM_ALL_TO_ALL:
			case FROM_CLIENT_TO_SERVER: {
				/** 인코더가 필요한 경우 */
				// private AbstractMessageDecoder messageDecoder = new EmptyDecoder();
				contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
				contentsStringBuilder.append("private AbstractMessageEncoder messageEncoder = new ");
				contentsStringBuilder.append(messageID);
				contentsStringBuilder.append("Encoder();");
				break;
			}
			default: {
				/** 인코더가 필요 없는 경우 */
				break;
			}
		}

		// @Override
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("@Override");

		// public MessageDecoder getMessageDecoder() throws
		// DynamicClassCallException {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder
				.append("public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {");

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);

		switch (messageTransferDirectionType) {
		case FROM_ALL_TO_ALL:
		case FROM_SERVER_TO_CLINET: {
			/** 디코더가 필요한 경우 */
			contentsStringBuilder.append("return messageDecoder;");
			break;
		}
		default: {
			/** 디코더가 필요 없는 경우 */
			contentsStringBuilder.append(
					"throw new DynamicClassCallException(\"the client don't need a message decoder because it is a message[");
			contentsStringBuilder.append(messageID);
			contentsStringBuilder.append("] that is not sent from server to client\");");
			break;
		}
		}

		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("}");

		// @Override
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("@Override");

		// public MessageEncoder getMessageEncoder() throws
		// DynamicClassCallException {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder
				.append("public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {");

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);

		switch (messageTransferDirectionType) {
		case FROM_ALL_TO_ALL:
		case FROM_CLIENT_TO_SERVER: {
			/** 인코더가 필요한 경우 */
			contentsStringBuilder.append("return messageEncoder;");
			break;
		}
		default: {
			/** 인코더가 필요 없는 경우 */
			contentsStringBuilder.append(
					"throw new DynamicClassCallException(\"the client don't need a message encoder because it is a message[");
			contentsStringBuilder.append(messageID);
			contentsStringBuilder.append("] that is not sent from client to server\");");
			break;
		}
		}

		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("}");

		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 0);
		contentsStringBuilder.append("}");

		return contentsStringBuilder.toString();
	}
}
