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

public class ClientRSATest {

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
	public void testConstructor_theParameterPublicKeyBytesIsNull() {
		try {
			new ClientRSA(null);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter publicKeyBytes is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEncrypt_theParameterPlainTextBytesIsNull() {
		try {
			ServerRSA serverRSA = new ServerRSA(ServerRSAKeypairGetter.getRSAKeyPairFromKeyGenerator(1024));
			
			ClientRSA clientRSA = new ClientRSA(serverRSA.getDupPublicKeyBytes());
			
			clientRSA.encrypt(null);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage);
			
			String expectedErrorMessage = "the parameter plainTextBytes is null";
			
			assertEquals(expectedErrorMessage, errorMessage);
			
		} catch(Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			fail(e.getMessage());
		}
	}

}
