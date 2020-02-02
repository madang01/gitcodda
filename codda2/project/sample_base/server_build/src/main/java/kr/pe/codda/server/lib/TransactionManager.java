package kr.pe.codda.server.lib;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 관리자, 오직 commit 만 할 수 있다.
 * 
 * @author Won Jonghoon
 *
 */
public class TransactionManager {
	private final Connection conn;
	
	private int countOfCommit = 0;
	
	/**
	 * 생성자
	 * @param conn 연결
	 */
	public TransactionManager(Connection conn) {
		if (null == conn) {
			throw new IllegalArgumentException("the parameter conn is null");
		}
		this.conn = conn;
	}
	
	/**
	 * commit 수행, commit 회수가 증가한다. WARNING! commit 횟수가 Intger.MAX 제한이 없기때문에 Integer.MAX 넘는다면 문제 발생함.
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		conn.commit();
		
		countOfCommit++;
	}
	
	/**
	 * @return commit 한 횟수
	 */
	public int getCountOfCommit() {
		return countOfCommit;
	}

	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionManager [conn=");
		builder.append(conn.hashCode());
		builder.append(", countOfCommit=");
		builder.append(countOfCommit);	
		builder.append("]");
		return builder.toString();
	}
}
