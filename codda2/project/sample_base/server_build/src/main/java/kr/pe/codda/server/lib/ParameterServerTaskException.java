package kr.pe.codda.server.lib;

import kr.pe.codda.common.exception.ServerTaskException;

public class ParameterServerTaskException extends ServerTaskException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -781873167308941145L;

	public ParameterServerTaskException(String errorMessage) {
		super(errorMessage);
	}

}