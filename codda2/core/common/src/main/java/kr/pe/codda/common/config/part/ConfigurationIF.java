package kr.pe.codda.common.config.part;

import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.util.SequencedProperties;

public interface ConfigurationIF {
	/**
	 * 설정 파일로 부터 읽어 들인 시퀀스 프로퍼티로 부터 값을 추출하여 저장한다.
	 *  
	 * @param sourceSequencedProperties 값을 추출할 설정 파일로 부터 읽어 들인 시퀀스 프로퍼티
	 * @throws IllegalArgumentException 설정 파일로 부터 읽어 들인 시퀀스 프로퍼티가 null 인 경우 던지는 예외
	 * @throws PartConfigurationException 설정 파일로 부터 읽어 들인 시퀀스 프로퍼티의 키와 값이 잘못된 경우 던지는 예외 
	 */
	public void fromProperties(SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, PartConfigurationException;
	
	
	/**
	 * 항목 값을 바탕으로 의존성 검사를 수행한다. WARNING! {@link #fromProperties(SequencedProperties)} 가 먼저 수행됨을 전제 조건으로 한다 
	 * 
	 * @throws PartConfigurationException
	 */
	public void checkForDependencies() throws PartConfigurationException;
	
	
	/**
	 * 설정 파일에 저장할 시퀀스 프로퍼티에 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 값이 저장될 설정 파일에 저장할 시퀀스 프로퍼티   
	 * @throws IllegalArgumentException 설정 파일에 저장할 시퀀스 프로퍼티가 null 인 경우 던지는 예외
	 */
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException;	
}
