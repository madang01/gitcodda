package kr.pe.codda.common.sessionkey;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;

public class ClientRSA implements ClientRSAIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);	
	
	private final byte[] publicKeyBytes;	
	
	public ClientRSA(byte[] publicKeyBytes) throws SymmetricException {
		if (null == publicKeyBytes) {
			throw new IllegalArgumentException("the parameter publicKeyBytes is null");
		}
		
		this.publicKeyBytes =publicKeyBytes;		
	}
	
	public byte[] getDupPublicKeyBytes() {		
		return Arrays.copyOf(publicKeyBytes, publicKeyBytes.length);
	}
	
	public byte[] encrypt(byte plainTextBytes[]) throws SymmetricException {
		if (null == plainTextBytes) {
			throw new IllegalArgumentException("the parameter plainTextBytes is null");
		}
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = new StringBuilder()
					.append("NoSuchAlgorithmException, errmsg=")
					.append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				publicKeyBytes);
		
		PublicKey publicKey = null;
		try {
			publicKey = rsaKeyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String
					.format("RSA Public Key InvalidKeySpecException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		
		Cipher rsaEncModeCipher = null;
		try {
			rsaEncModeCipher = Cipher.getInstance(CommonStaticFinalVars.RSA_TRANSFORMATION);
			// rsaDecModeCipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("RSA Cipher.getInstance NoSuchAlgorithmException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = String
					.format("RSA Cipher NoSuchPaddingException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		try {
			rsaEncModeCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		} catch (InvalidKeyException e) {
			String errorMessage = String
					.format("RSA Cipher InvalidKeyException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		
		byte encryptedBytes[] = null;
		try {
			encryptedBytes = rsaEncModeCipher
					.doFinal(plainTextBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = String
					.format("RSA Cipher IllegalBlockSizeException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = String
					.format("RSA Cipher BadPaddingException, errormessage=%s", e.getMessage());
			log.log(Level.WARNING, errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		return encryptedBytes;
	}
	
}
