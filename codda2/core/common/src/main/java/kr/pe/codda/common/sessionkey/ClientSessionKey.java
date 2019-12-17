package kr.pe.codda.common.sessionkey;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientSessionKey implements ClientSessionKeyIF {
	private ClientRSAIF clientRSA = null;
	
	private byte[] symmetricKeyBytes;
	private byte[] ivBytes;
	
	private ClientSymmetricKeyIF clientSymmetricKey = null;
	
	private byte[] sessionKeyBytes;
	
	public ClientSessionKey(ClientRSAIF clientRSA, String symmetricKeyAlgorithm, int symmetricKeySize, int symmetricIVSize, boolean isBase64) throws SymmetricException {
		if (null == clientRSA) {
			throw new IllegalArgumentException("the parameter clientRSA is null");
		}
		
		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}
		
		if (symmetricKeySize <= 0) {
			throw new IllegalArgumentException("the parameter symmetricKeySize is less than or equal to zero");
		}
		
		if (symmetricIVSize <= 0) {
			throw new IllegalArgumentException("the parameter symmetricIVSize is less than or equal to zero");
		}
		
		this.clientRSA = clientRSA;
				
		symmetricKeyBytes = new byte[symmetricKeySize];
		ivBytes = new byte[symmetricIVSize];
		
		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
		    /** dead code */
			String errorMesssage = "fail to create a instance of SecureRandom class";
			throw new SymmetricException(errorMesssage);
		}
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		if (isBase64) {
			sessionKeyBytes = clientRSA.encrypt(CommonStaticUtil.Base64Encoder.encode(symmetricKeyBytes));
		} else {
			sessionKeyBytes = clientRSA.encrypt(symmetricKeyBytes);
		}
		
		clientSymmetricKey = new ClientSymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);
	}
	
	public ClientSymmetricKeyIF getClientSymmetricKey() {
		return clientSymmetricKey;
	}

	public final byte[] getDupSessionKeyBytes() {
		return Arrays.copyOf(sessionKeyBytes, sessionKeyBytes.length);
	}
	
	public byte[] getDupPublicKeyBytes() {
		return clientRSA.getDupPublicKeyBytes();
	}
	
	
	public byte[] getDupIVBytes() {
		return Arrays.copyOf(ivBytes, ivBytes.length);
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientSessionKey [sessionKeyBytes=");
		builder.append(Arrays.toString(sessionKeyBytes));
		builder.append(", publicKeyBytes=");
		builder.append(Arrays.toString(ivBytes));
		builder.append(", ivBytes=");
		builder.append(Arrays.toString(ivBytes));
		builder.append("]");
		return builder.toString();
	}
}
