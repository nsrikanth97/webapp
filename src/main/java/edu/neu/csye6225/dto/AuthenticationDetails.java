package edu.neu.csye6225.dto;


import lombok.Data;

@Data
public class AuthenticationDetails {

    String email;

    String password;

    String token;

    String errorMessage;

    Response.ReturnStatus status;
}
