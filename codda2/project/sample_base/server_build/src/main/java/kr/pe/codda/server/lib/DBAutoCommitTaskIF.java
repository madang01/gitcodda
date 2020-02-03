package kr.pe.codda.server.lib;

import org.jooq.DSLContext;

/**
 * <pre>
 * 비지니스 로직  수행이 끝난 후 commit 이 수행되는 비지니스 로직 인터페이스
 *  
 * 역자주) 서버 비지니스 로직은 조립이 가능한 콤포넌트 성질을 가져야 하기에 
 * 트랜재션을 비지니스 로직에서 갖지 않아야 트랜재션을 서로 묶을 수 있다. 
 * 이 인터페이스는 이를 만족시키기 위한 비지니 로직 인터페이스이다.
 * 
 * WARNING! 트랜재션이 없다고 해도 자체적으로 레코드락을 걸수있기때문에 데드락에 대한 주의가 필요하며 비니지스 로직들을 묶을때도 역시 데드락에 대한 주의가 필요합니다.
 * </pre>   
 * 
 * @author Won Jonghoon
 *
 * @param <I> 입력 값을 담은 객체
 * @param <O> 출력 값을 담은 객체
 */
public interface DBAutoCommitTaskIF<I, O> {
	/**
	 * <pre>
	 * 입력 값을 담은 I 객체 'req' 를 받아 트랜재션 없는 비지니스 로직을 수행하여 결과물을 담은 O 객체를 반환한다. 
	 * 
	 * WARNING! 트랜재션이 없다고 해도 자체적으로 레코드락을 걸수있기때문에 묶을때 주의가 필요합니다.
	 * 
	 * WARNING! {@link ServerDBUtil#execute(String, DBAutoCommitTaskIF, Object)} 에서 
	 * {@link ParameterServerTaskException} 예외는 rollback 하는데 비용이 들어 rollback 이 생략된 예외이기때문에
	 * 만약 DB 작업 후 호출된다면 문제가 발생할 수 있어 주의가 필요합니다.    
	 * </pre>
	 * 
	 * @param dsl jooq DSLContext 객체로 쿼리문 수행에 필요하다.
	 * @param req 입력 값을 담은 객체
	 * @return 출력 값을 담은 객체
	 * @throws Exception 에러 발생시 던지는 예외
	 */
	public O doWork(final DSLContext dsl, final I req) throws Exception;
}
