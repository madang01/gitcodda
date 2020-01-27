package kr.pe.codda.server.lib;

import kr.pe.codda.common.exception.ServerTaskException;

public class RollbackServerTaskException extends ServerTaskException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1495062977382728870L;

	public RollbackServerTaskException(String errorMessage) {
		super(errorMessage);
	}
}
