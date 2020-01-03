/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package kr.pe.codda.common.config.itemidinfo;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.config.AbstractDependencyValidator;
import kr.pe.codda.common.config.AbstractDisabledItemChecker;
import kr.pe.codda.common.config.dependoninactivechecker.RSAKeyFileDisabledItemChecker;
import kr.pe.codda.common.config.dependonvalidchecker.MinAndMaxDependencyValidator;
import kr.pe.codda.common.config.fileorpathstringgetter.AbstractFileOrPathStringReturner;
import kr.pe.codda.common.config.fileorpathstringgetter.DBCPConfigFilePathStringReturner;
import kr.pe.codda.common.config.fileorpathstringgetter.SessionkeyRSAPrivatekeyFilePathStringReturner;
import kr.pe.codda.common.config.fileorpathstringgetter.SessionkeyRSAPublickeyFilePathStringReturner;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo.ConfigurationPart;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningCharset;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningByteOrder;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningConnectionType;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningInteger;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningMessageProtocolType;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningString;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * 환경 설정 정보 클래스. 언어 종속적인 타입으로 변환할 정보, 특정 항목의 값에 영향을 받는 의존 관계 정보, 특정 항목의 특정
 * 값들에 의해서 비활성화 되는 정보를 구축한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ItemIDInfoManger {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	private List<ItemIDInfo<?>> itemIDInfoList = new ArrayList<ItemIDInfo<?>>();

	private Map<String, ItemIDInfo<?>> itemIDInfoHash = new HashMap<String, ItemIDInfo<?>>();

	private Map<String, AbstractDisabledItemChecker> diabledItemCheckerHash = new HashMap<String, AbstractDisabledItemChecker>();
	private Map<String, AbstractDependencyValidator> dependencyValidationHash = new HashMap<String, AbstractDependencyValidator>();
	private Map<String, AbstractFileOrPathStringReturner> fileOrPathStringRetunerHash = new HashMap<String, AbstractFileOrPathStringReturner>();

	private List<ItemIDInfo<?>> dbcpPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();
	private List<ItemIDInfo<?>> commonPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();
	private List<ItemIDInfo<?>> projectPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class ItemIDInfoMangerHolder {
		static final ItemIDInfoManger singleton = new ItemIDInfoManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static ItemIDInfoManger getInstance() {
		return ItemIDInfoMangerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private ItemIDInfoManger() {
		try {
			addAllDBCPPartItemIDInfo();
		} catch (Exception e) {
			log.log(Level.SEVERE, "fail to add all of dbcp part item identification informtion", e);
			System.exit(1);
		}
		try {
			addAllCommonPartItemIDInfo();
		} catch (Exception e) {
			log.log(Level.SEVERE, "fail to add all of common part item identification informtion", e);
			System.exit(1);
		}
		try {
			addAllProjectPartItemIDInfo();
		} catch (Exception e) {
			log.log(Level.SEVERE, "fail to add all of project part item identification informtion", e);
			System.exit(1);
		}

		try {
			addDependencyValidation();
		} catch (Exception e) {
			log.log(Level.SEVERE, "fail to add valid checker", e);
			System.exit(1);
		}
		try {
			addAllDisabledItemChecker();
		} catch (Exception e) {
			log.log(Level.SEVERE, "fail to add inactive checker", e);
			System.exit(1);
		}

		try {
			addAllFileOrPathStringReturner();
		} catch (Exception e) {
			log.log(Level.SEVERE, "fail to add inactive checker", e);
			System.exit(1);
		}

		itemIDInfoList = Collections.unmodifiableList(itemIDInfoList);
		itemIDInfoHash = Collections.unmodifiableMap(itemIDInfoHash);

		diabledItemCheckerHash = Collections.unmodifiableMap(diabledItemCheckerHash);
		dependencyValidationHash = Collections.unmodifiableMap(dependencyValidationHash);

		dbcpPartItemIDInfoList = Collections.unmodifiableList(dbcpPartItemIDInfoList);
		commonPartItemIDInfoList = Collections.unmodifiableList(commonPartItemIDInfoList);
		projectPartItemIDInfoList = Collections.unmodifiableList(projectPartItemIDInfoList);
	}

	/**
	 * 모든 공통 파트의 항목 식별자 정보를 등록시킴
	 * 
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	private void addAllCommonPartItemIDInfo() throws Exception {
		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;
		boolean isDefaultValueCheck = false;

		/** Common start */

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.JDF_MEMBER_LOGIN_PAGE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<String>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.TEXT, itemID,
				"일반 유저용 로그인 입력 페이지", "/jsp/member/userLoginInput.jsp", isDefaultValueCheck,
				new GeneralConverterReturningNoTrimString());
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.JDF_ADMIN_LOGIN_PAGE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<String>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.TEXT, itemID,
				"어드민용 로그인 입력 페이지", "/jsp/member/adminLoginInput.jsp", isDefaultValueCheck,
				new GeneralConverterReturningNoTrimString());
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.JDF_SESSION_KEY_REDIRECT_PAGE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<String>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.TEXT, itemID,
				"세션키 없이 세션키가 필요한 페이지를 요청했을 경우 이 페이지가 호출되며 세션키를 갖고 파라미터를 유지한체로 처음 요청한 페이지로 이동시켜주는 페이지",
				"/sessionKeyRedirect.jsp", isDefaultValueCheck, new GeneralConverterReturningNoTrimString());
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.JDF_ERROR_MESSAGE_PAGE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<String>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.TEXT, itemID,
				"JDF framework 에서 에러 발생시 에러 내용을 보여주는 페이지", "/errorMessagePage.jsp", isDefaultValueCheck,
				new GeneralConverterReturningNoTrimString());
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.JDF_SERVLET_TRACE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Boolean>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.SINGLE_SET,
				itemID, "JDF framework에서 서블릿 경과시간 추적 여부", "true", isDefaultValueCheck,
				new SetTypeConverterReturningBoolean());
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<SessionKey.RSAKeypairSourceType>(ItemIDInfo.ConfigurationPart.COMMON,
				ItemIDInfo.ViewType.SINGLE_SET, itemID,
				"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
				SessionKey.RSAKeypairSourceType.SERVER.toString(), isDefaultValueCheck,
				new SetTypeConverterOfSessionKeyRSAKeypairSource());
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
		isDefaultValueCheck = false;
		itemIDInfo = new ItemIDInfo<File>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.FILE, itemID,
				"세션키에 사용되는 RSA 공개키 파일",
				"<installed path>/project/<main project name>/resouces/rsa_keypair/"
						+ CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME,
				isDefaultValueCheck, new GeneralConverterReturningRegularFile(false));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID;
		isDefaultValueCheck = false;
		itemIDInfo = new ItemIDInfo<File>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.FILE, itemID,
				"세션키에 사용되는 RSA 개인키 파일",
				"<sinnnori installed path>/project/<main project name>/resouces/rsa_keypair/"
						+ CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME,
				isDefaultValueCheck, new GeneralConverterReturningRegularFile(false));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYSIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.SINGLE_SET,
				itemID, "세션키에 사용하는 공개키 크기, 단위 byte", "1024", isDefaultValueCheck,
				new SetTypeConverterReturningInteger("512", "1024", "2048"));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_SYMMETRIC_KEY_ALGORITHM_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<String>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.SINGLE_SET, itemID,
				"세션키에 사용되는 대칭키 알고리즘", "AES", isDefaultValueCheck,
				new SetTypeConverterReturningString("AES", "DESede", "DES"));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_SYMMETRIC_KEY_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.SINGLE_SET,
				itemID, "세션키에 사용되는 대칭키 크기", "16", true, new SetTypeConverterReturningInteger("8", "16", "24"));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_IV_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.SINGLE_SET,
				itemID, "세션키에 사용되는 대칭키와 같이 사용되는 IV 크기", "16", isDefaultValueCheck,
				new SetTypeConverterReturningInteger("8", "16", "24"));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_LOCAL_SOURCE_FILE_RESOURCE_CNT_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.TEXT, itemID,
				"로컬 원본 파일 자원 갯수", "10", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_LOCAL_TARGET_FILE_RESOURCE_CNT_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.TEXT, itemID,
				"로컬 목적지 파일 자원 갯수", "10", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_FILE_BLOCK_MAX_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.TEXT, itemID,
				"파일 송수신 파일 블락 최대 크기, 1024 배수, 단위 byte", "1048576", isDefaultValueCheck,
				new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(1024, Integer.MAX_VALUE));
		addCommonPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.COMMON_CACHED_OBJECT_MAX_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.COMMON, ItemIDInfo.ViewType.TEXT, itemID,
				"싱글턴 객체 캐쉬 관리자에서 캐쉬로 관리할 객체의 최대 갯수. 주로 캐쉬되는 대상 객체는 xxxServerCodec, xxxClientCodec 이다", "100",
				isDefaultValueCheck, new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addCommonPartItemIDInfo(itemIDInfo);

		/** Common end */
	}

	/**
	 * 모든 DBCP 파트의 항목 식별자 정보를 등록시킴.
	 * 
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	private void addAllDBCPPartItemIDInfo() throws Exception {
		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;

		boolean isDefaultValueCheck = false;

		/** DBCP start */

		itemID = ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID;
		isDefaultValueCheck = false;
		{
			boolean isWritePermissionChecking = false;
			itemIDInfo = new ItemIDInfo<File>(ItemIDInfo.ConfigurationPart.DBCP, ItemIDInfo.ViewType.FILE, itemID,
					"dbcp 설정 파일 경로명", "<installed path>/project/<main project name>/config/<dbcp name>.properties",
					isDefaultValueCheck, new GeneralConverterReturningRegularFile(isWritePermissionChecking));
		}

		addDBCPPartItemIDInfo(itemIDInfo);

		/** DBCP end */
	}

	/**
	 * 모든 프로젝트 파트의 항목 식별자 정보를 등록시킴.
	 * 
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	private void addAllProjectPartItemIDInfo() throws Exception {
		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;
		boolean isDefaultValueCheck = false;

		/** 프로젝트 공통 설정 부분 */
		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_HOST_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<String>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"클라이언트에서 접속할 서버 주소", "localhost", isDefaultValueCheck, new GeneralConverterReturningNoTrimString());
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_PORT_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"포트 번호", "9090", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1024, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_BYTEORDER_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<ByteOrder>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.SINGLE_SET,
				itemID, "바이트 오더, LITTLE_ENDIAN:리틀 엔디안, BIG_ENDIAN:빅 엔디안", "LITTLE_ENDIAN", isDefaultValueCheck,
				new SetTypeConverterReturningByteOrder());
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_CHARSET_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Charset>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"문자셋", "UTF-8", isDefaultValueCheck, new GeneralConverterReturningCharset());
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<MessageProtocolType>(ItemIDInfo.ConfigurationPart.PROJECT,
				ItemIDInfo.ViewType.SINGLE_SET, itemID, "메시지 프로토콜, DHB:교차 md5 헤더+바디, DJSON:길이+존슨문자열, THB:길이+바디", "DHB",
				isDefaultValueCheck, new SetTypeConverterReturningMessageProtocolType());
		addProjectPartItemIDInfo(itemIDInfo);

		/** 프로젝트 클라이언트 설정 부분 */
		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_TIME_INTERVAL_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Long>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"모니터링 주기, 단위 ms", "5000", isDefaultValueCheck,
				new GeneralConverterReturningLongBetweenMinAndMax(1000L, (long) Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Boolean>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.SINGLE_SET,
				itemID, "whether or not this byte buffer is direct", "true", isDefaultValueCheck,
				new SetTypeConverterReturningBoolean());
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"1개 메시지당 할당 받을 수 있는 데이터 패킷 버퍼 최대수", "1000", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"데이터 패킷 버퍼 크기, 단위 byte", "4096", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1024, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"데이터 패킷 버퍼 큐 크기", "1000", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_TYPE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<ConnectionType>(ItemIDInfo.ConfigurationPart.PROJECT,
				ItemIDInfo.ViewType.SINGLE_SET, itemID, "소캣 랩퍼 클래스인 연결 종류, ASYN:비동기, SYNC:동기", "ASYN",
				isDefaultValueCheck, new SetTypeConverterReturningConnectionType());
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_SOCKET_TIMEOUT_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Long>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"소켓 타임아웃, 단위 ms", "5000", isDefaultValueCheck,
				new GeneralConverterReturningLongBetweenMinAndMax(1000L, (long) Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"연결 갯수", "1", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(0, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_POOL_SUPPORTOR_TIME_INTERVAL_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Long>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"연결 지원자 수행 주기", "600000", isDefaultValueCheck,
				new GeneralConverterReturningLongBetweenMinAndMax(1000L, Long.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"연결 최대 갯수", "5", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(0, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"비동기 출력 메시지 처리자 쓰레드 갯수", "1", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_SYNC_MESSAGE_MAILBOX_COUNT_PER_ASYN_NOSHARE_CONNECTION_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"비동기+공유 연결 클래스(ShareAsynConnection)의 메일함 갯수", "2", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"클라이언트 비동기 입출력 지원용 입력 메시지 큐 크기", "10", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"비동기 메시지에 대한 1:1 비지니스 로직 처리기(ClientExecutor) 가  갖는 출력 메시지 큐 크기", "10", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_SELECTOR_WAKEUP_INTERVAL_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Long>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"클라이언트 비동기 입출력 지원용 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기. 단위 ms", "1",
				isDefaultValueCheck, new GeneralConverterReturningLongBetweenMinAndMax(1L, (long) Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		/** 프로젝트 서버 설정 부분 */
		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_TIME_INTERVAL_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Long>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"모니터링 주기, 단위 ms", "5000", isDefaultValueCheck,
				new GeneralConverterReturningLongBetweenMinAndMax(1000L, (long) Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Boolean>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.SINGLE_SET,
				itemID, "whether or not this byte buffer is direct", "true", isDefaultValueCheck,
				new SetTypeConverterReturningBoolean());
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"1개 메시지당 할당 받을 수 있는 데이터 패킷 버퍼 최대수", "1000", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"데이터 패킷 버퍼 크기, 단위 byte", "4096", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1024, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"데이터 패킷 버퍼 큐 크기", "1000", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MAX_CLIENTS_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"서버로 접속할 수 있는 최대 클라이언트 수", "5", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(1, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_QUEUE_CAPACITY_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"입력 메시지 큐 크기", "10", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(10, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);

		itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_CAPACITY_ITEMID;
		isDefaultValueCheck = true;
		itemIDInfo = new ItemIDInfo<Integer>(ItemIDInfo.ConfigurationPart.PROJECT, ItemIDInfo.ViewType.TEXT, itemID,
				"출력 메시지 큐 크기", "10", isDefaultValueCheck,
				new GeneralConverterReturningIntegerBetweenMinAndMax(10, Integer.MAX_VALUE));
		addProjectPartItemIDInfo(itemIDInfo);
	}

	/**
	 * 항목 정보를 등록하다.
	 * 
	 * @param itemIDInfo 항목 정보
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	private void addItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws Exception {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder("the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		ItemIDInfo<?> olditemIDConfigInfo = itemIDInfoHash.get(itemIDInfo.getItemID());
		if (null != olditemIDConfigInfo) {
			String errorMessage = new StringBuilder("the item id[").append(itemIDInfo.getItemID())
					.append("] was registed").toString();

			// log.warn(errorMessage);

			throw new IllegalArgumentException(errorMessage);
		}

		itemIDInfoHash.put(itemIDInfo.getItemID(), itemIDInfo);
		itemIDInfoList.add(itemIDInfo);

		ConfigurationPart itemConfigPart = itemIDInfo.getConfigurationPart();

		if (ItemIDInfo.ConfigurationPart.DBCP == itemConfigPart) {
			dbcpPartItemIDInfoList.add(itemIDInfo);
		} else if (ItemIDInfo.ConfigurationPart.COMMON == itemConfigPart) {
			commonPartItemIDInfoList.add(itemIDInfo);
		} else {
			projectPartItemIDInfoList.add(itemIDInfo);
		}
	}

	/**
	 * DBCP 항목 정보를 등록하다.
	 * 
	 * @param itemIDInfo 항목 정보
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	
	private void addDBCPPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws Exception {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder("the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (!itemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.DBCP)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[").append(itemIDInfo.getItemID())
					.append("] is not a dbcp part item id").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		addItemIDInfo(itemIDInfo);
	}

	/**
	 * 공통 항목 정보를 등록하다.
	 * 
	 * @param itemIDInfo 항목 정보
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	private void addCommonPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws Exception {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder("the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!itemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[").append(itemIDInfo.getItemID())
					.append("] is not a common part item id").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		addItemIDInfo(itemIDInfo);
	}

	/**
	 * 프로젝트 파트 항목 정보를 등록하다.
	 * 
	 * @param itemIDInfo 항목 정보
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	private void addProjectPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws Exception {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder("the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!itemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.PROJECT)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[").append(itemIDInfo.getItemID())
					.append("] is not a project common part item id").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		addItemIDInfo(itemIDInfo);
	}

	@SuppressWarnings("unchecked")
	private void addDependencyValidation() throws IllegalArgumentException, CoddaConfigurationException {

		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder("dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}

			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder("dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}

			dependencyValidationHash.put(dependentSourceItemID,
					new MinAndMaxDependencyValidator<Integer>((ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
							(ItemIDInfo<Integer>) dependentTargetItemIDInfo, Integer.class));
		}
	}

	/**
	 * 모든 비활성 의존성 항목들을 등록한다.
	 *  
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	private void addAllDisabledItemChecker() throws Exception {
		{
			String disabledTargetItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
			String dependentItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;

			ItemIDInfo<?> disbaledTargetItemIDInfo = getItemIDInfo(disabledTargetItemID);
			if (null == disbaledTargetItemIDInfo) {
				String errorMessage = new StringBuilder("there is no RSA Public Key File item[")
						.append(disabledTargetItemID)
						.append("] identifier information, the RSA Public Key File item depends on RSA Keypair Source item[")
						.append(dependentItemID).append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}

			ItemIDInfo<?> dependentItemIDInfo = getItemIDInfo(dependentItemID);
			if (null == dependentItemIDInfo) {
				String errorMessage = new StringBuilder("there is no RSA Keypair Source item[").append(dependentItemID)
						.append("] identifier information, the RSA Public Key File item[").append(disabledTargetItemID)
						.append("] depends on RSA Keypair Source item").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}

			diabledItemCheckerHash.put(disabledTargetItemID, new RSAKeyFileDisabledItemChecker(disbaledTargetItemIDInfo,
					dependentItemIDInfo, new String[] { SessionKey.RSAKeypairSourceType.SERVER.toString() }));
		}

		{
			String disabledTargetItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID;
			String dependentItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;

			ItemIDInfo<?> disbaledTargetItemIDInfo = getItemIDInfo(disabledTargetItemID);
			if (null == disbaledTargetItemIDInfo) {
				String errorMessage = new StringBuilder("there is no RSA Public Key File item[")
						.append(disabledTargetItemID)
						.append("] identifier information, the RSA Public Key File item depends on RSA Keypair Source item[")
						.append(dependentItemID).append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}

			ItemIDInfo<?> dependentItemIDInfo = getItemIDInfo(dependentItemID);
			if (null == dependentItemIDInfo) {
				String errorMessage = new StringBuilder("there is no RSA Keypair Source item[").append(dependentItemID)
						.append("] identifier information, the RSA Public Key File item[").append(disabledTargetItemID)
						.append("] depends on RSA Keypair Source item").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}

			diabledItemCheckerHash.put(disabledTargetItemID, new RSAKeyFileDisabledItemChecker(disbaledTargetItemIDInfo,
					dependentItemIDInfo, new String[] { SessionKey.RSAKeypairSourceType.SERVER.toString() }));
		}

	}

	/**
	 * 모든 파일 혹은 경로 전체 이름 반환자들을 등록한다. 참고 : 파일 혹은 경로 전체 이름은 디폴트 값을  정적인 설정 파일에서 지정 할 수 없고 오직 런타임시 지정되는 설치 경로와 메인 프로젝트 이름 그리고 항목별 필요한 부가 정보를 통해서 결정된다.
	 * 
	 * @throws Exception 에러가 발생하여 던지는 예외
	 */
	private void addAllFileOrPathStringReturner() throws Exception {
		String itemID = null;

		itemID = ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID;
		fileOrPathStringRetunerHash.put(itemID, new DBCPConfigFilePathStringReturner(itemID));

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
		fileOrPathStringRetunerHash.put(itemID, new SessionkeyRSAPublickeyFilePathStringReturner(itemID));

		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID;
		fileOrPathStringRetunerHash.put(itemID, new SessionkeyRSAPrivatekeyFilePathStringReturner(itemID));
	}

	
	/**
	 * @param itemID 항목 식별자
	 * @return 항목 정보, 만약 항목 식별자에 1:1 대응하는 항목 정보가 없다면 null 을 반환함.
	 */
	private ItemIDInfo<?> getItemIDInfo(String itemID) {
		return itemIDInfoHash.get(itemID);
	}

	/**
	 * @param installedPathString 설치 경로
	 * @param mainProjectName     메인 프로젝트 이름
	 * @return 지정한 '설치 경로' 와 '메인 프로젝트 이름' 을 바탕으로한 설정 파일 내용이 담긴 신규 시퀀스 프로퍼티
	 */
	public SequencedProperties createNewConfigSequencedProperties(String installedPathString, String mainProjectName) {

		SequencedProperties configSequencedProperties = new SequencedProperties();

		/** common */
		{
			String prefixOfItemID = "";
			for (ItemIDInfo<?> commonPartItemIDInfo : commonPartItemIDInfoList) {
				String itemID = commonPartItemIDInfo.getItemID();
				String itemKey = itemID;
				String itemValue = commonPartItemIDInfo.getDefaultValue();

				String itemDescriptionKey = commonPartItemIDInfo.getItemDescKey(prefixOfItemID);
				String itemDescriptionValue = commonPartItemIDInfo.getDescription();

				AbstractFileOrPathStringReturner fileOrPathStringReturner = fileOrPathStringRetunerHash.get(itemID);

				if (null != fileOrPathStringReturner) {
					itemValue = fileOrPathStringReturner.getFileOrPathString(installedPathString,
							mainProjectName);
				}

				configSequencedProperties.put(itemDescriptionKey, itemDescriptionValue);
				configSequencedProperties.put(itemKey, itemValue);
			}
		}

		/** DBCP */
		{
			configSequencedProperties.setProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING, "");
		}

		/** main project */
		{
			String prefixOfItemID = new StringBuilder("mainproject.").toString();
			for (ItemIDInfo<?> mainProjectPartItemIDInfo : projectPartItemIDInfoList) {

				String itemID = mainProjectPartItemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(itemID).toString();
				String itemValue = mainProjectPartItemIDInfo.getDefaultValue();

				String itemDescriptionKey = mainProjectPartItemIDInfo.getItemDescKey(prefixOfItemID);
				String itemDescriptionValue = mainProjectPartItemIDInfo.getDescription();

				AbstractFileOrPathStringReturner fileOrPathStringReturner = fileOrPathStringRetunerHash.get(itemID);

				if (null != fileOrPathStringReturner) {
					itemValue = fileOrPathStringReturner.getFileOrPathString(installedPathString,
							mainProjectName);
				}

				configSequencedProperties.put(itemDescriptionKey, itemDescriptionValue);
				configSequencedProperties.put(itemKey, itemValue);
			}
		}

		/** sub project */
		{
			configSequencedProperties.setProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING, "");

		}

		return configSequencedProperties;
	}

	/**
	 * @param itemID 항목 식별자
	 * @param prefixOfItemID 설정 파일에서 항목 키 접두어
	 * @param sourceProperties 설정 파일 내용이 담긴 프로퍼티
	 * @return 지정한 항목 식별자를 갖는 항목의 비활성 여부
	 */
	public boolean isDisabled(String itemID, String prefixOfItemID, Properties sourceProperties) {
		AbstractDisabledItemChecker disabledItemChecker = diabledItemCheckerHash.get(itemID);		
		
		boolean isDisabled = (null == disabledItemChecker) ? false : disabledItemChecker.isDisabled(sourceProperties, prefixOfItemID);

		return isDisabled;
	}

	/**
	 * @param itemID 항목 식별자
	 * @return 파일 혹은 경로 전체 이름 반환기
	 */
	public boolean isFileOrPathStringReturner(String itemID) {
		boolean result = (null == fileOrPathStringRetunerHash.get(itemID)) ? false : true;
		return result;
	}
	
	/**
	 * @param itemID  항목 식별자
	 * @return 지정한 항목 식별자를 갖는 항목의 파일 혹은 경로 이름 반환자, 만약 '파일 혹은 경로 이름 반환자' 가 없다면 null 을 반환한다.
	 * @throws IllegalArgumentException 파라미터 '항목 식별자' 가 null 이면 던지는 예외
	 */
	public AbstractFileOrPathStringReturner getFileOrPathStringReturner(String itemID) throws IllegalArgumentException {
		if (null == itemID) {
			throw new IllegalArgumentException("the paramter itemID is null");
		}
		return fileOrPathStringRetunerHash.get(itemID);
	}

	/**
	 * @param itemKey 항목 키, 프로퍼티인 설정 파일에서의 키를 말함.  
	 * @param dbcpNameSet dbcp 이름 집합, null 이 아닌 경우에만 dbcp 이름을 dbcp 이름 집합에 있는 것으로 강제한다.
	 * @param subProjectNameSet 서브 프로젝트 이름 집합, null 이 아닌 경우에만 서브 프로젝트 이름을 서브 프로젝트 이름 집합에 있는 것으로 강제한다. 
	 * @return 항목 키에 대응하는 항목 정보, 만약 없다면 null 을 반환한다. 단 키가 {@link CommonStaticFinalVars#DBCP_NAME_LIST_KEY_STRING} 혹은 
	 * {@link CommonStaticFinalVars#SUBPROJECT_NAME_LIST_KEY_STRING} 인 경우 null 을 반환한다 그리고 항목 키값이  '.value' 로 끝나지 않는 경우에도 역시 null 을 반환한다.
	 * @throws IllegalArgumentException 파라미터 값이 잘못 되어있는 경우 던지는 예외
	 * @throws CoddaConfigurationException 항목 키 값이 잘못된 경우 혹은 설정 정보와 다르게 값을 설정할 경우 던지는 예외
	 */
	public ItemIDInfo<?> getItemIDInfoFromKey(String itemKey, Set<String> dbcpNameSet, Set<String> subProjectNameSet)
			throws IllegalArgumentException, CoddaConfigurationException {
		if (null == itemKey) {
			String errorMessage = "the parameter itemKey is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemKey.equals(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING)
				|| itemKey.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)) {
			return null;
		}

		if (! itemKey.endsWith(".value")) {
			return null;
		}

		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;

		StringTokenizer itemKeyStringTokenizer = new StringTokenizer(itemKey, ".");

		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("first token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		

		String firstToken = itemKeyStringTokenizer.nextToken();

		if (firstToken.equals("mainproject")) {
			itemID = getItemIDOfMainProjectPart(itemKey, itemKeyStringTokenizer, firstToken);

			itemIDInfo = getItemID(itemKey, itemID);

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			throwExceptionIfInvalidConfigurationPart(itemKey, itemIDInfo, ItemIDInfo.ConfigurationPart.PROJECT);
		} else if (firstToken.equals("subproject")) {
			/** project part */
			itemID = getItemIDOfSubProjectPart(itemKey, subProjectNameSet, itemKeyStringTokenizer, firstToken);

			itemIDInfo = getItemID(itemKey, itemID);

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			throwExceptionIfInvalidConfigurationPart(itemKey, itemIDInfo, ItemIDInfo.ConfigurationPart.PROJECT);

		} else if (firstToken.equals("dbcp")) {
			/** dbcp part */
			itemID = getItemIDOfDBCPPart(itemKey, dbcpNameSet, itemKeyStringTokenizer, firstToken);

			itemIDInfo = getItemID(itemKey, itemID);

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			throwExceptionIfInvalidConfigurationPart(itemKey, itemIDInfo, ItemIDInfo.ConfigurationPart.DBCP);
		} else {
			/** common part */
			itemID = getItemIDOfCommonPart(itemKey, itemKeyStringTokenizer);

			itemIDInfo = getItemID(itemKey, itemID);

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			throwExceptionIfInvalidConfigurationPart(itemKey, itemIDInfo, ItemIDInfo.ConfigurationPart.COMMON);
		}

		return itemIDInfo;
	}

	/**
	 * 항목의 설정 파일내 파트가 기대한 파트와 다르다면 예외를 던전다
	 * @param itemKey 항목 키
	 * @param itemIDInfo 항목 정보
	 * @param wantedConfigurationPart 기대하는 항목의 설정 파일내 파트
	 * @throws CoddaConfigurationException 항목의 설정 파일내 파트가 기대한 파트와 다를 경우 던지는 예외  
	 */
	private void throwExceptionIfInvalidConfigurationPart(String itemKey, ItemIDInfo<?> itemIDInfo,
			ItemIDInfo.ConfigurationPart wantedConfigurationPart) throws CoddaConfigurationException {
		ItemIDInfo.ConfigurationPart configPartOfItemID = itemIDInfo.getConfigurationPart();
		if (!configPartOfItemID.equals(wantedConfigurationPart)) {
			String errorMessage = new StringBuilder("the configuration part[").append(configPartOfItemID.toString())
					.append("] of the var itemIDInfo getting from the parameter itemKey[").append(itemKey)
					.append("] is not same to the wanted configuration part[")
					.append(wantedConfigurationPart.toString()).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}
	}

	/**
	 * @param itemKey 항목 키
	 * @param itemKeyStringTokenizer 항목 키를 '.' 자로 구별자로 한 토큰 분리자
	 * @return 항목 키에 대응하는 항목 식별자
	 * @throws CoddaConfigurationException 항목 키 값이 공통 파트의 것이 아닌 경우 던지는 예외
	 */
	private String getItemIDOfCommonPart(String itemKey, StringTokenizer itemKeyStringTokenizer) throws CoddaConfigurationException {
		String itemID;
		if (! itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("second token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String secondToken = itemKeyStringTokenizer.nextToken();
		if (secondToken.equals("")) {
			String errorMessage = new StringBuilder("second token is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		itemID = itemKey;
		return itemID;
	}

	/**
	 * @param itemKey 항목 키, 단순하게 로그 남기기 용도로 사용된다.
	 * @param itemID 항목 식별자, 참고 : 항목 키에서 추출된다.
	 * @return 항목 식별자에 대응하는 항목 정보
	 * @throws CoddaConfigurationException  항목 키에 대응하는 항목 식별자에 대한  항목 정보가 없을 경우 던지는 예외
	 */
	private ItemIDInfo<?> getItemID(String itemKey, String itemID) throws CoddaConfigurationException {
		ItemIDInfo<?> itemIDInfo;
		itemIDInfo = itemIDInfoHash.get(itemID);
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder("the parameter itemKey[").append(itemKey)
					.append("]'s itemID is not registed, check it").toString();
			throw new CoddaConfigurationException(errorMessage);
		}
		return itemIDInfo;
	}

	/**
	 * @param itemKey 항목 키
	 * @param dbcpNameSet 설정 파일에서 명시된 'dbcp 이름 집합', null 가능함. null 이 아닌 경우에만 dbcp 이름을 설정 파일에서 명시된 'dbcp 이름 집합' 에 있는 것으로 강제함. 
	 * @param itemKeyStringTokenizer 항목 키를 '.' 자로 구별자로 한 토큰 분리자
	 * @param firstToken 항목 키를 '.' 자로 구별자로 한 첫번째 토큰
	 * @return 항목 키에 대응하는 항목 식별자
	 * @throws CoddaConfigurationException 항목 키 값이 dbcp 파트의 것이 아닌 경우 던지는 예외
	 */
	private String getItemIDOfDBCPPart(String itemKey, Set<String> dbcpNameSet, StringTokenizer itemKeyStringTokenizer,
			String firstToken) throws CoddaConfigurationException {
		String itemID;
		if (! itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("second token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String dbcpName = itemKeyStringTokenizer.nextToken();

		if (null != dbcpNameSet) {
			if (!dbcpNameSet.contains(dbcpName)) {
				String errorMessage = new StringBuilder("the item key[").append(itemKey)
						.append("] has a wrong dbcp name not existing in the parameter dbcpNameList[")
						.append(dbcpNameSet.toString()).append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
		}

		if (! itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("third token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String thirdToken = itemKeyStringTokenizer.nextToken();
		if (thirdToken.equals("")) {
			String errorMessage = new StringBuilder("third token is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String prefixOfItemID = firstToken + "." + dbcpName + ".";
		itemID = itemKey.substring(prefixOfItemID.length());
		return itemID;
	}

	/**
	 * @param itemKey 항목 키
	 * @param subProjectNameSet 설정 파일에서 명시된 '서브 프로젝트 이름 집합', null 가능함. null 이 아닌 경우에만 서브 프로젝트 이름을 설정 파일에서 명시된 '서브 프로젝트 이름 집합' 에 있는 것으로 강제함. 
	 * @param itemKeyStringTokenizer 항목 키를 '.' 자로 구별자로 한 토큰 분리자
	 * @param firstToken 항목 키를 '.' 자로 구별자로 한 첫번째 토큰
	 * @return 항목 키에 대응하는 항목 식별자
	 * @throws CoddaConfigurationException 항목 키 값이 서브 프로젝트 파트의 것이 아닌 경우 던지는 예외
	 */
	private String getItemIDOfSubProjectPart(String itemKey, Set<String> subProjectNameSet,
			StringTokenizer itemKeyStringTokenizer, String firstToken) throws CoddaConfigurationException {
		String itemID;
		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("second token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String projectName = itemKeyStringTokenizer.nextToken();
		if (projectName.equals("")) {
			String errorMessage = "project name is an empty string at the project part";
			throw new CoddaConfigurationException(errorMessage);
		}

		if (null != subProjectNameSet) {
			if (! subProjectNameSet.contains(projectName)) {
				String errorMessage = new StringBuilder("the item key[").append(itemKey)
						.append("] has a wrong project name not existing in the parameter projectNameList[")
						.append(subProjectNameSet.toString()).append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
		}

		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("third token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String subPartName = itemKeyStringTokenizer.nextToken();
		if (subPartName.equals("")) {
			String errorMessage = new StringBuilder("subPartName is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		if (!subPartName.equals("common") && !subPartName.equals("client") && !subPartName.equals("server")) {
			String errorMessage = new StringBuilder("the sub part[").append(subPartName)
					.append("] of the parameter itemKey[").append(itemKey)
					.append("]'s itemID is bad, it must have one element of set {'common', 'client', 'server'}")
					.toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("fourth token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String fourthToken = itemKeyStringTokenizer.nextToken();
		if (fourthToken.equals("")) {
			String errorMessage = new StringBuilder("fourth Token is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String prefixOfItemID = firstToken + "." + projectName + ".";
		itemID = itemKey.substring(prefixOfItemID.length());
		return itemID;
	}

	/**
	 * @param itemKey 항목 키
	 * @param itemKeyStringTokenizer 항목 키를 '.' 자로 구별자로 한 토큰 분리자
	 * @param firstToken 항목 키를 '.' 자로 구별자로 한 첫번째 토큰
	 * @return 항목 키에 대응하는 항목 식별자
	 * @throws CoddaConfigurationException 항목 키 값이 메인 프로젝트 파트의 것이 아닌 경우 던지는 예외
	 */
	private String getItemIDOfMainProjectPart(String itemKey, StringTokenizer itemKeyStringTokenizer,
			String firstToken) throws CoddaConfigurationException {
		String itemID;
		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("second token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String subPartName = itemKeyStringTokenizer.nextToken();
		if (subPartName.equals("")) {
			String errorMessage = new StringBuilder("subPartName is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		if (!subPartName.equals("common") && !subPartName.equals("client") && !subPartName.equals("server")) {
			String errorMessage = new StringBuilder("the sub part[").append(subPartName)
					.append("] of the parameter itemKey[").append(itemKey)
					.append("]'s itemID is bad, it must have one element of set {'common', 'client', 'server'}")
					.toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder("third token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String thirdToken = itemKeyStringTokenizer.nextToken();
		if (thirdToken.equals("")) {
			String errorMessage = new StringBuilder("third Token is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new CoddaConfigurationException(errorMessage);
		}

		String prefixOfItemID = firstToken + ".";
		itemID = itemKey.substring(prefixOfItemID.length());
		return itemID;
	}

	/**
	 * @param itemKey 항목 키, 프로퍼터인 설정 파일에서의 키
	 * @param sourceProperties 설정 파일의 내용을 갖고 있는 프로퍼티
	 * @return 자바 내부의 값으로 변환된 지정한 프로퍼티에서 항목 키에 해당하는 값  
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 * @throws CoddaConfigurationException 항목 키 값이 의존성등에 유효하지 않았을때 던지는 예외 
	 */
	public Object getNativeValueAfterValidChecker(String itemKey, Properties sourceProperties)
			throws IllegalArgumentException, CoddaConfigurationException {
		if (null == itemKey) {
			throw new IllegalArgumentException("the parameter itemKey is null");
		}
		if (null == sourceProperties) {
			throw new IllegalArgumentException("the parameter sourceProperties is null");
		}

		ItemIDInfo<?> itemIDInfo = null;

		try {
			itemIDInfo = getItemIDInfoFromKey(itemKey, null, null);
		} catch (IllegalArgumentException e) {
			/**
			 * same parameter name 'itemKey' so error message is same
			 */
			throw e;
		}

		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder("the parameter itemValueKey[").append(itemKey)
					.append("](=dependentSourceKey) is bad, itemID is null").toString();

			log.log(Level.WARNING, errorMessage);

			throw new CoddaConfigurationException(errorMessage);
		}

		String itemID = itemIDInfo.getItemID();
		int inx = itemKey.indexOf(itemID);
		String prefixOfItemID = itemKey.substring(0, inx);

		AbstractDependencyValidator dependOnValidCheck = dependencyValidationHash.get(itemID);

		if (null != dependOnValidCheck) {

			try {
				boolean isValid = dependOnValidCheck.isValid(sourceProperties, prefixOfItemID);
				if (!isValid) {
					String errorMessage = new StringBuilder("the dependent source item").append(prefixOfItemID)
							.append(dependOnValidCheck.getDependentSourceItemID())
							.append("] doesn't depend on the dependent target item[").append(prefixOfItemID)
							.append(dependOnValidCheck.getDependentTargetItemID()).append("]").toString();

					// log.warn(errorMessage);

					throw new CoddaConfigurationException(errorMessage);
				}
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder("the parameter itemValueKey[").append(itemKey)
						.append("]'s invalid check fails errrorMessage=").append(e.getMessage()).toString();
				/** 다른 예외로 변환 되므로 이력 남긴다. */
				log.log(Level.WARNING, errorMessage, e);

				throw new CoddaConfigurationException(errorMessage);
			}
		}

		Object itemNativeValue = null;
		String itemValue = sourceProperties.getProperty(itemKey);
		try {
			itemNativeValue = itemIDInfo.getItemValueConverter().valueOf(itemValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to convert the parameter itemValueKey[").append(itemKey)
					.append("]'s value[").append(sourceProperties.getProperty(itemKey)).append("] to a native value")
					.toString();
			/** 다른 예외로 변환 되므로 이력 남긴다. */
			log.log(Level.WARNING, errorMessage, e);

			throw new CoddaConfigurationException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		return itemNativeValue;
	}

	/**
	 * @return 읽기 전용 'dbcp 파트 항목 식별자 목록'
	 */
	public List<ItemIDInfo<?>> getUnmodifiableDBCPPartItemIDInfoList() {
		return Collections.unmodifiableList(dbcpPartItemIDInfoList);
	}

	/**
	 * @return 읽기 전용 '공통 파트 항목 식별자 목록'
	 */
	public List<ItemIDInfo<?>> getUnmodifiableCommonPartItemIDInfoList() {
		return Collections.unmodifiableList(commonPartItemIDInfoList);
	}

	/**
	 * @return 읽기 전용 '프로젝트 파트 항목 식별자 목록'
	 */
	public List<ItemIDInfo<?>> getUnmodifiableProjectPartItemIDInfoList() {
		return Collections.unmodifiableList(projectPartItemIDInfoList);
	}

}
