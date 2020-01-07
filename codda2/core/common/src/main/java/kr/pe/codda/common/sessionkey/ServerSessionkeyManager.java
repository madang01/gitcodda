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
import java.security.KeyPair;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.type.SessionKey;

public final class ServerSessionkeyManager {
	private ServerSessionkeyIF mainProjectSeverSessionkey = null;
	private SymmetricException savedSymmetricException = null;
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ServerSessionkeyManagerHolder {
		static final ServerSessionkeyManager singleton = new ServerSessionkeyManager();
	}

	
	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * @return ServerSessionkeyManager 객체
	 */
	public static ServerSessionkeyManager getInstance() {
		return ServerSessionkeyManagerHolder.singleton;
	}
	
	/**
	 * 생성자
	 */
	private ServerSessionkeyManager() {
		
		try {
			CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
					.getRunningProjectConfiguration();
			CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
			
			int symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();
			int symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
			
			final String symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
			final KeyPair rsaKeypair;			

			SessionKey.RSAKeypairSourceType rsaKeyPairSoureOfSessionkey = commonPart.getRsaKeypairSourceOfSessionKey();			

			if (rsaKeyPairSoureOfSessionkey.equals(SessionKey.RSAKeypairSourceType.SERVER)) {
				final int rsaKeySize = commonPart.getRsaKeySizeOfSessionKey();

				rsaKeypair = ServerRSAKeypairUtil.createRSAKeyPairFromKeyGenerator(rsaKeySize);
			} else if (rsaKeyPairSoureOfSessionkey.equals(SessionKey.RSAKeypairSourceType.FILE)) {
				final File rsaPrivateKeyFile;
				final File rsaPublicKeyFile;
				try {
					rsaPrivateKeyFile = commonPart.getRSAPrivatekeyFileOfSessionKey();
					rsaPublicKeyFile = commonPart.getRSAPublickeyFileOfSessionKey();
				} catch (CoddaConfigurationException e) {
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

					log.log(Level.WARNING, e.getMessage(), e);
					throw new SymmetricException(e.getMessage());
				}

				rsaKeypair = ServerRSAKeypairUtil.createRSAKeyPairFromFile(rsaPrivateKeyFile, rsaPublicKeyFile);
			} else {
				throw new SymmetricException(new StringBuilder("unknown rsa keypair source[")
						.append(rsaKeyPairSoureOfSessionkey.toString()).append("]").toString());
			}
			
			mainProjectSeverSessionkey = new ServerSessionkey(new ServerRSA(rsaKeypair), symmetricKeyAlgorithm,
					symmetricKeySize, symmetricIVSize);
		} catch (SymmetricException e) {
			savedSymmetricException = e;
		}
	}
	
	/**
	 * @return 메인 프로젝트의 서버 세션키
	 * @throws SymmetricException 암복화 관련 에러 발생시 던지는 예외
	 */
	public ServerSessionkeyIF getMainProjectServerSessionkey() throws SymmetricException {
		if (null != savedSymmetricException) {
			throw savedSymmetricException;
		}
		
		return mainProjectSeverSessionkey;
	}
}
