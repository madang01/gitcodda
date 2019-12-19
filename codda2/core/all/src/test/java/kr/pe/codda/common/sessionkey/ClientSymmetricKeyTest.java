package kr.pe.codda.common.sessionkey;

import static org.junit.Assert.*;

import java.util.Random;
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

public class ClientSymmetricKeyTest {
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
	public void testClientSymmetricKey_theParameterSymmetricKeyAlgorithmIsNull() {
		String symmetricKeyAlgorithm = null;
		byte[] symmetricKeyBytes = new byte[16];
		byte[] ivBytes = new byte[16];
		
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			new ClientSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter symmetricKeyAlgorithm is null";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testClientSymmetricKey_theParameterSymmetricKeyBytesIsNull() {
		String symmetricKeyAlgorithm = "AES";
		byte[] symmetricKeyBytes = null;
		byte[] ivBytes = new byte[16];
		
		Random random = new Random();
		random.nextBytes(ivBytes);
		
		try {
			new ClientSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter symmetricKeyBytes is null";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
	}
	
	@Test
	public void testClientSymmetricKey_theParameterIvBytesIsNull() {
		String symmetricKeyAlgorithm = "AES";
		
		byte[] symmetricKeyBytes = new byte[1024];
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		
		byte[] ivBytes = null;
		
		try {
			new ClientSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter ivBytes is null";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testClientSymmetricKey_ok() {
		String symmetricKeyAlgorithm = "AES";
		
		byte[] symmetricKeyBytes = new byte[16];
		byte[] ivBytes = new byte[16];
		
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			ClientSymmetricKey clientSymmetricKey = new ClientSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
			
			byte[] plainTextBytes = new byte[1024];
			random.nextBytes(plainTextBytes);
			
			byte[] encryptedBytes = clientSymmetricKey.encrypt(plainTextBytes);			
			byte[] decryptedBytes = clientSymmetricKey.decrypt(encryptedBytes);
			
			assertArrayEquals(plainTextBytes, decryptedBytes);
			
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
		
	}

	@Test
	public void testEncrypt_theParameterPlainTextBytesIsNull() {
		String symmetricKeyAlgorithm = "AES";
		byte[] symmetricKeyBytes = new byte[16];
		byte[] ivBytes = new byte[16];
		
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			ClientSymmetricKey clientSymmetricKey = new ClientSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
			
			
			clientSymmetricKey.encrypt(null);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter plainTextBytes is null";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testDecrypt_theParameterEncryptedBytesIsNull() {
		String symmetricKeyAlgorithm = "AES";
		
		byte[] symmetricKeyBytes = new byte[16];
		byte[] ivBytes = new byte[16];
		
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			ClientSymmetricKey clientSymmetricKey = new ClientSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
			
			
			clientSymmetricKey.decrypt(null);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expcetedMessage = "the parameter encryptedBytes is null";
			
			assertEquals(expcetedMessage, errorMessage);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
	}

}
