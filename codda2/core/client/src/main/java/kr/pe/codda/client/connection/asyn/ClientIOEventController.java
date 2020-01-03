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
package kr.pe.codda.client.connection.asyn;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class ClientIOEventController extends Thread implements
		ClientIOEventControllerIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private long clientSelectorWakeupInterval = 0L;

	private Selector ioEventSelector = null;
	private LinkedBlockingDeque<ClientIOEventHandlerIF> unregisteredAsynConnectionQueue = new LinkedBlockingDeque<ClientIOEventHandlerIF>();

	public ClientIOEventController(long clientSelectorWakeupInterval) throws IOException,
			NoMoreDataPacketBufferException {
		this.clientSelectorWakeupInterval = clientSelectorWakeupInterval;

		ioEventSelector = Selector.open();
	}

	@Override
	public void addUnregisteredAsynConnection(
			ClientIOEventHandlerIF unregisteredAsynConnection) {
		unregisteredAsynConnectionQueue.addLast(unregisteredAsynConnection);
		
		String infoMessage = new StringBuilder().append("the unregisteredAsynConnection[")
				.append(unregisteredAsynConnection.hashCode())
				.append("] was registered to queue").toString();
		log.info(infoMessage);
	}

	private void processNewConnection() {
		while (! unregisteredAsynConnectionQueue.isEmpty()) {
			ClientIOEventHandlerIF unregisteredAsynConnection = unregisteredAsynConnectionQueue
					.removeFirst();

			boolean isConnectionFinshined;
			try {
				isConnectionFinshined = unregisteredAsynConnection.doConnect();			
			} catch (Exception e) {
				log.log(Level.WARNING, "fail to connect becase of error", e);
				unregisteredAsynConnection.close();
				unregisteredAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();
				continue;
			}			

			try {
				SelectionKey registeredSelectionKey = null;
				if (isConnectionFinshined) {					
					registeredSelectionKey = unregisteredAsynConnection
							.register(ioEventSelector, SelectionKey.OP_READ);
					unregisteredAsynConnection.doFinishConnect(registeredSelectionKey);
				} else {
					registeredSelectionKey = unregisteredAsynConnection
							.register(ioEventSelector, SelectionKey.OP_CONNECT);
				}
				
				registeredSelectionKey.attach(unregisteredAsynConnection);

			} catch (Exception e) {
				String errorMessage = new StringBuilder()
				.append("fail to register the socket channel[")
				.append(unregisteredAsynConnection.hashCode())
				.append("] on selector").toString();
				log.log(Level.WARNING, errorMessage, e);
				unregisteredAsynConnection.close();
				unregisteredAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();
			}
		}
	}
	

	@Override
	public void run() {
		log.info("ClientIOEventController Thread start");

		try {
			while (! Thread.currentThread().isInterrupted()) {				
				processNewConnection();

				ioEventSelector.select(clientSelectorWakeupInterval);
				

				Set<SelectionKey> selectedKeySet = ioEventSelector
						.selectedKeys();
				for (SelectionKey selectedKey : selectedKeySet) {
					Object attachedObject = selectedKey.attachment();
					
					if (null == attachedObject) {
						String warnMessage = new StringBuilder()
								.append("interestedAsynConnection[")
								.append(selectedKey.channel().hashCode())
								.append("] has no attached object").toString();
						
						log.warning(warnMessage);
						continue;
					}
					
					ClientIOEventHandlerIF interestedAsynConnection = (ClientIOEventHandlerIF)attachedObject;
					
					try {
						if (selectedKey.isConnectable()) {
							interestedAsynConnection.onConnect(selectedKey);
							continue;
						}
					} catch (IOException e) {
						String errorMessage = new StringBuilder()
								.append("fail to finish connection[").append(interestedAsynConnection.hashCode())
								.append("] becase io error occured, errmsg=")
								.append(e.getMessage()).toString();
						log.warning(errorMessage);

						interestedAsynConnection.close();
						interestedAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();

						continue;
					} catch (CancelledKeyException e) {
						String errorMessage = new StringBuilder()
								.append("this selector key[socket channel=")
								.append(interestedAsynConnection.hashCode())
								.append("] has been cancelled")
								.toString();
						log.warning(errorMessage);

						interestedAsynConnection.close();
						interestedAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();

						continue;
					} catch (Exception e) {
						String errorMessage = new StringBuilder()
								.append("fail to finish connection[").append(interestedAsynConnection.hashCode())
								.append("] becase unknown error occured").toString();
						log.log(Level.WARNING, errorMessage, e);

						interestedAsynConnection.close();
						interestedAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();

						continue;
					}
					
					try {
						if (selectedKey.isReadable()) {
							
							interestedAsynConnection.onRead(selectedKey);
						}

						if (selectedKey.isWritable()) {
							interestedAsynConnection.onWrite(selectedKey);

						}
					} catch (InterruptedException e) {
						String errorMessage = new StringBuilder()
								.append("InterruptedException occurred while reading the socket[")
								.append(interestedAsynConnection.hashCode()).append("]").toString();
						log.warning(errorMessage);
						interestedAsynConnection.close();
						throw e;
					} catch (NoMoreDataPacketBufferException e) {
						String errorMessage = new StringBuilder()
								.append("the no more data packet buffer error occurred while reading the socket[")
								.append(interestedAsynConnection.hashCode()).append("], errmsg=")
								.append(e.getMessage()).toString();
						log.warning(errorMessage);
						interestedAsynConnection.close();
						continue;
					} catch (IOException e) {
						String errorMessage = new StringBuilder()
								.append("the io error occurred while reading or writing the socket[")
								.append(interestedAsynConnection.hashCode()).append("], errmsg=")
								.append(e.getMessage()).toString();
						log.warning(errorMessage);
						interestedAsynConnection.close();
						continue;
					} catch (CancelledKeyException e) {
						String errorMessage = new StringBuilder()
						.append("this selector key[socket channel=")
						.append(interestedAsynConnection.hashCode())
						.append("] has been cancelled")
						.toString();
						
						log.warning(errorMessage);
						interestedAsynConnection.close();
						continue;
					} catch (Exception e) {
						String errorMessage = new StringBuilder()
								.append("the unknown error occurred while reading or writing the socket[")
								.append(interestedAsynConnection.hashCode()).append("], errmsg=")
								.append(e.getMessage()).toString();
						log.log(Level.WARNING, errorMessage, e);
						interestedAsynConnection.close();
						continue;
					}
				}
				selectedKeySet.clear();
			}
		} catch (InterruptedException e) {
			log.log(Level.WARNING, "Thread stop", e);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().toString();
			log.log(Level.WARNING, errorMessage, e);
		}
		log.info("ClientIOEventController Thread end");
	}

	public void cancel(SelectionKey selectedKey) {
		if (null == selectedKey) {
			return;
		}
		selectedKey.attach(null);
		selectedKey.cancel();
	}

	@Override
	public void wakeup() {
		ioEventSelector.wakeup();
	}
}
