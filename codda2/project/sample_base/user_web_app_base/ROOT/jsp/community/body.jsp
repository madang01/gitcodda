<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=WebCommonStaticFinalVars.USER_WEBSITE_TITLE%></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 
<script type='text/javascript'>
	function init() {
		// document.location.href = "";
	}
	
	window.onload = init;
</script>
</head>
<body>
<%=getMenuNavbarString(request)%>
	<div class="container-fluid">
		<h3>사랑방</h3>
		이곳은 게시판이 중심이 되는 사랑방입니다. 공지게시판, 자유게시판 마지막으로 FAQ 로 구성되어 있습니다.
	</div>
</body>
</html>
