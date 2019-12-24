import static org.junit.Assert.assertEquals;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.util.JDKLoggerCustomFormatter;
import kr.pe.codda.weblib.jsoup.SampleBaseUserSiteWhitelist;
import kr.pe.codda.weblib.jsoup.WhitelistManager;

public class JsoupTest {
	// private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	
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
	public void testClean_unsafeHTML() {
		String unsafe = "<p><a href='ftp://example.com/' onfocus='invalidLink()'>Link</a></p><a href='ftp://example2.com/'>Link 2</a>";
		// String safe = Jsoup.clean(unsafe, Whitelist.basic());
		
		
		boolean isValid = Jsoup.isValid(unsafe, WhitelistManager.getInstance());
		
		 assertEquals(false, isValid);
	}
	
	@Test
	public void testClean_게기글에포함된이미지URL유효성검사_정상인경우() {
		// String unsafe = "<p><a href='ftp://example.com/' onfocus='invalidLink()'>Link</a></p><a href='ftp://example2.com/'>Link 2</a>";
		//String unsafe = "<IMG src=\"javascript:alert('hello')\">";
		String unsafe = "<img src=\"/servlet/DownloadImage?yyyyMMdd=20191224&amp;daySequence=3\">";
		// String safe = Jsoup.clean(unsafe, Whitelist.basic());
		
		SampleBaseUserSiteWhitelist errorReportWhitelist = new SampleBaseUserSiteWhitelist();
		
		boolean isValid = Jsoup.isValid(unsafe, errorReportWhitelist);
		
		
		assertEquals(true, isValid);
		
	}
	
	@Test
	public void testClean_a태그_URL주소값에악성코드() {
		// String unsafe = "<p><a href='ftp://example.com/' onfocus='invalidLink()'>Link</a></p><a href='ftp://example2.com/'>Link 2</a>";
		//String unsafe = "<IMG src=\"javascript:alert('hello')\">";
		String unsafe = "<a href=\"http://javascript:alert('hello');\">aa</a>";
		// String safe = Jsoup.clean(unsafe, Whitelist.basic());
		
		SampleBaseUserSiteWhitelist errorReportWhitelist = new SampleBaseUserSiteWhitelist();
		
		boolean isValid = Jsoup.isValid(unsafe, errorReportWhitelist);
		
		
		assertEquals(false, isValid);
		
	}
	
	@Test
	public void testClean_a태그_URL주소값에악성코드_공백() {
		// String unsafe = "<p><a href='ftp://example.com/' onfocus='invalidLink()'>Link</a></p><a href='ftp://example2.com/'>Link 2</a>";
		//String unsafe = "<IMG src=\"javascript:alert('hello')\">";
		String unsafe = "<a href=\"http:// www.codda.pe.kr\">aa</a>";
		// String safe = Jsoup.clean(unsafe, Whitelist.basic());
		
		SampleBaseUserSiteWhitelist errorReportWhitelist = new SampleBaseUserSiteWhitelist();
		
		boolean isValid = Jsoup.isValid(unsafe, errorReportWhitelist);
		
		
		assertEquals(false, isValid);
		
	}
	
	@Test
	public void testClean_a태그_ftp프로토콜() {
		// String unsafe = "<p><a href='ftp://example.com/' onfocus='invalidLink()'>Link</a></p><a href='ftp://example2.com/'>Link 2</a>";
		//String unsafe = "<IMG src=\"javascript:alert('hello')\">";
		String unsafe = "<a href=\"ftp://ftp.codda.pe.kr\">aa</a>";
		// String safe = Jsoup.clean(unsafe, Whitelist.basic());
		
		SampleBaseUserSiteWhitelist errorReportWhitelist = new SampleBaseUserSiteWhitelist();
		
		boolean isValid = Jsoup.isValid(unsafe, errorReportWhitelist);
		
		
		assertEquals(false, isValid);
		
	}
	
	
	/**
	 * Null breaks up JavaScript directive 는 어떻게 테스트 해야 할지 몰라서 생략
	 * 
	 * 참고 주소 : https://www.owasp.org/index.php/XSS_Filter_Evasion_Cheat_Sheet#List-style-image
	 */
	@Test
	public void testClean_XSS_Filter_Evasion_Cheat_Sheet() {
		String[] unsafes = {
			"<SCRIPT src=http://xss.rocks/xss.js></SCRIPT>",
			"javascript:/*--></title></style></textarea></script></xmp><svg/onload='+/\"/+/onmouseover=1/+/[*/[]/+alert(1)//'>",
			"<IMG src=\"javascript:alert('XSS');\">",
			"<IMG src=javascript:alert('XSS')>",
			"<IMG src=JaVaScRiPt:alert('XSS')>",
			"<IMG src=javascript:alert(&quot;XSS&quot;)>",	
			"<IMG src=`javascript:alert(\"RSnake says, 'XSS'\")`>",
			"<a onmouseover=\"alert(document.cookie)\">xxs link</a>",
			"<a onmouseover=alert(document.cookie)>xxs link</a>",
			"<IMG \"\"\"><SCRIPT>alert(\"XSS\")</SCRIPT>\">",
			"<IMG src=javascript:alert(String.fromCharCode(88,83,83))>",
			"<IMG src=# onmouseover=\"alert('xxs')\">",
			"<IMG src= onmouseover=\"alert('xxs')\">",
			"<IMG onmouseover=\"alert('xxs')\">",
			"<IMG src=/ onerror=\"alert(String.fromCharCode(88,83,83))\"></img>",
			"<img src=x onerror=\"&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041\">",
			"<IMG src=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>",
			"<IMG src=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>",
			"<IMG src=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>",
			"<IMG src=\"jav	ascript:alert('XSS');\">", 
			"<IMG src=\"jav&#x09;ascript:alert('XSS');\">",
			"<IMG src=\"jav&#x0A;ascript:alert('XSS');\">",
			"<IMG src=\"jav&#x0D;ascript:alert('XSS');\">",			
			"<IMG src=\" &#14;  javascript:alert('XSS');\">",
			"<SCRIPT/XSS src=\"http://xss.rocks/xss.js\"></SCRIPT>",
			"<BODY onload!#$%&()*~+-_.,:;?@[/|\\]^`=alert(\"XSS\")>",
			"<SCRIPT/src=\"http://xss.rocks/xss.js\"></SCRIPT>",
			"<<SCRIPT>alert(\"XSS\");//<</SCRIPT>",
			"<SCRIPT src=http://xss.rocks/xss.js?< B >",
			"<SCRIPT src=//xss.rocks/.j>",
			"<IMG src=\"javascript:alert('XSS')\"",
			"<iframe src=http://xss.rocks/scriptlet.html <",
			"<iframe src=http://xss.rocks/scriptlet.html <b>",
			"</script><script>alert('XSS');</script>",
			"</TITLE><SCRIPT>alert(\"XSS\");</SCRIPT>",
			"<INPUT TYPE=\"IMAGE\" src=\"javascript:alert('XSS');\">",
			"<BODY BACKGROUND=\"javascript:alert('XSS')\">",
			"<IMG DYNsrc=\"javascript:alert('XSS')\">",
			"<IMG LOWsrc=\"javascript:alert('XSS')\">",
			"<STYLE>li {list-style-image: url(\"javascript:alert('XSS')\");}</STYLE><UL><LI>XSS</br>",
			"<IMG src='vbscript:msgbox(\"XSS\")'>",
			"<IMG src=\"livescript:alert('hello');\">",
			"<svg/onload=alert('XSS')>",
			// "Set.constructor`alert\\x28document.domain\\x29```",
			"<BODY ONLOAD=alert('XSS')>",
			"<BGSOUND src=\"javascript:alert('XSS');\">",
			"<BR SIZE=\"&{alert('XSS')}\">",
			"<LINK REL=\"stylesheet\" HREF=\"javascript:alert('XSS');\">",
			"<LINK REL=\"stylesheet\" HREF=\"http://xss.rocks/xss.css\">",
			"<STYLE>@import'http://xss.rocks/xss.css';</STYLE>",
			"<META HTTP-EQUIV=\"Link\" Content=\"<http://xss.rocks/xss.css>; REL=stylesheet\">",
			"<STYLE>BODY{-moz-binding:url(\"http://xss.rocks/xssmoz.xml#xss\")}</STYLE>",
			"<STYLE>@im\\port'\\ja\\vasc\\ript:alert(\"XSS\")';</STYLE>",
			"<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">", 
			"<A STYLE='no\\xss:noxss(\"*//*\");xss:ex/*XSS*//*/*/pression(alert(\"XSS\"))'>",		
			"<STYLE TYPE=\"text/javascript\">alert('XSS');</STYLE>",
			"<STYLE>.XSS{background-image:url(\"javascript:alert('XSS')\");}</STYLE><A CLASS=XSS></A>",
			"<STYLE type=\"text/css\">BODY{background:url(\"javascript:alert('XSS')\")}</STYLE>",
			"<XSS STYLE=\"xss:expression(alert('XSS'))\">",
			"<XSS STYLE=\"behavior: url(xss.htc);\">",
			// "¼script¾alert(¢XSS¢)¼/script¾",
			"<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=javascript:alert('XSS');\">",
			"<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=data:text/html base64,PHNjcmlwdD5hbGVydCgnWFNTJyk8L3NjcmlwdD4K\">",
			"<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=http://;URL=javascript:alert('XSS');\">",
			"<IFRAME src=\"javascript:alert('XSS');\"></IFRAME>",
			"<IFRAME src=# onmouseover=\"alert(document.cookie)\"></IFRAME>",
			"<FRAMESET><FRAME src=\"javascript:alert('XSS');\"></FRAMESET>",
			"<TABLE BACKGROUND=\"javascript:alert('XSS')\">",
			"<TABLE><TD BACKGROUND=\"javascript:alert('XSS')\">",
			"<DIV STYLE=\"background-image: url(javascript:alert('XSS'))\">",
			"<DIV STYLE=\"background-image:\\0075\\0072\\006C\\0028'\\006a\\0061\\0076\\0061\\0073\\0063\\0072\\0069\\0070\\0074\\003a\\0061\\006c\\0065\\0072\\0074\\0028.1027\\0058.1053\\0053\\0027\\0029'\\0029\">",
			"<DIV STYLE=\"background-image: url(&#1;javascript:alert('XSS'))\">",
			"<!--[if gte IE 4]> <SCRIPT>alert('XSS');</SCRIPT> <![endif]-->",
			"<BASE HREF=\"javascript:alert('XSS');//\">",
			"<OBJECT TYPE=\"text/x-scriptlet\" DATA=\"http://xss.rocks/scriptlet.html\"></OBJECT>",
			"EMBED src=\"http://ha.ckers.Using an EMBED tag you can embed a Flash movie that contains XSS. Click here for a demo. If you add the attributes allowScriptAccess=\"never\" and allownetworking=\"internal\" it can mitigate this risk (thank you to Jonathan Vanasco for the info).:org/xss.swf\" AllowScriptAccess=\"always\"></EMBED>",
			"<EMBED src=\"data:image/svg+xml;base64,PHN2ZyB4bWxuczpzdmc9Imh0dH A6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcv MjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hs aW5rIiB2ZXJzaW9uPSIxLjAiIHg9IjAiIHk9IjAiIHdpZHRoPSIxOTQiIGhlaWdodD0iMjAw IiBpZD0ieHNzIj48c2NyaXB0IHR5cGU9InRleHQvZWNtYXNjcmlwdCI+YWxlcnQoIlh TUyIpOzwvc2NyaXB0Pjwvc3ZnPg==\" type=\"image/svg+xml\" AllowScriptAccess=\"always\"></EMBED>",
			"<XML ID=\"xss\"><I><B><IMG src=\"javas<!-- -->cript:alert('XSS')\"></B></I></XML><SPAN DATAsrc=\"#xss\" DATAFLD=\"B\" DATAFORMATAS=\"HTML\"></SPAN>",
			"<XML src=\"xsstest.xml\" ID=I></XML><SPAN DATAsrc=#I DATAFLD=C DATAFORMATAS=HTML></SPAN>",
			"<?xml:namespace prefix=\"t\" ns=\"urn:schemas-microsoft-com:time\"><?import namespace=\"t\" implementation=\"#default#time2\"><t:set attributeName=\"innerHTML\" to=\"XSS<SCRIPT DEFER>alert('XSS')</SCRIPT>\">",
			"<SCRIPT src=\"http://xss.rocks/xss.jpg\"></SCRIPT>",
			"<IMG src=\"http://www.thesiteyouareon.com/somecommand.php?somevariables=maliciouscode\">",
			"<HEAD><META HTTP-EQUIV=\"CONTENT-TYPE\" CONTENT=\"text/html; charset=UTF-7\"> </HEAD>+ADw-SCRIPT+AD4-alert('XSS');+ADw-/SCRIPT+AD4-",
			
		};
		
		final boolean expectedValue = false;
		
		for (String unsafe : unsafes) {			
			boolean actualValue = Jsoup.isValid(unsafe, WhitelistManager.getInstance());
			
			// log.info(unsafe +" ==> " + actualValue);
			
			assertEquals(unsafe, expectedValue, actualValue);
		}
		
	}
}
