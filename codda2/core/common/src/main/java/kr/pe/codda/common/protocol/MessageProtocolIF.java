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
	 * 파라미터 'inputMessage' 로 지정된 메시지를 파라미터 'targetMessageStreamBuffer' 로 지정된 '스트림 버퍼' 에 저장한다.
	 *   
	 * @param inputMessage 입력 메시지
	 * @param messageEncoder 메시지 코덱
	 * @param targetMessageStreamBuffer 메시지가 저장될 '스트림 버퍼'
	 * @throws NoMoreDataPacketBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws BodyFormatException 메시지를 스트림으로 변환할때 바디 부분을 꾸리지 못할때 던지는 예외
	 * @throws HeaderFormatException 메시지를 스트림으로 변환할때 헤더 부분을 꾸리지 못할때 던지는 예외
	 */
	public void M2S(AbstractMessage inputMessage, AbstractMessageEncoder messageEncoder, StreamBuffer targetMessageStreamBuffer) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException;
	
	/**
	 * 파라미터 'incomingStream' 로 지정된 입력 스트림으로 부터 메시지를 추출하여 중간 객체를 만들어 파라미터 'receivedMessageForwarder' 를 통해 전달한다.
	 *  
	 * @param incomingStream 입력 스트림
	 * @param receivedMessageForwarder 수신한 중간 객체 전달자
	 * @throws HeaderFormatException 메시지를 스트림으로 변환할때 헤더 부분을 꾸리지 못할때 던지는 예외
	 * @throws NoMoreDataPacketBufferException 랩 버퍼 폴에 랩 버퍼가 없을 때 던지는 예외
	 * @throws InterruptedException 인터럽트가 발생하면 던지는 예외
	 */
	public void S2O(IncomingStream incomingStream, ReceivedMessageForwarderIF receivedMessageForwarder) 
					throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException;
	
	/**
	 * 파라미터 'receivedMiddleObject' 로 지정된 중간 객체를 메시지로 변환한다. 참고) 파라미터 'receivedMiddleObject' 로 지정된 중간 객체는 에러 여부에 상관없이 무조건 자원 해제된다.
	 * 
	 * @param messageDecoder 메시지 디코더
	 * @param mailboxID 메일 박스 식별자
	 * @param mailID 메일 식별자
	 * @param messageID 메시지 식별자
	 * @param receivedMiddleObject 스트림에서 추출된 중간 객체
	 * @return 파라미터 'receivedMiddleObject' 로 지정된 중간 객체로 부터 변환된 메시지
	 * @throws BodyFormatException 파라미터 'receivedMiddleObject' 로 지정된 중간 객체를 메시지로 변환 실패시 던지는 예외
	 */
	public AbstractMessage O2M(AbstractMessageDecoder messageDecoder, int mailboxID, int mailID, String messageID, Object receivedMiddleObject) throws BodyFormatException;
	
	/**
	 *  '중간 객체' 가 갖고 있는 자원을 해제시킨다. 참고) 프로토콜은 '중간 객체' 를 생성하였기에 '중간 객체'가 갖고 있는 자원 해제에 대해서도 온전히 책임을 갖는다.
	 * 
	 * @param mailboxID 메일 박스 식별자
	 * @param mailID 메일 식별자
	 * @param messageID 메시지 식별자
	 * @param receivedMiddleObject 스트림에서 추출된 중간 객체
	 */
	public void closeReadableMiddleObject(int mailboxID, int mailID, String messageID, Object receivedMiddleObject);	
}

