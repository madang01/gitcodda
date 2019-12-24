<%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.impl.message.UploadImageRes.UploadImageRes"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
	UploadImageRes uploadImageRes = (UploadImageRes) request.getAttribute("uploadImageRes");
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {<%
    	if (null != uploadImageRes) {
    		
    		String uploadImageResJsonString = new Gson().toJson(uploadImageRes);
%>
			var uploadImageResObj= <%= uploadImageResJsonString %>;
			parent.callBackForUploadImageProcess(uploadImageResObj);<%
    	} else {
%>
		alert("the var uploadImageRes is null");<%
    	}
%>    		
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>