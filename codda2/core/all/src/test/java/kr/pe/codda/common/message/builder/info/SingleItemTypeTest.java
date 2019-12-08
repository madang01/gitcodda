package kr.pe.codda.common.message.builder.info;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import kr.pe.codda.common.type.SingleItemType;

public class SingleItemTypeTest {
	
	@Test
	public void test_ItemTypeID가정말로키가맞는지그리고0부터순차적으로할당되었는지에대한테스트() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		int[] arrayOfSingleItemTypeID = new int[singleItemTypes.length];
		Arrays.fill(arrayOfSingleItemTypeID, -1);
		for (SingleItemType singleItemType : singleItemTypes) {
			int singleItemTypeID = singleItemType.getItemTypeID();
			try {
				arrayOfSingleItemTypeID[singleItemTypeID]=singleItemTypeID;
			} catch(IndexOutOfBoundsException e) {
				String errorMessage = new StringBuilder()
						.append("singleItemType[")
						.append(singleItemType.toString())
						.append("] is bad, singleItemTypeID[")
						.append(singleItemTypeID)
						.append("] is out of the range[0 ~ ")
						.append(singleItemTypes.length - 1)
						.append("]").toString();				
				fail(errorMessage);
			}
		}
		for (int i=0; i < arrayOfSingleItemTypeID.length; i++) {
			int singleItemTypeID = arrayOfSingleItemTypeID[i];
			if (-1 == singleItemTypeID) {
				String errorMessage = new StringBuilder()
						.append("the singleItemTypeID[")
						.append(i)
						.append("] is not found").toString();
				fail(errorMessage);
			}
		}
	}
	
	
}
