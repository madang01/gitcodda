cd D:\gitcodda\codda2\project\sample_test2\server_build
java -server -Xmx2048m -Xms1024m \
-Dlogback.configurationFile=D:\gitcodda\codda2\project\sample_test2\resources\logback.xml \
-Dcodda.logPath=D:\gitcodda\codda2\project\sample_test2\log\server \
-Dcodda.installedPath=D:\gitcodda\codda2 \
-Dcodda.projectName=sample_test2 \
-jar dist\CoddaServerRun.jar
cd -