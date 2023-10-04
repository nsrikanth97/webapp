package edu.neu.csye6225.annotations;


import edu.neu.csye6225.dto.AuthenticationDetails;
import edu.neu.csye6225.dto.Response;
import edu.neu.csye6225.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuthenticateRequestValidator  {



    private final HttpServletRequest request;

    private final AccountService accountService;

    @Autowired
    public AuthenticateRequestValidator(HttpServletRequest request, AccountService accountService){
        this.request = request;
        this.accountService = accountService;
    }

    @Around("@annotation(AuthenticateRequest)")
    public Object checkAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Response<Void> response = new Response<>();
        response.setStatus(Response.ReturnStatus.FAILURE);
        log.info("Checking access");
        HttpHeaders headers = new HttpHeaders();
        headers.add("WWW-Authenticate", "Basic realm=\"Access to the staging site\"");
        headers.add("charset", "UTF-8");
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            log.error("Token is null or empty");
            response.getErrorMessages().add("Token is null or empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).body(response);
        }
        AuthenticationDetails details = accountService.getEmailAndPassword(token);
        if(details.getStatus().equals(Response.ReturnStatus.FAILURE)){
            log.error(details.getErrorMessage());
            response.getErrorMessages().add(details.getErrorMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).body(response);
        }
        if(!accountService.authenticateUser(details.getEmail(), details.getPassword())){
            log.info("User authentication failed");
            response.getErrorMessages().add("User authentication failed, Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).body(response);
        }


        return joinPoint.proceed();
    }
}
