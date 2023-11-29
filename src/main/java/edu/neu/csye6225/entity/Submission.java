package edu.neu.csye6225.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Submission {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "assignment_id",nullable = false)
    @JsonIgnore
    private Assignment assignment;

    @Column(nullable = false)
    @JsonProperty("submission_url")
    @NotEmpty(message = "Submission URL cannot be null or empty, please provide valid URL")
    private String submissionUrl;

    @Column
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonIgnore
    private LocalDateTime submissionDate;

    @Column
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonAlias("submission_updated")
    @JsonIgnore
    private LocalDateTime submissionUpdated;

    @Transient
    @JsonProperty("assignment_id")
    private UUID assignmentId;

    @JsonProperty("submission_date")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getSubmissionDateForJson() {
        return submissionDate;
    }

    @JsonProperty("submission_updated")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    public LocalDateTime getSubmissionUpdated() {
        return submissionUpdated;
    }

    @Column
    private UUID userId;

}
