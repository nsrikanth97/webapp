package edu.neu.csye6225.repository;

import edu.neu.csye6225.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {


    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM SUBMISSION WHERE ASSIGNMENT_ID = ?1")
    int countByAssignment_Id(UUID assignmentId);
}
