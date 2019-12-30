package kr.pe.codda.weblib.summernote;

public class SummerNoteConfigurationManger {

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class SummerNoteConfigurationMangerHolder {
		
		static final String fontNameList[] = new String[0];
		// static final String fontNameList[] = new String[] {"Arial",	"Arial Black", "Comic Sans MS", "Courier New",	"Helvetica", "Impact", "Tahoma", "Times New Roman", "Verdana"};
				
		static final SummerNoteConfiguration singleton = new SummerNoteConfiguration(fontNameList);
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static SummerNoteConfiguration getInstance() {
		return SummerNoteConfigurationMangerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자 
	 */
	private SummerNoteConfigurationManger() {
	}	
}
