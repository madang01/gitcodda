package kr.pe.codda.weblib.common;

import org.apache.commons.text.StringEscapeUtils;

import kr.pe.codda.weblib.sitemenu.UserSiteMenuManger;

public abstract class DocumentFileBuilder {

	public static String build(AccessedUserInformation accessedUserInformation, String relativeURL, String subject, String contents) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<!DOCTYPE html>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<html lang=\"ko\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<head>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<meta charset=\"UTF-8\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<title>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(WebCommonStaticFinalVars.USER_WEBSITE_TITLE);
		
		stringBuilder.append("</title>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!-- Latest compiled and minified CSS -->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<link rel=\"stylesheet\" href=\"/bootstrap/3.3.7/css/bootstrap.css\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!-- jQuery library -->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<script src=\"/jquery/3.3.1/jquery.min.js\"></script>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<!-- Latest compiled JavaScript -->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<script src=\"/bootstrap/3.3.7/js/bootstrap.min.js\"></script>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<body>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<div class=header>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<div class=\"container\">");
		stringBuilder.append(System.getProperty("line.separator"));
		
		UserSiteMenuManger userSiteMenuManger = UserSiteMenuManger.getInstance();

		String menuNavbarString = userSiteMenuManger.getMenuNavbarString(relativeURL, accessedUserInformation.isLoginedIn(),
				accessedUserInformation.getUserName());
		
		stringBuilder.append(menuNavbarString);		
		stringBuilder.append("\t\t</div>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</div>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<div class=\"content\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<div class=\"container\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<div class=\"panel panel-default\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<div class=\"panel-heading\"><h1 style=\"text-align: center; \"><b>");
		
		stringBuilder.append(StringEscapeUtils.escapeHtml4(subject));
		
		stringBuilder.append("</b></h1></div>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<div class=\"panel-body\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<article style=\"white-space:pre-line;\">");
		
		stringBuilder.append(contents);
		
		stringBuilder.append("</article>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</div>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t</div>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t</div>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</div>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</body>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</html>");
		
		return stringBuilder.toString();
	}
}
