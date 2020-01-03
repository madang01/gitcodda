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
package kr.pe.codda.common.protocol;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.IncomingStream;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;

/**
 * 메시지 프로토콜 인터페이스.
 * 
 * @author Won Jonghoon
 *
 */
public interface MessageProtocolIF {
	
	/**
	 * @return 메시지가 담길 신규 스트림 버퍼를 반환한다 
	 */
	public StreamBuffer createNewMessageStreamBuffer();
	
	/**
	 * 
	 * @param inputMessage
	 * @param messageEncoder
	 * @param targetMessageStreamBuffer
	 * @throws NoMoreDataPacketBufferException
	 * @throws BodyFormatException
	 * @throws HeaderFormatException
	 */
	public void M2S(AbstractMessage inputMessage, AbstractMessageEncoder messageEncoder, StreamBuffer targetMessageStreamBuffer) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException;
	
	public void S2O(IncomingStream inputStreamResource, ReceivedMessageForwarderIF receivedMessageForwarder) 
					throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException;
	
	
	/**
	 * 중간 객체를 메시지로 변환한다. 참고) 파라미터로 넘어온 '중간 객체'는 에러 여부에 상관없이 무조건 자원 해제된다.
	 * 
	 * @param messageDecoder
	 * @param mailboxID
	 * @param mailID
	 * @param messageID
	 * @param readableMiddleObject
	 * @return
	 * @throws BodyFormatException
	 */
	public AbstractMessage O2M(AbstractMessageDecoder messageDecoder, int mailboxID, int mailID, String messageID, Object readableMiddleObject) throws BodyFormatException;
	
	/**
	 *  '중간 객체' 가 갖고 있는 자원을 해제시킨다. 참고) 프로토콜은 '중간 객체' 를 생성하였기에 '중간 객체'가 갖고 있는 자원 해제에 대해서도 온전히 책임을 갖는다.
	 * 
	 * @param mailboxID
	 * @param mailID
	 * @param messageID
	 * @param readableMiddleObject
	 */
	public void closeReadableMiddleObject(int mailboxID, int mailID, String messageID, Object readableMiddleObject);
	
	// public int getDataPacketBufferMaxCntPerMessage();
}

