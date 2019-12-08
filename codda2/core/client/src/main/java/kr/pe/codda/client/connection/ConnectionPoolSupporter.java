package kr.pe.codda.client.connection;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

public final class ConnectionPoolSupporter extends Thread implements ConnectionPoolSupporterIF {

protected Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	private long wakeupInterval=0;
	
	
	private ConnectionPoolIF connectionPool = null;
	private SynchronousQueue<String> wakeupEventQueue = new SynchronousQueue<String>();

	public ConnectionPoolSupporter(long wakeupInterval) {
		if (wakeupInterval <= 0) {
			String errorMessage = new StringBuilder()
					.append("the parameter wakeupInterval[")
					.append(wakeupInterval)
					.append("] is less than or equal to zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		this.wakeupInterval = wakeupInterval;
	}

	public void run() {
		log.info("연결 폴 후원자 시작");
		
		String reasonForWakingUp = null;
		try {
			while (! Thread.currentThread().isInterrupted()) {
				reasonForWakingUp = wakeupEventQueue.poll(wakeupInterval, TimeUnit.MILLISECONDS);
				if (null != reasonForWakingUp) {
					String infoMessage = new StringBuilder()
							.append("연결 폴 후원자 작업을 일찍 실행하는 사유[")
							.append(reasonForWakingUp)
							.append("] 발생").toString();
					
					log.info(infoMessage);
				}
				
				//log.debug("start the work adding the all missing connection");
				log.info("reasonForWakingUp=" + reasonForWakingUp);
				
				try {
					connectionPool.fillAllConnection();
				} catch(InterruptedException e) {
					throw e;
				} catch(Exception e) {
					log.log(Level.WARNING, "연결 폴 후원자에서 통제된 에러 발생하여 루프 계속", e);
					continue;
				}
				
				log.fine("end the work adding the all missing connection");
			}
			log.warning("연결 폴 후원자::루프 종료");
		} catch(InterruptedException e) {
			log.warning("연결 폴 후원자::인터럽트에 의한 종료");
		} catch(Exception e) {
			log.log(Level.WARNING, "연결 폴 후원자::에러에 의한 종료", e);
		}
	}
	
	public void registerPool(ConnectionPoolIF connectionPool) {
		if (null == connectionPool) {
			throw new IllegalArgumentException("the parameter connectionPool is null");
		}
		this.connectionPool = connectionPool;
	}
	
	public void notice(String reasonForWakingUp) {
		wakeupEventQueue.offer(reasonForWakingUp);
	}
}
