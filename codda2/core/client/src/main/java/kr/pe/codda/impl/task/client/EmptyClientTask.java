package kr.pe.codda.impl.task.client;

import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;


public class EmptyClientTask extends AbstractClientTask {
	public static int count = 0;

	public EmptyClientTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, AsynConnectionIF asynConnection, AbstractMessage outputMessage) throws Exception {
		/*
		String infoMessage = new StringBuilder()
				.append("socket channel[")
				.append(asynConnection.hashCode())
				.append("], output message=")
				.append(outputMessage.toString()).toString();
		
		log.finest(infoMessage);
		*/
		count++;
	}
}
