set OLDPWD=%CD%
cd /D D:\gitmadang\codda\project\sample_test\client_build\app_build
java -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitmadang\codda\project\sample_test\resources\logback.xml ^
-Dcodda.logPath=D:\gitmadang\codda\project\sample_test\log\appclient ^
-Dcodda.installedPath=D:\gitmadang\codda ^
-Dcodda.projectName=sample_test ^
-jar dist\CoddaAppClientRun.jar
cd /D %OLDPWD%