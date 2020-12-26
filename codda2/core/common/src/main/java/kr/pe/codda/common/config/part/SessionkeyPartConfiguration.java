package kr.pe.codda.common.config.part;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterOfRSAKeypairSource;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningInteger;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningString;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ItemViewType;
import kr.pe.codda.common.type.KeyTypeOfConfieProperties;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.SequencedProperties;

public class SessionkeyPartConfiguration implements PartConfigurationIF {
	private final Logger log = Logger.getLogger(SessionkeyPartConfiguration.class.getName());
	
	public static final String PART_NAME = "sessionkey";
	
	private final String prefixBeforeItemID = new StringBuilder().append(PART_NAME).append(".").toString();	
	
	public static final String itemIDForRSAKeypairSource = "rsa.keypair_source";
	private SetTypeConverterOfRSAKeypairSource nativeValueConverterForSessionKeyRSAKeypairSource = new SetTypeConverterOfRSAKeypairSource();
	private SessionKey.RSAKeypairSourceType rsaKeypairSource = null;	
	
	public static final String itemIDForRSAPublickeyFile = "rsa.publickey.file";
	private GeneralConverterReturningRegularFile nativeValueConverterForRSAPublickeyFile = new GeneralConverterReturningRegularFile(false);
	private File rsaPublickeyFile = null;
	
		
	public static final String itemIDForRSAPrivatekeyFile = "rsa.privatekey.file";
	private GeneralConverterReturningRegularFile nativeValueConverterForRSAPrivatekeyFile = new GeneralConverterReturningRegularFile(false);
	private File rsaPrivatekeyFile = null;
	
	public static final String itemIDForRSAKeySize = "rsa.keysize";
	private SetTypeConverterReturningInteger nativeValueConverterForRSAKeySize = new SetTypeConverterReturningInteger("512", "1024", "2048");
	private Integer rsaKeySize = null;	
	
	
	public static final String itemIDForSymmetricKeyAlgorithm = "symmetric_key.algorithm";
	private SetTypeConverterReturningString nativeValueConverterForSymmetricKeyAlgorithm = new SetTypeConverterReturningString("AES", "DESede", "DES");
	private String symmetricKeyAlgorithm = null;
	
	public static final String itemIDForSymmetricKeySize = "symmetric_key.size";
	private SetTypeConverterReturningInteger nativeValueConverterForSymmetricKeySize = new SetTypeConverterReturningInteger("8", "16", "24");
	private Integer symmetricKeySize=null;
		
	public static final String itemIDForSymmetricIVSize = "iv_size";
	private SetTypeConverterReturningInteger nativeValueConverterForSymmetricIVSize = new SetTypeConverterReturningInteger("8", "16", "24");
	private Integer symmetricIVSize=null;
	
	
	@Override
	public String getPartName() {
		return PART_NAME;
	}
	
	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		if (null == sourceSequencedProperties) {
			throw new IllegalArgumentException("the parameter sourceSequencedProperties is null");
		}
		
		
		/** session key start */
		fromPropertiesForRSAKeypairSource(sourceSequencedProperties);		
		if (SessionKey.RSAKeypairSourceType.FILE.equals(rsaKeypairSource)) {
			fromPropertiesForRSAPublickeyFile(sourceSequencedProperties);
			fromPropertiesForRSAPrivatekeyFile(sourceSequencedProperties);
		}
		fromPropertiesForRSAKeySize(sourceSequencedProperties);
		fromPropertiesForSymmetricIVSize(sourceSequencedProperties);
		fromPropertiesForSymmetricKeyAlgorithm(sourceSequencedProperties);
		fromPropertiesForSymmetricKeySize(sourceSequencedProperties);
		/** session key end */
	}

	@Override
	public void checkForDependencies() throws PartConfigurationException {
		/** nothing */
	}
	
	@Override
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException {
		if (null == targetSequencedProperties) {
			throw new IllegalArgumentException("the parameter targetSequencedProperties is null");
		}
	
		
		toPropertiesForRSAKeypairSource(targetSequencedProperties);
		toPropertiesForRSAPublickeyFile(targetSequencedProperties);
		toPropertiesForRSAPrivatekeyFile(targetSequencedProperties);
		toPropertiesForRSAKeySize(targetSequencedProperties);
		toPropertiesForSymmetricKeyAlgorithm(targetSequencedProperties);
		toPropertiesForSymmetricKeySize(targetSequencedProperties);
		toPropertiesForSymmetricIVSize(targetSequencedProperties);
	}


	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 공개키 키쌍 생성 방법을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 공개키 키쌍 생성 방법 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForRSAKeypairSource(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
		
		try {
			rsaKeypairSource = nativeValueConverterForSessionKeyRSAKeypairSource.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterOfRSAKeypairSource.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}
	

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 세션키에 사용되는 공개키 키쌍 생성 방법 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 세션키에 사용되는 공개키 키쌍 생성 방법 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForRSAKeypairSource(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "세션키에 사용되는 공개키 키쌍 생성 방법, SERVER:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성, 디폴트:SERVER";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == rsaKeypairSource) ? SessionKey.RSAKeypairSourceType.SERVER.toString() : rsaKeypairSource.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		
		String rsaKeypairSourceSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieProperties.SET);
		String rsaKeypairSourceSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForSessionKeyRSAKeypairSource.getItemValueSet());
				
		targetSequencedProperties.put(rsaKeypairSourceSetKey, rsaKeypairSourceSetValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 RSA 공개키 파일을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 RSA 공개키 파일 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForRSAPublickeyFile(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		
			String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
					prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieProperties.VALUE);

			String itemValue = sourceSequencedProperties.getProperty(itemKey);

			if (null == itemValue) {
				String errorMessage = new StringBuilder().append("the item '").append(itemKey)
						.append("' does not exist in the parameter sourceSequencedProperties").toString();

				throw new PartConfigurationException(itemKey, errorMessage);
			}

			try {
				rsaPublickeyFile = nativeValueConverterForRSAPublickeyFile.valueOf(itemValue);
			} catch (Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
						.append("] value[").append(itemValue).append("] to value using the value converter[")
						.append(GeneralConverterReturningRegularFile.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

				log.log(Level.WARNING, errorMessage, e);

				throw new PartConfigurationException(itemKey, errorMessage);
			}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 세션키에 사용되는 RSA 공개키 파일 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 세션키에 사용되는 RSA 공개키 파일 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForRSAPublickeyFile(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "세션키에 사용되는 RSA 공개키 파일, '세션키에 사용되는 공개키 키쌍 생성 방법'이 FILE 인 경우에 유효하다";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieProperties.VALUE);
				
		String itemValue = (null == rsaPublickeyFile) ? "" : rsaPublickeyFile.getAbsolutePath();
		targetSequencedProperties.put(itemKey, itemValue);	
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.FILE.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		String rsaPublickeyFileKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieProperties.FILE);
		String rsaPublickeyFileValue = "resources/rsa_keypair/codda.publickey";
		targetSequencedProperties.put(rsaPublickeyFileKey, rsaPublickeyFileValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 RSA 개인 파일을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 RSA 개인 파일 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForRSAPrivatekeyFile(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieProperties.VALUE);
		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
				
		try {
			rsaPrivatekeyFile = nativeValueConverterForRSAPrivatekeyFile.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningRegularFile.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 세션키에 사용되는 RSA 개인 파일 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 세션키에 사용되는 RSA 개인 파일 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForRSAPrivatekeyFile(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "세션키에 사용되는 RSA 개인 파일, '세션키에 사용되는 공개키 키쌍 생성 방법'이 FILE 인 경우에 유효하다";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieProperties.VALUE);		
		String itemValue = (null == rsaPrivatekeyFile) ? "" : rsaPrivatekeyFile.getAbsolutePath();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.FILE.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		
		String rsaPrivatekeyFileKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieProperties.FILE);
		String rsaPrivatekeyFileValue = "resources/rsa_keypair/codda.privatekey";
		targetSequencedProperties.put(rsaPrivatekeyFileKey, rsaPrivatekeyFileValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용하는 공개키 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용하는 공개키 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForRSAKeySize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			rsaKeySize = nativeValueConverterForRSAKeySize.valueOf(itemValue);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningInteger.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 세션키에 사용하는 공개키 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 세션키에 사용하는 공개키 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForRSAKeySize(SequencedProperties targetSequencedProperties) {		
		String rsaKeySizeSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForRSAKeySize.getItemValueSet());
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "세션키에 사용하는 공개키 크기, 단위 byte, default value[1024], the integer set[" + rsaKeySizeSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == rsaKeySize) ? "1024" : String.valueOf(rsaKeySize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		
		String rsaKeySizeSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieProperties.SET);		
				
		targetSequencedProperties.put(rsaKeySizeSetKey, rsaKeySizeSetValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 대칭키 알고리즘을 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 대칭키 알고리즘 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForSymmetricKeyAlgorithm(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			symmetricKeyAlgorithm = nativeValueConverterForSymmetricKeyAlgorithm.valueOf(itemValue);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningInteger.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 세션키에 사용되는 대칭키 알고리즘 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 세션키에 사용되는 대칭키 알고리즘 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForSymmetricKeyAlgorithm(SequencedProperties targetSequencedProperties) {		
		String symmetricKeyAlgorithmSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForSymmetricKeyAlgorithm.getItemValueSet());
		String symmetricKeyAlgorithmSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieProperties.SET);
		
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키 알고리즘, default value[AES], the string set[" + symmetricKeyAlgorithmSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == symmetricKeyAlgorithm) ? "AES" : symmetricKeyAlgorithm;
		targetSequencedProperties.put(itemKey, itemValue);		
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);	
				
				
		targetSequencedProperties.put(symmetricKeyAlgorithmSetKey, symmetricKeyAlgorithmSetValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 대칭키 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 대칭키 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForSymmetricKeySize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		

		

		try {
			symmetricKeySize = nativeValueConverterForSymmetricKeySize.valueOf(itemValue);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningInteger.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}	

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 세션키에 사용되는 대칭키 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 세션키에 사용되는 대칭키 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForSymmetricKeySize(SequencedProperties targetSequencedProperties) {		
		String symmetricKeySizeSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForSymmetricKeySize.getItemValueSet());
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키 크기, default value[16], the integer set[" + symmetricKeySizeSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == symmetricKeySize) ? "16" : String.valueOf(symmetricKeySize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieProperties.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);
		
		String symmetricKeySizeSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieProperties.SET);		
				
		targetSequencedProperties.put(symmetricKeySizeSetKey, symmetricKeySizeSetValue);
	}
	
	/**
	 * 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기를 얻어와 저장한다.
	 * 
	 * @param sourceSequencedProperties  원천 시퀀스 프로퍼티
	 * 
	 * @throws PartConfigurationException 지정받은 '원천 시퀀스 프로퍼티' 로 부터 세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기 값이 없을때 혹은 값이 잘못되어 있을때 던지는 예외
	 */
	public void fromPropertiesForSymmetricIVSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSize, KeyTypeOfConfieProperties.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		try {
			symmetricIVSize = nativeValueConverterForSymmetricIVSize.valueOf(itemValue);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningInteger.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	/**
	 * 지정한 '목적지 시퀀스 프러퍼티' 에 세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기 값을 저장한다.
	 * 
	 * @param targetSequencedProperties 세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기 값이 저장될 시퀀스 프로퍼티
	 */
	public void toPropertiesForSymmetricIVSize(SequencedProperties targetSequencedProperties) {	
		String symmetricIVSizeSetValue = RunningProjectConfiguration.toSetTypeValue(nativeValueConverterForSymmetricIVSize.getItemValueSet());
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSize, KeyTypeOfConfieProperties.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기, default value[16], the integer set[" + symmetricIVSizeSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSize, KeyTypeOfConfieProperties.VALUE);
		String itemValue = (null == symmetricIVSize) ? "16" : String.valueOf(symmetricIVSize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String symmetricIVSizeSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSize, KeyTypeOfConfieProperties.SET);		
				
		targetSequencedProperties.put(symmetricIVSizeSetKey, symmetricIVSizeSetValue);
	}
	
	

	/**
	 * @return 세션키에 사용되는 공개키 키쌍 생성 방법
	 */
	public SessionKey.RSAKeypairSourceType getRSAKeypairSource() {
		return rsaKeypairSource;
	}

	/**
	 * 지정한 세션키에 사용되는 공개키 키쌍 생성 방법 값을 저장한다.
	 * @param rsaKeypairSource 세션키에 사용되는 공개키 키쌍 생성 방법
	 */
	public void setRSAKeypairSource(SessionKey.RSAKeypairSourceType rsaKeypairSource) {
		this.rsaKeypairSource = rsaKeypairSource;
	}

	/**
	 * @return 세션키에 사용되는 RSA 공개키 파일
	 */
	public File getRSAPublickeyFile() {
		return rsaPublickeyFile;
	}

	/**
	 * 지정한 세션키에 사용되는 RSA 공개키 파일 값을 저장한다.
	 * @param rsaPublickeyFile 세션키에 사용되는 RSA 공개키 파일
	 */
	public void setRSAPublickeyFile(File rsaPublickeyFile) {		
		this.rsaPublickeyFile = rsaPublickeyFile;
	}

	/**
	 * @return 세션키에 사용되는 RSA 개인 파일
	 */
	public File getRSAPrivatekeyFile() {
		return rsaPrivatekeyFile;
	}

	/**
	 * 지정한 세션키에 사용되는 RSA 개인 파일 값을 저장한다.
	 * @param rsaPrivatekeyFile 세션키에 사용되는 RSA 개인 파일
	 */
	public void setRSAPrivatekeyFile(File rsaPrivatekeyFile) {
		this.rsaPrivatekeyFile = rsaPrivatekeyFile;
	}

	/**
	 * @return 세션키에 사용하는 공개키 크기
	 */
	public Integer getRSAKeySize() {
		return rsaKeySize;
	}

	/**
	 * 지정한 세션키에 사용하는 공개키 크기 값을 저장한다.
	 * @param rsaKeySize 세션키에 사용하는 공개키 크기
	 */
	public void setRSAKeySize(Integer rsaKeySize) {
		this.rsaKeySize = rsaKeySize;
	}

	/**
	 * @return 세션키에 사용되는 대칭키 알고리즘
	 */
	public String getSymmetricKeyAlgorithm() {
		return symmetricKeyAlgorithm;
	}

	/**
	 * 지정한 세션키에 사용되는 대칭키 알고리즘 값을 저장한다.
	 * @param symmetricKeyAlgorithm 세션키에 사용되는 대칭키 알고리즘
	 */
	public void setSymmetricKeyAlgorithm(String symmetricKeyAlgorithm) {		
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
	}

	/**
	 * @return 세션키에 사용되는 대칭키 크기
	 */
	public Integer getSymmetricKeySize() {
		return symmetricKeySize;
	}

	/**
	 * 지정한 세션키에 사용되는 대칭키 크기 값을 저장한다.
	 * @param symmetricKeySize 세션키에 사용되는 대칭키 크기
	 */
	public void setSymmetricKeySize(Integer symmetricKeySize) {
		this.symmetricKeySize = symmetricKeySize;
	}

	/**
	 * @return 세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기
	 */
	public Integer getSymmetricIVSize() {
		return symmetricIVSize;
	}

	/**
	 * 지정한 세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기 값을 저장한다.
	 * @param symmetricIVSizeOfSessionKey 세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기
	 */
	public void setSymmetricIVSize(Integer symmetricIVSizeOfSessionKey) {		
		this.symmetricIVSize = symmetricIVSizeOfSessionKey;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SessionkeyPartConfiguration [rsaKeypairSource=");
		builder.append(rsaKeypairSource);
		builder.append(", rsaPublickeyFile=");
		builder.append(rsaPublickeyFile);
		builder.append(", rsaPrivatekeyFile=");
		builder.append(rsaPrivatekeyFile);
		builder.append(", rsaKeySize=");
		builder.append(rsaKeySize);
		builder.append(", symmetricKeyAlgorithm=");
		builder.append(symmetricKeyAlgorithm);
		builder.append(", symmetricKeySize=");
		builder.append(symmetricKeySize);
		builder.append(", symmetricIVSize=");
		builder.append(symmetricIVSize);
		builder.append("]");
		return builder.toString();
	}
	
}
