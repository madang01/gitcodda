package kr.pe.codda.common.sessionkey;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

public class ServerSymmetricKeyTest {
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
	public void testServerSymmetricKey_theParameterSymmetricKeyAlgorithmIsNull() {
		
		try {
			new ServerSymmetricKey(null, null, null);
			
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
	public void testServerSymmetricKey_theParameterSymmetricKeyBytesIsNull() {
		
		try {
			new ServerSymmetricKey("AES", null, null);
			
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
	public void testServerSymmetricKey_theParameterIvBytesIsNull() {
		
		byte[] symmetricKeyBytes = new byte[1024];
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		
		try {
			new ServerSymmetricKey("AES", symmetricKeyBytes, null);
			
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
	public void testServerSymmetricKey_ok() {
		byte[] symmetricKeyBytes = new byte[16];
		byte[] ivBytes = new byte[16];
		
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			ServerSymmetricKey serverSymmetricKey = new ServerSymmetricKey("AES", symmetricKeyBytes, ivBytes);
			
			byte[] plainTextBytes = new byte[1024];
			random.nextBytes(plainTextBytes);
			
			byte[] encryptedBytes = serverSymmetricKey.encrypt(plainTextBytes);			
			byte[] decryptedBytes = serverSymmetricKey.decrypt(encryptedBytes);
			
			assertArrayEquals(plainTextBytes, decryptedBytes);
			
			
		} catch (Exception e) {
			log.log(Level.WARNING, "unknown error", e);
			
			fail("unknown error");
		}
	}

	@Test
	public void testEncrypt_theParameterPlainTextBytesIsNull() {
		byte[] symmetricKeyBytes = new byte[16];
		byte[] ivBytes = new byte[16];
		
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			ServerSymmetricKey serverSymmetricKey = new ServerSymmetricKey("AES", symmetricKeyBytes, ivBytes);
			
			
			serverSymmetricKey.encrypt(null);
			
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
		byte[] symmetricKeyBytes = new byte[16];
		byte[] ivBytes = new byte[16];
		
		Random random = new Random();
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			ServerSymmetricKey serverSymmetricKey = new ServerSymmetricKey("AES", symmetricKeyBytes, ivBytes);
			
			
			serverSymmetricKey.decrypt(null);
			
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
