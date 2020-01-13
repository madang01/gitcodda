<%@page import="kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter"%>
<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="java.util.Arrays"%><%
%><%@page import="java.util.HashSet"%><%
%><%@page import="kr.pe.codda.common.util.CommonStaticUtil"%><%
%><%@page import="kr.pe.codda.common.buildsystem.pathsupporter.WebClientBuildSystemPathSupporter"%><%
%><%@page import="kr.pe.codda.common.config.CoddaConfigurationManager"%><%
%><%@page import="kr.pe.codda.common.config.CoddaConfiguration"%><%
%><%@page import="java.io.File"%><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8" %><%!
	String[] whiteServerFullClassNameList = { "kr.pe.codda.impl.task.server.MemberLoginReqServerTask" };

	HashSet<String> whiteServerFullClassNameSet = new HashSet<String>(Arrays.asList(whiteServerFullClassNameList));

	String[] whiteWebClientFullClassNameList = { "kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq",
			"kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqClientCodec", 
			"kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqServerCodec",  
			"kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqEncoder", 
			"kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqDecoder",
			"kr.pe.codda.servlet.user.MemberLoginProcessSvl"};
	
	HashSet<String> whiteWebclientFullClassNameSet = new HashSet<String>(Arrays.asList(whiteWebClientFullClassNameList));

%><%
	String fullClassName = request.getParameter("fullClassName");
	
	if (null == fullClassName) {
		out.print("파리미터 'fullClassName' 를 입력해 주세요");
		return;
	}
	
	CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
			.getRunningProjectConfiguration();
	String mainProjectName = runningProjectConfiguration.getMainProjectName();
	String installedPathString = runningProjectConfiguration.getInstalledPathString();
	
	String sourceFilePathString = null;
	
	if (whiteWebclientFullClassNameSet.contains(fullClassName)) {
		sourceFilePathString = new StringBuilder()
				.append(WebClientBuildSystemPathSupporter.getWebClientBuildPathString(installedPathString, mainProjectName))				
				.append(File.separator)
				.append("src")
				.append(File.separator)
				.append("main")
				.append(File.separator)
				.append("java")
				.append(File.separator)
				.append(fullClassName.replace('.', File.separatorChar))
				.append(".java").toString();
	} else if (whiteServerFullClassNameSet.contains(fullClassName)) {
		sourceFilePathString = new StringBuilder()
				.append(ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName))				
				.append(File.separator)
				.append("src")
				.append(File.separator)
				.append("main")
				.append(File.separator)
				.append("java")
				.append(File.separator)
				.append(fullClassName.replace('.', File.separatorChar))
				.append(".java").toString();
	} else {
		out.print("파리미터 'fullClassName' 에 허용되지 않는 값이 들어왔습니다");
		return;
	}	

	File sourceFile = new File(sourceFilePathString);
	byte[] sourceFileContents = CommonStaticUtil.readFileToByteArray(sourceFile, 1024*1024*10L);
	
	out.print(new String(sourceFileContents, CommonStaticFinalVars.SOURCE_FILE_CHARSET));
%>