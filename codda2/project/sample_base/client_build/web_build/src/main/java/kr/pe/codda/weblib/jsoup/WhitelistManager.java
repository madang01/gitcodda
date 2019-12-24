package kr.pe.codda.weblib.jsoup;

import org.jsoup.safety.Whitelist;

public class WhitelistManager {
	private final Whitelist whitelist = new SampleBaseUserSiteWhitelist();

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class WhitelistManagerHolder {
		static final WhitelistManager singleton = new WhitelistManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static Whitelist getInstance() {
		return WhitelistManagerHolder.singleton.whitelist;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자 
	 */
	private WhitelistManager() {
	}	
}
