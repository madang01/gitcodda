<pre><h2 style=""><b>&gt;&gt;&gt;&gt;&gt; 알림</b></h2><h4 style="">현재 이 사이트는 코다 코어 라이브러리를 이용하여 작성된 사이트로 통합 테스트를 목적으로 공개를 한 상태입니다.
테스트상 잦은 먹통이 있을 수 있으니 양해 바랍니다.</h4></pre><h2><span style="color: inherit; font-family: inherit;">1. 코다란?</span><br></h2><p style="margin-left: 25px;">코다는 자바로 만든 아파치2 라이센스를 갖는오픈 소스로 RPC 서버를 기반 개발 프레임워크입니다.</p><h2>2. 코다 RPC 서버&nbsp;&nbsp;</h2><h3 style="margin-left: 25px;">2.1 정의</h3><p style="margin-left: 50px;">코다 RPC 서버는 <span style="font-weight: bold; color: rgb(0, 0, 255);">싱글 쓰레드</span>로 자바 NIO selector 기반 서버입니다.</p><h3 style="margin-left: 25px;">2.2 할일및 단점</h3><p style="margin-left: 50px;">(1) 현재 버전은 오래 걸리는 작업에 대한 해법이 없어서 만약 오래 걸리는 작업이 있으면 전체적으로 그 만큼 딜레이가 되는 단점이 있습니다.</p><p style="margin-left: 50px;">(2) 비동기 메시지는 특별한 노력 없이도 하드웨어 한계까지 데이터를 보낼 수 있기때문에 이를 적절하게 제어를 하지 않으면 서버 자원이 고갈되기때문에 이에 대한 대비책이 있어야 하는데 현재 클라이언트에서 메시지를 보낸 갯수와 수신한&nbsp; 메시지 갯수 차에 대한 제약을 걸어서 해결하고 있습니다. 이 방법은 악의적인 클라이언트에 속수 무책임 하여 클라이언트가 아닌 서버 단에서 이를 해결할 필요가 있음.</p><p style="margin-left: 50px;">(3) 현재 서버 비지니스 로직이 의존하는 동적 클래스 변경시 이를 감지 못하는데 감지하는 기능이 필요함.</p><p style="margin-left: 50px;">(4) 채팅이나 게임 같은 경우 다른 사용자한테 메시지를 보내는데 보낼 메시지 자체가 자원이라 이와 간련 정책을 정하고 그 정책을 수행할 방법론이 필요함</p><h2>3. 코다 RPC 서버 접속 API</h2><h3 style="margin-left: 25px;">3.1 지원 언어 : 자바</h3><h3 style="margin-left: 25px;">3.2 종류</h3><p style="margin-left: 50px;">(1) 동기 폴 : 구 자바 IO 의 소켓 폴로 입력 메시지를 보내고 응답을 기다려 출력 메시지를 얻는다.</p><p style="margin-left: 50px;">(2) 비동기 폴 : 자바 NIO selctor 를 이용한 socket channel pool 로&nbsp; 동기와 비동기중 하나를 선택한것에 따라 동작한다. 동기 선택시 입력 메시지를 보낸후 응답을 기다려 출력 메시지를 얻고, 비동기 선택시 입력 메시지를 보내고 응답 메시지를 기다리지 않는다.&nbsp; 참고) 클라이언트 자바 NIO selector&nbsp; 쓰레드는 오직 1개이며 비동기 메시지에 대한 처리를 담당하고 있다.</p><h3 style="margin-left: 25px;">3.3 할일및 단점</h3><p style="margin-left: 50px;">(1) 클라이언트용 동적 클래스로더 미 구현</p><p style="margin-left: 50px;">(2) 동기 폴에서도 비동기 메시지에 대한 지원</p><p style="margin-left: 50px;">(3) 다양한 언어및 플래폼 지원 검토및 필요함. 만약 자력이 어렵다면 직렬화/역직렬화 오픈 소스 라이브러리 사용을 검토해야함</p><p style="margin-left: 50px;">(4) half close 지원 검토 필요함</p><h2 style="">4. 입출력 문서 주도 개발</h2><h3 style="margin-left: 25px;">4.1 입출력 문서 주도 개발이란?</h3><p style="margin-left: 50px;">입출력 문서 주도 개발이란 입력 메시지와 출력 메시지를 먼저 정의한후 그에 맞추어서 작업을 진행해 나가는 개발입니다.</p><h3 style="margin-left: 25px;">4.2 장점</h3><p style="margin-left: 50px;">프론트와 백엔드가 작업이 분리되어 동시에 진행할 수 있어 업무 효율이 좋음.</p><h3 style="margin-left: 25px;">4.3 단점</h3><p style="margin-left: 50px;">시간이 지날 수록 의존 관계가 잘 정리되지 못한 모듈이 늘어나서 유지 보수를 힘들게함.</p><h2 style="">5. 코다 개발 프레임워크</h2><h3 style="margin-left: 25px;">5.1 개발 흐름도</h3><p style="margin-left: 25px;">(1) 1단계 : 입출력 메시지 작성, 예시) LoginReq, LoginRes</p><p style="margin-left: 25px;">(2) 2단계 : 코다 도우미(=codda-helper.jar) 를 이용하여 작성된 메시지로 부터 입출력에 필요한 파일 생성</p><p style="margin-left: 25px;">(3) 3단계</p><p style="margin-left: 50px;">(3-1) 입출력에 필요한 파일를 기반으로 서버 비지니스 로직 작성, 예시) LoginReqServerTask.java</p><p style="margin-left: 50px;">(3-2) 입출력에 필요한 파일를 기반으로 클라이언트 로직 작성, 예시) LoginProcessSvl.java, LoginSuccess.jsp, , LoginFail.jsp</p><p style="margin-left: 25px;">(4) 4단계 : 테스트</p><h3 style="margin-left: 25px;">5.2 할일및 단점</h3><p style="margin-left: 50px;">(1)&nbsp; 입출력 문서 기반한 개발이 가능하도록 지원해주는 도구 혹은 시스템 개발 필요함</p><p style="margin-left: 50px;">(2) 빈도수 통계 필요함</p>


참여를 원하시면 k9200544@hanmail.net 으로 연락해 주세요.
언제나 환영합니다.

