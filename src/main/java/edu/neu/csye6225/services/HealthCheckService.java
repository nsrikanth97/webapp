package edu.neu.csye6225.services;


import edu.neu.csye6225.repository.DatabaseConnectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthCheckService {

    @Autowired
    private DatabaseConnectionRepository databaseConnectionRepository;

    public boolean isDatabaseConnected() {
        try{
            int res = databaseConnectionRepository.checkConnection();
            log.info("HealthCheckService:isDatabaseConnected:-Database connection check result: " + res);
            return res == 1;
        }catch(Exception ex){
            log.error("HealthCheckService:isDatabaseConnected:-Exception occurred while checking database connection: " + ex.getMessage());
            return false;
        }
    }
}
