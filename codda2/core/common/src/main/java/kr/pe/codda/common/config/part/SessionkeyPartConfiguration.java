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
import kr.pe.codda.common.type.KeyTypeOfConfieFile;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.SequencedProperties;

public class SessionkeyPartConfiguration implements PartConfigurationIF {
	private final Logger log = Logger.getLogger(SessionkeyPartConfiguration.class.getName());
	
	private final String partName = "sessionkey";
	
	private final String prefixBeforeItemID = new StringBuilder().append(partName).append(".").toString();	
	
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
		return partName;
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


	
	public void fromPropertiesForRSAKeypairSource(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieFile.VALUE);

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
	

	public void toPropertiesForRSAKeypairSource(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 공개키 키쌍 생성 방법, SERVER:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성, 디폴트:SERVER";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == rsaKeypairSource) ? SessionKey.RSAKeypairSourceType.SERVER.toString() : rsaKeypairSource.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieFile.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		
		String rsaKeypairSourceSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSource, KeyTypeOfConfieFile.SET);
		String rsaKeypairSourceSetValue = RunningProjectConfiguration.toSetValue(nativeValueConverterForSessionKeyRSAKeypairSource.getItemValueSet());
				
		targetSequencedProperties.put(rsaKeypairSourceSetKey, rsaKeypairSourceSetValue);
	}
	
	public void fromPropertiesForRSAPublickeyFile(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		
			String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
					prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieFile.VALUE);

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

	public void toPropertiesForRSAPublickeyFile(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 RSA 공개키 파일, '세션키에 사용되는 공개키 키쌍 생성 방법'이 FILE 인 경우에 유효하다";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieFile.VALUE);
				
		String itemValue = (null == rsaPublickeyFile) ? "" : rsaPublickeyFile.getAbsolutePath();
		targetSequencedProperties.put(itemKey, itemValue);	
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieFile.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.FILE.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		String rsaPublickeyFileKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFile, KeyTypeOfConfieFile.FILE);
		String rsaPublickeyFileValue = "resources/rsa_keypair/codda.publickey";
		targetSequencedProperties.put(rsaPublickeyFileKey, rsaPublickeyFileValue);
	}
	
	public void fromPropertiesForRSAPrivatekeyFile(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieFile.VALUE);
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

	public void toPropertiesForRSAPrivatekeyFile(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 RSA 개인 파일, '세션키에 사용되는 공개키 키쌍 생성 방법'이 FILE 인 경우에 유효하다";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieFile.VALUE);		
		String itemValue = (null == rsaPrivatekeyFile) ? "" : rsaPrivatekeyFile.getAbsolutePath();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieFile.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.FILE.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		
		String rsaPrivatekeyFileKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFile, KeyTypeOfConfieFile.FILE);
		String rsaPrivatekeyFileValue = "resources/rsa_keypair/codda.privatekey";
		targetSequencedProperties.put(rsaPrivatekeyFileKey, rsaPrivatekeyFileValue);
	}
	
	
	public void fromPropertiesForRSAKeySize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieFile.VALUE);

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

	public void toPropertiesForRSAKeySize(SequencedProperties targetSequencedProperties) {		
		String rsaKeySizeSetValue = RunningProjectConfiguration.toSetValue(nativeValueConverterForRSAKeySize.getItemValueSet());
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용하는 공개키 크기, 단위 byte, default value[1024], the integer set[" + rsaKeySizeSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == rsaKeySize) ? "1024" : String.valueOf(rsaKeySize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieFile.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		
		String rsaKeySizeSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySize, KeyTypeOfConfieFile.SET);		
				
		targetSequencedProperties.put(rsaKeySizeSetKey, rsaKeySizeSetValue);
	}
	
	public void fromPropertiesForSymmetricKeyAlgorithm(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieFile.VALUE);

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

	public void toPropertiesForSymmetricKeyAlgorithm(SequencedProperties targetSequencedProperties) {		
		String symmetricKeyAlgorithmSetValue = RunningProjectConfiguration.toSetValue(nativeValueConverterForSymmetricKeyAlgorithm.getItemValueSet());
		String symmetricKeyAlgorithmSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieFile.SET);
		
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키 알고리즘, default value[AES], the string set[" + symmetricKeyAlgorithmSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == symmetricKeyAlgorithm) ? "AES" : symmetricKeyAlgorithm;
		targetSequencedProperties.put(itemKey, itemValue);		
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithm, KeyTypeOfConfieFile.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);	
				
				
		targetSequencedProperties.put(symmetricKeyAlgorithmSetKey, symmetricKeyAlgorithmSetValue);
	}
	
	
	public void fromPropertiesForSymmetricKeySize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieFile.VALUE);

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


	public void toPropertiesForSymmetricKeySize(SequencedProperties targetSequencedProperties) {		
		String symmetricKeySizeSetValue = RunningProjectConfiguration.toSetValue(nativeValueConverterForSymmetricKeySize.getItemValueSet());
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키 크기, default value[16], the integer set[" + symmetricKeySizeSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == symmetricKeySize) ? "16" : String.valueOf(symmetricKeySize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieFile.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);
		
		String symmetricKeySizeSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySize, KeyTypeOfConfieFile.SET);		
				
		targetSequencedProperties.put(symmetricKeySizeSetKey, symmetricKeySizeSetValue);
	}
	
	public void fromPropertiesForSymmetricIVSize(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSize, KeyTypeOfConfieFile.VALUE);

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

	public void toPropertiesForSymmetricIVSize(SequencedProperties targetSequencedProperties) {	
		String symmetricIVSizeSetValue = RunningProjectConfiguration.toSetValue(nativeValueConverterForSymmetricIVSize.getItemValueSet());
		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSize, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기, default value[16], the integer set[" + symmetricIVSizeSetValue + "]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSize, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == symmetricIVSize) ? "16" : String.valueOf(symmetricIVSize);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String symmetricIVSizeSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSize, KeyTypeOfConfieFile.SET);		
				
		targetSequencedProperties.put(symmetricIVSizeSetKey, symmetricIVSizeSetValue);
	}

	public SessionKey.RSAKeypairSourceType getRSAKeypairSource() {
		return rsaKeypairSource;
	}

	public void setRSAKeypairSource(SessionKey.RSAKeypairSourceType rsaKeypairSource) {
		this.rsaKeypairSource = rsaKeypairSource;
	}

	public File getRSAPublickeyFile() {
		return rsaPublickeyFile;
	}

	public void setRSAPublickeyFile(File rsaPublickeyFile) {		
		this.rsaPublickeyFile = rsaPublickeyFile;
	}

	public File getRSAPrivatekeyFile() {
		return rsaPrivatekeyFile;
	}

	public void setRSAPrivatekeyFile(File rsaPrivatekeyFile) {
		this.rsaPrivatekeyFile = rsaPrivatekeyFile;
	}

	public Integer getRSAKeySize() {
		return rsaKeySize;
	}

	public void setRSAKeySize(Integer rsaKeySize) {
		this.rsaKeySize = rsaKeySize;
	}

	public String getSymmetricKeyAlgorithm() {
		return symmetricKeyAlgorithm;
	}

	public void setSymmetricKeyAlgorithm(String symmetricKeyAlgorithm) {		
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
	}

	public Integer getSymmetricKeySize() {
		return symmetricKeySize;
	}

	public void setSymmetricKeySize(Integer symmetricKeySize) {
		this.symmetricKeySize = symmetricKeySize;
	}

	public Integer getSymmetricIVSize() {
		return symmetricIVSize;
	}

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
