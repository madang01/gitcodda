<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="userMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="debugMessage" class="java.lang.String" scope="request" /><%	
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 
<script type='text/javascript'>
	function init() {
		if (window.top != window.self) {
			var userMessageDiv = document.getElementById("userMessage");
			
			if (parent.callBackForErrorMessage != null) {				
				parent.callBackForErrorMessage(userMessageDiv.innerText);
			} else {
				alert(userMessageDiv.innerText);
			}
		}
	}

	window.onload = init;
</script>
</head>
<body>
	<div class=header>
		<div class="container">
<%= getMenuNavbarString(request) %>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading">에러 내용</div>
				<div class="panel-body">
					<div class="row">
						<div class="col-sm-1">종류</div>
						<div class="col-sm-11">내용</div>
					</div>
					<div class="row">
						<div class="col-sm-1">일반</div>
						<div class="col-sm-11" id="userMessage"><%=StringEscapeActorUtil.replace(userMessage, 
					STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
					STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
					</div><%
	if (null != debugMessage) {
%>
					<div class="row">
						<div class="col-sm-1">디버깅</div>
						<div class="col-sm-11" id="debugMessage"><%=StringEscapeActorUtil.replace(debugMessage, 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
								STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
					</div><%
	}
%>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
