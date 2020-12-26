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
package kr.codda.model;

/**
 * @author Won Jonghoon
 *
 */
public class CoddaHelperSiteManager {
	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class CoddaHelperSiteManagerHolder {
		static final CoddaHelperSite singleton = new CoddaHelperSite();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static CoddaHelperSite getInstance() {
		return CoddaHelperSiteManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private CoddaHelperSiteManager() {
		
	}
}
