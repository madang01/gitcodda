package kr.pe.codda.weblib.summernote;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class SampleBaseUserSiteWhiteListManager {
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class SampleBaseUserSiteWhiteInformationManagerHolder {
		static final SampleBaseUserSiteWhiteList singleton = new SampleBaseUserSiteWhiteList();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static SampleBaseUserSiteWhiteList getInstance() {
		return SampleBaseUserSiteWhiteInformationManagerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private SampleBaseUserSiteWhiteListManager() {
		
	}
}
