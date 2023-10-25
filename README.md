# webapp

#Pre-requisites 

    - Java(JDK 17+)
    - MariaDB server
      - Port 3306
      - Password for root user should be set as root
    - Maven(3.8.8+)
    
  
#Deployment

    - Create a database name with name cloud_computing_project
    - Check if the csv file with users data is available in the root directory /opt/
    - Name of the csv file should be users.csv
    - Build the application and run integration test case using the command : mvn clean install -B
    - Once the build is successful , run the application usinf the command : mvn spring-boot:run
