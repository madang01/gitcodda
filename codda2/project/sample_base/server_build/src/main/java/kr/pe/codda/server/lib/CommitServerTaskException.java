package kr.pe.codda.server.lib;

import kr.pe.codda.common.exception.ServerTaskException;

public class CommitServerTaskException extends ServerTaskException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9191625150652044073L;

	public CommitServerTaskException(String errorMessage) {
		super(errorMessage);
	}

}
