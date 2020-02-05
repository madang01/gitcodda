package kr.pe.codda.server.lib;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.jooq.tables.SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.server.dbcp.DBCPManager;

public abstract class ServerDBUtil {

	private static Settings DEFAULT_DBCP_SETTINGS = new Settings().withRenderMapping(new RenderMapping()
			.withSchemata(new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
					.withOutput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())));

	private static Settings GENERAL_TEST_DBCP_SETTINGS = new Settings().withRenderMapping(new RenderMapping()
			.withSchemata(new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
					.withOutput(ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME.toLowerCase())));

	private static Settings LOAD_TEST_DBCP_SETTINGS = new Settings().withRenderMapping(new RenderMapping()
			.withSchemata(new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
					.withOutput(ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME.toLowerCase())));

	private static void initializeSequenceTable(DSLContext dsl) throws Exception {
		for (SequenceType sequenceTypeValue : SequenceType.values()) {
			boolean exists = dsl.fetchExists(dsl.select(SB_SEQ_TB.SQ_ID).from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID.eq(sequenceTypeValue.getSequenceID())));

			if (!exists) {
				int countOfInsert = dsl.insertInto(SB_SEQ_TB).set(SB_SEQ_TB.SQ_ID, sequenceTypeValue.getSequenceID())
						.set(SB_SEQ_TB.SQ_NAME, sequenceTypeValue.getName())
						.set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1)).execute();

				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append("fail to insert the sequence(id:")
							.append(sequenceTypeValue.getSequenceID()).append(", name:")
							.append(sequenceTypeValue.getName()).append(")").toString();
					throw new Exception(errorMessage);
				}
			}
		}
	}

	/**
	 * WARNING! 일반 사용자 '사이트 메뉴 정보 테이블' 초기화는 '시퀀스 테이블' 초기화 이후 호출되어야 한다
	 * 
	 * @param dsl
	 * @throws Exception
	 */
	private static void initializeUserMenuInfoTable(DSLContext dsl) throws Exception {
		CoddaConfiguration mainProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		String installedPathString = mainProjectConfiguration.getInstalledPathString();
		String mainProjectName = mainProjectConfiguration.getMainProjectName();

		String projectDBInitializationDirecotryPathString = ProjectBuildSytemPathSupporter
				.getDBInitializationDirecotryPathString(installedPathString, mainProjectName);

		File projectBoardInfoJsonFile = new File(new StringBuilder(projectDBInitializationDirecotryPathString)
				.append(File.separator).append("sample_base_usermenu.json.txt").toString());

		byte[] buffer = CommonStaticUtil.readFileToByteArray(projectBoardInfoJsonFile, 10 * 1024 * 1024L);

		String userMenuInfoJsonString = new String(buffer, CommonStaticFinalVars.SOURCE_FILE_CHARSET);

		JsonParser jsonParser = new JsonParser();
		JsonElement userMenuInfoJsonElement = jsonParser.parse(userMenuInfoJsonString);

		if (!userMenuInfoJsonElement.isJsonObject()) {
			throw new Exception("the var userMenuInfoJsonElement is not a JsonObject");
		}

		JsonObject jsonObject = userMenuInfoJsonElement.getAsJsonObject();
		JsonElement siteMenuListJsonElement = jsonObject.get("siteMenuList");

		if (null == siteMenuListJsonElement) {
			String errorMessage = "the var siteMenuListJsonElement is null";
			throw new Exception(errorMessage);
		}

		if (!siteMenuListJsonElement.isJsonArray()) {
			String errorMessage = "the var siteMenuListJsonElement is not a json array";
			throw new Exception(errorMessage);
		}

		JsonArray siteMenuListJsonArray = siteMenuListJsonElement.getAsJsonArray();

		int size = siteMenuListJsonArray.size();
		for (int i = 0; i < size; i++) {
			JsonElement siteMenuJsonElement = siteMenuListJsonArray.get(i);
			if (!siteMenuJsonElement.isJsonObject()) {
				String errorMessage = "the var siteMenuJsonElement is not a json object";
				throw new Exception(errorMessage);
			}

			JsonObject siteMenuJsonObject = siteMenuJsonElement.getAsJsonObject();

			JsonElement menuNoJsonElement = siteMenuJsonObject.get("menu_no");
			JsonElement parentNoJsonElement = siteMenuJsonObject.get("parent_no");
			JsonElement depthJsonElement = siteMenuJsonObject.get("depth");
			JsonElement orderSequenceJsonElement = siteMenuJsonObject.get("order_sq");
			JsonElement menuNameJsonElement = siteMenuJsonObject.get("menu_nm");
			JsonElement linkURLJsonElement = siteMenuJsonObject.get("link_url");

			if (null == menuNoJsonElement) {
				String errorMessage = "the var menuNoJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == parentNoJsonElement) {
				String errorMessage = "the var parentNoJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == depthJsonElement) {
				String errorMessage = "the var depthJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == orderSequenceJsonElement) {
				String errorMessage = "the var orderSequenceJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == menuNameJsonElement) {
				String errorMessage = "the var menuNameJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == linkURLJsonElement) {
				String errorMessage = "the var linkURLJsonElement is is null";
				throw new Exception(errorMessage);
			}

			UInteger menuNo = UInteger.valueOf(menuNoJsonElement.getAsLong());
			UInteger parentNo = UInteger.valueOf(parentNoJsonElement.getAsLong());
			UByte depth = UByte.valueOf(depthJsonElement.getAsShort());
			UByte orderSequence = UByte.valueOf(orderSequenceJsonElement.getAsShort());
			String menuName = menuNameJsonElement.getAsString();
			String linkURL = linkURLJsonElement.getAsString();

			boolean isMenu = dsl.fetchExists(
					dsl.select(SB_SITEMENU_TB.MENU_NO).from(SB_SITEMENU_TB).where(SB_SITEMENU_TB.MENU_NO.eq(menuNo)));

			if (!isMenu) {
				int countOfInsert = dsl.insertInto(SB_SITEMENU_TB).set(SB_SITEMENU_TB.MENU_NO, menuNo)
						.set(SB_SITEMENU_TB.PARENT_NO, parentNo).set(SB_SITEMENU_TB.DEPTH, depth)
						.set(SB_SITEMENU_TB.ORDER_SQ, orderSequence).set(SB_SITEMENU_TB.MENU_NM, menuName)
						.set(SB_SITEMENU_TB.LINK_URL, linkURL).execute();

				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append("사용자 사이트 메뉴[번호:").append(menuNo).append(", 메뉴명:")
							.append(menuName).append("] 삽입 실패").toString();
					throw new Exception(errorMessage);
				}
			}
		}

		dsl.update(SB_SEQ_TB)
				.set(SB_SEQ_TB.SQ_VALUE,
						dsl.select(DSL.field("if ({0} is null, {1}, {2})", UInteger.class, SB_SITEMENU_TB.MENU_NO.max(),
								UInteger.valueOf(1), SB_SITEMENU_TB.MENU_NO.max().add(1))).from(SB_SITEMENU_TB))
				.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.MENU.getSequenceID())).execute();
	}

	private static void initializeBoardInfoTable(DSLContext dsl) throws Exception {
		CoddaConfiguration mainProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		String installedPathString = mainProjectConfiguration.getInstalledPathString();
		String mainProjectName = mainProjectConfiguration.getMainProjectName();

		String projectDBInitializationDirecotryPathString = ProjectBuildSytemPathSupporter
				.getDBInitializationDirecotryPathString(installedPathString, mainProjectName);

		File projectBoardInfoJsonFile = new File(new StringBuilder(projectDBInitializationDirecotryPathString)
				.append(File.separator).append("sample_base_board_info.json.txt").toString());

		byte[] buffer = CommonStaticUtil.readFileToByteArray(projectBoardInfoJsonFile, 10 * 1024 * 1024L);

		String boardInfoJsonString = new String(buffer, CommonStaticFinalVars.SOURCE_FILE_CHARSET);

		JsonParser jsonParser = new JsonParser();
		JsonElement boardInfoJsonElement = jsonParser.parse(boardInfoJsonString);

		if (!boardInfoJsonElement.isJsonObject()) {
			throw new Exception("the var boardInfoJsonElement is not a JsonObject");
		}

		JsonObject jsonObject = boardInfoJsonElement.getAsJsonObject();
		JsonElement boardInfomationListJsonElement = jsonObject.get("boardInfomationList");

		if (null == boardInfomationListJsonElement) {
			String errorMessage = "the var boardInfomationListJsonElement is null";
			throw new Exception(errorMessage);
		}

		if (!boardInfomationListJsonElement.isJsonArray()) {
			String errorMessage = "the var boardInfomationListJsonElement is not a json array";
			throw new Exception(errorMessage);
		}

		JsonArray boardInfomationListJsonArray = boardInfomationListJsonElement.getAsJsonArray();

		int size = boardInfomationListJsonArray.size();
		for (int i = 0; i < size; i++) {
			JsonElement boardInfomationJsonElement = boardInfomationListJsonArray.get(i);
			if (!boardInfomationJsonElement.isJsonObject()) {
				String errorMessage = "the var boardInfomationJsonElement is not a json object";
				throw new Exception(errorMessage);
			}

			JsonObject boardInfomationJsonObject = boardInfomationJsonElement.getAsJsonObject();

			JsonElement boardIDJsonElement = boardInfomationJsonObject.get("boardID");
			JsonElement boardNameJsonElement = boardInfomationJsonObject.get("boardName");
			JsonElement boardListTypeJsonElement = boardInfomationJsonObject.get("listType");
			JsonElement boardReplyPolicyTypeJsonElement = boardInfomationJsonObject.get("replyPolicyType");
			JsonElement boardWritePermissionTypeJsonElement = boardInfomationJsonObject.get("writePermissionType");
			JsonElement boardReplyPermissionTypeJsonElement = boardInfomationJsonObject.get("replyPermissionType");

			if (null == boardIDJsonElement) {
				String errorMessage = "the var boardIDJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardNameJsonElement) {
				String errorMessage = "the var boardNameJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardListTypeJsonElement) {
				String errorMessage = "the var boardListTypeJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardReplyPolicyTypeJsonElement) {
				String errorMessage = "the var boardReplyPolicyTypeJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardWritePermissionTypeJsonElement) {
				String errorMessage = "the var boardWritePermissionTypeJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardReplyPermissionTypeJsonElement) {
				String errorMessage = "the var boardReplyPermissionTypeJsonElement is is null";
				throw new Exception(errorMessage);
			}

			UByte boardID = UByte.valueOf(boardIDJsonElement.getAsShort());
			String boardName = boardNameJsonElement.getAsString();
			byte boardListType = boardListTypeJsonElement.getAsByte();
			byte boardReplyPolictyType = boardReplyPolicyTypeJsonElement.getAsByte();
			byte boardWritePermissionType = boardWritePermissionTypeJsonElement.getAsByte();
			byte boardReplyPermissionType = boardReplyPermissionTypeJsonElement.getAsByte();

			boolean isBoardTypeID = dsl.fetchExists(dsl.select(SB_BOARD_INFO_TB.BOARD_ID).from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)));

			if (!isBoardTypeID) {
				int countOfInsert = dsl.insertInto(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.BOARD_ID, boardID)
						.set(SB_BOARD_INFO_TB.BOARD_NAME, boardName).set(SB_BOARD_INFO_TB.LIST_TYPE, boardListType)
						.set(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, boardReplyPolictyType)
						.set(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, boardWritePermissionType)
						.set(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, boardReplyPermissionType)
						.set(SB_BOARD_INFO_TB.CNT, 0L).set(SB_BOARD_INFO_TB.TOTAL, 0L)
						.set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(1)).execute();

				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append("게시판 정보[게시판식별자:").append(boardID)
							.append(", 게시판이름:").append(boardName).append("] 삽입 실패").toString();
					throw new Exception(errorMessage);
				}
			}
		}
	}

	public static void initializeDBEnvoroment(String dbcpName) throws Exception {
		Logger log = LoggerFactory.getLogger(ServerDBUtil.class);

		DataSource dataSource = null;

		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);
		} catch (IllegalArgumentException | DBCPDataSourceNotFoundException e) {
			String errorMessage = e.getMessage();

			log.warn(errorMessage, e);

			/** 지정한 이름의 dbcp 를 받지 못할 경우 아무 처리도 하지 않는다 */
			return;
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(true);

			DSLContext dsl = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			initializeSequenceTable(dsl);

			initializeBoardInfoTable(dsl);

			initializeUserMenuInfoTable(dsl);

		} catch (Exception e) {
			log.warn("error", e);

			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}

		/*
		 * String nickname = "테스트어드민"; String pwdHint = "비밀번호힌트::그것이 알고싶다"; String
		 * pwdAnswer = "비밀번호답변::"; // 비밀번호는 영문으로 시작해서 영문/숫자/특수문자가 최소 1자이상 조합되어야 한다
		 * byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t',
		 * (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		 * 
		 * try { registerMember(MemberType.ADMIN, adminID, nickname, pwdHint, pwdAnswer,
		 * passwordBytes); } catch (Exception e) { log.warn("unknown error", e); throw
		 * e; }
		 */

	}


	/**
	 * <pre>
	 * 회원 테이블 '비밀번호' 필드와 '비밀번호 소금' 필드 값 묶음을 반환한다. 
	 * 
	 * 회원 테이블 '비밀번호' 필드 값은 base64로 인코딩한 비밀번호 해쉬 값이다.
	 * 회원 테이블  '비밀번호 소금' 필드 값은 base64로 인코딩한 소금값으로 비밀번호 역추적을 어렵게 하기 위해 목적을 갖는다. 
	 * 
	 * WARNING! 파라미터 유효성 검사를 수행하지 않기때문에 사용에 주의가 필요합니다.
	 * WARNING! 보안상 비밀번호 만드는 방법은 노출되어서는 안됩니다. 하여 만약 실제로 운영한다면 반듯이 노출하지 않도록 조취가 필요합니다.
	 * </pre>
	 * 
	 * @param passwordBytes 사용자가 입력한 비밀번호
	 * @param pwdSaltBytes  비밀번호 해쉬에 사용할 소금
	 * @return 회원 테이블 '비밀번호' 필드와 '비밀번호 소금' 필드 값 묶음
	 * @throws NoSuchAlgorithmException
	 */
	public static PasswordPairOfMemberTable toPasswordPairOfMemberTable(byte[] passwordBytes, byte[] pwdSaltBytes)
			throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance(CommonStaticFinalVars.PASSWORD_ALGORITHM_NAME);

		int limit = pwdSaltBytes.length + passwordBytes.length;

		ByteBuffer passwordByteBuffer = ByteBuffer.allocate(limit);
		passwordByteBuffer.put(pwdSaltBytes);
		passwordByteBuffer.put(passwordBytes);

		md.update(passwordByteBuffer.array());

		byte passwordMDBytes[] = md.digest();

		/** 복호환 비밀번호 초기화 */
		Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);

		return new PasswordPairOfMemberTable(CommonStaticUtil.Base64Encoder.encodeToString(passwordMDBytes),
				CommonStaticUtil.Base64Encoder.encodeToString(pwdSaltBytes));
	}

	public static Settings getDBCPSettings(String dbcpName) {
		if (dbcpName.equals(ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME)) {
			return GENERAL_TEST_DBCP_SETTINGS;
		} else if (dbcpName.equals(ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME)) {
			return LOAD_TEST_DBCP_SETTINGS;
		}

		return DEFAULT_DBCP_SETTINGS;
	}

	public static UByte getToOrderSeqOfRelativeRootMenu(DSLContext dsl, UByte orderSeq, UByte depth) {
		UByte toOrderSeq = orderSeq;

		Result<Record2<UByte, UByte>> childMenuResult = dsl.select(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.DEPTH)
				.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx1")).where(SB_SITEMENU_TB.ORDER_SQ.gt(orderSeq))
				.orderBy(SB_SITEMENU_TB.ORDER_SQ.asc()).fetch();

		for (Record2<UByte, UByte> childMenuRecord : childMenuResult) {
			UByte childOrderSeq = childMenuRecord.get(SB_SITEMENU_TB.ORDER_SQ);
			UByte childDepth = childMenuRecord.get(SB_SITEMENU_TB.DEPTH);

			if (childDepth.shortValue() <= depth.shortValue()) {
				break;
			}

			toOrderSeq = childOrderSeq;
		}

		return toOrderSeq;
	}

	public static UByte getToOrderSeqOfRelativeRootMenu(DSLContext dsl, UByte orderSeq, UInteger directParentNo)
			throws ServerTaskException {
		while (true) {
			Record1<UByte> toOrderSeqRecord = dsl.select(SB_SITEMENU_TB.ORDER_SQ.min().sub(1).as("toOrderSeq"))
					.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx2"))
					.where(SB_SITEMENU_TB.PARENT_NO.eq(directParentNo)).and(SB_SITEMENU_TB.ORDER_SQ.gt(orderSeq))
					.fetchOne();

			if (null == toOrderSeqRecord.getValue("toOrderSeq")) {
				if (0 == directParentNo.longValue()) {
					Record1<UByte> maxOrderSqRecord = dsl.select(SB_SITEMENU_TB.ORDER_SQ.max())
							.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx1")).fetchOne();

					return maxOrderSqRecord.value1();
				}

				Record1<UInteger> parentMenuRecord = dsl.select(SB_SITEMENU_TB.PARENT_NO).from(SB_SITEMENU_TB)
						.where(SB_SITEMENU_TB.MENU_NO.eq(directParentNo)).fetchOne();

				if (null == parentMenuRecord) {
					String errorMessage = new StringBuilder().append("직계 조상 메뉴[menuNo=").append(directParentNo)
							.append("]가 없습니다").toString();
					throw new ServerTaskException(errorMessage);
				}

				directParentNo = parentMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
				continue;
			}

			UByte toOrderSeq = toOrderSeqRecord.getValue("toOrderSeq", UByte.class);
			return toOrderSeq;
		}
	}

	public static UShort getToGroupSeqOfRelativeRootBoard(DSLContext dsl, UByte boardID, UInteger groupNo,
			UShort groupSeq, UByte depth) {

		Result<Record2<UShort, UByte>> childBoardResult = dsl.select(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.DEPTH)
				.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.GROUP_NO.eq(groupNo))
				.and(SB_BOARD_TB.GROUP_SQ.lt(groupSeq)).orderBy(SB_BOARD_TB.GROUP_SQ.desc()).fetch();

		UShort toGroupSeq = groupSeq;

		for (Record2<UShort, UByte> childBoardRecord : childBoardResult) {
			UShort childGroupSeq = childBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			UByte childDepth = childBoardRecord.getValue(SB_BOARD_TB.DEPTH);

			if (childDepth.shortValue() <= depth.shortValue()) {
				break;
			}

			toGroupSeq = childGroupSeq;
		}

		return toGroupSeq;
	}

	/*
	 * public static MemberRoleType getValidMemberRoleType(Connection conn,
	 * DSLContext dsl, InternalLogger log, String requestedUserID) throws
	 * ServerTaskException { if (null == requestedUserID) { try { conn.rollback(); }
	 * catch (Exception e) { log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = "서비스 요청자를 입력해 주세요"; throw new
	 * ServerTaskException(errorMessage); }
	 * 
	 * MemberRoleType memberRoleTypeOfRequestedUserID = null;
	 * 
	 * Record2<Byte, Byte> memberRecord = dsl.select(SB_MEMBER_TB.STATE,
	 * SB_MEMBER_TB.ROLE).from(SB_MEMBER_TB)
	 * .where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).fetchOne();
	 * 
	 * if (null == memberRecord) { try { conn.rollback(); } catch (Exception e) {
	 * log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).
	 * append("]가 회원 테이블에 존재하지 않습니다") .toString(); throw new
	 * ServerTaskException(errorMessage); }
	 * 
	 * byte memeberStateOfRequestedUserID =
	 * memberRecord.getValue(SB_MEMBER_TB.STATE); MemberStateType
	 * memberStateTypeOfRequestedUserID = null; try {
	 * memberStateTypeOfRequestedUserID =
	 * MemberStateType.valueOf(memeberStateOfRequestedUserID); } catch
	 * (IllegalArgumentException e) { try { conn.rollback(); } catch (Exception e1)
	 * { log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = new
	 * StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
	 * .append(memeberStateOfRequestedUserID).append("] 값이 잘못되었습니다").toString();
	 * 
	 * throw new ServerTaskException(errorMessage); }
	 * 
	 * if (! MemberStateType.OK.equals(memberStateTypeOfRequestedUserID)) { try {
	 * conn.rollback(); } catch (Exception e1) { log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = new
	 * StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
	 * .append(memberStateTypeOfRequestedUserID.getName()).append("]가 정상이 아닙니다").
	 * toString(); throw new ServerTaskException(errorMessage); }
	 * 
	 * byte memberRoleTypeValueOfRequestedUserID =
	 * memberRecord.getValue(SB_MEMBER_TB.ROLE);
	 * 
	 * try { memberRoleTypeOfRequestedUserID =
	 * MemberRoleType.valueOf(memberRoleTypeValueOfRequestedUserID); } catch
	 * (IllegalArgumentException e) { try { conn.rollback(); } catch (Exception e1)
	 * { log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = new
	 * StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 멤버 역활 유형[")
	 * .append(memberRoleTypeValueOfRequestedUserID).append("]이 잘못되어있습니다").toString(
	 * ); throw new ServerTaskException(errorMessage); }
	 * 
	 * return memberRoleTypeOfRequestedUserID; }
	 */

	/**
	 * <pre>
	 * 서비스 요청자가 지정한 서비스를 이용 가능한지 검사를 수행하며 
	 * 요청자의 회원 역활 유형을 반환한다, 만약 이용 권한이 없다면 예외를 던진다
	 * 서비스 이용 권한 유형으로는 (1) 관리자, (2) 일반회원, (3) 손님 이 있으며 
	 * 이 값에 따라 아래와 같이 회원 역활 유형이 정해진다
	 * - 서비스 이용 권한 유형에 따른 이용 가능한 회원 역활 유형 정리표 -
	 * ----------------------------------------------
	 * 서비스 이용 권한 유형	::	회원 역활 유형
	 * ----------------------------------------------
	 * 관리자			::	관리자
	 * 일반회원			::	관리자, 일반회원
	 * 손님			::	관리자, 일반회원, 손님
	 * ----------------------------------------------
	 * </pre>
	 * 
	 * @param conn                  연결 객체
	 * @param dsl                   jooq DLSContext 객체
	 * @param serviceName           서비스 이름
	 * @param servicePermissionType 서비스 이용 권한 유형
	 * @param requestedUserID       서비스 요청자
	 * @return 서비스 요청자의 회원 역활 유형
	 * @throws RollbackServerTaskException 서비스 이용 권한이 없거나 기타 에러 발생시 던지는 예외
	 */
	public static MemberRoleType checkUserAccessRights(DSLContext dsl, String serviceName,
			PermissionType servicePermissionType, String requestedUserID) throws RollbackServerTaskException {

		if (null == dsl) {
			String errorMessage = "the parameter dsl is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == serviceName) {
			String errorMessage = "the parameter serviceName is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == servicePermissionType) {
			String errorMessage = "the parameter servicePermissionType is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == requestedUserID) {
			String errorMessage = "the parameter requestedUserID is null";
			throw new IllegalArgumentException(errorMessage);
		}

		Record2<Byte, Byte> memberRecord = dsl.select(SB_MEMBER_TB.STATE, SB_MEMBER_TB.ROLE).from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).fetchOne();

		if (null == memberRecord) {

			String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]가 회원 테이블에 존재하지 않습니다")
					.toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte memeberStateOfRequestedUserID = memberRecord.getValue(SB_MEMBER_TB.STATE);
		MemberStateType memberStateTypeOfRequestedUserID = null;
		try {
			memberStateTypeOfRequestedUserID = MemberStateType.valueOf(memeberStateOfRequestedUserID);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
					.append(memberStateTypeOfRequestedUserID.getName()).append("] 값이 잘못되었습니다").toString();

			throw new RollbackServerTaskException(errorMessage);
		}

		if (!MemberStateType.OK.equals(memberStateTypeOfRequestedUserID)) {

			String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
					.append(memberStateTypeOfRequestedUserID.getName()).append("]가 정상이 아닙니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		byte memberRoleTypeValueOfRequestedUserID = memberRecord.getValue(SB_MEMBER_TB.ROLE);

		MemberRoleType memberRoleTypeOfRequestedUserID = null;
		try {
			memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(memberRoleTypeValueOfRequestedUserID);
		} catch (IllegalArgumentException e) {

			String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 멤버 역활 유형[")
					.append(memberRoleTypeValueOfRequestedUserID).append("]이 잘못되어있습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		if (PermissionType.ADMIN.equals(servicePermissionType)) {
			if (!MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {

				String errorMessage = new StringBuilder().append(serviceName).append("는 관리자 전용 서비스입니다").toString();
				throw new RollbackServerTaskException(errorMessage);
			}
		} else if (PermissionType.MEMBER.equals(servicePermissionType)) {
			if (MemberRoleType.GUEST.equals(memberRoleTypeOfRequestedUserID)) {

				String errorMessage = new StringBuilder().append(serviceName).append("는 로그인 해야만 이용할 수 있습니다").toString();
				throw new RollbackServerTaskException(errorMessage);
			}
		}

		return memberRoleTypeOfRequestedUserID;
	}

	public static void insertSiteLog(DSLContext dsl, String userID, String logText, Timestamp registeredDate, String ip)
			throws Exception {

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// String yyyyMMdd = sdf.format(registeredDate);

		Field<String> yyyyMMdd = DSL.field("date_format({0}, {1})", String.class, registeredDate, DSL.inline("%Y%m%d"));

		/** '일별 로그 순번' 동기화를 위해 락을 건다 */
		dsl.select(SB_SEQ_TB.SQ_ID).from(SB_SEQ_TB)
				.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.SITE_LOG_LOCK.getSequenceID())).forUpdate().fetchOne();

		long maxOfDayLogSeq = dsl
				.select(DSL.field("if ({0} is null, {1}, {2})", Long.class, SB_SITE_LOG_TB.DAY_LOG_SQ.max(),
						Long.valueOf(0), SB_SITE_LOG_TB.DAY_LOG_SQ.max()))
				.from(SB_SITE_LOG_TB).where(SB_SITE_LOG_TB.YYYYMMDD.eq(yyyyMMdd)).fetchOne().value1();

		if (maxOfDayLogSeq == CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			throw new ServerTaskException("작업 시점의 SB_SITE_LOG_TB 테이블의 날짜 시퀀스가 최대치에 도달하여 더 이상 로그를 추가할 수 없습니다");
		}

		dsl.insertInto(SB_SITE_LOG_TB).set(SB_SITE_LOG_TB.YYYYMMDD, yyyyMMdd)
				.set(SB_SITE_LOG_TB.DAY_LOG_SQ, UInteger.valueOf(maxOfDayLogSeq + 1))
				.set(SB_SITE_LOG_TB.USER_ID, userID).set(SB_SITE_LOG_TB.LOG_TXT, logText)
				.set(SB_SITE_LOG_TB.REG_DT, registeredDate).set(SB_SITE_LOG_TB.IP, ip).execute();
	}

	public static void insertMemberActivityHistory(final DSLContext dsl, String userID, MemberRoleType memberRoleType,
			MemberActivityType memberActivityType, UByte boardID, UInteger boardNo, Timestamp registeredDate)
			throws Exception {

		if (MemberRoleType.GUEST.equals(memberRoleType)) {
			/** 손님은 활동 이력 저장을 하지 않는다 */
			return;
		}

		/** '회원 이력 순번' 동기화를 위해 락을 건다 */
		Record1<UInteger> memberRecord = dsl.select(SB_MEMBER_TB.NEXT_ACTIVE_SQ).from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID)).forUpdate().fetchOne();

		if (null == memberRecord) {
			String errorMessage = new StringBuilder().append("회원[").append(userID).append("]이 존재하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger activitySeq = memberRecord.value1();

		if (UInteger.MAX_VALUE == activitySeq.longValue()) {
			String errorMessage = new StringBuilder().append("회원[").append(userID)
					.append("] 활동 이력이 최대 갯수 만큼 등록되어 더 이상 등록할 수 없습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		dsl.update(SB_MEMBER_TB).set(SB_MEMBER_TB.NEXT_ACTIVE_SQ, SB_MEMBER_TB.NEXT_ACTIVE_SQ.add(1))
				.where(SB_MEMBER_TB.USER_ID.eq(userID)).execute();

		dsl.insertInto(SB_MEMBER_ACTIVITY_HISTORY_TB).set(SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID, userID)
				.set(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ, activitySeq)
				.set(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID, boardID)
				.set(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO, boardNo)
				.set(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_TYPE, memberActivityType.getValue())
				.set(SB_MEMBER_ACTIVITY_HISTORY_TB.REG_DT, registeredDate).execute();
	}

	/**
	 * 지정한 게시글에 속한 그룹의 루트 노드에 해당하는 레코드에 락을 건후 그룹 번호를 반환한다
	 * 
	 * @param conn    JDBC 연결
	 * @param dsl     DSLContext
	 * @param boardID 게시판 식별자
	 * @param boardNo 게시글 번호
	 * @return 지정한 게시글에 속한 그룹 번호
	 * @throws Exception 에러
	 */
	public static UInteger lockRootRecordOfBoardGroup(DSLContext dsl, UByte boardID, UInteger boardNo)
			throws Exception {
		/** 게시글의 그룹 얻기 */
		Record1<UInteger> groupRecord = dsl.select(SB_BOARD_TB.GROUP_NO).from(SB_BOARD_TB)
				.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).fetchOne();

		if (null == groupRecord) {

			String errorMessage = new StringBuilder().append("해당 게시글[boardID=").append(boardID).append(", boardNo=")
					.append(boardNo).append("]이 존재 하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		UInteger groupNo = groupRecord.get(SB_BOARD_TB.GROUP_NO);

		/** 얻은 게시글의 그룹에 대한 락 걸기 */
		Record1<UInteger> groupLockRecord = dsl.select(SB_BOARD_TB.BOARD_NO).from(SB_BOARD_TB)
				.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(groupNo)).forUpdate().fetchOne();

		if (null == groupLockRecord) {
			String errorMessage = new StringBuilder().append("그룹 루트 게시글[boardID=").append(boardID).append(", groupNo=")
					.append(groupNo).append("]이 존재 하지 않습니다").toString();

			new StringBuilder("그룹 루트 게시글이 존재 하지 않습니다").toString();
			throw new RollbackServerTaskException(errorMessage);
		}

		return groupNo;
	}

	/**
	 * <pre>
	 * 명시적으로 공통단에서 마지막에 commit 을 보장하는 트랜재션을 묶을 목적의 서버 비지니스 로직 수행 후 결과를 반환한다.
	 * 
	 * WARNING! 트랜재션을 묶기 위해서 비지니스 로직 중간에 commit 을 해서은 안된다, {@link DSLContext#parsingConnection()} 등등으로 우회하지 말것
	 * 
	 * 참고 : 예외 처리 전략으로 3가지 주요 예외는 각각에 맞게 처리를 한후 예외를 던지고 그외 예외일 경우 rollback 을 해준 후 던진다.
	 * 이렇게 던져진  예외는 {@link ServerTaskException} 로 클라이언트로 전달된다.
	 * 
	 * 첫번째 {@link  RollbackServerTaskException} 는 rollback 을 해 주어야 하는 예외
	 * 
	 * 두번재 {@link CommitServerTaskException 는 commit 을 해주어야 하는 예외, 
	 * 예 로그인시 비밀번호 틀린 경우 에러 상황이지만 틀린 횟수에 대한 DB 작업은 commit 을 해야 한다.
	 * 
	 * 세번째 {@link ParameterServerTaskException 파라미터 값 오류시 던지는 예외,
	 * WARNING! rollback 하는데 비용이 들어 rollback 이 생략된 예외이기때문에 만약 DB 작업 후 호출된다면 문제가 발생할 수 있어 주의가 필요합니다. 
	 * </pre>
	 *  
	 * @param <I> 입력 클래스
	 * @param <O> 출력 클래스
	 * @param dbcpName  dbcp 이름
	 * @param dbTask dB 로 작업하는 서버 비지니 스로직
	 * @param req 입력값을 담은 객체
	 * @return 비지니스 로직 수행 결과값을 담은 객체
	 * @throws Exception 에러 발생시 던지는 예외
	 */
	public static <I, O> O execute(String dbcpName, DBAutoCommitTaskIF<I, O> dbTask, I req)
			throws Exception {
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext dsl = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			O res = dbTask.doWork(dsl, req);

			conn.commit();

			return res;
		} catch (ParameterServerTaskException e) {
			throw e;
		} catch (RollbackServerTaskException e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}

			throw e;
		} catch (CommitServerTaskException e) {
			if (null != conn) {
				try {
					conn.commit();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to commit", e1);
				}
			}

			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}

			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}

	/**
	 * 개발자가 직접 commit 을 해야 하는  서버 비지니스 로직을 수행후 결과를 반환한다.
	 * 
	 * 참고 : 예외 처리 전략으로 3가지 주요 예외는 각각에 맞게 처리를 한후 예외를 던지고 그외 예외일 경우 rollback 을 해준 후 던진다.
	 * 이렇게 던져진  예외는 {@link ServerTaskException} 로 클라이언트로 전달된다.
	 * 
	 * 첫번째 {@link  RollbackServerTaskException} 는 rollback 을 해 주어야 하는 예외
	 * 
	 * 두번재 {@link CommitServerTaskException 는 commit 을 해주어야 하는 예외, 
	 * 예 로그인시 비밀번호 틀린 경우 에러 상황이지만 틀린 횟수에 대한 DB 작업은 commit 을 해야 한다.
	 * 
	 * 세번째 {@link ParameterServerTaskException 파라미터 값 오류시 던지는 예외,
	 * WARNING! rollback 하는데 비용이 들어 rollback 이 생략된 예외이기때문에 만약 DB 작업 후 호출된다면 문제가 발생할 수 있어 주의가 필요합니다. 
	 * 
	 * @param <I> 입력 클래스
	 * @param <O> 출력 클래스
	 * @param dbcpName  dbcp 이름
	 * @param dbTask dB 로 작업하는 서버 비지니 스로직
	 * @param req 입력값을 담은 객체
	 * @return 비지니스 로직 수행 결과값을 담은 객체
	 * @throws Exception Exception 에러 발생시 던지는 예외
	 */
	public static <I, O> O execute(String dbcpName, DBManualCommitTaskIF<I, O> dbTask, I req)
			throws Exception {
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			TransactionManager transactionManager = new TransactionManager(conn);

			DSLContext dsl = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			O res = dbTask.doWork(transactionManager, dsl, req);

			if (0 == transactionManager.getCountOfCommit()) {
				String errorMessage = "DB 작업중 commit 이 호출되지 않아 모든 DB 작업을 취소합니다";
				throw new RollbackServerTaskException(errorMessage);
			}

			Logger log = LoggerFactory.getLogger(ServerDBUtil.class);

			log.info("dbcpName={}, req={}, {}", dbcpName, req.toString(), transactionManager.toString());

			return res;
		} catch (ParameterServerTaskException e) {
			throw e;
		} catch (RollbackServerTaskException e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}

			throw e;
		} catch (CommitServerTaskException e) {
			if (null != conn) {
				try {
					conn.commit();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to commit", e1);
				}
			}

			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}

			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}

	public static void execute(final String dbcpName, List<DBExecutorIF> dbExecutorList) throws Exception {
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext dsl = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			for (DBExecutorIF dbExecutor: dbExecutorList) {
				dbExecutor.execute(dsl);
			}
			
			conn.commit();
		} catch (RollbackServerTaskException e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}
			throw e;
		} catch (CommitServerTaskException e) {
			if (null != conn) {
				try {
					conn.commit();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to commit", e1);
				}
			}
			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}

			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}

	public static void execute(final String dbcpName, final DBExecutorIF dbExecutor) throws Exception {
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext dsl = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			dbExecutor.execute(dsl);		
			
			conn.commit();
			
		} catch (RollbackServerTaskException e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}
			
			throw e;
		} catch (CommitServerTaskException e) {
			if (null != conn) {
				try {
					conn.commit();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to commit", e1);
				}
			}
			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}

			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}

}
