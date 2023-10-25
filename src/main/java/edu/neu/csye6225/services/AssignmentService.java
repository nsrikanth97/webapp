package edu.neu.csye6225.services;


import edu.neu.csye6225.dto.Response;
import edu.neu.csye6225.entity.Assignment;
import edu.neu.csye6225.repository.AssignmentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssignmentService {

    private final  AssignmentRepository assignmentRepository;
    private final HttpServletRequest request;

    private final AccountService accountService;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository, HttpServletRequest request, AccountService accountService){
        this.assignmentRepository = assignmentRepository;
        this.request = request;
        this.accountService = accountService;
    }
    public List<Assignment> getAll(){
        return assignmentRepository.findAll();
    }

    public Response<Assignment> getAssignmentById(UUID uuid){
        Assignment assignment = assignmentRepository.getAssignmentById(uuid);
        Response<Assignment> response = new Response<>();
        if(assignment == null){
            response.getErrorMessages().add("Assignment with provided ID not found");
            response.setStatus(Response.ReturnStatus.FAILURE);
            return response;
        }
        response.setData(assignment);
        response.setStatus(Response.ReturnStatus.SUCCESS);
        return response;
    }

    public Object save(Assignment assignment){
        Response<Assignment> assignmentResponse = new Response<>();
        if(assignment.getId() != null){
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.setData(assignment);
            assignmentResponse.getErrorMessages().add("POST api should be used only for creating a new Assignment," +
                    "to update an assignment use PUT request");
            return assignmentResponse;
        }
        if(!assignment.getName().matches(".*[a-zA-Z].*")){
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.setData(assignment);
            assignmentResponse.getErrorMessages().add("Assignment name must contain atleast one alphabet");
            return assignmentResponse;
        }
        assignment.setAssignmentCreated(LocalDateTime.now());
        assignment.setAssignmentUpdated(LocalDateTime.now());
        UUID id = (UUID) request.getSession().getAttribute("accountId");
        if(id == null){
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.setData(assignment);
            assignmentResponse.getErrorMessages().add("Failed to fetch the details of logged in user, try again after" +
                    " sometime");
            return assignmentResponse;
        }
        assignment.setAccount(accountService.getAccountById(id));
        return assignmentRepository.save(assignment);
    }

    public ResponseEntity<Object> deleteAssignment(UUID id){
        ResponseEntity<Object> response;
        Optional<Assignment> assignment = assignmentRepository.findById(id);
        Response<String> assignmentResponse = new Response<>();
        if(assignment.isEmpty()){
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.getErrorMessages().add("Assignment with ID not found");
            response = ResponseEntity.status(404).body(assignmentResponse);
        }else{
            UUID accountId =assignment.get().getAccount().getId();
            UUID loggedId = (UUID) request.getSession().getAttribute("accountId");
            if(!loggedId.equals(accountId)){
                assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
                assignmentResponse.setData("FORBIDDEN : Only user who created the assignment can delete the assignment");
                response = ResponseEntity.status(HttpStatus.FORBIDDEN).body(assignmentResponse);
            }else{
                assignmentRepository.delete(assignment.get());
                response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        }
        return response;
    }

    public ResponseEntity<Object> updateAssignment(UUID id, Assignment updated){
        ResponseEntity<Object> response;
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        Response<Object> assignmentResponse = new Response<>();
        if(optionalAssignment.isEmpty()){
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.getErrorMessages().add("Assignment with ID not available");
            response = ResponseEntity.ok().body(assignmentResponse);
        }else{
            Assignment assignment = optionalAssignment.get();
            UUID accountId =assignment.getAccount().getId();
            UUID loggedId = (UUID) request.getSession().getAttribute("accountId");
            if(!loggedId.equals(accountId)){
                assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
                assignmentResponse.setData("FORBIDDEN : Only user who created the assignment can update the assignment");
                response = ResponseEntity.status(HttpStatus.FORBIDDEN).body(assignmentResponse);
            }else{
                assignment.setAssignmentUpdated(LocalDateTime.now());
                assignment.setName(updated.getName());
                assignment.setDeadline(updated.getDeadline());
                assignment.setPoints(updated.getPoints());
                assignment.setNumOfAttempts(updated.getNumOfAttempts());
                assignmentRepository.save(assignment);
                response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        }
        return response;
    }
}
