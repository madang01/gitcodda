#!/bin/bash

echo "the diretory 'log/apache' needs group write permission and its group should be 'www-data'"
chgrp www-data log/apache
chmod g+w log/apache

echo "the directory 'log/tomcat' and 'user_web_app_base/upload' need write permission and their group should be 'tomcat8'"
chgrp tomcat8 log/tomcat user_web_app_base/upload
chmod g+w log/tomcat user_web_app_base/upload