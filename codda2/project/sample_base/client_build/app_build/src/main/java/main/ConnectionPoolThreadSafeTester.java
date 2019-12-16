package main;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.Echo.Echo;

public class ConnectionPoolThreadSafeTester implements Runnable {
	private Logger log = LoggerFactory.getLogger("kr.pe.codda");
	
	private AnyProjectConnectionPoolIF mainProjectConnectionPool = null;

	
	public ConnectionPoolThreadSafeTester(AnyProjectConnectionPoolIF mainProjectConnectionPool) {
		this.mainProjectConnectionPool = mainProjectConnectionPool;
	}			

	@Override
	public void run() {
		log.info("start {}", Thread.currentThread().getName());				
		
		java.util.Random random = new java.util.Random();
		
		while (! Thread.currentThread().isInterrupted()) {
			Echo echoReq = new Echo();
			echoReq.setRandomInt(random.nextInt());
			echoReq.setStartTime(System.nanoTime());
			
			AbstractMessage messageFromServer = null;
			try {
				messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), echoReq);

				if (! (messageFromServer instanceof Echo)) {
					log.error("응답 메시지가 Echo 가 아님, 응답메시지={}", messageFromServer.toString());
					System.exit(1);
				}
				
				Echo echoRes = (Echo) messageFromServer;
				if ((echoReq.getRandomInt() == echoRes.getRandomInt())
						&& (echoReq.getStartTime() == echoRes.getStartTime())) {
					log.info("성공::경과 시간={} microseconds",
							TimeUnit.MICROSECONDS.convert((System.nanoTime() - echoRes.getStartTime()), TimeUnit.NANOSECONDS));
				} else {
					log.info("실패::echo 메시지 입력/출력 다름");
				}
				
				// Thread.sleep(200L);
			} catch (Exception e) {
				log.warn("소켓 통신중 에러 발생", e);
			}
		}
		
	}
}
