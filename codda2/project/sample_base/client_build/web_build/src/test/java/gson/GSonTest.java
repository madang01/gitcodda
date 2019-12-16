package gson;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;

public class GSonTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		JDKLoggerCustomFormatter formatter = new JDKLoggerCustomFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test메시지를존슨문자열로만든후역으로메시지객체로만들어비교() {
		ArraySiteMenuRes expectedArraySiteMenuRes = new ArraySiteMenuRes();
		
		
		expectedArraySiteMenuRes.setMailboxID(1);
		expectedArraySiteMenuRes.setMailID(3);
		
		
		List<ArraySiteMenuRes.Menu> menuList = new ArrayList<ArraySiteMenuRes.Menu>();
		ArraySiteMenuRes.Menu menu = new ArraySiteMenuRes.Menu();
		menu.setMenuNo(1);
		menu.setParentNo(0);
		menu.setOrderSeq((short)0);
		menu.setDepth((short)0);
		menu.setMenuName("테스트메뉴01");
		menu.setLinkURL("/test01");
		
		menuList.add(menu);
		
		expectedArraySiteMenuRes.setCnt(menuList.size());
		expectedArraySiteMenuRes.setMenuList(menuList);
		
		log.info(expectedArraySiteMenuRes.toString());
		
		String expectedArraySiteMenuResJson = new Gson().toJson(expectedArraySiteMenuRes);
		
		
		log.info("expectedArraySiteMenuResJson=" + expectedArraySiteMenuResJson);
		
		ArraySiteMenuRes actualArraySiteMenuRes = new Gson().fromJson(expectedArraySiteMenuResJson, ArraySiteMenuRes.class);		
		
		assertEquals(expectedArraySiteMenuRes.getMailboxID(), actualArraySiteMenuRes.getMailboxID());
		assertEquals(expectedArraySiteMenuRes.getMailID(), actualArraySiteMenuRes.getMailID());
		assertEquals(expectedArraySiteMenuRes.toString(), actualArraySiteMenuRes.toString());
		
	}
}
