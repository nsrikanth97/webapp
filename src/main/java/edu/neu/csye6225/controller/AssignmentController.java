package edu.neu.csye6225.controller;


import edu.neu.csye6225.annotations.AuthenticateRequest;
import edu.neu.csye6225.dto.Response;
import edu.neu.csye6225.entity.Assignment;
import edu.neu.csye6225.services.AssignmentService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("v1/assignments")
@Slf4j
public class AssignmentController {


//    private static final StatsDClient statsd = new NonBlockingStatsDClient("my.prefix", "statsd-host", 8125);


    private final AssignmentService  assignmentService;

    private final HttpServletRequest request;

    private final Validator validator;
    private final MeterRegistry meterRegistry;


    @Autowired
    public AssignmentController(AssignmentService assignmentService, HttpServletRequest request, Validator validator, MeterRegistry meterRegistry){
        this.assignmentService = assignmentService;
        this.request = request;
        this.validator =validator;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping
    @AuthenticateRequest

    public ResponseEntity<Object> getAllAssignments(@RequestBody(required = false) Object body){
        Counter.builder("api.calls.getAssignments")
                .register(meterRegistry)
                .increment();
        UUID loggedInUserId = (UUID) request.getSession().getAttribute("accountId");
        log.info("AssignmentController:getAllAssignments:-Request received to get all assignments for user: {}", loggedInUserId);
        if(body != null || StringUtils.hasLength(request.getQueryString())) {
            log.error("AssignmentController:getAllAssignments:-Invalid request parameters or body present. Returning Bad Request status.");
            if(body != null)
                log.debug("AssignmentController:getAllAssignments:-Request body to get all assignments from the user with id " + loggedInUserId + " is invalid. It has body" );
            if(StringUtils.hasLength(request.getQueryString()))
                log.debug("AssignmentController:getAllAssignments:-Request body to get all assignments from the user with id " + loggedInUserId + " is invalid. It has Query params," );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<Assignment> assignmentList = assignmentService.getAll();
        if(assignmentList.isEmpty()){
            log.warn("AssignmentController:getAllAssignments:-No assignments found in the system.");
            ResponseEntity.status(404);
        }
        log.info("AssignmentController:getAllAssignments:-Returning {} assignments for the user: {}", assignmentList.size(), loggedInUserId);
        log.debug("AssignmentController:getAllAssignments:-Assignments returned are: {}", assignmentList);
        return ResponseEntity.ok(assignmentList);
    }

    @GetMapping("/{id}")
    @AuthenticateRequest
    public ResponseEntity<Object> getAssignmentById(@PathVariable UUID id, @RequestBody(required = false) Object body){
//        statsd.incrementCounter("assignments.getById");
        log.info("AssignmentController:getAssignmentById:-Request received to fetch assignment by ID: {}", id);
        if(body != null || StringUtils.hasLength(request.getQueryString())) {
            log.error("AssignmentController:getAssignmentById:-Invalid request parameters or body present for assignment ID: {}. Returning Bad Request status.", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Response<Assignment> assignmentResponse = assignmentService.getAssignmentById(id);
        if(assignmentResponse.getStatus().equals(Response.ReturnStatus.SUCCESS)){
            log.info("AssignmentController:getAssignmentById:-Assignment with ID: {} found in the system.", id);
            return  ResponseEntity.ok(assignmentResponse.getData());
        }
        log.warn("AssignmentController:getAssignmentById:-Assignment not found for ID: {}. Returning Not Found status.", id);
        return ResponseEntity.status(404).body(assignmentResponse);
    }

    @PostMapping
    @AuthenticateRequest
    public ResponseEntity<Object> createAssignment(@RequestBody(required = false) Assignment assignment){
//        statsd.incrementCounter("assignments.post");
        UUID loggedInUserId = (UUID) request.getSession().getAttribute("accountId");
        log.info("AssignmentController:createAssignment:-Request received to create an assignment by the user: {}", loggedInUserId);
        if(StringUtils.hasLength(request.getQueryString()) || assignment == null) {
            log.error("AssignmentController:createAssignment:-Invalid request parameters or empty assignment. Returning Bad Request status.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        log.debug("AssignmentController:createAssignment:-Validating the request body for assignment creation." +
                " Request body is: {}", assignment);
        Map<String, String> hashMap = new HashMap<>();
        Set<ConstraintViolation<Assignment>> violations = validator.validate(assignment);
        if (!violations.isEmpty()){
            violations
                    .forEach( t -> hashMap.put(t.getPropertyPath().toString(),t.getMessage()));
            Response<Map<String,String>> response = new Response<>();
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.setData(hashMap);
            log.warn("AssignmentController:createAssignment:-Assignment creation failed for user: {}. Returning Bad Request status.", loggedInUserId);
            return ResponseEntity.badRequest().body(response);
        }
        log.debug("AssignmentController:createAssignment:-Request body is valid. Proceeding with the creation.");
        log.info("AssignmentController:createAssignment:-Assignment creation successful for user: {}", loggedInUserId);
        return ResponseEntity.status(201).body(assignmentService.save(assignment));
    }

    @DeleteMapping("/{id}")
    @AuthenticateRequest
    public ResponseEntity<Object> deleteAssignment(@PathVariable UUID id, @RequestBody(required = false) Object body){
//        statsd.incrementCounter("assignments.delete");
        UUID loggedInUserId = (UUID) request.getSession().getAttribute("accountId");
        log.info("AssignmentController:deleteAssignment:-Request received to delete assignment by ID: {} for user: {}", id, loggedInUserId);
        if(body != null || StringUtils.hasLength(request.getQueryString())) {
            log.error("AssignmentController:deleteAssignment:-Invalid request parameters or body present for assignment ID: {}. Returning Bad Request status.", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return assignmentService.deleteAssignment(id);
    }

    @PutMapping("/{id}")
    @AuthenticateRequest
    public ResponseEntity<Object> updateAssignment(@RequestBody(required = false) Assignment assignment,
                                                   @PathVariable UUID id){
//        statsd.incrementCounter("assignments.put");
        UUID loggedInUserId = (UUID) request.getSession().getAttribute("accountId");
        log.info("AssignmentController:updateAssignment:-Request received to update assignment by ID: {} for user: {}", id, loggedInUserId);
        if(StringUtils.hasLength(request.getQueryString()) || assignment == null) {
            log.error("AssignmentController:updateAssignment:-Invalid request parameters or empty assignment. Returning Bad Request status.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        log.debug("AssignmentController:updateAssignment:-Validating the request body for assignment update." +
                " Request body is: {}", assignment);
        Map<String, String> hashMap = new HashMap<>();
        Set<ConstraintViolation<Assignment>> violations = validator.validate(assignment);
        if (!violations.isEmpty()){
            violations
                    .forEach( t -> hashMap.put(t.getPropertyPath().toString(),t.getMessage()));
            Response<Map<String,String>> response = new Response<>();
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.setData(hashMap);
            log.error("AssignmentController:updateAssignment:-Assignment update failed for user: {}. Returning Bad Request status.", loggedInUserId);
            return ResponseEntity.badRequest().body(response);
        }
        log.debug("AssignmentController:updateAssignment:-Request body is valid. Proceeding with the update.");
        return assignmentService.updateAssignment(id,assignment);
    }

}
