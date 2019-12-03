#!/bin/bash

echo -n "##### Updating the package list #####"
( echo "y"; echo "y" ) | sudo pacman -Syu

echo -n "##### Installing netcat #####"
( echo "y"; echo "y" ) | sudo pacman -S openbsd-netcat

echo -n "##### Installing Docker and Docker-Compose #####"
( echo "y"; echo "y" ) |sudo pacman -S docker
( echo "y"; echo "y" ) |sudo pacman -S docker-compose

sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker vagrant

echo -n "##### Installing InfluxDb #####"
( echo "y"; echo "y" ) | sudo pacman -S influxdb
sudo systemctl enable influxdb
sudo systemctl start influxdb

echo -n "##### Installing Grafa #####"
( echo "y"; echo "y" ) | sudo pacman -S grafana
sudo systemctl enable grafana
sudo systemctl start grafana

echo -n "##### Giving some time for Grafana to start #####"
sleep 10s

echo -n "##### Adding Influx Data Source to Grafana #####"
echo '{"id":1,"orgId":1,"name":"InfluxDB","type":"influxdb","typeLogoUrl":"public/app/plugins/datasource/influxdb/img/influxdb_logo.svg","access":"proxy","url":"http://localhost:8086","password":"","user":"admin","database":"sineWave","basicAuth":false,"isDefault":true,"jsonData":{"httpMode":"GET","keepCookies":[]},"readOnly":false}' > grafanaDataSource.json

sudo curl -X "POST" "http://localhost:3000/api/datasources" -H "Content-Type: application/json" --user admin:admin  --data-binary @grafanaDataSource.json
