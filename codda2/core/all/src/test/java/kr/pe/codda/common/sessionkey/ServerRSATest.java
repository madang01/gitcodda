package kr.pe.codda.common.sessionkey;

import static org.junit.Assert.*;

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
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class ServerRSATest {
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
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor_theParameterRSAKeypairIsNull() {
		try {
			new ServerRSA(null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter rsaKeypair is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDecrypt_theParameterEncryptedBytesIsNull() {
		try {
			ServerRSA serverRSA = new ServerRSA(ServerRSAKeypairGetter.getRSAKeyPairFromKeyGenerator(1024));
			
			serverRSA.decrypt(null);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter encryptedBytes is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}

}
