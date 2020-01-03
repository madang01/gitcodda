package kr.pe.codda.impl.task.server;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.AllItemType.AllItemType;
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
		doWork(projectName, toLetterCarrier, (AllItemType)inputMessage);
	}
	
	private void doWork(String projectName,
			ToLetterCarrier toLetterCarrier, AllItemType allDataTypeInObj)
			throws Exception {		
		toLetterCarrier.addBypassOutputMessage(allDataTypeInObj);
	}
}
