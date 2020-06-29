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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningInteger;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningString;
import kr.pe.codda.common.config2.ConfigurationIF;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.GUIItemType;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class CommonPartConfiguration implements ConfigurationIF {

	private final Logger log = Logger.getLogger(CommonPartConfiguration.class.getName());

	public static final String itemIDForJDFMemberLoginPage = "jdf.member_login_page";
	private String jdfMemberLoginPage = null;

	public static final String itemIDForJDFAdminLoginPage = "jdf.admin_login_page";
	private String jdfAdminLoginPage = null;

	public static final String itemIDForJDFSessionKeyRedirectPage = "jdf.session_key_redirect_page";
	private String jdfSessionKeyRedirectPage = null;

	public static final String itemIDForJDFErrorMessagePage = "jdf.error_message_page";
	private String jdfErrorMessagePage = null;

	public static final String itemIDForJDFServletTrace = "jdf.servlet_trace";
	private Boolean jdfServletTrace = false;
	
	/*******************************************************************************************************/
	
	
	public static final String itemIDForRSAKeypairSourceOfSessionKey = "sessionkey.rsa.keypair_source";
	private SessionKey.RSAKeypairSourceType rsaKeypairSourceOfSessionKey = SessionKey.RSAKeypairSourceType.SERVER;	
	
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
	public void toValue(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		if (null == sourceSequencedProperties) {
			throw new IllegalArgumentException("the parameter sourceSequencedProperties is null");
		}

		/** JDF start */
		toValueForJDFMemberLoginPage(sourceSequencedProperties);
		toValueForJDFAdminLoginPage(sourceSequencedProperties);
		toValueForJDFSessionKeyRedirectPage(sourceSequencedProperties);
		toValueForJDFErrorMessagePage(sourceSequencedProperties);
		toValueForJDFServletTrace(sourceSequencedProperties);
		/** JDF end */
		
		
		/** session key start */
		toValueForRSAKeypairSourceOfSessionKey(sourceSequencedProperties);		
		if (SessionKey.RSAKeypairSourceType.FILE.equals(rsaKeypairSourceOfSessionKey)) {
			toValueForRSAPublickeyFileOfSessionKey(sourceSequencedProperties);
			toValueForRSAPrivatekeyFileOfSessionKey(sourceSequencedProperties);
		}
		toValueForRSAKeySizeOfSessionKey(sourceSequencedProperties);
		toValueForSymmetricKeyAlgorithmOfSessionKey(sourceSequencedProperties);
		toValueForSymmetricKeySizeOfSessionKey(sourceSequencedProperties);
		/** session key end */
		
		// FIXME!
	}

	@Override
	public void checkForDependencies(SequencedProperties sourceSequencedProperties) throws IllegalArgumentException, PartConfigurationException {
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
	}

	public void toValueForJDFMemberLoginPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForJDFMemberLoginPage).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForJDFMemberLoginPage).append(".desc").toString();
		String itemDescValue = "일반 회원용 사이트의 로그인 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = new StringBuilder().append(itemIDForJDFMemberLoginPage).append(".value").toString();
		String itemValue = (null == jdfMemberLoginPage) ? "" : jdfMemberLoginPage;
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForJDFMemberLoginPage).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}

	public void toValueForJDFAdminLoginPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForJDFAdminLoginPage).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForJDFAdminLoginPage).append(".desc").toString();
		String itemDescValue = "관리자 사이트의 로그인 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = new StringBuilder().append(itemIDForJDFAdminLoginPage).append(".value").toString();
		String itemValue = (null == jdfAdminLoginPage) ? "" : jdfAdminLoginPage;
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForJDFAdminLoginPage).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForJDFSessionKeyRedirectPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForJDFSessionKeyRedirectPage).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForJDFSessionKeyRedirectPage).append(".desc").toString();
		String itemDescValue = "세션키 없을때 가져오는 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = new StringBuilder().append(itemIDForJDFSessionKeyRedirectPage).append(".value").toString();
		String itemValue = (null == jdfSessionKeyRedirectPage) ? "" : jdfSessionKeyRedirectPage;
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForJDFSessionKeyRedirectPage).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForJDFErrorMessagePage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForJDFErrorMessagePage).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForJDFErrorMessagePage).append(".desc").toString();
		String itemDescValue = "JDF 서블릿 처리중 에러 발생시 에러 내용을 출력 해주는 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = new StringBuilder().append(itemIDForJDFErrorMessagePage).append(".value").toString();
		String itemValue = (null == jdfErrorMessagePage) ? "" : jdfErrorMessagePage;
		targetSequencedProperties.put(itemKey, itemValue);	
		
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForJDFErrorMessagePage).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForJDFServletTrace(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForJDFServletTrace).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForJDFServletTrace).append(".desc").toString();
		String itemDescValue = "JDF 서블릿의 응답 속도 추적 여부, 디폴트 false";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = new StringBuilder().append(itemIDForJDFServletTrace).append(".value").toString();
		String itemValue = jdfServletTrace.toString();
		targetSequencedProperties.put(itemKey, itemValue);		
		
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForJDFServletTrace).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForRSAKeypairSourceOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForRSAKeypairSourceOfSessionKey).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForRSAKeypairSourceOfSessionKey).append(".desc").toString();
		String itemDescValue = "세션키에 사용되는 공개키 키쌍 생성 방법, SERVER:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성, 디폴트:SERVER";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = new StringBuilder().append(itemIDForRSAKeypairSourceOfSessionKey).append(".value").toString();
		String itemValue = rsaKeypairSourceOfSessionKey.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForRSAKeypairSourceOfSessionKey).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForRSAPublickeyFileOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		
		
			String itemKey = new StringBuilder().append(itemIDForRSAPublickeyFileOfSessionKey).append(".value").toString();

			String itemValue = sourceSequencedProperties.getProperty(itemKey);

			if (null == itemValue) {
				String errorMessage = new StringBuilder().append("the item '").append(itemKey)
						.append("' does not exist in the parameter sourceSequencedProperties").toString();

				throw new PartConfigurationException(itemKey, errorMessage);
			}

			String itemConverterKey = new StringBuilder().append(itemIDForRSAPublickeyFileOfSessionKey).append(".converter")
					.toString();

			String itemConverterValue = sourceSequencedProperties.getProperty(itemConverterKey);

			if (null == itemConverterValue) {
				String errorMessage = new StringBuilder().append("the item '").append(itemConverterKey)
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
		String itemDescKey = new StringBuilder().append(itemIDForRSAPublickeyFileOfSessionKey).append(".desc").toString();
		String itemDescValue = "세션키에 사용되는 RSA 공개키 파일, '세션키에 사용되는 공개키 키쌍 생성 방법'이 FILE 인 경우에 유효하다";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = new StringBuilder().append(itemIDForRSAPublickeyFileOfSessionKey).append(".value").toString();		
		String itemValue = (null == rsaPublickeyFileOfSessionKey) ? "" : rsaPublickeyFileOfSessionKey.getAbsolutePath();
		targetSequencedProperties.put(itemKey, itemValue);
		
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForRSAPublickeyFileOfSessionKey).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.FILE.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);		
		
		
		String guiProjectHomeBaseRelativePathKey = new StringBuilder().append(itemIDForRSAPublickeyFileOfSessionKey).append(".file").toString();
		String guiProjectHomeBaseRelativePathValue = "resources/rsa_keypair/codda.publickey";
		targetSequencedProperties.put(guiProjectHomeBaseRelativePathKey, guiProjectHomeBaseRelativePathValue);
	}
	
	public void toValueForRSAPrivatekeyFileOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForRSAPrivatekeyFileOfSessionKey).append(".value").toString();
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
		String itemDescKey = new StringBuilder().append(itemIDForRSAPrivatekeyFileOfSessionKey).append(".desc").toString();
		String itemDescValue = "세션키에 사용되는 RSA 개인 파일, '세션키에 사용되는 공개키 키쌍 생성 방법'이 FILE 인 경우에 유효하다";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = new StringBuilder().append(itemIDForRSAPrivatekeyFileOfSessionKey).append(".value").toString();		
		String itemValue = (null == rsaPrivatekeyFileOfSessionKey) ? "" : rsaPrivatekeyFileOfSessionKey.getAbsolutePath();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForRSAPrivatekeyFileOfSessionKey).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.FILE.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);		
		
		
		String guiProjectHomeBaseRelativePathKey = new StringBuilder().append(itemIDForRSAPrivatekeyFileOfSessionKey).append(".file").toString();
		String guiProjectHomeBaseRelativePathValue = "resources/rsa_keypair/codda.privatekey";
		targetSequencedProperties.put(guiProjectHomeBaseRelativePathKey, guiProjectHomeBaseRelativePathValue);
	}
	
	
	public void toValueForRSAKeySizeOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForRSAKeySizeOfSessionKey).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForRSAKeySizeOfSessionKey).append(".desc").toString();
		String itemDescValue = "세션키에 사용하는 공개키 크기, 단위 byte, default value[1024], the integer set[512, 1024, 2048]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = new StringBuilder().append(itemIDForRSAKeySizeOfSessionKey).append(".value").toString();
		String itemValue = (null == rsaKeySizeOfSessionKey) ? "1024" : String.valueOf(rsaKeySizeOfSessionKey);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForRSAKeySizeOfSessionKey).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForSymmetricKeyAlgorithmOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForSymmetricKeyAlgorithmOfSessionKey).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForSymmetricKeyAlgorithmOfSessionKey).append(".desc").toString();
		String itemDescValue = "세션키에 사용되는 대칭키 알고리즘, default value[AES], the string set[DES, DESede, AES]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = new StringBuilder().append(itemIDForSymmetricKeyAlgorithmOfSessionKey).append(".value").toString();
		String itemValue = (null == symmetricKeyAlgorithmOfSessionKey) ? "AES" : symmetricKeyAlgorithmOfSessionKey;
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForSymmetricKeyAlgorithmOfSessionKey).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	
	public void toValueForSymmetricKeySizeOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForSymmetricKeySizeOfSessionKey).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForSymmetricKeySizeOfSessionKey).append(".desc").toString();
		String itemDescValue = "세션키에 사용되는 대칭키 크기, default value[16], the integer set[24, 16, 8]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = new StringBuilder().append(itemIDForSymmetricKeySizeOfSessionKey).append(".value").toString();
		String itemValue = (null == symmetricKeySizeOfSessionKey) ? "16" : String.valueOf(symmetricKeySizeOfSessionKey);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForSymmetricKeySizeOfSessionKey).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	public void toValueForSymmetricIVSizeOfSessionKey(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = new StringBuilder().append(itemIDForSymmetricIVSizeOfSessionKey).append(".value").toString();

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
		String itemDescKey = new StringBuilder().append(itemIDForSymmetricIVSizeOfSessionKey).append(".desc").toString();
		String itemDescValue = "세션키에 사용되는 대칭키와 쌍을 이루어 함께 사용되는 IV 크기, default value[16], the integer set[24, 16, 8]";
		targetSequencedProperties.put(itemDescKey, itemDescValue);

		String itemKey = new StringBuilder().append(itemIDForSymmetricIVSizeOfSessionKey).append(".value").toString();
		String itemValue = (null == symmetricIVSizeOfSessionKey) ? "16" : String.valueOf(symmetricIVSizeOfSessionKey);
		targetSequencedProperties.put(itemKey, itemValue);
		
		String guiItemTypeKey = new StringBuilder().append(itemIDForSymmetricIVSizeOfSessionKey).append(".gui_item_type").toString();
		String guiItemTypeValue = GUIItemType.DATA.name().toLowerCase();
		targetSequencedProperties.put(guiItemTypeKey, guiItemTypeValue);
	}
	
	// FIXME!
	
	public String getJdfMemberLoginPage() {
		return jdfMemberLoginPage;
	}

	public void setJdfMemberLoginPage(String jdfMemberLoginPage) {
		if (null == jdfMemberLoginPage) {
			String errorMessage = "the parameter jdfMemberLoginPage is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.jdfMemberLoginPage = jdfMemberLoginPage;
	}

	public String getJdfAdminLoginPage() {
		return jdfAdminLoginPage;
	}

	public void setJdfAdminLoginPage(String jdfAdminLoginPage) {
		if (null == jdfAdminLoginPage) {
			String errorMessage = "the parameter jdfAdminLoginPage is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.jdfAdminLoginPage = jdfAdminLoginPage;
	}

	public String getJdfSessionKeyRedirectPage() {
		return jdfSessionKeyRedirectPage;
	}

	public void setJdfSessionKeyRedirectPage(String jdfSessionKeyRedirectPage) {
		if (null == jdfSessionKeyRedirectPage) {
			String errorMessage = "the parameter jdfSessionKeyRedirectPage is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.jdfSessionKeyRedirectPage = jdfSessionKeyRedirectPage;
	}

	public String getJdfErrorMessagePage() {
		return jdfErrorMessagePage;
	}

	public void setJdfErrorMessagePage(String jdfErrorMessagePage) {
		if (null == jdfErrorMessagePage) {
			String errorMessage = "the parameter jdfErrorMessagePage is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.jdfErrorMessagePage = jdfErrorMessagePage;
	}

	public Boolean getJdfServletTrace() {
		return jdfServletTrace;
	}

	public void setJdfServletTrace(Boolean jdfServletTrace) {
		if (null == jdfServletTrace) {
			String errorMessage = "the parameter jdfServletTrace is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.jdfServletTrace = jdfServletTrace;
	}

	public SessionKey.RSAKeypairSourceType getRsaKeypairSourceOfSessionKey() {
		return rsaKeypairSourceOfSessionKey;
	}

	public void setRsaKeypairSourceOfSessionKey(SessionKey.RSAKeypairSourceType rsaKeypairSourceOfSessionKey) {
		if (null == rsaKeypairSourceOfSessionKey) {
			String errorMessage = "the parameter rsaKeypairSourceOfSessionKey is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.rsaKeypairSourceOfSessionKey = rsaKeypairSourceOfSessionKey;
	}

	public File getRsaPublickeyFileOfSessionKey() {
		return rsaPublickeyFileOfSessionKey;
	}

	public void setRsaPublickeyFileOfSessionKey(File rsaPublickeyFileOfSessionKey) {
		if (null == rsaPublickeyFileOfSessionKey) {
			String errorMessage = "the parameter rsaPublickeyFileOfSessionKey is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.rsaPublickeyFileOfSessionKey = rsaPublickeyFileOfSessionKey;
	}

	public File getRsaPrivatekeyFileOfSessionKey() {
		return rsaPrivatekeyFileOfSessionKey;
	}

	public void setRsaPrivatekeyFileOfSessionKey(File rsaPrivatekeyFileOfSessionKey) {
		if (null == rsaPrivatekeyFileOfSessionKey) {
			String errorMessage = "the parameter rsaPrivatekeyFileOfSessionKey is null";
			throw new IllegalArgumentException(errorMessage);
		}		
		
		this.rsaPrivatekeyFileOfSessionKey = rsaPrivatekeyFileOfSessionKey;
	}

	public Integer getRsaKeySizeOfSessionKey() {
		return rsaKeySizeOfSessionKey;
	}

	public void setRsaKeySizeOfSessionKey(Integer rsaKeySizeOfSessionKey) {
		if (null == rsaKeySizeOfSessionKey) {
			String errorMessage = "the parameter rsaKeySizeOfSessionKey is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.rsaKeySizeOfSessionKey = rsaKeySizeOfSessionKey;
	}

	public String getSymmetricKeyAlgorithmOfSessionKey() {
		return symmetricKeyAlgorithmOfSessionKey;
	}

	public void setSymmetricKeyAlgorithmOfSessionKey(String symmetricKeyAlgorithmOfSessionKey) {
		if (null == symmetricKeyAlgorithmOfSessionKey) {
			String errorMessage = "the parameter symmetricKeyAlgorithmOfSessionKey is null";
			throw new IllegalArgumentException(errorMessage);
		}		
		
		this.symmetricKeyAlgorithmOfSessionKey = symmetricKeyAlgorithmOfSessionKey;
	}

	public Integer getSymmetricKeySizeOfSessionKey() {
		return symmetricKeySizeOfSessionKey;
	}

	public void setSymmetricKeySizeOfSessionKey(Integer symmetricKeySizeOfSessionKey) {
		if (null == symmetricKeySizeOfSessionKey) {
			String errorMessage = "the parameter symmetricKeySizeOfSessionKey is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.symmetricKeySizeOfSessionKey = symmetricKeySizeOfSessionKey;
	}

	public Integer getSymmetricIVSizeOfSessionKey() {
		return symmetricIVSizeOfSessionKey;
	}

	public void setSymmetricIVSizeOfSessionKey(Integer symmetricIVSizeOfSessionKey) {
		if (null == symmetricIVSizeOfSessionKey) {
			String errorMessage = "the parameter symmetricIVSizeOfSessionKey is null";
			throw new IllegalArgumentException(errorMessage);
		}		
		
		this.symmetricIVSizeOfSessionKey = symmetricIVSizeOfSessionKey;
	}
}
