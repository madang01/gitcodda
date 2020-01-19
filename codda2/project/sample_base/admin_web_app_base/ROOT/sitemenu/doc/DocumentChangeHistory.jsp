<%@page import="kr.pe.codda.weblib.common.DocumentStateType"%>
<%@page import="kr.pe.codda.weblib.common.AccessedUserInformation"%><%
%><%@page import="kr.pe.codda.impl.message.DocumentChangeHistoryRes.DocumentChangeHistoryRes"%><%
%><%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="java.util.List"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%	
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="documentChangeHistoryRes" class="kr.pe.codda.impl.message.DocumentChangeHistoryRes.DocumentChangeHistoryRes" scope="request" /><%
	
	DocumentStateType documentStateType = DocumentStateType.valueOf(documentChangeHistoryRes.getDocumentSate());
	
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<!-- include summernote css/js-->
<link href="/summernote/summernote.css" rel="stylesheet">
<script src="/summernote/summernote.js"></script>

<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>

<script src="/js/common.js"></script>

<script type='text/javascript'>
	function buildPrivateKey() {
		var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);	
		return privateKey;
	}
	
	function putNewPrivateKeyToSessionStorage() {
		var newPrivateKey = buildPrivateKey();
		var newPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(newPrivateKey);
		
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', newPrivateKeyBase64);
		
		return newPrivateKeyBase64;
	}
	
	function getPrivateKeyFromSessionStorage() {
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (null == privateKeyBase64) {			
			privateKeyBase64 = putNewPrivateKeyToSessionStorage();
		}
		
		var privateKey = null;
		try {
			privateKey = CryptoJS.enc.Base64.parse(privateKeyBase64);
		} catch(err) {
			console.log(err);
			throw err;
		}
		
		return privateKey;
	}

	function getSessionkeyBase64FromSessionStorage() {
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (null == privateKeyBase64) {
			privateKeyBase64 = putNewPrivateKeyToSessionStorage();
		}
		
		
		var rsa = new RSAKey();	
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);
		var sessionKey = CryptoJS.enc.Hex.parse(sessionKeyHex);
		return CryptoJS.enc.Base64.stringify(sessionKey);
	}
	
	function buildIV() {
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		return iv;
	}
	
	function goDocumentManagerPage() {
		var iv = buildIV();
		
		var g = document.documentMangerFrm;
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();
	}
	
	function goDocumentViewPage() {
		var iv = buildIV();
		
		var g = document.documentViewFrm;
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();
	}
	
	function goDocumentChangeHistoryPage(pageNo) {
		var iv = buildIV();
		
		var g = document.documentChangeHistoryFrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		
		g.pageNo.value = pageNo;		
		g.submit();
	}

	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}
	}
	
	window.onload = init;
</script>
</head>
<body>
<div class="header">
	<div class="container">
<%= getMenuNavbarString(request) %>
	</div>
	<form name=documentChangeHistoryFrm method="post" action="/servlet/DocumentChangeHistory">
		<input type="hidden" name="documentNo" value="<%= documentChangeHistoryRes.getDocumentNo() %>" />
		<input type="hidden" name="pageNo" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>
	<form name=documentMangerFrm method="post" action="/servlet/DocumentManager">
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>
	<form name=documentViewFrm method="post" action="/servlet/DocumentView">
		<input type="hidden" name="documentNo" value="<%= documentChangeHistoryRes.getDocumentNo() %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>
</div>
<div class="content">
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading"><h4>개별 문서 #<%= documentChangeHistoryRes.getDocumentNo() %> 번 변경 이력 조회</h4></div>
			<div class="panel-body">
				<div class="btn-group">
					<button type="button" class="btn btn-primary btn-sm"  onClick="goDocumentViewPage()">문서 보기 화면</button>
					<button type="button" class="btn btn-primary btn-sm"  onClick="goDocumentManagerPage()">문서 관리 화면</button>
				</div>
				<br>
				<br>
				<div class="row">
					<div class="col-sm-1" style="background-color:lavender;"><h3>상태</h3></div>
					<div class="col-sm-2"><h3><%= documentStateType.getName() %></h3></div>
				</div>
				<br>
				<div id="listPartView"><%
	List<DocumentChangeHistoryRes.Document> documentList = documentChangeHistoryRes.getDocumentList();
	if (null == documentList || documentList.isEmpty()) {
%>
					<div class="row">
						<div class="col-sm-12" align="center">조회 결과가 없습니다</div>
					</div><%	
	} else {
		for (DocumentChangeHistoryRes.Document document : documentList) {
%>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">순번</div>
						<div class="col-sm-1">#<%= document.getDocumentSeq() %></div>
						
						<div class="col-sm-2" style="background-color:lavender;">HTML 파일명</div>
						<div class="col-sm-4"><%= toEscapeHtml4(document.getFileName()) %></div>
						
						<div class="col-sm-1" style="background-color:lavender;">등록일</div>
						<div class="col-sm-2"><%= document.getRegisteredDate() %></div>
					</div>
					<div class="row">
						<div class="col-sm-12" style="background-color:#fefbd8;"><h1><%= toEscapeHtml4(document.getSubject()) %></h1></div>
					</div>
					<div class="row">
						<div class="col-sm-12"><%= document.getContents() %></div>
					</div><%
		}
	}
	
	if (documentChangeHistoryRes.getTotal() > 1) {
		final int pageNo = documentChangeHistoryRes.getPageNo();
		final int pageSize = documentChangeHistoryRes.getPageSize();
		
		// long pageNo = boardListRes.getPageOffset() / boardListRes.getPageLength() + 1;
		
		long startPageNo = 1 + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE*(long)((pageNo - 1) / WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE);
		long endPageNo = Math.min(startPageNo + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE, 
		(documentChangeHistoryRes.getTotal() + pageSize - 1) / pageSize);
		
		
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<ul class=\"pagination pagination-sm\">");
		
		if (startPageNo > 1) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"previous\"><a href=\"#\" onClick=\"goDocumentChangeHistoryPage(");
			out.write(String.valueOf(startPageNo-1));
			out.write(")\">이전</a></li>");
		} else {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"disabled previous\"><a href=\"#\">이전</a></li>");
		}
		
		for (int i=0; i < WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE; i++) {
			long workingPageNo = startPageNo + i;
			if (workingPageNo > endPageNo) break;

			if (workingPageNo == pageNo) {
				out.write(CommonStaticFinalVars.NEWLINE);
				out.write("						");
				out.write("<li class=\"active\"><a href=\"#\">");
				out.write(String.valueOf(workingPageNo));
				out.write("</a></li>");
			} else {
				out.write(CommonStaticFinalVars.NEWLINE);
				out.write("						");
				out.write("<li><a href=\"#\" onClick=\"goDocumentChangeHistoryPage(");
				out.write(String.valueOf(workingPageNo));
				out.write(")\">");
				out.write(String.valueOf(workingPageNo));
				out.write("</a></li>");
			}
		}
		
		if (startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE <= endPageNo) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"next\"><a href=\"#\" onClick=\"goDocumentChangeHistoryPage(");
			out.write(String.valueOf(startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE));
			out.write(")\">다음</a></li>");
		} else {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"disabled next\"><a href=\"#\">다음</a></li>");
		}
		
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("</ul>");
	}
%>	
				</div>
			</div>			
		</div>
	</div>
</div>
</body>
</html>				