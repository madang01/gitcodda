set OLDPWD=%CD%
cd /D D:\gitmadang\codda\project\sample_test\server_build
java -server -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitmadang\codda\project\sample_test\resources\logback.xml ^
-Dcodda.logPath=D:\gitmadang\codda\project\sample_test\log\server ^
-Dcodda.installedPath=D:\gitmadang\codda ^
-Dcodda.projectName=sample_test ^
-jar dist\CoddaServerRun.jar
cd /D %OLDPWD%