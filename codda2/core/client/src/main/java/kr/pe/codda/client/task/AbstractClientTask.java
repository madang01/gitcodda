package kr.pe.codda.client.task;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.util.CommonStaticUtil;

public abstract class AbstractClientTask {
	protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private ClassLoader taskClassLoader = this.getClass().getClassLoader();
	private final AbstractMessageDecoder outputMessageDecoder;
	
	public AbstractClientTask() throws DynamicClassCallException {
		String className = this.getClass().getName();
		int startIndex = className.lastIndexOf(".") + 1;		
		int endIndex = className.indexOf("ClientTask");
		String messageID = className.substring(startIndex, endIndex);
		
		String classFullName = IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);
		Object retObject = CommonStaticUtil.getNewObjectFromClassloader(taskClassLoader, classFullName);		
		
		if (! (retObject instanceof MessageCodecIF)) {
			String warnMessage = new StringBuilder()
					.append("this instance of class '")
					.append(classFullName)
					.append("'(classLoader=")
					.append(taskClassLoader.hashCode())
					.append(") is not a instance of MessageCodecIF class").toString();
			log.warning(warnMessage);
		}
		
		MessageCodecIF clientOutputMessageCodec = (MessageCodecIF)retObject;
		
		outputMessageDecoder = clientOutputMessageCodec.getMessageDecoder();
	}

	public void execute(int index, String projectName, AsynConnectionIF asynConnection,
			int mailboxID, int mailID, String messageID, Object readableMiddleObject, 
			MessageProtocolIF messageProtocol)
			throws InterruptedException {

		AbstractMessage outputMessage = null;
		try {
			outputMessage = messageProtocol.O2M(outputMessageDecoder, mailboxID, mailID, messageID, readableMiddleObject);
		} catch (BodyFormatException e) {
			log.warning("fail to get a output message, errmsg=" + e.getMessage());
			return;
		} catch (Exception e) {
			String errorMessage = "fail to get a output message";
			log.log(Level.WARNING, errorMessage, e);
			return;
		}
		
		long startTime = System.nanoTime();

		try {
			doTask(projectName, asynConnection, outputMessage);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {
			
			String errorReason = new StringBuilder()
					.append("unknown error::fail to execuate the message[")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("]'s task::")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorReason, e);
			return;
		} finally {
			long endTime = System.nanoTime();
			String infoMessage = new StringBuilder().append("this client task[")
					.append(messageID)
					.append("] elapsed time[")
					.append(TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS))
					.append(" ms]").toString();
			log.info(infoMessage);
		}
		
	}

	abstract public void doTask(String projectName, AsynConnectionIF asynConnection, AbstractMessage outputMessage)
			throws Exception;
}
