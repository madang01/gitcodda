<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%>
<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%>
<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
	
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	
%><jsp:useBean id="sourceString" class="java.lang.String" scope="request" /><%
	
%><jsp:useBean id="targetString" class="java.lang.String" scope="request" /><%
	
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

<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function init() {	
	}

	window.onload = init;	
//-->
</script>
</head>
<body>
	<div class=header>
		<div class="container">
		<%=getMenuNavbarString(request)%>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4>자바 문자열 변환 도구 - 결과</h4></div>
				<div class="panel-body">
					<div class="row">
						<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
					</div>
					<div class="row">
						<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
					일반 사용자용 사이트에서 JDF 기본 서블릿은 AbstractServlet 를 상속 받습니다.<br>
					일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
					이 페이지는 일반 사용자 사이트의 JDF 기본 페이지로써 JavaStringConverterSvl.java + JavaStringConverterResult.jsp 로 구성되어있습니다.</div>
					</div>
					
					<div class="well well-sm">&nbsp;</div>
					
					<div class="row">
						<div class="col-sm-1" style="background-color:lavenderblush;">원본</div>
						<div class="col-sm-11" style="background-color:lavenderblush;"><%=StringEscapeActorUtil.replace(sourceString, 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
								STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
					</div>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavenderblush;">결과</div>
						<div class="col-sm-11" style="background-color:lavenderblush;"><%=StringEscapeActorUtil.replace(targetString, 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
								STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>