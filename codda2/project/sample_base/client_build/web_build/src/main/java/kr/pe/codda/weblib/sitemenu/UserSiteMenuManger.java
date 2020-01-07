package kr.pe.codda.weblib.sitemenu;

import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil;

public class UserSiteMenuManger {
	private Logger log = Logger.getLogger(UserSiteMenuManger.class.getName());
	
	private WebsiteMenuInfoFileWatcher websiteMenuPartStringFileWatcher = null;

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class UserSiteMenuMangerHolder {
		static final UserSiteMenuManger singleton = new UserSiteMenuManger();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static UserSiteMenuManger getInstance() {
		return UserSiteMenuMangerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private UserSiteMenuManger() {
		websiteMenuPartStringFileWatcher = new WebsiteMenuInfoFileWatcher();
		websiteMenuPartStringFileWatcher.start();
	}

	private String getTabStrings(int tapStep) {
		StringBuilder tapStringBuilder = new StringBuilder();
		for (int i = 0; i < tapStep; i++) {
			tapStringBuilder.append("\t");
		}
		return tapStringBuilder.toString();
	}

	private String makeWebsiteMenuPartStringUsingMenuList(String menuGroupURL,
			JsonArray menuListJsonArray, int tapStep) {
		if (null == menuListJsonArray) {
			return "";
		}
		
		StringBuilder websiteMenuPartStringBuilder = new StringBuilder();
		int size = menuListJsonArray.size();
		for (int i = 0; i < size; i++) {
			JsonElement menuJsonElement = menuListJsonArray.get(i);
			if (!menuJsonElement.isJsonObject()) {
				log.warning("the var menuJsonElement is not a JsonObject");
				return "";
			}

			JsonObject menuJsonObject = menuJsonElement.getAsJsonObject();

			JsonElement menuNameJsonElement = menuJsonObject.get("menuName");
			JsonElement linkURLJsonElement = menuJsonObject.get("linkURL");
			JsonElement childMenuListSizeJsonElement = menuJsonObject
					.get("childMenuListSize");
			JsonElement childMenuListJsonElement = menuJsonObject
					.get("childMenuList");

			if (null == menuNameJsonElement) {				
				String errorMessage = new StringBuilder()
						.append("the parameter menuListJsonArray[")
						.append(i)
						.append("] doesn't have a menuName element").toString();
				
				log.warning(errorMessage);
				return "";
			}
			
			String menuName = null;

			try {
				menuName = menuNameJsonElement.getAsString();
			} catch (Exception e) {
				log.warning("fail to convert the menuName jsonElement to string");
				return "";
			}


			if (null == linkURLJsonElement) {
				String errorMessage = new StringBuilder()
						.append("the parameter menuListJsonArray[index=")
						.append(i)
						.append(", menuName=")
						.append(menuName)
						.append("] doesn't have a linkURL element").toString();
				
				log.warning(errorMessage);
				return "";
			}
			
			String linkURL = null;
			try {
				linkURL = linkURLJsonElement.getAsString();
			} catch (Exception e) {
				log.warning("fail to convert the linkURL jsonElement to string");
				return "";
			}

			if (null == childMenuListSizeJsonElement) {
				String errorMessage = new StringBuilder()
						.append("the parameter menuListJsonArray[index=")
						.append(i)
						.append(", menuName=")
						.append(menuName)
						.append("] doesn't have a childMenuListSize element").toString();
				
				log.warning(errorMessage);
				return "";
			}
			
			int childMenuListSize;
			try {
				childMenuListSize = childMenuListSizeJsonElement.getAsInt();
			} catch (Exception e) {
				log.warning("fail to convert the childMenuListSize jsonElement to integer");
				return "";
			}

			if (null == childMenuListJsonElement) {
				String errorMessage = new StringBuilder()
						.append("the parameter menuListJsonArray[index=")
						.append(i)
						.append(", menuName=")
						.append(menuName)
						.append("] doesn't have a childMenuList element").toString();
				
				log.warning(errorMessage);
				return "";
			}			

			if (0 == childMenuListSize) {
				websiteMenuPartStringBuilder.append(getTabStrings(tapStep));

				if (null != menuGroupURL && menuGroupURL.equals(linkURL)) {
					websiteMenuPartStringBuilder
							.append("<li class=\"active\">");
				} else {
					websiteMenuPartStringBuilder.append("<li>");
				}

				websiteMenuPartStringBuilder.append("<a href=\"");
				websiteMenuPartStringBuilder.append(linkURL);
				websiteMenuPartStringBuilder.append("\">");
				websiteMenuPartStringBuilder.append(menuName);
				websiteMenuPartStringBuilder.append("</a></li>");
				websiteMenuPartStringBuilder
						.append(CommonStaticFinalVars.NEWLINE);
			} else {
				if (! childMenuListJsonElement.isJsonArray()) {
					String errorMessage = new StringBuilder()
							.append("the parameter menuListJsonArray[index=")
							.append(i)
							.append(", menuName=")
							.append(menuName)
							.append("'s childMenuList is not a JsonArray").toString();
					
					log.warning(errorMessage);
					return "";
				}

				JsonArray childMenuListJsonArray = childMenuListJsonElement
						.getAsJsonArray();

				websiteMenuPartStringBuilder.append(getTabStrings(tapStep));
				websiteMenuPartStringBuilder.append("<li class=\"dropdown\">");
				websiteMenuPartStringBuilder
						.append(CommonStaticFinalVars.NEWLINE);
				websiteMenuPartStringBuilder.append(getTabStrings(tapStep + 1));
				websiteMenuPartStringBuilder
						.append("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"");
				websiteMenuPartStringBuilder.append(linkURL);
				websiteMenuPartStringBuilder.append("\">");
				websiteMenuPartStringBuilder.append(menuName);
				websiteMenuPartStringBuilder
						.append("<span class=\"caret\"></span></a>");
				websiteMenuPartStringBuilder
						.append(CommonStaticFinalVars.NEWLINE);

				websiteMenuPartStringBuilder.append(getTabStrings(tapStep + 1));
				websiteMenuPartStringBuilder
						.append("<ul class=\"dropdown-menu\">");
				websiteMenuPartStringBuilder
						.append(CommonStaticFinalVars.NEWLINE);

				websiteMenuPartStringBuilder
						.append(makeWebsiteMenuPartStringUsingMenuList(
								menuGroupURL, childMenuListJsonArray,
								tapStep + 2));
				// siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);

				websiteMenuPartStringBuilder.append(getTabStrings(tapStep + 1));
				websiteMenuPartStringBuilder.append("</ul>");
				websiteMenuPartStringBuilder
						.append(CommonStaticFinalVars.NEWLINE);

				websiteMenuPartStringBuilder.append(getTabStrings(tapStep));
				websiteMenuPartStringBuilder.append("</li>");
				websiteMenuPartStringBuilder
						.append(CommonStaticFinalVars.NEWLINE);

			}

		}
		return websiteMenuPartStringBuilder.toString();
	}

	public String getMenuNavbarString(String menuGroupURL, boolean isLogin, String userName) {
		final int tapStep = 1;		

		JsonArray rootMenuListJsonArray = websiteMenuPartStringFileWatcher
				.getRootMenuListJsonArray();
		String webisteMenuPartString = makeWebsiteMenuPartStringUsingMenuList(
				menuGroupURL, rootMenuListJsonArray, tapStep + 4);

		if (null == menuGroupURL) {
			menuGroupURL = "/";
		}
		
		// userName = ;

		StringBuilder menuNavbarStringBuilder = new StringBuilder()
				.append(getTabStrings(tapStep))
				.append("<nav class=\"navbar navbar-default\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 1))
				.append("<div class=\"container-fluid\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 2))
				.append("<div class=\"navbar-header\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 3))
				.append("<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#coddaNavbar\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 4))
				.append("<span class=\"icon-bar\"></span>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 4))
				.append("<span class=\"icon-bar\"></span>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 4))
				.append("<span class=\"icon-bar\"></span>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 3))
				.append("</button>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 3))
				.append("<a class=\"navbar-brand\" href=\"/\">Codda</a>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 2))
				.append("</div>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 2))
				.append("<div class=\"collapse navbar-collapse\" id=\"coddaNavbar\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 3))
				.append("<ul class=\"nav navbar-nav\">")
				.append(CommonStaticFinalVars.NEWLINE);

		if (menuGroupURL.equals("/")) {
			menuNavbarStringBuilder.append(getTabStrings(tapStep + 4))
					.append("<li class=\"active\"><a href=\"/\">Home</a></li>")
					.append(CommonStaticFinalVars.NEWLINE);
		} else {
			menuNavbarStringBuilder.append(getTabStrings(tapStep + 4))
					.append("<li><a href=\"/\">Home</a></li>")
					.append(CommonStaticFinalVars.NEWLINE);
		}
		
		menuNavbarStringBuilder
				.append(webisteMenuPartString)
				.append(getTabStrings(tapStep + 3))
				.append("</ul>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 3))
				.append("<ul class=\"nav navbar-nav navbar-right\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 4))
				.append("<li><a href=\"")
				.append("/servlet/MemberRegisterInput")
				.append("\"><span class=\"glyphicon glyphicon-user\"></span> Sign Up</a></li>")
				.append(CommonStaticFinalVars.NEWLINE);

		if (isLogin) {
			menuNavbarStringBuilder
					.append(getTabStrings(tapStep + 4))
					.append("<li><a href=\"")
					.append("/jsp/member/logout.jsp")
					.append("\" title=\"")
					.append(StringEscapeActorUtil.replace(userName,
							StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4))
					.append("\"><span class=\"glyphicon glyphicon-log-out\"></span> logout</a></li>")
					.append(CommonStaticFinalVars.NEWLINE);
		} else {
			menuNavbarStringBuilder
					.append(getTabStrings(tapStep + 4))
					.append("<li><a href=\"")
					.append("/servlet/MemberLoginInput")
					.append("\"><span class=\"glyphicon glyphicon-log-in\"></span> login</a></li>")
					.append(CommonStaticFinalVars.NEWLINE);
		}

		menuNavbarStringBuilder.append(getTabStrings(tapStep + 3))
				.append("</ul>").append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 2)).append("</div>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep + 1)).append("</div>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep)).append("</nav>")
				.append(CommonStaticFinalVars.NEWLINE);

		return menuNavbarStringBuilder.toString();
	}
}
