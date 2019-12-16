set OLDPWD=%CD%
cd /D D:\gitcodda\codda2\project\sample_base\client_build\app_build
java -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitcodda\codda2\project\sample_base\resources\logback.xml ^
-Dcodda.logPath=D:\gitcodda\codda2\project\sample_base\log\appclient ^
-Dcodda.installedPath=D:\gitcodda\codda2 ^
-Dcodda.projectName=sample_base ^
-jar dist\CoddaAppClientRun.jar %1
cd /D %OLDPWD%