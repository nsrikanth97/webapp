#!/bin/bash

# Update the package list and upgrade existing packages
sudo apt update
#sudo apt upgrade -y

# Install MariaDB
sudo apt install mariadb-server -y

# Install Java (OpenJDK)
#sudo apt install openjdk-17-jdk -y

# Print installed versions for verification
java -version


echo "MariaDB, Java, Git and Maven have been installed."

#Add webapp user to the mariadb server installed in the AMI
sudo mysql -u root <<EOF
CREATE USER '$DB_USER'@'localhost' IDENTIFIED BY '$DB_USER';
GRANT ALL PRIVILEGES ON *.* TO '$DB_USER'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
EOF


sudo mv /tmp/users.csv /opt/users.csv

sudo mv /tmp/csye6225-0.0.1-SNAPSHOT.jar .

sudo mv /tmp/.env .