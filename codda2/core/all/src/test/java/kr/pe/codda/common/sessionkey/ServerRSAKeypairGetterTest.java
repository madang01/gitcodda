package kr.pe.codda.common.sessionkey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.security.KeyPair;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

public class ServerRSAKeypairGetterTest {

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
	
	@Ignore
	public void testGetRSAKeyPair() {
		/** 환경설정 파일을 참조하기 때문에 테스트 생략 */
	}

	@Test
	public void testGetRSAKeyPairFromKeyGenerator() {
		final String planText = "hello#7";
		
		/** WARNING! 현재 동작 확인한 키는 512, 1024, 2048 이다. 자바뿐 아니라 자바스크립트 RSA 모듈에서도 확인. 설정 파일에도 반영되어 있기때문에 반듯이 확인이 필요하다 */
		final int[] rsaKeySizes = {512, 1024, 2048};
		
		for (int rsaKeySize : rsaKeySizes) {
			try {
				
				KeyPair rsaKeyPair = ServerRSAKeypairGetter.getRSAKeyPairFromKeyGenerator(rsaKeySize);
				
				ServerRSA serverRSA = new ServerRSA(rsaKeyPair);
				ClientRSA clientRSA = new ClientRSA(serverRSA.getDupPublicKeyBytes());
				
				byte[] encryptedBytes = clientRSA.encrypt(planText.getBytes());
				
				byte[] decryptedBytes = serverRSA.decrypt(encryptedBytes);
				
				String decryptedText = new String(decryptedBytes);
				
				assertEquals(planText, decryptedText);
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
	
	@Test
	public void testGetRSAKeyPairFromFile() {
		final String planText = "hello#77";
		
		/** WARNING! 현재 동작 확인한 키는 512, 1024, 2048 이다. 자바뿐 아니라 자바스크립트 RSA 모듈에서도 확인. 설정 파일에도 반영되어 있기때문에 반듯이 확인이 필요하다 */
		final int[] rsaKeySizes = {512, 1024, 2048};

		for (int rsaKeySize : rsaKeySizes) {
			try {
				File rsaPrivateKeyFile = File.createTempFile("rsaPrivateKey", ".tmp");
				rsaPrivateKeyFile.deleteOnExit();
				File rsaPublicKeyFile = File.createTempFile("rsaPublicKey", ".tmp");
				rsaPublicKeyFile.deleteOnExit();
				
				ServerRSAKeypairGetter.saveRSAKeyPairFile(rsaKeySize, rsaPrivateKeyFile, rsaPublicKeyFile);
				
				KeyPair rsaKeyPair = ServerRSAKeypairGetter.getRSAKeyPairFromFile(rsaPrivateKeyFile, rsaPublicKeyFile);
				
				ServerRSA serverRSA = new ServerRSA(rsaKeyPair);
				ClientRSA clientRSA = new ClientRSA(serverRSA.getDupPublicKeyBytes());
				
				byte[] encryptedBytes = clientRSA.encrypt(planText.getBytes());
				
				byte[] decryptedBytes = serverRSA.decrypt(encryptedBytes);
				
				String decryptedText = new String(decryptedBytes);
				
				assertEquals(planText, decryptedText);
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
}
