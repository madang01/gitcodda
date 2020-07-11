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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningInteger;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningString;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.GUIItemType;
import kr.pe.codda.common.type.KeyTypeOfConfieFile;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class CommonPartConfiguration implements PartConfigurationIF {

	private final Logger log = Logger.getLogger(CommonPartConfiguration.class.getName());
	
	private final String prefixBeforeItemID = "";

	public static final String itemIDForJDFMemberLoginPage = "jdf.member_login_page";
	private String jdfMemberLoginPage = null;

	public static final String itemIDForJDFAdminLoginPage = "jdf.admin_login_page";
	private String jdfAdminLoginPage = null;

	public static final String itemIDForJDFSessionKeyRedirectPage = "jdf.session_key_redirect_page";
	private String jdfSessionKeyRedirectPage = null;

	public static final String itemIDForJDFErrorMessagePage = "jdf.error_message_page";
	private String jdfErrorMessagePage = null;

	public static final String itemIDForJDFServletTrace = "jdf.servlet_trace";
	private Boolean jdfServletTrace = null;
	
	/*******************************************************************************************************/
	
	
	public static final String itemIDForRSAKeypairSourceOfSessionKey = "sessionkey.rsa.keypair_source";
	private SessionKey.RSAKeypairSourceType rsaKeypairSourceOfSessionKey = null;	
	
	public static final String itemIDForRSAPublickeyFileOfSessionKey = "sessionkey.rsa.publickey.file";
	private File rsaPublickeyFileOfSessionKey = null;
	
		
	public static final String itemIDForRSAPrivatekeyFileOfSessionKey = "sessionkey.rsa.privatekey.file";
	private File rsaPrivatekeyFileOfSessionKey = null;
	
	public static final String itemIDForRSAKeySizeOfSessionKey = "sessionkey.rsa.keysize";	
	private Integer rsaKeySizeOfSessionKey = null;	
	
	
	public static final String itemIDForSymmetricKeyAlgorithmOfSessionKey = "sessionkey.symmetric_key.algorithm";
	private String symmetricKeyAlgorithmOfSessionKey = null;
	
	public static final String itemIDForSymmetricKeySizeOfSessionKey = "sessionkey.symmetric_key.size";	
	private Integer symmetricKeySizeOfSessionKey=null;
		
	public static final String itemIDForSymmetricIVSizeOfSessionKey = "sessionkey.iv_size";
	private Integer symmetricIVSizeOfSessionKey=null;
	
	
	/*******************************************************************************************************/
	
	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		if (null == sourceSequencedProperties) {
			throw new IllegalArgumentException("the parameter sourceSequencedProperties is null");
		}

		/** JDF start */
		fromPropertiesForJDFMemberLoginPage(sourceSequencedProperties);
		fromPropertiesForJDFAdminLoginPage(sourceSequencedProperties);
		fromPropertiesForJDFSessionKeyRedirectPage(sourceSequencedProperties);
		fromPropertiesForJDFErrorMessagePage(sourceSequencedProperties);
		fromPropertiesForJDFServletTrace(sourceSequencedProperties);
		/** JDF end */
		
		
		/** session key start */
		fromPropertiesForRSAKeypairSourceOfSessionKey(sourceSequencedProperties);		
		if (SessionKey.RSAKeypairSourceType.FILE.equals(rsaKeypairSourceOfSessionKey)) {
			fromPropertiesForRSAPublickeyFileOfSessionKey(sourceSequencedProperties);
			fromPropertiesForRSAPrivatekeyFileOfSessionKey(sourceSequencedProperties);
		}
		fromPropertiesForRSAKeySizeOfSessionKey(sourceSequencedProperties);
		fromPropertiesForSymmetricIVSizeOfSessionKey(sourceSequencedProperties);
		fromPropertiesForSymmetricKeyAlgorithmOfSessionKey(sourceSequencedProperties);
		fromPropertiesForSymmetricKeySizeOfSessionKey(sourceSequencedProperties);
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

		toPropertiesForJDFMemberLoginPage(targetSequencedProperties);
		toPropertiesForJDFAdminLoginPage(targetSequencedProperties);
		toPropertiesForJDFSessionKeyRedirectPage(targetSequencedProperties);
		toPropertiesForJDFErrorMessagePage(targetSequencedProperties);
		toPropertiesForJDFServletTrace(targetSequencedProperties);		
		
		toPropertiesForRSAKeypairSourceOfSessionKey(targetSequencedProperties);
		toPropertiesForRSAPublickeyFileOfSessionKey(targetSequencedProperties);
		toPropertiesForRSAPrivatekeyFileOfSessionKey(targetSequencedProperties);
		toPropertiesForRSAKeySizeOfSessionKey(targetSequencedProperties);
		toPropertiesForSymmetricKeyAlgorithmOfSessionKey(targetSequencedProperties);
		toPropertiesForSymmetricKeySizeOfSessionKey(targetSequencedProperties);
		toPropertiesForSymmetricIVSizeOfSessionKey(targetSequencedProperties);
	}

	public void fromPropertiesForJDFMemberLoginPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFMemberLoginPage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}


		GeneralConverterReturningNoTrimString nativeValueConverter = new GeneralConverterReturningNoTrimString();
		
		try {

			jdfMemberLoginPage = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForJDFMemberLoginPage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFMemberLoginPage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "일반 회원용 사이트의 로그인 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFMemberLoginPage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == jdfMemberLoginPage) ? "/sitemenu/member/MemberLoginInput.jsp" : jdfMemberLoginPage;
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForJDFAdminLoginPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFAdminLoginPage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningNoTrimString nativeValueConverter = new GeneralConverterReturningNoTrimString();
		
		try {
			jdfAdminLoginPage = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForJDFAdminLoginPage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFAdminLoginPage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "관리자 사이트의 로그인 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFAdminLoginPage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == jdfAdminLoginPage) ? "/sitemenu/member/AdminLoginInput.jsp" : jdfAdminLoginPage;
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForJDFSessionKeyRedirectPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFSessionKeyRedirectPage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		
		GeneralConverterReturningNoTrimString nativeValueConverter = new GeneralConverterReturningNoTrimString();
		
		try {
			jdfSessionKeyRedirectPage = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForJDFSessionKeyRedirectPage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFSessionKeyRedirectPage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키 없을때 가져오는 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFSessionKeyRedirectPage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == jdfSessionKeyRedirectPage) ? "/sessionKeyRedirect.jsp" : jdfSessionKeyRedirectPage;
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForJDFErrorMessagePage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFErrorMessagePage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
		
		GeneralConverterReturningNoTrimString nativeValueConverter = new GeneralConverterReturningNoTrimString();		
		try {

			jdfErrorMessagePage = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForJDFErrorMessagePage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFErrorMessagePage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "JDF 서블릿 처리중 에러 발생시 에러 내용을 출력 해주는 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFErrorMessagePage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == jdfErrorMessagePage) ? "/errorMessagePage.jsp" : jdfErrorMessagePage;
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForJDFServletTrace(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFServletTrace, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		SetTypeConverterReturningBoolean nativeValueConverter = new SetTypeConverterReturningBoolean();
		
		try {
			jdfServletTrace = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningBoolean.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForJDFServletTrace(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFServletTrace, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "JDF 서블릿의 응답 속도 추적 여부, 디폴트 false";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForJDFServletTrace, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == jdfServletTrace) ? Boolean.FALSE.toString() : jdfServletTrace.toString();
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForRSAKeypairSourceOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSourceOfSessionKey, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		
		SetTypeConverterOfSessionKeyRSAKeypairSource nativeValueConverter = new SetTypeConverterOfSessionKeyRSAKeypairSource();

		
		try {
			rsaKeypairSourceOfSessionKey = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterOfSessionKeyRSAKeypairSource.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}
	

	public void toPropertiesForRSAKeypairSourceOfSessionKey(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSourceOfSessionKey, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 공개키 키쌍 생성 방법, SERVER:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성, 디폴트:SERVER";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeypairSourceOfSessionKey, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == rsaKeypairSourceOfSessionKey) ? SessionKey.RSAKeypairSourceType.SERVER.toString() : rsaKeypairSourceOfSessionKey.toString();
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForRSAPublickeyFileOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		
			String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
					prefixBeforeItemID, itemIDForRSAPublickeyFileOfSessionKey, KeyTypeOfConfieFile.VALUE);

			String itemValue = sourceSequencedProperties.getProperty(itemKey);

			if (null == itemValue) {
				String errorMessage = new StringBuilder().append("the item '").append(itemKey)
						.append("' does not exist in the parameter sourceSequencedProperties").toString();

				throw new PartConfigurationException(itemKey, errorMessage);
			}

			GeneralConverterReturningRegularFile nativeValueConverter = new GeneralConverterReturningRegularFile(false);

			try {
				rsaPublickeyFileOfSessionKey = nativeValueConverter.valueOf(itemValue);
			} catch (Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
						.append("] value[").append(itemValue).append("] to value using the value converter[")
						.append(GeneralConverterReturningRegularFile.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

				log.log(Level.WARNING, errorMessage, e);

				throw new PartConfigurationException(itemKey, errorMessage);
			}
	}

	public void toPropertiesForRSAPublickeyFileOfSessionKey(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFileOfSessionKey, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 RSA 공개키 파일, '세션키에 사용되는 공개키 키쌍 생성 방법'이 FILE 인 경우에 유효하다";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFileOfSessionKey, KeyTypeOfConfieFile.VALUE);
				
		String itemValue = (null == rsaPublickeyFileOfSessionKey) ? "" : rsaPublickeyFileOfSessionKey.getAbsolutePath();
		targetSequencedProperties.put(itemKey, itemValue);
		
		
		String guiItemTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFileOfSessionKey, KeyTypeOfConfieFile.GUI_ITEM_TYPE);
		String guiItemTypeValue = GUIItemType.FILE.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);		
		
		
		String guiProjectHomeBaseRelativePathKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPublickeyFileOfSessionKey, KeyTypeOfConfieFile.FILE);
		String guiProjectHomeBaseRelativePathValue = "resources/rsa_keypair/codda.publickey";
		targetSequencedProperties.put(guiProjectHomeBaseRelativePathKey, guiProjectHomeBaseRelativePathValue);
	}
	
	public void fromPropertiesForRSAPrivatekeyFileOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFileOfSessionKey, KeyTypeOfConfieFile.VALUE);
		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		GeneralConverterReturningRegularFile nativeValueConverter = new GeneralConverterReturningRegularFile(false);		
		try {
			rsaPrivatekeyFileOfSessionKey = nativeValueConverter.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningRegularFile.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForRSAPrivatekeyFileOfSessionKey(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFileOfSessionKey, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 RSA 개인 파일, '세션키에 사용되는 공개키 키쌍 생성 방법'이 FILE 인 경우에 유효하다";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFileOfSessionKey, KeyTypeOfConfieFile.VALUE);		
		String itemValue = (null == rsaPrivatekeyFileOfSessionKey) ? "" : rsaPrivatekeyFileOfSessionKey.getAbsolutePath();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFileOfSessionKey, KeyTypeOfConfieFile.GUI_ITEM_TYPE);
		String guiItemTypeValue = GUIItemType.FILE.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);		
		
		
		String guiProjectHomeBaseRelativePathKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAPrivatekeyFileOfSessionKey, KeyTypeOfConfieFile.FILE);
		String guiProjectHomeBaseRelativePathValue = "resources/rsa_keypair/codda.privatekey";
		targetSequencedProperties.put(guiProjectHomeBaseRelativePathKey, guiProjectHomeBaseRelativePathValue);
	}
	
	
	public void fromPropertiesForRSAKeySizeOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySizeOfSessionKey, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		

		SetTypeConverterReturningInteger nativeValueConverter = new SetTypeConverterReturningInteger("512", "1024", "2048");

		try {
			rsaKeySizeOfSessionKey = nativeValueConverter.valueOf(itemValue);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningInteger.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}
	


	public void toPropertiesForRSAKeySizeOfSessionKey(SequencedProperties targetSequencedProperties) {		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySizeOfSessionKey, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용하는 공개키 크기, 단위 byte, default value[1024], the integer set[512, 1024, 2048]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForRSAKeySizeOfSessionKey, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == rsaKeySizeOfSessionKey) ? "1024" : String.valueOf(rsaKeySizeOfSessionKey);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForSymmetricKeyAlgorithmOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithmOfSessionKey, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		

		SetTypeConverterReturningString nativeValueConverter = new SetTypeConverterReturningString("AES", "DESede", "DES");

		try {
			symmetricKeyAlgorithmOfSessionKey = nativeValueConverter.valueOf(itemValue);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningInteger.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForSymmetricKeyAlgorithmOfSessionKey(SequencedProperties targetSequencedProperties) {		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithmOfSessionKey, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키 알고리즘, default value[AES], the string set[DES, DESede, AES]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeyAlgorithmOfSessionKey, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == symmetricKeyAlgorithmOfSessionKey) ? "AES" : symmetricKeyAlgorithmOfSessionKey;
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	public void fromPropertiesForSymmetricKeySizeOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySizeOfSessionKey, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		

		SetTypeConverterReturningInteger nativeValueConverter = new SetTypeConverterReturningInteger("8", "16", "24");

		try {
			symmetricKeySizeOfSessionKey = nativeValueConverter.valueOf(itemValue);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningInteger.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}	


	public void toPropertiesForSymmetricKeySizeOfSessionKey(SequencedProperties targetSequencedProperties) {		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySizeOfSessionKey, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키 크기, default value[16], the integer set[24, 16, 8]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricKeySizeOfSessionKey, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == symmetricKeySizeOfSessionKey) ? "16" : String.valueOf(symmetricKeySizeOfSessionKey);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForSymmetricIVSizeOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSizeOfSessionKey, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		

		SetTypeConverterReturningInteger nativeValueConverter = new SetTypeConverterReturningInteger("8", "16", "24");

		try {
			symmetricIVSizeOfSessionKey = nativeValueConverter.valueOf(itemValue);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningInteger.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForSymmetricIVSizeOfSessionKey(SequencedProperties targetSequencedProperties) {		
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSizeOfSessionKey, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기, default value[16], the integer set[24, 16, 8]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSymmetricIVSizeOfSessionKey, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == symmetricIVSizeOfSessionKey) ? "16" : String.valueOf(symmetricIVSizeOfSessionKey);
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	
	public String getJDFMemberLoginPage() {
		return jdfMemberLoginPage;
	}

	public void setJDFMemberLoginPage(String jdfMemberLoginPage) {
		
		this.jdfMemberLoginPage = jdfMemberLoginPage;
	}

	public String getJDFAdminLoginPage() {
		return jdfAdminLoginPage;
	}

	public void setJDFAdminLoginPage(String jdfAdminLoginPage) {		
		this.jdfAdminLoginPage = jdfAdminLoginPage;
	}

	public String getJDFSessionKeyRedirectPage() {
		return jdfSessionKeyRedirectPage;
	}

	public void setJDFSessionKeyRedirectPage(String jdfSessionKeyRedirectPage) {		
		
		this.jdfSessionKeyRedirectPage = jdfSessionKeyRedirectPage;
	}

	public String getJDFErrorMessagePage() {
		return jdfErrorMessagePage;
	}

	public void setJDFErrorMessagePage(String jdfErrorMessagePage) {
		
		this.jdfErrorMessagePage = jdfErrorMessagePage;
	}

	public Boolean getJDFServletTrace() {
		return jdfServletTrace;
	}

	public void setJDFServletTrace(Boolean jdfServletTrace) {
		this.jdfServletTrace = jdfServletTrace;
	}

	public SessionKey.RSAKeypairSourceType getRSAKeypairSourceOfSessionKey() {
		return rsaKeypairSourceOfSessionKey;
	}

	public void setRSAKeypairSourceOfSessionKey(SessionKey.RSAKeypairSourceType rsaKeypairSourceOfSessionKey) {
		this.rsaKeypairSourceOfSessionKey = rsaKeypairSourceOfSessionKey;
	}

	public File getRSAPublickeyFileOfSessionKey() {
		return rsaPublickeyFileOfSessionKey;
	}

	public void setRSAPublickeyFileOfSessionKey(File rsaPublickeyFileOfSessionKey) {		
		this.rsaPublickeyFileOfSessionKey = rsaPublickeyFileOfSessionKey;
	}

	public File getRSAPrivatekeyFileOfSessionKey() {
		return rsaPrivatekeyFileOfSessionKey;
	}

	public void setRSAPrivatekeyFileOfSessionKey(File rsaPrivatekeyFileOfSessionKey) {
		this.rsaPrivatekeyFileOfSessionKey = rsaPrivatekeyFileOfSessionKey;
	}

	public Integer getRSAKeySizeOfSessionKey() {
		return rsaKeySizeOfSessionKey;
	}

	public void setRSAKeySizeOfSessionKey(Integer rsaKeySizeOfSessionKey) {
		this.rsaKeySizeOfSessionKey = rsaKeySizeOfSessionKey;
	}

	public String getSymmetricKeyAlgorithmOfSessionKey() {
		return symmetricKeyAlgorithmOfSessionKey;
	}

	public void setSymmetricKeyAlgorithmOfSessionKey(String symmetricKeyAlgorithmOfSessionKey) {		
		this.symmetricKeyAlgorithmOfSessionKey = symmetricKeyAlgorithmOfSessionKey;
	}

	public Integer getSymmetricKeySizeOfSessionKey() {
		return symmetricKeySizeOfSessionKey;
	}

	public void setSymmetricKeySizeOfSessionKey(Integer symmetricKeySizeOfSessionKey) {
		this.symmetricKeySizeOfSessionKey = symmetricKeySizeOfSessionKey;
	}

	public Integer getSymmetricIVSizeOfSessionKey() {
		return symmetricIVSizeOfSessionKey;
	}

	public void setSymmetricIVSizeOfSessionKey(Integer symmetricIVSizeOfSessionKey) {		
		this.symmetricIVSizeOfSessionKey = symmetricIVSizeOfSessionKey;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommonPartConfiguration [jdfMemberLoginPage=");
		builder.append(jdfMemberLoginPage);
		builder.append(", jdfAdminLoginPage=");
		builder.append(jdfAdminLoginPage);
		builder.append(", jdfSessionKeyRedirectPage=");
		builder.append(jdfSessionKeyRedirectPage);
		builder.append(", jdfErrorMessagePage=");
		builder.append(jdfErrorMessagePage);
		builder.append(", jdfServletTrace=");
		builder.append(jdfServletTrace);
		builder.append(", rsaKeypairSourceOfSessionKey=");
		builder.append(rsaKeypairSourceOfSessionKey);
		builder.append(", rsaPublickeyFileOfSessionKey=");
		builder.append(rsaPublickeyFileOfSessionKey);
		builder.append(", rsaPrivatekeyFileOfSessionKey=");
		builder.append(rsaPrivatekeyFileOfSessionKey);
		builder.append(", rsaKeySizeOfSessionKey=");
		builder.append(rsaKeySizeOfSessionKey);
		builder.append(", symmetricKeyAlgorithmOfSessionKey=");
		builder.append(symmetricKeyAlgorithmOfSessionKey);
		builder.append(", symmetricKeySizeOfSessionKey=");
		builder.append(symmetricKeySizeOfSessionKey);
		builder.append(", symmetricIVSizeOfSessionKey=");
		builder.append(symmetricIVSizeOfSessionKey);
		builder.append("]");
		return builder.toString();
	}
	
	
}
