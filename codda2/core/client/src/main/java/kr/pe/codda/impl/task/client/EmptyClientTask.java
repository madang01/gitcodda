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

package kr.pe.codda.impl.task.client;

import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;


public class EmptyClientTask extends AbstractClientTask {
	public static int count = 0;

	public EmptyClientTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, AsynConnectionIF asynConnection, AbstractMessage outputMessage) throws Exception {
		/*
		String infoMessage = new StringBuilder()
				.append("socket channel[")
				.append(asynConnection.hashCode())
				.append("], output message=")
				.append(outputMessage.toString()).toString();
		
		log.finest(infoMessage);
		*/
		count++;
	}
}
