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
package kr.pe.codda.server.task;

import java.io.File;

/**
 * 서버 동적 클래스 파일 변경 이벤트 수신자
 * 
 * @author Won Jonghoon
 *
 */
public interface ServerDynamicClassFileModifyEventListener {
	/**
	 * 서버 동적 클래스 변경 이벤트를 전달한다
	 * 
	 * @param modifiedDynamicClassFile 수정된 동적 클래스 파일
	 * @throws Exception 수정된 동적 클래스 파일에 대한 처리 과정중 에러 발생시 던지는 예외
	 */
	public void onServerDynamicClassFileModify(File modifiedDynamicClassFile) throws Exception;
}
