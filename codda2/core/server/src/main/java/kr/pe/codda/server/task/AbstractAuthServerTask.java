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


package kr.pe.codda.server.task;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;

public abstract class AbstractAuthServerTask extends AbstractServerTask {

	public AbstractAuthServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void execute(String projectName,
			AcceptedConnection fromAcceptedConnection,			
			ProjectLoginManagerIF projectLoginManager,						
			int mailboxID, int mailID, String messageID, Object readableMiddleObject,
			MessageProtocolIF messageProtocol,
			PersonalLoginManagerIF fromPersonalLoginManager) throws InterruptedException {		
		
		
		if (! fromPersonalLoginManager.isLogin()) {
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					SelfExn.ErrorType.valueOf(ServerTaskPermissionException.class),
					"you are not logged in. this service requires a login",
					mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
			
			return;
		}
		super.execute(projectName,
				fromAcceptedConnection,				
				projectLoginManager,
				mailboxID, mailID, messageID, readableMiddleObject, 
				messageProtocol, fromPersonalLoginManager);
	}
}
