package edu.neu.csye6225.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "uuid")
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "Assignment Name cannot be null or empty, please provide valid name")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Values of points cannot be empty or null")
    @Min(value = 1, message = "Minimum value for the assignment points is 1")
    @Max(value = 100,message = "Maximum value for the points is 100, please provide number less than 100")
    private int points;

    @Column(nullable = false)
    @JsonProperty("num_of_attemps")
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
    @JsonIgnore
    private LocalDateTime assignmentCreated;

    @Column
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, namespace = "assignment_created")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonIgnore
    private LocalDateTime assignmentUpdated;

    @ManyToOne
    @JoinColumn(name = "created_by",nullable = false)
    @JsonIgnore
    private Account account;


    @JsonProperty("assignment_updated")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getAssignmentUpdatedForJson() {
        return assignmentUpdated;
    }

    @JsonProperty("assignment_created")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getAssignmentCreatedForJson() {
        return assignmentCreated;
    }

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<Submission> submissions;

}
