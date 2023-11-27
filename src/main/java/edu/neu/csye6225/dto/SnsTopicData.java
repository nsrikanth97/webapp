package edu.neu.csye6225.dto;


import lombok.Data;

@Data
public class SnsTopicData {

    private String assignmentId;

    private String submissionId;

    private String submissionUrl;

    private String submissionDate;
    private String emailId;
}
