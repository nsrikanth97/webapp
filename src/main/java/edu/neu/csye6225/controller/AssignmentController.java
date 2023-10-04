package edu.neu.csye6225.controller;


import edu.neu.csye6225.annotations.AuthenticateRequest;
import edu.neu.csye6225.dto.Response;
import edu.neu.csye6225.entity.Assignment;
import edu.neu.csye6225.services.AssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/assignments")
public class AssignmentController {


    private final AssignmentService  assignmentService;

    private final HttpServletRequest request;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, HttpServletRequest request){
        this.assignmentService = assignmentService;
        this.request = request;
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
    public ResponseEntity<Object> createAssignment(@Valid @RequestBody Assignment assignment){

        if(StringUtils.hasLength(request.getQueryString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
    public ResponseEntity<Object> updateAssignment(@RequestBody @Valid Assignment assignment,
                                                   @PathVariable UUID id){
        if(StringUtils.hasLength(request.getQueryString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return assignmentService.updateAssignment(id,assignment);
    }

}
