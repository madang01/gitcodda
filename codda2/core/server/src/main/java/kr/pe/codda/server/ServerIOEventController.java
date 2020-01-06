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
package kr.pe.codda.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.NoMoreWrapBufferException;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.server.classloader.ServerTaskMangerIF;

public class ServerIOEventController extends Thread implements ServerIOEvenetControllerIF, ProjectLoginManagerIF {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private final String projectName;
	private final String serverHost;
	private final int serverPort;
	private final int maxClients;
	private final StreamCharsetFamily streamCharsetFamily;
	private final long socketTimeOut;
	private final int serverDataPacketBufferMaxCntPerMessage;
	private final int serverOutputMessageQueueCapacity;

	private MessageProtocolIF messageProtocol = null;
	private WrapBufferPoolIF wrapBufferPool = null;
	private ServerTaskMangerIF serverTaskManager = null;

	private Selector ioEventSelector = null; // OP_ACCEPT 전용 selector
	private ServerSocketChannel ssc = null;

	private HashMap<SelectionKey, String> selectedKey2LonginIDHash = new HashMap<SelectionKey, String>();
	private HashMap<String, SelectionKey> longinID2SelectedKeyHash = new HashMap<String, SelectionKey>();

	public ServerIOEventController(String projectName, String serverHost, int serverPort, int maxClients,
			long socketTimeOut, StreamCharsetFamily streamCharsetFamily, int serverDataPacketBufferMaxCntPerMessage,
			int serverOutputMessageQueueCapacity, MessageProtocolIF messageProtocol, WrapBufferPoolIF wrapBufferPool,
			ServerTaskMangerIF serverTaskManager) {

		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.maxClients = maxClients;
		this.socketTimeOut = socketTimeOut;
		this.streamCharsetFamily = streamCharsetFamily;
		this.serverDataPacketBufferMaxCntPerMessage = serverDataPacketBufferMaxCntPerMessage;
		this.serverOutputMessageQueueCapacity = serverOutputMessageQueueCapacity;

		this.messageProtocol = messageProtocol;
		this.wrapBufferPool = wrapBufferPool;
		this.serverTaskManager = serverTaskManager;

		initServerSocket();
	}

	/**
	 * 서버 소켓을 생성하고 selector에 OP_ACCEPT로 등록한다.
	 * 
	 * @throws ServerSocketChannel관련 작업에서 발생한다.
	 */
	private void initServerSocket() {
		try {
			ioEventSelector = Selector.open();

			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false); // non block 설정
			ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);

			InetSocketAddress address = new InetSocketAddress(serverHost, serverPort);
			ssc.socket().bind(address);

			ssc.register(ioEventSelector, SelectionKey.OP_ACCEPT);
		} catch (IOException ioe) {
			log.log(Level.SEVERE, "IOException", ioe);
			System.exit(1);
		}

	}

	private void closeAcceptedSocketChannel(SocketChannel acceptableSocketChannel) {
		try {
			acceptableSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to set the value of a acceptable channel[")
					.append(acceptableSocketChannel.hashCode()).append("] option 'SO_LINGER'").toString();

			log.log(Level.WARNING, errorMessage, e);
		}
		try {
			acceptableSocketChannel.close();
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to close the acceptable channel[")
					.append(acceptableSocketChannel.hashCode()).append("]").toString();

			log.log(Level.WARNING, errorMessage, e);
		}
	}

	@Override
	public void run() {
		log.info(new StringBuilder().append("ServerIOEventController::projectName[").append(projectName)
				.append("] start").toString());

		try {
			while (!Thread.currentThread().isInterrupted()) {
				@SuppressWarnings("unused")
				int keyReady = ioEventSelector.select();

				Set<SelectionKey> selectedKeySet = ioEventSelector.selectedKeys();

				try {
					for (SelectionKey selectedKey : selectedKeySet) {
						try {
							if (selectedKey.isAcceptable()) {
								ServerSocketChannel readyChannel = (ServerSocketChannel) selectedKey.channel();

								SocketChannel acceptedSocketChannel = null;

								try {
									acceptedSocketChannel = readyChannel.accept();
								} catch (Exception e) {
									String errorMessage = new StringBuilder().append("fail to accept a connection[")
											.append(readyChannel.hashCode())
											.append("] made to this channel's socket, errmsg=").append(e.getMessage())
											.toString();

									log.warning(errorMessage);
									continue;
								}

								if (null == acceptedSocketChannel) {
									String errorMessage = new StringBuilder().append("fail to accept a connection[")
											.append(readyChannel.hashCode())
											.append("] made to this channel's socket becase the returned value is null")
											.toString();

									log.warning(errorMessage);
									continue;
								}

								if (getNumberOfAcceptedConnection() >= maxClients) {
									String errorMessage = new StringBuilder()
											.append("close the accepted socket channel[")
											.append(acceptedSocketChannel.hashCode())
											.append("] because the maximum number[").append(maxClients)
											.append("] of sockets has been reached").toString();
									log.warning(errorMessage);
									continue;
								}

								try {
									if (acceptedSocketChannel.isConnectionPending()) {

										log.info(new StringBuilder().append(
												"OP_CONNECT but a connection operation is in progress on this accepted channel[")
												.append(acceptedSocketChannel.hashCode()).append("]").toString());

										boolean isSuccess = acceptedSocketChannel.finishConnect();

										if (!isSuccess) {
											String errorMessage = new StringBuilder()
													.append("fail to finish connect the accepted channel[")
													.append(acceptedSocketChannel.hashCode()).append("]").toString();

											log.warning(errorMessage);
											continue;
										}
									}

									setupAcceptedSocketChannel(acceptedSocketChannel);

									SelectionKey acceptedKey = acceptedSocketChannel.register(ioEventSelector,
											SelectionKey.OP_READ);

									AcceptedConnection acceptedConnection = new AcceptedConnection(acceptedKey,
											acceptedSocketChannel, projectName, socketTimeOut, streamCharsetFamily,
											serverDataPacketBufferMaxCntPerMessage, serverOutputMessageQueueCapacity,
											this, messageProtocol, wrapBufferPool, this, serverTaskManager);

									/** 소켓 자원 등록 작업 */
									acceptedKey.attach(acceptedConnection);

									log.info(new StringBuilder().append("successfully changed acceptedKey[")
											.append(acceptedKey.hashCode()).append("]'s accepted socket channel[")
											.append(acceptedSocketChannel.hashCode())
											.append("] to accepted socket channel").toString());
								} catch (NoMoreWrapBufferException e) {
									String errorMessage = new StringBuilder().append(
											"the no more data packet buffer error occurred while registering the socket[")
											.append(acceptedSocketChannel.hashCode())
											.append("] in the accepted connection hash, errmsg=").append(e.getMessage())
											.toString();
									log.warning(errorMessage);

									closeAcceptedSocketChannel(acceptedSocketChannel);
									continue;
								} catch (IOException e) {
									String errorMessage = new StringBuilder()
											.append("the io error occurred while registering the socket[")
											.append(acceptedSocketChannel.hashCode())
											.append("] in the accepted connection hash, errmsg=").append(e.getMessage())
											.toString();
									log.warning(errorMessage);

									closeAcceptedSocketChannel(acceptedSocketChannel);
									continue;
								} catch (CancelledKeyException e) {
									String errorMessage = new StringBuilder()
											.append("this selector key[hashCode=socket channel=")
											.append(acceptedSocketChannel.hashCode()).append("] has been cancelled")
											.toString();

									log.warning(errorMessage);

									closeAcceptedSocketChannel(acceptedSocketChannel);
									continue;
								} catch (Exception e) {
									String errorMessage = new StringBuilder()
											.append("the unknown error occurred while registering the socket[")
											.append(acceptedSocketChannel.hashCode())
											.append("] in the accepted connection hash").toString();
									log.log(Level.WARNING, errorMessage, e);

									closeAcceptedSocketChannel(acceptedSocketChannel);
									continue;
								}
								continue;
							}
						} catch (CancelledKeyException e) {
							String errorMessage = new StringBuilder().append("this selector key[")
									.append(selectedKey.hashCode()).append("] has been cancelled").toString();

							log.warning(errorMessage);

							Object attachedObject = selectedKey.attachment();

							if (null != attachedObject) {
								/** 등록된 셀렉터 키인 경우 자원 회수및 소켓을 닫는다 */
								ServerIOEventHandlerIF accpetedConneciton = (ServerIOEventHandlerIF) attachedObject;
								accpetedConneciton.close();
							}
							continue;
						} catch (Exception e) {
							String errorMessage = new StringBuilder().append("dead code entering, this selector key[")
									.append(selectedKey.hashCode()).append("]").toString();

							log.log(Level.WARNING, errorMessage, e);

							Object attachedObject = selectedKey.attachment();

							if (null != attachedObject) {
								/** 등록된 셀렉터 키인 경우 자원 회수및 소켓을 닫는다 */
								ServerIOEventHandlerIF accpetedConneciton = (ServerIOEventHandlerIF) attachedObject;
								accpetedConneciton.close();
							}
							continue;
						}

						Object attachedObject = selectedKey.attachment();

						if (null == attachedObject) {

							log.warning(new StringBuilder().append(
									"this selectedKey2AcceptedConnectionHash map contains no mapping for the key[")
									.append(selectedKey.hashCode()).append("][")
									.append(selectedKey.channel().hashCode()).append("]").toString());
							continue;
						}

						ServerIOEventHandlerIF accpetedConneciton = (ServerIOEventHandlerIF) attachedObject;

						try {
							if (selectedKey.isReadable()) {
								accpetedConneciton.onRead(selectedKey);
							}

							if (selectedKey.isWritable()) {
								accpetedConneciton.onWrite(selectedKey);
							}
						} catch (InterruptedException e) {
							String errorMessage = new StringBuilder()
									.append("InterruptedException occurred while reading the socket[")
									.append(accpetedConneciton.hashCode()).append("]").toString();
							log.warning(errorMessage);

							accpetedConneciton.close();

							throw e;
						} catch (CancelledKeyException e) {
							String errorMessage = new StringBuilder().append("this selector key[socket channel=")
									.append(accpetedConneciton.hashCode()).append("] has been cancelled").toString();

							log.warning(errorMessage);

							accpetedConneciton.close();
							continue;
						} catch (NoMoreWrapBufferException e) {
							String errorMessage = new StringBuilder()
									.append("the no more data packet buffer error occurred while reading the socket[")
									.append(accpetedConneciton.hashCode()).append("], errmsg=").append(e.getMessage())
									.toString();
							log.warning(errorMessage);

							accpetedConneciton.close();
							continue;
						} catch (IOException e) {
							String errorMessage = new StringBuilder()
									.append("the io error occurred while reading or writing the socket[")
									.append(accpetedConneciton.hashCode()).append("], errmsg=").append(e.getMessage())
									.toString();
							log.warning(errorMessage);

							accpetedConneciton.close();
							continue;
						} catch (Exception e) {
							String errorMessage = new StringBuilder()
									.append("the unknown error occurred while reading or writing the socket[")
									.append(accpetedConneciton.hashCode()).append("]").toString();
							log.log(Level.WARNING, errorMessage, e);

							accpetedConneciton.close();
							continue;
						}
					}
				} finally {
					selectedKeySet.clear();
				}
			}
			// }

			log.warning(
					new StringBuilder().append(projectName).append(" ServerIOEventController loop exit").toString());
		} catch (InterruptedException e) {
			log.warning(new StringBuilder().append(projectName).append(" ServerIOEventController stop").toString());
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append(projectName)
					.append(" ServerIOEventController unknown error, errmsg=").append(e.getMessage()).toString();
			log.log(Level.WARNING, errorMessage, e);
		} finally {
			try {
				ssc.close();
			} catch (IOException ioe) {
				log.log(Level.WARNING, "IOException", ioe);
			}

			try {
				ioEventSelector.close();
			} catch (IOException ioe) {
				log.log(Level.WARNING, "IOException", ioe);
			}
		}
	}

	private void setupAcceptedSocketChannel(SocketChannel acceptedSocketChannel) throws Exception {
		acceptedSocketChannel.configureBlocking(false);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
	}

	@Override
	public void cancel(SelectionKey selectedKey) {
		if (null == selectedKey) {
			return;
		}

		selectedKey.cancel();
		selectedKey.attach(null);
	}

	public int getNumberOfAcceptedConnection() {
		Set<SelectionKey> selectionKeySet = ioEventSelector.keys();
		return selectionKeySet.size() - 1;
	}

	@Override
	public void registerloginUser(SelectionKey selectedKey, String loginID) {
		if (null == selectedKey) {
			throw new IllegalArgumentException("the parameter selectedKey is null");
		}

		if (null == loginID) {
			throw new IllegalArgumentException("the parameter loginID is null");
		}

		// synchronized (loginMangerMonitor) {
		if (selectedKey2LonginIDHash.containsKey(selectedKey)) {

			log.warning(new StringBuilder().append("the parameter selectedKey[").append(selectedKey.hashCode())
					.append("] is the socket channel that is already registered").toString());
			return;
		}
		if (longinID2SelectedKeyHash.containsKey(loginID)) {

			log.warning(new StringBuilder().append("the parameter loginID[").append(loginID)
					.append("] is the login id that is already registered").toString());
			return;
		}

		selectedKey2LonginIDHash.put(selectedKey, loginID);
		longinID2SelectedKeyHash.put(loginID, selectedKey);
		// }

		log.info(new StringBuilder().append("login register success, selectedKey=").append(selectedKey.hashCode())
				.append(", socketChannel=").append(selectedKey.channel().hashCode()).append(", loginID=")
				.append(loginID).toString());
	}

	private void doRemoveLoginUser(SelectionKey selectedKey, String loginID) {
		selectedKey2LonginIDHash.remove(selectedKey);
		longinID2SelectedKeyHash.remove(loginID);
	}

	public void removeLoginUser(SelectionKey selectedKey) {
		if (null == selectedKey) {
			throw new IllegalArgumentException("the parameter selectedKey is null");
		}

		String loginID = selectedKey2LonginIDHash.get(selectedKey);
		if (null != loginID) {
			doRemoveLoginUser(selectedKey, loginID);
		}
	}

	@Override
	public boolean isLogin(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the loginID sc is null");
		}

		boolean isLogin = false;
		SelectionKey selectedKey = null;
		// synchronized (loginMangerMonitor) {
		selectedKey = longinID2SelectedKeyHash.get(loginID);
		// }

		if (null != selectedKey) {
			isLogin = ((SocketChannel) selectedKey.channel()).isConnected();
		}

		return isLogin;
	}

	@Override
	public SelectionKey getSelectionKey(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the parameter loginID is null");
		}

		SelectionKey selectedKey = longinID2SelectedKeyHash.get(loginID);
		return selectedKey;
	}
}
