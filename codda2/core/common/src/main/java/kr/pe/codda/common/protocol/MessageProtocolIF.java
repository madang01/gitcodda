/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	
	public void M2S(AbstractMessage inputMessage, AbstractMessageEncoder messageEncoder, StreamBuffer targetMessageStreamBuffer) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException;
	
	public void S2O(IncomingStream inputStreamResource, ReceivedMessageForwarderIF receivedMessageForwarder) 
					throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException;
	
	
	public AbstractMessage O2M(AbstractMessageDecoder messageDecoder, int mailboxID, int mailID, String messageID, Object readableMiddleObject) throws BodyFormatException;
	
	public void closeReadableMiddleObject(int mailboxID, int mailID, String messageID, Object readableMiddleObject);
	
	// public int getDataPacketBufferMaxCntPerMessage();
}

