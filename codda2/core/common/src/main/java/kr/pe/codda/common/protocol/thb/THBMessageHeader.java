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

package kr.pe.codda.common.protocol.thb;

/**
 * <pre>
 * TBH 헤더
 * 
 * 서버와 클라이언트사이에 THB 방식의 데이터 교환은 THB 데이터 패킷 단위로 이루어진다.
 * THB 데이터 패킷은 THB 데이터 헤더와 THB 데이터 바디로 구성된다.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class THBMessageHeader {	
	public long bodySize= -1;
	

	@Override
	public String toString() {
		StringBuffer headerInfo = new StringBuffer();
		headerInfo.append("THBMessageHeader={body data size=[");
		headerInfo.append(bodySize);
		headerInfo.append("]");		
		headerInfo.append("}");

		return headerInfo.toString();
	}
}
