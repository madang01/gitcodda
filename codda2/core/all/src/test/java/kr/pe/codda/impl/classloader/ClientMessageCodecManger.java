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

package kr.pe.codda.impl.classloader;

import java.util.HashMap;

import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientMessageCodecManger implements MessageCodecMangerIF {
	private final HashMap<String, MessageCodecIF> messageID2ClientMessageCodecHash = 
			new HashMap<String, MessageCodecIF>();
	private final ClassLoader classloader = this.getClass().getClassLoader();
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class MessageCodecMangerHolder {
		static final ClientMessageCodecManger singleton = new ClientMessageCodecManger();
	}

	
	/**
	 * @return 동기화 쓰지 않는 싱글턴 객체
	 */
	public static ClientMessageCodecManger getInstance() {
		return MessageCodecMangerHolder.singleton;
	}
	
	/**
	 * 생성자
	 */
	private ClientMessageCodecManger() {		
	}

	@Override
	public AbstractMessageDecoder getMessageDecoder(String messageID) throws DynamicClassCallException {
		if (null == messageID) {
			throw new IllegalArgumentException("the parameter messageID is null");
		}

		MessageCodecIF clientMessageCodec = messageID2ClientMessageCodecHash.get(messageID);
		
		if (null == clientMessageCodec) {
			synchronized (messageID2ClientMessageCodecHash) {
				clientMessageCodec = messageID2ClientMessageCodecHash.get(messageID);
				if (null == clientMessageCodec) {
					String clientMessageCodecClassFullName = IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);
					
					Object retObject = CommonStaticUtil.createtNewObject(classloader, clientMessageCodecClassFullName);
					
					if (! (retObject instanceof MessageCodecIF)) {
						String errorMessage = new StringBuilder()
								.append("this instance of ")
								.append(clientMessageCodecClassFullName)
								.append(" class that was created by client classloader[")
								.append(classloader.hashCode())			
								.append("] class is not a instance of MessageCodecIF class").toString();
						throw new DynamicClassCallException(errorMessage);
					}
					clientMessageCodec = (MessageCodecIF)retObject;					
					messageID2ClientMessageCodecHash.put(messageID, clientMessageCodec);
				}
			}
		} 
		
		return clientMessageCodec.getMessageDecoder();
	}

	@Override
	public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException {
		MessageCodecIF clientMessageCodec = messageID2ClientMessageCodecHash.get(messageID);
		
		if (null == clientMessageCodec) {
			synchronized (messageID2ClientMessageCodecHash) {
				clientMessageCodec = messageID2ClientMessageCodecHash.get(messageID);
				if (null == clientMessageCodec) {
					String clientMessageCodecClassFullName = IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);
					
					Object retObject = CommonStaticUtil.createtNewObject(classloader, clientMessageCodecClassFullName);
					
					if (! (retObject instanceof MessageCodecIF)) {
						String errorMessage = new StringBuilder()
								.append("this instance of ")
								.append(clientMessageCodecClassFullName)
								.append(" class that was created by client classloader[")
								.append(classloader.hashCode())			
								.append("] class is not a instance of MessageCodecIF class").toString();
						throw new DynamicClassCallException(errorMessage);
					}
					clientMessageCodec = (MessageCodecIF)retObject;					
					messageID2ClientMessageCodecHash.put(messageID, clientMessageCodec);
				}
			}
		}
		
		return clientMessageCodec.getMessageEncoder();		
	}	
}
