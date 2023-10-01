package edu.neu.csye6225.controller;


import edu.neu.csye6225.services.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/healthz")
public class HealthCheckController {

    @Autowired
    private HealthCheckService healthCheckService;


    @GetMapping
    public ResponseEntity<Void> healthCheck(@RequestBody(required = false) Object body) {
        HttpStatus status;
        HttpHeaders headers = new HttpHeaders();

        headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.set("Pragma", "no-cache");
        headers.set("X-Content-Type-Options", "nosniff");
        if(body != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(headers).build();
        }
        if(healthCheckService.isDatabaseConnected()) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        return ResponseEntity.status(status)
                .headers(headers).build();

    }



}
