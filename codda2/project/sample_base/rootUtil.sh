#!/bin/bash

echo "change group of apache or tomcat log path and add 'group write permission' to apache or tomcat log path"
chgrp www-data log/apache
chmod g+w log/apache
chgrp tomcat8 log/tomcat
chmod g+w log/tomcat