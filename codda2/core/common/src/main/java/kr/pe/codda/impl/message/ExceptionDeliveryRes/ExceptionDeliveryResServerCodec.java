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

package kr.pe.codda.impl.message.ExceptionDeliveryRes;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;

/**
 * ExceptionDeliveryRes server codec
 * @author Won Jonghoon
 *
 */
public final class ExceptionDeliveryResServerCodec implements MessageCodecIF {

	private AbstractMessageEncoder messageEncoder = new ExceptionDeliveryResEncoder();

	@Override
	public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {
		throw new DynamicClassCallException("the server don't need a message decoder because it is a message[ExceptionDeliveryRes] that is not sent from client to server");
	}

	@Override
	public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
		return messageEncoder;
	}
}