package kr.pe.codda.client.connection.asyn;

import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class AyncThreadSafeSingleConnectedConnectionAdder implements AsynConnectedConnectionAdderIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private final Object monitor = new Object();
	private AsynConnectionIF connectedAsynConnection = null;
	private boolean isSocketTimeout=false;	 

	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) {
		synchronized (monitor) {
			if (isSocketTimeout) {
				log.warning("socket timeout occured so drop the connected asyn share connection");
				connectedAsynConnection.close();
				return;
			}
			this.connectedAsynConnection = connectedAsynConnection;
			monitor.notify();
		}
		
		String warnMessage = new StringBuilder()
				.append("add the connected asyn connection[")
				.append(connectedAsynConnection.hashCode())
				.append("]").toString();
		log.warning(warnMessage);
	}

	@Override
	public void subtractOneFromNumberOfUnregisteredConnections(AsynConnectionIF unregisteredAsynConnection) {
		String warnMessage = new StringBuilder()
				.append("remove the unregistered asyn connection[")
				.append(connectedAsynConnection.hashCode())
				.append("]").toString();
		
		log.warning(warnMessage);
	}	
	
	public AsynConnectionIF poll(long socketTimeout) throws InterruptedException, SocketTimeoutException {
		synchronized (monitor) {
			if (null == connectedAsynConnection) {
				monitor.wait(socketTimeout);
				
				if (null == connectedAsynConnection) {
					isSocketTimeout = true;
					throw new SocketTimeoutException();
				}
			}
			
			return connectedAsynConnection;
		}
	}

}
