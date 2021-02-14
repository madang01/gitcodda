set OLDPWD=%CD%
cd /D D:\gitcodda\codda2\project\sample_test2\client_build\app_build
java -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitcodda\codda2\project\sample_test2\resources\logback.xml ^
-Dcodda.logPath=D:\gitcodda\codda2\project\sample_test2\log\appclient ^
-Dcodda.installedPath=D:\gitcodda\codda2 ^
-Dcodda.projectName=sample_test2 ^
-jar dist\CoddaAppClientRun.jar
cd /D %OLDPWD%