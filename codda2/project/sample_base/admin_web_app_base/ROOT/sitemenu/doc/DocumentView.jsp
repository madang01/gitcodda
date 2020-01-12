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
%><jsp:useBean id="boardDetailRes" class="kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes" scope="request" /><%
	

	// FIXME!
	/* boardDetailRes.setBoardID(BoardType.FREE.getBoardID());
	boardDetailRes.setBoardNo(1);
	boardDetailRes.setViewCount(11);
	boardDetailRes.setBoardSate(BoardStateType.OK.getValue());
	boardDetailRes.setNickname("테스트01");
	boardDetailRes.setVotes(7);
	boardDetailRes.setSubject("게시글1");
	boardDetailRes.setContent("게시글1\n한글 그림 하나를 그리다\n호호하하");
	boardDetailRes.setWriterID("test01");
	boardDetailRes.setWriterIP("173.0.0.15");
	boardDetailRes.setRegisteredDate(Timestamp.valueOf("2018-09-01 13:00:01"));
	boardDetailRes.setLastModifierIP("173.0.0.17");
	boardDetailRes.setLastModifierID("admin");
	boardDetailRes.setLastModifierNickName("관리자");
	boardDetailRes.setLastModifiedDate(Timestamp.valueOf("2018-09-15 17:20:11"));
	
	{
		List<BoardDetailRes.AttachedFile> attachedFileList = new ArrayList<BoardDetailRes.AttachedFile>();
		
		{
	BoardDetailRes.AttachedFile attachedFile = new BoardDetailRes.AttachedFile();
	
	attachedFile.setAttachedFileName("temp01.pic");
	attachedFile.setAttachedFileSeq((short)0);
	
	attachedFileList.add(attachedFile);
		}
		
		{
	BoardDetailRes.AttachedFile attachedFile = new BoardDetailRes.AttachedFile();
	
	attachedFile.setAttachedFileName("temp0233f343434343434343.pic");
	attachedFile.setAttachedFileSeq((short)1);
	
	attachedFileList.add(attachedFile);
		}
		
		boardDetailRes.setAttachedFileCnt(attachedFileList.size());
		boardDetailRes.setAttachedFileList(attachedFileList);
	}	 */

	SummerNoteConfiguration summerNoteConfiguration = SummerNoteConfigurationManger.getInstance();
	
	MemberRoleType firstWriterRoleType = MemberRoleType.valueOf(boardDetailRes.getFirstWriterRole());

	BoardListType boardListType = BoardListType.valueOf(boardDetailRes.getBoardListType());
	BoardReplyPolicyType boardReplyPolicyType = BoardReplyPolicyType
			.valueOf(boardDetailRes.getBoardReplyPolicyType());
	PermissionType boardReplyPermissionType = PermissionType
			.valueOf(boardDetailRes.getBoardReplyPermssionType());

	AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(request);

	String paramInterestedBoadNo = request.getParameter("interestedBoadNo");

	long interestedBoadNo = 0L;

	try {
		interestedBoadNo = Long.parseLong(paramInterestedBoadNo);
	} catch (Exception e) {
	}
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
<script type="text/javascript"
	src="/js/cryptoJS/components/cipher-core-min.js"></script>

<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript">
var currentEditScreenDiv = null;

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

function goModify() {			
	var f = document.modifyInputFrm;
	
	if (f.subject != undefined) {
		if (f.subject.value == '') {
			alert("제목을 넣어 주세요");
			f.subject.focus();
			return;
		}
	}

	if ('' == f.contents.value) {
		alert("내용을 넣어 주세요.");
		f.contents.focus();
		return;
	}
	
	
	if (f.pwd != undefined) {
		try {
			checkValidPwd('게시글', f.pwd.value);
		} catch(err) {
			alert(err);
			f.pwd.focus();
			return;
		}
	}

	var newFileListDivNode = document.getElementById('newAttachedFileList');		
	var oldFileListDivNode = document.getElementById('oldAttachedFileList');
	
	var uploadFileCnt = oldFileListDivNode.childNodes.length + newFileListDivNode.childNodes.length;
		
	if (uploadFileCnt > _ATTACHED_FILE_MAX_COUNT) {
		alert("업로드 할 수 있는 파일 갯수는 최대["+_ATTACHED_FILE_MAX_COUNT+"] 까지 입니다.");
		return;
	}
	
	var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
	var privateKey = getPrivateKeyFromSessionStorage();		
	var iv = buildIV();
	
	var g = document.modifyProcessFrm;
	
	for (var i=0; i < newFileListDivNode.childNodes.length; i++) {				
		var fileInput = newFileListDivNode.childNodes[i].childNodes[0].childNodes[0];
		
		if (1 == newFileListDivNode.childNodes.length) {
			if (g.newAttachedFile.value == '') {
				alert("첨부 파일을 선택하세요");
				return;
			}
		} else {
			if (g.newAttachedFile[i].value == '') {
				alert(fileInput.getAttribute("title")+"을 선택하세요");
				return;
			}
		}			
	}
	
	g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();
	g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);

	if (f.subject != undefined) {
		g.subject.value = f.subject.value;
	}
	
	// g.contents.value = f.contents.value;
	g.contents.value = $('#contentsOfBoard').summernote("code");

	if (f.pwd != undefined) {
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
	}

	g.submit();		
	
	return;	
}

function callBackForBoardModifyProcess(boardModifyRes) {
	alert("게시글["+boardModifyRes.boardNo+"] 수정이 완료되었습니다");
	
	var g = document.goDetailFrm;
	g.interestedBoadNo.value = boardModifyRes.boardNo;
	g.submit();
}

function goReply() {
	var f = document.replyInputFrm;
	
	if (f.subject != undefined) {
		if (f.subject.value == '') {
			alert("제목을 넣어 주세요");
			f.subject.focus();
			return;
		}
	}

	if ('' == f.contents.value) {
		alert("내용을 넣어 주세요.");
		f.contents.focus();
		return;
	}
	
	if (f.pwd != undefined) {
		if (f.pwd != undefined) {
			try {
				checkValidPwd('게시글', f.pwd.value);
			} catch(err) {
				alert(err);
				f.pwd.focus();
				return;
			}
			
			try {
				checkValidPwdConfirm('게시글', f.pwd.value, f.pwdConfirm.value);
			} catch(err) {
				alert(err);
				f.pwd.focus();
				return;
			}
		}
	}		
			
	var newFileListDivNode = document.getElementById('newAttachedFileList');		
	var uploadFileCnt = newFileListDivNode.childNodes.length;	
	
	if (uploadFileCnt > _ATTACHED_FILE_MAX_COUNT) {
		alert("업로드 할 수 있는 파일 갯수는 최대["+_ATTACHED_FILE_MAX_COUNT+"] 까지 입니다.");
		return;
	}
	
	var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
	var privateKey = getPrivateKeyFromSessionStorage();		
	var iv = buildIV();
	
	var g = document.replyProcessFrm;
	
	for (var i=0; i < newFileListDivNode.childNodes.length; i++) {				
		var fileInput = newFileListDivNode.childNodes[i].childNodes[0].childNodes[0];
		
		if (1 == newFileListDivNode.childNodes.length) {
			if (g.newAttachedFile.value == '') {
				alert("첨부 파일을 선택하세요");
				return;
			}
		} else {
			if (g.newAttachedFile[i].value == '') {
				alert(fileInput.getAttribute("title")+"을 선택하세요");
				return;
			}
		}			
	}		
	
	g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();
	g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
	
	
	if (f.subject != undefined) {
		g.subject.value = f.subject.value;
	}
	
	// g.contents.value = f.contents.value;
	g.contents.value = $('#contentsOfBoard').summernote("code");
	
	if (f.pwd != undefined) {
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, getPrivateKeyFromSessionStorage(), { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
	}

	g.submit();
}

function callBackForBoardReplyProcess(boardWriteResObj) {		
	alert("댓글["+boardWriteResObj.boardNo+"] 등록이 완료되었습니다");
	if (opener != undefined) {
		opener.document.location.reload();
		self.close();
	} else {
		document.location.reload();
	}
}

function goDelete(boardNo) {		
	var f = document.deleteInputFrm;
	
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
	
	var g = document.deleteProcessFrm;

	g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value = getSessionkeyBase64FromSessionStorage();		
	g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>.value = CryptoJS.enc.Base64.stringify(iv);
	
	g.boardNo.value = boardNo;		
	if (f != undefined) {
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, getPrivateKeyFromSessionStorage(), { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
	}
	
	g.submit();
}


function showDeleteEditScreen(boardNo) {	
	var targetDiv = document.getElementById("editorScreenForBoard"+boardNo);
	
	if (null != currentEditScreenDiv) {
		closeEditScreen();
	}
	
	currentEditScreenDiv = targetDiv;
	targetDiv.style.display = "block";
	
	var titleTextNode = document.createTextNode("삭제용 비밀번호 입력 화면");
	var titleH4Node = document.createElement("h4");		
	titleH4Node.appendChild(titleTextNode);
	
	targetDiv.appendChild(titleH4Node);

	var inputFormNode = document.createElement("form");
	inputFormNode.setAttribute("name", "deleteInputFrm");
	inputFormNode.setAttribute("method", "get");
	inputFormNode.setAttribute("onSubmit", "return false;");
	
	var inputFormDiv = document.createElement("div");
	inputFormDiv.setAttribute("class", "form-group");
			
	
	var passwordLabelTextNode = document.createTextNode("비밀번호");
	
	var passwordLabelNode = document.createElement("label");
	passwordLabelNode.setAttribute("for", "passwordInEditor");
	passwordLabelNode.appendChild(passwordLabelTextNode);
	
	var passwordInputNode = document.createElement("input");
	passwordInputNode.setAttribute("type", "password");
	passwordInputNode.setAttribute("id", "passwordInEditor");
	passwordInputNode.setAttribute("name", "pwd");
	passwordInputNode.setAttribute("class", "form-control");
	passwordInputNode.setAttribute("placeholder", "Enter password");	
	
	inputFormDiv.appendChild(passwordLabelNode);
	inputFormDiv.appendChild(passwordInputNode);
	
	inputFormNode.appendChild(inputFormDiv);
	
	targetDiv.appendChild(inputFormNode);
	
	var functionDiv = document.createElement("div");
	functionDiv.setAttribute("class", "btn-group");
	
	var deleteProcessButtonNode = document.createElement("input");
	deleteProcessButtonNode.setAttribute("type", "button");
	deleteProcessButtonNode.setAttribute("class", "btn btn-default");		
	deleteProcessButtonNode.setAttribute("onClick", "goDelete("+boardNo+");");
	deleteProcessButtonNode.setAttribute("value", "삭제 처리");
	
	var hideButtonNode = document.createElement("input");
	hideButtonNode.setAttribute("type", "button");
	hideButtonNode.setAttribute("class", "btn btn-default");		
	hideButtonNode.setAttribute("onClick", "closeEditScreen();");
	hideButtonNode.setAttribute("value", "닫기");			
	
	functionDiv.appendChild(deleteProcessButtonNode);
	functionDiv.appendChild(hideButtonNode);
			
	targetDiv.appendChild(functionDiv);
}

function callBackForBoardDeleteProcess(boardNo) {
	var resultMessageDiv = document.getElementById("resultMessage");
	
	resultMessageDiv.setAttribute("class", "alert alert-success");
	resultMessageDiv.innerHTML = "<strong>Success!</strong> 게시글[" + boardNo+ "] 삭제가 완료되었습니다";
	
	alert(resultMessageDiv.innerText);
	
	if (opener != undefined) {
		opener.document.location.reload();
		self.close();
	} else {
		document.location.reload();
	}
}


function goBoardChangeHistory(boardNo) {
	var g = document.boardChangeHistoryFrm;
	g.boardNo.value = boardNo;		
	g.submit();
}

function goList() {	
	var g = document.goListFrm;		
	g.submit();
}

function downloadFile(boardNo, attachedFileSeq) {
	var g = document.goDownloadFrm;
	g.boardNo.value = boardNo;
	g.attachedFileSeq.value = attachedFileSeq;
	g.submit();
}

function addNewAttachedFile(f) {		
	var prefixOfNewChildDiv = 'newAttachedFileRowDiv';		

	var newFileListDivNode = document.getElementById('newAttachedFileList');		
	var oldFileListDivNode = document.getElementById('oldAttachedFileList');
	
	var uploadFileCnt;
	
	if (oldFileListDivNode == undefined) {
		uploadFileCnt = newFileListDivNode.childNodes.length;
	} else {
		uploadFileCnt = oldFileListDivNode.childNodes.length + newFileListDivNode.childNodes.length;
	}
		
	if (uploadFileCnt >= _ATTACHED_FILE_MAX_COUNT) {
		alert("업로드 할 수 있는 파일 갯수는 최대["+_ATTACHED_FILE_MAX_COUNT+"] 까지 입니다.");
		return;
	}
	
	var newAttachedFileRowSeq = parseInt(f.newAttachedFileRowSeq.value, 10);

	var attachedFileRowDivNode = makeNewAttachedFileRowDiv(prefixOfNewChildDiv+newAttachedFileRowSeq);
	
	newFileListDivNode.appendChild(attachedFileRowDivNode);
	
	newAttachedFileRowSeq++;
	f.newAttachedFileRowSeq.value = newAttachedFileRowSeq;
}

function makeNewAttachedFileRowDiv(attachedFileRowDivID) {
	var attachedFileRowDivNode = document.createElement("div");
	attachedFileRowDivNode.setAttribute("class", "row");
	attachedFileRowDivNode.setAttribute("id", attachedFileRowDivID);		
	
	var attachedFileNode  = document.createElement("INPUT");
	attachedFileNode .setAttribute("type", "file");
	attachedFileNode .setAttribute("class", "form-control");
	attachedFileNode .setAttribute("title", "첨부파일('"+attachedFileRowDivID+"')");
	attachedFileNode .setAttribute("name", "newAttachedFile");
	
	var attachedFileColDivNode = document.createElement("div");
	attachedFileColDivNode.setAttribute("class", "col-xs-10");
	
	attachedFileColDivNode.appendChild(attachedFileNode);
	
	var deleteButtonNode = document.createElement("INPUT");
	deleteButtonNode.setAttribute("type", "button");
	deleteButtonNode.setAttribute("value", "삭제");
	deleteButtonNode.setAttribute("title", "첨부파일('"+attachedFileRowDivID+"') 삭제");
	deleteButtonNode.setAttribute("onclick", "removeNewAttachFile('"+attachedFileRowDivID+"')");
	
	var buttonColDivNode = document.createElement("div");
	buttonColDivNode.setAttribute("class", "col-*-*");
	
	buttonColDivNode.appendChild(deleteButtonNode);
	
	attachedFileRowDivNode.appendChild(attachedFileColDivNode);
	attachedFileRowDivNode.appendChild(buttonColDivNode);
	
	return attachedFileRowDivNode;
}
	
function removeNewAttachFile(selectedDivID) {
	var newFileListDivNode = document.getElementById('newAttachedFileList');		
	var selectedDivNode = document.getElementById(selectedDivID);
	newFileListDivNode.removeChild(selectedDivNode);
}

function restoreOldAttachedFileList(boardNo) {
	var oldAttachedFileListJosnObj = JSON.parse(document.getElementById('oldAttachedFileListJosnStringOfBoard'+boardNo+'InViewScreen').innerText);
	var oldAttachedFileListDiv = document.getElementById('oldAttachedFileList');		
	
	// remove all child nodes of oldFileListInModifyPartForBoard node
	while(oldAttachedFileListDiv.hasChildNodes()) {
		oldAttachedFileListDiv.removeChild(oldAttachedFileListDiv.firstChild);
	}
	
	for (var i=0; i < oldAttachedFileListJosnObj.length; i++) {
		var oldAttachedFileRowDivID = "oldAttachedFileRow"+oldAttachedFileListJosnObj[i].attachedFileSeq;
		
		var oldAttachedFileRowDiv = document.createElement("div");
		oldAttachedFileRowDiv.setAttribute("class", "row");
		oldAttachedFileRowDiv.setAttribute("id", oldAttachedFileRowDivID);			
		
		var oldAttachedFileColDiv = document.createElement("div");
		oldAttachedFileColDiv.setAttribute("class", "col-xs-12");
		

		var oldAttachedFileHiddenInputNode = document.createElement("INPUT");
		oldAttachedFileHiddenInputNode.setAttribute("type", "hidden");	
		oldAttachedFileHiddenInputNode.setAttribute("name", "oldAttachedFileSeq");
		oldAttachedFileHiddenInputNode.setAttribute("value", oldAttachedFileListJosnObj[i].attachedFileSeq);			
		
		var fileNameTextNode = document.createTextNode(oldAttachedFileListJosnObj[i].attachedFileName+" ");			
				
		
		var deleteButtonNode = document.createElement("INPUT");
		deleteButtonNode.setAttribute("type", "button");
		deleteButtonNode.setAttribute("id", "oldAttachedFileButton"+oldAttachedFileListJosnObj[i].attachedFileSeq);
		deleteButtonNode.setAttribute("value", "삭제");
		deleteButtonNode.setAttribute("title", "delete file(attachedFileSeq:"+oldAttachedFileListJosnObj[i].attachedFileSeq+", fileName:"+ oldAttachedFileListJosnObj[i].attachedFileName + ")");
		deleteButtonNode.setAttribute("onclick", "deleteOldAttachedFile('"+oldAttachedFileRowDivID+"')");			
		
		oldAttachedFileColDiv.appendChild(oldAttachedFileHiddenInputNode);
		oldAttachedFileColDiv.appendChild(fileNameTextNode);
		oldAttachedFileColDiv.appendChild(deleteButtonNode);
		
		oldAttachedFileRowDiv.appendChild(oldAttachedFileColDiv);
		
		oldAttachedFileListDiv.appendChild(oldAttachedFileRowDiv)
	}		
}

function deleteOldAttachedFile(oldAttachedFileRowDivID) {
	var oldAttachedFileListDiv = document.getElementById('oldAttachedFileList');	
	var deleteTagetDiv = document.getElementById(oldAttachedFileRowDivID);
	oldAttachedFileListDiv.removeChild(deleteTagetDiv);
}


function showModifyEditScreen(boardID, boardNo, nextAttachedFileSeq, isSubject, isPassword) {
	var targetDiv = document.getElementById("editorScreenForBoard"+boardNo);
	var oldAttachedFileListJosnObj = JSON.parse(document.getElementById('oldAttachedFileListJosnStringOfBoard'+boardNo+'InViewScreen').innerText);
	
	/** remove all child nodes of targetDiv node */
	while(targetDiv.hasChildNodes()) {
		targetDiv.removeChild(targetDiv.firstChild);
	}
	
	if (null != currentEditScreenDiv) {
		closeEditScreen();
	}
	
	currentEditScreenDiv = targetDiv;
	targetDiv.style.display = "block";		
	
	var titleTextNode = document.createTextNode("게시글 수정 화면");
	var titleH4Node = document.createElement("h4");
	
	titleH4Node.appendChild(titleTextNode);
	
	targetDiv.appendChild(titleH4Node);
	

	var inputFormNode = document.createElement("form");
	inputFormNode.setAttribute("name", "modifyInputFrm");
	inputFormNode.setAttribute("method", "get");
	inputFormNode.setAttribute("onSubmit", "return false;");
	
	var inputFormDiv = document.createElement("div");
	inputFormDiv.setAttribute("class", "form-group");
	
	var newAttachedFileRowSeqHiddenNode = document.createElement("input");
	newAttachedFileRowSeqHiddenNode.setAttribute("type", "hidden");
	newAttachedFileRowSeqHiddenNode.setAttribute("name", "newAttachedFileRowSeq");
	newAttachedFileRowSeqHiddenNode.setAttribute("value", "0");
	
	inputFormDiv.appendChild(newAttachedFileRowSeqHiddenNode);
	
	if (isSubject) {			
		var subjetLabelTextNode = document.createTextNode("제목");
		
		var subjectLabelNode = document.createElement("label");
		subjectLabelNode.setAttribute("for", "subjectInEditor");
		subjectLabelNode.appendChild(subjetLabelTextNode);
		
		var subjectInputNode = document.createElement("input");
		subjectInputNode.setAttribute("type", "text");
		subjectInputNode.setAttribute("id", "subjectInEditor");
		subjectInputNode.setAttribute("name", "subject");
		subjectInputNode.setAttribute("class", "form-control");
		subjectInputNode.setAttribute("placeholder", "Enter subject");			
		
		inputFormDiv.appendChild(subjectLabelNode);
		inputFormDiv.appendChild(subjectInputNode);
	}
	
	var contentsLabelTextNode = document.createTextNode("내용");
	
	var contentsLabelNode = document.createElement("label");
	contentsLabelNode.setAttribute("for", "contentsOfBoard");
	contentsLabelNode.appendChild(contentsLabelTextNode);
	
	var contentsInputNode = document.createElement("textarea");		
	contentsInputNode.setAttribute("name", "contents");
	contentsInputNode.setAttribute("id", "contentsOfBoard");
	contentsInputNode.setAttribute("class", "form-control");
	contentsInputNode.setAttribute("placeholder", "Enter contents");		
	contentsInputNode.setAttribute("rows", "20");
	
	inputFormDiv.appendChild(contentsLabelNode);
	inputFormDiv.appendChild(contentsInputNode);
	
	if (isPassword) {
		var subjetLabelTextNode = document.createTextNode("비밀번호");
		
		var passwordLabelNode = document.createElement("label");
		passwordLabelNode.setAttribute("for", "passwordInEditor");
		passwordLabelNode.appendChild(subjetLabelTextNode);
		
		var passwordInputNode = document.createElement("input");
		passwordInputNode.setAttribute("type", "password");
		passwordInputNode.setAttribute("id", "passwordInEditor");
		passwordInputNode.setAttribute("name", "pwd");
		passwordInputNode.setAttribute("class", "form-control");
		passwordInputNode.setAttribute("placeholder", "Enter password");			
		
		inputFormDiv.appendChild(passwordLabelNode);
		inputFormDiv.appendChild(passwordInputNode);
	}
	
	inputFormNode.appendChild(inputFormDiv);
	
	targetDiv.appendChild(inputFormNode);
	
	var functionDiv = document.createElement("div");
	functionDiv.setAttribute("class", "btn-group");
	
	var saveButtonNode = document.createElement("input");
	saveButtonNode.setAttribute("type", "button");
	saveButtonNode.setAttribute("class", "btn btn-default");		
	saveButtonNode.setAttribute("onClick", "goModify();");
	saveButtonNode.setAttribute("value", "저장");
	
	
	var restoreOldAttachedFileListButtonNode = document.createElement("input");
	restoreOldAttachedFileListButtonNode.setAttribute("type", "button");
	restoreOldAttachedFileListButtonNode.setAttribute("class", "btn btn-default");		
	restoreOldAttachedFileListButtonNode.setAttribute("onClick", "restoreOldAttachedFileList(" + boardNo + ");");
	restoreOldAttachedFileListButtonNode.setAttribute("value", "기존 첨부 파일 목록 복구");	
	
	var addNewAttachedFIleButtonNode = document.createElement("input");
	addNewAttachedFIleButtonNode.setAttribute("type", "button");
	addNewAttachedFIleButtonNode.setAttribute("class", "btn btn-default");		
	addNewAttachedFIleButtonNode.setAttribute("onClick", "addNewAttachedFile(document.modifyInputFrm);");
	addNewAttachedFIleButtonNode.setAttribute("value", "신규 첨부 파일 추가");		
	
	var hideButtonNode = document.createElement("input");
	hideButtonNode.setAttribute("type", "button");
	hideButtonNode.setAttribute("class", "btn btn-default");		
	hideButtonNode.setAttribute("onClick", "closeEditScreen();");
	hideButtonNode.setAttribute("value", "닫기");
	
	functionDiv.appendChild(saveButtonNode);
	functionDiv.appendChild(restoreOldAttachedFileListButtonNode);
	functionDiv.appendChild(addNewAttachedFIleButtonNode);
	functionDiv.appendChild(hideButtonNode);
			
	targetDiv.appendChild(functionDiv);		
	
	var processFormNode = document.createElement("form");
	processFormNode.setAttribute("name", "modifyProcessFrm");
	processFormNode.setAttribute("target", "hiddenFrame");
	processFormNode.setAttribute("method", "post");
	processFormNode.setAttribute("action", "/servlet/DocumentModifyProcess");
	processFormNode.setAttribute("enctype", "multipart/form-data");
	
	var processFormDiv = document.createElement("div");
	processFormDiv.setAttribute("class", "form-group");
	
	var boardIDHiddenNode = document.createElement("input");
	boardIDHiddenNode.setAttribute("type", "hidden");
	boardIDHiddenNode.setAttribute("name", "boardID");
	boardIDHiddenNode.setAttribute("value", boardID);	
	
	var boardNoHiddenNode = document.createElement("input");
	boardNoHiddenNode.setAttribute("type", "hidden");
	boardNoHiddenNode.setAttribute("name", "boardNo");
	boardNoHiddenNode.setAttribute("value", boardNo);
	
	var nextAttachedFileSeqHiddenNode = document.createElement("input");
	nextAttachedFileSeqHiddenNode.setAttribute("type", "hidden");
	nextAttachedFileSeqHiddenNode.setAttribute("name", "nextAttachedFileSeq");
	nextAttachedFileSeqHiddenNode.setAttribute("value", nextAttachedFileSeq);
			
	var contentsHiddenNode = document.createElement("input");
	contentsHiddenNode.setAttribute("type", "hidden");
	contentsHiddenNode.setAttribute("name", "contents");
	
	var sessionkeyHiddenNode = document.createElement("input");
	sessionkeyHiddenNode.setAttribute("type", "hidden");
	sessionkeyHiddenNode.setAttribute("name", "<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>");		
	
	var ivHiddenNode = document.createElement("input");
	ivHiddenNode.setAttribute("type", "hidden");
	ivHiddenNode.setAttribute("name", "<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>");		
	
	
	var oldAttachedFileListDiv = document.createElement("div");
	oldAttachedFileListDiv.setAttribute("id", "oldAttachedFileList");
	
	var newAttachedFileListDiv = document.createElement("div");
	newAttachedFileListDiv.setAttribute("id", "newAttachedFileList");		
	
	processFormDiv.appendChild(boardIDHiddenNode);
	processFormDiv.appendChild(boardNoHiddenNode);
	processFormDiv.appendChild(nextAttachedFileSeqHiddenNode);
	if (isSubject) {
		var subjectHiddenNode = document.createElement("input");
		subjectHiddenNode.setAttribute("type", "hidden");
		subjectHiddenNode.setAttribute("name", "subject");
		
		processFormDiv.appendChild(subjectHiddenNode);
	}
	processFormDiv.appendChild(contentsHiddenNode);		
	processFormDiv.appendChild(sessionkeyHiddenNode);		
	processFormDiv.appendChild(ivHiddenNode);		
	processFormDiv.appendChild(oldAttachedFileListDiv);
	processFormDiv.appendChild(newAttachedFileListDiv);		
	
	if (isPassword) {
		var passwordHiddenNode = document.createElement("input");
		passwordHiddenNode.setAttribute("type", "hidden");
		passwordHiddenNode.setAttribute("name", "pwd");
		
		processFormDiv.appendChild(passwordHiddenNode);
	}		
	
	
	processFormNode.appendChild(processFormDiv);		
	
	targetDiv.appendChild(processFormNode);
	
	$('#contentsOfBoard').summernote({
<%= summerNoteConfiguration.buildInitializationOptionsString(3) %>,
		placeholder: '이곳에 글을 작성해 주세요',
        tabsize: 2,
        height: 400
	});
			
	var f = document.modifyInputFrm;
	
	if (isSubject) {
		var subjectDiv = document.getElementById("subjectOfBoard"+boardNo+"InViewScreen");
		f.subject.value = subjectDiv.innerText;
	}
	var contentsDiv = document.getElementById("contentsOfBoard"+boardNo+"InViewScreen");
	// f.contents.value = contentsDiv.innerText;
	
	$('#contentsOfBoard').summernote("code", contentsDiv.innerHTML);
	
	restoreOldAttachedFileList(boardNo);
	
	var contentsToggleButtonObj = document.getElementById("contentsToggleButton");
	contentsToggleButtonObj.innerText = "문서 보이기";
	var contentsScreenObj = document.getElementById("contentsOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen");
	contentsScreenObj.style.display = "none";
	
}

function closeEditScreen() {
	if (null != currentEditScreenDiv) {
		currentEditScreenDiv.style.display = "none";
		
		/** remove all child nodes of targetDiv node */
		while(currentEditScreenDiv.hasChildNodes()) {
			currentEditScreenDiv.removeChild(currentEditScreenDiv.firstChild);
		}
		
		currentEditScreenDiv = null;
		
		var contentsToggleButtonObj = document.getElementById("contentsToggleButton");
		contentsToggleButtonObj.innerText = "문서 닫기";
		var contentsScreenObj = document.getElementById("contentsOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen");
		contentsScreenObj.style.display = "block";
	}
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

	function clickContentsToggleButton(buttonObj) {
		var contentsScreenObj = document.getElementById("contentsOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen");
		
		if (contentsScreenObj.style.display == 'none') {
			buttonObj.innerText = "문서 닫기";
			contentsScreenObj.style.display = "block";			
		} else {
			buttonObj.innerText = "문서 보이기";
			contentsScreenObj.style.display = "none";
		}
	}
	
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    top.location.href = "/";
		}
		
		var interestedBoadNoDiv = document.getElementById('viewScreenForBoard<%=interestedBoadNo%>');
		
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
			<%
				if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType)) {
					out.write(getMenuNavbarString(request));
				}
			%>
		</div>
	</div>
	<form name=goListFrm method="get" action="/servlet/DocumentManager">
		<input type="hidden" name="boardID"
			value="<%=boardDetailRes.getBoardID()%>" />
	</form>

	<form name=goDetailFrm method="get" action="/servlet/DocumentView">
		<input type="hidden" name="boardID"
			value="<%=boardDetailRes.getBoardID()%>" /> <input type="hidden"
			name="boardNo" value="<%=boardDetailRes.getBoardNo()%>" /> <input
			type="hidden" name="interestedBoadNo" />
	</form>	
	
	<form name=boardChangeHistoryFrm method="get"
		action="/servlet/BoardChangeHistory">
		<input type="hidden" name="boardID"
			value="<%=boardDetailRes.getBoardID()%>" /> <input type="hidden"
			name="boardNo" />
	</form>

	<form name=goDownloadFrm target="hiddenFrame" method="post"
		action="/servlet/BoardDownload">
		<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" /> <input type="hidden" name="attachedFileSeq" />
	</form>
	
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h4><%=boardDetailRes.getBoardName()%>
						게시판 - 상세보기
					</h4>
				</div>
				<div class="panel-body">
					<div class="btn-group"><%
							

	if (accessedUserformation.getUserID().equals(boardDetailRes.getFirstWriterID())
			|| accessedUserformation.isAdmin()) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		// boardID, boardNo, nextAttachedFileSeq, isSubject, isPassword
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showModifyEditScreen(");
		out.write(String.valueOf(boardDetailRes.getBoardID()));
		out.write(", ");
		out.write(String.valueOf(boardDetailRes.getBoardNo()));
		out.write(", ");
		out.write(String.valueOf(boardDetailRes.getNextAttachedFileSeq()));
		out.write(", ");
		// 제목은 계층형 게시판에만 본문(=루트)과 댓글 모두 허용되며 다른 게시판의 경우 오직 본문(=루트)글에만 허용된다
		out.write(
				String.valueOf(BoardListType.TREE.equals(boardListType) || boardDetailRes.getParentNo() == 0));
		out.write(", ");
		out.write(String.valueOf(boardDetailRes.getIsBoardPassword()));
		out.write(")\">수정</button>");
	}
	
	if (accessedUserformation.getUserID().equals(boardDetailRes.getFirstWriterID())) {
		if (accessedUserformation.isLoginedIn()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						  				");
			out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goDelete(");
			out.write(String.valueOf(boardDetailRes.getBoardNo()));
			out.write(")\">삭제</button>");
		} else {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						  				");
			out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showDeleteEditScreen(");
			out.write(String.valueOf(boardDetailRes.getBoardNo()));
			out.write(")\">삭제</button>");
		}
	}

						%>
						<button type="button" class="btn btn-primary btn-sm"
							onClick="goBoardChangeHistory(<%=boardDetailRes.getBoardNo()%>)">수정
							이력 조회</button><%
	if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType)) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goList();\">목록으로</button>");
	}							
%>
						<button id="contentsToggleButton" type="button" class="btn btn-primary btn-sm" onClick="clickContentsToggleButton(this);">문서 닫기</button>
						<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>
					</div>
					
					<div id="resultMessage"></div>
					<br>
					<div id="viewScreenForBoard<%=boardDetailRes.getBoardNo()%>">
						<div style="display: none"
							id="oldAttachedFileListJosnStringOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen"><%=StringEscapeActorUtil.replace(
					(null == boardDetailRes.getAttachedFileList()) ? "[]"
							: new Gson().toJson(boardDetailRes.getAttachedFileList()),
					StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></div>
					
						<div class="row">
							<div class="col-xs-1">글번호</div>
							<div class="col-xs-1"><%=boardDetailRes.getBoardNo()%></div>
							<div class="col-xs-2">작성자</div>
							<div class="col-xs-2"><%
	if (MemberRoleType.GUEST.equals(firstWriterRoleType)) {
		out.write(boardDetailRes.getFirstWriterID());
	} else {
		String firstWriterNickName = StringEscapeActorUtil.replace(boardDetailRes.getFirstWriterNickname(),
				STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4);

		out.write("<div class=\"dropdown\">");

		out.write("<a href=\"#\" data-toggle=\"dropdown\">");
		out.write(firstWriterNickName);
		out.write("</a>");

		out.write("<ul class=\"dropdown-menu\" role=\"menu\">");

		out.write("<li role=\"presentation\">");
		out.write("<a role=\"menuitem\" tabindex=\"-1\" href=\"#\" onclick=\"goMemberInformation('");
		// out.write("/servlet/MemberInformation?targetUserID=");
		out.write(boardDetailRes.getFirstWriterID());
		out.write("')\">개인 정보</a>");
		out.write("</li>");

		out.write("<li role=\"presentation\">");
		out.write("<a role=\"menuitem\" tabindex=\"-1\" href=\"#\" onclick=\"goPersonalActivityHistory('");
		// out.write("/servlet/PersonalActivityHistory?targetUserID=");
		out.write(boardDetailRes.getFirstWriterID());
		out.write("')\">회원 활동 이력 조회</a>");
		out.write("</li>");

		out.write("</ul>");
		out.write("</div>");

	}
%></div>
							<div class="col-xs-2">최초 작성일</div>
							<div class="col-xs-3"><%=boardDetailRes.getFirstRegisteredDate()%></div>
						</div>
	
						<div class="row">
							<div class="col-xs-1">추천수</div>
							<div class="col-xs-1" id="voteOfBoard<%=boardDetailRes.getBoardNo()%>"><%=boardDetailRes.getVotes()%></div>
							<div class="col-xs-2">게시판 상태</div>
							<div class="col-xs-2"><%=BoardStateType.valueOf(boardDetailRes.getBoardSate()).getName()%></div>
							<div class="col-xs-2">마지막 수정일</div>
							<div class="col-xs-3"><%=boardDetailRes.getLastModifiedDate()%></div>
						</div>
						<div class="row">
							<div class="col-xs-1">제  목 :</div>
							<div class="col-xs-11" id="subjectOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen"><%= StringEscapeActorUtil.replace(boardDetailRes.getSubject(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></div>
						</div>
						<div class="row">
							<article id="contentsOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen"><%=boardDetailRes.getContents()%></article>
						</div>
						<div class="row">
							<div class="col-xs-2"><b>첨부 파일</b></div>
							<div class="col-xs-10" id="oldFileListOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen"><%
	if (null != boardDetailRes.getAttachedFileList()) {
		boolean isFirst = true;
		for (BoardDetailRes.AttachedFile oldAttachedFile : boardDetailRes.getAttachedFileList()) {
			if (isFirst) {
				isFirst = false;
			} else {
				out.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			}
			out.write("<a style=\"text-decoration: underline;\" title=\"다운로드 ");
			out.write(StringEscapeActorUtil.replace(oldAttachedFile.getAttachedFileName(),
					STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
			out.write("(seq:");
			out.write(String.valueOf(oldAttachedFile.getAttachedFileSeq()));
			out.write(", size:");
			out.write(String.valueOf(oldAttachedFile.getAttachedFileSize()));
			out.write(")\" onClick=\"downloadFile(");
			out.write(String.valueOf(boardDetailRes.getBoardNo()));
			out.write(", ");
			out.write(String.valueOf(oldAttachedFile.getAttachedFileSeq()));
			out.write(")\">");
			out.write(StringEscapeActorUtil.replace(oldAttachedFile.getAttachedFileName(),
					STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
			out.write("</a>");
		}
	}
%></div>
						</div>
						<div id="editorScreenForBoard<%=boardDetailRes.getBoardNo()%>" style="display: block"></div>
					</div>
				</div>
			<iframe id="hiddenFrame" name="hiddenFrame" style="display: none;"></iframe>
			</div>
		</div>
	</div>
</body>
</html>