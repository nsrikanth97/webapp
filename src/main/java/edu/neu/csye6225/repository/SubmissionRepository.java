package edu.neu.csye6225.repository;

import edu.neu.csye6225.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {


    long  countByAssignment_IdAndAndUserId(UUID assignmentId, UUID userId);
}
