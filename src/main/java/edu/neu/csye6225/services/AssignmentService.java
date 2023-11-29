package edu.neu.csye6225.services;


import edu.neu.csye6225.dto.Response;
import edu.neu.csye6225.entity.Assignment;
import edu.neu.csye6225.repository.AssignmentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
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
        try {
            log.info("AssignmentService:getAssignmentById:-Fetching assignment by ID: {}", uuid);

            Assignment assignment = assignmentRepository.getAssignmentById(uuid);
            Response<Assignment> response = new Response<>();
            if (assignment == null) {
                log.warn("AssignmentService:getAssignmentById:-Assignment with ID {} not found", uuid);
                response.getErrorMessages().add("Assignment with provided ID not found");
                response.setStatus(Response.ReturnStatus.FAILURE);
                return response;
            }
            log.info("AssignmentService:getAssignmentById:-Assignment with ID {} found", uuid);
            log.debug("Assignment details: {}", assignment);
            response.setData(assignment);
            response.setStatus(Response.ReturnStatus.SUCCESS);
            return response;
        } catch (Exception e) {
            log.error("AssignmentService:getAssignmentById:-An error occurred while fetching assignment with ID {}: {}", uuid, e.getMessage());
            Response<Assignment> errorResponse = new Response<>();
            errorResponse.getErrorMessages().add("An error occurred while fetching the assignment");
            errorResponse.setStatus(Response.ReturnStatus.FAILURE);
            return errorResponse;
        }
    }

    public Object save(Assignment assignment){
        Response<Assignment> assignmentResponse = new Response<>();
        if(assignment.getId() != null){
            log.warn("AssignmentService:save:-POST api should be used only for creating a new Assignment, " +
                    "to update an assignment use PUT request. Id : {}", assignment.getId());
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.setData(assignment);
            assignmentResponse.getErrorMessages().add("POST api should be used only for creating a new Assignment," +
                    "to update an assignment use PUT request");
            return assignmentResponse;
        }
        if(!assignment.getName().matches(".*[a-zA-Z].*")){
            log.warn("AssignmentService:save:-Assignment name must contain at least one alphabet. Provided name: {}", assignment.getName());
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.setData(assignment);
            assignmentResponse.getErrorMessages().add("Assignment name must contain atleast one alphabet");
            return assignmentResponse;
        }
        assignment.setAssignmentCreated(LocalDateTime.now());
        assignment.setAssignmentUpdated(LocalDateTime.now());
        UUID id = (UUID) request.getSession().getAttribute("accountId");
        if(id == null){
            log.error("AssignmentService:save:-Failed to fetch the details of logged in user, try again after sometime");
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.setData(assignment);
            assignmentResponse.getErrorMessages().add("Failed to fetch the details of logged in user, try again after" +
                    " sometime");
            return assignmentResponse;
        }
        log.info("AssignmentService:save:-Assignment creating user with ID: {}", id);
        assignment.setAccount(accountService.getAccountById(id));
        Assignment savedAssignment = assignmentRepository.save(assignment);
        log.info("AssignmentService:save:-Assignment created successfully with ID: {}", savedAssignment.getId());
        return savedAssignment;
    }

    public ResponseEntity<Object> deleteAssignment(UUID id){
        ResponseEntity<Object> response;
        Optional<Assignment> assignment = assignmentRepository.findById(id);
        Response<String> assignmentResponse = new Response<>();
        log.info("AssignmentService:deleteAssignment:-Request received to delete assignment by ID: {}", id);
        if(assignment.isEmpty()){
            log.error("AssignmentService:deleteAssignment:-Assignment with ID: {} not found", id);
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.getErrorMessages().add("Assignment with ID not found");
            response = ResponseEntity.status(404).body(assignmentResponse);
        }else{
            log.info("AssignmentService:deleteAssignment:-Assignment with ID: {} found", id);
            UUID accountId =assignment.get().getAccount().getId();
            UUID loggedId = (UUID) request.getSession().getAttribute("accountId");
            if(!loggedId.equals(accountId)){
                log.error("AssignmentService:deleteAssignment:-Only user who created the assignment can delete the " +
                        "assignment");
                assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
                assignmentResponse.setData("FORBIDDEN : Only user who created the assignment can delete the assignment");
                response = ResponseEntity.status(HttpStatus.FORBIDDEN).body(assignmentResponse);
            }else{
                if(!CollectionUtils.isEmpty(assignment.get().getSubmissions())){
                    log.error("AssignmentService:deleteAssignment:-Assignment with ID: {} cannot be deleted as it has " +
                            "submissions", id);
                    assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
                    assignmentResponse.getErrorMessages().add("Assignment with ID: " + id + " cannot be deleted as it has submissions");
                    response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(assignmentResponse);
                    return response;
                }
                log.info("AssignmentService:deleteAssignment:-Deleting assignment with ID: {}", id);
                assignmentRepository.delete(assignment.get());
                log.info("AssignmentService:deleteAssignment:-Assignment with ID: {} deleted successfully", id);
                response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        }
        log.info("AssignmentService:deleteAssignment:-Returning from the delete Assignment, Status: {}", response.getStatusCode());
        return response;
    }

    public ResponseEntity<Object> updateAssignment(UUID id, Assignment updated){
        ResponseEntity<Object> response;
        log.info("AssignmentService:updateAssignment:-Request received to update assignment by ID: {}", id);
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        Response<Object> assignmentResponse = new Response<>();
        if(optionalAssignment.isEmpty()){
            log.error("AssignmentService:updateAssignment:-Assignment with ID: {} not found", id);
            assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
            assignmentResponse.getErrorMessages().add("Assignment with ID not available");
            response = ResponseEntity.ok().body(assignmentResponse);
        }else{
            log.info("AssignmentService:updateAssignment:-Assignment with ID: {} found", id);
            Assignment assignment = optionalAssignment.get();
            UUID accountId =assignment.getAccount().getId();
            UUID loggedId = (UUID) request.getSession().getAttribute("accountId");
            log.info("AssignmentService:updateAssignment:-Logged in user ID: {}", loggedId);
            if(!loggedId.equals(accountId)){
                log.error("AssignmentService:updateAssignment:-Only user who created the assignment can update the " +
                        "assignment");
                assignmentResponse.setStatus(Response.ReturnStatus.FAILURE);
                assignmentResponse.setData("FORBIDDEN : Only user who created the assignment can update the assignment");
                response = ResponseEntity.status(HttpStatus.FORBIDDEN).body(assignmentResponse);
            }else{
                log.info("AssignmentService:updateAssignment:-Updating assignment with ID : {} at Time : {}", id, LocalDateTime.now());
                assignment.setAssignmentUpdated(LocalDateTime.now());
                if(log.isDebugEnabled() && !Objects.equals(assignment.getName(), updated.getName()))
                    log.debug("AssignmentService:updateAssignment:-Updating assignment name from {} to {}", assignment.getName(), updated.getName());
                assignment.setName(updated.getName());
                if(log.isDebugEnabled() && !Objects.equals(assignment.getDeadline(), updated.getDeadline()))
                    log.debug("AssignmentService:updateAssignment:-Updating assignment deadline from {} to {}", assignment.getDeadline(), updated.getDeadline());
                assignment.setDeadline(updated.getDeadline());
                if(log.isDebugEnabled() && !Objects.equals(assignment.getPoints(), updated.getPoints()))
                    log.debug("AssignmentService:updateAssignment:-Updating assignment points from {} to {}", assignment.getPoints(), updated.getPoints());
                assignment.setPoints(updated.getPoints());
                if(log.isDebugEnabled() && !Objects.equals(assignment.getNumOfAttempts(), updated.getNumOfAttempts()))
                    log.debug("AssignmentService:updateAssignment:-Updating assignment number of attempts from {} to {}", assignment.getNumOfAttempts(), updated.getNumOfAttempts());
                assignment.setNumOfAttempts(updated.getNumOfAttempts());
                log.info("AssignmentService:updateAssignment:-Saving assignment with ID: {}", id);
                assignmentRepository.save(assignment);
                log.info("AssignmentService:updateAssignment:-Assignment with ID: {} updated successfully", id);
                response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        }
        return response;
    }
    public Assignment getAssignment(UUID uuid){
        return assignmentRepository.getAssignmentById(uuid);
    }
}
