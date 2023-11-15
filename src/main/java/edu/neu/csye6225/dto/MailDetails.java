package edu.neu.csye6225.dto;


import lombok.Data;

@Data
public class MailDetails {

    private String from;

    private String to;

    private String subject;

    private String text;

}
