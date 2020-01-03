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

import kr.pe.codda.common.exception.SymmetricException;

public interface ServerSessionkeyIF {
	public ServerSymmetricKeyIF createNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException;
	public ServerSymmetricKeyIF createNewInstanceOfServerSymmetricKey(boolean isBase64, byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException;
	public String getModulusHexStrForWeb();
	public byte[] getDupPublicKeyBytes();
	public byte[] decryptUsingPrivateKey(byte[] encryptedBytesWithPublicKey) throws SymmetricException;
	
}
