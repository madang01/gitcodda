<%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.impl.message.DocumentModifyRes.DocumentModifyRes"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
DocumentModifyRes documentModifyRes = (DocumentModifyRes)request.getAttribute("documentModifyRes");
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {<%
    if (null != documentModifyRes) {
    		String documentModifyResJsonString = new Gson().toJson(documentModifyRes);	
%>
		var documentModifyResJson = <%= documentModifyResJsonString %>;
    	parent.callBackForDoucmentModify(documentModifyResJson);<%
    } else {
%>
		alert("the var boardModifyRes is null");<%
	}
%>	
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>