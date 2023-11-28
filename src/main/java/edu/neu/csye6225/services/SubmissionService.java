package edu.neu.csye6225.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.csye6225.dto.Response;
import edu.neu.csye6225.dto.SnsTopicData;
import edu.neu.csye6225.entity.Assignment;
import edu.neu.csye6225.entity.Submission;
import edu.neu.csye6225.repository.SubmissionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    private final AssignmentService assignmentService;

    private final HttpServletRequest request;

    private final SnsClient snsClient;

    @Value("${REGION}")
    String region;

    @Value("${TOPIC_ARN}")
    String topicArn;
    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository, AssignmentService assignmentService,
                             HttpServletRequest request, SnsClient snsClient) {
        this.submissionRepository = submissionRepository;
        this.assignmentService = assignmentService;
        this.request = request;
        this.snsClient = snsClient;
    }

    public ResponseEntity<Object> createSubmission(UUID assignmentId, Submission submission) {
        Response<Submission> response = new Response<>();
        if(assignmentId == null || submission == null || submission.getSubmissionUrl() == null) {
            if(log.isDebugEnabled() && assignmentId == null)
                log.debug("SubmissionService:createSubmission:-Invalid request received to create a new submission. Assignment id is null");
            if(log.isDebugEnabled() && submission == null)
                log.debug("SubmissionService:createSubmission:-Invalid request received to create a new submission. Submission is null");
            if(log.isDebugEnabled() && submission != null && submission.getSubmissionUrl() == null)
                log.debug("SubmissionService:createSubmission:-Invalid request received to create a new submission. Submission URL is null");
            log.error("SubmissionService:createSubmission:-Invalid request received to create a new submission");
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.getErrorMessages().add("Invalid request received to create a new submission");
            return ResponseEntity.badRequest().body(response);
        }
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        if(assignment == null) {
            log.error("SubmissionService:createSubmission:-Invalid request received to create a new submission. Assignment with id: {} does not exist", assignmentId);
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.getErrorMessages().add("Invalid request received to create a new submission. Assignment with id: " + assignmentId + " does not exist");
            return ResponseEntity.status(404).body(response);
        }
//        UUID accountId =assignment.getAccount().getId();
//        UUID loggedId = (UUID) request.getSession().getAttribute("accountId");
//        if (!accountId.equals(loggedId)) {
//            log.error("SubmissionService:createSubmission:-Invalid request received to create a new submission. Assignment with id: {} does not belong to the user with id: {}", assignmentId, loggedId);
//            response.setStatus(Response.ReturnStatus.FAILURE);
//            response.getErrorMessages().add("Invalid request received to create a new submission. Assignment with id: " + assignmentId + " does not belong to the user with id: " + loggedId);
//            return ResponseEntity.status(403).body(response);
//        }
        String email = (String) request.getSession().getAttribute("email");
        String firstName = (String) request.getSession().getAttribute("firstName");
        long submissionCount = submissionRepository.countByAssignment_Id(assignmentId);
        if(submissionCount >= assignment.getNumOfAttempts()) {
            log.error("SubmissionService:createSubmission:-Invalid request received to create a new submission. Maximum number of attempts reached for assignment with id: {}", assignmentId);
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.getErrorMessages().add("Invalid request received to create a new submission. Maximum number of attempts reached for assignment with id: " + assignmentId);
            return ResponseEntity.badRequest().body(response);
        }
        LocalDateTime submissionTime = LocalDateTime.now();
        if(submissionTime.isAfter(assignment.getDeadline())) {
            log.error("SubmissionService:createSubmission:-Invalid request received to create a new submission. Assignment with id: {} is past due date", assignmentId);
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.getErrorMessages().add("Invalid request received to create a new submission. Assignment with id: " + assignmentId + " is past due date");
            return ResponseEntity.badRequest().body(response);
        }

        String urlRegex = "^(https?|ftp)://[a-zA-Z0-9+&@#/%?=~_|!:,.;]+[a-zA-Z0-9+&@#/%=~_|]";
        if(!submission.getSubmissionUrl().matches(urlRegex)) {
            log.error("SubmissionService:createSubmission:-Invalid request received to create a new submission. Submission URL is invalid");
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.getErrorMessages().add("Invalid request received to create a new submission. Submission URL is invalid");
            return ResponseEntity.badRequest().body(response);
        }
        submission.setSubmissionDate(submissionTime);
        submission.setSubmissionUpdated(submissionTime);
        submission.setAssignment(assignment);
        log.info("SubmissionService:createSubmission:-Request received to create a new submission");
        Submission savedSubmission = submissionRepository.save(submission);
        savedSubmission.setAssignmentId(assignmentId);
        SnsTopicData snsTopicData = new SnsTopicData();
        snsTopicData.setSubmissionDate(submissionTime.toString());
        snsTopicData.setAssignmentId(assignmentId.toString());
        snsTopicData.setSubmissionId(savedSubmission.getId().toString());
        snsTopicData.setSubmissionUrl(savedSubmission.getSubmissionUrl());
        snsTopicData.setEmailId(email);
        snsTopicData.setFirstName(firstName);
        snsTopicData.setAssignmentName(assignment.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        String message = "";
        try {
            message = objectMapper.writeValueAsString(snsTopicData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();
        try {
            PublishResponse publishResponse= snsClient.publish(publishRequest);
            log.info("SubmissionService:createSubmission:-Message published to SNS topic with message id: {}",
                    publishResponse.messageId());
        } catch (Exception e) {
            log.error("SubmissionService:createSubmission:-Failed to publish message to SNS topic");
            response.setStatus(Response.ReturnStatus.FAILURE);
            response.getErrorMessages().add("Failed to publish message to SNS topic");
            return ResponseEntity.status(500).body(response);
        }
        log.info("SubmissionService:createSubmission:-Submission created successfully with id: {}", savedSubmission.getId());
        return ResponseEntity.status(201).body(savedSubmission);
    }
}
