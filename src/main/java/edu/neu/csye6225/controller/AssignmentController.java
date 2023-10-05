package edu.neu.csye6225.controller;


import edu.neu.csye6225.annotations.AuthenticateRequest;
import edu.neu.csye6225.dto.Response;
import edu.neu.csye6225.entity.Assignment;
import edu.neu.csye6225.services.AssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("v1/assignments")
public class AssignmentController {


    private final AssignmentService  assignmentService;

    private final HttpServletRequest request;

    private final Validator validator;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, HttpServletRequest request, Validator validator){
        this.assignmentService = assignmentService;
        this.request = request;
        this.validator =validator;
    }

    @GetMapping
    @AuthenticateRequest
    public ResponseEntity<Object> getAllAssignments(@RequestBody(required = false) Object body){
        if(body != null || StringUtils.hasLength(request.getQueryString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<Assignment> assignmentList = assignmentService.getAll();
        Response<List<Assignment>> response = new Response<>();
        response.setData(assignmentList);
        response.setStatus(Response.ReturnStatus.SUCCESS);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @AuthenticateRequest
    public ResponseEntity<Object> getAssignmentById(@PathVariable UUID id, @RequestBody(required = false) Object body){
        if(body != null || StringUtils.hasLength(request.getQueryString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    @PostMapping
    @AuthenticateRequest
    public ResponseEntity<Object> createAssignment(@RequestBody(required = false) Assignment assignment){
        if(StringUtils.hasLength(request.getQueryString()) || assignment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Map<String, String> hashMap = new HashMap<>();
        Set<ConstraintViolation<Assignment>> violations = validator.validate(assignment);
        if (!violations.isEmpty()){
            violations
                    .forEach( t -> hashMap.put(t.getPropertyPath().toString(),t.getMessage()));
            Response<Map<String,String>> response = new Response<>();
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.setData(hashMap);
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(assignmentService.save(assignment));
    }

    @DeleteMapping("/{id}")
    @AuthenticateRequest
    public ResponseEntity<Object> deleteAssignment(@PathVariable UUID id, @RequestBody(required = false) Object body){
        if(body != null || StringUtils.hasLength(request.getQueryString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return assignmentService.deleteAssignment(id);
    }

    @PutMapping("/{id}")
    @AuthenticateRequest
    public ResponseEntity<Object> updateAssignment(@RequestBody(required = false) Assignment assignment,
                                                   @PathVariable UUID id){
        if(StringUtils.hasLength(request.getQueryString()) || assignment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Map<String, String> hashMap = new HashMap<>();
        Set<ConstraintViolation<Assignment>> violations = validator.validate(assignment);
        if (!violations.isEmpty()){
            violations
                    .forEach( t -> hashMap.put(t.getPropertyPath().toString(),t.getMessage()));
            Response<Map<String,String>> response = new Response<>();
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.setData(hashMap);
            return ResponseEntity.badRequest().body(response);
        }
        return assignmentService.updateAssignment(id,assignment);
    }

}
