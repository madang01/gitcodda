package kr.pe.codda.impl.task.server;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.server.LoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BinaryPublicKeyServerTask extends AbstractServerTask {
	// private Logger log = LoggerFactory.getLogger(AccountSearchProcessReqServerTask.class);

	public BinaryPublicKeyServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, LoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		doWork(projectName, toLetterCarrier, (BinaryPublicKey)inputMessage);
		
	}
	private void doWork(String projectName,
			ToLetterCarrier toLetterCarrier, BinaryPublicKey binaryPublicKey)
			throws Exception {
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		ServerSessionkeyIF serverSessionkey  = serverSessionkeyManager.getMainProjectServerSessionkey();

		binaryPublicKey.setPublicKeyBytes(serverSessionkey.getDupPublicKeyBytes());
		toLetterCarrier.addSyncOutputMessage(binaryPublicKey);
	}
}
