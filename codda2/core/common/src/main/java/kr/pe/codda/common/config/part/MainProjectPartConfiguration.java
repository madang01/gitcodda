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

import java.util.logging.Level;

import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ItemViewType;
import kr.pe.codda.common.type.KeyTypeOfConfieProperties;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class MainProjectPartConfiguration extends AbstractProjectPartConfiguration {
	public static final String PART_NAME = "mainproject";
	
	private final String prefixBeforeItemID = new StringBuilder().append(PART_NAME).append(".").toString();
	
	/************* server 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	public static final String itemIDForServerMonitorTimeInterval = "server.monitor.time_interval";
	private GeneralConverterReturningLongBetweenMinAndMax nativeValueConverterForServerMonitorTimeInterval = new GeneralConverterReturningLongBetweenMinAndMax(1000L, (long) Integer.MAX_VALUE);
	private Long serverMonitorTimeInterval = null;
	/***** 모니터 환경 변수 종료 *****/
	
	public static final String itemIDForWhetherServerWrapBufferIsDirect = "server.wrap_buffer.isdirect";
	private SetTypeConverterReturningBoolean nativeValueConverterForWhetherServerWrapBufferIsDirect = new SetTypeConverterReturningBoolean();
	private Boolean whetherServerWrapBufferIsDirect = null;
	
	
	public static final String itemIDForServerWrapBufferMaxCntPerMessage = "server.wrap_buffer.max_cnt_per_message";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForServerWrapBufferMaxCntPerMessage = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
	private Integer serverWrapBufferMaxCntPerMessage = null;
	
	
	public static final String itemIDForServerWrapBufferSize = "server.wrap_buffer.size";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForServerWrapBufferSize = new GeneralConverterReturningIntegerBetweenMinAndMax(1024, Integer.MAX_VALUE);
	private Integer serverWrapBufferSize = null;
	
	
	public static final String itemIDForServerWrapBufferPoolSize = "server.wrap_buffer.pool_size";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForServerWrapBufferPoolSize = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
	private Integer serverWrapBufferPoolSize = null;
	
	public static final String itemIDForServerMaxClients = "server.max_clients";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForServerMaxClients = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
	private Integer serverMaxClients = null;	
	
	
	/***** 서버 비동기 입출력 지원용 자원 시작 *****/
	public static final String itemIDForServerInputMessageQueueCapacity = "server.input_message_queue_capacity";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForServerInputMessageQueueCapacity = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
	private Integer  serverInputMessageQueueCapacity = null;
	
	
	public static final String itemIDForServerOutputMessageQueueCapacity = "server.output_message_queue_capacity";
	private GeneralConverterReturningIntegerBetweenMinAndMax nativeValueConverterForServerOutputMessageQueueCapacity = new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE);
	private Integer  serverOutputMessageQueueCapacity = null;
	/***** 서버 비동기 입출력 지원용 자원 종료 *****/
	
	/************* server 변수 종료 ******************/
	
	@Override
	public String getPartName() {
		return PART_NAME;
	}
	
	@Override
	public String getPrefixBeforeItemID() {
		return prefixBeforeItemID;
	}
	
	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		super.fromProperties(sourceSequencedProperties);
		
		fromPropertiesForServerMonitorTimeInterval(sourceSequencedProperties);
		fromPropertiesForWhetherServerWrapBufferIsDirect(sourceSequencedProperties);
		fromPropertiesForServerWrapBufferMaxCntPerMessage(sourceSequencedProperties);
		fromPropertiesForServerWrapBufferSize(sourceSequencedProperties);
		fromPropertiesForServerWrapBufferPoolSize(sourceSequencedProperties);
		fromPropertiesForServerMaxClients(sourceSequencedProperties);
		fromPropertiesForServerInputMessageQueueCapacity(sourceSequencedProperties);
		fromPropertiesForServerOutputMessageQueueCapacity(sourceSequencedProperties);	
	}
	
	
	@Override
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException {	
		super.toProperties(targetSequencedProperties);
		
		toPropertiesForServerMonitorTimeInterval(targetSequencedProperties);
		toPropertiesForWhetherServerWrapBufferIsDirect(targetSequencedProperties);
		toPropertiesForServerWrapBufferMaxCntPerMessage(targetSequencedProperties);
		toPropertiesForServerWrapBufferSize(targetSequencedProperties);
		toPropertiesForServerWrapBufferPoolSize(targetSequencedProperties);
		toPropertiesForServerMaxClients(targetSequencedProperties);
		toPropertiesForServerInputMessageQueueCapacity(targetSequencedProperties);
		toPropertiesForServerOutputMessageQueueCapacity(targetSequencedProperties);
	}
	
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 모니터링 주기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 모니터링 주기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerMonitorTimeInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMonitorTimeInterval, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
		
		try {
			serverMonitorTimeInterval = nativeValueConverterForServerMonitorTimeInterval.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}
	
	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버 모니터링 주기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버 모니터링 주기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerMonitorTimeInterval(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMonitorTimeInterval, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "서버 모니터링 주기, 단위 ms, default value[5000], min[" + nativeValueConverterForServerMonitorTimeInterval.getMin() + "], max[" + nativeValueConverterForServerMonitorTimeInterval.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMonitorTimeInterval, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverMonitorTimeInterval) ? "5000" : String.valueOf(serverMonitorTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 랩버퍼 다이렉트 여부를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 랩버퍼 다이렉트 여부 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForWhetherServerWrapBufferIsDirect(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForWhetherServerWrapBufferIsDirect, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
		
		try {
			whetherServerWrapBufferIsDirect = nativeValueConverterForWhetherServerWrapBufferIsDirect.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버 랩버퍼 다이렉트 여부 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버 랩버퍼 다이렉트 여부 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForWhetherServerWrapBufferIsDirect(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForWhetherServerWrapBufferIsDirect, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "whether server wrap buffer is direct, default value[true], the boolean set[false, true]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForWhetherServerWrapBufferIsDirect, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == whetherServerWrapBufferIsDirect) ? Boolean.TRUE.toString() : whetherServerWrapBufferIsDirect.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherServerWrapBufferIsDirect, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);
		
		String whetherClientWrapBufferIsDirectSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				getPrefixBeforeItemID(), itemIDForWhetherServerWrapBufferIsDirect, KeyTypeOfConfieProperties.SET);
		String whetherClientWrapBufferIsDirectSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForWhetherServerWrapBufferIsDirect.getItemValueSet());
		
		targetSequencedProperties.put(whetherClientWrapBufferIsDirectSetKey, whetherClientWrapBufferIsDirectSetValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 메시지당 서버 랩버퍼 최대 갯수를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 메시지당 서버 랩버퍼 최대 갯수 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerWrapBufferMaxCntPerMessage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferMaxCntPerMessage, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	
		
		try {
			serverWrapBufferMaxCntPerMessage = nativeValueConverterForServerWrapBufferMaxCntPerMessage.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 메시지당 서버 랩버퍼 최대 갯수 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 메시지당 서버 랩버퍼 최대 갯수 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerWrapBufferMaxCntPerMessage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferMaxCntPerMessage, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "서버에서 1개 메시지당 할당 받을 수 있는 랩 버퍼 최대 갯수, default value[1000], min[" + nativeValueConverterForServerWrapBufferMaxCntPerMessage.getMin() + "], max[" + nativeValueConverterForServerWrapBufferMaxCntPerMessage.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferMaxCntPerMessage, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverWrapBufferMaxCntPerMessage) ? "1000" : String.valueOf(serverWrapBufferMaxCntPerMessage);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 랩 버퍼 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 랩 버퍼 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerWrapBufferSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferSize, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		
		
		try {
			serverWrapBufferSize = nativeValueConverterForServerWrapBufferSize.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버 랩 버퍼 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버 랩 버퍼 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerWrapBufferSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferSize, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "서버 랩 버퍼 크기, 단위 byte, default value[4096], min[" + nativeValueConverterForServerWrapBufferSize.getMin()+"], max[" + nativeValueConverterForServerWrapBufferSize.getMax()+"]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferSize, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverWrapBufferSize) ? "4096" : String.valueOf(serverWrapBufferSize);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 랩 버퍼 폴 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 랩 버퍼 폴 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerWrapBufferPoolSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferPoolSize, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}	
		
		try {
			serverWrapBufferPoolSize = nativeValueConverterForServerWrapBufferPoolSize.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버 랩 버퍼 폴 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버 랩 버퍼 폴 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerWrapBufferPoolSize(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferPoolSize, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "서버 랩 버퍼 폴 크기, default value[1000], min[" + nativeValueConverterForServerWrapBufferPoolSize.getMin() + "], max[" + nativeValueConverterForServerWrapBufferPoolSize.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferPoolSize, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverWrapBufferPoolSize) ? "1000" : String.valueOf(serverWrapBufferPoolSize);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버에 접속할 수 있는 최대 클라이언트 수를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버에 접속할 수 있는 최대 클라이언트 수 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerMaxClients(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMaxClients, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
		
		try {
			serverMaxClients = nativeValueConverterForServerMaxClients.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버에 접속할 수 있는 최대 클라이언트 수 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버에 접속할 수 있는 최대 클라이언트 수 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerMaxClients(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMaxClients, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "서버에 접속할 수 있는 최대 클라이언트 수, default value[5], min[" + nativeValueConverterForServerMaxClients.getMin() + "], max[" + nativeValueConverterForServerMaxClients.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMaxClients, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverMaxClients) ? "5" : String.valueOf(serverMaxClients);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 입력 메시지 큐가 최대 수용할 수 있는 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 입력 메시지 큐가 최대 수용할 수 있는 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerInputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerInputMessageQueueCapacity, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			serverInputMessageQueueCapacity = nativeValueConverterForServerInputMessageQueueCapacity.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버 입력 메시지 큐가 최대 수용할 수 있는 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버 입력 메시지 큐가 최대 수용할 수 있는 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerInputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerInputMessageQueueCapacity, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "서버 입력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[" + nativeValueConverterForServerInputMessageQueueCapacity.getMin() + "], max[" + nativeValueConverterForServerInputMessageQueueCapacity.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerInputMessageQueueCapacity, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverInputMessageQueueCapacity) ? "10" : String.valueOf(serverInputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 서버 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForServerOutputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerOutputMessageQueueCapacity, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
		
		try {
			serverOutputMessageQueueCapacity = nativeValueConverterForServerOutputMessageQueueCapacity.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 서버 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 서버 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForServerOutputMessageQueueCapacity(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerOutputMessageQueueCapacity, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "서버 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[" + nativeValueConverterForServerOutputMessageQueueCapacity.getMin() + "], max[" + nativeValueConverterForServerOutputMessageQueueCapacity.getMax() + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerOutputMessageQueueCapacity, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == serverOutputMessageQueueCapacity) ? "10" : String.valueOf(serverOutputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	/**
	 * @return 서버 모니터링 주기
	 */
	public Long getServerMonitorTimeInterval() {
		return serverMonitorTimeInterval;
	}

	/**
	 * 지정한 서버 모니터링 주기 값을 저장한다.
	 * @param serverMonitorTimeInterval 서버 모니터링 주기
	 */
	public void setServerMonitorTimeInterval(Long serverMonitorTimeInterval) {
		this.serverMonitorTimeInterval = serverMonitorTimeInterval;
	}


	/**
	 * @return 서버 랩버퍼 다이렉트 여부
	 */
	public Boolean getWhetherServerWrapBufferIsDirect() {
		return whetherServerWrapBufferIsDirect;
	}

	/**
	 * 지정한 서버 랩버퍼 다이렉트 여부 값을 저장한다.
	 * @param whetherServerWrapBufferIsDirect 서버 랩버퍼 다이렉트 여부
	 */
	public void setWhetherServerWrapBufferIsDirect(Boolean whetherServerWrapBufferIsDirect) {
		this.whetherServerWrapBufferIsDirect = whetherServerWrapBufferIsDirect;
	}


	/**
	 * @return 메시지당 서버 랩버퍼 최대 갯수
	 */
	public Integer getServerWrapBufferMaxCntPerMessage() {
		return serverWrapBufferMaxCntPerMessage;
	}

	/**
	 * 지정한 메시지당 서버 랩버퍼 최대 갯수 값을 저장한다.
	 * @param serverWrapBufferMaxCntPerMessage 메시지당 서버 랩버퍼 최대 갯수
	 */
	public void setServerWrapBufferMaxCntPerMessage(Integer serverWrapBufferMaxCntPerMessage) {
		this.serverWrapBufferMaxCntPerMessage = serverWrapBufferMaxCntPerMessage;
	}


	/**
	 * @return 서버 랩버퍼 크기
	 */
	public Integer getServerWrapBufferSize() {
		return serverWrapBufferSize;
	}


	/**
	 * 지정한 서버 랩버퍼 크기 값을 저장한다.
	 * @param serverWrapBufferSize 서버 랩버퍼 크기
	 */
	public void setServerWrapBufferSize(Integer serverWrapBufferSize) {
		this.serverWrapBufferSize = serverWrapBufferSize;
	}


	/**
	 * @return 서버 랩버퍼 폴 크기
	 */
	public Integer getServerWrapBufferPoolSize() {
		return serverWrapBufferPoolSize;
	}


	/**
	 * 지정한 서버 랩버퍼 폴 크기 값을 저장한다.
	 * @param serverWrapBufferPoolSize 서버 랩버퍼 폴 크기
	 */
	public void setServerWrapBufferPoolSize(Integer serverWrapBufferPoolSize) {
		this.serverWrapBufferPoolSize = serverWrapBufferPoolSize;
	}


	/**
	 * @return 서버에 접속 가능한 최대 클라이언트 수
	 */
	public Integer getServerMaxClients() {
		return serverMaxClients;
	}


	/**
	 * 지정한 서버에 접속 가능한 최대 클라이언트 수 값을 저장한다.
	 * @param serverMaxClients 서버에 접속 가능한 최대 클라이언트 수
	 */
	public void setServerMaxClients(Integer serverMaxClients) {
		this.serverMaxClients = serverMaxClients;
	}


	/**
	 * @return 서버 입력 메시지 큐가 최대 수용할 수 있는 크기
	 */
	public Integer getServerInputMessageQueueCapacity() {
		return serverInputMessageQueueCapacity;
	}


	/**
	 * 지정한 서버 입력 메시지 큐가 최대 수용할 수 있는 크기 값을 저장한다.
	 * @param serverInputMessageQueueCapacity 서버 입력 메시지 큐가 최대 수용할 수 있는 크기
	 */
	public void setServerInputMessageQueueCapacity(Integer serverInputMessageQueueCapacity) {
		this.serverInputMessageQueueCapacity = serverInputMessageQueueCapacity;
	}


	/**
	 * @return 서버 출력 메시지 큐가 최대 수용할 수 있는 크기
	 */
	public Integer getServerOutputMessageQueueCapacity() {
		return serverOutputMessageQueueCapacity;
	}


	/**
	 * 지정한 서버 출력 메시지 큐가 최대 수용할 수 있는 크기 값을 저장한다.
	 * @param serverOutputMessageQueueCapacity 서버 출력 메시지 큐가 최대 수용할 수 있는 크기
	 */
	public void setServerOutputMessageQueueCapacity(Integer serverOutputMessageQueueCapacity) {
		this.serverOutputMessageQueueCapacity = serverOutputMessageQueueCapacity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MainProjectPartConfiguration [serverMonitorTimeInterval=");
		builder.append(serverMonitorTimeInterval);
		builder.append(", whetherServerWrapBufferIsDirect=");
		builder.append(whetherServerWrapBufferIsDirect);
		builder.append(", serverWrapBufferMaxCntPerMessage=");
		builder.append(serverWrapBufferMaxCntPerMessage);
		builder.append(", serverWrapBufferSize=");
		builder.append(serverWrapBufferSize);
		builder.append(", serverWrapBufferPoolSize=");
		builder.append(serverWrapBufferPoolSize);
		builder.append(", serverMaxClients=");
		builder.append(serverMaxClients);
		builder.append(", serverInputMessageQueueCapacity=");
		builder.append(serverInputMessageQueueCapacity);
		builder.append(", serverOutputMessageQueueCapacity=");
		builder.append(serverOutputMessageQueueCapacity);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
	
}
