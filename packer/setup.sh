#!/bin/bash

# Update the package list and upgrade existing packages
sudo apt update
sudo apt upgrade -y

# Install Java (OpenJDK)
sudo apt install openjdk-17-jdk -y
# Print installed versions for verification
java -version

sudo groupadd csye6225
sudo useradd -s /bin/false -g csye6225 -d /opt/csye6225 -m csye6225

sudo mv /tmp/users.csv /opt/users.csv

sudo mv /tmp/csye6225-0.0.1-SNAPSHOT.jar /opt/csye6225/csye6225-0.0.1-SNAPSHOT.jar

sudo mv /tmp/web-application.service /etc/systemd/system/web-application.service
sudo -u csye6225 touch /opt/csye6225/application.properties
sudo chown csye6225:csye6225 /opt/csye6225/csye6225-0.0.1-SNAPSHOT.jar
sudo chown csye6225:csye6225 /opt/csye6225/application.properties
sudo chmod 750 /opt/csye6225/csye6225-0.0.1-SNAPSHOT.jar
sudo chmod 750 /opt/csye6225/application.properties

sudo systemctl enable web-application.service
