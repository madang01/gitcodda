package kr.pe.codda.common.io;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public interface ServerOutgoingStreamIF {
	public boolean offer(StreamBuffer messageStreamBuffer) throws InterruptedException;
	public int write(SocketChannel writableSocketChannel) throws IOException, NoMoreDataPacketBufferException;
	public void close();
}
