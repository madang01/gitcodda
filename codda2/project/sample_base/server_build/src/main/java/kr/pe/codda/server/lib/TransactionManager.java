package kr.pe.codda.server.lib;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 관리자
 * 
 * @author Won Jonghoon
 *
 */
public class TransactionManager {
	private final Connection conn;
	
	private int countOfCommit = 0;
	private int countOfRollback = 0;
	
	
	public TransactionManager(Connection conn) {
		if (null == conn) {
			throw new IllegalArgumentException("the parameter conn is null");
		}
		this.conn = conn;
	}
	
	public void commit() throws SQLException {
		conn.commit();
		
		countOfCommit++;
	}
	
	public void rollback() throws SQLException {
		conn.rollback();
		
		countOfRollback++;
	}

	public int getCountOfCommit() {
		return countOfCommit;
	}

	public int getCountOfRollback() {
		return countOfRollback;
	}
	
	
}
