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

package kr.pe.codda.impl.message.BoardBlockReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardBlockReq message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardBlockReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		BoardBlockReq boardBlockReq = (BoardBlockReq)messageObj;
		encodeBody(boardBlockReq, singleItemEncoder, middleObjectToSend);
	}


	private void encodeBody(BoardBlockReq boardBlockReq, SingleItemEncoderIF singleItemEncoder, Object middleObjectToSend) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardBlockReq");


		singleItemEncoder.putValue(pathStack.peek(), "requestedUserID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardBlockReq.getRequestedUserID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardBlockReq.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardBlockReq.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		singleItemEncoder.putValue(pathStack.peek(), "boardNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardBlockReq.getBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleObjectToSend);

		pathStack.pop();
	}
}