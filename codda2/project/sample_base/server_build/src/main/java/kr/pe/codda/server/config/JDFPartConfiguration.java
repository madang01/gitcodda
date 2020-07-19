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
package kr.pe.codda.server.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.codda.common.config.part.PartConfigurationIF;
import kr.pe.codda.common.config.part.RunningProjectConfiguration;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.type.ItemViewType;
import kr.pe.codda.common.type.KeyTypeOfConfieFile;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * @author Won Jonghoon
 *
 */
public class JDFPartConfiguration implements PartConfigurationIF {
	private final Logger log = Logger.getLogger(JDFPartConfiguration.class.getName());
	
	private final String partName = "jdf";
	
	private final String prefixBeforeItemID = new StringBuilder().append(partName).append(".").toString();

	public static final String itemIDForMemberLoginPage = "member_login_page";
	private GeneralConverterReturningNoTrimString nativeValueConverterForMemberLoginPage = new GeneralConverterReturningNoTrimString();
	private String memberLoginPage = null;

	public static final String itemIDForAdminLoginPage = "admin_login_page";
	GeneralConverterReturningNoTrimString nativeValueConverterForAdminLoginPage = new GeneralConverterReturningNoTrimString();
	private String adminLoginPage = null;

	public static final String itemIDForSessionKeyRedirectPage = "session_key_redirect_page";
	GeneralConverterReturningNoTrimString nativeValueConverterForSessionKeyRedirectPage = new GeneralConverterReturningNoTrimString();
	private String jdfSessionKeyRedirectPage = null;

	public static final String itemIDForErrorMessagePage = "error_message_page";
	GeneralConverterReturningNoTrimString nativeValueConverterForErrorMessagePage = new GeneralConverterReturningNoTrimString();	
	private String errorMessagePage = null;

	public static final String itemIDForServletTrace = "servlet_trace";
	SetTypeConverterReturningBoolean nativeValueConverterForServletTrace = new SetTypeConverterReturningBoolean();
	private Boolean servletTrace = null;
	
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

		fromPropertiesForMemberLoginPage(sourceSequencedProperties);
		fromPropertiesForAdminLoginPage(sourceSequencedProperties);
		fromPropertiesForSessionKeyRedirectPage(sourceSequencedProperties);
		fromPropertiesForErrorMessagePage(sourceSequencedProperties);
		fromPropertiesForServletTrace(sourceSequencedProperties);		
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

		toPropertiesForMemberLoginPage(targetSequencedProperties);
		toPropertiesForAdminLoginPage(targetSequencedProperties);
		toPropertiesForSessionKeyRedirectPage(targetSequencedProperties);
		toPropertiesForErrorMessagePage(targetSequencedProperties);
		toPropertiesForServletTrace(targetSequencedProperties);
	}

	public void fromPropertiesForMemberLoginPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForMemberLoginPage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}
		
		try {

			memberLoginPage = nativeValueConverterForMemberLoginPage.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForMemberLoginPage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForMemberLoginPage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "일반 회원용 사이트의 로그인 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);		
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForMemberLoginPage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == memberLoginPage) ? "/sitemenu/member/MemberLoginInput.jsp" : memberLoginPage;
		targetSequencedProperties.put(itemKey, itemValue);
	}

	public void fromPropertiesForAdminLoginPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForAdminLoginPage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		
		
		try {
			adminLoginPage = nativeValueConverterForAdminLoginPage.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForAdminLoginPage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForAdminLoginPage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "관리자 사이트의 로그인 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForAdminLoginPage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == adminLoginPage) ? "/sitemenu/member/AdminLoginInput.jsp" : adminLoginPage;
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForSessionKeyRedirectPage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSessionKeyRedirectPage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}

		
		
		
		try {
			jdfSessionKeyRedirectPage = nativeValueConverterForSessionKeyRedirectPage.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForSessionKeyRedirectPage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSessionKeyRedirectPage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "세션키 없을때 가져오는 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForSessionKeyRedirectPage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == jdfSessionKeyRedirectPage) ? "/sessionKeyRedirect.jsp" : jdfSessionKeyRedirectPage;
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForErrorMessagePage(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForErrorMessagePage, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		
			
		try {

			errorMessagePage = nativeValueConverterForErrorMessagePage.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(GeneralConverterReturningNoTrimString.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForErrorMessagePage(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForErrorMessagePage, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "JDF 서블릿 처리중 에러 발생시 에러 내용을 출력 해주는 페이지 URL, 루트로 시작하는 절대경로 권장함";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForErrorMessagePage, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == errorMessagePage) ? "/errorMessagePage.jsp" : errorMessagePage;
		targetSequencedProperties.put(itemKey, itemValue);
	}
	
	public void fromPropertiesForServletTrace(SequencedProperties sourceSequencedProperties)
			throws PartConfigurationException {
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServletTrace, KeyTypeOfConfieFile.VALUE);

		String itemValue = sourceSequencedProperties.getProperty(itemKey);

		if (null == itemValue) {
			String errorMessage = new StringBuilder().append("the item '").append(itemKey)
					.append("' does not exist in the parameter sourceSequencedProperties").toString();

			throw new PartConfigurationException(itemKey, errorMessage);
		}		
		
		try {
			servletTrace = nativeValueConverterForServletTrace.valueOf(itemValue);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to converter the parameter sequencedProperties's item[").append(itemKey)
					.append("] value[").append(itemValue).append("] to value using the value converter[")
					.append(SetTypeConverterReturningBoolean.class.getName()).append("], errmsg=").append(e.getMessage()).toString();

			log.log(Level.WARNING, errorMessage, e);

			throw new PartConfigurationException(itemKey, errorMessage);
		}
	}

	public void toPropertiesForServletTrace(SequencedProperties targetSequencedProperties) {
		String itemDescKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServletTrace, KeyTypeOfConfieFile.DESC);
		String itemDescValue = "JDF 서블릿의 응답 속도 추적 여부, 디폴트 false";
		targetSequencedProperties.put(itemDescKey, itemDescValue);
		
		String itemKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServletTrace, KeyTypeOfConfieFile.VALUE);
		String itemValue = (null == servletTrace) ? Boolean.FALSE.toString() : servletTrace.toString();
		targetSequencedProperties.put(itemKey, itemValue);
		
		String itemViewTypeKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServletTrace, KeyTypeOfConfieFile.ITEM_VIEW_TYPE);
		String itemViewTypeValue = ItemViewType.SET.name().toLowerCase();
		targetSequencedProperties.put(itemViewTypeKey, itemViewTypeValue);		
		
		
		String servletTraceSetKey = RunningProjectConfiguration.buildKeyOfConfigFile(
				prefixBeforeItemID, itemIDForServletTrace, KeyTypeOfConfieFile.SET);
		String servletTraceSetValue = RunningProjectConfiguration.toSetValue(nativeValueConverterForServletTrace.getItemValueSet());
				
		targetSequencedProperties.put(servletTraceSetKey, servletTraceSetValue);
	}
	
	
	public String getJDFMemberLoginPage() {
		return memberLoginPage;
	}

	public void setJDFMemberLoginPage(String jdfMemberLoginPage) {
		
		this.memberLoginPage = jdfMemberLoginPage;
	}

	public String getJDFAdminLoginPage() {
		return adminLoginPage;
	}

	public void setJDFAdminLoginPage(String jdfAdminLoginPage) {		
		this.adminLoginPage = jdfAdminLoginPage;
	}

	public String getJDFSessionKeyRedirectPage() {
		return jdfSessionKeyRedirectPage;
	}

	public void setJDFSessionKeyRedirectPage(String jdfSessionKeyRedirectPage) {		
		
		this.jdfSessionKeyRedirectPage = jdfSessionKeyRedirectPage;
	}

	public String getJDFErrorMessagePage() {
		return errorMessagePage;
	}

	public void setJDFErrorMessagePage(String jdfErrorMessagePage) {
		
		this.errorMessagePage = jdfErrorMessagePage;
	}

	public Boolean getJDFServletTrace() {
		return servletTrace;
	}

	public void setJDFServletTrace(Boolean jdfServletTrace) {
		this.servletTrace = jdfServletTrace;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommonPartConfiguration [jdfMemberLoginPage=");
		builder.append(memberLoginPage);
		builder.append(", jdfAdminLoginPage=");
		builder.append(adminLoginPage);
		builder.append(", jdfSessionKeyRedirectPage=");
		builder.append(jdfSessionKeyRedirectPage);
		builder.append(", jdfErrorMessagePage=");
		builder.append(errorMessagePage);
		builder.append(", jdfServletTrace=");
		builder.append(servletTrace);
		builder.append("]");
		return builder.toString();
	}
	
	
}
