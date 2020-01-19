<%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="documentModifyRes" class="kr.pe.codda.impl.message.DocumentModifyRes.DocumentModifyRes" scope="request" /><%

	String documentModifyResJsonString = new Gson().toJson(documentModifyRes);

%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {
		var documentModifyResJson = <%= documentModifyResJsonString %>;
    	parent.callBackForDocumentModifyProcess(documentModifyResJson);
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>