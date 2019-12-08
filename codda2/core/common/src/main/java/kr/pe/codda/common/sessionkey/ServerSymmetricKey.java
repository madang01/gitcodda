package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.exception.SymmetricException;

/**
 * 서버에서 클라이언트가 보낸 세션키로 부터 생성되는 대칭키
 * @author Won Jonghoon
 *
 */
public class ServerSymmetricKey implements ServerSymmetricKeyIF {
	private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
	
	private final byte[] symmetricKeyBytes;
	private final String symmetricKeyAlgorithm;
	// private final int symmetricKeySize;	
	private byte ivBytes[] = null;
	// private final int symmetricIVSize;
	
	public ServerSymmetricKey(String symmetricKeyAlgorithm, byte[] symmetricKeyBytes, byte[] ivBytes) throws SymmetricException {
		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("the paramter symmetricKeyBytes is null");
		}
		if (null == ivBytes) {
			throw new IllegalArgumentException("the paramter ivBytes is null");
		}
		
		/*
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		int symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();		
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		*/
		
		/*
		if (symmetricKeySize != symmetricKeyBytes.length) {
			String errorMessage = new StringBuilder()
					.append("the parameter sessionkeyBytes's length[")
					.append(symmetricKeyBytes.length)
					.append("] is differenct from symmetric key size[")
					.append(symmetricKeySize)
					.append("] of configuration").toString();
			
			throw new SymmetricException(errorMessage);
		}
		
		if (symmetricIVSize != ivBytes.length) {
			String errorMessage = new StringBuilder()
					.append("the parameter ivBytes length[")
					.append(ivBytes.length)
					.append("] is differenct from symmetric iv size[")
					.append(symmetricIVSize)
					.append("] of configuration").toString();
			
			
			throw new SymmetricException(errorMessage);
		}
		*/
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
		this.symmetricKeyBytes = symmetricKeyBytes;
		this.ivBytes = ivBytes;
		// this.symmetricKeySize = symmetricKeySize;
		// this.symmetricIVSize = symmetricIVSize;
	}
	
	
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException {
		if (null == symmetricKeyBytes) {
			throw new SymmetricException("sessionkey not setting");
		}
		return symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
	}
	
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException {
		if (null == symmetricKeyBytes) {
			throw new SymmetricException("sessionkey not setting");
		}
		
		return symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
	}
}
