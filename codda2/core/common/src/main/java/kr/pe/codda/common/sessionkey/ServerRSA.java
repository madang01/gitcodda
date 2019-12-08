package kr.pe.codda.common.sessionkey;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.HexUtil;

public final class ServerRSA implements ServerRSAIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final KeyPair rsaKeypair;
	private final String rsaPrivateKeyModulesHexString;
	// private final BigInteger modulusOfRSAPrivateCrtKeySpec;
	
	public ServerRSA(KeyPair rsaKeypair) throws SymmetricException {
		if (null == rsaKeypair) {
			throw new IllegalArgumentException("the parameter rsaKeypair is null");
		}		

		this.rsaKeypair = rsaKeypair; 
		
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder()
					.append("fail to get the RSA KeyFactory, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}
		
		RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = null;
		try {
			rsaPrivateCrtKeySpec = rsaKeyFactory.getKeySpec(rsaKeypair.getPrivate(), RSAPrivateCrtKeySpec.class);			
		} catch (InvalidKeySpecException e) {
			String errorMessage = new StringBuilder()
					.append("fail to get the RSA private key spec(=RSAPrivateCrtKeySpec), errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.SEVERE, errorMessage, e);
			System.exit(1);
		}	
		
		BigInteger modulusOfRSAPrivateCrtKeySpec = rsaPrivateCrtKeySpec.getModulus();
		
		rsaPrivateKeyModulesHexString = HexUtil.getHexStringFromByteArray(modulusOfRSAPrivateCrtKeySpec.toByteArray());
	}

	public byte[] getDupPublicKeyBytes() {
		byte[] publickKeyBytes = rsaKeypair.getPublic().getEncoded();
		return Arrays.copyOf(publickKeyBytes, publickKeyBytes.length);
	}

	public byte[] decrypt(byte[] encryptedBytes) throws SymmetricException {
		if (null == encryptedBytes) {
			throw new IllegalArgumentException("the parameter encryptedBytes is null");
		}
		final byte[] decryptedBytes;		
		final Cipher rsaDecryptModeCipher;
		
		try {
			rsaDecryptModeCipher = Cipher.getInstance(CommonStaticFinalVars.RSA_TRANSFORMATION);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder()
					.append("NoSuchAlgorithmException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = 
					new StringBuilder()
					.append("NoSuchPaddingException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		try {
			rsaDecryptModeCipher.init(Cipher.DECRYPT_MODE, rsaKeypair.getPrivate());
		} catch (InvalidKeyException e) {
			String errorMessage = new StringBuilder()
					.append("InvalidKeyException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		
		try {
			decryptedBytes = rsaDecryptModeCipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = new StringBuilder()
					.append("IllegalBlockSizeException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = 
					new StringBuilder()
					.append("BadPaddingException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		// log.info("공개키로 암호화한 이진 데이터를 16진수로 표현한 문자열[%s]",
		// HexUtil.byteArrayAllToHex(encryptedBytesWithPublicKey));
		// log.info("비밀키로 복호화한 이진 데이터를 16진수로 표현한 문자열[%s]",
		// HexUtil.byteArrayAllToHex(decryptedBytesUsingPrivateKey));
		return decryptedBytes;
	}

	public String getModulusHexStrForWeb() {
		return rsaPrivateKeyModulesHexString;
	}
}