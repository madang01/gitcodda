package kr.pe.codda.common.io;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public interface ClientOutgoingStreamIF {
	public boolean offer(StreamBuffer messageStreamBuffer, long timeout) throws InterruptedException;
	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreDataPacketBufferException;
	public void decreaseOutputMessageCount();
	public void close();
}
