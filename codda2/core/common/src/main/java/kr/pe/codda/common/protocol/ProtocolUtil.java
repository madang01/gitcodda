package kr.pe.codda.common.protocol;

public abstract class ProtocolUtil {
	/*
	 * public static void closeReadableMiddleObject(int mailboxID, int mailID,
	 * String messageID, Object readableMiddleObject) { if (readableMiddleObject
	 * instanceof FreeSizeInputStream) { FreeSizeInputStream messageStream =
	 * (FreeSizeInputStream)readableMiddleObject; try { messageStream.close(); }
	 * catch(Exception e) { String errorMessage = new StringBuilder()
	 * .append("fail to close the message body stream[messageID=")
	 * .append(messageID) .append(", mailboxID=") .append(mailboxID)
	 * .append(", mailID=") .append(mailID) .append("] body stream").toString();
	 * InternalLogger log = InternalLoggerFactory .getInstance(ProtocolUtil.class);
	 * log.warn(errorMessage, e); } } }
	 */
}
