package edu.neu.csye6225.services;


import edu.neu.csye6225.repository.DatabaseConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

    @Autowired
    private DatabaseConnectionRepository databaseConnectionRepository;

    public boolean isDatabaseConnected() {
        try{
            int res = databaseConnectionRepository.checkConnection();
            return res == 1;
        }catch(Exception ex){
            return false;
        }
    }
}
