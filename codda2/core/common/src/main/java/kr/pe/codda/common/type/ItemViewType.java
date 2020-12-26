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
package kr.pe.codda.common.type;

/**
 * 항목 뷰 유형, 참고) 도우미 GUI 프로그램에서 설정 파일 항목을 보여주고 처리하는 방식을 말한다. DATA 는 문자열, SET 은 콤보박스, PATH 는 경로, FILE 은 파일 로 보여주며 처리된다.
 * 
 * @author Won Jonghoon
 *
 */
public enum ItemViewType {
	DATA, SET, PATH, FILE;
}
