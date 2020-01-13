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
					<article style="white-space:pre-wrap;"><pre><h2 style=""><b>&gt;&gt;&gt;&gt;&gt; 알림</b></h2><h4 style="margin-left: 25px;">현재 이 사이트는 자체 제작한 코다를 이용하여 작성된 사이트로 베타 테스트가 목적인 사이트입니다.
베타 테스트를 위해 잦은 먹통이 있을 수 있으니 양해 바랍니다.</h4></pre><h2><span style="color: inherit; font-family: inherit;">1. 코다란?</span><br></h2><p style="margin-left: 25px;">(1) 자바로 만든 아파치2 라이센스를 갖는오픈 소스</p><p style="margin-left: 25px;">(2) RPC 서버 기반의 개발 프레임워크</p><p style="margin-left: 25px;">(3) 입출력 메시지 주도 개발 프레임워크</p><h2>2. 코다 RPC 서버&nbsp;&nbsp;</h2><h3 style="margin-left: 25px;">2.1 정의</h3><p style="margin-left: 50px;">코다 RPC 서버는 <span style="font-weight: bold; color: rgb(0, 0, 255);">싱글 쓰레드</span>로 자바 NIO selector 기반 서버입니다.</p><h3 style="margin-left: 25px;">2.2 특징</h3><p style="margin-left: 50px;">(1) 싱글 쓰레드 : 동기화 관련 시행 착오를 걸처 찾은 근본적 해결책이 싱글 쓰레드임.</p><p style="margin-left: 50px;">(2) 자체 동적 클래스 로더 : 서버 비지니스 로직 클래스와 입출력 클래스를 동적 클래스 대상으로 서버 재 가동 없이도 서버 비지니스 로직 변경이 적용됨.</p><h3 style="margin-left: 25px;">2.3 할일및 단점</h3><p style="margin-left: 50px;">(1) 현재 버전은 오래 걸리는 작업에 대한 해법이 없어서 만약 오래 걸리는 작업이 있으면 전체적으로 그 만큼 딜레이가 되는 단점이 있습니다.</p><p style="margin-left: 50px;">(2) 비동기 메시지는 특별한 노력 없이도 하드웨어 한계까지 데이터를 보낼 수 있기때문에 이를 적절하게 제어를 하지 않으면 서버 자원이 고갈되기때문에 이에 대한 대비책이 있어야 하는데 현재 클라이언트에서 메시지를 보낸 갯수와 수신한&nbsp; 메시지 갯수 차에 대한 제약을 걸어서 해결하고 있습니다. 이 방법은 악의적인 클라이언트에 속수 무책임 하여 클라이언트가 아닌 서버 단에서 이를 해결할 필요가 있음.</p><p style="margin-left: 50px;">(3) 현재 서버 비지니스 로직이 의존하는 동적 클래스 변경시 이를 감지하여 서버 비지니스 로직을 재 로딩 못함. 의존성 파일들까지 변경 감지하는 기능이 필요함.</p><p style="margin-left: 50px;">(4) 채팅이나 게임 같은 경우 다른 사용자한테 메시지를 보내는데 보낼 메시지 자체가 자원이라 이와 간련 정책을 정하고 그 정책을 수행할 방법론이 필요함</p><h2>3. 코다 RPC 서버 접속 API</h2><h3 style="margin-left: 25px;">3.1 지원 언어 : 자바</h3><h3 style="margin-left: 25px;">3.2 종류</h3><p style="margin-left: 50px;">(1) 동기 폴 : 구 자바 IO 의 소켓를 랩퍼로 감싼 연결 폴로 입력 메시지를 보내고 응답을 기다려 출력 메시지를 얻는다.</p><p style="margin-left: 50px;">(2) 비동기 폴 : 자바 NIO selctor 를 이용한 소켓 채널을 랩퍼로 감싼 연결 폴로&nbsp; 동기와 비동기중 하나를 선택한것에 따라 동작한다. 동기 선택시 입력 메시지를 보낸후 응답을 기다려 출력 메시지를 얻고, 비동기 선택시 입력 메시지를 보내고 응답 메시지를 기다리지 않는다.&nbsp; 참고) 클라이언트 자바 NIO selector&nbsp; 쓰레드는 오직 1개이며 비동기 메시지에 대한 처리를 담당하고 있다.</p><p style="margin-left: 50px;">(3) 동기 단독 연결 : 동기 메시지 송수신만 가능한 연결</p><p style="margin-left: 50px;">(4) 비동기 단독 연결 : 동기/비동기 메시지&nbsp; 송수신 모두 가능한 연결</p><h3 style="margin-left: 25px;">3.3 할일및 단점</h3><p style="margin-left: 50px;">(1) 클라이언트용 동적 클래스로더 미 구현</p><p style="margin-left: 50px;">(2) 동기 폴에서도 비동기 메시지에 대한 지원</p><h2 style="">4. 입출력 메시지 주도 개발 프레임워크</h2><h3 style="margin-left: 25px;">4.1 입출력 메시지 주도 개발이란?</h3><p style="margin-left: 50px;">입출력 문서를 바탕으로 입력 메시지와 출력 메시지를 먼저 정의한 후 서비스를 개발해 나가는 프레임워크</p><h3 style="margin-left: 25px;">4.2 장점</h3><p style="margin-left: 50px;">프론트와 백엔드가 작업이 분리되어 동시에 진행할 수 있어 업무 효율이 좋음.</p><h3 style="margin-left: 25px;">4.3 단점</h3><p style="margin-left: 50px;">시간이 지날 수록 의존 관계가 꼬인 모듈을 편의성 차원에서 늘리게 되어 결국 유지 보수가 힘들어짐.</p><h2 style="">5. 코다 개발 프레임워크</h2><h3 style="margin-left: 25px;">5.1 개발 흐름도</h3><p style="margin-left: 50px;">(1) 1단계 : 입출력 메시지 정보 파일 작성</p><pre style="margin-left: 25px;"># 예시 : MemberLoginReq.xml

&lt;?xml version="1.0" encoding="utf-8" ?&gt;
&lt;!--
	item type : (unsigned) byte, (unsigned) short, (unsigned) integer, long,
					fixed length string, 
					ub pascal string, us pascal string, si pascal string, 
					fixed length byte[], ub variable length byte[], 
					us variable length byte[], si variable length byte[]
					java sql date, java sql timestamp
	array counter type : reference 변수참조, direct 직접입력
	direction : FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL
	(1) FROM_NONE_TO_NONE : 메시지는 서버에서 클라이언트로 혹은 클라이언트에서 서버로 양쪽 모두에서 전송되지 않는다.
	(2) FROM_SERVER_TO_CLINET : 메시지는 서버에서 클라이언트로만 전송된다.
	(3) FROM_CLIENT_TO_SERVER : 메시지는 클라이언트에서 서버로만 전송된다.
	(4) FROM_ALL_TO_ALL : 메시지는 서버에서 클라이언트로도 혹은 클라이언트에서 서버로 양쪽 모두에서 전송된다.
--&gt;
&lt;message&gt;
&lt;messageID&gt;MemberLoginReq&lt;/messageID&gt;
&lt;direction&gt;FROM_CLIENT_TO_SERVER&lt;/direction&gt;
&lt;desc&gt;회원 로그인 입력 메시지&lt;/desc&gt;
&lt;singleitem name="idCipherBase64" type="si pascal string" /&gt;
&lt;singleitem name="pwdCipherBase64" type="si pascal string" /&gt;
&lt;singleitem name="sessionKeyBase64" type="si pascal string" /&gt;
&lt;singleitem name="ivBase64" type="si pascal string" /&gt;
&lt;singleitem name="ip" type="ub pascal string"/&gt;
&lt;/message&gt;</pre><p style="margin-left: 50px;">(2) 2단계 : 코다 도우미(=codda-helper.jar) 를 이용하여 작성된 메시지 정보 파일로 부터 입출력에 필요한 파일 생성,</p><p style="margin-left: 75px;">- MemberLogin.xml 를 통해 총 5개 파일이 생성됩니다.</p><p style="margin-left: 75px;">(2-1) <a href="/sitemenu/doc/JavaSourceFile.jsp?fullClassName=kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq">MemberLoginReq.java</a></p><p style="margin-left: 75px;">(2-2)&nbsp;<a href="/sitemenu/doc/JavaSourceFile.jsp?fullClassName=kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqClientCodec">MemberLoginReqClientCodec.java</a></p><p style="margin-left: 75px;">(2-3)&nbsp;<a href="/sitemenu/doc/JavaSourceFile.jsp?fullClassName=kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqServerCodec">MemberLoginReqServerCodec.java</a></p><p style="margin-left: 75px;">(2-4)&nbsp;&nbsp;<a href="/sitemenu/doc/JavaSourceFile.jsp?fullClassName=kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqEncoder">MemberLoginReqEncoder.java</a></p><p style="margin-left: 75px;">(2-5)&nbsp;<a href="/sitemenu/doc/JavaSourceFile.jsp?fullClassName=kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReqDecoder">MemberLoginReqDecoder.java</a></p><p style="margin-left: 50px;">(3) 3단계</p><p style="margin-left: 75px;">(3-1) 입출력에 필요한 파일를 기반으로 서버 비지니스 로직 작성, 예시) <a href="/sitemenu/doc/JavaSourceFile.jsp?fullClassName=kr.pe.codda.impl.task.server.MemberLoginReqServerTask">MemberLoginReqServerTask.java</a></p><p style="margin-left: 75px;">(3-2) 입출력에 필요한 파일를 기반으로 클라이언트 로직 작성, 예시) <a href="/sitemenu/doc/JavaSourceFile.jsp?fullClassName=kr.pe.codda.servlet.user.MemberLoginProcessSvl">MemberLoginProcessSvl.java</a>, MemberLoginProcess.jsp</p><p style="margin-left: 50px;">(4) 4단계 : 테스트</p><h3 style="margin-left: 25px;">5.2 할일및 단점</h3><p style="margin-left: 50px;">(1) 입출력 문서 기반한 개발이 가능하도록 지원해주는 도구 혹은 시스템 개발 필요함</p><p style="margin-left: 50px;">(2) 빈도수 통계 필요함</p><p style="margin-left: 50px;">(3) half close 검토 필요</p><p style="margin-left: 50px;">(4) json&nbsp; 지원 프로토콜 추가</p><p style="margin-left: 50px;">(5) 자체 제작한 직렬화/역직렬화 라이브러리를 다양한 언어및 플래폼에서도 지원할 필요가 있음. 만약 자력이 어렵다면 직렬화/역직렬화 오픈 소스 라이브러리 사용을 검토해야함.</p></article>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
