/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.pe.codda.client.connection.asyn;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.etc.StreamCharsetFamily;
import kr.pe.codda.common.exception.TimeoutDelayException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.io.WrapBufferPool;
import kr.pe.codda.common.io.WrapBufferPoolIF;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;

/**
 * @author Won Jonghoon
 *
 */
public class ClientOutgoingStreamTest {
	private static Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		JDKLoggerCustomFormatter formatter = new JDKLoggerCustomFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);

		//////////////////////////////////////////		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOffer_최대갯수초과하여즉각적으로실패리턴검증() {
		class ClientIOEventControllerMock implements ClientIOEventControllerIF {

			@Override
			public void addUnregisteredAsynConnection(ClientIOEventHandlerIF asynInterestedConnection) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void wakeup() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cancel(SelectionKey selectedKey) {
				// TODO Auto-generated method stub
				
			}
			
		}

		
		class SelectionKeyMock extends SelectionKey {

			@Override
			public SelectableChannel channel() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Selector selector() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isValid() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int interestOps() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public SelectionKey interestOps(int ops) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int readyOps() {
				// TODO Auto-generated method stub
				return 0;
			}			
		}
		
		
		ClientIOEventControllerMock clientIOEventControllerMock = new ClientIOEventControllerMock();
		SelectionKeyMock selectionKeyMock = new SelectionKeyMock();
		int outputStreamBufferQueueCapacity = 5;
		long aliveTimePerWrapBuffer = 400;
		long timeout = 5000;
		int maxNumberOfWrapBuffer = 5;
		
		
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1024, 100);
		
		
		ClientOutgoingStream clientOutgoingStream = new ClientOutgoingStream(clientIOEventControllerMock, selectionKeyMock, outputStreamBufferQueueCapacity, aliveTimePerWrapBuffer);
		
		for (int i=0; i < outputStreamBufferQueueCapacity; i++) {
			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, maxNumberOfWrapBuffer, wrapBufferPool);
			try {
				sb.putByte((byte)0x11);
				sb.flip();
				sb.setLastBufferLimitUsingLimit();
			} catch (Exception e) {
				log.log(Level.SEVERE, "unknown error", e);
				fail("unknown error");
			}
			
			try {
				clientOutgoingStream.add(sb, timeout);
			} catch (Exception e) {
				log.log(Level.SEVERE, "unknown error", e);
				fail("unknown error");
			}
		}
		
		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, maxNumberOfWrapBuffer, wrapBufferPool);
		try {
			sb.putByte((byte)0x33);
			sb.flip();
			sb.setLastBufferLimitUsingLimit();
		} catch (Exception e) {
			log.log(Level.SEVERE, "unknown error", e);
			fail("unknown error");
		}
		
		try {
			clientOutgoingStream.add(sb, timeout);
			
			fail("no TimeoutDelayException");
		} catch (TimeoutDelayException e) {
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "unknown error", e);
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testOffer_생존시간까지남아있는지테스트() {
		
		class ClientIOEventControllerMock implements ClientIOEventControllerIF {

			@Override
			public void addUnregisteredAsynConnection(ClientIOEventHandlerIF asynInterestedConnection) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void wakeup() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cancel(SelectionKey selectedKey) {
				// TODO Auto-generated method stub
				
			}
			
		}

		
		class SelectionKeyMock extends SelectionKey {

			@Override
			public SelectableChannel channel() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Selector selector() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isValid() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int interestOps() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public SelectionKey interestOps(int ops) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int readyOps() {
				// TODO Auto-generated method stub
				return 0;
			}			
		}
		
		class SocketChannelMock extends SocketChannel {
			
			public SocketChannelMock() {
				super(null);
			}
			

			@Override
			public <T> T getOption(SocketOption<T> name) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<SocketOption<?>> supportedOptions() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SocketChannel bind(SocketAddress local) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> SocketChannel setOption(SocketOption<T> name, T value) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SocketChannel shutdownInput() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SocketChannel shutdownOutput() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Socket socket() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isConnected() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isConnectionPending() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean connect(SocketAddress remote) throws IOException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean finishConnect() throws IOException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public SocketAddress getRemoteAddress() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int read(ByteBuffer dst) throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int write(ByteBuffer src) throws IOException {
				int ret = src.remaining();
				
				src.position(src.limit());
				
				return ret;
			}

			@Override
			public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public SocketAddress getLocalAddress() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void implCloseSelectableChannel() throws IOException {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void implConfigureBlocking(boolean block) throws IOException {
				// TODO Auto-generated method stub
				
			}			
		}		
		
		
		ClientIOEventControllerMock clientIOEventControllerMock = new ClientIOEventControllerMock();
		SelectionKeyMock selectionKeyMock = new SelectionKeyMock();
		SocketChannelMock socketChannelMock = new SocketChannelMock();
		
		int outputStreamBufferQueueCapacity = 5;
		long aliveTimePerWrapBuffer = 5000*1000;
		long timeout = 1;
		int maxNumberOfWrapBuffer = 5;
		
		
		StreamCharsetFamily streamCharsetFamily = new StreamCharsetFamily(Charset.defaultCharset());
		WrapBufferPoolIF wrapBufferPool = new WrapBufferPool(false, ByteOrder.LITTLE_ENDIAN, 1024, 100);
		
		
		ClientOutgoingStream clientOutgoingStream = new ClientOutgoingStream(clientIOEventControllerMock, selectionKeyMock, outputStreamBufferQueueCapacity, aliveTimePerWrapBuffer);
		
		StreamBuffer firstStreamBuffer = null;
		
		for (int i=0; i < outputStreamBufferQueueCapacity; i++) {
			StreamBuffer sb = new StreamBuffer(streamCharsetFamily, maxNumberOfWrapBuffer, wrapBufferPool);
			
			if (null == firstStreamBuffer) {
				firstStreamBuffer = sb;
			}
			
			try {
				sb.putByte((byte)0x11);
				sb.flip();
				sb.setLastBufferLimitUsingLimit();
			} catch (Exception e) {
				log.log(Level.SEVERE, "unknown error", e);
				fail("unknown error");
			}
			
			try {
				clientOutgoingStream.add(sb, timeout);
			} catch (Exception e) {
				log.log(Level.SEVERE, "unknown error", e);
				fail("unknown error");
			}
		}
		
		
		StreamBuffer sb = new StreamBuffer(streamCharsetFamily, maxNumberOfWrapBuffer, wrapBufferPool);
		try {
			sb.putByte((byte)0x33);
			sb.flip();
			sb.setLastBufferLimitUsingLimit();
		} catch (Exception e) {
			log.log(Level.SEVERE, "unknown error", e);
			fail("unknown error");
		}
		
		try {
			while (firstStreamBuffer.hasRemaining()) {
				clientOutgoingStream.write(socketChannelMock);
			}
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "unknown error", e);
			fail("unknown error");
		}		
		
		try {
			clientOutgoingStream.add(sb, timeout);
			
			fail("no TimeoutDelayException");
		} catch (TimeoutDelayException e) {
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "unknown error", e);
			fail("unknown error");
		}
		
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, "unknown error", e);
			fail("unknown error");
		}
		
		try {
			clientOutgoingStream.add(sb, timeout);
		} catch (Exception e) {
			log.log(Level.SEVERE, "unknown error", e);
			fail("unknown error");
		}
	}
}
