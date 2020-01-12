<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
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
				<div class="panel-heading">코다(Codda)</div>
				<div class="panel-body">
					<article style="white-space:pre-wrap;">
  코다는 자바로 만든 아파치2 라이센스를 갖는 오픈 소스로 RPC 서버를 기반 개발 프레임워크입니다.
  
RPC 서버는 자바 NIO selector 로 작성되었으며
RPC 서버 접속 API 는 2가지 버전이 있는데 하나는 동기 메시지만 보낼 수 있는 socket pool 이고 
       다른 또 하나는 자바 NIO selctor 를 이용한 socket channel pool 입니다.  


현재 이 사이트는 코다 코어 라이브러리를 이용하여 작성된 사이트로 통합 테스트를 목적으로 공개를 한 상태입니다.
테스트상 잦은 먹통이 있을 수 있으니 양해 바랍니다.
					</article>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
