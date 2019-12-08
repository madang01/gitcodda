package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.exception.SymmetricException;

public class ClientSymmetricKey implements ClientSymmetricKeyIF {
	private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
	
	private final String symmetricKeyAlgorithm;
	private final byte[] symmetricKeyBytes;
	private final byte[] ivBytes;
	
	public ClientSymmetricKey(String symmetricKeyAlgorithm,  byte[] symmetricKeyBytes, byte[] ivBytes) throws SymmetricException {
		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}
		
		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("the parameter symmetricKeyBytes is null");
		}
		
		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
		}
		
		/*
				
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		*/
		
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
		this.symmetricKeyBytes = symmetricKeyBytes;
		this.ivBytes = ivBytes;
		
	}
		
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException {
		return symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
	}
	
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException {
		return symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
	}
	
}
