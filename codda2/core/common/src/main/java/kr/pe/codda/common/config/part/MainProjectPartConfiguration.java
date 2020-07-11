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
import kr.pe.codda.common.type.KeyTypeOfConfieFile;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class MainProjectPartConfiguration extends AbstractProjectPartConfiguration {
	private final String prefixBeforeItemID = "mainproject.";
	
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
	
	
	public void fromPropertiesForServerMonitorTimeInterval(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMonitorTimeInterval, KeyTypeOfConfieFile.VALUE);

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
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMonitorTimeInterval, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "서버 모니터링 주기, 단위 ms, default value[5000], min[1000], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMonitorTimeInterval, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverMonitorTimeInterval) ? "5000" : String.valueOf(serverMonitorTimeInterval);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForWhetherServerWrapBufferIsDirect(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForWhetherServerWrapBufferIsDirect, KeyTypeOfConfieFile.VALUE);

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
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForWhetherServerWrapBufferIsDirect, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "whether server wrap buffer is direct, default value[true], the boolean set[false, true]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForWhetherServerWrapBufferIsDirect, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == whetherServerWrapBufferIsDirect) ? Boolean.TRUE.toString() : whetherServerWrapBufferIsDirect.toString();
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForServerWrapBufferMaxCntPerMessage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferMaxCntPerMessage, KeyTypeOfConfieFile.VALUE);

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
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferMaxCntPerMessage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "서버에서 1개 메시지당 할당 받을 수 있는 랩 버퍼 최대 갯수, default value[1000], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferMaxCntPerMessage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverWrapBufferMaxCntPerMessage) ? "1000" : String.valueOf(serverWrapBufferMaxCntPerMessage);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForServerWrapBufferSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferSize, KeyTypeOfConfieFile.VALUE);

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
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferSize, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "서버 랩 버퍼 크기, 단위 byte, default value[4096], min[1024], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferSize, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverWrapBufferSize) ? "4096" : String.valueOf(serverWrapBufferSize);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	public void fromPropertiesForServerWrapBufferPoolSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferPoolSize, KeyTypeOfConfieFile.VALUE);

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
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferPoolSize, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "서버 랩 버퍼 폴 크기, default value[1000], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerWrapBufferPoolSize, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverWrapBufferPoolSize) ? "1000" : String.valueOf(serverWrapBufferPoolSize);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	public void fromPropertiesForServerMaxClients(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMaxClients, KeyTypeOfConfieFile.VALUE);

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
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMaxClients, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "서버에 접속할 수 있는 최대 클라이언트 수, default value[5], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerMaxClients, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverMaxClients) ? "5" : String.valueOf(serverMaxClients);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	public void fromPropertiesForServerInputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerInputMessageQueueCapacity, KeyTypeOfConfieFile.VALUE);

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
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerInputMessageQueueCapacity, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "서버 입력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerInputMessageQueueCapacity, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverInputMessageQueueCapacity) ? "10" : String.valueOf(serverInputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForServerOutputMessageQueueCapacity(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerOutputMessageQueueCapacity, KeyTypeOfConfieFile.VALUE);

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
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerOutputMessageQueueCapacity, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "클라이언트 비동기용 출력 메시지 큐가 최대 수용할 수 있는 크기, default value[10], min[1], max[2147483647]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServerOutputMessageQueueCapacity, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == serverOutputMessageQueueCapacity) ? "10" : String.valueOf(serverOutputMessageQueueCapacity);
		targetSequencedProperties.put(itemKey, itemValue);
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
