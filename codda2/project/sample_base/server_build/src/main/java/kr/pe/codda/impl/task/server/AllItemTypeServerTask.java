package kr.pe.codda.impl.task.server;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class AllItemTypeServerTask extends AbstractServerTask {
	public AllItemTypeServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, 
			LoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		
		toLetterCarrier.addBypassOutputMessage(inputMessage);
	}
	
}
