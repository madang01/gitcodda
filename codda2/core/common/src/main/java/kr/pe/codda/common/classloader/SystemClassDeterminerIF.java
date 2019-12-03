package kr.pe.codda.common.classloader;

/**
 * 시스템 클래스 여부 판단자 인터페이스
 * @author Won Jonghoon
 *
 */
public interface SystemClassDeterminerIF {
	
	/**
	 * @param classFullName 클래스 이름
	 * @return 시스템 클래스 여부, 지정한 클래스 이름이 시스템 클래스이면 참을 반환하고 아니면 거짓을 반환한다
	 */
	public boolean isSystemClassName(String classFullName);
}
