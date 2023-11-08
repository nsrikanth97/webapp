package edu.neu.csye6225.dto;


import lombok.Data;

@Data
public class MetricTag {
    private String name;
    private String value;
    public MetricTag(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }
}
