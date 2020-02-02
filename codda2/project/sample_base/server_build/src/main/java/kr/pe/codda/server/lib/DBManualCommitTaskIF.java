package kr.pe.codda.server.lib;

import org.jooq.DSLContext;

/**
 * <pre>
 * 개발자가 직접 commit 을 호출하는 비지니스 로직 인터페이스
 * 
 * 역자주) 서버 비지니스 로직은 조립이 가능한 콤포넌트 성질을 가져야 하기에 
 * 트랜재션을 비지니스 로직에서 갖지 않아야 트랜재션을 서로 묶을 수 있다.
 * 하지만 이 인터페이스는 이를 포기한 비지니스 로직 인터페이스이다.
 * </pre> 
 * 
 * @author Won Jonghoon
 *
 * @param <I> 입력 값을 담은 객체
 * @param <O> 출력 값을 담은 객체
 */
public interface DBManualCommitTaskIF<I, O> {
	/**
	 * 트랜재션 관리자(=transactionManager) 와 입력 I 를 받아 직접 트랜재션을 제어하는 비지니스 로직을 수행하여 결과물을 담은 O 객체를 반환한다. 
	 * 
	 * @param transactionManager 트랜재션 관리자
	 * @param dsl jooq DSLContext 객체로 쿼리문 수행에 필요하다.
	 * @param req 입력 값을 담은 객체
	 * @return 출력 값을 담은 객체
	 * @throws Exception 에러 발생시 던지는 예외
	 */
	public O doWork(final TransactionManager transactionManager, final DSLContext dsl, final I req) throws Exception;
}
