package kr.pe.codda.weblib.summernote;

public class BoardContentsWhiteParserMananger {
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class BoardContentsSAXParserManangerHolder {
		static final BoardContentsWhiteParser singleton = new BoardContentsWhiteParser();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static BoardContentsWhiteParser getInstance() {
		return BoardContentsSAXParserManangerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자 
	 */
	private BoardContentsWhiteParserMananger() {
		
	}
}
