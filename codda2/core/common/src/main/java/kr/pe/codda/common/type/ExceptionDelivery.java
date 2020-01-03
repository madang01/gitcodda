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

import java.io.IOException;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.impl.message.ExceptionDeliveryRes.ExceptionDeliveryRes;

public abstract class ExceptionDelivery {
	public enum ErrorPlace {
		SERVER((byte)'S'), CLIENT((byte)'C');
		
		private byte errorPlaceByte;
		
		private ErrorPlace(byte errorPlaceByte) {
			this.errorPlaceByte = errorPlaceByte;
		}
		
		public byte getErrorPlaceByte() {
			return errorPlaceByte;
		}		
		
		public static ErrorPlace valueOf(byte nativeErrorPlace) {
			ErrorPlace[] errorPlcaes = ErrorPlace.values();
			for (ErrorPlace errorPlcae : errorPlcaes) {
				if (errorPlcae.getErrorPlaceByte() == nativeErrorPlace) {
					return errorPlcae;
				}
			}
			throw new IllegalArgumentException("the parameter nativeErrorPlace[" + nativeErrorPlace + "] is a unknown error place");
		}
	}
	
	public enum ErrorType {
		BodyFormatException((byte)'B'), 
		DynamicClassCallException((byte)'D'), 
		NoMoreDataPacketBufferException((byte)'N'),
		ServerTaskException((byte)'S'),
		ServerTaskPermissionException((byte)'A'),
		ClientIOException((byte)'E');
		
		private byte errorTypeByte;
		
		
		private ErrorType(byte errorTypeByte) {
			this.errorTypeByte = errorTypeByte;
		}
		
		public byte getErrorTypeByte() {
			return errorTypeByte;
		}
		
		public static ErrorType valueOf(byte errorType) {
			ErrorType[] errorTypes = ErrorType.values();
			for (ErrorType oneErrorType : errorTypes) {
				if (oneErrorType.getErrorTypeByte() == errorType) {
					return oneErrorType;
				}
			}
			throw new IllegalArgumentException("the parameter errorType[" + errorType + "] is a unknown error type");
		}
		
		public static ErrorType valueOf(Class<?> errorTypeClass) {
			if (null == errorTypeClass) {
				throw new IllegalArgumentException("the parameter errorTypeClass is null");
			}
			
			if (errorTypeClass.equals(BodyFormatException.class)) {
				return BodyFormatException;
			} else if (errorTypeClass.equals(DynamicClassCallException.class)) {
				return DynamicClassCallException;
			} else if (errorTypeClass.equals(NoMoreDataPacketBufferException.class)) {
				return NoMoreDataPacketBufferException;
			} else if (errorTypeClass.equals(ServerTaskException.class)) {
				return ServerTaskException;
			} else if (errorTypeClass.equals(ServerTaskPermissionException.class)) {
				return ServerTaskPermissionException;
			} else if (errorTypeClass.equals(IOException.class)) {
				return ClientIOException;
			} else {
				String errorMessage = new StringBuilder()
						.append("the parameter errorTypeClass[")
						.append(errorTypeClass.getCanonicalName())
						.append("] is not 1: 1 class corresponding to error type").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		public static void throwSelfExnException(ExceptionDeliveryRes throwExceptionRes) 
				throws DynamicClassCallException, 
				NoMoreDataPacketBufferException, ServerTaskException,
				ServerTaskPermissionException, BodyFormatException, IOException {
			ErrorType errorType = throwExceptionRes.getErrorType();
			String errorMessage = throwExceptionRes.toString();
			
			switch (errorType) {
			case BodyFormatException :
				throw new BodyFormatException(errorMessage);
			case DynamicClassCallException :
				throw new DynamicClassCallException(errorMessage);
			case NoMoreDataPacketBufferException :
				throw new NoMoreDataPacketBufferException(errorMessage);
			case ServerTaskException :
				throw new ServerTaskException(errorMessage);
			case ServerTaskPermissionException :
				throw new ServerTaskPermissionException(errorMessage);
			case ClientIOException :
				throw new IOException(errorMessage);
		
			default:
				throw new IllegalArgumentException("unknown error type["+throwExceptionRes.toString()+"]");
			}
		}
	}
}
