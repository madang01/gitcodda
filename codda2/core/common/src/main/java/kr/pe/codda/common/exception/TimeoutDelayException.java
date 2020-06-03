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
package kr.pe.codda.common.exception;

/**
 * @author Won Jonghoon
 *
 */
public class TimeoutDelayException extends IllegalStateException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3751746198011951119L;
	private final long waitingTimeBeforeThrowingTimeoutException; 
	
	public TimeoutDelayException(long waitingTimeBeforeThrowingTimeoutException) {
		this.waitingTimeBeforeThrowingTimeoutException = waitingTimeBeforeThrowingTimeoutException;
	}
	
	public long getWaitingTime() {
		return waitingTimeBeforeThrowingTimeoutException;
	}
}
