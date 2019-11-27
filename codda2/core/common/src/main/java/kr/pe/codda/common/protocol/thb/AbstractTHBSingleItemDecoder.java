package kr.pe.codda.common.protocol.thb;

import java.nio.BufferUnderflowException;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.UnknownItemTypeException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.message.builder.info.SingleItemTypeManger;
import kr.pe.codda.common.type.SingleItemType;

public abstract class AbstractTHBSingleItemDecoder {
	abstract public Object getValue(int itemTypeID, String itemName, int itemSize,
			String nativeItemCharset, StreamBuffer binaryInputStream) throws Exception;
	
	protected void throwExceptionIfItemTypeIsDifferent(int itemTypeID, String itemName,
			StreamBuffer binaryInputStream) throws BufferUnderflowException, BodyFormatException, NoMoreDataPacketBufferException {
		int receivedItemTypeID = binaryInputStream.getUnsignedByte();
		if (itemTypeID != receivedItemTypeID) {
			
			String itemTypeName = "unknown";
			try {
				itemTypeName = SingleItemTypeManger.getInstance().getItemTypeName(itemTypeID);
			} catch (UnknownItemTypeException e) {
			}
			
			String receivedItemTypeName = "unknown";
			try {
				receivedItemTypeName = SingleItemTypeManger.getInstance().getItemTypeName(receivedItemTypeID);
			} catch (UnknownItemTypeException e) {
			}
			
			String errorMesssage = String.format("this single item type[id:%d, name:%s][%s] is different from the received item type[id:%d, name:%s]", 
					itemTypeID,
					itemTypeName,
					itemName, 
					receivedItemTypeID,
					receivedItemTypeName);
			throw new BodyFormatException(errorMesssage);
		}
	}	
	
	abstract public SingleItemType getSingleItemType();
}