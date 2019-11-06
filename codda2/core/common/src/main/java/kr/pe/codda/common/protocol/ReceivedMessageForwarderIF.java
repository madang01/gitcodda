package kr.pe.codda.common.protocol;

/**
 * 수신한 메시지 전달자 인터페이스.
 * 
 * @author Won Jonghoon
 *
 */
public interface ReceivedMessageForwarderIF {
	public void putReceivedMessage(int mailboxID, int mailID, String messageID, Object readableMiddleObject) throws InterruptedException;
}
