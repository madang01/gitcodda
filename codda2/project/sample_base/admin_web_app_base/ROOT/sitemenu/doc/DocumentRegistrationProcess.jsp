<%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="documentWriteRes" class="kr.pe.codda.impl.message.DocumentWriteRes.DocumentWriteRes" scope="request" /><%
	String documentWriteResJsonString = new Gson().toJson(documentWriteRes);
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {
		var documentWriteResJson = <%= documentWriteResJsonString %>;		
		parent.callBackForDocumentRegistrationProcess(documentWriteResJson);  		
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>
