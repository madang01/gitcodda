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

package kr.pe.codda.common.protocol.dhb;

import kr.pe.codda.common.util.HexUtil;

/**
 * 메시시 헤더 클래스.
 * 
 * <pre>
 * 서버와 클라이언트사이에 DHB 방식의 데이터 교환은 DHB 데이터 패킷 단위로 이루어진다.
 * DHB 데이터 패킷은 DHB 데이터 헤더와 DHB 데이터 바디로 구성된다.
 * 
 * 메시지 교환은 DHB 데이터 패킷를 기반으로 실행된다. 
 * DHB 데이터 바디안에 메시지 헤더 정보와 메시지 내용을 넣어서 메시지를 교환하게 된다. 
 * 단, 덩치 큰 메시지는 DHB 데이터 패킷의 최대 크기 제한 때문에 보낼 수 없다.
 * 덩치 큰 메시지를 교환하기 위해서는 나누어서 보낸후 조립해야 한다.
 * 이런 목적을 가진 메시지가 바로 래퍼(Wrapper) 메시지 이다.
 * 송신측에서는 래퍼 메시지 그룹에 덩치 큰 메시지를 나누어 담아서 수신측에 보낸후 
 * 수신측에서는 래퍼 메시지 그룹을 조립하여 송신측에서 보내고자 하는 덩치 큰 메시지로 복원한다.
 * 서버/클라이언트 모두 래퍼 메시지 그룹 관리자를 두어서 
 * 래퍼 메시지 그룹을 관리하며 래퍼 메시지 그룹 완성시 덩치 큰 메시지로 복원 시킨다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class DHBMessageHeader {
	/** bodySize : long 8byte */
	public long bodySize = -1;
	/** bodyMD5 : byte array 16byte */
	public byte bodyMD5Bytes[] = null;
	/** headerMD5 : byte array 16byte */
	public byte headerBodyMD5Bytes[] = null;
	
	

	@Override
	public String toString() {
		StringBuffer headerInfo = new StringBuffer();
		headerInfo.append("DHBMessageHeader={body data size=[");
		headerInfo.append(bodySize);
		headerInfo.append("]");
		if (null != bodyMD5Bytes) {
			headerInfo.append(", body MD5=[");
			headerInfo.append(HexUtil.getHexStringFromByteArray(bodyMD5Bytes));
			headerInfo.append("]");
		}
		if (null != headerBodyMD5Bytes) {
			headerInfo.append(", header MD5=[");
			headerInfo.append(HexUtil.getHexStringFromByteArray(headerBodyMD5Bytes));
			headerInfo.append("]");
		}
		headerInfo.append("}");

		return headerInfo.toString();
	}
}
