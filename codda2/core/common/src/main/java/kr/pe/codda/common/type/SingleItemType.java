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
package kr.pe.codda.common.type;

public enum SingleItemType {
	EXCEPTION_DELIVERY_ERROR_PLACE(0, "exception delivery error place"),
	EXCEPTION_DELIVERY_ERROR_TYPE(1, "exception delivery error type"),
	BYTE(2, "byte"),
	UNSIGNED_BYTE(3, "unsigned byte"),
	SHORT(4, "short"),
	UNSIGNED_SHORT(5, "unsigned short"), 
	INTEGER(6, "integer"), 
	UNSIGNED_INTEGER(7, "unsigned integer"),
	LONG(8, "long"),
	UB_PASCAL_STRING(9, "ub pascal string"),
	US_PASCAL_STRING(10, "us pascal string"),
	SI_PASCAL_STRING(11, "si pascal string"),
	FIXED_LENGTH_STRING(12, "fixed length string"),
	UB_VARIABLE_LENGTH_BYTES(13, "ub variable length byte[]"),
	US_VARIABLE_LENGTH_BYTES(14, "us variable length byte[]"),
	SI_VARIABLE_LENGTH_BYTES(15, "si variable length byte[]"),
	FIXED_LENGTH_BYTES(16, "fixed length byte[]"),
	JAVA_SQL_DATE(17, "java sql date"),
	JAVA_SQL_TIMESTAMP(18, "java sql timestamp"),	
	BOOLEAN(19, "boolean");
	
	
	private String itemTypeName;
	private int itemTypeID;
	
	
	private SingleItemType(int itemTypeID, String itemTypeName) {
		this.itemTypeID = itemTypeID;
		this.itemTypeName = itemTypeName;
	}
	
	public int getItemTypeID() {
		return itemTypeID;
	}
	
	public String getItemTypeName() {
		return itemTypeName;
	}
}
