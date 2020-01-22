<%@page import="kr.pe.codda.weblib.common.DocumentStateType"%>
<%@page import="kr.pe.codda.weblib.summernote.SummerNoteConfiguration"%><%
%><%@page import="kr.pe.codda.weblib.summernote.SummerNoteConfigurationManger"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true"	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="documentViewRes" class="kr.pe.codda.impl.message.DocumentViewRes.DocumentViewRes" scope="request" /><%

	SummerNoteConfiguration summerNoteConfiguration = SummerNoteConfigurationManger.getInstance();
	
	DocumentStateType documentStateType = DocumentStateType.valueOf(documentViewRes.getDocumentSate());


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
		
		
		turnOffDocumentViewScreen();
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
	
	function callBackForDocumentModifyProcess(documentModifyResJson) {
		alert("문서["+documentModifyResJson.documentNo+"] 수정이 완료되었습니다");
		
		document.location.reload();
	}
	
	function deleteDocument() {
		var r = confirm("정말로 문서를 삭제하시겠습니까?");
		
		if (r != true) {
			console.log("삭제 취소");
			return;
		}
		
		var iv = buildIV();
		
		var g = document.documentDeleteFrm;
	
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();
	}
		
	function callBackForDocumentDeleteProcess() {
		alert("문서["+document.documentDeleteFrm.documentNo.value+"] 삭제가 완료되었습니다");
		
		goDocumentManagerPage();
	}
	
	
	function applyDocumenetToWebSite() {
		var r = confirm("문서를 일반 웹 사이트에 적용하시겠습니까?");
		
		if (r != true) {
			console.log("적용 취소");
			return;
		}
		
		var iv = buildIV();
		
		var g = document.documentWebSiteApplyFrm;
	
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();
	}
	
	function callBackForDocumentWebSiteApplyProcess(documentViewResJson) {
		alert("문서["+documentViewResJson.documentNo+"] 일반 웹 사이트 적용이 완료되었습니다");
	}
	
	
	function turnOffDocumentViewScreen() {
		var documentViewScreen = document.getElementById("documentViewScreen");
		documentViewScreen.style.display = "none";
		var documentViewScreenDisplayToggleButton = document.getElementById("documentViewScreenDisplayToggleButton");
		documentViewScreenDisplayToggleButton.innerText = "문서 보기 보이기";
	}
	
	function turnOnDocumentViewScreen() {
		var documentViewScreen = document.getElementById("documentViewScreen");
		documentViewScreen.style.display = "block";
		var documentViewScreenDisplayToggleButton = document.getElementById("documentViewScreenDisplayToggleButton");
		documentViewScreenDisplayToggleButton.innerText = "문서 보기 닫기";
	}
	
	function closeDocumentModifyScreen() {
		var documentModifyScreen = document.getElementById("documentModifyScreen");
		documentModifyScreen.style.display = "none";
		
		turnOnDocumentViewScreen();
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
	
	function goDocumentManagerPage() {
		var iv = buildIV();
		
		var g = document.documentMangerFrm;
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();
	}
	
	function goDoucmentChangeHistoryPage() {
		var iv = buildIV();
		
		var g = document.documentChangeHistoryFrm;
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
	<form name=documentMangerFrm method="post" action="/servlet/DocumentManager">
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>
	
	<form name=documentDeleteFrm method="post" target="hiddenFrame" action="/servlet/DocumentDeleteProcess">
		<input type="hidden" name="documentNo" value="<%= documentViewRes.getDocumentNo() %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>	
	
	<form name=documentWebSiteApplyFrm method="post" target="hiddenFrame" action="/servlet/DocumentWebSiteApplyProcess">
		<input type="hidden" name="documentNo" value="<%= documentViewRes.getDocumentNo() %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>	
	
	<form name=documentChangeHistoryFrm method="post" action="/servlet/DocumentChangeHistory">
		<input type="hidden" name="documentNo" value="<%= documentViewRes.getDocumentNo() %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	</form>
	
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h4>문서 보기 화면</h4>
				</div>
				<div class="panel-body">
					<div class="btn-group">
						<button type="button" class="btn btn-primary btn-sm"  onClick="goDocumentManagerPage()">문서 관리 화면</button><%
	if (DocumentStateType.OK.equals(documentStateType)) {
%>					
						<button type="button" class="btn btn-primary btn-sm"  onClick="showDocumentModifyInputScreen()">수정</button>
						<button type="button" class="btn btn-primary btn-sm"  onClick="deleteDocument()">삭제</button>
						<button type="button" class="btn btn-primary btn-sm"  onClick="applyDocumenetToWebSite()">웹사이트 적용</button><%
	}
%>					
						<button type="button" class="btn btn-primary btn-sm"  onClick="goDoucmentChangeHistoryPage()">변경 이력 조회</button>
						<button id="documentViewScreenDisplayToggleButton" type="button" class="btn btn-primary btn-sm" onClick="clieckDocumentViewScreenDisplayToggleButton(this);">문서 보기 닫기</button>
						<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>
					</div>
					
					<div id="resultMessage"></div>
					<br>
					<div id="documentViewScreen">
						<div class="row">
							<div class="col-xs-1"><b>문서번호</b></div>
							<div class="col-xs-1"><%= documentViewRes.getDocumentNo() %></div>
							<div class="col-xs-1"><b>상태</b></div>
							<div class="col-xs-1"><%= documentStateType.getName() %></div>
							<div class="col-xs-2"><b>마지막 변경일</b></div>
							<div class="col-xs-2"><%= documentViewRes.getLastModifiedDate() %></div>
						</div>
						<div class="row">
							<div class="col-xs-2"><b>HTML 파일이름</b></div>
							<div class="col-xs-6" id="fileNameOfDocumnetViewScreen"><%= documentViewRes.getFileName() %></div>
						</div>
						<div class="row" style="background-color:#fefbd8;">
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
								<label for="subject">HTML 파일 이름</label>
								<input type="text" name="fileName" class="form-control" placeholder="Enter file name" />
								<label for="subject">제목</label>
								<input type="text" name="subject" class="form-control" placeholder="Enter subject" />
								<label for="content">내용</label>							
								<textarea name="contents" id="contentsOfDocumentModifyScreen" class="form-control input-block-level" placeholder="Enter contents" rows="20"></textarea>
							</div>
						</form>
						<div class="btn-group">
							<input type="button" class="btn btn-default" onClick="modifyDocument();" value="저장" />
							<input type="button" class="btn btn-default" onClick="closeDocumentModifyScreen();" value="닫기" />
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