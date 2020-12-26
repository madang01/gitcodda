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

package kr.pe.codda.common.message.builder.info;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import kr.pe.codda.common.type.MessageSingleItemType;

public class SingleItemTypeTest {
	
	@Test
	public void test_ItemTypeID가정말로키가맞는지그리고0부터순차적으로할당되었는지에대한테스트() {
		MessageSingleItemType[] singleItemTypes = MessageSingleItemType.values();
		int[] arrayOfSingleItemTypeID = new int[singleItemTypes.length];
		Arrays.fill(arrayOfSingleItemTypeID, -1);
		for (MessageSingleItemType singleItemType : singleItemTypes) {
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
