package kr.pe.codda.server.lib;

import kr.pe.codda.common.exception.ServerTaskException;

/**
 * <pre>
 * DB 작업전 파라미터 검사 수행하여 값에 오류가 있을때 던지는 예외.
 * 
 * WARNING! {@link ServerDBUtil#execute(String, DBAutoCommitTaskIF, Object)} 에서 
 * {@link ParameterServerTaskException} 예외는 rollback 하는데 비용이 들어 rollback 이 생략된 예외이기때문에
 * 만약 DB 작업 후 호출된다면 문제가 발생할 수 있어 주의가 필요합니다.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class ParameterServerTaskException extends ServerTaskException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -781873167308941145L;

	/**
	 * 생성자
	 * @param errorMessage 에러 메시지
	 */
	public ParameterServerTaskException(String errorMessage) {
		super(errorMessage);
	}

}