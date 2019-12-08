package kr.pe.codda.common.sessionkey;

import static org.junit.Assert.fail;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientRSA;
import kr.pe.codda.common.sessionkey.ServerRSA;
import kr.pe.codda.common.sessionkey.ServerRSAKeypairGetter;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class RSATest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		JDKLoggerCustomFormatter formatter = new JDKLoggerCustomFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testRSAThreadSafe() {
		ServerRSA serverRSA = null;
		ClientRSA clientRSA = null;
		try {
			serverRSA = new ServerRSA(ServerRSAKeypairGetter.getRSAKeyPairFromKeyGenerator(2048));
			clientRSA = new ClientRSA(serverRSA.getDupPublicKeyBytes());
		} catch (SymmetricException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
		
		
		
		int threadID = 0;
		RSATestThread rasTestThreadList[]  = {
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA)
		};
		for (RSATestThread rasTestThread : rasTestThreadList) {
			rasTestThread.start();
		}
		
		try {
			Thread.sleep(1000L*60*2);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
		
		for (RSATestThread rasTestThread : rasTestThreadList) {
			rasTestThread.interrupt();
		}
		
		while (! isAllTerminated(rasTestThreadList)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.log(Level.WARNING, e.getMessage(), e);
				fail(e.getMessage());
			}
		}
		for (RSATestThread rasTestThread : rasTestThreadList) {
			if (rasTestThread.isError()) {
				fail(rasTestThread.getErrorMessage());
			}
		}		
	}
	
	private boolean isAllTerminated(RSATestThread rasTestThreadList[]) {
		for (RSATestThread rasTestThread : rasTestThreadList) {
			if (!rasTestThread.isTerminated()) {
				return false;
			}
		}
		return true;
	}
}
