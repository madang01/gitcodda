<%@page import="kr.pe.codda.weblib.common.DocumentStateType"%>
<%@page import="kr.pe.codda.weblib.summernote.SummerNoteConfiguration"%><%
%><%@page import="kr.pe.codda.weblib.summernote.SummerNoteConfigurationManger"%><%
%><%@page import="kr.pe.codda.weblib.common.AccessedUserInformation"%><%
%><%@page import="kr.pe.codda.weblib.common.MemberRoleType"%><%
%><%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardReplyPolicyType"%><%
%><%@page import="kr.pe.codda.weblib.common.PermissionType"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardListType"%><%
%><%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardStateType"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true"	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="documentViewRes" class="kr.pe.codda.impl.message.DocumentViewRes.DocumentViewRes" scope="request" /><%
	

	SummerNoteConfiguration summerNoteConfiguration = SummerNoteConfigurationManger.getInstance();
	
	
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
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>

<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript">
	function buildPrivateKey() {
		var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);	
		return privateKey;
	}
	
	function putNewPrivateKeyToSessionStorage() {
		var newPrivateKey = buildPrivateKey();
		var newPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(newPrivateKey);
		
		sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>', newPrivateKeyBase64);
		
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
		rsa.setPublic("<%=getModulusHexString(request)%>", "10001");
			
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);		
		return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
	}
	
	function buildIV() {
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		return iv;
	}
	
	function modifyDocument() {			
		var f = document.documentModifyInputFrm;
		
		if ('' == f.fileName.value) {
			alert("파일 이름을 넣어 주세요.");
			f.fileName.focus();
			return;
		}
		
		if (f.subject.value == '') {
			alert("제목을 넣어 주세요");
			f.subject.focus();
			return;
		}
	
		if ('' == f.contents.value) {
			alert("내용을 넣어 주세요.");
			f.contents.focus();
			return;
		}
		
		
		var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
		var privateKey = getPrivateKeyFromSessionStorage();		
		var iv = buildIV();
		
		var g = document.documentModifyProcessFrm;
		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
	
		
		g.fileName.value = f.fileName.value;
		g.subject.value = f.subject.value;
		g.contents.value = $('#contentsOfDocumentModifyScreen').summernote("code");
	
		g.submit();		
		
		return;	
	}
	
	function callBackForDoucmentModify(documentModifyResJson) {
		alert("문서["+documentModifyResJson.documentNo+"] 수정이 완료되었습니다");
		
		document.location.reload();
	}
	
	function goDoucmentDelete(documentNo) {		
		var f = document.documentDeleteInputFrm;
		
		if (f != undefined) {
			try {
				checkValidPwd('게시글', f.pwd.value);
			} catch(err) {
				alert(err);
				f.pwd.focus();
				return;
			}
		}
		
		var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
		var privateKey = getPrivateKeyFromSessionStorage();		
		var iv = buildIV();
		
		var g = document.documentDeleteProcessFrm;
	
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.boardNo.value = boardNo;		
		if (f != undefined) {
			g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, getPrivateKeyFromSessionStorage(), { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		}
		
		g.submit();
	}
	
	
	
	function goDocumentChangeHistory() {
		var g = document.boardChangeHistoryFrm;
		g.boardNo.value = boardNo;		
		g.submit();
	}
	
	
	
	
	function showDocumentModifyInputScreen(documentNo) {
		var documentModifyScreen = document.getElementById("documentModifyScreen");
		documentModifyScreen.style.display = "block";	
		
		$('#contentsOfDocumentModifyScreen').summernote({
	<%= summerNoteConfiguration.buildInitializationOptionsString(3) %>,
			placeholder: '이곳에서 문서를 작성해 주세요',
	        tabsize: 2,
	        height: 400
		});
				
		var fileNameOfDocumnetViewScreen = document.getElementById("fileNameOfDocumnetViewScreen");
		var subjectOfDocumnetViewScreen = document.getElementById("subjectOfDocumnetViewScreen");
		var contentsOfDocumnetViewScreen = document.getElementById("contentsOfDocumnetViewScreen");
		
		var f = document.documentModifyInputFrm;
		f.fileName.value = fileNameOfDocumnetViewScreen.innerText;
		f.subject.value = subjectOfDocumnetViewScreen.innerText;
		
		$('#contentsOfDocumentModifyScreen').summernote("code", contentsOfDocumnetViewScreen.innerHTML);
		
		
		var documentViewScreen = document.getElementById("documentViewScreen");
		documentViewScreen.style.display = "none";
		var documentViewScreenDisplayToggleButton = document.getElementById("documentViewScreenDisplayToggleButton");
		documentViewScreenDisplayToggleButton.innerText = "문서 보기 보이기";
		
	}
	
	function closeDocumentModifyInputScreen() {
		var documentModifyScreen = document.getElementById("documentModifyScreen");
		documentModifyScreen.style.display = "none";
		
		var documentViewScreen = document.getElementById("documentViewScreen");
		documentViewScreen.style.display = "block";
		var documentViewScreenDisplayToggleButton = document.getElementById("documentViewScreenDisplayToggleButton");
		documentViewScreenDisplayToggleButton.innerText = "문서 보기 닫기";
	}
		
	function clickHiddenFrameButton(thisObj) {		
		var hiddenFrameObj = document.getElementById("hiddenFrame");
		
		if (hiddenFrameObj.style.display == 'none') {
			thisObj.innerText = "Hide Hidden Frame";
			hiddenFrameObj.style.display = "block";			
		} else {
			thisObj.innerText = "Show Hidden Frame";
			hiddenFrameObj.style.display = "none";
		}
	}
	
	
	function goMemberInformation(targetUserID) {		
		if (opener != undefined) {			
			opener.document.location.href = "http://www.sinoiri.pe.kr/servlet/MemberInformation?targetUserID="+targetUserID;
			self.close();
		} else {
			document.location.href = "http://www.sinoiri.pe.kr/servlet/MemberInformation?targetUserID="+targetUserID;
		}
	}
	
	function goPersonalActivityHistory(targetUserID) {
		if (opener != undefined) {			
			opener.document.location.href = "http://www.sinoiri.pe.kr/servlet/PersonalActivityHistory?targetUserID="+targetUserID;
			self.close();
		} else {
			document.location.href = "http://www.sinoiri.pe.kr/servlet/PersonalActivityHistory?targetUserID="+targetUserID;
		}
	}

	function clieckDocumentViewScreenDisplayToggleButton(buttonObj) {
		var documentViewScreen = document.getElementById("documentViewScreen");

		if (documentViewScreen.style.display == 'none') {
			buttonObj.innerText = "문서 보기 닫기";
			documentViewScreen.style.display = "block";			
		} else {
			buttonObj.innerText = "문서 보기 보이기";
			documentViewScreen.style.display = "none";
		}
	}
	
	function goDocumentMnager() {
		var iv = buildIV();
		
		var g = document.documnetMangerFrm;
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();
	}
	
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    top.location.href = "/";
		}
		
		var interestedBoadNoDiv = document.getElementById('viewScreenForBoard');
		
		if (interestedBoadNoDiv != undefined) {
			
			var offsetTop = getOffsetTop(interestedBoadNoDiv);
			var offsetLeft = getOffsetLeft(interestedBoadNoDiv);
			<!-- WARNING! setTimeout 함수에 window.scrollTo 을 넣어야 크롬에서 잘 동작함 -->
			setTimeout( function() { window.scrollTo(offsetLeft, offsetTop); }, 100);
		}	
	}

	window.onload=init;

</script>
</head>
<body>
	<div class=header>
		<div class="container">
<%= getMenuNavbarString(request) %>
		</div>
	</div>
	<form name=documnetMangerFrm method="post" action="/servlet/DocumentManager">
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>
	
	<form name=documnetDeleteFrm method="post" action="/servlet/DocumentDeleteProcess">
		<input type="hidden" name="documentNo" value="<%= documentViewRes.getDocumentNo() %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>	
	
	<form name=documentHistoryFrm method="post" action="/servlet/DocumentChangeHistory">
		<input type="hidden" name="documentNo" value="<%= documentViewRes.getDocumentNo() %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>
	
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h4>문서 수정 화면</h4>
				</div>
				<div class="panel-body">
					<div class="btn-group">
						<button type="button" class="btn btn-primary btn-sm"  onClick="goDocumentMnager()">문서 관리 화면 이동</button>
						<button type="button" class="btn btn-primary btn-sm"  onClick="showDocumentModifyInputScreen()">수정</button>
						<button type="button" class="btn btn-primary btn-sm"  onClick="goDoucmentDelete()">삭제</button>
						<button type="button" class="btn btn-primary btn-sm"  onClick="goDoucmentChangeHistory()">변경 이력 조회</button>
						<button id="documentViewScreenDisplayToggleButton" type="button" class="btn btn-primary btn-sm" onClick="clieckDocumentViewScreenDisplayToggleButton(this);">문서 보기 닫기</button>
						<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>
					</div>
					
					<div id="resultMessage"></div>
					<br>
					<div id="documentViewScreen">
						<div class="row">
							<div class="col-xs-1"><b>번호</b></div>
							<div class="col-xs-1"><%= documentViewRes.getDocumentNo() %></div>
							<div class="col-xs-1"><b>상태</b></div>
							<div class="col-xs-2"><%= DocumentStateType.valueOf(documentViewRes.getDocumentSate()).getName() %></div>
							<div class="col-xs-2"><b>파일 이름</b></div>
							<div class="col-xs-5" id="fileNameOfDocumnetViewScreen"><%= documentViewRes.getFileName() %></div>
						</div>					
						<div class="row">
							<div class="col-xs-2"><h3><b>제  목 :</b></h3></div>
							<div class="col-xs-10" id="subjectOfDocumnetViewScreen"><h3><%= toEscapeHtml4(documentViewRes.getSubject())%></h3></div>
						</div>
						<div class="row">
							<div class="col-xs-12">
								<article id="contentsOfDocumnetViewScreen"><%= documentViewRes.getContents() %></article>
							</div>
						</div>
					</div>
					<div id="documentModifyScreen" style="display: none">
						<form name="documentModifyInputFrm" method="post" action="/servlet/DocumentModifyProcess" onsubmit="return false;">
							<input type="hidden" name="newAttachedFileRowSeq" value="0" />
							<div class="form-group">
								<label for="subject">파일 이름</label>
								<input type="text" name="fileName" class="form-control" placeholder="Enter file name" />
								<label for="subject">제목</label>
								<input type="text" name="subject" class="form-control" placeholder="Enter subject" />
								<label for="content">내용</label>							
								<textarea name="contents" id="contentsOfDocumentModifyScreen" class="form-control input-block-level" placeholder="Enter contents" rows="20"></textarea>
							</div>
						</form>
						<div class="btn-group">
							<input type="button" class="btn btn-default" onClick="modifyDocument();" value="저장" />
							<input type="button" class="btn btn-default" onClick="closeDocumentModifyInputScreen();" value="닫기" />
						</div>
						<form name="documentModifyProcessFrm" method="post" target="hiddenFrame" action="/servlet/DocumentModifyProcess">
							<div class="form-group">
								<input type="hidden" name="documentNo" value="<%= documentViewRes.getDocumentNo() %>" />
								<input type="hidden" name="fileName" />
								<input type="hidden" name="subject" />
								<input type="hidden" name="contents" />
								<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
								<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
							</div>
						</form>	
					</div>
				</div>
			<iframe id="hiddenFrame" name="hiddenFrame" style="display: none;"></iframe>
			</div>
		</div>
	</div>
</body>
</html>