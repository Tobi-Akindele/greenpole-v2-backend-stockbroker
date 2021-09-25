#!/bin/bash

#Change OS time zone
mv -f /etc/localtime /etc/localtime.bak
ln -s /usr/share/zoneinfo/Africa/Lagos /etc/localtime

#download keystore from repo
set -e
cd /opt/greenpolestockbroker/config/


cd /opt/greenpolestockbroker/

java -jar -Dspring.profiles.active=docker greenpole-stockbroker-0.0.1-SNAPSHOT.jar