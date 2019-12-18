set OLDPWD=%CD%
cd /D D:\gitcodda\codda2\project\sample_test\client_build\app_build
java -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitcodda\codda2\project\sample_test\resources\logback.xml ^
-Dcodda.logPath=D:\gitcodda\codda2\project\sample_test\log\appclient ^
-Dcodda.installedPath=D:\gitcodda\codda2 ^
-Dcodda.projectName=sample_test ^
-jar dist\CoddaAppClientRun.jar
cd /D %OLDPWD%