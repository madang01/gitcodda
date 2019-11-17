package kr.pe.codda.client.connection.sync;

import java.util.logging.Logger;

import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageForwarderIF;

public class SyncOutputMessageReceiver implements ReceivedMessageForwarderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	// private ReadableMiddleObjectWrapper readableMiddleObjectWrapper = null;
	private MessageProtocolIF messageProtocol = null;
	private MessageCodecMangerIF messageCodecManger = null;
	private AbstractMessage receivedMessage = null;
	private boolean isError = false;

	public SyncOutputMessageReceiver(MessageProtocolIF messageProtocol) {
		this.messageProtocol = messageProtocol;
	}

	@Override
	public void putReceivedMessage(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {
		if (null != receivedMessage) {
			/** discard message */
			isError = true;

			AbstractMessage discardedMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger,
					messageProtocol, mailboxID, mailID, messageID, readableMiddleObject);

			String warnMessage = new StringBuilder().append("discard the received message[")
					.append(discardedMessage.toString())
					.append("] becase there are one more recevied messages").toString();
			
			log.warning(warnMessage);

			return;
		}

		receivedMessage = ClientMessageUtility.buildOutputMessage("recevied", messageCodecManger, messageProtocol, mailboxID,
				mailID, messageID, readableMiddleObject);
	}

	public void ready(MessageCodecMangerIF messageCodecManger) {
		this.messageCodecManger = messageCodecManger;
		receivedMessage = null;
		isError = false;
	}

	public AbstractMessage getReceiveMessage() {
		return receivedMessage;
	}

	public boolean isReceivedMessage() {
		return (null != receivedMessage);
	}

	public boolean isError() {
		return isError;
	}
}
