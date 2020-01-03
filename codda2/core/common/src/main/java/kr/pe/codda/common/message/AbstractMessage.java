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

package kr.pe.codda.common.message;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

/**
 * 입출력 메시지 클래스의 부모 추상화 클래스.<br/>
 * 메시지 구현 추상화 클래스를 상속 받으며 메시지 운용에 필요한 메시지 헤더 정보를 갖고 있다.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractMessage {

	private final String messageID;
	/**
	 * 메일함 식별자, mailboxID : unsinged short 2bytes
	 */
	private int mailboxID;
	/**
	 * 메일 식별자, mailID : int 4bytes
	 */
	private int mailID;
	
	/**
	 * 입력 받은 메세지 식별자의 유효성을 반환한다.
	 * 
	 * @param messageID 메세지 식별자
	 * @return 입력 받은 "메세지 식별자"의 유효성 여부
	 */
	public static boolean IsValidMessageID(String messageID) {
		/**
		 * 첫문자는 대문자로 시작하는 이유는 IO 관련 클래스 파일들 생성시 메시지 식별자가 파일명 접두어로 사용되기때문이다.
		 */
		String regexMessageID = "[A-Z][a-zA-Z0-9]+";
		boolean isValid = messageID.matches(regexMessageID);
		return isValid;
	}

	public AbstractMessage() {
		messageID = this.getClass().getSimpleName();
		if (!AbstractMessage.IsValidMessageID(messageID)) {
			String errorMessage = new StringBuilder()
					.append("this class's name[")
					.append(messageID)
					.append("] is not a valid message id").toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.SEVERE, errorMessage);
			System.exit(1);
		}
	}
	
	public String getMessageID() {
		return messageID;
	}
	
	public int getMailboxID() {
		return mailboxID;
	}

	public void setMailboxID(int mailboxID) {
		this.mailboxID = mailboxID;
	}

	public int getMailID() {
		return mailID;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

	/**
	 * 개별 항목에 대한 정보를 입력받아 StringBuffer에 저장하는 함수.
	 * 
	 * @param fieldName
	 *            항목명
	 * @param fieldValue
	 *            항목의 값
	 * @param strBuff
	 *            개별 항목에 대한 정보가 저장되는 StringBuffer
	 */
	private void appendValue(String fieldName, Object fieldValue,
			StringBuffer strBuff) {
		strBuff.append(CommonStaticFinalVars.NEWLINE);
		strBuff.append("\t");
		strBuff.append(fieldName);
		strBuff.append("=[");
		strBuff.append(fieldValue);
		strBuff.append("]");
	}

	/**
	 * 배열 항목에 대한 정보를 입력받아 StringBuffer에 저장하는 함수.
	 * 
	 * @param fieldName
	 *            항목명
	 * @param inx
	 *            // fields = thisClass.getDeclare 배열내 상대 위치
	 * @param fieldValue
	 *            항목명
	 * @param strBuff
	 *            배열 항목에 대한 정보가 저장되는 StringBuffer
	 */
	private void appendArryValue(String fieldName, int inx, Object fieldValue,
			StringBuffer strBuff) {
		// strBuff.append(CommonStaticFinal.NEWLINE);
		// strBuff.append("\t");
		if (inx > 0)
			strBuff.append(", ");
		else {
			strBuff.append(CommonStaticFinalVars.NEWLINE);
			strBuff.append("\t");
			strBuff.append(fieldName);
		}
		strBuff.append("[");
		strBuff.append(inx);
		strBuff.append("]");
		strBuff.append("=[");
		strBuff.append(fieldValue);
		strBuff.append("]");
	}

	/**
	 * java.lang.reflect 를 이용한 멤버 변수들에 대한 이름, 값를 알기쉽게 표현한 문자열로 반환한다.<br/>
	 * 참고) 이 메소드는 32767 번 반복 수행시 멤버변수가 적은것은 200-250 ms, 많은것은 500-600 ms 정도 걸렸음.<br/>
	 * <br/>
	 * 1. 구현시 참고한 사이트<br/>
	 * 1.1 http://www.javapractices.com/topic/TopicAction.do?Id=55<br/>
	 * 1.2 http://www.roseindia.net/javatutorials/Generic_toString.shtml
	 * 
	 * @return 멤버 변수 이름과 값을 알기 쉽게 표현한 문자열
	 */	
	public String toStringUsingReflection() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(this.getClass().getName());
		strBuff.append(" Object {");

		Field[] fields  = this.getClass().getFields();
		if (fields != null) {
			for (Field field : fields) {
				try {
					boolean isSynthetic = field.isSynthetic();
					/*
					 * final 건너띄기. final 변수 검색시 아래와 같은 에러가 발생한다. 에러) Class
					 * io.AbstractOut can not access a member of class
					 * io.out.TestFieldOut$Member$Item with modifiers "final"
					 */
					if (isSynthetic)
						continue;

					Object fieldValue = field.get(this);
					String fieldName = field.getName();

					if (fieldValue != null && fieldValue.getClass().isArray()) {
						int length = Array.getLength(fieldValue);
						for (int i = 0; i < length; i++) {
							Object arry_value = Array.get(fieldValue, i);
							appendArryValue(fieldName, i, arry_value, strBuff);
						}
					} else {
						appendValue(fieldName, fieldValue, strBuff);
					}
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
			strBuff.append(CommonStaticFinalVars.NEWLINE);
			strBuff.append("}");
		}
		return strBuff.toString();
	}
}
