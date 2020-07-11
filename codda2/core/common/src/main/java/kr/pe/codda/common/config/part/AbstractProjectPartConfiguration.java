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
import kr.pe.codda.common.type.KeyTypeOfConfieFile;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public abstract class AbstractProjectPartConfiguration implements PartConfigurationIF {
	protected final Logger log = Logger.getLogger(AbstractProjectPartConfiguration.class.getName());

	/************* common 변수 시작 ******************/
	public static final String itemIDForServerHost = "common.host";
	private String serverHost = null;

	public static final String itemIDForServerPort = "common.port";
	private Integer serverPort = null;

	public static final String itemIDForByteOrder = "common.byteorder";
	private ByteOrder byteOrder = null;

	public static final String itemIDForCharset = "common.charset";
	private Charset charset = null;

	public static final String itemIDForMessageProtocolType = "common.message_protocol_type";
	private MessageProtocolType messageProtocolType = null;
	/************* common 변수 종료 ******************/

	/************* client 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	public static final String itemIDForClientMonitorTimeInterval = "client.monitor.time_interval";
	private Long clientMonitorTimeInterval = null;
	/***** 모니터 환경 변수 종료 *****/

	public static final String itemIDForWhetherClientWrapBufferIsDirect = "client.wrap_buffer.isdirect";
	private Boolean whetherClientWrapBufferIsDirect = null;

	public static final String itemIDForClientWrapBufferMaxCntPerMessage = "client.wrap_buffer.max_cnt_per_message";
	private Integer clientWrapBufferMaxCntPerMessage = null;

	public static final String itemIDForClientWrapBufferSize = "client.wrap_buffer.size";
	private Integer clientWrapBufferSize = null;

	public static final String itemIDForClientWrapBufferPoolSize = "client.wrap_buffer.pool_size";
	private Integer clientWrapBufferPoolSize = null;

	/***** 연결 클래스 관련 환경 변수 시작 *****/
	/** 연결 종류 */
	public static final String itemIDForClientConnectionType = "client.connection.type";
	private ClientConnectionType clientConnectionType = null;

	/** 연결 타임 아웃 시간 */
	public static final String itemIDForClientConnectionTimeout = "client.connection.timeout";
	private Long clientConnectionTimeout = null;

	/** 연결 갯수 */
	public static final String itemIDForClientConnectionCount = "client.connection.count";
	private Integer clientConnectionCount = null;

	/** 연결 최대 갯수 */
	public static final String itemIDForClientConnectionMaxCount = "client.connection.max_count";
	private Integer clientConnectionMaxCount = null;

	/** 연결 폴 지원자 활동 주기 */
	public static final String itemIDForClientConnectionPoolSupporterTimeInterval = "client.connection.pool.supportor.time_interval";
	private Long clientConnectionPoolSupporterTimeInterval = null;

	/** 연결 폴로무터 연결을 얻기 위한 재 시도 간격 */
	public static final String itemIDForRetryIntervaTimeToGetConnection = "client.connection.pool.retry_interval_time_to_get_connection";
	private Long clientRetryIntervaTimeToGetConnection = null;

	/***** 연결 클래스 관련 환경 변수 종료 *****/

	/** 비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간 */
	public static final String itemIDForAliveTimePerWrapBuffer = "client.asyn.alive_time_per_wrapbuffer";
	private Long clientAsynAliveTimePerWrapBuffer = null;

	/** 비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격 */
	public static final String itemIDForRetryIntervaTimeToAddInputMessage = "client.asyn.retry_interval_time_to_add_input_message";
	private Long clientAsynRetryIntervaTimeToAddInputMessage = null;

	/** 비동기+공유 연결의 개인 메일함 갯수 */
	public static final String itemIDForClientMailboxCountPerAsynShareConnection = "client.asyn.share_connection.mailbox_count";
	private Integer clientMailboxCountPerAsynShareConnection = null;

	/** 비동기 출력 메시지 처리자 쓰레드 갯수 */
	// private Integer clientAsynExecutorPoolSize = null;

	/** 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기 */
	public static final String itemIDForClientAsynInputMessageQueueCapacity = "client.asyn.input_message_queue_capacity";
	private Integer clientAsynInputMessageQueueCapacity = null;

	/** 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 */
	public static final String itemIDForClientAsynOutputMessageQueueCapacity = "client.asyn.output_message_queue_capacity";
	private Integer clientAsynOutputMessageQueueCapacity = null;

	/** 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기 */
	public static final String itemIDForClientSelectorWakeupInterval = "client.asyn.selector.wakeup_interval";
	private Long clientSelectorWakeupInterval = null;

	/************* client 변수 종료 ******************/

	abstract public String getPrefixBeforeItemID();

	@Override
	public void checkForDependencies() throws PartConfigurationException {
		if (clientConnectionMaxCount < clientConnectionCount) {
			String clientConnectionMaxCountItemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
					getPrefixBeforeItemID(), itemIDForClientConnectionMaxCount, KeyTypeOfConfieFile.VALUE);

			String clientConnectionCountItemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
					getPrefixBeforeItemID(), itemIDForClientConnectionCount, KeyTypeOfConfieFile.VALUE);
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

	public void fromPropertiesForServerHost(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = 
				RunningProjectConfiguration.buildKeyOfConfigFile(
						getPrefixBeforeItemID(), itemIDForServerHost, KeyTypeOfConfieFile.VALUE);
		

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningNoTrimString nativeValueConverter = new GeneralConverterReturningNoTrimString();

		try {

			serverHost = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForServerHost(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerHost, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트에서 접속할 서버 주소, default value[localhost]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerHost, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverHost) ? "localhost" : serverHost;
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForServerPort(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerPort, KeyTypeOfConfieFile.VALUE);
		

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1024, Integer.MAX_VALUE);

		try {
			serverPort = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForServerPort(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerPort, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "포트 번호, default value[9090], min[1024], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForServerPort, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverPort) ? "9090" : String.valueOf(serverPort);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForByteOrder(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForByteOrder, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		SetTypeConverterReturningByteOrder nativeValueConverter = new SetTypeConverterReturningByteOrder();

		try {

			byteOrder = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForByteOrder(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForByteOrder, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "바이트 오더, default value[LITTLE_ENDIAN], the byteorder set[BIG_ENDIAN, LITTLE_ENDIAN]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForByteOrder, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == byteOrder) ? ByteOrder.LITTLE_ENDIAN.toString() : byteOrder.toString();
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForCharset(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForCharset, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningCharset nativeValueConverter = new GeneralConverterReturningCharset();

		try {

			charset = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForCharset(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForCharset, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "문자셋, default value[UTF-8]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForCharset, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == charset) ? "UTF-8" : charset.name();
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForMessageProtocolType(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForMessageProtocolType, KeyTypeOfConfieFile.VALUE);
		

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		SetTypeConverterReturningMessageProtocolType nativeValueConverter = new SetTypeConverterReturningMessageProtocolType();

		try {

			messageProtocolType = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForMessageProtocolType(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForMessageProtocolType, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "메시지 프로토콜, DHB:교차 md5 헤더+바디, DJSON:길이+존슨문자열, THB:길이+바디, default value[DHB], the message protocol set[DJSON, DHB, THB]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForMessageProtocolType, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == messageProtocolType) ? MessageProtocolType.DHB.name() : messageProtocolType.name();
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientMonitorTimeInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMonitorTimeInterval, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				1000L, (long) Integer.MAX_VALUE);

		try {

			clientMonitorTimeInterval = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientMonitorTimeInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMonitorTimeInterval, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 모니터링 주기, 단위 ms, default value[5000], min[1000], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMonitorTimeInterval, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientMonitorTimeInterval) ? "5000" : String.valueOf(clientMonitorTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForWhetherClientWrapBufferIsDirect(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherClientWrapBufferIsDirect, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		SetTypeConverterReturningBoolean nativeValueConverter = new SetTypeConverterReturningBoolean();

		try {

			whetherClientWrapBufferIsDirect = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForWhetherClientWrapBufferIsDirect(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherClientWrapBufferIsDirect, KeyTypeOfConfieFile.DESC);
		
		String itemDescValue = "whether client wrap buffer is direct, default value[true], the boolean set[false, true]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherClientWrapBufferIsDirect, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == whetherClientWrapBufferIsDirect) ? Boolean.TRUE.toString()
				: whetherClientWrapBufferIsDirect.toString();
		targetSequencedProperties.put(itemKey, itemValue);
	}

	
	public void fromPropertiesForClientWrapBufferMaxCntPerMessage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferMaxCntPerMessage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1, Integer.MAX_VALUE);

		try {

			clientWrapBufferMaxCntPerMessage = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientWrapBufferMaxCntPerMessage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferMaxCntPerMessage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트에서 1개 메시지당 할당 받을 수 있는 랩 버퍼 최대 갯수, default value[1000], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferMaxCntPerMessage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientWrapBufferMaxCntPerMessage) ? "1000"
				: String.valueOf(clientWrapBufferMaxCntPerMessage);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientWrapBufferSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferSize, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1024, Integer.MAX_VALUE);

		try {

			clientWrapBufferSize = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientWrapBufferSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferSize, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 랩 버퍼 크기, 단위 byte, default value[4096], min[1024], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferSize, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientWrapBufferSize) ? "4096" : String.valueOf(clientWrapBufferSize);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientWrapBufferPoolSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferPoolSize, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1, Integer.MAX_VALUE);

		try {

			clientWrapBufferPoolSize = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientWrapBufferPoolSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferPoolSize, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 랩 버퍼 폴 크기, default value[1000], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientWrapBufferPoolSize, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientWrapBufferPoolSize) ? "1000" : String.valueOf(clientWrapBufferPoolSize);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientConnectionType(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionType, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		SetTypeConverterReturningConnectionType nativeValueConverter = new SetTypeConverterReturningConnectionType();

		try {

			clientConnectionType = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientConnectionType(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionType, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 연결 종류, ASYN:비동기, SYNC:동기, default value[ASYN], the connection type set[ASYN, SYNC]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionType, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientConnectionType) ? ClientConnectionType.ASYN.name()
				: clientConnectionType.name();
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientConnectionTimeout(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionTimeout, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				1000L, (long) Integer.MAX_VALUE);

		try {

			clientConnectionTimeout = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientConnectionTimeout(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionTimeout, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "연결 타임아웃, 단위 ms, default value[5000], min[1000], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionTimeout, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientConnectionTimeout) ? "5000" : String.valueOf(clientConnectionTimeout);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientConnectionCount(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionCount, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1, Integer.MAX_VALUE);

		try {

			clientConnectionCount = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientConnectionCount(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionCount, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "연결 갯수, default value[5], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionCount, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientConnectionCount) ? "5" : String.valueOf(clientConnectionCount);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientConnectionMaxCount(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionMaxCount, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1, Integer.MAX_VALUE);

		try {

			clientConnectionMaxCount = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientConnectionMaxCount(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionMaxCount, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "최대 연결 갯수, default value[5], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionMaxCount, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientConnectionMaxCount) ? "5" : String.valueOf(clientConnectionMaxCount);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientConnectionPoolSupporterTimeInterval(
			SequencedProperties sourceSequencedProperties) throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionPoolSupporterTimeInterval, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				1000L, (long) Integer.MAX_VALUE);

		try {

			clientConnectionPoolSupporterTimeInterval = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientConnectionPoolSupporterTimeInterval(
			SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionPoolSupporterTimeInterval, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 연결 지원자 수행 주기, 단위 ms, 디폴트 600000, 최소 1000, 최대 Integer.MAX_VALUE";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientConnectionPoolSupporterTimeInterval, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientConnectionPoolSupporterTimeInterval) ? "600000"
				: String.valueOf(clientConnectionPoolSupporterTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForRetryIntervaTimeToGetConnection(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToGetConnection, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				1000L, (long) Integer.MAX_VALUE);

		try {

			clientRetryIntervaTimeToGetConnection = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForRetryIntervaTimeToGetConnection(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToGetConnection, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "연결 폴로부터 연결을 얻기 위한 재 시도 간격, 단위 nanoseconds, 디폴트 5000, 최소 1000, 최대 Integer.MAX_VALUE";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToGetConnection, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientRetryIntervaTimeToGetConnection) ? "5000"
				: String.valueOf(clientRetryIntervaTimeToGetConnection);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForAliveTimePerWrapBuffer(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForAliveTimePerWrapBuffer, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				100L, (long) Integer.MAX_VALUE);

		try {

			clientAsynAliveTimePerWrapBuffer = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForAliveTimePerWrapBuffer(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForAliveTimePerWrapBuffer, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "비동기에서 송신할 데이터가 담긴 랩 버퍼당 생존 시간, 단위 nanoseconds, 디폴트 400, 최소 100, 최대 Integer.MAX_VALUE";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForAliveTimePerWrapBuffer, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientAsynAliveTimePerWrapBuffer) ? "400"
				: String.valueOf(clientAsynAliveTimePerWrapBuffer);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForRetryIntervaTimeToAddInputMessage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToAddInputMessage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				100L, (long) Integer.MAX_VALUE);

		try {

			clientAsynRetryIntervaTimeToAddInputMessage = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForRetryIntervaTimeToAddInputMessage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToAddInputMessage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "비동기에서 입력 메시지 스트림 큐에 입력 메시지 스트림을 다시 추가하는 간격, 단위 nanoseconds, 디폴트 400, 최소 100, 최대 Integer.MAX_VALUE";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForRetryIntervaTimeToAddInputMessage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientAsynRetryIntervaTimeToAddInputMessage) ? "400"
				: String.valueOf(clientAsynRetryIntervaTimeToAddInputMessage);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientMailboxCountPerAsynShareConnection(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMailboxCountPerAsynShareConnection, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1, Integer.MAX_VALUE);

		try {

			clientMailboxCountPerAsynShareConnection = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientMailboxCountPerAsynShareConnection(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMailboxCountPerAsynShareConnection, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "비동기+공유 연결 1개당 메일함 갯수, default value[2], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientMailboxCountPerAsynShareConnection, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientMailboxCountPerAsynShareConnection) ? "2"
				: String.valueOf(clientMailboxCountPerAsynShareConnection);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientAsynInputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynInputMessageQueueCapacity, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1, Integer.MAX_VALUE);

		try {

			clientAsynInputMessageQueueCapacity = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientAsynInputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynInputMessageQueueCapacity, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynInputMessageQueueCapacity, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientAsynInputMessageQueueCapacity) ? "10"
				: String.valueOf(clientAsynInputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientAsynOutputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynOutputMessageQueueCapacity, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(
				1, Integer.MAX_VALUE);

		try {

			clientAsynOutputMessageQueueCapacity = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientAsynOutputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynOutputMessageQueueCapacity, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientAsynOutputMessageQueueCapacity, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientAsynOutputMessageQueueCapacity) ? "10"
				: String.valueOf(clientAsynOutputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForClientSelectorWakeupInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientSelectorWakeupInterval, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(
				1L, (long) Integer.MAX_VALUE);

		try {
			clientSelectorWakeupInterval = nativeValueConverter.valueOf(itemValue);
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

	public void toPropertiesForClientSelectorWakeupInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientSelectorWakeupInterval, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 selector 를 깨우는 주기. 단위 ms, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForClientSelectorWakeupInterval, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == clientSelectorWakeupInterval) ? "10" : String.valueOf(clientSelectorWakeupInterval);
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public MessageProtocolType getMessageProtocolType() {
		return messageProtocolType;
	}

	public void setMessageProtocolType(MessageProtocolType messageProtocolType) {
		this.messageProtocolType = messageProtocolType;
	}

	public Long getClientMonitorTimeInterval() {
		return clientMonitorTimeInterval;
	}

	public void setClientMonitorTimeInterval(Long clientMonitorTimeInterval) {
		this.clientMonitorTimeInterval = clientMonitorTimeInterval;
	}

	public Boolean getWhetherClientWrapBufferIsDirect() {
		return whetherClientWrapBufferIsDirect;
	}

	public void setWhetherClientWrapBufferIsDirect(Boolean whetherClientWrapBufferIsDirect) {
		this.whetherClientWrapBufferIsDirect = whetherClientWrapBufferIsDirect;
	}

	public Integer getClientWrapBufferMaxCntPerMessage() {
		return clientWrapBufferMaxCntPerMessage;
	}

	public void setClientWrapBufferMaxCntPerMessage(Integer clientWrapBufferMaxCntPerMessage) {
		this.clientWrapBufferMaxCntPerMessage = clientWrapBufferMaxCntPerMessage;
	}

	public Integer getClientWrapBufferSize() {
		return clientWrapBufferSize;
	}

	public void setClientWrapBufferSize(Integer clientWrapBufferSize) {
		this.clientWrapBufferSize = clientWrapBufferSize;
	}

	public Integer getClientWrapBufferPoolSize() {
		return clientWrapBufferPoolSize;
	}

	public void setClientWrapBufferPoolSize(Integer clientWrapBufferPoolSize) {
		this.clientWrapBufferPoolSize = clientWrapBufferPoolSize;
	}

	public ClientConnectionType getClientConnectionType() {
		return clientConnectionType;
	}

	public void setClientConnectionType(ClientConnectionType clientConnectionType) {
		this.clientConnectionType = clientConnectionType;
	}

	public Long getClientConnectionTimeout() {
		return clientConnectionTimeout;
	}

	public void setClientConnectionTimeout(Long clientConnectionTimeout) {
		this.clientConnectionTimeout = clientConnectionTimeout;
	}

	public Integer getClientConnectionCount() {
		return clientConnectionCount;
	}

	public void setClientConnectionCount(Integer clientConnectionCount) {
		this.clientConnectionCount = clientConnectionCount;
	}

	public Integer getClientConnectionMaxCount() {
		return clientConnectionMaxCount;
	}

	public void setClientConnectionMaxCount(Integer clientConnectionMaxCount) {
		this.clientConnectionMaxCount = clientConnectionMaxCount;
	}

	public Long getClientConnectionPoolSupporterTimeInterval() {
		return clientConnectionPoolSupporterTimeInterval;
	}

	public void setClientConnectionPoolSupporterTimeInterval(Long clientConnectionPoolSupporterTimeInterval) {
		this.clientConnectionPoolSupporterTimeInterval = clientConnectionPoolSupporterTimeInterval;
	}

	public Long getClientAsynAliveTimePerWrapBuffer() {
		return clientAsynAliveTimePerWrapBuffer;
	}

	public void setClientAsynAliveTimePerWrapBuffer(Long clientAsynAliveTimePerWrapBuffer) {
		this.clientAsynAliveTimePerWrapBuffer = clientAsynAliveTimePerWrapBuffer;
	}

	public Integer getClientMailboxCountPerAsynShareConnection() {
		return clientMailboxCountPerAsynShareConnection;
	}

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

	public Integer getClientAsynInputMessageQueueCapacity() {
		return clientAsynInputMessageQueueCapacity;
	}

	public void setClientAsynInputMessageQueueCapacity(Integer clientAsynInputMessageQueueCapacity) {
		this.clientAsynInputMessageQueueCapacity = clientAsynInputMessageQueueCapacity;
	}

	public Integer getClientAsynOutputMessageQueueCapacity() {
		return clientAsynOutputMessageQueueCapacity;
	}

	public void setClientAsynOutputMessageQueueCapacity(Integer clientAsynOutputMessageQueueCapacity) {
		this.clientAsynOutputMessageQueueCapacity = clientAsynOutputMessageQueueCapacity;
	}

	public Long getClientSelectorWakeupInterval() {
		return clientSelectorWakeupInterval;
	}

	public void setClientSelectorWakeupInterval(Long clientSelectorWakeupInterval) {
		this.clientSelectorWakeupInterval = clientSelectorWakeupInterval;
	}

	public Long getClientRetryIntervaTimeToGetConnection() {
		return clientRetryIntervaTimeToGetConnection;
	}

	public void setClientRetryIntervaTimeToGetConnection(Long clientRetryIntervaTimeToGetConnection) {
		this.clientRetryIntervaTimeToGetConnection = clientRetryIntervaTimeToGetConnection;
	}

	public Long getClientAsynRetryIntervaTimeToAddInputMessage() {
		return clientAsynRetryIntervaTimeToAddInputMessage;
	}

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
