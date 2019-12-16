package kr.pe.codda.common.sessionkey;

import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ServerSessionkey implements ServerSessionkeyIF {
	
	private final ServerRSAIF serverRSA;
	private final String symmetricKeyAlgorithm;
	private final int symmetricKeySize;
	private final int symmetricIVSize;
	
	public ServerSessionkey(ServerRSAIF serverRSA, String symmetricKeyAlgorithm, int symmetricKeySize, int symmetricIVSize) {
		if (null == serverRSA) {
			throw new IllegalArgumentException("the parameter serverRSA is null");
		}
		
		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("the parameter symmetricKeyAlgorithm is null");
		}
		
		this.serverRSA = serverRSA;
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
		this.symmetricKeySize = symmetricKeySize;
		this.symmetricIVSize = symmetricIVSize; 
	}

	public ServerSymmetricKeyIF createNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		return this.createNewInstanceOfServerSymmetricKey(false, sessionkeyBytes, ivBytes);
	}
	
	/**
	 * 참고) 웹에서는 RAS 관련 javascript API 가 이진 데이터를 다룰 수 없기때문에 부득이 base64 인코딩하여 문자열로 만들어 사용하였다. 하여 대칭키를 얻고자 한다면 base64 디코딩 해야한다. 
	 */
	public ServerSymmetricKeyIF createNewInstanceOfServerSymmetricKey(boolean isBase64, byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		// log.info("isBase64={}", isBase64);
		
		if (null == sessionkeyBytes) {
			throw new IllegalArgumentException("the parameter sessionkeyBytes is null");
		}
		
		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
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
		
		final byte[] realSymmetricKeyBytes;
		
		if (isBase64) {
			byte[] base64EncodedStringBytes = serverRSA.decrypt(sessionkeyBytes);
			try {
				realSymmetricKeyBytes = CommonStaticUtil.Base64Decoder.decode(base64EncodedStringBytes);
			} catch (Exception e) {
				String errorMessage = "fail to decode the parameter sessionkeyBytes using base64";
				
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, errorMessage, e);
				
				throw new SymmetricException(errorMessage);
			}
			
			
		} else {
			realSymmetricKeyBytes = serverRSA.decrypt(sessionkeyBytes);
		}
		
		if (symmetricKeySize != realSymmetricKeyBytes.length) {
			String errorMessage = new StringBuilder()
					.append("the parameter sessionkeyBytes's length[")
					.append(realSymmetricKeyBytes.length)
					.append("] is differenct from symmetric key size[")
					.append(symmetricKeySize)
					.append("] of configuration").toString();
			
			throw new SymmetricException(errorMessage);
		}
		
		return new ServerSymmetricKey(symmetricKeyAlgorithm, realSymmetricKeyBytes, ivBytes);
	}	
		
	public String getModulusHexStrForWeb() {
		return serverRSA.getModulusHexStrForWeb();
	}
	
	public final byte[] getDupPublicKeyBytes() {
		return serverRSA.getDupPublicKeyBytes();
	}
	
	public byte[] decryptUsingPrivateKey(byte[] encryptedBytesWithPublicKey) throws SymmetricException {		
		return serverRSA.decrypt(encryptedBytesWithPublicKey);
	}
}
