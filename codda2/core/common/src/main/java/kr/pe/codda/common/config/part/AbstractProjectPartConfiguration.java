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
package kr.pe.codda.common.config.part;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningCharset;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningByteOrder;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningConnectionType;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningMessageProtocolType;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ClientConnectionType;
import kr.pe.codda.common.type.ItemViewType;
import kr.pe.codda.common.type.KeyTypeOfConfieProperties;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * 프로젝트 파트 설정 추상화 클래스
 * 
 * @author Won Jonghoon
 *
 */
public abstract class AbstractProjectPartConfiguration implements PartConfigurationIF {
	protected final Logger log = Logger.getLogger(AbstractProjectPartConfiguration.class.getName());

	/************* common 변수 시작 ******************/
	public static final String itemIDForServerHost = "common.host";
	private GeneralConverterReturningNoTrimString nativeValueConverterForServerHost = new GeneralConverterReturningNoTrimString();
	private String serverHost = null;

	public static final String itemIDForServerPort = "common.port";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForServerPort = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1024, Integer.MAX_VALUE);
	private Integer serverPort = null;

	public static final String itemIDForByteOrder = "common.byteorder";
	private SetTypeConverterReturningByteOrder nativeValueConverterForByteOrder = new SetTypeConverterReturningByteOrder();
	private ByteOrder byteOrder = null;

	public static final String itemIDForCharset = "common.charset";
	private GeneralConverterReturningCharset nativeValueConverterForCharset = new GeneralConverterReturningCharset();
	private Charset charset = null;

	public static final String itemIDForMessageProtocolType = "common.message_protocol_type";
	private SetTypeConverterReturningMessageProtocolType nativeValueConverterForMessageProtocolType = new SetTypeConverterReturningMessageProtocolType();
	private MessageProtocolType messageProtocolType = null;
	/************* common 변수 종료 ******************/

	/************* client 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	public static final String itemIDForClientMonitorTimeInterval = "client.monitor.time_interval";
	private GeneralConverterReturningLongBetweenMinAndMax nativeValueConverterForClientMonitorTimeInterval = new GeneralConverterReturningLongBetweenMinAndMax(
			1000L, (long) Integer.MAX_VALUE);
	private Long clientMonitorTimeInterval = null;
	/***** 모니터 환경 변수 종료 *****/

	public static final String itemIDForWhetherClientWrapBufferIsDirect = "client.wrap_buffer.isdirect";
	private SetTypeConverterReturningBoolean nativeValueConverterForWhetherClientWrapBufferIsDirect = new SetTypeConverterReturningBoolean();
	private Boolean whetherClientWrapBufferIsDirect = null;

	public static final String itemIDForClientWrapBufferMaxCntPerMessage = "client.wrap_buffer.max_cnt_per_message";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForClientWrapBufferMaxCntPerMessage = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1, Integer.MAX_VALUE);
	private Integer clientWrapBufferMaxCntPerMessage = null;

	public static final String itemIDForClientWrapBufferSize = "client.wrap_buffer.size";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForClientWrapBufferSize = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1024, Integer.MAX_VALUE);
	private Integer clientWrapBufferSize = null;

	public static final String itemIDForClientWrapBufferPoolSize = "client.wrap_buffer.pool_size";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForClientWrapBufferPoolSize = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1, Integer.MAX_VALUE);
	private Integer clientWrapBufferPoolSize = null;

	/***** 연결 클래스 관련 환경 변수 시작 *****/
	/** 연결 종류 */
	public static final String itemIDForClientConnectionType = "client.connection.type";
	private SetTypeConverterReturningConnectionType nativeValueConverterForClientConnectionType = new SetTypeConverterReturningConnectionType();
	private ClientConnectionType clientConnectionType = null;

	/** 연결 타임 아웃 시간 */
	public static final String itemIDForClientConnectionTimeout = "client.connection.timeout";
	private GeneralConverterReturningLongBetweenMinAndMax nativeValueConverterForClientConnectionTimeout = new GeneralConverterReturningLongBetweenMinAndMax(
			1000L, (long) Integer.MAX_VALUE);
	private Long clientConnectionTimeout = null;

	/** 연결 갯수 */
	public static final String itemIDForClientConnectionCount = "client.connection.count";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForClientConnectionCount = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1, Integer.MAX_VALUE);
	private Integer clientConnectionCount = null;

	/** 연결 최대 갯수 */
	public static final String itemIDForClientConnectionMaxCount = "client.connection.max_count";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForClientConnectionMaxCount = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1, Integer.MAX_VALUE);
	private Integer clientConnectionMaxCount = null;

	/** 연결 폴 지원자 활동 주기 */
	public static final String itemIDForClientConnectionPoolSupporterTimeInterval = "client.connection.pool.supportor.time_interval";
	private GeneralConverterReturningLongBetweenMinAndMax nativeValueConverterForClientConnectionPoolSupporterTimeInterval = new GeneralConverterReturningLongBetweenMinAndMax(
			1000L, (long) Integer.MAX_VALUE);
	private Long clientConnectionPoolSupporterTimeInterval = null;

	/** 연결 폴로무터 연결을 얻기 위한 재 시도 간격 */
	public static final String itemIDForRetryIntervaTimeToGetConnection = "client.connection.pool.retry_interval_time_to_get_connection";
	private GeneralConverterReturningLongBetweenMinAndMax nativeValueConverterForRetryIntervaTimeToGetConnection = new GeneralConverterReturningLongBetweenMinAndMax(
			1000L, (long) Integer.MAX_VALUE);
	private Long clientRetryIntervaTimeToGetConnection = null;

	/***** 연결 클래스 관련 환경 변수 종료 *****/

	/** 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간 */
	public static final String itemIDForAliveTimePerWrapBuffer = "client.asyn.alive_time_per_wrapbuffer";
	private GeneralConverterReturningLongBetweenMinAndMax nativeValueConverterForAliveTimePerWrapBuffer = new GeneralConverterReturningLongBetweenMinAndMax(
			100L, (long) Integer.MAX_VALUE);
	private Long clientAsynAliveTimePerWrapBuffer = null;

	/** 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격 */
	public static final String itemIDForRetryIntervaTimeToAddInputMessage = "client.asyn.retry_interval_time_to_add_input_message";
	private GeneralConverterReturningLongBetweenMinAndMax nativeValueConverterForRetryIntervaTimeToAddInputMessage = new GeneralConverterReturningLongBetweenMinAndMax(
			100L, (long) Integer.MAX_VALUE);
	private Long clientAsynRetryIntervaTimeToAddInputMessage = null;

	/** 비동기+공유 연결의 개인 메일함 갯수 */
	public static final String itemIDForClientMailboxCountPerAsynShareConnection = "client.asyn.share_connection.mailbox_count";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForClientMailboxCountPerAsynShareConnection = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1, Integer.MAX_VALUE);
	private Integer clientMailboxCountPerAsynShareConnection = null;

	/** 비동기 출력 메시지 처리자 쓰레드 갯수 */
	// private Integer clientAsynExecutorPoolSize = null;

	/** 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기 */
	public static final String itemIDForClientAsynInputMessageQueueCapacity = "client.asyn.input_message_queue_capacity";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForClientAsynInputMessageQueueCapacity = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1, Integer.MAX_VALUE);
	private Integer clientAsynInputMessageQueueCapacity = null;

	/** 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 */
	public static final String itemIDForClientAsynOutputMessageQueueCapacity = "client.asyn.output_message_queue_capacity";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForClientAsynOutputMessageQueueCapacity = new GeneralConverterReturningIntegerBetweenMinAndMax(
			1, Integer.MAX_VALUE);
	private Integer clientAsynOutputMessageQueueCapacity = null;

	/** 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기 */
	public static final String itemIDForClientSelectorWakeupInterval = "client.asyn.selector.wakeup_interval";
	private GeneralConverterReturningLongBetweenMinAndMax nativeValueConverterForClientSelectorWakeupInterval = new GeneralConverterReturningLongBetweenMinAndMax(
			1L, (long) Integer.MAX_VALUE);
	private Long clientSelectorWakeupInterval = null;

	/************* client 변수 종료 ******************/

	/**
	 * @return 키를 이루는 항목 식별자 이전까지의 접두어
	 */
	abstract public String getPrefixBeforeItemID();

	@Override
	public void checkForDependencies() throws PartConfigurationException {
		if (clientConnectionMaxCount < clientConnectionCount) {
			String clientConnectionMaxCountItemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
					getPrefixBeforeItemID(), itemIDForClientConnectionMaxCount, KeyTypeOfConfieProperties.VALUE);

			String clientConnectionCountItemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
					getPrefixBeforeItemID(), itemIDForClientConnectionCount, KeyTypeOfConfieProperties.VALUE);
			String errorMessage = new StringBuilder().append("연결 갯수[key=").append(clientConnectionCountItemKey)
					.append(", value=").append(clientConnectionMaxCount).append("]가 최대 연결 갯수[key=")
					.append(clientConnectionMaxCountItemKey).append(", value=").append(clientConnectionCount)
					.append("] 보다 큽니다").toString();
			throw new PartConfigurationException(clientConnectionCountItemKey, errorMessage);
		}
	}

	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		fromPropertiesForServerHost(sourceSequencedProperties);
		fromPropertiesForServerPort(sourceSequencedProperties);
		fromPropertiesForByteOrder(sourceSequencedProperties);
		fromPropertiesForCharset(sourceSequencedProperties);
		fromPropertiesForMessageProtocolType(sourceSequencedProperties);

		fromPropertiesForClientMonitorTimeInterval(sourceSequencedProperties);
		fromPropertiesForWhetherClientWrapBufferIsDirect(sourceSequencedProperties);
		fromPropertiesForClientWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		fromPropertiesForClientWrapBufferSize(sourceSequencedProperties);
		fromPropertiesForClientWrapBufferPoolSize(sourceSequencedProperties);
		fromPropertiesForClientConnectionType(sourceSequencedProperties);
		fromPropertiesForClientConnectionTimeout(sourceSequencedProperties);
		fromPropertiesForClientConnectionCount(sourceSequencedProperties);
		fromPropertiesForClientConnectionMaxCount(sourceSequencedProperties);
		fromPropertiesForClientConnectionPoolSupporterTimeInterval(sourceSequencedProperties);
		fromPropertiesForRetryIntervaTimeToGetConnection(sourceSequencedProperties);

		fromPropertiesForAliveTimePerWrapBuffer(sourceSequencedProperties);
		fromPropertiesForRetryIntervaTimeToAddInputMessage(sourceSequencedProperties);
		fromPropertiesForClientMailboxCountPerAsynShareConnection(sourceSequencedProperties);
		fromPropertiesForClientAsynInputMessageQueueCapacity(sourceSequencedProperties);
		fromPropertiesForClientAsynOutputMessageQueueCapacity(sourceSequencedProperties);
		fromPropertiesForClientSelectorWakeupInterval(sourceSequencedProperties);
	}

	@Override
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException {
		toPropertiesForServerHost(targetSequencedProperties);
		toPropertiesForServerPort(targetSequencedProperties);
		toPropertiesForByteOrder(targetSequencedProperties);
		toPropertiesForCharset(targetSequencedProperties);
		toPropertiesForMessageProtocolType(targetSequencedProperties);

		toPropertiesForClientMonitorTimeInterval(targetSequencedProperties);
		toPropertiesForWhetherClientWrapBufferIsDirect(targetSequencedProperties);
		toPropertiesForClientWrapBufferMaxCntPerMessage(targetSequencedProperties);
		toPropertiesForClientWrapBufferSize(targetSequencedProperties);
		toPropertiesForClientWrapBufferPoolSize(targetSequencedProperties);
		toPropertiesForClientConnectionType(targetSequencedProperties);
		toPropertiesForClientConnectionTimeout(targetSequencedProperties);
		toPropertiesForClientConnectionCount(targetSequencedProperties);
		toPropertiesForClientConnectionMaxCount(targetSequencedProperties);
		toPropertiesForClientConnectionPoolSupporterTimeInterval(targetSequencedProperties);

		toPropertiesForRetryIntervaTimeToGetConnection(targetSequencedProperties);
		toPropertiesForAliveTimePerWrapBuffer(targetSequencedProperties);
		toPropertiesForRetryIntervaTimeToAddInputMessage(targetSequencedProperties);

		toPropertiesForClientMailboxCountPerAsynShareConnection(targetSequencedProperties);
		toPropertiesForClientAsynInputMessageQueueCapacity(targetSequencedProperties);
		toPropertiesForClientAsynOutputMessageQueueCapacity(targetSequencedProperties);
		toPropertiesForClientSelectorWakeupInterval(targetSequencedProperties);

	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 호스트 주소를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 호스트 주소 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerHost(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = 
				RunningProjectConfiguration.buildKeyOfConfigFile(
						getPrefixBeforeItemID(), itemIDForServerHost, KeyTypeOfConfieProperties.VALUE);
		

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {

			serverHost = nativeValueConverterForServerHost.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버 호스트 주소 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버 호스트 주소 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerHost(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerHost, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트에서 접속할 서버 주소, default value[localhost]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerHost, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverHost) ? "localhost" : serverHost;
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 포트 번호를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 포트 번호 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerPort(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerPort, KeyTypeOfConfieProperties.VALUE);
		

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			serverPort = nativeValueConverterForServerPort.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버 포트 번호 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버 포트 번호 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerPort(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerPort, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "포트 번호, default value[9090], min[" + nativeValueConverterForServerPort.getMin()+"], max[" + nativeValueConverterForServerPort.getMax()+"]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerPort, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverPort) ? "9090" : String.valueOf(serverPort);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 바이트 오더를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 바이트 오더 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForByteOrder(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForByteOrder, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			byteOrder = nativeValueConverterForByteOrder.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 바이트 오더 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 바이트 오더 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForByteOrder(SequencedProperties targetSequencedProperties) {
		String byteOrderSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForByteOrder.getItemValueSet());
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForByteOrder, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "바이트 오더, default value[LITTLE_ENDIAN], the byteorder set[" + byteOrderSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForByteOrder, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == byteOrder) ? ByteOrder.LITTLE_ENDIAN.toString() : byteOrder.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String byteOrderSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForByteOrder, KeyTypeOfConfieProperties.SET);		
				
		targetSequencedProperties.put(byteOrderSetKey, byteOrderSetValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 문자셋을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 문자셋 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForCharset(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForCharset, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			charset = nativeValueConverterForCharset.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 문자셋 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 문자셋 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForCharset(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForCharset, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "문자셋, default value[UTF-8]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForCharset, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == charset) ? "UTF-8" : charset.name();
		targetSequencedProperties.put(itemKey, itemValue);
	}


	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 메시지 프로토콜 유형을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 메시지 프로토콜 유형 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForMessageProtocolType(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForMessageProtocolType, KeyTypeOfConfieProperties.VALUE);
		

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {

			messageProtocolType = nativeValueConverterForMessageProtocolType.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 메시지 프로토콜 유형 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 메시지 프로토콜 유형 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForMessageProtocolType(SequencedProperties targetSequencedProperties) {
		String messageProtocolTypeSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForMessageProtocolType, KeyTypeOfConfieProperties.SET);
		String messageProtocolTypeSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForMessageProtocolType.getItemValueSet());
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForMessageProtocolType, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "메시지 프로토콜, DHB:교차 md5 헤더+바디, DJSON:길이+존슨문자열, THB:길이+바디, default value[DHB], the message protocol set[" + messageProtocolTypeSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForMessageProtocolType, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == messageProtocolType) ? MessageProtocolType.DHB.name() : messageProtocolType.name();
		targetSequencedProperties.put(itemKey, itemValue);		
		
		targetSequencedProperties.put(messageProtocolTypeSetKey, messageProtocolTypeSetValue);		
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 모니터링 주기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 모니터링 주기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForClientMonitorTimeInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMonitorTimeInterval, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			clientMonitorTimeInterval = nativeValueConverterForClientMonitorTimeInterval.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 모니터링 주기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 모니터링 주기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientMonitorTimeInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMonitorTimeInterval, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트 모니터링 주기, 단위 ms, default value[5000], min[" + nativeValueConverterForClientMonitorTimeInterval.getMin() + "], max[" + nativeValueConverterForClientMonitorTimeInterval.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMonitorTimeInterval, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientMonitorTimeInterval) ? "5000" : String.valueOf(clientMonitorTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 랩버퍼 다이렉트 여부를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 랩버퍼 다이렉트 여부 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForWhetherClientWrapBufferIsDirect(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherClientWrapBufferIsDirect, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			whetherClientWrapBufferIsDirect = nativeValueConverterForWhetherClientWrapBufferIsDirect.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 랩버퍼 다이렉트 여부 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 랩버퍼 다이렉트 여부 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForWhetherClientWrapBufferIsDirect(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherClientWrapBufferIsDirect, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "whether client wrap buffer is direct, default value[true], the boolean set[false, true]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherClientWrapBufferIsDirect, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == whetherClientWrapBufferIsDirect) ? Boolean.TRUE.toString()
				: whetherClientWrapBufferIsDirect.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherClientWrapBufferIsDirect, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);
		
		String whetherClientWrapBufferIsDirectSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForWhetherClientWrapBufferIsDirect.getItemValueSet());
		String whetherClientWrapBufferIsDirectSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherClientWrapBufferIsDirect, KeyTypeOfConfieProperties.SET);
		targetSequencedProperties.put(whetherClientWrapBufferIsDirectSetValue, whetherClientWrapBufferIsDirectSetKey);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 메시지당 클라이언트 랩버퍼 최대 갯수를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 메시지당 클라이언트 랩버퍼 최대 갯수 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForClientWrapBufferMaxCntPerMessage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferMaxCntPerMessage, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			clientWrapBufferMaxCntPerMessage = nativeValueConverterForClientWrapBufferMaxCntPerMessage.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 메시지당 클라이언트 랩버퍼 최대 갯수 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 메시지당 클라이언트 랩버퍼 최대 갯수 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientWrapBufferMaxCntPerMessage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferMaxCntPerMessage, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트에서 1개 메시지당 할당 받을 수 있는 랩 버퍼 최대 갯수, default value[1000], min[" + nativeValueConverterForClientWrapBufferMaxCntPerMessage.getMin() + "], max[" + nativeValueConverterForClientWrapBufferMaxCntPerMessage.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferMaxCntPerMessage, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientWrapBufferMaxCntPerMessage) ? "1000"
				: String.valueOf(clientWrapBufferMaxCntPerMessage);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 랩버퍼 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 랩버퍼 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForClientWrapBufferSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferSize, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			clientWrapBufferSize = nativeValueConverterForClientWrapBufferSize.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 랩버퍼 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 랩버퍼 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientWrapBufferSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferSize, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트 랩 버퍼 크기, 단위 byte, default value[4096], min[" + nativeValueConverterForClientWrapBufferSize.getMin() + "], max[" + nativeValueConverterForClientWrapBufferSize.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferSize, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientWrapBufferSize) ? "4096" : String.valueOf(clientWrapBufferSize);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 랩버퍼 폴 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 랩버퍼 폴 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForClientWrapBufferPoolSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferPoolSize, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			clientWrapBufferPoolSize = nativeValueConverterForClientWrapBufferPoolSize.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 랩버퍼 폴 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 랩버퍼 폴 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientWrapBufferPoolSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferPoolSize, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트 랩 버퍼 폴 크기, default value[1000], min[" + nativeValueConverterForClientWrapBufferPoolSize.getMin() + "], max[" + nativeValueConverterForClientWrapBufferPoolSize.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferPoolSize, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientWrapBufferPoolSize) ? "1000" : String.valueOf(clientWrapBufferPoolSize);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 연결 유형을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 연결 유형 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientConnectionType(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionType, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		

		try {
			clientConnectionType = nativeValueConverterForClientConnectionType.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 연결 유형 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 연결 유형 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientConnectionType(SequencedProperties targetSequencedProperties) {
		String clientConnectionTypeSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForClientConnectionType.getItemValueSet());
		String clientConnectionTypeSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionType, KeyTypeOfConfieProperties.SET);		
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionType, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트 연결 종류, ASYN:비동기, SYNC:동기, default value[ASYN], the connection type set[ASYN, SYNC]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionType, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientConnectionType) ? ClientConnectionType.ASYN.name()
				: clientConnectionType.name();
		targetSequencedProperties.put(itemKey, itemValue);		
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionType, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		targetSequencedProperties.put(clientConnectionTypeSetValue, clientConnectionTypeSetKey);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 연결 타임아웃을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 연결 타임아웃 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientConnectionTimeout(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionTimeout, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientConnectionTimeout = nativeValueConverterForClientConnectionTimeout.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 연결 타임아웃 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 연결 타임아웃 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientConnectionTimeout(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionTimeout, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "연결 타임아웃, 단위 ms, default value[5000], min[" + nativeValueConverterForClientConnectionTimeout.getMin() + "], max[" + nativeValueConverterForClientConnectionTimeout.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionTimeout, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientConnectionTimeout) ? "5000" : String.valueOf(clientConnectionTimeout);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 연결 갯수를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 연결 갯수 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientConnectionCount(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionCount, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		

		try {
			clientConnectionCount = nativeValueConverterForClientConnectionCount.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 연결 갯수 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 연결 갯수 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientConnectionCount(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionCount, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "연결 갯수, default value[5], min[" + nativeValueConverterForClientConnectionCount.getMin() + "], max[" + nativeValueConverterForClientConnectionCount.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionCount, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientConnectionCount) ? "5" : String.valueOf(clientConnectionCount);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 최대 연결 갯수를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 최대 연결 갯수 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientConnectionMaxCount(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionMaxCount, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientConnectionMaxCount = nativeValueConverterForClientConnectionMaxCount.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 최대 연결 갯수 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 최대 연결 갯수 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientConnectionMaxCount(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionMaxCount, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "최대 연결 갯수, default value[5], min[" + nativeValueConverterForClientConnectionMaxCount.getMin() + "], max[" + nativeValueConverterForClientConnectionMaxCount.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionMaxCount, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientConnectionMaxCount) ? "5" : String.valueOf(clientConnectionMaxCount);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 연결 폴 지원자 수행 주기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 연결 폴 지원자 수행 주기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientConnectionPoolSupporterTimeInterval(
			SequencedProperties sourceSequencedProperties) throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionPoolSupporterTimeInterval, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientConnectionPoolSupporterTimeInterval = nativeValueConverterForClientConnectionPoolSupporterTimeInterval.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 연결 폴 지원자 수행 주기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 연결 폴 지원자 수행 주기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientConnectionPoolSupporterTimeInterval(
			SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionPoolSupporterTimeInterval, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트 연결 폴 지원자 수행 주기, 단위 ms, 디폴트 600000, min[" + nativeValueConverterForClientConnectionPoolSupporterTimeInterval.getMin() + "], max[" + nativeValueConverterForClientConnectionPoolSupporterTimeInterval.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionPoolSupporterTimeInterval, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientConnectionPoolSupporterTimeInterval) ? "600000"
				: String.valueOf(clientConnectionPoolSupporterTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 연결 폴로부터 연결을 얻기 위한 재 시도 간격을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 연결 폴로부터 연결을 얻기 위한 재 시도 간격 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForRetryIntervaTimeToGetConnection(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToGetConnection, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientRetryIntervaTimeToGetConnection = nativeValueConverterForRetryIntervaTimeToGetConnection.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 연결 폴로부터 연결을 얻기 위한 재 시도 간격 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 연결 폴로부터 연결을 얻기 위한 재 시도 간격 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForRetryIntervaTimeToGetConnection(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToGetConnection, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "연결 폴로부터 연결을 얻기 위한 재 시도 간격, 단위 nanoseconds, 디폴트 5000, min[" + nativeValueConverterForRetryIntervaTimeToGetConnection.getMin() + "], max["+ nativeValueConverterForRetryIntervaTimeToGetConnection.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToGetConnection, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientRetryIntervaTimeToGetConnection) ? "5000"
				: String.valueOf(clientRetryIntervaTimeToGetConnection);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForAliveTimePerWrapBuffer(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForAliveTimePerWrapBuffer, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientAsynAliveTimePerWrapBuffer = nativeValueConverterForAliveTimePerWrapBuffer.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간을 얻기 위한 재 시도 간격 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForAliveTimePerWrapBuffer(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForAliveTimePerWrapBuffer, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간, 단위 nanoseconds, 디폴트 400, min["+ nativeValueConverterForAliveTimePerWrapBuffer.getMin() + "], max["+ nativeValueConverterForAliveTimePerWrapBuffer.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForAliveTimePerWrapBuffer, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientAsynAliveTimePerWrapBuffer) ? "400"
				: String.valueOf(clientAsynAliveTimePerWrapBuffer);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForRetryIntervaTimeToAddInputMessage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToAddInputMessage, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientAsynRetryIntervaTimeToAddInputMessage = nativeValueConverterForRetryIntervaTimeToAddInputMessage.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForRetryIntervaTimeToAddInputMessage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToAddInputMessage, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격, 단위 nanoseconds, 디폴트 400, min["+ nativeValueConverterForRetryIntervaTimeToAddInputMessage.getMin() + ", max[" + nativeValueConverterForRetryIntervaTimeToAddInputMessage.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToAddInputMessage, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientAsynRetryIntervaTimeToAddInputMessage) ? "400"
				: String.valueOf(clientAsynRetryIntervaTimeToAddInputMessage);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 비동기+공유 연결 1개당 메일함 갯수를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 비동기+공유 연결 1개당 메일함 갯수 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientMailboxCountPerAsynShareConnection(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMailboxCountPerAsynShareConnection, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientMailboxCountPerAsynShareConnection = nativeValueConverterForClientMailboxCountPerAsynShareConnection.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 비동기+공유 연결 1개당 메일함 갯수 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 비동기+공유 연결 1개당 메일함 갯수 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientMailboxCountPerAsynShareConnection(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMailboxCountPerAsynShareConnection, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "비동기+공유 연결 1개당 메일함 갯수, default value[2], min[" + nativeValueConverterForClientMailboxCountPerAsynShareConnection.getMin() + "], max[" + nativeValueConverterForClientMailboxCountPerAsynShareConnection.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMailboxCountPerAsynShareConnection, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientMailboxCountPerAsynShareConnection) ? "2"
				: String.valueOf(clientMailboxCountPerAsynShareConnection);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientAsynInputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynInputMessageQueueCapacity, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		

		try {
			clientAsynInputMessageQueueCapacity = nativeValueConverterForClientAsynInputMessageQueueCapacity.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientAsynInputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynInputMessageQueueCapacity, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[" + nativeValueConverterForClientAsynInputMessageQueueCapacity.getMin() + "], max["+ nativeValueConverterForClientAsynInputMessageQueueCapacity.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynInputMessageQueueCapacity, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientAsynInputMessageQueueCapacity) ? "10"
				: String.valueOf(clientAsynInputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientAsynOutputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynOutputMessageQueueCapacity, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientAsynOutputMessageQueueCapacity = nativeValueConverterForClientAsynOutputMessageQueueCapacity.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientAsynOutputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynOutputMessageQueueCapacity, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[" + nativeValueConverterForClientAsynOutputMessageQueueCapacity.getMin() +"], max[" + nativeValueConverterForClientAsynOutputMessageQueueCapacity.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynOutputMessageQueueCapacity, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientAsynOutputMessageQueueCapacity) ? "10"
				: String.valueOf(clientAsynOutputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 selector 를 깨우는 주기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 클라이언트 selector 를 깨우는 주기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */	
	public void fromPropertiesForClientSelectorWakeupInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientSelectorWakeupInterval, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	

		try {
			clientSelectorWakeupInterval = nativeValueConverterForClientSelectorWakeupInterval.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=")
					.append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 클라이언트 selector 를 깨우는 주기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 클라이언트 selector 를 깨우는 주기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForClientSelectorWakeupInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientSelectorWakeupInterval, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "클라이언트 selector 를 깨우는 주기. 단위 ms, default value[10], min[" + nativeValueConverterForClientSelectorWakeupInterval.getMin() + "], max[" + nativeValueConverterForClientSelectorWakeupInterval.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientSelectorWakeupInterval, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == clientSelectorWakeupInterval) ? "10" : String.valueOf(clientSelectorWakeupInterval);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	/**
	 * @return 서버 호스트 주소
	 */
	public String getServerHost() {
		return serverHost;
	}

	/**
	 * 지정한 서버 호스트 주소 값을 저장한다.
	 * 
	 * @param serverHost 서버 호스트 주소
	 */
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	/**
	 * @return 서버 포트 번호
	 */
	public Integer getServerPort() {
		return serverPort;
	}

	/**
	 * 지정한 서버 포트 번호 값을 저장한다.
	 * @param serverPort 서버 포트 번호
	 */
	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @return 바이트 오더
	 */
	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	/**
	 * 지정한 바이트 오더 값을 저장한다.
	 * @param byteOrder 바이트 오더
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/**
	 * @return 문자셋
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * 지정한 문자셋 값을 저장한다. 
	 * @param charset 문자셋
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * @return 메시지 프로토콜 유형
	 */
	public MessageProtocolType getMessageProtocolType() {
		return messageProtocolType;
	}

	/**
	 * 지정한 메시지 프로토콜 유형 값을 저장한다.
	 * @param messageProtocolType 메시지 프로토콜 유형
	 */
	public void setMessageProtocolType(MessageProtocolType messageProtocolType) {
		this.messageProtocolType = messageProtocolType;
	}

	/**
	 * @return 클라이언트 모니터닝 주기
	 */
	public Long getClientMonitorTimeInterval() {
		return clientMonitorTimeInterval;
	}

	/**
	 * 지정한 클라이언트 모니터닝 주기 값을 저장한다.
	 * @param clientMonitorTimeInterval 클라이언트 모니터닝 주기
	 */
	public void setClientMonitorTimeInterval(Long clientMonitorTimeInterval) {
		this.clientMonitorTimeInterval = clientMonitorTimeInterval;
	}

	/**
	 * @return 클라이언트 랩버퍼 다이렉트 여부
	 */
	public Boolean getWhetherClientWrapBufferIsDirect() {
		return whetherClientWrapBufferIsDirect;
	}

	/**
	 * 지정한 클라이언트 랩버퍼 다이렉트 여부 값을 저장한다.
	 * @param whetherClientWrapBufferIsDirect 클라이언트 랩버퍼 다이렉트 여부
	 */
	public void setWhetherClientWrapBufferIsDirect(Boolean whetherClientWrapBufferIsDirect) {
		this.whetherClientWrapBufferIsDirect = whetherClientWrapBufferIsDirect;
	}

	/**
	 * @return 메시지당 클라이언트 랩버퍼 최대 갯수 
	 */
	public Integer getClientWrapBufferMaxCntPerMessage() {
		return clientWrapBufferMaxCntPerMessage;
	}

	/**
	 * 지정한 메시지당 클라이언트 랩버퍼 최대 갯수 값을 저장한다.
	 * @param clientWrapBufferMaxCntPerMessage 메시지당 클라이언트 랩버퍼 최대 갯수
	 */
	public void setClientWrapBufferMaxCntPerMessage(Integer clientWrapBufferMaxCntPerMessage) {
		this.clientWrapBufferMaxCntPerMessage = clientWrapBufferMaxCntPerMessage;
	}

	/**
	 * @return 클라이언트 랩버퍼 크기
	 */
	public Integer getClientWrapBufferSize() {
		return clientWrapBufferSize;
	}

	/**
	 * 지정한 클라이언트 랩버퍼 크기 값을 저장한다.
	 * @param clientWrapBufferSize 클라이언트 랩버퍼 크기
	 */
	public void setClientWrapBufferSize(Integer clientWrapBufferSize) {
		this.clientWrapBufferSize = clientWrapBufferSize;
	}

	/**
	 * @return 클라이언트 랩버퍼 폴 크기
	 */
	public Integer getClientWrapBufferPoolSize() {
		return clientWrapBufferPoolSize;
	}

	/**
	 * 지정한 클라이언트 랩버퍼 폴 크기 값을 저장한다.
	 * @param clientWrapBufferPoolSize 클라이언트 랩버퍼 폴 크기
	 */
	public void setClientWrapBufferPoolSize(Integer clientWrapBufferPoolSize) {
		this.clientWrapBufferPoolSize = clientWrapBufferPoolSize;
	}

	/**
	 * @return 클라이언트 연결 유형
	 */
	public ClientConnectionType getClientConnectionType() {
		return clientConnectionType;
	}

	/**
	 * 지정한 클라이언트 연결 유형 값을 저장한다.
	 * @param clientConnectionType 클라이언트 연결 유형
	 */
	public void setClientConnectionType(ClientConnectionType clientConnectionType) {
		this.clientConnectionType = clientConnectionType;
	}

	/**
	 * @return 클라이언트 연결 타임아웃
	 */
	public Long getClientConnectionTimeout() {
		return clientConnectionTimeout;
	}

	/**
	 * 지정한 클라이언트 연결 타임아웃 값을 저장한다.
	 * @param clientConnectionTimeout 클라이언트 연결 타임아웃
	 */
	public void setClientConnectionTimeout(Long clientConnectionTimeout) {
		this.clientConnectionTimeout = clientConnectionTimeout;
	}

	/**
	 * @return 클라이언트 연결 갯수
	 */
	public Integer getClientConnectionCount() {
		return clientConnectionCount;
	}

	/**
	 * 지정한 클라이언트 연결 갯수 값을 저장한다.
	 * @param clientConnectionCount 클라이언트 연결 갯수
	 */	
	public void setClientConnectionCount(Integer clientConnectionCount) {
		this.clientConnectionCount = clientConnectionCount;
	}

	/**
	 * @return 클라이언트 연결 최대 갯수
	 */
	public Integer getClientConnectionMaxCount() {
		return clientConnectionMaxCount;
	}

	/**
	 * 지정한 클라이언트 연결 최대 갯수 값을 저장한다.
	 * @param clientConnectionMaxCount 클라이언트 연결 최대 갯수
	 */
	public void setClientConnectionMaxCount(Integer clientConnectionMaxCount) {
		this.clientConnectionMaxCount = clientConnectionMaxCount;
	}

	/**
	 * @return 클라이언트 연결 폴 지원자 수행 주기
	 */
	public Long getClientConnectionPoolSupporterTimeInterval() {
		return clientConnectionPoolSupporterTimeInterval;
	}

	/**
	 * 지정한 클라이언트 연결 폴 지원자 수행 주기 값을 저장한다.
	 * @param clientConnectionPoolSupporterTimeInterval 클라이언트 연결 폴 지원자 수행 주기
	 */
	public void setClientConnectionPoolSupporterTimeInterval(Long clientConnectionPoolSupporterTimeInterval) {
		this.clientConnectionPoolSupporterTimeInterval = clientConnectionPoolSupporterTimeInterval;
	}

	/**
	 * @return 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간
	 */
	public Long getClientAsynAliveTimePerWrapBuffer() {
		return clientAsynAliveTimePerWrapBuffer;
	}

	/**
	 * 지정한 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간 값을 저장한다.
	 * @param clientAsynAliveTimePerWrapBuffer 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간
	 */
	public void setClientAsynAliveTimePerWrapBuffer(Long clientAsynAliveTimePerWrapBuffer) {
		this.clientAsynAliveTimePerWrapBuffer = clientAsynAliveTimePerWrapBuffer;
	}

	/**
	 * @return 비동기+공유 연결 1개당 메일함 갯수
	 */
	public Integer getClientMailboxCountPerAsynShareConnection() {
		return clientMailboxCountPerAsynShareConnection;
	}

	/**
	 * 지정한 비동기+공유 연결 1개당 메일함 갯수 값을 저장한다.
	 * @param clientMailboxCountPerAsynShareConnection 비동기+공유 연결 1개당 메일함 갯수
	 */
	public void setClientMailboxCountPerAsynShareConnection(Integer clientMailboxCountPerAsynShareConnection) {
		this.clientMailboxCountPerAsynShareConnection = clientMailboxCountPerAsynShareConnection;
	}

	/*
	 * public Integer getClientAsynExecutorPoolSize() { return
	 * clientAsynExecutorPoolSize; }
	 * 
	 * 
	 * public void setClientAsynExecutorPoolSize(Integer clientAsynExecutorPoolSize)
	 * { this.clientAsynExecutorPoolSize = clientAsynExecutorPoolSize; }
	 */

	/**
	 * @return 클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기
	 */
	public Integer getClientAsynInputMessageQueueCapacity() {
		return clientAsynInputMessageQueueCapacity;
	}

	/**
	 * 지정한 클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기 값을 저장한다.
	 * @param clientAsynInputMessageQueueCapacity 클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기
	 */
	public void setClientAsynInputMessageQueueCapacity(Integer clientAsynInputMessageQueueCapacity) {
		this.clientAsynInputMessageQueueCapacity = clientAsynInputMessageQueueCapacity;
	}

	/**
	 * @return 클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기
	 */
	public Integer getClientAsynOutputMessageQueueCapacity() {
		return clientAsynOutputMessageQueueCapacity;
	}

	/**
	 * 지정한 클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 값을 저장한다.
	 * @param clientAsynOutputMessageQueueCapacity 클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기
	 */
	public void setClientAsynOutputMessageQueueCapacity(Integer clientAsynOutputMessageQueueCapacity) {
		this.clientAsynOutputMessageQueueCapacity = clientAsynOutputMessageQueueCapacity;
	}

	/**
	 * @return 클라이언트 selector 를 깨우는 주기
	 */
	public Long getClientSelectorWakeupInterval() {
		return clientSelectorWakeupInterval;
	}

	/**
	 * 지정한 클라이언트 selector 를 깨우는 주기 값을 저장한다.
	 * @param clientSelectorWakeupInterval 클라이언트 selector 를 깨우는 주기
	 */
	public void setClientSelectorWakeupInterval(Long clientSelectorWakeupInterval) {
		this.clientSelectorWakeupInterval = clientSelectorWakeupInterval;
	}

	/**
	 * @return 연결 폴로부터 연결을 얻기 위한 재 시도 간격
	 */
	public Long getClientRetryIntervaTimeToGetConnection() {
		return clientRetryIntervaTimeToGetConnection;
	}

	/**
	 * 지정한 연결 폴로부터 연결을 얻기 위한 재 시도 간격 값을 저장한다.
	 * @param clientRetryIntervaTimeToGetConnection 연결 폴로부터 연결을 얻기 위한 재 시도 간격
	 */
	public void setClientRetryIntervaTimeToGetConnection(Long clientRetryIntervaTimeToGetConnection) {
		this.clientRetryIntervaTimeToGetConnection = clientRetryIntervaTimeToGetConnection;
	}

	/**
	 * 
	 * @return 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격
	 */
	public Long getClientAsynRetryIntervaTimeToAddInputMessage() {
		return clientAsynRetryIntervaTimeToAddInputMessage;
	}

	/**
	 * 지정한 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격 값을 저장한다.
	 * 
	 * @param clientAsynRetryIntervaTimeToAddInputMessage 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격
	 */
	public void setClientAsynRetryIntervaTimeToAddInputMessage(Long clientAsynRetryIntervaTimeToAddInputMessage) {
		this.clientAsynRetryIntervaTimeToAddInputMessage = clientAsynRetryIntervaTimeToAddInputMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractProjectPartConfiguration [serverHost=");
		builder.append(serverHost);
		builder.append(", serverPort=");
		builder.append(serverPort);
		builder.append(", byteOrder=");
		builder.append(byteOrder);
		builder.append(", charset=");
		builder.append(charset);
		builder.append(", messageProtocolType=");
		builder.append(messageProtocolType);
		builder.append(", clientMonitorTimeInterval=");
		builder.append(clientMonitorTimeInterval);
		builder.append(", whetherClientWrapBufferIsDirect=");
		builder.append(whetherClientWrapBufferIsDirect);
		builder.append(", clientWrapBufferMaxCntPerMessage=");
		builder.append(clientWrapBufferMaxCntPerMessage);
		builder.append(", clientWrapBufferSize=");
		builder.append(clientWrapBufferSize);
		builder.append(", clientWrapBufferPoolSize=");
		builder.append(clientWrapBufferPoolSize);
		builder.append(", clientConnectionType=");
		builder.append(clientConnectionType);
		builder.append(", clientConnectionTimeout=");
		builder.append(clientConnectionTimeout);
		builder.append(", clientConnectionCount=");
		builder.append(clientConnectionCount);
		builder.append(", clientConnectionMaxCount=");
		builder.append(clientConnectionMaxCount);
		builder.append(", clientConnectionPoolSupporterTimeInterval=");
		builder.append(clientConnectionPoolSupporterTimeInterval);
		builder.append(", clientRetryIntervaTimeToGetConnection=");
		builder.append(clientRetryIntervaTimeToGetConnection);
		builder.append(", clientAsynAliveTimePerWrapBuffer=");
		builder.append(clientAsynAliveTimePerWrapBuffer);
		builder.append(", clientAsynRetryIntervaTimeToAddInputMessage=");
		builder.append(clientAsynRetryIntervaTimeToAddInputMessage);
		builder.append(", clientMailboxCountPerAsynShareConnection=");
		builder.append(clientMailboxCountPerAsynShareConnection);
		builder.append(", clientAsynInputMessageQueueCapacity=");
		builder.append(clientAsynInputMessageQueueCapacity);
		builder.append(", clientAsynOutputMessageQueueCapacity=");
		builder.append(clientAsynOutputMessageQueueCapacity);
		builder.append(", clientSelectorWakeupInterval=");
		builder.append(clientSelectorWakeupInterval);
		builder.append("]");
		return builder.toString();
	}
	
	
}
