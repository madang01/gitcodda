package kr.pe.codda.common.protocol.thb;

import java.nio.BufferOverflowException;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.type.SingleItemType;

public abstract class AbstractTHBSingleItemEncoder {
	abstract public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
			String nativeItemCharset, StreamBuffer binaryOutputStream)
			throws Exception;
	
	protected void writeItemID(int itemTypeID, StreamBuffer binaryOutputStream) throws BufferOverflowException, IllegalArgumentException, BufferOverflowException, NoMoreDataPacketBufferException {
		binaryOutputStream.putUnsignedByte(itemTypeID);
	}
	
	abstract public SingleItemType getSingleItemType();
}
