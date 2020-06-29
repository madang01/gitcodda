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
package kr.pe.codda.common.config2.part;

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
import kr.pe.codda.common.config2.ConfigurationIF;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ClientConnectionType;
import kr.pe.codda.common.type.GUIItemType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class ProjectPartConfiguration implements ConfigurationIF {
	private final Logger log = Logger.getLogger(ProjectPartConfiguration.class.getName());
	
	
	private final ProjectType projectType;
	private final String subProjectName;
	private final String prefexOfItemID;
	
	/************* common 변수 시작 ******************/	
	public static final String itemIDForServerHost = "common.host";
	private String serverHost = null;	
	
	public static final String itemIDForServerPort = "common.port";
	private Integer  serverPort = null;
	
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
	private Integer  clientConnectionCount = null;
	
	/** 연결 최대 갯수 */
	public static final String itemIDForClientConnectionMaxCount = "client.connection.max_count";
	private Integer  clientConnectionMaxCount = null;
	
	
	public static final String itemIDForClientConnectionPoolSupporterTimeInterval = "client.connection.pool.supportor.time_interval";
	private Long  clientConnectionPoolSupporterTimeInterval = null;
	
	/***** 연결 클래스 관련 환경 변수 종료 *****/	
	
	/** 비동기+공유 연결의 개인 메일함 갯수 */
	public static final String itemIDForClientMailboxCountPerAsynShareConnection = "client.asyn.share_connection.mailbox_count";
	private Integer  clientMailboxCountPerAsynShareConnection = null;
	
	/** 비동기 출력 메시지 처리자 쓰레드 갯수 */
	// private Integer  clientAsynExecutorPoolSize = null;
		
	/** 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기   */
	public static final String itemIDForClientAsynInputMessageQueueCapacity = "client.asyn.input_message_queue_capacity";
	private Integer  clientAsynInputMessageQueueCapacity = null;
	
	/** 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기   */
	public static final String itemIDForClientAsynOutputMessageQueueCapacity = "client.asyn.output_message_queue_capacity";
	private Integer  clientAsynOutputMessageQueueCapacity = null;
	
	/** 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기 */
	public static final String itemIDForClientSelectorWakeupInterval = "client.asyn.selector.wakeup_interval";
	private Long clientSelectorWakeupInterval = null;	
	/************* client 변수 종료 ******************/
	
	/************* server 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	public static final String itemIDForServerMonitorTimeInterval = "server.monitor.time_interval";
	private Long serverMonitorTimeInterval = null;
	/***** 모니터 환경 변수 종료 *****/
	
	public static final String itemIDForWhetherServerWrapBufferIsDirect = "server.wrap_buffer.isdirect";
	private Boolean whetherServerWrapBufferIsDirect = null;
	
	
	public static final String itemIDForServerWrapBufferMaxCntPerMessage = "server.wrap_buffer.max_cnt_per_message";
	private Integer serverWrapBufferMaxCntPerMessage = null;
	
	
	public static final String itemIDForServerWrapBufferSize = "server.wrap_buffer.size";
	private Integer serverWrapBufferSize = null;
	
	
	public static final String itemIDForServerWrapBufferPoolSize = "server.wrap_buffer.pool_size";
	private Integer serverWrapBufferPoolSize = null;
	
	public static final String itemIDForServerMaxClients = "server.max_clients";
	private Integer serverMaxClients = null;	
	
	
	/***** 서버 비동기 입출력 지원용 자원 시작 *****/
	public static final String itemIDForServerInputMessageQueueCapacity = "server.input_message_queue_capacity";
	private Integer  serverInputMessageQueueCapacity = null;
	
	
	public static final String itemIDForServerOutputMessageQueueCapacity = "server.output_message_queue_capacity";
	private Integer  serverOutputMessageQueueCapacity = null;
	/***** 서버 비동기 입출력 지원용 자원 종료 *****/
	
	/************* server 변수 종료 ******************/
	
	
	public ProjectPartConfiguration(ProjectType projectType, String subProjectName) {
		if (null == projectType) {
			throw new IllegalArgumentException("the parameter projectType is null");
		}
		
		this.projectType = projectType;
				
		if (this.projectType.equals(ProjectType.MAIN)) {
			prefexOfItemID = new StringBuilder("mainproject.").toString();
			this.subProjectName = null;
		} else {
			if (null == subProjectName) {
				throw new IllegalArgumentException("the parameter subProjectName is null");
			}
			
			this.subProjectName = subProjectName;
			prefexOfItemID = new StringBuilder("subproject.").append(subProjectName)
					.append(".").toString();
		}
	}

	@Override
	public void toValue(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		toValueForServerHost(sourceSequencedProperties);
		toValueForServerPort(sourceSequencedProperties);
		toValueForByteOrder(sourceSequencedProperties);
		toValueForCharset(sourceSequencedProperties);
		toValueForMessageProtocolType(sourceSequencedProperties);		
		
		toValueForClientMonitorTimeInterval(sourceSequencedProperties);
		toValueForWhetherClientWrapBufferIsDirect(sourceSequencedProperties);
		toValueForClientWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		toValueForClientWrapBufferSize(sourceSequencedProperties);
		toValueForClientWrapBufferPoolSize(sourceSequencedProperties);
		toValueForClientConnectionType(sourceSequencedProperties);
		toValueForClientConnectionTimeout(sourceSequencedProperties);
		toValueForClientConnectionCount(sourceSequencedProperties);
		toValueForClientConnectionMaxCount(sourceSequencedProperties);
		toValueForClientConnectionPoolSupporterTimeInterval(sourceSequencedProperties);
		toValueForClientMailboxCountPerAsynShareConnection(sourceSequencedProperties);
		toValueForClientAsynInputMessageQueueCapacity(sourceSequencedProperties);
		toValueForClientAsynOutputMessageQueueCapacity(sourceSequencedProperties);
		toValueForClientSelectorWakeupInterval(sourceSequencedProperties);
		
		
		toValueForServerMonitorTimeInterval(sourceSequencedProperties);
		toValueForWhetherServerWrapBufferIsDirect(sourceSequencedProperties);
		toValueForServerWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		toValueForServerWrapBufferSize(sourceSequencedProperties);
		toValueForServerWrapBufferPoolSize(sourceSequencedProperties);
		toValueForServerMaxClients(sourceSequencedProperties);
		toValueForServerInputMessageQueueCapacity(sourceSequencedProperties);
		toValueForServerOutputMessageQueueCapacity(sourceSequencedProperties);		
	}
	
	@Override
	public void checkForDependencies(SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, PartConfigurationException {
		if (clientConnectionMaxCount < clientConnectionCount) {
			String clientConnectionMaxCountItemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionMaxCount).append(".value").toString();
			String clientConnectionCountItemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionCount).append(".value").toString();
			String errorMessage = new StringBuilder().append("연결 갯수[key=")
					.append(clientConnectionCountItemKey)
					.append(", value=")
					.append(clientConnectionMaxCount)
					.append("]가 최대 연결 갯수[key=")
					.append(clientConnectionMaxCountItemKey)
					.append(", value=")
					.append(clientConnectionCount)
					.append("] 보다 큽니다").toString();
			throw new PartConfigurationException(clientConnectionCountItemKey, errorMessage);
		}
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
		toPropertiesForClientMailboxCountPerAsynShareConnection(targetSequencedProperties);
		toPropertiesForClientAsynInputMessageQueueCapacity(targetSequencedProperties);
		toPropertiesForClientAsynOutputMessageQueueCapacity(targetSequencedProperties);
		toPropertiesForClientSelectorWakeupInterval(targetSequencedProperties);
		
		
		toPropertiesForServerMonitorTimeInterval(targetSequencedProperties);
		toPropertiesForWhetherServerWrapBufferIsDirect(targetSequencedProperties);
		toPropertiesForServerWrapBufferMaxCntPerMessage(targetSequencedProperties);
		toPropertiesForServerWrapBufferSize(targetSequencedProperties);
		toPropertiesForServerWrapBufferPoolSize(targetSequencedProperties);
		toPropertiesForServerMaxClients(targetSequencedProperties);
		toPropertiesForServerInputMessageQueueCapacity(targetSequencedProperties);
		toPropertiesForServerOutputMessageQueueCapacity(targetSequencedProperties);
	}
	
	
	public void toValueForServerHost(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerHost).append(".value").toString();

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
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerHost(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerHost).append(".desc").toString();
		String itemDescValue = "클라이언트에서 접속할 서버 주소, default value[localhost]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerHost).append(".value").toString();
		String itemValue = (null == serverHost) ? "localhost" : serverHost;
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerHost).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}

	
	public void toValueForServerPort(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerPort).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1024, Integer.MAX_VALUE);
		
		try {
			serverPort = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerPort(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerPort).append(".desc").toString();
		String itemDescValue = "포트 번호, default value[9090], min[1024], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerPort).append(".value").toString();
		String itemValue = (null == serverPort) ? "9090" : String.valueOf(serverPort);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerPort).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForByteOrder(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForByteOrder).append(".value").toString();

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
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForByteOrder(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForByteOrder).append(".desc").toString();
		String itemDescValue = "바이트 오더, default value[LITTLE_ENDIAN], the byteorder set[BIG_ENDIAN, LITTLE_ENDIAN]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForByteOrder).append(".value").toString();
		String itemValue = (null == byteOrder) ? ByteOrder.LITTLE_ENDIAN.toString() : byteOrder.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForByteOrder).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForCharset(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForCharset).append(".value").toString();

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
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForCharset(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForCharset).append(".desc").toString();
		String itemDescValue = "문자셋, default value[UTF-8]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForCharset).append(".value").toString();
		String itemValue = (null == charset) ? "UTF-8" : charset.name();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForCharset).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	
	public void toValueForMessageProtocolType(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForMessageProtocolType).append(".value").toString();

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
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForMessageProtocolType(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForMessageProtocolType).append(".desc").toString();
		String itemDescValue = "메시지 프로토콜, DHB:교차 md5 헤더+바디, DJSON:길이+존슨문자열, THB:길이+바디, default value[DHB], the message protocol set[DJSON, DHB, THB]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForMessageProtocolType).append(".value").toString();
		String itemValue = (null == messageProtocolType) ? MessageProtocolType.DHB.name() : messageProtocolType.name();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForMessageProtocolType).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientMonitorTimeInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientMonitorTimeInterval).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(1000L, (long) Integer.MAX_VALUE);
		
		try {

			clientMonitorTimeInterval = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientMonitorTimeInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientMonitorTimeInterval).append(".desc").toString();
		String itemDescValue = "클라이언트 모니터링 주기, 단위 ms, default value[5000], min[1000], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientMonitorTimeInterval).append(".value").toString();
		String itemValue = (null == clientMonitorTimeInterval) ? "5000" : String.valueOf(clientMonitorTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientMonitorTimeInterval).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForWhetherClientWrapBufferIsDirect(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForWhetherClientWrapBufferIsDirect).append(".value").toString();

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
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForWhetherClientWrapBufferIsDirect(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForWhetherClientWrapBufferIsDirect).append(".desc").toString();
		String itemDescValue = "whether client wrap buffer is direct, default value[true], the boolean set[false, true]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForWhetherClientWrapBufferIsDirect).append(".value").toString();
		String itemValue = (null == whetherClientWrapBufferIsDirect) ? Boolean.TRUE.toString() : whetherClientWrapBufferIsDirect.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForWhetherClientWrapBufferIsDirect).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	
	public void toValueForClientWrapBufferMaxCntPerMessage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferMaxCntPerMessage).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			clientWrapBufferMaxCntPerMessage = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientWrapBufferMaxCntPerMessage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferMaxCntPerMessage).append(".desc").toString();
		String itemDescValue = "클라이언트에서 1개 메시지당 할당 받을 수 있는 랩 버퍼 최대 갯수, default value[1000], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferMaxCntPerMessage).append(".value").toString();
		String itemValue = (null == clientWrapBufferMaxCntPerMessage) ? "1000" : String.valueOf(clientWrapBufferMaxCntPerMessage);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferMaxCntPerMessage).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientWrapBufferSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferSize).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1024, Integer.MAX_VALUE);
		
		try {

			clientWrapBufferSize = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientWrapBufferSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferSize).append(".desc").toString();
		String itemDescValue = "클라이언트 랩 버퍼 크기, 단위 byte, default value[4096], min[1024], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferSize).append(".value").toString();
		String itemValue = (null == clientWrapBufferSize) ? "4096" : String.valueOf(clientWrapBufferSize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferSize).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	
	public void toValueForClientWrapBufferPoolSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferPoolSize).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			clientWrapBufferPoolSize = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientWrapBufferPoolSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferPoolSize).append(".desc").toString();
		String itemDescValue = "클라이언트 랩 버퍼 폴 크기, default value[1000], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferPoolSize).append(".value").toString();
		String itemValue = (null == clientWrapBufferPoolSize) ? "1000" : String.valueOf(clientWrapBufferPoolSize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientWrapBufferPoolSize).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	
	public void toValueForClientConnectionType(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionType).append(".value").toString();

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
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientConnectionType(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionType).append(".desc").toString();
		String itemDescValue = "클라이언트 연결 종류, ASYN:비동기, SYNC:동기, default value[ASYN], the connection type set[ASYN, SYNC]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionType).append(".value").toString();
		String itemValue = (null == clientConnectionType) ? ClientConnectionType.ASYN.name() : clientConnectionType.name();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionType).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientConnectionTimeout(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionTimeout).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(1000L, (long)Integer.MAX_VALUE);
		
		try {

			clientConnectionTimeout = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientConnectionTimeout(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionTimeout).append(".desc").toString();
		String itemDescValue = "연결 타임아웃, 단위 ms, default value[5000], min[1000], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionTimeout).append(".value").toString();
		String itemValue = (null == clientConnectionTimeout) ? "5000" : String.valueOf(clientConnectionTimeout);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionTimeout).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientConnectionCount(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionCount).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			clientConnectionCount = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientConnectionCount(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionCount).append(".desc").toString();
		String itemDescValue = "연결 갯수, default value[5], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionCount).append(".value").toString();
		String itemValue = (null == clientConnectionCount) ? "5" : String.valueOf(clientConnectionCount);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionCount).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientConnectionMaxCount(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionMaxCount).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			clientConnectionMaxCount = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientConnectionMaxCount(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionMaxCount).append(".desc").toString();
		String itemDescValue = "최대 연결 갯수, default value[5], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionMaxCount).append(".value").toString();
		String itemValue = (null == clientConnectionMaxCount) ? "5" : String.valueOf(clientConnectionMaxCount);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionMaxCount).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientConnectionPoolSupporterTimeInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionPoolSupporterTimeInterval).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(1000L, (long)Integer.MAX_VALUE);
		
		try {

			clientConnectionPoolSupporterTimeInterval = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientConnectionPoolSupporterTimeInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionPoolSupporterTimeInterval).append(".desc").toString();
		String itemDescValue = "클라이언트 연결 지원자 수행 주기, 단위 ms, 디폴트 600000, 최소 1000, 최대 Long.MAX_VALUE";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionPoolSupporterTimeInterval).append(".value").toString();
		String itemValue = (null == clientConnectionPoolSupporterTimeInterval) ? "600000" : String.valueOf(clientConnectionPoolSupporterTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientConnectionPoolSupporterTimeInterval).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientMailboxCountPerAsynShareConnection(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientMailboxCountPerAsynShareConnection).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			clientMailboxCountPerAsynShareConnection = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientMailboxCountPerAsynShareConnection(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientMailboxCountPerAsynShareConnection).append(".desc").toString();
		String itemDescValue = "비동기+공유 연결 1개당 메일함 갯수, default value[2], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientMailboxCountPerAsynShareConnection).append(".value").toString();
		String itemValue = (null == clientMailboxCountPerAsynShareConnection) ? "2" : String.valueOf(clientMailboxCountPerAsynShareConnection);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientMailboxCountPerAsynShareConnection).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientAsynInputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientAsynInputMessageQueueCapacity).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			clientAsynInputMessageQueueCapacity = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientAsynInputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientAsynInputMessageQueueCapacity).append(".desc").toString();
		String itemDescValue = "클라이언트 비동기용 입력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientAsynInputMessageQueueCapacity).append(".value").toString();
		String itemValue = (null == clientAsynInputMessageQueueCapacity) ? "10" : String.valueOf(clientAsynInputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientAsynInputMessageQueueCapacity).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientAsynOutputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientAsynOutputMessageQueueCapacity).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			clientAsynOutputMessageQueueCapacity = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientAsynOutputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientAsynOutputMessageQueueCapacity).append(".desc").toString();
		String itemDescValue = "클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientAsynOutputMessageQueueCapacity).append(".value").toString();
		String itemValue = (null == clientAsynOutputMessageQueueCapacity) ? "10" : String.valueOf(clientAsynOutputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientAsynOutputMessageQueueCapacity).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForClientSelectorWakeupInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientSelectorWakeupInterval).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(1L, (long) Integer.MAX_VALUE);
		
		try {
			clientSelectorWakeupInterval = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForClientSelectorWakeupInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientSelectorWakeupInterval).append(".desc").toString();
		String itemDescValue = "클라이언트 selector 를 깨우는 주기. 단위 ms, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientSelectorWakeupInterval).append(".value").toString();
		String itemValue = (null == clientSelectorWakeupInterval) ? "10" : String.valueOf(clientSelectorWakeupInterval);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForClientSelectorWakeupInterval).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForServerMonitorTimeInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerMonitorTimeInterval).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningLongBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningLongBetweenMinAndMax(1000L, (long) Integer.MAX_VALUE);
		
		try {

			serverMonitorTimeInterval = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerMonitorTimeInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerMonitorTimeInterval).append(".desc").toString();
		String itemDescValue = "서버 모니터링 주기, 단위 ms, default value[5000], min[1000], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerMonitorTimeInterval).append(".value").toString();
		String itemValue = (null == serverMonitorTimeInterval) ? "5000" : String.valueOf(serverMonitorTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerMonitorTimeInterval).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForWhetherServerWrapBufferIsDirect(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForWhetherServerWrapBufferIsDirect).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		SetTypeConverterReturningBoolean nativeValueConverter = new SetTypeConverterReturningBoolean();
		
		try {

			whetherServerWrapBufferIsDirect = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForWhetherServerWrapBufferIsDirect(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForWhetherServerWrapBufferIsDirect).append(".desc").toString();
		String itemDescValue = "whether server wrap buffer is direct, default value[true], the boolean set[false, true]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForWhetherServerWrapBufferIsDirect).append(".value").toString();
		String itemValue = (null == whetherServerWrapBufferIsDirect) ? Boolean.TRUE.toString() : whetherServerWrapBufferIsDirect.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForWhetherServerWrapBufferIsDirect).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForServerWrapBufferMaxCntPerMessage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferMaxCntPerMessage).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			serverWrapBufferMaxCntPerMessage = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerWrapBufferMaxCntPerMessage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferMaxCntPerMessage).append(".desc").toString();
		String itemDescValue = "서버에서 1개 메시지당 할당 받을 수 있는 랩 버퍼 최대 갯수, default value[1000], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferMaxCntPerMessage).append(".value").toString();
		String itemValue = (null == serverWrapBufferMaxCntPerMessage) ? "1000" : String.valueOf(serverWrapBufferMaxCntPerMessage);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferMaxCntPerMessage).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForServerWrapBufferSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferSize).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1024, Integer.MAX_VALUE);
		
		try {

			serverWrapBufferSize = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerWrapBufferSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferSize).append(".desc").toString();
		String itemDescValue = "서버 랩 버퍼 크기, 단위 byte, default value[4096], min[1024], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferSize).append(".value").toString();
		String itemValue = (null == serverWrapBufferSize) ? "4096" : String.valueOf(serverWrapBufferSize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferSize).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	
	public void toValueForServerWrapBufferPoolSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferPoolSize).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			serverWrapBufferPoolSize = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerWrapBufferPoolSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferPoolSize).append(".desc").toString();
		String itemDescValue = "서버 랩 버퍼 폴 크기, default value[1000], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferPoolSize).append(".value").toString();
		String itemValue = (null == serverWrapBufferPoolSize) ? "1000" : String.valueOf(serverWrapBufferPoolSize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerWrapBufferPoolSize).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	
	public void toValueForServerMaxClients(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerMaxClients).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {
			serverMaxClients = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerMaxClients(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerMaxClients).append(".desc").toString();
		String itemDescValue = "서버에 접속할 수 있는 최대 클라이언트 수, default value[5], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerMaxClients).append(".value").toString();
		String itemValue = (null == serverMaxClients) ? "5" : String.valueOf(serverMaxClients);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerMaxClients).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	
	public void toValueForServerInputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerInputMessageQueueCapacity).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			serverInputMessageQueueCapacity = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerInputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerInputMessageQueueCapacity).append(".desc").toString();
		String itemDescValue = "서버 입력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerInputMessageQueueCapacity).append(".value").toString();
		String itemValue = (null == serverInputMessageQueueCapacity) ? "10" : String.valueOf(serverInputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerInputMessageQueueCapacity).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForServerOutputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerOutputMessageQueueCapacity).append(".value").toString();

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverter = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
		
		try {

			serverOutputMessageQueueCapacity = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServerOutputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerOutputMessageQueueCapacity).append(".desc").toString();
		String itemDescValue = "클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerOutputMessageQueueCapacity).append(".value").toString();
		String itemValue = (null == serverOutputMessageQueueCapacity) ? "10" : String.valueOf(serverOutputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(prefexOfItemID).append(itemIDForServerOutputMessageQueueCapacity).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}

	// FIXME!	
	public String getSubProjectName() {
		return subProjectName;
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


	public Integer getClientMailboxCountPerAsynShareConnection() {
		return clientMailboxCountPerAsynShareConnection;
	}


	public void setClientMailboxCountPerAsynShareConnection(
			Integer clientMailboxCountPerAsynShareConnection) {
		this.clientMailboxCountPerAsynShareConnection = clientMailboxCountPerAsynShareConnection;
	}


	/*
	public Integer getClientAsynExecutorPoolSize() {
		return clientAsynExecutorPoolSize;
	}


	public void setClientAsynExecutorPoolSize(Integer clientAsynExecutorPoolSize) {
		this.clientAsynExecutorPoolSize = clientAsynExecutorPoolSize;
	}
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


	public Long getServerMonitorTimeInterval() {
		return serverMonitorTimeInterval;
	}


	public void setServerMonitorTimeInterval(Long serverMonitorTimeInterval) {
		this.serverMonitorTimeInterval = serverMonitorTimeInterval;
	}


	public Boolean getWhetherServerWrapBufferIsDirect() {
		return whetherServerWrapBufferIsDirect;
	}


	public void setWhetherServerWrapBufferIsDirect(Boolean whetherServerWrapBufferIsDirect) {
		this.whetherServerWrapBufferIsDirect = whetherServerWrapBufferIsDirect;
	}


	public Integer getServerWrapBufferMaxCntPerMessage() {
		return serverWrapBufferMaxCntPerMessage;
	}


	public void setServerWrapBufferMaxCntPerMessage(Integer serverWrapBufferMaxCntPerMessage) {
		this.serverWrapBufferMaxCntPerMessage = serverWrapBufferMaxCntPerMessage;
	}


	public Integer getServerWrapBufferSize() {
		return serverWrapBufferSize;
	}


	public void setServerWrapBufferSize(Integer serverWrapBufferSize) {
		this.serverWrapBufferSize = serverWrapBufferSize;
	}


	public Integer getServerWrapBufferPoolSize() {
		return serverWrapBufferPoolSize;
	}


	public void setServerWrapBufferPoolSize(Integer serverWrapBufferPoolSize) {
		this.serverWrapBufferPoolSize = serverWrapBufferPoolSize;
	}


	public Integer getServerMaxClients() {
		return serverMaxClients;
	}


	public void setServerMaxClients(Integer serverMaxClients) {
		this.serverMaxClients = serverMaxClients;
	}


	public Integer getServerInputMessageQueueCapacity() {
		return serverInputMessageQueueCapacity;
	}


	public void setServerInputMessageQueueCapacity(Integer serverInputMessageQueueCapacity) {
		this.serverInputMessageQueueCapacity = serverInputMessageQueueCapacity;
	}


	public Integer getServerOutputMessageQueueCapacity() {
		return serverOutputMessageQueueCapacity;
	}


	public void setServerOutputMessageQueueCapacity(Integer serverOutputMessageQueueCapacity) {
		this.serverOutputMessageQueueCapacity = serverOutputMessageQueueCapacity;
	}
	
	

}
