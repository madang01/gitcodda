package kr.pe.codda.client.connection;

import java.io.IOException;

import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public interface ConnectionPoolIF {
	public ConnectionIF getConnection() throws InterruptedException, ConnectionPoolTimeoutException, ConnectionPoolException;	
	public void release(ConnectionIF conn) throws ConnectionPoolException;
	
	
	public void fillAllConnection() throws NoMoreDataPacketBufferException, IOException, InterruptedException;
		
	public String getPoolState();
}
