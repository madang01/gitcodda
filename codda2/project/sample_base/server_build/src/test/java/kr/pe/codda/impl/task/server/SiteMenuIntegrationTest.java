package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import junitlib.AbstractBoardTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
import kr.pe.codda.impl.message.MenuDeleteReq.MenuDeleteReq;
import kr.pe.codda.impl.message.MenuMoveDownReq.MenuMoveDownReq;
import kr.pe.codda.impl.message.MenuMoveUpReq.MenuMoveUpReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.impl.message.TreeSiteMenuReq.TreeSiteMenuReq;
import kr.pe.codda.impl.message.TreeSiteMenuRes.TreeSiteMenuRes;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.SiteMenuTree;
import kr.pe.codda.server.lib.SiteMenuTreeNode;
import kr.pe.codda.server.lib.VirtualSiteMenuTreeBuilderIF;

/**
 * 사이트 메뉴 통합 테스트
 * 
 * @author Won Jonghoon
 *
 */
public class SiteMenuIntegrationTest extends AbstractBoardTest {
	// private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	// private static UInteger backupMenuNo = null;
	// private static ArraySiteMenuRes backupArraySiteMenuRes = null;

	@Before
	public void setUp() {
		UByte menuSequenceID = SequenceType.MENU.getSequenceID();
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1))
						.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID)).execute();

				create.delete(SB_SITEMENU_TB).execute();

				conn.commit();
			});
		} catch (Exception e) {
			log.warn(e.getMessage(), e);

			fail("알수 없는 에러");
		}
	}

	@After
	public void tearDown() {

	}

	@Test
	public void 사이트메뉴배열형목록조회_일반인() {
		String requestedUserIDForUser = "test01";

		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = null;
		try {
			arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		arraySiteMenuReq.setRequestedUserID(requestedUserIDForUser);

		try {
			arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "배열형 메뉴 조회 서비스는 관리자 전용 서비스입니다";

			assertEquals("관리자에 의해 차단된 글 삭제할때 경고 메시지인지 검사", expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}

	}

	@Test
	public void 사이트메뉴계층형조회_일반인() {
		String requestedUserIDForUser = "test01";

		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = null;
		try {
			treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		treeSiteMenuReq.setRequestedUserID(requestedUserIDForUser);
		try {
			treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "계층형 메뉴 조회 서비스는 관리자 전용 서비스입니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
	}

	@Test
	public void 초기상태에서_사이트메뉴배열형목록조회와_계층형조회비교() {
		String requestedUserIDForAdmin = "admin";

		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = null;
		try {
			arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		arraySiteMenuReq.setRequestedUserID(requestedUserIDForAdmin);

		try {
			ArraySiteMenuRes arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);

			if (arraySiteMenuRes.getCnt() != 0) {
				log.warn("초기 상태에서는 배열 크기가 0이 아닙니다, arraySiteMenuRes={}", arraySiteMenuRes.toString());
				fail("초기 상태에서는 배열 크기가 0이 아닙니다");
			}
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}

		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = null;
		try {
			treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		treeSiteMenuReq.setRequestedUserID(requestedUserIDForAdmin);
		try {
			TreeSiteMenuRes treeSiteMenuRes = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);

			if (treeSiteMenuRes.getRootMenuListSize() != 0) {
				log.warn("초기 상태에서는 배열 크기가 0이 아닙니다, treeSiteMenuRes={}", treeSiteMenuRes.toString());
				fail("초기 상태에서는 배열 크기가 0이 아닙니다");
			}
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
	}

	@Test
	public void 메뉴이동테스트_상단이동후다시하단이동하여원복() {
		String requestedUserIDForAdmin = "admin";

		/**
		 * WARNING! 메뉴 이동 테스트 대상 메뉴는 메뉴 깊이 3을 갖는 '세션키 테스트' 와 'RSA 테스트' 이다. 입력한 메뉴 순서는
		 * '세션키 테스트' 이고 다음이 'RSA 테스트' 이다.
		 */
		// final long menuNoForMoveUpDownTest = 14L;

		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = null;
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = null;
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = null;
		try {
			arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
			menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
			menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		class VirtualSiteMenuTreeBuilder implements VirtualSiteMenuTreeBuilderIF {

			@Override
			public SiteMenuTree build() {
				SiteMenuTree siteMenuTree = new SiteMenuTree();

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("사랑방");
					rootSiteMenuTreeNode.setLinkURL("/jsp/community/body.jsp");

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("공지");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=0");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자유게시판");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=1");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("FAQ");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=2");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("문서");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("코다 활용 howto");
						childSiteMenuTreeNode.setLinkURL("/jsp/doc/CoddaHowTo.jsp");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("도구");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-비 로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFNotLogin");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFLogin");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("세션키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFSessionKey");

						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("세션키_2단계_1");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_1");

							{
								SiteMenuTreeNode childchildchildSiteMenuTreeNode = new SiteMenuTreeNode();

								childchildchildSiteMenuTreeNode.setMenuName("세션키_3단계_1");
								childchildchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_1");

								childchildSiteMenuTreeNode.addChildSiteMenuNode(childchildchildSiteMenuTreeNode);
							}

							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}

						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("세션키_2단계_2");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_2");

							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("RSA 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSRSAInput");

						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("RSA_2단계_1");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_twoDepth_1");

							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}

						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("RSA_2단계_2");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_twoDepth_2");

							{
								SiteMenuTreeNode childchildchildSiteMenuTreeNode = new SiteMenuTreeNode();

								childchildchildSiteMenuTreeNode.setMenuName("RSA_3단계_1");
								childchildchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_threeDepth_1");

								childchildSiteMenuTreeNode.addChildSiteMenuNode(childchildchildSiteMenuTreeNode);
							}

							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("메세지 다이제스트(MD) 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSMessageDigestInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("대칭키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSSymmetricKeyInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("에코 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/Echo");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("모든 데이터 타입 검증");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자바 문자열 변환 도구");
						childSiteMenuTreeNode.setLinkURL("/servlet/JavaStringConverterInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("회원");
					rootSiteMenuTreeNode.setLinkURL("/jsp/member/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("로그인");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("회원 가입");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserSiteMembershipInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				return siteMenuTree;
			}
		}

		VirtualSiteMenuTreeBuilderIF virtualSiteMenuTreeBuilder = new VirtualSiteMenuTreeBuilder();
		SiteMenuTree virtualSiteMenuTree = virtualSiteMenuTreeBuilder.build();
		virtualSiteMenuTree.toDBRecord(TEST_DBCP_NAME);

		// RSA 테스트
		SiteMenuTreeNode sourceSiteMenuTreeNode = virtualSiteMenuTree.find("RSA 테스트");
		if (null == sourceSiteMenuTreeNode) {
			fail("상단 이동할 대상 메뉴[RSA 테스트] 찾기 실패");
		}

		SiteMenuTreeNode targetSiteMenuTreeNode = virtualSiteMenuTree.find("세션키 테스트");

		if (null == targetSiteMenuTreeNode) {
			fail("상단 이동할 위치에 있는 메뉴[세션키 테스트] 찾기 실패");
		}

		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		menuUpMoveReq.setMenuNo(sourceSiteMenuTreeNode.getMenuNo());

		try {
			MessageResultRes messageResultRes = menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);
			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}

		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		arraySiteMenuReq.setRequestedUserID(requestedUserIDForAdmin);
		ArraySiteMenuRes arraySiteMenuRes = null;

		try {
			arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}

		for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
			String menuName = siteMenu.getMenuName();

			if (menuName.equals("RSA 테스트")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_2단계_1")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq() + 1, siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_2단계_2")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq() + 2, siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_3단계_1")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq() + 3, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키 테스트")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_2단계_1")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq() + 1, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_3단계_1")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq() + 2, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_2단계_2")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq() + 3, siteMenu.getOrderSeq());
			} else {
				SiteMenuTreeNode workingSiteMenuTreeNode = virtualSiteMenuTree.find(menuName);
				if (null == workingSiteMenuTreeNode) {
					fail("메뉴[" + menuName + "] 찾기 실패");
				}
				assertEquals("메뉴 순서 비교", workingSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			}
		}

		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		menuDownMoveReq.setMenuNo(sourceSiteMenuTreeNode.getMenuNo());

		try {
			MessageResultRes messageResultRes = menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}

		try {
			arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}

		for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
			String menuName = siteMenu.getMenuName();

			SiteMenuTreeNode workingSiteMenuTreeNode = virtualSiteMenuTree.find(menuName);
			if (null == workingSiteMenuTreeNode) {
				fail("메뉴[" + menuName + "] 찾기 실패");
			}
			assertEquals("메뉴 순서 비교", workingSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
		}
	}

	@Test
	public void 메뉴이동테스트_하단이동후다시상단이동하여원복() {
		String requestedUserIDForAdmin = "admin";

		/**
		 * WARNING! 메뉴 이동 테스트 대상 메뉴는 메뉴 깊이 3을 갖는 '세션키 테스트' 와 'RSA 테스트' 이다. 입력한 메뉴 순서는
		 * '세션키 테스트' 이고 다음이 'RSA 테스트' 이다.
		 */
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = null;
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = null;
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = null;
		try {
			arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
			menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
			menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		class VirtualSiteMenuTreeBuilder implements VirtualSiteMenuTreeBuilderIF {

			@Override
			public SiteMenuTree build() {
				SiteMenuTree siteMenuTree = new SiteMenuTree();

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("사랑방");
					rootSiteMenuTreeNode.setLinkURL("/jsp/community/body.jsp");

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("공지");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=0");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자유게시판");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=1");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("FAQ");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=2");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("문서");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("코다 활용 howto");
						childSiteMenuTreeNode.setLinkURL("/jsp/doc/CoddaHowTo.jsp");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("도구");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-비 로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFNotLogin");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFLogin");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("세션키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFSessionKey");

						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("세션키_2단계_1");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_1");

							{
								SiteMenuTreeNode childchildchildSiteMenuTreeNode = new SiteMenuTreeNode();

								childchildchildSiteMenuTreeNode.setMenuName("세션키_3단계_1");
								childchildchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_1");

								childchildSiteMenuTreeNode.addChildSiteMenuNode(childchildchildSiteMenuTreeNode);
							}

							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}

						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("세션키_2단계_2");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_2");

							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("RSA 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSRSAInput");

						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("RSA_2단계_1");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_twoDepth_1");

							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}

						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("RSA_2단계_2");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_twoDepth_2");

							{
								SiteMenuTreeNode childchildchildSiteMenuTreeNode = new SiteMenuTreeNode();

								childchildchildSiteMenuTreeNode.setMenuName("RSA_3단계_1");
								childchildchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_threeDepth_1");

								childchildSiteMenuTreeNode.addChildSiteMenuNode(childchildchildSiteMenuTreeNode);
							}

							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("메세지 다이제스트(MD) 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSMessageDigestInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("대칭키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSSymmetricKeyInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("에코 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/Echo");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("모든 데이터 타입 검증");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자바 문자열 변환 도구");
						childSiteMenuTreeNode.setLinkURL("/servlet/JavaStringConverterInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("회원");
					rootSiteMenuTreeNode.setLinkURL("/jsp/member/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("로그인");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("회원 가입");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserSiteMembershipInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				return siteMenuTree;
			}
		}

		VirtualSiteMenuTreeBuilderIF virtualSiteMenuTreeBuilder = new VirtualSiteMenuTreeBuilder();
		SiteMenuTree virtualSiteMenuTree = virtualSiteMenuTreeBuilder.build();
		virtualSiteMenuTree.toDBRecord(TEST_DBCP_NAME);

		SiteMenuTreeNode sourceSiteMenuTreeNode = virtualSiteMenuTree.find("세션키 테스트");
		if (null == sourceSiteMenuTreeNode) {
			fail("하단 이동할 대상 메뉴[세션키 테스트] 찾기 실패");
		}

		SiteMenuTreeNode targetSiteMenuTreeNode = virtualSiteMenuTree.find("RSA 테스트");

		if (null == targetSiteMenuTreeNode) {
			fail("하단 이동할 위치에 있는 메뉴[RSA 테스트] 찾기 실패");
		}

		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		menuDownMoveReq.setMenuNo(sourceSiteMenuTreeNode.getMenuNo());

		try {
			MessageResultRes messageResultRes = menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}

		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		arraySiteMenuReq.setRequestedUserID(requestedUserIDForAdmin);
		ArraySiteMenuRes arraySiteMenuRes = null;

		try {
			arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}

		for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
			String menuName = siteMenu.getMenuName();

			if (menuName.equals("RSA 테스트")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_2단계_1")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq() + 1, siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_2단계_2")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq() + 2, siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_3단계_1")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq() + 3, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키 테스트")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_2단계_1")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq() + 1, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_3단계_1")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq() + 2, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_2단계_2")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq() + 3, siteMenu.getOrderSeq());
			} else {
				SiteMenuTreeNode workingSiteMenuTreeNode = virtualSiteMenuTree.find(menuName);
				if (null == workingSiteMenuTreeNode) {
					fail("메뉴[" + menuName + "] 찾기 실패");
				}
				assertEquals("메뉴 순서 비교", workingSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			}
		}

		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		menuUpMoveReq.setMenuNo(sourceSiteMenuTreeNode.getMenuNo());

		try {
			MessageResultRes messageResultRes = menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);
			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}

		try {
			arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}

		for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
			String menuName = siteMenu.getMenuName();

			SiteMenuTreeNode workingSiteMenuTreeNode = virtualSiteMenuTree.find(menuName);
			if (null == workingSiteMenuTreeNode) {
				fail("메뉴[" + menuName + "] 찾기 실패");
			}
			assertEquals("메뉴 순서 비교", workingSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
		}
	}

	@Test
	public void 루트메뉴등록테스트_255개초과() {
		/*
		 * ch.qos.logback.classic.Logger logger =
		 * (Logger)LoggerFactory.getLogger("org.jooq"); Level oldLogLevel =
		 * logger.getLevel(); logger.setLevel(Level.OFF);
		 */
		String requestedUserIDForAdmin = "admin";

		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		for (int i = 0; i < CommonStaticFinalVars.UNSIGNED_BYTE_MAX; i++) {
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
			rootMenuAddReq.setMenuName("temp" + i);
			rootMenuAddReq.setLinkURL("/temp" + i);
			try {
				rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to add ").append(i + 1).append("th root menu")
						.toString();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		// logger.setLevel(oldLogLevel);

		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
		rootMenuAddReq.setMenuName("temp255");
		rootMenuAddReq.setLinkURL("/temp255");
		try {
			rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String expectedMessage = "메뉴 갯수가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다";
			log.warn(e.getMessage(), e);

			assertEquals(expectedMessage, e.getMessage());

		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("알수 없는 에러가 발생하여 메뉴 최대치 초과 테스트 실패");
		}

		// logger.setLevel(Level.OFF);
	}

	@Test
	public void 자식메뉴추가테스트_255개초과() {
		String requestedUserIDForAdmin = "admin";

		ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger("org.jooq");
		Level oldLogLevel = logger.getLevel();
		logger.setLevel(Level.OFF);

		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		for (int i = 0; (i + 1) < CommonStaticFinalVars.UNSIGNED_BYTE_MAX; i++) {
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
			rootMenuAddReq.setMenuName("temp" + i);
			rootMenuAddReq.setLinkURL("/temp" + i);
			try {
				rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to add ").append(i + 1).append("th root menu")
						.toString();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		logger.setLevel(oldLogLevel);

		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
		rootMenuAddReq.setMenuName("temp254");
		rootMenuAddReq.setLinkURL("/temp254");

		RootMenuAddRes rootMenuAddRes = new RootMenuAddRes();
		try {
			rootMenuAddRes = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to add ").append(255).append("th root menu")
					.toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		ChildMenuAddReqServerTask childMenuAddReqServerTask = null;
		try {
			childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		ChildMenuAddReq firstChildMenuAddReq = new ChildMenuAddReq();
		firstChildMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
		firstChildMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		firstChildMenuAddReq.setMenuName("temp254_1");
		firstChildMenuAddReq.setLinkURL("/temp254_1");

		try {
			childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, firstChildMenuAddReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String expectedMessage = "메뉴 갯수가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다";
			log.warn(e.getMessage(), e);

			assertEquals(expectedMessage, e.getMessage());
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("알수 없는 에러가 발생하여 메뉴 최대치 초과 테스트 실패");
		}

		logger.setLevel(Level.OFF);
	}

	@Test
	public void 자식메뉴추가테스트_부모없음() {
		String requestedUserIDForAdmin = "admin";
		final long parentMenuNo = 10;

		ChildMenuAddReqServerTask childMenuAddReqServerTask = null;
		try {
			childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		ChildMenuAddReq firstChildMenuAddReq = new ChildMenuAddReq();
		firstChildMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
		firstChildMenuAddReq.setParentNo(parentMenuNo);
		firstChildMenuAddReq.setMenuName("tempNoParent_1");
		firstChildMenuAddReq.setLinkURL("/tempNoParent_1");

		try {
			childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, firstChildMenuAddReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String expectedMessage = new StringBuilder().append("부모 메뉴[").append(parentMenuNo).append("]가 존재하지 않습니다")
					.toString();
			log.warn(e.getMessage(), e);

			assertEquals(expectedMessage, e.getMessage());
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("알수 없는 에러가 발생하여 메뉴 최대치 초과 테스트 실패");
		}
	}

	@Test
	public void 메뉴삭제테스트_2개루트메뉴등록후1개만삭제한경우() {
		String requestedUserIDForAdmin = "admin";

		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		RootMenuAddReq rootMenuAddReqForDelete = new RootMenuAddReq();
		rootMenuAddReqForDelete.setRequestedUserID(requestedUserIDForAdmin);
		rootMenuAddReqForDelete.setMenuName("temp1");
		rootMenuAddReqForDelete.setLinkURL("/temp01");

		RootMenuAddRes rootMenuAddResForDelete = null;
		try {
			rootMenuAddResForDelete = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReqForDelete);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}

		RootMenuAddReq rootMenuAddReqForSpace = new RootMenuAddReq();
		rootMenuAddReqForSpace.setRequestedUserID(requestedUserIDForAdmin);
		rootMenuAddReqForSpace.setMenuName("temp2");
		rootMenuAddReqForSpace.setLinkURL("/temp02");

		RootMenuAddRes rootMenuAddResForSpace = null;
		try {
			rootMenuAddResForSpace = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReqForSpace);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}

		MenuDeleteReqServerTask menuDeleteReqServerTask = null;
		try {
			menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setRequestedUserID(requestedUserIDForAdmin);
		menuDeleteReq.setMenuNo(rootMenuAddResForDelete.getMenuNo());

		MessageResultRes messageResultRes = null;
		try {
			messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}

		if (!messageResultRes.getIsSuccess()) {
			fail(messageResultRes.getResultMessage());
		}

		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = null;
		try {
			arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		arraySiteMenuReq.setRequestedUserID(requestedUserIDForAdmin);
		try {
			ArraySiteMenuRes arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);

			if (arraySiteMenuRes.getCnt() != 1) {
				log.info(arraySiteMenuRes.toString());
				fail("메뉴 삭제 실패하였습니다");
			}

			for (ArraySiteMenuRes.Menu menu : arraySiteMenuRes.getMenuList()) {
				if (menu.getMenuNo() != rootMenuAddResForSpace.getMenuNo()) {
					fail("메뉴 삭제후 남은 메뉴 번호와 목록에서 얻은 메뉴 번호가 다릅니다");
				}
			}

		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error");
		}
	}

	/**
	 * 메뉴 테이블 초기 상태 즉 메뉴가 하나도 없는 상태에서 삭제 테스트
	 */
	@Test
	public void 메뉴삭제테스트_삭제할대상메뉴없는경우() {
		String requestedUserIDForAdmin = "admin";

		MenuDeleteReqServerTask menuDeleteReqServerTask = null;
		try {
			menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setRequestedUserID(requestedUserIDForAdmin);
		menuDeleteReq.setMenuNo(10);

		try {
			menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			String expectedMessage = new StringBuilder().append("삭제할 메뉴[").append(menuDeleteReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();

			assertEquals(expectedMessage, e.getMessage());
		} catch (Exception e) {
			log.warn("error", e);
			fail("알수 없는 에러가 발생하여 삭제 대상이 없는 삭제 테스트 실패");
		}
	}

	@Test
	public void 메뉴삭제테스트_자식이있는메뉴삭제할경우() {
		String requestedUserIDForAdmin = "admin";

		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
		rootMenuAddReq.setMenuName("temp1");
		rootMenuAddReq.setLinkURL("/temp01");

		RootMenuAddRes rootMenuAddRes = null;
		try {
			rootMenuAddRes = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}

		ChildMenuAddReqServerTask childMenuAddReqServerTask = null;
		try {
			childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		ChildMenuAddReq childMenuAddReq = new ChildMenuAddReq();
		childMenuAddReq.setRequestedUserID(requestedUserIDForAdmin);
		childMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		childMenuAddReq.setMenuName("temp1_1");
		childMenuAddReq.setLinkURL("/temp01_1");

		ChildMenuAddRes childMenuAddRes = null;
		try {
			childMenuAddRes = childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, childMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}

		MenuDeleteReqServerTask menuDeleteReqServerTask = null;
		try {
			menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setRequestedUserID(requestedUserIDForAdmin);
		menuDeleteReq.setMenuNo(rootMenuAddRes.getMenuNo());

		MessageResultRes messageResultRes = null;
		try {
			messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);

			fail("no ServerTaskException");
		} catch (ServerTaskException e) {
			log.info(e.getMessage(), e);

			String expectedErrorMessage = new StringBuilder().append("자식이 있는 메뉴[").append(menuDeleteReq.getMenuNo())
					.append("]는 삭제 할 수 없습니다").toString();

			String acutalErrorMessage = e.getMessage();

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}

		menuDeleteReq.setMenuNo(childMenuAddRes.getMenuNo());

		try {
			messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);

			if (!messageResultRes.getIsSuccess()) {
				fail("테스트용 자식 메뉴 삭제 실패");
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}

		menuDeleteReq.setMenuNo(rootMenuAddRes.getMenuNo());

		try {
			messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);

			if (!messageResultRes.getIsSuccess()) {
				fail("테스트용 루트 메뉴 삭제 실패");
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
	}

	@Test
	public void 메뉴생성_2018년8월19일기준메뉴() {
		class VirtualSiteMenuTreeBuilder implements VirtualSiteMenuTreeBuilderIF {

			@Override
			public SiteMenuTree build() {
				SiteMenuTree siteMenuTree = new SiteMenuTree();

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("사랑방");
					rootSiteMenuTreeNode.setLinkURL("/jsp/community/body.jsp");

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("공지");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=0");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자유게시판");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=1");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("FAQ");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=2");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("문서");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("코다 활용 howto");
						childSiteMenuTreeNode.setLinkURL("/jsp/doc/CoddaHowTo.jsp");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("도구");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-비 로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFNotLogin");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFLogin");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("세션키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFSessionKey");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("RSA 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSRSAInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("메세지 다이제스트(MD) 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSMessageDigestInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("대칭키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSSymmetricKeyInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("에코 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/Echo");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("모든 데이터 타입 검증");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자바 문자열 변환 도구");
						childSiteMenuTreeNode.setLinkURL("/servlet/JavaStringConverterInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();

					rootSiteMenuTreeNode.setMenuName("회원");
					rootSiteMenuTreeNode.setLinkURL("/jsp/member/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("로그인");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("회원 가입");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserSiteMembershipInput");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				return siteMenuTree;
			}
		}

		VirtualSiteMenuTreeBuilderIF virtualSiteMenuTreeBuilder = new VirtualSiteMenuTreeBuilder();
		SiteMenuTree virtualSiteMenuTree = virtualSiteMenuTreeBuilder.build();
		virtualSiteMenuTree.toDBRecord(TEST_DBCP_NAME);
	}
}
