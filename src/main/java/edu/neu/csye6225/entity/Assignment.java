package edu.neu.csye6225.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.UUID;

@Entity
@Data
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "uuid")
    private UUID id;

    @Column(nullable = false)
    @NotNull(message = "Assignment Name cannot be null or empty, please provide valid name")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Values of points cannot be empty or null")
    @Min(value = 1, message = "Minimum value for the assignment points is 1")
    @Max(value = 100,message = "Maximum value for the points is 100, please provide number less than 100")
    private Double points;

    @Column(nullable = false)
    @NotNull(message = "Value of number of attempts cannot be empty or null")
    @Min(value = 1, message = "Minimum value for the Number of attempts is 1")
    @Max(value = 100,message = "Maximum value for the Number of attempts is 100, please provide number less than 100")
    private Integer numOfAttempts;

    @Column(nullable = false)
    @NotNull(message = "Value of deadline cannot be empty or null")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime deadline;

    @Column
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime assignmentCreated;

    @Column
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime assignmentUpdated;

    @ManyToOne
    @JoinColumn(name = "created_by",nullable = false)
    @JsonIgnore
    private Account account;


}
