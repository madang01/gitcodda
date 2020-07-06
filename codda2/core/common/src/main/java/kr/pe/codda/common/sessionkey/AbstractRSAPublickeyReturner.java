/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package kr.pe.codda.common.sessionkey;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.part.CommonPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * RSA 공개키 반환자 추상화 클래스
 * @author Won Jonghoon
 *
 */
public abstract class AbstractRSAPublickeyReturner {
	
	/**
	 * @return 메인 프로젝트의 공개키 바이트 배열
	 * @throws SymmetricException 암호화 관련 처리중 에러 발생시 던지는 예외
	 */
	public final byte[] getMainProjectPublickeyBytes() throws SymmetricException {
		byte[] publicKeyBytes = null;
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		CommonPartConfiguration commonPart = runningProjectConfiguration.getDefaultConfiguration().getCommonPartConfiguration();

		SessionKey.RSAKeypairSourceType rsaKeyPairSoureOfSessionkey = commonPart
				.getRSAKeypairSourceOfSessionKey();
		
		if (rsaKeyPairSoureOfSessionkey.equals(SessionKey.RSAKeypairSourceType.SERVER)) {
			publicKeyBytes = getPublickeyBytesFromMainProjectServer();
		} else if (rsaKeyPairSoureOfSessionkey.equals(SessionKey.RSAKeypairSourceType.FILE)) {
			publicKeyBytes = getPublickeyBytesFromFile();
		} else {
			throw new SymmetricException(new StringBuilder("unknown rsa keypair source[")
					.append(rsaKeyPairSoureOfSessionkey.toString()).append("]").toString());
		}
		
		return publicKeyBytes;
	}
	
	/**
	 * 메인 프로젝트의 공개키 바이트 배열을 반환하는 추상화 메소드
	 * 
	 * @return 메인 프로젝트의 공개키 바이트 배열
	 * @throws SymmetricException 암호화 관련 처리중 에러 발생시 던지는 예외
	 */
	abstract protected byte[] getPublickeyBytesFromMainProjectServer() throws SymmetricException;
	
	/*private static final byte[] getPublickeyBytesFromMainProjectServer() throws SymmetricException {
		Logger log = LoggerFactory.getLogger(ClientRSAPublickeyGetter.class);
		
		AnyProjectClient mainClientProject = ProjectClientManager.getInstance().getMainProjectClient();

		byte[] publicKeyBytes = null;
		try {
			publicKeyBytes = getPublickeyFromServer(mainClientProject);
		} catch (SocketTimeoutException | ServerNotReadyException | NoMoreDataPacketBufferException
				| BodyFormatException | DynamicClassCallException | ServerTaskException | NotLoginException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}*/
	
	/**
	 * 파라미터 'subProjectName'(=서브 프로젝트 이름) 예 1:1 대응하는 서브 프로젝트의 공개키 바이트 배열을 반환하는 추상화 메소드
	 * 
	 * @param subProjectName 서브 프로젝트 이름
	 * @return 파라미터 'subProjectName'(=서브 프로젝트 이름) 예 1:1 대응하는 서브 프로젝트의 공개키 바이트 배열
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	abstract public byte[] getSubProjectPublickeyBytes(String subProjectName) throws SymmetricException;
	
	
	/**
	 * @return 코다 환경 설정 파일에서 지정한 공개키 파일로 부터 얻은 공개키 바이트 배열
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	private final byte[] getPublickeyBytesFromFile() throws SymmetricException {
		
		byte[] publicKeyBytes = null;

		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		CommonPartConfiguration commonPart = runningProjectConfiguration.getDefaultConfiguration().getCommonPartConfiguration();

		File rsaPublickeyFile = null;
	
		try {
			
			rsaPublickeyFile = commonPart.getRSAPublickeyFileOfSessionKey();
			
			publicKeyBytes = CommonStaticUtil.readFileToByteArray(rsaPublickeyFile, 10*1024*1024);	
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("fail to read RSA public key file[")
					.append(rsaPublickeyFile.getAbsolutePath())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);
			
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}
	
	/*public byte[] getPublickeyFromServer(AnyProjectClient clientProject)
			throws SymmetricException, SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException {
		BinaryPublicKey inObj = new BinaryPublicKey();
		inObj.setPublicKeyBytes(new byte[0]);
		
		AbstractMessage outObj = clientProject.sendSyncInputMessage(inObj);
		BinaryPublicKey binaryPublicKeyObj = (BinaryPublicKey) outObj;

		return binaryPublicKeyObj.getPublicKeyBytes();
	}*/
}
