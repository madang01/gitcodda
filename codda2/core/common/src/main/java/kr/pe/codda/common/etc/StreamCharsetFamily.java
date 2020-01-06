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

package kr.pe.codda.common.etc;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * 문자셋, 문자셋 인코더 그리고 문자셋 디코더 묶음 클래스  
 * 
 * @author Won Jonghoon
 *
 */
public class StreamCharsetFamily {
	private final Charset streamCharset;
	private final CharsetDecoder streamCharsetDecoder;
	private final CharsetEncoder streamCharsetEncoder;
	
	/**
	 * 생성자
	 * @param streamCharset 문자셋
	 */
	public StreamCharsetFamily(Charset streamCharset) {
		if (null == streamCharset) {
			throw new IllegalArgumentException("the parameter streamCharset is null");
		}
		
		this.streamCharset = streamCharset;
		
		streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
	}
	
	/**
	 * @return 문자셋
	 */
	public Charset getCharset() {
		return streamCharset;
	}
	
	/**
	 * @return 문자셋 디코더
	 */
	public CharsetDecoder getCharsetDecoder() {
		return streamCharsetDecoder;
	}
	
	/**
	 * @return 문자셋 인코더
	 */
	public CharsetEncoder getCharsetEncoder() {
		return streamCharsetEncoder;
	}
}
