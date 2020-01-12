#!/bin/bash

echo "'www-data' group and 'www-data' group write permision work"
chgrp www-data log/apache
chmod g+w log/apache

echo "'tomcat8' group and 'tomcat8' group write permision work"
chgrp tomcat8 log/tomcat user_web_app_base/upload user_web_app_base/ROOT/userWebsiteMenuInfo.txt
chmod g+w log/tomcat user_web_app_base/upload