set OLDPWD=%CD%
cd /D D:\gitcodda\codda2\project\sample_test\server_build
java -server -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitcodda\codda2\project\sample_test\resources\logback.xml ^
-Dcodda.logPath=D:\gitcodda\codda2\project\sample_test\log\server ^
-Dcodda.installedPath=D:\gitcodda\codda2 ^
-Dcodda.projectName=sample_test ^
-jar dist\CoddaServerRun.jar
cd /D %OLDPWD%