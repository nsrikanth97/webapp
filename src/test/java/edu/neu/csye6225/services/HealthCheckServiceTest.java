package edu.neu.csye6225.services;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HealthCheckServiceTest {

    @Autowired
    private HealthCheckService healthCheckService;

    @Test
    public void testDataBaseConnection() {
        assert healthCheckService.isDatabaseConnected();
    }
}
