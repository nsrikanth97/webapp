package edu.neu.csye6225.controller;


import edu.neu.csye6225.annotations.AuthenticateRequest;
import edu.neu.csye6225.services.HealthCheckService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthz")
@Slf4j
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    private final HttpServletRequest request;

    @Autowired
    public HealthCheckController(HealthCheckService healthCheckService, HttpServletRequest request){
        this.healthCheckService = healthCheckService;
        this.request = request;
    }


    @GetMapping
    public ResponseEntity<Void> healthCheck(@RequestBody(required = false) Object body) {
        HttpStatus status;
        HttpHeaders headers = new HttpHeaders();
        log.info("HealthCheckController:healthCheck:-Request received to check health of the system.");
        headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.set("Pragma", "no-cache");
        headers.set("X-Content-Type-Options", "nosniff");
        if(body != null || StringUtils.hasLength(request.getQueryString())) {
            log.error("HealthCheckController:healthCheck:-Invalid request parameters or body present. Returning Bad Request status.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(headers).build();
        }

        if(healthCheckService.isDatabaseConnected()) {
            log.info("HealthCheckController:healthCheck:-Database is connected. Returning OK status.");
            status = HttpStatus.OK;
        } else {
            log.error("HealthCheckController:healthCheck:-Database is not connected. Returning Service Unavailable status.");
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        return ResponseEntity.status(status)
                .headers(headers).build();
    }



}
