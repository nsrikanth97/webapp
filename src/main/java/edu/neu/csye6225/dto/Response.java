package edu.neu.csye6225.dto;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response<T> {

    public enum ReturnStatus{
        SUCCESS,
        FAILURE
    }

    private ReturnStatus status;

    List<String> errorMessages = new ArrayList<>();

    T data;
}
