<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	request.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL, "/sitemenu/doc/CoddaHowTo.jsp");
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
<body>
	<div class=header>
		<div class="container">
		<%= getMenuNavbarString(request) %>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h1 style="text-align: center; "><b>코다 데비안 개발환경 구축 HOWTO</b></h1></div>
				<div class="panel-body">
					<article style="white-space:pre-line;">
<h2>0. 시작하기 앞서 알아 두어야할 사항</h2><p style="margin-left: 25px;">첫번째 보안을 위해서 git 에 DB 계정 혹은 개인 메일 계정의 아이디와 비밀번호를 올리지 않도록 주의하시기 바랍니다.</p><p style="margin-left: 25px;">두번째 서버로 운영해야 하기때문에 자동 IP 가 아닌 수동으로 설정한 IP 이어야 합니다. 코다 개발 환경을 구축할 PC 는 OS 로 데비안 9.9 가 설치되었으며 IP 주소는 172.30.1.15 를 갖습니다. 자신의 PC 환경에 맞게 IP 와 와 DB 계정 비밀번호를 바꾸어 주시면 됩니다. DB 계정 비밀번호가 test1234 라면 아래 howto 문서에 있는 ''root비밀번호'' 로 표시된 부분에서 작은 따옴표 안에 자신의 비밀번호를 넣어 ''test1234'' 로 수정하시면 됩니다.</p><p style="margin-left: 25px;">세번째 아파치 &amp; tomcat 은 계속 진화중인지라 각 버전마다 설정 방법이 조금씩 다를 수 있습니다.</p><p style="margin-left: 25px;">네번째 sample_base 라는 프로젝트는 코다 코어 라이브러리를 활용하여 코다 커뮤니트 사이트를 구축할 목적으로 만든 프로젝트입니다. 코다 커뮤니트 사이트에서 비밀번호 찾기 메뉴에서 메일 서버가 필요한데 git 에 올려진 메일 서버 설정(=&gt; [codda 설치 경로]/project/sample_base/resources/email.properties)은 가짜 메일 서버에 맞춘것입니다. 메일 서버 설치및 운영은 매우 복잡하기때문에 네이버, 다음, 구글등 외부 메일 서버를 이용하는것을 권장합니다. 외부 메일 서버 설정은 <a href="http://blog.naver.com/jmkoo02/20199969614" target="_blank">Java Mail API 활용:주요 포털 웹메일 IMAP/SMTP 설정 정보</a>" 블로그를 참고해 주시기 바랍니다.</p><p style="margin-left: 25px;">다섯번째 리눅스의 mariadb 는 대소문자를 구별 하는것이 디폴트이고&nbsp; jooq 는 소문자로 움직이기때문에 테이블명이나 항목명 모두 소문자로 맞추었습니다.&nbsp;</p><p style="margin-left: 25px;">여섯째 코다의 과거 이름은 신놀이 였으며 그때 사용한 도메인이 이곳 www.sinnori.pe.kr 입니다. 이 문서는 이 도메인 기준으로 작성되었으며 통합 테스트를 위해서 이 도메인으로 가 오픈할 예정입니다.</p><p style="margin-left: 25px;">일곱번째 아래 필요한 파일들을 설치해서인지 ant 가 자동으로 설치된것을 확인할 수 있습니다. 만약에 ant 가 없다면 ant 도 추가해 주시기 바랍니다.</p><pre style="margin-left: 25px;">------------ 필요한 파일 설치 파트 시작 ------------<br>(1) apt-get install git<br>(2) apt-get install default-jdk<br>(3) apt-get install mariadb-server<br>(4) apt-get install apache2<br>(5) apt-get install tomcat8<br>(6) apt-get install libapache2-mod-jk<br>(7) apt-get install mysql-workbench<br>------------ 필요한 파일 설치 파트 종료 ------------<br>------------ 기타 유틸 설치 파트 시작 ------------<br>(8) apt-get install net-tools<br>------------ 기타 유틸 설치 파트 종료 ------------</pre><p><span style="color: inherit; font-family: inherit; font-size: 30px;">1. 코다(=Codda) 개발 환경 구축하기 시작</span><br></p><h3 style="margin-left: 25px;">1.1 코다(=Codda) 소스 다운로드</h3><p style="margin-left: 50px;">(1) cd ~</p><p style="margin-left: 50px;">(2) git clone https://github.com/madang01/gitcodda.git<br></p><h3 style="margin-left: 25px;">1.2 코다(=Codda) 설치 경로 적용</h3><p style="margin-left: 50px;">(3) cd gitcodda</p><p style="margin-left: 50px;">(4) cd codda2&nbsp; &nbsp;&nbsp;</p><p style="margin-left: 50px;">(5) chmod u+x Helper.sh</p><p style="margin-left: 50px;">(6) 코다 도우미 유틸을 통해 설치 경로 적용</p><p style="margin-left: 50px;">(7-1) ./Helper.sh</p><p style="margin-left: 50px;">(7-2) 코다 도우미 첫화면에서 "All Main Project Manger" 버튼 클릭</p><p style="margin-left: 50px;"><img src="/img/howto/codda_helper_screenshot01.png"></p><p style="margin-left: 50px;">(7-3) 설치 경로 지정 화면에서 설치 경로를 입력후 "Next" 버튼 클릭</p><p style="margin-left: 50px;"><img src="/img/howto/codda_helper_screenshot02.png" style="width: 50%;"></p><p style="margin-left: 50px;">(7-4) "All Main Project Manger" 화면에서 "apply Codda installed path to all project" 버튼 클릭</p><p style="margin-left: 50px;"><img src="/img/howto/codda_helper_screenshot03.png" style="width: 50%;"></p><p style="margin-left: 50px;">(7-5) 창종료<br></p><h3 style="margin-left: 25px;">1.3 마리아 DB 연동 시키기</h3><p style="margin-left: 50px;">(1) mariadb 원격 접속 허용및 대소문자 구별 없애기 설정</p><p style="margin-left: 75px;">(1-1) /etc/mysql/mariadb.conf.d/50-server.cnf 파일에서 bind-address = 127.0.0.1 앞에 # 을 붙여 주석 처리하기</p><p style="margin-left: 75px;">(1-2) 바로 아래줄에 lower_case_table_names = 1 추가</p><p style="margin-left: 75px;">(1-3) mariadb 리스타트 : /etc/init.d/mysql restart</p><p style="margin-left: 50px;">(2) mariadb root 비밀번호 설정 :&nbsp;<a href="https://sarc.io/index.php/mariadb/931-mysql-mariadb-update-root-password" target="_blank">MySQL/MariaDB 초기 설치 후 계정 패스워드 초기화</a></p><p style="margin-left: 75px;">(2-1) mysql -u root</p><p style="margin-left: 75px;">(2-2) DB root 계정 비밀번호 설정</p><pre style="margin-left: 25px;">MariaDB [(none)]&gt; use mysql;<br>Reading table information for completion of table and column names<br>You can turn off this feature to get a quicker startup with -A<br>Database changed<br>MariaDB [mysql]&gt; update user set password=password(''root비밀번호'') where user=''root'';<br>Query OK, 1 row affected (0.00 sec)<br>Rows matched: 1&nbsp; Changed: 1&nbsp; Warnings: 0<br>MariaDB [mysql]&gt; flush privileges;</pre><p style="margin-left: 75px;">(2-3) root 계정 원격에서 접속 허용하기 :&nbsp;<a href="https://mariadb.com/kb/en/library/configuring-mariadb-for-remote-client-access/" target="_blank">Configuring MariaDB for Remote Client Access</a></p><pre style="margin-left: 25px;">MariaDB [(none)]&gt; GRANT ALL PRIVILEGES ON *.* TO ''root''@''172.30.1.15'' IDENTIFIED BY ''root비밀번호'' WITH GRANT OPTION;<br>Query OK, 0 rows affected (0.00 sec)</pre><p style="margin-left: 50px;">(3) workbench 설정</p><p style="margin-left: 75px;">(3-1) workbench 초기 화면</p><p style="margin-left: 75px;"><img src="/img/howto/workbench_screenshot01.png" style="width: 50%;"></p><p style="margin-left: 75px;">(3-2) 기본적으로 있는 "Local instance 3306" 에 마우스 우클릭으로 펼쳐진 메뉴에서 "Edit Connection..." 선택</p><p style="margin-left: 75px;"><img src="/img/howto/workbench_screenshot02.png" style="width: 50%;"></p><p style="margin-left: 75px;">(3-3) "Connection" tab -&gt; "Parameters" tab 에서 Hostname 항목에 호스트 주소를 입력후 "Store in Keychain ..." 버튼 클릭하여 비밀번호 설정후 "Test Connection" 버튼 클릭하여 연결 테스트하여 정상 연결되면 "close" 버튼 클릭</p><p style="margin-left: 75px;"><img src="/img/howto/workbench_screenshot03.png" style="width: 50%;"></p><p style="margin-left: 75px;">(3-4) "Local instance 3306" 버튼 클릭후 "Connection Warning" 경고창에서 "Continue Anyway" 버튼 클릭</p><p style="margin-left: 75px;"><img src="/img/howto/workbench_screenshot04.png" style="width: 50%;"></p><p style="margin-left: 75px;">(3-5) workbench 정상 접속 화면</p><p style="margin-left: 75px;"><img src="/img/howto/workbench_screenshot05.png" style="width: 50%;"></p><p style="margin-left: 75px;">(3-6)&nbsp; "sample_base_db_erd.mwb" 파일을 열어 Databse -&gt; Synchronize Model 메뉴을 선택하여 ''sb_db'', ''gt_sb_db'', ''lt_sb_db'' 각 스키마 생성후 madangshoe01&nbsp; 계정을 새로 만들때 select, insert, update, delete 권한을 줄것</p><p style="margin-left: 50px;">(4) 하드 코딩된 웹사이트 관리자 비밀 번호 변경하기</p><pre style="margin-left: 25px;">[코다 설치 경로]/project/sample_base/server_build 경로로 들어가서 
src/main/java/main/SeverMain.java 에 하드코딩된 admin 계정의 비밀번호를 바꾼후 컴파일 한후 
sh sample_baseServer.sh 을 실행하여 서버 실행이 잘되는지를 확인합니다.<br></pre><h3 style="margin-left: 25px;">1.4 코다 sample_base 프로젝트를 위한 아파치와 톰캣 환경 구축</h3><p style="margin-left: 50px;">(1) 아파치와 tomcat 설정</p><p style="margin-left: 75px;">(1-1) 아파치에 가상 호스트 등록</p><pre style="margin-left: 25px;">[코다 설치 경로]/resources/tomcat/debian/vhost1.conf 파일을 /etc/apache2/sites-available/ 로 복사후 자기 환경에 맞게 수정
### vhost1.conf file 샘플 시작 ###
&lt;virtualhost www.sinnori.pe.kr&gt;
	JkMount /*.jsp ajp13_worker
	JkMount /servlet/* ajp13_worker
	ServerName www.codda.pe.kr
	ServerAdmin k9200544@hanmail.net
	DocumentRoot [코다 설치 경로]/project/sample_base/user_web_app_base/ROOT
	ErrorLog [코다 설치 경로]/project/sample_base/log/apache/error.log
	CustomLog [코다 설치 경로]/project/sample_base/log/apache/access.log common
	&lt;directory [코다 설치 경로]/project/sample_base/user_web_app_base/ROOT&gt;
		Require all granted
	&lt;/directory&gt;
&lt;/virtualhost&gt;
### vhost1 file 샘플 종료 ###<br></pre><p style="margin-left: 75px;">(1-2) tomcat bin 경로(ex /usr/share/tomcat8/bin/)에 [user home directory]/gitcodda/codda2/resources/tomcat/debian/ 경로에 있는 setenv.sh 을&nbsp; 복사후 chmod a+x 해 줍니다.</p><pre style="margin-left: 25px;">### setenv.sh file 샘플 시작 ###
#!/bin/sh
export CODDA_HOME=[user home directory]/gitcodda/codda2
export PROJECT_NAME=sample_base
export WEB_BUILD_HOME=$CODDA_HOME/project/$PROJECT_NAME/client_build/web_build
export WEB_CORELIB_PATH=$WEB_BUILD_HOME/corelib/ex
export WEB_MAINLIB_PATH=$WEB_BUILD_HOME/lib/main/ex
export CLASSPATH=$CATALINA_HOME/lib/servlet-api.jar;$CATALINA_HOME/lib/jsp-api.jar;$WEB_BUILD_HOME/dist/CoddaWebLib.jar
for jarfile in $WEB_CORELIB_PATH/*.jar
do
        export CLASSPATH=$CLASSPATH:&dollar;{jarfile}
done
for jarfile in $WEB_MAINLIB_PATH/*.jar
do
        export CLASSPATH=$CLASSPATH:&dollar;{jarfile}
done
echo $CLASSPATH
export JAVA_OPTS="$JAVA_OPTS \
-Dfile.encoding=UTF-8 \
-Dlogback.configurationFile=$CODDA_HOME/project/$PROJECT_NAME/config/logback.xml \
-Dcodda.logPath=$CODDA_HOME/project/$PROJECT_NAME/log/tomcat \
-Dcodda.installedPath=$CODDA_HOME \
-Dcodda.projectName=$PROJECT_NAME"
### setenv.sh file 샘플 종료 ###<br></pre><p style="margin-left: 75px;">(1-3) tomcat 에서 AJP 활성화</p><pre style="margin-left: 25px;">아파치 server.xml(=&gt; /etc/tomcat8/server.xml) 파일에서  server.xml 를 보면 "Define an AJP 1.3 Connector on port 8009" 이
부분이 있다. &lt;Connector port="8009" protocol="AJP/1.3"redirectPort="8443" /&gt; 부분의 주석을 풀어 준다.</pre><p style="margin-left: 75px;">(1-4) 아파치 신규 등록한 vhost 활성화 : a2ensite vhost1</p><p style="margin-left: 75px;">(1-5) tomcat UTF8 세팅</p><pre style="margin-left: 25px;">web.xml(참고 : 윈도의 경우 D:\apache-tomcat-8.5.32\conf\web.xml, 리눅스의 경우 /etc/tomcat8/web.xml) 파일 에서 
웹 파라미터 값을 UTF8 문자열로 다루기 위한 조취로
아래 처럼 2 군데 주석(첫번째 : 첫번째 UTF8 을 갖는 문자열 인코딩 필터 정의 부, 
두번째 : 두번째 UTF8 문자열 인코딩 필터 URL 매칭 정의 부)을 풀어준다.
  
# 첫번째 UTF8 을 갖는 문자열 인코딩 필터 정의 부
&lt;filter&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;filter-name&gt;setCharacterEncodingFilter&lt;/filter-name&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;filter-class&gt;org.apache.catalina.filters.SetCharacterEncodingFilter&lt;/filter-class&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;init-param&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;encoding&lt;/param-name&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;UTF-8&lt;/param-value&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/init-param&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;async-supported&gt;true&lt;/async-supported&gt;
&lt;/filter&gt;
    
# 두번째 UTF8 문자열 인코딩 필터 URL 매칭 정의 부
&lt;filter-mapping&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;filter-name&gt;setCharacterEncodingFilter&lt;/filter-name&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;
&lt;/filter-mapping&gt;<br></pre><p style="margin-left: 75px;">(1-6) tomcat 에서 호스트 등록 : 아파치 /etc/tomcat8/server.xml 파일에서 아래 2개 호스트 등록</p><pre style="margin-left: 25px;">Host 태그로 문자열 찾아 들어가면 그곳에 기본으로 설정된 로컬 호스트가 등록되어 있습니다. 그 아래에 추가하시면 됩니다.

&lt;!-- 일반 사용자 사이트 --&gt;
&lt;Host name="www.sinnori.pe.kr" appBase="[코다 설치 경로]/project/sample_base/user_web_app_base" <br>	unpackWARs="true" autoDeploy="true"&gt;
	
	&lt;Context path="" docBase="ROOT" debug="0" reloadable="true"/&gt;
	
	&lt;Valve className="org.apache.catalina.valves.AccessLogValve" 
		directory="[코다 설치 경로]/project/sample_base/log/tomcat"  prefix="tomcat_access_www_" suffix=".log" <br>		pattern="%h %l %u %t &amp;quot;%r&amp;quot; %s %b" resolveHosts="false"/&gt;
&lt;/Host&gt;
	
&lt;!-- 관리자 사이트 --&gt;
&lt;Host name="admin.sinnori.pe.kr" appBase="[코다 설치 경로]/project/sample_base/admin_web_app_base" 
	unpackWARs="true" autoDeploy="true"&gt;
	
	&lt;Context path="" docBase="ROOT" debug="0" reloadable="true"/&gt;
	
	&lt;Valve className="org.apache.catalina.valves.AccessLogValve" directory="[코다 설치 경로]/codda/project/sample_base/log/tomcat" <br>		prefix="tomcat_access_admin_" suffix=".log" pattern="%h %l %u %t &amp;quot;%r&amp;quot; %s %b" resolveHosts="false"/&gt;
&lt;/Host&gt;
</pre><h3 style="margin-left: 25px;"><span style="color: inherit; font-family: inherit;">1.5 ant 개발 환경 구축하기</span><br></h3><p style="margin-left: 50px;">(1) 코어 라이브러리 컴파일및 jar 파일 생성 </p><pre style="margin-left: 25px;">경로 [코다 설치 경로]/core 밑에 있는 
'common', 'client', 'server', 'all' 자식 경로에 각각 차례로 들어가서 
(1-1) ant clean (1-2) ant 를 수행한다.
그러면 [codda 설치 경로]/core/dist 경로 밑에 codda-core-all.jar 파일이 생성된다.<br></pre><p style="margin-left: 50px;">(2) 코다 도우미 컴파일및 jar 파일 생성<br></p><pre style="margin-left: 25px;">경로 [home directory]/gitcodda/codda2/core/helper 밑에서 (2-1) ant copy.core (2) ant clean (3) ant all 를 수행한다.
그러면 [codda 설치 경로]/codda-helper.jar 파일이 신규로 생성된것을 알 수 있다.</pre><p style="margin-left: 50px;">(3) sample_base 프로젝트의 서버 ant 환경 구축&nbsp;</p><pre style="margin-left: 25px;"><span style="font-size: 14px; white-space: normal;">[home directory]/gitcodda/codda2/project/sample_base/server_build/ 경로 들어가서&nbsp;</span><span style="color: rgb(0, 0, 0); font-size: 14px; white-space: normal; background-color: rgb(255, 255, 255);">(3-1) ant clean.core (3-2) ant copy.core (3-3) ant clean (3-4) ant all</span><br></pre><p style="margin-left: 50px;">(4) sample_base 프로젝트의 클라이언트 어플리케이션 ant 환경 구축</p><pre style="margin-left: 25px;">[home directory]/gitcodda/codda2/project/sample_base/client_build/app_build/ 경로 들어가서&nbsp;<span style="color: rgb(0, 0, 0); font-size: 14px; white-space: normal; background-color: rgb(255, 255, 255);">(4-1) ant clean.core (4-2) ant copy.core (4-3) ant clean (4-4) ant all</span><br></pre><p style="margin-left: 50px;">(5) sample_base 프로젝트의 웹 어플리케이션 ant 환경 구축</p><pre style="margin-left: 25px; line-height: 1.42857;">[home directory]/gitcodda/codda2/project/sample_base/client_build/web_build/ 경로 들어가서 
(5-1) webAnt.properties 파일을 열어 servlet.systemlib.path 의 값을 /usr/share/tomcat8/lib 로 수정할것
(5-2) ant clean.core (5-3) ant copy.core (5-4) ant clean (5-5) ant all<br></pre><h3 style="margin-left: 25px;">1.6 코다 이클립스 환경 구축</h3><p style="margin-left: 50px;">(1) 이클립스를 다운로드 받는다</p><pre style="margin-left: 25px;">wget -O eclipse.tar.gz 'https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/neon/1a/eclipse-java-neon-1a-linux-gtk-x86_64.tar.gz&amp;r=1'</pre><p style="margin-left: 50px;">(2) eclipse.tar.gz 압축을 푼후 이클립스를 구동시킨다.</p><p style="margin-left: 50px;">(3) 이때 workspace 를 ~ 바로 밑에 생성한다.</p><p style="margin-left: 50px;">(4) 미리 생성한 이클립스 자바 프로젝트 파일들을 ~/workspaece 에 복사한다.</p><pre style="margin-left: 25px;">cp -r [user home directory]/gitcodda/codda2/resources/eclipse/workbench/* ~/workspace</pre><p style="margin-left: 50px;">(5) eclipse 실행상태에서 복사해온 이클립스 자바 프로젝트들을 import 한다.</p><pre style="margin-left: 25px;">이클립스 'File' 메뉴 ==&gt; Import 메뉴 ==&gt; "General" ==&gt; "Existing Projects into Workspace" 를 선택하여 아래 이클립스 프로젝트인
codda2_helper, codda2_core_all, codda2_sample_base_server, codda2_sample_base_appclient, codda2_sample_base_webclient 차례대로 포함시킨다.</pre><p style="margin-left: 50px;"><br></p><p style="margin-left: 75px;">그림1) Import menu screen shot</p><p style="margin-left: 25px;"><img src="/img/howto/eclipse_sample_base_proejct_importscreen01.png" style="width: 50%;"></p><p style="margin-left: 75px;">그림2) 'Existing Projects into Workspace' menu screen shot</p><p style="margin-left: 25px;"><img src="/img/howto/eclipse_sample_base_proejct_importscreen02.png" style="width: 50%;"></p>
					</article>
				</div>
			</div>
		</div>
	</div>
</body>
</html>