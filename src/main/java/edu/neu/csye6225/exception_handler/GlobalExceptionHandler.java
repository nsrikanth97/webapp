package edu.neu.csye6225.exception_handler;

import edu.neu.csye6225.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                      HttpHeaders headers, HttpStatusCode status,
                                                                      WebRequest request) {
        headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.set("Pragma", "no-cache");
        headers.set("X-Content-Type-Options", "nosniff");
        log.error("GlobalExceptionHandler:handleHttpRequestMethodNotSupported:-Request received with invalid method type. Returning Method Not Allowed status.");
        return ResponseEntity.status(405).headers(headers).build();
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException e,
                                                              HttpHeaders headers, HttpStatusCode status,
                                                                WebRequest request) {

        log.error("GlobalExceptionHandler:handleNoHandlerFoundException:-Request received with invalid URL. Returning Not Found status.");
        return ResponseEntity.status(404).headers(headers).build();
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> hashMap = new HashMap<>();
        headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.set("Pragma", "no-cache");
        headers.set("X-Content-Type-Options", "nosniff");
        ex.getBindingResult().getFieldErrors()
                .forEach( t -> hashMap.put(t.getField(),t.getDefaultMessage()));
        Response<Map<String,String>> response = new Response<>();
        response.setStatus(Response.ReturnStatus.FAILURE);
        response.setData(hashMap);
        log.error("GlobalExceptionHandler:handleMethodArgumentNotValid:-Request received with invalid arguments. Returning Bad Request status.");
        return ResponseEntity.badRequest().headers(headers).body(response);
    }


}
