<%@page import="kr.pe.codda.impl.message.DocumentWriteRes.DocumentWriteRes"%><%
%><%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
	DocumentWriteRes documentWriteRes = (DocumentWriteRes)request.getAttribute("documentWriteRes");
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {<%
    	if (null != documentWriteRes) {
    		String documentWriteResJsonString = new Gson().toJson(documentWriteRes);
    		
    		
%>
		var documentWriteResJson = <%= documentWriteResJsonString %>;		
		parent.callBackForDocumentRegistrationProcess(documentWriteResJson);<%
    	} else {
%>
		alert("the var boardWriteRes is null");<%
    	}
%>    		
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>
