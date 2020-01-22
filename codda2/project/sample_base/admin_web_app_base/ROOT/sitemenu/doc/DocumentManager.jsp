<%@page import="kr.pe.codda.weblib.common.AccessedUserInformation" %><%
%><%@page import="kr.pe.codda.weblib.common.DocumentSateSearchType" %><%
%><%@page import="kr.pe.codda.weblib.common.DocumentStateType" %><%
%><%@page import="kr.pe.codda.impl.message.DocumentListRes.DocumentListRes"%><%
%><%@page import="kr.pe.codda.weblib.summernote.SummerNoteConfiguration"%><%
%><%@page import="kr.pe.codda.weblib.summernote.SummerNoteConfigurationManger"%><%
%><%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="java.util.List"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%	
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="documentListRes" class="kr.pe.codda.impl.message.DocumentListRes.DocumentListRes" scope="request" /><%

	DocumentSateSearchType documentSateSearchType = (DocumentSateSearchType)request.getAttribute("documentSateSearchType");
	if (null == documentSateSearchType) {
		documentSateSearchType = DocumentSateSearchType.OK;
	}

	SummerNoteConfiguration summerNoteConfiguration = SummerNoteConfigurationManger.getInstance();
	AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(request);
	
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
		
	function saveNewDocument() {		
		var f = document.documentRegistrationInputFrm;		
		
		if ('' == f.fileName.value) {
			alert("파일 이름을 넣어 주세요.");
			f.fileName.focus();
			return;
		}
		
		if ('' == f.subject.value) {
			alert("제목을 넣어 주세요.");
			f.subject.focus();
			return;
		}

		if ('' == f.contents.value) {
			alert("내용을 넣어 주세요.");
			f.contents.focus();
			return;
		}
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;		
		var privateKey = getPrivateKeyFromSessionStorage();
		var iv = buildIV();

		var g = document.documentRegistrationProcessFrm;	
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();	
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.fileName.value = f.fileName.value;
		g.subject.value = f.subject.value;
		g.contents.value = $('#contentsOfDocument').summernote('code');

		g.submit()
	}
	
	function callBackForDocumentRegistrationProcess(documentWriteResJson) {
		alert("문서[문서 번호=" + documentWriteResJson.documentNo +"] 작성이 완료되었습니다");		
		goDocumentManagerPage(1);
	}	
		
	function showDocumentWriteInputScreen() {	
		var documentWriteInputScreen = document.getElementById('documentWriteInputScreen');
		documentWriteInputScreen.style.display = "block";
		
		
		$('#contentsOfDocument').summernote({
<%= summerNoteConfiguration.buildInitializationOptionsString(3) %>,
			placeholder: '이곳에 글을 작성해 주세요',
			tabsize: 2,
			height: 200
		});
		
		var f = document.documentRegistrationInputFrm;	
		f.reset();
	}
	
	
	function hideWriteEditScreen() {	
		var editScreenNodeOfBoard0 = document.getElementById('documentWriteInputScreen');
		editScreenNodeOfBoard0.style.display = "none";
	}
	
	
	function goViewPage(boardID, boardNo, isTreeTypeList) {
		var detailPageURL = "/servlet/DocumentView?boardID="+boardID+"&boardNo="+boardNo;
		
		if (isTreeTypeList) {
			window.open(detailPageURL, "", "width=800,height=600");
		}  else {
			document.location.href = detailPageURL;
		}		
	}
	
	function goDocumentManagerPage(pageNo) {
		var iv = buildIV();
		
		var g = document.documentMangerFrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		
		g.pageNo.value = pageNo;		
		g.submit();
	}
	
	function goDocumentViewPage(documentNo) {
		var iv = buildIV();
		
		var g = document.documentViewFrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.documentNo.value = documentNo;
		
		g.submit();
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
</div>

<form name=documentMangerFrm method="post" action="/servlet/DocumentManager">
<input type="hidden" name="pageNo" />
<input type="hidden" name="documentSateSearchType" value="<%= documentSateSearchType.getValue() %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>

<form name=documentViewFrm method="post" action="/servlet/DocumentView">
<input type="hidden" name="documentNo" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>

<div class="content">
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading"><h4>문서 관리</h4></div>
			<div class="panel-body">
				<div class="btn-group">
					<button type="button" class="btn btn-primary btn-sm" onClick="showDocumentWriteInputScreen()">글 작성하기</button>
					<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>					
				</div>
				<div style="height: 20px;"></div>
				<form class="form-inline" name="searchFrm" method="post" action="/servlet/DocumentManager">
					<div class="form-group">
						<label for="documentSateSearchType">상태 :&nbsp;</label>						
						<label class="radio-inline">
							<input type="radio" name="documentSateSearchType" value="A"<% 
	if (DocumentSateSearchType.ALL.equals(documentSateSearchType)) {
		out.print(" checked");
	}
	%>>전체
						</label>
						<label class="radio-inline">
							<input type="radio" name="documentSateSearchType" value="Y"<% 
		if (DocumentSateSearchType.OK.equals(documentSateSearchType)) {
			out.print(" checked");
		}
		%>>정상
						</label>
						<label class="radio-inline">
							<input type="radio" name="documentSateSearchType" value="D"<% 
		if (DocumentSateSearchType.DELETE.equals(documentSateSearchType)) {
			out.print(" checked");
		}
		%>>삭제
						</label>						
					</div>
					<div class="form-group">&nbsp;
						<button type="submit" class="btn btn-default btn-sm">조회</button>
					</div>		
				</form>
				<div style="height: 10px;"></div>
				<div id="listPartView">
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">번호</div>
						<div class="col-sm-5" style="background-color:lavender;">제목</div>
						<div class="col-sm-1" style="background-color:lavender;">상태</div>
						<div class="col-sm-2" style="background-color:lavender;">마지막 수정일</div>
					</div><%
	List<DocumentListRes.Document> documentList = documentListRes.getDocumentList();
	if (null == documentList || documentList.isEmpty()) {
%>
					<div class="row">
						<div class="col-sm-12" align="center">조회 결과가 없습니다</div>
					</div><%
	} else {
		for (DocumentListRes.Document document : documentList) {
			byte documentStateTypeValue = document.getDocumentSate();
			DocumentStateType documentStateType = DocumentStateType.valueOf(documentStateTypeValue);
			
			
%>
					<div class="row">
						<div class="col-sm-1"><%= document.getDocumentNo() %></div>
						<div class="col-sm-5"><a href="#" onClick="goDocumentViewPage(<%=document.getDocumentNo()%>);"><%= toEscapeHtml4(document.getSubject()) %></a></div>
						<div class="col-sm-1"<%
			if (DocumentStateType.DELETE.equals(documentStateType)) {
				out.print(" style=\"color:red;\"");
			}
%>><%= documentStateType.getName() %></div>
						<div class="col-sm-2"><%= document.getLastModifiedDate()%></div>
					</div><%
		}
	}

	if (documentListRes.getTotal() > 1) {
		final int pageNo = documentListRes.getPageNo();
		final int pageSize = documentListRes.getPageSize();
		
		// long pageNo = boardListRes.getPageOffset() / boardListRes.getPageLength() + 1;
		
		long startPageNo = 1 + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE*(long)((pageNo - 1) / WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE);
		long endPageNo = Math.min(startPageNo + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE, 
		(documentListRes.getTotal() + pageSize - 1) / pageSize);
		
		
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<ul class=\"pagination pagination-sm\">");
		
		if (startPageNo > 1) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"previous\"><a href=\"#\" onClick=\"goDocumentManagerPage(");
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
				out.write("<li><a href=\"#\" onClick=\"goDocumentManagerPage(");
				out.write(String.valueOf(workingPageNo));
				out.write(")\">");
				out.write(String.valueOf(workingPageNo));
				out.write("</a></li>");
			}
		}
		
		if (startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE <= endPageNo) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"next\"><a href=\"#\" onClick=\"goDocumentManagerPage(");
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
				<div id="documentWriteInputScreen" style="display:none">
					<form name="documentRegistrationInputFrm" method="post" action="/servlet/DocumentRegistrationProcess" onsubmit="return false;">
						<input type="hidden" name="newAttachedFileRowSeq" value="0" />
						<div class="form-group">
							<label for="subject">HTML 파일 이름</label>
							<input type="text" name="fileName" class="form-control" placeholder="Enter file name" />
							<label for="subject">제목</label>
							<input type="text" name="subject" class="form-control" placeholder="Enter subject" />
							<label for="content">내용</label>							
							<textarea name="contents" id="contentsOfDocument" class="form-control input-block-level" placeholder="Enter contents" rows="20"></textarea>
						</div>
					</form>
					<div class="btn-group">
						<input type="button" class="btn btn-default" onClick="saveNewDocument();" value="저장" />
						<input type="button" class="btn btn-default" onClick="hideWriteEditScreen();" value="닫기" />
					</div>
					<form name="documentRegistrationProcessFrm" method="post" target="hiddenFrame" action="/servlet/DocumentRegistrationProcess">
						<div class="form-group">
							<input type="hidden" name="fileName" />
							<input type="hidden" name="subject" />
							<input type="hidden" name="contents" />
							<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
							<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						</div>
					</form>				
				</div>
				<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>		
			</div>			
		</div>
	</div>
</div>
</body>
</html>